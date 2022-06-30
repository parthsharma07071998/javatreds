package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.CostCollectionLeg;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class FinancierAuctionSettingBean {
    public static final String FIELDGROUP_UPDATEUTILISED = "updateUtilised";
    public static final String FIELDGROUP_UPDATEAPPROVALSTATUS = "updateApprovalStatus";
    public static final String FIELDGROUP_EXPORTFIELDS = "exportFields";
    
    public enum Level implements IKeyValEnumInterface<String>{
        Financier_Self("YNNN","Financier Self"),Financier_Buyer("YYNN","Financier Buyer"),Financier_Buyer_Seller("YYYN","Financier Buyer Seller"),Financier_User("YNNY","Financier User"),System_Buyer("NYNN","System Buyer");
        
        private final String code;
        private final String desc;
        private Level(String pCode, String pDesc) {
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
    public enum RateRangeType implements IKeyValEnumInterface<String>{
        Absolute("A","Absolute"),Spread("S","Spread");
        
        private final String code;
        private final String desc;
        private RateRangeType(String pCode, String pDesc) {
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
    public enum Active implements IKeyValEnumInterface<String>{
        Active("Y","Active"),Suspended("N","Suspended");
        
        private final String code;
        private final String desc;
        private Active(String pCode, String pDesc) {
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
    public enum EffectiveStatus implements IKeyValEnumInterface<String>{
        Active("Y","Active"),InActive("N","In-Active");
        
        private final String code;
        private final String desc;
        private EffectiveStatus(String pCode, String pDesc) {
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
    public enum ApprovalStatus implements IKeyValEnumInterface<String>{
        Draft("DFT","Draft"),Submitted("SUB","Submitted"),Returned("RET","Returned"),Rejected("REJ","Rejected"),Approved("APP","Approved"),Deleted("DEL","Deleted");
        
        private final String code;
        private final String desc;
        private ApprovalStatus(String pCode, String pDesc) {
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

    private Long id;
    private Level level;
    private String financier;
    private String purchaser;
    private String purchaserRef;
    private String financierRef;
    private String supplier;
    private String supplierRef;
    private Long auId;
    private String loginId;
    private String userName;
    private BigDecimal limit;
    private String currency;
    private BigDecimal utilised;
    private List<TenureWiseBaseRateBean> baseRateList;
    private BigDecimal minBidRate;
    private BigDecimal maxBidRate;
    private BigDecimal minSpread;
    private BigDecimal maxSpread;
    private BigDecimal bidLimit;
    private BigDecimal bidLimitUtilised;
    private CostCollectionLeg purchaserCostLeg;
    private Date expiryDate;
    private Active active;
    private ApprovalStatus approvalStatus;
    private String approvalRemarks;
    private Long makerAUId;
    private Long checkerAUId;
    private Yes bypassCheckForDelete;
    private Yes withdrawBidModChecker;
    private Yes sellerLimitMandatory;
    private Long finClId;
    private String financierLocation;
    private String checkerFlag;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Yes isLocationEnabled;
    private Long chkLevel;
    private String cersaiCode;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level pLevel) {
        level = pLevel;
    }

    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String pFinancier) {
        financier = pFinancier;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getPurchaserRef() {
        return purchaserRef;
    }

    public void setPurchaserRef(String pPurchaserRef) {
        purchaserRef = pPurchaserRef;
    }

    public String getFinancierRef() {
        return financierRef;
    }

    public void setFinancierRef(String pFinancierRef) {
        financierRef = pFinancierRef;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getSupplierRef() {
        return supplierRef;
    }

    public void setSupplierRef(String pSupplierRef) {
        supplierRef = pSupplierRef;
    }

    public Long getAuId() {
        return auId;
    }

    public void setAuId(Long pAuId) {
        auId = pAuId;
        loginId = null;
        userName = null;
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
            AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{auId});
            if (lAppUserBean != null) {
                loginId = lAppUserBean.getLoginId();
                userName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String pLoginId) {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String pUserName) {
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal pLimit) {
        limit = pLimit;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String pCurrency) {
        currency = pCurrency;
    }

    public BigDecimal getUtilised() {
        return utilised;
    }

    public void setUtilised(BigDecimal pUtilised) {
        utilised = pUtilised;
    }

    public BigDecimal getBalance() {
        return limit.subtract(utilised).setScale(2, RoundingMode.HALF_UP);
    }

    public void setBalance(BigDecimal pBalance) {
    }

    public BigDecimal getUtilPercent() {
        return utilised.multiply(AppConstants.HUNDRED).divide(limit, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
    }

    public void setUtilPercent(BigDecimal pUtilPercent) {
    }

    public RateRangeType getRateRangeType() {
        if ((minBidRate != null) || (maxBidRate != null)) return RateRangeType.Absolute;
        else if ((minSpread != null) || (maxSpread != null)) return RateRangeType.Spread;
        else return null;
    }

    public void setRateRangeType(RateRangeType pRateRangeType) {
    }

    public String getBaseRate() {
        if (baseRateList == null)
            return null;
        else {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(TenureWiseBaseRateBean.class);
            List<Map<String, Object>> lList = new ArrayList<Map<String,Object>>();
            for (TenureWiseBaseRateBean lTenureWiseBaseRateBean : baseRateList) {
                lList.add(lBeanMeta.formatAsMap(lTenureWiseBaseRateBean));
            }
            return new JsonBuilder(lList).toString();
        }
    }

    public void setBaseRate(String pBaseRate) {
        if (StringUtils.isNotBlank(pBaseRate)) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(TenureWiseBaseRateBean.class);
            List<Map<String, Object>> lList = (List<Map<String, Object>>)new JsonSlurper().parseText(pBaseRate);
            baseRateList = new ArrayList<TenureWiseBaseRateBean>();
            for (Map<String, Object> lMap : lList) {
                TenureWiseBaseRateBean lTenureWiseBaseRateBean = new TenureWiseBaseRateBean();
                lBeanMeta.validateAndParse(lTenureWiseBaseRateBean, lMap, null);
                baseRateList.add(lTenureWiseBaseRateBean);
            }
            
        } else
            baseRateList = null;
    }

    public List<TenureWiseBaseRateBean> getBaseRateList() {
        return baseRateList;
    }

    public void setBaseRateList(List<TenureWiseBaseRateBean> pBaseRateList) {
        baseRateList = pBaseRateList;
    }

    public BigDecimal getMinBidRate() {
        return minBidRate;
    }

    public void setMinBidRate(BigDecimal pMinBidRate) {
        minBidRate = pMinBidRate;
    }

    public BigDecimal getMaxBidRate() {
        return maxBidRate;
    }

    public void setMaxBidRate(BigDecimal pMaxBidRate) {
        maxBidRate = pMaxBidRate;
    }

    public BigDecimal getMinSpread() {
        return minSpread;
    }

    public void setMinSpread(BigDecimal pMinSpread) {
        minSpread = pMinSpread;
    }

    public BigDecimal getMaxSpread() {
        return maxSpread;
    }

    public void setMaxSpread(BigDecimal pMaxSpread) {
        maxSpread = pMaxSpread;
    }

    public BigDecimal getBidLimit() {
        return bidLimit;
    }

    public void setBidLimit(BigDecimal pBidLimit) {
        bidLimit = pBidLimit;
    }

    public BigDecimal getBidLimitUtilised() {
        return bidLimitUtilised;
    }

    public void setBidLimitUtilised(BigDecimal pBidLimitUtilised) {
        bidLimitUtilised = pBidLimitUtilised;
    }

    public CostCollectionLeg getPurchaserCostLeg() {
        return purchaserCostLeg;
    }

    public void setPurchaserCostLeg(CostCollectionLeg pPurchaserCostLeg) {
        purchaserCostLeg = pPurchaserCostLeg;
    }

    public Date getExpiryDate(){
    	return expiryDate;
    }
    
    public void setExpiryDate(Date pExpiryDate){
    	expiryDate = pExpiryDate;    			
    }
    
    public Active getActive() {
        return active;
    }

    public void setActive(Active pActive) {
        active = pActive;
    }

    public EffectiveStatus getEffectiveStatus() {
    	if(Active.Suspended.equals(active)){
    		return EffectiveStatus.InActive;
    	}
    	if(expiryDate!=null){ 
    		if( !expiryDate.after(TredsHelper.getInstance().getBusinessDate())) {
        		return EffectiveStatus.InActive;
    		}
    		if( !expiryDate.after(CommonUtilities.getCurrentDate())) {
           		return EffectiveStatus.InActive;
           	}
    	}
		return EffectiveStatus.Active;
    }
    public boolean hasExpired(){
    	if(expiryDate!=null){ 
    		if( expiryDate.before(TredsHelper.getInstance().getBusinessDate())	){
        		return true;
    		}
    		if(expiryDate.before(CommonUtilities.getCurrentDate())){
           		return true;
           	}
    	}
    	return false;
    }

    public void setEffectiveStatus(EffectiveStatus pEffectiveStatus) {
    }

    
    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus pApprovalStatus) {
        approvalStatus = pApprovalStatus;
    }

    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String pApprovalRemarks) {
        approvalRemarks = pApprovalRemarks;
    }

    public Long getMakerAUId() {
        return makerAUId;
    }

    public void setMakerAUId(Long pMakerAUId) {
        makerAUId = pMakerAUId;
    }

    public String getMakerUserLogin() {
        if (makerAUId == null)
            return null;
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppUserBean.ENTITY_NAME);
            AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(AppUserBean.f_Id, new Long[]{makerAUId});
            if (lAppUserBean != null)
                return lAppUserBean.getLoginId();
        } catch (Exception lException) {
        }
        return null;
    }

    public void setMakerUserLogin(Long pMakerUserLogin) {
    }

    public Long getCheckerAUId() {
        return checkerAUId;
    }

    public void setCheckerAUId(Long pCheckerAUId) {
        checkerAUId = pCheckerAUId;
    }

    public String getCheckerUserLogin() {
        if (checkerAUId == null)
            return null;
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppUserBean.ENTITY_NAME);
            AppUserBean lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(AppUserBean.f_Id, new Long[]{checkerAUId});
            if (lAppUserBean != null)
                return lAppUserBean.getLoginId();
        } catch (Exception lException) {
        }
        return null;
    }

    public void setCheckerUserLogin(String pCheckerUserLogin) {
    }

    public Yes getBypassCheckForDelete() {
        return bypassCheckForDelete;
    }

    public void setBypassCheckForDelete(Yes pBypassCheckForDelete) {
        bypassCheckForDelete = pBypassCheckForDelete;
    }

    public Yes getWithdrawBidModChecker() {
        return withdrawBidModChecker;
    }

    public void setWithdrawBidModChecker(Yes pWithdrawBidModChecker) {
        withdrawBidModChecker = pWithdrawBidModChecker;
    }

    public Yes getSellerLimitMandatory() {
        return sellerLimitMandatory;
    }

    public void setSellerLimitMandatory(Yes pSellerLimitMandatory) {
        sellerLimitMandatory = pSellerLimitMandatory;
    }

    public Long getFinClId() {
        return finClId;
    }

    public void setFinClId(Long pFinClId) {
        finClId = pFinClId;
    }

    public String getFinancierLocation() {
        return financierLocation;
    }

    public void setFinancierLocation(String pFinanciertLocation) {
        financierLocation = pFinanciertLocation;
    }
    
    public String getCheckerFlag() {
        return checkerFlag;
    }

    public void setCheckerFlag(String pCheckerFlag) {
        checkerFlag = pCheckerFlag;
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

    public String getEffRateRange() {
        List<Map<String, Object>> lList = new ArrayList<Map<String,Object>>();
        if ((baseRateList != null) && (minSpread != null) && (maxSpread != null)) {
            for (TenureWiseBaseRateBean lBean : baseRateList) {
                Map<String, Object> lMap = new HashMap<String, Object>();
                lMap.put("day", lBean.getTenure());
                BigDecimal lMin = lBean.getBaseRate().add(minSpread);
                BigDecimal lMax = lBean.getBaseRate().add(maxSpread);
                if ((minBidRate != null) && (minBidRate.compareTo(lMin) > 0))
                    lMin = minBidRate;
                if ((maxBidRate != null) && (maxBidRate.compareTo(lMax) < 0))
                    lMax = maxBidRate;
                lMap.put("min", lMin);
                lMap.put("max", lMax);
                lList.add(lMap);
            }
        }
        else if ((minBidRate != null) && (maxBidRate != null)) {
            Map<String, Object> lMap = new HashMap<String, Object>();
            lMap.put("min", minBidRate);
            lMap.put("max", maxBidRate);
            lList.add(lMap);
        }
        return new JsonBuilder(lList).toString();
    }
    public void setEffRateRange(String pEffRateRange) {
    }

    public String getFinName() {
        if (financier != null) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            try {
                AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{financier});
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            } catch (Exception lException) {
            }
        }
        return null;
    }
    public void setFinName(String pFinName) {
    }
    
    public String getPurName() {
        if (purchaser != null) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            try {
                AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{purchaser});
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            } catch (Exception lException) {
            }
        }
        return null;
    }
    public void setPurName(String pPurName) {
    }
    
    public String getSupName() {
        if (supplier != null) {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            try {
                AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{supplier});
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            } catch (Exception lException) {
            }
        }
        return null;
    }
    public void setSupName(String pSupName) {
        
    }
    
    public BigDecimal getBaseBidRate(Long pTenure) {
        if ((pTenure != null) && (baseRateList != null)) {
            int lTenure = pTenure.intValue();
            int lSelectedTenure = Integer.MAX_VALUE;
            TenureWiseBaseRateBean lTenureWiseBaseRateBean = null;
            for (TenureWiseBaseRateBean lBean : baseRateList) {
                int lTempTenure = lBean.getTenure().intValue();
                if (lTempTenure >= lTenure) {
                    if (lTenureWiseBaseRateBean == null) {
                        lTenureWiseBaseRateBean = lBean;
                        lSelectedTenure = lTempTenure;
                    } else if (lTempTenure < lSelectedTenure) {
                        lTenureWiseBaseRateBean = lBean;
                        lSelectedTenure = lTempTenure;
                    }
                }
            }
            if (lTenureWiseBaseRateBean != null)
                return lTenureWiseBaseRateBean.getBaseRate();
        }
        return null;
    }
    
    public String getTitle(){
    	return FinancierAuctionSettingBean.getTitle(level);
    }
    
    public static String getTitle(Level pLevel){
    	switch(pLevel) {
    	case Financier_Self:
    	    return  "platform limit";
    	case Financier_Buyer:
    	    return "buyer limit";
    	case Financier_Buyer_Seller:
    	    return "buyer seller limit";
    	case Financier_User:
    	    return "user limit";
    	}
    	return "";
    }
    
    
    public Yes getIsLocationEnabled() {
		return isLocationEnabled;
	}

	public void setIsLocationEnabled(Yes pIsLocationEnabled) {
		this.isLocationEnabled = pIsLocationEnabled;
	}
    
	public List<ApprovalStatus> getTabWiseStatus(Long pTab){
		Map<Long, List<ApprovalStatus>> lMap = new HashMap<Long, List<ApprovalStatus>>();
//		 Draft("DFT","Draft"),Submitted("SUB","Submitted")
//		 ,Returned("RET","Returned"),Rejected("REJ","Rejected")
//		 ,Approved("APP","Approved"),Deleted("DEL","Deleted");
		List<ApprovalStatus> lList = new ArrayList<ApprovalStatus>(); 
		if (pTab.equals(new Long(0))){
			lList.add(ApprovalStatus.Draft);
			lList.add(ApprovalStatus.Returned);
		}else if(pTab.equals(new Long(1)) || pTab.equals(new Long(2))){
			lList.add(ApprovalStatus.Submitted);
		}else if(pTab.equals(new Long(3))){
			lList.add(ApprovalStatus.Approved);
		}
		if (!lList.isEmpty()){
			 return lList;
		}
		return null;
	}
	
    public Long getChkLevel() {
        return chkLevel;
    }

    public void setChkLevel(Long pChkLevel) {
        chkLevel = pChkLevel;
    }
    
    public String getCersaiCode() {
        return cersaiCode;
    }

    public void setCersaiCode(String pCersaiCode) {
        cersaiCode = pCersaiCode;
    }
    
}