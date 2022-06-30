package com.xlx.treds.sftp.rest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.base.FileParameter;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.Secured;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.sftp.SFTPClientHolder;
import com.xlx.treds.sftp.SFTPConfigBean;
import com.xlx.treds.sftp.SFTPProcessBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/sftp")
public class SFTPResource {

	
    public SFTPResource() {
        super();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/sftp.jsp").forward(pRequest, pResponse);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/config")
    public String getConfig(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
    	Map<String, SFTPConfigBean> lSFTPConfigMap = SFTPClientHolder.getInstance(SFTPClientHolder.KEY_TREDSSFTPCLIENT).getConfiguration();
    	Map<String,Object> lMap = null;
    	List<Map<String,Object>> lRtnList = new ArrayList<>();
    	for (String lNameStr : lSFTPConfigMap.keySet()) {
    		lMap = new HashMap<>();
    		lMap.put("name",lNameStr);
    		lMap.put("in", new ArrayList<Map<String,Object>>());
    		lMap.put("out", new ArrayList<Map<String,Object>>());
    		List<Map<String,Object>> lArrList = null;
    		for (SFTPProcessBean lProcessBean:lSFTPConfigMap.get(lNameStr).getInList()) {
    			lArrList = (List<Map<String,Object>>) lMap.get("in");
    			Map<String, Object> lTmpMap = new HashMap<>();
    			List<File> lTmpFiles = (List<File>) FileUtils.listFiles(new File(lProcessBean.getDestPath()), null, false);
    			Object[] lObjs = lTmpFiles.toArray();
    			File[] lFileObjs = new File[lObjs.length];
    			for (int lPtrX = 0; lPtrX < lObjs.length; lPtrX++) {
    				lFileObjs[lPtrX] = (File) lObjs[lPtrX];
    			}
    			LastModifiedFileComparator comparator = new LastModifiedFileComparator();
    		    File[] lSortedFiles = comparator.sort(lFileObjs);
    			List<String> lFilesOnRemoteSystem = new ArrayList<String>();
    			for (int lPtrX = lSortedFiles.length-1; lPtrX >= 0 ; lPtrX--) {
    				if ( ! lFilesOnRemoteSystem.contains((lSortedFiles[lPtrX]).getName())) {
    					lFilesOnRemoteSystem.add((lSortedFiles[lPtrX]).getName());
    				}
    			}
    			lTmpMap.put("key",lProcessBean.getName());
    			lTmpMap.put("list",lFilesOnRemoteSystem);
    			lArrList.add(lTmpMap);
    		}
    		for (SFTPProcessBean lProcessBean:lSFTPConfigMap.get(lNameStr).getOutList()) {
    			lArrList = (List<Map<String,Object>>) lMap.get("out");
    			Map<String, Object> lTmpMap = new HashMap<>();
    			List<File> lTmpFiles = (List<File>) FileUtils.listFiles(new File(lProcessBean.getSrcPath()), null, false);
    			Object[] lObjs = lTmpFiles.toArray();
    			File[] lFileObjs = new File[lObjs.length];
    			for (int lPtrX = 0; lPtrX < lObjs.length; lPtrX++) {
    				lFileObjs[lPtrX] = (File) lObjs[lPtrX];
    			}    			
    			LastModifiedFileComparator comparator = new LastModifiedFileComparator();
    		    File[] lSortedFiles = comparator.sort(lFileObjs);
    			List<String> lFilesOnRemoteSystem = new ArrayList<String>();
    			for (int lPtrX = lSortedFiles.length-1; lPtrX >= 0 ; lPtrX--) {
    				if ( ! lFilesOnRemoteSystem.contains((lSortedFiles[lPtrX]).getName())) {
    					lFilesOnRemoteSystem.add((lSortedFiles[lPtrX]).getName());
    				}
    			}
    			lTmpMap.put("key",lProcessBean.getName());
    			lTmpMap.put("list",lFilesOnRemoteSystem);
    			lTmpMap.put("upload",true);
    			lArrList.add(lTmpMap);
    			lTmpMap = new HashMap<>();
    			lTmpFiles = (List<File>) FileUtils.listFiles(new File(lProcessBean.getSuccess()), null, false);
    			lObjs = lTmpFiles.toArray();
    			lFileObjs = new File[lObjs.length];
    			for (int lPtrX = 0; lPtrX < lObjs.length; lPtrX++) {
    				lFileObjs[lPtrX] = (File) lObjs[lPtrX];
    			}
    			comparator = new LastModifiedFileComparator();
    		    lSortedFiles = comparator.sort(lFileObjs);
    			lFilesOnRemoteSystem = new ArrayList<String>();
    			for (int lPtrX = lSortedFiles.length-1; lPtrX >= 0 ; lPtrX--) {
    				if ( ! lFilesOnRemoteSystem.contains((lSortedFiles[lPtrX]).getName())) {
    					lFilesOnRemoteSystem.add((lSortedFiles[lPtrX]).getName());
    				}
    			}
    			lTmpMap.put("key","SUCCESS"+"-"+lProcessBean.getName());
    			lTmpMap.put("list",lFilesOnRemoteSystem);
    			lArrList.add(lTmpMap);
    		}
    		lRtnList.add(lMap);
    	}
		return new JsonBuilder(lRtnList).toString();
    }
    
