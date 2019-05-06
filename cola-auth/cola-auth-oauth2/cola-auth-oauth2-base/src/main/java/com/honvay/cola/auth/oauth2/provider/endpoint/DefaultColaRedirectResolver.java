package com.honvay.cola.auth.oauth2.provider.endpoint;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class DefaultColaRedirectResolver extends DefaultRedirectResolver implements RedirectResolver {

	private Collection<String> redirectGrantTypes = Arrays.asList("implicit", "authorization_code");

	
	@Override
	public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {

		Set<String> authorizedGrantTypes = client.getAuthorizedGrantTypes();
		if (authorizedGrantTypes.isEmpty()) {
			throw new InvalidGrantException("A client must have at least one authorized grant type.");
		}
		if (!containsRedirectGrantType(authorizedGrantTypes)) {
			throw new InvalidGrantException(
					"A redirect_uri can only be used by implicit or authorization_code grant types.");
		}

		Set<String> registeredRedirectUris = client.getRegisteredRedirectUri();
		return obtainMatchingRedirect(registeredRedirectUris, requestedRedirect);
	}
	
	/**
	 * Attempt to match one of the registered URIs to the that of the requested one.
	 * 
	 * @param redirectUris the set of the registered URIs to try and find a match. This cannot be null or empty.
	 * @param requestedRedirect the URI used as part of the request
	 * @return the matching URI
	 * @throws RedirectMismatchException if no match was found
	 */
	private String obtainMatchingRedirect(Set<String> redirectUris, String requestedRedirect) {
		if(!StringUtils.isEmpty(requestedRedirect) && (null == redirectUris || redirectUris.isEmpty())) {
			return requestedRedirect;
		}

		if (redirectUris.size() == 1 && requestedRedirect == null) {
			return redirectUris.iterator().next();
		}
		for (String redirectUri : redirectUris) {
			if (requestedRedirect != null && redirectMatches(requestedRedirect, redirectUri)) {
				return requestedRedirect;
			}
		}
		throw new RedirectMismatchException("Invalid redirect: " + requestedRedirect
				+ " does not match one of the registered values.");
	}
	
	/**
	 * @param grantTypes some grant types
	 * @return true if the supplied grant types includes one or more of the redirect types
	 */
	private boolean containsRedirectGrantType(Set<String> grantTypes) {
		for (String type : grantTypes) {
			if (redirectGrantTypes.contains(type)) {
				return true;
			}
		}
		return false;
	}

}
