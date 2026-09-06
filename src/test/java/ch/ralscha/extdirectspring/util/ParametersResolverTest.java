/*
 * Copyright the original author or authors.
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
package ch.ralscha.extdirectspring.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectRequest;

class ParametersResolverTest {

	private final JsonHandler jsonHandler = new JsonHandler();

	private final ParametersResolver resolver = new ParametersResolver(new DefaultFormattingConversionService(),
			this.jsonHandler);

	@Test
	void convertsNestedCollections() throws Exception {
		assertThat(resolve("nested", List.class, "[[{\"name\":\"Ada\"}]]"))
			.isEqualTo(List.of(List.of(new Person("Ada"))));
	}

	@Test
	void convertsTypedMapValues() throws Exception {
		assertThat(resolve("map", Map.class, "{\"team\":[{\"name\":\"Ada\"}]}"))
			.isEqualTo(Map.of("team", List.of(new Person("Ada"))));
	}

	@Test
	void convertsConcreteCollectionsEvenWhenRawTypesMatch() throws Exception {
		assertThat(resolve("concrete", ArrayList.class, "[{\"name\":\"Ada\"}]")).isEqualTo(List.of(new Person("Ada")));
	}

	@Test
	void convertsOptionalObjects() throws Exception {
		assertThat(resolve("optional", Optional.class, "{\"name\":\"Ada\"}")).isEqualTo(Optional.of(new Person("Ada")));
	}

	@Test
	void convertsOptionalCollections() throws Exception {
		assertThat(resolve("optionalList", Optional.class, "[{\"name\":\"Ada\"}]"))
			.isEqualTo(Optional.of(List.of(new Person("Ada"))));
	}

	@Test
	void convertsGenericObjects() throws Exception {
		assertThat(resolve("wrapped", Envelope.class, "{\"value\":{\"name\":\"Ada\"}}"))
			.isEqualTo(new Envelope<>(new Person("Ada")));
	}

	@Test
	void resolvesInheritedGenericTypes() throws Exception {
		assertThat(resolve("inherited", Object.class, "{\"name\":\"Ada\"}")).isEqualTo(new Person("Ada"));
	}

	@Test
	void resolvesInheritedGenericCollectionTypes() throws Exception {
		assertThat(resolve("inheritedList", List.class, "[{\"name\":\"Ada\"}]")).isEqualTo(List.of(new Person("Ada")));
	}

	@Test
	void convertsNamedArgumentMap() throws Exception {
		assertThat(resolve("named", Map.class, "{\"team\":[{\"name\":\"Ada\"}]}"))
			.isEqualTo(Map.of("team", List.of(new Person("Ada"))));
	}

	@Test
	void namedArgumentMapCanContainTheJavaParameterName() throws Exception {
		assertThat(resolve("named", Map.class, "{\"value\":[{\"name\":\"Ada\"}]}"))
			.isEqualTo(Map.of("value", List.of(new Person("Ada"))));
	}

	@Test
	void preservesEmptyNamedArgumentMap() throws Exception {
		assertThat(resolve("named", Map.class, "{}")).isEqualTo(Map.of());
	}

	private Object resolve(String methodName, Class<?> parameterType, String json) throws Exception {
		Method method = ParameterMethods.class.getMethod(methodName, parameterType);
		MethodInfo info = new MethodInfo(ParameterMethods.class, null, "parameters", method);
		ExtDirectRequest request = new ExtDirectRequest();
		Object data = this.jsonHandler.getMapper().readValue(json, Object.class);
		request.setData(info.isType(ExtDirectMethodType.SIMPLE_NAMED) ? data : List.of(data));
		return this.resolver.resolveParameters(new MockHttpServletRequest(), new MockHttpServletResponse(),
				Locale.ENGLISH, request, info)[0];
	}

	public record Person(String name) {
	}

	public record Envelope<T>(T value) {
	}

	public static class GenericMethods<T> {

		@ExtDirectMethod
		public T inherited(T value) {
			return value;
		}

		@ExtDirectMethod
		public List<T> inheritedList(List<T> value) {
			return value;
		}

	}

	public static class ParameterMethods extends GenericMethods<Person> {

		@ExtDirectMethod(ExtDirectMethodType.SIMPLE_NAMED)
		public Map<String, List<Person>> named(Map<String, List<Person>> value) {
			return value;
		}

		@ExtDirectMethod
		public List<List<Person>> nested(List<List<Person>> value) {
			return value;
		}

		@ExtDirectMethod
		public Map<String, List<Person>> map(Map<String, List<Person>> value) {
			return value;
		}

		@ExtDirectMethod
		@SuppressWarnings("NonApiType")
		public ArrayList<Person> concrete(ArrayList<Person> value) {
			return value;
		}

		@ExtDirectMethod
		public Optional<Person> optional(Optional<Person> value) {
			return value;
		}

		@ExtDirectMethod
		public Optional<List<Person>> optionalList(Optional<List<Person>> value) {
			return value;
		}

		@ExtDirectMethod
		public Envelope<Person> wrapped(Envelope<Person> value) {
			return value;
		}

	}

}
