package ch.ralscha.extdirectspring.generator;

/**
 * Enumeration of all possible output formates for the model generator.
 * Difference between Touch and ExtJS is the config system that Touch uses.
 * 
 * @author Ralph Schaer
 */
public enum OutputFormat {
	/**
	 * Orders the model generator to create ExtJS4 compatible code.
	 */
	EXTJS4,

	/**
	 * Orders the model generator to create Touch2 compatible code.
	 */
	TOUCH2;
}
