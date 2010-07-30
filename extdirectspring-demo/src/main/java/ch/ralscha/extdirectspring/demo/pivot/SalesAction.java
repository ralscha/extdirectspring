package ch.ralscha.extdirectspring.demo.pivot;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;

@Named
public class SalesAction {

  @Inject
  private PivotDataBean dataBean;

  @ExtDirectMethod(value = ExtDirectMethodType.STORE_READ, group = "pivot")
  public List<Sale> load(ExtDirectStoreReadRequest request) {
    return dataBean.getSalesData();
  }

}
