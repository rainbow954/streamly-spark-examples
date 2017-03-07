package io.streamly.examples;

import static java.lang.Math.toIntExact;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraJavaUtil;

import io.streamly.examples.domain.Transactions;

/**
 * This is a streaming application that consumes data from Mqtt and send it to
 * Cassandra.
 **/
public class StreamlyMqttCassandra implements Serializable {

	static Logger log = LoggerFactory.getLogger(StreamlyMqttCassandra.class);

	static String keyspace;
	static String table;
	private static int seconds = 0;
	static JavaStreamingContext jssc;

	public static void main(String[] args) throws Exception {
		tieSystemOutAndErrToLog();
		if (args.length != 7) {
			System.err.println(
					"Usage: StreamlyMqttCassandra <brokerUrl> <topic> <clientId> <username> <password> <keyspace> <table>");
			System.exit(1);
		}

		String brokerUrl = args[0];
		String topic = args[1];
		String clientID = args[2];
		String username = args[3];
		String password = args[4];
		keyspace = args[5];
		table = args[6];

		SparkConf sparkConf = new SparkConf().setAppName("StreamlyMqttCassandra");

		jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));

		JavaReceiverInputDStream<String> lines = MQTTUtils.createStream(jssc, brokerUrl, topic, clientID, username,
				password, false);

		// Prepare the schema
		log.info("Create and populate table");
		CassandraConnector connector = CassandraConnector.apply(jssc.sparkContext().getConf());
		try (Session session = connector.openSession()) {
			session.execute("CREATE TABLE IF NOT EXISTS " + keyspace + "." + table
					+ " (seconds int PRIMARY KEY, transactions int)");
		}

		log.info("Table : {} created successfully", table);

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
