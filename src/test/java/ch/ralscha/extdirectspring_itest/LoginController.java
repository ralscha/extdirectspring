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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

	private final InMemoryUserDetailsManager userManager;

	@Autowired
	public LoginController(InMemoryUserDetailsManager userManager) {
		this.userManager = userManager;
	}

	@RequestMapping("/login")
	@ResponseBody
	public void login() {
		UserDetails ud = this.userManager.loadUserByUsername("jimi");
		if (ud != null) {
			UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
					ud, null, ud.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(token);
		}
	}

}
