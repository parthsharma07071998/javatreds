package com.xlx.treds.instrument.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.AutoAcceptableBidTypes;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.ChargeType;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.BidBean.BidType;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.SplittingPoint;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonSlurper;

public class FactoringUnitBean implements ILegInterest{
	private static final Logger logger = LoggerFactory.getLogger(FactoringUnitBean.class);

	public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    public static final String FIELDGROUP_ACCEPTBID = "acceptBid";
    public static final String FIELDGROUP_UPDATEBESTBID = "updateBestBid";
    public static final String FIELDGROUP_SUPPURLIST = "supPurList";
    public static final String FIELDGROUP_FINLIST = "finList";
    public static final String FIELDGROUP_UPDATELEG3FLAG = "updateLeg3Flag";
    public static final String FIELDGROUP_UPDATEPURSUPLIMIT = "updatePurSupLimit";
    public static final String FIELDGROUP_UPDATEBILLID = "updateBillId";
    
    public static final String FIELDGROUP_UPDATEEXTENSION = "updateExtenstion";
        
    public static final String FIELDGROUP_UPDATEWITHDRAWNSTATUS = "updateWithdrawnStatus";
    
    public static final String FIELDGROUP_BRINGBACKTOAUCTION = "bringBackToAuction";
    public static final String FIELDGROUP_UPDATEGSTEXTENSION = "updateGstExtension";
    private BeanMeta gstSummaryBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(GstSummaryBean.class);
    
    public enum Status implements IKeyValEnumInterface<String>{
        Ready_For_Auction("RDY","Ready For Auction"),Active("ACT","Active"),Factored("FACT","Factored"),Bid_Acceptance_In_Progress("WAIT","Bid Acceptance In Progress"),Expired("EXP","Expired"),Leg_3_Generated("LEG3","Leg 3 Generated"),Withdrawn("WTHDRN","Withdrawn"),Suspended("SUSP","On Hold"),Leg_1_Settled("L1SET","Leg 1 Settled"),Leg_1_Failed("L1FAIL","Leg 1 Failed"),Leg_2_Settled("L2SET","Leg 2 Settled"),Leg_2_Failed("L2FAIL","Leg 2 Failed"),Leg_3_Settled("L3SET","Leg 3 Settled"),Leg_3_Failed("L3FAIL","Leg 3 Failed");;
        
        private final String code;
        private final String desc;
        private Status(String pCode, String pDesc) {
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
    private Date maturityDate;
    private Date statDueDate;
    private Yes enableExtension;
    private Long extendedCreditPeriod;
    private Date extendedDueDate;
    private String currency;
    private BigDecimal amount;
    private String purchaser;
    private String purchaserRef;
    private String supplier;
    private String supplierRef;
    private String introducingEntity;
    private Long introducingAuId;
    private String introducingLoginId;
    private String introducingName;
    private String counterEntity;
    private Long counterAuId;
    private String counterLoginId;
    private String counterName;
    private String ownerEntity;
    private Long ownerAuId;
    private String ownerLoginId;
    private String ownerName;
    private Status status;
    private Timestamp factorStartDateTime;
    private Timestamp factorEndDateTime;
    private Timestamp factorMaxEndDateTime;
    private AutoAcceptBid autoAccept;
    private AutoAcceptableBidTypes autoAcceptableBidTypes;
    private AutoConvert autoConvert;
    private CostBearer period1CostBearer;
    private BigDecimal period1CostPercent;
    private CostBearer period2CostBearer;
    private BigDecimal period2CostPercent;
    private CostBearer period3CostBearer;
    private BigDecimal period3CostPercent;
    private CostBearer period1ChargeBearer;
    private BigDecimal period1ChargePercent;
    private CostBearer period2ChargeBearer;
    private BigDecimal period2ChargePercent;
    private CostBearer period3ChargeBearer;
    private BigDecimal period3ChargePercent;
    private String supGstState;
    private String supGstn;
    private String purGstState;
    private String purGstn;
    private YesNo settleLeg3Flag;
    private Long bdId;
    private BidType acceptedBidType;
    private BigDecimal acceptedRate;
    private BigDecimal acceptedHaircut;
    private Date leg1Date;
    private BigDecimal factoredAmount;
    private BigDecimal purchaserLeg1Interest;
    private BigDecimal supplierLeg1Interest;
    private BigDecimal purchaserLeg2Interest;
    private BigDecimal leg2ExtensionInterest;
    private BigDecimal charges;
    private String financier;
    private String acceptingEntity;
    private Long acceptingAuId;
    private String acceptingLoginId;
    private String acceptingName;
    private Timestamp acceptDateTime;
    private BigDecimal limitUtilized;
    private String limitIds;
    private BigDecimal purSupLimitUtilized;
    private Date filterMaturityDate;
    private BigDecimal filterAmount;
    private String salesCategory;
    private Long purchaserSettleLoc;
    private Long supplierSettleLoc;
    private Long purchaserBillLoc;
    private Long supplierBillLoc;
    private Long recordVersion;
    private String purName;
    private String supName;
    private String finName;
    private Long tenure;
    private Long filterFromTenure;
    private Long filterToTenure;
    private String filterSellerCategory;
    private String filterMsmeStatus;
    private BigDecimal capRate;
    private BigDecimal filterToCapRate;
    private BigDecimal interest;
    private String purBankName;
    private String supBankName;
    private String purIfsc;
    private String supIfsc;
    private String purAccNo;
    private String supAccNo;
    private Yes purDesignatedBankFlag;
    private Yes supDesignatedBankFlag;
    private Timestamp statusUpdateTime;
    private Long costBearerBillId;
    private Long financierBillId;
    private Long extBillId1;
    private Long extBillId2;
    private Timestamp recordCreateTime;
    private Long oldInstId;
    private List<GstSummaryBean> entityGstSummaryList;
    private Long financierSettleLoc;
    private Long financierBillLoc;
    private BigDecimal financierCharge;
    private BigDecimal chargeBearerCharge;
    private Date fromAcceptanceDate;
    private Date toAcceptanceDate;
    private Date fromFactorStartDate;
    private Date toFactorEndDate;
    private Yes fetchActiveBids;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date pMaturityDate) {
        maturityDate = pMaturityDate;
    }

