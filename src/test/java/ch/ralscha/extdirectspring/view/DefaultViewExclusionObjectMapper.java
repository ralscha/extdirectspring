package ch.ralscha.extdirectspring.view;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultViewExclusionObjectMapper extends ObjectMapper {
	public DefaultViewExclusionObjectMapper() {
		super();

		configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
	}
}