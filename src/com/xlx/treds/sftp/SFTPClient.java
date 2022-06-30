package com.xlx.treds.sftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;
import com.jcraft.jsch.UserInfo;
import com.xlx.common.file.sftp.SSHProgressMonitor;
import com.xlx.common.file.sftp.SSHUserInfo;
import com.xlx.common.io.ManagedThread;
import com.xlx.common.utilities.CommonUtilities;

import groovy.json.JsonSlurper;

public class SFTPClient extends ManagedThread {

	public static Logger logger = Logger.getLogger(SFTPClient.class);

	public String SETTINGS_JSON = "sftpclientSnorkel.json";
	
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_PROXYIP = "proxyip";
	public static final String KEY_PROXYPORT = "proxyport";
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
	public static final String KEY_IN = "in";
	public static final String KEY_OUT = "out";
	public static final String KEY_NAME = "name";

	private List<SFTPConfigBean> folderWiseConfigs;
	// Failed files wise list of host
	private Hashtable<String, ArrayList<SFTPConfigBean>> failedFilesMap;
	private int frequency;
	private String proxyIp;
	private int proxyPort;

	public SFTPClient(String pSettingFileName) {
		SETTINGS_JSON = pSettingFileName;
		initialize();
	}

	private void initialize() {
		folderWiseConfigs = new ArrayList<SFTPConfigBean>();
		String lFolder = null;
		String lSuccess = null;

		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		if(lMasterConfig.get(KEY_FREQUENCY)  != null) {
			frequency = (Integer) lMasterConfig.get(KEY_FREQUENCY);
		}
		if(frequency <= 0) {
			frequency = 60;
		}
		if(lMasterConfig.get(KEY_PROXYIP)  != null) {
			proxyIp = (String) lMasterConfig.get(KEY_PROXYIP);
			proxyPort = (Integer) lMasterConfig.get(KEY_PROXYPORT);
		}
		
		List<Map> lSettings = (List<Map>) lMasterConfig.get(KEY_CONFIG);

		int lCount = lSettings.size();
		for (int lPtr = 0; lPtr < lCount; lPtr++) {
			Map<String, Object> lSettingMap = (Map<String, Object>) lSettings.get(lPtr);

			List<Map> lHosts = (List<Map>) lSettingMap.get(KEY_HOSTS);
			boolean lIsIn = false;
			if(lHosts!=null && !lHosts.isEmpty()) {
				for (int lPtrX = 0; lPtrX < lHosts.size(); lPtrX++) {
					Map<String, Object> lHostCnf = lHosts.get(lPtrX);
					List<Map<String,String>> lInList = (List<Map<String, String>>) lHostCnf.get(KEY_IN);
					lIsIn=true;
					List<SFTPProcessBean> lInProcessBeanList = readInOutConfig(lInList,lIsIn);
					List<Map<String,String>> lOutList = (List<Map<String, String>>) lHostCnf.get(KEY_OUT);
					lIsIn=false;
					List<SFTPProcessBean> lOutProcessBeanList = readInOutConfig(lOutList,lIsIn);
					SFTPConfigBean lConfig = new SFTPConfigBean(
							(String) lHostCnf.get(KEY_NAME)
							, (String) lHostCnf.get(KEY_USER)
							, (String) lHostCnf.get(KEY_PASSWORD)
							, (Integer) lHostCnf.get(KEY_PORT)
							, (String) lHostCnf.get(KEY_IP)
							, (String) lHostCnf.get(KEY_KEY)
							, lInProcessBeanList
							, lOutProcessBeanList
						);
					folderWiseConfigs.add(lConfig);
				}
			}	
		}
	}

	@Override
	public boolean initThread() {
		return true;
	}

	@Override
	public void serviceThread() {
		try {
			if(folderWiseConfigs!=null && folderWiseConfigs.size() > 0 ) {
				for ( SFTPConfigBean lConfigBean : folderWiseConfigs) {
					//multiple host loop 
					//	host connnect 
					//	in folder move (get)
					//	out folder move (out)
					Session lSession = null;
					try {
						lSession = getSSHSession(lConfigBean);
						lSession.connect();
						Channel lChannel = lSession.openChannel("sftp");
						lChannel.connect();
						List<File> lValidFiles = null;
						for(SFTPProcessBean lProcessBean : lConfigBean.getInList()) {
							processFilesIn(lProcessBean,lChannel,lValidFiles);
						}
						for(SFTPProcessBean lProcessBean : lConfigBean.getOutList()) {
							processFiles(lProcessBean,lChannel,lValidFiles);
						}
					}catch(Exception lEx) {
						logger.info("Error in SFTPClient.serviceThread : "+lEx.getMessage());
					}finally {
							if (lSession != null)
								lSession.disconnect();
						}
					} // For on configs ends
				}
		} catch (Exception pException) {
			logger.debug("Err", pException);
		}
		try {
			Thread.sleep(1000 * frequency);
		} catch (InterruptedException lInterruptedException) {
		}
	}
	
