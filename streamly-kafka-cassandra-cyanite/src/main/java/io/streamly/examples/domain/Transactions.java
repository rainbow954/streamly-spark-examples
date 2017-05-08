package io.streamly.examples.domain;

import java.io.Serializable;

public class Transactions implements Serializable  {

	private int transactions;
	private int seconds;
	
	
	public int getTransactions() {
		return transactions;
	}
	public void setTransactions(int transactions) {
		this.transactions = transactions;
	}
	public int getSeconds() {
		return seconds;
	}
	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	public Transactions(int transactions, int seconds) {
		this.transactions = transactions;
		this.seconds = seconds;
	}
	public Transactions() {
	}
	@Override
	public String toString() {
		return "Transactions [transactions=" + transactions + ", seconds=" + seconds + "]";
	}
	
	
}
