# Streamly Kafka Cassandra with Logstash Example Project

## Introduction
This is a simple stream processing job written in Java for the [Streamly Dashboard] [streamly-dashboard] cluster computing platform, consuming events from [Apache Kafka] [kafka] and writing aggregates to [Apache Cassandra] [cassandra]. It also populates events into a file using [Logstash] [logstash] plugins.

**Running this requires an account on Streamly Dashboard.**

## Quickstart

### 1. Build the project
Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/spark-logstash-dummy
 host$ mvn clean install
 host$ cd ../streamly-kafka-cassandra-logstash-file
 host$ mvn clean install
```
### 2. Create your topic
You can create a kafka topic on [Streamly Dashboard] [streamly-dashboard] or use an existing one. There are [Open Streams][open-streams] topics available to all registered users. 

### 3. Create your keyspace
When you register on [Streamly Dashboard] [streamly-dashboard], you have a default keyspace. You can either use it or create a new keyspace. 

### 5. Update configuration files
Open `spark.properties` file and edit as appropriate.

| Name                                  | Description                															 |
|---------------------------------------|----------------------------------------------------------------------------------------|
| main.class                            | The entry point for your application                                                   |
| app.args                              | Arguments passed to the main method                                                    |
| app.resource                          | Name of the bundled jar including your application                                     |
| spark.cassandra.connection.port       | Cassandra native connection port                                                       |
| spark.cassandra.connection.host       | Comma separated Cassandra hosts                                                        |
| spark.cassandra.auth.username         | Your access key available in the Profile section  of your Streamly account             |
| spark.cassandra.auth.password         | Your secret key available in the Profile section  of your Streamly account             |

Open `logstash.conf` file and replace empty settings with correct values.

```conf
input {
  kafka { 
  bootstrap_servers => ["192.168.0.206:9093"] # list of kafka nodes
  topics => ["system-bitcoin-transactions"] # list of kafka topics with unsecured read
        codec => "json"
        session_timeout_ms => "30000"
        group_id => "streamly-kafka-logstash-elasticsearch-group" 
  }
}
output {
  file {
  	path => "./output-%{+YYYY-MM-dd}.txt"
  	create_if_deleted => true # create the file if it does not exist
  }
}
```

### 6. Submit your application on Streamly Dashboard
 - Log into [Streamly Dashboard] [streamly-dashboard]
 - Create an application in the Processing tab
 - Provide a valid name for your application
 - Upload  `logstash.conf`,`spark.properties` and `streamly-kafka-cassandra-logstash-file-0.0.1.jar` files
 - Click on the start icon
![streamly-kafka-cassanda-logstash][streamly-kafka-cassanda-logstash]

### 7. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-cassandra-logstash-spark-ui][streamly-kafka-cassandra-logstash-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 8. Check application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-cassandra-logstash-kibana-ui][streamly-kafka-cassandra-logstash-kibana-ui]

### 9. Visualize your data
#### a. Query Cassandra
  - Go to Notebook tab
  - Create a new note
  - Query your table and see the result
![streamly-kafka-cassandra-logstash-zeppelin-cassandra][streamly-kafka-cassandra-logstash-zeppelin-cassandra]


## Copyright
Copyright © 2017 Streamly, Inc.

[streamly-dashboard]: https://board.streamly.io:20080
[kafka]: https://kafka.apache.org/
[cassandra]: http://cassandra.apache.org/
[logstash]: https://www.elastic.co/guide/en/logstash/5.2/introduction.html/
[logstash plugins]: https://www.elastic.co/guide/en/logstash/current/output-plugins.html 
[open-streams]: http://streamly.io/streamly-new/streams.html
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-kafka-cassanda-logstash]: https://cloud.githubusercontent.com/assets/25694018/23123253/ed978d0a-f767-11e6-9535-8ef1da0b2781.png
[streamly-kafka-cassandra-logstash-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23123079/361e72e2-f767-11e6-929c-676e7a903538.png
[streamly-kafka-cassandra-logstash-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23123511/f141e080-f768-11e6-9943-4f9ed30b8b80.png
[streamly-kafka-cassandra-logstash-zeppelin-cassandra]: https://cloud.githubusercontent.com/assets/25694018/23123951/d71c47de-f76a-11e6-89be-d791d66bd9b4.png
[streamly-kafka-cassandra-logstash-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23125897/5cd45b1a-f774-11e6-9f75-016f7377c339.png
[streamly-kafka-cassandra-logstash-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23125896/5cd41e8e-f774-11e6-9b86-65cbb2c3779d.png