 package com.xlx.treds.sftp;

import java.util.List;

public class AdapterSFTPConfigBean
 {
 	public static final String TYPE_WHITE = "WHITE";
 	public static final String TYPE_BLACK = "BLACK";
 	private final String user;
 	private final String entityCode;
 	private final String password;
 	private final String host;
 	private final int port;
 	private final String key;
 	private final String srcPath;
 	private final String destPath;
 	private final String successPath;
 	private final String inSrcPath;
 	private final String inDestPath;
 	private final String inSuccessPath;
 	private final String extensions;
 	private final boolean isBlack;
 	private final String instDetInOblig;
 	private final List<String> obligList;
 	private final List<String> instList;
 	private final List<String> factUnitList;
 	
 	public AdapterSFTPConfigBean(String pSource,String pDestination,String pSuccessPath,String pExtensions,String pType,
 			String pUser, String pPassword,String pHost,int pPort,String pKey,String pInSource,String pInDestination,String pInSuccessPath
 			,String pEntityCode, String pInstDetInOblig , List<String> pObligList , List<String> pInstList , List<String> pFactUnitList)
 	{
 		user = pUser;
 		password = pPassword;
 		srcPath = pSource;
 		destPath = pDestination;
 		successPath = pSuccessPath;
 		host = pHost;
 		port = pPort;
 		key = pKey;
 		extensions = pExtensions;
 		isBlack = !TYPE_WHITE.equals(pType);
 		inSrcPath = pInSource;
 		inSuccessPath = pInSuccessPath;
 		inDestPath = pInDestination;
 		entityCode = pEntityCode;
 		instDetInOblig = pInstDetInOblig;
 		obligList = pObligList;
 		instList = pInstList;
 		factUnitList = pFactUnitList;
 	}

 	public String getUser() {
 		return user;
 	}

 	public String getPassword() {
 		return password;
 	}

 	public String getHost() {
 		return host;
 	}

 	public int getPort() {
 		return port;
 	}

 	public String getKey() {
 		return key;
 	}

 	public String getSrcPath() {
 		return srcPath;
 	}

 	public String getDestPath() {
 		return destPath;
 	}

 	public String getSuccessPath() {
 		return successPath;
 	}
 	
 	public String getExtensions() {
 		return extensions;
 	}

 	public boolean isBlack() {
 		return isBlack;
 	}
 	
 	public String getInSrcPath() {
 		return inSrcPath;
 	}

 	public String getInDestPath() {
 		return inDestPath;
 	}

 	public String getInSuccessPath() {
 		return inSuccessPath;
 	}

 	public String getEntityCode() {
 		return entityCode;
 	}

	public String getInstDetInOblig() {
		return instDetInOblig;
	}

	public List<String> getObligList() {
		return obligList;
	}

	public List<String> getInstList() {
		return instList;
	}

	public List<String> getFactUnitList() {
		return factUnitList;
	}

 }