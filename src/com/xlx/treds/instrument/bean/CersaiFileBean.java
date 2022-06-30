package com.xlx.treds.instrument.bean;

import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.user.bean.AppUserBean;

public class CersaiFileBean {

	public static final String FIELDGROUP_INSERT = "insert";
	
    private Long id;
    private Date date;
    private String financier;
    private String fileName;
    private String storageFileName;
    private Long recordCount;
    private Long serialNumber;
    private Long generatedByAuId;
    private Timestamp generatedTime;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String pFinancier) {
        financier = pFinancier;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String pFileName) {
        fileName = pFileName;
    }

    public String getStorageFileName() {
        return storageFileName;
    }

    public void setStorageFileName(String pStorageFileName) {
        storageFileName = pStorageFileName;
    }

    public Long getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(Long pRecordCount) {
        recordCount = pRecordCount;
    }

    public Long getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Long pSerialNumber) {
        serialNumber = pSerialNumber;
    }

    public Long getGeneratedByAuId() {
        return generatedByAuId;
    }

    public void setGeneratedByAuId(Long pGeneratedByAuId) {
        generatedByAuId = pGeneratedByAuId;
    }

    public String getGeneratedByLoginId() {
    	if(generatedByAuId!=null){
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
            try {
                AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{generatedByAuId});
                if (lAppUserBean != null) {
                	return lAppUserBean.getLoginId();
                }
            } catch (Exception lException) {
            }
    	}
    	return "";
    }

    public Timestamp getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(Timestamp pGeneratedTime) {
        generatedTime = pGeneratedTime;
    }

    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

}