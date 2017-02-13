package com.examples;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
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

import com.datastax.driver.core.Session;
import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraJavaUtil;

/** 
 * This is a streaming class that consumes data with mqtt and send those
 * data to Cassandra. Each word from each line collected
 * by Mqtt is counted and the result is send to Cassandra.
 * 
 * 	
 * @author Pascal Fenkam
 **/
public class MQTTCassandraStreaming extends TimerTask implements Serializable{
	
	static Logger log = LoggerFactory.getLogger(MQTTCassandraStreaming.class);
	
	static String keyspace;
	static String wordTable;
	
	static private Map<String,Long> wordCounts = new HashMap<>();
	static JavaStreamingContext jssc;
	private static void sendDataToCassandra(JavaSparkContext sc,String keyspace,Map<String, Long> wordCounts) {
		log.debug("In sendDataToCassandra");
		CassandraConnector connector = CassandraConnector.apply(sc.getConf());
		
		// Prepare the schema
		try (Session session = connector.openSession()){
			session.execute("CREATE TABLE IF NOT EXISTS " + keyspace +"." +wordTable + " (id INT PRIMARY KEY, word TEXT, counts INT)");
		}
		
		log.info("keyspace {} and tables : {} created successfully", keyspace, wordTable);
		// Prepare the products hierarchy
		List<Word> words = new ArrayList<>();
		int i = 0;
		for (Map.Entry<String, Long> word : wordCounts.entrySet()){
			Word eWord = new Word(i, word.getKey(), (int) (long)word.getValue());
			words.add(i, eWord);
			i++;
		}
		
		log.info("Words to add : {}", words);
		JavaRDD<Word> productsRDD = sc.parallelize(words);
		
		CassandraJavaUtil.javaFunctions(productsRDD).writerBuilder(keyspace, wordTable, CassandraJavaUtil.mapToRow(Word.class)).saveToCassandra();

		log.info("Words successfully added : {}, keyspace {}, table {}", words,keyspace,wordTable);
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

	public static void main(String[] args) throws Exception {
        tieSystemOutAndErrToLog();
		if (args.length < 2) {
			System.err.println("Usage: MQTTCassandraStreaming <brokerUrl> <topic> <clientId> <username> <password> <keyspace> <tablename> <cassandraContactPoints>");
			System.exit(1);
		}

		String brokerUrl = args[0];
		String topic = args[1];
		String clientID = args[2];
		String username = args[3];
		String password = args[4];
		keyspace = args[5];
		wordTable = args[6];
		String cassandraContactPoints = args[7];
		
		SparkConf sparkConf = new SparkConf().setAppName("MQTTToCassandraStreaming");
		sparkConf.set("spark.driver.allowMultipleContexts", "true");
		sparkConf.set("spark.cores.max", "1");
		sparkConf.set("spark.cassandra.connection.host", cassandraContactPoints );
		sparkConf.set("spark.cassandra.connection.port", "9042" );
		sparkConf.set("spark.cassandra.auth.username", username);
		sparkConf.set("spark.cassandra.auth.password", password);
		sparkConf.set("spark.streaming.stopGracefullyOnShutdown", "true");

		// check Spark configuration for master URL, set it to local if not
		// configured
		if (!sparkConf.contains("spark.master")) {
			sparkConf.setMaster("local[4]");
		}

		jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));

		JavaReceiverInputDStream<String> lines = MQTTUtils.createStream(jssc, brokerUrl, topic, clientID, username,
				password, false);

		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			public Iterator<String> call(String x) {
				return Arrays.asList(x.split(" ")).iterator();
			}
		});
		
		// Convert RDDs of the words DStream to DataFrame and run SQL query
		words.foreachRDD(
				new VoidFunction2<JavaRDD<String>, Time>() {
			
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
				for (Row row : listRows){
					wordCounts.put((String)row.get(0), (Long)row.get(1));
				}
			}
		});
		// And From your main() method or any other method
		Timer timer = new Timer();
		// Insert into cassandra after every 5seconds because processing takes 5seconds
		timer.schedule(new MQTTCassandraStreaming(), 0, 5000);
		jssc.start();
		jssc.awaitTermination();
	}
	public MQTTCassandraStreaming(){
		
	}
	@Override
	public void run() {
		sendDataToCassandra(jssc.sparkContext(), keyspace, wordCounts);
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


