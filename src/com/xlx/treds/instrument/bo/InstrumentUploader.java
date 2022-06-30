package com.xlx.treds.instrument.bo;

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
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploadHelper;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.other.bean.CustomFieldBean;
import com.xlx.treds.other.bo.CustomFieldBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class InstrumentUploader extends BaseFileUploader {
    public static final String SEPERATOR = "|"; 
    public static final String FILE_TYPE = "INST";
    private InstrumentBO instrumentBO;
    private CustomFieldBO customFieldBO;
    private BeanMeta instrumentBeanMeta;
    private List<String> headerFieldList;
    public static final String PARAM_ZIPFILENAME = "imgZipFile";
    public HashMap<String, String> newFileNameHash = null;
    private GenericDAO<InstrumentBean> instrumentDAO;
    
    public InstrumentUploader() {
        super();
        instrumentBO = new InstrumentBO();
        customFieldBO = new CustomFieldBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
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
//        Map<String, BeanFieldMeta> lFieldMap = instrumentBeanMeta.getFieldMap();
//        for (String lHeader : headerFieldList) {
//            if (!lFieldMap.containsKey(lHeader))
//                throw new Exception("Invalid field in header : " + lHeader);
//        }
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
        InstrumentBean lInstrumentBean = new InstrumentBean();

        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord);
        Long lId = null;
        boolean lSuccess = false;
        String lMessage = null;
        List<String> lFieldList = new ArrayList<String>();
        List<String> lRemoveList = new ArrayList<String>();
        try {
        	//removing all the items that are to be fetched from Buyser/Seller Link
			String[] lTempList = new String[] { "autoConvert", "costBearingType", "buyerPercent","splittingPoint", "preSplittingCostBearer", "postSplittingCostBearer","chargeBearer","settleLeg3Flag","bidAcceptingEntityType","haircutPercent","cashDiscountPercent","cashDiscountValue" };
			for(int lPtr=0; lPtr< lTempList.length; lPtr++){
				lRemoveList.add(lTempList[lPtr]);
			}
        	for(int lPtr=0; lPtr < headerFieldList.size(); lPtr++){
        		if(!lRemoveList.contains(headerFieldList.get(lPtr))){
        			lFieldList.add(headerFieldList.get(lPtr));
        		}
        	}
        	//
        	//conversion of comma seperated keys to json
        	String lTmpFieldKey = "instrumentCreationKeys";
        	if(lFieldList.contains(lTmpFieldKey)){
            	for(int lPtr=0; lPtr < headerFieldList.size(); lPtr++){
            		if(lTmpFieldKey.equals(headerFieldList.get(lPtr))){
            			if(StringUtils.isNotEmpty(lValues[lPtr])){
                    		List<String> lTmpKeys = CommonUtilities.getDelimitedString(lValues[lPtr], ",");
                    		if(lTmpKeys!=null){
                    			lMap.put(lTmpFieldKey, new JsonBuilder(lTmpKeys).toString());
                    		}
            			}
            			break;
            		}
            	}
        	}
        	//
        	Map<String, Object> lDiffMap = TredsHelper.getInstance().getExtraPayloadRecd(instrumentDAO,lMap);
        	boolean lSubmit = false;
        	boolean lUpload = true;
        	if (lDiffMap!=null && lDiffMap.containsKey("submit")) {
        		lSubmit = Boolean.valueOf((String) lDiffMap.get("submit")).booleanValue();
        		if (lSubmit) {
        			lUpload = false;
        		}
        	}
            List<ValidationFailBean> lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, lMap, null, lFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
            	//manually set the supporting file names (non-Database) since they are not set in the validateAndPares function
            	lInstrumentBean.setSup1((String)lMap.get("sup1"));
            	lInstrumentBean.setSup2((String)lMap.get("sup2"));
            	lInstrumentBean.setSup3((String)lMap.get("sup3"));
            	lInstrumentBean.setSup4((String)lMap.get("sup4"));
            	lInstrumentBean.setSup5((String)lMap.get("sup5"));
            	//
            	if(newFileNameHash!=null){
            		HashMap<String, String> lNewFileNameHash = newFileNameHash;
                    if(CommonUtilities.hasValue(lInstrumentBean.getInstImage()))
                      	lInstrumentBean.setInstImage(lNewFileNameHash.get(lInstrumentBean.getInstImage().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getCreditNoteImage()))
                      	lInstrumentBean.setCreditNoteImage(lNewFileNameHash.get(lInstrumentBean.getCreditNoteImage().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup1()))
                     	lInstrumentBean.setSup1(lNewFileNameHash.get(lInstrumentBean.getSup1().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup2()))
                     	lInstrumentBean.setSup2(lNewFileNameHash.get(lInstrumentBean.getSup2().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup3()))
                     	lInstrumentBean.setSup3(lNewFileNameHash.get(lInstrumentBean.getSup3().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup4()))
                     	lInstrumentBean.setSup4(lNewFileNameHash.get(lInstrumentBean.getSup4().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup5()))
                     	lInstrumentBean.setSup5(lNewFileNameHash.get(lInstrumentBean.getSup5().toLowerCase()));
            	}else{
                    lInstrumentBean.setInstImage(" ");
            	}
            	//
                lInstrumentBean.setFileId(pFileUploadBean.getId());
                if (lInstrumentBean.getPurchaser()!=null && lDiffMap!=null) {
                	CustomFieldBean lFieldBean = new CustomFieldBean();
    				lFieldBean.setCode(lInstrumentBean.getPurchaser());
    				lFieldBean = customFieldBO.findBean(pFileUploadBean.getExecutionContext().getConnection(), lFieldBean);
    				if (lFieldBean!=null && lFieldBean.getId()!=null && lFieldBean.getId()>0) {
    					lInstrumentBean.setCfId(lFieldBean.getId());
    				}
                }
                instrumentBO.save(pFileUploadBean.getExecutionContext(), lInstrumentBean, (AppUserBean)pFileUploadBean.getAppUserBean(), lInstrumentBean.getId()==null, lUpload, lSubmit, "", false,null,lDiffMap);
                lId = lInstrumentBean.getId();
                lSuccess = true;
            }
        } catch (Exception lException) {
        	lException.printStackTrace();
            lMessage = "Internal Error : " + lException.getMessage();
        }
        lResponseBuffer.append(SEPERATOR);
        if (lId != null)
            lResponseBuffer.append(lInstrumentBean.getId());
        lResponseBuffer.append(SEPERATOR).append(lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL);
        lResponseBuffer.append(SEPERATOR);
        if (lMessage != null)
            lResponseBuffer.append(lMessage);
        lResponseBuffer.append(FileUploaderFactory.RECORD_SEPARATOR);
        return lSuccess;
    }
     
    private byte[] getZipFileContent(String pZipFileName) throws Exception{
    	HashMap lConfig = RegistryHelper.getInstance().getStructureUsingKey(CommonAppConstants.REGISTRY_FILEUPLOADS, "INSTRUMENTIMAGESZIP");
    	if(lConfig != null){
            String lUploadPath = (String)lConfig.get(CommonAppConstants.ATTRIBUTE_UPLOADPATH);
            String lDBStore = (String)lConfig.get(CommonAppConstants.ATTRIBUTE_DBSTORE);
    	    return FileUploadHelper.readFile(pZipFileName, lUploadPath, lDBStore);
    	}
    	return null;
    }
    
    private HashMap<String, String> saveZipFileContent(Map<String, ByteArrayOutputStream> lNameWiseFiles) throws Exception {
        HashMap lConfig = RegistryHelper.getInstance().getStructureUsingKey(CommonAppConstants.REGISTRY_FILEUPLOADS, "INSTRUMENTS");
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
