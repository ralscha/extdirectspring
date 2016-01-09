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

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

@Service
public class FileUploadService {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST,
			group = "itest_upload_service", event = "test", entryClass = String.class,
			synchronizeOnSession = true)
	public ExtDirectFormPostResult uploadTest(
			@RequestParam("fileUpload") MultipartFile file, @Valid User user,
			BindingResult result) throws IOException {

		ExtDirectFormPostResult resp = new ExtDirectFormPostResult(result, false);

		if (file != null && !file.isEmpty()) {
			resp.addResultProperty("fileContents", new String(file.getBytes()));
			resp.addResultProperty("fileName", file.getOriginalFilename());
		}

		resp.addResultProperty("name", user.getName());
		resp.addResultProperty("firstName", user.getFirstName());
		resp.addResultProperty("age", user.getAge());
		resp.addResultProperty("e-mail", user.getEmail());

		resp.setSuccess(true);
		return resp;
	}

}
