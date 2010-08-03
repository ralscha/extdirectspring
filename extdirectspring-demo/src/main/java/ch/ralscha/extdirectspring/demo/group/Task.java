package ch.ralscha.extdirectspring.demo.group;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.codehaus.jackson.map.annotate.JsonSerialize;

public class Task {

  private int projectId;
  private String project;
  private int taskId;
  private String description;
  private BigDecimal estimate;
  private BigDecimal rate;
  private Date due;

  public Task(int projectId, String project, int taskId, String description, BigDecimal estimate, BigDecimal rate,
      int dueYear, int dueMonth, int dueDay) {

    this.projectId = projectId;
    this.project = project;
    this.taskId = taskId;
    this.description = description;
    this.estimate = estimate;
    this.rate = rate;

    Calendar cal = new GregorianCalendar(dueYear, dueMonth, dueDay);
    this.due = cal.getTime();
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public String getProject() {
    return project;
  }

  public void setProject(String project) {
    this.project = project;
  }

  public int getTaskId() {
    return taskId;
  }

  public void setTaskId(int taskId) {
    this.taskId = taskId;
  }

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

}
