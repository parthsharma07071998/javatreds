package com.xlx.treds.auction.bo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

public class PurchaserSupplierLinkUploader extends BaseFileUploader {
	public static final String SEPERATOR = ","; 
	public static final String FILE_TYPE = "PURSUPLNK";
	//
	private PurchaserSupplierLinkBO purchaserSupplierLinkBO;
	private BeanMeta purchaserSupplierLinkBeanMeta;
    private List<String> headerFieldList;
    private AppUserBean loggedInUserBean;
	
	//
	public PurchaserSupplierLinkUploader(){
		super();
		purchaserSupplierLinkBO = new PurchaserSupplierLinkBO();
		purchaserSupplierLinkBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PurchaserSupplierLinkBean.class);
	}
	
    @Override
	public int getHeaderCount() {
		return 1;
	}
	@Override
	public boolean buildReturnFile() {
		return true;
	}

	@Override
	public String getFileStore() {
		return FILE_TYPE;
	}
	@Override
	public void onStart(FileUploadBean pFileUploadBean) throws Exception {
        Map<String, String> lParameters = pFileUploadBean.getFormParameters();
        //purchaser is not checked since the only the purchaser is allowed to upload link
        //check whether the logged in user is purchaser
        loggedInUserBean = (AppUserBean)AuthenticationHandler.getInstance().getUserDataSource().getUserBean(
                pFileUploadBean.getExecutionContext(), pFileUploadBean.getDomain(), pFileUploadBean.getLoginId());
        if(loggedInUserBean==null)
        	throw new CommonBusinessException("Loggedin User not found.");
        else{
        	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(loggedInUserBean.getDomain());
        	if(lAppEntityBean==null)
            	throw new CommonBusinessException("Loggedin Entity not found.");
        	else{
        		if(!lAppEntityBean.isPurchaser())
        			throw new CommonBusinessException("Only Purchaser is allowed to create/upload links.");
        	}
        }
        
	}
	@Override
	public void onHeader(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
        headerFieldList = Arrays.asList(CommonUtilities.splitString(pRecord, SEPERATOR));
        Map<String, BeanFieldMeta> lFieldMap = purchaserSupplierLinkBeanMeta.getFieldMap();
        for (String lHeader : headerFieldList) {
            if (!lFieldMap.containsKey(lHeader)) {
                throw new Exception("Invalid field in header : " + lHeader);
            }
        }
        if (!headerFieldList.contains("supplier"))
        	throw new CommonBusinessException("Supplier input parameter missing.");
        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord).append(SEPERATOR).append("id").append(SEPERATOR);
        lResponseBuffer.append("uploadStatus").append(SEPERATOR).append("uploadMessage").append(FileUploaderFactory.RECORD_SEPARATOR);
	}
	@Override
	public boolean onData(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
        String[] lValues = CommonUtilities.splitString(pRecord, SEPERATOR);
        Map<String, Object> lMap = new HashMap<String, Object>();
        int lCount = headerFieldList.size()<lValues.length?headerFieldList.size():lValues.length;
        //add the data as per the header list
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            if (StringUtils.isNotBlank(lValues[lPtr])){
                lMap.put(headerFieldList.get(lPtr), lValues[lPtr]);
            }
        }
        //add the purchaser also
        lMap.put("purchaser", loggedInUserBean.getDomain());
        //
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = new PurchaserSupplierLinkBean();

        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord);
        boolean lSuccess = false;
        String lMessage = null;
        String lSupplier = null;
        AppEntityBean lAppEntityBean = null;
        try {
            //
            List<ValidationFailBean> lValidationFailBeans = purchaserSupplierLinkBeanMeta.validateAndParse(lPurchaserSupplierLinkBean, lMap, null, headerFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
                if (lMessage == null) {
                	//check the supplier code
                	lSupplier = (String) lMap.get("supplier");
                    if(CommonUtilities.hasValue(lSupplier)){
                    	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lSupplier);
                    	if(lAppEntityBean == null)
                            throw new CommonBusinessException("Supplier : " + lSupplier + " not found.");
                    	else if (!lAppEntityBean.isSupplier())
                            throw new CommonBusinessException("Entity : " + lSupplier + " is not a supplier.");
                    }else{
                    	throw new CommonBusinessException("Supplier missing.");
                    }
                    purchaserSupplierLinkBO.save(pFileUploadBean.getExecutionContext(), lPurchaserSupplierLinkBean, loggedInUserBean, false, true, headerFieldList, false);
                    lSuccess = true;
                }
            }
        } catch (CommonBusinessException lException) {
            lMessage = lException.getMessage();
        } catch (Exception lException) {
            lMessage = "Internal Error : " + lException.getMessage();
            logger.error("Error while uploading file " + pFileUploadBean.getFileName(), lException);
        }
        lResponseBuffer.append(SEPERATOR);
        lResponseBuffer.append(lPurchaserSupplierLinkBean.getSupplier());
        lResponseBuffer.append(SEPERATOR).append(lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL);
        lResponseBuffer.append(SEPERATOR);
        if (lMessage != null)
            lResponseBuffer.append(lMessage);
        lResponseBuffer.append(FileUploaderFactory.RECORD_SEPARATOR);
        return lSuccess;
	}
	@Override
	public void onEndOfFile(FileUploadBean pFileUploadBean) throws Exception {
		//TODO: THINK WHAT TO DO
	}
	@Override
	public void onComplete(FileUploadBean pFileUploadBean) {
		//TODO: THINK WHAT TO DO
	}

}
