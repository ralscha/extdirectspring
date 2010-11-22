/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
