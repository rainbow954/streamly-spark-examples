package io.streamly.cyanite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cyanite {
	
	private static Logger logger = LoggerFactory.getLogger(Cyanite.class);

	public static void start(String[] args){
		logger.info("Cyanite started");
	}
	
	public static void putToQueue(String s){
		logger.info("Data added to cyanite queue :{}", s);
	}
}
