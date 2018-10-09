package com.qceda.module.blog.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.qceda.module.blog.service.AuthService;
import com.qceda.module.blog.wso.ReqContextAuth;
import com.qceda.module.blog.wso.ServiceResponse;

/**
 * This filter check the request header for an auth token. It then put the
 * context user in the request attribute if auth token is valid for use during
 * the lifetime of the request. It will also return an updated token if the auth
 * service returns one in the response header.
 * 
 * @author vtran
 *
 */
@Component
@Order(1)
public class AuthFilter implements Filter {

	private static final String AUHT_RESPONSE = "authResponse";

	@SuppressWarnings("unchecked")
	public static ServiceResponse<ReqContextAuth> getAuthResponse() {
		return (ServiceResponse<ReqContextAuth>) RequestContextHolder
				.getRequestAttributes()
				.getAttribute(AUHT_RESPONSE, RequestAttributes.SCOPE_REQUEST);
	}

	private static void setAuthResponse(ServiceResponse<ReqContextAuth> authRes) {
		RequestContextHolder.getRequestAttributes().setAttribute(AUHT_RESPONSE, authRes,
				RequestAttributes.SCOPE_REQUEST);
	}

	@Autowired
	private AuthService authService;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Nothing needed
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		ServiceResponse<ReqContextAuth> serviceRes = null;

		String authHeader = req.getHeader("authorization");
		if (authHeader != null && authHeader.length() > 0) {
			String[] parts = authHeader.split(" ");
			if (parts.length > 1) {
				serviceRes = this.authService.verifyAuth(parts[1]);
				if (serviceRes.getResult() != null
						&& serviceRes.getResult().getToken() != null) {
					res.setHeader("Authorization", String.format("Bearer %s",
							serviceRes.getResult().getToken().getToken()));
				}
				setAuthResponse(serviceRes);
			}
		}

		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// Nothing needed
	}

}
