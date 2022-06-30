package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;


public interface IObligation {
    
	public Long getId();
    public void setId(Long pObid);

    public Long getPartNumber();
    public void setPartNumber(Long pPartNumber);

    public BigDecimal getAmount();
    public void setAmount(BigDecimal pAmount);

    public Long getPfId();
    public void setPfId(Long pPfId) ;

    public Long getFileSeqNo();
    public void setFileSeqNo(Long pFileSeqNo);

    public Date getSettledDate();
    public void setSettledDate(Date pSettledDate);
    
    public BigDecimal getSettledAmount();
    public void setSettledAmount(BigDecimal pSettledAmount);
    
    public String getPaymentRefNo();
    public void setPaymentRefNo(String pPaymentRefNo);

    public String getRespErrorCode();
    public void setRespErrorCode(String pRespErrorCode);

    public String getRespRemarks();
    public void setRespRemarks(String pRespRemarks);

    public Long getRecordUpdator();
    public void setRecordUpdator(Long pRecordUpdator);
    
    public Timestamp getRecordUpdateTime();
    public void setRecordUpdateTime(Timestamp pRecordUpdateTime);

    public Long getRecordVersion();
    public void setRecordVersion(Long pRecordVersion);
    
    public Status getStatus();
    public void setStatus(Status pStatus);

    public Long getFuId();
    public void setFuId(Long pFuId);
    
    public String getTxnEntity();
    public void setTxnEntity(String pTxnEntity);

    public String getTxnEntityName();
    public void setTxnEntityName(String pTxnEntityName);
    
    public TxnType getTxnType();
    public void setTxnType(TxnType pTxnType);

    public Date getDate();
    public void setDate(Date pDate);
    
    public String getPayDetail1();
    public void setPayDetail1(String pPayDetail1);
    
    public String getPayDetail2();
    public void setPayDetail2(String pPayDetail2);
    
    public String getPayDetail3();
    public void setPayDetail3(String pPayDetail3);
    
    public String getPayDetail4();
	public void setPayDetail4(String payDetail4);

	public Long getSettlementCLId();
	public void setSettlementCLId(Long pSettlementCLId);
	
	public Type getType();
    public void setType(Type pType);
    
	public Long getTotalSplits();
	public void setTotalSplits(Long pTotalSplits);
    //
    //
    public boolean isParentObligation();
    public String getUniqueObligationKey();
    //
    public void setParentObligation(IObligation pParentObligation);
    public IObligation getParentObligation();
	
	public void setCurrency(String pCurrency);
    public String getCurrency();
}
