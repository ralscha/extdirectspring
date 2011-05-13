/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.itest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;

@Component
public class MyExceptionHandler implements HandlerExceptionResolver {

	private ObjectMapper mapper = new ObjectMapper();
	
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse res, Object handler,
			Exception ex) {
		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
		builder.unsuccessful();
		
		ExtDirectResponse response = builder.build();
		response.setType("exception");
		response.setMessage("Server Error");
		response.setWhere(ex.toString());
		try {
			res.getOutputStream().print(mapper.writeValueAsString(response));
			res.getOutputStream().flush();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
        return null;
		
	}

}
