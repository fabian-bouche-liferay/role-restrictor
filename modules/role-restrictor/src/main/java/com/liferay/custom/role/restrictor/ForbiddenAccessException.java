package com.liferay.custom.role.restrictor;

import com.liferay.portal.kernel.events.ActionException;

public class ForbiddenAccessException extends ActionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ForbiddenAccessException(String message) {
		super(message);
	}
	
}
