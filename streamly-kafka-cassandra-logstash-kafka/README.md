# Streamly Kafka Cassandra with Logstash Example Project

## Introduction

This is a simple stream processing application that you can deploy on [Streamly].
It is written in Java and consumes events from [Kafka] then writes aggregates to [Cassandra].
It also populates events to [Kafka] using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-kafka
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
| system-apache-logs           | It contains apache logs gathered from various servers       		|

In this example, we consume events from `system-apache-logs`.


### 4. Create your keyspace
To create a new keyspace :

  - Go to Cassandra tab
  - Provide the name of the keyspace, in the Keyspace Name box (eg `greenspace_keyspace`). It should start with your namespace.
  - Choose the strategy (eg `SimpleStrategy`) and define the replication factor (eg `1`)

![streamly-create-keyspace][streamly-create-keyspace]

  - Click on Create Keyspace button

The keyspace appears in the list of existing keyspaces:

![streamly-list-keyspace][streamly-list-keyspace]

### 5. Create your output topic
To create a new topic :
  
  - Go to Kafka tab
  - Write the name of the index in Index name box. We assume that the name is `greenspace-kafka-logstash`.
  - Define the number of partitions(eg `1`), maximum messages(eg `100`), replication(eg `1`), retention(eg `1`) and Authorized hosts(eg `*`). 
  - Enable Unsecured read and Unsecured write buttons.

![streamly-create-topic][streamly-create-topic]

  - Click on Add New Index button

The indexes appears in the list of existing indexes:

![streamly-list-topics][streamly-list-topics]

### 6. Get your access and secret keys
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
app.args=apps.streamly.io:29093,system-apache-logs,greenspace_keyspace,greenspace_table,-f,file://logstash.conf
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
  kafka {
    topic_id => "greenspace-kafka-logstash" # Should be prefixed by your namespace
    bootstrap_servers => ["apps.streamly.io:29093"] # list of kafka nodes
  }
}
```

### 8. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-kafka-cassandra-logstash-kafka`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-kafka-0.0.1.jar` files
 - Click on the Start icon
 
![streamly-kafka-cassanda-logstash-kafka][streamly-kafka-cassanda-logstash-kafka]

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

#### b. Query Kafka
  - Download and query kafka to consume all published events
```bash
 host$ wget http://www-us.apache.org/dist/kafka/0.10.1.1/kafka_2.10-0.10.1.1.tgz
 host$ tar xvzf kafka_2.10-0.10.1.1.tgz
 host$ cd kafka_2.10-0.10.1.1/
 host$ bin/kafka-console-consumer.sh --bootstrap-server apps.streamly.io:29093 --topic greenspace-kafka-logstash --from-beginning
```
The output console looks like this: 
![streamly-kafka-cassandra-logstash-kafka-consumer][streamly-kafka-cassandra-logstash-kafka-consumer]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[logstash plugins]: https://www.elastic.co/guide/en/logstash/current/output-plugins.html 
[open-streams]: http://streamly.io/streamly-new/streams.html
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23483283/90ef6560-fed2-11e6-8c03-71d3976a3dd5.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23549122/f6ef1ee4-000a-11e7-8d12-26e73c6d86cb.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23470714/6cd57f6e-fea7-11e6-8dfe-47f0d70b5b6a.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23607834/c53a97d0-0266-11e7-94eb-b3271812b28c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23482368/271888a4-fecf-11e6-95a2-e7c5ba962901.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23482456/6d883294-fecf-11e6-9cf4-4c9fed49b140.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23464521/a0368b08-fe95-11e6-8851-4a205d4d99e3.png
[streamly-kafka-cassanda-logstash-kafka]: https://cloud.githubusercontent.com/assets/25694018/23483078/b205a36e-fed1-11e6-99b8-fc30ea422bcb.png
[streamly-kafka-cassandra-logstash-kafka-consumer]: https://cloud.githubusercontent.com/assets/25694018/23580340/07d155b0-0100-11e7-809a-a962efee8ea4.png