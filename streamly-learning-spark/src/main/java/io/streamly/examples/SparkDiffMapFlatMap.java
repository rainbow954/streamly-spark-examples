package io.streamly.examples;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;

public class SparkDiffMapFlatMap {
	private static final Pattern SPACE = Pattern.compile(" ");

	public static void main(String[] args) {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Simple Application");

		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<String> rdd = sc.parallelize(Arrays.asList("coffee panda","happy panda","happiest party","happy day"));
		JavaRDD<String> mappedRDD = rdd.map(new Function<String, String>() {
			@Override
			public String call(String v1) throws Exception {
				return v1;
			}
		});
		
		JavaRDD<String> flatMappedRDD = rdd.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterator<String> call(String t) throws Exception {
				return Arrays.asList(SPACE.split(t)).iterator();
			}
		});
		
		// Print both results
		// Output with map
		SparkDiffMapFlatMap.print(mappedRDD);
		// Output with flatmap
		SparkDiffMapFlatMap.print(flatMappedRDD);
	}
	
	private static void print(JavaRDD<String> rdd){
		List<String> outputs = rdd.collect();
		for (String output : outputs){
			System.out.println(output);
		}
	}

}
