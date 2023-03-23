package com.fivefu.core.report.config;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * 自定义拦截器
 */
public class InterceptorConfig implements HandlerInterceptor{
    /**   
     * 进入controller层之前拦截请求   
     * @param o
     * @return   
     * @throws Exception   
     */    
    @Override    
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {    
    		// System.out.println("---------------------开始进入请求地址拦截----------------------------");
		   HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		   HttpServletResponse httpServletResponse = (HttpServletResponse) response;
	       httpServletResponse.setHeader("Access-Control-Allow-Origin", httpServletRequest.getHeader("Origin"));
	       httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
	       httpServletResponse.setHeader("P3P", "CP=CAO PSA OUR");
	       httpServletResponse.addHeader("Access-Control-Allow-Methods", "POST,GET,TRACE,OPTIONS");
	       httpServletResponse.addHeader("Access-Control-Allow-Headers", "Content-Type,Origin,Accept");
	       httpServletResponse.addHeader("Access-Control-Max-Age", "120");
	       httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
	       httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
	       httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
	       // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
	       if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
	           httpServletResponse.setStatus(HttpStatus.OK.value());
	           return false;
	       }
       return true;
    }    
    
    @Override    
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {    
    	//System.out.println("--------------处理请求完成后视图渲染之前的处理操作---------------");    
    }    
    
    @Override    
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {    
    	//System.out.println("---------------视图渲染之后的操作-------------------------");    
    }    
}    