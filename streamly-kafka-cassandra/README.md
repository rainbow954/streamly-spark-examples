# Streamly Kafka Cassandra Example Project

## Introduction
This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] and writes aggregates to [Cassandra].


## Quickstart


### 1. Build the project

Assuming git, java, and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-cassandra
 host$ mvn clean install
```
### 2. Create your topic
 - Log into [Streamly]
 - Open Messaging tab
 - In the Topic Name box, enter the topic name. This example assumes that the name is `streamly-kafka-topic`.
 - Set values of other fields (partitions, max messages, replication, retention and authorized hosts)
 - Enable unsecure read
 - Click on Add New Topic

![streamly-create-topic][streamly-create-topic]

There are [Open Streams][open-streams] topics available to all registered users :

| Name                         | Description                                                 			  						 |
|------------------------------|-------------------------------------------------------------------------------------------------|
| system-bitcoin-transactions  | It contains transaction events produced by a bitcoin network                                    |
| system-bitcoin-peers         | It contains peer-to-peer communication events of a bitcoin network                              |
| system-ethereum-transactions | It contains transaction events produced by an ethereum network                                  |
| system-ethereum-blocks       | It contains events related to blocks(blockchain miners, block number ,..) of an ethereum network|
| system-ethereum-peers        | It contains peer-to-peer communication events of an ethereum network                            |

### 3. Create your keyspace
When you register on [Streamly] , you have a default keyspace. You can either use it or create a new one.
To create a new keyspace :

  - Go to Cassandra tab
  - Provide the name of the keyspace, in the Keyspace Name box
  - Choose your strategy and define replication factor value according to that
  - Click on Create Keyspace button

![streamly-create-keyspace][streamly-create-keyspace]

### 4. Update your configuration file
Open `spark.properties` file and edit as appropriate.

| Name                                  | Description                															 |
|---------------------------------------|----------------------------------------------------------------------------------------|
| main.class                            | The entry point for your application                                                   |
| app.args                              | Arguments passed to the main method                                                    |
| app.resource                          | Name of the bundled jar including your application                                     |
| spark.cassandra.connection.port       | Cassandra native connection port                                                       |
| spark.cassandra.connection.host       | Comma separated Cassandra hosts                                                        |
| spark.cassandra.auth.username         | Your access key available in the Profile section  of your Streamly account             |
| spark.cassandra.auth.password         | Your secret key available in the Profile section  of your Streamly account             |


### 5. Submit your application 
 - Go to Processing tab
 - Click on Add Application
 - Provide a valid name for your application and save it. In this example the name is `streamly-kafka-cassandra`.
 - Upload `spark.properties` and `streamly-kafka-cassandra-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-cassandra][streamly-kafka-cassandra]

### 6. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-cassandra-spark-ui][streamly-kafka-cassandra-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 7. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-kibana-ui][streamly-kafka-cassandra-kibana-ui]

### 8. Visualize your data
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result

![streamly-kafka-cassandra-zeppelin-cassandra][streamly-kafka-cassandra-zeppelin-cassandra]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[streamly-kafka-cassandra-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23133957/486c6216-f793-11e6-8885-a43441646a08.png
[streamly-kafka-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23133958/4870ace0-f793-11e6-938f-38c4feb10bf3.png
[streamly-kafka-cassandra-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23134241/537095dc-f794-11e6-9db6-3048b4798115.png
[streamly-kafka-cassandra-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23123951/d71c47de-f76a-11e6-89be-d791d66bd9b4.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23131876/fcfc79ee-f78b-11e6-8c6a-762fa35b5606.png
[open-streams]: http://streamly.io/streamly-new/streams.html