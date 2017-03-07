package io.streamly.examples;

import static java.lang.Math.toIntExact;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.elasticsearch.spark.rdd.api.java.JavaEsSpark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import scala.Tuple2;

/**
 * This is a spark streaming application that consumes event from kafka topic
 * and saves the result into an elasticsearch index.
 **/
public class StreamlyKafkaElasticsearch {

	private static final Pattern SPACE = Pattern.compile(" ");
	static Logger log = LoggerFactory.getLogger(StreamlyKafkaElasticsearch.class);
	static Set<String> globalLine = new HashSet<>();
	private static int seconds = 0;

	public static void main(String[] args) throws InterruptedException {
		tieSystemOutAndErrToLog();
		if (args.length != 3) {
			System.err.println("Usage: StreamlyKafkaElasticSearch <brokerUrl> <topic> <resource>");
			System.exit(1);
		}
		String brokers = args[0];
		String topics = args[1];
		String resource = args[2];

		SparkConf sparkConf = new SparkConf().setAppName("StreamlyKafkaElasticSearch");

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

		Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("group.id", "kafka-elasticsearch-consumer");
		kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		final JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(topicsSet, kafkaParams));

		log.info("Consumed messages : {}", messages);
		JavaPairDStream<String, String> results = messages
				.mapToPair(new PairFunction<ConsumerRecord<String, String>, String, String>() {
					@Override
					public Tuple2<String, String> call(ConsumerRecord<String, String> record) {
						return new Tuple2<>(record.key(), record.value());
					}
				});

		JavaDStream<String> lines = results.map(new Function<Tuple2<String, String>, String>() {
			@Override
			public String call(Tuple2<String, String> tuple2) {
				return tuple2._2();
			}
		});

		JavaDStream<String> transactionCounts = lines.window(Durations.seconds(60));
		transactionCounts.foreachRDD(new VoidFunction<JavaRDD<String>>() {

			@Override
			public void call(JavaRDD<String> t0) throws Exception {
				if (t0 != null) {
					String transactions = "transactions " + toIntExact(t0.count()) + " process in " + seconds
							+ " seconds";
					seconds = seconds + 2;
					Map<String, Integer> transactionsCountsMap = new HashMap<>();
					transactionsCountsMap.put(transactions, seconds);
					JavaRDD<Map<String, ?>> javaRDD = jssc.sparkContext()
							.parallelize(ImmutableList.of(transactionsCountsMap));
					JavaEsSpark.saveToEs(javaRDD, resource);
					log.info("Transactions successfully added : {} into {}", transactionsCountsMap, resource);
				}
			}

		});

		jssc.start();
		jssc.awaitTermination();
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
