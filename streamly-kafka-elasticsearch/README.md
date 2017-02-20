# Streamly Kafka Elasticsearch Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] and writes aggregates to [Elasticsearch].


## Quickstart

### 1. Build the project

Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-elasticsearch
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

### 3. Create your index
When you register on [Streamly], you have a default index. You can either use it or create a new one. 
To create a new index :
  
  - Go to Elasticsearch tab
  - Write the name of the index in Index name box. We assume that the name is `streamly-index`.
  - Define the number of replicas
  - Click on Add New Index button

![streamly-create-index][streamly-create-index]

### 4. Update your configuration file
Open `spark.properties` file and edit as appropriate.

| Name                                  | Description                															 |
|---------------------------------------|----------------------------------------------------------------------------------------|
| main.class                            | The entry point for your application                                                   |
| app.args                              | Arguments passed to the main method                                                    |
| app.resource                          | Name of the bundled jar including your application                                     |
| spark.es.port                         | Elasticsearch http client port                                                         |
| spark.es.nodes                        | Comma separated Elasticsearch hosts                                                    |
| spark.es.net.http.auth.user           | Your access key available in the Profile section  of your Streamly account             |
| spark.es.net.http.auth.pass           | Your secret key available in the Profile section  of your Streamly account             |
| spark.es.resource                     | Elasticsearch resource location, where data is read or written to                      |

### 5. Submit your application 
 - Go to Processing tab
 - Click on Add Application
 - Provide a valid name for your application and save it. In this example the name is `streamly-kafka-elasticsearch`.
 - Upload `spark.properties` and `streamly-kafka-elasticsearch-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-elasticsearch][streamly-kafka-elasticsearch]

### 6. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-elasticsearch-spark-ui][streamly-kafka-elasticsearch-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 7. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-elasticsearch-kibana-ui][streamly-kafka-elasticsearch-kibana-ui]

### 8. Visualize your data
  - Go to Kibana tab
  - Create a new index pattern with your index name and @timestamp as time-field name

![streamly-kafka-elasticsearch-kibana-index-pattern][streamly-kafka-elasticsearch-kibana-index-pattern]

  - Go to discover

![streamly-kafka-elasticsearch-kibana-discover][streamly-kafka-elasticsearch-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[kafka]: https://kafka.apache.org/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-elasticsearch-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23135037/4ed108c4-f797-11e6-9566-7f2ca2bc98ce.png
[streamly-kafka-elasticsearch]: https://cloud.githubusercontent.com/assets/25694018/23135954/d1766802-f79a-11e6-8346-6b6fe1d6f54c.png
[streamly-kafka-elasticsearch-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-elasticsearch-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23129770/43736cfa-f784-11e6-99d8-68920335c410.png
[streamly-kafka-elasticsearch-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23136472/277ec3d2-f79d-11e6-9bc2-b92be662f141.png
[open-streams]: http://streamly.io/streamly-new/streams.html