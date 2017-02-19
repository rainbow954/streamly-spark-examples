package examples;
import java.io.PrintStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
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
import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import com.wouri.streamly.spark.logstash.Logstash;

import examples.domain.KafkaTopic;
import scala.Tuple2;

/** 
 * This is a spark streaming application that consumes event from a kafka topic
 * and saves the result into a cassandra table.	
 **/

public class StreamlyKafkaCassandra {
	private static Logstash logstash;
	static Logger log = LoggerFactory.getLogger(StreamlyKafkaCassandra.class);
	private static final Pattern SPACE = Pattern.compile(" ");
	
	public static void main(String[] args) throws Exception {
		tieSystemOutAndErrToLog();
		if (args.length != 6) {
			System.err.println("Usage: StreamlyKafkaCassandra <brokerUrl> <topic> <keyspace> <table> <parameter> <file>");
			System.exit(1);
		}
		String brokers = args[0];		
		String topics =  args[1];
		String keyspace =  args[2];
		String table = args[3];
		String parameter = args[4];
		String file = args[5];
		SparkConf sparkConf = new SparkConf()
				.setAppName("StreamlyKafkaCassandraLogstash");
		
		
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));

		Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));

		
		Map<String, Object> kafkaParams = new HashMap<>();
		kafkaParams.put("bootstrap.servers", brokers);
		kafkaParams.put("group.id", "kafka-cassandra"+ new SecureRandom().nextInt(100));
		kafkaParams.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		kafkaParams.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
		// Prepare the schema
		log.info("Create and populate table");
		CassandraConnector connector = CassandraConnector.apply(jssc.sparkContext().getConf());
		try (Session session = connector.openSession()) {
			session.execute("CREATE TABLE IF NOT EXISTS " +keyspace+"."+table+ " (word TEXT PRIMARY KEY, count int)");
		}

		log.info("Table : {} created successfully", table);
		

		final JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(jssc,
				LocationStrategies.PreferConsistent(),
				ConsumerStrategies.<String, String>Subscribe(topicsSet, kafkaParams));
		
		log.info("messages :{} " ,messages);
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

		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterator<String> call(String x) {
				log.info("Line retrieved {}",x);
				return Arrays.asList(SPACE.split(x)).iterator();
			}
		});

		String[] argumentFile = {parameter,file}; 
		logstash = Logstash.start(argumentFile);
		
		JavaPairDStream<String, Integer> wordCounts = words.mapToPair(
				new PairFunction<String, String, Integer>() {
					@Override
					public Tuple2<String, Integer> call(String s) {
						log.info("Word to count {}", s);
						return new Tuple2<>(s, 1);
					}
				}).reduceByKey(
						new Function2<Integer, Integer, Integer>() {
							@Override
							public Integer call(Integer i1, Integer i2) {
								log.info("Count with reduceByKey {}", i1+i2);
								return i1 + i2;
							}
						});
		
		
		log.info("words retrieved {}"+ words);
		
		wordCounts.print();
		
		wordCounts.foreachRDD(new VoidFunction<JavaPairRDD<String,Integer>>() {
			
			@Override
			public void call(JavaPairRDD<String, Integer> arg0) throws Exception {
				Map<String, Integer> wordCountMap = arg0.collectAsMap();
				logstash.addToQueue(wordCountMap.toString());
				List<KafkaTopic> topicList  = new ArrayList<>();
				for(String key : wordCountMap.keySet()){
					KafkaTopic t = new KafkaTopic();
					t.setWord(key);
					t.setCount(wordCountMap.get(key));
					log.info("New topic = {}", t);
					if(!t.getWord().isEmpty())
						topicList.add(t);
				}
				JavaRDD<KafkaTopic> kafkaTopicRDD = jssc.sparkContext().parallelize(topicList);
				CassandraJavaUtil.javaFunctions(kafkaTopicRDD).writerBuilder(keyspace, table, CassandraJavaUtil.mapToRow(KafkaTopic.class)).saveToCassandra();
				log.info("Words successfully added : {}, keyspace {}, table {}", words,keyspace,table);
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
