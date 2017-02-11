package com.example;

/** Java Bean class to be used with the example JavaSqlNetworkWordCount. */
public class JavaRecord implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String word;

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}
}
