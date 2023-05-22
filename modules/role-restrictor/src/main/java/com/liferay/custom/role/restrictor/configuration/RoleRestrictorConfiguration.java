package com.liferay.custom.role.restrictor.configuration;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;

import aQute.bnd.annotation.metatype.Meta;

@ExtendedObjectClassDefinition(category = "security-tools")
@Meta.OCD(
	id = "com.liferay.custom.role.restrictor.configuration.RoleRestrictorConfiguration",
	localization = "content/Language", name = "role-restrictor-configuration-name"
)
public interface RoleRestrictorConfiguration {

	@Meta.AD(
			deflt = "false", description = "role-restrictor-enabled-description",
			name = "role-restrictor-enabled", required = false
		)
	public boolean roleRestrictorEnabled();

	@Meta.AD(
			deflt = "Administrator|Site Administrator|Power User",
			description = "black-listed-roles-description",
			name = "black-listed-roles", required = false
	)
	public String[] blackListedRoles();

	
	@Meta.AD(
			deflt = "127.0.0.0/24",
			description = "white-listed-ip-ranges-description",
			name = "white-listed-ip-ranges", required = false
	)
	public String[] whiteListedIPRanges();	

	@Meta.AD(
			deflt = "127.0.0.1",
			description = "white-listed-ips-description",
			name = "white-listed-ips", required = false
	)
	public String[] whiteListedIPs();
	
	@Meta.AD(
			deflt = "X-Internal-Network",
			description = "internal-network-header-name-description",
			name = "internal-network-header-name", required = false
	)
	public String internalNetworkHeaderName();
	
	@Meta.AD(
			deflt = "true",
			description = "internal-network-header-value-description",
			name = "internal-network-header-value", required = false
	)
	public String internalNetworkHeaderValue();	

}
