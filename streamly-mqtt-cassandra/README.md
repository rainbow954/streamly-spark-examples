# Streamly Mqtt Cassandra Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Mqtt] then writes aggregates to [Cassandra].


**Running this requires an account on Streamly Dashboard.**

## Quickstart

### 1. Build the project

Assuming git, java and maven installed:

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-cassandra
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

### 4. Create your keyspace
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
main.class=io.streamly.examples.StreamlyMqttCassandra
app.args=tcp://apps.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,greenspace_keyspace,greenspace_table
app.resource=file://streamly-mqtt-cassandra-0.0.1.jar
spark.cassandra.connection.port=9042
spark.cassandra.connection.host=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.cassandra.auth.username=ci00jji37jfhq8q
spark.cassandra.auth.password=r30qwridiw8qkxj
```

### 7. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-mqtt-cassandra`.
 - Upload `spark.properties` and `streamly-mqtt-cassandra-0.0.1.jar` files
 - Click on the Start icon

![streamly-mqtt-cassandra][streamly-mqtt-cassandra]

### 8. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-mqtt-cassandra-spark-ui][streamly-mqtt-cassandra-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 9. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-mqtt-cassandra-kibana-ui][streamly-mqtt-cassandra-kibana-ui]

### 10. Visualize your data
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result

![streamly-mqtt-cassandra-zeppelin-cassandra][streamly-mqtt-cassandra-zeppelin-cassandra]


## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[mqtt]: https://www.wut.de/e-mqttw-03-apus-000.php
[cassandra]: http://cassandra.apache.org/
[streamly-kafka-mqtt]: https://github.com/streamlyio/streamly-spark-examples/tree/master/streamly-kafka-mqtt
[streamly-mqtt-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23525892/57d9383c-ff90-11e6-9394-e1b7c7501d8a.png
[streamly-mqtt-cassandra-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23525926/743c87cc-ff90-11e6-8ba0-8c17a0d1bc6e.png
[streamly-mqtt-cassandra-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23525999/bc037eb2-ff90-11e6-9196-b190acbe7dd1.png
[streamly-mqtt-cassandra-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23470714/6cd57f6e-fea7-11e6-8dfe-47f0d70b5b6a.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342406/00b63c50-fc5a-11e6-8245-e079bc8d224c.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23464521/a0368b08-fe95-11e6-8851-4a205d4d99e3.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23477215/8b354d66-febd-11e6-9384-44f941ffc783.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23477275/bedb827a-febd-11e6-898f-cd5ac571bd2f.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23477993/19a74dc2-fec0-11e6-8466-29b918b95218.png
