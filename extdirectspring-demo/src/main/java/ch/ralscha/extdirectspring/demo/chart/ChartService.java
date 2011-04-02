/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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
package ch.ralscha.extdirectspring.demo.chart;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.inject.Named;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.Lists;

@Named
public class ChartService {

	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "area")
	public List<AreaData> getAreaData() {

		List<AreaData> result = Lists.newArrayList();
		
		String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
		
		for (String month : months) {
			result.add(new AreaData(month));
		}
		
		return result;
	}
	
	
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "live")
	public List<SiteInfo> getSiteInfo() {
		
		List<SiteInfo> siteInfo = Lists.newArrayList();
		
		Calendar today = Calendar.getInstance();
		siteInfo.add(new SiteInfo(today.getTimeInMillis()));
		
		return siteInfo;
		
	}
}
