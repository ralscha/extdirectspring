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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

public class SiteInfo {
	
	private static final Random rnd = new Random();
	
	private long dateInMillis;
	private int visits;
	private int views;
	private int users;
	
	
	public SiteInfo(long dateInMillis) {
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(dateInMillis);
		
		this.dateInMillis = dateInMillis;
		this.visits = rnd.nextInt();
		this.views = rnd.nextInt();
		this.users = rnd.nextInt();
	}


	public static Random getRnd() {
		return rnd;
	}


	public long getDateInMillis() {
		return dateInMillis;
	}


	public int getVisits() {
		return visits;
	}


	public int getViews() {
		return views;
	}


	public int getUsers() {
		return users;
	}
	
}
