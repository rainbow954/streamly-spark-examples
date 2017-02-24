# Streamly Kafka Cassandra with Logstash Example Project

## Introduction

This is a simple stream processing application that you can deploy on [Streamly].
It is written in Java and consumes events from [Kafka] and writes aggregates to [Cassandra].
It also populates events to [Elasticsearch] using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-es
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

### 4. Create your index
When you register on [Streamly], you have a default index. You can either use it or create a new one. 
To create a new index :
  
  - Go to Elasticsearch tab
  - Write the name of the index in Index name box. We assume that the name is `streamly-index`.
  - Define the number of replicas
  - Click on Add New Index button

![streamly-create-index][streamly-create-index]

### 5. Update your configuration files
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

Open `logstash.conf` file and replace empty settings with correct values. 
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
  elasticsearch {
    hosts => ["http://192.168.0.201:9200"]  # list of elasticsearch nodes
    user => "" # Username to authenticate ( your access key)
    password => "" # Password to authenticate ( your secret key)
    manage_template => true
    index => "" # Should be prefixed by your namespace
  }
}
```

### 6. Submit your application 
 - Go to Processing tab
 - Click on Add Application
 - Provide a valid name for your application and save it. In this example the name is `streamly-kafka-cassandra-logstash-es`.
 - Upload  `logstash.conf`,`spark.properties` and `streamly-kafka-cassandra-logstash-es-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-cassanda-logstash][streamly-kafka-cassanda-logstash]

### 7. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-cassandra-logstash-spark-ui][streamly-kafka-cassandra-logstash-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 8. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 9. Visualize your data
#### a. Query Cassandra
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result

![streamly-kafka-cassandra-logstash-zeppelin-cassandra][streamly-kafka-cassandra-logstash-zeppelin-cassandra]

#### b. Query Elasticsearch
  - Go to Kibana tab
  - Create a new index pattern with your index name and @timestamp as time-field name

![streamly-kafka-cassandra-logstash-kibana-index-pattern][streamly-kafka-cassandra-logstash-kibana-index-pattern]

  - Go to discover

![streamly-kafka-cassandra-logstash-kibana-discover][streamly-kafka-cassandra-logstash-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[open-streams]: http://streamly.io/streamly-new/streams.html
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23123079/361e72e2-f767-11e6-929c-676e7a903538.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23123511/f141e080-f768-11e6-9943-4f9ed30b8b80.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23123951/d71c47de-f76a-11e6-89be-d791d66bd9b4.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23129770/43736cfa-f784-11e6-99d8-68920335c410.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23131876/fcfc79ee-f78b-11e6-8c6a-762fa35b5606.png