    @POST
    @Path("/download")
    @Secured
    public Response downloadFile(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse,String pFilter) throws Exception {
    	Map<String, SFTPConfigBean> lSFTPConfigMap = SFTPClientHolder.getInstance(SFTPClientHolder.KEY_TREDSSFTPCLIENT).getConfiguration();
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        if (lMap.containsKey("file")){
        	String[] lKeyArr = CommonUtilities.splitString(lMap.get("file").toString(), CommonConstants.KEY_SEPARATOR);
        	SFTPConfigBean lSftpConfigBean = lSFTPConfigMap.get(lKeyArr[0]);
        	List<SFTPProcessBean> lBeanList = null;
        	if (lKeyArr[1].equals("in")) {
        		lBeanList = lSftpConfigBean.getInList();
        	}else if  (lKeyArr[1].equals("out")) {
        		lBeanList = lSftpConfigBean.getOutList();
        	}
        	String lPath = null;
        	for (SFTPProcessBean lBean:lBeanList) {
        		if (lKeyArr[2].contains("-")) {
        			String[] lArr = CommonUtilities.splitString(lKeyArr[2], "-");
        			if (lBean.getName().equals(lArr[1])){
        				if (lBean.isIn()) {
        				}else {
        					lPath = lBean.getSuccess();
        					break;
        				}	
        			}
        		}
        		if (lBean.getName().equals(lKeyArr[2])) {
        			if (lBean.isIn()) {
    					lPath = lBean.getDestPath();
    					break;
    				}else {
    					lPath = lBean.getSrcPath();
    					break;
    				}
        		}
        	}
        	return TredsHelper.getInstance().sendFileContents(lKeyArr[3],FileUtils.readFileToByteArray(new File(lPath+File.separator+lKeyArr[3])));
        }
        return null;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)  
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/upload/{filePath}")
    @Secured
    public void uploadFile(@Context HttpServletRequest pRequest,@PathParam("filePath") String pFilePath) throws Exception {
    	Map<String, SFTPConfigBean> lSFTPConfigMap = SFTPClientHolder.getInstance(SFTPClientHolder.KEY_TREDSSFTPCLIENT).getConfiguration();
    	Map<String, Object> lParameterMap = getMultiplartRequestParameters(pRequest);
        FileParameter lFileParameter = (FileParameter)lParameterMap.get("filecontent");
        String[] lKeyArr = CommonUtilities.splitString(pFilePath, CommonConstants.KEY_SEPARATOR);
        SFTPConfigBean lBean = lSFTPConfigMap.get(lKeyArr[0]);
        for(SFTPProcessBean lSFTPProcessBean : lBean.getOutList()) {
        	if(lKeyArr[1].equals(lSFTPProcessBean.getName())){
        		 FileUtils.writeByteArrayToFile(new File(lSFTPProcessBean.getSrcPath()+"//"+lFileParameter.getFileName()), lFileParameter.getContents() );
        	}
        }
       
    }
    
    private Map<String, Object> getMultiplartRequestParameters(HttpServletRequest pRequest)  throws FileUploadException {
        Map<String, Object> lMap = new HashMap<String, Object>();
        ServletFileUpload lUpload = new ServletFileUpload(new DiskFileItemFactory());
        List lItems = lUpload.parseRequest(pRequest);
        Iterator lItemIterator = lItems.iterator();
        String lParameterName;
        while (lItemIterator.hasNext()){
            FileItem lItem = (FileItem)lItemIterator.next();
            lParameterName = lItem.getFieldName();
            if (lItem.isFormField())
                lMap.put(lParameterName, lItem.getString());
            else
                lMap.put(lParameterName, new FileParameter(lItem.getName(), lItem.getContentType(), lItem.get()));
        }
        return lMap;
    }
    

}