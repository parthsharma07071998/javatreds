package com.xlx.treds.auction.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class ObligationExtensionPenaltyBean {

    private String financier;
    private String purchaser;
    private YesNo allowExtension;
    private Long maxExtension;
    private List<PenaltyDetailBean> penaltyList;
    private Long recordCreator;
    private Timestamp recordCreateTime;
    private Long recordUpdator;
    private Timestamp recordUpdateTime;
    private Long recordVersion;

    public String getFinancier() {
        return financier;
    }

    public void setFinancier(String pFinancier) {
        financier = pFinancier;
    }

    public String getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(String pPurchaser) {
        purchaser = pPurchaser;
    }

    public YesNo getAllowExtension() {
        return allowExtension;
    }

    public void setAllowExtension(YesNo pAllowExtension) {
        allowExtension = pAllowExtension;
    }

    public Long getMaxExtension() {
        return maxExtension;
    }

    public void setMaxExtension(Long pMaxExtension) {
        maxExtension = pMaxExtension;
    }

    public String getPenalty() {
        if (penaltyList == null)
            return null;
        BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PenaltyDetailBean.class);
        List<Object> lList = new ArrayList<Object>();
        for (PenaltyDetailBean lPenaltyDetailBean : penaltyList) {
            lList.add(lBeanMeta.formatAsMap(lPenaltyDetailBean));
        }
        return new JsonBuilder(lList).toString();
    }

    public void setPenalty(String pPenalty) {
        if (StringUtils.isBlank(pPenalty)) {
            penaltyList = null;
        } else {
            penaltyList = new ArrayList<PenaltyDetailBean>();
            BeanMeta lBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PenaltyDetailBean.class);
            List<Map<String, Object>> lList = (List<Map<String, Object>>)new JsonSlurper().parseText(pPenalty);
            for (Map<String, Object> lMap : lList) {
                PenaltyDetailBean lPenaltyDetailBean = new PenaltyDetailBean();
                lBeanMeta.validateAndParse(lPenaltyDetailBean, lMap, null);
                penaltyList.add(lPenaltyDetailBean);
            }
        }
    }

    public List<PenaltyDetailBean> getPenaltyList() {
        return penaltyList;
    }

    public void setPenaltyList(List<PenaltyDetailBean> pPenaltyList) {
        penaltyList = pPenaltyList;
    }

    public String getPurchaserName() {
        try {
            if (StringUtils.isNotBlank(purchaser)) {
                if (purchaser.equals(DEFAULT))
                    return purchaser;
                AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(purchaser);
                if (lAppEntityBean != null)
                    return lAppEntityBean.getName();
            }
        } catch (Exception lException) {
        }
        return null;
    }

    public void setPurchaserName(String pPurchaserName) {
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

    public static final String DEFAULT = "DEFAULT"; 
}