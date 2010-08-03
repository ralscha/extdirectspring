package ch.ralscha.extdirectspring.demo.group;

import java.math.BigDecimal;
import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

public class Summary {

  private String description;
  private BigDecimal estimate;
  private BigDecimal rate;
  private Date due;
  private BigDecimal cost;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public BigDecimal getEstimate() {
    return estimate;
  }

  public void setEstimate(BigDecimal estimate) {
    this.estimate = estimate;
  }

  public BigDecimal getRate() {
    return rate;
  }

  public void setRate(BigDecimal rate) {
    this.rate = rate;
  }

  @JsonSerialize(using = MDYDateSerializer.class)
  public Date getDue() {
    return due;
  }

  public void setDue(Date due) {
    this.due = due;
  }

  public BigDecimal getCost() {
    return cost;
  }

  public void setCost(BigDecimal cost) {
    this.cost = cost;
  }

}
