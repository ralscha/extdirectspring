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
package ch.ralscha.extdirectspring.demo.pivot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.ImmutableList;

@Named
public class PivotDataBean {

	@Inject
	private Resource pivotdata;

	private List<Sale> sales;

	@PostConstruct
	public void readData() throws IOException {
		ImmutableList.Builder<Sale> builder = ImmutableList.builder();

		InputStream is = pivotdata.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		CSVReader reader = new CSVReader(br, '|');
		String[] nextLine;
		while ((nextLine = reader.readNext()) != null) {
			builder.add(new Sale(nextLine));
		}

		br.close();
		is.close();

		sales = builder.build();
	}

	public List<Sale> getSalesData() {
		return sales;
	}

}
