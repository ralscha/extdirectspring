#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.jdbcx.JdbcDataSource;

public class StartJetty {
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		System.setProperty("spring.profiles.active", "development");
		
		int port = 8080;

		try {
			ServerSocket srv = new ServerSocket(port);
			srv.close();
		} catch (IOException e) {
			System.out.println("PORT " + port + " ALREADY IN USE");
			return;
		}

		WebAppContext context = new WebAppContext("./src/main/webapp", "/");
		context.setDefaultsDescriptor("./src/main/config/webdefault.xml");

		List<Artifact> includeOnlyArtifact = new ArrayList<Artifact>();
		includeOnlyArtifact.add(new Artifact("resources", "extjs"));
		
		context.setConfigurations(new Configuration[] { new MavenWebInfConfiguration(includeOnlyArtifact),
				new org.eclipse.jetty.webapp.WebXmlConfiguration(),
				new org.eclipse.jetty.webapp.MetaInfConfiguration(),
				new org.eclipse.jetty.webapp.FragmentConfiguration(),
				new org.eclipse.jetty.plus.webapp.EnvConfiguration(),
				new org.eclipse.jetty.plus.webapp.PlusConfiguration(),
				new org.eclipse.jetty.webapp.JettyWebXmlConfiguration() });

		JdbcDataSource dataSource = new JdbcDataSource();
		dataSource.setUser("sa");
		dataSource.setURL("jdbc:h2:~/starter");
		
		Resource resource = new Resource("jdbc/ds", dataSource);	

		context.addBean(resource);
		
		Server server = new Server(port);
		server.setHandler(context);
		server.start();

		System.out.println("Jetty Startup Time: " + (System.currentTimeMillis()-start) + " ms");
		System.out.println("Jetty running on 8080");
	}

}