	private boolean isValidFile(File pFile,SFTPProcessBean pSFTPProcessBean) {
		try {
			String lExtension = FilenameUtils.getExtension(pFile.getCanonicalPath());
			if(StringUtils.isNotBlank(pSFTPProcessBean.getExtensions())) {
				if(pSFTPProcessBean.isBlack() && pSFTPProcessBean.getExtensions().toLowerCase().indexOf("."+lExtension.toLowerCase()) >= 0) {
					return false;
				} else if(!pSFTPProcessBean.isBlack() && pSFTPProcessBean.getExtensions().toLowerCase().indexOf("."+lExtension.toLowerCase()) < 0) {
					return false;
				}
			}
		} catch (IOException pException) {
			logger.debug("Err while getting extension",pException);
			return false;
		}
		return true;
	}
	
	private void doFileTransfer(File pSrcFile,ChannelSftp pSftpChannel,SFTPProcessBean pSFTPProcessBean) throws Exception {
			String lBasePath = pSFTPProcessBean.getSrcPath();
			File lBaseFile = new File(lBasePath);
			String lDestBasePath = pSFTPProcessBean.getDestPath();
			String lFileName = FilenameUtils.getName(pSrcFile.getCanonicalPath());
			String lFilePath = FilenameUtils.getFullPath(pSrcFile.getCanonicalPath());
			//
			String lRelativePath = lFilePath.replace(lBaseFile.getCanonicalPath(), "");
			String lFtpRelPath = lRelativePath.replace(File.separator, "/");
			String lCurrWorkingDir = pSftpChannel.pwd();
			//logger.info("PWD : " + lCurrWorkingDir);
			if(StringUtils.isNotBlank(lFtpRelPath)) {
				changeAndCreateDirs(pSftpChannel, lFtpRelPath);
				pSftpChannel.cd(lCurrWorkingDir);
			}
			if (!pSFTPProcessBean.isIn()) {
				int lMode = ChannelSftp.OVERWRITE;
				SftpProgressMonitor lMonitor = new SSHProgressMonitor();
				logger.info("Putting file "+pSrcFile.getCanonicalPath()+" to " + lDestBasePath + lFtpRelPath + lFileName);
				pSftpChannel.put(pSrcFile.getCanonicalPath(),lDestBasePath + lFtpRelPath + lFileName, lMonitor, lMode);
				logger.info("Success file "+pSrcFile.getCanonicalPath()+" to " + lDestBasePath + lFtpRelPath + lFileName);
				movefile(pSrcFile.getCanonicalPath(), pSFTPProcessBean.getSuccess() + lRelativePath);
			}else {
				pSftpChannel.cd(lBasePath);
				Vector<ChannelSftp.LsEntry> lServerFiles = pSftpChannel.ls("*");
	            for (ChannelSftp.LsEntry lEntry : lServerFiles) {
	            	if (!(lEntry.getAttrs().isDir() || lEntry.getFilename().equals(".") || lEntry.equals(".."))) {
	            		pSftpChannel.get(lEntry.getFilename(), pSFTPProcessBean.getDestPath()+File.separatorChar+lEntry.getFilename());
	            	}
	            }
			}
	}
	
	@Override
	public void destroyThread() {

	}

	private void changeAndCreateDirs(ChannelSftp pSftpChannel, String pRelativePath) throws SftpException {
		String[] lFolders = pRelativePath.split("/");
		for (String lFolder : lFolders) {
			if (lFolder.length() > 0) {
				try {
					pSftpChannel.cd(lFolder);
				} catch (SftpException e) {
					pSftpChannel.mkdir(lFolder);
					pSftpChannel.cd(lFolder);
				}
			}
		}
	}

