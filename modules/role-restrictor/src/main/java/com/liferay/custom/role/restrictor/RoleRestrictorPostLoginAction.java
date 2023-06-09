package com.liferay.custom.role.restrictor;

import com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.audit.AuditMessage;
import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.audit.AuditRouter;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.UserLockoutException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.security.auth.session.AuthenticatedSessionManagerUtil;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.audit.event.generators.constants.EventTypes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

@Component(
		configurationPid = "com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration",
		immediate = true,
		property = {
	        "key=login.events.pre"
	    },
	    service = LifecycleAction.class
	)
public class RoleRestrictorPostLoginAction implements LifecycleAction {

	@Activate
	@Modified
	protected void activate(Map<String, Object> properties) {
		_roleRestrictorConfiguration = ConfigurableUtil.createConfigurable(
				RoleRestrictorConfiguration.class, properties);
		_blackListedRoleNames = Arrays.asList(_roleRestrictorConfiguration.blackListedRoles());
	}
	
	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent) throws ActionException {

		if(_roleRestrictorConfiguration.roleRestrictorEnabled()) {

			if(!_headerChecker.check() || !_ipChecker.check()) {
				
				long userId = PrincipalThreadLocal.getUserId();
				try {
					User user = _userLocalService.getUser(userId);
					long [] roleIds = user.getRoleIds();
					
					boolean anyMatch = _roleLocalService.getRoles(roleIds).stream().map(role -> role.getName()).anyMatch(_blackListedRoleNames::contains);
					
					List<UserGroupRole> siteRoles = _userGroupRoleLocalService.getUserGroupRoles(userId);

					if(!anyMatch) {
						anyMatch = siteRoles.stream().map(siteRole -> {
							try {
								return siteRole.getRole().getName();
							} catch (PortalException e) {
								if(_log.isErrorEnabled()) {
									_log.error("Danger, could not check roles for access restriction", e);
								}
								return null;
							}
						}).filter(name -> Validator.isNotNull(name)).anyMatch(_blackListedRoleNames::contains);
					}
					
					if(anyMatch) {
						
						StringBundler messageBundler = new StringBundler();
						messageBundler.append("Invalidating session for user ");
						messageBundler.append(user.getScreenName());
						messageBundler.append(" with IP ");
						messageBundler.append(AuditRequestThreadLocal.getAuditThreadLocal().getClientIP());
						
						String message = messageBundler.toString();
						
						if(_log.isErrorEnabled()) {
							_log.error(message);
						}
						
						//AuthenticatedSessionManagerUtil.logout(lifecycleEvent.getRequest(), lifecycleEvent.getResponse());
						//lifecycleEvent.getRequest().getRequestDispatcher("/c/portal/logout").forward(lifecycleEvent.getRequest(), lifecycleEvent.getResponse());
						
						lifecycleEvent.getRequest().getSession(false).invalidate();
						
						lifecycleEvent.getRequest().setAttribute("ROLE_RESTRICTOR", true);
						
//						throw new ForbiddenAccessException(message);
						
					}
					
				} catch(ForbiddenAccessException e) {
					throw e;
				} catch (Exception e) {
					if(_log.isErrorEnabled()) {
						_log.error("Danger, could not check roles for access restriction", e);
					}
				}				
				
			}
			
		}		
	}
	
	private volatile RoleRestrictorConfiguration _roleRestrictorConfiguration;

	private volatile List<String> _blackListedRoleNames;
	
	@Reference
	private RoleLocalService _roleLocalService;
	
	@Reference
	private UserGroupRoleLocalService _userGroupRoleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private IPChecker _ipChecker;

	@Reference
	private HeaderChecker _headerChecker;
	
	@Reference
	private JSONFactory _jsonFactory;

	private static final Log _log = LogFactoryUtil.getLog(
			RoleRestrictorPostLoginAction.class);
}
