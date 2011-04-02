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
