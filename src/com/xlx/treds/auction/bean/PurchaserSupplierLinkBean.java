package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.AutoAcceptableBidTypes;
import com.xlx.treds.AppConstants.AutoConvert;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;

public class PurchaserSupplierLinkBean {
	private static final Logger logger = LoggerFactory.getLogger(PurchaserSupplierLinkBean.class);
	
    public static final String FIELDGROUP_UPDATESUPPLIER = "updateSupplier";
    public static final String FIELDGROUP_UPDATEPURCHASER = "updatePurchaser";
    public static final String FIELDGROUP_UPDATEPURCHASERAPPROVALSTATUS = "updatePurchaserApprovalStatus";
    public static final String FIELDGROUP_UPDATESUPPLIERAPPROVALSTATUS = "updateSupplierApprovalStatus";
    public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    public static final String FIELDGROUP_UPDATESUPPLIREREFCODE = "updateSupplierRefCode";
    public static final String FIELDGROUP_UPDATEPURCHASEREFCODE = "updatePurchaserRefCode";
    public static final String FIELDGROUP_EXPORTFIELDS = "exportFields";
    public static final String FIELDGROUP_RESPFIELDSAPI= "respFieldsApi";
    public static final String FIELDGROUP_UPDATEPLATFORMSTATUS= "updatePlatformStatus";
    //
    public static final Long TABINDEX_INBOX = new Long(0);
    public static final Long TABINDEX_ACTIVE = new Long(1);
    public static final Long TABINDEX_PENDINGAPPROVAL = new Long(2);
    public static final Long TABINDEX_SUSPENDED = new Long(3);
	//
    public enum SplittingPoint implements IKeyValEnumInterface<String>{
        Statutory_Due_Date("SDD","Statutory Due Date"),Invoice_Due_Date("IDD","Invoice Due Date");
        
