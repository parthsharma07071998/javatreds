package com.xlx.treds;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

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
import com.xlx.common.utilities.DBHelper;
import com.xlx.treds.sftp.AdapterSFTPConfigBean;

import groovy.json.JsonSlurper;

public class AdapterSFTPClient extends ManagedThread  {
	public static Logger logger = Logger.getLogger(AdapterSFTPClient.class);

	public String SETTINGS_JSON = "bhelsftpclient.json";
	
	public static final String KEY_FREQUENCY = "frequency";
	public static final String KEY_EXTENTIONS = "extensions";
	public static final String KEY_TYPE = "type";
	public static final String KEY_SRCPATH = "srcPath";
	public static final String KEY_DESTPATH = "destPath";
	public static final String KEY_SUCCESS = "success";
	public static final String KEY_HOST = "host";
	public static final String KEY_USER = "user";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_IP = "ip";
	public static final String KEY_PORT = "port";
	public static final String KEY_KEY = "key";
	public static final String KEY_INSRCPATH = "inSrcPath";
	public static final String KEY_INDESTPATH = "inDestPath";
	public static final String KEY_INSUCCESS = "inSuccess";
	public static final String KEY_ENTITYCODE = "entityCode";
	public static final String KEY_INSTDETINOBLIG = "instDetInOblig";
	public static final String KEY_OBLIGLIST = "obligList";
	public static final String KEY_INSTLIST = "instList";
	public static final String KEY_FACTUNITLIST = "factUnitList";

	private AdapterSFTPConfigBean configBean;
	private static final String[] SKIP_FOLDER_LIST = new String[] { ".", "..", "OUT", "lost+found", "SFTP-config", "sftpd-AuthKey", "system_files_backup", "TREDS" };
	private static Set<String> SKIP_FOLDER_HASH = new HashSet<String>();
	
	private long currentTime = 0;
	private long lastSendTime = 0;
	private String proxyIp;
	private int proxyPort;

	public AdapterSFTPClient(String pSettingFileName) {
		SETTINGS_JSON = pSettingFileName;
		initialize();
	}

	private void initialize() {
		currentTime = System.currentTimeMillis();
		lastSendTime = currentTime;
		String lFolder = null;
		String lSuccess = null;

		InputStream lInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(SETTINGS_JSON);
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map lMasterConfig = (Map) lJsonSlurper.parse(lInputStream);
		lFolder = (String) lMasterConfig.get(KEY_SRCPATH);
		lSuccess = (String) lMasterConfig.get(KEY_SUCCESS);
		String lType = (String) lMasterConfig.get(KEY_TYPE);
		String lExtensions = (String) lMasterConfig.get(KEY_EXTENTIONS);
		AdapterSFTPConfigBean lSftpConfigBean =  new AdapterSFTPConfigBean(lFolder, (String) lMasterConfig.get(KEY_DESTPATH), lSuccess,lExtensions,lType,
				(String) lMasterConfig.get(KEY_USER), (String) lMasterConfig.get(KEY_PASSWORD), (String) lMasterConfig.get(KEY_IP),
				(Integer) lMasterConfig.get(KEY_PORT), (String) lMasterConfig.get(KEY_KEY),(String) lMasterConfig.get(KEY_INSRCPATH)
				,(String) lMasterConfig.get(KEY_INDESTPATH),(String) lMasterConfig.get(KEY_INSUCCESS), (String) lMasterConfig.get(KEY_ENTITYCODE),(String) lMasterConfig.get(KEY_INSTDETINOBLIG)
				,(List<String>) lMasterConfig.get(KEY_OBLIGLIST) ,(List<String>) lMasterConfig.get(KEY_INSTLIST) ,(List<String>) lMasterConfig.get(KEY_FACTUNITLIST));
		configBean = lSftpConfigBean;
		for(String lTmpFolder : SKIP_FOLDER_LIST){
			//SKIP_FOLDER_
			SKIP_FOLDER_HASH.add(lTmpFolder);
		}
	}

	@Override
	public boolean initThread() {
		return true;
	}

