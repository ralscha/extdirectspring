package ch.ralscha.extdirectspring.demo.store;

public class PersonFullNameCity {
  private String fullName;
  private String city;

  public PersonFullNameCity(Person person) {
    this.fullName = person.getFullName();
    this.city = person.getCity();
  }
  
  public String getFullName() {
    return fullName;
  }

  public String getCity() {
    return city;
  }

}
