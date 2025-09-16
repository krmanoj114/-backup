package com.tpex.admin.filter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * The Class JsonRequestHeaderFilter.
 */
@Component
public class JsonRequestHeaderFilter implements Filter {

	/**
	 * Do filter.
	 *
	 * @param request the request
	 * @param response the response
	 * @param chain the chain
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ServletException the servlet exception
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper((HttpServletRequest) request) {
			@Override
			public Enumeration<String> getHeaders(String name) {
				if (name.equalsIgnoreCase("Accept")) {
					Set<String> customHeaders = new HashSet<>();
					Enumeration<String> curHeaders = super.getHeaders(name);
					while (curHeaders.hasMoreElements()) {
						String header = curHeaders.nextElement();
						customHeaders.add(MediaType.APPLICATION_JSON_VALUE.concat(";").concat(header));
					}

					return Collections.enumeration(customHeaders);
				}
				return super.getHeaders(name);
			}
		};

		chain.doFilter(requestWrapper, response);
	}

}