	public void movefile(String pSource, String pDestination) throws Exception {
		Path source = Paths.get(pSource);
		Path destinationDir = Paths.get(pDestination);
		Files.createDirectories(destinationDir, new FileAttribute[] {});
		// System.out.println("copying " + source.toString());
		Path d2 = destinationDir.resolve(source.getFileName());
		// System.out.println("destination File=" + d2);
		Files.move(source, d2,java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	}

	public Session getSSHSession(SFTPConfigBean pSFTPConfigBean) throws JSchException {
		JSch lSSHClient = new JSch();
		if(pSFTPConfigBean.getKey()!=null){
			lSSHClient.addIdentity(pSFTPConfigBean.getKey());
		}
		
		Session lSession = lSSHClient.getSession(pSFTPConfigBean.getUser(), pSFTPConfigBean.getIp(),
				pSFTPConfigBean.getPort());
		// username and password will be given via UserInfo interface.
		UserInfo lUserInfo = new SSHUserInfo();
		lSession.setUserInfo(lUserInfo);
		if (pSFTPConfigBean.getPassword()!=null){
			lSession.setPassword(pSFTPConfigBean.getPassword());
		}
		java.util.Properties lConfig = new java.util.Properties();
		lConfig.put("StrictHostKeyChecking", "no");
		lSession.setConfig(lConfig);
		if(StringUtils.isNotEmpty(proxyIp)){
			lSession.setProxy(new ProxyHTTP(proxyIp,proxyPort));
		}
		return lSession;
	}
	
	private List<SFTPProcessBean> readInOutConfig(List<Map<String, String>> lList, boolean lIsIn) {
		List<SFTPProcessBean> lRtnList = new ArrayList<>();
		for (Map<String, String> lMap: lList) {
			lRtnList.add(new SFTPProcessBean(
					(String)lMap.get(KEY_NAME)
					,(String)lMap.get(KEY_SRCPATH)
					, (String)lMap.get(KEY_DESTPATH)
					, (String)lMap.get(KEY_SUCCESS)
					, (String)lMap.get(KEY_EXTENTIONS)
					, (String)lMap.get(KEY_TYPE)
					, lIsIn
					)
				);
		}
		return lRtnList;
	}
	
	private void processFiles(SFTPProcessBean pProcessBean, Channel pChannel, List<File> pValidFiles) throws Exception {
		ChannelSftp lSftpChannel = (ChannelSftp) pChannel;
		String lDestBasePath = pProcessBean.getDestPath();
		File lSrcDirectory = new File(pProcessBean.getSrcPath());

			if (CommonUtilities.hasValue(lDestBasePath))
				lSftpChannel.cd(lDestBasePath);

		Collection lTmpFiles = FileUtils.listFiles(lSrcDirectory, null, false);
		Object[] lFileObjs = lTmpFiles.toArray();
		pValidFiles = new ArrayList<File>();
		for (int lPtrX = 0; lPtrX < lFileObjs.length; lPtrX++) {
			File lSrcFile = (File) lFileObjs[lPtrX];
			if(!isValidFile(lSrcFile, pProcessBean)) {
				logger.debug("Ignoring file " + lSrcFile);
				continue;
			}
			pValidFiles.add(lSrcFile);
			try {
				doFileTransfer(lSrcFile, lSftpChannel, pProcessBean);
			} catch (Exception lException) {
				logger.info("error in processFiles: "+ lException.getMessage());
			}
		}
	}
	private void processFilesIn(SFTPProcessBean lProcessBean, Channel pChannel, List<File> pValidFiles) throws Exception {
		ChannelSftp lSftpChannel = (ChannelSftp) pChannel;
		String lDestBasePath = lProcessBean.getDestPath();
		File lSrcDirectory = new File(lProcessBean.getSrcPath());
		Collection lTmpFiles = FileUtils.listFiles(new File(lDestBasePath), null, false);
		Object[] lFileObjs = lTmpFiles.toArray();
		Set<String> lFilesOnRemoteSystem = new HashSet();
		for (int lPtrX = 0; lPtrX < lFileObjs.length; lPtrX++) {
			lFilesOnRemoteSystem.add(((File)lFileObjs[lPtrX]).getName());
		}
		if (lProcessBean.isIn()) {
			lSrcDirectory = new File(lProcessBean.getSrcPath());
			if (CommonUtilities.hasValue(lProcessBean.getSrcPath()))
				lSftpChannel.cd(lProcessBean.getSrcPath());
		}
		Vector<ChannelSftp.LsEntry> lServerFiles = lSftpChannel.ls("*");
        for (ChannelSftp.LsEntry lEntry : lServerFiles) {
        	if (!(lEntry.getAttrs().isDir() || lEntry.getFilename().equals(".") || lEntry.equals(".."))) {
        		if (!lFilesOnRemoteSystem.contains(lEntry.getFilename())){
        			lSftpChannel.get(lEntry.getFilename(), lProcessBean.getDestPath()+File.separatorChar+lEntry.getFilename());
        		}
        	}
        }
		//doFileTransfer(lSrcFile, lSftpChannel, lProcessBean);

		return;
		/*
		Collection lTmpFiles = FileUtils.listFiles(lSrcDirectory, null, false);
		Object[] lFileObjs = lTmpFiles.toArray();
		pValidFiles = new ArrayList<File>();
		for (int lPtrX = 0; lPtrX < lFileObjs.length; lPtrX++) {
			File lSrcFile = (File) lFileObjs[lPtrX];
			if(!isValidFile(lSrcFile, lProcessBean)) {
				logger.debug("Ignoring file " + lSrcFile);
				continue;
			}
			pValidFiles.add(lSrcFile);
			try {
				if(lProcessBean.isIn()) {
					doFileTransfer(lSrcFile, lSftpChannel, lProcessBean);
				}else {
					//DONE
					//doFileTransfer(lSrcFile, lSftpChannel, lProcessBean);
				}
			} catch (Exception lException) {
				logger.info("error in processFiles: "+ lException.getMessage());
			}
		}
		* */
	}
	
	public Map<String,SFTPConfigBean> getConfiguration() {
		Map<String, SFTPConfigBean> lRtnMap = new HashMap<>();
		for  (SFTPConfigBean lBean : folderWiseConfigs) {
			lRtnMap.put(lBean.getName(), lBean);
		}
		return lRtnMap;
	}

	
	public static void main(String[] args) {
		//getInstance();
		/*try {
			Thread.currentThread().sleep(10000);
			dropInstance();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}