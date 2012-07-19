/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.controller;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.provider.Row;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerInterfaceTest {

	@Autowired
	private RouterController controller;

	@BeforeClass
	public static void beforeTest() {
		Locale.setDefault(Locale.US);
	}

	@Test
	public void testNoParameters() throws IOException {
		ControllerUtil.sendAndReceive(controller, "remoteProviderImplementation", "method2", null, "method2() called");
	}

	@Test
	public void testNoParameterAnnotation() throws IOException {
		ControllerUtil.sendAndReceive(controller, "remoteProviderImplementation", "method3", new Object[] { 21, 3.1,
				"aString2" }, "method3() called-21-3.1-aString2");
	}

	@Test
	public void testWithRequestParamAnnotation() throws IOException {

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("lastName", "Smith");
		readRequest.put("active", true);

		List<Row> rows = (List<Row>) ControllerUtil.sendAndReceive(controller, "remoteProviderImplementation",
				"storeRead", readRequest, new TypeReference<List<Row>>() {/* nothing_here */
				});

		assertThat(rows).hasSize(1);
		Row theRow = rows.get(0);
		assertThat(theRow.getId()).isEqualTo(1);
		assertThat(theRow.getName()).isEqualTo("Smith");
		assertThat(theRow.getSalary()).isEqualTo(new BigDecimal("40"));
	}
}