        private final String code;
        private final String desc;
        private SplittingPoint(String pCode, String pDesc) {
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
        Active("ACT","Active"),Suspended_by_Buyer("SBB","Suspended by Buyer"),Suspended_by_Seller("SBS","Suspended by Seller"),Suspended_by_Platform("SBP","Suspended by Platform");
        
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
    public enum ApprovalStatus implements IKeyValEnumInterface<String>{
        Draft("DFT","Draft"),Submitted("SUB","Submitted"),Returned("RET","Returned"),Approved("APP","Approved"),Deleted("DEL","Deleted"),Suspended("SPN","Suspended"),ReActivate("ACT","ReActivate"),Withdraw("WDR","Withdraw");
        
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
    
    public enum InstrumentCreation implements IKeyValEnumInterface<String>{
        Purchaser("P","Purchaser"),Supplier("S","Supplier"),Both("B","Both");
        
        private final String code;
        private final String desc;
        private InstrumentCreation(String pCode, String pDesc) {
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
    
    public enum PlatformStatus implements IKeyValEnumInterface<String>{
        Active("ACT","Active"),Suspended("SUS","Suspended");
        
        private final String code;
        private final String desc;
        private PlatformStatus(String pCode, String pDesc) {
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
    
    private String supplier;
    private String purchaser;
    private String supplierPurchaserRef;
    private Long creditPeriod;
    private Long extendedCreditPeriod;
    private String purchaserSupplierRef;
    private CostBearer period1CostBearer;
    private BigDecimal period1CostPercent;
    private CostBearer period2CostBearer;
    private BigDecimal period2CostPercent;
    private CostBearer period3CostBearer;
    private BigDecimal period3CostPercent;
    private CostBearer bidAcceptingEntityType;
    private CostBearingType costBearingType;
    private SplittingPoint splittingPoint;
    private CostBearer preSplittingCostBearer;
    private CostBearer postSplittingCostBearer;
    private BigDecimal buyerPercent;
    private BigDecimal sellerPercent;
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
    private YesNo settleLeg3Flag;
    private AutoAcceptBid autoAccept;
    private AutoAcceptableBidTypes autoAcceptableBidTypes;
    private AutoConvert autoConvert;
    private YesNo purchaserAutoApproveInvoice;
    private YesNo sellerAutoApproveInvoice;
    private Status status;
    private ApprovalStatus approvalStatus;
    private YesNo invoiceMandatory;
    private InstrumentCreation instrumentCreation;
    private String remarks;
    private Yes inWorkFlow;
    private BigDecimal cashDiscountPercent;
    private BigDecimal haircutPercent;
    private Long tab;
    private Yes fetchSupGstn;
    private PlatformStatus platformStatus;
	private YesNo relationFlag;
    private String platformReasonCode;
    private String relationDoc;
    private Date relationEffectiveDate;
    private String platformRemarks;
    private String supGstn;
    private String supPan;
    private YesNo buyerTds;
    private YesNo sellerTds;
    private BigDecimal buyerTdsPercent;
    private BigDecimal sellerTdsPercent;
    private YesNo authorizeRxil;

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String pSupplier) {
        supplier = pSupplier;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public String getSupplierPurchaserRef() {
        return supplierPurchaserRef;
    }

    public void setSupplierPurchaserRef(String pSupplierPurchaserRef) {
        supplierPurchaserRef = pSupplierPurchaserRef;
    }

    public Long getCreditPeriod() {
        return creditPeriod;
    }

    public void setCreditPeriod(Long pCreditPeriod) {
        creditPeriod = pCreditPeriod;
    }

    public Long getExtendedCreditPeriod() {
        return extendedCreditPeriod;
    }

    public void setExtendedCreditPeriod(Long pExtendedCreditPeriod) {
        extendedCreditPeriod = pExtendedCreditPeriod;
    }

    public String getPurchaserSupplierRef() {
        return purchaserSupplierRef;
    }

    public void setPurchaserSupplierRef(String pPurchaserSupplierRef) {
        purchaserSupplierRef = pPurchaserSupplierRef;
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

    public CostBearer getBidAcceptingEntityType() {
        return bidAcceptingEntityType;
    }

    public void setBidAcceptingEntityType(CostBearer pBidAcceptingEntityType) {
        bidAcceptingEntityType = pBidAcceptingEntityType;
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

    public YesNo getPurchaserAutoApproveInvoice() {
        return purchaserAutoApproveInvoice;
    }

    public void setPurchaserAutoApproveInvoice(YesNo pPurchaserAutoApproveInvoice) {
        purchaserAutoApproveInvoice = pPurchaserAutoApproveInvoice;
    }

    public YesNo getSellerAutoApproveInvoice() {
        return sellerAutoApproveInvoice;
    }

    public void setSellerAutoApproveInvoice(YesNo pSellerAutoApproveInvoice) {
        sellerAutoApproveInvoice = pSellerAutoApproveInvoice;
    }

    public Status getStatus() {
    	if(PlatformStatus.Suspended.equals(platformStatus)){
    		return Status.Suspended_by_Platform;
    	}
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus pApprovalStatus) {
        approvalStatus = pApprovalStatus;
    }

    public YesNo getInvoiceMandatory() {
        return invoiceMandatory;
    }

    public void setInvoiceMandatory(YesNo pInvoiceMandatory) {
        invoiceMandatory = pInvoiceMandatory;
    }

    public InstrumentCreation getInstrumentCreation() {
        return instrumentCreation;
    }

    public void setInstrumentCreation(InstrumentCreation pInstrumentCreation) {
        instrumentCreation = pInstrumentCreation;
    }
    
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
    }

    public Yes getInWorkFlow() {
        return inWorkFlow;
    }

    public void setInWorkFlow(Yes pInWorkFlow) {
        inWorkFlow = pInWorkFlow;
    }

    public BigDecimal getCashDiscountPercent() {
        return cashDiscountPercent;
    }

    public void setCashDiscountPercent(BigDecimal pCashDiscountPercent) {
        cashDiscountPercent = pCashDiscountPercent;
    }

    public BigDecimal getHaircutPercent() {
        return haircutPercent;
    }

    public void setHaircutPercent(BigDecimal pHaircutPercent) {
        haircutPercent = pHaircutPercent;
    }

    public Long getTab() {
        return tab;
    }

    public void setTab(Long pTab) {
        tab = pTab;
    }

    public String getSupName() {
        if (supplier == null) 
            return null;
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{supplier});
            if (lAppEntityBean != null)
                return lAppEntityBean.getName();
        } catch (Exception lException) {
    }
        return null;
    }
    public void setSupName(String pSupName) {
        
    }

    public String getPurName() {
        if (purchaser == null) 
            return null;
        try {
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
            AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{purchaser});
            if (lAppEntityBean != null)
                return lAppEntityBean.getName();
        } catch (Exception lException) {
    }
        return null;
    }
    public void setPurName(String pPurName) {
        
    }
    
    public boolean isCostBearerChangeValid(CostBearingType pNewCostBearingType){
    	boolean lOldTypeSharing = !(CostBearingType.Buyer.equals(costBearingType) || CostBearingType.Seller.equals(costBearingType)) ;
    	boolean lNewTypeSharing = !(CostBearingType.Buyer.equals(pNewCostBearingType) || CostBearingType.Seller.equals(pNewCostBearingType)) ;
    	
    	if(lOldTypeSharing){
    		if(!lNewTypeSharing)
    			return true;
    		else {
    			return costBearingType.equals(pNewCostBearingType); //if sharing then should remain same
    		}
    	}else{
    		// !lOldTypeSharing
    		if(lNewTypeSharing)
    			return false;
    		else 
    			return true;
    	}
    }
    
    public void populateNonDatabaseFields(){
    	costBearingType = null;
    	buyerPercent = null;
    	sellerPercent = null;
    	splittingPoint = null;
    	preSplittingCostBearer = null;
    	postSplittingCostBearer = null;
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
    	//
    	if(this.getPeriod1ChargePercent()==null || this.getPeriod2ChargePercent() == null || this.getPeriod3ChargePercent() == null){
			logger.info("Cannot determine NonDbFields of PSLinkBean.");
    		return;
    	}
    	//
    	if(CostBearer.Buyer.equals(this.getPeriod1CostBearer()) &&
    		this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) && 
			this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer()) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0)){
	
    		if(this.getPeriod1CostPercent().compareTo(BigDecimal.valueOf(100))==0){
    			costBearingType = CostBearingType.Buyer;
    			buyerPercent =  new BigDecimal(100);
    			sellerPercent = null;
			}else if (this.getPeriod1CostPercent().compareTo(new BigDecimal(100)) == -1){
				costBearingType = CostBearingType.Percentage_Split;
				buyerPercent = this.getPeriod1CostPercent();
				sellerPercent = (new BigDecimal(100)).subtract(this.getPeriod1CostPercent());
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if(CostBearer.Seller.equals(this.getPeriod1CostBearer()) &&
			this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) && 
			this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer()) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0)){
			if(this.getPeriod1CostPercent().compareTo(BigDecimal.valueOf(100))==0){
				costBearingType = CostBearingType.Seller;
    			buyerPercent =  null;
    			sellerPercent = new BigDecimal(100);
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
			}
		}else if((this.getPeriod1CostPercent().compareTo(this.getPeriod2CostPercent())==0) &&
			(this.getPeriod1CostPercent().compareTo(this.getPeriod3CostPercent())==0)){
			
			if(this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) &&
					!this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer())){
    			costBearingType = CostBearingType.Periodical_Split;
				splittingPoint = SplittingPoint.Invoice_Due_Date;
				preSplittingCostBearer = this.getPeriod1CostBearer();
				postSplittingCostBearer = this.getPeriod3CostBearer();
			}else if(!this.getPeriod1CostBearer().equals(this.getPeriod2CostBearer()) &&
				this.getPeriod2CostBearer().equals(this.getPeriod3CostBearer())){
    			costBearingType = CostBearingType.Periodical_Split;
				splittingPoint = SplittingPoint.Statutory_Due_Date;
				preSplittingCostBearer = this.getPeriod1CostBearer();
				postSplittingCostBearer = this.getPeriod3CostBearer();
			}else{
				logger.info("Cannot determine NonDbFields of PSLinkBean.");
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
    
    public String getSalesCategory() {
    	if(CommonUtilities.hasValue(this.getSupplier())){
            try {
                MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
                AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{supplier});
                if (lAppEntityBean != null)
                    return lAppEntityBean.getSalesCategory();
            } catch (Exception lException) {
            }
    	}
    	return null;
    }
    
