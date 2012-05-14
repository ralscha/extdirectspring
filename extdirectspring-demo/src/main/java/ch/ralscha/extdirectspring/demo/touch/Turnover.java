package ch.ralscha.extdirectspring.demo.touch;

import java.math.BigDecimal;

public class Turnover {
	private final String name;
	private final BigDecimal turnover;

	public Turnover(String name, BigDecimal turnover) {
		super();
		this.name = name;
		this.turnover = turnover;
	}

	public String getName() {
		return name;
	}

	public BigDecimal getTurnover() {
		return turnover;
	}

}
