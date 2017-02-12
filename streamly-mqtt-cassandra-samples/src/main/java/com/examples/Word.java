package com.examples;

import java.io.Serializable;
import java.text.MessageFormat;

public class Word implements Serializable{
	private Integer id;
    private String word;
    private Integer counts;

    public Word() { }

    public Word(Integer id, String word, Integer counts) {
        this.id = id;
        this.word = word;
        this.counts = counts;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String name) { this.word = word; }

    public Integer getCounts() { return counts; }
    public void setCounts(Integer counts) { this.counts = counts; }

    @Override
    public String toString() {
        return MessageFormat.format("Word'{'id={0}, word=''{1}'', counts={2}'}'", id, word, counts);
    }
}
