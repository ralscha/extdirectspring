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
package ch.ralscha.extdirectspring.demo.group;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;

import com.google.common.collect.ImmutableList;

@Service
public class GroupAction {

	private static List<Task> tasks;

	static {
		ImmutableList.Builder<Task> builder = new ImmutableList.Builder<Task>();
		builder.add(new Task(100, "Ext Forms: Field Anchoring", 112, "Integrate 2.0 Forms with 2.0 Layouts",
				new BigDecimal(6), new BigDecimal(150), 2010, 6, 24));
		builder.add(new Task(100, "Ext Forms: Field Anchoring", 113, "Implement AnchorLayout", new BigDecimal(4),
				new BigDecimal(150), 2010, 6, 25));
		builder.add(new Task(100, "Ext Forms: Field Anchoring", 114, "Add support for multiple types of anchors",
				new BigDecimal(4), new BigDecimal(150), 2010, 6, 27));
		builder.add(new Task(100, "Ext Forms: Field Anchoring", 115, "Testing and debugging", new BigDecimal(8),
				new BigDecimal(0), 2010, 6, 29));
		builder.add(new Task(101, "Ext Grid: Single-level Grouping", 101, "Add required rendering 'hooks' to GridView",
				new BigDecimal(6), new BigDecimal(100), 2010, 7, 1));
		builder.add(new Task(101, "Ext Grid: Single-level Grouping", 102,
				"Extend GridView and override rendering functions", new BigDecimal(6), new BigDecimal(100), 2010, 7, 3));
		builder.add(new Task(101, "Ext Grid: Single-level Grouping", 103, "Extend Store with grouping functionality",
				new BigDecimal(4), new BigDecimal(100), 2010, 7, 4));
		builder.add(new Task(101, "Ext Grid: Single-level Grouping", 121, "Default CSS Styling", new BigDecimal(2),
				new BigDecimal(100), 2010, 7, 5));
		builder.add(new Task(101, "Ext Grid: Single-level Grouping", 104, "Testing and debugging", new BigDecimal(6),
				new BigDecimal(100), 2010, 7, 6));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 105, "Ext Grid plugin integration", new BigDecimal(4),
				new BigDecimal(125), 2010, 7, 1));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 106, "Summary creation during rendering phase",
				new BigDecimal(4), new BigDecimal(125), 2010, 7, 2));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 107, "Dynamic summary updates in editor grids",
				new BigDecimal(6), new BigDecimal(125), 2010, 7, 5));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 108, "Remote summary integration", new BigDecimal(4),
				new BigDecimal(125), 2010, 7, 5));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 109, "Summary renderers and calculators",
				new BigDecimal(4), new BigDecimal(125), 2010, 7, 6));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 110, "Integrate summaries with GroupingView",
				new BigDecimal(10), new BigDecimal(125), 2010, 7, 11));
		builder.add(new Task(102, "Ext Grid: Summary Rows", 111, "Testing and debugging", new BigDecimal(8),
				new BigDecimal(125), 2010, 7, 15));

		tasks = builder.build();

	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "total")
	public List<Task> load(ExtDirectStoreReadRequest request) {
		return tasks;
	}

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "hybrid")
	public List<Task> loadHybrid(ExtDirectStoreReadRequest request) {
		return tasks;
	}

	@ExtDirectMethod(group = "hybrid")
	public Summary updateSummary(String groupValue) {
		Summary summary = new Summary();
		summary.setDescription("22");
		summary.setEstimate(new BigDecimal(888));
		summary.setRate(new BigDecimal(999));
		summary.setDue(new Date());
		summary.setCost(new BigDecimal(8));
		return summary;
	}
}
