import ch.ralscha.embeddedtc.EmbeddedTomcat;

public class StartTomcat {
	public static void main(String[] args) throws Exception {
		EmbeddedTomcat.create().addContextEnvironmentAndResourceFromFile("./src/main/config/tomcat.xml").startAndWait();
	}
}