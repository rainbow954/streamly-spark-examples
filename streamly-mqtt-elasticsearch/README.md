# Streamly Mqtt Elasticsearch Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your index](#4-create-your-index)
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
It is written in Java and consumes events from [Mqtt] then writes aggregates to [Elasticsearch].


## Quickstart

### 1. Build the project
Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-elasticsearch
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

### 4. Create your index 
To create a new index :
  
  - Open the Streamly dashboard and switch to the Elasticsearch tab
  - Specify the index name in the corresponding text field (e.g. `greenspace-myindex`). Be sure to prefix it with your namespace.
  - Define the number of replicas (e.g. `1`)

![streamly-create-index][streamly-create-index]

  - Click on `ADD NEW INDEX`

The newly created index should appear in the list of existing indexes on the right side of the screen:

![streamly-list-indexes][streamly-list-indexes]

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
| app.resource                          | Elasticsearch http client port                      |
| spark.es.port                         | Cassandra native connection port                    |
| spark.es.nodes                        | Comma separated Elasticsearch hosts                 |
| spark.es.net.http.auth.user           | Access key          			                      |
| spark.es.net.http.auth.pass           | Secret key                                          |
| spark.es.resource                     | Elasticsearch resources, here we used the index/type|

The resulting file should look as depicted below:

```properties
main.class=io.streamly.examples.StreamlyMqttElasticsearch
app.args=tcp://apps.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,greenspace-myindex,logs
app.resource=file://streamly-mqtt-elasticsearch-0.0.1.jar
spark.es.resource=greenspace-myindex/logs
spark.es.nodes=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.es.port=9200
spark.es.net.http.auth.user=ci00jji37jfhq8q
spark.es.net.http.auth.pass=r30qwridiw8qkxj
```

### 7. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-mqtt-es`.
 - Upload `spark.properties` and `streamly-mqtt-elasticsearch-0.0.1.jar` files
 - Click on ![start][start]

![streamly-mqtt-elasticsearch][streamly-mqtt-elasticsearch]

### 8. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:
![streamly-mqtt-elasticsearch-spark-ui][streamly-mqtt-elasticsearch-spark-ui]
You can see how our Spark Streaming job _processes_ the Mqtt events stream.

### 9. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.
![streamly-mqtt-elasticsearch-kibana-ui][streamly-mqtt-elasticsearch-kibana-ui]

### 10. Visualize your data
  - In Streamly Dashboard, go to Kibana tab
  - Create a new index pattern with your index name (e.g. `greenspace-myindex`)

![streamly-mqtt-elasticsearch-kibana-index-pattern][streamly-mqtt-elasticsearch-kibana-index-pattern]

  - Since the data sent by the spark job do not generate @timestamp, kibana won't be able to display them on the discover tab. Nevertheless looking at the screenshot below, we can see the data from the bitcoins topic are actually populated to elasticsearch.

![streamly-mqtt-elasticsearch-kibana-discover][streamly-mqtt-elasticsearch-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-kafka-mqtt]: https://github.com/streamlyio/streamly-spark-examples/tree/master/streamly-kafka-mqtt
[mqtt]: http://mqtt.org/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[streamly-mqtt-elasticsearch-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23481368/6ab3e378-fecb-11e6-8bbe-eb585d185015.png
[streamly-mqtt-elasticsearch-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23481441/9e1dff00-fecb-11e6-9e38-9de49622c56b.png
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23468239/9450193e-fea0-11e6-8cb1-1d7ee64d464e.png
[streamly-list-indexes]: https://cloud.githubusercontent.com/assets/25694018/23468146/4b761a60-fea0-11e6-9db5-dd5fcd20edcd.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-mqtt-elasticsearch-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23531279/078726bc-ffa6-11e6-81de-312d92d272d1.png
[streamly-mqtt-elasticsearch]: https://cloud.githubusercontent.com/assets/25694018/23531280/07d2018c-ffa6-11e6-9be4-9ff30e420e7b.png
[streamly-mqtt-elasticsearch-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23531377/6ca17e1c-ffa6-11e6-8d2d-61879ff366a9.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png