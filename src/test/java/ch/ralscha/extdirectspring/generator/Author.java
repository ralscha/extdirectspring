package ch.ralscha.extdirectspring.generator;

@Model(value = "MyApp.Author", idProperty = "id")
public class Author {

	public String id;

	public String title;

	public String firstName;

	public String lastName;

	public int book_id;

}
