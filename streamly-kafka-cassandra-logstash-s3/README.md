# Streamly Kafka Cassandra with Logstash Example Project

## Introduction

This is a simple stream processing application that you can deploy on [Streamly].
It is written in Java and consumes events from [Kafka] and writes aggregates to [Cassandra].
It also populates events into an Amazon s3 bucket  using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-s3
 host$ mvn clean install
```

### 2. Setup an account
 - Go to [Streamly Registration Page][streamly-signup] and sign up by providing your email address and a valid namespace. <br /> 
  The namespace is a string on which you have full authorization for services that you make used on [Streamly]. Every service that you make used on [Streamly] should start with your namespace. That is for instance if you want to create a keyspace, your keyspace must be prefixed by your namespace. <br />
  **Make sure you choose your namespace carefully because you wouldn't change it afterwards.**
![streamly-signup-step1][streamly-signup-step1]
 - Complete your registration 
![streamly-signup-step2][streamly-signup-step2]
 - Log into [Streamly] with your email and password

In the following steps, we assume the namespace is `greenspace`.

### 3. Choose the topic to read from
There are [Open Streams][open-streams] topics available to all registered users :

| Name                         | Description                                                 	    |
|------------------------------|--------------------------------------------------------------------|
| system-bitcoin-transactions  | It contains transaction events of a bitcoin network                |
| system-bitcoin-peers         | It contains peer events of a bitcoin network                       |
| system-bitcoin-blocks        | It contains block events of a bitcoin network                      |
| system-ethereum-transactions | It contains transaction events of an ethereum network              |
| system-ethereum-blocks       | It contains block events of an ethereum network					|
| system-ethereum-hashs        | It contains (transaction/block) hash events of an ethereum network |                         
| system-ethereum-extras       | It contains other events of an ethereum network     				|

In this example, we consume events from `system-bitcoin-transactions`.

### 4. Create your keyspace
To create a new keyspace :

  - Go to Cassandra tab
  - Provide the name of the keyspace, in the Keyspace Name box (eg `greenspace_keyspace`). It should start with your namespace.
  - Choose the strategy (eg `SimpleStrategy`) and define the replication factor (eg `1`)

![streamly-create-keyspace][streamly-create-keyspace]

  - Click on Create Keyspace button

The keyspace appears in the list of existing keyspaces:

![streamly-list-keyspace][streamly-list-keyspace]

### 5. Create your s3 bucket and get your aws Identity and Access Management (IAM)
To create a s3 bucket :
  
  - Go to your [AWS Console] [aws]
![streamly-kafka-cassandra-logstash-aws-s3][streamly-kafka-cassandra-logstash-aws-s3]

  - Click on Create Bucket, leave it with the default settings and click on create.
![streamly-kafka-cassandra-logstash-aws-s3-bucket][streamly-kafka-cassandra-logstash-aws-s3-bucket]

To get your aws IAM 

  - Go to your [AWS Console] [aws] and create a user. Once the creation of your user is done, to go Permissions and add the AmazonS3FullAccess. Finally, go to Security credentials and click on Create access key. Download the csv file. This file contents your access keys. 

### 6. Get your streamly access and secret keys
  - Click on the Profile icon
  - Look at Access Keys Management section

![streamly-list-apikeys][streamly-list-apikeys]

In this example : access key is `ci00jji37jfhq8q` and secret key is `r30qwridiw8qkxj`.
Access and secret keys authenticate users on Streamly services (Kafka, Mqtt, Cassandra, Elasticsearch,...).

### 7. Update your configuration file
Open `spark.properties` file and edit as appropriate.

| Name                                  | Description                						  |
|---------------------------------------|-----------------------------------------------------|
| main.class                            | The entry point for your application                |
| app.args                              | Arguments passed to the main method                 |
| app.resource                          | Name of the bundled jar including your application  |
| spark.cassandra.connection.port       | Cassandra native connection port                    |
| spark.cassandra.connection.host       | Comma separated Cassandra hosts                     |
| spark.cassandra.auth.username         | Access key          			                      |
| spark.cassandra.auth.password         | Secret key             							  |

The resulting file looks like :

```properties
main.class=io.streamly.examples.StreamlyKafkaCassandraLogstash
app.args=london206.streamly.io:9093,system-bitcoin-transactions,greenspace_keyspace,greenspace_table,-f,file://logstash.conf
app.resource=file://streamly-kafka-cassandra-logstash-es-0.0.1.jar
spark.cassandra.connection.port=9042
spark.cassandra.connection.host=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.cassandra.auth.username=ci00jji37jfhq8q
spark.cassandra.auth.password=r30qwridiw8qkxj
```

Open `logstash.conf` file and replace empty settings with correct values. <br/>
We provide you with some dummy input configuration because the 
input plugin is mandatory for logstash to start properly.
Hence, the data populated into elasticsearch come from the spark RDD.

```conf
input {
  file {
  	path => "/tmp/dummyfile" # Dummy logstash input file
  }
}
output {
  s3{
     access_key_id => "my_aws_access_key_id" # (required) your aws access key
     secret_access_key => "my_secret_access_key" # (required) your aws secret key
     region => "us-east-1" # (optional, default = "us-east-1")
     bucket => "greenspace-backet" # (required)  your bucket name              
     size_file => 2048 # (optional) In Bytes
     time_file => 5 # (optional) In Minutes
   }
}
```

### 8. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-kafka-cassandra-logstash-s3`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-s3-0.0.1.jar` files
 - Click on the Start icon
 
 ![streamly-kafka-cassandra-logstash-s3][streamly-kafka-cassandra-logstash-s3]
 
### 9. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-cassandra-logstash-spark-ui][streamly-kafka-cassandra-logstash-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 10. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 11. Visualize your data
#### a. Query Cassandra
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result

![streamly-kafka-cassandra-logstash-zeppelin-cassandra][streamly-kafka-cassandra-logstash-zeppelin-cassandra]

#### b. Query s3
  - Go to your [AWS Console] [aws] and click on s3 then on the bucket my_bucket

![streamly-kafka-cassandra-logstash-aws-s3-bucket-results1][streamly-kafka-cassandra-logstash-aws-s3-bucket-results1]

   - Choose a file
   
![streamly-kafka-cassandra-logstash-aws-s3-bucket-results2][streamly-kafka-cassandra-logstash-aws-s3-bucket-results2]

   - Download the file and open it with your favaorite
   
![streamly-kafka-cassandra-logstash-aws-s3-bucket-results3][streamly-kafka-cassandra-logstash-aws-s3-bucket-results3]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly-dashboard]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342406/00b63c50-fc5a-11e6-8245-e079bc8d224c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[aws]: https://console.aws.amazon.com/s3/buckets/?region=us-east-1
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[logstash plugins]: https://www.elastic.co/guide/en/logstash/current/output-plugins.html 
[open-streams]: http://streamly.io/streamly-new/streams.html
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23503719/1b5de0cc-ff3d-11e6-977c-3051479c7ec9.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23503781/53cd0190-ff3d-11e6-82ce-80c5e1172fad.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23470714/6cd57f6e-fea7-11e6-8dfe-47f0d70b5b6a.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-kafka-cassandra-logstash-aws-s3]: https://cloud.githubusercontent.com/assets/25694018/23501915/28164f2c-ff36-11e6-91b8-5ad12f84a1b9.png
[streamly-kafka-cassandra-logstash-aws-s3-bucket]: https://cloud.githubusercontent.com/assets/25694018/23502081/cc9ac51e-ff36-11e6-9a3f-5586660579f3.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23464521/a0368b08-fe95-11e6-8851-4a205d4d99e3.png
[streamly-kafka-cassandra-logstash-s3]: https://cloud.githubusercontent.com/assets/25694018/23503453/2a1646f0-ff3c-11e6-9458-7e5a76f06420.png

[streamly-kafka-cassandra-logstash-aws-s3-bucket-results1]: https://cloud.githubusercontent.com/assets/25694018/23504085/5dbe5bf8-ff3e-11e6-9ead-a3d530bbf1f4.png
[streamly-kafka-cassandra-logstash-aws-s3-bucket-results2]: https://cloud.githubusercontent.com/assets/25694018/23504101/6e25a37a-ff3e-11e6-84e9-98769733df39.png
[streamly-kafka-cassandra-logstash-aws-s3-bucket-results3]: https://cloud.githubusercontent.com/assets/25694018/23504128/80e9610e-ff3e-11e6-8fad-b4bb99e21230.png