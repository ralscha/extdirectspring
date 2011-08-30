#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
import java.io.File;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.deploy.ContextResource;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang.SystemUtils;

public class StartTomcat {

	public static void main(String[] args) throws ServletException, LifecycleException {

		System.setProperty("spring.profiles.active", "development");

		Tomcat tomcat = new Tomcat();
		//tomcat.setSilent(true);
		tomcat.setPort(8080);
		tomcat.setBaseDir(SystemUtils.getJavaIoTmpDir().getPath());
		tomcat.getConnector().setURIEncoding("UTF-8");

		File currentDir = new File(".");
		Context ctx = tomcat.addWebapp("/", currentDir.getAbsolutePath() + "/src/main/webapp");

		tomcat.enableNaming();

		ContextResource res = new ContextResource();
		res.setName("jdbc/ds");
		res.setType("javax.sql.DataSource");
		res.setAuth("Container");

		res.setProperty("username", "sa");
		res.setProperty("driverClassName", "org.h2.Driver");

		res.setProperty("url", "jdbc:h2:~/starter");
		res.setProperty("maxActive", "5");
		res.setProperty("maxIdle", "1");
		res.setProperty("maxWait", "10000");
		res.setProperty("defaultAutoCommit", "false");

		ctx.getNamingResources().addResource(res);

		tomcat.start();
		tomcat.getServer().await();
	}
}
