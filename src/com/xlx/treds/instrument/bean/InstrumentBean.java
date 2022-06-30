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

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.DataType;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppInitializer;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.AutoAcceptableBidTypes;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.SplittingPoint;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class InstrumentBean implements ILegInterest {
	private static final Logger logger = LoggerFactory.getLogger(InstrumentBean.class);

	public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
	public static final String FIELDGROUP_UPDATESTATUSONSUBMIT = "updateStatusOnSubmit";
    public static final String FIELDGROUP_UPDATECOUNTERSTATUS = "updateCounterStatus";
    public static final String FIELDGROUP_UPDATECHECKERSTATUS = "updateCheckerStatus";
    public static final String FIELDGROUP_UPDATECONVFACTUNIT = "updateConvFactUnit";
    public static final String FIELDGROUP_DUEDATEREQUEST = "duedaterequest";
    public static final String FIELDGROUP_DUEDATERESPONSE = "duedateresponse";
    public static final String FIELDGROUP_UPDATEMONETAGOCANCEL = "updateMonetagoCancel";
    public static final String FIELDGROUP_UPDATEEXPIREINSTRUMENT= "expireInstrument";
    public static final String FIELDGROUP_UPDATEGROUPFIELDSCHILD = "updateGroupFieldsChild";
    public static final String FIELDGROUP_UPDATEGROUPFIELDSPARENT = "updateGroupFieldsParent";
    public static final String FIELDGROUP_UPDATEMONETAGOGROUP = "updateMonetagoGroup";
    public static final String FIELDGROUP_APIRESPONSE = "apiResponse";
    public static final String FIELDGROUP_UPDATESAVE = "updateSave";
    public static final String FIELDGROUP_UPDATEEWAYINST = "updateEwayInv";
    public static final String FIELDGROUP_EINVOICEFIELDS = "eInvoiceFields";
    public static final String FIELDGROUP_CHECKERLEVEL = "checkerlevel";
    public static final String FIELDGROUP_COUNTERLEVEL = "counterlevel";
    public static final String FIELDGROUP_PURCHASER_BULK = "purchaserBulk";
    public static final String FIELDGROUP_SUPPLIER_BULK = "supplierBulk";
    public static final String FIELDGROUP_BHELPEMINST = "bhelPemInst";
    public static final String FIELDGROUP_BHELCOUAPP = "bhelCouApp";
    public static final String FIELDGROUP_COUNTERMAKERUPDATE = "counterMakerUpdate";
    public static final String FIELDGROUP_COUNTERMAKERUPDATEFIELDS = "counterMakerUpdateFields";
    
    public static final String FIELD_ZIPFILENAME = "instImgsZipFile";

    
    public enum Type implements IKeyValEnumInterface<String>{
        Invoice("INV","Invoice");
        
        private final String code;
        private final String desc;
        private Type(String pCode, String pDesc) {
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
    public enum DelCat implements IKeyValEnumInterface<String>{
        Material("MAT","Material"),Service("SER","Service");
        
        private final String code;
        private final String desc;
        private DelCat(String pCode, String pDesc) {
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
    public enum Status implements IKeyValEnumInterface<String>{
        Drafting("DRFT","Drafting"),Submitted("SUB","Checker Pending"),Checker_Approved("CHKAPP","Checker Approved"),Checker_Returned("CHKRET","Checker Returned"),Checker_Rejected("CHKREJ","Checker Rejected"),Counter_Approved("COUAPP","Counter Approved"),Counter_Returned("COURET","Counter Returned"),Counter_Rejected("COUREJ","Counter Rejected"),Converted_To_Factoring_Unit("FACUNT","In Auction"),Withdrawn("WTHDRN","Withdrawn"),Expired("EXP","Expired"),Leg_3_Generated("LEG3","Leg 3 Generated"),Factored("FACT","Factored"),Leg_1_Settled("L1SET","Leg 1 Settled"),Leg_1_Failed("L1FAIL","Leg 1 Failed"),Leg_2_Settled("L2SET","Leg 2 Settled"),Leg_2_Failed("L2FAIL","Leg 2 Failed"),Counter_Checker_Pending("COUCHKPEN","Counter Checker Pending"),Counter_Checker_Return("COUCHKRET","Counter Checker Return"),Leg_3_Settled("L3SET","Leg 3 Settled"),Leg_3_Failed("L3FAIL","Leg 3 Failed");
        
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
    public enum SupplyType implements IKeyValEnumInterface<String>{
        Inward("I","Inward"),Outward("O","Outward");
        
        private final String code;
        private final String desc;
        private SupplyType(String pCode, String pDesc) {
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
    public enum DocType implements IKeyValEnumInterface<String>{
        Invoice("INV","Invoice"),Bill("BIL","Bill"),Bill_of_Entry("BOE","Bill of Entry"),Challan("CHL","Challan"),Credit_Note("CNT","Credit Note"),_Others("OTH"," Others");
        
        private final String code;
        private final String desc;
        private DocType(String pCode, String pDesc) {
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
    public enum TransMode implements IKeyValEnumInterface<Long>{
        Road(Long.valueOf(1),"Road"),Rail(Long.valueOf(2),"Rail"),Air(Long.valueOf(3),"Air"),Ship(Long.valueOf(4),"Ship");
        
        private final Long code;
        private final String desc;
        private TransMode(Long pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public Long getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }

    private Long id;
    private Type type;
    private String supplier;
    private String supplierRef;
    private Long supClId;
    private String supLocation;
    private String supGstState;
    private String supGstn;
    private Long supSettleClId;
    private Long supBillClId;
    private String supMsmeStatus;
    private String purchaser;
    private String purchaserRef;
    private Long purClId;
    private String purLocation;
    private String purGstState;
    private String purGstn;
    private Long purSettleClId;
    private Long purBillClId;
    private Date poDate;
    private String poNumber;
    private String counterRefNum;
    private Date goodsAcceptDate;
    private DelCat delCat;
    private String description;
    private String instNumber;
    private Date instDate;
    private Date instDueDate;
    private Date statDueDate;
    private Date maturityDate;
    private Timestamp factorMaxEndDateTime;
    private String currency;
    private BigDecimal amount;
    private BigDecimal haircutPercent;
    private BigDecimal adjAmount;
    private BigDecimal cashDiscountPercent;
    private BigDecimal cashDiscountValue;
    private BigDecimal tdsAmount;
    private BigDecimal netAmount;
    private String instImage;
    private String creditNoteImage;
    private String sup1;
    private String sup2;
    private String sup3;
    private String sup4;
    private String sup5;
    private Long creditPeriod;
    private Yes enableExtension;
    private Long extendedCreditPeriod;
    private Date extendedDueDate;
    private AutoAcceptBid autoAccept;
    private AutoAcceptableBidTypes autoAcceptableBidTypes;
    private AutoConvert autoConvert;
    private CostBearer period1CostBearer;
    private BigDecimal period1CostPercent;
    private CostBearer period2CostBearer;
    private BigDecimal period2CostPercent;
    private CostBearer period3CostBearer;
    private BigDecimal period3CostPercent;
    private CostBearingType costBearingType;
    private SplittingPoint splittingPoint;
    private CostBearer preSplittingCostBearer;
    private CostBearer postSplittingCostBearer;
    private BigDecimal buyerPercent;
    private BigDecimal sellerPercent;
    private CostBearer bidAcceptingEntityType;
    private CostBearingType chargeBearer;
    private CostBearer period1ChargeBearer;
    private BigDecimal period1ChargePercent;
    private CostBearer period2ChargeBearer;
    private BigDecimal period2ChargePercent;
    private CostBearer period3ChargeBearer;
    private BigDecimal period3ChargePercent;
    private SplittingPoint splittingPointCharge;
    private CostBearer preSplittingCharge;
    private CostBearer postSplittingCharge;
    private BigDecimal buyerPercentCharge;
    private BigDecimal sellerPercentCharge;
    private BigDecimal purchaserLeg1Interest;
    private BigDecimal supplierLeg1Interest;
    private BigDecimal purchaserLeg2Interest;
    private BigDecimal leg2ExtensionInterest;
    private YesNo settleLeg3Flag;
    private Long fileId;
    private String monetagoLedgerId;
    private String monetagoFactorTxnId;
    private String monetagoCancelTxnId;
    private Status status;
    private String statusRemarks;
    private Timestamp statusUpdateTime;
    private String makerEntity;
    private Long makerAuId;
    private String makerLoginId;
    private String makerName;
    private Long checkerAuId;
    private String checkerLoginId;
    private String checkerName;
    private String counterEntity;
    private Long counterAuId;
    private String counterLoginId;
    private String counterName;
    private Long counterCheckerAuId;
    private String counterCheckerLoginId;
    private String counterCheckerName;
    private String ownerEntity;
    private Long ownerAuId;
    private String ownerLoginId;
    private String ownerName;
    private Long fuId;
    private String salesCategory;
    private String ewayBillNo;
    private SupplyType supplyType;
    private DocType docType;
    private String docNo;
    private Date docDate;
    private String fromPincode;
    private String toPincode;
    private TransMode transMode;
    private String transporterName;
    private String transporterId;
    private String transDocNo;
    private Date transDocDate;
    private String vehicleNo;
    private Yes acceptAgreement;
    private Yes groupFlag;
    private Long groupInId;
    private String groupRefNo;
    private Long cersaiFileId;
    private Long ebdId;
    private Long mkrChkLevel;
    private Long cntChkLevel;
    private String otherSettings;
    private Long recordVersion;
    private Long tab;
    private String counterModifiedFields;
    private String settlePurLocation;
    private String settlePurGstState;
    private String settlePurGstn;
    private YesNo filtHistFlag;
    private String aggregatorEntity;
    private Long aggregatorAuId;
    private Long cfId;
    private String cfData;
	//nondatabase
    private List<InstrumentBean> groupedInstruments;
    private Long instCount = null;
    private Long groupId;
    private String group;
    private Long count;
    private Timestamp recordCreateTime;
    private Date toDate;
    private Date fromDate;
    private List<String> instrumentCreationKeysList;
    private boolean isAllowedToRecalculateFromPercent = true;
    private Yes instVisibleToMaker;
    //
    private Date fromFilterGoodsAcceptDate;
    private Date toFilterGoodsAcceptDate;
    private Date fromFilterInstDate;
    private Date toFilterInstDate;
    private Date fromFilterInstDueDate;
    private Date toFilterInstDueDate;
    private String counterUpdateFields;
    //
    private boolean resetCostChargeBearer;
    //
	public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
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

    public Long getSupClId() {
        return supClId;
    }

    public void setSupClId(Long pSupClId) {
        supClId = pSupClId;
    }

    public String getSupLocation() {
        return supLocation;
    }

    public void setSupLocation(String pSupLocation) {
        supLocation = pSupLocation;
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
    
    public Long getSupSettleClId() {
        return supSettleClId;
    }

    public void setSupSettleClId(Long pSupSettleClId) {
        supSettleClId = pSupSettleClId;
    }
    
    public Long getSupBillClId() {
        return supBillClId;
    }

    public void setSupBillClId(Long pSupBillClId) {
        supBillClId = pSupBillClId;
    }

    public String getSupMsmeStatus() {
        return supMsmeStatus;
    }

    public void setSupMsmeStatus(String pSupMsmeStatus) {
        supMsmeStatus = pSupMsmeStatus;
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

    public Long getPurClId() {
        return purClId;
    }

    public void setPurClId(Long pPurClId) {
        purClId = pPurClId;
    }

    public String getPurLocation() {
        return purLocation;
    }

    public void setPurLocation(String pPurLocation) {
        purLocation = pPurLocation;
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
    
    public Long getPurSettleClId() {
        return purSettleClId;
    }

    public void setPurSettleClId(Long pPurSettleClId) {
        purSettleClId = pPurSettleClId;
    }
    
    public Long getPurBillClId() {
        return purBillClId;
    }

    public void setPurBillClId(Long pPurBillClId) {
    	purBillClId = pPurBillClId;
    }

    public Date getPoDate() {
        return poDate;
    }

    public void setPoDate(Date pPoDate) {
        poDate = pPoDate;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String pPoNumber) {
        poNumber = pPoNumber;
    }

    public String getCounterRefNum() {
        return counterRefNum;
    }

    public void setCounterRefNum(String pCounterRefNum) {
        counterRefNum = pCounterRefNum;
    }

    public Date getGoodsAcceptDate() {
        return goodsAcceptDate;
    }

    public void setGoodsAcceptDate(Date pGoodsAcceptDate) {
        goodsAcceptDate = pGoodsAcceptDate;
    }

    public DelCat getDelCat() {
        return delCat;
    }

    public void setDelCat(DelCat pDelCat) {
        delCat = pDelCat;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public String getInstNumber() {
        return instNumber;
    }

    public void setInstNumber(String pInstNumber) {
        instNumber = pInstNumber;
    }

    public Date getInstDate() {
        return instDate;
    }

    public void setInstDate(Date pInstDate) {
        instDate = pInstDate;
    }

    public Date getInstDueDate() {
        return instDueDate;
    }

    public void setInstDueDate(Date pInstDueDate) {
        instDueDate = pInstDueDate;
    }

    public Date getStatDueDate() {
        return statDueDate;
    }

    public void setStatDueDate(Date pStatDueDate) {
        statDueDate = pStatDueDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date pMaturityDate) {
        maturityDate = pMaturityDate;
    }

    public Timestamp getFactorMaxEndDateTime() {
        return factorMaxEndDateTime;
    }

    public void setFactorMaxEndDateTime(Timestamp pFactorMaxEndDateTime) {
        factorMaxEndDateTime = pFactorMaxEndDateTime;
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

    public BigDecimal getHaircutPercent() {
        return haircutPercent;
    }

    public void setHaircutPercent(BigDecimal pHaircutPercent) {
        haircutPercent = pHaircutPercent;
    }

    public BigDecimal getAdjAmount() {
        return adjAmount;
    }

    public void setAdjAmount(BigDecimal pAdjAmount) {
        adjAmount = pAdjAmount;
    }

    public String getMonetagoLedgerId() {
        return monetagoLedgerId;
    }

    public void setMonetagoLedgerId(String pMonetagoLedgerId) {
        monetagoLedgerId = pMonetagoLedgerId;
    }

    public String getMonetagoFactorTxnId() {
        return monetagoFactorTxnId;
    }

    public void setMonetagoFactorTxnId(String pMonetagoFactorTxnId) {
        monetagoFactorTxnId = pMonetagoFactorTxnId;
    }

    public String getMonetagoCancelTxnId() {
        return monetagoCancelTxnId;
    }

    public void setMonetagoCancelTxnId(String pMonetagoCancelTxnId) {
        monetagoCancelTxnId = pMonetagoCancelTxnId;
    }

    public BigDecimal getCashDiscountPercent() {
        return cashDiscountPercent;
    }

    public void setCashDiscountPercent(BigDecimal pCashDiscountPercent) {
        cashDiscountPercent = pCashDiscountPercent;
    }

    public BigDecimal getCashDiscountValue() {
        return cashDiscountValue;
    }

    public void setCashDiscountValue(BigDecimal pCashDiscountValue) {
        cashDiscountValue = pCashDiscountValue;
    }

    public BigDecimal getTdsAmount() {
        return tdsAmount;
    }

    public void setTdsAmount(BigDecimal pTdsAmount) {
        tdsAmount = pTdsAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal pNetAmount) {
        netAmount = pNetAmount;
    }

    public String getInstImage() {
        return instImage;
    }

    public void setInstImage(String pInstImage) {
        instImage = pInstImage;
    }

    public String getCreditNoteImage() {
        return creditNoteImage;
    }

    public void setCreditNoteImage(String pCreditNoteImage) {
        creditNoteImage = pCreditNoteImage;
    }

    public String getSupportings() {
        if (StringUtils.isNotBlank(sup1) || StringUtils.isNotBlank(sup2) || StringUtils.isNotBlank(sup3) || StringUtils.isNotBlank(sup4) || StringUtils.isNotBlank(sup5)) {
            List<String> lSupportings = new ArrayList<String>();
            lSupportings.add(sup1);
            lSupportings.add(sup2);
            lSupportings.add(sup3);
            lSupportings.add(sup4);
            lSupportings.add(sup5);
            return new JsonBuilder(lSupportings).toString();
        } else 
            return null;
    }

    public void setSupportings(String pSupportings) {
        List<String> lSupportings = null;
        if (StringUtils.isNotBlank(pSupportings))
            lSupportings = (List<String>)(new JsonSlurper().parseText(pSupportings));
        sup1 = (lSupportings != null) && (lSupportings.size() > 0) ? lSupportings.get(0) : null;
        sup2 = (lSupportings != null) && (lSupportings.size() > 1) ? lSupportings.get(1) : null;
        sup3 = (lSupportings != null) && (lSupportings.size() > 2) ? lSupportings.get(2) : null;
        sup4 = (lSupportings != null) && (lSupportings.size() > 3) ? lSupportings.get(3) : null;
        sup5 = (lSupportings != null) && (lSupportings.size() > 4) ? lSupportings.get(4) : null;
    }

    public String getSup1() {
        return sup1;
    }

    public void setSup1(String pSup1) {
        sup1 = pSup1;
    }

    public String getSup2() {
        return sup2;
    }

    public void setSup2(String pSup2) {
        sup2 = pSup2;
    }

    public String getSup3() {
        return sup3;
    }

    public void setSup3(String pSup3) {
        sup3 = pSup3;
    }

    public String getSup4() {
        return sup4;
    }

    public void setSup4(String pSup4) {
        sup4 = pSup4;
    }

    public String getSup5() {
        return sup5;
    }

    public void setSup5(String pSup5) {
        sup5 = pSup5;
    }

    public Long getCreditPeriod() {
        return creditPeriod;
    }

    public void setCreditPeriod(Long pCreditPeriod) {
        creditPeriod = pCreditPeriod;
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

    public CostBearingType getCostBearingType() {
        return costBearingType;
    }

    public void setCostBearingType(CostBearingType pCostBearingType) {
        costBearingType = pCostBearingType;
    }

    public SplittingPoint getSplittingPoint() {
        return splittingPoint;
    }

    public void setSplittingPoint(SplittingPoint pSplittingPoint) {
        splittingPoint = pSplittingPoint;
    }

    public CostBearer getPreSplittingCostBearer() {
        return preSplittingCostBearer;
    }

    public void setPreSplittingCostBearer(CostBearer pPreSplittingCostBearer) {
        preSplittingCostBearer = pPreSplittingCostBearer;
    }

    public CostBearer getPostSplittingCostBearer() {
        return postSplittingCostBearer;
    }

    public void setPostSplittingCostBearer(CostBearer pPostSplittingCostBearer) {
        postSplittingCostBearer = pPostSplittingCostBearer;
    }

    public BigDecimal getBuyerPercent() {
        return buyerPercent;
    }

    public void setBuyerPercent(BigDecimal pBuyerPercent) {
        buyerPercent = pBuyerPercent;
    }

    public BigDecimal getSellerPercent() {
        return sellerPercent;
    }

    public void setSellerPercent(BigDecimal pSellerPercent) {
        sellerPercent = pSellerPercent;
    }


    public CostBearer getBidAcceptingEntityType() {
    	return bidAcceptingEntityType;
    }

    public void setBidAcceptingEntityType(CostBearer pBidAcceptingEntityType) {
    	bidAcceptingEntityType = pBidAcceptingEntityType;
    }

    public CostBearingType getChargeBearer() {
        return chargeBearer;
    }

    public void setChargeBearer(CostBearingType pChargeBearer) {
        chargeBearer = pChargeBearer;
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

    public SplittingPoint getSplittingPointCharge() {
        return splittingPointCharge;
    }

    public void setSplittingPointCharge(SplittingPoint pSplittingPointCharge) {
        splittingPointCharge = pSplittingPointCharge;
    }

    public CostBearer getPreSplittingCharge() {
        return preSplittingCharge;
    }

    public void setPreSplittingCharge(CostBearer pPreSplittingCharge) {
        preSplittingCharge = pPreSplittingCharge;
    }

    public CostBearer getPostSplittingCharge() {
        return postSplittingCharge;
    }

    public void setPostSplittingCharge(CostBearer pPostSplittingCharge) {
        postSplittingCharge = pPostSplittingCharge;
    }

    public BigDecimal getBuyerPercentCharge() {
        return buyerPercentCharge;
    }

    public void setBuyerPercentCharge(BigDecimal pBuyerPercentCharge) {
        buyerPercentCharge = pBuyerPercentCharge;
    }

    public BigDecimal getSellerPercentCharge() {
        return sellerPercentCharge;
    }

    public void setSellerPercentCharge(BigDecimal pSellerPercentCharge) {
        sellerPercentCharge = pSellerPercentCharge;
    }


    public YesNo getSettleLeg3Flag() {
        return settleLeg3Flag;
    }

    public void setSettleLeg3Flag(YesNo pSettleLeg3Flag) {
        settleLeg3Flag = pSettleLeg3Flag;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long pFileId) {
        fileId = pFileId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public String getStatusDesc() {
    	if(status != null){
    		return status.toString();
    	}
        return "";
    }

    public void setStatusDesc(Status pStatus) {
    }

    public String getStatusRemarks() {
        return statusRemarks;
    }

    public void setStatusRemarks(String pStatusRemarks) {
        statusRemarks = pStatusRemarks;
    }

    public Timestamp getStatusUpdateTime() {
        return statusUpdateTime;
    }

    public void setStatusUpdateTime(Timestamp pStatusUpdateTime) {
        statusUpdateTime = pStatusUpdateTime;
    }

    public String getMakerEntity() {
        return makerEntity;
    }

    public void setMakerEntity(String pMakerEntity) {
        makerEntity = pMakerEntity;
    }

    public Long getMakerAuId() {
        return makerAuId;
    }

    public void setMakerAuId(Long pMakerAuId) {
        makerAuId = pMakerAuId;
        makerLoginId = null;
        makerName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{makerAuId});
            if (lAppUserBean != null) {
                makerLoginId = lAppUserBean.getLoginId();
                makerName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getMakerLoginId() {
        return makerLoginId;
    }

    public void setMakerLoginId(String pMakerLoginId) {
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String pMakerName) {
    }

    public Long getCheckerAuId() {
        return checkerAuId;
    }

    public void setCheckerAuId(Long pCheckerAuId) {
        checkerAuId = pCheckerAuId;
        checkerLoginId = null;
        checkerName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{checkerAuId});
            if (lAppUserBean != null) {
                checkerLoginId = lAppUserBean.getLoginId();
                checkerName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getCheckerLoginId() {
        return checkerLoginId;
    }

    public void setCheckerLoginId(String pCheckerLoginId) {
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String pCheckerName) {
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
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String pCounterName) {
    }
    
    public Long getCounterCheckerAuId() {
        return counterCheckerAuId;
    }

    public void setCounterCheckerAuId(Long pCounterCheckerAuId) {
        counterCheckerAuId = pCounterCheckerAuId;
        counterCheckerLoginId = null;
        counterCheckerName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{counterCheckerAuId});
            if (lAppUserBean != null) {
                counterCheckerLoginId = lAppUserBean.getLoginId();
                counterCheckerName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getCounterCheckerLoginId() {
        return counterCheckerLoginId;
    }

    public void setCounterCheckerLoginId(String pCounterCheckerLoginId) {
    }

    public String getCounterCheckerName() {
        return counterCheckerName;
    }

    public void setCounterCheckerName(String pCounterCheckerName) {
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
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String pOwnerName) {
    }

    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public String getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(String pSalesCategory) {
        salesCategory = pSalesCategory;
    }
    public String getEwayBillNo() {
        return ewayBillNo;
    }

    public void setEwayBillNo(String pEwayBillNo) {
        ewayBillNo = pEwayBillNo;
    }

    public SupplyType getSupplyType() {
        return supplyType;
    }

    public void setSupplyType(SupplyType pSupplyType) {
        supplyType = pSupplyType;
    }

    public DocType getDocType() {
        return docType;
    }

    public void setDocType(DocType pDocType) {
        docType = pDocType;
    }

    public String getDocNo() {
        return docNo;
    }

    public void setDocNo(String pDocNo) {
        docNo = pDocNo;
    }
    
    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date pDocDate) {
        docDate = pDocDate;
    }

    public String getFromPincode() {
        return fromPincode;
    }

    public void setFromPincode(String pFromPincode) {
        fromPincode = pFromPincode;
    }

    public String getToPincode() {
        return toPincode;
    }

    public void setToPincode(String pToPincode) {
        toPincode = pToPincode;
    }

    public TransMode getTransMode() {
        return transMode;
    }

    public void setTransMode(TransMode pTransMode) {
        transMode = pTransMode;
    }

    public String getTransporterName() {
        return transporterName;
    }

    public void setTransporterName(String pTransporterName) {
        transporterName = pTransporterName;
    }

    public String getTransporterId() {
        return transporterId;
    }

    public void setTransporterId(String pTransporterId) {
        transporterId = pTransporterId;
    }

    public String getTransDocNo() {
        return transDocNo;
    }

    public void setTransDocNo(String pTransDocNo) {
        transDocNo = pTransDocNo;
    }

    public Date getTransDocDate() {
        return transDocDate;
    }

    public void setTransDocDate(Date pTransDocDate) {
        transDocDate = pTransDocDate;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String pVehicleNo) {
        vehicleNo = pVehicleNo;
    }

    public Yes getAcceptAgreement() {
        return acceptAgreement;
    }

    public void setAcceptAgreement(Yes pAcceptAgreement) {
        acceptAgreement = pAcceptAgreement;
    }
    
    public Yes getGroupFlag() {
        return groupFlag;
    }

    public void setGroupFlag(Yes pGroupFlag) {
        groupFlag = pGroupFlag;
    }

    public Long getGroupInId() {
        return groupInId;
    }

    public void setGroupInId(Long pGroupInId) {
        groupInId = pGroupInId;
    }

    public String getGroupRefNo() {
        return groupRefNo;
    }

    public void setGroupRefNo(String pGroupRefNo) {
        groupRefNo = pGroupRefNo;
    }

    public Long getCersaiFileId() {
        return cersaiFileId;
    }

    public void setCersaiFileId(Long pCersaiFileId) {
        cersaiFileId = pCersaiFileId;
    }
    
    public Long getEbdId() {
        return ebdId;
    }

    public void setEbdId(Long pEbdId) {
        ebdId = pEbdId;
    }
    
    public Long getMkrChkLevel() {
        return mkrChkLevel;
    }

    public void setMkrChkLevel(Long pMkrChkLevel) {
        mkrChkLevel = pMkrChkLevel;
    }

    public Long getCntChkLevel() {
        return cntChkLevel;
    }
    
    public void setCntChkLevel(Long pCntChkLevel) {
    	cntChkLevel = pCntChkLevel;
    }
    
    public String getOtherSettings() {
        return otherSettings;
    }

    public void setOtherSettings(String pOtherSettings) {
        otherSettings = pOtherSettings;
    }
    
    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

    public Long getTab() {
        return tab;
    }

    public void setTab(Long pTab) {
        tab = pTab;
    }

    public Long getAge() {
        if (statusUpdateTime == null) return Long.valueOf(0);
        long lDay1 = (statusUpdateTime.getTime() + OtherResourceCache.UTC_OFFSET) / AppConstants.DAY_IN_MILLIS;
        long lDay2 = (System.currentTimeMillis() + OtherResourceCache.UTC_OFFSET) / AppConstants.DAY_IN_MILLIS;
        return Long.valueOf(lDay2 - lDay1);
    }

    public void setAge(Long pAge) {
    }

    public String getCounterModifiedFields() {
        return counterModifiedFields;
    }

    public void setCounterModifiedFields(String pCounterModifiedFields) {
        counterModifiedFields = pCounterModifiedFields;
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
    public Long getTenure() {
		return TredsHelper.getInstance().getTenure(getLeg2MaturityExtendedDate());
    }

    public void setTenure(Long pTenure) {
    }

    public void populateNonDatabaseFields(){
    	costBearingType = null;
    	buyerPercent = null;
    	sellerPercent = null;
    	splittingPoint = null;
    	preSplittingCostBearer = null;
    	postSplittingCostBearer = null;
    	bidAcceptingEntityType = null;
    	//
    	//
    	chargeBearer = null;
    	buyerPercentCharge = null;
    	sellerPercentCharge = null;
    	splittingPointCharge = null;
    	preSplittingCharge = null;
    	postSplittingCharge = null;
    	//
    	
    	if(this.getPeriod1CostPercent()==null || this.getPeriod2CostPercent() == null || this.getPeriod3CostPercent() == null){
			logger.info("Cannot determine NonDbFields of PSLinkBean.");
    		return;
    	}
    	//
    	if(this.getPeriod1ChargePercent()==null || this.getPeriod2ChargePercent() == null || this.getPeriod3ChargePercent() == null){
			logger.info("Cannot determine NonDbFields of PSLinkBean.");
    		return;
    	}
    	//
    	if(CostBearer.Buyer.equals(this.getPeriod1CostBearer()) &&
    		(this.getPeriod1CostBearer().compareTo(this.getPeriod2CostBearer()) == 0) && 
			(this.getPeriod2CostBearer().compareTo(this.getPeriod3CostBearer()) == 0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent()) == 0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent()) == 0) ){
	
    		if(this.getPeriod1CostPercent().compareTo(BigDecimal.valueOf(100))==0){
    			costBearingType = CostBearingType.Buyer;
    			buyerPercent =  new BigDecimal(100);
    			sellerPercent = null;
    			bidAcceptingEntityType = CostBearer.Buyer;
			}else if (this.getPeriod1CostPercent().compareTo(new BigDecimal(100)) == -1){
				costBearingType = CostBearingType.Percentage_Split;
				buyerPercent = this.getPeriod1CostPercent();
				sellerPercent = (new BigDecimal(100)).subtract(this.getPeriod1CostPercent());
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if(CostBearer.Seller.equals(this.getPeriod1CostBearer()) &&
			(this.getPeriod1CostBearer().compareTo(this.getPeriod2CostBearer())==0) && 
			(this.getPeriod2CostBearer().compareTo(this.getPeriod3CostBearer())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0) ){
			if(this.getPeriod1CostPercent().compareTo(BigDecimal.valueOf(100))==0){
				costBearingType = CostBearingType.Seller;
    			buyerPercent =  null;
    			sellerPercent = new BigDecimal(100);
    			bidAcceptingEntityType = CostBearer.Seller;
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if((this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0)){
			
			if((this.getPeriod1CostBearer().compareTo(this.getPeriod2CostBearer())==0) &&
					this.getPeriod2CostBearer().compareTo(this.getPeriod3CostBearer())!=0){
    			costBearingType = CostBearingType.Periodical_Split;
				splittingPoint = SplittingPoint.Invoice_Due_Date;
				preSplittingCostBearer = this.getPeriod1CostBearer();
				postSplittingCostBearer = this.getPeriod3CostBearer();
			}else if((this.getPeriod1CostBearer().compareTo(this.getPeriod2CostBearer())!=0) &&
				(this.getPeriod2CostBearer().compareTo(this.getPeriod3CostBearer())==0)){
    			costBearingType = CostBearingType.Periodical_Split;
				splittingPoint = SplittingPoint.Statutory_Due_Date;
				preSplittingCostBearer = this.getPeriod1CostBearer();
				postSplittingCostBearer = this.getPeriod3CostBearer();
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}
    	
    	if(bidAcceptingEntityType == null){
    		if(this.getPurchaser().equals(this.getOwnerEntity())){
    			bidAcceptingEntityType = CostBearer.Buyer;
    		}else if(this.getSupplier().equals(this.getOwnerEntity())){
    			bidAcceptingEntityType = CostBearer.Seller;
    		}
    	}else{
    		if( (bidAcceptingEntityType.equals(CostBearer.Buyer) && !this.getPurchaser().equals(this.getOwnerEntity())) ||
    				(bidAcceptingEntityType.equals(CostBearer.Seller) && !this.getSupplier().equals(this.getOwnerEntity())) ){
				logger.info("Bid Accepting Entity mismatch.");
    		}
    	}
    	
    	//
    	if(CostBearer.Buyer.equals(this.getPeriod1ChargeBearer()) &&
    		this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) && 
			this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer()) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod2ChargePercent())==0) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod3ChargePercent())==0)){
	
    		if(this.getPeriod1ChargePercent().compareTo(BigDecimal.valueOf(100))==0){
    			chargeBearer = CostBearingType.Buyer;
    			buyerPercentCharge =  new BigDecimal(100);
    			sellerPercentCharge = null;
			}else if (this.getPeriod1ChargePercent().compareTo(new BigDecimal(100)) == -1){
				chargeBearer = CostBearingType.Percentage_Split;
				buyerPercentCharge = this.getPeriod1ChargePercent();
				sellerPercentCharge = (new BigDecimal(100)).subtract(this.getPeriod1ChargePercent());
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if(CostBearer.Seller.equals(this.getPeriod1ChargeBearer()) &&
			this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) && 
			this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer()) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod2ChargePercent())==0) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod3ChargePercent())==0)){
			if(this.getPeriod1ChargePercent().compareTo(BigDecimal.valueOf(100))==0){
				chargeBearer = CostBearingType.Seller;
				buyerPercentCharge =  null;
				sellerPercentCharge = new BigDecimal(100);
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if((this.getPeriod1ChargePercent().compareTo(this.getPeriod2ChargePercent())==0) &&
			(this.getPeriod1ChargePercent().compareTo(this.getPeriod3ChargePercent())==0)){
			
			if(this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) &&
					!this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer())){
    			chargeBearer = CostBearingType.Periodical_Split;
    			splittingPointCharge = SplittingPoint.Invoice_Due_Date;
				preSplittingCharge = this.getPeriod1ChargeBearer();
				postSplittingCharge = this.getPeriod3ChargeBearer();
			}else if(!this.getPeriod1ChargeBearer().equals(this.getPeriod2ChargeBearer()) &&
				this.getPeriod2ChargeBearer().equals(this.getPeriod3ChargeBearer())){
				chargeBearer = CostBearingType.Periodical_Split;
				splittingPointCharge = SplittingPoint.Statutory_Due_Date;
				preSplittingCharge = this.getPeriod1ChargeBearer();
				postSplittingCharge = this.getPeriod3ChargeBearer();
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}
    }

    public void populateDatabaseFields(){
    	CostBearingType lCostBearingType = this.getCostBearingType();
    	if(CostBearingType.Buyer.equals(lCostBearingType)){
			//p1cb=p2cb=p3cb=B;
			//p1%=pw%=p3%=100;
    		this.setPeriod1CostBearer(CostBearer.Buyer);
    		this.setPeriod2CostBearer(CostBearer.Buyer);
    		this.setPeriod3CostBearer(CostBearer.Buyer);
    		this.setPeriod1CostPercent(new BigDecimal(100));
    		this.setPeriod2CostPercent(new BigDecimal(100));
    		this.setPeriod3CostPercent(new BigDecimal(100));
    		this.setOwnerEntity(this.getPurchaser());
    	}else if(CostBearingType.Seller.equals(lCostBearingType)){
			//p1cb=p2cb=p3cb=S;
			//p1%=pw%=p3%=100
    		this.setPeriod1CostBearer(CostBearer.Seller);
    		this.setPeriod2CostBearer(CostBearer.Seller);
    		this.setPeriod3CostBearer(CostBearer.Seller);
    		this.setPeriod1CostPercent(new BigDecimal(100));
    		this.setPeriod2CostPercent(new BigDecimal(100));
    		this.setPeriod3CostPercent(new BigDecimal(100));
    		this.setOwnerEntity(this.getSupplier());
    	}else if(CostBearingType.Periodical_Split.equals(lCostBearingType)){
			//p1%=p2%=p3%=100;
			//p1cb=preSplitting;
			//p2cb=splittingpoint==statutoryduedate?postSplitting:presplitting;
			//p3cb=postSplitting
    		this.setPeriod1CostPercent(new BigDecimal(100));
    		this.setPeriod2CostPercent(new BigDecimal(100));
    		this.setPeriod3CostPercent(new BigDecimal(100));
    		this.setPeriod1CostBearer(this.getPreSplittingCostBearer());
    		if(SplittingPoint.Statutory_Due_Date.equals(this.getSplittingPoint())){
    			this.setPeriod2CostBearer(this.getPostSplittingCostBearer());
    		}else if(SplittingPoint.Invoice_Due_Date.equals(this.getSplittingPoint())){
    			this.setPeriod2CostBearer(this.getPreSplittingCostBearer());
    		}
    		this.setPeriod3CostBearer(this.getPostSplittingCostBearer());
    		if(CostBearer.Buyer.equals(this.getBidAcceptingEntityType()))
    			this.setOwnerEntity(this.getPurchaser());
    		else if(CostBearer.Seller.equals(this.getBidAcceptingEntityType()))
    			this.setOwnerEntity(this.getSupplier());
    	}else if(CostBearingType.Percentage_Split.equals(lCostBearingType)){    		
			//p1cb=p2cb=p3cb=B;
			//p1%=pw%=p3%=buyerPercent
    		this.setPeriod1CostBearer(CostBearer.Buyer);
    		this.setPeriod2CostBearer(CostBearer.Buyer);
    		this.setPeriod3CostBearer(CostBearer.Buyer);
    		this.setPeriod1CostPercent(this.getBuyerPercent());
    		this.setPeriod2CostPercent(this.getBuyerPercent());
    		this.setPeriod3CostPercent(this.getBuyerPercent());
    		if(CostBearer.Buyer.equals(this.getBidAcceptingEntityType()))
    			this.setOwnerEntity(this.getPurchaser());
    		else if(CostBearer.Seller.equals(this.getBidAcceptingEntityType()))
    			this.setOwnerEntity(this.getSupplier());
    	}
    	
    	CostBearingType lChargeBearer = this.getChargeBearer();
    	if(CostBearingType.Buyer.equals(lChargeBearer)){
			//p1cb=p2cb=p3cb=B;
			//p1%=pw%=p3%=100;
    		this.setPeriod1ChargeBearer(CostBearer.Buyer);
    		this.setPeriod2ChargeBearer(CostBearer.Buyer);
    		this.setPeriod3ChargeBearer(CostBearer.Buyer);
    		this.setPeriod1ChargePercent(new BigDecimal(100));
    		this.setPeriod2ChargePercent(new BigDecimal(100));
    		this.setPeriod3ChargePercent(new BigDecimal(100));
    	}else if(CostBearingType.Seller.equals(lChargeBearer)){
			//p1cb=p2cb=p3cb=S;
			//p1%=pw%=p3%=100
    		this.setPeriod1ChargeBearer(CostBearer.Seller);
    		this.setPeriod2ChargeBearer(CostBearer.Seller);
    		this.setPeriod3ChargeBearer(CostBearer.Seller);
    		this.setPeriod1ChargePercent(new BigDecimal(100));
    		this.setPeriod2ChargePercent(new BigDecimal(100));
    		this.setPeriod3ChargePercent(new BigDecimal(100));
    	}else if(CostBearingType.Periodical_Split.equals(lChargeBearer)){
			//p1%=p2%=p3%=100;
			//p1cb=preSplitting;
			//p2cb=splittingpoint==statutoryduedate?postSplitting:presplitting;
			//p3cb=postSplitting
    		this.setPeriod1ChargePercent(new BigDecimal(100));
    		this.setPeriod2ChargePercent(new BigDecimal(100));
    		this.setPeriod3ChargePercent(new BigDecimal(100));
    		this.setPeriod1ChargeBearer(this.getPreSplittingCharge());
    		if(SplittingPoint.Statutory_Due_Date.equals(this.getSplittingPointCharge())){
    			this.setPeriod2ChargeBearer(this.getPostSplittingCharge());
    		}else if(SplittingPoint.Invoice_Due_Date.equals(this.getSplittingPointCharge())){
    			this.setPeriod2ChargeBearer(this.getPreSplittingCharge());
    		}
    		this.setPeriod3ChargeBearer(this.getPostSplittingCharge());
    	}else if(CostBearingType.Percentage_Split.equals(lChargeBearer)){    		
			//p1cb=p2cb=p3cb=B;
			//p1%=pw%=p3%=buyerPercent
    		this.setPeriod1ChargeBearer(CostBearer.Buyer);
    		this.setPeriod2ChargeBearer(CostBearer.Buyer);
    		this.setPeriod3ChargeBearer(CostBearer.Buyer);
    		this.setPeriod1ChargePercent(this.getBuyerPercentCharge());
    		this.setPeriod2ChargePercent(this.getBuyerPercentCharge());
    		this.setPeriod3ChargePercent(this.getBuyerPercentCharge());
    		
    	}
    }
    
    public Long getChargeBearersCompanyId(){
    	if(CostBearer.Buyer.equals(chargeBearer))
    		return purClId;
    	else if(CostBearer.Seller.equals(chargeBearer))
    		return supClId;
    	return null;
    }
    
    public Date getLeg2MaturityExtendedDate() {
    	if(CommonAppConstants.Yes.Yes.equals(enableExtension) &&
    		extendedDueDate != null	){
    		return extendedDueDate;
    	}
        return maturityDate;
    }

 	public String getSettlePurLocation() {
 		return settlePurLocation;
 	}

 	public void setSettlePurLocation(String pSettlePurLocation) {
 		settlePurLocation = pSettlePurLocation;
 	}

 	public String getSettlePurGstState() {
 		return settlePurGstState;
 	}

 	public void setSettlePurGstState(String pSettlePurGstState) {
 		settlePurGstState = pSettlePurGstState;
 	}

 	public String getSettlePurGstn() {
 		return settlePurGstn;
 	}

 	public void setSettlePurGstn(String pSettlePurGstn) {
 		settlePurGstn = pSettlePurGstn;
 	}
 
    public YesNo getFiltHistFlag() {
		return filtHistFlag;
	}

	public void setFiltHistFlag(YesNo pFiltHistFlag) {
		filtHistFlag = pFiltHistFlag;
	}

	public List<InstrumentBean> getGroupedInstruments(){
 		return groupedInstruments;
 	}
 	public void setGroupedInstruments(List<InstrumentBean> pGroupedInstruments){
 		groupedInstruments = pGroupedInstruments;
 	}
 	
 	public InstrumentBean getFromGroupedInstruments(Long pInstrumentId){
 		InstrumentBean lBean = null;
 		if(groupedInstruments != null){
 			for(InstrumentBean lTmpBean : groupedInstruments){
 				if(lTmpBean.getId().equals(pInstrumentId)){
 					return lTmpBean;
 				}
 			}
 		}
 		return lBean;
 	}
 	
 	public Long getInstCount(){
 		if(CommonAppConstants.Yes.Yes.equals(groupFlag)){
 			if(groupedInstruments!=null){
 				return new Long(groupedInstruments.size());
 			}
 			return instCount;
 		}
 		return new Long(1);
 	}
 	
 	public void setInstCount(Long pInstCount){
 		instCount = pInstCount;
 	}
 	
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long pGroupId) {
        groupId = pGroupId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String pGroup) {
        group = pGroup;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long pCount) {
        count = pCount;
    }
    
    public Timestamp getRecordCreateTime() {
        return recordCreateTime;
    }

    public void setRecordCreateTime(Timestamp pRecordCreateTime) {
        recordCreateTime = pRecordCreateTime;
    }
    
    public String getPurPan() {
    	if(StringUtils.isNotEmpty(purchaser)){
    		return TredsHelper.getInstance().getCompanyPAN(purchaser);
    	}
        return "";
    }

    public void setPurPan(String pPurPan) {
    }

    public String getSupPan() {
    	if(StringUtils.isNotEmpty(supplier)){
    		return TredsHelper.getInstance().getCompanyPAN(supplier);
    	}
        return "";
    }

    public void setSupPan(String pSupPan) {
    }
    
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date pFromDate) {
        fromDate = pFromDate;
    }

    public Yes getIsAggregatorCreated() {
    	if (aggregatorAuId!=null && StringUtils.isNotEmpty(aggregatorEntity)) {
	                	return Yes.Yes;
    	}
        return null;
    }

    public void setIsAggregatorCreated(Yes pIsAggregatorCreated) {
    }
    
    public String getAggregatorEntity() {
        return aggregatorEntity;
    }

    public void setAggregatorEntity(String pAggregatorEntity) {
    	aggregatorEntity = pAggregatorEntity;
    }
    
    public String getAdapterEntity() {
 		if (this.getIsAggregatorCreated()!=null && this.getAggregatorEntity()!=null) {
 			return this.getAggregatorEntity();
 		}
 		return this.getPurchaser();

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
    
    public boolean getIsAllowedToRecalculateFromPercent() {
    	return isAllowedToRecalculateFromPercent;
    }

    public void setIsAllowedToRecalculateFromPercent(boolean pIsAllowedToRecalculateFromPercent) {
    	isAllowedToRecalculateFromPercent = pIsAllowedToRecalculateFromPercent;
    }
    
    public Long getAggregatorAuId() {
        return aggregatorAuId;
    }

    public void setAggregatorAuId(Long pAggregatorAuId) {
        aggregatorAuId = pAggregatorAuId;
    }
    
    public String getInstrumentCreationKeys() {
   	   if (instrumentCreationKeysList == null)
           return null;
       else 
           return new JsonBuilder(instrumentCreationKeysList).toString();
    }

    public void setInstrumentCreationKeys(String pInstrumentKeys) {
     	if (StringUtils.isNotBlank(pInstrumentKeys))
     		instrumentCreationKeysList = (List<String>)new JsonSlurper().parseText(pInstrumentKeys);
         else
        	 instrumentCreationKeysList = null;
    }

    public List<String> getInstrumentCreationKeysList() {
        return instrumentCreationKeysList;
    }

    public void setInstrumentCreationKeysList(List<String> pInstrumentKeysList) {
        instrumentCreationKeysList = pInstrumentKeysList;
    }
    
    public Yes getInstVisibleToMaker() {
		return instVisibleToMaker;
	}

	public void setInstVisibleToMaker(Yes instVisibleToMaker) {
		this.instVisibleToMaker = instVisibleToMaker;
	}

	public Date getFromFilterGoodsAcceptDate() {
		return fromFilterGoodsAcceptDate;
	}

	public void setFromFilterGoodsAcceptDate(Date fromFilterGoodsAcceptDate) {
		this.fromFilterGoodsAcceptDate = fromFilterGoodsAcceptDate;
	}

	public Date getToFilterGoodsAcceptDate() {
		return toFilterGoodsAcceptDate;
	}

	public void setToFilterGoodsAcceptDate(Date toFilterGoodsAcceptDate) {
		this.toFilterGoodsAcceptDate = toFilterGoodsAcceptDate;
	}

	public Date getFromFilterInstDate() {
		return fromFilterInstDate;
	}

	public void setFromFilterInstDate(Date fromFilterInstDate) {
		this.fromFilterInstDate = fromFilterInstDate;
	}

	public Date getToFilterInstDate() {
		return toFilterInstDate;
	}

	public void setToFilterInstDate(Date toFilterInstDate) {
		this.toFilterInstDate = toFilterInstDate;
	}

	public Date getFromFilterInstDueDate() {
		return fromFilterInstDueDate;
	}

	public void setFromFilterInstDueDate(Date fromFilterInstDueDate) {
		this.fromFilterInstDueDate = fromFilterInstDueDate;
	}

	public Date getToFilterInstDueDate() {
		return toFilterInstDueDate;
	}

	public void setToFilterInstDueDate(Date toFilterInstDueDate) {
		this.toFilterInstDueDate = toFilterInstDueDate;
	}

	public boolean resetCostChargeBearer() {
		return resetCostChargeBearer;
	}
	public void setResetCostChargeBearer(boolean pResetCostChargeBearer) {
		resetCostChargeBearer = pResetCostChargeBearer;
	}
	
	public Long getCfId() {
        return cfId;
    }

    public void setCfId(Long pCfId) {
        cfId = pCfId;
    }

    public String getCfData() {
        return cfData;
    }

    public void setCfData(String pCfData) {
        cfData = pCfData;
    }
	
	
	public String getCounterUpdateFields() {
		return counterUpdateFields;
	}

	public void setCounterUpdateFields(String counterUpdateFields) {
		this.counterUpdateFields = counterUpdateFields;
	}

	public Yes getIsApiVerified() {
		if(StringUtils.isNotEmpty(counterUpdateFields)) {
			//this is checked since, if nothing has changed then we are storing emtpy hash ie {}
			if(StringUtils.length(counterUpdateFields)>0) {
				return CommonAppConstants.Yes.Yes;
			}
		}
		return null;
	}

	public void setIsApiVerified(Yes isApiVerified) {
	}

	public boolean hasCounterUpdateFields() {
		return CommonAppConstants.Yes.Yes.equals(getIsApiVerified());
	}
	
	public boolean updateFieldsFromCounterUpdate() {
		if(StringUtils.isNotEmpty(counterUpdateFields)) {
			Map<String, Object> lFieldValueMap = (Map<String, Object>)  new JsonSlurper().parseText(counterUpdateFields);
			if(lFieldValueMap!=null && lFieldValueMap.size() > 0) {
				List<String> lFieldsToUpdate = new ArrayList<String>();
				GenericDAO<InstrumentBean> lInstrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class); 
				//
				for(String lKey : lFieldValueMap.keySet()) {
					lFieldsToUpdate.add(lKey);
				}
				List<BeanFieldMeta> lFieldsMeta = lInstrumentDAO.getBeanMeta().getFieldMetaList(null, lFieldsToUpdate);
				BeanFieldMeta lFieldMeta = null;
				List<ValidationFailBean> lMessages = new ArrayList<BeanFieldMeta.ValidationFailBean>();
				//
				Object lValue = null;
				for (int lPtr = 0; lPtr < lFieldsMeta.size(); lPtr++) {
					lFieldMeta = lFieldsMeta.get(lPtr);
					lMessages.clear();
					lValue = lFieldValueMap.get(lFieldMeta.getName());
					if(lValue != null) {
						if(DataType.STRING.equals(lFieldMeta.getDataType())) {
							lFieldMeta.setProperty(this, lValue , lMessages);
						}else if(DataType.DATE.equals(lFieldMeta.getDataType())) {
							lFieldMeta.setProperty(this, CommonUtilities.getDate(lValue.toString(), AppConstants.DATE_FORMAT) , lMessages);
						}else if(DataType.INTEGER.equals(lFieldMeta.getDataType())) {
							lFieldMeta.setProperty(this, Long.parseLong(lValue.toString()), lMessages);
						}else if(DataType.DECIMAL.equals(lFieldMeta.getDataType())) {
							lFieldMeta.setProperty(this, new BigDecimal(lValue.toString()), lMessages);
						}
					}else {
						lFieldMeta.setProperty(this, null, lMessages);
					}
					if(lMessages.size() > 0) {
						logger.info("Error while setting field in instrument : " + (id!=null?id:"") + ". Field : "+lFieldMeta.getName()+ ". Value : "+(lFieldValueMap.get(lFieldMeta.getName())!=null?lFieldValueMap.get(lFieldMeta.getName()).toString():""));
					}
				}
				if(lFieldsMeta.size()>0) {
					return true;
				}
			}
		}
		return false;
	}


   public Boolean getIsGemInvoice(){
	   if(StringUtils.isNotEmpty(this.aggregatorEntity)) {
		   String lEntityCode = RegistryHelper.getInstance().getString(AppInitializer.REGISTRY_GEM_ARTERIA);
		   if(this.aggregatorEntity.equals(lEntityCode)){
			   return Boolean.TRUE;
		   }
	   }
	   return Boolean.FALSE;
   }
   public void setIsGemInvoice(Boolean pIsGemInvoice) {
	   //do nothing
   }

@Override
public BigDecimal getFactoredAmount() {
	// TODO Auto-generated method stub
	return null;
}

@Override
public void setFactoredAmount(BigDecimal pFactoredAmount) {
	// TODO Auto-generated method stub
	
}
   

}