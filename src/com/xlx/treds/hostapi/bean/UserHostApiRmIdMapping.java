package com.xlx.treds.hostapi.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserHostApiRmIdMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserHostApiRmIdMapping() {
		/*
		 * Do Nothing
		 */
	}

	private int roleId;
	private String roleName;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

}
