package ch.ralscha.extdirectspring.bean;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.StringFilter;
public class ExtDirectStoreReadRequestTest {

	@Test
	public void testSetFilter() {
		ExtDirectStoreReadRequest request = new ExtDirectStoreReadRequest();
		assertThat(request.getFilters()).isEmpty();
		
		request.setFilters(null);
		assertThat(request.getFilters()).isEmpty();
		
		StringFilter sf = new StringFilter("field", "10");
		List<Filter> filters = new ArrayList<Filter>();
		filters.add(sf);
		request.setFilters(filters);
		assertThat(request.getFilters()).hasSize(1).contains(sf);
	}

}
