package io.streamly.examples;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * This class process "json" events coming from a secured mqtt topic
 * and persist them in a secured Elasticsearc Index.
 * 	
 **/
public class StreamlyMqttElasticsearch {
	
	static Logger log = LoggerFactory.getLogger(StreamlyMqttElasticsearch.class);
	
	public static void main(String[] args) throws InterruptedException {
		tieSystemOutAndErrToLog();
		if (args.length != 7) {
			System.err.println("Usage: StreamlyMqttElasticsearch <mqttBrokerUrl> <mqttTopic> <mqttClientID> <mqttUsername> <mqttPassword> <esIndexName> <esIndexType>");
			System.exit(1);
		}
		
		String mqttBrokerUrl = args[0];
		String mqttTopic = args[1];
		String mqttClientID = args[2];
		String mqttUsername = args[3];
		String mqttPassword = args[4];
		final String esIndexName = args[5];
		final String esIndexType = args[6];
		
		Duration batchInterval = new Duration(2000);

		SparkConf sparkConf = new SparkConf().setAppName("StreamlyMqttElasticsearch");
		
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, batchInterval);

		JavaReceiverInputDStream<String> inputDStream = MQTTUtils.createStream(jssc, mqttBrokerUrl, mqttTopic, mqttClientID, mqttUsername,
				mqttPassword, false);
		
		JavaDStream<String> jStream = inputDStream.map(new Function<String, String>() {
			public String call(String arg0) throws Exception {
				String str = new String(arg0);
				JSONObject json = new JSONObject(str);
				json.put("@timestamp", nowDate());
				return json + "";
			}
		});

		jStream.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			public void call(JavaRDD<String> arg0) throws Exception {
				JavaEsSpark.saveJsonToEs(arg0, esIndexName + "/" + esIndexType);
			}
		});

		jStream.print();
		jssc.start();
		jssc.awaitTermination();

	}

	public static String nowDate() {
		return new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()).replace(" ", "T") + "Z";
	}
	
	public static void tieSystemOutAndErrToLog() {
        System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }
    public static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
           
            public void print(final String string) {    
                log.error(string);
            }
            public void println(final String string) {
                log.error(string);
            }
        };
    }
}
