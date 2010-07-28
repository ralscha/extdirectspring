package ch.ralscha.extdirectspring.demo.filter;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class SizeSerializer extends JsonSerializer<SizeEnum> {
  @Override
  public void serialize(SizeEnum value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
      JsonProcessingException {

    jgen.writeString(value.getLabel());
  }
}
