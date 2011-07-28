#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ${package}.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.domain.Persistable;

/**
 * Abstract base class for entities. Allows parameterization of id type, chooses
 * auto-generation and implements {@link ${symbol_pound}equals(Object)} and
 * {@link ${symbol_pound}hashCode()} based on that id.
 * 
 * @author Oliver Gierke
 * @param <PK> the the of the entity
 */
@MappedSuperclass
public abstract class AbstractPersistable<PK extends Serializable> implements Persistable<PK> {

	private static final long serialVersionUID = -5554308939380869754L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private PK id;

	@Override
	public PK getId() {
		return id;
	}

	public void setId(final PK id) {
		this.id = id;
	}

	@Override
	@JsonIgnore
	public boolean isNew() {
		return null == getId();
	}

	@Override
	public String toString() {
		return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!getClass().equals(obj.getClass())) {
			return false;
		}

		AbstractPersistable<?> that = (AbstractPersistable<?>) obj;

		return null == this.getId() ? false : this.getId().equals(that.getId());
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode += null == getId() ? 0 : getId().hashCode() * 31;
		return hashCode;
	}
}