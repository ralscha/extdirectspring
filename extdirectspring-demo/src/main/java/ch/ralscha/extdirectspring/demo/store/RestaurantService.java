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
package ch.ralscha.extdirectspring.demo.store;

import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.GroupInfo;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.demo.util.PropertyOrderingFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@Service
public class RestaurantService {
	private final static List<Restaurant> restaurants;

	static {

		ImmutableList.Builder<Restaurant> builder = new ImmutableList.Builder<Restaurant>();

		builder.add(new Restaurant("Cheesecake Factory", "American"));
		builder.add(new Restaurant("University Cafe", "American"));
		builder.add(new Restaurant("Slider Bar", "American"));
		builder.add(new Restaurant("Shokolaat", "American"));
		builder.add(new Restaurant("Gordon Biersch", "American"));
		builder.add(new Restaurant("Crepevine", "American"));
		builder.add(new Restaurant("Creamery", "American"));
		builder.add(new Restaurant("Old Pro", "American"));
		builder.add(new Restaurant("Nola's", "Cajun"));
		builder.add(new Restaurant("House of Bagels", "Bagels"));
		builder.add(new Restaurant("The Prolific Oven", "Sandwiches"));
		builder.add(new Restaurant("La Strada", "Italian"));
		builder.add(new Restaurant("Buca di Beppo", "Italian"));
		builder.add(new Restaurant("Pasta", "Italian"));
		builder.add(new Restaurant("The Prolific Oven", "Sandwiches"));
		builder.add(new Restaurant("Madame Tam", "Asian"));
		builder.add(new Restaurant("Sprout Cafe", "Salad"));
		builder.add(new Restaurant("Pluto's", "Salad"));
		builder.add(new Restaurant("Junoon", "Indian"));
		builder.add(new Restaurant("Bistro Maxine", "French"));
		builder.add(new Restaurant("Three Seasons", "Vietnamese"));
		builder.add(new Restaurant("Sancho's Taquira", "Mexican"));
		builder.add(new Restaurant("Reposado", "Mexican"));
		builder.add(new Restaurant("Siam Royal", "Thai"));
		builder.add(new Restaurant("Krung Siam", "Thai"));
		builder.add(new Restaurant("Thaiphoon", "Thai"));
		builder.add(new Restaurant("Tamarine", "Vietnamese"));
		builder.add(new Restaurant("Joya", "Tapas"));
		builder.add(new Restaurant("Jing Jing", "Chinese"));
		builder.add(new Restaurant("Patxi's Pizza", "Pizza"));
		builder.add(new Restaurant("Evvia Estiatorio", "Mediterranean"));
		builder.add(new Restaurant("Cafe 220", "Mediterranean"));
		builder.add(new Restaurant("Cafe Renaissance", "Mediterranean"));
		builder.add(new Restaurant("Kan Zeman", "Mediterranean"));
		builder.add(new Restaurant("Gyros-Gyros", "Mediterranean"));
		builder.add(new Restaurant("Mango Caribbean Cafe", "Caribbean"));
		builder.add(new Restaurant("Coconuts Caribbean Restaurant & Bar", "Caribbean"));
		builder.add(new Restaurant("Rose & Crown", "English"));
		builder.add(new Restaurant("Baklava", "Mediterranean"));
		builder.add(new Restaurant("Mandarin Gourmet", "Chinese"));
		builder.add(new Restaurant("Bangkok Cuisine", "Thai"));
		builder.add(new Restaurant("Darbar Indian Cuisine", "Indian"));
		builder.add(new Restaurant("Mantra", "Indian"));
		builder.add(new Restaurant("Janta", "Indian"));
		builder.add(new Restaurant("Hyderabad House", "Indian"));
		builder.add(new Restaurant("Starbucks", "Coffee"));
		builder.add(new Restaurant("Peet's Coffee", "Coffee"));
		builder.add(new Restaurant("Coupa Cafe", "Coffee"));
		builder.add(new Restaurant("Lytton Coffee Company", "Coffee"));
		builder.add(new Restaurant("Il Fornaio", "Italian"));
		builder.add(new Restaurant("Lavanda", "Mediterranean"));
		builder.add(new Restaurant("MacArthur Park", "American"));
		builder.add(new Restaurant("St Michael's Alley", "Californian"));
		builder.add(new Restaurant("Cafe Renzo", "Italian"));
		builder.add(new Restaurant("Osteria", "Italian"));
		builder.add(new Restaurant("Vero", "Italian"));
		builder.add(new Restaurant("Cafe Renzo", "Italian"));
		builder.add(new Restaurant("Miyake", "Sushi"));
		builder.add(new Restaurant("Sushi Tomo", "Sushi"));
		builder.add(new Restaurant("Kanpai", "Sushi"));
		builder.add(new Restaurant("Pizza My Heart", "Pizza"));
		builder.add(new Restaurant("New York Pizza", "Pizza"));
		builder.add(new Restaurant("California Pizza Kitchen", "Pizza"));
		builder.add(new Restaurant("Round Table", "Pizza"));
		builder.add(new Restaurant("Loving Hut", "Vegan"));
		builder.add(new Restaurant("Garden Fresh", "Vegan"));
		builder.add(new Restaurant("Cafe Epi", "French"));
		builder.add(new Restaurant("Tai Pan", "Chinese"));

		restaurants = builder.build();
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "grouping")
	public List<Restaurant> getRestaurants(ExtDirectStoreReadRequest request) {

		if (!request.getGroups().isEmpty()) {

			GroupInfo groupInfo = request.getGroups().iterator().next();

			if (!request.getSorters().isEmpty()) {
				for (SortInfo sortInfo : request.getSorters()) {
					if (groupInfo.getProperty().equals(sortInfo.getProperty())) {
						groupInfo = new GroupInfo(groupInfo.getProperty(), sortInfo.getDirection());
					}
				}
			}

			Ordering<Restaurant> ordering = PropertyOrderingFactory.INSTANCE.createOrderingFromGroups(Lists.newArrayList(groupInfo));
			Ordering<Restaurant> sortOrdering = PropertyOrderingFactory.INSTANCE.createOrderingFromSorters(request.getSorters());

			if (sortOrdering != null) {
				ordering = ordering.compound(sortOrdering);
			}

			if (ordering != null) {
				return ordering.sortedCopy(restaurants);
			}
		}

		if (!request.getSorters().isEmpty()) {
			Ordering<Restaurant> ordering = PropertyOrderingFactory.INSTANCE.createOrderingFromSorters(request.getSorters());
			if (ordering != null) {
				return ordering.sortedCopy(restaurants);
			}
		}

		return restaurants;

	}
}
