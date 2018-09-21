package com.mmall.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

	@Override
	public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, 
			Object object,	Exception exception) {
		log.error("{} Exception", httpServletRequest.getRequestURI(),exception);
		
		ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());
		//当使用jackson2的时候，使用MappingJackson2JsonView。
		modelAndView.addObject("status",ResponseCode.ERROR.getCode());
		modelAndView.addObject("msg","内部接口异常，详情请看服务端日志！！");
		modelAndView.addObject("data",exception.toString());
		
		return modelAndView;
	}

}
