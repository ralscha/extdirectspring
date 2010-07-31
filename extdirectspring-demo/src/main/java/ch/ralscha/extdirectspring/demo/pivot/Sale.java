package ch.ralscha.extdirectspring.demo.pivot;

public class Sale {

  private int id;
  private String product;
  private String city;
  private int quantity;
  private int value;
  private int month;
  private int quarter;
  private int year;
  private String person;

  public Sale() {
    // no action here
  }

  public Sale(String[] line) {
    this.id = Integer.parseInt(line[0]);
    this.product = line[1];
    this.city = line[2];
    this.value = Integer.parseInt(line[3]);
    this.quantity = Integer.parseInt(line[4]);
    this.month = Integer.parseInt(line[5]);
    this.quarter = Integer.parseInt(line[6]);
    this.year = Integer.parseInt(line[7]);
    this.person = line[8];
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getProduct() {
    return product;
  }

  public void setProduct(String product) {
    this.product = product;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }

  public int getMonth() {
    return month;
  }

  public void setMonth(int month) {
    this.month = month;
  }

  public int getQuarter() {
    return quarter;
  }

  public void setQuarter(int quarter) {
    this.quarter = quarter;
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public String getPerson() {
    return person;
  }

  public void setPerson(String person) {
    this.person = person;
  }

}
