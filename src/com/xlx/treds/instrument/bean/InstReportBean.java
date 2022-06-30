package com.xlx.treds.instrument.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.AutoAcceptableBidTypes;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.AppConstants.CostCollectionLeg;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean.SplittingPoint;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

public class InstReportBean {
    public enum Inst_Type implements IKeyValEnumInterface<String>{
        Invoice("INV","Invoice");
        
        private final String code;
        private final String desc;
        private Inst_Type(String pCode, String pDesc) {
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
    public enum Inst_DelCat implements IKeyValEnumInterface<String>{
        Material("MAT","Material"),Service("SER","Service");
        
        private final String code;
        private final String desc;
        private Inst_DelCat(String pCode, String pDesc) {
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
    public enum BidType implements IKeyValEnumInterface<String>{
        Reserved("RES","Reserved"),Open("OPN","Open");
        
        private final String code;
        private final String desc;
        private BidType(String pCode, String pDesc) {
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
    public enum Inst_AutoConvert implements IKeyValEnumInterface<String>{
        Auto("Y","Auto"),Supplier("S","Supplier"),Purchaser("P","Purchaser");
        
        private final String code;
        private final String desc;
        private Inst_AutoConvert(String pCode, String pDesc) {
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
    public enum Inst_SplittingPoint implements IKeyValEnumInterface<String>{
        Statutory_Due_Date("SDD","Statutory Due Date"),Invoice_Due_Date("IDD","Invoice Due Date");
        
        private final String code;
        private final String desc;
        private Inst_SplittingPoint(String pCode, String pDesc) {
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
    public enum Inst_Status implements IKeyValEnumInterface<String>{
        Drafting("DRFT","Drafting"),Checker_Pending("SUB","Checker Pending"),Checker_Approved("CHKAPP","Checker Approved"),Checker_Returned("CHKRET","Checker Returned"),Checker_Rejected("CHKREJ","Checker Rejected"),Counter_Approved("COUAPP","Counter Approved"),Counter_Returned("COURET","Counter Returned"),Counter_Rejected("COUREJ","Counter Rejected"),Converted_To_Factoring_Unit("FACUNT","Converted To Factoring Unit"),Withdrawn("WTHDRN","Withdrawn"),Expired("EXP","Expired"),Leg_3_Generated("LEG3","Leg 3 Generated"),Factored("FACT","Factored"),Leg_1_Settled("L1SET","Leg 1 Settled"),Leg_1_Failed("L1FAIL","Leg 1 Failed"),Leg_2_Settled("L2SET","Leg 2 Settled"),Leg_2_Failed("L2FAIL","Leg 2 Failed"),Counter_Checker_Pending("COUCHKPEN","Counter Checker Pending"),Counter_Checker_Return("COUCHKRET","Counter Checker Return");;
        
        private final String code;
        private final String desc;
        private Inst_Status(String pCode, String pDesc) {
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
    public enum Inst_SupplyType implements IKeyValEnumInterface<String>{
        Inward("I","Inward"),Outward("O","Outward");
        
        private final String code;
        private final String desc;
        private Inst_SupplyType(String pCode, String pDesc) {
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
    public enum Inst_DocType implements IKeyValEnumInterface<String>{
        Invoice("INV","Invoice"),Bill("BIL","Bill"),Bill_of_Entry("BOE","Bill of Entry"),Challan("CHL","Challan"),Credit_Note("CNT","Credit Note"),_Others("OTH"," Others");
        
        private final String code;
        private final String desc;
        private Inst_DocType(String pCode, String pDesc) {
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
    public enum Inst_TransMode implements IKeyValEnumInterface<Long>{
        Road(Long.valueOf(1),"Road"),Rail(Long.valueOf(2),"Rail"),Air(Long.valueOf(3),"Air"),Ship(Long.valueOf(4),"Ship");
        
        private final Long code;
        private final String desc;
        private Inst_TransMode(Long pCode, String pDesc) {
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
    public enum Fact_Status implements IKeyValEnumInterface<String>{
        Ready_For_Auction("RDY","Ready For Auction"),Active("ACT","Active"),Factored("FACT","Factored"),Expired("EXP","Expired"),Leg_3_Generated("LEG3","Leg 3 Generated"),Withdrawn("WTHDRN","Withdrawn"),Suspended("SUSP","Suspended"),Leg_1_Settled("L1SET","Leg 1 Settled"),Leg_1_Failed("L1FAIL","Leg 1 Failed"),Leg_2_Settled("L2SET","Leg 2 Settled"),Leg_2_Failed("L2FAIL","Leg 2 Failed");
        
        private final String code;
        private final String desc;
        private Fact_Status(String pCode, String pDesc) {
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
    public enum Bid_Status implements IKeyValEnumInterface<String>{
        Active("ACT","Active"),Deleted("DEL","Deleted"),Deleted_By_Owner("DLO","Deleted By Owner"),Accepted("APT","Accepted"),Expired("EXP","Bid Expired"),NotAccepted("NAT","NotAccepted");
        
        private final String code;
        private final String desc;
        private Bid_Status(String pCode, String pDesc) {
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
    public enum Bid_BidType implements IKeyValEnumInterface<String>{
        Reserved("RES","Reserved"),Open("OPN","Open");
        
        private final String code;
        private final String desc;
        private Bid_BidType(String pCode, String pDesc) {
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
    public enum Bid_ProvAction implements IKeyValEnumInterface<String>{
        Entry("E","Entry"),Modify("M","Modify"),Cancel("C","Cancel");
        
        private final String code;
        private final String desc;
        private Bid_ProvAction(String pCode, String pDesc) {
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
    public enum Bid_AppStatus implements IKeyValEnumInterface<String>{
        Pending("P","Pending"),Approved("A","Approved"),Rejected("R","Rejected"),Withdrawn("W","Withdrawn");
        
        private final String code;
        private final String desc;
        private Bid_AppStatus(String pCode, String pDesc) {
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
    
    public enum Gst_ChargeType implements IKeyValEnumInterface<String>{
        Normal("N","Normal"),Split("S","Split");
        
        private final String code;
        private final String desc;
        private Gst_ChargeType(String pCode, String pDesc) {
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

    private Long inst_Id;
    private Inst_Type inst_Type;
    private String inst_Supplier;
    private String inst_SupplierRef;
    private String inst_SupName;
    private Long inst_SupClId;
    private String inst_SupLocation;
    private String inst_SupGstState;
    private String inst_SupGstn;
    private String inst_SupMsmeStatus;
    private String inst_Purchaser;
    private String inst_PurchaserRef;
    private String inst_PurName;
    private Long inst_PurClId;
    private String inst_PurLocation;
    private String inst_PurGstState;
    private String inst_PurGstn;
    private Date inst_PoDate;
    private String inst_PoNumber;
    private String inst_CounterRefNum;
    private Date inst_GoodsAcceptDate;
    private Inst_DelCat inst_DelCat;
    private String inst_Description;
    private String inst_InstNumber;
    private Date inst_InstDate;
    private Date inst_InstDueDate;
    private Date inst_StatDueDate;
    private Date inst_MaturityDate;
    private Timestamp inst_FactorMaxEndDateTime;
    private String inst_Currency;
    private BigDecimal inst_Amount;
    private BigDecimal inst_HaircutPercent;
    private BigDecimal inst_AdjAmount;
    private BigDecimal inst_CashDiscountPercent;
    private BigDecimal inst_CashDiscountValue;
    private BigDecimal inst_TdsAmount;
    private BigDecimal inst_NetAmount;
    private String inst_InstImage;
    private String inst_CreditNoteImage;
    private String inst_Supportings;
    private String inst_Sup1;
    private String inst_Sup2;
    private String inst_Sup3;
    private String inst_Sup4;
    private String inst_Sup5;
    private Long inst_CreditPeriod;
    private Yes inst_EnableExtension;
    private Long inst_ExtendedCreditPeriod;
    private Date inst_ExtendedDueDate;
    private AutoAcceptBid inst_AutoAccept;
    private AutoAcceptableBidTypes inst_AutoAcceptableBidTypes;
    private Inst_AutoConvert inst_AutoConvert;
    private CostBearer inst_Period1CostBearer;
    private BigDecimal inst_Period1CostPercent;
    private CostBearer inst_Period2CostBearer;
    private BigDecimal inst_Period2CostPercent;
    private CostBearer inst_Period3CostBearer;
    private BigDecimal inst_Period3CostPercent;
    private CostBearingType inst_CostBearingType;
    private Inst_SplittingPoint inst_SplittingPoint;
    private CostBearer inst_PreSplittingCostBearer;
    private CostBearer inst_PostSplittingCostBearer;
    private BigDecimal inst_BuyerPercent;
    private BigDecimal inst_SellerPercent;
    private CostBearer inst_BidAcceptingEntityType;
    private CostBearingType inst_ChargeBearer;
    private CostBearer inst_Period1ChargeBearer;
    private BigDecimal inst_Period1ChargePercent;
    private CostBearer inst_Period2ChargeBearer;
    private BigDecimal inst_Period2ChargePercent;
    private CostBearer inst_Period3ChargeBearer;
    private BigDecimal inst_Period3ChargePercent;
    private SplittingPoint inst_SplittingPointCharge;
    private CostBearer inst_PreSplittingCharge;
    private CostBearer inst_PostSplittingCharge;
    private BigDecimal inst_BuyerPercentCharge;
    private BigDecimal inst_SellerPercentCharge;
    private YesNo inst_SettleLeg3Flag;
    private Long inst_FileId;
    private Inst_Status inst_Status;
    private String inst_StatusRemarks;
    private Timestamp inst_StatusUpdateTime;
    private String inst_MakerEntity;
    private Long inst_MakerAuId;
    private String inst_MakerLoginId;
    private String inst_MakerName;
    private Long inst_CheckerAuId;
    private String inst_CheckerLoginId;
    private String inst_CheckerName;
    private String inst_CounterEntity;
    private Long inst_CounterAuId;
    private String inst_CounterLoginId;
    private String inst_CounterName;
    private Long inst_CounterCheckerAuId;
    private String inst_CounterCheckerLoginId;
    private String inst_CounterCheckerName;
    private String inst_OwnerEntity;
    private Long inst_OwnerAuId;
    private String inst_OwnerLoginId;
    private String inst_OwnerName;
    private String inst_MonetagoLedgerId;
    private String inst_MonetagoFactorTxnId;
    private String inst_MonetagoCancelTxnId;
    private String inst_SettlePurLocation;
    private String inst_SettlePurGstState;
    private String inst_SettlePurGstn;
    private Long inst_FuId;
    private String inst_CounterModifiedFields;
    private String inst_SalesCategory;
    private String inst_EwayBillNo;
    private Inst_SupplyType inst_SupplyType;
    private Inst_DocType inst_DocType;
    private String inst_DocNo;
    private String inst_FromPincode;
    private String inst_ToPincode;
    private Inst_TransMode inst_TransMode;
    private String inst_TransporterName;
    private String inst_TransporterId;
    private String inst_TransDocNo;
    private Date inst_TransDocDate;
    private String inst_VehicleNo;
    private Long inst_RecordVersion;
    private Long inst_Tab;
    private Long inst_Age;
    private String inst_AggregatorEntity;
    private Long  inst_AggregatorAuId;
    private Long fact_Id;
    private Date fact_MaturityDate;
    private Date fact_StatDueDate;
    private Yes fact_EnableExtension;
    private Long fact_ExtendedCreditPeriod;
    private Date fact_ExtendedDueDate;
    private String fact_Currency;
    private BigDecimal fact_Amount;
    private String fact_Purchaser;
    private String fact_PurchaserRef;
    private String fact_Supplier;
    private String fact_SupplierRef;
    private String fact_IntroducingEntity;
    private Long fact_IntroducingAuId;
    private String fact_IntroducingLoginId;
    private String fact_IntroducingName;
    private String fact_CounterEntity;
    private Long fact_CounterAuId;
    private String fact_CounterLoginId;
    private String fact_CounterName;
    private String fact_OwnerEntity;
    private Long fact_OwnerAuId;
    private String fact_OwnerLoginId;
    private String fact_OwnerName;
    private Fact_Status fact_Status;
    private Timestamp fact_FactorStartDateTime;
    private Timestamp fact_FactorEndDateTime;
    private Timestamp fact_FactorMaxEndDateTime;
    private AutoAcceptBid fact_AutoAccept;
    private AutoAcceptableBidTypes fact_AutoAcceptableBidTypes;
    private AutoConvert fact_AutoConvert;
    private CostBearer fact_Period1CostBearer;
    private BigDecimal fact_Period1CostPercent;
    private CostBearer fact_Period2CostBearer;
    private BigDecimal fact_Period2CostPercent;
    private CostBearer fact_Period3CostBearer;
    private BigDecimal fact_Period3CostPercent;
    private CostBearingType fact_ChargeBearer;
    private CostBearer fact_Period1ChargeBearer;
    private BigDecimal fact_Period1ChargePercent;
    private CostBearer fact_Period2ChargeBearer;
    private BigDecimal fact_Period2ChargePercent;
    private CostBearer fact_Period3ChargeBearer;
    private BigDecimal fact_Period3ChargePercent;
    private String fact_SupGstState;
    private String fact_SupGstn;
    private String fact_PurGstState;
    private String fact_PurGstn;
    private YesNo fact_SettleLeg3Flag;
    private Long fact_BdId;
    private BidType fact_AcceptedBidType;
    private BigDecimal fact_AcceptedRate;
    private BigDecimal fact_AcceptedHaircut;
    private Date fact_Leg1Date;
    private BigDecimal fact_FactoredAmount;
    private BigDecimal fact_PurchaserLeg1Interest;
    private BigDecimal fact_SupplierLeg1Interest;
    private BigDecimal fact_PurchaserLeg2Interest;
    private BigDecimal fact_Leg2ExtensionInterest;
    private BigDecimal fact_Charges;
    private BigDecimal fact_Cgst;
    private BigDecimal fact_Sgst;
    private BigDecimal fact_Igst;
    private BigDecimal fact_CgstSurcharge;
    private BigDecimal fact_SgstSurcharge;
    private BigDecimal fact_IgstSurcharge;
    private BigDecimal fact_CgstValue;
    private BigDecimal fact_SgstValue;
    private BigDecimal fact_IgstValue;
    private String fact_Financier;
    private String fact_AcceptingEntity;
    private Long fact_AcceptingAuId;
    private String fact_AcceptingLoginId;
    private String fact_AcceptingName;
    private Timestamp fact_AcceptDateTime;
    private BigDecimal fact_LimitUtilized;
    private String fact_LimitIds;
    private BigDecimal fact_PurSupLimitUtilized;
    private Date fact_FilterMaturityDate;
    private BigDecimal fact_FilterAmount;
    private String fact_SalesCategory;
    private Long fact_PurchaserSettleLoc;
    private Long fact_SupplierSettleLoc;
    private Long fact_RecordVersion;
    private String fact_PurName;
    private String fact_SupName;
    private String fact_FinName;
    private Long fact_Tenure;
    private Long fact_FilterFromTenure;
    private Long fact_FilterToTenure;
    private String fact_FilterSellerCategory;
    private String fact_FilterMsmeStatus;
    private BigDecimal fact_CapRate;
    private BigDecimal fact_FilterToCapRate;
    private Long bid_FuId;
    private String bid_FinancierEntity;
    private Long bid_FinancierAuId;
    private String bid_FinancierLoginId;
    private String bid_FinancierName;
    private BigDecimal bid_Rate;
    private BigDecimal bid_Haircut;
    private Date bid_ValidTill;
    private Bid_Status bid_Status;
    private String bid_StatusRemarks;
    private Long bid_Id;
    private Timestamp bid_Timestamp;
    private Long bid_LastAuId;
    private String bid_LastLoginId;
    private Bid_BidType bid_BidType;
    private BigDecimal bid_ProvRate;
    private BigDecimal bid_ProvHaircut;
    private Date bid_ProvValidTill;
    private BidType bid_ProvBidType;
    private Bid_ProvAction bid_ProvAction;
    private Bid_AppStatus bid_AppStatus;
    private String bid_AppRemarks;
    private Long bid_CheckerAuId;
    private BigDecimal bid_LimitUtilised;
    private BigDecimal bid_BidLimitUtilised;
    private String bid_LimitIds;
    private CostCollectionLeg bid_CostLeg;
    private Yes inst_IsAggregatorCreated;

    public Long getInst_Id() {
        return inst_Id;
    }

    public void setInst_Id(Long pInst_Id) {
        inst_Id = pInst_Id;
    }

    public Inst_Type getInst_Type() {
        return inst_Type;
    }

    public void setInst_Type(Inst_Type pInst_Type) {
        inst_Type = pInst_Type;
    }

    public String getInst_Supplier() {
        return inst_Supplier;
    }

    public void setInst_Supplier(String pInst_Supplier) {
        inst_Supplier = pInst_Supplier;
    }

    public String getInst_SupplierRef() {
        return inst_SupplierRef;
    }

    public void setInst_SupplierRef(String pInst_SupplierRef) {
        inst_SupplierRef = pInst_SupplierRef;
    }

    public String getInst_SupName() {
        return inst_SupName;
    }

    public void setInst_SupName(String pInst_SupName) {
        inst_SupName = pInst_SupName;
    }

    public Long getInst_SupClId() {
        return inst_SupClId;
    }

    public void setInst_SupClId(Long pInst_SupClId) {
        inst_SupClId = pInst_SupClId;
    }

    public String getInst_SupLocation() {
        return inst_SupLocation;
    }

    public void setInst_SupLocation(String pInst_SupLocation) {
        inst_SupLocation = pInst_SupLocation;
    }

    public String getInst_SupGstState() {
        return inst_SupGstState;
    }

    public void setInst_SupGstState(String pInst_SupGstState) {
        inst_SupGstState = pInst_SupGstState;
    }

    public String getInst_SupGstn() {
        return inst_SupGstn;
    }

    public void setInst_SupGstn(String pInst_SupGstn) {
        inst_SupGstn = pInst_SupGstn;
    }

    public String getInst_SupMsmeStatus() {
        return inst_SupMsmeStatus;
    }

    public void setInst_SupMsmeStatus(String pInst_SupMsmeStatus) {
        inst_SupMsmeStatus = pInst_SupMsmeStatus;
    }

    public String getInst_Purchaser() {
        return inst_Purchaser;
    }

    public void setInst_Purchaser(String pInst_Purchaser) {
        inst_Purchaser = pInst_Purchaser;
    }

    public String getInst_PurchaserRef() {
        return inst_PurchaserRef;
    }

    public void setInst_PurchaserRef(String pInst_PurchaserRef) {
        inst_PurchaserRef = pInst_PurchaserRef;
    }

    public String getInst_PurName() {
        return inst_PurName;
    }

    public void setInst_PurName(String pInst_PurName) {
        inst_PurName = pInst_PurName;
    }

    public Long getInst_PurClId() {
        return inst_PurClId;
    }

    public void setInst_PurClId(Long pInst_PurClId) {
        inst_PurClId = pInst_PurClId;
    }

    public String getInst_PurLocation() {
        return inst_PurLocation;
    }

    public void setInst_PurLocation(String pInst_PurLocation) {
        inst_PurLocation = pInst_PurLocation;
    }

    public String getInst_PurGstState() {
        return inst_PurGstState;
    }

    public void setInst_PurGstState(String pInst_PurGstState) {
        inst_PurGstState = pInst_PurGstState;
    }

    public String getInst_PurGstn() {
        return inst_PurGstn;
    }

    public void setInst_PurGstn(String pInst_PurGstn) {
        inst_PurGstn = pInst_PurGstn;
    }

    public Date getInst_PoDate() {
        return inst_PoDate;
    }

    public void setInst_PoDate(Date pInst_PoDate) {
        inst_PoDate = pInst_PoDate;
    }

    public String getInst_PoNumber() {
        return inst_PoNumber;
    }

    public void setInst_PoNumber(String pInst_PoNumber) {
        inst_PoNumber = pInst_PoNumber;
    }

    public String getInst_CounterRefNum() {
        return inst_CounterRefNum;
    }

    public void setInst_CounterRefNum(String pInst_CounterRefNum) {
        inst_CounterRefNum = pInst_CounterRefNum;
    }

    public Date getInst_GoodsAcceptDate() {
        return inst_GoodsAcceptDate;
    }

    public void setInst_GoodsAcceptDate(Date pInst_GoodsAcceptDate) {
        inst_GoodsAcceptDate = pInst_GoodsAcceptDate;
    }

    public Inst_DelCat getInst_DelCat() {
        return inst_DelCat;
    }

    public void setInst_DelCat(Inst_DelCat pInst_DelCat) {
        inst_DelCat = pInst_DelCat;
    }

    public String getInst_Description() {
        return inst_Description;
    }

    public void setInst_Description(String pInst_Description) {
        inst_Description = pInst_Description;
    }

    public String getInst_InstNumber() {
        return inst_InstNumber;
    }

    public void setInst_InstNumber(String pInst_InstNumber) {
        inst_InstNumber = pInst_InstNumber;
    }

    public Date getInst_InstDate() {
        return inst_InstDate;
    }

    public void setInst_InstDate(Date pInst_InstDate) {
        inst_InstDate = pInst_InstDate;
    }

    public Date getInst_InstDueDate() {
        return inst_InstDueDate;
    }

    public void setInst_InstDueDate(Date pInst_InstDueDate) {
        inst_InstDueDate = pInst_InstDueDate;
    }

    public Date getInst_StatDueDate() {
        return inst_StatDueDate;
    }

    public void setInst_StatDueDate(Date pInst_StatDueDate) {
        inst_StatDueDate = pInst_StatDueDate;
    }

    public Date getInst_MaturityDate() {
        return inst_MaturityDate;
    }

    public void setInst_MaturityDate(Date pInst_MaturityDate) {
        inst_MaturityDate = pInst_MaturityDate;
    }

    public Timestamp getInst_FactorMaxEndDateTime() {
        return inst_FactorMaxEndDateTime;
    }

    public void setInst_FactorMaxEndDateTime(Timestamp pInst_FactorMaxEndDateTime) {
        inst_FactorMaxEndDateTime = pInst_FactorMaxEndDateTime;
    }

    public String getInst_Currency() {
        return inst_Currency;
    }

    public void setInst_Currency(String pInst_Currency) {
        inst_Currency = pInst_Currency;
    }

    public BigDecimal getInst_Amount() {
        return inst_Amount;
    }

    public void setInst_Amount(BigDecimal pInst_Amount) {
        inst_Amount = pInst_Amount;
    }

    public BigDecimal getInst_HaircutPercent() {
        return inst_HaircutPercent;
    }

    public void setInst_HaircutPercent(BigDecimal pInst_HaircutPercent) {
        inst_HaircutPercent = pInst_HaircutPercent;
    }

    public BigDecimal getInst_AdjAmount() {
        return inst_AdjAmount;
    }

    public void setInst_AdjAmount(BigDecimal pInst_AdjAmount) {
        inst_AdjAmount = pInst_AdjAmount;
    }

    public BigDecimal getInst_CashDiscountPercent() {
        return inst_CashDiscountPercent;
    }

    public void setInst_CashDiscountPercent(BigDecimal pInst_CashDiscountPercent) {
        inst_CashDiscountPercent = pInst_CashDiscountPercent;
    }

    public BigDecimal getInst_CashDiscountValue() {
        return inst_CashDiscountValue;
    }

    public void setInst_CashDiscountValue(BigDecimal pInst_CashDiscountValue) {
        inst_CashDiscountValue = pInst_CashDiscountValue;
    }

    public BigDecimal getInst_TdsAmount() {
        return inst_TdsAmount;
    }

    public void setInst_TdsAmount(BigDecimal pInst_TdsAmount) {
        inst_TdsAmount = pInst_TdsAmount;
    }

    public BigDecimal getInst_NetAmount() {
        return inst_NetAmount;
    }

    public void setInst_NetAmount(BigDecimal pInst_NetAmount) {
        inst_NetAmount = pInst_NetAmount;
    }

    public String getInst_InstImage() {
        return inst_InstImage;
    }

    public void setInst_InstImage(String pInst_InstImage) {
        inst_InstImage = pInst_InstImage;
    }

    public String getInst_CreditNoteImage() {
        return inst_CreditNoteImage;
    }

    public void setInst_CreditNoteImage(String pInst_CreditNoteImage) {
        inst_CreditNoteImage = pInst_CreditNoteImage;
    }

    public String getInst_Supportings() {
        return inst_Supportings;
    }

    public void setInst_Supportings(String pInst_Supportings) {
        inst_Supportings = pInst_Supportings;
    }

    public String getInst_Sup1() {
        return inst_Sup1;
    }

    public void setInst_Sup1(String pInst_Sup1) {
        inst_Sup1 = pInst_Sup1;
    }

    public String getInst_Sup2() {
        return inst_Sup2;
    }

    public void setInst_Sup2(String pInst_Sup2) {
        inst_Sup2 = pInst_Sup2;
    }

    public String getInst_Sup3() {
        return inst_Sup3;
    }

    public void setInst_Sup3(String pInst_Sup3) {
        inst_Sup3 = pInst_Sup3;
    }

    public String getInst_Sup4() {
        return inst_Sup4;
    }

    public void setInst_Sup4(String pInst_Sup4) {
        inst_Sup4 = pInst_Sup4;
    }

    public String getInst_Sup5() {
        return inst_Sup5;
    }

    public void setInst_Sup5(String pInst_Sup5) {
        inst_Sup5 = pInst_Sup5;
    }

    public Long getInst_CreditPeriod() {
        return inst_CreditPeriod;
    }

    public void setInst_CreditPeriod(Long pInst_CreditPeriod) {
        inst_CreditPeriod = pInst_CreditPeriod;
    }

    public Yes getInst_EnableExtension() {
        return inst_EnableExtension;
    }

    public void setInst_EnableExtension(Yes pInst_EnableExtension) {
        inst_EnableExtension = pInst_EnableExtension;
    }

    public Long getInst_ExtendedCreditPeriod() {
        return inst_ExtendedCreditPeriod;
    }

    public void setInst_ExtendedCreditPeriod(Long pInst_ExtendedCreditPeriod) {
        inst_ExtendedCreditPeriod = pInst_ExtendedCreditPeriod;
    }

    public Date getInst_ExtendedDueDate() {
        return inst_ExtendedDueDate;
    }

    public void setInst_ExtendedDueDate(Date pInst_ExtendedDueDate) {
        inst_ExtendedDueDate = pInst_ExtendedDueDate;
    }

    public AutoAcceptBid getInst_AutoAccept() {
        return inst_AutoAccept;
    }

    public void setInst_AutoAccept(AutoAcceptBid pInst_AutoAccept) {
        inst_AutoAccept = pInst_AutoAccept;
    }

    public AutoAcceptableBidTypes getInst_AutoAcceptableBidTypes() {
        return inst_AutoAcceptableBidTypes;
    }

    public void setInst_AutoAcceptableBidTypes(AutoAcceptableBidTypes pInst_AutoAcceptableBidTypes) {
        inst_AutoAcceptableBidTypes = pInst_AutoAcceptableBidTypes;
    }

    public Inst_AutoConvert getInst_AutoConvert() {
        return inst_AutoConvert;
    }

    public void setInst_AutoConvert(Inst_AutoConvert pInst_AutoConvert) {
        inst_AutoConvert = pInst_AutoConvert;
    }

    public CostBearer getInst_Period1CostBearer() {
        return inst_Period1CostBearer;
    }

    public void setInst_Period1CostBearer(CostBearer pInst_Period1CostBearer) {
        inst_Period1CostBearer = pInst_Period1CostBearer;
    }

    public BigDecimal getInst_Period1CostPercent() {
        return inst_Period1CostPercent;
    }

    public void setInst_Period1CostPercent(BigDecimal pInst_Period1CostPercent) {
        inst_Period1CostPercent = pInst_Period1CostPercent;
    }

    public CostBearer getInst_Period2CostBearer() {
        return inst_Period2CostBearer;
    }

    public void setInst_Period2CostBearer(CostBearer pInst_Period2CostBearer) {
        inst_Period2CostBearer = pInst_Period2CostBearer;
    }

    public BigDecimal getInst_Period2CostPercent() {
        return inst_Period2CostPercent;
    }

    public void setInst_Period2CostPercent(BigDecimal pInst_Period2CostPercent) {
        inst_Period2CostPercent = pInst_Period2CostPercent;
    }

    public CostBearer getInst_Period3CostBearer() {
        return inst_Period3CostBearer;
    }

    public void setInst_Period3CostBearer(CostBearer pInst_Period3CostBearer) {
        inst_Period3CostBearer = pInst_Period3CostBearer;
    }

    public BigDecimal getInst_Period3CostPercent() {
        return inst_Period3CostPercent;
    }

    public void setInst_Period3CostPercent(BigDecimal pInst_Period3CostPercent) {
        inst_Period3CostPercent = pInst_Period3CostPercent;
    }

    public CostBearingType getInst_CostBearingType() {
        return inst_CostBearingType;
    }

    public void setInst_CostBearingType(CostBearingType pInst_CostBearingType) {
        inst_CostBearingType = pInst_CostBearingType;
    }

    public Inst_SplittingPoint getInst_SplittingPoint() {
        return inst_SplittingPoint;
    }

    public void setInst_SplittingPoint(Inst_SplittingPoint pInst_SplittingPoint) {
        inst_SplittingPoint = pInst_SplittingPoint;
    }

    public CostBearer getInst_PreSplittingCostBearer() {
        return inst_PreSplittingCostBearer;
    }

    public void setInst_PreSplittingCostBearer(CostBearer pInst_PreSplittingCostBearer) {
        inst_PreSplittingCostBearer = pInst_PreSplittingCostBearer;
    }

    public CostBearer getInst_PostSplittingCostBearer() {
        return inst_PostSplittingCostBearer;
    }

    public void setInst_PostSplittingCostBearer(CostBearer pInst_PostSplittingCostBearer) {
        inst_PostSplittingCostBearer = pInst_PostSplittingCostBearer;
    }

    public BigDecimal getInst_BuyerPercent() {
        return inst_BuyerPercent;
    }

    public void setInst_BuyerPercent(BigDecimal pInst_BuyerPercent) {
        inst_BuyerPercent = pInst_BuyerPercent;
    }

    public BigDecimal getInst_SellerPercent() {
        return inst_SellerPercent;
    }

    public void setInst_SellerPercent(BigDecimal pInst_SellerPercent) {
        inst_SellerPercent = pInst_SellerPercent;
    }

    public CostBearer getInst_BidAcceptingEntityType() {
        return inst_BidAcceptingEntityType;
    }

    public void setInst_BidAcceptingEntityType(CostBearer pInst_BidAcceptingEntityType) {
        inst_BidAcceptingEntityType = pInst_BidAcceptingEntityType;
    }

    public YesNo getInst_SettleLeg3Flag() {
        return inst_SettleLeg3Flag;
    }

    public void setInst_SettleLeg3Flag(YesNo pInst_SettleLeg3Flag) {
        inst_SettleLeg3Flag = pInst_SettleLeg3Flag;
    }

    public Long getInst_FileId() {
        return inst_FileId;
    }

    public void setInst_FileId(Long pInst_FileId) {
        inst_FileId = pInst_FileId;
    }

    public Inst_Status getInst_Status() {
        return inst_Status;
    }

    public void setInst_Status(Inst_Status pInst_Status) {
        inst_Status = pInst_Status;
    }

    public String getInst_StatusRemarks() {
        return inst_StatusRemarks;
    }

    public void setInst_StatusRemarks(String pInst_StatusRemarks) {
        inst_StatusRemarks = pInst_StatusRemarks;
    }

    public Timestamp getInst_StatusUpdateTime() {
        return inst_StatusUpdateTime;
    }

    public void setInst_StatusUpdateTime(Timestamp pInst_StatusUpdateTime) {
        inst_StatusUpdateTime = pInst_StatusUpdateTime;
    }

    public String getInst_MakerEntity() {
        return inst_MakerEntity;
    }

    public void setInst_MakerEntity(String pInst_MakerEntity) {
        inst_MakerEntity = pInst_MakerEntity;
    }

    public Long getInst_MakerAuId() {
        return inst_MakerAuId;
    }

    public void setInst_MakerAuId(Long pInst_MakerAuId) {
        inst_MakerAuId = pInst_MakerAuId;
    }

    public String getInst_MakerLoginId() {
        return inst_MakerLoginId;
    }

    public void setInst_MakerLoginId(String pInst_MakerLoginId) {
        inst_MakerLoginId = pInst_MakerLoginId;
    }

    public String getInst_MakerName() {
        return inst_MakerName;
    }

    public void setInst_MakerName(String pInst_MakerName) {
        inst_MakerName = pInst_MakerName;
    }

    public Long getInst_CheckerAuId() {
        return inst_CheckerAuId;
    }

    public void setInst_CheckerAuId(Long pInst_CheckerAuId) {
        inst_CheckerAuId = pInst_CheckerAuId;
    }

    public String getInst_CheckerLoginId() {
        return inst_CheckerLoginId;
    }

    public void setInst_CheckerLoginId(String pInst_CheckerLoginId) {
        inst_CheckerLoginId = pInst_CheckerLoginId;
    }

    public String getInst_CheckerName() {
        return inst_CheckerName;
    }

    public void setInst_CheckerName(String pInst_CheckerName) {
        inst_CheckerName = pInst_CheckerName;
    }

    public String getInst_CounterEntity() {
        return inst_CounterEntity;
    }

    public void setInst_CounterEntity(String pInst_CounterEntity) {
        inst_CounterEntity = pInst_CounterEntity;
    }

    public Long getInst_CounterAuId() {
        return inst_CounterAuId;
    }

    public void setInst_CounterAuId(Long pInst_CounterAuId) {
        inst_CounterAuId = pInst_CounterAuId;
    }

    public String getInst_CounterLoginId() {
        return inst_CounterLoginId;
    }

    public void setInst_CounterLoginId(String pInst_CounterLoginId) {
        inst_CounterLoginId = pInst_CounterLoginId;
    }

    public String getInst_CounterName() {
        return inst_CounterName;
    }

    public void setInst_CounterName(String pInst_CounterName) {
        inst_CounterName = pInst_CounterName;
    }

    public Long getInst_CounterCheckerAuId() {
        return inst_CounterCheckerAuId;
    }

    public void setInst_CounterCheckerAuId(Long pInst_CounterCheckerAuId) {
        inst_CounterCheckerAuId = pInst_CounterCheckerAuId;
    }

    public String getInst_CounterCheckerLoginId() {
        return inst_CounterCheckerLoginId;
    }

    public void setInst_CounterCheckerLoginId(String pInst_CounterCheckerLoginId) {
        inst_CounterCheckerLoginId = pInst_CounterCheckerLoginId;
    }

    public String getInst_CounterCheckerName() {
        return inst_CounterCheckerName;
    }

    public void setInst_CounterCheckerName(String pInst_CounterCheckerName) {
        inst_CounterCheckerName = pInst_CounterCheckerName;
    }
    
    public String getInst_OwnerEntity() {
        return inst_OwnerEntity;
    }

    public void setInst_OwnerEntity(String pInst_OwnerEntity) {
        inst_OwnerEntity = pInst_OwnerEntity;
    }

    public Long getInst_OwnerAuId() {
        return inst_OwnerAuId;
    }

    public void setInst_OwnerAuId(Long pInst_OwnerAuId) {
        inst_OwnerAuId = pInst_OwnerAuId;
    }

    public String getInst_OwnerLoginId() {
        return inst_OwnerLoginId;
    }

    public void setInst_OwnerLoginId(String pInst_OwnerLoginId) {
        inst_OwnerLoginId = pInst_OwnerLoginId;
    }

    public String getInst_OwnerName() {
        return inst_OwnerName;
    }

    public void setInst_OwnerName(String pInst_OwnerName) {
        inst_OwnerName = pInst_OwnerName;
    }

    public String getInst_MonetagoLedgerId() {
        return inst_MonetagoLedgerId;
    }

    public void setInst_MonetagoLedgerId(String pInst_MonetagoLedgerId) {
        inst_MonetagoLedgerId = pInst_MonetagoLedgerId;
    }

    public String getInst_MonetagoFactorTxnId() {
        return inst_MonetagoFactorTxnId;
    }

    public void setInst_MonetagoFactorTxnId(String pInst_MonetagoFactorTxnId) {
        inst_MonetagoFactorTxnId = pInst_MonetagoFactorTxnId;
    }

    public String getInst_MonetagoCancelTxnId() {
        return inst_MonetagoCancelTxnId;
    }

    public void setInst_MonetagoCancelTxnId(String pInst_MonetagoCancelTxnId) {
        inst_MonetagoCancelTxnId = pInst_MonetagoCancelTxnId;
    }

    public String getInst_SettlePurLocation() {
        return inst_SettlePurLocation;
    }

    public void setInst_SettlePurLocation(String pInst_SettlePurLocation) {
        inst_SettlePurLocation = pInst_SettlePurLocation;
    }

    public String getInst_SettlePurGstState() {
        return inst_SettlePurGstState;
    }

    public void setInst_SettlePurGstState(String pInst_SettlePurGstState) {
        inst_SettlePurGstState = pInst_SettlePurGstState;
    }

    public String getInst_SettlePurGstn() {
        return inst_SettlePurGstn;
    }

    public void setInst_SettlePurGstn(String pInst_SettlePurGstn) {
        inst_SettlePurGstn = pInst_SettlePurGstn;
    }

    public Long getInst_FuId() {
        return inst_FuId;
    }

    public void setInst_FuId(Long pInst_FuId) {
        inst_FuId = pInst_FuId;
    }

    public String getInst_CounterModifiedFields() {
        return inst_CounterModifiedFields;
    }

    public void setInst_CounterModifiedFields(String pInst_CounterModifiedFields) {
        inst_CounterModifiedFields = pInst_CounterModifiedFields;
    }

    public String getInst_SalesCategory() {
        return inst_SalesCategory;
    }

    public void setInst_SalesCategory(String pInst_SalesCategory) {
        inst_SalesCategory = pInst_SalesCategory;
    }

    public String getInst_EwayBillNo() {
        return inst_EwayBillNo;
    }

    public void setInst_EwayBillNo(String pInst_EwayBillNo) {
        inst_EwayBillNo = pInst_EwayBillNo;
    }

    public Inst_SupplyType getInst_SupplyType() {
        return inst_SupplyType;
    }

    public void setInst_SupplyType(Inst_SupplyType pInst_SupplyType) {
        inst_SupplyType = pInst_SupplyType;
    }

    public Inst_DocType getInst_DocType() {
        return inst_DocType;
    }

    public void setInst_DocType(Inst_DocType pInst_DocType) {
        inst_DocType = pInst_DocType;
    }

    public String getInst_DocNo() {
        return inst_DocNo;
    }

    public void setInst_DocNo(String pInst_DocNo) {
        inst_DocNo = pInst_DocNo;
    }

    public String getInst_FromPincode() {
        return inst_FromPincode;
    }

    public void setInst_FromPincode(String pInst_FromPincode) {
        inst_FromPincode = pInst_FromPincode;
    }

    public String getInst_ToPincode() {
        return inst_ToPincode;
    }

    public void setInst_ToPincode(String pInst_ToPincode) {
        inst_ToPincode = pInst_ToPincode;
    }

    public Inst_TransMode getInst_TransMode() {
        return inst_TransMode;
    }

    public void setInst_TransMode(Inst_TransMode pInst_TransMode) {
        inst_TransMode = pInst_TransMode;
    }

    public String getInst_TransporterName() {
        return inst_TransporterName;
    }

    public void setInst_TransporterName(String pInst_TransporterName) {
        inst_TransporterName = pInst_TransporterName;
    }

    public String getInst_TransporterId() {
        return inst_TransporterId;
    }

    public void setInst_TransporterId(String pInst_TransporterId) {
        inst_TransporterId = pInst_TransporterId;
    }

    public String getInst_TransDocNo() {
        return inst_TransDocNo;
    }

    public void setInst_TransDocNo(String pInst_TransDocNo) {
        inst_TransDocNo = pInst_TransDocNo;
    }

    public Date getInst_TransDocDate() {
        return inst_TransDocDate;
    }

    public void setInst_TransDocDate(Date pInst_TransDocDate) {
        inst_TransDocDate = pInst_TransDocDate;
    }

    public String getInst_VehicleNo() {
        return inst_VehicleNo;
    }

    public void setInst_VehicleNo(String pInst_VehicleNo) {
        inst_VehicleNo = pInst_VehicleNo;
    }

    public Long getInst_RecordVersion() {
        return inst_RecordVersion;
    }

    public void setInst_RecordVersion(Long pInst_RecordVersion) {
        inst_RecordVersion = pInst_RecordVersion;
    }

    public Long getInst_Tab() {
        return inst_Tab;
    }

    public void setInst_Tab(Long pInst_Tab) {
        inst_Tab = pInst_Tab;
    }

    public Long getInst_Age() {
        return inst_Age;
    }

    public void setInst_Age(Long pInst_Age) {
        inst_Age = pInst_Age;
    }

    public Long getFact_Id() {
        return fact_Id;
    }

    public void setFact_Id(Long pFact_Id) {
        fact_Id = pFact_Id;
    }

    public Date getFact_MaturityDate() {
        return fact_MaturityDate;
    }

    public void setFact_MaturityDate(Date pFact_MaturityDate) {
        fact_MaturityDate = pFact_MaturityDate;
    }

    public Date getFact_StatDueDate() {
        return fact_StatDueDate;
    }

    public void setFact_StatDueDate(Date pFact_StatDueDate) {
        fact_StatDueDate = pFact_StatDueDate;
    }

    public Yes getFact_EnableExtension() {
        return fact_EnableExtension;
    }

    public void setFact_EnableExtension(Yes pFact_EnableExtension) {
        fact_EnableExtension = pFact_EnableExtension;
    }

    public Long getFact_ExtendedCreditPeriod() {
        return fact_ExtendedCreditPeriod;
    }

    public void setFact_ExtendedCreditPeriod(Long pFact_ExtendedCreditPeriod) {
        fact_ExtendedCreditPeriod = pFact_ExtendedCreditPeriod;
    }

    public Date getFact_ExtendedDueDate() {
        return fact_ExtendedDueDate;
    }

    public void setFact_ExtendedDueDate(Date pFact_ExtendedDueDate) {
        fact_ExtendedDueDate = pFact_ExtendedDueDate;
    }

    public String getFact_Currency() {
        return fact_Currency;
    }

    public void setFact_Currency(String pFact_Currency) {
        fact_Currency = pFact_Currency;
    }

    public BigDecimal getFact_Amount() {
        return fact_Amount;
    }

    public void setFact_Amount(BigDecimal pFact_Amount) {
        fact_Amount = pFact_Amount;
    }

    public String getFact_Purchaser() {
        return fact_Purchaser;
    }

    public void setFact_Purchaser(String pFact_Purchaser) {
        fact_Purchaser = pFact_Purchaser;
    }

    public String getFact_PurchaserRef() {
        return fact_PurchaserRef;
    }

    public void setFact_PurchaserRef(String pFact_PurchaserRef) {
        fact_PurchaserRef = pFact_PurchaserRef;
    }

    public String getFact_Supplier() {
        return fact_Supplier;
    }

    public void setFact_Supplier(String pFact_Supplier) {
        fact_Supplier = pFact_Supplier;
    }

    public String getFact_SupplierRef() {
        return fact_SupplierRef;
    }

    public void setFact_SupplierRef(String pFact_SupplierRef) {
        fact_SupplierRef = pFact_SupplierRef;
    }

    public String getFact_IntroducingEntity() {
        return fact_IntroducingEntity;
    }

    public void setFact_IntroducingEntity(String pFact_IntroducingEntity) {
        fact_IntroducingEntity = pFact_IntroducingEntity;
    }

    public Long getFact_IntroducingAuId() {
        return fact_IntroducingAuId;
    }

    public void setFact_IntroducingAuId(Long pFact_IntroducingAuId) {
        fact_IntroducingAuId = pFact_IntroducingAuId;
    }

    public String getFact_IntroducingLoginId() {
        return fact_IntroducingLoginId;
    }

    public void setFact_IntroducingLoginId(String pFact_IntroducingLoginId) {
        fact_IntroducingLoginId = pFact_IntroducingLoginId;
    }

    public String getFact_IntroducingName() {
        return fact_IntroducingName;
    }

    public void setFact_IntroducingName(String pFact_IntroducingName) {
        fact_IntroducingName = pFact_IntroducingName;
    }

    public String getFact_CounterEntity() {
        return fact_CounterEntity;
    }

    public void setFact_CounterEntity(String pFact_CounterEntity) {
        fact_CounterEntity = pFact_CounterEntity;
    }

    public Long getFact_CounterAuId() {
        return fact_CounterAuId;
    }

    public void setFact_CounterAuId(Long pFact_CounterAuId) {
        fact_CounterAuId = pFact_CounterAuId;
    }

    public String getFact_CounterLoginId() {
        return fact_CounterLoginId;
    }

    public void setFact_CounterLoginId(String pFact_CounterLoginId) {
        fact_CounterLoginId = pFact_CounterLoginId;
    }

    public String getFact_CounterName() {
        return fact_CounterName;
    }

    public void setFact_CounterName(String pFact_CounterName) {
        fact_CounterName = pFact_CounterName;
    }

    public String getFact_OwnerEntity() {
        return fact_OwnerEntity;
    }

    public void setFact_OwnerEntity(String pFact_OwnerEntity) {
        fact_OwnerEntity = pFact_OwnerEntity;
    }

    public Long getFact_OwnerAuId() {
        return fact_OwnerAuId;
    }

    public void setFact_OwnerAuId(Long pFact_OwnerAuId) {
        fact_OwnerAuId = pFact_OwnerAuId;
    }

    public String getFact_OwnerLoginId() {
        return fact_OwnerLoginId;
    }

    public void setFact_OwnerLoginId(String pFact_OwnerLoginId) {
        fact_OwnerLoginId = pFact_OwnerLoginId;
    }

    public String getFact_OwnerName() {
        return fact_OwnerName;
    }

    public void setFact_OwnerName(String pFact_OwnerName) {
        fact_OwnerName = pFact_OwnerName;
    }

    public Fact_Status getFact_Status() {
        return fact_Status;
    }

    public void setFact_Status(Fact_Status pFact_Status) {
        fact_Status = pFact_Status;
    }

    public Timestamp getFact_FactorStartDateTime() {
        return fact_FactorStartDateTime;
    }

    public void setFact_FactorStartDateTime(Timestamp pFact_FactorStartDateTime) {
        fact_FactorStartDateTime = pFact_FactorStartDateTime;
    }

    public Timestamp getFact_FactorEndDateTime() {
        return fact_FactorEndDateTime;
    }

    public void setFact_FactorEndDateTime(Timestamp pFact_FactorEndDateTime) {
        fact_FactorEndDateTime = pFact_FactorEndDateTime;
    }

    public Timestamp getFact_FactorMaxEndDateTime() {
        return fact_FactorMaxEndDateTime;
    }

    public void setFact_FactorMaxEndDateTime(Timestamp pFact_FactorMaxEndDateTime) {
        fact_FactorMaxEndDateTime = pFact_FactorMaxEndDateTime;
    }

    public AutoAcceptBid getFact_AutoAccept() {
        return fact_AutoAccept;
    }

    public void setFact_AutoAccept(AutoAcceptBid pFact_AutoAccept) {
        fact_AutoAccept = pFact_AutoAccept;
    }

    public AutoAcceptableBidTypes getFact_AutoAcceptableBidTypes() {
        return fact_AutoAcceptableBidTypes;
    }

    public void setFact_AutoAcceptableBidTypes(AutoAcceptableBidTypes pFact_AutoAcceptableBidTypes) {
        fact_AutoAcceptableBidTypes = pFact_AutoAcceptableBidTypes;
    }

    public AutoConvert getFact_AutoConvert() {
        return fact_AutoConvert;
    }

    public void setFact_AutoConvert(AutoConvert pFact_AutoConvert) {
        fact_AutoConvert = pFact_AutoConvert;
    }

    public CostBearer getFact_Period1CostBearer() {
        return fact_Period1CostBearer;
    }

    public void setFact_Period1CostBearer(CostBearer pFact_Period1CostBearer) {
        fact_Period1CostBearer = pFact_Period1CostBearer;
    }

    public BigDecimal getFact_Period1CostPercent() {
        return fact_Period1CostPercent;
    }

    public void setFact_Period1CostPercent(BigDecimal pFact_Period1CostPercent) {
        fact_Period1CostPercent = pFact_Period1CostPercent;
    }

    public CostBearer getFact_Period2CostBearer() {
        return fact_Period2CostBearer;
    }

    public void setFact_Period2CostBearer(CostBearer pFact_Period2CostBearer) {
        fact_Period2CostBearer = pFact_Period2CostBearer;
    }

    public BigDecimal getFact_Period2CostPercent() {
        return fact_Period2CostPercent;
    }

    public void setFact_Period2CostPercent(BigDecimal pFact_Period2CostPercent) {
        fact_Period2CostPercent = pFact_Period2CostPercent;
    }

    public CostBearer getFact_Period3CostBearer() {
        return fact_Period3CostBearer;
    }

    public void setFact_Period3CostBearer(CostBearer pFact_Period3CostBearer) {
        fact_Period3CostBearer = pFact_Period3CostBearer;
    }

    public BigDecimal getFact_Period3CostPercent() {
        return fact_Period3CostPercent;
    }

    public void setFact_Period3CostPercent(BigDecimal pFact_Period3CostPercent) {
        fact_Period3CostPercent = pFact_Period3CostPercent;
    }

    public String getFact_SupGstState() {
        return fact_SupGstState;
    }

    public void setFact_SupGstState(String pFact_SupGstState) {
        fact_SupGstState = pFact_SupGstState;
    }

    public String getFact_SupGstn() {
        return fact_SupGstn;
    }

    public void setFact_SupGstn(String pFact_SupGstn) {
        fact_SupGstn = pFact_SupGstn;
    }

    public String getFact_PurGstState() {
        return fact_PurGstState;
    }

    public void setFact_PurGstState(String pFact_PurGstState) {
        fact_PurGstState = pFact_PurGstState;
    }

    public String getFact_PurGstn() {
        return fact_PurGstn;
    }

    public void setFact_PurGstn(String pFact_PurGstn) {
        fact_PurGstn = pFact_PurGstn;
    }

    public YesNo getFact_SettleLeg3Flag() {
        return fact_SettleLeg3Flag;
    }

    public void setFact_SettleLeg3Flag(YesNo pFact_SettleLeg3Flag) {
        fact_SettleLeg3Flag = pFact_SettleLeg3Flag;
    }

    public Long getFact_BdId() {
        return fact_BdId;
    }

    public void setFact_BdId(Long pFact_BdId) {
        fact_BdId = pFact_BdId;
    }

    public BidType getFact_AcceptedBidType() {
        return fact_AcceptedBidType;
    }

    public void setFact_AcceptedBidType(BidType pFact_AcceptedBidType) {
        fact_AcceptedBidType = pFact_AcceptedBidType;
    }

    public BigDecimal getFact_AcceptedRate() {
        return fact_AcceptedRate;
    }

    public void setFact_AcceptedRate(BigDecimal pFact_AcceptedRate) {
        fact_AcceptedRate = pFact_AcceptedRate;
    }

    public BigDecimal getFact_AcceptedHaircut() {
        return fact_AcceptedHaircut;
    }

    public void setFact_AcceptedHaircut(BigDecimal pFact_AcceptedHaircut) {
        fact_AcceptedHaircut = pFact_AcceptedHaircut;
    }

    public Date getFact_Leg1Date() {
        return fact_Leg1Date;
    }

    public void setFact_Leg1Date(Date pFact_Leg1Date) {
        fact_Leg1Date = pFact_Leg1Date;
    }

    public BigDecimal getFact_FactoredAmount() {
        return fact_FactoredAmount;
    }

    public void setFact_FactoredAmount(BigDecimal pFact_FactoredAmount) {
        fact_FactoredAmount = pFact_FactoredAmount;
    }

    public BigDecimal getFact_PurchaserLeg1Interest() {
        return fact_PurchaserLeg1Interest;
    }

    public void setFact_PurchaserLeg1Interest(BigDecimal pFact_PurchaserLeg1Interest) {
        fact_PurchaserLeg1Interest = pFact_PurchaserLeg1Interest;
    }

    public BigDecimal getFact_SupplierLeg1Interest() {
        return fact_SupplierLeg1Interest;
    }

    public void setFact_SupplierLeg1Interest(BigDecimal pFact_SupplierLeg1Interest) {
        fact_SupplierLeg1Interest = pFact_SupplierLeg1Interest;
    }

    public BigDecimal getFact_PurchaserLeg2Interest() {
        return fact_PurchaserLeg2Interest;
    }

    public void setFact_PurchaserLeg2Interest(BigDecimal pFact_PurchaserLeg2Interest) {
        fact_PurchaserLeg2Interest = pFact_PurchaserLeg2Interest;
    }

    public BigDecimal getFact_Leg2ExtensionInterest() {
        return fact_Leg2ExtensionInterest;
    }

    public void setFact_Leg2ExtensionInterest(BigDecimal pFact_Leg2ExtensionInterest) {
        fact_Leg2ExtensionInterest = pFact_Leg2ExtensionInterest;
    }

    public BigDecimal getFact_Charges() {
        return fact_Charges;
    }

    public void setFact_Charges(BigDecimal pFact_Charges) {
        fact_Charges = pFact_Charges;
    }

    public BigDecimal getFact_Cgst() {
        return fact_Cgst;
    }

    public void setFact_Cgst(BigDecimal pFact_Cgst) {
        fact_Cgst = pFact_Cgst;
    }

    public BigDecimal getFact_Sgst() {
        return fact_Sgst;
    }

    public void setFact_Sgst(BigDecimal pFact_Sgst) {
        fact_Sgst = pFact_Sgst;
    }

    public BigDecimal getFact_Igst() {
        return fact_Igst;
    }

    public void setFact_Igst(BigDecimal pFact_Igst) {
        fact_Igst = pFact_Igst;
    }

    public BigDecimal getFact_CgstSurcharge() {
        return fact_CgstSurcharge;
    }

    public void setFact_CgstSurcharge(BigDecimal pFact_CgstSurcharge) {
        fact_CgstSurcharge = pFact_CgstSurcharge;
    }

    public BigDecimal getFact_SgstSurcharge() {
        return fact_SgstSurcharge;
    }

    public void setFact_SgstSurcharge(BigDecimal pFact_SgstSurcharge) {
        fact_SgstSurcharge = pFact_SgstSurcharge;
    }

    public BigDecimal getFact_IgstSurcharge() {
        return fact_IgstSurcharge;
    }

    public void setFact_IgstSurcharge(BigDecimal pFact_IgstSurcharge) {
        fact_IgstSurcharge = pFact_IgstSurcharge;
    }

    public BigDecimal getFact_CgstValue() {
        return fact_CgstValue;
    }

    public void setFact_CgstValue(BigDecimal pFact_CgstValue) {
        fact_CgstValue = pFact_CgstValue;
    }

    public BigDecimal getFact_SgstValue() {
        return fact_SgstValue;
    }

    public void setFact_SgstValue(BigDecimal pFact_SgstValue) {
        fact_SgstValue = pFact_SgstValue;
    }

    public BigDecimal getFact_IgstValue() {
        return fact_IgstValue;
    }

    public void setFact_IgstValue(BigDecimal pFact_IgstValue) {
        fact_IgstValue = pFact_IgstValue;
    }

    public String getFact_Financier() {
        return fact_Financier;
    }

    public void setFact_Financier(String pFact_Financier) {
        fact_Financier = pFact_Financier;
    }

    public String getFact_AcceptingEntity() {
        return fact_AcceptingEntity;
    }

    public void setFact_AcceptingEntity(String pFact_AcceptingEntity) {
        fact_AcceptingEntity = pFact_AcceptingEntity;
    }

    public Long getFact_AcceptingAuId() {
        return fact_AcceptingAuId;
    }

    public void setFact_AcceptingAuId(Long pFact_AcceptingAuId) {
        fact_AcceptingAuId = pFact_AcceptingAuId;
    }

    public String getFact_AcceptingLoginId() {
        return fact_AcceptingLoginId;
    }

    public void setFact_AcceptingLoginId(String pFact_AcceptingLoginId) {
        fact_AcceptingLoginId = pFact_AcceptingLoginId;
    }

    public String getFact_AcceptingName() {
        return fact_AcceptingName;
    }

    public void setFact_AcceptingName(String pFact_AcceptingName) {
        fact_AcceptingName = pFact_AcceptingName;
    }

    public Timestamp getFact_AcceptDateTime() {
        return fact_AcceptDateTime;
    }

    public void setFact_AcceptDateTime(Timestamp pFact_AcceptDateTime) {
        fact_AcceptDateTime = pFact_AcceptDateTime;
    }

    public BigDecimal getFact_LimitUtilized() {
        return fact_LimitUtilized;
    }

    public void setFact_LimitUtilized(BigDecimal pFact_LimitUtilized) {
        fact_LimitUtilized = pFact_LimitUtilized;
    }

    public String getFact_LimitIds() {
        return fact_LimitIds;
    }

    public void setFact_LimitIds(String pFact_LimitIds) {
        fact_LimitIds = pFact_LimitIds;
    }

    public BigDecimal getFact_PurSupLimitUtilized() {
        return fact_PurSupLimitUtilized;
    }

    public void setFact_PurSupLimitUtilized(BigDecimal pFact_PurSupLimitUtilized) {
        fact_PurSupLimitUtilized = pFact_PurSupLimitUtilized;
    }

    public Date getFact_FilterMaturityDate() {
        return fact_FilterMaturityDate;
    }

    public void setFact_FilterMaturityDate(Date pFact_FilterMaturityDate) {
        fact_FilterMaturityDate = pFact_FilterMaturityDate;
    }

    public BigDecimal getFact_FilterAmount() {
        return fact_FilterAmount;
    }

    public void setFact_FilterAmount(BigDecimal pFact_FilterAmount) {
        fact_FilterAmount = pFact_FilterAmount;
    }

    public String getFact_SalesCategory() {
        return fact_SalesCategory;
    }

    public void setFact_SalesCategory(String pFact_SalesCategory) {
        fact_SalesCategory = pFact_SalesCategory;
    }

    public Long getFact_PurchaserSettleLoc() {
        return fact_PurchaserSettleLoc;
    }

    public void setFact_PurchaserSettleLoc(Long pFact_PurchaserSettleLoc) {
        fact_PurchaserSettleLoc = pFact_PurchaserSettleLoc;
    }

    public Long getFact_SupplierSettleLoc() {
        return fact_SupplierSettleLoc;
    }

    public void setFact_SupplierSettleLoc(Long pFact_SupplierSettleLoc) {
        fact_SupplierSettleLoc = pFact_SupplierSettleLoc;
    }

    public Long getFact_RecordVersion() {
        return fact_RecordVersion;
    }

    public void setFact_RecordVersion(Long pFact_RecordVersion) {
        fact_RecordVersion = pFact_RecordVersion;
    }

    public String getFact_PurName() {
        return fact_PurName;
    }

    public void setFact_PurName(String pFact_PurName) {
        fact_PurName = pFact_PurName;
    }

    public String getFact_SupName() {
        return fact_SupName;
    }

    public void setFact_SupName(String pFact_SupName) {
        fact_SupName = pFact_SupName;
    }

    public String getFact_FinName() {
        return fact_FinName;
    }

    public void setFact_FinName(String pFact_FinName) {
        fact_FinName = pFact_FinName;
    }

    public Long getFact_Tenure() {
        return fact_Tenure;
    }

    public void setFact_Tenure(Long pFact_Tenure) {
        fact_Tenure = pFact_Tenure;
    }

    public Long getFact_FilterFromTenure() {
        return fact_FilterFromTenure;
    }

    public void setFact_FilterFromTenure(Long pFact_FilterFromTenure) {
        fact_FilterFromTenure = pFact_FilterFromTenure;
    }

    public Long getFact_FilterToTenure() {
        return fact_FilterToTenure;
    }

    public void setFact_FilterToTenure(Long pFact_FilterToTenure) {
        fact_FilterToTenure = pFact_FilterToTenure;
    }

    public String getFact_FilterSellerCategory() {
        return fact_FilterSellerCategory;
    }

    public void setFact_FilterSellerCategory(String pFact_FilterSellerCategory) {
        fact_FilterSellerCategory = pFact_FilterSellerCategory;
    }

    public String getFact_FilterMsmeStatus() {
        return fact_FilterMsmeStatus;
    }

    public void setFact_FilterMsmeStatus(String pFact_FilterMsmeStatus) {
        fact_FilterMsmeStatus = pFact_FilterMsmeStatus;
    }

    public BigDecimal getFact_CapRate() {
        return fact_CapRate;
    }

    public void setFact_CapRate(BigDecimal pFact_CapRate) {
        fact_CapRate = pFact_CapRate;
    }

    public BigDecimal getFact_FilterToCapRate() {
        return fact_FilterToCapRate;
    }

    public void setFact_FilterToCapRate(BigDecimal pFact_FilterToCapRate) {
        fact_FilterToCapRate = pFact_FilterToCapRate;
    }

    public Long getBid_FuId() {
        return bid_FuId;
    }

    public void setBid_FuId(Long pBid_FuId) {
        bid_FuId = pBid_FuId;
    }

    public String getBid_FinancierEntity() {
        return bid_FinancierEntity;
    }

    public void setBid_FinancierEntity(String pBid_FinancierEntity) {
        bid_FinancierEntity = pBid_FinancierEntity;
    }

    public Long getBid_FinancierAuId() {
        return bid_FinancierAuId;
    }

    public void setBid_FinancierAuId(Long pBid_FinancierAuId) {
        bid_FinancierAuId = pBid_FinancierAuId;
    }

    public String getBid_FinancierLoginId() {
        return bid_FinancierLoginId;
    }

    public void setBid_FinancierLoginId(String pBid_FinancierLoginId) {
        bid_FinancierLoginId = pBid_FinancierLoginId;
    }

    public String getBid_FinancierName() {
        return bid_FinancierName;
    }

    public void setBid_FinancierName(String pBid_FinancierName) {
        bid_FinancierName = pBid_FinancierName;
    }

    public BigDecimal getBid_Rate() {
        return bid_Rate;
    }

    public void setBid_Rate(BigDecimal pBid_Rate) {
        bid_Rate = pBid_Rate;
    }

    public BigDecimal getBid_Haircut() {
        return bid_Haircut;
    }

    public void setBid_Haircut(BigDecimal pBid_Haircut) {
        bid_Haircut = pBid_Haircut;
    }

    public Date getBid_ValidTill() {
        return bid_ValidTill;
    }

    public void setBid_ValidTill(Date pBid_ValidTill) {
        bid_ValidTill = pBid_ValidTill;
    }

    public Bid_Status getBid_Status() {
        return bid_Status;
    }

    public void setBid_Status(Bid_Status pBid_Status) {
        bid_Status = pBid_Status;
    }

    public String getBid_StatusRemarks() {
        return bid_StatusRemarks;
    }

    public void setBid_StatusRemarks(String pBid_StatusRemarks) {
        bid_StatusRemarks = pBid_StatusRemarks;
    }

    public Long getBid_Id() {
        return bid_Id;
    }

    public void setBid_Id(Long pBid_Id) {
        bid_Id = pBid_Id;
    }

    public Timestamp getBid_Timestamp() {
        return bid_Timestamp;
    }

    public void setBid_Timestamp(Timestamp pBid_Timestamp) {
        bid_Timestamp = pBid_Timestamp;
    }

    public Long getBid_LastAuId() {
        return bid_LastAuId;
    }

    public void setBid_LastAuId(Long pBid_LastAuId) {
        bid_LastAuId = pBid_LastAuId;
    }

    public String getBid_LastLoginId() {
        return bid_LastLoginId;
    }

    public void setBid_LastLoginId(String pBid_LastLoginId) {
        bid_LastLoginId = pBid_LastLoginId;
    }

    public Bid_BidType getBid_BidType() {
        return bid_BidType;
    }

    public void setBid_BidType(Bid_BidType pBid_BidType) {
        bid_BidType = pBid_BidType;
    }

    public BigDecimal getBid_ProvRate() {
        return bid_ProvRate;
    }

    public void setBid_ProvRate(BigDecimal pBid_ProvRate) {
        bid_ProvRate = pBid_ProvRate;
    }

    public BigDecimal getBid_ProvHaircut() {
        return bid_ProvHaircut;
    }

    public void setBid_ProvHaircut(BigDecimal pBid_ProvHaircut) {
        bid_ProvHaircut = pBid_ProvHaircut;
    }

    public Date getBid_ProvValidTill() {
        return bid_ProvValidTill;
    }

    public void setBid_ProvValidTill(Date pBid_ProvValidTill) {
        bid_ProvValidTill = pBid_ProvValidTill;
    }

    public BidType getBid_ProvBidType() {
        return bid_ProvBidType;
    }

    public void setBid_ProvBidType(BidType pBid_ProvBidType) {
        bid_ProvBidType = pBid_ProvBidType;
    }

    public Bid_ProvAction getBid_ProvAction() {
        return bid_ProvAction;
    }

    public void setBid_ProvAction(Bid_ProvAction pBid_ProvAction) {
        bid_ProvAction = pBid_ProvAction;
    }

    public Bid_AppStatus getBid_AppStatus() {
        return bid_AppStatus;
    }

    public void setBid_AppStatus(Bid_AppStatus pBid_AppStatus) {
        bid_AppStatus = pBid_AppStatus;
    }

    public String getBid_AppRemarks() {
        return bid_AppRemarks;
    }

    public void setBid_AppRemarks(String pBid_AppRemarks) {
        bid_AppRemarks = pBid_AppRemarks;
    }

    public Long getBid_CheckerAuId() {
        return bid_CheckerAuId;
    }

    public void setBid_CheckerAuId(Long pBid_CheckerAuId) {
        bid_CheckerAuId = pBid_CheckerAuId;
    }

    public BigDecimal getBid_LimitUtilised() {
        return bid_LimitUtilised;
    }

    public void setBid_LimitUtilised(BigDecimal pBid_LimitUtilised) {
        bid_LimitUtilised = pBid_LimitUtilised;
    }

    public BigDecimal getBid_BidLimitUtilised() {
        return bid_BidLimitUtilised;
    }

    public void setBid_BidLimitUtilised(BigDecimal pBid_BidLimitUtilised) {
        bid_BidLimitUtilised = pBid_BidLimitUtilised;
    }

    public String getBid_LimitIds() {
        return bid_LimitIds;
    }

    public void setBid_LimitIds(String pBid_LimitIds) {
        bid_LimitIds = pBid_LimitIds;
    }

    public CostCollectionLeg getBid_CostLeg() {
        return bid_CostLeg;
    }

    public void setBid_CostLeg(CostCollectionLeg pBid_CostLeg) {
        bid_CostLeg = pBid_CostLeg;
    }
    
    public Yes getInst_IsAggregatorCreated() {
    	if (inst_AggregatorAuId!=null && StringUtils.isNotEmpty(inst_AggregatorEntity)) {
	                	return Yes.Yes;
    	}
        return null;
    }

    public void setInst_IsAggregatorCreated(Yes pInst_IsAggregatorCreated) {
       
    }
    
    public String getAggregatorEntity() {
        return inst_AggregatorEntity;
    }

    public void setAggregatorEntity(String pInst_AggregatorEntity) {
    	inst_AggregatorEntity = pInst_AggregatorEntity;
    }

	public CostBearer getInst_Period1ChargeBearer() {
		return inst_Period1ChargeBearer;
	}

	public void setInst_Period1ChargeBearer(CostBearer pInst_Period1ChargeBearer) {
		inst_Period1ChargeBearer = pInst_Period1ChargeBearer;
	}

	public BigDecimal getInst_Period1ChargePercent() {
		return inst_Period1ChargePercent;
	}

	public void setInst_Period1ChargePercent(BigDecimal pInst_Period1ChargePercent) {
		inst_Period1ChargePercent = pInst_Period1ChargePercent;
	}

	public CostBearer getInst_Period2ChargeBearer() {
		return inst_Period2ChargeBearer;
	}

	public void setInst_Period2ChargeBearer(CostBearer pInst_Period2ChargeBearer) {
		inst_Period2ChargeBearer = pInst_Period2ChargeBearer;
	}

	public BigDecimal getInst_Period2ChargePercent() {
		return inst_Period2ChargePercent;
	}

	public void setInst_Period2ChargePercent(BigDecimal pInst_Period2ChargePercent) {
		inst_Period2ChargePercent = pInst_Period2ChargePercent;
	}
	
	public CostBearer getInst_Period3ChargeBearer() {
		return inst_Period3ChargeBearer;
	}

	public void setInst_Period3ChargeBearer(CostBearer pInst_Period3ChargeBearer) {
		inst_Period3ChargeBearer = pInst_Period3ChargeBearer;
	}

	public BigDecimal getInst_Period3ChargePercent() {
		return inst_Period3ChargePercent;
	}

	public void setInst_Period3ChargePercent(BigDecimal pInst_Period3ChargePercent) {
		inst_Period3ChargePercent = pInst_Period3ChargePercent;
	}

	public SplittingPoint getInst_SplittingPointCharge() {
		return inst_SplittingPointCharge;
	}

	public void setInst_SplittingPointCharge(SplittingPoint pInst_SplittingPointCharge) {
		inst_SplittingPointCharge = pInst_SplittingPointCharge;
	}

	public CostBearer getInst_PreSplittingCharge() {
		return inst_PreSplittingCharge;
	}

	public void setInst_PreSplittingCharge(CostBearer pInst_PreSplittingCharge) {
		inst_PreSplittingCharge = pInst_PreSplittingCharge;
	}

	public CostBearer getInst_PostSplittingCharge() {
		return inst_PostSplittingCharge;
	}

	public void setInst_PostSplittingCharge(CostBearer pInst_PostSplittingCharge) {
		inst_PostSplittingCharge = pInst_PostSplittingCharge;
	}

	public BigDecimal getInst_BuyerPercentCharge() {
		return inst_BuyerPercentCharge;
	}

	public void setInst_BuyerPercentCharge(BigDecimal pInst_BuyerPercentCharge) {
		inst_BuyerPercentCharge = pInst_BuyerPercentCharge;
	}

	public BigDecimal getInst_SellerPercentCharge() {
		return inst_SellerPercentCharge;
	}

	public void setInst_SellerPercentCharge(BigDecimal pInst_SellerPercentCharge) {
		inst_SellerPercentCharge = pInst_SellerPercentCharge;
	}

	public CostBearingType getInst_ChargeBearer() {
		return inst_ChargeBearer;
	}

	public void setInst_ChargeBearer(CostBearingType pInst_ChargeBearer) {
		inst_ChargeBearer = pInst_ChargeBearer;
	}
	
	public CostBearingType getFact_ChargeBearer() {
		return fact_ChargeBearer;
	}

	public void setFact_ChargeBearer(CostBearingType pFact_ChargeBearer) {
		fact_ChargeBearer = pFact_ChargeBearer;
	}
	
	public CostBearer getFact_Period1ChargeBearer() {
		return fact_Period1ChargeBearer;
	}

	public void setFact_Period1ChargeBearer(CostBearer pFact_Period1ChargeBearer) {
		fact_Period1ChargeBearer = pFact_Period1ChargeBearer;
	}

	public BigDecimal getFact_Period1ChargePercent() {
		return fact_Period1ChargePercent;
	}

	public void setFact_Period1ChargePercent(BigDecimal pFact_Period1ChargePercent) {
		fact_Period1ChargePercent = pFact_Period1ChargePercent;
	}

	public CostBearer getFact_Period2ChargeBearer() {
		return fact_Period2ChargeBearer;
	}

	public void setFact_Period2ChargeBearer(CostBearer pFact_Period2ChargeBearer) {
		fact_Period2ChargeBearer = pFact_Period2ChargeBearer;
	}

	public BigDecimal getFact_Period2ChargePercent() {
		return fact_Period2ChargePercent;
	}

	public void setFact_Period2ChargePercent(BigDecimal pFact_Period2ChargePercent) {
		fact_Period2ChargePercent = pFact_Period2ChargePercent;
	}
	
	public CostBearer getFact_Period3ChargeBearer() {
		return fact_Period3ChargeBearer;
	}

	public void setFact_Period3ChargeBearer(CostBearer pFact_Period3ChargeBearer) {
		fact_Period3ChargeBearer = pFact_Period3ChargeBearer;
	}

	public BigDecimal getFact_Period3ChargePercent() {
		return fact_Period3ChargePercent;
	}

	public void setFact_Period3ChargePercent(BigDecimal pFact_Period3ChargePercent) {
		fact_Period3ChargePercent = pFact_Period3ChargePercent;
	}

}