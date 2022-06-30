package com.xlx.treds.master.bean;

import java.sql.Date;
import java.sql.Timestamp;

import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.treds.TredsHelper;

public class CircularBean {

	public static final int TABINDEX_LATEST = 1;
	public static final int TABINDEX_CIRCULARS = 2;
	public static final int TABINDEX_ARCHIVE = 3;
	public static final int TABINDEX_SIZE = 4;
	public static final String FIELDGROUP_ARCHIVE = "archive";
	
    private Long id;
    private String circularNo;
    private String title;
    private String description;
    private Date date;
    private String category;
    private YesNo purchaser;
    private YesNo supplier;
    private YesNo financier;
    private YesNo admin;
    private YesNo user;
    private String department;
    private Long displayAsNewForDays;
    private String fileName;
    private String storageFileName;
    private YesNo archive;
    private Long tab;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private boolean isLatest;
    private Long age;
    private Date filterToDate;
    private Date filterFromDate;
    private Long recordVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getCircularNo() {
        return circularNo;
    }

    public void setCircularNo(String pCircularNo) {
        circularNo = pCircularNo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String pTitle) {
        title = pTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String pDescription) {
        description = pDescription;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String pCategory) {
        category = pCategory;
    }

    public YesNo getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(YesNo pPurchaser) {
        purchaser = pPurchaser;
    }

    public YesNo getSupplier() {
        return supplier;
    }

    public void setSupplier(YesNo pSupplier) {
        supplier = pSupplier;
    }

    public YesNo getFinancier() {
        return financier;
    }

    public void setFinancier(YesNo pFinancier) {
        financier = pFinancier;
    }

    public YesNo getAdmin() {
        return admin;
    }

    public void setAdmin(YesNo pAdmin) {
        admin = pAdmin;
    }

    public YesNo getUser() {
        return user;
    }

    public void setUser(YesNo pUser) {
        user = pUser;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String pDepartment) {
        department = pDepartment;
    }

    public Long getDisplayAsNewForDays() {
        return displayAsNewForDays;
    }

    public void setDisplayAsNewForDays(Long pDisplayAsNewForDays) {
        displayAsNewForDays = pDisplayAsNewForDays;
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

    public YesNo getArchive() {
        return archive;
    }

    public void setArchive(YesNo pArchive) {
        archive = pArchive;
    }

    public Long getTab() {
        return tab ;
    }

    public void setTab(Long pTab) {
        tab = pTab;
    }

    public Long getAge() {
    	if(age != null){
    		return age; //this works for query computation
    	}
    	//this if for post bean generation
        return TredsHelper.getInstance().getDiffInDays(TredsHelper.getInstance().getBusinessDate(), date); 
    }

    public void setAge(Long pAge) {
    	age = pAge;
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
    

    public Date getFilterToDate() {
        return filterToDate;
    }

    public void setFilterToDate(Date pFilterToDate) {
        filterToDate = pFilterToDate;
    }

    public Date getFilterFromDate() {
        return filterFromDate;
    }

    public void setFilterFromDate(Date pFilterFromDate) {
        filterFromDate = pFilterFromDate;
    }
   
    public Long getRecordVersion() {
        return recordVersion;
    }

    public void setRecordVersion(Long pRecordVersion) {
        recordVersion = pRecordVersion;
    }

}