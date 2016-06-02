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

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectDocParameters;
import ch.ralscha.extdirectspring.annotation.ExtDirectDocReturn;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodDocumentation;

@Service
public class RemoteProviderSimpleDoc {

	@ExtDirectMethod(group = "doc", documentation = @ExtDirectMethodDocumentation(
			value = "this method is used to test the documentation generation",
			author = "dbs", version = "0.1", deprecated = true,
			returnMethod = @ExtDirectDocReturn(properties = { "success", "errors" },
					descriptions = { "true for success, false otherwise",
							"list of failed fields" }),
			parameters = @ExtDirectDocParameters(params = { "a", "b", "c", "d", "e" },
					descriptions = { "property a integer", "property b string",
							"property c string", "property d boolean",
							"array of integers" })))
	public String method1() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation("method two doc"))
	public String method2() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method three doc",
					author = "dbs"))
	public String method3() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method four doc",
					author = "sr", version = "0.4"))
	public String method4() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method five doc",
					author = "dbs", version = "0.5", deprecated = true))
	public String method5() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method six doc",
					author = "sr", version = "0.6", deprecated = false))
	public String method6() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method seven doc",
					author = "sr", version = "0.7", deprecated = true,
					returnMethod = @ExtDirectDocReturn(properties = "p1",
							descriptions = "p1 desc")))
	public String method7() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method eight doc",
					author = "sr", version = "0.8", deprecated = false,
					returnMethod = @ExtDirectDocReturn(properties = { "p1", "p2" },
							descriptions = { "p1 desc", "p2 desc" })))
	public String method8() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method nine doc",
					author = "dbs", version = "0.9",
					returnMethod = @ExtDirectDocReturn(properties = { "p1" },
							descriptions = { "p1 desc", "p2 desc" })))
	public String method9() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc", documentation = @ExtDirectMethodDocumentation(
			value = "method ten doc", author = "sr",
			returnMethod = @ExtDirectDocReturn(properties = { "p1", "p2" },
					descriptions = { "p1 desc", "p2 desc" }),
			parameters = @ExtDirectDocParameters(params = "a", descriptions = "a desc")))
	public String method10() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc",
			documentation = @ExtDirectMethodDocumentation(value = "method eleven doc",
					parameters = @ExtDirectDocParameters(params = { "a", "b" },
							descriptions = { "a desc", "b desc" })))
	public String method11() {
		return "nothing here";
	}

	@ExtDirectMethod(group = "doc", documentation = @ExtDirectMethodDocumentation(
			value = "method twelve doc", author = "sr",
			parameters = @ExtDirectDocParameters(params = { "a" }, descriptions = {})))
	public String method12() {
		return "nothing here";
	}

}
