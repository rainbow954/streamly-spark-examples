# Streamly Kafka Cassandra with Logstash Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Setup an account](#2-setup-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your keyspace](#4-create-your-keyspace)
  - [5. Create your output topic](#5-create-your-output-topic)
  - [6. Get your access and secret keys](#6-get-your-access-and-secret-keys)
  - [7. Update your configuration file](#7-update-your-configuration-file)
  - [8. Submit your application](#8-submit-your-application)
  - [9. Monitor your application](#9-monitor-your-application)
  - [10. Check your application logs](#10-check-your-application-logs)
  - [11. Visualize your data](#11-visualize-your-data)
    - [a. Query Cassandra](#a-query-cassandra)
    - [b. Query Kafka](#b-query-kafka)
- [Copyright](#copyright)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## Introduction

This is a sample stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] count each word on the event then writes aggregates to [Cassandra].
It also populates the counted words to [Kafka] using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-kafka
 host$ mvn clean install
```

### 2. Setup an account
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
| system-apache-logs           | It contains apache logs gathered from various servers       		|

In this example, we consume events from `system-apache-logs`.


### 4. Create your keyspace
To create a new keyspace :

  - Open the Streamly dashboard and switch to the Cassandra tab
  - Specify the keyspace name in the corresponding text field (e.g. `greenspace_keyspace`). Be sure to prefix it with your namespace.
  - Choose a replication strategy (e.g. `SimpleStrategy`) and define the replication factor (e.g. `1`)

![streamly-create-keyspace][streamly-create-keyspace]

  - Click on Create Keyspace button

The newly created keyspace should appear in the list of existing keyspaces on the right side of the screen:

![streamly-list-keyspace][streamly-list-keyspace]

The job will create the table precise in the spark.properties file (e.g. `greenspace_table`) with the adequate fields for you.

### 5. Create your output topic
To create a new topic :
  
  - Open the Streamly dashboard and switch to the Cassandra tab
  - Specify the topic name in the corresponding text field (e.g. `greenspace-kafka-logstash`). Be sure to prefix it with your namespace.
  - Define the number of partitions(eg `1`), maximum messages(eg `100`), replication(eg `1`), retention(eg `1`) and Authorized hosts(eg `*`). 
  - Enable `Unsecured Read` and `Unsecured Write`

![streamly-create-topic][streamly-create-topic]

  - Click on `Add New Topic`

The newly created topic should appear in the list of existing topics on the right side of the screen:

![streamly-list-topics][streamly-list-topics]

### 6. Get your access and secret keys
  - Open the Streamly dashboard and click on the Profile icon
  - Open the Access Keys Management section and copy your access and secret keys

![streamly-list-apikeys][streamly-list-apikeys]

In this example : access key is `ci00jji37jfhq8q` and secret key is `r30qwridiw8qkxj`.
Access and secret keys authenticate users on Streamly services (Kafka, Mqtt, Cassandra, Elasticsearch,...).


### 7. Update your configuration file
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
main.class=io.streamly.examples.StreamlyKafkaCassandraLogstash
app.args=apps.streamly.io:29093,system-apache-logs,greenspace_keyspace,greenspace_table,-f,file://logstash.conf
app.resource=file://streamly-kafka-cassandra-logstash-es-0.0.1.jar
spark.cassandra.connection.port=9042
spark.cassandra.connection.host=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.cassandra.auth.username=ci00jji37jfhq8q
spark.cassandra.auth.password=r30qwridiw8qkxj
```

Open `logstash.conf` file and edit as appropriate.
We provide you with some dummy input configuration because the input plugin is mandatory for logstash to start properly.
The resulting file should look as depicted below:

```conf
input {
  file {
  	path => "/tmp/dummyfile" # Dummy logstash input file
  }
}
output {
  kafka {
    topic_id => "greenspace-kafka-logstash" # Should be prefixed by your namespace
    bootstrap_servers => ["apps.streamly.io:29093"] # list of kafka nodes
  }
}
```

### 8. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on `Save`. Again, your application name should start with your namespace. In this example the application name is `greenspace-kafka-cassandra-logstash-kafka`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-kafka-0.0.1.jar` files
 - Click on `Start`
 
![streamly-kafka-cassanda-logstash-kafka][streamly-kafka-cassanda-logstash-kafka]

### 9. Monitor your application
Wait until your application's status changes to RUNNING. Click on Show UI icon. You should subsequently see a screen similar to below screen:
![streamly-kafka-cassandra-logstash-spark-ui][streamly-kafka-cassandra-logstash-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 10. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.

![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 11. Visualize your data
#### a. Query Cassandra
  - In Streamly dashboard, go to Notebook tab
  - Create a new note
  - Query your table and explore your data
  
![streamly-kafka-cassandra-logstash-zeppelin-cassandra][streamly-kafka-cassandra-logstash-zeppelin-cassandra]

#### b. Query Kafka
  - Download and query kafka to consume all published events. Issue the following commands in your terminal :
```bash
 host$ wget http://www-us.apache.org/dist/kafka/0.10.1.1/kafka_2.10-0.10.1.1.tgz
 host$ tar xvzf kafka_2.10-0.10.1.1.tgz
 host$ cd kafka_2.10-0.10.1.1/
 host$ bin/kafka-console-consumer.sh --bootstrap-server apps.streamly.io:29093 --topic greenspace-kafka-logstash --from-beginning
```
The output console looks like this: 
![streamly-kafka-cassandra-logstash-kafka-consumer][streamly-kafka-cassandra-logstash-kafka-consumer]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[logstash plugins]: https://www.elastic.co/guide/en/logstash/current/output-plugins.html 
[open-streams]: http://www.streamly.io/open-streams/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23483283/90ef6560-fed2-11e6-8c03-71d3976a3dd5.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23549122/f6ef1ee4-000a-11e7-8d12-26e73c6d86cb.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23470714/6cd57f6e-fea7-11e6-8dfe-47f0d70b5b6a.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23607834/c53a97d0-0266-11e7-94eb-b3271812b28c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23482368/271888a4-fecf-11e6-95a2-e7c5ba962901.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23482456/6d883294-fecf-11e6-9cf4-4c9fed49b140.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-kafka-cassanda-logstash-kafka]: https://cloud.githubusercontent.com/assets/25694018/23483078/b205a36e-fed1-11e6-99b8-fc30ea422bcb.png
[streamly-kafka-cassandra-logstash-kafka-consumer]: https://cloud.githubusercontent.com/assets/25694018/23580340/07d155b0-0100-11e7-809a-a962efee8ea4.png