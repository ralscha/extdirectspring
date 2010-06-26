package ch.ralscha.extdirectspring.mock;

import java.math.BigDecimal;

public class Row implements Comparable<Row> {

  private int id;
  private String name;
  private boolean admin;
  private BigDecimal salary;

  public Row() {
    //no action
  }
  
  public Row(int id, String name, boolean admin, String salary) {
    super();
    this.id = id;
    this.name = name;
    this.admin = admin;
    this.salary = new BigDecimal(salary);
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }

  public BigDecimal getSalary() {
    return salary;
  }

  public void setSalary(BigDecimal salary) {
    this.salary = salary;
  }

  @Override
  public int compareTo(Row o) {
    return name.compareTo(o.name);
  }

}
