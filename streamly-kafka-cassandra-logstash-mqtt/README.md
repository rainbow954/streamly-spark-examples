# Streamly Kafka Cassandra with Logstash Example Project

## Introduction

This is a simple stream processing application that you can deploy on [Streamly].
It is written in Java and consumes events from [Kafka] then writes aggregates to [Cassandra].
It also populates events to [Mqtt] using [Logstash].

## Quickstart

### 1. Build the project
Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-bridge
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-mqtt
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


### 4. Create your keyspace
To create a new keyspace :

  - Go to Cassandra tab
  - Provide the name of the keyspace, in the Keyspace Name box (eg `greenspace_keyspace`). It should start with your namespace.
  - Choose the strategy (eg `SimpleStrategy`) and define the replication factor (eg `1`)

![streamly-create-keyspace][streamly-create-keyspace]

  - Click on Create Keyspace button

The keyspace appears in the list of existing keyspaces:

![streamly-list-keyspace][streamly-list-keyspace]

### 5. Create a Mqtt topic
To create a Mqtt topic :

  - Go to Messaging tab
  - Switch type to Mqtt
  - Provide the name of the topic, in the Topic Name box (eg `greenspace/mqtt/topic`). It should start with your namespace.
  - Set authorized hosts to `*` so that the topic can be access from anywhere.

![streamly-create-topic][streamly-create-topic]

  - Click on ADD NEW TOPIC

The topics appears in the list of existing topics:

![streamly-list-topics][streamly-list-topics]                          


### 6. Get your access and secret keys
  - Click on the Profile icon
  - Look at Access Keys Management section

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

The resulting file looks like :

```properties
main.class=io.streamly.examples.StreamlyKafkaCassandraLogstash
app.args=apps.streamly.io:29093,system-bitcoin-transactions,greenspace_keyspace,greenspace_table,-f,file://logstash.conf
app.resource=file://streamly-kafka-cassandra-logstash-mqtt-0.0.1.jar
spark.cassandra.connection.port=9042
spark.cassandra.connection.host=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.cassandra.auth.username=ci00jji37jfhq8q
spark.cassandra.auth.password=r30qwridiw8qkxj
```

Open `logstash.conf` file and replace empty settings with correct values. <br/>
We provide you with some dummy input configuration because the 
input plugin is mandatory for logstash to start properly.
Hence, the data populated into elasticsearch come from the spark RDD.

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
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-kafka-cassandra-logstash-mqtt`.
 - Upload `logstash.conf`, `spark.properties` and `streamly-kafka-cassandra-logstash-mqtt-0.0.1.jar` files
 - Click on the Start icon
 
![streamly-kafka-cassanda-logstash-kafka][streamly-kafka-cassanda-logstash-kafka]

### 9. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-cassandra-logstash-spark-ui][streamly-kafka-cassandra-logstash-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 10. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 11. Visualize your data
#### a. Query Cassandra
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result
![streamly-kafka-cassandra-logstash-zeppelin-cassandra][streamly-kafka-cassandra-logstash-zeppelin-cassandra]

#### b. Query Mqtt
In your local machine : 
  - Install mosquitto-clients
  - On Windows <br /> 
    Go to http://www.eclipse.org/downloads/download.php?file=/mosquitto/binary/win32/mosquitto-1.4.11-install-win32.exe and download mosquitto
  - On Centos
```bash
 host$ sudo yum -y install epel-release
 host$ sudo yum -y install mosquitto
 host$ sudo systemctl start mosquitto
```
  - On Debian
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
  - Consume events published into your MQTT topic 
```bash
 host$ mosquitto_sub -h apps.streamly.io -p 21883 -t greenspace/mqtt/topic -q 1 -u ci00jji37jfhq8q -P r30qwridiw8qkxj
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
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23470714/6cd57f6e-fea7-11e6-8dfe-47f0d70b5b6a.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png
[streamly-list-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342406/00b63c50-fc5a-11e6-8245-e079bc8d224c.png
[streamly-create-keyspace]: https://cloud.githubusercontent.com/assets/25694018/23342425/61cf2970-fc5a-11e6-81c3-6e5aab35e71e.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23464521/a0368b08-fe95-11e6-8851-4a205d4d99e3.png
[streamly-kafka-cassanda-logstash-kafka]: https://cloud.githubusercontent.com/assets/25694018/23549942/bbfe10a2-000e-11e7-9389-e3ead84a4104.png
[streamly-kafka-cassandra-logstash-kafka-consumer]: https://cloud.githubusercontent.com/assets/25694018/23549941/bbfdbeae-000e-11e7-9619-1652616f31b8.png