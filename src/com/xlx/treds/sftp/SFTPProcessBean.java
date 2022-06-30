package com.xlx.treds.sftp;

import java.util.Map;

public class SFTPProcessBean {
	public static final String TYPE_WHITE = "WHITE";
	public static final String TYPE_BLACK = "BLACK";
	private final String name;
	private final String srcPath;
	private final String destPath;
	private final String success;
	private final String extensions;
	private final boolean isBlack;
	private final boolean isIn;
	
	public SFTPProcessBean(String pName,String pSrcPath, String pDestPath,String pSuccess,String pExtension,String pType, boolean pIsIn) {
		name = pName;
		srcPath = pSrcPath;
		destPath = pDestPath;
		success = pSuccess;
		extensions = pExtension;
		isBlack = !TYPE_WHITE.equals(pType);
		isIn = pIsIn;
	}
	
	public String getSrcPath() {
		return srcPath;
	}
	
	public String getDestPath() {
		return destPath;
	}
	
	public String getSuccess() {
		return success;
	}
	
	public String getExtensions() {
		return extensions;
	}
	
	public boolean isBlack() {
		return isBlack;
	}
	
	public boolean isIn() {
		return isIn;
	}

	public String getName() {
		return name;
	}

}
