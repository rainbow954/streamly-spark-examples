# Streamly Cyanite Bridge Mock

This is a mock project that allows you to build a spark application that can invoke Cyanite. Once the spark application calls cyanite, it passes events to this cyanite instance using a queue. The interaction between the Spark Application and Cyanite happens within the same Java Virtual Machine. 

The real implementation of this bridge is already deployed in the Streamly platform and is transparent to developers.  As a result, you should import this project in your maven pom with the "provided" scope. An example is depicted in this project: https://github.com/streamlyio/streamly-spark-examples/tree/master/streamly-kafka-cassandra-cyanite.
