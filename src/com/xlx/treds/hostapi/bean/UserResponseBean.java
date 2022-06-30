package com.xlx.treds.hostapi.bean;

import java.io.Serializable;
import java.util.List;

public class UserResponseBean implements Serializable {

	private static final long serialVersionUID = 1L;

	public UserResponseBean() {
		/*
		 * Do Nothing
		 */
	}

	private String messageCode;
	private String message;
	private List<UserHostApiBean> userHostApiBean;

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<UserHostApiBean> getUserHostApiBean() {
		return userHostApiBean;
	}

	public void setUserHostApiBean(List<UserHostApiBean> userHostApiBean) {
		this.userHostApiBean = userHostApiBean;
	}

}
