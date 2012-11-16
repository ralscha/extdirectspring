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
package ch.ralscha.extdirectspring.generator;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ModelAssociationBeanTest {

	@Test(expected = IllegalArgumentException.class)
	public void hasManySetSetterName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_MANY, "User");
		mab.setSetterName("dummy");
	}

	@Test
	public void hasOneSetSetterName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_ONE, "User");
		mab.setSetterName("dummy");
		assertThat(mab.getSetterName()).isEqualTo("dummy");
	}

	@Test
	public void belongsToSetSetterName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.BELONGS_TO, "User");
		mab.setSetterName("dummy");
		assertThat(mab.getSetterName()).isEqualTo("dummy");
	}

	@Test(expected = IllegalArgumentException.class)
	public void hasManySetGetterName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_MANY, "User");
		mab.setGetterName("dummy");
	}

	@Test
	public void hasOneSetGetterName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_ONE, "User");
		mab.setGetterName("dummy");
		assertThat(mab.getGetterName()).isEqualTo("dummy");
	}

	@Test
	public void belongsToSetGetterName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.BELONGS_TO, "User");
		mab.setGetterName("dummy");
		assertThat(mab.getGetterName()).isEqualTo("dummy");
	}

	@Test(expected = IllegalArgumentException.class)
	public void hasOneSetName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_ONE, "User");
		mab.setName("dummy");
	}

	@Test(expected = IllegalArgumentException.class)
	public void belongsToSetName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.BELONGS_TO, "User");
		mab.setName("dummy");
	}

	@Test
	public void hasManySetName() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_MANY, "User");
		mab.setName("dummy");
		assertThat(mab.getName()).isEqualTo("dummy");
	}

	@Test(expected = IllegalArgumentException.class)
	public void hasOneSetAutoLoad() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_ONE, "User");
		mab.setAutoLoad(true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void belongsToAutoLoad() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.BELONGS_TO, "User");
		mab.setAutoLoad(true);
	}

	@Test
	public void hasManyAutoLoad() {
		ModelAssociationBean mab = new ModelAssociationBean(ModelAssociationType.HAS_MANY, "User");
		mab.setAutoLoad(true);
		assertThat(mab.getAutoLoad()).isTrue();
	}
}
