package com.mmall.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieUtil {
	private final static String COOKIE_DOMAIN = ".cdl.com";
	private final static String COOKIE_NAME = "mmall_login_token";
	
	public static String readLoginToken(HttpServletRequest request){
		Cookie[] cks = request.getCookies();
		if (cks != null){
			for (Cookie ck : cks) {
				log.info("read cookie_name: {}  cookie_value: {}", ck.getName(),ck.getValue());
				if (StringUtils.equals(ck.getName(), COOKIE_NAME)){
					log.info("return cookie_name {} cookie_value {}",ck.getName(),ck.getValue());
					return ck.getValue();
				}
			}
		}
		return null;
	}
	
	public static void writeLoginToken(HttpServletResponse response,String token){
		Cookie ck = new Cookie(COOKIE_NAME, token);
		ck.setDomain(COOKIE_DOMAIN);
		ck.setPath("/");
		ck.setHttpOnly(true);
		ck.setMaxAge(60 * 60 * 24 * 365);//单位为秒
		//如果不设置,则不写硬盘,当前页有效
		
		//设置-1是永不过期
//		ck.setMaxAge(-1);
		
		log.info("cookie_name: {}  cookie_value: {}",ck.getName(),ck.getValue());
		response.addCookie(ck);
	}
	
	public static void delLoginToken(HttpServletRequest request,HttpServletResponse response){
		Cookie[] cks = request.getCookies();
		if (cks != null){
			for (Cookie ck : cks) {
				if (StringUtils.equals(ck.getName(), COOKIE_NAME)){
					ck.setDomain(COOKIE_DOMAIN);
					ck.setPath("/");
					ck.setMaxAge(0);
					log.info("del cookie_name {} cookid_value {}",ck.getName(),ck.getValue());
					response.addCookie(ck);
					return;
				}
			}
		}
	}
}
