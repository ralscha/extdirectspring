package ch.ralscha.extdirectspring.generator;

public enum ModelAssociationType {
	BELONGS_TO("belongsTo"), HAS_MANY("hasMany"), HAS_ONE("hasOne");

	private String jsName;

	private ModelAssociationType(String jsName) {
		this.jsName = jsName;
	}

	/**
	 * @return the name of the type for JS code
	 */
	public String getJsName() {
		return jsName;
	}
}
