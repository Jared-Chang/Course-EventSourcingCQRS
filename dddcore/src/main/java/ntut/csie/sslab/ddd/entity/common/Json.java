package ntut.csie.sslab.ddd.entity.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import java.io.IOException;

public class Json {

	public static final JsonMapper mapper;

	static {
				mapper = JsonMapper.builder()
				.addModule(new ParameterNamesModule())
				.addModule(new Jdk8Module())
				.addModule(new JavaTimeModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
				.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
				.build();
				mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

	}

	public static String asString(Object value){
		try {
			return mapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T readValue(String content, Class<T> valueType) {
		try {
			return mapper.readValue(content, valueType);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <A> A readAs(byte[] content, Class<A> clazz) {

		try {
			return mapper.readValue(content, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static JsonNode readTree(String content)  {
		try {
			return mapper.readTree(content);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}

	}

}
