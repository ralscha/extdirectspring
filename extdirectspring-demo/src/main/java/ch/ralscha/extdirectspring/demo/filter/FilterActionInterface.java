package ch.ralscha.extdirectspring.demo.filter;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

public interface FilterActionInterface {
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "filter")
	ExtDirectStoreResponse<Company> load(ExtDirectStoreReadRequest request, String dRif);
}