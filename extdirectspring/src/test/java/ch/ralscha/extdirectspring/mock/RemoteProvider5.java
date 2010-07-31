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

package ch.ralscha.extdirectspring.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

import org.springframework.web.bind.annotation.RequestParam;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.filter.BooleanFilter;
import ch.ralscha.extdirectspring.filter.ComparisonEnum;
import ch.ralscha.extdirectspring.filter.DateFilter;
import ch.ralscha.extdirectspring.filter.ListFilter;
import ch.ralscha.extdirectspring.filter.NumericFilter;
import ch.ralscha.extdirectspring.filter.StringFilter;

@Named
public class RemoteProvider5 {

  @ExtDirectMethod(ExtDirectMethodType.STORE_READ)
  public List<Row> method1(@RequestParam("type") int type, ExtDirectStoreReadRequest request) {

    switch (type) {
      case 1:
        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof NumericFilter);

        NumericFilter nf = (NumericFilter)request.getFilters().get(0);
        assertEquals(2, nf.getValue());
        assertEquals("id", nf.getField());
        assertEquals(ComparisonEnum.EQUAL, nf.getComparison());

        return createResult(1);
      case 2:
        assertEquals(2, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof NumericFilter);
        assertTrue(request.getFilters().get(1) instanceof NumericFilter);

        nf = (NumericFilter)request.getFilters().get(0);
        assertEquals(100, nf.getValue());
        assertEquals("id", nf.getField());
        assertEquals(ComparisonEnum.LESS_THAN, nf.getComparison());

        nf = (NumericFilter)request.getFilters().get(1);
        assertEquals(90, nf.getValue());
        assertEquals("id", nf.getField());
        assertEquals(ComparisonEnum.GREATER_THAN, nf.getComparison());
        return createResult(2);
      case 3:
        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof BooleanFilter);

        BooleanFilter bf = (BooleanFilter)request.getFilters().get(0);
        assertEquals(true, bf.getValue());
        assertEquals("visible", bf.getField());

        return createResult(3);
      case 4:
        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof BooleanFilter);

        bf = (BooleanFilter)request.getFilters().get(0);
        assertEquals(false, bf.getValue());
        assertEquals("visible", bf.getField());

        return createResult(4);
      case 5:
        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof StringFilter);

        StringFilter sf = (StringFilter)request.getFilters().get(0);
        assertEquals("abb", sf.getValue());
        assertEquals("company", sf.getField());

        return createResult(5);

      case 6:
        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof ListFilter);

        ListFilter lf = (ListFilter)request.getFilters().get(0);
        assertEquals(1, lf.getValue().size());
        assertEquals("small", lf.getValue().get(0));
        assertEquals("size", lf.getField());

        return createResult(6);

      case 7:

        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof ListFilter);

        lf = (ListFilter)request.getFilters().get(0);
        assertEquals(2, lf.getValue().size());
        assertEquals("small", lf.getValue().get(0));
        assertEquals("medium", lf.getValue().get(1));
        assertEquals("size", lf.getField());

        return createResult(7);

      case 8:

        assertEquals(2, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof DateFilter);
        assertTrue(request.getFilters().get(1) instanceof DateFilter);

        DateFilter df = (DateFilter)request.getFilters().get(0);
        assertEquals("07/31/2010", df.getValue());
        assertEquals("date", df.getField());
        assertEquals(ComparisonEnum.LESS_THAN, df.getComparison());

        df = (DateFilter)request.getFilters().get(1);
        assertEquals("07/01/2010", df.getValue());
        assertEquals("date", df.getField());
        assertEquals(ComparisonEnum.GREATER_THAN, df.getComparison());

        return createResult(8);

      case 9:
        assertEquals(1, request.getFilters().size());
        assertTrue(request.getFilters().get(0) instanceof DateFilter);

        df = (DateFilter)request.getFilters().get(0);
        assertEquals("07/01/2010", df.getValue());
        assertEquals("date", df.getField());
        assertEquals(ComparisonEnum.EQUAL, df.getComparison());

        return createResult(9);

    }

    return Collections.emptyList();
  }

  private List<Row> createResult(int i) {
    Row r = new Row(i, null, false, null);
    List<Row> result = new ArrayList<Row>();
    result.add(r);
    return result;
  }

}
