/**
 * Copyright 2010-2012 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.demo.touch;

import java.util.Date;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import ch.ralscha.extdirectspring.demo.util.DMYDateSerializer;

public class Note {
	private Integer id;

	private Date dateCreated;

	private String title;

	private String narrative;

	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	@JsonSerialize(using = DMYDateSerializer.class)
	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(final Date date) {
		this.dateCreated = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public String getNarrative() {
		return narrative;
	}

	public void setNarrative(final String narrative) {
		this.narrative = narrative;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Note other = (Note) obj;
		if (id != other.id) {
			return false;
		}
		return true;
	}

}
