package com.spark.examples;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

public class SparkFilterOutput {

	public static void main(String[] args) {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Simple Application");

		JavaSparkContext sc = new JavaSparkContext(conf);

		// Load your own data
		JavaRDD<String> inputRDD = sc.textFile("README.md");

		// Passing functions directly
		JavaRDD<String> sparkRDD = inputRDD.filter(new Function<String, Boolean>() {
			@Override
			public Boolean call(String arg0) throws Exception {
				return arg0.contains("Spark");
			}
		});

		// Using the top-level functions
		JavaRDD<String> hashRDD = inputRDD.filter(new ContainsHash());

		// With the java 8 lambda expression
		JavaRDD<String> mvnRDD = inputRDD.filter(s -> s.contains("mvn"));

		// Unit all RDDs
		JavaRDD<String> sparkHashMvnRDD = sparkRDD.union(hashRDD).union(mvnRDD);

		// Print the result
		List<String> outputs = sparkHashMvnRDD.collect();
		for (String output : outputs) {
			System.out.println(output);
		}
		sc.stop();
	}

	static class ContainsHash implements Function<String, Boolean> {

		// One thing good with top-level functions is that you can pass some
		// arguments to the constructors
		private String query;

		public ContainsHash() {
		}

		public ContainsHash(String query) {
			this.query = query;
		}

		@Override
		public Boolean call(String v1) throws Exception {
			return v1.contains("##");
		}
	}

}
