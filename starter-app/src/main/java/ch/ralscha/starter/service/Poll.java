package ch.ralscha.starter.service;

public class Poll {
	private String date;
	private int value;

	public Poll(String date, int value) {
		this.date = date;
		this.value = value;
	}

	public String getDate() {
		return date;
	}

	public int getValue() {
		return value;
	}

}
