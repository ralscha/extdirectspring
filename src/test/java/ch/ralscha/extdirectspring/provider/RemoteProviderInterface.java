package ch.ralscha.extdirectspring.provider;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;

public interface RemoteProviderInterface {

	@ExtDirectMethod(group = "interface")
	String method2();

	@ExtDirectMethod(group = "interface")
	String method3(long i, Double d, String s);

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "interface")
	List<Row> storeRead(ExtDirectStoreReadRequest request, String name, Integer age, Boolean active, HttpServletRequest httpRequest);

}
