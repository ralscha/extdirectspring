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

import java.util.LinkedHashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import ch.ralscha.extdirectspring.bean.ExtDirectRequest;

public class ControllerUtil {

	private static ObjectMapper mapper = new ObjectMapper();

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequestJson(String action, String method, int tid, Object... data) {
		ExtDirectRequest dr = new ExtDirectRequest();
		dr.setAction(action);
		dr.setMethod(method);
		dr.setTid(tid);
		dr.setType("rpc");
		dr.setData(data);
		return mapper.convertValue(dr, LinkedHashMap.class);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> createRequestJsonNamedParam(String action, String method, int tid,
			Map<String, Object> data) {
		ExtDirectRequest dr = new ExtDirectRequest();
		dr.setAction(action);
		dr.setMethod(method);
		dr.setTid(tid);
		dr.setType("rpc");
		dr.setData(data);
		return mapper.convertValue(dr, LinkedHashMap.class);
	}

}
