package ch.ralscha.extdirectspring.demo.chart;

import javax.inject.Named;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;

@Named
public class DynamicDataProvider {
  
  @ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "newData", group = "dynamic")
  public double newData() {
    return 0.1;
  }
}
