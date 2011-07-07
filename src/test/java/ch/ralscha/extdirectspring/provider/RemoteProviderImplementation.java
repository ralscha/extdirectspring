package ch.ralscha.extdirectspring.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;

@Service
public class RemoteProviderImplementation implements RemoteProviderInterface {

	public String method2() {
		return "method2() called";
	}

	public String method3(long i, Double d, String s) {
		return String.format("method3() called-%d-%.1f-%s", i, d, s);
	}

	public List<Row> storeRead(ExtDirectStoreReadRequest request, @RequestParam(value = "lastName") String name,
			@RequestParam(value = "theAge", defaultValue="40") Integer age, Boolean active, HttpServletRequest httpRequest) {

		assertEquals(40, age.intValue());
		assertNotNull(httpRequest);
		assertNotNull(request);
		assertEquals("Smith", name);
		assertTrue(active);
		
		List<Row> result = new ArrayList<Row>();
		result.add(new Row(1, name, active, ""+age));
		return result;
	}

}
