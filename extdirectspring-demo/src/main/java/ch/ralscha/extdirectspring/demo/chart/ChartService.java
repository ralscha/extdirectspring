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
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Service;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

import com.google.common.collect.Lists;

@Service
public class ChartService {

	private static final Random rnd = new Random();
	
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "area")
	public List<AreaData> getAreaData() {

		List<AreaData> result = Lists.newArrayList();
		
		String[] months = DateFormatSymbols.getInstance(Locale.ENGLISH).getMonths();
		
		for (String month : months) {
			result.add(new AreaData(month));
		}
		
		return result;
	}
	
	
	@ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "live", synchronizeOnSession=true)
	public List<SiteInfo> getSiteInfo(HttpSession session) {
		
		@SuppressWarnings("unchecked")
		List<SiteInfo> siteInfo = (List<SiteInfo>)session.getAttribute("siteInfos");
		if (siteInfo == null) {
			siteInfo = Lists.newArrayList();
			session.setAttribute("siteInfos", siteInfo);
			
			LocalDate ld = new LocalDate(2011, 1, 1);
			siteInfo.add(new SiteInfo(ld, rnd.nextInt(100)+1, rnd.nextInt(100)+1, rnd.nextInt(100)+1));			
		} else {
			SiteInfo lastSiteInfo = siteInfo.get(siteInfo.size()-1);
			
			LocalDate nextDate = lastSiteInfo.getDate().plusDays(1);
			int nextVisits = Math.min(100, Math.max((int)(lastSiteInfo.getVisits() + (rnd.nextDouble() - 0.5) * 20), 0));
			int nextViews = Math.min(100, Math.max((int)(lastSiteInfo.getViews() + (rnd.nextDouble() - 0.5) * 10), 0));
			int nextVeins = Math.min(100, Math.max((int)(lastSiteInfo.getVeins() + (rnd.nextDouble() - 0.5) * 20), 0));
			siteInfo.add(new SiteInfo(nextDate, nextVisits, nextViews, nextVeins));

			if (siteInfo.size() > 7) {
				siteInfo.remove(0);
			}
		}

		return siteInfo;		
	}
}
