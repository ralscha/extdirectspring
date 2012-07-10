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
package ch.ralscha.extdirectspring.util;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Tests for {@link ExtDirectSpringUtil}.
 * 
 * @author Ralph Schaer
 */
public class ExtDirectSpringUtilTest {

	@Test
	public void testEqual() {
		assertThat(ExtDirectSpringUtil.equal(1, 1)).isTrue();
		assertThat(ExtDirectSpringUtil.equal(1, 2)).isFalse();

		assertThat(ExtDirectSpringUtil.equal(true, true)).isTrue();
		assertThat(ExtDirectSpringUtil.equal(false, false)).isTrue();

		assertThat(ExtDirectSpringUtil.equal(true, false)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(false, true)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(false, null)).isFalse();

		assertThat(ExtDirectSpringUtil.equal("a", "a")).isTrue();
		assertThat(ExtDirectSpringUtil.equal("a", "b")).isFalse();
		assertThat(ExtDirectSpringUtil.equal(null, "a")).isFalse();
		assertThat(ExtDirectSpringUtil.equal("a", null)).isFalse();
		assertThat(ExtDirectSpringUtil.equal(null, null)).isTrue();
	}

}
