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
			StringBuffer apiStringDoc = new StringBuffer(2048);//doc may be very verbose 
			for (Entry<String, List<Action>> entry : value.entrySet()) {
				String key = entry.getKey();
				jgen.writeArrayFieldStart(key);
				List<Action> actions = entry.getValue();
				for (Action action : actions) {
					if(action instanceof ActionDoc) {//insertion of doc here
						jgen.writeRaw("/**");
						jgen.writeRaw("*/");
					}
					jgen.writeObject(action);
				}
				jgen.writeEndArray();
			}
		
			jgen.writeString(apiStringDoc.toString());
		}
	}

}
