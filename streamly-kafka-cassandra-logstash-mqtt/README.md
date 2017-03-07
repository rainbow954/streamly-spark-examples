# Streamly Kafka Cassandra with Logstash Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create your keyspace](#4-create-your-keyspace)
  - [5. Create a Mqtt topic](#5-create-a-mqtt-topic)
  - [6. Get your access and secret keys](#6-get-your-access-and-secret-keys)
  - [7. Update your configuration file](#7-update-your-configuration-file)
  - [8. Submit your application](#8-submit-your-application)
  - [9. Monitor your application](#9-monitor-your-application)
  - [10. Check your application logs](#10-check-your-application-logs)
  - [11. Visualize your data](#11-visualize-your-data)
    - [a. Query Cassandra](#a-query-cassandra)
    - [b. Query Mqtt](#b-query-mqtt)
- [Copyright](#copyright)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## Introduction

This is a sample stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] after every 2 seconds, counts those events then writes aggregates to [Cassandra].
It also populates the counted events to [Mqtt] using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-mqtt
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

### 5. Create a Mqtt topic
To create a Mqtt topic :

  - Open the Streamly dashboard and switch to Messaging tab
  - In type field select `MQTT`
  - Specify the topic name in the corresponding text field (e.g. `greenspace/mqtt/topic`). It should start with your namespace.
  - Set Authorized Hosts to `*` so that the topic can be access from anywhere.

![streamly-create-topic][streamly-create-topic]

  - Click on `ADD NEW TOPIC`

The newly created topic should appear in the list of existing topics on the right side of the screen:

![streamly-list-topics][streamly-list-topics]                          


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
app.resource=file://streamly-kafka-cassandra-logstash-mqtt-0.0.1.jar
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
    mqtt {
     host => "apps.streamly.io" # Mqtt host
     port => 21883 # Mqtt port
     topic => "greenspace/mqtt/topic" # Mqtt topic
     username => "ci00jji37jfhq8q"  # Access key
     password => "r30qwridiw8qkxj"  # Secret key
    }
}
```

### 8. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-kafka-cassandra-logstash-mqtt`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-mqtt-0.0.1.jar` files
 - Click on ![start][start]
 
![streamly-kafka-cassanda-logstash-kafka][streamly-kafka-cassanda-logstash-kafka]

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

#### b. Query Mqtt
Install mosquitto in your local machine by typing the following commands in your terminal:

  - On Centos 7
```bash
 host$ sudo yum install epel-release -y
 host$ sudo yum install mosquitto -y 
```
  - On Debian
```bash
 host$ sudo apt-get install mosquitto-clients -y 
```
  - On Mac
```bash
 host$ ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
 host$ brew doctor
 host$ brew prune
 host$ brew install mosquitto
 host$ ln -sfv /usr/local/opt/mosquitto/*.plist ~/Library/LaunchAgents
 host$ launchctl load ~/Library/LaunchAgents/homebrew.mxcl.mosquitto.plist
```
  - On Windows
    - Go to http://www.eclipse.org/downloads/download.php?file=/mosquitto/binary/win32/mosquitto-1.4.11-install-win32.exe and download mosquitto
    - Double click the downloaded exe to install mosquitto

Consume events from your MQTT topic. Replace `greenspace/mqtt/topic` with your topic, `ci00jji37jfhq8q` with your access key and `r30qwridiw8qkxj` with your secret key.

 ```bash
 host$ mosquitto_sub -h apps.streamly.io -p 21883 -t greenspace/mqtt/topic -q 1 -u ci00jji37jfhq8q -P r30qwridiw8qkxj
 ``` 

  The output console should look as depicted below:

![streamly-kafka-cassandra-logstash-kafka-consumer][streamly-kafka-cassandra-logstash-kafka-consumer]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[kafka]: https://kafka.apache.org/
[mqtt]: http://mqtt.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[logstash plugins]: https://www.elastic.co/guide/en/logstash/current/output-plugins.html 
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23477215/8b354d66-febd-11e6-9384-44f941ffc783.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23477275/bedb827a-febd-11e6-898f-cd5ac571bd2f.png
[open-streams]: http://streamly.io/streamly-new/streams.html
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23549940/bbfca1e0-000e-11e7-8308-b32603f37271.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23549943/bbfefc24-000e-11e7-8d7e-6816f166978e.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23672741/f400ef10-036f-11e7-828b-d1dabe5ec38a.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23607834/c53a97d0-0266-11e7-94eb-b3271812b28c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-kafka-cassanda-logstash-kafka]: https://cloud.githubusercontent.com/assets/25694018/23549942/bbfe10a2-000e-11e7-9389-e3ead84a4104.png
[streamly-kafka-cassandra-logstash-kafka-consumer]: https://cloud.githubusercontent.com/assets/25694018/23674978/0dbe16fa-0378-11e7-9074-f42fb93ceb70.png
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png