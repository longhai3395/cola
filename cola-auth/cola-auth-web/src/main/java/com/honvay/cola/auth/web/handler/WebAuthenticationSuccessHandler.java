package com.honvay.cola.auth.web.handler;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import com.honvay.cola.framework.core.Constants;

/**
 * @author LIQIU
 * created on 2018/12/26
 **/
public class WebAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		request.getSession().removeAttribute(Constants.AUTHENTICATION_FAIL_MESSAGE_KEY);
		request.getSession().removeAttribute(Constants.CAPTCHA_AUTHENTICATION_REQUIRED_KEY);
		super.onAuthenticationSuccess(request,response,authentication);
	}
}
