# Streamly Mqtt Cassandra Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your keyspace](#4-create-your-keyspace)
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
It is written in Java and consumes events from [Mqtt] count each word on the event then writes aggregates to [Cassandra].

## Quickstart

### 1. Build the project

Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-cassandra
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

### 4. Create your keyspace
To create a new keyspace :

  - Open the Streamly dashboard and switch to the Cassandra tab
  - Specify the keyspace name in the corresponding text field (e.g. `greenspace_keyspace`). Be sure to prefix it with your namespace.
  - Choose a replication strategy (e.g. `SimpleStrategy`) and define the replication factor (e.g. `1`)
  
![streamly-create-keyspace][streamly-create-keyspace]

  - Click on `CREATE NEW KEYSPACE`

The newly created keyspace should appear in the list of existing keyspaces on the right side of the screen:

![streamly-list-keyspace][streamly-list-keyspace]

The job will create the table precise in the spark.properties file (e.g. `greenspace_table`) with the adequate fields for you

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
| spark.cassandra.connection.port       | Cassandra native connection port                    |
| spark.cassandra.connection.host       | Comma separated Cassandra hosts                     |
| spark.cassandra.auth.username         | Access key          			                      |
| spark.cassandra.auth.password         | Secret key             							  |

The resulting file should look as depicted below:

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
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-mqtt-cassandra`.
 - Upload `spark.properties` and `streamly-mqtt-cassandra-0.0.1.jar` files
 - Click on ![start][start]

![streamly-mqtt-cassandra][streamly-mqtt-cassandra]

### 8. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:
![streamly-mqtt-cassandra-spark-ui][streamly-mqtt-cassandra-spark-ui]
You can see how our Spark Streaming job _processes_ the Mqtt events stream.

### 9. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.
![streamly-mqtt-cassandra-kibana-ui][streamly-mqtt-cassandra-kibana-ui]

### 10. Visualize your data
  - In Streamly dashboard, go to Notebook tab
  - Create a new note
  - Query your table and explore your data

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
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23607834/c53a97d0-0266-11e7-94eb-b3271812b28c.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23611534/1f7671cc-0278-11e7-92f3-ac400a2d5c87.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23477215/8b354d66-febd-11e6-9384-44f941ffc783.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23477275/bedb827a-febd-11e6-898f-cd5ac571bd2f.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23477993/19a74dc2-fec0-11e6-8466-29b918b95218.png
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png