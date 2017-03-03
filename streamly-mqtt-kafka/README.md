# Streamly Mqtt Kafka Example Project


## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Mqtt] [mqtt] then writes aggregates to [Kafka].


## Quickstart

### 1. Build the project

Assuming git, java and maven installed:

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-kafka
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
We assume that you have followed up the [streamly-kafka-mqtt] project, because this project creates a MQTT topic and sends some data inside. This topic is called  `greenspace/mqtt/topic`. In the next steps, we consume events from  `greenspace/mqtt/topic`.

### 4. Create your output topic
To create a new topic :
  
  - Go to Messaging tab
  - Write the name of the topic in Topic Name box. We assume that the name is `greenspace-mqtt-kafka`.
  - Define the number of partitions(eg `1`), maximum messages(eg `100`), replication(eg `1`), retention(eg `1`) and Authorized hosts(eg `*`). 
  - Enable Unsecured read and Unsecured write buttons.

![streamly-create-topic][streamly-create-topic]

  - Click on Add New Index button

The topic appears in the list of existing topics:

![streamly-list-topics][streamly-list-topics]

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

The resulting file looks like :

```properties
main.class=io.streamly.examples.StreamlyMqttKafka
app.resource=file://streamly-mqtt-kafka-0.0.1.jar
app.args=tcp://apps.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,apps.streamly.io:29093,greenspace-mqtt-kafka
```

### 7. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-mqtt-kafka`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-mqtt-kafka-0.0.1.jar` files
 - Click on the Start icon
 
![streamly-mqtt-kafka-ui][streamly-mqtt-kafka-ui]

### 8. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-mqtt-kafka-spark-ui][streamly-mqtt-kafka-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 9. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 10. Visualize your data
  - Download and query kafka and consume events from your kafka topic
```bash
 host$ wget http://www-us.apache.org/dist/kafka/0.10.1.1/kafka_2.10-0.10.1.1.tgz /tmp/kafka
 host$ cd /tmp/kafka
 host$ bin/kafka-console-consumer.sh --bootstrap-server apps.streamly.io:29093 --topic greenspace-mqtt-kafka --from-beginning
```
The output console looks like this: 
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
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23464521/a0368b08-fe95-11e6-8851-4a205d4d99e3.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23477215/8b354d66-febd-11e6-9384-44f941ffc783.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23526687/4ac46b00-ff93-11e6-9eda-815e9d35b9d7.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23477993/19a74dc2-fec0-11e6-8466-29b918b95218.png
[streamly-mqtt-kafka-ui]: https://cloud.githubusercontent.com/assets/25694018/23527019/88f9ad8a-ff94-11e6-9849-d7f738e43ffe.png
[streamly-mqtt-kafka-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23527097/d80c4086-ff94-11e6-830d-1b6556ac5ce7.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23527187/3e547f98-ff95-11e6-91fa-b88ead60214d.png
[streamly-kafka-cassandra-logstash-kafka-consumer]: https://cloud.githubusercontent.com/assets/25694018/23533263/d0c52006-ffb0-11e6-91e2-5a8deb414801.png