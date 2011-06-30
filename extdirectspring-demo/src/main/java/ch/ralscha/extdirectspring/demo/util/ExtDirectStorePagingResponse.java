package ch.ralscha.extdirectspring.demo.util;

import java.util.Collection;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

public class ExtDirectStorePagingResponse<T> extends ExtDirectStoreResponse<T> {

	public ExtDirectStorePagingResponse(final ExtDirectStoreReadRequest request,  final Collection<T> allRecords) {
				
		int totalSize = allRecords.size();				
		Collection<T> records = allRecords;
		
		Ordering<T> ordering = PropertyOrderingFactory.INSTANCE.createOrderingFromSorters(request.getSorters());
		if (ordering != null) {
			records = ordering.sortedCopy(records);
		}

		if (request.getPage() != null && request.getLimit() != null) {
			int start = (request.getPage() - 1) * request.getLimit();
			int end = Math.min(totalSize, start + request.getLimit());
			records = Lists.newArrayList(records).subList(start, Math.min(totalSize, end));
		} 
		
		init(totalSize, records, true);
	}

}
