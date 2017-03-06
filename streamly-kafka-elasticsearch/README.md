# Streamly Kafka Elasticsearch Example Project

## Introduction

This is a simple stream processing application that you can deploy in [Streamly].
It is written in Java and consumes events from [Kafka] then writes aggregates to [Elasticsearch].


## Quickstart


### 1. Build the project

Assuming git, java and maven installed. In your local terminal :

```bash
 host$ git clone https://github.com/streamlyio/streamly-spark-examples.git
 host$ cd streamly-spark-examples/streamly-kafka-elasticsearch
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
main.class=io.streamly.examples.StreamlyKafkaElasticsearch
app.args=apps.streamly.io:29093,system-bitcoin-transactions,greenspace-myindex/logs
app.resource=file://streamly-kafka-elasticsearch-0.0.1.jar
spark.es.resource=greenspace-myindex/logs
spark.es.nodes=london201.streamly.io,london202.streamly.io,london205.streamly.io
spark.es.port=9200
spark.es.net.http.auth.user=ci00jji37jfhq8q
spark.es.net.http.auth.pass=r30qwridiw8qkxj
```

### 7. Submit your application 
 - Go to Processing tab
 - Click on Add Application. A new application is created with name : `No Name`.
 - Provide a valid name for your application and click on Save icon. It should start with your namespace. In this example the name is `greenspace-kafka-es`.
 - Upload `spark.properties` and `streamly-kafka-elasticsearch-0.0.1.jar` files
 - Click on the Start icon

![streamly-kafka-elasticsearch][streamly-kafka-elasticsearch]

### 8. Monitor your application
Wait until your application is running. Then click on Show UI icon. You should see something like this :
![streamly-kafka-elasticsearch-spark-ui][streamly-kafka-elasticsearch-spark-ui]
You can see how our Spark Streaming job _processes_ the Kafka events stream.

### 9. Check your application logs
You may have some errors and can't find why this happening. Application logs are populated in Elasticsearch and can be visualized through Kibana.
![streamly-kafka-elasticsearch-kibana-ui][streamly-kafka-elasticsearch-kibana-ui]

### 10. Visualize your data
  - Go to Kibana tab
  - Create a new index pattern with your index name and created-at as time-field name

![streamly-kafka-elasticsearch-kibana-index-pattern][streamly-kafka-elasticsearch-kibana-index-pattern]

  - Since our streaming application does not generate time field data in Elasticsearch, Kibana is not able to display documents of this index on the discover tab. Nevertheless looking at the screenshot below, we can see the data from the bitcoins topic are actually populated to elasticsearch.

![streamly-kafka-elasticsearch-kibana-discover][streamly-kafka-elasticsearch-kibana-discover]

## Copyright
Copyright © 2017 Streamly, Inc.

[streamly]: https://board.streamly.io:20080
[streamly-signup]: https://board.streamly.io:20080/#/signup
[streamly-signup-step1]: https://cloud.githubusercontent.com/assets/25694018/23342086/2d3072e2-fc54-11e6-93b3-30223946e8d8.png
[streamly-signup-step2]: https://cloud.githubusercontent.com/assets/25694018/23342085/2d303ce6-fc54-11e6-8839-b9b6c00d2efd.png
[kafka]: https://kafka.apache.org/
[elasticsearch]: https://www.elastic.co/products/elasticsearch
[streamly-list-apikeys]: https://cloud.githubusercontent.com/assets/25694018/23608419/8909b464-0269-11e7-99fd-c28fef9f0d87.png
[streamly-kafka-elasticsearch-spark-ui]: https://cloud.githubusercontent.com/assets/25694018/23468983/99758d3e-fea2-11e6-82df-080d6de5f2bf.png
[streamly-kafka-elasticsearch]: https://cloud.githubusercontent.com/assets/25694018/23468574/6705b884-fea1-11e6-9e21-dc9eb5b84cfd.png
[streamly-kafka-elasticsearch-kibana-discover]: https://cloud.githubusercontent.com/assets/25694018/23481441/9e1dff00-fecb-11e6-9e38-9de49622c56b.png
[streamly-kafka-elasticsearch-kibana-index-pattern]: https://cloud.githubusercontent.com/assets/25694018/23481368/6ab3e378-fecb-11e6-8bbe-eb585d185015.png
[streamly-create-topic]: https://cloud.githubusercontent.com/assets/25694018/23468239/9450193e-fea0-11e6-8cb1-1d7ee64d464e.png
[streamly-create-index]: https://cloud.githubusercontent.com/assets/25694018/23468239/9450193e-fea0-11e6-8cb1-1d7ee64d464e.png
[streamly-list-indexes]: https://cloud.githubusercontent.com/assets/25694018/23468146/4b761a60-fea0-11e6-9db5-dd5fcd20edcd.png
[streamly-kafka-elasticsearch-kibana-ui]: https://cloud.githubusercontent.com/assets/25694018/23469043/cfb53084-fea2-11e6-94fa-080cb005b2fb.png
[open-streams]: http://streamly.io/streamly-new/streams.html