package ch.ralscha.extdirectspring.bean.api;

import java.util.List;
import java.util.Map;

import ch.ralscha.extdirectspring.util.MapActionSerializer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class RemotingApiMixin {
	@JsonSerialize(using = MapActionSerializer.class)
	abstract Map<String, List<Action>> getActions();
}
