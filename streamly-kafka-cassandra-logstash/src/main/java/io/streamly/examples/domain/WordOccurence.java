package io.streamly.examples.domain;

import java.io.Serializable;

public class WordOccurence implements Serializable  {

	private String word;
	private int count;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	@Override
	public String toString() {
		return "WordOccurence [word=" + word + ", count=" + count + "]";
	}
	public WordOccurence(String word, int count) {
		this.word = word;
		this.count = count;
	}
	public WordOccurence() {
	}
	
	
	
	
	
	
}
