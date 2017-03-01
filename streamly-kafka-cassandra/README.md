# Streamly Kafka Cassandra Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] then writes aggregates to [Cassandra].


## Quickstart


### 1. Build the project

Assuming git, java, and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-cassandra
 host$ mvn clean install
```

### 2. Setup an account
 - Go to [Streamly Registration Page][streamly-signup] and sign up by providing your email address and a valid namespace
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
When you register on [Streamly] , you have a default keyspace. You can either use it or create a new one.
To create a new keyspace :

  - Go to Cassandra tab
  - Provide the name of the keyspace, in the Keyspace Name box (eg `greenspace_keyspace`). It should start with your namespace.
  - Choose the strategy (eg `SimpleStrategy`) and define the replication factor (eg `1`)

![streamly-create-keyspace][streamly-create-keyspace]

  - Click on Create Keyspace button

The keyspace appears in the list of existing keyspaces:

![streamly-list-keyspace][streamly-list-keyspace]

### 5. Get your access and secret keys
  - Click on the Profile icon
  - Look at Access Keys Management section

![streamly-list-apikeys][streamly-list-apikeys]

In this example : access key is `ci00jji37jfhq8q` and secret key is `r30qwridiw8qkxj`.
Access and secret keys authenticate users on Streamly services (Kafka, Mqtt, Cassandra, Elasticsearch,...).

### 6. Update your configuration file
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
main.class=io.streamly.examples.StreamlyKafkaCassandra
app.args=london206.streamly.io:9093,system-bitcoin-transactions,greenspace_keyspace,greenspace_table
app.resource=file://streamly-kafka-cassandra-0.0.1.jar
spark.cassandra.connection.port=9042
spark.cassandra.connection.host=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.cassandra.auth.username=ci00jji37jfhq8q
spark.cassandra.auth.password=r30qwridiw8qkxj
```
### 6. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-kafka-cassandra`.
 - Upload `spark.properties` and `streamly-kafka-cassandra-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-cassandra][streamly-kafka-cassandra]

### 7. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-cassandra-spark-ui][streamly-kafka-cassandra-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 8. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-kibana-ui][streamly-kafka-cassandra-kibana-ui]

### 9. Visualize your data
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result

![streamly-kafka-cassandra-zeppelin-cassandra][streamly-kafka-cassandra-zeppelin-cassandra]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[streamly-kafka-cassandra-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23463973/b8c5ab1a-fe93-11e6-99af-e580f0e4a7f8.png
[streamly-kafka-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23463864/5e5b2394-fe93-11e6-907c-c7f45f88cd2f.png
[streamly-kafka-cassandra-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23464035/e0156926-fe93-11e6-9d07-5e7a5e8ebfec.png
[streamly-kafka-cassandra-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23464099/0847dec4-fe94-11e6-88f3-c8b192cc59d1.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342406/00b63c50-fc5a-11e6-8245-e079bc8d224c.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23463922/922e44e4-fe93-11e6-9f62-5a0b98fd4299.png
[open-streams]: http://streamly.io/streamly-new/streams.html
