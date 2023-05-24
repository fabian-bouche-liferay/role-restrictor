package com.liferay.custom.role.restrictor;

import com.liferay.portal.servlet.filters.BasePortalFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;

@Component(
		immediate = true,
        property = {
        		"dispatcher=FORWARD",
        		"dispatcher=REQUEST",
        		"servlet-context-name=",
                "servlet-filter-name=Role Restrictor Filter",
                "url-pattern=/*"
        },		
		service = Filter.class
		)
public class RoleRestrictorFilter extends BasePortalFilter {

	@Override
	protected void processFilter(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws Exception {
		
		filterChain.doFilter(httpServletRequest, httpServletResponse);
		
		if(httpServletRequest.getAttribute("ROLE_RESTRICTOR") != null) {
			HttpSession session = httpServletRequest.getSession();
			if(session != null) session.invalidate();
		}
		
	}
	
}
