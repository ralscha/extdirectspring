import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MavenWebInfConfiguration extends WebInfConfiguration {

	private List<File> jars;

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
					String artifactFileName = groupId + "/" + artifact + "/" + version + "/" + artifact + "-" + version
							+ ".jar";
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
