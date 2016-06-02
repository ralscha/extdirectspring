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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;

@Controller
@RequestMapping("/upload")
public class FileUploadController {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "itest_upload",
			event = "test", entryClass = String.class, synchronizeOnSession = true)
	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public void uploadTest(HttpServletRequest request,
			@RequestParam("fileUpload") MultipartFile file,
			final HttpServletResponse response, @Valid User user, BindingResult result)
			throws IOException {

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request,
				response);

		if (file != null && !file.isEmpty()) {
			builder.addResultProperty("fileContents", new String(file.getBytes()));
			builder.addResultProperty("fileName", file.getOriginalFilename());
		}
		builder.addErrors(result);
		builder.addResultProperty("name", user.getName());
		builder.addResultProperty("firstName", user.getFirstName());
		builder.addResultProperty("age", user.getAge());
		builder.addResultProperty("email", user.getEmail());

		builder.successful();
		builder.buildAndWrite();
	}

}
