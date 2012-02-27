package ch.ralscha.extdirectspring.demo.touch;

public class President {
	private String firstName;
	private String lastName;
	private String middleInitial;

	public President(String firstName, String lastName) {
		this(firstName, null, lastName);
	}
	
	public President(String firstName, String middleInitial, String lastName) {
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;		
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

}
