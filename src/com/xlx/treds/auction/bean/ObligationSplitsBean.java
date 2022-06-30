package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;

public class ObligationSplitsBean implements IObligation {
	//
	private IObligation parentObligation;
	//
	public static final String FIELDGROUP_UPDATESETTLOR = "updateSettlor";
	public static final String FIELDGROUP_MARKPROCESSED = "markProcessed";
	public static final String FIELDGROUP_UPDATEPREGENERATIONMODIDICATION = "updatePreGenerationModification";
	public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
	public static final String FIELDGROUP_UPDATEUTR = "updateUtr";
	public static final String FIELDGROUP_MARKASSUCCESS= "updateMarkAsSuccess";
    public static final String FIELDGROUP_UPDATEPAYMENTREFNO = "updatePayRefNo";
	//
    private Long obid;
    private Long partNumber;
    private BigDecimal amount;
    private Status status;
    private Long pfId;
    private Long fileSeqNo;
    private Date settledDate;
    private BigDecimal settledAmount;
    private String paymentRefNo;
    private String respErrorCode;
    private String respRemarks;
    private String paymentSettlor;
    private Yes settlorProcessed;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private Long factUntId;
    private String transactionEntity;
    private TxnType transactionType;
    private Type legType;
    private String financierEntity;

    @Override
    public Long getId() {
        return obid;
    }

    @Override
    public void setId(Long pObid) {
        obid = pObid;
    }

    public Long getObid() {
        return obid;
    }

    public void setObid(Long pObid) {
        obid = pObid;
    }

    public Long getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Long pPartNumber) {
        partNumber = pPartNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal pAmount) {
        amount = pAmount;
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

	public String getPaymentSettlor() {
		return paymentSettlor;
	}

	public void setPaymentSettlor(String pPaymentSettlor) {
		paymentSettlor = pPaymentSettlor;
	}
	
	public Yes getSettlorProcessed() {
		return settlorProcessed;
	}

	public void setSettlorProcessed(Yes pSettlorProcessed) {
		settlorProcessed = pSettlorProcessed;
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

	@Override
	public Long getFuId() {
		if(parentObligation!=null){
			return parentObligation.getFuId();
		}
		return null;
	}

	@Override
	public void setFuId(Long pFuId) {
		if(parentObligation!=null){
			parentObligation.setFuId(pFuId);
		}
	}

	@Override
	public String getTxnEntity() {
		if(parentObligation!=null){
			return parentObligation.getTxnEntity();
		}
		return null;
	}

	@Override
	public void setTxnEntity(String pTxnEntity) {
		if(parentObligation!=null){
			parentObligation.setTxnEntity(pTxnEntity);
		}
	}

	@Override
	public String getTxnEntityName() {
		if(parentObligation!=null){
			return parentObligation.getTxnEntityName();
		}
		return null;
	}

	@Override
	public void setTxnEntityName(String pTxnEntityName) {
		if(parentObligation!=null){
			parentObligation.setTxnEntityName(pTxnEntityName);
		}
	}

	@Override
	public TxnType getTxnType() {
		if(parentObligation!=null){
			return parentObligation.getTxnType();
		}
		return null;
	}

	@Override
	public void setTxnType(TxnType pTxnType) {
		if(parentObligation!=null){
			parentObligation.setTxnType(pTxnType);
		}
	}

	@Override
	public Date getDate() {
		if(parentObligation!=null){
			return parentObligation.getDate();
		}
		return null;
	}

	@Override
	public void setDate(Date pDate) {
		if(parentObligation!=null){
			parentObligation.setDate(pDate);
		}
	}

	@Override
	public String getPayDetail1() {
		if(parentObligation!=null){
			return parentObligation.getPayDetail1();
		}
		return null;
	}

	@Override
	public void setPayDetail1(String pPayDetail1) {
		if(parentObligation!=null){
			parentObligation.setPayDetail1(pPayDetail1);
		}
	}

	@Override
	public String getPayDetail2() {
		if(parentObligation!=null){
			return parentObligation.getPayDetail2();
		}
		return null;
	}

	@Override
	public void setPayDetail2(String pPayDetail2) {
		if(parentObligation!=null){
			parentObligation.setPayDetail2(pPayDetail2);
		}
	}

	@Override
	public String getPayDetail3() {
		if(parentObligation!=null){
			return parentObligation.getPayDetail3();
		}
		return null;
	}

	@Override
	public void setPayDetail3(String pPayDetail3) {
		if(parentObligation!=null){
			parentObligation.setPayDetail3(pPayDetail3);
		}
	}

	@Override
	public String getPayDetail4() {
		if(parentObligation!=null){
			return parentObligation.getPayDetail4();
		}
		return null;
	}

	@Override
	public void setPayDetail4(String pPayDetail4) {
		if(parentObligation!=null){
			parentObligation.setPayDetail4(pPayDetail4);
		}
	}
	@Override
	public Long getSettlementCLId() {
		if(parentObligation!=null){
			return parentObligation.getSettlementCLId();
		}
		return null;
	}

	@Override
	public void setSettlementCLId(Long pSettlementCLId) {
		if(parentObligation!=null){
			parentObligation.setSettlementCLId(pSettlementCLId);
		}
	}
	
	@Override
	public Type getType() {
		if(parentObligation!=null){
			return parentObligation.getType();
		}
		return null;
	}

	@Override
	public void setType(Type pType) {
		if(parentObligation!=null){
			parentObligation.setType(pType);
		}
	}
	
	@Override
	public boolean isParentObligation() {
		return false;
	}

	@Override
	public void setParentObligation(IObligation pParentObligation) {
		parentObligation = pParentObligation;
	}
	@Override
	public IObligation getParentObligation() {
		return parentObligation;
	}

	@Override
	public String getUniqueObligationKey() {
		
		if(partNumber!=null){
			return obid+CommonUtilities.appendChars(3,partNumber.toString(),"0",false);
		}
		return obid.toString();
	}

	@Override
	public Long getTotalSplits() {
		if(parentObligation!=null){
			return parentObligation.getTotalSplits();
		}
		return null;
	}

	@Override
	public void setTotalSplits(Long pTotalSplits) {
		if(parentObligation!=null){
			parentObligation.setTotalSplits(pTotalSplits);
		}
	}

	@Override
	public void setCurrency(String pCurrency) {
		if(parentObligation!=null){
			parentObligation.setCurrency(pCurrency);
		}
	}

	@Override
	public String getCurrency() {
		if(parentObligation!=null){
			return parentObligation.getCurrency();
		}
		return null;
	}

	public String getTransactionEntity() {
		return transactionEntity;
	}

	public void setTransactionEntity(String pTransactionEntity) {
		transactionEntity = pTransactionEntity;
	}

	public Long getFactUntId() {
		return factUntId;
	}

	public void setFactUntId(Long pFactUntId) {
		factUntId = pFactUntId;
	}

	public TxnType getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(TxnType pTransactionType) {
		transactionType = pTransactionType;
	}

	public Type getLegType() {
		return legType;
	}

	public void setLegType(Type pLegType) {
		legType = pLegType;
	}

	public String getFinancierEntity() {
		return financierEntity;
	}

	public void setFinancierEntity(String pFinancierEntity) {
		financierEntity = pFinancierEntity;
	}

}