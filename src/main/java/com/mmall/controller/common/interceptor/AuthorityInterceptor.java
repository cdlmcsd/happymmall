package com.mmall.controller.common.interceptor;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		log.info("preHandle");
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		
		//获取拦截的方法名，类名
		String methodName = handlerMethod.getMethod().getName();
		String className = handlerMethod.getBean().getClass().getSimpleName();
		
		//获取输入的参数
		StringBuffer requestParaBuf = new StringBuffer();
		Map paraMap = request.getParameterMap();
		Iterator it = paraMap.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String mapkey = (String) entry.getKey();
			String mapvalue = StringUtils.EMPTY;
			
			if(entry.getValue() instanceof String[]){
				String[] strs = (String[]) entry.getValue();
				mapvalue = Arrays.toString(strs);
			}
			requestParaBuf.append(mapkey).append("=").append(mapvalue);
		}
		
		if(StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")){
			log.info("权限拦截器，拦截到className: {} methodName: {}",className,methodName);
			//拦截器拦截到的是登陆请求，就不打印登陆参数，防止日志泄露
			return true;
		}
		
		
		User user = null;
		
    	String token = CookieUtil.readLoginToken(request);
    	if (StringUtils.isNotEmpty(token)){
    		String json = RedisShardedPoolUtil.get(token);
        	user = JsonUtil.string2Obj(json, User.class);
    	}
    	
    	if(user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN){
    		//返回false,不会执行controller里的方法
    		response.reset();//这里要加reset,否则会报异常,原因是接管了
    		response.setCharacterEncoding("UTF-8");
    		response.setContentType("application/json;charset=UTF-8");
    		
    		
    		PrintWriter out = response.getWriter();
    		if(user == null){
    			out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截 用户未登陆")));
    		}else{
    			out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截 用户不是管理员组")));
    		}
    		out.flush();
    		out.close();
    		return false;
    	}
		
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("postHandle");
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		log.info("afterCompletion");
		
	}

}
