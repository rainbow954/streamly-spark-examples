# Streamly Kafka Elasticsearch Example Project

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
It is written in Java and consumes events from [Kafka] after every 2 seconds, counts them then writes aggregates to [Elasticsearch].


## Quickstart


### 1. Build the project
Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-elasticsearch
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
| system-apache-logs           | It contains apache logs gathered from various servers          |

This example consumes events from `system-bitcoin-transactions`.

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
main.class=io.streamly.examples.StreamlyKafkaElasticsearch
app.args=apps.streamly.io:29093,system-bitcoin-transactions,greenspace-myindex/logs
app.resource=file://streamly-kafka-elasticsearch-0.0.1.jar
spark.es.resource=greenspace-myindex/logs
spark.es.nodes=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.es.port=9200
spark.es.net.http.auth.user=ci00jji37jfhq8q
spark.es.net.http.auth.pass=r30qwridiw8qkxj
```

### 7. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-kafka-es`.
 - Upload `spark.properties` and `streamly-kafka-elasticsearch-0.0.1.jar` files
 - Click on ![start][start]

![streamly-kafka-elasticsearch][streamly-kafka-elasticsearch]

### 8. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:
![streamly-kafka-elasticsearch-spark-ui][streamly-kafka-elasticsearch-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 9. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.
![streamly-kafka-elasticsearch-kibana-ui][streamly-kafka-elasticsearch-kibana-ui]

### 10. Visualize your data
  - In Streamly Dashboard, go to Kibana tab
  - Create a new index pattern with your index name (e.g. `greenspace-myindex`)

![streamly-kafka-elasticsearch-kibana-index-pattern][streamly-kafka-elasticsearch-kibana-index-pattern]

  - Since our streaming application does not generate time field data in Elasticsearch, Kibana is not able to display documents of this index on the discover tab. Nevertheless looking at the screenshot below, we can see the data actually populated to elasticsearch.

![streamly-kafka-elasticsearch-kibana-discover][streamly-kafka-elasticsearch-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[kafka]: https://kafka.apache.org/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-kafka-elasticsearch-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23468983/99758d3e-fea2-11e6-82df-080d6de5f2bf.png
[streamly-kafka-elasticsearch]: https://cloud.githubusercontent.com/assets/25694018/23468574/6705b884-fea1-11e6-9e21-dc9eb5b84cfd.png
[streamly-kafka-elasticsearch-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23680413/700730d6-038b-11e7-8f98-701c6c686edb.png
[streamly-kafka-elasticsearch-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23481368/6ab3e378-fecb-11e6-8bbe-eb585d185015.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23468239/9450193e-fea0-11e6-8cb1-1d7ee64d464e.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23468239/9450193e-fea0-11e6-8cb1-1d7ee64d464e.png
[streamly-list-indexes]: https://cloud.githubusercontent.com/assets/25694018/23611875/5530b664-0279-11e7-8980-5272a98353b4.png
[streamly-kafka-elasticsearch-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23469043/cfb53084-fea2-11e6-94fa-080cb005b2fb.png
[open-streams]: http://www.streamly.io/open-streams/
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png
