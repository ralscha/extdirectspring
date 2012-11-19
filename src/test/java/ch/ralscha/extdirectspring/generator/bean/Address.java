package ch.ralscha.extdirectspring.generator.bean;

import ch.ralscha.extdirectspring.generator.ModelAssociation;
import ch.ralscha.extdirectspring.generator.ModelAssociationType;

public class Address {
	public int id;

	public int e_id;

	@ModelAssociation(value = ModelAssociationType.HAS_ONE, autoLoad = true, foreignKey = "e_id", model = Employee.class, getterName = "getE", setterName = "setE", name = "emp")
	public Employee employee;
}
