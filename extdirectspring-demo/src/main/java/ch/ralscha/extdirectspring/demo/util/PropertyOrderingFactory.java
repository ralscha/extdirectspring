package ch.ralscha.extdirectspring.demo.util;

import java.util.Collection;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;

import com.google.common.collect.Ordering;

public enum PropertyOrderingFactory {
	INSTANCE;

	private final SpelExpressionParser parser = new SpelExpressionParser();
	
	public <T> Ordering<T> createOrdering(String propertyName, SortDirection sortDirection) {
		Expression readPropertyExpression = parser.parseExpression(propertyName);
		Ordering<T> ordering = new PropertyOrdering<T>(readPropertyExpression);
		
		if (sortDirection == SortDirection.DESCENDING) {
			ordering = ordering.reverse();
		}
		
		return ordering;
	}
	
	
	public <T> Ordering<T> createOrdering(Collection<SortInfo> sortInfos) {
		Ordering<T> ordering = null;
		
		if (sortInfos != null) {
			for (SortInfo sorter : sortInfos) {
				Ordering<T> propertyOrdering = createOrdering(sorter.getProperty(), sorter.getDirection());
				if (ordering == null) {
					ordering = propertyOrdering;
				} else {
					ordering = ordering.compound(propertyOrdering);
				}			
			}	
		}
		
		return ordering;
	}

	
}
