package com.honvay.cola.auth.captcha.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.honvay.cola.framework.core.Constants;

/**
 * @author LIQIU
 * created on 2018-11-20
 **/
public class CaptchaAuthenticationFilter extends GenericFilterBean {

	public static final String LOGIN_CAPTCHA_SESSION_KEY = Constants.LOGIN_CAPTCHA_SESSION_KEY;

	public static final String LOGIN_CAPTCHA_PARAM_NAME = Constants.LOGIN_CAPTCHA_PARAM_NAME;

	private Map<RequestMatcher, AuthenticationFailureHandler> requestMatcherMap = new HashMap<>();

	public void addRequestMatcher(RequestMatcher requestMatcher, AuthenticationFailureHandler handler) {
		this.requestMatcherMap.put(requestMatcher, handler);
	}

	private AuthenticationFailureHandler requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
		for (RequestMatcher matcher : requestMatcherMap.keySet()) {
			if (matcher.matches(request)) {
				return requestMatcherMap.get(matcher);
			}
		}
		return null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		AuthenticationFailureHandler authenticationFailureHandler = requiresAuthentication(request, response);
		if (authenticationFailureHandler == null) {
			chain.doFilter(request, response);
			return;
		}

		HttpSession session = request.getSession();
		Object captcha = session.getAttribute(LOGIN_CAPTCHA_SESSION_KEY);
		
		String inputCaptcha = request.getParameter(LOGIN_CAPTCHA_PARAM_NAME);
		
		if(Boolean.TRUE.equals(session.getAttribute(Constants.CAPTCHA_AUTHENTICATION_REQUIRED_KEY)) && 
				StringUtils.isEmpty(inputCaptcha)){
			authenticationFailureHandler.onAuthenticationFailure(request, response, new InsufficientAuthenticationException("请输入验证码"));
			return;
		}
		
		if(!StringUtils.isEmpty(inputCaptcha) && !inputCaptcha.equalsIgnoreCase(String.valueOf(captcha))) {
			authenticationFailureHandler.onAuthenticationFailure(request, response, new InsufficientAuthenticationException("验证码错误"));
			return;
		}
	
		chain.doFilter(request, response);
	}
}