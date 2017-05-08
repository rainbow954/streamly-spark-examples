package com.spark.examples;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class SparkWordCount {
	private static final Pattern SPACE = Pattern.compile(" ");

	public static void main(String[] args) {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf()
				.setMaster("local[2]")
				.setAppName("Simple Application");	

		JavaSparkContext sc = new JavaSparkContext(conf);

		// Load your own data
		JavaRDD<String> input = sc.textFile("README.md");

		// Split into words
		JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
			public Iterator<String> call(String s) {
				return Arrays.asList(SPACE.split(s)).iterator();
			}
		});

		// Transform into pairs and count.
		JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
			public Tuple2<String, Integer> call(String x) {
				return new Tuple2<>(x, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer x, Integer y) throws Exception {
				return x + y;
			}
		});

		// Print the result
		System.out.println("Output with collect()");
		List<Tuple2<String, Integer>> outputs = counts.collect();
		for (Tuple2<?, ?> tuple : outputs) {
			System.out.println(tuple._1() + ":" + tuple._2());
		}
		// This prints the results of the wordcount ie (tuple._1() + ":" + tuple._2()) 1 
		System.out.println("Output with countByValue()");
		Map<Tuple2<String, Integer>,Long> outputs1 =counts.countByValue();
		for(Map.Entry<Tuple2<String, Integer>,Long> output : outputs1.entrySet()){
			System.out.println(output.getKey()+" "+output.getValue());
		}
		sc.stop();
	}

}
