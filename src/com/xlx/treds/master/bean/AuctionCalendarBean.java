package com.xlx.treds.master.bean;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xlx.common.memdb.IMemoryTableRow;
import com.xlx.common.user.bean.UserFormatsBean;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanMeta;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class AuctionCalendarBean implements IMemoryTableRow {
    public enum Status implements IKeyValEnumInterface<String>{
        Pending("P","Pending"),Bidding("B","Bidding"),Closed("C","Closed");
        
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
    public enum AuctionDay implements IKeyValEnumInterface<String>{
        Today("0","Today"),Tomorrow("1","Tomorrow");
        
        private final String code;
        private final String desc;
        private AuctionDay(String pCode, String pDesc) {
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
    private String type;
    private Date date;
    private Timestamp bidStartTime;
    private Timestamp bidEndTime;
    private List<ConfirmationWindowBean> confWinList;
    private YesNo active;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;
    private AuctionDay auctionDay;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getType() {
        return type;
    }

    public void setType(String pType) {
        type = pType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date pDate) {
        date = pDate;
    }

    public Timestamp getBidStartTime() {
        return bidStartTime;
    }

    public void setBidStartTime(Timestamp pBidStartTime) {
        bidStartTime = pBidStartTime;
    }

    public Timestamp getBidEndTime() {
        return bidEndTime;
    }

    public void setBidEndTime(Timestamp pBidEndTime) {
        bidEndTime = pBidEndTime;
    }

    public Status getStatus() {
        long lCurrentTime = System.currentTimeMillis(); 
        if (lCurrentTime < bidStartTime.getTime())
            return Status.Pending;
        else if (lCurrentTime <= bidEndTime.getTime())
            return Status.Bidding;
        else
            return Status.Closed;
    }

    public void setStatus(Status pStatus) {
    }

    public List<ConfirmationWindowBean> getConfWinList() {
        return confWinList;
    }

    public void setConfWinList(List<ConfirmationWindowBean> pConfWinList) {
        confWinList = pConfWinList;
    }

    public String getConfWindows() {
        if (confWinList == null)
            return null;
        List<Map<String,Object>> lList = new ArrayList<Map<String, Object>>();
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ConfirmationWindowBean.class);
        for (ConfirmationWindowBean lConfirmationWindowBean : confWinList) {
            lList.add(lBeanMeta.formatAsMap(lConfirmationWindowBean));
        }
        return new JsonBuilder(lList).toString();
    }

    public void setConfWindows(String pConfWindows) {
        if (pConfWindows == null)
            confWinList = null;
        else {
            List<Map<String,Object>> lList = (List<Map<String,Object>>)(new JsonSlurper().parseText(pConfWindows));
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ConfirmationWindowBean.class);
            confWinList = new ArrayList<ConfirmationWindowBean>();
            for (Map<String, Object> lMap : lList) {
                ConfirmationWindowBean lConfirmationWindowBean =  new ConfirmationWindowBean();
                lBeanMeta.validateAndParse(lConfirmationWindowBean, lMap, null);
                confWinList.add(lConfirmationWindowBean);
            }
        }
    }

    public YesNo getActive() {
        return active;
    }

    public void setActive(YesNo pActive) {
        active = pActive;
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
    
    public Time getBidStartTimeTime() {
        return bidStartTime==null?null:new Time(bidStartTime.getTime());
    }

    public void setBidStartTimeTime(Time pBidStartTimeTime) {
    }

    public Time getBidEndTimeTime() {
        return bidEndTime==null?null:new Time(bidEndTime.getTime());
    }

    public void setBidEndTimeTime(Time pBidEndTimeTime) {
    }

    public AuctionDay getAuctionDay() {
        return auctionDay;
    }

    public void setAuctionDay(AuctionDay pAuctionDay) {
        auctionDay = pAuctionDay;
    }

    public static final String ENTITY_NAME = "AUCTIONCALENDAR";
    public static final String f_Id = "ACId";
    public static final String f_TypeAuctionDay = "ACTypeAuctionDay";
    public static final int idx_Id = 0;
    public static final int idx_Type = 1;
    public static final int idx_AuctionDay = 2;
    
    private AuctionCalendarBean backupBean;
    
    public String getEntityName() {
        return ENTITY_NAME;
    }

    public void fill(String pMessage, UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
        lBeanMeta.validateAndParse(this, pMessage, null);
    }

    public String getAsString(UserFormatsBean pUserFormatsBean) {
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
        return lBeanMeta.formatAsJson(this);
    }

    public Object getColumnValue(String pColumnName) {
        return null;
    }

    public Object getColumnValue(int pColumnIndex) {
        if (pColumnIndex == idx_Id)
            return id;
        else if (pColumnIndex == idx_Type)
            return type;
        else if (pColumnIndex == idx_AuctionDay)
            return auctionDay;
        else
            return null;
    }

    public void backup() {
        if (backupBean == null)
            backupBean = new AuctionCalendarBean();
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
        lBeanMeta.copyBean(this, backupBean);
    }

    public void rollback() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
            lBeanMeta.copyBean(backupBean, this);
        }
    }

    public void commit() {
        if (backupBean != null) {
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AuctionCalendarBean.class);
            lBeanMeta.clearBean(backupBean);
        }
    }

}