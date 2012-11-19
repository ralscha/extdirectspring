package ch.ralscha.extdirectspring.generator.bean;

import ch.ralscha.extdirectspring.generator.Model;
import ch.ralscha.extdirectspring.generator.ModelAssociation;
import ch.ralscha.extdirectspring.generator.ModelAssociationType;

@Model(idProperty = "eId", value = "MyApp.Employee")
public class Employee {
	public int eId;

	public int address_id;

	@ModelAssociation(value = ModelAssociationType.HAS_ONE)
	public Address address;
}
