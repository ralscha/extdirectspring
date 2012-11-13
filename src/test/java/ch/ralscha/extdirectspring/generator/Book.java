package ch.ralscha.extdirectspring.generator;

import java.util.List;

import org.joda.time.LocalDate;

@Model(value = "MyApp.Book", idProperty = "isbn")
public class Book {

	public String title;

	public String publisher;

	public String isbn;

	@ModelField(dateFormat = "d-m-Y")
	public LocalDate publishDate;

	public int numberOfPages;

	public boolean read;

	@ModelAssociation(value = ModelAssociationType.HAS_MANY, model = Author.class, autoLoad = true)
	public List<Author> authors;

}
