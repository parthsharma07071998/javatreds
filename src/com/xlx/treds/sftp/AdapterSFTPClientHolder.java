package com.xlx.treds.sftp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.treds.AdapterSFTPClient;
import com.xlx.treds.AppInitializer;
import com.xlx.treds.sftp.SFTPClient;

public final class AdapterSFTPClientHolder {
    private static final Logger logger = LoggerFactory.getLogger(AdapterSFTPClientHolder.class);

	public static final String KEY_BHELSFTPCLIENT = "BHELPEM";
	public static final String FILENAME_BHELSFTPCLIENT = "SFTP_BH0000824.json";
	public static final String KEY_EDNSFTPCLIENT = "BHELEDN";
	public static final String FILENAME_EDNSFTPCLIENT = "ednsftpclient.json";
	public static final String KEY_JBMSFTPCLIENT = "JBM";
	public static final String FILENAME_JBMSFTPCLIENT = "SFTP_JB0003393.json";
	public static final String KEY_NEELSFTPCLIENT = "NEEL";
	public static final String FILENAME_NEELSFTPCLIENT = "SFTP_NE0003316.json";
	
	private static Map<String, AdapterSFTPClient> sftpClientInstances = new HashMap<String, AdapterSFTPClient>();
	
	public static void createInstance(String pKey, String pJsonFileName) {
		synchronized (AdapterSFTPClientHolder.class) {
			if(!sftpClientInstances.containsKey(pKey)) {
				try {
					AdapterSFTPClient theInstance = new AdapterSFTPClient(pJsonFileName);
					theInstance.newThread(pKey);
					sftpClientInstances.put(pKey, theInstance);
				}catch(Exception lEx) {
					logger.debug("Error while creating SFTP client for key "+pKey, lEx );
				}
			}
		}
	}

	public static AdapterSFTPClient getInstance(String pKey) {
		if(sftpClientInstances.containsKey(pKey)) {
			return sftpClientInstances.get(pKey);
		}
		return null;
	}

	public static void dropInstance(String pKey)
    {
        synchronized(AdapterSFTPClientHolder.class)
        {
			if(sftpClientInstances.containsKey(pKey)) {
				AdapterSFTPClient theInstance = sftpClientInstances.get(pKey);
	        	if(theInstance != null) {
	        		theInstance.stopThread();
	        	}
	            theInstance = null;
				sftpClientInstances.remove(pKey);
			}
        }
    }


	
}
