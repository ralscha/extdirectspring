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
package ch.ralscha.extdirectspring.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;

@Service
public class RemoteProviderImplementation implements RemoteProviderInterface {

	@Override
	public String method2() {
		return "method2() called";
	}

	@Override
	public String method3(long i, Double d, String s) {
		return String.format("method3() called-%d-%.1f-%s", i, d, s);
	}

	@Override
	public List<Row> storeRead(ExtDirectStoreReadRequest request,
			@RequestParam(value = "lastName") String name,
			@RequestParam(value = "theAge", defaultValue = "40") Integer age,
			Boolean active, final HttpServletRequest httpRequest) {

		assertThat(age.intValue()).isEqualTo(40);
		assertThat(httpRequest).isNotNull();
		assertThat(request).isNotNull();
		assertThat(name).isEqualTo("Smith");
		assertThat(active).isTrue();

		assertThat(request.getParams()).hasSize(2);
		assertThat(request.getParams()).contains(entry("lastName", "Smith"));
		assertThat(request.getParams()).contains(entry("active", Boolean.TRUE));

		List<Row> result = new ArrayList<Row>();
		result.add(new Row(1, name, active, "" + age));
		return result;
	}

}
