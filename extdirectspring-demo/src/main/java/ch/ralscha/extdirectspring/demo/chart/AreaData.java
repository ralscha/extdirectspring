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

import java.util.Random;

public class AreaData {
	
	private static Random rnd = new Random();
	
	private String name;
	private int data1;
	private int data2;
	private int data3;
	private int data4;
	private int data5;
	private int data6;
	private int data7;
	private int data8;
	private int data9;
	
	public AreaData(String name) {
		this.name = name;
				
		data1 = rnd.nextInt(100) + 1;
		data2 = rnd.nextInt(100) + 1;
		data3 = rnd.nextInt(100) + 1;
		data4 = rnd.nextInt(100) + 1;
		data5 = rnd.nextInt(100) + 1;
		data6 = rnd.nextInt(100) + 1;
		data7 = rnd.nextInt(100) + 1;
		data8 = rnd.nextInt(100) + 1;
		data9 = rnd.nextInt(100) + 1;		
	}
	
	public String getName() {
		return name;
	}
	public int getData1() {
		return data1;
	}
	public int getData2() {
		return data2;
	}
	public int getData3() {
		return data3;
	}
	public int getData4() {
		return data4;
	}
	public int getData5() {
		return data5;
	}
	public int getData6() {
		return data6;
	}
	public int getData7() {
		return data7;
	}
	public int getData8() {
		return data8;
	}
	public int getData9() {
		return data9;
	}
	
	
	
}
