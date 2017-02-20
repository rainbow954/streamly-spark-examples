# Streamly Kafka Mqtt Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] and writes aggregates to [Mqtt].

## Quickstart

### 1. Build the project

Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-mqtt
 host$ mvn clean install
```

### 2. Create your topics

#### a. Kafka
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

#### b. Mqtt
  - Open Messaging tab
  - Switch type to Mqtt
  - In the Topic Name box, enter the topic name. This example assumes that the name is `streamly/mqtt/topic`.
  - Set authorized hosts to `*`.
  - Click on Add New Topic

![streamly-create-mqtt-topic][streamly-create-mqtt-topic]

### 3. Update your configuration file
Open `spark.properties` file and edit as appropriate.

| Name                                  | Description                															 |
|---------------------------------------|----------------------------------------------------------------------------------------|
| main.class                            | The entry point for your application                                                   |
| app.args                              | Arguments passed to the main method                                                    |
| app.resource                          | Name of the bundled jar including your application                                     |

### 4. Submit your application 
 - Go to Processing tab
 - Click on Add Application
 - Provide a valid name for your application and save it. In this example the name is `streamly-kafka-mqtt`.
 - Upload `spark.properties` and `streamly-kafka-mqtt-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-mqtt][streamly-kafka-mqtt]

### 6. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-mqtt-spark-ui][streamly-kafka-mqtt-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 7. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-mqtt-kibana-ui][streamly-kafka-mqtt-kibana-ui]

### 8. Visualize your data
In your local machine : 
  - Install [mosquitto-clients]
  - Open a terminal and consume events published into your MQTT topic with your credentials

![streamly-kafka-mqtt-consumer][streamly-kafka-mqtt-consumer]

## Copyright
Copyright © 2017 Streamly, Inc.


[streamly]: https://board.streamly.io:20080
[kafka]: https://kafka.apache.org/
[mqtt]: http://mqtt.org/
[blog-post]: http://streamly.io/streamly-new/blog.html
[streamly-create-mqtt-topic]: https://cloud.githubusercontent.com/assets/25694018/23137087/52f97108-f7a0-11e6-8567-56c91625cbbe.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[open-streams]: http://streamly.io/streamly-new/streams.html
[mosquitto-clients]: https://mosquitto.org/download/
[streamly-kafka-mqtt-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23140917/6cb32012-f7b4-11e6-9ffd-28e866a7f6ff.png
[streamly-kafka-mqtt-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23140926/7a5dc154-f7b4-11e6-9daa-f44abea5af6f.png
[streamly-kafka-mqtt]: https://cloud.githubusercontent.com/assets/25694018/23140981/c6a0bfe4-f7b4-11e6-80db-3823b5116599.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23140899/5d14437a-f7b4-11e6-9dd0-5ae57542c10c.png