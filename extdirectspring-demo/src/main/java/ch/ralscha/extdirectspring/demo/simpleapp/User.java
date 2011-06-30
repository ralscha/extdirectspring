package ch.ralscha.extdirectspring.demo.simpleapp;

public class User {
	private String id;
	private String firstName;
	private String lastName;
	private String email;
	private String city;

	public User() {
		//default constructor
	}

	public User(String[] line) {
		this.id = line[0];
		this.firstName = line[1];
		this.lastName = line[2];
		this.email = line[3];
		this.city = line[4];
	}

	public void update(User newValues) {
		this.firstName = newValues.getFirstName();
		this.lastName = newValues.getLastName();
		this.email = newValues.email;
		this.city = newValues.getCity();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", city=" + city + "]";
	}

}
