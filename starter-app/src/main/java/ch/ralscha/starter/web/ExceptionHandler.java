package ch.ralscha.starter.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectResponseBuilder;
import ch.ralscha.extdirectspring.controller.Configuration;

@Component
public class ExceptionHandler implements HandlerExceptionResolver, InitializingBean {
	private final static Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired(required = false)
	private Configuration configuration;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (configuration == null) {
			configuration = new Configuration();
		}
	}

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse res, Object handler,
			Exception ex) {

		logger.error("error", ex);

		ExtDirectResponseBuilder builder = new ExtDirectResponseBuilder(request);
		builder.unsuccessful();

		ExtDirectResponse response = builder.build();
		response.setType("exception");
		response.setMessage(configuration.getMessage(ex));

		if (configuration.isSendStacktrace()) {
			response.setWhere(getStackTrace(ex));
		} else {
			response.setWhere(null);
		}

		try {
			res.getOutputStream().print(mapper.writeValueAsString(response));
			res.getOutputStream().flush();
		} catch (IOException e) {
			logger.error("error writing response", e);
		}

		return null;

	}

	private String getStackTrace(final Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		t.printStackTrace(pw);
		pw.flush();
		sw.flush();
		return sw.toString();
	}

}
