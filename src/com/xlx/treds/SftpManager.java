package com.xlx.treds;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Proxy;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import com.xlx.common.utilities.CommonUtilities;

public class SftpManager {

	public static Logger logger = Logger.getLogger(SftpManager.class);
	
	public void sendFile(File pFile, String pIp, Integer pPort, String pProxyIp, 
			Integer pProxyPort, String pUsername, String pPassword, String pSendPath) throws Exception {
		if ((pFile == null) || (!CommonUtilities.hasValue(pIp)) || (pPort == null) 
				|| (!CommonUtilities.hasValue(pUsername)) || (!CommonUtilities.hasValue(pPassword)) 
				|| (!CommonUtilities.hasValue(pSendPath)))
			throw new Exception ("Important properties missing");
		
		Session lSession = null;
		Channel lChannel = null;
		ChannelSftp lChannelSftp = null;
		try {
			JSch jsch = new JSch();
			lSession = jsch.getSession(pUsername, pIp, pPort);
			lSession.setPassword(pPassword);
			if ((CommonUtilities.hasValue(pProxyIp)) && (pProxyPort != null)) {
				Proxy lProxy = new ProxyHTTP(pProxyIp, pProxyPort);
    			lSession.setProxy(lProxy);
			}
			java.util.Properties lConfig = new java.util.Properties();
			lConfig.put("StrictHostKeyChecking", "no");
			lConfig.put("PreferredAuthentications", "password");
			lSession.setConfig(lConfig);
			lSession.connect();
			System.out.println("Host connected.");
			lChannel = lSession.openChannel("sftp");
			lChannel.connect();
			System.out.println("sftp channel opened and connected.");
			lChannelSftp = (ChannelSftp) lChannel;
			lChannelSftp.cd(pSendPath);
			lChannelSftp.put(new FileInputStream(pFile), pFile.getName(), ChannelSftp.OVERWRITE);
			logger.info("File "+pFile.getName()+" succesfully sent through SFTP");
		} catch (Exception lException) {
			logger.error("Error while sending file through SFTP : "+lException);
			throw new Exception(lException);
		} finally {
			if (lChannelSftp != null) {
				lChannelSftp.exit();
				logger.info("sftp Channel disconnected.");
			}
			if(lChannel != null) {
				lChannel.disconnect();
				logger.info("Channel disconnected.");
			}
			if (lSession != null) {
				lSession.disconnect();
				logger.info("Host Session disconnected.");
			}
		}
	}

	public void receiveFile(String pIp, Integer pPort, String pProxyIp, 
			Integer pProxyPort, String pUsername, String pPassword, String pRemoteReceivePath, 
			String pLocalReceivePath, String pRemoteBackupPath, Integer pFileCount) throws Exception {
		if ((!CommonUtilities.hasValue(pIp)) || (pPort == null) || (!CommonUtilities.hasValue(pUsername)) 
				|| (!CommonUtilities.hasValue(pPassword)) || (!CommonUtilities.hasValue(pRemoteReceivePath))
				|| (!CommonUtilities.hasValue(pLocalReceivePath)))
			throw new Exception ("Important properties missing");
		SimpleDateFormat lFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
		Session lSession = null;
		Channel lChannel = null;
		ChannelSftp lChannelSftp = null;
		try {
			JSch jsch = new JSch();
			lSession = jsch.getSession(pUsername, pIp, pPort);
			lSession.setPassword(pPassword);
			if ((CommonUtilities.hasValue(pProxyIp)) && (pProxyPort != null)) {
				Proxy lProxy = new ProxyHTTP(pProxyIp, pProxyPort);
    			lSession.setProxy(lProxy);
			}
			java.util.Properties lConfig = new java.util.Properties();
			lConfig.put("StrictHostKeyChecking", "no");
			lConfig.put("PreferredAuthentications", "password");
			lSession.setConfig(lConfig);
			lSession.connect();
			System.out.println("Host connected.");
			lChannel = lSession.openChannel("sftp");
			lChannel.connect();
			System.out.println("sftp channel opened and connected.");
			lChannelSftp = (ChannelSftp) lChannel;
			lChannelSftp.cd(pRemoteReceivePath);
			Vector<ChannelSftp.LsEntry> lServerFiles = lChannelSftp.ls("*");
			if ((pFileCount != null) && (lServerFiles.size() != pFileCount))
				throw new Exception("All files are not present on remote server");
			for (ChannelSftp.LsEntry lEntry : lServerFiles) {
				lChannelSftp.get(lEntry.getFilename(), pLocalReceivePath+File.separatorChar+lEntry.getFilename());
				logger.info("File received through SFTP File Name : "+lEntry.getFilename());
				if (CommonUtilities.hasValue(pRemoteBackupPath)) {
					String lBackupFileName = lFormat.format(new Date()) + "_" + lEntry.getFilename();
					lChannelSftp.rename(lEntry.getFilename(), pRemoteBackupPath+"/"+lBackupFileName);
					logger.info("File moved to remote backup path File Name : "+lEntry.getFilename());
				}
			}
		} catch (Exception lException) {
			logger.error("Error while receiving file through SFTP : "+lException);
			throw new Exception(lException);
		} finally {
			if (lChannelSftp != null) {
				lChannelSftp.exit();
				logger.info("sftp Channel disconnected.");
			}
			if(lChannel != null) {
				lChannel.disconnect();
				logger.info("Channel disconnected.");
			}
			if (lSession != null) {
				lSession.disconnect();
				logger.info("Host Session disconnected.");
			}
		}		
	}
	
}