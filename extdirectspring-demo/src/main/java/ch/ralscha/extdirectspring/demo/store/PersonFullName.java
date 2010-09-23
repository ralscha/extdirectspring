package ch.ralscha.extdirectspring.demo.store;

public class PersonFullName {
  private final String fullName;

  public PersonFullName(Person person) {
    this.fullName = person.getFullName();
  }

  public String getFullName() {
    return fullName;
  }


}
