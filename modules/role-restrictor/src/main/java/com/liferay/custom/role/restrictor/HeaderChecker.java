package com.liferay.custom.role.restrictor;

import com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.util.Validator;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

@Component(
		configurationPid = "com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration",
		immediate = true,
		service = HeaderChecker.class)
public class HeaderChecker {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_roleRestrictorConfiguration = ConfigurableUtil.createConfigurable(
				RoleRestrictorConfiguration.class, properties);
	}
	
	public boolean check() {
		String internalNetworkHeaderName = _roleRestrictorConfiguration.internalNetworkHeaderName();
		String internalNetworkHeaderValue = _roleRestrictorConfiguration.internalNetworkHeaderValue();
		if(!Validator.isBlank(internalNetworkHeaderName) &&
				!Validator.isBlank(internalNetworkHeaderValue)) {
			
			Map<String, String> headers = ServiceContextThreadLocal.getServiceContext().getHeaders();
			if(!headers.containsKey(internalNetworkHeaderName) || 
					!headers.get(internalNetworkHeaderName).equals(internalNetworkHeaderValue)) {
				return true;
			}
			
			return false;
			
		}
		return true;
	}
	
	private volatile RoleRestrictorConfiguration _roleRestrictorConfiguration;

	
}
