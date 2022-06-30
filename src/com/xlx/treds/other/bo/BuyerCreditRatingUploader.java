package com.xlx.treds.other.bo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploadHelper;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.other.bean.BuyerCreditRatingBean;
import com.xlx.treds.user.bean.AppUserBean;

public class BuyerCreditRatingUploader extends BaseFileUploader {
    public static final String SEPERATOR = "|"; 
    public static final String FILE_TYPE = "BUYERCREDITRATING";
    private BuyerCreditRatingBO buyerRatingBO;
    private BeanMeta buyerCreditRatingBeanMeta;
    private List<String> headerFieldList;
    public static final String PARAM_ZIPFILENAME = "pdfZipFile";
    public HashMap<String, String> newFileNameHash = null;
    
    public BuyerCreditRatingUploader() {
        super();
        buyerRatingBO = new BuyerCreditRatingBO();
        buyerCreditRatingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BuyerCreditRatingBean.class);
    }
    
    @Override
    public int getHeaderCount() {
        return 1;
    }
    
    @Override
    public String getFileStore() {
        return FILE_TYPE;
    }
    
    @Override
    public boolean buildReturnFile() {
        return true;
    }

    @Override
    public void onHeader(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
        headerFieldList = Arrays.asList(CommonUtilities.splitString(pRecord, SEPERATOR));
        Map<String, BeanFieldMeta> lFieldMap = buyerCreditRatingBeanMeta.getFieldMap();
        for (String lHeader : headerFieldList) {
            if (!lFieldMap.containsKey(lHeader))
                throw new Exception("Invalid field in header : " + lHeader);
        }
        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord).append(SEPERATOR).append("id").append(SEPERATOR);
        lResponseBuffer.append("uploadStatus").append(SEPERATOR).append("uploadMessage").append(FileUploaderFactory.RECORD_SEPARATOR);
        //
    	//
        //For setting the filenames from the csv file to actual file names after storage.
    	Map<String, String> lFormParameters = pFileUploadBean.getFormParameters();
    	String lZipFileName = null;
    	if(lFormParameters!=null && lFormParameters.size() > 0){
    		lZipFileName = lFormParameters.get(PARAM_ZIPFILENAME);
    	}
    	if(CommonUtilities.hasValue(lZipFileName)){
            byte[] lContent = getZipFileContent(lZipFileName);
            if(lContent!=null){
                Map<String, ByteArrayOutputStream> lNameWiseFiles = CommonUtilities.unZIPAll(lContent);
                if(lNameWiseFiles!=null){
                	 newFileNameHash = saveZipFileContent(lNameWiseFiles);
                }
            }
    	}
    	//
    }
    
    @Override
    public boolean onData(int pIndex, String pRecord, FileUploadBean pFileUploadBean) {
        String[] lValues = CommonUtilities.splitString(pRecord, SEPERATOR);
        Map<String, Object> lMap = new HashMap<String, Object>();
        int lCount = headerFieldList.size()<lValues.length?headerFieldList.size():lValues.length;
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            if (StringUtils.isNotBlank(lValues[lPtr]))
                lMap.put(headerFieldList.get(lPtr), lValues[lPtr]);
        }
        BuyerCreditRatingBean lBuyerCreditRatingBean = new BuyerCreditRatingBean();

        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord);
        Long lId = null;
        boolean lSuccess = false;
        String lMessage = null;
        List<String> lFieldList = new ArrayList<String>();
        List<String> lRemoveList = new ArrayList<String>();
        try {
        	for(int lPtr=0; lPtr < headerFieldList.size(); lPtr++){
        		if(!lRemoveList.contains(headerFieldList.get(lPtr))){
        			lFieldList.add(headerFieldList.get(lPtr));
        		}
        	}
        	//
            List<ValidationFailBean> lValidationFailBeans = buyerCreditRatingBeanMeta.validateAndParse(lBuyerCreditRatingBean, lMap, null, lFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
            	if(newFileNameHash!=null){
            		HashMap<String, String> lNewFileNameHash = newFileNameHash;
                    if(CommonUtilities.hasValue(lBuyerCreditRatingBean.getRatingFile()))
                    	lBuyerCreditRatingBean.setRatingFile(lNewFileNameHash.get(lBuyerCreditRatingBean.getRatingFile().toLowerCase()));
            	}else{
            		lBuyerCreditRatingBean.setRatingFile(" ");
            	}
            	lBuyerCreditRatingBean.setFileId(pFileUploadBean.getId());
            	if (lBuyerCreditRatingBean.getExpiryDays()==null) {
            		lBuyerCreditRatingBean.setExpiryDays(new Long("365"));
            	}else {
            		if (lBuyerCreditRatingBean.getExpiryDays() > new Long("365") ) {
            			throw new CommonBusinessException(" Expiry Days cannot be greater than 365 days .");
            		}
            	}
                buyerRatingBO.save(pFileUploadBean.getExecutionContext(), lBuyerCreditRatingBean, (AppUserBean)pFileUploadBean.getAppUserBean(), true);
                lId = lBuyerCreditRatingBean.getId();
                lSuccess = true;
            }
        } catch (Exception lException) {
            lMessage = "Internal Error : " + lException.getMessage();
        }
        lResponseBuffer.append(SEPERATOR);
        if (lId != null)
            lResponseBuffer.append(lBuyerCreditRatingBean.getId());
        lResponseBuffer.append(SEPERATOR).append(lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL);
        lResponseBuffer.append(SEPERATOR);
        if (lMessage != null)
            lResponseBuffer.append(lMessage);
        lResponseBuffer.append(FileUploaderFactory.RECORD_SEPARATOR);
        return lSuccess;
    }
     
    private byte[] getZipFileContent(String pZipFileName) throws Exception{
    	HashMap lConfig = RegistryHelper.getInstance().getStructureUsingKey(CommonAppConstants.REGISTRY_FILEUPLOADS, "BUYERRATINGZIP");
    	if(lConfig != null){
            String lUploadPath = (String)lConfig.get(CommonAppConstants.ATTRIBUTE_UPLOADPATH);
            String lDBStore = (String)lConfig.get(CommonAppConstants.ATTRIBUTE_DBSTORE);
    	    return FileUploadHelper.readFile(pZipFileName, lUploadPath, lDBStore);
    	}
    	return null;
    }
    
    private HashMap<String, String> saveZipFileContent(Map<String, ByteArrayOutputStream> lNameWiseFiles) throws Exception {
        HashMap lConfig = RegistryHelper.getInstance().getStructureUsingKey(CommonAppConstants.REGISTRY_FILEUPLOADS, "BUYERCREDITRATINGS");
        if (lConfig == null)
            throw new CommonBusinessException("Invalid file type");
        String lUploadPath = (String)lConfig.get(CommonAppConstants.ATTRIBUTE_UPLOADPATH);
        String lDBStore = (String)lConfig.get(CommonAppConstants.ATTRIBUTE_DBSTORE);
        
        HashMap<String, String> lNewFileNameHash = new HashMap<String, String>(); 
        for(String lInnerFileName : lNameWiseFiles.keySet()){
            String lFileName = lInnerFileName.toLowerCase();
            String lNewFileName = FileUploadHelper.saveFile(lFileName, lNameWiseFiles.get(lInnerFileName).toByteArray(), lUploadPath, lDBStore);
            lNewFileNameHash.put(lFileName, lNewFileName);
        }
        return lNewFileNameHash;
    }
   
}
