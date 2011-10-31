/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ralscha.extdirectspring.demo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Launches the demo Web application on a light Jetty WebServer. <br/>
 * This is easier to use in debugging than 'mvn jetty:run' and it starts faster.
 *
 * 
 * @author CreatedBy: Gael Marziou
 */
public class RunDemoOnJetty {

	/**
	 * The port Jetty listens on.
	 */
	private static final int JETTY_PORT = 8080;

	/**
	 * Our own web default settings. See org/eclipse/jetty/webapp/webdefault.xml.
	 */
	private static final String WEB_DEFAULTS_XML = "./src/main/config/jetty/webdefault.xml";

	/**
	  * Path designing web application. Can be path to .war or to exploded war.
	  */
	private static final String WEBAPP_PATH = "./src/main/webapp";

	/**
	 * Context path of the web application.
	 */
	private static final String WEBAPP_CONTEXT_PATH = "/";

	public static void main(String[] args) throws Exception {
		start();

	}

	private static Server start() throws Exception {
		// Creates the web app context
		WebAppContext context = new WebAppContext(WEBAPP_PATH, WEBAPP_CONTEXT_PATH);
		// Remove slf4j from list of classes not exposed to webapp
		context.setServerClasses(new String[] { "-org.mortbay.jetty.plus.jaas.", "org.mortbay.jetty." });
		// Load our default settings to avoid locking files on Windows
		context.setDefaultsDescriptor(WEB_DEFAULTS_XML);

		List<Artifact> includeOnlyArtifact = new ArrayList<Artifact>();
		includeOnlyArtifact.add(new Artifact("resources", "senchatouch"));

		context.setConfigurations(new Configuration[] { new MavenWebInfConfiguration(includeOnlyArtifact),
				new org.eclipse.jetty.webapp.WebXmlConfiguration(),
				new org.eclipse.jetty.webapp.MetaInfConfiguration(),
				new org.eclipse.jetty.webapp.FragmentConfiguration(),
				new org.eclipse.jetty.webapp.JettyWebXmlConfiguration() });

		Server server = new Server(JETTY_PORT);
		server.setHandler(context);

		// Starts server
		server.setStopAtShutdown(true);

		System.out.println("Jetty running on " + JETTY_PORT);

		server.start();

		return server;
	}

	private static class Artifact {
		private String groupId;
		private String artifact;

		public Artifact(String groupId, String artifact) {
			this.groupId = groupId;
			this.artifact = artifact;
		}

		public boolean is(String group, String arti) {
			return this.groupId.equals(group) && this.artifact.equals(arti);
		}
	}

	private static class MavenWebInfConfiguration extends WebInfConfiguration {

		private List<File> jars;

		@SuppressWarnings("unused")
		public MavenWebInfConfiguration() throws ParserConfigurationException, SAXException, IOException {
			this(new ArrayList<Artifact>());
		}

		public MavenWebInfConfiguration(List<Artifact> includeOnlyArtifacts) throws ParserConfigurationException,
				SAXException, IOException {
			File homeDir = new File(System.getProperty("user.home"));

			jars = new ArrayList<File>();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("./pom.xml"));

			Map<String, String> properties = new HashMap<String, String>();
			NodeList propertiesNodeList = doc.getElementsByTagName("properties");
			if (propertiesNodeList != null && propertiesNodeList.item(0) != null) {
				NodeList propertiesChildren = propertiesNodeList.item(0).getChildNodes();
				for (int i = 0; i < propertiesChildren.getLength(); i++) {
					Node node = propertiesChildren.item(i);
					if (node instanceof Element) {
						properties.put("${" + node.getNodeName() + "}", stripWhitespace(node.getTextContent()));
					}
				}
			}

			NodeList nodeList = doc.getElementsByTagName("dependency");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Element node = (Element) nodeList.item(i);
				String groupId = node.getElementsByTagName("groupId").item(0).getTextContent();
				String artifact = node.getElementsByTagName("artifactId").item(0).getTextContent();
				String version = node.getElementsByTagName("version").item(0).getTextContent();

				groupId = stripWhitespace(groupId);
				artifact = stripWhitespace(artifact);
				version = stripWhitespace(version);

				groupId = resolveProperty(groupId, properties);
				artifact = resolveProperty(artifact, properties);
				version = resolveProperty(version, properties);

				if (isIncluded(includeOnlyArtifacts, groupId, artifact)) {

					String scope = null;
					NodeList scopeNode = node.getElementsByTagName("scope");
					if (scopeNode != null && scopeNode.item(0) != null) {
						scope = stripWhitespace(scopeNode.item(0).getTextContent());
					}

					if (scope == null || !scope.equals("provided")) {
						groupId = groupId.replace(".", "/");
						String artifactFileName = groupId + "/" + artifact + "/" + version + "/" + artifact + "-"
								+ version + ".jar";
						jars.add(new File(homeDir, ".m2/repository/" + artifactFileName));
					}

				}
			}
		}

		private boolean isIncluded(List<Artifact> includeOnlyArtifacts, String groupId, String artifactId) {
			for (Artifact artifact : includeOnlyArtifacts) {
				if (artifact.is(groupId, artifactId)) {
					return true;
				}
			}
			return false;
		}

		private String stripWhitespace(String orig) {
			if (orig != null) {
				return orig.replace("\r", "").replace("\n", "").replace("\t", "").trim();
			}
			return orig;
		}

		private String resolveProperty(String orig, Map<String, String> properties) {
			String property = properties.get(orig);
			if (property != null) {
				return property;
			}
			return orig;
		}

		@Override
		protected List<Resource> findJars(WebAppContext context) throws Exception {
			List<Resource> resources = super.findJars(context);

			for (File jar : jars) {
				resources.add(new FileResource(jar.toURI().toURL()));
			}

			return resources;
		}
	}

}
