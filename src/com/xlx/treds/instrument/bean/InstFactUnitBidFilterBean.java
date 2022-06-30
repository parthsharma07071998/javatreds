package com.xlx.treds.instrument.bean;

import java.util.List;

import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.treds.auction.bean.BidBean;

public class InstFactUnitBidFilterBean {

    private String instid;
    private List<InstrumentBean.Status> inststatus;
    private List<String> salesCategory;
    private List<String> purchaser;
    private List<String> supplier;
    private String factid;
    private List<FactoringUnitBean.Status> factstatus;
    private String bidID;
    private List<BidBean.Status> status;
    private List<String> financierEntity;
    private Yes instIsAggregatorCreated;
    private List<String> instAggregatorEntity;

    public String getInstid() {
        return instid;
    }

    public void setInstid(String pInstid) {
        instid = pInstid;
    }

    public List<InstrumentBean.Status> getInststatus() {
        return inststatus;
    }

    public void setInststatus(List<InstrumentBean.Status> pInststatus) {
        inststatus = pInststatus;
    }

    public List<String> getSalesCategory() {
        return salesCategory;
    }

    public void setSalesCategory(List<String> pSalesCategory) {
        salesCategory = pSalesCategory;
    }

    public List<String> getPurchaser() {
        return purchaser;
    }

    public void setPurchaser(List<String> pPurchaser) {
        purchaser = pPurchaser;
    }

    public List<String> getSupplier() {
        return supplier;
    }

    public void setSupplier(List<String> pSupplier) {
        supplier = pSupplier;
    }

    public String getFactid() {
        return factid;
    }

    public void setFactid(String pFactid) {
        factid = pFactid;
    }

    public List<FactoringUnitBean.Status> getFactstatus() {
        return factstatus;
    }

    public void setFactstatus(List<FactoringUnitBean.Status> pFactstatus) {
        factstatus = pFactstatus;
    }

    public String getBidID() {
        return bidID;
    }

    public void setBidID(String pBidID) {
        bidID = pBidID;
    }

    public List<BidBean.Status> getStatus() {
        return status;
    }

    public void setStatus(List<BidBean.Status> pStatus) {
        status = pStatus;
    }

    public List<String> getFinancierEntity() {
        return financierEntity;
    }

    public void setFinancierEntity(List<String> pFinancierEntity) {
        financierEntity = pFinancierEntity;
    }

    public Yes getInstIsAggregatorCreated() {
        return instIsAggregatorCreated;
    }

    public void setInstIsAggregatorCreated(Yes pInstIsAggregatorCreated) {
        instIsAggregatorCreated = pInstIsAggregatorCreated;
    }

    public List<String> getInstAggregatorEntity() {
        return instAggregatorEntity;
    }

    public void setInstAggregatorEntity(List<String> pInstAggregatorEntity) {
        instAggregatorEntity = pInstAggregatorEntity;
    }

}