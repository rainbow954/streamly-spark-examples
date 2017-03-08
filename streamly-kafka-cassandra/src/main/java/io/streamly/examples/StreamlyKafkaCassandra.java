package io.streamly.examples;

import static java.lang.Math.toIntExact;

import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraJavaUtil;

import io.streamly.examples.domain.Transactions;
import scala.Tuple2;

/**
 * This is a spark streaming application that consumes event from a kafka topic
 * and saves the result into a cassandra table.
 **/

public class StreamlyKafkaCassandra {
	static Logger log = LoggerFactory.getLogger(StreamlyKafkaCassandra.class);
	private static final Pattern SPACE = Pattern.compile(" ");
	private static int seconds = 0;

	public static void main(String[] args) throws Exception {
		tieSystemOutAndErrToLog();
		if (args.length != 4) {
			System.err.println("Usage: StreamlyKafkaCassandra <brokerUrl> <topic> <keyspace> <table>");
			System.exit(1);
		}
		String brokers = args[0];
		String topics = args[1];
		String keyspace = args[2];
		String table = args[3];
		SparkConf sparkConf = new SparkConf().setAppName("StreamlyKafkaCassandra");

		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

		Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));

		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("group.id", "kafka-cassandra" + new SecureRandom().nextInt(100));
		kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

		// Prepare the schema
		log.info("Create and populate table");
		CassandraConnector connector = CassandraConnector.apply(jssc.sparkContext().getConf());
		try (Session session = connector.openSession()) {
			session.execute("CREATE TABLE IF NOT EXISTS " + keyspace + "." + table
					+ " (seconds int PRIMARY KEY, transactions int)");
		}

		log.info("Table : {} created successfully", table);

		final JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(topicsSet, kafkaParams));

		log.info("messages :{} ", messages);
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
				List<Transactions> transactions = new ArrayList<>();
				Transactions t = new Transactions();
				t.setTransactions(toIntExact(t0.count()));
				t.setSeconds(seconds);
				seconds = seconds + 2;
				transactions.add(t);
				JavaRDD<Transactions> transactionsRdd = jssc.sparkContext().parallelize(transactions);
				CassandraJavaUtil.javaFunctions(transactionsRdd)
						.writerBuilder(keyspace, table, CassandraJavaUtil.mapToRow(Transactions.class))
						.saveToCassandra();
				log.info("Number of Transactions :{} successfully added after {} seconds, keyspace {}, table {}",
						t.getTransactions(), t.getSeconds(), keyspace, table);
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
