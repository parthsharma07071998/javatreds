package com.xlx.treds.master.bean;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.xlx.common.memdb.IMemoryTableRow;
import com.xlx.common.user.bean.UserFormatsBean;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanMeta;

public class HolidayMasterBean implements IMemoryTableRow {
    public static final String ENTITY_NAME = "HOLIDAYMASTER";
    
    public static final String f_Id = "id";
    public static final String f_Date = "date";
    
    public static final int idx_Id = 0;
    public static final int idx_Date = 1;
    
    private static final SimpleDateFormat formatterForWeekDay = new SimpleDateFormat("EEEE");
    
    public enum Type implements IKeyValEnumInterface<String>{
        Both("B","Both"),Trading("T","Trading"),Clearing("C","Clearing");
        
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

    private Long id;
    private Date date;
    private String desc;
    private Type type;
    private YesNo disableShifting;
    private Date fromDate;
    private Date toDate;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String pDesc) {
        desc = pDesc;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type pType) {
        type = pType;
    }
    
    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date pFromDate) {
        fromDate = pFromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date pToDate) {
        toDate = pToDate;
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

    public String getDay() {
        return formatterForWeekDay.format(date);
    }

    public void setDay(String pDay) {
	}

    public String getEntityName() {
        return ENTITY_NAME;
    }

    public void fill(String pMessage, UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
        lBeanMeta.validateAndParse(this, pMessage, null, null);
    }

    public String getAsString(UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
        return lBeanMeta.formatAsJson(this);
    }

    public Object getColumnValue(String pColumnName) {
        return null;
    }

    public Object getColumnValue(int pColumnIndex) {
        switch(pColumnIndex) {
            case idx_Id : return id;
            case idx_Date : return date;
        }
        return null;
    }
    private HolidayMasterBean backupBean;

    public void backup() {
        if (backupBean == null)
            backupBean = new HolidayMasterBean();
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
        lBeanMeta.copyBean(this, backupBean);
    }

    public void rollback() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
            lBeanMeta.copyBean(backupBean, this);
        }
    }

    public void commit() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
            lBeanMeta.clearBean(backupBean);
        }
    }
    
    public YesNo getDisableShifting() {
        return disableShifting;
    }

    public void setDisableShifting(YesNo pDisableShifting) {
    	disableShifting = pDisableShifting;
    }
    
}