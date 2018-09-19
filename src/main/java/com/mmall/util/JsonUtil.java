package com.mmall.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.annotate.JsonUnwrapped;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import com.google.common.collect.Lists;
import com.mmall.pojo.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		//把对象的所有字段都序列化
		objectMapper.setSerializationInclusion(Inclusion.ALWAYS);
		
		//默认日期不转换成timestamps形式
		objectMapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,false);
		
		//忽略空beanl转json报错
		objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
		
		//日期时间格式统一成 "yyyy-MM-dd HH:mm:ss"
		objectMapper.setDateFormat(new SimpleDateFormat(DateTimeUtil.STANDARD_FORMAT));
		
		//反序列化时未知属性忽略json存在,bean中不存在的
		objectMapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	
	public static <T> String obj2String (T obj) {
		if (obj == null){
			return null;
		}
		try {
			return obj instanceof String ? (String)obj : objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			log.warn("Parse Object to String error",e);
			return null;
		} 
	}
	
	public static <T> String obj2StringPretty (T obj) {
		if (obj == null){
			return null;
		}
		try {
			return obj instanceof String ? (String)obj : objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (Exception e) {
			log.warn("Parse Object to String error",e);
			return null;
		} 
	}
	
	public static <T> T string2Obj (String str,Class<T> clazz){
		if(StringUtils.isEmpty(str) || clazz == null){
			return null;
		}
		try {
			return clazz.equals(String.class) ? (T) str : objectMapper.readValue(str, clazz);
		} catch (Exception e) {
			log.warn("Parse Object from String Error",e);
			return null;
		}
	}
	
	
	public static <T> T string2Obj (String str,TypeReference<T> typeReference){
		if(StringUtils.isEmpty(str) || typeReference == null){
			return null;
		}
		try {
			return (T) (typeReference.equals(String.class) ?  str : objectMapper.readValue(str, typeReference));
		} catch (Exception e) {
			log.warn("Parse Object from String Error",e);
			return null;
		}
	}
	
	public static <T> T string2Obj (String str, Class<?> collectionClass,Class<?>... elementClasses){
		JavaType javatype = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
		try {
			return objectMapper.readValue(str, javatype);
		} catch (Exception e) {
			log.warn("Parse Object from String Error",e);
			return null;
		}
	}
	
	public static void main(String[] args) {
		User u1 = new User();
		u1.setId(1);
		u1.setEmail("69981523@qq.com");
		User u2 = new User();
		u2.setId(2);
		u2.setEmail("69981523222@qq.com");
		
		String json1 = JsonUtil.obj2String(u1);
		
		String json2 = JsonUtil.obj2StringPretty(u1);
		
		log.info("obj2String: {}",json1);
		log.info("obj2StringPretty: {}",json2);
		
		User u = JsonUtil.string2Obj(json2, User.class);
//		User u = JsonUtil.string2Obj(json2, new TypeReference<User>() {
//		});
		
		List<User> userList = Lists.newArrayList();
		userList.add(u1);
		userList.add(u2);
		
		String json3 = JsonUtil.obj2StringPretty(userList);
		
		List<User> userList2 = JsonUtil.string2Obj(json3, new TypeReference<List<User>>() {
		});
		
		List<User> userList3 =  JsonUtil.string2Obj(json3, List.class, User.class);
		
		log.info("=============================");
		
		log.info("userList: {}",json3);
		System.out.println("end!!");
	}
	
}
