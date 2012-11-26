package ch.ralscha.extdirectspring_itest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ch.ralscha.extdirectspring.generator.ModelGenerator;
import ch.ralscha.extdirectspring.generator.OutputFormat;
import ch.ralscha.extdirectspring.generator.bean.Author;
import ch.ralscha.extdirectspring.generator.bean.Book;

@Controller
public class ModelGeneratorController {

	@RequestMapping(value = "/Author.js", method = RequestMethod.GET)
	public void author(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelGenerator.writeModel(request, response, Author.class, OutputFormat.EXTJS4);
	}
	
	@RequestMapping(value = "/Book", method = RequestMethod.GET)
	public void book(HttpServletRequest request, HttpServletResponse response) throws IOException {
		ModelGenerator.writeModel(request, response, Book.class, OutputFormat.TOUCH2);
	}
}
