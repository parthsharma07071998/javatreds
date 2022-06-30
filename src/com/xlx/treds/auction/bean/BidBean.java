package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants.CostCollectionLeg;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean.ChargeType;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonSlurper;

public class BidBean {
    public static final String FIELDGROUP_UPDATESTATUS = "updateStatus";
    public static final String FIELDGROUP_UPDATEACCEPTSTATUS = "updateAcceptStatus";
    public static final String FIELDGROUP_FINLIST = "finList";
    public static final String FIELDGROUP_DEPTH = "depth";
    public enum Status implements IKeyValEnumInterface<String>{
        Active("ACT","Active"),Deleted("DEL","Deleted"),Deleted_By_Owner("DLO","Deleted By Owner"),Accepted("APT","Accepted"),Auto_Reject("REJ","Auto Accept Failed"),Expired("EXP","Bid Expired"),NotAccepted("NAT","NotAccepted");
        
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
    public enum BidType implements IKeyValEnumInterface<String>{
        Reserved("RES","Reserved"),Open("OPN","Open");
        
        private final String code;
        private final String desc;
        private BidType(String pCode, String pDesc) {
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
    public enum ProvAction implements IKeyValEnumInterface<String>{
        Entry("E","New Bid"),Modify("M","Modification"),Cancel("C","Cancellation");
        
        private final String code;
        private final String desc;
        private ProvAction(String pCode, String pDesc) {
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
    public enum AppStatus implements IKeyValEnumInterface<String>{
        Pending("P","Pending"),Approved("A","Approved"),Rejected("R","Rejected"),Withdrawn("W","Withdrawn");
        
        private final String code;
        private final String desc;
        private AppStatus(String pCode, String pDesc) {
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

    private Long fuId;
    private String financierEntity;
    private Long financierAuId;
    private String financierLoginId;
    private String financierUserName;
    private BigDecimal rate;
    private BigDecimal haircut;
    private Date validTill;
    private Status status;
    private String statusRemarks;
    private Long id;
    private Timestamp timestamp;
    private Long lastAuId;
    private String lastLoginId;
    private BidType bidType;
    private BigDecimal provRate;
    private BigDecimal provHaircut;
    private Date provValidTill;
    private BidType provBidType;
    private ProvAction provAction;
    private AppStatus appStatus;
    private String appRemarks;
    private Long checkerAuId;
    private BigDecimal limitUtilised;
    private BigDecimal bidLimitUtilised;
    private String limitIds;
    private CostCollectionLeg costLeg;
    private Long chkLevel;
    private String charges;
    private BigDecimal normalPercent;
    private BigDecimal normalMinAmt;
    private BigDecimal normalMaxAmt;
    private ChargeType normalChargeType;
    private BigDecimal splitChargeBearerPercent;
    private BigDecimal splitMinCharge;
    private BigDecimal splitPercent;
    
    public Long getFuId() {
        return fuId;
    }

    public void setFuId(Long pFuId) {
        fuId = pFuId;
    }

    public String getFinancierEntity() {
        return financierEntity;
    }

    public void setFinancierEntity(String pFinancierEntity) {
        financierEntity = pFinancierEntity;
    }

    public Long getFinancierAuId() {
        return financierAuId;
    }

    public void setFinancierAuId(Long pFinancierAuId) {
        financierAuId = pFinancierAuId;
        financierLoginId = null;
        //financierUserName = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{financierAuId});
            if (lAppUserBean != null) {
                financierLoginId = lAppUserBean.getLoginId();
                //financierUserName = lAppUserBean.getName();
            }
        } catch (Exception lException) {
        }
    }

    public String getFinancierLoginId() {
        return financierLoginId;
    }

    public void setFinancierLoginId(String pFinancierLoginId) {
        financierLoginId = pFinancierLoginId;
    }

    public String getLastAuIdUserName() {
    	if(lastAuId==null) return "";
    	 MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
         try {
             AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lastAuId});
             if (lAppUserBean != null) {
                 return lAppUserBean.getName();
             }
         } catch (Exception lException) {
         }
    	return "";
    }

    public String getFinancierName() {
    	  if (financierEntity != null) {
              MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
              try {
                  AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{financierEntity});
                  if (lAppEntityBean != null)
                      return lAppEntityBean.getName();
              } catch (Exception lException) {
              }
          }
    	  return "";
    }

    public void setFinancierName(String pFinancierName) {
        financierUserName = pFinancierName;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal pRate) {
        rate = pRate;
    }

    public BigDecimal getHaircut() {
        return haircut;
    }

    public void setHaircut(BigDecimal pHaircut) {
        haircut = pHaircut;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date pValidTill) {
        validTill = pValidTill;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status pStatus) {
        status = pStatus;
    }

    public String getStatusRemarks() {
        return statusRemarks;
    }

    public void setStatusRemarks(String pStatusRemarks) {
        statusRemarks = pStatusRemarks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp pTimestamp) {
        timestamp = pTimestamp;
    }

    public Long getLastAuId() {
        return lastAuId;
    }

    public void setLastAuId(Long pLastAuId) {
        lastAuId = pLastAuId;
        lastLoginId = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
        try {
            AppUserBean lAppUserBean = (AppUserBean) lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lastAuId});
            if (lAppUserBean != null) {
                lastLoginId = lAppUserBean.getLoginId();
            }
        } catch (Exception lException) {
        }

    }

