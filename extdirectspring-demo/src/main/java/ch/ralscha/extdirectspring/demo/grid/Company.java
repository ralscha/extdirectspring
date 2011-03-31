package ch.ralscha.extdirectspring.demo.grid;


public class Company {
	private String name;
	private int turnover;

	public Company(String name, int turnover) {
		this.name = name;
		this.turnover = turnover;
	}

	public String getName() {
		return name;
	}

	public int getTurnover() {
		return turnover;
	}

}
