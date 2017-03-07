package io.streamly.examples.domain;

import java.io.Serializable;

public class ApacheLogs implements Serializable  {

	private int messages;
	private int seconds;
	
	
	public int getMessages() {
		return messages;
	}
	public void setMessages(int messages) {
		this.messages = messages;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	@Override
	public String toString() {
		return "ApacheLogs [messages=" + messages + ", seconds=" + seconds + "]";
	}
	public ApacheLogs(int messages, int seconds) {
		this.messages = messages;
		this.seconds = seconds;
	}
	public ApacheLogs() {
	}
	
}
