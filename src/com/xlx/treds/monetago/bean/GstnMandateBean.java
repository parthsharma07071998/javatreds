
package com.xlx.treds.monetago.bean;

import java.sql.Date;
import java.util.List;
import com.xlx.commonn.IKeyValEnumInterface;

public class GstnMandateBean {
    public enum Status implements IKeyValEnumInterface<String>{
        Completed("C","Completed"),Pending("P","Pending"),Not_Done("N","Not Done");
        
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
    private String supplierCode;
    private String gstn;
    private Status status;
    private Date statusDate;
    private String statusPayload;
    private List<String> emailList;
    private Long auId;
    private Long consentType;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(String pSupplierCode) {
        supplierCode = pSupplierCode;
    }

    public String getGstn() {
        return gstn;
    }

    public void setGstn(String pGstn) {
        gstn = pGstn;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date pStatusDate) {
        statusDate = pStatusDate;
    }

    public String getStatusPayload() {
        return statusPayload;
    }

    public void setStatusPayload(String pStatusPayload) {
        statusPayload = pStatusPayload;
    }

    public List<String> getEmailList() {
        return emailList;
    }
    public void setEmailList(List<String> pEmailList) {
        emailList = pEmailList;
    }
    public Long getAuId() {
        return auId;
    }

    public void setAuId(Long pAuId) {
        auId = pAuId;
    }

    public Long getConsentType() {
        return consentType;
    }
    public void setConsentType(Long pConsentType) {
        consentType = pConsentType;
    }
    
    public String getConsentTypeDesc() {
    	return getConsentTypeDesc(consentType);
    }
    public static String getConsentTypeDesc(Long pConsentType) {
    	if(pConsentType!=null){
    		if(pConsentType.equals(new Long(1))){
    			return "Manual - Single Invoice";
    		}else if(pConsentType.equals(new Long(2))){
    			return "Automatic - Specific Lender"; 
    		}else if(pConsentType.equals(new Long(3))){
    			return "Automatic - All Lenders"; 
    		}
    	}
        return "";
    }
 
    public void setConsentType(String pConsentTypeDesc) {
    }
    
    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

	@Override
	public String toString() {
		return super.toString();
	}
}