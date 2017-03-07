package io.streamly.examples;

import static java.lang.Math.toIntExact;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is a streaming class that consumes events from mqtt and send them to an
 * unsecured kafka topic.
 **/
public class StreamlyMqttKafka {
	static Logger log = LoggerFactory.getLogger(StreamlyMqttKafka.class);
	private static int seconds = 0;

	public static void sendToKafka(String brokersUrl, int time, int transactions, String topic) {
		log.debug("About to send data to kafka");
		Properties props = new Properties();
		props.put("bootstrap.servers", brokersUrl);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("acks", "1");
		// how many times to retry when produce request fails?
		props.put("retries", "3");
		props.put("linger.ms", 5);

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

		try {
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(
					transactions + " transactions processed in " + 2 + " secs total time spent " + time+" secs");
			String event = "{\"transactions_stats\":" + jsonInString + "}";
			log.info("Message to send to kafka : {}", event);
			producer.send(new ProducerRecord<String, String>(topic, event));
			log.info("Event : " + event + " published successfully into kafka!!");
		} catch (Exception e) {
			log.error("Problem while publishing the event to kafka : " + e.getMessage());
		}
		producer.close();

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

	public static void main(String[] args) throws InterruptedException {
		tieSystemOutAndErrToLog();
		if (args.length != 7) {
			System.err.println(
					"Usage: StreamlyMqttKafka <MQTTBrokerUrl> <topic> <clientId> <username> <password> <kafkaBrokersUrl> <kafkaTopic>");
			System.exit(1);
		}

		String brokerUrl = args[0];
		String topic = args[1];
		String clientID = args[2];
		String username = args[3];
		String password = args[4];
		String kafkaBrokersUrl = args[5];
		String kafkaTopic = args[6];

		SparkConf sparkConf = new SparkConf().setAppName("StreamlyMqttKafka");

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));

		JavaReceiverInputDStream<String> lines = MQTTUtils.createStream(jssc, brokerUrl, topic, clientID, username,
				password, false);

		JavaDStream<String> transactionCounts = lines.window(Durations.seconds(60));
		transactionCounts.foreachRDD(new VoidFunction<JavaRDD<String>>() {

			@Override
			public void call(JavaRDD<String> t0) throws Exception {
				int transactions = toIntExact(t0.count());
				seconds = seconds + 2;
				sendToKafka(kafkaBrokersUrl, seconds, transactions, kafkaTopic);
			}

		});

		// JavaDStream<String> transactionCounts =
		// lines.window(Durations.seconds(60));
		// transactionCounts.foreachRDD(new VoidFunction2<JavaRDD<String>,
		// Time>() {
		// @Override
		// public void call(JavaRDD<String> rdd, Time time) {
		// SparkSession spark =
		// JavaSparkSessionSingleton.getInstance(rdd.context().getConf());
		// // Convert JavaRDD[String] to JavaRDD[bean class] to DataFrame
		// JavaRDD<JavaRecord> rowRDD = rdd.map(new Function<String,
		// JavaRecord>() {
		// // @Override
		// public JavaRecord call(String transaction) {
		// JavaRecord record = new JavaRecord();
		// record.setTransaction(transaction);
		// return record;
		// }
		// });
		// Dataset<Row> transactionsDataFrame = spark.createDataFrame(rowRDD,
		// JavaRecord.class);
		//
		// // Creates a temporary view using the DataFrame
		// transactionsDataFrame.createOrReplaceTempView("transactions");
		// // Do word count on table using SQL and print it
		// Dataset<Row> transactionCountsDataFrame = spark
		// .sql("select transaction, count(*) as total from transactions group
		// by transaction");
		// List<Row> listRows = transactionCountsDataFrame.collectAsList();
		// for (Row row : listRows) {
		// log.debug("Data to send: {}", row);
		// sendToKafka(kafkaBrokersUrl, (String) row.get(0), (Long) row.get(1),
		// kafkaTopic);
		// }
		//
		// }
		// });

		jssc.start();
		jssc.awaitTermination();

	}

}

/** Lazily instantiated singleton instance of SparkSession */
class JavaSparkSessionSingleton {
	private static transient SparkSession instance = null;

	public static SparkSession getInstance(SparkConf sparkConf) {
		if (instance == null) {
			instance = SparkSession.builder().config(sparkConf).getOrCreate();
		}
		return instance;
	}
}
