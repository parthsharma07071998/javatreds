package com.xlx.treds.instrument.bo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.user.bean.AppUserBean;

public class InstrumentCounterUploader extends BaseFileUploader {
    public static final String SEPERATOR = "|"; 
    public static final String FILE_TYPE = "INSTCNTR";
    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
    private List<String> headerFieldList;
    public HashMap<String, String> newFileNameHash = null;
    
    public InstrumentCounterUploader() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
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
        Map<String, BeanFieldMeta> lFieldMap = instrumentBeanMeta.getFieldMap();
        for (String lHeader : headerFieldList) {
            if (!lFieldMap.containsKey(lHeader))
                throw new Exception("Invalid field in header : " + lHeader);
        }
        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord).append(SEPERATOR).append("id").append(SEPERATOR);
        lResponseBuffer.append("uploadStatus").append(SEPERATOR).append("uploadMessage").append(FileUploaderFactory.RECORD_SEPARATOR);
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
        	for(int lPtr=0; lPtr < headerFieldList.size(); lPtr++){
        		if(!lRemoveList.contains(headerFieldList.get(lPtr))){
        			lFieldList.add(headerFieldList.get(lPtr));
        		}
        	}
        	//
            List<ValidationFailBean> lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, lMap, null, lFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
            	lInstrumentBean.setFileId(pFileUploadBean.getId());
            	InstrumentBean lInstBean = instrumentBO.findBean(pFileUploadBean.getExecutionContext(), lInstrumentBean);
            	lInstBean.populateNonDatabaseFields();
            	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pFileUploadBean.getAppUserBean().getDomain());
            	String lFieldGroup = null;
            	List<String> lCopyFieldList = null; 
            	Map<String, String> lFormParam = pFileUploadBean.getFormParameters(); 
        		if(lAppEntityBean.isPurchaser()) {
        			lFieldGroup = InstrumentBean.FIELDGROUP_PURCHASER_BULK;
        			if (lFormParam!=null && lFormParam.containsKey("sftp")) {
        				lFieldGroup = null;
        				lCopyFieldList = new ArrayList<String>();
        				lCopyFieldList.addAll(headerFieldList);
        			}
        		}else if (lAppEntityBean.isSupplier()) {
        			lFieldGroup = InstrumentBean.FIELDGROUP_SUPPLIER_BULK; 
        		}
            	instrumentBeanMeta.copyBean(lInstrumentBean, lInstBean, lFieldGroup, lCopyFieldList);
            	lInstBean.populateNonDatabaseFields();
            	//lInstBean.setIsAllowedToRecalculateFromPercent(false);
            	lInstBean.setNetAmount(null);
            	instrumentBO.updateNetAmount(lInstBean, lInstBean.getIsAllowedToRecalculateFromPercent(), true);
                instrumentBO.updateCounterStatus(pFileUploadBean.getExecutionContext(), lInstBean, (AppUserBean)pFileUploadBean.getAppUserBean(), null);
                lId = lInstBean.getId();
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

}
