package com.xlx.treds.other.bean;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.GenericDAO;

public class RegistrationFilesBean extends UploadFileBean {

	public RegistrationFilesBean(){
		super();
	}

	public RegistrationFilesBean(IUploadFileBean pUploadFileBean){
		GenericDAO<UploadFileBean> uploadFileDAO = new GenericDAO<UploadFileBean>(UploadFileBean.class);
		uploadFileDAO.getBeanMeta().copyBean(pUploadFileBean, this);
	}

	public String getConstitution() {
    	//will alway return from key
    	String lKey = getKey();
    	if(CommonUtilities.hasValue(lKey)){
    		String[] lTemp = CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR);
    		return lTemp[1];
    	}
        return "";
    }

    public void setConstitution(String pConstitution) {
    	//will update the key
    	String lKey = getKey();
    	if(CommonUtilities.hasValue(lKey)){
    		String[] lTemp =  CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR);
    		lTemp[1] = pConstitution;
    		setKey(lTemp[0]+CommonConstants.KEY_SEPARATOR+lTemp[1]);
    	}else{
    		String[] lTemp = new String[]{ "", "" };
    		lTemp[1] = pConstitution;
    		setKey(lTemp[0]+CommonConstants.KEY_SEPARATOR+lTemp[1]);
    	}
    }

    public String getEntityType() {
    	//will alway return from key
    	String lKey = getKey();
    	if(CommonUtilities.hasValue(lKey)){
    		String[] lTemp =  CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR);
    		return lTemp[0];
    	}
        return "";
    }

    public void setEntityType(String pEntityType) {
    	//will update the key
    	String lKey = getKey();
    	if(CommonUtilities.hasValue(lKey)){
    		String[] lTemp =  CommonUtilities.splitString(lKey, CommonConstants.KEY_SEPARATOR);
    		lTemp[0] = pEntityType;
    		setKey(lTemp[0]+CommonConstants.KEY_SEPARATOR+lTemp[1]);
    	}else{
    		String[] lTemp = new String[]{ "", "" };
    		lTemp[0] = pEntityType;
    		setKey(lTemp[0]+CommonConstants.KEY_SEPARATOR+lTemp[1]);
    	}
    }
    
    

}