    public void setSalesCategory(String pSalesCategory){
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
    	}else if(CostBearingType.Seller.equals(lCostBearingType)){
			//p1cb=p2cb=p3cb=S;
			//p1%=pw%=p3%=100
    		this.setPeriod1CostBearer(CostBearer.Seller);
    		this.setPeriod2CostBearer(CostBearer.Seller);
    		this.setPeriod3CostBearer(CostBearer.Seller);
    		this.setPeriod1CostPercent(new BigDecimal(100));
    		this.setPeriod2CostPercent(new BigDecimal(100));
    		this.setPeriod3CostPercent(new BigDecimal(100));
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
    	}else if(CostBearingType.Percentage_Split.equals(lCostBearingType)){    		
			//p1cb=p2cb=p3cb=B;
			//p1%=pw%=p3%=buyerPercent
    		this.setPeriod1CostBearer(CostBearer.Buyer);
    		this.setPeriod2CostBearer(CostBearer.Buyer);
    		this.setPeriod3CostBearer(CostBearer.Buyer);
    		this.setPeriod1CostPercent(this.getBuyerPercent());
    		this.setPeriod2CostPercent(this.getBuyerPercent());
    		this.setPeriod3CostPercent(this.getBuyerPercent());
    		
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

	public Yes getSupportsInstCreationKeys() {
		if(StringUtils.isNotEmpty(purchaser)) {
			if(TredsHelper.getInstance().supportsInstrumentKeys(purchaser)) {
				return Yes.Yes;
			}
		}
		return null;
	}

	public void setSupportsInstCreationKeys(Yes supportsInstCreationKeys) {
	}
	
    public Yes getFetchSupGstn() {
        return fetchSupGstn;
    }

    public void setFetchSupGstn(Yes pFetchSupGstn) {
        fetchSupGstn = pFetchSupGstn;
    }
    
    public PlatformStatus getPlatformStatus() {
        return platformStatus;
    }

    public void setPlatformStatus(PlatformStatus pPlatformStatus) {
        platformStatus = pPlatformStatus;
    }

    public YesNo getRelationFlag() {
        return relationFlag;
    }

    public void setRelationFlag(YesNo pRelationFlag) {
        relationFlag = pRelationFlag;
    }

    public String getPlatformReasonCode() {
        return platformReasonCode;
    }

    public void setPlatformReasonCode(String pPlatformReasonCode) {
        platformReasonCode = pPlatformReasonCode;
    }

    public String getRelationDoc() {
        return relationDoc;
    }

    public void setRelationDoc(String pRelationDoc) {
        relationDoc = pRelationDoc;
    }

    public Date getRelationEffectiveDate() {
        return relationEffectiveDate;
    }

    public void setRelationEffectiveDate(Date pRelationEffectiveDate) {
        relationEffectiveDate = pRelationEffectiveDate;
    }
    
    public String getPlatformRemarks() {
		return platformRemarks;
	}

	public void setPlatformRemarks(String platformRemarks) {
		this.platformRemarks = platformRemarks;
	}
	
	public String getRelationShipAsString(){
		StringBuilder lRetVal = new StringBuilder();
		if(YesNo.Yes.equals(relationFlag)){
			lRetVal.append(relationFlag.getCode());
		}else{
			lRetVal.append(YesNo.No.getCode());
		}
		lRetVal.append(CommonConstants.COMMA);
		if(relationEffectiveDate!=null){
			lRetVal.append(FormatHelper.getDisplay("DD-MM-YYYY",relationEffectiveDate));
		}else{
			lRetVal.append(YesNo.No.getCode());
		}
		lRetVal.append(CommonConstants.COMMA);
		if(StringUtils.isNotEmpty(relationDoc)){
			lRetVal.append(relationDoc);
		}
		return lRetVal.toString();
	}

	public String getSupGstn() {
		return supGstn;
	}

	public void setSupGstn(String supGstn) {
		this.supGstn = supGstn;
	}

	public String getSupPan() {
		return supPan;
	}

	public void setSupPan(String supPan) {
		this.supPan = supPan;
	}
    public YesNo getBuyerTds() {
        return buyerTds;
    }

    public void setBuyerTds(YesNo pBuyerTds) {
        buyerTds = pBuyerTds;
    }

    public YesNo getSellerTds() {
        return sellerTds;
    }

    public void setSellerTds(YesNo pSellerTds) {
        sellerTds = pSellerTds;
    }

    public BigDecimal getBuyerTdsPercent() {
        return buyerTdsPercent;
    }

    public void setBuyerTdsPercent(BigDecimal pBuyerTdsPercent) {
        buyerTdsPercent = pBuyerTdsPercent;
    }

    public BigDecimal getSellerTdsPercent() {
        return sellerTdsPercent;
    }

    public void setSellerTdsPercent(BigDecimal pSellerTdsPercent) {
        sellerTdsPercent = pSellerTdsPercent;
    }
    
    public YesNo getAuthorizeRxil() {
        return authorizeRxil;
    }

    public void setAuthorizeRxil(YesNo pAuthorizeRxil) {
        authorizeRxil = pAuthorizeRxil;
    }
    
}