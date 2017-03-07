# Streamly Kafka Cassandra with Logstash Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your keyspace](#4-create-your-keyspace)
  - [5. Create your index](#5-create-your-index)
  - [6. Get your access and secret keys](#6-get-your-access-and-secret-keys)
  - [7. Update your configuration file](#7-update-your-configuration-file)
  - [8. Submit your application](#8-submit-your-application)
  - [9. Monitor your application](#9-monitor-your-application)
  - [10. Check your application logs](#10-check-your-application-logs)
  - [11. Visualize your data](#11-visualize-your-data)
    - [a. Query Cassandra](#a-query-cassandra)
    - [b. Query Elasticsearch](#b-query-elasticsearch)
- [Copyright](#copyright)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction

This is a sample stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] after every 2 seconds, counts those events and writes aggregates to [Cassandra].
It also populates the counted events as logs to [Elasticsearch] using [Logstash].

## Quickstart

### 1. Build the project

Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-es
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
| system-apache-logs           | It contains apache logs gathered from various servers       		|

This example consumes events from `system-apache-logs`.


### 4. Create your keyspace
To create a new keyspace :

  - Open the Streamly dashboard and switch to the Cassandra tab
  - Specify the keyspace name in the corresponding text field (e.g. `greenspace_keyspace`). Be sure to prefix it with your namespace.
  - Choose a replication strategy (e.g. `SimpleStrategy`) and define the replication factor (e.g. `1`)

![streamly-create-keyspace][streamly-create-keyspace]

  - Click on `CREATE NEW KEYSPACE`

The newly created keyspace should appear in the list of existing keyspaces on the right side of the screen:

![streamly-list-keyspace][streamly-list-keyspace]

The job will create the table precise in the spark.properties file (e.g. `greenspace_table`) with the adequate fields for you.

### 5. Create your index
To create a new index :
  
  - Open the Streamly dashboard and switch to the Elasticsearch tab
  - Specify the index name in the corresponding text field (e.g. `greenspace-index-to-es`). Be sure to prefix it with your namespace.
  - Define the number of replicas (e.g. `1`)

![streamly-create-index][streamly-create-index]

  - Click on `ADD NEW INDEX`

The newly created index should appear in the list of existing indexes on the right side of the screen:

![streamly-list-indexes][streamly-list-indexes]

### 6. Get your access and secret keys
  - Open the Streamly dashboard and click on ![profile][profile]
  - Copy your access and secret keys in the `Access Keys Management` section

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
  elasticsearch {
    hosts => ["london201.streamly.io:9200","london202.streamly.io:9200","london205.streamly.io:9200"]  # list of elasticsearch nodes
    user => "ci00jji37jfhq8q" # Username to authenticate ( your access key)
    password => "r30qwridiw8qkxj" # Password to authenticate ( your secret key)
    manage_template => true
    index => "greenspace-index-to-es2" # Should be prefixed by your namespace
  }
}
```

### 8. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-kafka-cassandra-logstash-es`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-es-0.0.1.jar` files
 - Click on ![start][start]

![streamly-kafka-cassandra-logstash-es][streamly-kafka-cassandra-logstash-es]

### 9. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:

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

#### b. Query Elasticsearch
  - In Streamly dashboard, go to Kibana tab
  - Create a new index pattern with your index name and @timestamp as time-field name

![streamly-kafka-cassandra-logstash-kibana-index-pattern][streamly-kafka-cassandra-logstash-kibana-index-pattern]

  - Go to discover

![streamly-kafka-cassandra-logstash-kibana-discover][streamly-kafka-cassandra-logstash-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[open-streams]: http://www.streamly.io/open-streams/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23479954/7bb84cb8-fec6-11e6-9028-fb77fff3e615.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23673553/ffc2e6d4-0372-11e7-982e-2fa8e7244302.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23672741/f400ef10-036f-11e7-828b-d1dabe5ec38a.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23673582/1a675b78-0373-11e7-83bc-48fe463210a0.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23673492/c8aa787e-0372-11e7-8308-c9c4f0e0b87c.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23129771/4375024a-f784-11e6-97ca-7d3b16b06929.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23478736/8b9c7996-fec2-11e6-9fd4-8cc4a798ca5d.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23607834/c53a97d0-0266-11e7-94eb-b3271812b28c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-list-indexes]: https://cloud.githubusercontent.com/assets/25694018/23478988/4d5393f8-fec3-11e6-8b56-84db0b04a9b3.png
[streamly-kafka-cassandra-logstash-es]: https://cloud.githubusercontent.com/assets/25694018/23479559/23e5ab26-fec5-11e6-90dd-823d6b6d18e1.png
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png

