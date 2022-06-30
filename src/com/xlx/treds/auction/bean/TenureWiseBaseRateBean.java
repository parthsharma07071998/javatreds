package com.xlx.treds.auction.bean;

import java.math.BigDecimal;


public class TenureWiseBaseRateBean implements Comparable<TenureWiseBaseRateBean> {

    private Long tenure;
    private BigDecimal baseRate;

    public Long getTenure() {
        return tenure;
    }

    public void setTenure(Long pTenure) {
        tenure = pTenure;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal pBaseRate) {
        baseRate = pBaseRate;
    }

    public int compareTo(TenureWiseBaseRateBean pTenureWiseBaseRateBean) {
        return tenure.compareTo(pTenureWiseBaseRateBean.getTenure());
    }

}