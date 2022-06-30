package com.xlx.treds.auction.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.auction.bean.ObligationBean.Type;

public class ObligationModificationRequestBean {
	
	public static final String FIELDGROUP_APPROVEREJECT = "approveReject";
	public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    
    public enum Status implements IKeyValEnumInterface<String>{
        Created("CRT","Created"),Sent("SNT","Sent"),Rejected("REJ","Rejected"),Approved("APP","Approved"),Applied("APL","Applied");
        
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
    private Long fuId;
    private Long partNumber;
    private Type type;
    private Date date;
    private Status status;
    private List<ObligationModificationDetailBean> obliModDetailsList;
    private Date createDate;
    private Long createrAuId;
    private String createrName;
    private String createrLogin;
    private Date approveRejectDate;
    private Long approveRejectAuId;
    private String approveRejectName;
    private String approveRejectLogin;
    private String remarks;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

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

    public Long getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(Long pPartNumber) {
        partNumber = pPartNumber;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public List<ObligationModificationDetailBean> getObliModDetailsList() {
        return obliModDetailsList;
    }

    public void setObliModDetailsList(List<ObligationModificationDetailBean> pObliModDetailsList) {
        obliModDetailsList = pObliModDetailsList;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date pCreateDate) {
        createDate = pCreateDate;
    }

    public Long getCreaterAuId() {
        return createrAuId;
    }

    public void setCreaterAuId(Long pCreaterAuId) {
        createrAuId = pCreaterAuId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String pCreaterName) {
        createrName = pCreaterName;
    }

    public String getCreaterLogin() {
        return createrLogin;
    }

    public void setCreaterLogin(String pCreaterLogin) {
        createrLogin = pCreaterLogin;
    }

    public Date getApproveRejectDate() {
        return approveRejectDate;
    }

    public void setApproveRejectDate(Date pApproveRejectDate) {
        approveRejectDate = pApproveRejectDate;
    }

    public Long getApproveRejectAuId() {
        return approveRejectAuId;
    }

    public void setApproveRejectAuId(Long pApproveRejectAuId) {
        approveRejectAuId = pApproveRejectAuId;
    }

    public String getApproveRejectName() {
        return approveRejectName;
    }

    public void setApproveRejectName(String pApproveRejectName) {
        approveRejectName = pApproveRejectName;
    }

    public String getApproveRejectLogin() {
        return approveRejectLogin;
    }

    public void setApproveRejectLogin(String pApproveRejectLogin) {
        approveRejectLogin = pApproveRejectLogin;
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
    
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String pRemarks) {
        remarks = pRemarks;
    }
 
    public YesNo getIsPreModification() {
    	boolean lRetVal = false;
    	if(obliModDetailsList!=null && obliModDetailsList.size() > 0){
    		lRetVal = ObligationBean.Status.Ready.equals(obliModDetailsList.get(0).getOrigStatus()) || ObligationBean.Status.Created.equals(obliModDetailsList.get(0).getOrigStatus()) ;
    	}
       	return lRetVal?YesNo.Yes:YesNo.No;
    }

    public void setIsPreModification(YesNo pIsPreModification) {
    }
    
    public boolean hasDetailsChanged(){
    	if(obliModDetailsList!=null){
    		for(ObligationModificationDetailBean lOMDBean : obliModDetailsList){
    			if(!lOMDBean.isNotModified()){
    				return true;
    			}
    		}
    	}
    	return false;
    }
}