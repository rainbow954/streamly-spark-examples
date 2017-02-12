package com.example;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction2;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.Time;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/** 
 * This is a streaming class that consumes data with mqtt and send those
 * data to an unsecure kafka topic. Each word from each line collected
 * by Mqtt is counted and the result is send to kafka.
 * 
 * 	
 * @author Pascal Fenkam
 **/
public class MQTTUnsecurekafkaStreaming {
	static Logger log = LoggerFactory.getLogger(MQTTUnsecurekafkaStreaming.class);

	public static void sendToKafka(String brokersUrl,String word, Long count, String topic) {
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
			String jsonInString = mapper.writeValueAsString(word + " " + count);
			String event = "{\"word_stats\":" + jsonInString + "}";
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

	public static void main(String[] args) {
		tieSystemOutAndErrToLog();
		if (args.length < 2) {
			System.err.println("Usage: MQTTUnsecurekafkaStreaming <MQTTBrokerUrl> <topic> <clientId> <username> <password> <kafkaBrokersUrl> <kafkaTopic> <sparkJobName>");
			System.exit(1);
		}

		String brokerUrl = args[0];
		String topic = args[1];
		String clientID = args[2];
		String username = args[3];
		String password = args[4];
		String kafkaBrokersUrl = args[5];
		String kafkaTopic = args[6];
		String sparkJobName = args[7];
		log.debug("All needed data initialized");

		SparkConf sparkConf = new SparkConf().setAppName(sparkJobName);
		sparkConf.set("spark.driver.allowMultipleContexts", "true");

		// check Spark configuration for master URL, set it to local if not
		// configured
		if (!sparkConf.contains("spark.master")) {
			sparkConf.setMaster("local[4]");
		}

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));

		JavaReceiverInputDStream<String> lines = MQTTUtils.createStream(jssc, brokerUrl, topic, clientID, username,
				password, false);

		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			public Iterator<String> call(String x) {
				return Arrays.asList(x.split(" ")).iterator();
			}
		});

		// Convert RDDs of the words DStream to DataFrame and run SQL query
		words.foreachRDD(new VoidFunction2<JavaRDD<String>, Time>() {
			// @Override
			public void call(JavaRDD<String> rdd, Time time) {
				SparkSession spark = JavaSparkSessionSingleton.getInstance(rdd.context().getConf());
				// Convert JavaRDD[String] to JavaRDD[bean class] to DataFrame
				JavaRDD<JavaRecord> rowRDD = rdd.map(new Function<String, JavaRecord>() {
					// @Override
					public JavaRecord call(String word) {
						JavaRecord record = new JavaRecord();
						record.setWord(word);
						return record;
					}
				});
				Dataset<Row> wordsDataFrame = spark.createDataFrame(rowRDD, JavaRecord.class);

				// Creates a temporary view using the DataFrame
				wordsDataFrame.createOrReplaceTempView("words");
				// Do word count on table using SQL and print it
				Dataset<Row> wordCountsDataFrame = spark.sql("select word, count(*) as total from words group by word");
				List<Row> listRows = wordCountsDataFrame.collectAsList();
				for (Row row : listRows) {
					log.debug("Data to send: {}",row);
					sendToKafka(kafkaBrokersUrl,(String) row.get(0), (Long) row.get(1), kafkaTopic);
				}

			}
		});

		jssc.start();
		try {
			jssc.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