	@Override
	public void serviceThread() {
		currentTime = System.currentTimeMillis();
		if (currentTime > lastSendTime + 600000) {
			Session lSession = null;
			Channel lChannel = null;
			try {
				lSession = getSSHSession(configBean);
				lSession.connect();
				lChannel = lSession.openChannel("sftp");
				lChannel.connect();
				List<File> lValidFiles = null;
				processFilesIn(lChannel,lValidFiles);
				TredsHelper.getInstance().createAndReadFiles(configBean);
				processFiles(lChannel,lValidFiles);
			}catch(Exception lEx) {
				logger.error("Error in SFTPClient.serviceThread  ",lEx);
			}finally {
				if (lSession != null)
					lSession.disconnect();
			}
			lastSendTime = currentTime;
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}		
			
	}

	
	@Override
	public void destroyThread() {
		// TODO Auto-generated method stub
		
	}
	
	private static final String encryptionKey           = "ABCDEFGHIJKLMNOP";
    private static final String characterEncoding       = "UTF-8";
    private static final String cipherTransformation    = "AES/CBC/PKCS5PADDING";
    private static final String aesEncryptionAlgorithem = "AES";
	    
	    
    /**
     * Method for Encrypt Plain String Data
     * @param plainText
     * @return encryptedText
     */
    public static String encrypt(String plainText) {
        String encryptedText = "";
        try {
            Cipher cipher   = Cipher.getInstance(cipherTransformation);
            byte[] key      = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivparameterspec);
            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF8"));
            Base64.Encoder encoder = Base64.getEncoder();
            encryptedText = encoder.encodeToString(cipherText);

        } catch (Exception E) {
             System.err.println("Encrypt Exception : "+E.getMessage());
        }
        return encryptedText;
    }

    public static String decrypt(String encryptedText) {
        String decryptedText = "";
        try {
            Cipher cipher = Cipher.getInstance(cipherTransformation);
            byte[] key = encryptionKey.getBytes(characterEncoding);
            SecretKeySpec secretKey = new SecretKeySpec(key, aesEncryptionAlgorithem);
            IvParameterSpec ivparameterspec = new IvParameterSpec(key);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivparameterspec);
            Base64.Decoder decoder = Base64.getDecoder();
            byte[] cipherText = decoder.decode(encryptedText.getBytes("UTF8"));
            decryptedText = new String(cipher.doFinal(cipherText), "UTF-8");

        } catch (Exception E) {
            System.err.println("decrypt Exception : "+E.getMessage());
        }
        return decryptedText;
    }
	    
    public Session getSSHSession(AdapterSFTPConfigBean pSFTPConfigBean) throws JSchException {
		JSch lSSHClient = new JSch();
		if(pSFTPConfigBean.getKey()!=null){
			lSSHClient.addIdentity(pSFTPConfigBean.getKey());
		}
		
		Session lSession = lSSHClient.getSession(pSFTPConfigBean.getUser(), pSFTPConfigBean.getHost(),
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
	
	    
    public void movefile(String pSource, String pDestination) throws Exception {
		Path source = Paths.get(pSource);
		Path destinationDir = Paths.get(pDestination);
		Files.createDirectories(destinationDir, new FileAttribute[] {});
		// System.out.println("copying " + source.toString());
		Path d2 = destinationDir.resolve(source.getFileName());
		// System.out.println("destination File=" + d2);
		Files.move(source, d2,java.nio.file.StandardCopyOption.REPLACE_EXISTING);
	}
	    
    private void processFiles(Channel pChannel, List<File> pValidFiles) throws Exception {
		ChannelSftp lSftpChannel = (ChannelSftp) pChannel;
		String lDestBasePath = configBean.getDestPath();
		File lSrcDirectory = new File(configBean.getSrcPath());

			if (CommonUtilities.hasValue(lDestBasePath))
				lSftpChannel.cd(lDestBasePath);

		Collection lTmpFiles = FileUtils.listFiles(lSrcDirectory, null, false);
		Object[] lFileObjs = lTmpFiles.toArray();
		pValidFiles = new ArrayList<File>();
		for (int lPtrX = 0; lPtrX < lFileObjs.length; lPtrX++) {
			File lSrcFile = (File) lFileObjs[lPtrX];
			if(!isValidFile(lSrcFile)) {
				logger.debug("Ignoring file " + lSrcFile);
				continue;
			}
			pValidFiles.add(lSrcFile);
			try {
				doFileTransfer(lSrcFile, lSftpChannel);
			} catch (Exception lException) {
				logger.info("error in processFiles: "+ lException.getMessage());
			}
		}
	}
    
	private void processFilesIn(Channel pChannel, List<File> pValidFiles) throws Exception {
		ChannelSftp lSftpChannel = (ChannelSftp) pChannel;
		String lDestBasePath = configBean.getInDestPath();
		File lSrcDirectory = null;
		logger.info("configBean.getInDestPath() : "+lDestBasePath );
		Collection lTmpFiles = FileUtils.listFiles(new File(lDestBasePath), null, false);
		Object[] lFileObjs = lTmpFiles.toArray();
		Set<String> lFilesOnRemoteSystem = new HashSet();
			for (int lPtrX = 0; lPtrX < lFileObjs.length; lPtrX++) {
				lFilesOnRemoteSystem.add(((File)lFileObjs[lPtrX]).getName());
		}
		lSrcDirectory = new File(configBean.getInSrcPath());
		if (CommonUtilities.hasValue(configBean.getInSrcPath())) {
			lSftpChannel.cd(configBean.getInSrcPath());
		}
		Vector<ChannelSftp.LsEntry> lServerFiles = lSftpChannel.ls("*");
		logger.info("lServerFiles size :  "+lServerFiles.size());
        for (ChannelSftp.LsEntry lEntry : lServerFiles) {
        	if (!(lEntry.getAttrs().isDir() || lEntry.getFilename().equals(".") || lEntry.equals(".."))) {
        		if (!lFilesOnRemoteSystem.contains(lEntry.getFilename())){
        			logger.info("lSftpChannel.get() start ");
        			logger.info("FileName :"+lEntry.getFilename());
        			lSftpChannel.get(lEntry.getFilename(), configBean.getInDestPath()+lEntry.getFilename());
        			logger.info("lSftpChannel.get() complete");
        			try {
        				logger.info("lSftpChannel.cd() start ");
        				logger.info("Path :: " + configBean.getInSrcPath()+"archive/");
        				lSftpChannel.cd(configBean.getInSrcPath()+"archive/");
            			logger.info("lSftpChannel.cd() complete");
            			logger.info("lSftpChannel.rename() start ");
            			lSftpChannel.rename( configBean.getInSrcPath()+lEntry.getFilename(),configBean.getInSrcPath()+"archive/"+lEntry.getFilename());
            			logger.info("lSftpChannel.rename() complete");
        			}catch (Exception e) {
        				logger.error("error ::" , e);
					}
        		}
        	}
        }
		return;
	}
		
	private boolean isValidFile(File pFile) {
		try {
			String lExtension = FilenameUtils.getExtension(pFile.getCanonicalPath());
			if(StringUtils.isNotBlank(configBean.getExtensions())) {
				if(configBean.isBlack() && configBean.getExtensions().toLowerCase().indexOf("."+lExtension.toLowerCase()) >= 0) {
					return false;
				} else if(!configBean.isBlack() && configBean.getExtensions().toLowerCase().indexOf("."+lExtension.toLowerCase()) < 0) {
					return false;
				}
			}
		} catch (IOException pException) {
			logger.debug("Err while getting extension",pException);
			return false;
		}
		return true;
	}
	
	private void doFileTransfer(File pSrcFile,ChannelSftp pSftpChannel) throws Exception {
			String lBasePath = configBean.getSrcPath();
			File lBaseFile = new File(lBasePath);
			String lDestBasePath = configBean.getDestPath();
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
			int lMode = ChannelSftp.OVERWRITE;
			SftpProgressMonitor lMonitor = new SSHProgressMonitor();
			logger.info("Putting file "+pSrcFile.getCanonicalPath()+" to " + lDestBasePath + lFtpRelPath + lFileName);
			pSftpChannel.put(pSrcFile.getCanonicalPath(),lDestBasePath + lFtpRelPath + lFileName, lMonitor, lMode);
			logger.info("Success file "+pSrcFile.getCanonicalPath()+" to " + lDestBasePath + lFtpRelPath + lFileName);
			movefile(pSrcFile.getCanonicalPath(), configBean.getSuccessPath() + lRelativePath);
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


}