    public String getLastLoginId() {
        return lastLoginId;
    }

    public void setLastLoginId(String pLastLoginId) {
        lastLoginId = pLastLoginId;
    }

    public BidType getBidType() {
        return bidType;
    }

    public void setBidType(BidType pBidType) {
        bidType = pBidType;
    }

    public BigDecimal getProvRate() {
        return provRate;
    }

    public void setProvRate(BigDecimal pProvRate) {
        provRate = pProvRate;
    }

    public BigDecimal getProvHaircut() {
        return provHaircut;
    }

    public void setProvHaircut(BigDecimal pProvHaircut) {
        provHaircut = pProvHaircut;
    }

    public Date getProvValidTill() {
        return provValidTill;
    }

    public void setProvValidTill(Date pProvValidTill) {
        provValidTill = pProvValidTill;
    }

    public BidType getProvBidType() {
        return provBidType;
    }

    public void setProvBidType(BidType pProvBidType) {
        provBidType = pProvBidType;
    }

    public ProvAction getProvAction() {
        return provAction;
    }

    public void setProvAction(ProvAction pProvAction) {
        provAction = pProvAction;
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(AppStatus pAppStatus) {
        appStatus = pAppStatus;
    }

    public String getAppRemarks() {
        return appRemarks;
    }

    public void setAppRemarks(String pAppRemarks) {
        appRemarks = pAppRemarks;
    }

    public Long getCheckerAuId() {
        return checkerAuId;
    }

    public void setCheckerAuId(Long pCheckerAuId) {
        checkerAuId = pCheckerAuId;
    }

    public BigDecimal getLimitUtilised() {
        return limitUtilised;
    }

    public void setLimitUtilised(BigDecimal pLimitUtilised) {
        limitUtilised = pLimitUtilised;
    }

    public BigDecimal getBidLimitUtilised() {
        return bidLimitUtilised;
    }

    public void setBidLimitUtilised(BigDecimal pBidLimitUtilised) {
        bidLimitUtilised = pBidLimitUtilised;
    }

    public String getLimitIds() {
        return limitIds;
    }

    public void setLimitIds(String pLimitIds) {
        limitIds = pLimitIds;
    }

    public CostCollectionLeg getCostLeg() {
        return costLeg;
    }

    public void setCostLeg(CostCollectionLeg pCostLeg) {
        costLeg = pCostLeg;
    }

    public void clearFinalBid() {
        rate = null;
        haircut = null;
        bidType = null;
        validTill = null;
    }
    
    public void clearProvBid() {
        provRate = null;
        provHaircut = null;
        provBidType = null;
        provValidTill = null;
        provAction = null;
    }
    
    public Long getChkLevel() {
        return chkLevel;
    }

    public void setChkLevel(Long pChkLevel) {
        chkLevel = pChkLevel;
    }
    
    public String getCharges() {
        return charges;
    }

    public void setCharges(String pCharges) {
        charges = pCharges;
        //logic - to initialize all the below
    	normalPercent = BigDecimal.ZERO;
    	normalMinAmt = BigDecimal.ZERO;
    	normalMaxAmt = BigDecimal.ZERO;
    	splitChargeBearerPercent = BigDecimal.ZERO;
    	splitMinCharge = BigDecimal.ZERO;
    	splitPercent = BigDecimal.ZERO;
    	normalChargeType = null;
        if(!StringUtils.isEmpty(charges)){
        	Map<String,Object> lMap = null;
        	JsonSlurper lJsonSlurper = new JsonSlurper();
        	lMap = (Map<String, Object>) lJsonSlurper.parseText(charges);
        	if(lMap!=null){
        		if(lMap.get("normalPercent")!=null) {
        			normalPercent = new BigDecimal(lMap.get("normalPercent").toString());
        		}
        		if(lMap.get("normalMinAmt")!=null) {
        			normalMinAmt = new BigDecimal(lMap.get("normalMinAmt").toString());
        		}
        		if(lMap.get("normalMaxAmt")!=null) {
        			normalMaxAmt = new BigDecimal(lMap.get("normalMaxAmt").toString());
        		}
        		if(lMap.get("normalChargeType")!=null) {
        			ChargeType lChargeType =  (ChargeType) TredsHelper.getInstance().getValue(AuctionChargeSlabBean.class,"chargeType",lMap.get("normalChargeType").toString());
        			normalChargeType = lChargeType;
        		}
        		if(lMap.get("splitChargeBearerPercent")!=null) {
        			splitChargeBearerPercent = new BigDecimal(lMap.get("splitChargeBearerPercent").toString());
        		}
        		if(lMap.get("splitMinCharge")!=null) {
        			splitMinCharge = new BigDecimal(lMap.get("splitMinCharge").toString());
        		}
        		if(lMap.get("splitPercent")!=null) {
        			splitPercent = new BigDecimal(lMap.get("splitPercent").toString());
        		}
        	}
        }
    }

    public BigDecimal getNormalPercent() {
        return normalPercent;
    }

    public void setNormalPercent(BigDecimal pNormalPercent) {
        normalPercent = pNormalPercent;
    }

    public BigDecimal getNormalMinAmt() {
        return normalMinAmt;
    }

    public void setNormalMinAmt(BigDecimal pNormalMinAmt) {
        normalMinAmt = pNormalMinAmt;
    }
    
    public BigDecimal getNormalMaxAmt() {
        return normalMinAmt;
    }

    public void setNormalMaxAmt(BigDecimal pNormalMaxAmt) {
        normalMaxAmt = pNormalMaxAmt;
    }
    
    public ChargeType getNormalChargeType() {
        return normalChargeType;
    }

    public void setNormalChargeType(ChargeType pNormalChargeType) {
    	normalChargeType = pNormalChargeType;
    }

    public BigDecimal getSplitChargeBearerPercent() {
        return splitChargeBearerPercent;
    }

    public void setSplitChargeBearerPercent(BigDecimal pSplitChargeBearerPercent) {
    	splitChargeBearerPercent = pSplitChargeBearerPercent;
    }

    public BigDecimal getSplitMinCharge() {
        return splitMinCharge;
    }

    public void setSplitMinCharge(BigDecimal pSplitMinCharge) {
        splitMinCharge = pSplitMinCharge;
    }

    public BigDecimal getSplitPercent() {
        return splitPercent;
    }

    public void setSplitPercent(BigDecimal pSplitPercent) {
    	splitPercent = pSplitPercent;
    }
}