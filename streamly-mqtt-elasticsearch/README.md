# Streamly Mqtt Elasticsearch Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Mqtt] then writes aggregates to [Elasticsearch].


## Quickstart

### 1. Build the project

Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-mqtt-elasticsearch
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
We assume that you have followed up the [streamly-kafka-mqtt] project, because this project creates a MQTT topic and sends some data inside. This topic is called  `greenspace/mqtt/topic`. In the next steps, we consume events from  `greenspace/mqtt/topic`.

### 4. Create your index 
To create a new index :
  
  - Go to Elasticsearch tab
  - Write the name of the index in Index name box. We assume that the name is `greenspace-myindex`.
  - Define the number of replicas

![streamly-create-index][streamly-create-index]

  - Click on Add New Index button

The index appears in the list of existing indexes:

![streamly-list-indexes][streamly-list-indexes]

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
| app.resource                          | Elasticsearch http client port                      |
| spark.es.port                         | Cassandra native connection port                    |
| spark.es.nodes                        | Comma separated Elasticsearch hosts                 |
| spark.es.net.http.auth.user           | Access key          			                      |
| spark.es.net.http.auth.pass           | Secret key                                          |
| spark.es.resource                     | Elasticsearch resources, here we used the index/type|
The resulting file looks like :

```properties
main.class=io.streamly.examples.StreamlyMqttElasticsearch
app.args=tcp://apps.streamly.io:21883,greenspace/mqtt/topic,greenspace,ci00jji37jfhq8q,r30qwridiw8qkxj,greenspace-myindex,logs
app.resource=file://streamly-mqtt-elasticsearch-0.0.1.jar
spark.es.resource=greenspace-myindex/logs
spark.es.nodes=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.es.port=9200
spark.es.net.http.auth.user=ci00jji37jfhq8q
spark.es.net.http.auth.pass=r30qwridiw8qkxj
```

### 7. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-mqtt-es`.
 - Upload `spark.properties` and `streamly-mqtt-elasticsearch-0.0.1.jar` files
 - Click on the Start icon

![streamly-mqtt-elasticsearch][streamly-mqtt-elasticsearch]

### 8. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-mqtt-elasticsearch-spark-ui][streamly-mqtt-elasticsearch-spark-ui]
You can see how our Spark Streaming job _processes_ the Mqtt events stream.

### 9. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-mqtt-elasticsearch-kibana-ui][streamly-mqtt-elasticsearch-kibana-ui]

### 10. Visualize your data
  - Go to Kibana tab
  - Create a new index pattern with your index name and created-at as time-field name

![streamly-mqtt-elasticsearch-kibana-index-pattern][streamly-mqtt-elasticsearch-kibana-index-pattern]

  - Since the data sent by the spark job do not generate @timestamp, kibana won't be able to display them on the discover tab. Nevertheless looking at the screenshot below, we can see the data from the bitcoins topic are actually populated to elasticsearch.

![streamly-mqtt-elasticsearch-kibana-discover][streamly-mqtt-elasticsearch-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-kafka-mqtt]: https://github.com/streamlyio/streamly-spark-examples/tree/master/streamly-kafka-mqtt
[mqtt]: http://mqtt.org/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[blog-post]: http://streamly.io/streamly-new/blog.html
[streamly-mqtt-elasticsearch-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23481368/6ab3e378-fecb-11e6-8bbe-eb585d185015.png
[streamly-mqtt-elasticsearch-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23481441/9e1dff00-fecb-11e6-9e38-9de49622c56b.png
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23468239/9450193e-fea0-11e6-8cb1-1d7ee64d464e.png
[streamly-list-indexes]: https://cloud.githubusercontent.com/assets/25694018/23468146/4b761a60-fea0-11e6-9db5-dd5fcd20edcd.png
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23631833/ff32f0f0-02bf-11e7-9bca-8ccf17224620.png
[streamly-mqtt-elasticsearch-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23531279/078726bc-ffa6-11e6-81de-312d92d272d1.png
[streamly-mqtt-elasticsearch]: https://cloud.githubusercontent.com/assets/25694018/23531280/07d2018c-ffa6-11e6-9be4-9ff30e420e7b.png
[streamly-mqtt-elasticsearch-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23531377/6ca17e1c-ffa6-11e6-8d2d-61879ff366a9.png