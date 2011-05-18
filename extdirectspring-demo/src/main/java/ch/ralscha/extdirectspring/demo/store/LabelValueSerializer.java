package ch.ralscha.extdirectspring.demo.store;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

public class LabelValueSerializer extends JsonSerializer<LabelValue> {

	@Override
	public void serialize(LabelValue value, JsonGenerator jgen, SerializerProvider provider) throws IOException,
			JsonProcessingException {

		jgen.writeStartObject();
		jgen.writeFieldName("label");
		jgen.writeString(value.getLabel());
		jgen.writeFieldName("value");
		jgen.writeString(value.getValue());
		jgen.writeEndObject();

	}

}
