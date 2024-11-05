package www.raven.jc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * json util
 *
 * @author 刘家辉
 * @date 2023/11/23
 */
@Slf4j
public class JsonUtil {

	private static final ObjectMapper OBJECT_MAPPER;

	static {
		OBJECT_MAPPER = new ObjectMapper();
	}

	public static <T> T jsonToObj(String json, Class<T> clazz) {
		try {
			return OBJECT_MAPPER.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			log.error("JsonUtil jsonToObj error:{}", e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public static <T> String objToJson(T obj) {
		try {
			return OBJECT_MAPPER.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			log.error("JsonUtil objToJson error:{}", e.getMessage());
			throw new RuntimeException(e);
		}

	}
}
