# Streamly Mqtt Cassandra Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Mqtt] [mqtt] then writes aggregates to [Cassandra].


**Running this requires an account on Streamly Dashboard.**

## Quickstart

### 1. Build the project

Assuming git, java and maven installed:

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-cassandra
 host$ mvn clean install
```

### 2. Update the configuration file
Open `spark.properties` file and edit as appropriate.

### 3. Running the job on Streamly Dashboard
 - Log into [Streamly Dashboard] [streamly-dashboard]
 - Create an application in the Processing tab
 - Upload both `spark.properties` and `streamly-mqtt-cassandra-0.0.1.jar`
 - Click on the start icon

### 4. Monitoring your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![spark ui image][spark-ui-image]

You can see how our Spark Streaming job _processes_ the Kafka events stream.

[spark-ui-image]: https://github.com/streamlyio/streamly-spark-examples/raw/master/streamly-mqtt-cassandra/images/spark-ui-image.png
[streamly-dashboard]: https://board.streamly.io:20080
[cassandra]: http://cassandra.apache.org/
[mqtt]: http://mqtt.org/
[blog-post]: http://streamly.io/streamly-new/blog.html