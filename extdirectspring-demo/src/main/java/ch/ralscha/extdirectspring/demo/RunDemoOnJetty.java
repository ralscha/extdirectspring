/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

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

		Server server = new Server(JETTY_PORT);
		server.setHandler(context);

		// Starts server
		server.setStopAtShutdown(true);

		System.out.println("Jetty running on " + JETTY_PORT);

		server.start();

		return server;
	}
}
