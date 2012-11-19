package ch.ralscha.extdirectspring.generator.bean;

import ch.ralscha.extdirectspring.generator.ModelAssociation;
import ch.ralscha.extdirectspring.generator.ModelAssociationType;

public class Pos {

	public int entityId;

	public int orderId;

	@ModelAssociation(autoLoad = true, foreignKey = "orderId", getterName = "getMeTheOrder", setterName = "setTheOrder", model = Order.class, name = "theOrder", primaryKey = "entityId", value = ModelAssociationType.BELONGS_TO)
	public Order order;
}
