package ch.ralscha.starter.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;

@Controller
public class I18nMessageController {

	private ObjectMapper objectMapper = new ObjectMapper();
	private String prefix = "var i18n = ";
	private String postfix = ";";

	@RequestMapping(value = "/i18n.js", method = RequestMethod.GET)
	public void i18n(final HttpServletRequest request, final HttpServletResponse response, final Locale locale)
			throws JsonGenerationException, JsonMappingException, IOException {

		response.setContentType("application/x-javascript;charset=UTF-8");

		ResourceBundle rb = ResourceBundle.getBundle("messages", locale);

		Map<String, String> messages = Maps.newHashMap();
		Enumeration<String> e = rb.getKeys();
		while (e.hasMoreElements()) {
			String key = e.nextElement();
			messages.put(key, rb.getString(key));
		}

		String output = prefix + objectMapper.writeValueAsString(messages) + postfix;
		response.getOutputStream().write(output.getBytes());
	}

}
