package com.monitor.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.TypeReference;

public final class JacksonUtil {

	private static ObjectMapper MAPPER;

	static {
		MAPPER = generateMapper(Inclusion.ALWAYS);
	}

	private JacksonUtil() {
	}

	/* json转成对象 */
	public static <T> T fromJson(String json, Class<T> clazz)
			throws IOException {
		return clazz.equals(String.class) ? (T) json : MAPPER.readValue(json,
				clazz);
	}

	/* json转为对象 */
	public static <T> T fromJson(String json, TypeReference<?> typeReference)
			throws IOException {
		return (T) (typeReference.getType().equals(String.class) ? json
				: MAPPER.readValue(json, typeReference));
	}

	/* 对象转json */
	public static <T> String toJson(T src) throws IOException {
		return src instanceof String ? (String) src : MAPPER
				.writeValueAsString(src);
	}

	/* 将对象转json 可设置输出属性 */
	public static <T> String toJson(T src, Inclusion inclusion)
			throws IOException {
		if (src instanceof String) {
			return (String) src;
		} else {
			ObjectMapper customMapper = generateMapper(inclusion);
			return customMapper.writeValueAsString(src);
		}
	}

	/* 对象转json字符串 */
	public static <T> String toJson(T src, ObjectMapper mapper)
			throws IOException {
		if (null != mapper) {
			if (src instanceof String) {
				return (String) src;
			} else {
				return mapper.writeValueAsString(src);
			}
		} else {
			return null;
		}
	}

	/* json转对象 */
	public static Object jsonToBean(String json, Class<?> cls) {
		if (!StringUtils.isNotBlank(json))
			return null;
		ObjectMapper objectMapper = new ObjectMapper();
		Object vo;
		try {
			objectMapper.configure(
					SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
					false);
			SimpleDateFormat myDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			objectMapper.setDateFormat(myDateFormat);
			vo = objectMapper.readValue(json, cls);
		} catch (JsonParseException e) {
			e.printStackTrace();
			return null;
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return vo;
	}

	@SuppressWarnings("rawtypes")
	public static Map json2Map(String src) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<?, ?> map = mapper.readValue(src, Map.class);
			return map;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 对象转成Map
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> objectToMap(Object obj) throws Exception {    
        if(obj == null){    
            return null;    
        }   
   
        Map<String, Object> map = new HashMap<String, Object>();    
   
        Field[] declaredFields = obj.getClass().getDeclaredFields();    
        for (Field field : declaredFields) {    
            field.setAccessible(true);  
            map.put(field.getName(), field.get(obj));  
        }    
   
        return map;  
    }   
	/**
	 * Map转成对象
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static <T> T mapToObject(Map map,Class<T> clazz) throws Exception {    
		if (map == null) {
			return null;
		}
		String json = toJson(map);
		return fromJson(json, clazz);
	}
	
	/* 返回ObjectMapper */
	public static ObjectMapper mapper() {
		return MAPPER;
	}

	private static ObjectMapper generateMapper(Inclusion inclusion) {

		ObjectMapper customMapper = new ObjectMapper();

		// 设置输入时忽略在JSON字符串中存在但Java对象实际没有的属性
		customMapper.configure(Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		// 禁止使用int代表Enum的order()來反序列化Enum,非常危險
		customMapper.configure(Feature.FAIL_ON_NUMBERS_FOR_ENUMS, true);

		// 所有日期格式都统一为以下样式
		customMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

		return customMapper;
	}
	
	/**
	 *  读文件，返回json字符串转Map
	 * @param path
	 * @return
	 */
	public static Map readJsonFileToMap(String path){
	    File file = new File(path);
	    BufferedReader reader = null;
	    String laststr = "";
	    try {
		     reader = new BufferedReader(new FileReader(file));
		     String tempString = null;
		    
		     //一次读入一行，直到读入null为文件结束
		     while ((tempString = reader.readLine()) != null) {
			     laststr = laststr +tempString;
		     }
		     reader.close();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
		     if (reader != null) {
			      try {
			    	  reader.close();
			      } catch (IOException e1) {
			      }
		     }
		}
	    return json2Map(laststr);
	}
}
