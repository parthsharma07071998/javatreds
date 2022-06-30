package com.xlx.treds.master.bo;

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
import com.xlx.treds.master.bean.HolidayMasterBean;
import com.xlx.treds.user.bean.AppUserBean;

public class HolidayMasterUploader extends BaseFileUploader {

    public static final String SEPERATOR = ","; 
    public static final String FILE_TYPE = "HOLIDAYMASTER";
    private BeanMeta holidayMasterBeanMeta;
    private HolidayMasterBO holidayMasterBO;
    private List<String> headerFieldList;
    public static final String PARAM_ZIPFILENAME = "pdfZipFile";
    public HashMap<String, String> newFileNameHash = null;
    
    public HolidayMasterUploader() {
        super();
        holidayMasterBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
        holidayMasterBO = new HolidayMasterBO();
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
        Map<String, BeanFieldMeta> lFieldMap = holidayMasterBeanMeta.getFieldMap();
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
        HolidayMasterBean lHolidayMasterBean = new HolidayMasterBean();

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
            List<ValidationFailBean> lValidationFailBeans = holidayMasterBeanMeta.validateAndParse(lHolidayMasterBean, lMap, null, lFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
//            	lHolidayMasterBean.setDisableShifting(YesNo.Yes);
            	holidayMasterBO.save(pFileUploadBean.getExecutionContext(), lHolidayMasterBean, (AppUserBean)pFileUploadBean.getAppUserBean(), true);
            	lSuccess = true;
            }
        } catch (Exception lException) {
            lMessage = "Internal Error : " + lException.getMessage();
        }
        lResponseBuffer.append(SEPERATOR);
        lResponseBuffer.append(SEPERATOR).append(lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL);
        lResponseBuffer.append(SEPERATOR);
        if (lMessage != null)
            lResponseBuffer.append(lMessage);
        lResponseBuffer.append(FileUploaderFactory.RECORD_SEPARATOR);
        return lSuccess;
    }
    
}
