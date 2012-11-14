/**
 * 
 */
package ch.ralscha.extdirectspring.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.ralscha.extdirectspring.bean.api.Action;
import ch.ralscha.extdirectspring.bean.api.ActionDoc;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @author dbs
 *
 */
public class MapActionSerializer extends JsonSerializer<Map<String, List<Action>>> {

	@Override
	public void serialize(Map<String, List<Action>> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if(null != value){
			jgen.writeStartObject();
			for (Entry<String, List<Action>> entry : value.entrySet()) {
				String key = entry.getKey();
				jgen.writeArrayFieldStart(key);
				List<Action> actions = entry.getValue();
				for (Action action : actions) {
					//PrettyPrinter is not set the generator is on one line mode
					if(jgen.getPrettyPrinter() != null && action instanceof ActionDoc) {//insertion of doc here
						ActionDoc actionDoc = (ActionDoc) action; 
						jgen.writeRaw("\n\t/**");
						if(actionDoc.isDeprecated())
							jgen.writeRaw("\n\t* @deprecated");
						jgen.writeRaw("\n\t* " + actionDoc.getName() + ": " + actionDoc.getMethodComment());
						jgen.writeRaw("\n\t* @author: " + actionDoc.getAuthor());
						jgen.writeRaw("\n\t* @version: " + actionDoc.getVersion());
						jgen.writeRaw("\n\t*");
						for (Entry<String, String> entry2 : actionDoc.getParameters().entrySet()) {
							jgen.writeRaw("\n\t* @param: [" + entry2.getKey()+"] " + entry2.getValue());
						}
						jgen.writeRaw("\n\t* @return");
						for (Entry<String, String> entry2 : actionDoc.getReturnMethod().entrySet()) {
							jgen.writeRaw("\n\t*\t [" + entry2.getKey()+"] " + entry2.getValue());
						}
						jgen.writeRaw("\n\t*/\n");
					}
					jgen.writeObject(action);
				}
				jgen.writeEndArray();
			}
			jgen.writeEndObject();
		}
	}

}
