# Streamly Kafka Cassandra with Logstash Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your keyspace](#4-create-your-keyspace)
  - [5. Create your table](#5-create-your-table)
  - [6. Get your streamly access and secret keys](#6-get-your-streamly-access-and-secret-keys)
  - [7. Update your configuration file](#7-update-your-configuration-file)
  - [8. Submit your application](#8-submit-your-application)
  - [9. Monitor your application](#9-monitor-your-application)
  - [10. Check your application logs](#10-check-your-application-logs)
  - [11. Visualize your data](#11-visualize-your-data)
    - [a. Query greenspace_table](#a-query-greenspace_table)
    - [a. Query greenspace_output](#a-query-greenspace_output)
- [Copyright](#copyright)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction

This is a sample stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] after every 2 seconds, counts those events and writes aggregates to [Cassandra].
It also populates the counted events to [Cassandra] using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-cassandradb
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

The job will create the table precise in the spark.properties file (e.g. `greenspace_table`) with the adequate fields for you

### 5. Create your table
To create a new table :

  - Open the Streamly dashboard and switch to the Cassandra tab
  - Select the keyspace you created previously (e.g. `greenspace_keyspace`) and click on `VIEW TABLES`

![streamly-create-keyspaces][streamly-create-keyspaces]
  
  - The list of existing tables should appear. Click on `ADD NEW TABLE`

![streamly-add-new-table][streamly-add-new-table]

  - Specify the table name in the corresponding text field (e.g. `greenspace_output`).
  - Set the field name (e.g. `message`) , the field type (e.g. `text`) and the field option (e.g. `Primary key`)

![streamly-add-table][streamly-add-table]

  - Click on `EXECUTE`

The newly created table should appear in the list of existing tables:

![streamly-list-tables-full][streamly-list-tables-full]

### 6. Get your streamly access and secret keys
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
app.args=apps.streamly.io:29093,system-apache-logs,greenspace_keyspace,greenspace_table,file://logstash.conf
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
    cassandra {
        hosts => ["london201.streamly.io","london202.streamly.io","london205.streamly.io"]
        port => 9042
        keyspace => "greenspace_keyspace" # Keyspace should be prefixed by your namespace
        table => "greenspace_output" 
        username => "ci00jji37jfhq8q" # Username to authenticate ( your access key)
        password => "r30qwridiw8qkxj" # Username to authenticate ( your secret key)
    }
}
```

### 8. Submit your application 
  - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-kafka-cassandradb-logstash-cassandra`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-cassandradb-0.0.1.jar` files
 - Click on ![start][start]
 
 ![streamly-kafka-cassandra-logstash-cassandra][streamly-kafka-cassandra-logstash-cassandra]


### 9. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:
![streamly-kafka-cassandra-logstash-spark-ui][streamly-kafka-cassandra-logstash-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 10. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.
![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 11. Visualize your data
#### a. Query greenspace_table
  - In Streamly dashboard, go to Notebook tab
  - Create a new note
  - Query your table and explore your data

![streamly-kafka-cassandra-logstash-zeppelin-cassandra][streamly-kafka-cassandra-logstash-zeppelin-cassandra]

#### a. Query greenspace_output
  - In Streamly dashboard, go to Notebook tab
  - Create a new note
  - Query your table and explore your data
![streamly-kafka-cassandra-logstash-zeppelin-cassandra2][streamly-kafka-cassandra-logstash-zeppelin-cassandra2]


## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23607834/c53a97d0-0266-11e7-94eb-b3271812b28c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/products/logstash
[logstash_output_cassandra]: https://github.com/PerimeterX/logstash-output-cassandra	
[open-streams]: http://www.streamly.io/open-streams/
[cassandradb]: http://cassandra.apache.org/
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23123079/361e72e2-f767-11e6-929c-676e7a903538.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23522234/86354da0-ff82-11e6-86e2-6701282dd76c.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23672741/f400ef10-036f-11e7-828b-d1dabe5ec38a.png
[streamly-create-keyspaces]: https://cloud.githubusercontent.com/assets/25694018/23621620/cd01d462-029b-11e7-855c-13b14d2d5b97.png
[streamly-list-tables-empty]: https://cloud.githubusercontent.com/assets/25694018/23521195/3c4200ac-ff7e-11e6-8bc2-ce2208a193c2.png
[streamly-add-table]: https://cloud.githubusercontent.com/assets/25694018/23609467/742bd9be-026e-11e7-8f23-d04be3462d9f.png
[streamly-list-tables-full]: https://cloud.githubusercontent.com/assets/25694018/23521218/55a3f690-ff7e-11e6-8def-da180aadf874.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-kafka-cassandra-logstash-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23521634/16354a8e-ff80-11e6-90e0-c194ead8afb6.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra2]: https://cloud.githubusercontent.com/assets/25694018/23672721/da60d354-036f-11e7-9ee0-22546d154bb6.png
[streamly-add-new-table]: https://cloud.githubusercontent.com/assets/25694018/23610193/fd57eb26-0271-11e7-9e88-f64d184b4585.png
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png