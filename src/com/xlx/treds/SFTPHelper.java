package com.xlx.treds;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;
import com.xlx.common.file.sftp.SFTPConfigBean;
import com.xlx.common.file.sftp.SSHUserInfo;

import groovy.json.JsonSlurper;

public class SFTPHelper  {

	public static Logger logger = Logger.getLogger(SFTPHelper.class);

	public static final String SETTINGS_JSON = "sftpclient.json";
	
	//public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_CONFIG = "config";
	public static final String KEY_EXTENTIONS = "extensions";
	public static final String KEY_TYPE = "type";
	public static final String KEY_SRCPATH = "srcPath";
	public static final String KEY_DESTPATH = "destPath";
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_HOSTS = "hosts";
	public static final String KEY_USER = "user";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IP = "ip";
	public static final String KEY_PORT = "port";
	public static final String KEY_KEY = "key";

	private static SFTPHelper theInstance;
	private HashMap<String, SFTPConfigBean[]> folderWiseConfigs;
	private static final String[] SKIP_FOLDER_LIST = new String[] { ".", "..", "OUT", "lost+found", "SFTP-config", "sftpd-AuthKey", "system_files_backup", "TREDS" };
	private static Set<String> SKIP_FOLDER_HASH = new HashSet<String>();

	
	private SFTPHelper() {
		initialize();
	}

	public static SFTPHelper getInstance() {
		if (theInstance == null) {
			synchronized (SFTPHelper.class) {
				if (theInstance == null) {
					SFTPHelper tmpTheInstance = new SFTPHelper();
					theInstance = tmpTheInstance;
				}
			}
		}
		return theInstance;
	}
	
	public static void dropInstance()
    {
        synchronized(SFTPHelper.class)
        {
            theInstance = null;
        }
    }

