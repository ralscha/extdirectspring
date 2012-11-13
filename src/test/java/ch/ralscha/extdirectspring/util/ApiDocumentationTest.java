package ch.ralscha.extdirectspring.util;

import javax.validation.Valid;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.*;
import ch.ralscha.extdirectspring.bean.ExtDirectFormPostResult;

public class ApiDocumentationTest {

	
	@ExtDirectMethod(value = ExtDirectMethodType.FORM_POST, documentation=
			@ExtDirectMethodDocumentation(value="this method is used to test the documentation generation",
					author="dbs",
					version="0.1",
					returnMethod=@ExtDirectDocReturn(properties= {"success", "errors"}, descriptions= {"true for success, false otherwise", "list of failed fields"}),
					parameters=@ExtDirectDocParameters(params = {"a", "b", "c", "d", "e"},descriptions= {"property a integer", "property b string", "property c string", "property d boolean", "array of integers"}))
	)
	public ExtDirectFormPostResult methodPostToDocument(
			@RequestParam(value = "firstName") String firstName,
			@RequestParam(value = "lastName") String lastName, 
         @RequestParam(value = "age") int age,
			@Valid JsonTestBean jsonTestBean, BindingResult result) {
		
		return new ExtDirectFormPostResult(result);
	}

}
