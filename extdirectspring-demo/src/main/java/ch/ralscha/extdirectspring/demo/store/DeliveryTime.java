package ch.ralscha.extdirectspring.demo.store;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(using=DeliveryTimeSerializer.class)
public enum DeliveryTime {
	
	BeginningOfMonth("Beginning of Month"), BeginningToMiddleOfMonth("Beginning to Middle of Month"), MiddleOfMonth(
			"Middle of Month"), MiddleToEndOfMonth("Middle to End of Month"), EndOfMonth("End of Month"), Unknown(
			"Unknown");

	private String label;

	DeliveryTime(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
	
	
}