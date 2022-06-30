package com.xlx.treds.sftp;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.treds.AppInitializer;
import com.xlx.treds.sftp.SFTPClient;

public final class SFTPClientHolder {
    private static final Logger logger = LoggerFactory.getLogger(SFTPClientHolder.class);

	//"tredssftpcli", "sftpclientSnorkel.json"
	public static final String KEY_TREDSSFTPCLIENT = "tredssftpcli";
	public static final String FILENAME_TREDSSFTPCLIENT = "sftpclientSnorkel.json";
	//"logsftpcli", "sftpclienttredslog.json"
	public static final String KEY_LOGSFTPCLIENT = "logsftpcli";
	public static final String FILENAME_LOGSSFTPCLIENT = "sftpclienttredslog.json";
	
	public static final String KEY_TREDSDBBACKUP = "tredsdbbackup";
	public static final String FILENAME_TREDSDBBACKUP = "backup.json";
	
	private static Map<String, SFTPClient> sftpClientInstances = new HashMap<String, SFTPClient>();
	
	public static void createInstance(String pKey, String pJsonFileName) {
		synchronized (SFTPClientHolder.class) {
			if(!sftpClientInstances.containsKey(pKey)) {
				try {
					SFTPClient theInstance = new SFTPClient(pJsonFileName);
					theInstance.newThread(pKey);
					sftpClientInstances.put(pKey, theInstance);
				}catch(Exception lEx) {
					logger.debug("Error while creating SFTP client for key "+pKey, lEx );
				}
			}
		}
	}

	public static SFTPClient getInstance(String pKey) {
		if(sftpClientInstances.containsKey(pKey)) {
			return sftpClientInstances.get(pKey);
		}
		return null;
	}

	public static void dropInstance(String pKey)
    {
        synchronized(SFTPClientHolder.class)
        {
			if(sftpClientInstances.containsKey(pKey)) {
				SFTPClient theInstance = sftpClientInstances.get(pKey);
	        	if(theInstance != null) {
	        		theInstance.stopThread();
	        	}
	            theInstance = null;
				sftpClientInstances.remove(pKey);
			}
        }
    }


	
}
