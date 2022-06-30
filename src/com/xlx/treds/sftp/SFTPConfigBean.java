package com.xlx.treds.sftp;

import java.util.List;
import java.util.Map;

public class SFTPConfigBean
{
	public static final String TYPE_WHITE = "WHITE";
	public static final String TYPE_BLACK = "BLACK";
	private final String name;
	private final String user;
	private final String password;
	private final int port;
	private final String ip;
	private final String key;
	private final List<SFTPProcessBean> inList; //key=ProcessKey, Map=srcPath,destPath,success
	private final List<SFTPProcessBean> outList;  //key=ProcessKey, Map=srcPath,destPath,success
	
	public SFTPConfigBean(String pName,String pUser, String pPassword,int pPort,String pIp, String pKey,
			List<SFTPProcessBean> pInList, List<SFTPProcessBean> pOutList)
	{
		name = pName;
		user = pUser;
		password = pPassword;
		port = pPort;
		ip = pIp;
		key = pKey;
		inList= pInList;
		outList = pOutList;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public String getIp() {
		return ip;
	}
	
	public String getKey() {
		return key;
	}
	

	public  List<SFTPProcessBean> getInList() {
		return inList;
	}

	public  List<SFTPProcessBean> getOutList() {
		return outList;
	}

	public String getName() {
		return name;
	}

}