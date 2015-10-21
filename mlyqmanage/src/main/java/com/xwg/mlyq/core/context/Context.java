package com.xwg.mlyq.core.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author 作者 : Andy_Liu
 * @version 创建时间：2015年7月1日 上午10:36:01 类说明
 */
public class Context implements IContext {

	public HttpServletRequest getRequst() {
		HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();
		return req;
	}

	public ApplicationContext getContext() {
		WebApplicationContext context = ContextLoader
				.getCurrentWebApplicationContext();

		return context;
	}
	
	public HttpServletResponse getResponse() {
		HttpServletResponse req = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getResponse();
		return req;
	}
}
