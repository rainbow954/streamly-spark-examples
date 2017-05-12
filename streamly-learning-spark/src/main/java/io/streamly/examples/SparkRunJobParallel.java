package io.streamly.examples;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkRunJobParallel {
	public static void main(String... args) throws InterruptedException, ExecutionException {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Simple Application");

		final JavaSparkContext sc = new JavaSparkContext(conf);
		ExecutorService executorService = Executors.newFixedThreadPool(2);

		// Start thread 1
		Future<Long> future1 = executorService.submit(new Callable<Long>() {

			@Override
			public Long call() throws Exception {
				JavaRDD<String> file1 = sc.textFile("README.md");
				return file1.count();
			}
		});
		// Start thread 2
		Future<Long> future2 = executorService.submit(new Callable<Long>() {
			@Override
			public Long call() throws Exception {
				JavaRDD<String> file2 = sc.textFile("spark.properties");
				return file2.count();
			}
		});
		// Wait thread 1
		System.out.println("Number of lines in README.md: " + future1.get());
		// Wait thread 2
		System.out.println("Number of lines in spark.properties: " + future2.get());
		// Close spark context
		sc.close();
	}

}
