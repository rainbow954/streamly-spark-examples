package examples.domain;

import java.io.Serializable;

public class KafkaTopic implements Serializable  {

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
		return "KafkaTopic [word=" + word + ", count=" + count + "]";
	}
	public KafkaTopic(String word, int count) {
		this.word = word;
		this.count = count;
	}
	public KafkaTopic() {
	}
	
	
	
	
	
	
}
