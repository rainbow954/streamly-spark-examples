# Streamly Kafka Mqtt Example Project

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->


- [Introduction](#introduction)
- [Quickstart](#quickstart)
  - [1. Build the project](#1-build-the-project)
  - [2. Create an account](#2-create-an-account)
  - [3. Choose the topic to read from](#3-choose-the-topic-to-read-from)
  - [4. Create a Mqtt topic](#4-create-a-mqtt-topic)
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
It is written in Java and consumes events from [Kafka] after every 2 seconds, counts those events and writes aggregates to [Mqtt].

## Quickstart

### 1. Build the project

Assuming git, java, and maven are installed on your machine. Issue the following commands in your terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-mqtt
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


### 4. Create a Mqtt topic
To create a Mqtt topic :

  - Open the Streamly dashboard and switch to Messaging tab
  - In type field select `MQTT`
  - Specify the topic name in the corresponding text field (e.g. `greenspace/mqtt/topic`). It should start with your namespace.
  - Set Authorized Hosts to `*` so that the topic can be access from anywhere.

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
main.class=io.streamly.examples.StreamlyKafkaMqtt
app.args=tcp://apps.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,apps.streamly.io:29093,system-bitcoin-transactions
app.resource=file://streamly-kafka-mqtt-0.0.1.jar
```

### 7. Submit your application 
 - Open the Processing tab in the Streamly dashboard
 - Click on Add Application. A new application is created with name: `No Name`.
 - Provide a valid name for your application and click on ![save][save]. Again, your application name should start with your namespace. In this example the application name is `greenspace-kafka-mqtt`.
 - Upload `spark.properties` and `streamly-kafka-mqtt-0.0.1.jar` files
 - Click on ![start][start]

![streamly-kafka-mqtt-submit][streamly-kafka-mqtt-submit]


### 8. Monitor your application
Wait until your application's status changes to RUNNING. Click on ![show-ui][show-ui]. You should subsequently see a screen similar to below screen:
![streamly-kafka-mqtt-spark-ui][streamly-kafka-mqtt-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 9. Check your application logs
Application logs are populated in Elasticsearch and can be visualized in Kibana. No manual configuration needed.
![streamly-kafka-mqtt-kibana-ui][streamly-kafka-mqtt-kibana-ui]

### 10. Visualize your data
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

![streamly-kafka-mqtt-consumer][streamly-kafka-mqtt-consumer]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[kafka]: https://kafka.apache.org/
[mqtt]: http://mqtt.org/
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-create-mqtt-topic]: https://cloud.githubusercontent.com/assets/25694018/23137087/52f97108-f7a0-11e6-8567-56c91625cbbe.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23477215/8b354d66-febd-11e6-9384-44f941ffc783.png
[streamly-list-topics]: https://cloud.githubusercontent.com/assets/25694018/23477275/bedb827a-febd-11e6-898f-cd5ac571bd2f.png
[open-streams]: http://streamly.io/streamly-new/streams.html
[mosquitto-clients]: https://mosquitto.org/download/
[streamly-kafka-mqtt-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23477616/d0500b42-febe-11e6-82e4-ad38f3294bad.png
[streamly-kafka-mqtt-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23477533/8f90a24c-febe-11e6-8a24-8ff10f273e62.png
[streamly-kafka-mqtt]: https://cloud.githubusercontent.com/assets/25694018/23140981/c6a0bfe4-f7b4-11e6-80db-3823b5116599.png
[streamly-list-apikeys]: hhttps://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23676152/079e7ea0-037c-11e7-9a18-99acb643d9b5.png
[streamly-kafka-mqtt-submit]: https://cloud.githubusercontent.com/assets/25694018/23477460/49096642-febe-11e6-833a-1bdffc0fa7f5.png
[save]: https://cloud.githubusercontent.com/assets/25694018/23614986/3086f3da-0285-11e7-9eb0-0c141e1fb5ff.png
[start]: https://cloud.githubusercontent.com/assets/25694018/23615196/e7976a50-0285-11e7-92d0-e10c1bab0165.png
[profile]: https://cloud.githubusercontent.com/assets/25694018/23615301/3da3d06e-0286-11e7-8118-038ee1a22e92.png
[show-ui]: https://cloud.githubusercontent.com/assets/25694018/23653314/64a964c0-032c-11e7-9610-4d89de66e7bf.png