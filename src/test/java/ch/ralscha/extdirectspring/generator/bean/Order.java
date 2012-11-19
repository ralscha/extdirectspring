package ch.ralscha.extdirectspring.generator.bean;

import java.util.List;

import ch.ralscha.extdirectspring.generator.ModelAssociation;
import ch.ralscha.extdirectspring.generator.ModelAssociationType;

public class Order {

	public int entityId;

	@ModelAssociation(value = ModelAssociationType.HAS_MANY, model = Pos.class, getterName = "ignore", setterName = "ignore", autoLoad = false, foreignKey = "orderId", name = "pos", primaryKey = "entityId")
	public List<Pos> positions;

}
