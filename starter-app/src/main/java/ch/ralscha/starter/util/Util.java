package ch.ralscha.starter.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;

import com.google.common.collect.Lists;

public class Util {

	private Util() {
		//do not instantiate this class
	}

	public static Pageable createPageRequest(ExtDirectStoreReadRequest request) {
		return createPageRequest(request, Collections.<String, String> emptyMap());
	}

	public static Pageable createPageRequest(ExtDirectStoreReadRequest request, Map<String, String> mapGuiColumn2Dbfield) {

		List<Order> orders = Lists.newArrayList();
		for (SortInfo sortInfo : request.getSorters()) {

			String property = mapGuiColumn2Dbfield.get(sortInfo.getProperty());
			if (property == null) {
				property = sortInfo.getProperty();
			}

			if (sortInfo.getDirection() == SortDirection.ASCENDING) {
				orders.add(new Order(Direction.ASC, property));
			} else {
				orders.add(new Order(Direction.DESC, property));
			}
		}

		int page = Math.max(request.getPage() - 1, 0);

		if (orders.isEmpty()) {
			return new PageRequest(page, request.getLimit());
		}

		Sort sort = new Sort(orders);
		return new PageRequest(page, request.getLimit(), sort);

	}

}
