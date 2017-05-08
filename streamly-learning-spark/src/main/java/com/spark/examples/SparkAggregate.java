package com.spark.examples;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

public class SparkAggregate {

	public static void main(String[] args) {
		// Create a Java Spark Context
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("Simple Application");

		JavaSparkContext sc = new JavaSparkContext(conf);
		
		JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1,2,3,4,5));
		
		Function2<AvgCount, Integer, AvgCount> addAndCount = new Function2<AvgCount, Integer, AvgCount>() {
			@Override
			public AvgCount call(AvgCount a, Integer x) throws Exception {
				a.total = a.total+x;
				System.out.println("a "+a.total+" x "+x);
				a.num += 1;
				return a;
			}
		};
		
		Function2<AvgCount, AvgCount, AvgCount> combine = new Function2<AvgCount, AvgCount, AvgCount>() {
			@Override
			public AvgCount call(AvgCount a, AvgCount b) throws Exception {
				a.total = a.total+b.total;
				System.out.println("a "+a.total+" b "+b.total);
				a.num = a.num+b.num;
				return a;
			}
		};
		
		AvgCount initial = new AvgCount(0, 0);
		AvgCount result = rdd.aggregate(initial, addAndCount, combine);
		System.out.println("Average "+result.avg());
		sc.close();

	}

	static class AvgCount implements Serializable {
		public AvgCount(int total, int num) {
			this.total = total;
			this.num = num;
		}

		public int total;
		public int num;

		public double avg() {
			return total / (double) num;
		}
	}

}
