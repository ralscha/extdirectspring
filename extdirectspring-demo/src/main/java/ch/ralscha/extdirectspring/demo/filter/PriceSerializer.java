package ch.ralscha.extdirectspring.demo.filter;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class PriceSerializer extends JsonSerializer<BigDecimal> {
  @Override
  public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
      JsonProcessingException {

    DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    jgen.writeString(decimalFormat.format(value));
  }
}
