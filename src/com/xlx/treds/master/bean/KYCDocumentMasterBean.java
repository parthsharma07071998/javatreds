package com.xlx.treds.master.bean;

import java.sql.Timestamp;
import java.util.List;

import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class KYCDocumentMasterBean {
    public enum RepeatType implements IKeyValEnumInterface<String>{
        Promoter("P","Promoter"),Authorized_Person("AP","Authorized Person"),Administrator("A","Administrator"),Ultimate_Benificiery("UB","Ultimate Benificiery"),Individual("IN","Individual"),Entity("EN","Entity"),EntityKmp("KM","EntityKmp"),Bank("BN","Bank"),Location("LO","Location");
        
        private final String code;
        private final String desc;
        private RepeatType(String pCode, String pDesc) {
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
    private String documentType;
    private String documentCat;
    private List<String> constitutionList;
    private String constitutions;
    private Long serialNo;
    private List<String> documentList;
    private String documents;
    private RepeatType repeatType;
    private Long minSupplier;
    private Long maxSupplier;
    private Long minPurchaser;
    private Long maxPurchaser;
    private Long minFinancier;
    private Long maxFinancier;
    private YesNo softCopy;
    private YesNo hardCopy;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String pDocumentType) {
        documentType = pDocumentType;
    }

    public String getDocumentCat() {
        return documentCat;
    }

    public void setDocumentCat(String pDocumentCat) {
        documentCat = pDocumentCat;
    }

    public List<String> getConstitutionList() {
        return constitutionList;
    }

    public void setConstitutionList(List<String> pConstitutionList) {
        constitutionList = pConstitutionList;
    }

    public String getConstitutions() {
        if (constitutionList == null) return null;
        else {
            return new JsonBuilder(constitutionList).toString();
        }
    }

    public void setConstitutions(String pConstitutions) {
        if (pConstitutions == null)
            constitutionList = null;
        else {
            constitutionList = (List<String>)(new JsonSlurper().parseText(pConstitutions));
        }
    }

    public Long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Long pSerialNo) {
        serialNo = pSerialNo;
    }

    public List<String> getDocumentList() {
        return documentList;
    }

    public void setDocumentList(List<String> pDocumentList) {
        documentList = pDocumentList;
    }

    public String getDocuments() {
        if (documentList == null) return null;
        else {
            return new JsonBuilder(documentList).toString();
        }
    }

    public void setDocuments(String pDocuments) {
        if (pDocuments == null)
            documentList = null;
        else {
            documentList = (List<String>)(new JsonSlurper().parseText(pDocuments));
        }
    }

    public RepeatType getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(RepeatType pRepeatType) {
        repeatType = pRepeatType;
    }

    public Long getMinSupplier() {
        return minSupplier;
    }

    public void setMinSupplier(Long pMinSupplier) {
        minSupplier = pMinSupplier;
    }

    public Long getMaxSupplier() {
        return maxSupplier;
    }

    public void setMaxSupplier(Long pMaxSupplier) {
        maxSupplier = pMaxSupplier;
    }

    public Long getMinPurchaser() {
        return minPurchaser;
    }

    public void setMinPurchaser(Long pMinPurchaser) {
        minPurchaser = pMinPurchaser;
    }

    public Long getMaxPurchaser() {
        return maxPurchaser;
    }

    public void setMaxPurchaser(Long pMaxPurchaser) {
        maxPurchaser = pMaxPurchaser;
    }

    public Long getMinFinancier() {
        return minFinancier;
    }

    public void setMinFinancier(Long pMinFinancier) {
        minFinancier = pMinFinancier;
    }

    public Long getMaxFinancier() {
        return maxFinancier;
    }

    public void setMaxFinancier(Long pMaxFinancier) {
        maxFinancier = pMaxFinancier;
    }

    public YesNo getSoftCopy() {
        return softCopy;
    }

    public void setSoftCopy(YesNo pSoftCopy) {
        softCopy = pSoftCopy;
    }

    public YesNo getHardCopy() {
        return hardCopy;
    }

    public void setHardCopy(YesNo pHardCopy) {
        hardCopy = pHardCopy;
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

}