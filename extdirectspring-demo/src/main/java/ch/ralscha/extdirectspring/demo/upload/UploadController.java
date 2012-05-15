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
package ch.ralscha.extdirectspring.demo.upload;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;

@Controller
public class UploadController {

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "upload")
	@RequestMapping(value = "/uploadTest", method = RequestMethod.POST)
	public void uploadTest(Locale locale, HttpServletRequest request, @RequestParam("fileUpload") MultipartFile file,
			HttpServletResponse response) throws IOException {

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request, response);

		if (file != null && !file.isEmpty()) {
			builder.addResultProperty("fileContents", new String(file.getBytes()));
		}
		builder.successful();
		builder.buildAndWrite();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, group = "upload4")
	@RequestMapping(value = "/uploadTest4", method = RequestMethod.POST)
	public void uploadTest4(Locale locale, HttpServletRequest request, @RequestParam("fileUpload1") MultipartFile file1,
			@RequestParam("fileUpload2") MultipartFile file2, HttpServletResponse response) throws IOException {

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request, response);

		if (file1 != null && !file1.isEmpty()) {
			System.out.println("File1 Name : " + file1.getName());
			System.out.println("File1 Bytes: " + file1.getSize());
		}

		if (file2 != null && !file2.isEmpty()) {
			System.out.println("File2 Name : " + file2.getName());
			System.out.println("File2 Bytes: " + file2.getSize());

			builder.addResultProperty("fileContents", new String(file2.getBytes()));
		}

		builder.successful();
		builder.buildAndWrite();
	}

}
