/**
 * Copyright 2010-2016 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

import ch.ralscha.extdirectspring.provider.FormInfoController;
import ch.ralscha.extdirectspring.provider.FormInfoController2;
import ch.ralscha.extdirectspring.provider.FormInfoController3;
import ch.ralscha.extdirectspring.provider.PollProvider;
import ch.ralscha.extdirectspring.provider.RemoteProviderFormLoad;
import ch.ralscha.extdirectspring.provider.RemoteProviderSimple;
import ch.ralscha.extdirectspring.provider.RemoteProviderSimpleNamed;
import ch.ralscha.extdirectspring.provider.RemoteProviderStoreModify;
import ch.ralscha.extdirectspring.provider.RemoteProviderStoreRead;
import ch.ralscha.extdirectspring.provider.RemoteProviderTreeLoad;
import ch.ralscha.extdirectspring.provider.UploadService;
import ch.ralscha.extdirectspring.provider.WrongFormPostController;

public class ExtDirectMethodTypeTest {

	@Test
	public void testSimpleIsValid() throws SecurityException {
		assertThat(ExtDirectMethodType.SIMPLE.isValid("remoteProviderSimple.method1",
				RemoteProviderSimple.class,
				findMethod(RemoteProviderSimple.class, "method1"))).isTrue();
		assertThat(ExtDirectMethodType.SIMPLE.isValid("remoteProviderSimple.method2",
				RemoteProviderSimple.class,
				findMethod(RemoteProviderSimple.class, "method2"))).isTrue();
		assertThat(ExtDirectMethodType.SIMPLE.isValid("remoteProviderSimple.method3",
				RemoteProviderSimple.class,
				findMethod(RemoteProviderSimple.class, "method3"))).isTrue();
		assertThat(ExtDirectMethodType.SIMPLE.isValid(
				"remoteProviderSimple.method3WithError", RemoteProviderSimple.class,
				findMethod(RemoteProviderSimple.class, "method3WithError"))).isFalse();
	}

	@Test
	public void testSimpleNamedIsValid() throws SecurityException {
		for (Method method : RemoteProviderSimpleNamed.class.getMethods()) {
			if (method.getName().startsWith("method")) {
				assertThat(ExtDirectMethodType.SIMPLE_NAMED.isValid(
						"remoteProviderSimpleNamed." + method.getName(),
						RemoteProviderSimpleNamed.class, method)).isTrue();
			}
		}
	}

	@Test
	public void testFormLoadIsValid() throws SecurityException {
		for (Method method : RemoteProviderFormLoad.class.getMethods()) {
			if (method.getName().startsWith("method")) {
				assertThat(ExtDirectMethodType.FORM_LOAD.isValid(
						"remoteProviderFormLoad." + method.getName(),
						RemoteProviderFormLoad.class, method)).isTrue();
			}
		}
	}

	@Test
	public void testStoreReadIsValid() throws SecurityException {
		for (Method method : RemoteProviderStoreRead.class.getMethods()) {
			if (method.getName().startsWith("method")) {
				assertThat(ExtDirectMethodType.STORE_READ.isValid(
						"remoteProviderStoreRead." + method.getName(),
						RemoteProviderStoreRead.class, method)).isTrue();
			}
		}
	}

	@Test
	public void testStoreModifyIsValid() throws SecurityException {
		for (Method method : RemoteProviderStoreModify.class.getMethods()) {
			if (method.getName().startsWith("create")
					|| method.getName().startsWith("update")
					|| method.getName().startsWith("destroy")) {
				assertThat(ExtDirectMethodType.STORE_MODIFY.isValid(
						"remoteProviderStoreModify." + method.getName(),
						RemoteProviderStoreModify.class, method)).isTrue();
			}
		}

	}

	@Test
	public void testFormPostIsValid() throws SecurityException {
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"wrongFormPostController.updateInfo1", WrongFormPostController.class,
				findMethod(WrongFormPostController.class, "updateInfo1"))).isFalse();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"wrongFormPostController.updateInfo2", WrongFormPostController.class,
				findMethod(WrongFormPostController.class, "updateInfo2"))).isFalse();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"wrongFormPostController.updateInfo3", WrongFormPostController.class,
				findMethod(WrongFormPostController.class, "updateInfo3"))).isFalse();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"wrongFormPostController.updateInfo4", WrongFormPostController.class,
				findMethod(WrongFormPostController.class, "updateInfo4"))).isFalse();

		assertThat(ExtDirectMethodType.FORM_POST.isValid("uploadService.upload",
				UploadService.class, findMethod(UploadService.class, "upload"))).isTrue();
		assertThat(ExtDirectMethodType.FORM_POST.isValid("uploadService.uploadEd",
				UploadService.class, findMethod(UploadService.class, "uploadEd")))
						.isTrue();

		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController2.updateInfo1", FormInfoController2.class,
				findMethod(FormInfoController2.class, "updateInfo1"))).isTrue();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController2.updateInfo2", FormInfoController2.class,
				findMethod(FormInfoController2.class, "updateInfo2"))).isTrue();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController2.invalidMethod", FormInfoController2.class,
				findMethod(FormInfoController2.class, "invalidMethod"))).isFalse();

		assertThat(ExtDirectMethodType.FORM_POST.isValid("formInfoController.updateInfo",
				FormInfoController.class,
				findMethod(FormInfoController.class, "updateInfo"))).isTrue();
		assertThat(ExtDirectMethodType.FORM_POST.isValid("formInfoController.upload",
				FormInfoController.class, findMethod(FormInfoController.class, "upload")))
						.isTrue();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController.invalidMethod1", FormInfoController.class,
				findMethod(FormInfoController.class, "invalidMethod1"))).isFalse();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController.invalidMethod2", FormInfoController.class,
				findMethod(FormInfoController.class, "invalidMethod2"))).isFalse();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController.updateInfoDirect", FormInfoController.class,
				findMethod(FormInfoController.class, "updateInfoDirect"))).isTrue();
		assertThat(ExtDirectMethodType.FORM_POST.isValid(
				"formInfoController.updateInfoDirectEd", FormInfoController.class,
				findMethod(FormInfoController.class, "updateInfoDirectEd"))).isTrue();
	}

	@Test
	public void testTreeLoadIsValid() throws SecurityException {
		for (Method method : RemoteProviderTreeLoad.class.getMethods()) {
			if (method.getName().startsWith("method")) {
				assertThat(ExtDirectMethodType.TREE_LOAD.isValid(
						"remoteProviderTreeLoad." + method.getName(),
						RemoteProviderTreeLoad.class, method)).isTrue();
			}
		}
	}

	@Test
	public void testPollIsValid() throws SecurityException {
		for (Method method : PollProvider.class.getMethods()) {
			if (method.getName().startsWith("message")
					|| method.getName().startsWith("handleMessage")) {
				assertThat(ExtDirectMethodType.POLL.isValid(
						"pollProvider." + method.getName(), PollProvider.class, method))
								.isTrue();
			}
		}
	}

	@Test
	public void testFormPostJsonIsValid() throws SecurityException {
		assertThat(ExtDirectMethodType.FORM_POST_JSON.isValid(
				"formInfoController3.updateInfoJson", FormInfoController3.class,
				findMethod(FormInfoController3.class, "updateInfoJson"))).isTrue();

		assertThat(ExtDirectMethodType.FORM_POST_JSON.isValid(
				"formInfoController3.updateInfoJsonDirect", FormInfoController3.class,
				findMethod(FormInfoController3.class, "updateInfoJsonDirect"))).isTrue();

		assertThat(ExtDirectMethodType.FORM_POST_JSON.isValid(
				"formInfoController3.updateInfoJsonDirectError",
				FormInfoController3.class,
				findMethod(FormInfoController3.class, "updateInfoJsonDirectError")))
						.isTrue();

		assertThat(ExtDirectMethodType.FORM_POST_JSON.isValid(
				"formInfoController3.updateInfoJsonDirectNotRegisteredWithBindingResultAsParameter",
				FormInfoController3.class,
				findMethod(FormInfoController3.class,
						"updateInfoJsonDirectNotRegisteredWithBindingResultAsParameter")))
								.isFalse();

		assertThat(ExtDirectMethodType.FORM_POST_JSON.isValid(
				"formInfoController3.updateInfoJsonDirectNotRegisteredWithMultipartFileAsParameter",
				FormInfoController3.class,
				findMethod(FormInfoController3.class,
						"updateInfoJsonDirectNotRegisteredWithMultipartFileAsParameter")))
								.isFalse();

	}

	private static Method findMethod(Class<?> clazz, String methodName) {
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				return method;
			}
		}

		return null;
	}

}
