package com.honvay.cola.auth.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import com.honvay.cola.framework.core.Constants;

/**
 * @author LIQIU
 * created on 2018-11-24
 **/
public class WebAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {


	public WebAuthenticationFailureHandler(String defaultFailureUrl) {
		super(defaultFailureUrl);
	}

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
		request.getSession().setAttribute(Constants.AUTHENTICATION_FAIL_MESSAGE_KEY, exception.getMessage());
		request.getSession().setAttribute(Constants.CAPTCHA_AUTHENTICATION_REQUIRED_KEY, true);
		super.onAuthenticationFailure(request, response, exception);
	}
}
