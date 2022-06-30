
package com.xlx.treds.user.bean;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.user.bean.UserFormatsBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.user.bean.RoleMasterBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.AppEntityBean.TwoFAType;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class AppUserBean implements IAppUserBean {
    
    public static final String FIELDGROUP_UPDATEENABLEAPI = "updateenableapi";
    public static final String FIELDGROUP_VALIDATEUPDATE = "validateUpdate";
    public enum Type implements IKeyValEnumInterface<Long>{
        Admin(Long.valueOf(1)),User(Long.valueOf(2)),RegisteringUser(Long.valueOf(3));
        
        private final Long code;
        private Type(Long pCode) {
            code = pCode;
        }
        public Long getCode() {
            return code;
        }
    }
	public static final String FIELD_MODE = "mode";
	public static final Long MODE_BROWSER = new Long(1);
	public static final Long MODE_MOBILE = new Long(2);
	//API WILL BE NULL

    private Long id;
    private String companyName;
    private String entityType;
    private String pan;
    private String constitution;
    private String domain;
    private String loginId;
    private String password1;
    private Timestamp passwordUpdatedAt1;
    private YesNo forcePasswordChange;
    private Yes resetPassword;
    private Status status;
    private String reason;
    private Long failedLoginCount;
    private Type type;
    private String salutation;
    private String firstName;
    private String middleName;
    private String lastName;
    private String telephone;
    private String mobile;
    private String email;
    private String altEmail;
    private YesNo enable2FA;
    private Yes enableAPI;
    private List<String> secretQuestions;
    private List<String> secretAnswers;
    private String secretText;
    private String secretImage;
    private String rsaTokenKey;
    private List<String> ipList;
    private Long recordCreator;
    private List<Long> rmIdList;
    private Yes fullOwnership;
    private Long ownerAuId;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private List<Long> checkersInstrument;
    private List<Long> checkersPlatformLimit;
    private List<Long> checkersBuyerLimit;
    private List<Long> checkersBuyerSellerLimit;
    private List<Long> checkersUserLimit;
    private List<Long> locationIdList;
    private String locationIds;
    private List<Long> checkersBid;
    private List<Long> checkersInstrumentCounter;
    private Long instLevel;
    private Long instCntrLevel;
    private Long bidLevel;
    private Long platformLimitLevel;
    private Long buyerLimitLevel;
    private Long buyerSellerLimitLevel;
    private Long userLimitLevel;
    private String rmLocation;
    //
    private BigDecimal minUserLimit;
    private BigDecimal maxUserLimit;
    
    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String pCompanyName) {
        companyName = pCompanyName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String pEntityType) {
        entityType = pEntityType;
    }

    public String getConstitution() {
        return constitution;
    }

    public void setConstitution(String pConstitution) {
        constitution = pConstitution;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pPan) {
        pan = pPan;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String pDomain) {
        domain = pDomain;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String pLoginId) {
        loginId = pLoginId;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String pPassword1) {
        password1 = pPassword1;
    }

    public Timestamp getPasswordUpdatedAt1() {
        return passwordUpdatedAt1;
    }

    public void setPasswordUpdatedAt1(Timestamp pPasswordUpdatedAt1) {
        passwordUpdatedAt1 = pPasswordUpdatedAt1;
    }

    public YesNo getForcePasswordChange() {
        return forcePasswordChange;
    }

    public void setForcePasswordChange(YesNo pForcePasswordChange) {
        forcePasswordChange = pForcePasswordChange;
    }

    public Yes getResetPassword() {
        return resetPassword;
    }

    public void setResetPassword(Yes  pResetPassword) {
    	resetPassword = pResetPassword;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String pReason) {
        reason = pReason;
    }

    public Long getFailedLoginCount() {
        return failedLoginCount;
    }

    public void setFailedLoginCount(Long pFailedLoginCount) {
        failedLoginCount = pFailedLoginCount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public String getSalutation() {
        return salutation;
    }

    public void setSalutation(String pSalutation) {
        salutation = pSalutation;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String pFirstName) {
        firstName = pFirstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String pMiddleName) {
        middleName = pMiddleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String pLastName) {
        lastName = pLastName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String pTelephone) {
        telephone = pTelephone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String pMobile) {
        mobile = pMobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String pEmail) {
        email = pEmail;
    }

    public String getAltEmail() {
        return altEmail;
    }

    public void setAltEmail(String pAltEmail) {
        altEmail = pAltEmail;
    }

    public YesNo getEnable2FA() {
        return enable2FA;
    }

    public void setEnable2FA(YesNo pEnable2fa) {
        enable2FA = pEnable2fa;
    }

    
    public String getOtherSettings() {
        if (StringUtils.isNotBlank(companyName) || StringUtils.isNotBlank(entityType) || 
                StringUtils.isNotBlank(constitution) || StringUtils.isNotBlank(pan)) {
            Map<String, Object> otherSettings = new HashMap<String, Object>();
            otherSettings.put(AppConstants.OTHR_SETTING_ENTITYNAME, companyName);
            otherSettings.put(AppConstants.OTHR_SETTING_ENTITYTYPE, entityType);
            otherSettings.put(AppConstants.OTHR_SETTING_CONSTITUTION, constitution);
            otherSettings.put(AppConstants.OTHR_SETTING_PAN, pan);
            return new JsonBuilder(otherSettings).toString();
        } else 
            return null;
    }

    public void setOtherSettings(String pOtherSettings) {
        if (StringUtils.isNotBlank(pOtherSettings)) {
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> otherSettings = (Map<String, Object>)lJsonSlurper.parseText(pOtherSettings);
            companyName = (String) otherSettings.get(AppConstants.OTHR_SETTING_ENTITYNAME);
            entityType = (String)otherSettings.get(AppConstants.OTHR_SETTING_ENTITYTYPE);
            constitution = (String)otherSettings.get(AppConstants.OTHR_SETTING_CONSTITUTION);
            pan = (String)otherSettings.get(AppConstants.OTHR_SETTING_PAN);
        }
        else
        {
        	companyName = null;
        	entityType = null;
        	constitution = null;
        	pan = null;
        }
    }

    public Yes getEnableAPI() {
        return enableAPI;
    }

    public void setEnableAPI(Yes pEnableAPI) {
        enableAPI = pEnableAPI;
    }

    public YesNo getSupplierFlag() {
    	if(entityType!=null)
            return entityType.substring(0, 1).equals(CommonAppConstants.YesNo.Yes.getCode())?CommonAppConstants.YesNo.Yes:CommonAppConstants.YesNo.No;
    	return CommonAppConstants.YesNo.No;
    }
    public YesNo getPurchaserFlag() {
    	if(entityType!=null)
            return entityType.substring(1, 2).equals(CommonAppConstants.YesNo.Yes.getCode())?CommonAppConstants.YesNo.Yes:CommonAppConstants.YesNo.No;
    	return CommonAppConstants.YesNo.No;
    }
    public YesNo getFinancierFlag() {
    	if(entityType!=null)
            return entityType.substring(2, 3).equals(CommonAppConstants.YesNo.Yes.getCode())?CommonAppConstants.YesNo.Yes:CommonAppConstants.YesNo.No;
    	return CommonAppConstants.YesNo.No;
    }
    public YesNo getPurchaserAggregatorFlag() {
    	if(entityType!=null){
    		if(entityType.substring(0, 1).equals("A") && 
    				entityType.substring(1, 2).equals("A") && 
    				entityType.substring(2, 3).equals("A") ){
    			return CommonAppConstants.YesNo.Yes;
    		}
    	}
    	return CommonAppConstants.YesNo.No;
    }
 
    public String getSecuritySettings() {
        HashMap<String, Object> lSettings = new HashMap<String, Object>();
        if ((secretQuestions != null) && (secretQuestions.size() > 0))
            lSettings.put("q", secretQuestions);
        if ((secretAnswers != null) && (secretAnswers.size() > 0))
            lSettings.put("a", secretAnswers);
        if (StringUtils.isNotBlank(secretText))
            lSettings.put("txt", secretText);
        if (StringUtils.isNotBlank(secretImage))
            lSettings.put("img", secretImage);
        if (StringUtils.isNotBlank(rsaTokenKey))
        	lSettings.put("rtk", rsaTokenKey);
        if (lSettings.isEmpty())
            return null;
        else
            return new JsonBuilder(lSettings).toString();
    }

    public void setSecuritySettings(String pSecuritySettings) {
        if (StringUtils.isNotBlank(pSecuritySettings)) {
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pSecuritySettings);
            secretQuestions = (List<String>)lMap.get("q");
            secretAnswers = (List<String>)lMap.get("a");
            secretText = (String)lMap.get("txt");
            secretImage = (String)lMap.get("img");
            rsaTokenKey = (String)lMap.get("rtk");
            if ((secretQuestions == null) || (secretAnswers == null) || (secretQuestions.size() != secretAnswers.size()) || (secretQuestions.size() == 0)) {
                secretQuestions = null;
                secretAnswers = null;
            }
        }
    }

    public List<String> getSecretQuestions() {
        return secretQuestions;
    }

    public void setSecretQuestions(List<String> pSecretQuestions) {
        secretQuestions = pSecretQuestions;
    }

    public List<String> getSecretAnswers() {
        return secretAnswers;
    }

    public void setSecretAnswers(List<String> pSecretAnswers) {
        secretAnswers = pSecretAnswers;
    }

    public String getSecretText() {
        return secretText;
    }

    public void setSecretText(String pSecretText) {
        secretText = pSecretText;
    }

    public String getSecretImage() {
        return secretImage;
    }

    public void setSecretImage(String pSecretImage) {
        secretImage = pSecretImage;
    }

    public String getIps() {
        if (ipList == null)
            return null;
        else 
            return new JsonBuilder(ipList).toString();
    }

    public void setIps(String pIps) {
        if (StringUtils.isNotBlank(pIps))
            ipList = (List<String>)new JsonSlurper().parseText(pIps);
        else
            ipList = null;
    }

    public List<String> getIpList() {
        return ipList;
    }

    public void setIpList(List<String> pIpList) {
        ipList = pIpList;
    }

    public List<Long> getRmIdList() {
        return rmIdList;
    }

    public void setRmIdList(List<Long> pRmIdList) {
        rmIdList = pRmIdList;
    }

    public String getRmIds() {
        if (rmIdList == null) return null;
        else return new JsonBuilder(rmIdList).toString();
    }

    public void setRmIds(String pRmIds) {
        if (pRmIds == null) rmIdList = null;
        else {
            List<Integer> lRmIdList = (List<Integer>)(new JsonSlurper().parseText(pRmIds));
            rmIdList = new ArrayList<Long>();
            for (Integer lRmId : lRmIdList) {
                rmIdList.add(Long.valueOf(lRmId.longValue()));
            }
        }
    }
    
    public String getRmListDesc(){
		String lRolesDesc = "";
		String lTmp = this.toString();
    	if(rmIdList!=null && rmIdList.size() > 0){
    		RoleMasterBean lRoleMasterBean = null;
    		MemoryDBManager lMemoryDBManager = MemoryDBManager.getInstance();
    		MemoryTable lRoleMasterTable = lMemoryDBManager.getTable(RoleMasterBean.ENTITY_NAME);
    		try {
	    		for(Long lId : rmIdList){
	    			if(lId!=null){
	        			lRoleMasterBean = (RoleMasterBean) lRoleMasterTable.selectSingleRow(RoleMasterBean.f_Id, new Long[] { lId });
						if(lRoleMasterBean!=null){
		        			if(CommonUtilities.hasValue(lRolesDesc)) lRolesDesc +=", ";
		        			lRolesDesc += lRoleMasterBean.getDesc();
						}
	    			}
	    		}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
		return lRolesDesc;
    }
    public void setRmListDesc(String pRMListDesc) {
    }

    public Yes getFullOwnership() {
        return fullOwnership;
    }

    public void setFullOwnership(Yes pFullOwnership) {
        fullOwnership = pFullOwnership;
    }
    
    public Long getOwnerAuId() {
        return ownerAuId;
    }

    public void setOwnerAuId(Long pOwnerAuId) {
        ownerAuId = pOwnerAuId;
    }

    public Long getRecordCreator() {
        return recordCreator;
    }

    public void setRecordCreator(Long pRecordCreator) {
        recordCreator = pRecordCreator;
    }

    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }

    public Long getRecordUpdator() {
        return recordUpdator;
    }

    public void setRecordUpdator(Long pRecordUpdator) {
        recordUpdator = pRecordUpdator;
    }

    public Timestamp getRecordUpdateTime() {
        return recordUpdateTime;
    }

    public void setRecordUpdateTime(Timestamp pRecordUpdateTime) {
        recordUpdateTime = pRecordUpdateTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }
    
    public Long getInstLevel() {
        return instLevel;
    }

    public void setInstLevel(Long pInstLevel) {
        instLevel = pInstLevel;
    }
    
    public Long getInstCntrLevel() {
        return instCntrLevel;
    }

    public void setInstCntrLevel(Long pInstCntrLevel) {
        instCntrLevel = pInstCntrLevel;
    }

    public Long getBidLevel() {
        return bidLevel;
    }

    public void setBidLevel(Long pBidLevel) {
        bidLevel = pBidLevel;
    }

    public Long getPlatformLimitLevel() {
        return platformLimitLevel;
    }

    public void setPlatformLimitLevel(Long pLimitLevel) {
    	platformLimitLevel = pLimitLevel;
    }
    
    public Long getBuyerLimitLevel() {
        return buyerLimitLevel;
    }

    public void setBuyerLimitLevel(Long pBuyerLimitLevel) {
        buyerLimitLevel = pBuyerLimitLevel;
    }

    public Long getBuyerSellerLimitLevel() {
        return buyerSellerLimitLevel;
    }

    public void setBuyerSellerLimitLevel(Long pBuyerSellerLimitLevel) {
        buyerSellerLimitLevel = pBuyerSellerLimitLevel;
    }

    public Long getUserLimitLevel() {
        return userLimitLevel;
    }

    public void setUserLimitLevel(Long pUserLimitLevel) {
        userLimitLevel = pUserLimitLevel;
    }
    
    public String getPassword2() {
        return null;
    }

    public void setPassword2(String pPassword) {
    }

    public Timestamp getPasswordUpdatedAt2() {
        return null;
    }

    public void setPasswordUpdatedAt2(Timestamp pTimestamp) {
    }

    public Object getPreference(PreferenceKey pPrefKey) {
		AppEntityBean  lAppEntityBean = null;
		boolean lRegUser = false;
		try {
			lRegUser = (AppConstants.DOMAIN_REGUSER.equals(domain));
			switch (pPrefKey) {
	        case APIUser :
	            return enableAPI;
	        case OtpSendMethod :
	            //case twoFAMail
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(domain);
    			if (!lRegUser && lAppEntityBean!=null && (TwoFAType.OneTimePassword.equals(lAppEntityBean.getTwoFaType()))){
    				if (TredsHelper.getInstance().getIsOtpSmsEnabled() && TredsHelper.getInstance().getIsOtpEmailEnabled()){
                    	return OtpSendMethod.Both;
                	}
                    else if (TredsHelper.getInstance().getIsOtpEmailEnabled()){
                    	return OtpSendMethod.Email;
                    }
                    else if (TredsHelper.getInstance().getIsOtpSmsEnabled()){
                    	return OtpSendMethod.Sms;
                    }
    			}
	        	return null;
	        case TwoFATypeOtp:
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(domain);
	        	if (!lRegUser && lAppEntityBean != null && TwoFAType.OneTimePassword.equals(lAppEntityBean.getTwoFaType())){
	            	return CommonAppConstants.YesNo.Yes;
	        	}
            	return CommonAppConstants.YesNo.No;
	        case TwoFATypeQuestionAndAnswer:
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(domain);
	        	if (!lRegUser && lAppEntityBean != null && TwoFAType.QuestionAndAnswer.equals(lAppEntityBean.getTwoFaType())){
	            	return CommonAppConstants.YesNo.Yes;
	        	}
	        	//if (lAppEntityBean.getTwoFaType()==null){
	        	//	return CommonAppConstants.YesNo.Yes;
	        	//}
            	return CommonAppConstants.YesNo.No;
	        case TwoFATypeToken:
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(domain);
	        	if (!lRegUser && lAppEntityBean != null && TwoFAType.Token.equals(lAppEntityBean.getTwoFaType())){
	            	return CommonAppConstants.YesNo.Yes;
	        	}
            	return CommonAppConstants.YesNo.No;
	        }
		} catch (MemoryDBException e1) {
			e1.printStackTrace();
		}
        
        return null;
    }
    
    public Long getTypeValue() {
        return type==null?null:type.code;
    }

    public String getEntityName() {
        return ENTITY_NAME;
    }

    public void fill(String pMessage, UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
        lBeanMeta.validateAndParse(this, pMessage, null, null);
    }

    public String getAsString(UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppUserBean.class);
        return lBeanMeta.formatAsJson(this);
    }

    public Object getColumnValue(String pColumnName) {
        return null;
    }

    public Object getColumnValue(int pColumnIndex) {
        switch(pColumnIndex) {
            case idx_Id : return id;
            case idx_Domain : return domain;
            case idx_LoginId : return loginId;
        }
        return null;
    }
    
    public String getCheckerLevelSetting() {
    	List<Map<String,Object>> lList =  new ArrayList<Map<String,Object>>();
    	HashMap<String, Object> lMap =null;
        if (instLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.INSTRUMENT_CHECKER);
        	lMap.put("level", instLevel);
        	lList.add(lMap);
        }
        if (instCntrLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.INSTRUMENT_COUNTER_CHECKER);
        	lMap.put("level", instCntrLevel);
        	lList.add(lMap);
        }
        if (bidLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.BID_CHECKER);
        	lMap.put("level", bidLevel);
        	lList.add(lMap);
        }
        if (platformLimitLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.PLATFORM_LIMIT_CHECKER);
        	lMap.put("level", platformLimitLevel);
        	lList.add(lMap);
        }
        if (buyerLimitLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.BUYER_LIMIT_CHECKER);
        	lMap.put("level", buyerLimitLevel);
        	lList.add(lMap);
        }
        if (buyerSellerLimitLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.BUYERSELLER_LIMIT_CHECKER);
        	lMap.put("level", buyerSellerLimitLevel);
        	lList.add(lMap);
        }
        if (userLimitLevel!=null) {
        	lMap = new HashMap<String, Object>();
        	lMap.put("act", AppConstants.USER_LIMIT_CHECKER);
        	lMap.put("level", userLimitLevel);
        	lList.add(lMap);
        }
        if (lList.isEmpty()) return null;
        return new JsonBuilder(lList).toString();
    }

    public void setCheckerLevelSetting(String pCheckerLevelSetting) {
    	instLevel = new Long(0);
    	bidLevel = new Long(0);
    	platformLimitLevel = new Long(0);
    	instCntrLevel = new Long(0);
    	userLimitLevel = new Long(0);
    	buyerLimitLevel = new Long(0);
    	buyerSellerLimitLevel = new Long(0);
    	if (pCheckerLevelSetting!=null) {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		List<Map<String,Object>> lList = (List<Map<String,Object>>) lJsonSlurper.parseText(pCheckerLevelSetting);
			if (lList!=null && !lList.isEmpty()) {
				for (Map<String,Object> lMap : lList) {
					if (lMap.containsKey("act") && lMap.containsKey("level")) {
						if (AppConstants.INSTRUMENT_CHECKER.equals(lMap.get("act").toString())) {
							 instLevel =Long.valueOf(lMap.get("level").toString()); 
						}
						if (AppConstants.INSTRUMENT_COUNTER_CHECKER.equals(lMap.get("act").toString())) {
							 instCntrLevel =Long.valueOf(lMap.get("level").toString()); 
						} 
						if (AppConstants.BID_CHECKER.equals(lMap.get("act").toString())) { 
							 bidLevel =  Long.valueOf(lMap.get("level").toString()); 
						} 
						if(AppConstants.PLATFORM_LIMIT_CHECKER.equals(lMap.get("act").toString())) { 
							 platformLimitLevel = Long.valueOf(lMap.get("level").toString());  
						}
						if(AppConstants.BUYER_LIMIT_CHECKER.equals(lMap.get("act").toString())) { 
							 buyerLimitLevel = Long.valueOf(lMap.get("level").toString()); 
						}
						if(AppConstants.BUYERSELLER_LIMIT_CHECKER.equals(lMap.get("act").toString())) { 
							 buyerSellerLimitLevel = Long.valueOf(lMap.get("level").toString()); 
						}
						if(AppConstants.USER_LIMIT_CHECKER.equals(lMap.get("act").toString())) { 
							 userLimitLevel = Long.valueOf(lMap.get("level").toString()); 
						}
					}
			 	}
			}
    	}
    }
    
    private AppUserBean backupBean;
    public void backup() {
        if (backupBean == null)
            backupBean = new AppUserBean();
        backupBean.setId(getId());
        backupBean.setDomain(getDomain());
        backupBean.setLoginId(getLoginId());
        backupBean.setPassword1(getPassword1());
        backupBean.setPasswordUpdatedAt1(getPasswordUpdatedAt1());
        backupBean.setForcePasswordChange(getForcePasswordChange());
        backupBean.setStatus(getStatus());
        backupBean.setReason(getReason());
        backupBean.setFailedLoginCount(getFailedLoginCount());
        backupBean.setType(getType());
        backupBean.setSalutation(getSalutation());
        backupBean.setFirstName(getFirstName());
        backupBean.setMiddleName(getMiddleName());
        backupBean.setLastName(getLastName());
        backupBean.setTelephone(getTelephone());
        backupBean.setMobile(getMobile());
        backupBean.setEmail(getEmail());
        backupBean.setAltEmail(getAltEmail());
        backupBean.setSecuritySettings(getSecuritySettings());
        backupBean.setSecretQuestions(getSecretQuestions());
        backupBean.setSecretAnswers(getSecretAnswers());
        backupBean.setSecretText(getSecretText());
        backupBean.setSecretImage(getSecretImage());
        backupBean.setOtpEmail(getOtpEmail());
        backupBean.setOtpMobile(getOtpMobile());
        backupBean.setRsaTokenKey(getRsaTokenKey());
        backupBean.setRecordCreator(getRecordCreator());
        backupBean.setRecordCreateTime(getRecordCreateTime());
        backupBean.setRecordUpdator(getRecordUpdator());
        backupBean.setRecordUpdateTime(getRecordUpdateTime());
        backupBean.setRecordVersion(getRecordVersion());

    }

    public void rollback() {
        if (backupBean != null) {
            setId(backupBean.getId());
            setDomain(backupBean.getDomain());
            setLoginId(backupBean.getLoginId());
            setPassword1(backupBean.getPassword1());
            setPasswordUpdatedAt1(backupBean.getPasswordUpdatedAt1());
            setForcePasswordChange(backupBean.getForcePasswordChange());
            setStatus(backupBean.getStatus());
            setReason(backupBean.getReason());
            setFailedLoginCount(backupBean.getFailedLoginCount());
            setType(backupBean.getType());
            setSalutation(backupBean.getSalutation());
            setFirstName(backupBean.getFirstName());
            setMiddleName(backupBean.getMiddleName());
            setLastName(backupBean.getLastName());
            setTelephone(backupBean.getTelephone());
            setMobile(backupBean.getMobile());
            setEmail(backupBean.getEmail());
            setAltEmail(backupBean.getAltEmail());
            setSecuritySettings(backupBean.getSecuritySettings());
            setSecretQuestions(backupBean.getSecretQuestions());
            setSecretAnswers(backupBean.getSecretAnswers());
            setSecretText(backupBean.getSecretText());
            setSecretImage(backupBean.getSecretImage());
            setOtpEmail(backupBean.getOtpEmail());
            setOtpMobile(backupBean.getOtpMobile());
            setRsaTokenKey(backupBean.getRsaTokenKey());
            setRecordCreator(backupBean.getRecordCreator());
            setRecordCreateTime(backupBean.getRecordCreateTime());
            setRecordUpdator(backupBean.getRecordUpdator());
            setRecordUpdateTime(backupBean.getRecordUpdateTime());
            setRecordVersion(backupBean.getRecordVersion());
        }
    }

    public void commit() {
        if (backupBean != null) {
            backupBean.setId(null);
            backupBean.setDomain(null);
            backupBean.setLoginId(null);
            backupBean.setPassword1(null);
            backupBean.setPasswordUpdatedAt1(null);
            backupBean.setForcePasswordChange(null);
            backupBean.setStatus(null);
            backupBean.setReason(null);
            backupBean.setFailedLoginCount(null);
            backupBean.setType(null);
            backupBean.setSalutation(null);
            backupBean.setFirstName(null);
            backupBean.setMiddleName(null);
            backupBean.setLastName(null);
            backupBean.setTelephone(null);
            backupBean.setMobile(null);
            backupBean.setEmail(null);
            backupBean.setAltEmail(null);
            backupBean.setSecuritySettings(null);
            backupBean.setSecretQuestions(null);
            backupBean.setSecretAnswers(null);
            backupBean.setSecretText(null);
            backupBean.setSecretImage(null);
            backupBean.setOtpEmail(null);
            backupBean.setOtpMobile(null);
            backupBean.setRsaTokenKey(null);
            backupBean.setRecordCreator(null);
            backupBean.setRecordCreateTime(null);
            backupBean.setRecordUpdator(null);
            backupBean.setRecordUpdateTime(null);
            backupBean.setRecordVersion(null);
        }
    }

    public YesNo getEnableFPQuestion() {
        return YesNo.Yes;
    }

    public void setEnableFPQuestion(YesNo pEnable) {
    }
    
    public List<String> getUserTypes() {
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{domain});
            return lAppEntityBean.getEntityTypes();
        } catch (Exception lException) {
        }
        return null;
    }
    
    public String getName() {
        StringBuilder lName = new StringBuilder();
        String lSpace = " ";
        lName.append(salutation).append(lSpace).append(firstName);
        if (StringUtils.isNotBlank(middleName)) lName.append(lSpace).append(middleName);
        if (StringUtils.isNotBlank(lastName)) lName.append(lSpace).append(lastName);
        return lName.toString();
    }
    
    public List<Long> getCheckersInstrument() {
        return checkersInstrument;
    }

    public void setCheckersInstrument(List<Long> pCheckersInstrument) {
        checkersInstrument = pCheckersInstrument;
    }

    public List<Long> getCheckersPlatformLimit() {
        return checkersPlatformLimit;
    }

    public void setCheckersPlatformLimit(List<Long> pCheckersPlatformLimit) {
        checkersPlatformLimit = pCheckersPlatformLimit;
    }

    public List<Long> getCheckersBuyerLimit() {
        return checkersBuyerLimit;
    }

    public void setCheckersBuyerLimit(List<Long> pCheckersBuyerLimit) {
        checkersBuyerLimit = pCheckersBuyerLimit;
    }

    public List<Long> getCheckersBuyerSellerLimit() {
        return checkersBuyerSellerLimit;
    }

    public void setCheckersBuyerSellerLimit(List<Long> pCheckersBuyerSellerLimit) {
        checkersBuyerSellerLimit = pCheckersBuyerSellerLimit;
    }

    public List<Long> getCheckersUserLimit() {
        return checkersUserLimit;
    }

    public void setCheckersUserLimit(List<Long> pCheckersUserLimit) {
        checkersUserLimit = pCheckersUserLimit;
    }

    public List<Long> getLocationIdList() {
        return locationIdList;
    }

    public void setLocationIdList(List<Long> pLocationIdList) {
        locationIdList = pLocationIdList;
    }

    public String getLocationIds() {
    	if (locationIdList == null) return null;
        else return new JsonBuilder(locationIdList).toString();
    }

    public void setLocationIds(String pLocationIds) {
        if (pLocationIds == null) locationIdList = null;
        else {
            List<Integer> lLocationIdList = (List<Integer>)(new JsonSlurper().parseText(pLocationIds));
            locationIdList = new ArrayList<Long>();
            for (Integer lLocationId : lLocationIdList) {
                locationIdList.add(Long.valueOf(lLocationId.longValue()));
            }
        }
    }
    public List<Long> getCheckersBid() {
        return checkersBid;
    }

    public void setCheckersBid(List<Long> pCheckersBid) {
        checkersBid = pCheckersBid;
    }
    
    public List<Long> getCheckersInstrumentCounter() {
        return checkersInstrumentCounter;
    }

    public void setCheckersInstrumentCounter(List<Long> pCheckersInstrumentCounter) {
    	checkersInstrumentCounter = pCheckersInstrumentCounter;
    }
    
    public String getUserLimits(){
        HashMap<String, Object> lSettings = new HashMap<String, Object>();
        if(minUserLimit != null){
        	lSettings.put("minUserLimit", minUserLimit);
        }
        if(maxUserLimit != null){
        	lSettings.put("maxUserLimit", maxUserLimit);
        }
        if (lSettings.isEmpty())
            return null;
        else
            return new JsonBuilder(lSettings).toString();
    }

    public void setUserLimits(String pUserLimits){
    	if (StringUtils.isNotEmpty(pUserLimits)){
            JsonSlurper lJsonSlurper = new JsonSlurper();
            Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pUserLimits);
            if(lMap!=null && lMap.size() > 0){
            	if(lMap.containsKey("minUserLimit")){
            		minUserLimit = new BigDecimal(lMap.get("minUserLimit").toString());
            	}
            	if(lMap.containsKey("maxUserLimit")){
            		maxUserLimit = new BigDecimal(lMap.get("maxUserLimit").toString());
            	}
            }
    	}else{
    		minUserLimit = null;
    		maxUserLimit = null;
    	}
    }

    public BigDecimal getMinUserLimit(){
    	return minUserLimit;
    }

    public BigDecimal getMaxUserLimit(){
    	return maxUserLimit;
    }
    
    public void setMinUserLimit(BigDecimal pMinUserLimit){
    	minUserLimit = pMinUserLimit;
    }

    public void setMaxUserLimit(BigDecimal pMaxUserLimit){
    	maxUserLimit = pMaxUserLimit;
    }
    public boolean hasUserLimit(){
    	if(minUserLimit!=null || maxUserLimit!=null){
    		return true;
    	}
    	return false;
    }

	public boolean emailCategoryHasRole(String pCategory) {
		if (!AppConstants.DOMAIN_PLATFORM.equals(domain)){
			List<Long> lEmailRoles = getRolesForEmailCategory(pCategory);
			if (lEmailRoles != null && !lEmailRoles.isEmpty()){
				for (Long lId : lEmailRoles){
					if (rmIdList.contains(lId)){
						return true;
					}
				}
			}else{
				return true;
			}
		}else{
			return true;
		}
		return false;
	}
	
	public List<Long> getRolesForEmailCategory(String pCategory){
		return (List<Long>) OtherResourceCache.getInstance().getCategoryWiseRoles().get(pCategory);
	}

	@Override
	public String getOtpEmail() {
		return email;
	}
	@Override
	public void setOtpEmail(String pOtpEmail) {
		if (pOtpEmail != null)
			email = pOtpEmail;
	}
	@Override
	public String getOtpMobile() {
		return mobile;
	}
	@Override
	public void setOtpMobile(String pOtpMobile) {
		if (pOtpMobile != null)
			mobile = pOtpMobile;
	}
	@Override
	public String getRsaTokenKey() {
		return rsaTokenKey;
	}
	@Override
	public void setRsaTokenKey(String pRsaTokenKey) {
		rsaTokenKey = pRsaTokenKey;
	}
	
    public String getRmLocation() {
        return rmLocation;
    }

    public void setRmLocation(String pRmLocation) {
        rmLocation = pRmLocation;
    }
}