package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;

public class ObligationBean implements IObligation, Comparable<ObligationBean>{
    public static final String FIELDGROUP_GENERATE = "generate";
    public static final String FIELDGROUP_GENERATE_DIRECT = "generateDirect";
    public static final String FIELDGROUP_RETURN = "return";
    public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    public static final String FIELDGROUP_UPDATEBILLID = "updateBillId";
    public static final String FIELDGROUP_UPDATEPREGENERATIONMODIDICATION = "updatePreGenerationModification";
    public static final String FIELDGROUP_UPDATESETTLEDAMOUNT = "updateSettledAmount";
    public static final String FIELDGROUP_REMARKS= "remarks";
    
    public enum TxnType implements IKeyValEnumInterface<String>{
        Debit("D","Debit"),Credit("C","Credit");
        
        private final String code;
        private final String desc;
        private TxnType(String pCode, String pDesc) {
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
    public enum Type implements IKeyValEnumInterface<String>{
        Leg_1("L1","Leg 1"),Leg_2("L2","Leg 2"),Leg_3("L3","Leg 3");
        
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
    public enum Status implements IKeyValEnumInterface<String>{
        Created("CRT","Created"),Ready("RDY","Ready"),Sent("SNT","Sent"),Shifted("SFT","Shifted"),Success("SUC","Success"),Prov_Success("PSC","Prov Success")
        ,Returned("RET","Returned"),Failed("FL","Failed"), Cancelled("CNL","Cancelled"),Extended("EXT","Extended"),L2_Prov_Outside("L2P","L2 Prov Outside"),L2_Set_Outside("L2S","L2 Set Outside");
        
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
    public enum BillingStatus implements IKeyValEnumInterface<String>{
        Billed("B","Billed"),UnBilled("U","Un-billed");
        
        private final String code;
        private final String desc;
        private BillingStatus(String pCode, String pDesc) {
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
    private Long fuId;
    private Long bdId;
    private String txnEntity;
    private TxnType txnType;
    private Date date;
    private Date originalDate;
    private String currency;
    private BigDecimal amount;
    private BigDecimal originalAmount;
    private Type type;
    private String narration;
    private Status status;
    private Long pfId;
    private Long fileSeqNo;
    private String payDetail1;
    private String payDetail2;
    private String payDetail3;
    private String payDetail4;
	private Date settledDate;
    private BigDecimal settledAmount;
    private String paymentRefNo;
    private String respErrorCode;
    private String respRemarks;
    private Date filterToDate;
    private Long oldObligationId;
    private Long billId;
    private String salesCategory;
    private BillingStatus billingStatus;
    private Long settlementCLId;
    private Long extendedDays;
    private String settlementLocationName;
    private String settlementLocationCity;
    private Long totalSplits;
    private String paymentSettlor;
    private Long inId;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
	//this is used in settlement report
    private Date filterBidAcceptFromDate;
    private Date filterBidAcceptToDate;
    private boolean forSettlementReport;
    private YesNo allowExtension;
    private Yes isUpfrontOblig;
    private Yes isUpfront;
	private Long instrumentCount;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public Long getBdId() {
        return bdId;
    }

    public void setBdId(Long pBdId) {
        bdId = pBdId;
    }

    public String getTxnEntity() {
        return txnEntity;
    }

    public void setTxnEntity(String pTxnEntity) {
        txnEntity = pTxnEntity;
    }

    public String getTxnEntityName() {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        try {
            AppEntityBean lAppEntityBean = (AppEntityBean) lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{txnEntity});
            if (lAppEntityBean != null)
                return lAppEntityBean.getName();
        } catch (Exception lException) {
        }
        return null;
    }

    public void setTxnEntityName(String pTxnEntityName) {
    }

    public TxnType getTxnType() {
        return txnType;
    }

    public void setTxnType(TxnType pTxnType) {
        txnType = pTxnType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }
    public Date getOriginalDate() {
        return originalDate;
    }
    public void setOriginalDate(Date pOriginalDate) {
        originalDate = pOriginalDate;
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

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal pOriginalAmount) {
    	originalAmount = pOriginalAmount;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String pNarration) {
        narration = pNarration;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Long getPfId() {
        return pfId;
    }

    public void setPfId(Long pPfId) {
        pfId = pPfId;
    }

    public Long getFileSeqNo() {
        return fileSeqNo;
    }

    public void setFileSeqNo(Long pFileSeqNo) {
        fileSeqNo = pFileSeqNo;
    }

    public String getPayDetail1() {
        return payDetail1;
    }

    public void setPayDetail1(String pPayDetail1) {
        payDetail1 = pPayDetail1;
    }

    public String getPayDetail2() {
        return payDetail2;
    }

    public void setPayDetail2(String pPayDetail2) {
        payDetail2 = pPayDetail2;
    }

    public String getPayDetail3() {
        return payDetail3;
    }

    public void setPayDetail3(String pPayDetail3) {
        payDetail3 = pPayDetail3;
    }

    public String getPayDetail4() {
		return payDetail4;
	}

	public void setPayDetail4(String payDetail4) {
		this.payDetail4 = payDetail4;
	}
	
    public Date getSettledDate() {
        return settledDate;
    }

    public void setSettledDate(Date pSettledDate) {
        settledDate = pSettledDate;
    }

    public BigDecimal getSettledAmount() {
        return settledAmount;
    }

    public void setSettledAmount(BigDecimal pSettledAmount) {
        settledAmount = pSettledAmount;
    }

    public String getPaymentRefNo() {
        return paymentRefNo;
    }

    public void setPaymentRefNo(String pPaymentRefNo) {
        paymentRefNo = pPaymentRefNo;
    }

    public String getRespErrorCode() {
        return respErrorCode;
    }

    public void setRespErrorCode(String pRespErrorCode) {
        respErrorCode = pRespErrorCode;
    }

    public String getRespRemarks() {
        return respRemarks;
    }

    public void setRespRemarks(String pRespRemarks) {
        respRemarks = pRespRemarks;
    }

    public Date getFilterToDate() {
        return filterToDate;
    }

    public void setFilterToDate(Date pFilterToDate) {
        filterToDate = pFilterToDate;
    }

    public String getPan() {
        if (StringUtils.isBlank(txnEntity)) return null;
        return TredsHelper.getInstance().getCompanyPAN(txnEntity);
    }
    
    public void setPan(String pPan) {
    }
    
    public Long getOldObligationId() {
        return oldObligationId;
    }

    public void setOldObligationId(Long pOldObligationId) {
        oldObligationId = pOldObligationId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long pBillId) {
        billId = pBillId;
    }

    public String getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(String pSalesCategory) {
        salesCategory = pSalesCategory;
    }

    public BillingStatus getBillingStatus() {
    	if((AppConstants.DOMAIN_PLATFORM.equals(txnEntity) && billId != null) || BillingStatus.Billed.equals(billingStatus) )
    		return BillingStatus.Billed;
    	else if(AppConstants.DOMAIN_PLATFORM.equals(txnEntity)|| BillingStatus.UnBilled.equals(billingStatus) ) 
    		return BillingStatus.UnBilled;
    	return null;
    }

    public void setBillingStatus(BillingStatus pBillingStatus) {
    	billingStatus = pBillingStatus;
    }
    
	public Long getSettlementCLId() {
		return settlementCLId;
	}

	public void setSettlementCLId(Long pSettlementCLId) {
		settlementCLId = pSettlementCLId;
	}
	
    public Long getExtendedDays() {
        return extendedDays;
    }
    public void setExtendedDays(Long pExtendedDays) {
        extendedDays = pExtendedDays;
    }
	public String getSettlementLocationName() {
	    return settlementLocationName;
	}

    public void setSettlementLocationName(String pSettlementLocationName) {
        settlementLocationName = pSettlementLocationName;
    }
	
    public String getSettlementLocationCity() {
	    return settlementLocationCity;
	}

    public void setSettlementLocationCity(String pSettlementLocationCity) {
        settlementLocationCity = pSettlementLocationCity;
    }
    
	public Long getTotalSplits() {
		return totalSplits;
	}

	public void setTotalSplits(Long pTotalSplits) {
		totalSplits = pTotalSplits;
	}
	
	public String getPaymentSettlor() {
		return paymentSettlor;
	}

	public void setPaymentSettlor(String pPaymentSettlor) {
		paymentSettlor = pPaymentSettlor;
	}
	
	public Long getInId() {
        return inId;
    }

    public void setInId(Long pInId) {
        inId = pInId;
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
    

    public String getSalesCategoryDesc() {
    	return TredsHelper.getInstance().getSalesCategoryDescription(salesCategory);
    }
    

	@Override
	public Long getPartNumber() {
		return new Long(0);
	}

	@Override
	public void setPartNumber(Long pPartNumber) {
		
	}

	@Override
	public boolean isParentObligation() {
		return true;
	}

	@Override
	public void setParentObligation(IObligation pParentObligation) {
		
	}


	@Override
	public String getUniqueObligationKey() {
		return id.toString();
	}

	@Override
	public IObligation getParentObligation() {
		return null;
	}

	@Override
	public int compareTo(ObligationBean arg0) {
		int lCompare = 0;
		
		lCompare = arg0.getType().compareTo(this.type);
		if(lCompare==0){
			lCompare = arg0.getTxnType().compareTo(this.txnType);
			if(lCompare==0){
				lCompare = arg0.getAmount().compareTo(this.getAmount());
				lCompare *= -1;
			}else{
				lCompare *= -1;
			}
		}else{
			lCompare *= -1;
		}
		return lCompare;
	}

	public Date getFilterBidAcceptFromDate() {
		return filterBidAcceptFromDate;//this is used in settlement report
	}

	public void setFilterBidAcceptFromDate(Date filterBidAcceptFromDate) {
		this.filterBidAcceptFromDate = filterBidAcceptFromDate;//this is used in settlement report
	}

	public Date getFilterBidAcceptToDate() {
		return filterBidAcceptToDate;//this is used in settlement report
	}

	public void setFilterBidAcceptToDate(Date filterBidAcceptToDate) {
		this.filterBidAcceptToDate = filterBidAcceptToDate;//this is used in settlement report
	}

	public Long getInvoiceCount() {
		return recordVersion; //this is used in settlement report
	}

	public void setInvoiceCount(Long invoiceCount) {
		//do nothing
	}
	public Long getInstrumentCount() {
		return instrumentCount; //this is used in settlement report
	}
	public void setInstrumentCount(Long pInstrumentCount) {
		instrumentCount = pInstrumentCount;
	}

	public boolean isForSettlementReport() {
		return forSettlementReport;
	}

	public void setForSettlementReport(boolean forSettlementReport) {
		this.forSettlementReport = forSettlementReport;
	}

	public Timestamp getBidAcceptDateTime() {
        return recordCreateTime;//this is used in settlement report
    }

	public void setBidAcceptDateTime(Date bidAcceptDateTime) {
		//DO NOTHING
	}
	
	public YesNo getAllowExtension() {
        return allowExtension;
    }

    public void setAllowExtension(YesNo pAllowExtension) {
    	allowExtension = pAllowExtension;
    }
    
    public Yes getIsUpfrontOblig() {
        return isUpfrontOblig;
    }

    public void setIsUpfrontOblig(Yes pIsUpfrontOblig) {
    	isUpfrontOblig = pIsUpfrontOblig;
    }
    
    public Yes getIsUpfront() {
        return isUpfront;
    }

    public void setIsUpfront(Yes pIsUpfront) {
    	isUpfront = pIsUpfront;
    }
}