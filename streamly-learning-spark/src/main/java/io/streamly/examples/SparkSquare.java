package io.streamly.examples;

import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.DoubleFunction;

public class SparkSquare {
	public static void main(String... args) {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Simple Application");

		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1,2,3,4));
		// With lambda expressions
		JavaRDD<Integer> sqRDD = rdd.map(x -> x*x);
		
		// With Function
//		JavaRDD<Integer> sqRDD = rdd.map(new Function<Integer, Integer>() {
//
//			@Override
//			public Integer call(Integer v1) throws Exception {
//				return v1*v1;
//			}
//		});
		
		// To get access to additional functions like mean, variance etc
		JavaDoubleRDD sqRDDFxn = rdd.mapToDouble(new DoubleFunction<Integer>() {
			@Override
			public double call(Integer t) throws Exception {
				return t*t;
			}
		});
		 
		// Print the result
		List<Integer> outputs = sqRDD.collect();
		for (Integer output : outputs){
			System.out.println(output);
		}
		System.out.println("The mean is "+sqRDDFxn.mean());
		System.out.println("The variance is "+sqRDDFxn.variance());
		System.out.println("The standard deviation is "+sqRDDFxn.stdev());
		// Close the stream
		sc.close();
	}
}
