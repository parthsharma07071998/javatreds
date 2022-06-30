package com.xlx.treds.adapter;

import java.util.Map;

public class ClientSettingsBean {
	
	private String clientUrl;
	private String clientUsername;
	private String clientPassword;
	private String certificatePath;
	private String certificateIdentityPassword;
	private String certificateAlias;
	private Map<String,String> headers;

	public ClientSettingsBean(String pClientUrl, String pClientUsername, String pClientPassword, String pCertificatePath, String pCertificateIdentityPassword, String pCertificateAlias, Map<String,String> pHeaders) {
		super();
		this.clientUrl = pClientUrl;
		this.clientUsername = pClientUsername;
		this.clientPassword = pClientPassword;
		this.certificatePath = pCertificatePath;
		this.certificateIdentityPassword = pCertificateIdentityPassword;
		this.certificateAlias = pCertificateAlias;
		this.headers = pHeaders;
	}
	
	public String getClientUrl() {
		return clientUrl;
	}

	public String getClientUsername() {
		return clientUsername;
	}
	
	public String getClientPassword() {
		return clientPassword;
	}

	public String getCertificatePath() {
		return certificatePath;
	}
	
	public String getCertificateIdentityPassword() {
		return certificateIdentityPassword;
	}
	
	public String getCertificateAlias() {
		return certificateAlias;
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}
	
}
