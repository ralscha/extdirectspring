/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.generic;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.controller.ControllerUtil;

import com.fasterxml.jackson.core.type.TypeReference;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class GenericTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setupMockMvc() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testSimpleMethod() {
		Color newColor = new Color();
		newColor.setActive(true);
		Date now = new Date();
		newColor.setCreateDate(now);
		newColor.setId(1L);
		newColor.setLongName("longName");
		newColor.setShortName("shortName");
		String result = (String) ControllerUtil.sendAndReceive(mockMvc, "colorOptionService", "simpleMethod", String.class,
				newColor, Collections.singletonList(newColor));
		assertThat(result).isEqualTo("1longNameshortName;1");
	}
	
	@Test
	public void testCreateOne() {
		Color newColor = new Color();
		newColor.setActive(true);
		Date now = new Date();
		newColor.setCreateDate(now);
		newColor.setId(null);
		newColor.setLongName("longName");
		newColor.setShortName("shortName");

		Color color = (Color) ControllerUtil.sendAndReceive(mockMvc, "colorOptionService", "createOne", Color.class,
				newColor);
		assertThat(color.getId()).isEqualTo(1L);
		assertThat(color.isActive()).isTrue();
		assertThat(color.getLongName()).isEqualTo("longName");
		assertThat(color.getShortName()).isEqualTo("shortName");
		assertThat(color.getCreateDate()).isEqualTo(now);
	}

	@Test
	public void testCreateMultiple() {
		Date now = new Date();

		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Color> colors = new ArrayList<Color>();

		Color newColor = new Color();
		newColor.setActive(false);
		newColor.setCreateDate(now);
		newColor.setId(null);
		newColor.setLongName("one");
		newColor.setShortName("1");
		colors.add(newColor);

		newColor = new Color();
		newColor.setActive(true);
		newColor.setCreateDate(now);
		newColor.setId(null);
		newColor.setLongName("two");
		newColor.setShortName("2");
		colors.add(newColor);

		storeRequest.put("records", colors);

		List<Color> result = (List<Color>) ControllerUtil.sendAndReceive(mockMvc, "colorOptionService",
				"createMultiple", new TypeReference<List<Color>>() {/* nothing_here */
				}, storeRequest);

		assertThat(result).hasSize(2);
		Color color = result.get(0);
		assertThat(color.getId()).isEqualTo(1L);
		assertThat(color.isActive()).isFalse();
		assertThat(color.getLongName()).isEqualTo("one");
		assertThat(color.getShortName()).isEqualTo("1");
		assertThat(color.getCreateDate()).isEqualTo(now);

		color = result.get(1);
		assertThat(color.getId()).isEqualTo(2L);
		assertThat(color.isActive()).isTrue();
		assertThat(color.getLongName()).isEqualTo("two");
		assertThat(color.getShortName()).isEqualTo("2");
		assertThat(color.getCreateDate()).isEqualTo(now);
	}

	@Test
	public void testDestroyOne() {
		Color color = new Color();
		color.setId(1L);

		ControllerUtil.sendAndReceive(mockMvc, "colorOptionService", "destroyOne", new TypeReference<List<Color>>() {/* nothing_here */
		}, color);
	}

	@Test
	public void testDestroyMultiple() {
		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Color> colors = new ArrayList<Color>();

		Color color = new Color();
		color.setId(1L);
		colors.add(color);

		color = new Color();
		color.setId(2L);
		colors.add(color);

		storeRequest.put("records", colors);

		ControllerUtil.sendAndReceive(mockMvc, "colorOptionService", "destroyMultiple",
				new TypeReference<List<Color>>() {/* nothing_here */
				}, storeRequest);
	}

	@Test
	public void testUpdateOne() {
		Color newColor = new Color();
		newColor.setActive(true);
		Date now = new Date();
		newColor.setCreateDate(now);
		newColor.setId(1L);
		newColor.setLongName("longName");
		newColor.setShortName("shortName");

		Color color = (Color) ControllerUtil.sendAndReceive(mockMvc, "colorOptionService", "updateOne", Color.class,
				newColor);
		assertThat(color.getId()).isEqualTo(1L);
		assertThat(color.isActive()).isTrue();
		assertThat(color.getLongName()).isEqualTo("LONGNAME");
		assertThat(color.getShortName()).isEqualTo("SHORTNAME");
		assertThat(color.getCreateDate()).isEqualTo(now);
	}

	@Test
	public void testUpdateMultiple() {
		Date now = new Date();

		Map<String, Object> storeRequest = new LinkedHashMap<String, Object>();
		List<Color> colors = new ArrayList<Color>();

		Color newColor = new Color();
		newColor.setActive(false);
		newColor.setCreateDate(now);
		newColor.setId(1L);
		newColor.setLongName("one");
		newColor.setShortName("a");
		colors.add(newColor);

		newColor = new Color();
		newColor.setActive(true);
		newColor.setCreateDate(now);
		newColor.setId(2L);
		newColor.setLongName("two");
		newColor.setShortName("b");
		colors.add(newColor);

		storeRequest.put("records", colors);

		List<Color> result = (List<Color>) ControllerUtil.sendAndReceive(mockMvc, "colorOptionService",
				"updateMultiple", new TypeReference<List<Color>>() {/* nothing_here */
				}, storeRequest);

		assertThat(result).hasSize(2);
		Color color = result.get(0);
		assertThat(color.getId()).isEqualTo(1L);
		assertThat(color.isActive()).isFalse();
		assertThat(color.getLongName()).isEqualTo("ONE");
		assertThat(color.getShortName()).isEqualTo("A");
		assertThat(color.getCreateDate()).isEqualTo(now);

		color = result.get(1);
		assertThat(color.getId()).isEqualTo(2L);
		assertThat(color.isActive()).isTrue();
		assertThat(color.getLongName()).isEqualTo("TWO");
		assertThat(color.getShortName()).isEqualTo("B");
		assertThat(color.getCreateDate()).isEqualTo(now);
	}


}