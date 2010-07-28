/**
 * Copyright 2010 Ralph Schaer <ralphschaer@gmail.com>
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

package ch.ralscha.extdirectspring.demo.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.core.io.Resource;

import au.com.bytecode.opencsv.CSVReader;
import ch.ralscha.extdirectspring.filter.BooleanFilter;
import ch.ralscha.extdirectspring.filter.Filter;
import ch.ralscha.extdirectspring.filter.StringFilter;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Named
public class CompanyDataBean {

  @Inject
  private Resource randomdata;

  private Map<Integer, Company> companies;

  @PostConstruct
  public void readData() throws IOException {
    Random rand = new Random();

    companies = Maps.newHashMap();
    InputStream is = randomdata.getInputStream();

    BufferedReader br = new BufferedReader(new InputStreamReader(is));

    CSVReader reader = new CSVReader(br, '|');
    String[] nextLine;
    while ((nextLine = reader.readNext()) != null) {
      Company company = new Company();
      company.setId(Integer.parseInt(nextLine[0]));
      company.setCompany(nextLine[2]);

      company.setDate(new GregorianCalendar(rand.nextInt(50) + 1950, rand.nextInt(12), rand.nextInt(28)).getTime());
      company.setPrice(new BigDecimal(rand.nextFloat() * 100));
      company.setSize(SizeEnum.values()[rand.nextInt(4)]);
      company.setVisible(rand.nextBoolean());

      companies.put(company.getId(), company);
    }

    br.close();
    is.close();
  }

  public List<Company> findAllCompanies() {
    return ImmutableList.copyOf(companies.values());
  }

  public List<Company> findCompanies(List<Filter> filters) {

    List<Predicate<Company>> predicates = Lists.newArrayList();
    for (Filter filter : filters) {
      if (filter.getField().equals("company")) {
        predicates.add(new CompanyPredicate(((StringFilter)filter).getValue()));
      } else if (filter.getField().equals("visible")) {
        predicates.add(new VisiblePredicate(((BooleanFilter)filter).getValue()));
      }

    }

    Iterable<Company> filtered = Iterables.filter(companies.values(), Predicates.and(predicates));
    return ImmutableList.copyOf(filtered);

  }

  private static class CompanyPredicate implements Predicate<Company> {

    private String value;

    CompanyPredicate(String value) {
      this.value = value;
    }

    @Override
    public boolean apply(Company company) {
      return company.getCompany().toLowerCase().startsWith(value.trim().toLowerCase());
    }

  }

  private static class VisiblePredicate implements Predicate<Company> {
    private boolean flag;

    VisiblePredicate(boolean flag) {
      this.flag = flag;
    }

    @Override
    public boolean apply(Company company) {
      return company.isVisible() == flag;
    }

  }

}
