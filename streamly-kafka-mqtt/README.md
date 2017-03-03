# Streamly Kafka Mqtt Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] and writes aggregates to [Mqtt].

## Quickstart

### 1. Build the project

Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-mqtt
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

In this example, we consume events from `system-bitcoin-transactions`.


### 4. Create a Mqtt topic
To create a Mqtt topic :

  - Go to Messaging tab
  - Switch type to Mqtt
  - Provide the name of the topic, in the Topic Name box (eg `greenspace/mqtt/topic`). It should start with your namespace.
  - Set authorized hosts to `*` so that the topic can be access from anywhere.

![streamly-create-topic][streamly-create-topic]

  - Click on ADD NEW TOPIC

The topics appears in the list of existing topics:

![streamly-list-topics][streamly-list-topics]                          |


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
main.class=io.streamly.examples.StreamlyKafkaMqtt
app.args=tcp://board.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,board.streamly.io:29093,system-bitcoin-transactions
app.resource=file://streamly-kafka-mqtt-0.0.1.jar
```

### 7. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-kafka-mqtt`.
 - Upload `spark.properties` and `streamly-kafka-mqtt-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-mqtt-submit][streamly-kafka-mqtt-submit]


### 8. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-mqtt-spark-ui][streamly-kafka-mqtt-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 9. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-mqtt-kibana-ui][streamly-kafka-mqtt-kibana-ui]

### 10. Visualize your data
In your local machine : 
  - Install mosquitto-clients
  - On windows <br /> 
    Go to http://www.eclipse.org/downloads/download.php?file=/mosquitto/binary/win32/mosquitto-1.4.11-install-win32.exe and download mosquitto
  - On centos
```bash
 host$ sudo yum -y install epel-release
 host$ sudo yum -y install mosquitto
 host$ sudo systemctl start mosquitto
```
  - On ubuntu
```bash
 host$ sudo apt-get install mosquitto
```
  - On Mac <br />
    Assume that brew is already installed
```bash
 host$ ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
 host$ brew doctor
 host$ brew prune
 host$ brew install mosquitto
 host$ ln -sfv /usr/local/opt/mosquitto/*.plist ~/Library/LaunchAgents
 host$ launchctl load ~/Library/LaunchAgents/homebrew.mxcl.mosquitto.plist
```
  - Consume events published into your MQTT topic with your credentials
```bash
 host$ mosquitto_sub -h apps.streamly.io -p 21883 -t greenspace/mqtt/topic -q 1 -u ci00jji37jfhq8q -P r30qwridiw8qkxj
 ```
     The output console looks like this: 
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
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23464521/a0368b08-fe95-11e6-8851-4a205d4d99e3.png
[streamly-kafka-mqtt-consumer]: https://cloud.githubusercontent.com/assets/25694018/23533262/d0c386f6-ffb0-11e6-93fd-ca38193bcad4.png
[streamly-kafka-mqtt-submit]: https://cloud.githubusercontent.com/assets/25694018/23477460/49096642-febe-11e6-833a-1bdffc0fa7f5.png