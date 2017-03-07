# Streamly Mqtt Kafka Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your output topic](#4-create-your-output-topic)
  - [5. Get your access and secret keys](#5-get-your-access-and-secret-keys)
  - [6. Update your configuration file](#6-update-your-configuration-file)
  - [7. Submit your application](#7-submit-your-application)
  - [8. Monitor your application](#8-monitor-your-application)
  - [9. Check your application logs](#9-check-your-application-logs)
  - [10. Visualize your data](#10-visualize-your-data)
- [Copyright](#copyright)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction

This is a sample stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Mqtt] [mqtt] count each word on the event then writes aggregates to [Kafka].


## Quickstart

### 1. Build the project
Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-kafka
 host$ mvn clean install
```

### 2. Create an account

 - Go to [Streamly Registration Page][streamly-signup] and sign up by providing your email address and a namespace. <br /> 
  The namespace is a string that Streamly uses to scope resources. For instance, any keyspace, index, application, or topic you create must have a name that is prefixed with your namespace.  <br />

    **A user has one single namespace. Once an account is created, the associated namespace cannot be changed. Therefore, be sure to choose your namespace carefully.**

![streamly-signup-step1][streamly-signup-step1]

After about 2 min of clicking on `Sign up`, you should receive a confimation email. Click on this email to land on the following screen.

 - Complete your registration 

![streamly-signup-step2][streamly-signup-step2]

 - Log in into [Streamly] with your email and password

In the following steps, we assume the namespace is `greenspace`.

### 3. Choose the topic to read from
We assume that you have followed up the [streamly-kafka-mqtt] project, because this project creates a MQTT topic and sends some data inside. This topic is called  `greenspace/mqtt/topic`. In the next steps, we consume events from  `greenspace/mqtt/topic`.

### 4. Create your output topic
To create a new topic :
  
  - Open the Streamly dashboard and switch to the Cassandra tab
  - Specify the topic name in the corresponding text field (e.g. `greenspace-mqtt-kafka`). Be sure to prefix it with your namespace.
  - Define the number of partitions(eg `1`), maximum messages(eg `100`), replication(eg `1`), retention(eg `1`) and Authorized hosts(eg `*`). 
  - Enable `Unsecured Read` and `Unsecured Write`

![streamly-create-topic][streamly-create-topic]

  - Click on `ADD NEW TOPIC`

The newly created topic should appear in the list of existing topics on the right side of the screen:

![streamly-list-topics][streamly-list-topics]

### 5. Get your access and secret keys
  - Open the Streamly dashboard and click on ![profile][profile]
  - Copy your access and secret keys in the `Access Keys Management` section

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

The resulting file should look as depicted below:

```properties
main.class=io.streamly.examples.StreamlyMqttKafka
app.resource=file://streamly-mqtt-kafka-0.0.1.jar
app.args=tcp://apps.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,apps.streamly.io:29093,greenspace-mqtt-kafka
```

### 7. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on `Add Application`. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-mqtt-kafka`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-mqtt-kafka-0.0.1.jar` files
 - Click on ![start][start]
 
![streamly-mqtt-kafka-ui][streamly-mqtt-kafka-ui]

### 8. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:
![streamly-mqtt-kafka-spark-ui][streamly-mqtt-kafka-spark-ui]
You can see how our Spark Streaming job _processes_ the Mqtt events stream.

### 9. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.

![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 10. Visualize your data
 - Download and query kafka to consume all published events. Issue the following commands in your terminal (replace `greenspace-kafka-logstash` with your topic) :
```bash
 host$ wget http://www-us.apache.org/dist/kafka/0.10.1.1/kafka_2.10-0.10.1.1.tgz
 host$ tar xvzf kafka_2.10-0.10.1.1.tgz
 host$ cd kafka_2.10-0.10.1.1/
 host$ bin/kafka-console-consumer.sh --bootstrap-server apps.streamly.io:29093 --topic greenspace-mqtt-kafka --from-beginning
```
The output console should look as depicted below:
![streamly-kafka-cassandra-logstash-kafka-consumer][streamly-kafka-cassandra-logstash-kafka-consumer]


## Copyright
Copyright © 2017 Streamly, Inc.

[spark-ui-image]: https://github.com/streamlyio/streamly-spark-examples/raw/master/streamly-mqtt-kafka/images/spark-ui-image.png
[streamly-dashboard]: https://board.streamly.io:20080
[kafka]: https://kafka.apache.org/
[mqtt]: http://mqtt.org/
[blog-post]: http://streamly.io/streamly-new/blog.html
[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[mqtt]: https://www.wut.de/e-mqttw-03-apus-000.php
[cassandra]: http://cassandra.apache.org/
[streamly-kafka-mqtt]: https://github.com/streamlyio/streamly-spark-examples/tree/master/streamly-kafka-mqtt
[streamly-kafka-cassandra-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23525926/743c87cc-ff90-11e6-8ba0-8c17a0d1bc6e.png
[streamly-kafka-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23463864/5e5b2394-fe93-11e6-907c-c7f45f88cd2f.png
[streamly-kafka-cassandra-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23525999/bc037eb2-ff90-11e6-9196-b190acbe7dd1.png
[streamly-kafka-cassandra-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23470714/6cd57f6e-fea7-11e6-8dfe-47f0d70b5b6a.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23526668/2ea2d0ce-ff93-11e6-9cb0-cec08b43c04c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342406/00b63c50-fc5a-11e6-8245-e079bc8d224c.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23526668/2ea2d0ce-ff93-11e6-9cb0-cec08b43c04c.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23526687/4ac46b00-ff93-11e6-9eda-815e9d35b9d7.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23477993/19a74dc2-fec0-11e6-8466-29b918b95218.png
[streamly-mqtt-kafka-ui]: https://cloud.githubusercontent.com/assets/25694018/23527019/88f9ad8a-ff94-11e6-9849-d7f738e43ffe.png
[streamly-mqtt-kafka-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23527097/d80c4086-ff94-11e6-830d-1b6556ac5ce7.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23527187/3e547f98-ff95-11e6-91fa-b88ead60214d.png
[streamly-kafka-cassandra-logstash-kafka-consumer]: https://cloud.githubusercontent.com/assets/25694018/23533263/d0c52006-ffb0-11e6-91e2-5a8deb414801.png
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png