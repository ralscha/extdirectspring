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

	@RequestMapping(value="/picresize", method=RequestMethod.GET)
	public void resize(@RequestParam("url") String url, 
			@RequestParam(value = "width", required = false) Integer width,
			@RequestParam(value = "height", required = false) Integer height,
			HttpServletRequest request, HttpServletResponse response) throws MalformedURLException, IOException {

		File servletTmpDir = (File) request.getServletContext().getAttribute("javax.servlet.context.tempdir");
		String sha = org.apache.commons.codec.digest.DigestUtils.sha256Hex(url);
		File pictureFile = new File(servletTmpDir, "pictures/"+sha);
		
		if (!pictureFile.exists()) {			
			FileUtils.copyURLToFile(new URL(url), pictureFile);			
		}
		
		OutputStream out = response.getOutputStream();
		
		if (width != null && height != null) {
			BufferedImage image = ImageIO.read(pictureFile);
			if (image.getWidth() > width || image.getHeight() > height) {
				BufferedImage resizedImage =
						  Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.AUTOMATIC,
								  width, height, Scalr.OP_ANTIALIAS);
				
				int pos = url.lastIndexOf(".");
				String format = url.substring(pos+1).toUpperCase();
				
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
		
		out.close();
		
	}

}
