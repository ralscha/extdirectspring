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
package ch.ralscha.extdirectspring.controller;

import java.util.List;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.EdJsonStoreResult;
import ch.ralscha.extdirectspring.bean.ExtDirectRawJsonStoreResult;

public class RawJsonStoreReadService {

	private static final String USER_1 = "{ \"_id\" : { \"$oid\" : \"4cf8e5b8924e23349fb99454\"} , \"username\" : \"johnd\" , \"firstName\" : \"John\" , \"name\" : \"Doe\" , \"email\" : \"john.doe@test.com\" , \"enabled\" : true , \"dob\" : { \"$date\" : \"1985-02-03T23:00:00Z\"} , \"noOfLogins\" : 5 , \"password\" : \"91dfd9ddb4198affc5c194cd8ce6d338fde470e2\" , \"groups\" : [ \"admin\" , \"user\"] , \"phoneNumber\" : [ { \"type\" : \"home\" , \"number\" : \"212 555-1234\"} , { \"type\" : \"fax\" , \"number\" : \"646 555-4567\"}]}";

	private static final String USER_2 = "{ \"_id\" : { \"$oid\" : \"4cf8e5b8924e2334a0b99454\"} , \"username\" : \"francol\" , \"firstName\" : \"Franco\" , \"name\" : \"Lawrence\" , \"email\" : \"franco.lawrence@test.com\" , \"enabled\" : true , \"dob\" : { \"$date\" : \"1979-12-01T23:00:00Z\"} , \"noOfLogins\" : 3 , \"password\" : \"5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8\" , \"groups\" : [ \"user\"] , \"phoneNumber\" : [ { \"type\" : \"home\" , \"number\" : \"212 555-4321\"} , { \"type\" : \"fax\" , \"number\" : \"646 555-7654\"}]}";

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "raw_json_test")
	public ExtDirectRawJsonStoreResult listUsers1() {
		return new ExtDirectRawJsonStoreResult(records());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "raw_json_test")
	public ExtDirectRawJsonStoreResult listUsers2() {
		return new ExtDirectRawJsonStoreResult(2, records());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "raw_json_test")
	public ExtDirectRawJsonStoreResult listUsers3() {
		return new ExtDirectRawJsonStoreResult(2L, records(), Boolean.FALSE);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "raw_json_test")
	public EdJsonStoreResult listUsers1Ed() {
		return EdJsonStoreResult.success(records());
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "raw_json_test")
	public EdJsonStoreResult listUsers2Ed() {
		return EdJsonStoreResult.success(records(), 2L);
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "raw_json_test")
	public EdJsonStoreResult listUsers3Ed() {
		return EdJsonStoreResult.builder().records(records()).total(2L).success(false).build();
	}

	private static List<String> records() {
		return List.of(USER_1, USER_2);
	}

}