    public Date getStatDueDate() {
        return statDueDate;
    }

    public void setStatDueDate(Date pStatDueDate) {
        statDueDate = pStatDueDate;
    }

    public Yes getEnableExtension() {
        return enableExtension;
    }

    public void setEnableExtension(Yes pEnableExtension) {
        enableExtension = pEnableExtension;
    }

    public Long getExtendedCreditPeriod() {
        return extendedCreditPeriod;
    }

    public void setExtendedCreditPeriod(Long pExtendedCreditPeriod) {
        extendedCreditPeriod = pExtendedCreditPeriod;
    }

    public Date getExtendedDueDate() {
        return extendedDueDate;
    }

    public void setExtendedDueDate(Date pExtendedDueDate) {
        extendedDueDate = pExtendedDueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String pCurrency) {
        currency = pCurrency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
    }

    public BigDecimal getNetAmount() {
        return amount;
    }
    public void setNetAmount(BigDecimal pNetAmount) {
    	amount = pNetAmount;
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

    public String getIntroducingEntity() {
        return introducingEntity;
    }

    public void setIntroducingEntity(String pIntroducingEntity) {
        introducingEntity = pIntroducingEntity;
    }

    public Long getIntroducingAuId() {
        return introducingAuId;
    }

    public void setIntroducingAuId(Long pIntroducingAuId) {
        introducingAuId = pIntroducingAuId;
        introducingLoginId = null;
        introducingName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{introducingAuId});
            if (lAppUserBean != null) {
                introducingLoginId = lAppUserBean.getLoginId();
                introducingName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getIntroducingLoginId() {
        return introducingLoginId;
    }

    public void setIntroducingLoginId(String pIntroducingLoginId) {
        introducingLoginId = pIntroducingLoginId;
    }

    public String getIntroducingName() {
        return introducingName;
    }

    public void setIntroducingName(String pIntroducingName) {
        introducingName = pIntroducingName;
    }

    public String getCounterEntity() {
        return counterEntity;
    }

    public void setCounterEntity(String pCounterEntity) {
        counterEntity = pCounterEntity;
    }

    public Long getCounterAuId() {
        return counterAuId;
    }

    public void setCounterAuId(Long pCounterAuId) {
        counterAuId = pCounterAuId;
        counterLoginId = null;
        counterName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{counterAuId});
            if (lAppUserBean != null) {
                counterLoginId = lAppUserBean.getLoginId();
                counterName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getCounterLoginId() {
        return counterLoginId;
    }

    public void setCounterLoginId(String pCounterLoginId) {
        counterLoginId = pCounterLoginId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String pCounterName) {
        counterName = pCounterName;
    }

    public String getOwnerEntity() {
        return ownerEntity;
    }

    public void setOwnerEntity(String pOwnerEntity) {
        ownerEntity = pOwnerEntity;
    }

    public Long getOwnerAuId() {
        return ownerAuId;
    }

    public void setOwnerAuId(Long pOwnerAuId) {
        ownerAuId = pOwnerAuId;
        ownerLoginId = null;
        ownerName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{ownerAuId});
            if (lAppUserBean != null) {
                ownerLoginId = lAppUserBean.getLoginId();
                ownerName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getOwnerLoginId() {
        return ownerLoginId;
    }

    public void setOwnerLoginId(String pOwnerLoginId) {
        ownerLoginId = pOwnerLoginId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String pOwnerName) {
        ownerName = pOwnerName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Timestamp getFactorStartDateTime() {
        return factorStartDateTime;
    }

    public void setFactorStartDateTime(Timestamp pFactorStartDateTime) {
        factorStartDateTime = pFactorStartDateTime;
    }

    public Timestamp getFactorEndDateTime() {
        return factorEndDateTime;
    }

    public void setFactorEndDateTime(Timestamp pFactorEndDateTime) {
        factorEndDateTime = pFactorEndDateTime;
    }

    public Timestamp getFactorMaxEndDateTime() {
        return factorMaxEndDateTime;
    }

    public void setFactorMaxEndDateTime(Timestamp pFactorMaxEndDateTime) {
        factorMaxEndDateTime = pFactorMaxEndDateTime;
    }

    public AutoAcceptBid getAutoAccept() {
        return autoAccept;
    }

    public void setAutoAccept(AutoAcceptBid pAutoAccept) {
        autoAccept = pAutoAccept;
    }

    public AutoAcceptableBidTypes getAutoAcceptableBidTypes() {
        return autoAcceptableBidTypes;
    }

    public void setAutoAcceptableBidTypes(AutoAcceptableBidTypes pAutoAcceptableBidTypes) {
        autoAcceptableBidTypes = pAutoAcceptableBidTypes;
    }
    
    public AutoConvert getAutoConvert() {
        return autoConvert;
    }

    public void setAutoConvert(AutoConvert pAutoConvert) {
        autoConvert = pAutoConvert;
    }
    
    
    public CostBearer getPeriod1CostBearer() {
        return period1CostBearer;
    }

    public void setPeriod1CostBearer(CostBearer pPeriod1CostBearer) {
        period1CostBearer = pPeriod1CostBearer;
    }

    public BigDecimal getPeriod1CostPercent() {
        return period1CostPercent;
    }

    public void setPeriod1CostPercent(BigDecimal pPeriod1CostPercent) {
        period1CostPercent = pPeriod1CostPercent;
    }

    public CostBearer getPeriod2CostBearer() {
        return period2CostBearer;
    }

    public void setPeriod2CostBearer(CostBearer pPeriod2CostBearer) {
        period2CostBearer = pPeriod2CostBearer;
    }

    public BigDecimal getPeriod2CostPercent() {
        return period2CostPercent;
    }

    public void setPeriod2CostPercent(BigDecimal pPeriod2CostPercent) {
        period2CostPercent = pPeriod2CostPercent;
    }

    public CostBearer getPeriod3CostBearer() {
        return period3CostBearer;
    }

    public void setPeriod3CostBearer(CostBearer pPeriod3CostBearer) {
        period3CostBearer = pPeriod3CostBearer;
    }

    public BigDecimal getPeriod3CostPercent() {
        return period3CostPercent;
    }

    public void setPeriod3CostPercent(BigDecimal pPeriod3CostPercent) {
        period3CostPercent = pPeriod3CostPercent;
    }
    
    public CostBearer getPeriod1ChargeBearer() {
        return period1ChargeBearer;
    }

    public void setPeriod1ChargeBearer(CostBearer pPeriod1ChargeBearer) {
        period1ChargeBearer = pPeriod1ChargeBearer;
    }

    public BigDecimal getPeriod1ChargePercent() {
        return period1ChargePercent;
    }

    public void setPeriod1ChargePercent(BigDecimal pPeriod1ChargePercent) {
        period1ChargePercent = pPeriod1ChargePercent;
    }

    public CostBearer getPeriod2ChargeBearer() {
        return period2ChargeBearer;
    }

    public void setPeriod2ChargeBearer(CostBearer pPeriod2ChargeBearer) {
        period2ChargeBearer = pPeriod2ChargeBearer;
    }

    public BigDecimal getPeriod2ChargePercent() {
        return period2ChargePercent;
    }

    public void setPeriod2ChargePercent(BigDecimal pPeriod2ChargePercent) {
        period2ChargePercent = pPeriod2ChargePercent;
    }

    public CostBearer getPeriod3ChargeBearer() {
        return period3ChargeBearer;
    }

    public void setPeriod3ChargeBearer(CostBearer pPeriod3ChargeBearer) {
        period3ChargeBearer = pPeriod3ChargeBearer;
    }

    public BigDecimal getPeriod3ChargePercent() {
        return period3ChargePercent;
    }

    public void setPeriod3ChargePercent(BigDecimal pPeriod3ChargePercent) {
        period3ChargePercent = pPeriod3ChargePercent;
    }

    public String getSupGstState() {
        return supGstState;
    }

    public void setSupGstState(String pSupGstState) {
        supGstState = pSupGstState;
    }

    public String getSupGstn() {
        return supGstn;
    }

    public void setSupGstn(String pSupGstn) {
        supGstn = pSupGstn;
    }

    public String getPurGstState() {
        return purGstState;
    }

    public void setPurGstState(String pPurGstState) {
        purGstState = pPurGstState;
    }

    public String getPurGstn() {
        return purGstn;
    }

    public void setPurGstn(String pPurGstn) {
        purGstn = pPurGstn;
    }

    public YesNo getSettleLeg3Flag() {
        return settleLeg3Flag;
    }

    public void setSettleLeg3Flag(YesNo pSettleLeg3Flag) {
        settleLeg3Flag = pSettleLeg3Flag;
    }

    public Long getBdId() {
        return bdId;
    }

    public void setBdId(Long pBdId) {
        bdId = pBdId;
    }

    public BidType getAcceptedBidType() {
        return acceptedBidType;
    }

    public void setAcceptedBidType(BidType pAcceptedBidType) {
        acceptedBidType = pAcceptedBidType;
    }

    public BigDecimal getAcceptedRate() {
        return acceptedRate;
    }

    public void setAcceptedRate(BigDecimal pAcceptedRate) {
        acceptedRate = pAcceptedRate;
    }

    public BigDecimal getAcceptedHaircut() {
        return acceptedHaircut;
    }

    public void setAcceptedHaircut(BigDecimal pAcceptedHaircut) {
        acceptedHaircut = pAcceptedHaircut;
    }

    public Date getLeg1Date() {
        return leg1Date;
    }

    public void setLeg1Date(Date pLeg1Date) {
        leg1Date = pLeg1Date;
    }

    public BigDecimal getFactoredAmount() {
        return factoredAmount;
    }

    public void setFactoredAmount(BigDecimal pFactoredAmount) {
        factoredAmount = pFactoredAmount;
    }

    public BigDecimal getPurchaserLeg1Interest() {
        return purchaserLeg1Interest;
    }

    public void setPurchaserLeg1Interest(BigDecimal pPurchaserLeg1Interest) {
        purchaserLeg1Interest = pPurchaserLeg1Interest;
    }

    public BigDecimal getSupplierLeg1Interest() {
        return supplierLeg1Interest;
    }

    public void setSupplierLeg1Interest(BigDecimal pSupplierLeg1Interest) {
        supplierLeg1Interest = pSupplierLeg1Interest;
    }

    public BigDecimal getPurchaserLeg2Interest() {
        return purchaserLeg2Interest;
    }

    public void setPurchaserLeg2Interest(BigDecimal pPurchaserLeg2Interest) {
        purchaserLeg2Interest = pPurchaserLeg2Interest;
    }

    public BigDecimal getLeg2ExtensionInterest() {
        return leg2ExtensionInterest;
    }

    public void setLeg2ExtensionInterest(BigDecimal pLeg2ExtensionInterest) {
        leg2ExtensionInterest = pLeg2ExtensionInterest;
    }
    /*
    public BigDecimal getCharges() {
        return charges;
    }

    public void setCharges(BigDecimal pCharges) {
        charges = pCharges;
    }

    public BigDecimal getCgst() {
    	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
     	if(lChargBearerGst!=null && lChargBearerGst.getCgst()!=null){
    		return lChargBearerGst.getCgst();
    	}
        return BigDecimal.ZERO;
    }

    public void setCgst(BigDecimal pCgst) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setCgst(pCgst);
    }

    public BigDecimal getSgst() {
    	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getSgst()!=null){
    		return lChargBearerGst.getSgst();
    	}
        return BigDecimal.ZERO;
    }

    public void setSgst(BigDecimal pSgst) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setSgst(pSgst);    
    }

    public BigDecimal getIgst() {
    	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getIgst()!=null){
    		return lChargBearerGst.getIgst();
    	}
        return BigDecimal.ZERO;
    }

    public void setIgst(BigDecimal pIgst) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setIgst(pIgst);
    }

    public BigDecimal getCgstSurcharge() {
    	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getCgstSurcharge()!=null){
    		return lChargBearerGst.getCgstSurcharge();
    	}
        return BigDecimal.ZERO;
    }

    public void setCgstSurcharge(BigDecimal pCgstSurcharge) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setCgstSurcharge(pCgstSurcharge);
    }

    public BigDecimal getSgstSurcharge() {
      	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getSgstSurcharge()!=null){
    		return lChargBearerGst.getSgstSurcharge();
    	}
        return BigDecimal.ZERO;
    }

    public void setSgstSurcharge(BigDecimal pSgstSurcharge) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setSgstSurcharge(pSgstSurcharge);
    }

    public BigDecimal getIgstSurcharge() {
      	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getIgstSurcharge()!=null){
    		return lChargBearerGst.getIgstSurcharge();
    	}
        return BigDecimal.ZERO;
    }

    public void setIgstSurcharge(BigDecimal pIgstSurcharge) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setIgstSurcharge(pIgstSurcharge);
    }

    public BigDecimal getCgstValue() {
      	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getCgstValue()!=null){
    		return lChargBearerGst.getCgstValue();
    	}
        return BigDecimal.ZERO;
    }

    public void setCgstValue(BigDecimal pCgstValue) {
      	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setCgstValue(pCgstValue);
    }
    
    public BigDecimal getSgstValue() {
      	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getSgstValue()!=null){
    		return lChargBearerGst.getSgstValue();
    	}
        return BigDecimal.ZERO;
    }

    public void setSgstValue(BigDecimal pSgstValue) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setSgstValue(pSgstValue);
    }

    public BigDecimal getIgstValue() {
      	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst!=null && lChargBearerGst.getIgstValue()!=null){
    		return lChargBearerGst.getIgstValue();
    	}
        return BigDecimal.ZERO;
    }

    public void setIgstValue(BigDecimal pIgstValue) {
       	GstSummaryBean lChargBearerGst = getGstSummary(getChargeBearerEntityCode());
    	if(lChargBearerGst==null){
    		lChargBearerGst = addGstSummaryBean(getChargeBearerEntityCode());
    	}
    	lChargBearerGst.setIgstValue(pIgstValue);
    }
*/
    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String pFinancier) {
        financier = pFinancier;
    }

    public String getAcceptingEntity() {
        return acceptingEntity;
    }

    public void setAcceptingEntity(String pAcceptingEntity) {
        acceptingEntity = pAcceptingEntity;
    }

    public Long getAcceptingAuId() {
        return acceptingAuId;
    }

    public void setAcceptingAuId(Long pAcceptingAuId) {
        acceptingAuId = pAcceptingAuId;
        acceptingLoginId = null;
        acceptingLoginId = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{acceptingAuId});
            if (lAppUserBean != null) {
                acceptingLoginId = lAppUserBean.getLoginId();
                acceptingName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getAcceptingLoginId() {
        return acceptingLoginId;
    }

    public void setAcceptingLoginId(String pAcceptingLoginId) {
        acceptingLoginId = pAcceptingLoginId;
    }

    public String getAcceptingName() {
        return acceptingName;
    }

    public void setAcceptingName(String pAcceptingName) {
        acceptingName = pAcceptingName;
    }

    public Timestamp getAcceptDateTime() {
        return acceptDateTime;
    }

    public void setAcceptDateTime(Timestamp pAcceptDateTime) {
        acceptDateTime = pAcceptDateTime;
    }

    public BigDecimal getLimitUtilized() {
        return limitUtilized;
    }

    public void setLimitUtilized(BigDecimal pLimitUtilized) {
        limitUtilized = pLimitUtilized;
    }

    public String getLimitIds() {
        return limitIds;
    }

    public void setLimitIds(String pLimitIds) {
        limitIds = pLimitIds;
    }

    public BigDecimal getPurSupLimitUtilized() {
        return purSupLimitUtilized;
    }

    public void setPurSupLimitUtilized(BigDecimal pPurSupLimitUtilized) {
        purSupLimitUtilized = pPurSupLimitUtilized;
    }

    public Date getFilterMaturityDate() {
        return filterMaturityDate;
    }

    public void setFilterMaturityDate(Date pFilterMaturityDate) {
        filterMaturityDate = pFilterMaturityDate;
    }

    public BigDecimal getFilterAmount() {
        return filterAmount;
    }

    public void setFilterAmount(BigDecimal pFilterAmount) {
        filterAmount = pFilterAmount;
    }

    public String getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(String pSalesCategory) {
        salesCategory = pSalesCategory;
    }
    public Long getPurchaserSettleLoc() {
        return purchaserSettleLoc;
    }
    public void setPurchaserSettleLoc(Long pPurchaserSettleLoc) {
    	purchaserSettleLoc = pPurchaserSettleLoc;
    }
    public Long getSupplierSettleLoc() {
        return supplierSettleLoc;
    }
    public void setSupplierSettleLoc(Long pSupplierSettleLoc) {
    	supplierSettleLoc = pSupplierSettleLoc;
    }
    
    public Long getPurchaserBillLoc() {
        return purchaserBillLoc;
    }
    public void setPurchaserBillLoc(Long pPurchaserBillLoc) {
    	purchaserBillLoc = pPurchaserBillLoc;
    }
    public Long getSupplierBillLoc() {
        return supplierBillLoc;
    }
    public void setSupplierBillLoc(Long pSupplierBillLoc) {
    	supplierBillLoc = pSupplierBillLoc;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
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

    public Long getTenure() {
    	if(Status.Active.equals(status) || Status.Ready_For_Auction.equals(status) || Status.Suspended.equals(status)  || Status.Withdrawn.equals(status))
    		return TredsHelper.getInstance().getTenure(getLeg2MaturityExtendedDate());
    	else
    		return TredsHelper.getInstance().getTenure(getLeg2MaturityExtendedDate(),  getLeg1Date());
    }

    public void setTenure(Long pTenure) {
    }

    public Long getFilterFromTenure() {
        return filterFromTenure;
    }

    public void setFilterFromTenure(Long pFilterFromTenure) {
        filterFromTenure = pFilterFromTenure;
    }

    public Long getFilterToTenure() {
        return filterToTenure;
    }

    public void setFilterToTenure(Long pFilterToTenure) {
        filterToTenure = pFilterToTenure;
    }

    public String getFilterSellerCategory() {
        return filterSellerCategory;
    }

    public void setFilterSellerCategory(String pFilterSellerCategory) {
        filterSellerCategory = pFilterSellerCategory;
    }

    public String getFilterMsmeStatus() {
        return filterMsmeStatus;
    }

    public void setFilterMsmeStatus(String pFilterMsmeStatus) {
        filterMsmeStatus = pFilterMsmeStatus;
    }

    public BigDecimal getCapRate() {
        return capRate;
    }

    public void setCapRate(BigDecimal pCapRate) {
        capRate = pCapRate;
    }

    public BigDecimal getFilterToCapRate() {
        return filterToCapRate;
    }

    public void setFilterToCapRate(BigDecimal pFilterToCapRate) {
        filterToCapRate = pFilterToCapRate;
    }
    
    public void setAcceptedBid(BidBean pBidBean) {
        if (pBidBean == null) {
            bdId = null;
            acceptedBidType = null;
            acceptedRate = null;
            acceptedHaircut = null;
        } else {
            bdId = pBidBean.getId();
            acceptedBidType = pBidBean.getBidType();
            acceptedRate = pBidBean.getRate();
            acceptedHaircut = pBidBean.getHaircut();
        }
    }
    
    public Date getLeg2MaturityExtendedDate() {
    	if(CommonAppConstants.Yes.Yes.equals(enableExtension) &&
    		extendedDueDate != null	){
    		return extendedDueDate;
    	}
        return maturityDate;
    }
    
    public boolean isPurchaserCostBearer(){
    	CostBearingType lCostBearingType = getCostBearingType();
    	return (lCostBearingType!=null && !(CostBearingType.Seller.equals(lCostBearingType)));
    }
    public boolean isSupplierCostBearer(){
    	CostBearingType lCostBearingType = getCostBearingType();
    	return (lCostBearingType!=null && !(CostBearingType.Buyer.equals(lCostBearingType)));
    }
    public boolean isPurchaserCompleteCostBearer(){
    	CostBearingType lCostBearingType = getCostBearingType();
    	return (lCostBearingType!=null && CostBearingType.Buyer.equals(lCostBearingType));
    }
    public boolean isSupplierCompleteCostBearer(){
    	CostBearingType lCostBearingType = getCostBearingType();
    	return (lCostBearingType!=null && CostBearingType.Seller.equals(lCostBearingType));
    }
    public BigDecimal getTotalCost(){
        BigDecimal lTotalCost = new BigDecimal(0);
        if(this.getPurchaserLeg1Interest()!=null) lTotalCost = lTotalCost.add(this.getPurchaserLeg1Interest());
        if(this.getSupplierLeg1Interest()!=null) lTotalCost = lTotalCost.add(this.getSupplierLeg1Interest());
        if(this.getPurchaserLeg2Interest()!=null) lTotalCost = lTotalCost.add(this.getPurchaserLeg2Interest());
        if(this.getLeg2ExtensionInterest()!=null) lTotalCost = lTotalCost.add(this.getLeg2ExtensionInterest());
        return lTotalCost;
    }
    
    public boolean isCostSplit(){
    	CostBearingType lCostBearingType = getCostBearingType();
		return (lCostBearingType!=null && !CostBearingType.Buyer.equals(lCostBearingType) && !CostBearingType.Seller.equals(lCostBearingType));
    }
    
    public CostBearingType getCostBearingType(){
    	CostBearingType lCostBearingType = null;
    	//
    	if(this.getPeriod1CostPercent()==null || this.getPeriod2CostPercent() == null || this.getPeriod3CostPercent() == null){
			logger.info("Cannot determine NonDbFields of PSLinkBean.");
    		return null;
    	}
    	//
    	if(this.getPeriod1CostBearer().equals(CostBearer.Buyer) &&
    		this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) && 
			this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer()) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0)){
	
    		if(this.getPeriod1CostPercent().compareTo(BigDecimal.valueOf(100))==0){
    			lCostBearingType = CostBearingType.Buyer;
			}else if (this.getPeriod1CostPercent().compareTo(new BigDecimal(100)) == -1){
				lCostBearingType = CostBearingType.Percentage_Split;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if(this.getPeriod1CostBearer().equals(CostBearer.Seller) &&
			this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) && 
			this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer()) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0) ){

    		if(this.getPeriod1CostPercent().compareTo(BigDecimal.valueOf(100))==0){
    			lCostBearingType = CostBearingType.Seller;
			}else if (this.getPeriod1CostPercent().compareTo(new BigDecimal(100)) == -1){
				lCostBearingType = CostBearingType.Percentage_Split;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if((this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0) ){
			if(this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) &&
					!this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer())){
				lCostBearingType = CostBearingType.Periodical_Split;
			}else if(!this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) &&
					this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer())){
				lCostBearingType = CostBearingType.Periodical_Split;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}
    	return lCostBearingType;
    }
    
    public CostBearingType getChargeBearer(){
    	CostBearingType lChargeBearingType = null;
    	//
    	if(this.getPeriod1ChargePercent()==null || this.getPeriod2ChargePercent() == null || this.getPeriod3ChargePercent() == null){
			logger.info("Cannot determine NonDbFields of PSLinkBean.");
    		return null;
    	}
    	//
    	if(this.getPeriod1ChargeBearer().equals(CostBearer.Buyer) &&
    		this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) && 
			this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer()) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod2ChargePercent())==0) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod3ChargePercent())==0)){
	
    		if(this.getPeriod1ChargePercent().compareTo(BigDecimal.valueOf(100))==0){
    			lChargeBearingType = CostBearingType.Buyer;
			}else if (this.getPeriod1ChargePercent().compareTo(new BigDecimal(100)) == -1){
				lChargeBearingType = CostBearingType.Percentage_Split;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if(this.getPeriod1ChargeBearer().equals(CostBearer.Seller) &&
			this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) && 
			this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer()) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod2ChargePercent())==0) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod3ChargePercent())==0) ){

    		if(this.getPeriod1ChargePercent().compareTo(BigDecimal.valueOf(100))==0){
    			lChargeBearingType = CostBearingType.Seller;
			}else if (this.getPeriod1ChargePercent().compareTo(new BigDecimal(100)) == -1){
				lChargeBearingType = CostBearingType.Percentage_Split;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if((this.getPeriod1ChargePercent().compareTo(this.getPeriod2ChargePercent())==0) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod3ChargePercent())==0) ){
			if(this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) &&
					!this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer())){
				lChargeBearingType = CostBearingType.Periodical_Split;
			}else if(!this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) &&
					this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer())){
				lChargeBearingType = CostBearingType.Periodical_Split;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}
    	return lChargeBearingType;
    }

    
    public Long getChargeBearerBillLocationId(){
    	if(CostBearingType.Buyer.equals(getChargeBearer()))
    		return purchaserBillLoc!=null?purchaserBillLoc:purchaserSettleLoc;
    	else if(CostBearingType.Seller.equals(getChargeBearer()))
    		return supplierBillLoc!=null?supplierBillLoc:supplierSettleLoc;
    	return null;
    }
    
    public Long getBillLocationIdForChargeSplit(CostBearer pChargeBearer){
    	if(CostBearer.Buyer.equals(pChargeBearer))
    		return purchaserBillLoc!=null?purchaserBillLoc:purchaserSettleLoc;
    	else if(CostBearer.Seller.equals(pChargeBearer))
    		return supplierBillLoc!=null?supplierBillLoc:supplierSettleLoc;
    	return null;
    }
    
    
    public String getChargeBearerStateCode(){
    	if(CostBearingType.Buyer.equals(getChargeBearer()))
    		return purGstState;
    	else if(CostBearingType.Seller.equals(getChargeBearer()))
    		return supGstState;
    	return "";
    }
    public String getChargeBearerEntityCode(){
    	if(CostBearingType.Buyer.equals(getChargeBearer()))
    		return purchaser;
    	else if(CostBearingType.Seller.equals(getChargeBearer()))
    		return supplier;
    	return "";
    }
    public BigDecimal getTotalChargesIncludingTax(){
    	//return charges.add(cgstValue).add(sgstValue).add(igstValue);
    	GstSummaryBean lGstSummaryBean = getGstSummary(getChargeBearerEntityCode());
    	BigDecimal lCharges = charges;
    	if(lCharges == null ) lCharges = BigDecimal.ZERO;
    	if(lGstSummaryBean!=null){
    		lCharges = lCharges.add(lGstSummaryBean.getCgstValue());
    		lCharges = lCharges.add(lGstSummaryBean.getSgstValue());
    		lCharges = lCharges.add(lGstSummaryBean.getIgstValue());
    		lCharges = lCharges.add(lGstSummaryBean.getCharge());
    	}
    	return lCharges;
    }
    
    public BigDecimal getTotalChargesExcludingTax(){
    	//return charges.add(cgstValue).add(sgstValue).add(igstValue);
    	GstSummaryBean lGstSummaryBean = getGstSummary(getChargeBearerEntityCode());
    	BigDecimal lCharges = charges;
    	if(lCharges == null ) lCharges = BigDecimal.ZERO;
    	if(lGstSummaryBean!=null){
    		lCharges = lCharges.add(lGstSummaryBean.getCharge());
    	}
    	return lCharges;
    }
    
    public BigDecimal getTotalChargesTax(){
    	//return charges.add(cgstValue).add(sgstValue).add(igstValue);
    	GstSummaryBean lGstSummaryBean = getGstSummary(getChargeBearerEntityCode());
    	BigDecimal lCharges = charges;
    	if(lCharges == null ) lCharges = BigDecimal.ZERO;
    	if(lGstSummaryBean!=null){
    		lCharges = lCharges.add(lGstSummaryBean.getCgstValue());
    		lCharges = lCharges.add(lGstSummaryBean.getSgstValue());
    		lCharges = lCharges.add(lGstSummaryBean.getIgstValue());
    	}
    	return lCharges;
    }
    
    public BigDecimal getTotalPurchaserInterest(){
    	BigDecimal lTotal = BigDecimal.ZERO;
    	if(purchaserLeg1Interest!=null) lTotal = lTotal.add(purchaserLeg1Interest);
    	if(purchaserLeg2Interest!=null) lTotal = lTotal.add(purchaserLeg2Interest);
    	return lTotal;
    }
    
    public BigDecimal getFinLeg2Amount(){
    	BigDecimal lTotal = BigDecimal.ZERO;
    	if(factoredAmount!=null) lTotal = lTotal.add(factoredAmount);
    	if(purchaserLeg2Interest!=null) lTotal = lTotal.add(purchaserLeg2Interest);
    	return lTotal;
    }
    
    public Long getTab(){
    	Long lTab =  null;
    	Status lStatus = getStatus();
		  if (FactoringUnitBean.Status.Ready_For_Auction.equals(lStatus)){
		      lTab = FactoringUnitBO.TABSP_READYFORAUCTION;
		  }
		  else if (FactoringUnitBean.Status.Active.equals(lStatus))
		      lTab = FactoringUnitBO.TABSP_ACTIVE;
		  else if (FactoringUnitBean.Status.Factored.equals(lStatus)) {
		      lTab = FactoringUnitBO.TABSP_FACTORED;
		  }
		  else if (FactoringUnitBean.Status.Suspended.equals(lStatus)) {
		      lTab = FactoringUnitBO.TABSP_SUSPENDED;
		  }
		  else if (FactoringUnitBean.Status.Expired.equals(lStatus)) {
		      lTab = FactoringUnitBO.TABSP_SUSPENDED;
		  }   	
		  return lTab;
    }
    public boolean isFactored(){
    	if(Status.Factored.equals(status) ||
    			Status.Leg_1_Settled.equals(status) ||
    			Status.Leg_1_Failed.equals(status) ||
    			Status.Leg_2_Settled.equals(status) ||
    			Status.Leg_2_Failed.equals(status) ||
    			Status.Leg_3_Generated.equals(status)){
    		return true;
    	}
    	return false;
    }
    
    public BigDecimal getInterest(){
		return getTotalCost();
    }
    
    public void setInterest(BigDecimal pInterest){
    	
    }
    
    public String getPurBankName() {
        return purBankName;
    }

    public void setPurBankName(String pPurBankName) {
        purBankName = pPurBankName;
    }

    public String getSupBankName() {
        return supBankName;
    }

    public void setSupBankName(String pSupBankName) {
        supBankName = pSupBankName;
    }

    public String getPurIfsc() {
        return purIfsc;
    }

    public void setPurIfsc(String pPurIfsc) {
        purIfsc = pPurIfsc;
    }

    public String getSupIfsc() {
        return supIfsc;
    }

    public void setSupIfsc(String pSupIfsc) {
        supIfsc = pSupIfsc;
    }

    public String getPurAccNo() {
        return purAccNo;
    }

    public void setPurAccNo(String pPurAccNo) {
        purAccNo = pPurAccNo;
    }

    public String getSupAccNo() {
        return supAccNo;
    }

    public void setSupAccNo(String pSupAccNo) {
        supAccNo = pSupAccNo;
    }
    
    public Yes getPurDesignatedBankFlag() {
        return purDesignatedBankFlag;
    }

    public void setPurDesignatedBankFlag(Yes pPurDesignatedBankFlag) {
        purDesignatedBankFlag = pPurDesignatedBankFlag;
    }

    public Yes getSupDesignatedBankFlag() {
        return supDesignatedBankFlag;
    }

    public void setSupDesignatedBankFlag(Yes pSupDesignatedBankFlag) {
        supDesignatedBankFlag = pSupDesignatedBankFlag;
    }
    
    public Timestamp getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Timestamp pStatusUpdateTime) {
        statusUpdateTime = pStatusUpdateTime;
    }
    
    public Long getCostBearerBillId() {
        return costBearerBillId;
    }

    public void setCostBearerBillId(Long pCostBearerBillId) {
        costBearerBillId = pCostBearerBillId;
    }

    public Long getFinancierBillId() {
        return financierBillId;
    }

    public void setFinancierBillId(Long pFinancierBillId) {
        financierBillId = pFinancierBillId;
    }
    
    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }
    

    public Long getOldInstId() {
        return oldInstId;
    }

    public void setOldInstId(Long pOldInstId) {
        oldInstId = pOldInstId;
    }
    public String getEntityGstSummary() {
    	if(entityGstSummaryList != null && !entityGstSummaryList.isEmpty()){
    		return gstSummaryBeanMeta.formatListAsJson(entityGstSummaryList, null, null, false, false);
    	}
    	return null;
    }

    public void setEntityGstSummary(String pEntityGstSummary) {
    	entityGstSummaryList  = null;
    	if(StringUtils.isNotEmpty(pEntityGstSummary)){
    		JsonSlurper lJsonSlurper = new JsonSlurper();
            List<Map<String, Object>> lListOfMap = (List<Map<String, Object>>)lJsonSlurper.parseText(pEntityGstSummary);
            if(lListOfMap!=null && !lListOfMap.isEmpty()){
            	entityGstSummaryList =new ArrayList<GstSummaryBean>();
            	GstSummaryBean lGstSummaryBean = null;
                for(Map<String, Object> lMap : lListOfMap){
                	lGstSummaryBean = new GstSummaryBean();
                	gstSummaryBeanMeta.validateAndParse(lGstSummaryBean, lMap, null);
                	entityGstSummaryList.add(lGstSummaryBean);
                }
            }
    	}
    }
    public List<GstSummaryBean> getEntityGstSummaryList() {
        return entityGstSummaryList;
    }
    public void setEntityGstSummaryList(List<GstSummaryBean> pEntityGstSummaryList) {
        entityGstSummaryList = pEntityGstSummaryList;
    }
    public GstSummaryBean getTotalGstSummary(String pEntityCode){
    	GstSummaryBean lTotalCharge = new GstSummaryBean();
    	lTotalCharge.initialize();
    	lTotalCharge.add(getGstSummary(pEntityCode, ChargeType.Normal,null));
    	lTotalCharge.add(getGstSummary(pEntityCode, ChargeType.Split,null));
    	return lTotalCharge;
    }
    public GstSummaryBean getGstSummary(String pEntityCode){
    	return getGstSummary(pEntityCode, ChargeType.Normal,null);
    }
    public GstSummaryBean getGstSummary(String pEntityCode, AppConstants.ChargeType pChargeType,Long pObId){
        if(entityGstSummaryList!=null && !entityGstSummaryList.isEmpty()){
        	for (GstSummaryBean lGstSummaryBean : entityGstSummaryList) {
        		if (pObId!=null) {
        			if(lGstSummaryBean.getEntity().equals(pEntityCode) && 
            				pChargeType.equals(lGstSummaryBean.getChargeType()) && 
            					pObId.equals(lGstSummaryBean.getObId())){
            			return lGstSummaryBean;
            		}
        		}else {
        			if(lGstSummaryBean.getEntity().equals(pEntityCode) && 
            				pChargeType.equals(lGstSummaryBean.getChargeType()) ){
            			return lGstSummaryBean;
            		}
        		}
			}        	
        }
        return null;
    }
    public GstSummaryBean addGstSummaryBean(String pEntityCode){
    	return addGstSummaryBean(pEntityCode, ChargeType.Normal);
    }
    public GstSummaryBean addGstSummaryBean(String pEntityCode, ChargeType pChargeType){
    	GstSummaryBean lChargBearerGst = new GstSummaryBean();
		lChargBearerGst.initialize();
		lChargBearerGst.setEntity(pEntityCode);
		lChargBearerGst.setChargeType(pChargeType);
		if( entityGstSummaryList == null ){
			entityGstSummaryList = new ArrayList<GstSummaryBean>();
		}
		entityGstSummaryList.add(lChargBearerGst);
    	return lChargBearerGst;
    }
    public BigDecimal getTransactionCharge(String pEntity){
    	GstSummaryBean lGstSummaryBean = getGstSummary(pEntity);
    	if(lGstSummaryBean != null){
    		return lGstSummaryBean.getCharge();
    	}
    	return BigDecimal.ZERO;
    }
    public String[] getKeysForBilling(ObligationBean.Type pType){
    	ArrayList<String> lKeys = new ArrayList<String>();
		String lTmpKey = null;
		GstSummaryBean lGstSummaryBean = getGstSummary(getChargeBearerEntityCode());
		if(Type.Leg_1.equals(pType)) {
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
				lTmpKey = getChargeBearerEntityCode() + CommonConstants.KEY_SEPARATOR + (getChargeBearerBillLocationId()!=null?getChargeBearerBillLocationId():"");
				lKeys.add(lTmpKey);
			}else{
				lGstSummaryBean = getGstSummary(getChargeBearerEntityCode(),ChargeType.Split,null);
				if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
					lTmpKey = getChargeBearerEntityCode() + CommonConstants.KEY_SEPARATOR + (getChargeBearerBillLocationId()!=null?getChargeBearerBillLocationId():"");
					lKeys.add(lTmpKey);
				}
			}
			lGstSummaryBean = getGstSummary(getFinancier());
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
				lTmpKey = getFinancier() + CommonConstants.KEY_SEPARATOR + (getFinancierBillLoc()!=null?getFinancierBillLoc():"");
				lKeys.add(lTmpKey);
			}else{
				lGstSummaryBean = getGstSummary(getFinancier(),ChargeType.Split,null);
				if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
					lTmpKey = getFinancier() + CommonConstants.KEY_SEPARATOR + (getFinancierBillLoc()!=null?getFinancierBillLoc():"");
					lKeys.add(lTmpKey);
				}
			}
		}else if (Type.Leg_2.equals(pType)) {
			lGstSummaryBean = getGstSummary(getPurchaser(),ChargeType.Extension,null);
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
				lTmpKey = getPurchaser() + CommonConstants.KEY_SEPARATOR + (purchaserBillLoc!=null?purchaserBillLoc:purchaserSettleLoc);
				lKeys.add(lTmpKey);
			}
		}
    	String[] lRetVal = new String[lKeys.size()];
    	int lPtr =0;
    	for(String lKey : lKeys){
    		lRetVal[lPtr++] = lKey;
    	}
    	return lRetVal;
    }
    public String getEntityCode(String pBillingKey){
    	if(StringUtils.isNotEmpty(pBillingKey)){
    		String[] lTmpList = CommonUtilities.splitString(pBillingKey,CommonConstants.KEY_SEPARATOR );
    		if(lTmpList.length > 0){
    			return lTmpList[0];
    		}
    	}
    	return "";
    }
    public String[] getEntitiesCharged(){
    	ArrayList<String> lKeys = new ArrayList<String>();
		GstSummaryBean lGstSummaryBean = getGstSummary(getChargeBearerEntityCode());
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
			lKeys.add(getChargeBearerEntityCode());
		}
		lGstSummaryBean = getGstSummary(getFinancier());
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
			lKeys.add(getFinancier());
		}
		lGstSummaryBean = getGstSummary(getChargeBearerEntityCode(),ChargeType.Split,null);
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
			lKeys.add(getChargeBearerEntityCode());
		}
		lGstSummaryBean = getGstSummary(getFinancier(),ChargeType.Split,null);
			if(lGstSummaryBean!=null && lGstSummaryBean.getCharge()!=null && lGstSummaryBean.getCharge().compareTo(BigDecimal.ZERO) > 0){
			lKeys.add(getFinancier());
		}
    	String[] lRetVal = new String[lKeys.size()];
    	int lPtr =0;
    	for(String lKey : lKeys){
    		lRetVal[lPtr++] = lKey;
    	}
    	return lRetVal;
    }
    public Long getFinancierSettleLoc(){
    	return financierSettleLoc;
    }
    public void setFinancierSettleLoc(Long pFinancierSettleLoc){
    	financierSettleLoc = pFinancierSettleLoc;
    }
    
    public Long getFinancierBillLoc(){
    	return financierBillLoc;
    }
    public void setFinancierBillLoc(Long pFinancierBillLoc){
    	financierBillLoc = pFinancierBillLoc;
    }
    
    public BigDecimal getFinancierCharge() {
      return getTransactionCharge(financier);
    }

    public void setFinancierCharge(BigDecimal pFinancierCharge) {

    }

    public BigDecimal getChargeBearerCharge() {
    	return getTransactionCharge(getChargeBearerEntityCode());
    }

    public void setChargeBearerCharge(BigDecimal pChargeBearerCharge) {
     
    }
    
    public Date getFromAcceptanceDate() {
        return fromAcceptanceDate;
    }

    public void setFromAcceptanceDate(Date pFromAcceptanceDate) {
        fromAcceptanceDate = pFromAcceptanceDate;
    }

    public Date getToAcceptanceDate() {
        return toAcceptanceDate;
    }

    public void setToAcceptanceDate(Date pToAcceptanceDate) {
        toAcceptanceDate = pToAcceptanceDate;
    }

    public Date getFromFactorStartDate() {
        return fromFactorStartDate;
    }

    public void setFromFactorStartDate(Date pFromFactorStartDate) {
        fromFactorStartDate = pFromFactorStartDate;
    }

    public Date getToFactorEndDate() {
        return toFactorEndDate;
    }

    public void setToFactorEndDate(Date pToFactorEndDate) {
        toFactorEndDate = pToFactorEndDate;
    }
    
    public Yes getFetchActiveBids() {
        return fetchActiveBids;
    }

    public void setFetchActiveBids(Yes pFetchActiveBids) {
        fetchActiveBids = pFetchActiveBids;
    }

	public Object getExtBillId1() {
		return extBillId1;
	}
	public Object getExtBillId2() {
		return extBillId2;
	}

	public void setExtBillId1(Long pExtBillId1) {
		extBillId1 = pExtBillId1;
	}
	public void setExtBillId2(Long pExtBillId2) {
		extBillId2 = pExtBillId2;
	}
}