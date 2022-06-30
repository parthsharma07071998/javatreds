package com.xlx.treds.user.bo;

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
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bo.PurchaserSupplierLinkBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

public class RegistrationUploader extends BaseFileUploader{
	
	public static final String SEPERATOR = "|"; 
	public static final String FILE_TYPE = "REGUSER";
	private BeanMeta appUserBeanMeta;
	private List<String> headerFieldList;
	private AppUserBean loggedInUserBean;
	private AppUserBO appUserBO;
	private RegisterBO registerBO;
	private static final String PURCHASER = "P";
	private static final String SUPPLIER = "S";
	private static final String FINANCIER = "F";
	
	public RegistrationUploader(){
		super();
		appUserBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
		appUserBO = new AppUserBO();
		registerBO = new RegisterBO();
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
        		if(!lAppEntityBean.isPlatform() && !lAppEntityBean.isRegistringEntity())
        			throw new CommonBusinessException("Only Admin or Registering Entity is allowed to create/upload links.");
        	}
        }
        
	}
	
	@Override
	public void onHeader(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
        headerFieldList = Arrays.asList(CommonUtilities.splitString(pRecord, SEPERATOR));
        Map<String, BeanFieldMeta> lFieldMap = appUserBeanMeta.getFieldMap();
        for (String lHeader : headerFieldList) {
            if (!lFieldMap.containsKey(lHeader)) {
                throw new Exception("Invalid field in header : " + lHeader);
            }
        }
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
        AppUserBean lAppUserBean = new AppUserBean();
        StringBuilder lResponseBuffer = pFileUploadBean.getResponseBuffer();
        lResponseBuffer.append(pRecord);
        boolean lSuccess = false;
        String lMessage = null;
        String lCompanyCode = null;
        AppEntityBean lAppEntityBean = null;
        try {
        	String lEntityType = lMap.get("entityType").toString();
        	if (lMap.get("companyName")==null){
        		throw new CommonBusinessException("Company Name is Mandatory");
        	}
            lMap.put("entityType", getEntityType(lEntityType));

        	lAppUserBean.setLoginId(TredsHelper.getInstance().createCompanyOrEntityCode(pFileUploadBean.getExecutionContext().getConnection(),lMap.get("companyName").toString()));
            List<ValidationFailBean> lValidationFailBeans = appUserBeanMeta.validateAndParse(lAppUserBean, lMap, null, headerFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
                if (lMessage == null) {
//                	if((PURCHASER.equals(lEntityType) || SUPPLIER.equals(lEntityType)) && lAppUserBean.getConstitution() == null){
//                		throw new CommonBusinessException("Please select a valid Constitution");
//                	}
                	registerBO.save(pFileUploadBean.getExecutionContext(), lAppUserBean, loggedInUserBean, true);
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
        lResponseBuffer.append(SEPERATOR).append(lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL);
        if (lMessage != null){
            lResponseBuffer.append(SEPERATOR);
            lResponseBuffer.append(lMessage);
        }
        lResponseBuffer.append(FileUploaderFactory.RECORD_SEPARATOR);
        return lSuccess;
	}
	
	private String getEntityType(String pEntity) throws Exception{
		if (PURCHASER.equals(pEntity)){
			return AppEntityBean.EntityType.Purchaser.getCode();
		}else if (SUPPLIER.equals(pEntity)){
			return AppEntityBean.EntityType.Supplier.getCode();
		}else if (FINANCIER.equals(pEntity)){
			return AppEntityBean.EntityType.Financier.getCode();
		}else{
			throw new CommonBusinessException("Please select a valid entity Type");
		}
	}

}