	private void initialize() {
		folderWiseConfigs = new HashMap<String, SFTPConfigBean[]>();

		String lFolder = null;
		String lSuccess = null;

		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		List<Map> lSettings = (List<Map>) lMasterConfig.get(KEY_CONFIG);

		int lCount = lSettings.size();
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			Map<String, Object> lSettingMap = (Map<String, Object>) lSettings.get(lPtr);

			lFolder = (String) lSettingMap.get(KEY_SRCPATH);
			lSuccess = (String) lSettingMap.get(KEY_SUCCESS);
			List<Map> lHosts = (List<Map>) lSettingMap.get(KEY_HOSTS);

			SFTPConfigBean[] lConfigs = new SFTPConfigBean[lHosts.size()];
			String lType = (String) lSettingMap.get(KEY_TYPE);
			String lExtensions = (String) lSettingMap.get(KEY_EXTENTIONS);
			for (int lPtrX = 0; lPtrX < lHosts.size(); lPtrX++) {
				Map<String, Object> lHostCnf = lHosts.get(lPtrX);
				
				lConfigs[lPtrX] = new SFTPConfigBean(lFolder, (String) lHostCnf.get(KEY_DESTPATH), lSuccess,lExtensions,lType,
						(String) lHostCnf.get(KEY_USER), (String) lHostCnf.get(KEY_PASSWORD), (String) lHostCnf.get(KEY_IP),
						(Integer) lHostCnf.get(KEY_PORT), (String) lHostCnf.get(KEY_KEY));
			}
			folderWiseConfigs.put(lFolder, lConfigs);
		}
		for(String lTmpFolder : SKIP_FOLDER_LIST){
			//SKIP_FOLDER_
			SKIP_FOLDER_HASH.add(lTmpFolder);
		}
	}

	public void moveSFTPFilesINtoOUT() {
		try {
			Set<String> lKeys = folderWiseConfigs.keySet();
			Iterator<String> lIterator = lKeys.iterator();
			logger.info("Started moving SFTP files from IN to OUT.");
			while (lIterator.hasNext()) {
				String lSrcFolder = lIterator.next();
				SFTPConfigBean[] lConfigs = folderWiseConfigs.get(lSrcFolder);
				if (lConfigs != null) {
					for (int lPtr = 0; lPtr < lConfigs.length; lPtr++) {
						SFTPConfigBean lSFTPConfigBean = lConfigs[lPtr];
						Session lSession = null;
						String lLastStep = "";
						try {
							lLastStep = "getting SSHSession.";
							lSession = getSSHSession(lSFTPConfigBean);
							lLastStep = "connecting Session.";
							lSession.connect();
							lLastStep = "opening sftp channel.";
							Channel lChannel = lSession.openChannel("sftp");
							lLastStep = "connecting Channel.";
							lChannel.connect();
							lLastStep = "Channel casting to ChannelSftp.";
							ChannelSftp lSftpChannel = (ChannelSftp) lChannel;
							//
							lLastStep = "Checking Folder.";
							checkFolder(lSftpChannel, lSftpChannel.getHome() , lSFTPConfigBean.getDestPath());
							
						} catch (Exception pException) {
							logger.info("" + lSFTPConfigBean.getHost() + " :: " + lSFTPConfigBean.getSrcPath() + " :: Last Step : "  + lLastStep + " " + pException.getMessage());
							logger.info(" pringting stack trace.");
							pException.printStackTrace();
							logger.info(" printing each stack trace element.");
							for(StackTraceElement lStackElement : pException.getStackTrace() ){
								logger.info(lStackElement.toString());
							}
						} finally {
							if (lSession != null)
								lSession.disconnect();
						}
					} // For on configs ends
				}
			}
			logger.info("Finished moving SFTP files from IN to OUT.");
		} catch (Exception pException) {
			logger.error("Err in SFTP moving IN to OUT : ",pException);
		}
	}
	
	private void checkFolder(ChannelSftp lSftpChannel, String pRootFolder, String lDestBasePath){
		Vector<ChannelSftp.LsEntry> list;
		try {
			lSftpChannel.cd(pRootFolder);
			list = lSftpChannel.ls(lDestBasePath);
			lSftpChannel.cd(lDestBasePath);
			logger.info("SFTPHelper:: Func Params :: Root : "+ pRootFolder + " DesPath : "+lDestBasePath + " pwd : " + lSftpChannel.pwd());
			// List source directory structure.
			for (ChannelSftp.LsEntry oListItem : list) { // Iterate objects in the list to get file/folder names.
		        if (oListItem.getAttrs().isDir()) {
		        	if(SKIP_FOLDER_HASH.contains(oListItem.getFilename())){
		        		continue;
		        	}
		        	//logger.info("SFTPHelper:: Dir : " + oListItem.getFilename() + " PWD : "+lSftpChannel.pwd());
		        	if(oListItem.getFilename().equals("IN")){
		        		Vector<ChannelSftp.LsEntry> listFiles = lSftpChannel.ls("IN");
		        		if(listFiles !=null && listFiles.size() > 0){
		        			try{
			        			lSftpChannel.mkdir("OUT");
		        			}catch(Exception lEx){
		        			}
		        			for (ChannelSftp.LsEntry inFiles : listFiles) {
		        				if(inFiles.getFilename().equals(".") ||
		        						inFiles.getFilename().equals("..")){
		    		        		continue;
		    		        	}
		        				try{
			        				lSftpChannel.rename(lSftpChannel.pwd()+"/IN/"+inFiles.getFilename(), lSftpChannel.pwd()+"/OUT/"+inFiles.getFilename());
			    		        	logger.info("SFTPHelper:: File : " + lSftpChannel.pwd()+"/IN/"+inFiles.getFilename() +" moved successfully." );
		        				}catch(Exception lExRename){
		        					logger.info("SFTPHelper:: Error while Renaming : " + lExRename.getMessage());
		        				}
		        			}
		        		}
		        	}else{
		        		String lPwd = lSftpChannel.pwd();
			            checkFolder(lSftpChannel, lPwd, oListItem.getFilename());
			            lSftpChannel.cd(lSftpChannel.getHome());
			            lSftpChannel.cd(lPwd);
		        	}
		        } else {
		        	//logger.info("SFTPHelper:: File : " + oListItem.getFilename());
		        }
		    }
		} catch (SftpException e) {
			e.printStackTrace();
		} 
	}
	
	public Session getSSHSession(SFTPConfigBean pSFTPConfigBean) throws JSchException {
		JSch lSSHClient = new JSch();
		lSSHClient.addIdentity(pSFTPConfigBean.getKey());
		Session lSession = lSSHClient.getSession(pSFTPConfigBean.getUser(), pSFTPConfigBean.getHost(),
				pSFTPConfigBean.getPort());
		// username and password will be given via UserInfo interface.
		UserInfo lUserInfo = new SSHUserInfo();
		lSession.setUserInfo(lUserInfo);
		java.util.Properties lConfig = new java.util.Properties();
		lConfig.put("StrictHostKeyChecking", "no");
		lSession.setConfig(lConfig);
		return lSession;
	}


	public static void main(String[] args) {
		SFTPHelper lSftpHelper = SFTPHelper.getInstance();
		lSftpHelper.moveSFTPFilesINtoOUT();
	}
}