/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.generic;

import static ch.ralscha.extdirectspring.annotation.ExtDirectMethodType.STORE_MODIFY;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;

public abstract class BaseOptionService<T extends BaseOption> {

	@ExtDirectMethod(value = STORE_MODIFY, group = "generic")
	@Transactional
	public T createOne(T newEntity) {
		assertThat(newEntity.getId()).isNull();
		assertThat(newEntity).isNotNull().isInstanceOf(Color.class);
		newEntity.setId(1L);
		return newEntity;
	}

	@ExtDirectMethod(value = STORE_MODIFY, group = "generic")
	@Transactional
	public List<T> createMultiple(List<T> newEntities) {
		long i = 1;
		for (T newEntity : newEntities) {
			assertThat(newEntity.getId()).isNull();
			assertThat(newEntity).isNotNull().isInstanceOf(Color.class);
			newEntity.setId(i++);
		}
		return newEntities;
	}

	@ExtDirectMethod(value = STORE_MODIFY, group = "generic")
	@Transactional
	public void destroyOne(T destroyEntity) {
		assertThat(destroyEntity.getId()).isEqualTo(1L);
		assertThat(destroyEntity).isNotNull().isInstanceOf(Color.class);
	}

	@ExtDirectMethod(value = STORE_MODIFY, group = "generic")
	@Transactional
	public void destroyMultiple(List<T> destroyEntities) {
		long i = 1;
		for (T destroyEntity : destroyEntities) {
			assertThat(destroyEntity.getId()).isEqualTo(i++);
			assertThat(destroyEntity).isNotNull().isInstanceOf(Color.class);
		}
	}

	@ExtDirectMethod(value = STORE_MODIFY, group = "generic")
	@Transactional
	public T updateOne(T updatedEntity) {
		assertThat(updatedEntity.getId()).isEqualTo(1L);
		assertThat(updatedEntity).isNotNull().isInstanceOf(Color.class);
		updatedEntity.setShortName(updatedEntity.getShortName().toUpperCase());
		updatedEntity.setLongName(updatedEntity.getLongName().toUpperCase());
		return updatedEntity;
	}

	@ExtDirectMethod(value = STORE_MODIFY, group = "generic")
	@Transactional
	public List<T> updateMultiple(List<T> updatedEntities) {
		long i = 1;
		for (T updatedEntity : updatedEntities) {
			assertThat(updatedEntity.getId()).isEqualTo(i++);
			assertThat(updatedEntity).isNotNull().isInstanceOf(Color.class);
			updatedEntity.setShortName(updatedEntity.getShortName().toUpperCase());
			updatedEntity.setLongName(updatedEntity.getLongName().toUpperCase());
		}
		return updatedEntities;
	}

	@ExtDirectMethod(group = "generic")
	public String simpleMethod(T entity, List<T> more) {
		assertThat(entity.getId()).isEqualTo(1L);
		assertThat(entity).isNotNull().isInstanceOf(Color.class);
		for (T t : more) {
			assertThat(t.getId()).isEqualTo(1L);
			assertThat(t).isNotNull().isInstanceOf(Color.class);
		}
		return entity.getId() + entity.getLongName() + entity.getShortName() + ";" + more.size();
	}

}
