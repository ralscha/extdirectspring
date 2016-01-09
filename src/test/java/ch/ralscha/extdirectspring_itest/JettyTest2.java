/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring_itest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class JettyTest2 {
	private static Server server;

	@BeforeClass
	public static void startServer() throws Exception {
		WebAppContext context = new WebAppContext("./src/test/webapp2", "/");

		server = new Server(9998);
		server.setHandler(context);

		server.setStopAtShutdown(true);
		server.start();

	}

	@AfterClass
	public static void stopServer() throws Exception {
		server.stop();
	}
}
