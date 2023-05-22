package com.liferay.custom.role.restrictor;

import com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;

import java.util.Arrays;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;

import inet.ipaddr.IPAddressString;

@Component(
		configurationPid = "com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration",
		immediate = true,
		service = IPChecker.class)
public class IPChecker {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_roleRestrictorConfiguration = ConfigurableUtil.createConfigurable(
				RoleRestrictorConfiguration.class, properties);
	}
	
	public boolean check() {
		String[] whiteListedIPs = _roleRestrictorConfiguration.whiteListedIPs();
		String[] whiteListedIPRanges = _roleRestrictorConfiguration.whiteListedIPRanges();
		if(whiteListedIPRanges.length + whiteListedIPs.length > 0) {
			
			String clientIP = AuditRequestThreadLocal.getAuditThreadLocal().getClientIP();
			
			for(int i = 0; i < whiteListedIPs.length; i++) {
				String whiteListedIP = whiteListedIPs[i];
				if(clientIP.equals(whiteListedIP)) {
					return true;
				}
			}
			
			IPAddressString clientIPAddressString = new IPAddressString(clientIP);
			for(int i = 0; i < whiteListedIPRanges.length; i++) {
				String whiteListedIPange = whiteListedIPRanges[i];
				if(new IPAddressString(whiteListedIPange).contains(clientIPAddressString)) {
					return true;
				}
			}
			
			return false;
		}
		
		return true;
	}
	
	private volatile RoleRestrictorConfiguration _roleRestrictorConfiguration;

	
}
