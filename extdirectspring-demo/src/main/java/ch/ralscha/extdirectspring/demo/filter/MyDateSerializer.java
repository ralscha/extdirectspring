package ch.ralscha.extdirectspring.demo.filter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class MyDateSerializer extends JsonSerializer<Date> {
  @Override
  public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
      JsonProcessingException {

    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    jgen.writeString(formatter.format(value.getTime()));
  }
}
