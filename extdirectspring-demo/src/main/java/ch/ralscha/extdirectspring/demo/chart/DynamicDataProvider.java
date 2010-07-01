package ch.ralscha.extdirectspring.demo.chart;

import java.util.Map;
import javax.inject.Named;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethod;
import ch.ralscha.extdirectspring.annotation.ExtDirectMethodType;
import com.google.common.collect.ImmutableMap;

@Named
public class DynamicDataProvider {

  @ExtDirectMethod(value = ExtDirectMethodType.POLL, event = "newData", group = "dynamic")
  public Map<String, Double> newData() {
    return new ImmutableMap.Builder<String, Double>().put("x", Math.random()).put("y", Math.random()).build();
  }
}
