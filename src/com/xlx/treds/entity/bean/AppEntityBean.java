package com.xlx.treds.entity.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.IMemoryTableRow;
import com.xlx.common.user.bean.UserFormatsBean;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AppEntityStatus;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class AppEntityBean implements IMemoryTableRow {
    public static final String ENTITY_NAME = "APPENTITIES";
    public static final String FIELDGROUP_UPDATEBLOCKEDFINANCIERS = "updateBlockedFinanciers";
    public static final String FIELDGROUP_UPDATECLIKWRAP = "updateClickWrap";
    public static final String FIELDGROUP_UPDATEIPLIST = "updateIpList";
    public static final String FIELDGROUP_UPDATE2FA = "update2FaSettings";
    public static final String FIELDGROUP_UPDATEREQ_VER = "updateReqVer";
    public static final String FIELDGROUP_UPDATESPLITSETTINGS = "updateSplitSettings";
    public static final String FIELDGROUP_INSERTPURCHASERAGGERGATOR = "insertPurchaserAggregator";
    public static final String FIELDGROUP_UPDATEPURCHASERAGGERGATOR = "updatePurchaserAggregator";
    public static final String FIELDGROUP_UPDATECHECKERLIMITS = "updateCheckerLimits";
    public static final String FIELDGROUP_UPDATEPREFERENCES = "updatePreferences";
    public static final String FIELDGROUP_UPDATECREDITREPORT = "updateCreditReport";
    public static final String FIELDGROUP_UPDATEREGEXPIRY = "updateRegExpiry";    
    public static final String FIELDGROUP_UPDATERM = "updateRm";
    public static final String FIELDGROUP_COPYRM = "copyRm";
    public static final String FIELDGROUP_REFERERDETAILS = "refererDetails";
    public static final String FIELDGROUP_UPDATECUSTOMFIELDS = "updateCustomFields";
    public static final String f_Code = "code";
    public static final int idx_Code = 0;

    public enum TwoFAType implements IKeyValEnumInterface<String> {
        QuestionAndAnswer("qna","Question And Answer"),OneTimePassword("otp","One Time Password"),Token("tkn","Token");
        private final String code;
        private final String desc;
        private TwoFAType(String pCode,String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String getdesc() {
            return desc;
        }
    }
    public enum BusinessSource implements IKeyValEnumInterface<String> {
        Direct("D","Direct"),Referal("R","Referred");
        private final String code;
        private final String desc;
        private BusinessSource(String pCode,String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String getdesc() {
            return desc;
        }
    }
    public enum FeeType implements IKeyValEnumInterface<String> {
        Absolute("A","Absoulute"),Percentage("P","Percentage");
        private final String code;
        private final String desc;
        private FeeType(String pCode,String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String getdesc() {
            return desc;
        }
    }
    
    private String code;
    private Long cdId;
    private String name;
    private String type;
    private AppEntityStatus status;
    private String msmeStatus;
    private String promoterCategory;
    private String pan;
    private List<String> blockedFinancierList;
    private String acceptedAgreementVersion;
    private List<String> ipList;
    private String salesCategory;
    private TwoFAType twoFaType;
    private String requiredAgreementVersion;
    private YesNo allowObliSplitting;
    private Long instLevel;
    private Long instCntrLevel;
    private Long bidLevel;
    private Long platformLimitLevel;
    private Long buyerLimitLevel;
    private Long buyerSellerLimitLevel;
    private Long userLimitLevel;
    private AppEntityPreferenceBean preferences;
	private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private List<String> purchaserList;
    private String rating;
    private Long financierCount;
    private String creditReport ;
    private Date regExpiryDate;
    private Date extendedRegExpiryDate;
    private String aggCompanyGSTN;
    private String aggContactPerson;
    private String aggContactMobile;
    private String aggContactEmail;
    private Long rmUserId;
    private String rmUserLogin;
    private String rmUserName;
    private String rmLocation;
    private String rmLocationDesc;
    private Long rsmUserId;
    private String rsmUserLogin;
    private String rsmUserName;
    private String rsmLocation;
    private String rsmLocationDesc;
    private BusinessSource businessSource;
    private String refererCode;
    private String transFeeType;
    private String regFeeType;
    private String annFeeType;
    private Long transFeePerc;
    private Long regFeePerc;
    private Long annFeePerc;
    private BigDecimal transFeeAmt;
    private BigDecimal regFeeAmt;
    private BigDecimal annFeeAmt;
    private Long cfId;
    private Yes enableLocationwiseSettlement;

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public Long getCdId() {
        return cdId;
    }

    public void setCdId(Long pCdId) {
        cdId = pCdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String pName) {
        name = pName;
    }

    public String getType() {
        return type;
    }

    public void setType(String pType) {
        type = pType;
    }

    public AppEntityStatus getStatus() {
        return status;
    }

    public void setStatus(AppEntityStatus pStatus) {
        status = pStatus;
    }

    public String getMsmeStatus() {
        return msmeStatus;
    }

    public void setMsmeStatus(String pMsmeStatus) {
        msmeStatus = pMsmeStatus;
    }

    public String getPromoterCategory() {
        return promoterCategory;
    }

    public void setPromoterCategory(String pPromoterCategory) {
        promoterCategory = pPromoterCategory;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pPan) {
        pan = pPan;
    }

    public String getBlockedFinanciers() {
        if (blockedFinancierList == null) return null;
        return new JsonBuilder(blockedFinancierList).toString();
    }

    public void setBlockedFinanciers(String pBlockedFinanciers) {
    	List<String> lBlockedFinancierList = null;
    	if (StringUtils.isNotBlank(pBlockedFinanciers)) {
    		lBlockedFinancierList = (List<String>)(new JsonSlurper().parseText(pBlockedFinanciers));
    	}
        setBlockedFinancierList(lBlockedFinancierList);
    }

    public List<String> getBlockedFinancierList() {
        return blockedFinancierList;
    }

    public void setBlockedFinancierList(List<String> pBlockedFinancierList) {
        blockedFinancierList = pBlockedFinancierList;
    }

    public String getAcceptedAgreementVersion() {
        return acceptedAgreementVersion;
    }

    public void setAcceptedAgreementVersion(String pAcceptedAgreementVersion) {
        acceptedAgreementVersion = pAcceptedAgreementVersion;
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

    public String getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(String pSalesCategory) {
        salesCategory = pSalesCategory;
    }

    public TwoFAType getTwoFaType() {
		return twoFaType;
	}

    public String getTwoFaTypeDesc() {
		return (twoFaType!=null?twoFaType.getdesc():"");
	}

	public void setTwoFaType(TwoFAType pTwoFaType) {
		twoFaType = pTwoFaType;
	}
	private static final String SETTING_TWOFATYPE = "twoFaType";
	public String getSettings() {
		Map<String, Object> lSettingsMap = new HashMap<String, Object>();
		if (twoFaType != null)
			lSettingsMap.put(SETTING_TWOFATYPE, twoFaType.getCode());
		if (lSettingsMap.isEmpty())
			return null;
		else
			return new JsonBuilder(lSettingsMap).toString();
	}
	

	public void setSettings(String pSettings) {
		twoFaType = null;
		if (StringUtils.isNotBlank(pSettings)) {
			Map<String, Object> lSettingsMap = (Map<String, Object>)(new JsonSlurper().parseText(pSettings));
			String lTwoFaType = (String)lSettingsMap.get(SETTING_TWOFATYPE);
			if (TwoFAType.OneTimePassword.getCode().equals(lTwoFaType))
				twoFaType = TwoFAType.OneTimePassword;
			else if (TwoFAType.QuestionAndAnswer.getCode().equals(lTwoFaType))
				twoFaType = TwoFAType.QuestionAndAnswer;
			else if (TwoFAType.Token.getCode().equals(lTwoFaType))
				twoFaType = TwoFAType.Token;
		}
	}
	
    public String getRequiredAgreementVersion() {
        return requiredAgreementVersion;
    }

    public void setRequiredAgreementVersion(String pRequiredAgreementVersion) {
        requiredAgreementVersion = pRequiredAgreementVersion;
    }
    
    public YesNo getAllowObliSplitting() {
        return allowObliSplitting;
    }

    public void setAllowObliSplitting(YesNo pAllowObliSplitting) {
        allowObliSplitting = pAllowObliSplitting;
    }
    
    public String getAggPurchaser() {
        if (purchaserList == null) return null;
        return new JsonBuilder(purchaserList).toString();
    }

    public void setAggPurchaser(String pAggPurchaser) {
    	List<String> lPurchaserList = null;
    	if (StringUtils.isNotBlank(pAggPurchaser)) {
    		lPurchaserList = (List<String>)(new JsonSlurper().parseText(pAggPurchaser));
    	}
        setBlockedFinancierList(lPurchaserList);
    }

    public List<String> getPurchaserList() {
        return purchaserList;
    }

    public void setPurchaserList(List<String> pPurchaserList) {
        purchaserList = pPurchaserList;
    }

    public AppEntityPreferenceBean getPreferences() {
		return preferences;
	}
	public void setPreferences(AppEntityPreferenceBean pPreferences) {
		preferences = pPreferences;
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

    public String getEntityName() {
        return ENTITY_NAME;
    }
    public void fill(String pMessage, UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        lBeanMeta.validateAndParse(this, pMessage, null, null);
    }
    public String getAsString(UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        return lBeanMeta.formatAsJson(this);
    }
    public Object getColumnValue(String pColumnName) {
        return null;
    }
    public Object getColumnValue(int pColumnIndex) {
        switch(pColumnIndex) {
            case idx_Code : return code;
        }
        return null;
    }
    private AppEntityBean backupBean;
    public void backup() {
        if (backupBean == null)
            backupBean = new AppEntityBean();
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
        lBeanMeta.copyBean(this, backupBean);
    }
    public void rollback() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
            lBeanMeta.copyBean(backupBean, this);
        }
    }
    public void commit() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
            lBeanMeta.clearBean(backupBean);
        }
    }
    public boolean isSupplier() {
        return (type != null) && (type.length() > 0) && (type.substring(0, 1).equals(CommonAppConstants.Yes.Yes.getCode()));
    }
    public boolean isPurchaser() {
        return (type != null) && (type.length() > 1) && (type.substring(1, 2).equals(CommonAppConstants.Yes.Yes.getCode()));
    }
    public boolean isFinancier() {
        return (type != null) && (type.length() > 2) && (type.substring(2, 3).equals(CommonAppConstants.Yes.Yes.getCode()));
    }
    public boolean isPlatform() {
        return AppConstants.DOMAIN_PLATFORM.equals(code);
    }
    public boolean isRegistringEntity() {
        return AppConstants.DOMAIN_REGENTITY.equals(code);
    }
    public boolean isRegulatoryEntity() {
        return AppConstants.DOMAIN_REGULATOR.equals(code);
    }
	public boolean isPurchaserAggregator() {
	  return (type != null) && (type.length() > 0) && (type.equals("AAA"));
	}
    public String getTypeDesc() {
        StringBuilder lDesc = new StringBuilder();
        if (isSupplier()) lDesc.append("Supplier ");
        if (isPurchaser()) lDesc.append("Buyer ");
        if (isFinancier()) lDesc.append("Financier ");
        if (isPlatform()) lDesc.append("Platform ");
        if (isRegistringEntity()) lDesc.append("Reg Entity ");
        if (isRegulatoryEntity()) lDesc.append("Regulatory ");
        if (isPurchaserAggregator()) lDesc.append("Aggregator ");
        return lDesc.toString();
    }
    public List<String> getEntityTypes() {
        List<String> lUserTypes = new ArrayList<String>();
        try {
            if (isPurchaser())
                lUserTypes.add(AppConstants.EntityType.Purchaser.getCode());
            if (isSupplier())
                lUserTypes.add(AppConstants.EntityType.Supplier.getCode());
            if (isFinancier())
                lUserTypes.add(AppConstants.EntityType.Financier.getCode());
            if (isPlatform())
                lUserTypes.add(AppConstants.EntityType.Platform.getCode());
            if (isRegistringEntity())
                lUserTypes.add(AppConstants.EntityType.RegEntity.getCode());
            if (isRegulatoryEntity())
                lUserTypes.add(AppConstants.EntityType.Regulator.getCode());
            if (isPurchaserAggregator())
                lUserTypes.add(AppConstants.EntityType.Aggregator.getCode());
        } catch (Exception lException) {
        }
        return lUserTypes;
    }
    public boolean isFinancierBlocked(String pCode) {
    	return blockedFinancierList==null?false:blockedFinancierList.contains(pCode);
    }
    
    public enum EntityType implements IKeyValEnumInterface<String>{
        Financier("NNY","Financier"),Purchaser("NYN","Purchaser"),Supplier("YNN","Supplier"),Aggregator("AAA","Aggregator");
        
        private final String code;
        private final String desc;
        private EntityType(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
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

    public boolean hasHierarchicalChecker(String pType) {
    	if (AppConstants.INSTRUMENT_CHECKER.equals(pType)) {
    		if (instLevel==null || instLevel == 0) {
    			return false;
    		}
    	}else if (AppConstants.INSTRUMENT_COUNTER_CHECKER.equals(pType)) {
    		if (instCntrLevel==null || instCntrLevel == 0) {
    			return false;
    		}
    	}else if (AppConstants.BID_CHECKER.equals(pType)){
    		if (bidLevel == 0 || bidLevel==null ) {
    			return false;
    		}
    	}else if (AppConstants.PLATFORM_LIMIT_CHECKER.equals(pType)){
    		if ( platformLimitLevel==null || platformLimitLevel == 0 ) {
    			return false;
    		}
    	}else if (AppConstants.USER_LIMIT_CHECKER.equals(pType)){
    		if ( userLimitLevel==null || userLimitLevel == 0 ) {
    			return false;
    		}
    	}else if (AppConstants.BUYER_LIMIT_CHECKER.equals(pType)){
    		if ( buyerLimitLevel==null || buyerLimitLevel == 0 ) {
    			return false;
    		}
    	}else if (AppConstants.BUYERSELLER_LIMIT_CHECKER.equals(pType)){
    		if ( buyerSellerLimitLevel==null || buyerSellerLimitLevel == 0 ) {
    			return false;
    		}
    	}else {
    		return false;
    	}
    	return true;
    }
    
    public String getRating() {
        return rating;
    }
    public void setRating(String pRating) {
        rating = pRating;
    }
    
    public Long getFinancierCount() {
        return financierCount;
    }

    public void setFinancierCount(Long pFinancierCount) {
        financierCount = pFinancierCount;
    }
    
    public String getCreditReport () {
        return creditReport ;
    }

    public void setCreditReport (String pCreditReport ) {
        creditReport  = pCreditReport ;
    }

	public Date getRegExpiryDate() {
		return regExpiryDate;
	}

	public void setRegExpiryDate(Date regExpiryDate) {
		this.regExpiryDate = regExpiryDate;
	}

	public Date getExtendedRegExpiryDate() {
		return extendedRegExpiryDate;
	}

	public void setExtendedRegExpiryDate(Date extendedRegExpiryDate) {
		this.extendedRegExpiryDate = extendedRegExpiryDate;
	}

	//EffectiveExpiryDate = max(extended, actual)
	//If it is null or if less than current business date then registration of entity would be treated as expired. 
	//Bid acceptance in case of buyer and seller will not be allowed. 
	//In case of financier bid placement will not be allowed
	public Date getEffectiveRegExpiryDate() {
		if(extendedRegExpiryDate!=null) {
			if(regExpiryDate!=null && extendedRegExpiryDate.after(regExpiryDate)) {
				return extendedRegExpiryDate;
			}
		}
		return regExpiryDate;
	}

	public void setEffectiveRegExpiryDate(Date effectiveRegExpiryDate) {
		//nothing will be set
	}
	
	public boolean isRegistrationExpired() {
		Date lBusinessDate = TredsHelper.getInstance().getBusinessDate();
		Date lRegExpiryDate = getEffectiveRegExpiryDate();
		if(lRegExpiryDate==null || lRegExpiryDate.before(lBusinessDate)) {
			return true;
		}
		return false;
	}
	
	public String getAggregatorInfo() {
		Map<String,Object> lMap = new HashMap<String, Object>();
		if(StringUtils.isNotEmpty(aggCompanyGSTN)) {
			lMap.put("aggCompanyGSTN", aggCompanyGSTN);
		}
		if(StringUtils.isNotEmpty(aggContactPerson)) {
			lMap.put("aggContactPerson", aggContactPerson);
		}
		if(StringUtils.isNotEmpty(aggContactMobile)) {
			lMap.put("aggContactMobile", aggContactMobile);
		}
		if(StringUtils.isNotEmpty(aggContactEmail)) {
			lMap.put("aggContactEmail", aggContactEmail);
		}
		if(lMap.size()>0) {
	        return  new JsonBuilder(lMap).toString();
		}
		return null;
    }

    public void setAggregatorInfo(String pAggregatorInfo) {
    	aggCompanyGSTN = null;
    	aggContactPerson = null;
    	aggContactMobile = null;
    	aggContactEmail = null;
    	if(StringUtils.isNotEmpty(pAggregatorInfo)) {
    		Map<String,Object> lMap = (Map<String,Object>) new JsonSlurper().parseText(pAggregatorInfo);
    		if(lMap.containsKey("aggCompanyGSTN")) {
    			aggCompanyGSTN = (String) lMap.get("aggCompanyGSTN");
    		}
    		if(lMap.containsKey("aggContactPerson")) {
    			aggContactPerson = (String) lMap.get("aggContactPerson");
    		}
    		if(lMap.containsKey("aggContactMobile")) {
    			aggContactMobile = (String) lMap.get("aggContactMobile");
    		}
    		if(lMap.containsKey("aggContactEmail")) {
    			aggContactEmail = (String) lMap.get("aggContactEmail");
    		}
    	}
    }

    public String getAggCompanyGSTN() {
        return aggCompanyGSTN;
    }

    public void setAggCompanyGSTN(String pAggCompanyGSTN) {
        aggCompanyGSTN = pAggCompanyGSTN;
    }

    public String getAggContactPerson() {
        return aggContactPerson;
    }

    public void setAggContactPerson(String pAggContactPerson) {
        aggContactPerson = pAggContactPerson;
    }

    public String getAggContactMobile() {
        return aggContactMobile;
    }

    public void setAggContactMobile(String pAggContactMobile) {
        aggContactMobile = pAggContactMobile;
    }

    public String getAggContactEmail() {
        return aggContactEmail;
    }

    public void setAggContactEmail(String pAggContactEmail) {
        aggContactEmail = pAggContactEmail;
    }
    
    public Long getRmUserId() {
        return rmUserId;
    }

    public void setRmUserId(Long pRmUserId) {
        rmUserId = pRmUserId;
    	if (rmUserId!=null) {
    		try {
				AppUserBean lUserBean = TredsHelper.getInstance().getAppUser(rmUserId);
				if(lUserBean!=null) {
					rmUserLogin = lUserBean.getLoginId();
					rmUserName = lUserBean.getName();
					setRmLocation(lUserBean.getRmLocation());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

    public Long getRsmUserId() {
        return rsmUserId;
    }

    public void setRsmUserId(Long pRsmUserId) {
        rsmUserId = pRsmUserId;
    	if (rsmUserId!=null) {
    		try {
				AppUserBean lUserBean = TredsHelper.getInstance().getAppUser(rsmUserId);
				if(lUserBean!=null) {
					rsmUserLogin = lUserBean.getLoginId();
					rsmUserName = lUserBean.getName();
					setRsmLocation(lUserBean.getRmLocation());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    }

    public String getRmLocation() {
        return rmLocation;
    }

    public void setRmLocation(String pRmLocation) {
    	rmLocation = pRmLocation;
    	if(rmLocation!=null) {
    		rmLocationDesc = TredsHelper.getInstance().getRefCodeValue("RMLOCATION", rmLocation);
    	}
    }

    public String getRsmLocation() {
        return rsmLocation;
    }

    public void setRsmLocation(String pRsmLocation) {
    	rsmLocation = pRsmLocation;
    	if(rsmLocation!=null) {
    		rsmLocationDesc = TredsHelper.getInstance().getRefCodeValue("RMLOCATION", rsmLocation);
    	}
    }

    public String getRmSettings() {
    	Map<String,Object> lMap = new HashMap<String, Object>();
		if(businessSource!=null) {
			lMap.put("businessSource", businessSource.code);
		}
		if(StringUtils.isNotEmpty(refererCode)) {
			lMap.put("refererCode", refererCode);
		}
		if(StringUtils.isNotEmpty(annFeeType)) {
			lMap.put("annFeeType", annFeeType);
		}
		if(StringUtils.isNotEmpty(regFeeType)) {
			lMap.put("regFeeType", regFeeType);
		}
		if(StringUtils.isNotEmpty(transFeeType)) {
			lMap.put("transFeeType", transFeeType);
		}
		if(annFeePerc!=null) {
			lMap.put("annFeePerc", annFeePerc);
		}
		if(regFeePerc!=null) {
			lMap.put("regFeePerc", regFeePerc);
		}
		if(transFeePerc!=null) {
			lMap.put("transFeePerc", transFeePerc);
		}
		if(annFeeAmt!=null) {
			lMap.put("annFeeAmt", annFeeAmt);
		}
		if(regFeeAmt!=null) {
			lMap.put("regFeeAmt", regFeeAmt);
		}
		if(transFeeAmt!=null) {
			lMap.put("transFeeAmt", transFeeAmt);
		}
		if(lMap.size()>0) {
	        return  new JsonBuilder(lMap).toString();
		}
		return null;
    }

    public void setRmSettings(String pRmSettings) {
    	annFeeType = null;
    	regFeeType = null;
    	transFeeType = null;
    	annFeePerc = null;
    	regFeePerc = null;
    	transFeePerc = null;
    	annFeeAmt = null;
    	regFeeAmt = null;
    	transFeeAmt = null;
    	if(StringUtils.isNotEmpty(pRmSettings)) {
    		Map<String,Object> lMap = (Map<String,Object>) new JsonSlurper().parseText(pRmSettings);
    		if(lMap.containsKey("businessSource")) {
    			businessSource = (BusinessSource) TredsHelper.getInstance().getValue(AppEntityBean.class, "businessSource", lMap.get("businessSource").toString());
    		}
    		if(lMap.containsKey("refererCode")) {
    			refererCode = lMap.get("refererCode").toString();
    		}
    		if(lMap.containsKey("annFeeType")) {
    			annFeeType = (String) lMap.get("annFeeType");
    		}
    		if(lMap.containsKey("regFeeType")) {
    			regFeeType = (String) lMap.get("regFeeType");
    		}
    		if(lMap.containsKey("transFeeType")) {
    			transFeeType =  (String) lMap.get("transFeeType");
    		}
    		if(lMap.containsKey("annFeePerc")) {
    			annFeePerc = new Long(lMap.get("annFeePerc").toString());
    		}
    		if(lMap.containsKey("regFeePerc")) {
    			regFeePerc = new Long(lMap.get("regFeePerc").toString());
    		}
    		if(lMap.containsKey("transFeePerc")) {
    			transFeePerc = new Long(lMap.get("transFeePerc").toString());
    		}
    		if(lMap.containsKey("annFeeAmt")) {
    			annFeeAmt = new BigDecimal(lMap.get("annFeeAmt").toString());
    		}
    		if(lMap.containsKey("regFeeAmt")) {
    			regFeeAmt = new BigDecimal(lMap.get("regFeeAmt").toString());
    		}
    		if(lMap.containsKey("transFeeAmt")) {
    			transFeeAmt = new BigDecimal(lMap.get("transFeeAmt").toString());
    		}
    	}
    }

    public Long getTransFeePerc() {
        return transFeePerc;
    }

    public void setTransFeePerc(Long pTransFee) {
        transFeePerc = pTransFee;
    }

    public Long getRegFeePerc() {
        return regFeePerc;
    }

    public void setRegFeePerc(Long pRegFee) {
        regFeePerc = pRegFee;
    }

    public Long getAnnFeePerc() {
        return annFeePerc;
    }

    public void setAnnFeePerc(Long pAnnFee) {
        annFeePerc = pAnnFee;
    }

	public BusinessSource getBusinessSource() {
		return businessSource;
	}

	public void setBusinessSource(BusinessSource pBusinessSource) {
		this.businessSource = pBusinessSource;
	}

	public String getRefererCode() {
		return refererCode;
	}

	public void setRefererCode(String refererCode) {
		this.refererCode = refererCode;
	}

	public String getRmUserName() {
		return rmUserName;
	}

	public void setRmUserName(String rmUserName) {
		//this.rmUserName = rmUserName;
	}

	public String getRsmUserName() {
		return rsmUserName;
	}

	public void setRsmUserName(String rsmUserName) {
		//this.rsmUserName = rsmUserName;
	}

	public String getRmUserLogin() {
		return rmUserLogin;
	}

	public void setRmUserLogin(String rmUserLogin) {
		//this.rmUserLogin = rmUserLogin;
	}

	public String getRsmUserLogin() {
		return rsmUserLogin;
	}

	public void setRsmUserLogin(String rsmUserLogin) {
		//this.rsmUserLogin = rsmUserLogin;
	}

	public String getRmLocationDesc() {
		return rmLocationDesc;
	}

	public void setRmLocationDesc(String rmLocationDesc) {
		this.rmLocationDesc = rmLocationDesc;
	}

	public String getRsmLocationDesc() {
		return rsmLocationDesc;
	}

	public void setRsmLocationDesc(String rsmLocationDesc) {
		this.rsmLocationDesc = rsmLocationDesc;
	}

	public Long getCfId() {
		return cfId;
	}

	public void setCfId(Long pCfId) {
		cfId = pCfId;
	}

	public String getTransFeeType() {
		return transFeeType;
	}

	public void setTransFeeType(String transFeeType) {
		this.transFeeType = transFeeType;
	}

	public String getRegFeeType() {
		return regFeeType;
	}

	public void setRegFeeType(String regFeeType) {
		this.regFeeType = regFeeType;
	}

	public String getAnnFeeType() {
		return annFeeType;
	}

	public void setAnnFeeType(String annFeeType) {
		this.annFeeType = annFeeType;
	}

	public BigDecimal getTransFeeAmt() {
		return transFeeAmt;
	}

	public void setTransFeeAmt(BigDecimal transFeeAmt) {
		this.transFeeAmt = transFeeAmt;
	}

	public BigDecimal getRegFeeAmt() {
		return regFeeAmt;
	}

	public void setRegFeeAmt(BigDecimal regFeeAmt) {
		this.regFeeAmt = regFeeAmt;
	}

	public BigDecimal getAnnFeeAmt() {
		return annFeeAmt;
	}

	public void setAnnFeeAmt(BigDecimal annFeeAmt) {
		this.annFeeAmt = annFeeAmt;
	}
	
    public Yes getEnableLocationwiseSettlement() {
        return enableLocationwiseSettlement;
    }

    public void setEnableLocationwiseSettlement(Yes pEnableLocationwiseSettlement) {
        enableLocationwiseSettlement = pEnableLocationwiseSettlement;
    }
}