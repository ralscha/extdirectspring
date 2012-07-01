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
package ch.ralscha.extdirectspring.demo.touch;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PictureResizer {

	@RequestMapping(value = "/picresize", method = RequestMethod.GET)
	public void resize(@RequestParam("url") final String url,
			@RequestParam(value = "width", required = false) final Integer width,
			@RequestParam(value = "height", required = false) final Integer height, HttpServletRequest request,
			final HttpServletResponse response) throws MalformedURLException, IOException {

		File servletTmpDir = (File) request.getServletContext().getAttribute("javax.servlet.context.tempdir");
		String sha = org.apache.commons.codec.digest.DigestUtils.sha256Hex(url);
		File pictureFile = new File(servletTmpDir, "pictures/" + sha);

		if (!pictureFile.exists()) {
			FileUtils.copyURLToFile(new URL(url), pictureFile);
		}

		try (OutputStream out = response.getOutputStream()) {

			if (width != null && height != null) {
				BufferedImage image = ImageIO.read(pictureFile);
				if (image.getWidth() > width || image.getHeight() > height) {
					BufferedImage resizedImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC,
							width, height, Scalr.OP_ANTIALIAS);

					int pos = url.lastIndexOf(".");
					String format = url.substring(pos + 1).toUpperCase();

					File tempFile = File.createTempFile("resized", format);
					tempFile.deleteOnExit();
					ImageIO.write(resizedImage, format, tempFile);
					FileUtils.copyFile(tempFile, out);
					tempFile.delete();
				} else {
					FileUtils.copyFile(pictureFile, out);
				}
			} else {
				FileUtils.copyFile(pictureFile, out);
			}
		}

	}

}
