package com.xlx.treds.instrument.bo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppInitializer;
import com.xlx.treds.AppConstants.AutoAcceptBid;
import com.xlx.treds.AppConstants.ChargeType;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.CostBearingType;
import com.xlx.treds.AppConstants.CostCollectionLeg;
import com.xlx.treds.ClickWrapHelper;
import com.xlx.treds.MonetagoBusinessException;
import com.xlx.treds.MonetagoTredsHelper;
import com.xlx.treds.MonetagoTredsHelper.CancelResonCode;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.BidBean.AppStatus;
import com.xlx.treds.auction.bean.BidBean.BidType;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.FactoringUnitWatchBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PurchaserSupplierCapRateBean;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.auction.bo.PurchaserSupplierLimitUtilizationBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.BillingLocationBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.entity.bo.BillingLocationBO;
import com.xlx.treds.entity.bo.PurchaserAggregatorBO;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.FactoringUnitBidBean;
import com.xlx.treds.instrument.bean.GstSummaryBean;
import com.xlx.treds.instrument.bean.ILegInterest;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.AuctionChargePlanBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean.Status;
import com.xlx.treds.master.bean.GSTRateBean;
import com.xlx.treds.monitor.bean.PurSupRelationInstBean;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.IAgreementAcceptanceBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean.CheckerType;
import com.xlx.treds.user.bo.AppUserBO;

import groovy.json.JsonBuilder;

public class FactoringUnitBO {
    private static final Logger logger = LoggerFactory.getLogger(FactoringUnitBO.class);
    
    public static Long TABSP_READYFORAUCTION = Long.valueOf(0);
    public static Long TABSP_ACTIVE = Long.valueOf(1);
    public static Long TABSP_FACTORED = Long.valueOf(2);
    public static Long TABSP_SUSPENDED = Long.valueOf(3);
    private static Long TABSP_HISTORY = Long.valueOf(4);

    private static Long TABFIN_ACTIVE = Long.valueOf(0);
    private static Long TABFIN_FACTORED = Long.valueOf(1);
    private static Long TABFIN_OTHER = Long.valueOf(2);

    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private InstrumentBO instrumentBO;
    private AppUserBO appUserBO;
    private EmailGeneratorBO emailGeneratorBO;
    private PurchaserSupplierLimitUtilizationBO purchaserSupplierLimitUtilizationBO;
    private BillingLocationBO billingLocationBO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private GenericDAO<BidBean> bidDAO;
    private GenericDAO<FactoringUnitWatchBean> factoringUnitWatchDAO;
    private GenericDAO<ObligationBean> obligationDAO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO;
    private CompositeGenericDAO<FactoringUnitBidBean> factoringUnitBidDAO;
    private GenericDAO<PurchaserSupplierCapRateBean> purchaserSupplierCapRateDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private GenericDAO<BidBean> bidBeanDAO;
    private CompositeGenericDAO<FactoredBean> factoredBeanDAO;
    private GenericDAO<GstSummaryBean> gstSummaryBeanDAO;
	private GenericDAO<PurSupRelationInstBean> purSupRelationInstDAO;
	private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
    private static final int INT_PUR_LEG1 = 0;
    private static final int INT_SUP_LEG1 = 1;
    private static final int INT_PUR_LEG2 = 2;
    
    public FactoringUnitBO() {
        super();
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        instrumentBO = new InstrumentBO();
        appUserBO = new AppUserBO();
        emailGeneratorBO = new EmailGeneratorBO();
        
        purchaserSupplierLimitUtilizationBO = new PurchaserSupplierLimitUtilizationBO();
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
        factoringUnitWatchDAO = new GenericDAO<FactoringUnitWatchBean>(FactoringUnitWatchBean.class);
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        billingLocationBO = new BillingLocationBO();
        factoringUnitBidDAO = new CompositeGenericDAO<FactoringUnitBidBean>(FactoringUnitBidBean.class);
        financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
        purchaserSupplierCapRateDAO = new GenericDAO<PurchaserSupplierCapRateBean>(PurchaserSupplierCapRateBean.class);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
        financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
        bidBeanDAO = new GenericDAO<BidBean>(BidBean.class);
        factoredBeanDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
        gstSummaryBeanDAO = new GenericDAO<GstSummaryBean>(GstSummaryBean.class);
        purSupRelationInstDAO = new GenericDAO<PurSupRelationInstBean>(PurSupRelationInstBean.class);
		purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
    }
    
    public FactoringUnitBean findBean(ExecutionContext pExecutionContext, 
        FactoringUnitBean pFilterBean) throws Exception {
        FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lFactoringUnitBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lFactoringUnitBean;
    }
    
    public String findListSP(ExecutionContext pExecutionContext, AppUserBean pUserBean, boolean pHistorical, FactoringUnitBean pHistoryFilterBean, ObligationBean pFilterObliBean) throws Exception {
        List<FactoringUnitBean> lList = getListSP(pExecutionContext.getConnection(), pUserBean, pHistorical, pHistoryFilterBean, pFilterObliBean);
        List<Map<String, Object>> lJsonList = new ArrayList<Map<String,Object>>();
        for (FactoringUnitBean lFactoringUnitBean : lList) {
            lJsonList.add(getFactoringUnitJsonSP(lFactoringUnitBean, pUserBean, pHistorical));
        }
        return new JsonBuilder(lJsonList).toString();
    }
    
    public List<FactoringUnitBean> getListSP(Connection pConnection, AppUserBean pUserBean,boolean pHistorical, FactoringUnitBean pHistoryFilterBean, ObligationBean pFilterObliBean) throws Exception{
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        lSql.append("SELECT * FROM FactoringUnits ");
        boolean lCheckAccessToLocations = TredsHelper.getInstance().checkAccessToLocations((AppUserBean) pUserBean);
		if (lCheckAccessToLocations){
			lSql.append("join Instruments on infuid = fuid ");
		}
		lSql.append(" WHERE FURecordVersion > 0 ");
        lSql.append(" AND FUStatus ").append(pHistorical?" NOT ":"").append(" IN (").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lSql.append(",").append(lDBHelper.formatString(FactoringUnitBean.Status.Factored.getCode()));
        lSql.append(",").append(lDBHelper.formatString(FactoringUnitBean.Status.Ready_For_Auction.getCode()));
        lSql.append(",").append(lDBHelper.formatString(FactoringUnitBean.Status.Suspended.getCode())).append(")");
        if(pUserBean!=null && AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
        {
        	//no filters
        }
        else
        {
        	StringBuilder lWhere1, lWhere2, lWhere3;
        	lWhere1 = new StringBuilder();
        	lWhere2 = new StringBuilder();
        	lWhere3 = new StringBuilder();
        	//
        	lWhere1.append(" FUIntroducingEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	lWhere2.append(" FUCounterEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	lWhere3.append(" FUOwnerEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	//
        	if (pUserBean.getType() != AppUserBean.Type.Admin) {
                if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
                   	lWhere1.append(" AND ( FUIntroducingAUId = ").append(pUserBean.getId()).append(" OR FUIntroducingAUId IS NULL ) ");
                	lWhere2.append(" AND ( FUCounterAUId = ").append(pUserBean.getId()).append(" OR FUCounterAUId IS NULL ) ");
                	lWhere3.append(" AND ( FUOwnerAuId = ").append(pUserBean.getId()).append(" OR  FUOwnerAuId IS NULL ) ");            
                }
        	}
            lSql.append(" AND ( ");
            lSql.append(" ( ").append(lWhere1.toString()).append(" ) ");
            lSql.append(" OR ( ").append(lWhere2.toString()).append(" ) ");
            lSql.append(" OR ( ").append(lWhere3.toString()).append(" ) ");
            lSql.append(" ) ");
        }
        if(pHistorical){
            lSql.append(" AND (FUAcceptDateTime IS NOT NULL AND FUAcceptDateTime < ").append(lDBHelper.formatDate(TredsHelper.getInstance().getBusinessDate())).append(")");
            if(pHistoryFilterBean.getId()!=null){
            	lSql.append(" AND FUId = ").append(pHistoryFilterBean.getId());
            }
            if(CommonUtilities.hasValue(pHistoryFilterBean.getPurchaser())){
            	lSql.append(" AND FUPurchaser = ").append(lDBHelper.formatString(pHistoryFilterBean.getPurchaser()));
            }
            if(CommonUtilities.hasValue(pHistoryFilterBean.getSupplier())){
            	lSql.append(" AND FUSupplier = ").append(lDBHelper.formatString(pHistoryFilterBean.getSupplier()));
            }
            if(CommonUtilities.hasValue(pHistoryFilterBean.getFinancier())){
            	lSql.append(" AND FUFINANCIER = ").append(lDBHelper.formatString(pHistoryFilterBean.getFinancier()));
            }
            if(pHistoryFilterBean.getMaturityDate()!=null ||
            		pHistoryFilterBean.getFilterMaturityDate()!=null || 
            		pFilterObliBean.getStatus()!=null || pFilterObliBean.getType() !=null ||
            		pFilterObliBean.getTxnType() !=null || pFilterObliBean.getPfId()!=null ){
            	lSql.append(" AND FUId IN ( ");
            	lSql.append(" SELECT OBFUID FROM OBLIGATIONS WHERE OBRecordVersion > 0 ");
            	lSql.append(" AND OBTXNENTITY = ").append(lDBHelper.formatString(pUserBean.getDomain()));
                if(pHistoryFilterBean.getMaturityDate()!=null){
                    lSql.append(" AND OBDate >= ").append(lDBHelper.formatDate(pHistoryFilterBean.getMaturityDate()));
                }
                if(pHistoryFilterBean.getFilterMaturityDate()!=null){
                    lSql.append(" AND OBDate <= ").append(lDBHelper.formatDate(pHistoryFilterBean.getFilterMaturityDate()));
                }
                if(pFilterObliBean.getStatus()!=null){
                	lSql.append(" AND OBSTATUS = ").append(lDBHelper.formatString(pFilterObliBean.getStatus().getCode()));
                }
                if(pFilterObliBean.getType()!=null){//Leg
                	lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(pFilterObliBean.getType().getCode()));
                }
                if(pFilterObliBean.getTxnType()!=null){
                	lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(pFilterObliBean.getTxnType().getCode()));
                }
                if(pFilterObliBean.getPfId()!=null){
                	lSql.append(" AND OBPFID = ").append(pFilterObliBean.getPfId().toString());
                }
                //lSql.append(" AND OBDate >= ").append(lDBHelper.formatDate(TredsHelper.getInstance().getBusinessDate()));
                lSql.append(" ) ");
            }
        }else{
            lSql.append(" AND (FUAcceptDateTime IS NULL OR FUAcceptDateTime > ").append(lDBHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate())).append(")");
        }
		if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()) && lCheckAccessToLocations){
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
			if(lAppEntityBean.isPurchaser()){
        		lSql.append(" AND INPurClId in (").append(TredsHelper.getInstance()
        			.getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(") ");
			}else if(lAppEntityBean.isSupplier()){
	        	lSql.append(" AND INSupClId in (").append(TredsHelper.getInstance()
	        			.getCSVIdsForInQuery(pUserBean.getLocationIdList())).append(") ");				
			}
        }
		if(pUserBean.hasUserLimit()){
			if(pUserBean.getMinUserLimit()!=null){
				lSql.append(" AND FUAmount >= ").append(pUserBean.getMinUserLimit());
			}
			if(pUserBean.getMaxUserLimit()!=null){
				lSql.append(" AND FUAmount <= ").append(pUserBean.getMaxUserLimit());
			}
		}
        lSql.append(" ORDER BY FUId");
        return factoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
    }

    
    private Map<String, Object> getFactoringUnitJsonSP(FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean, boolean pHistorical) throws Exception {
        Map<String, Object> lJson = factoringUnitDAO.getBeanMeta().formatAsMap(pFactoringUnitBean, FactoringUnitBean.FIELDGROUP_SUPPURLIST, null, false, true);
        lJson.put("statusDesc", pFactoringUnitBean.getStatus());
        lJson.put("costBearerDesc", pFactoringUnitBean.getCostBearingType()); 
        boolean lOwner = (isOwnerAdmin(pFactoringUnitBean, pUserBean) || isOwner(pFactoringUnitBean, pUserBean));
        boolean lAuctionRights = (hasAuctionManagementRights(pFactoringUnitBean, pUserBean));
        lJson.put("owner", lOwner);
        lJson.put("auctionRights", lAuctionRights);        
        lJson.put("ownerOrAuctionRights", (lOwner || lAuctionRights));        
        
        lJson.put("isBuyer", (pUserBean.getDomain().equals(pFactoringUnitBean.getPurchaser())?Boolean.TRUE:Boolean.FALSE));
        Long lTab = pFactoringUnitBean.getTab();
        if(pHistorical){
            lTab = TABSP_HISTORY;
        }
        lJson.put("tab", lTab);
        lJson.put("autoAccept", pFactoringUnitBean.getAutoAccept()==null?AppConstants.AutoAcceptBid.Disabled.toString():pFactoringUnitBean.getAutoAccept().toString());
        lJson.put("autoAcceptableBidTypes", pFactoringUnitBean.getAutoAcceptableBidTypes()==null?"":pFactoringUnitBean.getAutoAcceptableBidTypes().toString());
        return lJson;
    }
    
    private Boolean hasAuctionManagementRights(FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean)throws Exception{
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, false);
        if(lAppEntityBean != null){
        	if(lAppEntityBean.isPurchaserAggregator()){
        		//TODO: set proper conditions - should we check the actions over here?
        		//Helper function -> user bean pass , hardcoded action-key pass
        		return Boolean.TRUE;
        	}
        	if(AppUserBean.Type.Admin.equals(pUserBean.getType())){
        		if(lAppEntityBean.isPurchaser()){
        			if(AppConstants.AutoConvert.Purchaser.equals(pFactoringUnitBean.getAutoConvert())){
        				return Boolean.TRUE;
        			}
        			else if(AppConstants.AutoConvert.Auto.equals(pFactoringUnitBean.getAutoConvert())){
        				if(!CostBearingType.Buyer.equals(pFactoringUnitBean.getCostBearingType()) && 
        						!CostBearingType.Seller.equals(pFactoringUnitBean.getCostBearingType())){
        					//on basis of owner
        					return (pFactoringUnitBean.getOwnerEntity().equals(pFactoringUnitBean.getPurchaser()));
        				}else
        					return CostBearingType.Buyer.equals(pFactoringUnitBean.getCostBearingType());
        			}
            	}else if(lAppEntityBean.isSupplier()){
            		if(AppConstants.AutoConvert.Supplier.equals(pFactoringUnitBean.getAutoConvert())){
        				return Boolean.TRUE;
        			}
           			else if(AppConstants.AutoConvert.Auto.equals(pFactoringUnitBean.getAutoConvert())){
        				if(!CostBearingType.Buyer.equals(pFactoringUnitBean.getCostBearingType()) && 
        						!CostBearingType.Seller.equals(pFactoringUnitBean.getCostBearingType())){
        					//on basis of owner
        					return (pFactoringUnitBean.getOwnerEntity().equals(pFactoringUnitBean.getSupplier()));
        				}else
        					return CostBearingType.Seller.equals(pFactoringUnitBean.getCostBearingType());
        			}
            	}
        	} 
        	if(AppUserBean.Type.User.equals(pUserBean.getType())) {
        		Long lUserId = pUserBean.getId(); //id or ownerid ????
        		boolean lFullOwnership =  !TredsHelper.getInstance().checkOwnership(pUserBean);
        		if(lUserId.equals(pFactoringUnitBean.getOwnerAuId()) ||
        				lUserId.equals(pFactoringUnitBean.getIntroducingAuId()) ||
        				lUserId.equals(pFactoringUnitBean.getCounterAuId()) ||
        				lFullOwnership){
            		if(lAppEntityBean.isPurchaser()){
            			if(AppConstants.AutoConvert.Purchaser.equals(pFactoringUnitBean.getAutoConvert())){
            				return Boolean.TRUE;
            			}
            			else if(AppConstants.AutoConvert.Auto.equals(pFactoringUnitBean.getAutoConvert())){
            				if(!CostBearingType.Buyer.equals(pFactoringUnitBean.getCostBearingType()) && 
            						!CostBearingType.Seller.equals(pFactoringUnitBean.getCostBearingType())){
            					//on basis of owner
            					return (pFactoringUnitBean.getOwnerEntity().equals(pFactoringUnitBean.getPurchaser()));
            				}else
            					return CostBearingType.Buyer.equals(pFactoringUnitBean.getCostBearingType());
            			}
                	}else if(lAppEntityBean.isSupplier()){
                		if(AppConstants.AutoConvert.Supplier.equals(pFactoringUnitBean.getAutoConvert())){
            				return Boolean.TRUE;
            			}
               			else if(AppConstants.AutoConvert.Auto.equals(pFactoringUnitBean.getAutoConvert())){
            				if(!CostBearingType.Buyer.equals(pFactoringUnitBean.getCostBearingType()) && 
            						!CostBearingType.Seller.equals(pFactoringUnitBean.getCostBearingType())){
            					//on basis of owner
            					return (pFactoringUnitBean.getOwnerEntity().equals(pFactoringUnitBean.getSupplier()));
            				}else
            					return CostBearingType.Seller.equals(pFactoringUnitBean.getCostBearingType());
            			}
                	}
        		}
        	}
        }
    	return Boolean.FALSE;
    }
    
    private Boolean isOwner(FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean) {
        if (pUserBean.getDomain().equals(pFactoringUnitBean.getOwnerEntity())) {
            if (pFactoringUnitBean.getOwnerAuId() == null
                    || pUserBean.getId().equals(pFactoringUnitBean.getOwnerAuId()) 
                    		|| !TredsHelper.getInstance().checkOwnership(pUserBean) )  {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    private Boolean isOwnerAdmin(FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean) {
        if (pUserBean.getDomain().equals(pFactoringUnitBean.getOwnerEntity())) {
            if (pFactoringUnitBean.getOwnerAuId() == null
                    || (AppUserBean.Type.Admin.equals(pUserBean.getType()))) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    
    public void updateFactoringUnit(ExecutionContext pExecutionContext, FactoringUnitBean pFactoringUnitBean, 
            AppUserBean pAppUserBean) throws Exception {
        FactoringUnitBean lFactoringUnitBean = findBean(pExecutionContext, pFactoringUnitBean);
        if (!isOwner(lFactoringUnitBean, pAppUserBean).booleanValue())
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        if ((lFactoringUnitBean.getFactorEndDateTime().compareTo(lCurrentTime) <= 0) || 
                ((lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Active) && 
                		(lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Ready_For_Auction) &&
                        (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Suspended)))
            throw new CommonBusinessException("End date time cannot be changed now");
        if (pFactoringUnitBean.getFactorEndDateTime().compareTo(lCurrentTime) < 0)
            throw new CommonBusinessException("End Date time cannot be reduced below current time");
        if (pFactoringUnitBean.getFactorEndDateTime().compareTo(lFactoringUnitBean.getFactorMaxEndDateTime()) > 0)
            throw new CommonBusinessException("Cannot increase end date time. Minimum usance criteria not met");
        
        factoringUnitDAO.getBeanMeta().copyBean(pFactoringUnitBean, lFactoringUnitBean, FactoringUnitBean.FIELDGROUP_UPDATEEXTENSION, null);
        factoringUnitDAO.update(pExecutionContext.getConnection(), lFactoringUnitBean, FactoringUnitBean.FIELDGROUP_UPDATEEXTENSION);
        factoringUnitDAO.insertAudit(pExecutionContext.getConnection(), lFactoringUnitBean, AuditAction.Update, pAppUserBean.getId());
    }
    
    public void updateFactoringUnitLeg3Settlement(ExecutionContext pExecutionContext, FactoringUnitBean pFactoringUnitBean, 
            AppUserBean pAppUserBean) throws Exception {
        FactoringUnitBean lFactoringUnitBean = findBean(pExecutionContext, pFactoringUnitBean);
        if (!isOwner(lFactoringUnitBean, pAppUserBean).booleanValue())
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        if ((lFactoringUnitBean.getFactorEndDateTime().compareTo(lCurrentTime) <= 0) || 
                ((lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Active) && 
                		(lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Ready_For_Auction) &&
                        (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Suspended)))
            throw new CommonBusinessException("Leg 3 Settlement flag cannot be changed now.");
        if (lFactoringUnitBean.getFactorEndDateTime().compareTo(lCurrentTime) < 0)
            throw new CommonBusinessException("Leg 3 Settlement flag cannot be changed now.");
        if (lFactoringUnitBean.getFactorEndDateTime().compareTo(lFactoringUnitBean.getFactorMaxEndDateTime()) > 0)
            throw new CommonBusinessException("Leg 3 Settlement flag cannot be changed now.");
        if(!lFactoringUnitBean.getPurchaser().equals(pAppUserBean.getDomain()))
        	throw new CommonBusinessException("Only the Buyer can modify the Leg 3 settlement.");
        factoringUnitDAO.getBeanMeta().copyBean(pFactoringUnitBean, lFactoringUnitBean, FactoringUnitBean.FIELDGROUP_UPDATELEG3FLAG, null);
        factoringUnitDAO.update(pExecutionContext.getConnection(), lFactoringUnitBean, FactoringUnitBean.FIELDGROUP_UPDATELEG3FLAG);
        factoringUnitDAO.insertAudit(pExecutionContext.getConnection(), lFactoringUnitBean, AuditAction.Update, pAppUserBean.getId());
    }
    
    public String updateStatus(ExecutionContext pExecutionContext, List<FactoringUnitBean> pFilterBeans, 
        AppUserBean pUserBean, FactoringUnitBean.Status pStatus) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        List<Map<String,Object>> lMessages = new ArrayList<Map<String,Object>>();
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        List<FactoringUnitBean> lFactoringUnitList = new ArrayList<FactoringUnitBean>();// list of successfully updated factoring unit beans
        Map<Long, List<BidBean>> lDeletedBidsMap = new HashMap<Long, List<BidBean>>();// key : factoring unit id. value : list of deleted bids
        BeanMeta lFOBeanMeta = factoringUnitDAO.getBeanMeta();
        AppEntityBean lLoggedInEntity = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
		PurchaserAggregatorBO lPurchaserAggregatorBO = null;
        if(lLoggedInEntity.isPurchaserAggregator()) {
			lPurchaserAggregatorBO = new PurchaserAggregatorBO();
        }
        for (FactoringUnitBean lFilterBean : pFilterBeans) {
            pExecutionContext.setAutoCommit(false);
            lConnection = pExecutionContext.getConnection();
            String lError = null;
            FactoringUnitBean lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(lConnection, lFilterBean);
            FactoringUnitBean lFUBeanOriginal = null;
            if(lFactoringUnitBean!=null) {
                if(lLoggedInEntity.isPurchaserAggregator()) {
        			lPurchaserAggregatorBO.validateMappedEntity(pExecutionContext.getConnection(), pUserBean.getDomain(), lFactoringUnitBean.getPurchaser());
                }
            	lFUBeanOriginal = new FactoringUnitBean();
        		lFOBeanMeta.copyBean(lFactoringUnitBean, lFUBeanOriginal);
        		try {
                	checkPlatformStatus(lConnection, lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getSupplier());
        		}catch(Exception lEx) {
        			lError = lEx.getMessage();
        		}
        		if(!lLoggedInEntity.isPurchaserAggregator() && FactoringUnitBean.Status.Withdrawn.equals(pStatus)) {
        			InstrumentBean lInstrumentBean = new InstrumentBean();
        			lInstrumentBean.setFuId(lFactoringUnitBean.getId());
        			lInstrumentBean =  instrumentDAO.findBean(lConnection, lInstrumentBean);
        			if(lInstrumentBean!=null) {
        				if(lInstrumentBean.getIsGemInvoice()) {
        					String lEntityCode = RegistryHelper.getInstance().getString(AppInitializer.REGISTRY_GEM_ARTERIA);
        					if(!pUserBean.getDomain().equals(lEntityCode)) {
        						lError = "Gem invoice cannot be withdrawn.";
        					}
        				}
        			}
        		}
            }
            if (lError == null) {
				if (lFactoringUnitBean == null) {
	            	lError = CommonBusinessException.RECORD_NOT_FOUND;
	            }else if(!(FactoringUnitBean.Status.Suspended.equals(pStatus) ||
	            		FactoringUnitBean.Status.Active.equals(pStatus)||
	            		FactoringUnitBean.Status.Withdrawn.equals(pStatus))){
	            	//TODO:
	            	if(lLoggedInEntity.isPurchaserAggregator()){
	            	}else{
	                    if (!lFactoringUnitBean.getOwnerEntity().equals(pUserBean.getDomain()))
	                        lError = CommonBusinessException.ACCESS_DENIED;
	                    else if (TredsHelper.getInstance().checkOwnership(pUserBean) && 
	                    		lFactoringUnitBean.getOwnerAuId()!=null && 
	                			!lFactoringUnitBean.getOwnerAuId().equals(pUserBean.getId())){
	                		lError = CommonBusinessException.ACCESS_DENIED;
	                    }
	            	}
	            }
	            }
            List<BidBean> lDeletedBids = null;
            if (lError == null) {
                // check if factoring unit is expired
                if ((lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Active) || 
                		(lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Ready_For_Auction) || 
                        (lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Suspended)) {
                    if ((lFactoringUnitBean.getFactorEndDateTime() != null) && (lFactoringUnitBean.getFactorEndDateTime().compareTo(lCurrentTime) < 0)) {
                        lError = "Factoring unit expired.";
                        lDeletedBids = deleteFactoringUnitBids(lConnection, lFactoringUnitBean, pUserBean);
                        updateStatus(lConnection, lFactoringUnitBean, FactoringUnitBean.Status.Expired, pUserBean);
                    }
                    if(lLoggedInEntity.isPurchaserAggregator()){
                    	//TODO: What?
                    }else{
                        if(pUserBean.hasUserLimit()){
                			if(pUserBean.getMinUserLimit()!=null && pUserBean.getMinUserLimit().compareTo(lFactoringUnitBean.getAmount()) > 0 ){
                				lError = "Limit violation.";
                			}
                   			if(pUserBean.getMaxUserLimit()!=null && pUserBean.getMaxUserLimit().compareTo(lFactoringUnitBean.getAmount()) < 0 ){
                				lError = "Limit violation.";
                			}
                		}
                    }
                }
                if (lError == null) {
                    switch (pStatus) {
                    case Suspended: 
                        if (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Active)
                            lError = "Cannot put factoring unit on hold. Current status is " + lFactoringUnitBean.getStatus().toString();
                        else {
                        	if(hasAuctionManagementRights(lFactoringUnitBean,pUserBean)){
                        		try{
                            		// release pursup limit utilised if any
                                    if (lFactoringUnitBean.getPurSupLimitUtilized() != null) {
                                        purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(lConnection, lFactoringUnitBean.getPurchaser(),
                                                lFactoringUnitBean.getSupplier(), pUserBean, lFactoringUnitBean.getPurSupLimitUtilized(), true);
                                    }
                                    lFactoringUnitBean.setPurSupLimitUtilized(null);
                                    lDeletedBids = deleteFactoringUnitBids(lConnection, lFactoringUnitBean, pUserBean);
                                    updateStatus(lConnection, lFactoringUnitBean, pStatus, pUserBean);
                                    //call email code for the suspended
                        		 } catch (CommonBusinessException lCommonBusinessException) {
                                     lError = lCommonBusinessException.getMessage();
                                 }
                        	}else{
                        		lError="User dose not have rights to Suspend the instrument";
                        	}
                        }
                        break;
                    case Active: 
                        if (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Suspended &&
                        		lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Ready_For_Auction)
                            lError = "Cannot activate factoring unit. Current status is " + lFactoringUnitBean.getStatus().toString();
                        else {
                        	if(hasAuctionManagementRights(lFactoringUnitBean,pUserBean)){
                        		try {
                                	BigDecimal lBlocked = purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(lConnection, lFactoringUnitBean.getPurchaser(), 
                                	        lFactoringUnitBean.getSupplier(), pUserBean, lFactoringUnitBean.getAmount(), false);
                                	lFactoringUnitBean.setPurSupLimitUtilized(lBlocked);
                                    updateStatus(lConnection, lFactoringUnitBean, pStatus, pUserBean);
                                 //call email code for the activate

                                } catch (CommonBusinessException lCommonBusinessException) {
                                    lError = lCommonBusinessException.getMessage();
                                }
                        	}else{
                        		lError="User dose not have rights to Send the instrument for auction";
                        	} 
                            if (FactoringUnitBean.Status.Active.equals(lFactoringUnitBean.getStatus())){
                             	List<String> lFinEntities = TredsHelper.getInstance().getMappedFinancier(lConnection, lFactoringUnitBean.getPurchaser());
                             	if	(lFinEntities != null && lFinEntities.size() > 0){
                             		InstrumentBean lInstFilter = new InstrumentBean();
                             		lInstFilter.setFuId(lFactoringUnitBean.getId());
                             		InstrumentBean lInstBean = instrumentDAO.findBean(lConnection, lInstFilter);
                                 	emailGeneratorBO.sendEmailFuActivationInformToMappedFinancier(lConnection, lFinEntities, lFactoringUnitBean, lInstBean);
                             	}
                            }
                        }
                        break;
                    case Withdrawn: 
                        if ((lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Active) && 
                                (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Ready_For_Auction)&& 
                                (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Suspended))
                            lError = "Cannot withdraw factoring unit. Current status is " + lFactoringUnitBean.getStatus().toString();
                        else {
                        	if(hasAuctionManagementRights(lFactoringUnitBean,pUserBean)){
                        		try{
                            		// release pursup limit utilised if any
                                    if (lFactoringUnitBean.getPurSupLimitUtilized() != null) {
                                        purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(lConnection, lFactoringUnitBean.getPurchaser(),
                                                lFactoringUnitBean.getSupplier(), pUserBean, lFactoringUnitBean.getPurSupLimitUtilized(), true);
                                    }
                                    lFactoringUnitBean.setPurSupLimitUtilized(null);
                                    lDeletedBids = deleteFactoringUnitBids(lConnection, lFactoringUnitBean, pUserBean);
                                    boolean lsucess = updateStatus(lConnection, lFactoringUnitBean, pStatus, pUserBean);
                                    //call the email code for the withdrawn
                                    if (lsucess)
                                    {
                                	   ///
                                    }                        			
                        		} catch (CommonBusinessException lCommonBusinessException) {
                                    lError = lCommonBusinessException.getMessage();
                                }
                        	}else{
                        		lError="User does not have rights to Withdraw the instrument";
                        	}
                        }
                        break;
                    case Factored: 
                        if (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Active) 
                            lError = "Cannot accept bid for factoring unit. Current status is " + lFactoringUnitBean.getStatus().toString();
                        else {
                            ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
							try{
	                            if (lConfirmationWindowBean == null)
	                                throw new CommonBusinessException("Bid acceptance not allowed at this time.");
	                            if (lConfirmationWindowBean.getActive() == CommonAppConstants.YesNo.No)
	                                throw new CommonBusinessException("Bid acceptance has been stopped.");
 	                           acceptBid(lConnection, lFactoringUnitBean, lFilterBean.getBdId(), lFilterBean.getAcceptedRate(), 
                            		pUserBean,  lConfirmationWindowBean.getSettlementDate());
							}
							catch(Exception lEx){
								if(lEx instanceof MonetagoBusinessException){
									lFUBeanOriginal = factoringUnitDAO.findByPrimaryKey(lConnection, lFilterBean);
								}
								lError = lEx.getMessage();
							}
                            // email code for the Factored
                        }
                        break;
                    default:
                        lError = "Invalid status : " + pStatus;
                        break;
                    }
                }
            }
            Map<String, Object> lMap = new HashMap<String, Object>();
            //
            if(StringUtils.isNotBlank(lError)){
            	pExecutionContext.rollback();
            }else{
            	pExecutionContext.commitAndDispose();
            }
            //
            lMap.put("id", lFilterBean.getId());
            if (lError != null){
                lMap.put("error", lError);
                if(lFactoringUnitBean!=null) {
                	lMap.put("data", getFactoringUnitJsonSP(lFUBeanOriginal, pUserBean,false));
                }
            }else{
                lMap.put("data", getFactoringUnitJsonSP(lFactoringUnitBean, pUserBean,false));
            }
            lMessages.add(lMap);
            if (lError == null) {
                lFactoringUnitList.add(lFactoringUnitBean);
                lDeletedBidsMap.put(lFactoringUnitBean.getId(), lDeletedBids);
            }
            if(lFactoringUnitBean!=null && FactoringUnitBean.Status.Withdrawn.equals(pStatus) && 
            		FactoringUnitBean.Status.Withdrawn.equals(lFactoringUnitBean.getStatus()) ) {
				Connection lAdapterConnection = DBHelper.getInstance().getConnection();
				InstrumentBean lInstFilter = new InstrumentBean();
         		lInstFilter.setFuId(lFactoringUnitBean.getId());
         		InstrumentBean lInstBean = instrumentDAO.findBean(lAdapterConnection, lInstFilter);
				IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstBean.getAdapterEntity());
				if (lClientAdapter!=null){
             		//
					ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS, lAdapterConnection);
					lProcessInformationBean.setTredsDataForProcessing(new Object[] {lFactoringUnitBean,lInstBean});
					lProcessInformationBean.setKey(lInstBean.getId().toString());
					lClientAdapter.sendResponseToClient(lProcessInformationBean);
				}
				lAdapterConnection.close();
			}
        }
        // send mail
        emailGeneratorBO.sendFactoringUnitSuspendWithdrawEmails(pExecutionContext.getConnection(), lFactoringUnitList, lDeletedBidsMap, pUserBean);
        
        return new JsonBuilder(lMessages).toString();
    }
    
    public List<FactoringUnitBean> findList(ExecutionContext pExecutionContext, FactoringUnitBean pFilterBean, 
            List<String> pColumnList, AppUserBean pUserBean, boolean pShowAll) throws Exception {
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, true);
		Date lSettlementDate = null;
		Connection lConnection = pExecutionContext.getConnection();
        //if (!lAppEntityBean.isFinancier())
        //    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
		ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        if (lConfirmationWindowBean != null)
            lSettlementDate = lConfirmationWindowBean.getSettlementDate();
        
        if(lSettlementDate==null){
        	logger.error("Settlement Date is null.");
        	lSettlementDate = OtherResourceCache.getInstance().getCurrentDate();
        }

        //SELECT FUId,FUMaturityDate,FUAmount,FUPurchaser,FUSupplier,FUStatus,
        //FUMATURITYDATE - TO_DATE('15-07-2016','DD-MM-YYYY'),PSLCAPRATE, FUOWNERENTITY
        lSql.append("SELECT FUId,FUMaturityDate,FUAmount,FUPurchaser,FUSupplier,FUStatus ");
    	lSql.append(" ,PSLCAPRATE AS FUAcceptedHaircut ");
        lSql.append(" FROM FactoringUnits ");
        lSql.append(" LEFT OUTER JOIN PURCHASERSUPPLIERCAPRATE ");
        lSql.append(" ON PSLCOUNTERENTITYCODE=FUPURCHASER AND PSLENTITYCODE= FUOWNERENTITY ");
        lSql.append(" AND PSLFROMHAIRCUT = 0 ");
        if(lSettlementDate!=null)
        	lSql.append(" AND FUMATURITYDATE - ").append(lDBHelper.formatDate(lSettlementDate)).append(" BETWEEN PSLFROMUSANCE AND PSLTOUSANCE ");
        lSql.append(" WHERE FURecordVersion > 0");
        lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lSql.append(" AND NOT EXISTS (SELECT FUWAuId FROM FactoringUnitWatch WHERE FUWFUId = FUId AND FUWAuId = ").append(pUserBean.getId()).append(")");

        if (!pShowAll) {
            lSql.append(" AND EXISTS (SELECT FASPurchaser FROM FinancierAuctionSettings WHERE FASPurchaser = FUPurchaser AND FASLevel = ");
            lSql.append(lDBHelper.formatString(FinancierAuctionSettingBean.Level.Financier_Buyer.getCode()));
            lSql.append(" AND FASActive = ").append(lDBHelper.formatString(CommonAppConstants.YesNo.Yes.getCode()));
    		lSql.append(" AND ( FASEXPIRYDATE IS NULL  OR FASEXPIRYDATE >= ").append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append("  ) ");
            lSql.append(" AND FASFinancier = ").append(lDBHelper.formatString(pUserBean.getDomain())).append(")");
        }
        
        if(lSettlementDate!=null){
    		if(pFilterBean.getFilterFromTenure()!=null)
    			lSql.append(" AND FUMATURITYDATE - ").append(lDBHelper.formatDate(lSettlementDate)).append(" >= ").append(pFilterBean.getFilterFromTenure());
    		if(pFilterBean.getFilterToTenure()!=null)
    			lSql.append(" AND FUMATURITYDATE - ").append(lDBHelper.formatDate(lSettlementDate)).append(" <= ").append(pFilterBean.getFilterToTenure());
        }

		if(pFilterBean.getCapRate()!=null)
			lSql.append(" AND PSLCAPRATE >= ").append(pFilterBean.getCapRate());
		if(pFilterBean.getFilterToCapRate()!=null)
			lSql.append(" AND PSLCAPRATE <= ").append(pFilterBean.getFilterToCapRate());
        
		if(pUserBean.hasUserLimit()){
			if(pUserBean.getMinUserLimit()!=null){
				lSql.append(" AND FUAmount >= ").append(pUserBean.getMinUserLimit());
			}
			if(pUserBean.getMaxUserLimit()!=null){
				lSql.append(" AND FUAmount <= ").append(pUserBean.getMaxUserLimit());
			}
		}
        BeanMeta lBeanMeta = factoringUnitDAO.getBeanMeta();
        Map<String, BeanFieldMeta> lFieldMap = lBeanMeta.getFieldMap();
        if (pFilterBean.getPurchaser() != null)
            lFieldMap.get("purchaser").appendAsSqlFilterForValue(lSql, pFilterBean.getPurchaser(), GenericDAO.Operator.EQUALS);
        if (pFilterBean.getSupplier() != null)
            lFieldMap.get("supplier").appendAsSqlFilterForValue(lSql, pFilterBean.getSupplier(), GenericDAO.Operator.EQUALS);
        if (pFilterBean.getAmount() != null)
            lFieldMap.get("amount").appendAsSqlFilterForValue(lSql, pFilterBean.getAmount(), GenericDAO.Operator.GREATERTHANEQUALTO);
        if (pFilterBean.getFilterAmount() != null)
            lFieldMap.get("amount").appendAsSqlFilterForValue(lSql, pFilterBean.getFilterAmount(), GenericDAO.Operator.LESSTHANEQUALTO);
        if (pFilterBean.getMaturityDate() != null)
            lFieldMap.get("maturityDate").appendAsSqlFilterForValue(lSql, pFilterBean.getMaturityDate(), GenericDAO.Operator.GREATERTHANEQUALTO);
        if (pFilterBean.getFilterMaturityDate() != null)
            lFieldMap.get("maturityDate").appendAsSqlFilterForValue(lSql, pFilterBean.getFilterMaturityDate(), GenericDAO.Operator.LESSTHANEQUALTO);
        lSql.append(" ORDER BY FUId");
        List<FactoringUnitBean> lBeans = factoringUnitDAO.findListFromSql(lConnection, lSql.toString(), -1);
        if(lBeans!=null && lBeans.size() > 0)
        {
        	Map<String, AppEntityBean> lAppEntity = new HashMap<String, AppEntityBean>();
        	FactoringUnitBean lFUBean = null;
        	AppEntityBean lPurchaser = null,lSupplier = null;
        	boolean lRemove = false;
        	for(int lPtr=lBeans.size()-1; lPtr >=0 ; lPtr--)
        	{
        		lFUBean = lBeans.get(lPtr);
        		lRemove = false;
        		//Purchaser
        		if(lAppEntity.containsKey(lFUBean.getPurchaser()))
        			lPurchaser = lAppEntity.get(lFUBean.getPurchaser());
        		else
        		{
        			lPurchaser = getAppEntityBean(lFUBean.getPurchaser());
        			lAppEntity.put(lPurchaser.getCode(), lPurchaser);
        		}
        		lRemove = lPurchaser.isFinancierBlocked(lAppEntityBean.getCode());
        		if(!lRemove){
            		//Supplier
            		if(lAppEntity.containsKey(lFUBean.getSupplier()))
            			lSupplier = lAppEntity.get(lFUBean.getSupplier());
            		else
            		{
            			lSupplier = getAppEntityBean(lFUBean.getSupplier());
            			lAppEntity.put(lPurchaser.getCode(), lPurchaser);
            		}
            		if(!lRemove && pFilterBean.getFilterMsmeStatus()!=null && !pFilterBean.getFilterMsmeStatus().equals(lSupplier.getMsmeStatus()))
            			lRemove = true;
            		if(!lRemove && pFilterBean.getFilterSellerCategory()!=null && !pFilterBean.getFilterSellerCategory().equals(lSupplier.getPromoterCategory()))
            			lRemove = true;
            		if(!lRemove && (pFilterBean.getFilterFromTenure()!=null && pFilterBean.getFilterFromTenure() > lFUBean.getTenure()))
            			lRemove = true;
            		if(!lRemove && (pFilterBean.getFilterToTenure()!=null && pFilterBean.getFilterToTenure() < lFUBean.getTenure()))
            			lRemove = true;
        		}
    			if(lRemove)
    				lBeans.remove(lPtr);
    			else{
    				lFUBean.setFilterMsmeStatus(lSupplier.getMsmeStatus());
    				lFUBean.setFilterSellerCategory(lSupplier.getPromoterCategory());
    			}
        	}
        }
        return lBeans;
    }
    
    public String findWatchListFin(ExecutionContext pExecutionContext, AppUserBean pUserBean) throws Exception {
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, true);
        Connection lConnection = pExecutionContext.getConnection();
        if (!lAppEntityBean.isPlatform() && !lAppEntityBean.isFinancier())
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        List<Map<String, Object>> lJsonList = new ArrayList<Map<String,Object>>();
        List<FactoringUnitBidBean> lList = getFactoringUnitInWatchWindow(lConnection,pUserBean, false);
        for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
            lJsonList.add(getFactoringUnitBidJsonFin(lConnection,lFactoringUnitBidBean, pUserBean));
        }
        return new JsonBuilder(lJsonList).toString();
    }

    public List<FactoringUnitBidBean> getFactoringUnitInWatchWindow(Connection pConnection, AppUserBean pUserBean, boolean pSetPurchaserRef) throws Exception{
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        lSql.append("SELECT * FROM FactoringUnits,Bids,FactoringUnitWatch, Instruments WHERE FURecordVersion > 0 AND FUId = BDFuId AND FUWFuId = FUId AND FUID = INFUID ");
        if(!(pUserBean!=null && AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))){
            lSql.append(" AND FUWAuId = ").append(pUserBean.getId());
        	lSql.append(" AND BDFinancierEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        }
        //lSql.append(" AND BDStatus = ").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
        //can't filter on bidding type since the record is needed to show the added factoring unit in the watch
        lSql.append(" AND FUStatus IN (").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lSql.append(",").append(lDBHelper.formatString(FactoringUnitBean.Status.Factored.getCode())).append(")");
        lSql.append(" AND (FUAcceptDateTime IS NULL OR FUAcceptDateTime > ").append(lDBHelper.formatDate(OtherResourceCache.getInstance().getCurrentDate())).append(")");
        //lSql.append(" AND (FUFinancier IS NULL OR FUFinancier = ").append(lDBHelper.formatString(pUserBean.getDomain())).append(")");
        lSql.append(" ORDER BY FUId");
        if(pUserBean!=null && AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()) )
        {
        	lSql = new StringBuilder();
    	   lSql.append("SELECT * FROM FactoringUnits LEFT OUTER JOIN Bids ");
           lSql.append(" ON BDFUId = FUId ");
           lSql.append(" LEFT OUTER JOIN FactoringUnitWatch ON FUWFuId = FUId AND FUWAuId = ").append(pUserBean.getId());
           lSql.append(" WHERE FURecordVersion > 0 ");
           lSql.append(" ORDER BY FUId");
        }
        List<FactoringUnitBidBean> lList = factoringUnitBidDAO.findListFromSql(pConnection, lSql.toString(), -1);
        if (pSetPurchaserRef) {
            Map<String, FinancierAuctionSettingBean> lPurchaserLimitMap = new HashMap<String, FinancierAuctionSettingBean>();
            FinancierAuctionSettingBean lFilterCreditLimitBean = new FinancierAuctionSettingBean();
            lFilterCreditLimitBean.setFinancier(pUserBean.getDomain());
            lFilterCreditLimitBean.setLevel(FinancierAuctionSettingBean.Level.Financier_Buyer);
            for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
                FactoringUnitBean lFactoringUnitBean = lFactoringUnitBidBean.getFactoringUnitBean();
                FinancierAuctionSettingBean lFinancierAuctionSettingBean = null;
                if (lPurchaserLimitMap.containsKey(lFactoringUnitBean.getPurchaser())) {
                    lFinancierAuctionSettingBean = lPurchaserLimitMap.get(lFactoringUnitBean.getPurchaser());
                } else {
                    lFilterCreditLimitBean.setPurchaser(lFactoringUnitBean.getPurchaser());
                    lFinancierAuctionSettingBean = financierAuctionSettingDAO.findBean(pConnection, lFilterCreditLimitBean);
                    lPurchaserLimitMap.put(lFactoringUnitBean.getPurchaser(), lFinancierAuctionSettingBean);
                }
                if (lFinancierAuctionSettingBean != null)
                    lFactoringUnitBean.setPurchaserRef(lFinancierAuctionSettingBean.getPurchaserRef());
            }
        }
    	HashMap<String, String> lBankHash = TredsHelper.getInstance().getBankName();
        for (FactoringUnitBidBean lBean : lList){
        	FactoringUnitBean lFactoringUnitBean = lBean.getFactoringUnitBean();
        	InstrumentBean lInstrumentBean = lBean.getInstrumentBean();
        	//SET BANK DETAILS PURCHASER 2param is purchaser flag
        	setBankDetails(pConnection,true ,lFactoringUnitBean.getPurchaser(), (lInstrumentBean.getPurSettleClId()!=null?lInstrumentBean.getPurSettleClId():lInstrumentBean.getPurClId()), lFactoringUnitBean ,lBankHash);
        	//SET BANK DETAILS SUPPLIER 2param is purchaser flag
        	setBankDetails(pConnection,false,lFactoringUnitBean.getSupplier(), (lInstrumentBean.getSupSettleClId()!=null?lInstrumentBean.getSupSettleClId():lInstrumentBean.getSupClId()), lFactoringUnitBean ,lBankHash);
        }
        return lList;
    }

    public String findHistoryFin(ExecutionContext pExecutionContext, AppUserBean pUserBean, FactoringUnitBean pFilterFUBean, ObligationBean pFilterObliBean) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
        //if (!lAppEntityBean.isFinancier())
        //    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        List<Map<String, Object>> lJsonList = new ArrayList<Map<String,Object>>();
        List<FactoringUnitBidBean> lList = getFactoringUnitHistory(lConnection,pUserBean, pFilterFUBean, pFilterObliBean);
        for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
            lJsonList.add(getFactoringUnitBidJsonFin(lConnection,lFactoringUnitBidBean, pUserBean));
        }
        return new JsonBuilder(lJsonList).toString();
    }

    public List<FactoringUnitBidBean> getFactoringUnitHistory(Connection pConnection, AppUserBean pUserBean, FactoringUnitBean pFilterFUBean, ObligationBean pFilterObliBean) throws Exception{
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        lSql.append("SELECT * FROM FactoringUnits,Instruments, Bids WHERE FURecordVersion > 0 AND INRecordVersion > 0 AND FUId = BDFuId AND INFUID = FUID ");
        //REMOVED THE FINANCIER CHECKING FROM FU SINCE OTHER NON ACCEPTED/EXPIRED/DELETED WERE NOT BEEN SHOWN 
        if(!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain())){
            lSql.append(" AND BDFINANCIERENTITY = ").append(lDBHelper.formatString(pUserBean.getDomain())); 
        }
        lSql.append(" AND FUStatus != ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lSql.append(" AND (FUAcceptDateTime IS NULL OR FUAcceptDateTime < ").append(lDBHelper.formatDate(TredsHelper.getInstance().getBusinessDate())).append(")");
        if(pFilterFUBean.getId()!=null){
        	lSql.append(" AND FUId = ").append(pFilterFUBean.getId());
        }
        if(CommonUtilities.hasValue(pFilterFUBean.getPurchaser())){
        	lSql.append(" AND FUPurchaser = ").append(lDBHelper.formatString(pFilterFUBean.getPurchaser()));
        }
        if(CommonUtilities.hasValue(pFilterFUBean.getSupplier())){
        	lSql.append(" AND FUSupplier = ").append(lDBHelper.formatString(pFilterFUBean.getSupplier()));
        }
        //all users having access for bidding must be able to see the factoring units in history tab.
        /*if(!AppUserBean.Type.Admin.equals(pUserBean.getType())){
        	lSql.append(" AND BDLASTAUID = ").append(pUserBean.getId());
        }*/
        lSql.append(" AND BDSTATUS IN ( ").append(lDBHelper.formatString(BidBean.Status.Accepted.getCode())).append(" , ")
        .append(lDBHelper.formatString(BidBean.Status.Auto_Reject.getCode())).append(" , ")
        .append(lDBHelper.formatString(BidBean.Status.Deleted.getCode())).append(" , ")
        .append(lDBHelper.formatString(BidBean.Status.Deleted_By_Owner.getCode())).append(" , ")
        .append(lDBHelper.formatString(BidBean.Status.Expired.getCode())).append(" , ")
        .append(lDBHelper.formatString(BidBean.Status.NotAccepted.getCode())).append(" ) ");
        if(pFilterFUBean.getMaturityDate()!=null ||
        		pFilterFUBean.getFilterMaturityDate()!=null || 
        		pFilterObliBean.getStatus()!=null || pFilterObliBean.getType() !=null ||
        		pFilterObliBean.getTxnType() !=null || pFilterObliBean.getPfId()!=null ){
        	lSql.append(" AND FUId IN ( ");
        	lSql.append(" SELECT OBFUID FROM OBLIGATIONS WHERE OBRecordVersion > 0 ");
        	lSql.append(" AND OBTXNENTITY = ").append(lDBHelper.formatString(pUserBean.getDomain()));
            if(pFilterFUBean.getMaturityDate()!=null){
                lSql.append(" AND OBDate >= ").append(lDBHelper.formatDate(pFilterFUBean.getMaturityDate()));
            }
            if(pFilterFUBean.getFilterMaturityDate()!=null){
                lSql.append(" AND OBDate <= ").append(lDBHelper.formatDate(pFilterFUBean.getFilterMaturityDate()));
            }
            if(pFilterObliBean.getStatus()!=null){
            	lSql.append(" AND OBSTATUS = ").append(lDBHelper.formatString(pFilterObliBean.getStatus().getCode()));
            }
            if(pFilterObliBean.getType()!=null){//Leg
            	lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(pFilterObliBean.getType().getCode()));
            }
            if(pFilterObliBean.getTxnType()!=null){
            	lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(pFilterObliBean.getTxnType().getCode()));
            }
            if(pFilterObliBean.getPfId()!=null){
            	lSql.append(" AND OBPFID = ").append(pFilterObliBean.getPfId().toString());
            }
            //lSql.append(" AND OBDate >= ").append(lDBHelper.formatDate(TredsHelper.getInstance().getBusinessDate()));
            lSql.append(" ) ");
        }
        lSql.append(" ORDER BY FUId");
        return  factoringUnitBidDAO.findListFromSql(pConnection, lSql.toString(), -1);
    }

    public String addToWatch(ExecutionContext pExecutionContext, List<Object> pFuIds, AppUserBean pUserBean) throws Exception {
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, true);
        if (!lAppEntityBean.isFinancier())
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        lSql.append("SELECT * FROM FactoringUnits ");
        lSql.append(" LEFT OUTER JOIN INSTRUMENTS ON INFUID = FUID ");
        lSql.append(" LEFT OUTER JOIN Bids ON BDFUId = FUId ");
        //lSql.append(" AND BDStatus IN (").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
        //lSql.append(",").append(lDBHelper.formatString(BidBean.Status.Accepted.getCode())).append(")");
        lSql.append(" AND BDFinancierEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        lSql.append(" LEFT OUTER JOIN FactoringUnitWatch ON FUWFuId = FUId AND FUWAuId = ").append(pUserBean.getId());
        lSql.append(" WHERE FURecordVersion > 0 ");
        lSql.append(" AND INRECORDVERSION > 0 ");
        lSql.append(" AND FUId IN (");
        for (Object lFuId : pFuIds)
            lSql.append(lFuId).append(",");
        lSql.setLength(lSql.length() - 1);
        lSql.append(")");
        lSql.append(" ORDER BY FUId");
        
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        List<FactoringUnitBidBean> lList = factoringUnitBidDAO.findListFromSql(lConnection, lSql.toString(), -1);
        List<Map<String, Object>> lResponse = new ArrayList<Map<String,Object>>();
        Map<String, FinancierAuctionSettingBean> lPurchaserLimitMap = new HashMap<String, FinancierAuctionSettingBean>();
        FinancierAuctionSettingBean lFilterCreditLimitBean = new FinancierAuctionSettingBean();
        AppEntityBean lAEPurchaser = null;
        lFilterCreditLimitBean.setFinancier(pUserBean.getDomain());
        lFilterCreditLimitBean.setLevel(FinancierAuctionSettingBean.Level.Financier_Buyer);
        for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
            FactoringUnitBean lFactoringUnitBean = lFactoringUnitBidBean.getFactoringUnitBean();
            checkPlatformStatus(lConnection, lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getSupplier());
            InstrumentBean lInstrumentBean = lFactoringUnitBidBean.getInstrumentBean();
            HashMap<String, String> lBankHash = TredsHelper.getInstance().getBankName();
        	//SET BANK DETAILS PURCHASER 2param is purchaser flag
        	setBankDetails(lConnection,true ,lFactoringUnitBean.getPurchaser(), (lInstrumentBean.getPurSettleClId()!=null?lInstrumentBean.getPurSettleClId():lInstrumentBean.getPurClId()), lFactoringUnitBean ,lBankHash);
        	//SET BANK DETAILS SUPPLIER 2param is purchaser flag
        	setBankDetails(lConnection,false,lFactoringUnitBean.getSupplier(), (lInstrumentBean.getSupSettleClId()!=null?lInstrumentBean.getSupSettleClId():lInstrumentBean.getSupClId()), lFactoringUnitBean ,lBankHash);
            FinancierAuctionSettingBean lFinancierAuctionSettingBean = null;
            if (lPurchaserLimitMap.containsKey(lFactoringUnitBean.getPurchaser())) {
                lFinancierAuctionSettingBean = lPurchaserLimitMap.get(lFactoringUnitBean.getPurchaser());
            } else {
                lFilterCreditLimitBean.setPurchaser(lFactoringUnitBean.getPurchaser());
                lFinancierAuctionSettingBean = financierAuctionSettingDAO.findBean(lConnection, lFilterCreditLimitBean);
                lPurchaserLimitMap.put(lFactoringUnitBean.getPurchaser(), lFinancierAuctionSettingBean);
            }
            String lError = null;
            if (lFinancierAuctionSettingBean == null)
                lError = "Credit limit not defined for buyer " + lFactoringUnitBean.getPurchaser();
            else if (lFinancierAuctionSettingBean.getActive() == FinancierAuctionSettingBean.Active.Suspended)
                lError = "Credit limit not active for buyer " + lFactoringUnitBean.getPurchaser();
            if(lFinancierAuctionSettingBean!=null){
            	if(lFinancierAuctionSettingBean.hasExpired()){
            		lError = "Credit limit expiry date reached for buyer " + lFactoringUnitBean.getPurchaser();
            	}
            }
            Map<String, Object> lMap = new HashMap<String, Object>();
            lMap.put("id", lFactoringUnitBean.getId());
            if (lFactoringUnitBean.getStatus() != FactoringUnitBean.Status.Active)
                lError = "Invalid status of factoring unit.";
            else if (lFactoringUnitBidBean.getFactoringUnitWatchBean().getAuId() != null)
                lError = "Factoring unit already added to watch";
            //financier blocked by purchaser
            lAEPurchaser = getAppEntityBean(lFactoringUnitBean.getPurchaser());
            if(lAEPurchaser.isFinancierBlocked(pUserBean.getDomain()))
            	lError = CommonBusinessException.ACCESS_DENIED;
            if (lError == null) {
                BidBean lBidBean = lFactoringUnitBidBean.getBidBean();
                AppConstants.CostCollectionLeg lCostLeg = null;

                //no split of cost and bearer is buyer then only consider the FinAuc settings
                if(!lFactoringUnitBean.isCostSplit() && lFactoringUnitBean.isPurchaserCompleteCostBearer()){
                    lCostLeg = lFinancierAuctionSettingBean.getPurchaserCostLeg();
                }else{
                    lCostLeg = AppConstants.CostCollectionLeg.Leg_1;
                }
/*                if (lFactoringUnitBean.getCostBearer() == AppConstants.CostBearer.Buyer)
                    lCostLeg = lFinancierAuctionSettingBean.getPurchaserCostLeg();
                else 
                    lCostLeg = AppConstants.CostCollectionLeg.Leg_1;*/
                if (lBidBean.getFuId() == null) {// bid record not inserted for financier
                    lBidBean.setFuId(lFactoringUnitBean.getId());
                    lBidBean.setFinancierEntity(pUserBean.getDomain());
                    lBidBean.setStatus(BidBean.Status.Active);
                    lBidBean.setCostLeg(lCostLeg);
                    bidDAO.insert(lConnection, lBidBean);
                } else if (lBidBean.getCostLeg() != lCostLeg) {
                    // if factoring unit is removed and re added to watch then costleg stored on bid is updated
                    lBidBean.setCostLeg(lCostLeg);
                    bidDAO.update(lConnection, lBidBean);
                }
                // insert record in factoringunitwatch
                FactoringUnitWatchBean lFactoringUnitWatchBean = lFactoringUnitBidBean.getFactoringUnitWatchBean();
                lFactoringUnitWatchBean.setAuId(pUserBean.getId());
                lFactoringUnitWatchBean.setFuId(lFactoringUnitBean.getId());
                factoringUnitWatchDAO.insert(lConnection, lFactoringUnitWatchBean);
                lMap.put("data", getFactoringUnitBidJsonFin(lConnection,lFactoringUnitBidBean, pUserBean));
            } else {
                lMap.put("error", lError);
            }
            lResponse.add(lMap);
        }
        pExecutionContext.commitAndDispose();
        return new JsonBuilder(lResponse).toString();
    }
    
    public String removeFromWatch(ExecutionContext pExecutionContext, List<Object> pFuIds, AppUserBean pUserBean) throws Exception {
        List<Long> lFuIds = new ArrayList<Long>();
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        String lFinancier = pUserBean.getDomain();
        BidBean lFilterBean = new BidBean();
        lFilterBean.setFinancierEntity(lFinancier);
        for (Object lFuId : pFuIds) {
            lFilterBean.setFuId(Long.valueOf(lFuId.toString()));
            BidBean lBidBean = bidDAO.findBean(lConnection, lFilterBean);
            if ( pUserBean.getId().equals(lBidBean.getFinancierAuId()) &&
            		BidBean.Status.Active.equals(lBidBean.getStatus()) &&
            		(lBidBean.getRate() != null || lBidBean.getProvRate() !=null ) ) 
                throw new CommonBusinessException("Cannot remove factoring unit " + lBidBean.getFuId() + " from watch. There is an active bid.");
            FactoringUnitWatchBean lFactoringUnitWatchBean = new FactoringUnitWatchBean();
            lFactoringUnitWatchBean.setFuId(lFilterBean.getFuId());
            lFactoringUnitWatchBean.setAuId(pUserBean.getId());
            factoringUnitWatchDAO.delete(lConnection, lFactoringUnitWatchBean);
            lFuIds.add(lBidBean.getFuId());
        }
        pExecutionContext.commitAndDispose();
        return new JsonBuilder(lFuIds).toString();
    }
    
    public String updateBid(ExecutionContext pExecutionContext, List<Object> pFuIds, BidBean pBidBean, AppUserBean pUserBean) throws Exception {
        AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        Set<Long> lAutoConfirmedFuIds = new HashSet<Long>();
        Map<String, Object> lBidMail = null;
        List<BidBean> lBestBidDown = new ArrayList<BidBean>();
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        AppEntityBean lFinEntityBean = (lAppEntityBean.isFinancier()?lAppEntityBean:null); 
        List<Map<String, Object>> lBidMailList = new ArrayList<Map<String,Object>>();
        if ((lAuctionCalendarBean == null) || (lAuctionCalendarBean.getStatus() != AuctionCalendarBean.Status.Bidding))
            throw new CommonBusinessException("Bidding not allowed at this time.");
        if (lAuctionCalendarBean.getActive() == CommonAppConstants.YesNo.No)
            throw new CommonBusinessException("Bidding has been stopped.");
        List<Map<String, Object>> lResponse = new ArrayList<Map<String,Object>>();
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        Map<Object, FactoringUnitBidBean> lMap = getFinancierBids(lConnection, pFuIds, pUserBean);
        
        boolean lForChecking = (pBidBean.getAppStatus() != null); // true indicates that update is being done by a checker
        boolean lForDelete = (pBidBean.getRate() == null);
        boolean lHasChecker = false;// true indicates that bid is being entered by a user who has a checker
        boolean lHideBidOnModify = false;// true indicates when maker modifies a bid, the old bid is withdrawn. Actual setting in financierauctionsettings at self level
        if (!lForChecking) {
            List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(lConnection, pUserBean.getId(), MakerCheckerMapBean.CheckerType.Bid);
            lHasChecker = (lCheckers != null) && !lCheckers.isEmpty();
        }
        
        // entry / modification request : set values to default for missing input fields
        if (!lForChecking && !lForDelete) { // add/modify bid
            if (pBidBean.getValidTill() == null)
                pBidBean.setValidTill(lAuctionCalendarBean.getDate());
            else if (pBidBean.getValidTill().compareTo(lAuctionCalendarBean.getDate()) < 0)
                throw new CommonBusinessException("Valid Till cannot be a past date.");
            if (pBidBean.getHaircut() == null)
                pBidBean.setHaircut(BigDecimal.ZERO);
            if (pBidBean.getBidType() == null) {
                pBidBean.setBidType(BidType.Reserved);
            }
        }
        
        ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        // get bid rate bands to check if rate is within the band of various settings and update limit utilisations
        FactoringUnitBidBean lFactoringUnitBidBean = null;
        Map<String, Object> lReturnFuData =  null;
        Map<String,String> lErrorMsgs = new HashMap<String, String>();
        for (Object lFuId : pFuIds) {
        	try{
                boolean lNewBid = false; // new bid post expiry or deletion
                BidBean.Status lOldStatus = null;
                lFactoringUnitBidBean = lMap.get(Long.valueOf(lFuId.toString()));
                if (lFactoringUnitBidBean == null) {
                    throw new CommonBusinessException("Factoring unit is not active");
                }
                FactoringUnitBean lFactoringUnitBean = lFactoringUnitBidBean.getFactoringUnitBean();
                BidBean lOldBidBean = lFactoringUnitBidBean.getBidBean(); //TODO: check if the first time bid is null, if yes then create new bidbean.
                Long lChkLevel = lOldBidBean.getChkLevel();
                BidBean lNewBidBean = new BidBean();
                checkPlatformStatus(lConnection, lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getSupplier());
                if (lOldBidBean != null){
                	lOldStatus = lOldBidBean.getStatus();
                	if (BidBean.Status.Accepted.equals(lOldBidBean.getStatus())){
                		throw new CommonBusinessException("Bid already accepted.");
                	}
                	if (BidBean.Status.NotAccepted.equals(lOldBidBean.getStatus())){
                		throw new CommonBusinessException("Bid already unaccepted.");
                	}
                	if(!lForChecking && lForDelete &&
                			( BidBean.Status.Deleted.equals(lOldBidBean.getStatus()) ||
                					BidBean.Status.Deleted_By_Owner.equals(lOldBidBean.getStatus()) ||
                					BidBean.Status.Expired.equals(lOldBidBean.getStatus()) )){
                		throw new CommonBusinessException("Bid already deleted.");
                	}
                	if (!BidBean.Status.Active.equals(lOldBidBean.getStatus())){
                		lOldBidBean.setId(null);
                		lOldBidBean.setStatus(BidBean.Status.Active);;
                		lOldBidBean.setFuId(lFactoringUnitBean.getId());
                		lOldBidBean.setFinancierEntity(pUserBean.getDomain());
                		lOldBidBean.setFinancierAuId(pUserBean.getId());
    					//the cost leg will remain same as pervious ie. subsequently same as the condition when the fu was added to the watch window
                		lNewBid = true;
                	}
                    bidDAO.getBeanMeta().copyBean(lOldBidBean, lNewBidBean);
                    if(lFinEntityBean.hasHierarchicalChecker(AppConstants.BID_CHECKER)){
                		lNewBidBean.setChkLevel(lChkLevel);
                	}
                }
                String lKey = lFactoringUnitBean.getPurchaser() + CommonConstants.KEY_SEPARATOR + lFactoringUnitBean.getSupplier();
                //
                List<FinancierAuctionSettingBean> lSettings = getFinancierAuctionSettings(lConnection, pUserBean.getDomain(), lFactoringUnitBean.getPurchaser(), 
                        lFactoringUnitBean.getSupplier(), lForChecking?lNewBidBean.getFinancierAuId():pUserBean.getId(), false);
                // if it is a maker action entry/mod/del
                if (!lForChecking && lHasChecker) {
                    // get financier auction setting at financier self level and check the flag
                    for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lSettings) {
                        if (lFinancierAuctionSettingBean.getLevel() == FinancierAuctionSettingBean.Level.Financier_Self) {
                            // TODO
                            if (lForDelete) // delete action. Check if checker is disabled for delete
                                lHasChecker = (lFinancierAuctionSettingBean.getBypassCheckForDelete() == null);
                            else // add/mod action. Check if original bid is to be removed upon modification
                                lHideBidOnModify = (lFinancierAuctionSettingBean.getWithdrawBidModChecker() == CommonAppConstants.Yes.Yes);
                        }
                    }
                }
                //
                AppEntityBean lAEPurchaser = getAppEntityBean(lFactoringUnitBean.getPurchaser());

                if (lForChecking) { // checker user action
                    // access check
                    if (!appUserBO.isValidChecker(lConnection, lOldBidBean.getFinancierAuId(), pUserBean.getId(), MakerCheckerMapBean.CheckerType.Bid)){
                    	if (BidBean.Status.Active.equals(lOldStatus)){
                    		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED +" Please refresh before performing any action.");
                    	}else{
                    		throw new CommonBusinessException("Current bid status is " + lOldStatus.toString()+". Please refresh before performing any action.");
                    	}
                    }
                    if (!BidBean.AppStatus.Pending.equals(lOldBidBean.getAppStatus()))
                        throw new CommonBusinessException("Bid cannot be approved/rejected now.");
                    if (pBidBean.getAppStatus() == BidBean.AppStatus.Approved) {
                        if (lNewBidBean.getProvAction() == BidBean.ProvAction.Cancel) {
                            lNewBidBean.setStatus(BidBean.Status.Deleted);
                        } else { // entry modify
                            lNewBidBean.setRate(lNewBidBean.getProvRate());
                            lNewBidBean.setHaircut(lNewBidBean.getProvHaircut());
                            lNewBidBean.setBidType(lNewBidBean.getProvBidType());
                            lNewBidBean.setValidTill(lNewBidBean.getProvValidTill());
                            lNewBidBean.clearProvBid();
                            //check financiers blocked by purchasers
                            if (lAEPurchaser.isFinancierBlocked(pUserBean.getDomain()))
                                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
                            validateBidRate(lSettings, lNewBidBean.getRate(), lFactoringUnitBean.getTenure(),lNewBidBean.getValidTill());
                            if ((pBidBean.getValidTill() != null) && (pBidBean.getValidTill().compareTo(lFactoringUnitBean.getFactorEndDateTime()) > 0))
                                throw new CommonBusinessException("Valid Till cannot be greater than factoring period end date for the instrument.");
                            boolean lIsGroupedInstrument = isGroupedInstrument(lConnection, lNewBidBean.getFuId());
                            validateFinancierMandate(lConnection, lFactoringUnitBean, pUserBean.getDomain(), lNewBidBean.getHaircut(), lNewBidBean.getRate(), lNewBidBean.getCostLeg(), lNewBidBean.getValidTill() , lIsGroupedInstrument);
                        }
                    }
                    lNewBidBean.setAppStatus(pBidBean.getAppStatus());
                    lNewBidBean.setAppRemarks(pBidBean.getAppRemarks());
                    lNewBidBean.setCheckerAuId(pUserBean.getId());
                } else { // maker user action
                    // access check
                	if( BidBean.Status.Active.equals(lOldBidBean.getStatus()) ){
                        if ((lOldBidBean.getFinancierAuId() != null) && (pUserBean.getType() != AppUserBean.Type.Admin)) {
                            if (!pUserBean.getId().equals(lOldBidBean.getFinancierAuId()))
                                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
                        }
                	}
                    
                    if (lForDelete) { // cancel bid
                        // already deleted
                        if ((lOldBidBean == null) || (lOldBidBean.getStatus() == BidBean.Status.Deleted) || (lOldBidBean.getStatus() == BidBean.Status.Deleted_By_Owner))
                            continue;
                        if (lHasChecker) { // checker exists
                            if (lNewBidBean.getAppStatus() == BidBean.AppStatus.Pending) { // unapproved prov bid
                                lNewBidBean.setAppStatus(BidBean.AppStatus.Withdrawn);
                            } else { // approved bid
                                lNewBidBean.setProvRate(lNewBidBean.getRate());
                                lNewBidBean.setProvHaircut(lNewBidBean.getHaircut());
                                lNewBidBean.setProvBidType(lNewBidBean.getBidType());
                                lNewBidBean.setProvValidTill(lNewBidBean.getValidTill());
                                lNewBidBean.setProvAction(BidBean.ProvAction.Cancel);
                                lNewBidBean.setAppStatus(BidBean.AppStatus.Pending);
                                if (lFinEntityBean!=null) {
                                	if(lFinEntityBean.hasHierarchicalChecker(AppConstants.BID_CHECKER)){
                                		List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(lConnection, pUserBean.getId(),MakerCheckerMapBean.CheckerType.Bid);
                                		Long lMinLevel = instrumentBO.getLevel(lConnection,lCheckers,pUserBean.getId(),CheckerType.Bid, true);
                                		lNewBidBean.setChkLevel(new Long(lMinLevel));
                                	}
                                }
                            }
                        } else { // checker does not exist
                            lNewBidBean.setAppStatus(null);
                            lNewBidBean.setStatus(BidBean.Status.Deleted);
                        }
                        lNewBidBean.setCheckerAuId(null);
                    } else {
                        if (lAEPurchaser.isFinancierBlocked(pUserBean.getDomain()))
                            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
                        validateBidRate(lSettings, pBidBean.getRate(), lFactoringUnitBean.getTenure(),pBidBean.getValidTill());
                        if ((pBidBean.getValidTill() != null) && (pBidBean.getValidTill().compareTo(lFactoringUnitBean.getFactorEndDateTime()) > 0))
                            throw new CommonBusinessException("Valid Till cannot be greater than factoring period end date for the instrument.");
                        if (lOldBidBean.getId() == null)
                            lNewBidBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, "Bids.Id"));
                        lNewBidBean.setFinancierAuId(pUserBean.getId()); // ???
                        lNewBidBean.setStatus(BidBean.Status.Active);
                        lNewBidBean.setProvRate(lHasChecker?pBidBean.getRate():null);
                        lNewBidBean.setProvHaircut(lHasChecker?pBidBean.getHaircut():null);
                        lNewBidBean.setProvValidTill(lHasChecker?pBidBean.getValidTill():null);
                        lNewBidBean.setProvBidType(lHasChecker?pBidBean.getBidType():null);
                        lNewBidBean.setProvAction(lHasChecker?(lOldBidBean.getRate()==null?BidBean.ProvAction.Entry:BidBean.ProvAction.Modify):null);
                        lNewBidBean.setAppStatus(lHasChecker?BidBean.AppStatus.Pending:BidBean.AppStatus.Approved);
                        if (lFinEntityBean!=null) {
                        	if(lFinEntityBean.hasHierarchicalChecker(AppConstants.BID_CHECKER)){
                        		List<MakerCheckerMapBean> lCheckers = appUserBO.getCheckers(lConnection, pUserBean.getId(),MakerCheckerMapBean.CheckerType.Bid);
                        		if (!lCheckers.isEmpty()) {
                        			Long lMinLevel = instrumentBO.getLevel(lConnection,lCheckers,pUserBean.getId(),CheckerType.Bid, true);
                        			if(lMinLevel!=null) {
                        				lNewBidBean.setChkLevel(new Long(lMinLevel));
                        			}
                        		}
                        	}
                        }
                        if(lHasChecker){
                        	//check provisional values
                            boolean lIsGroupedInstrument = isGroupedInstrument(lConnection, lNewBidBean.getFuId());
                            validateFinancierMandate(lConnection, lFactoringUnitBean, pUserBean.getDomain(), lNewBidBean.getProvHaircut(), lNewBidBean.getProvRate(), lNewBidBean.getCostLeg(), lNewBidBean.getProvValidTill(), lIsGroupedInstrument);
                        }
                        if (!lHasChecker) {
                            lNewBidBean.setRate(pBidBean.getRate());
                            lNewBidBean.setHaircut(pBidBean.getHaircut());
                            lNewBidBean.setValidTill(pBidBean.getValidTill());
                            lNewBidBean.setBidType(pBidBean.getBidType());
                            //
                        	//no checker
                            boolean lIsGroupedInstrument = isGroupedInstrument(lConnection, lNewBidBean.getFuId());
                            validateFinancierMandate(lConnection, lFactoringUnitBean, pUserBean.getDomain(), lNewBidBean.getHaircut(), lNewBidBean.getRate(), lNewBidBean.getCostLeg(), lNewBidBean.getValidTill(), lIsGroupedInstrument);
                        } else if (lHideBidOnModify) {
                            lNewBidBean.clearFinalBid();
                        }
                    } // end if lForDelete
                } // end if lForChecking
                
                boolean lFinalBidDeleted = (lNewBidBean.getStatus() == BidBean.Status.Deleted) 
                        || (lNewBidBean.getStatus() == BidBean.Status.Deleted_By_Owner);
                boolean lFinalBidChanged = valuesChanged(new Object[]{lNewBidBean.getRate(), lNewBidBean.getBidType(), lNewBidBean.getHaircut()}, 
                        new Object[]{lOldBidBean.getRate(), lOldBidBean.getBidType(), lOldBidBean.getHaircut()});
                
                // compute limits if bid is deleted or final bid image changed or 
                if (lFinalBidChanged || lFinalBidDeleted) {
                    // update limit utilisations
                    if (lFinalBidDeleted || (lNewBidBean.getRate() == null)) {
                        lNewBidBean.setLimitUtilised(BigDecimal.ZERO);
                        lNewBidBean.setBidLimitUtilised(BigDecimal.ZERO);
                        lNewBidBean.setLimitIds(null);
                    } else {
                        BigDecimal[] lLegAmounts = computeLegInterest(lFactoringUnitBean, lConfirmationWindowBean.getSettlementDate(), lNewBidBean.getHaircut(), 
                                lNewBidBean.getRate(), lNewBidBean.getCostLeg());
                        
                      //set the factored amount
                		BigDecimal lLeg2Amt = null;
                        lLeg2Amt = lFactoringUnitBean.getAmount().add(lLegAmounts[INT_PUR_LEG2]);
                        
                        //setting the second leg oblig amount
                        if(BidBean.BidType.Reserved.equals(lNewBidBean.getBidType()))
                            lNewBidBean.setLimitUtilised(lLeg2Amt);
                        else
                            lNewBidBean.setLimitUtilised(BigDecimal.ZERO);
                        lNewBidBean.setBidLimitUtilised(lLeg2Amt);
                    }
                    updateBidLimits(lConnection, lFactoringUnitBean, lOldBidBean, lNewBidBean, pUserBean, lSettings, false, false);
                }

                // bid entry withdrawal, bid status will be set to deleted
                if ((lNewBidBean.getRate() == null) && ((lNewBidBean.getProvRate() == null) 
                        || (lNewBidBean.getAppStatus() == BidBean.AppStatus.Withdrawn) || (lNewBidBean.getAppStatus() == BidBean.AppStatus.Rejected))) {
                    lNewBidBean.setStatus(BidBean.Status.Deleted);
                    lFinalBidDeleted = true;
                }
                lNewBidBean.setTimestamp(new Timestamp(System.currentTimeMillis()));
                lNewBidBean.setLastAuId(pUserBean.getId());
                TredsHelper.getInstance().validateUserLimit(pUserBean, lFactoringUnitBean.getAmount());
        		if(lForChecking && lHasChecker){
                    TredsHelper.getInstance().validateCheckersLimit(lConnection, pUserBean.getId(), lFactoringUnitBean.getAmount(), null);
        		}
                bidDAO.insertAudit(lConnection, lNewBidBean, (lNewBid?GenericDAO.AuditAction.Insert:GenericDAO.AuditAction.Update), pUserBean.getId());
                if (lFinalBidDeleted) {
                    lNewBidBean.clearFinalBid();
                    lNewBidBean.setFinancierAuId(null);
                }
                if (lForChecking || lFinalBidDeleted || (lNewBidBean.getAppStatus() == BidBean.AppStatus.Withdrawn)) {
                    lNewBidBean.clearProvBid();
                    if (lFinEntityBean!=null && lFinEntityBean.hasHierarchicalChecker(AppConstants.BID_CHECKER)) {
                    	if (lNewBidBean.getChkLevel()!=null) lNewBidBean.setChkLevel(null);
                    }
                }
                if (lForChecking && AppStatus.Approved.equals(lNewBidBean.getAppStatus())) {
                	if(lFinEntityBean.hasHierarchicalChecker(AppConstants.BID_CHECKER)){
                		if(instrumentBO.hasAccessToLevel(lChkLevel,pUserBean.getBidLevel(),lAppEntityBean.getBidLevel())) {
                			bidDAO.insertAudit(lConnection, lNewBidBean, GenericDAO.AuditAction.Update, pUserBean.getId());
                			Long lNextlevel = instrumentBO.getNextLevel(lConnection, lNewBidBean.getFinancierAuId(),lChkLevel, CheckerType.Bid);
    						if (lNextlevel != null) {
	                			lNewBidBean.setChkLevel(lNextlevel);
	        					bidDAO.update(lConnection, lNewBidBean, InstrumentBean.FIELDGROUP_CHECKERLEVEL);
	        					pExecutionContext.commitAndDispose();
	        					Map<String, Object> lJson = new HashMap<String, Object>();
	        	    			lJson.put("id", lNewBidBean.getFuId());
	        	    			lJson.put("message", "Sucess");
	        	                lResponse.add(lJson);     
	        					return new JsonBuilder(lResponse).toString();
    						}
                		}
                	}
                }
                BigDecimal lFactoredAmount = lFactoringUnitBean.getAmount();
                if(pBidBean.getHaircut() != null && pBidBean.getHaircut().doubleValue()>0){
                	lFactoredAmount = lFactoringUnitBean.getAmount().multiply(BigDecimal.valueOf(100.0-pBidBean.getHaircut().doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
                }
                Object[] lCharges = getSplitPlanDetails(lConnection, lFactoringUnitBean, lFactoringUnitBean.getPurchaser(), lOldBidBean.getFinancierEntity(), lFactoredAmount, lFinEntityBean,null);
                //0=Financier Normal Percent
            	//1=Financier Normal Amount
            	//2=ChargeBearer Split Charge percent
            	//3=Split Min value
            	//4=Financier Split Charge percent
                lNewBidBean.setNormalPercent((BigDecimal)lCharges[0]);
                lNewBidBean.setNormalMinAmt((BigDecimal)lCharges[1]);
                lNewBidBean.setSplitChargeBearerPercent((BigDecimal)lCharges[2]);
                lNewBidBean.setSplitMinCharge((BigDecimal)lCharges[3]);
                lNewBidBean.setSplitPercent((BigDecimal)lCharges[4]);
                lNewBidBean.setNormalMaxAmt((BigDecimal)lCharges[5]);
                lNewBidBean.setNormalChargeType((AuctionChargeSlabBean.ChargeType)lCharges[6]);
                Map<String,Object> lBidChargesMap = new HashMap<String, Object>();
                lBidChargesMap.put("normalPercent", lNewBidBean.getNormalPercent());
                lBidChargesMap.put("normalMinAmt", lNewBidBean.getNormalMinAmt());
                lBidChargesMap.put("normalMaxAmt", lNewBidBean.getNormalMaxAmt());
                lBidChargesMap.put("normalChargeType", lNewBidBean.getNormalChargeType());
                lBidChargesMap.put("splitChargeBearerPercent", lNewBidBean.getSplitChargeBearerPercent());
                lBidChargesMap.put("splitMinCharge", lNewBidBean.getSplitMinCharge());
                lBidChargesMap.put("splitPercent", lNewBidBean.getSplitPercent());
                lNewBidBean.setCharges(new JsonBuilder(lBidChargesMap).toString());
                bidDAO.update(lConnection, lNewBidBean, BeanMeta.FIELDGROUP_UPDATE);
                //
//                if (MonetagoTredsHelper.getInstance().performMonetagoCheck() && lNewBidBean.getRate()!= null && isGroupedInstrument(lConnection, lNewBidBean.getFuId())){
//    	            InstrumentBean lInstBean = new InstrumentBean();
//    	            lInstBean.setFuId(lNewBidBean.getFuId());
//    	            lInstBean = instrumentDAO.findBean(lConnection, lInstBean);
//    	            List<InstrumentBean> lFactoredInstList = getInstrumentsFactoredByMonetago(lConnection, lInstBean, pUserBean.getId());
//    	            if(lFactoredInstList!=null && !lFactoredInstList.isEmpty()){
//    	            	lConnection.rollback();
//    	            	String lInstNoMsg = reStructureInstrumentBean(lConnection, lInstBean, lFactoredInstList, pUserBean);
//    	            	throw new CommonBusinessException(lInstNoMsg+ " Grouped instrument " + lInstBean.getId() + " has been restructured. Please refresh.");
//    	            }
//                }
                //
                lFactoringUnitBidBean.setBidBean(lNewBidBean);
                if (lFinalBidChanged || lFinalBidDeleted) {
                    boolean lCurrentBest = false;// indicates current bid is best 
                    boolean lNewBest = false;// indicates new best has to be computed
                    boolean lBestBidChanged = false;
                    Long lOldBestBidId = new Long(0);
                    if (lFactoringUnitBean.getBdId() == null &&
                    		lNewBidBean.getId() != null && !lFinalBidDeleted ) // first bid
                    	//&& lNewBidBean.getRate() != null 
                		lCurrentBest = true;
                    else if (lNewBidBean.getId() == null){
                    	// the financier added the fu to the watch and then removed it
                    	//hence no reason to change the best bid
                    }
                    else if ( lFactoringUnitBean.getBdId() == null ||
                    		lFactoringUnitBean.getBdId().equals(lNewBidBean.getId())) { // current bid is already best bid
                        if (lFinalBidDeleted){
                            lNewBest = true;
                        }
                        else { // bid type wont be checked since both are at par.
                        	if(lHideBidOnModify){
                        		lNewBest = true;
                        	}else if (lNewBidBean.getRate().compareTo(lFactoringUnitBean.getAcceptedRate()) <= 0){
                            	// bid rate bettered. Retain and update best bid
                                lCurrentBest = true;
                            }
                            else if (lNewBidBean.getRate().compareTo(lFactoringUnitBean.getAcceptedRate()) > 0){
                            	// bid rate worsened. Get new best bid
                                lNewBest = true;
                            }
                        }
                    } else {
                    	if(lNewBidBean.getRate()!=null){
                            if (lNewBidBean.getRate().compareTo(lFactoringUnitBean.getAcceptedRate()) < 0){
                            	// current bid rate is better than best bid
                                lCurrentBest = true;
                            }
                    	}
                    }
                    if(lCurrentBest){
                    	lBestBidChanged = true;
                    	lOldBestBidId = lFactoringUnitBean.getBdId();
                    }
                    //PRASAD
                    if (lCurrentBest)
                        updateBestBidWithBidBean(lConnection, lFactoringUnitBean, lNewBidBean);
                    else if (lNewBest)
                        updateBestBid(lConnection, lFactoringUnitBean,null);
                    
                    // send bid email
                    if(!(lForChecking && BidBean.AppStatus.Pending.equals(lNewBidBean.getAppStatus()))){
                    	if(lNewBidBean.getId()!=null)
                    		lBidMail = emailGeneratorBO.createBidManageEmail(lConnection, lFactoringUnitBean, lOldBidBean, lNewBidBean, pUserBean);
                    	else
                        	lBidMail = null;
                    }else{
                    	lBidMail = null;
                    }
                    if(lBestBidChanged && lOldBestBidId!=null){
                    	BidBean pBidFilterBean = new BidBean();
                    	pBidFilterBean.setId(lOldBestBidId);
                    	pBidFilterBean.setStatus(BidBean.Status.Active);
                    	BidBean lOldBestBidBean = bidDAO.findBean(lConnection, pBidFilterBean);
                    	if(lOldBestBidBean!=null){
                    		if(lNewBidBean!=null && !lOldBestBidBean.getFinancierEntity().equals(lNewBidBean.getFinancierEntity())){
                        		lBestBidDown.add(lOldBestBidBean);
                    		}
                    	}
                    }
                    // auto accept bid if onreceiptbid enabled.
                    if ((lNewBidBean.getRate() != null) && AutoAcceptBid.OnRecepitOfBid.equals(lFactoringUnitBean.getAutoAccept())) {
                        if(lConfirmationWindowBean!=null && lConfirmationWindowBean.getStatus() == Status.Open) {
                            try {
                                logger.info("\tAuto confirming FUId on Receipt : " + lFactoringUnitBean.getId() + " Bid : " + lFactoringUnitBean.getBdId());
                                List<PurchaserSupplierCapRateBean> lCapRateList = getCapRateList(lConnection, 
                                        lFactoringUnitBean, lConfirmationWindowBean.getSettlementDate());
                                boolean lAutoConfirmed = acceptBid(lConnection, lFactoringUnitBean, lNewBidBean, pUserBean, 
                                        lConfirmationWindowBean.getSettlementDate(), lCapRateList);
                                if(lAutoConfirmed){
                                	if(!lAutoConfirmedFuIds.contains(lFactoringUnitBean.getId()) && 
                                		FactoringUnitBean.Status.Factored.equals(lFactoringUnitBean.getStatus())){
                                		lAutoConfirmedFuIds.add(lFactoringUnitBean.getId());
                                	}
                                }
                                logger.info("Auto confirming bids completed - updateBid");                            
                            }
                            catch(CommonBusinessException lException) {
                                logger.error("Error while autoconfirming bids", lException);
                                //if the bids auto acceptance is on receipt then log the error to the statusRemark of the bid.
                                logBidError(lNewBidBean, lException.getMessage(), pUserBean);
                                lBidMail = null;
                                lErrorMsgs.put(lFuId.toString(), "Auto accept for FuId " + lFuId +" failed : "+ lException.getMessage());
                            }
                        }
                    }
                    if(lBidMail !=null && lBidMail.size() > 0){
                    	lBidMailList.add(lBidMail);
                    }
                }
                lConnection.commit();
        	}catch(Exception lEx){
        		logger.error("Error in updateBid for FUId : " + lFuId , lEx);
                lErrorMsgs.put(lFuId.toString(), "Bid not placed for the Factoring unit. Err Msg : "+ lEx.getMessage());
        	}
        }// end for fuids
        pExecutionContext.commitAndDispose();
        // send email
        if(lBidMailList!=null && lBidMailList.size() > 0){
        	for(int lPtr=0; lPtr < lBidMailList.size(); lPtr++){
        		lBidMail = lBidMailList.get(lPtr);
        		for(String lTemplate : lBidMail.keySet()){
                    EmailSender.getInstance().addMessage(lTemplate, (Map<String,Object>) lBidMail.get(lTemplate));
        		}
        	}
        }
        if(lBestBidDown!=null && lBestBidDown.size() > 0){
            Connection lConn = pExecutionContext.getConnection();
        	for(BidBean lOldBestBidBean : lBestBidDown){
            	emailGeneratorBO.sendEmailBestBidChanged(lConn, lOldBestBidBean);
        	}
        }
        //refetch the details so that there is no mismatch in the status of fu an bids
        Connection lNewConn = pExecutionContext.getConnection();
        lMap = getFinancierBids(lNewConn, pFuIds, pUserBean);
        for(Object lFuId : pFuIds){
            lFactoringUnitBidBean = lMap.get(Long.valueOf(lFuId.toString()));
    		if(lFactoringUnitBidBean!=null){
    			lReturnFuData =  getFactoringUnitBidJsonFin(lNewConn,lFactoringUnitBidBean, pUserBean);
    			if(lErrorMsgs.containsKey(lFuId.toString())){
        			lReturnFuData.put("error", lErrorMsgs.get(lFuId.toString()));
    			}
    			lReturnFuData.put("id", lFuId.toString());
                lResponse.add(lReturnFuData);
    		}
        }
        if(lAutoConfirmedFuIds!=null){
        	for(Long lFuId : lAutoConfirmedFuIds){
        		Map<String, Object> lJson = new HashMap<String, Object>();
    			lJson.put("id", lFuId.toString());
    			lJson.put("message", "Bid AutoConfirmed - Factoring Unit Factored.");
                lResponse.add(lJson);        		
        	}
        }
        //
        return new JsonBuilder(lResponse).toString();
    }
    
    private void updateBidLimits(Connection pConnection, FactoringUnitBean pFactoringUnitBean, BidBean pOldBidBean, BidBean pNewBidBean, 
            AppUserBean pUserBean, List<FinancierAuctionSettingBean> pSettings, boolean pAcceptance, boolean pReleaseLimit) throws Exception{
        List<FinancierAuctionSettingBean> lSettings = pSettings;
        if (lSettings == null)
            lSettings = getFinancierAuctionSettings(pConnection, pNewBidBean.getFinancierEntity(), pFactoringUnitBean.getPurchaser(), 
                pFactoringUnitBean.getSupplier(), pNewBidBean.getFinancierAuId(), false);
        boolean lValidateNewBid = !(BidBean.Status.Deleted.equals(pNewBidBean.getStatus()) || BidBean.Status.Deleted_By_Owner.equals(pNewBidBean.getStatus()) || BidBean.Status.Expired.equals(pNewBidBean.getStatus()));
        boolean lDeletedOldBid = (pOldBidBean!=null && pOldBidBean.getStatus()!=null && (BidBean.Status.Deleted.equals(pOldBidBean.getStatus()) || BidBean.Status.Deleted_By_Owner.equals(pOldBidBean.getStatus()) || BidBean.Status.Expired.equals(pOldBidBean.getStatus())));
        boolean lSelf = false, lPurchaser = false, lSystemBuyer = false;
        StringBuilder lNewLimitIdCsv = new StringBuilder();
        HashSet<Long> lOldLimitIds = new HashSet<Long>();
        if(!lDeletedOldBid && CommonUtilities.hasValue(pOldBidBean.getLimitIds())){
        	String[] lTemp = pOldBidBean.getLimitIds().split(",");
        	if(lTemp!=null){
        		for(int lPtr=0; lPtr < lTemp.length; lPtr++){
            		lOldLimitIds.add(Long.valueOf(lTemp[lPtr]));
        		}
        	}
        }
        //old limits can be null in case where fu is placed in the watch but no explicit bid has been place
        if(pOldBidBean.getLimitUtilised() == null) pOldBidBean.setLimitUtilised(BigDecimal.ZERO);
        if(pOldBidBean.getBidLimitUtilised() == null)pOldBidBean.setBidLimitUtilised(BigDecimal.ZERO);
        
        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lSettings) {
            BigDecimal lDeltaUtilised = BigDecimal.ZERO;
            //
            if(lFinancierAuctionSettingBean.getUtilised()==null) lFinancierAuctionSettingBean.setUtilised(BigDecimal.ZERO);
            if(lFinancierAuctionSettingBean.getBidLimit()==null) lFinancierAuctionSettingBean.setBidLimit(BigDecimal.ZERO);
            if(lFinancierAuctionSettingBean.getBidLimitUtilised()==null) lFinancierAuctionSettingBean.setBidLimitUtilised(BigDecimal.ZERO);
            //
            //for normal utilisation - if the bid is reserved bid - even if InActive then also the 
            //in case of open->reserved the limitUtilised & bidlimitutilised is already set in the new
            //in case of reserved->open then the limitUtilised & bidlimitutilised is set to zero
            //checking for acceptance - so that even if the bid is changed to open and is accepted, the limit should be applied.
            //if the limit is already applied (ie. its Reserved bid then the utilisation is adjusted
            if(pAcceptance || BidBean.BidType.Reserved.equals(pNewBidBean.getBidType())){
            	lDeltaUtilised = pNewBidBean.getLimitUtilised();
            }
            if(pReleaseLimit && !lDeletedOldBid && pOldBidBean.getId()!=null && lOldLimitIds.contains(lFinancierAuctionSettingBean.getId())){
            	lDeltaUtilised = lDeltaUtilised.subtract(pOldBidBean.getLimitUtilised());            	
            }else if(!lDeletedOldBid && pOldBidBean.getId()!=null && lOldLimitIds.contains(lFinancierAuctionSettingBean.getId())){
                if(pOldBidBean.getLimitUtilised()!=null){
                	lDeltaUtilised = lDeltaUtilised.subtract(pOldBidBean.getLimitUtilised());
                }
            }
            lFinancierAuctionSettingBean.setUtilised(lFinancierAuctionSettingBean.getUtilised().add(lDeltaUtilised));
            //
            //active or inactive always adjust
        	lDeltaUtilised = pNewBidBean.getBidLimitUtilised();
            if(!lDeletedOldBid && pOldBidBean.getId()!=null && lOldLimitIds.contains(lFinancierAuctionSettingBean.getId())){
                if(pOldBidBean.getBidLimitUtilised()!=null)
                	lDeltaUtilised = lDeltaUtilised.subtract(pOldBidBean.getBidLimitUtilised());
            }
            lFinancierAuctionSettingBean.setBidLimitUtilised(lFinancierAuctionSettingBean.getBidLimitUtilised().add(lDeltaUtilised));
            //
            //the active check will only be used for skipping validation. 
            if(!pReleaseLimit && lValidateNewBid && 
                    FinancierAuctionSettingBean.Active.Active.getCode().equals(lFinancierAuctionSettingBean.getActive().getCode())){
            	if(lFinancierAuctionSettingBean.hasExpired()){
            		throw new CommonBusinessException("Limit expiry date exceeded.");
            	}
            	if((lDeltaUtilised.compareTo(BigDecimal.ZERO) >= 0) && lFinancierAuctionSettingBean.getLimit().compareTo(lFinancierAuctionSettingBean.getUtilised()) < 0){
                    if (lFinancierAuctionSettingBean.getLevel() == FinancierAuctionSettingBean.Level.System_Buyer)
                        throw new CommonBusinessException("Banker Credit Limit for buyer not adequate. Short fall of " 
                            + lFinancierAuctionSettingBean.getUtilised().subtract(lFinancierAuctionSettingBean.getLimit()));
                    else
                        throw new CommonBusinessException("Financier Credit limit not adequate for " + lFinancierAuctionSettingBean.getLevel().toString() 
                            + ". Short fall of " + lFinancierAuctionSettingBean.getUtilised().subtract(lFinancierAuctionSettingBean.getLimit()));
            	}
            	//TODO : (SKIPPING OF CHECK PENDING) If BidLimit is NOT set then skipping the check as it is not mandatory
            	//if(lFinancierAuctionSettingBean.getBidLimit()!=null && !lFinancierAuctionSettingBean.getBidLimit().equals(new Long(0)) &&
            			if(lFinancierAuctionSettingBean.getBidLimit().compareTo(lFinancierAuctionSettingBean.getBidLimitUtilised()) < 0){
            		throw new CommonBusinessException("Bid Limit shortfall of " + lFinancierAuctionSettingBean.getBidLimitUtilised().subtract(lFinancierAuctionSettingBean.getBidLimit()).toString() + " at " + lFinancierAuctionSettingBean.getLevel().toString() + " Level.") ;
            	}
                if (lFinancierAuctionSettingBean.getLevel() == FinancierAuctionSettingBean.Level.Financier_Self)
                    lSelf = true;
                else if (lFinancierAuctionSettingBean.getLevel() == FinancierAuctionSettingBean.Level.Financier_Buyer)
                    lPurchaser = true;
                else if (lFinancierAuctionSettingBean.getLevel() == FinancierAuctionSettingBean.Level.System_Buyer)
                    lSystemBuyer = true;
            }
        	lNewLimitIdCsv.append(lFinancierAuctionSettingBean.getId()).append(",");
       }
       if(!pReleaseLimit && lValidateNewBid){
           if (!lSelf)
               throw new CommonBusinessException("Credit limit not defined for financier (self)");
           if (!lPurchaser)
               throw new CommonBusinessException("Credit limit not defined for buyer");
           if (!lSystemBuyer)
               throw new CommonBusinessException("Bank Limit not defined for buyer by the platform");
       }
       if(lNewLimitIdCsv.length()> 0)
    	   lNewLimitIdCsv.setLength(lNewLimitIdCsv.length() - 1);// last comma
       if(!pReleaseLimit){
    	   pNewBidBean.setLimitIds(lNewLimitIdCsv.toString());
       }
       // update utilized values
       for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lSettings) {
           lFinancierAuctionSettingBean.setRecordUpdator( (pUserBean!=null?pUserBean.getId():new Long(0)) );
           financierAuctionSettingDAO.update(pConnection, lFinancierAuctionSettingBean, FinancierAuctionSettingBean.FIELDGROUP_UPDATEUTILISED);
           //financierAuctionSettingDAO.insertAudit(pConnection, lFinancierAuctionSettingBean, AuditAction.Update, pUserBean.getId());
       }
    }
    
    public void releaseLimits(Connection pConnection, FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean) throws Exception{
    	if(FactoringUnitBean.Status.Factored.equals(pFactoringUnitBean.getStatus()) ||
    			FactoringUnitBean.Status.Expired.equals(pFactoringUnitBean.getStatus())){
            List<BidBean> lBids = getBids(pConnection, pFactoringUnitBean.getId(),null,null,-1, null);
            boolean lFactored =FactoringUnitBean.Status.Factored.equals(pFactoringUnitBean.getStatus());
            for (BidBean lBidBean : lBids) {
            	try {
            		//skip the accepted bid (only for factored unit)
            		if(lFactored && lBidBean.getId().equals(pFactoringUnitBean.getBdId()))
            			continue;
        			releaseLimit(pConnection, pFactoringUnitBean, lBidBean, pUserBean);
            	} catch (Exception lException) {
            		logger.info("Error in releaseLimits : " + lException.getMessage());
            	}
            }    		
    	}
    }
    
    public void releaseLimit(Connection pConnection, FactoringUnitBean pFactoringUnitBean, BidBean pOldBidBean, AppUserBean pUserBean) throws Exception{
    	try {
	        BidBean lNewBidBean = new BidBean();
	        bidDAO.getBeanMeta().copyBean(pOldBidBean, lNewBidBean);
	    	lNewBidBean.setLimitUtilised(BigDecimal.ZERO);
	        lNewBidBean.setBidLimitUtilised(BigDecimal.ZERO);
			updateBidLimits(pConnection, pFactoringUnitBean, pOldBidBean, lNewBidBean, pUserBean, null, false, true);
			lNewBidBean.setLimitIds(null);
			//pOldBidBean.setLimitUtilised(BigDecimal.ZERO);
			//pOldBidBean.setBidLimitUtilised(BigDecimal.ZERO);
    	} catch (Exception lException) {
    		logger.info("Error in releaseLimit : " + lException.getMessage());
    	}    	
    }
    
    public void autoConfirmBids(Date pSettelmentDate, AutoAcceptBid pAutoAcceptBidType) {
        logger.info("Auto confirming bids started");
        Connection lConnection = null;
        try {
            DBHelper lDBHelper = DBHelper.getInstance();
	        lConnection = lDBHelper.getConnection();
	        lConnection.setAutoCommit(false);
            StringBuilder lSql = new StringBuilder();
            lSql.append("SELECT * FROM FactoringUnits WHERE FURecordVersion > 0 AND FUBdId IS NOT NULL AND FUStatus = ");
            lSql.append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
            lSql.append(" AND FUAutoAccept = ").append(lDBHelper.formatString(pAutoAcceptBidType.getCode()));
            lSql.append(" ORDER BY FUId");
            List<FactoringUnitBean> lFactoringUnits = factoringUnitDAO.findListFromSql(lConnection, lSql.toString(), 0);
            MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
            for (FactoringUnitBean lFactoringUnitBean : lFactoringUnits) {
                try {
                    logger.info("\tAuto confirming FUId : " + lFactoringUnitBean.getId() + " Bid : " + lFactoringUnitBean.getBdId());
                    //TODO: The Blocked Financiers by Purchaser check - pending implementation - case: if the best bid is by financier who is blocked by purchaser.
                    acceptBid(lConnection, lFactoringUnitBean, null, null, null, pSettelmentDate);
                    lConnection.commit();
                } catch (Exception lException) {
                    lConnection.rollback();
                    logger.error("Error while auto accepting bid for factoring unit " + lFactoringUnitBean.getId() + " : Message : "+lException.getMessage());
                }
            }
            logger.info("Auto confirming bids completed");
        } catch (Exception lException) {
            logger.error("Error while autoconfirming bids", lException);
        } finally {
            try {
                if (lConnection != null) 
                    lConnection.close();
            } catch (Exception lException) {
                logger.error("Error while closing connection", lException);
            }
         }
    }
    
    public void updateBestBidAfterExpireBids(Connection pConnection, Date pBusinessDate) {
        //Connection lConnection = null;
        Date lBusinessDate = pBusinessDate;
        logger.info("Updating best bid after expiring bids started");
        try {
        	if(pBusinessDate==null){
                AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
                if (lAuctionCalendarBean == null) {
                    throw new CommonBusinessException("Auction calendar not found");
                }
                lBusinessDate=lAuctionCalendarBean.getDate();
        	}
            DBHelper lDBHelper = DBHelper.getInstance();
            //lConnection = lDBHelper.getConnection();
            //lConnection.setAutoCommit(false);
            StringBuilder lSql = new StringBuilder();
            lSql.append("SELECT * FROM FactoringUnits,Bids WHERE FURecordVersion > 0 AND FUBdId = BDId AND BDValidTill IS NOT NULL");
            lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
            lSql.append(" AND BDValidTill < ").append(lDBHelper.formatDate(lBusinessDate));
            List<FactoringUnitBean> lFactoringUnits = factoringUnitDAO.findListFromSql(pConnection, lSql.toString(), 0);
            for (FactoringUnitBean lFactoringUnitBean : lFactoringUnits) {
                try {
                    updateBestBid(pConnection, lFactoringUnitBean,lBusinessDate);
                    logger.info("Updating best bid for fuid " + lFactoringUnitBean.getId() + " Expired bid " + lFactoringUnitBean.getBdId());
                } catch (Exception lException) {
                    logger.error("Error while updating best bid for factoring unit " + lFactoringUnitBean.getId()+ " : Message : "+lException.getMessage());
                }
            }
            logger.info("Updating best bid after expiring bids completed");
        } catch (Exception lException) {
            logger.error("Error while autoconfirming bids", lException);
        } finally {
        }
    }

    public String findForApproval(ExecutionContext pExecutionContext, AppUserBean pUserBean) throws Exception {
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        Connection lConnection = pExecutionContext.getConnection();
        lSql.append("SELECT * FROM FactoringUnits,Bids,Instruments WHERE FURecordVersion > 0 AND FUId = BDFuId AND INFUID = FUID");
        lSql.append(" AND BDFINANCIERENTITY = ").append(lDBHelper.formatString(pUserBean.getDomain())); 
        lSql.append(" AND BDAPPSTATUS = ").append(lDBHelper.formatString(BidBean.AppStatus.Pending.getCode()));
        lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lSql.append(" AND INRECORDVERSION > 0 ");
        lSql.append(" AND BDFINANCIERAUID IN (SELECT MCMMAKERID FROM MAKERCHECKERMAP WHERE MCMRECORDVERSION > 0");
        lSql.append(" AND MCMCHECKERTYPE = ").append(lDBHelper.formatString(MakerCheckerMapBean.CheckerType.Bid.getCode()));
        lSql.append(" AND MCMCHECKERID = ").append(pUserBean.getId()).append(")");
        if (pUserBean.getBidLevel()!=null && pUserBean.getBidLevel()>0) lSql.append(" AND (BDCHKLEVEL IS NULL OR BDCHKLEVEL= ").append(pUserBean.getBidLevel()).append(" )");
        List<FactoringUnitBidBean> lList = factoringUnitBidDAO.findListFromSql(lConnection, lSql.toString(), -1);
        List<Map<String, Object>> lJsonList = new ArrayList<Map<String,Object>>();
        for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
            lJsonList.add(getFactoringUnitBidJsonFin(lConnection,lFactoringUnitBidBean, pUserBean));
        }
        return new JsonBuilder(lJsonList).toString();
    }
    
    public String depth(ExecutionContext pExecutionContext, Long pFuId, AppUserBean pUserBean) throws Exception {
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, false);
        boolean lIsFinancier = lAppEntityBean.isFinancier();
        boolean lIsAdmin = pUserBean.getType() == AppUserBean.Type.Admin;
        boolean lIsTredsAdmin = (pUserBean!=null && AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()));
        String lDomain = pUserBean.getDomain();
        
        List<Map<String, Object>> lBidList = new ArrayList<Map<String,Object>>();
        Map<String, Object> lData = new HashMap<String, Object>();
        lData.put("fuId", pFuId);
        lData.put("depth", lBidList);
        FactoringUnitBean lFactoringUnitBean = null;
        InstrumentBean lInstrumentBean = null;
        ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        if (lIsTredsAdmin || !lIsFinancier) {
            FactoringUnitBean lFilterBean = new FactoringUnitBean();
            lFilterBean.setId(pFuId);
            lFactoringUnitBean = findBean(pExecutionContext, lFilterBean);
            InstrumentBean lFilterInstBean = new InstrumentBean();
            lFilterInstBean.setFuId(pFuId);
            lInstrumentBean = instrumentDAO.findBean(pExecutionContext.getConnection(), lFilterInstBean);
            lInstrumentBean.populateNonDatabaseFields();
            if(lAppEntityBean.isPurchaserAggregator()) {
    			PurchaserAggregatorBO lPurchaserAggregatorBO = new PurchaserAggregatorBO();
    			lPurchaserAggregatorBO.validateMappedEntity(pExecutionContext.getConnection(), pUserBean.getDomain(), lInstrumentBean.getPurchaser());
	        	lDomain = lInstrumentBean.getPurchaser();
            }else {
                if (!lIsTredsAdmin && !lDomain.equals(lFactoringUnitBean.getPurchaser()) 
                        && !lDomain.equals(lFactoringUnitBean.getSupplier()) && !lDomain.equals(lFactoringUnitBean.getOwnerEntity()))
                    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
            }
//Explicitly - Removed by SRINI even after informing that the Introducer,Owner or Counter is not the same loging user. 
//            if (!lIsTredsAdmin && !lIsAdmin) {
//                Long lAuId = pUserBean.getId();
//                if (!lAuId.equals(lFactoringUnitBean.getIntroducingAuId()) 
//                        && (lFactoringUnitBean.getCounterAuId()!=null && !lAuId.equals(lFactoringUnitBean.getCounterAuId())) 
//                        && (lFactoringUnitBean.getOwnerAuId()!=null && !lAuId.equals(lFactoringUnitBean.getOwnerAuId())))
//                    throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
//            }
            lData.put("owner",isOwner(lFactoringUnitBean, pUserBean));
            if (lConfirmationWindowBean != null) {
                lData.put("settleDate", BeanMetaFactory.getInstance().getDateFormatter().format(lConfirmationWindowBean.getSettlementDate()));
                lData.put("maturityDate", BeanMetaFactory.getInstance().getDateFormatter().format(lFactoringUnitBean.getMaturityDate()));
                lData.put("statDueDate", BeanMetaFactory.getInstance().getDateFormatter().format(lInstrumentBean.getStatDueDate()));
            }
            lData.put("id", lFactoringUnitBean.getId());
            lData.put("amount", lFactoringUnitBean.getAmount());
            lData.put("purName", lFactoringUnitBean.getPurName());
            lData.put("supName", lFactoringUnitBean.getSupName());
            lData.put("factorStartDateTime", BeanMetaFactory.getInstance().getDateFormatter().format(lFactoringUnitBean.getFactorStartDateTime()));
            if (lFactoringUnitBean.getAutoAccept() != null)
                lData.put("autoAcceptDesc", lFactoringUnitBean.getAutoAccept().toString());
            if (lFactoringUnitBean.getAutoAcceptableBidTypes() != null)
                lData.put("autoAcceptableBidTypesDesc", lFactoringUnitBean.getAutoAcceptableBidTypes().toString());
            lData.put("settleLeg3FlagDesc", lFactoringUnitBean.getSettleLeg3Flag());
            lData.put("costBearingType", lInstrumentBean.getCostBearingType());
            lData.put("chargeBearer", lFactoringUnitBean.getChargeBearer());
            lData.put("enableExtension", lFactoringUnitBean.getEnableExtension()!=null?lFactoringUnitBean.getEnableExtension():"");
            lData.put("extendedDueDate", lFactoringUnitBean.getExtendedDueDate()!=null?BeanMetaFactory.getInstance().getDateFormatter().format(lFactoringUnitBean.getExtendedDueDate()):"");
            
            if(lAppEntityBean.isPurchaserAggregator()) {
                if (lFactoringUnitBean.getAutoAccept() != null)
                    lData.put("autoAcceptDesc", lFactoringUnitBean.getAutoAccept().getCode());
                if (lFactoringUnitBean.getAutoAcceptableBidTypes() != null)
                    lData.put("autoAcceptableBidTypesDesc", lFactoringUnitBean.getAutoAcceptableBidTypes().getCode());
                lData.put("settleLeg3FlagDesc", (lFactoringUnitBean.getSettleLeg3Flag()!=null?lFactoringUnitBean.getSettleLeg3Flag().getCode():""));
                lData.put("costBearingType", lInstrumentBean.getCostBearingType().getCode());
                lData.put("chargeBearer", lFactoringUnitBean.getChargeBearer().getCode());
                lData.put("enableExtension", lFactoringUnitBean.getEnableExtension()!=null?lFactoringUnitBean.getEnableExtension().getCode():"");
            }
        }
        lData.put("currency", (lInstrumentBean!=null?lInstrumentBean.getCurrency():""));
        List<BidBean> lBids = getBids(pExecutionContext.getConnection(), pFuId, null, null,-1, null);
        BeanMeta lBidBeanMeta = bidDAO.getBeanMeta();
        for (BidBean lBidBean : lBids) {
            Map<String, Object> lMap = lBidBeanMeta.formatAsMap(lBidBean, BidBean.FIELDGROUP_DEPTH, null, (lAppEntityBean.isPurchaserAggregator()?false:true));
            if (lIsFinancier) {
                if (lAppEntityBean.getCode().equals(lBidBean.getFinancierEntity())) {
                    lMap.put("own",Boolean.TRUE);
                }
            }
            lBidList.add(lMap);
            if ((lFactoringUnitBean != null) && (lConfirmationWindowBean != null)) {
                lFactoringUnitBean.setAcceptedBid(lBidBean);
                BigDecimal[] lLegIntrests = computeLegInterest(lFactoringUnitBean, lConfirmationWindowBean.getSettlementDate(), 
                        lBidBean.getHaircut(), lBidBean.getRate(), lBidBean.getCostLeg());
                //
                BigDecimal[] lLegAmounts = getLegAmounts(lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getSupplier(), lFactoringUnitBean.getFinancier(), lFactoringUnitBean.getAmount(), lFactoringUnitBean.getAcceptedHaircut(),lFactoringUnitBean.getSettleLeg3Flag(), lLegIntrests, lDomain);
                //
                lMap.put("leg1", lLegAmounts[0]);
                lMap.put("leg2", lLegAmounts[1]);
                lMap.put("leg3", lLegAmounts[2]);
                lMap.put("disc", lLegIntrests[INT_PUR_LEG1].add(lLegIntrests[INT_PUR_LEG2]).add(lLegIntrests[INT_PUR_LEG2]));
            }
        }
        return new JsonBuilder(lData).toString();
    }
    
    public BigDecimal[] getLegAmounts(String pPurchaser, String pSupplier, String pFinancier
    		, BigDecimal pFUAmount, BigDecimal pHaircut, YesNo pSettleLeg3Flag
    		, BigDecimal[] pLegInterests, String pDomain){
    	BigDecimal[] lRetVal = new BigDecimal[] { null, null, null };
        BigDecimal lFactoredAmount = pFUAmount;
		BigDecimal lHaircutAmt = null;
    	if(pHaircut!=null && pHaircut.doubleValue() > 0){
    		lHaircutAmt = lFactoredAmount.multiply(BigDecimal.valueOf(pHaircut.doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
    		lFactoredAmount = lFactoredAmount.subtract(lHaircutAmt);
    	}
    	if(pPurchaser.equals(pDomain)){
    		lRetVal[0] = pLegInterests[INT_PUR_LEG1];
    		lRetVal[1] = lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]);
    		if(CommonAppConstants.YesNo.Yes.equals(pSettleLeg3Flag))
    			lRetVal[2] = lHaircutAmt;
    	}else if(pSupplier.equals(pDomain)){
    		lRetVal[0] = lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]);
    		if(CommonAppConstants.YesNo.Yes.equals(pSettleLeg3Flag))
    			lRetVal[2] = lHaircutAmt;
    	}else if(pFinancier.equals(pDomain)){
    		BigDecimal lLeg1 = new BigDecimal(lFactoredAmount.doubleValue());
    		BigDecimal lLeg2 = new BigDecimal(lFactoredAmount.doubleValue());
    		if(pLegInterests[INT_PUR_LEG1]!=null) lLeg1 = lLeg1.subtract(pLegInterests[INT_PUR_LEG1]);
    		if(pLegInterests[INT_SUP_LEG1]!=null) lLeg1 = lLeg1.subtract(pLegInterests[INT_SUP_LEG1]);
			lRetVal[0] = lLeg1;
			if((pLegInterests[INT_PUR_LEG2])!=null) lLeg2 = lLeg2.subtract(pLegInterests[INT_SUP_LEG1]);
			lRetVal[1] = lLeg2;
		}
    	//
    	return lRetVal;
    }

    public List<BidBean> deleteFactoringUnitBids(Connection pConnection, 
            FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean) throws Exception {
        BidBean lFilterBean = new BidBean();
        lFilterBean.setFuId(pFactoringUnitBean.getId());
        lFilterBean.setStatus(BidBean.Status.Active);
        List<BidBean> lBids = bidDAO.findList(pConnection, lFilterBean, (String)null);
        for (BidBean lBidBean : lBids) {
            Long lFinancierAUId = lBidBean.getFinancierAuId();
            deleteBid(pConnection, lBidBean, pFactoringUnitBean, null, pUserBean);
            lBidBean.setFinancierAuId(lFinancierAUId); // resetting the financier auid so that emails can be sent to bidder user
        }
        pFactoringUnitBean.setAcceptedBid(null);
        return lBids;
    }
    
    public boolean updateStatus(Connection pConnection, FactoringUnitBean pFactoringUnitBean, 
            FactoringUnitBean.Status pStatus, AppUserBean pUserBean) throws Exception
    {
    	pFactoringUnitBean.setStatus(pStatus);
    	String lFieldGroup =FactoringUnitBean.FIELDGROUP_UPDATESTATUS;
    	if(FactoringUnitBean.Status.Withdrawn.equals(pStatus)){
    		lFieldGroup =FactoringUnitBean.FIELDGROUP_UPDATEWITHDRAWNSTATUS;
    	}
		pFactoringUnitBean.setStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
        factoringUnitDAO.update(pConnection, pFactoringUnitBean, lFieldGroup);
        factoringUnitDAO.insertAudit(pConnection, pFactoringUnitBean, AuditAction.Update, (pUserBean!=null?pUserBean.getId():new Long(0)));
        
        //if the factoring unit is withdrawn then put the instrument back to draft mode.
        if (FactoringUnitBean.Status.Withdrawn.equals(pStatus)){	
        	InstrumentBean lInstrumentBean = new InstrumentBean();
        	lInstrumentBean.setFuId(pFactoringUnitBean.getId());
        	lInstrumentBean = instrumentDAO.findBean(pConnection, lInstrumentBean);
        	if(!lInstrumentBean.getIsGemInvoice()) {
        	lInstrumentBean.setFuId(null);
        	lInstrumentBean.setOwnerAuId(null);
        	lInstrumentBean.setOwnerEntity(null);
        	lInstrumentBean.setCheckerAuId(null);
        	lInstrumentBean.setCounterAuId(null);
        	lInstrumentBean.setCounterCheckerAuId(null);
        	lInstrumentBean.setCounterModifiedFields(null);
        	lInstrumentBean.setStatus(InstrumentBean.Status.Drafting);
        	lInstrumentBean.setStatusRemarks("Factoring Unit : " + pFactoringUnitBean.getId().toString() + " withdrawn.");
        	lInstrumentBean.setStatusUpdateTime(new Timestamp(System.currentTimeMillis()));
    		if(MonetagoTredsHelper.getInstance().performMonetagoCheck()){
    			if(StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) && !CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
    				Map<String,String> lResult= new HashMap<String, String>();
    	     		String lInfoMessage = "Instrument No :"+lInstrumentBean.getId();
    	    		lResult=MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.Withdrawn,lInfoMessage,lInstrumentBean.getId());
    	    		if(StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))){
    	   				lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
    	   				lInstrumentBean.setMonetagoLedgerId("");
    	   				logger.info("Message Success :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
    	    		}else{
    	    			logger.info("Message Error :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
    	    			throw new CommonBusinessException("Error while Rejecting : " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
    	    		}
    	        }else{
    	        	//DO NOTHING SINCE GROUP GOES IN DRAFTING AND THERE IT WOULD BE UNGROUPED
        	        }
    	        }
        	}else {
        		//if gem invoice then put in checker approved state
            	lInstrumentBean.setFuId(null);
            	lInstrumentBean.setStatus(InstrumentBean.Status.Checker_Approved);
            	lInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
    	    }
            instrumentDAO.update(pConnection, lInstrumentBean, InstrumentBean.FIELDGROUP_UPDATESTATUS);
            //  Withdraw of instrument by supplier
            //TODO: change setting
            AppEntityBean lUserEntity = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
            if(lUserEntity.isPurchaserAggregator()){
            	// TODO : What to do over here?
            }else{
                if(FactoringUnitBean.Status.Withdrawn.equals(pStatus) && !pUserBean.getDomain().equals(lInstrumentBean.getPurchaser())){
                	if (ClickWrapHelper.getInstance().isAgreementEnabled(lInstrumentBean.getPurchaser())){
                		List<IAgreementAcceptanceBean> lList = ClickWrapHelper.getInstance().getAcceptedAggrementDetails(pConnection,lInstrumentBean.getPurchaser(),lInstrumentBean.getId());
                		ClickWrapHelper.getInstance().removeClickWrapAgreements(pConnection, lList,pUserBean.getId());
                	}
                	
                }
            }
            instrumentBO.insertInstrumentWorkFlow(pConnection, lInstrumentBean, pUserBean, null);
        }
        return true; 
   }
    
    private List<FinancierAuctionSettingBean> getFinancierAuctionSettings(Connection pConnection, String pFinancier, 
            String pPurchaser, String pSupplier, Long pAuId, boolean pOnlyActive) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("SELECT * FROM FinancierAuctionSettings WHERE 1=1 ");
        if(pOnlyActive) {
        	lSql.append(" AND FASActive = ").append(lDBHelper.formatString(CommonAppConstants.YesNo.Yes.getCode()));
        }
        //TODO: Expiry should not be checked here or above since the list is then checked.
        //TODO: WHETHER THE ACTIVE STATUS IS IN CORELATION WITH THE  ABOVE ACTIVE FLAG SENT
		//lSql.append(" AND ( FASEXPIRYDATE IS NULL  OR FASEXPIRYDATE > ").append(DBHelper.getInstance().formatDate(TredsHelper.getInstance().getBusinessDate())).append("  ) ");
		//
        lSql.append(" AND ((SUBSTR(FASLevel,1,1)='N' AND FASFinancier IS NULL) OR FASFinancier = ").append(lDBHelper.formatString(pFinancier)).append(")");
        lSql.append(" AND ((SUBSTR(FASLevel,2,1)='N' AND FASPurchaser IS NULL) OR FASPurchaser = ").append(lDBHelper.formatString(pPurchaser)).append(")");
        lSql.append(" AND ((SUBSTR(FASLevel,3,1)='N' AND FASSupplier IS NULL) OR FASSupplier = ").append(lDBHelper.formatString(pSupplier)).append(")");
        lSql.append(" AND ((SUBSTR(FASLevel,4,1)='N' AND FASAuId IS NULL) OR FASAuId = ").append(pAuId).append(")");
        lSql.append(" ORDER BY FASLEVEL ASC ");
        return financierAuctionSettingDAO.findListFromSql(pConnection, lSql.toString(), -1);
    }
    
    private void validateBidRate(List<FinancierAuctionSettingBean> pSettings, BigDecimal pBidRate, 
            Long pTenure,Date pValidTill) throws CommonBusinessException
    {
        // validate rate
        BigDecimal lBaseRate = null;
        boolean lValidateSellerLimit = false, lHasSellerLimit = false;
        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : pSettings) {
            if(lFinancierAuctionSettingBean.hasExpired()){
            	throw new CommonBusinessException("Expiry date exceeded for "+ lFinancierAuctionSettingBean.getLevel().toString()+".");
            }
            if(lFinancierAuctionSettingBean.getExpiryDate()!=null){
            	if (pValidTill!=null && pValidTill.after(lFinancierAuctionSettingBean.getExpiryDate())){
            		throw new CommonBusinessException("Bid validity exceededs "+ lFinancierAuctionSettingBean.getLevel().toString()+" expiry.");
            	}
            }

            if (!FinancierAuctionSettingBean.Active.Active.equals(lFinancierAuctionSettingBean.getActive())) {
            	continue;
            }
            if ((lFinancierAuctionSettingBean.getMinBidRate() != null) 
                    && (lFinancierAuctionSettingBean.getMinBidRate().compareTo(pBidRate) > 0))
               throw new CommonBusinessException("Rate below allowed minimum bid rate " + lFinancierAuctionSettingBean.getMinBidRate() + " for " + lFinancierAuctionSettingBean.getLevel().toString()); 
            if ((lFinancierAuctionSettingBean.getMaxBidRate() != null) 
                    && (lFinancierAuctionSettingBean.getMaxBidRate().compareTo(pBidRate) < 0))
               throw new CommonBusinessException("Rate above allowed maximum bid rate " + lFinancierAuctionSettingBean.getMaxBidRate() + " for " + lFinancierAuctionSettingBean.getLevel().toString());
            //  repo rate based range
            if (FinancierAuctionSettingBean.Level.Financier_Self.equals(lFinancierAuctionSettingBean.getLevel()))
            	lBaseRate = lFinancierAuctionSettingBean.getBaseBidRate(pTenure);
            else if ((lFinancierAuctionSettingBean.getMinSpread() != null) || (lFinancierAuctionSettingBean.getMaxSpread() != null)) {
            	if(lBaseRate==null)
                    throw new CommonBusinessException("Financiers base rate not set for the tenure.");
            	BigDecimal lEffMinRate = lBaseRate.add(lFinancierAuctionSettingBean.getMinSpread()); 
            	if(lEffMinRate.compareTo(pBidRate) > 0 )
                    throw new CommonBusinessException("Rate below allowed minimum effective bid rate " + lEffMinRate + " for " + lFinancierAuctionSettingBean.getLevel().toString());
            	BigDecimal lEffMaxRate = lBaseRate.add(lFinancierAuctionSettingBean.getMaxSpread());
            	if(lEffMaxRate.compareTo(pBidRate) < 0 )
                    throw new CommonBusinessException("Rate above allowed maximum effective bid rate " + lEffMaxRate + " for " + lFinancierAuctionSettingBean.getLevel().toString());
            }
            if (FinancierAuctionSettingBean.Level.Financier_Buyer.equals(lFinancierAuctionSettingBean.getLevel())){
            	lValidateSellerLimit = CommonAppConstants.Yes.Yes.equals(lFinancierAuctionSettingBean.getSellerLimitMandatory());
            }
            if (FinancierAuctionSettingBean.Level.Financier_Buyer_Seller.equals(lFinancierAuctionSettingBean.getLevel())){
            	lHasSellerLimit = true;
            }
            
        }
        if(lValidateSellerLimit && !lHasSellerLimit){
        	throw new CommonBusinessException("No limit set for the Seller.");
        }
    }
    /**
     * 
     * @param pInstOrFactUnitBean
     * @param pSettlementDate
     * @param pAcceptedHaircut
     * @param pAcceptedRate
     * @return Array with 3 values
     * Value 1 : Purchaser Leg1 Interest
     * Value 2 : Purchaser Leg2 Interest
     * Value 3 : Supplier Leg1 Interest 
     */
    private BigDecimal[] computeLegInterest(ILegInterest pInstOrFactUnitBean, java.sql.Date pSettlementDate, BigDecimal pAcceptedHaircut, 
            BigDecimal pAcceptedRate, AppConstants.CostCollectionLeg pCostLeg) {
    	//input 
    	Date lSettlementDate = null, lStatutoryDueDate = null, lMaturityDate = null, lExtendedDueDate = null;
    	BigDecimal lFactoredAmount = null;
    	//output
    	BigDecimal lPurLeg1Int = BigDecimal.ZERO, lPurLeg2Int = BigDecimal.ZERO, lSuppLeg1Int = BigDecimal.ZERO;
    	BigDecimal lPeriod1Days = null, lPeriod2Days = null, lPeriod3Days = null;
    	BigDecimal lTotPurInt = BigDecimal.ZERO, lTotSupInt = BigDecimal.ZERO;
    	//set the initial dates
    	lSettlementDate = pSettlementDate;
    	lMaturityDate = pInstOrFactUnitBean.getMaturityDate();
    	lStatutoryDueDate = pInstOrFactUnitBean.getStatDueDate();    	
    	lMaturityDate = pInstOrFactUnitBean.getMaturityDate();
    	if(CommonAppConstants.Yes.Yes.equals(pInstOrFactUnitBean.getEnableExtension())){
        	lExtendedDueDate = pInstOrFactUnitBean.getExtendedDueDate();
    	}
    	//set the factored amount
		lFactoredAmount = pInstOrFactUnitBean.getNetAmount();
    	if(pAcceptedHaircut!=null && pAcceptedHaircut.doubleValue() > 0){
    		lFactoredAmount = lFactoredAmount.multiply(BigDecimal.valueOf(100.0-pAcceptedHaircut.doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
    	}
    	//
    	logger.info("lSettlementDate : "+(lSettlementDate!=null?lSettlementDate:""));
    	logger.info("lMaturityDate : "+(lMaturityDate!=null?lMaturityDate:""));
    	logger.info("lStatutoryDueDate : "+(lStatutoryDueDate!=null?lStatutoryDueDate:""));
    	logger.info("lMaturityDate : "+(lMaturityDate!=null?lMaturityDate:""));
    	logger.info("lExtendedDueDate : "+(lExtendedDueDate!=null?lExtendedDueDate:""));
    	logger.info("lFactoredAmount : "+(lFactoredAmount!=null?lFactoredAmount:""));
    	//
    	//determine the Breakup dates
    	lPeriod1Days =  BigDecimal.valueOf((lStatutoryDueDate.getTime() - lSettlementDate.getTime())/86400000);
    	lPeriod2Days =  BigDecimal.valueOf((lMaturityDate.getTime() - lStatutoryDueDate.getTime())/86400000);
    	//
    	logger.info("lPeriod1Days : "+(lPeriod1Days!=null?lPeriod1Days:""));
    	logger.info("lPeriod2Days : "+(lPeriod2Days!=null?lPeriod2Days:""));
    	//
    	if(lPeriod2Days.compareTo(BigDecimal.ZERO) == -1){
    		lPeriod2Days =  BigDecimal.valueOf((lMaturityDate.getTime() - lSettlementDate.getTime())/86400000);
    		lPeriod2Days = BigDecimal.ZERO;
    	}
    	logger.info("lPeriod2Days : "+(lPeriod2Days!=null?lPeriod2Days:""));
    	lPeriod3Days = (lExtendedDueDate==null)?BigDecimal.ZERO:BigDecimal.valueOf((lExtendedDueDate.getTime() - lMaturityDate.getTime())/86400000);
    	//
    	logger.info("lPeriod3Days : "+(lPeriod3Days!=null?lPeriod3Days:""));
    	//
    	//period-wise cost
    	CostBearer[] lPeriodCostBearers = new CostBearer[] {pInstOrFactUnitBean.getPeriod1CostBearer(), pInstOrFactUnitBean.getPeriod2CostBearer(), pInstOrFactUnitBean.getPeriod3CostBearer() }; 
    	BigDecimal[] lPeriodDays = new BigDecimal[] { lPeriod1Days, lPeriod2Days, lPeriod3Days }; 
    	BigDecimal[] lPeriodCostPercent = new BigDecimal[] { pInstOrFactUnitBean.getPeriod1CostPercent(), pInstOrFactUnitBean.getPeriod2CostPercent(), pInstOrFactUnitBean.getPeriod3CostPercent() };
    	BigDecimal[] lPeriodCost = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
    	BigDecimal lTmpSplitCost = null;
    	CostCollectionLeg lFinCostLeg = pCostLeg;
    	//
        // check purchaser limit for leg2 amount
//        List<FinancierAuctionSettingBean> lSettings = getFinancierAuctionSettings(lConnection, pBidBean.getFinancierEntity(), pFactoringUnitBean.getPurchaser(), 
//                pFactoringUnitBean.getSupplier(), pBidBean.getFinancierAuId(), false);
        // 
    	for(int lPtr=0; lPtr < 3; lPtr++){
    		lPeriodCost[lPtr] = lFactoredAmount.multiply(pAcceptedRate).multiply(lPeriodDays[lPtr]).divide(BigDecimal.valueOf(36500.0), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
        	logger.info("lPeriodCost[lPtr] : "+ lPtr + " : "+(lPeriodCost[lPtr]!=null?lPeriodCost[lPtr]:""));
        	logger.info("lPeriodCostBearers[lPtr] : "+ lPtr + " : "+(lPeriodCostBearers[lPtr]!=null?lPeriodCostBearers[lPtr]:""));
        	logger.info("lPeriodCostPercent[lPtr] : "+ lPtr + " : "+(lPeriodCostPercent[lPtr]!=null?lPeriodCostPercent[lPtr]:""));
    		if(lPeriodCostPercent[0].compareTo((BigDecimal.valueOf(100.0))) == 0 ) {
    			//full cost
    			if(lPeriodCostBearers[lPtr].equals(CostBearer.Buyer)){
    				lTotPurInt = lTotPurInt.add(lPeriodCost[lPtr]);
    			}else {
    				lTotSupInt = lTotSupInt.add(lPeriodCost[lPtr]);
    			}
    		}else{
    			//split cost
    			lTmpSplitCost = lPeriodCost[lPtr].multiply(lPeriodCostPercent[lPtr]).divide(BigDecimal.valueOf(100.0), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
            	logger.info("lTmpSplitCost : "+ lPtr + " : "+ lPeriodCostBearers[lPtr] +" : " +(lTmpSplitCost!=null?lTmpSplitCost:""));
    			if(lPeriodCostBearers[lPtr].equals(CostBearer.Buyer)){
    				lTotPurInt = lTotPurInt.add(lTmpSplitCost);
    				lTotSupInt = lTotSupInt.add(lPeriodCost[lPtr].subtract(lTmpSplitCost));
    			}else {
    				lTotSupInt = lTotSupInt.add(lTmpSplitCost);
    				lTotPurInt = lTotPurInt.add(lPeriodCost[lPtr].subtract(lTmpSplitCost));
    			}
    		}
    		logger.info("lTotPurInt : " + lPtr + " : "+ lTotPurInt);
    		logger.info("lTotSupInt : " + lPtr + " : "+ lTotSupInt);
    	}
		logger.info("post loop ");
		logger.info("lTotPurInt : "+ lTotPurInt);
		logger.info("lTotSupInt : "+ lTotSupInt);
		
		logger.info("lSuppLeg1Int : "+ lSuppLeg1Int);
		logger.info("lFinCostLeg : "+ lFinCostLeg);
    	//cumulative  - leg and pur/sup wise
    	lSuppLeg1Int = lTotSupInt;
    	if(lSuppLeg1Int.compareTo(BigDecimal.ZERO) == 1){
    		lPurLeg1Int = lTotPurInt;
    		lPurLeg2Int = BigDecimal.ZERO;
    	}else{
    		// get purchaser cost leg from financierauctionsettings for the purchaser
    		if(lFinCostLeg.equals(CostCollectionLeg.Leg_1)){
    			lPurLeg1Int = lTotPurInt;
    			lPurLeg2Int = BigDecimal.ZERO;
    		}else if(lFinCostLeg.equals(CostCollectionLeg.Leg_2)){
    			lPurLeg2Int = lTotPurInt;    			
    			lPurLeg1Int = BigDecimal.ZERO;
    		}
    	}
		logger.info("lPurLeg1Int : "+ lPurLeg1Int);
		logger.info("lSuppLeg1Int : "+ lSuppLeg1Int);
		logger.info("lPurLeg2Int : "+ lPurLeg2Int);
    	return new BigDecimal[]{lPurLeg1Int, lSuppLeg1Int, lPurLeg2Int};
    }
    
    private BigDecimal[] computeCharge(Connection pConnection ,ILegInterest pInstOrFactUnitBean, java.sql.Date pSettlementDate ) throws Exception {
    	//input 
    	logger.info("computeCharge : Platform Charge Calculation");
    	Date lSettlementDate = null, lStatutoryDueDate = null, lMaturityDate = null, lExtendedDueDate = null;
    	BigDecimal lFactoredAmount = null;
    	//output
    	BigDecimal lPeriod1Days = null, lPeriod2Days = null, lPeriod3Days = null;
    	BigDecimal lTotPurChrg = BigDecimal.ZERO, lTotSupChrg = BigDecimal.ZERO;
    	//set the initial dates
    	lSettlementDate = pSettlementDate;
    	lMaturityDate = pInstOrFactUnitBean.getMaturityDate();
    	lStatutoryDueDate = pInstOrFactUnitBean.getStatDueDate();    	
    	lMaturityDate = pInstOrFactUnitBean.getMaturityDate();
    	if(CommonAppConstants.Yes.Yes.equals(pInstOrFactUnitBean.getEnableExtension())){
        	lExtendedDueDate = pInstOrFactUnitBean.getExtendedDueDate();
    	}
    	//set the factored amount
		lFactoredAmount = pInstOrFactUnitBean.getFactoredAmount();
    	//
    	logger.info("lSettlementDate : "+(lSettlementDate!=null?lSettlementDate:""));
    	logger.info("lMaturityDate : "+(lMaturityDate!=null?lMaturityDate:""));
    	logger.info("lStatutoryDueDate : "+(lStatutoryDueDate!=null?lStatutoryDueDate:""));
    	logger.info("lMaturityDate : "+(lMaturityDate!=null?lMaturityDate:""));
    	logger.info("lExtendedDueDate : "+(lExtendedDueDate!=null?lExtendedDueDate:""));
    	logger.info("lFactoredAmount : "+(lFactoredAmount!=null?lFactoredAmount:""));
    	//
    	//determine the Breakup dates
    	lPeriod1Days =  BigDecimal.valueOf((lStatutoryDueDate.getTime() - lSettlementDate.getTime())/86400000);
    	lPeriod2Days =  BigDecimal.valueOf((lMaturityDate.getTime() - lStatutoryDueDate.getTime())/86400000);
    	//
    	logger.info("lPeriod1Days : "+(lPeriod1Days!=null?lPeriod1Days:""));
    	logger.info("lPeriod2Days : "+(lPeriod2Days!=null?lPeriod2Days:""));
    	//
    	if(lPeriod2Days.compareTo(BigDecimal.ZERO) == -1){
    		lPeriod2Days =  BigDecimal.valueOf((lMaturityDate.getTime() - lSettlementDate.getTime())/86400000);
    		lPeriod2Days = BigDecimal.ZERO;
    	}
    	logger.info("lPeriod2Days : "+(lPeriod2Days!=null?lPeriod2Days:""));
    	lPeriod3Days = (lExtendedDueDate==null)?BigDecimal.ZERO:BigDecimal.valueOf((lExtendedDueDate.getTime() - lMaturityDate.getTime())/86400000);
    	//
    	logger.info("lPeriod3Days : "+(lPeriod3Days!=null?lPeriod3Days:""));
    	//
    	//period-wise cost
    	CostBearer[] lPeriodChargeBearers = new CostBearer[] {pInstOrFactUnitBean.getPeriod1ChargeBearer(), pInstOrFactUnitBean.getPeriod2ChargeBearer(), pInstOrFactUnitBean.getPeriod3ChargeBearer() }; 
    	BigDecimal[] lPeriodDays = new BigDecimal[] { lPeriod1Days, lPeriod2Days, lPeriod3Days }; 
    	BigDecimal[] lPeriodChargePercent = new BigDecimal[] { pInstOrFactUnitBean.getPeriod1ChargePercent(), pInstOrFactUnitBean.getPeriod2ChargePercent(), pInstOrFactUnitBean.getPeriod3ChargePercent() };
    	BigDecimal[] lPeriodCharge = new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO };
    	BigDecimal lTmpSplitCharge = null;
    	HashMap<CostBearer, MemberwisePlanBean> lACPPlanMap = new HashMap<AppConstants.CostBearer, MemberwisePlanBean>(); 
    	
    	Map<CostBearer, Long> lSettleLoc = new HashMap<AppConstants.CostBearer, Long>();
    	lSettleLoc.put(CostBearer.Buyer, ((FactoringUnitBean)pInstOrFactUnitBean).getBillLocationIdForChargeSplit(CostBearer.Buyer));
    	lSettleLoc.put(CostBearer.Seller, ((FactoringUnitBean)pInstOrFactUnitBean).getBillLocationIdForChargeSplit(CostBearer.Seller));
    	
    	Map<CostBearer, String> lEntityMap = new HashMap<AppConstants.CostBearer, String>();
    	lEntityMap.put(CostBearer.Buyer, pInstOrFactUnitBean.getPurchaser());
    	lEntityMap.put(CostBearer.Seller, pInstOrFactUnitBean.getSupplier());
    	
    	MemberwisePlanBean lMPBean = TredsHelper.getInstance().getPlan(pConnection, pInstOrFactUnitBean.getPurchaser(), TredsHelper.getInstance().getBusinessDate(),true);
    	lACPPlanMap.put(CostBearer.Buyer, lMPBean);
    	lMPBean = TredsHelper.getInstance().getPlan(pConnection, pInstOrFactUnitBean.getSupplier(), TredsHelper.getInstance().getBusinessDate(),true);
    	lACPPlanMap.put(CostBearer.Seller, (lMPBean==null?lACPPlanMap.get(CostBearer.Buyer):lMPBean));
    	// 
    	for(int lPtr=0; lPtr < 3; lPtr++){
    		lMPBean = lACPPlanMap.get(lPeriodChargeBearers[lPtr]);
    		
    		lPeriodCharge[lPtr] = TredsHelper.getInstance().getNormalPlanCharge(pConnection, (FactoringUnitBean) pInstOrFactUnitBean, lMPBean.getCode(), lEntityMap.get(lPeriodChargeBearers[lPtr]), pInstOrFactUnitBean.getFactoredAmount(), lSettleLoc.get(lPeriodChargeBearers[lPtr]), null,lPeriodDays[lPtr].longValue());
        	logger.info("lPeriodCharge[lPtr] : "+ lPtr + " : "+(lPeriodCharge[lPtr]!=null?lPeriodCharge[lPtr]:""));
        	logger.info("lPeriodChargeBearers[lPtr] : "+ lPtr + " : "+(lPeriodChargeBearers[lPtr]!=null?lPeriodChargeBearers[lPtr]:""));
        	logger.info("lPeriodChargePercent[lPtr] : "+ lPtr + " : "+(lPeriodChargePercent[lPtr]!=null?lPeriodChargePercent[lPtr]:""));
    		if(lPeriodChargePercent[0].compareTo((BigDecimal.valueOf(100.0))) == 0 ) {
    			//full charge
    			if(lPeriodChargeBearers[lPtr].equals(CostBearer.Buyer)){
    				lTotPurChrg = lTotPurChrg.add(lPeriodCharge[lPtr]);
    			}else {
    				lTotSupChrg = lTotSupChrg.add(lPeriodCharge[lPtr]);
    			}
    		}else{
    			//split charge
    			lTmpSplitCharge = lPeriodCharge[lPtr].multiply(lPeriodChargePercent[lPtr]).divide(BigDecimal.valueOf(100.0), MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
            	logger.info("lTmpSplitCharge : "+ lPtr + " : "+ lPeriodChargeBearers[lPtr] +" : " +(lTmpSplitCharge!=null?lTmpSplitCharge:""));
    			if(lPeriodChargeBearers[lPtr].equals(CostBearer.Buyer)){
    				lTotPurChrg = lTotPurChrg.add(lTmpSplitCharge);
    				lTotSupChrg = lTotSupChrg.add(lPeriodCharge[lPtr].subtract(lTmpSplitCharge));
    			}else {
    				lTotSupChrg = lTotSupChrg.add(lTmpSplitCharge);
    				lTotPurChrg = lTotPurChrg.add(lPeriodCharge[lPtr].subtract(lTmpSplitCharge));
    			}
    		}
    		logger.info("lTotPurChrg : " + lPtr + " : "+ lTotPurChrg);
    		logger.info("lTotSupChrg : " + lPtr + " : "+ lTotSupChrg);
    	}
		logger.info("post loop ");
		logger.info("lTotPurChrg : "+ lTotPurChrg);
		logger.info("lTotSupChrg : "+ lTotSupChrg);
		
    	return new BigDecimal[]{lTotPurChrg, lTotSupChrg};
    }
    
    private void acceptBid(Connection pConnection, FactoringUnitBean pFactoringUnitBean, 
    		Long pBidId, BigDecimal pBidRate, 
            AppUserBean pUserBean, Date pSettelmentDate) throws Exception {
        List<BidBean> lBids = getBids(pConnection, pFactoringUnitBean.getId(),pBidId, null,-1, null);
        boolean lManualAcceptance = false;
        if (pBidId != null) {
        	// manual acceptance
        	if (lBids.size() != 1)
                throw new CommonBusinessException("Bid does not exist for factoring unit " + pFactoringUnitBean.getId() + ". Please refresh.");
            if ((pBidRate != null) && 
					lBids.get(0).getRate() != null &&
                    (pBidRate.compareTo(lBids.get(0).getRate())!=0))
                throw new CommonBusinessException("It seems the bid rate has changed for factoring unit " + pFactoringUnitBean.getId() + ". Please refresh.");
            lManualAcceptance = true;
        }
        
        List<PurchaserSupplierCapRateBean> lCapRateList = null;
        if (pBidId == null)//check cap rate only in case of auto acceptance 
        	lCapRateList = getCapRateList(pConnection, pFactoringUnitBean, pSettelmentDate);

        boolean lBidAccepted = false;
        TredsHelper lTredsHelper = TredsHelper.getInstance();
        for (BidBean lBidBean : lBids) {
        	try {
        		//if manual acceptance and open bid then throw error
        		//else if Auto Acceptance then check FU for the type of bid acceptable, if not then throw error for that bid
        		if(lManualAcceptance){
        			if(BidType.Open.equals(lBidBean.getBidType())){
        				//throw new CommonBusinessException("Open Bid cannot be accepted manually.");
        			}
        		}else{
        			if(AppConstants.AutoAcceptBid.Disabled.equals(pFactoringUnitBean.getAutoAccept())){
        				throw new CommonBusinessException("Auto Confirmation mode disabled.");
        			}
        			if(!lTredsHelper.isTransactionableBidType(lBidBean.getBidType(), pFactoringUnitBean.getAutoAcceptableBidTypes())){
        				throw new CommonBusinessException("Bid Type not in acceptable in Auto Confirmation mode.");
        			}
        		}
        		lBidAccepted = acceptBid(pConnection, pFactoringUnitBean, lBidBean, pUserBean, pSettelmentDate, lCapRateList);
        		if(lBidAccepted)
        			break;
        	} catch (CommonBusinessException lCommonBusinessException) {
        		if (lManualAcceptance && pBidId != null && lBids.size() == 1) // manual acceptance
        			throw lCommonBusinessException;
        		else if (lManualAcceptance && lBids.size() > 1){
        			throw new CommonBusinessException("Error in factoring unit " + lBidBean.getFuId() + ". " + lCommonBusinessException.getMessage());
        		}
        		else{
        			//if in auto acceptance bid is not accepted then log the error in the bid statusRemarks
					logBidError(lBidBean, lCommonBusinessException.getMessage(), pUserBean);
        		}
        	}
        }
    }
	private List<BidBean> getUnacceptedBids(Connection pConnection,Long pFuId, Long pAcceptedBidId) throws Exception{
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM BIDS");
		lSql.append(" WHERE BDFUID = ").append(pFuId);
		lSql.append(" AND BDSTATUS = ").append(DBHelper.getInstance().formatString(BidBean.Status.Active.getCode()));
		lSql.append(" AND BDID != ").append(pAcceptedBidId);
		List<BidBean> lBids = bidDAO.findListFromSql(pConnection, lSql.toString(), 0);
		return lBids;
	}
	private void updateUnacceptedBids(Connection pConnection, List<BidBean> pBids) throws Exception{
		Timestamp lTimestamp = new Timestamp(System.currentTimeMillis());
		for(BidBean lBean:pBids){
			if(AppStatus.Approved.equals(lBean.getAppStatus()) || 
					(AppStatus.Pending.equals(lBean.getAppStatus()) && lBean.getRate()!=null)){
				lBean.setStatus(BidBean.Status.NotAccepted);
			}else if(AppStatus.Pending.equals(lBean.getAppStatus()) ||
					((lBean.getRate()==null) && lBean.getAppStatus()==null && lBean.getProvRate()==null)) {
				lBean.setStatus(BidBean.Status.Deleted);
			}
			lBean.setTimestamp(lTimestamp);
         	bidDAO.update(pConnection, lBean,BidBean.FIELDGROUP_UPDATESTATUS);
         	bidDAO.insertAudit(pConnection, lBean,AuditAction.Update,new Long(1));
		}
	}
    private boolean acceptBid(Connection pConnection, FactoringUnitBean pFactoringUnitBean, BidBean pBidBean,
    		AppUserBean pUserBean, Date pSettelmentDate, List<PurchaserSupplierCapRateBean> pCapRateList) throws Exception {
        InstrumentBean lInstrumentBean = null, lInstrumentBeanPostMonetago = null;
        if(pFactoringUnitBean!=null) {
        	checkPlatformStatus(pConnection, pFactoringUnitBean.getPurchaser(), pFactoringUnitBean.getSupplier());
    		}
        
		//TODO: Change FactoringUnit Status to Bid Acceptance Pending
        lockFactoringUnit(pConnection, pFactoringUnitBean);
    	try {
	        //check financiers blocked by purchasers
	        AppEntityBean lAEPurchaser = getAppEntityBean(pFactoringUnitBean.getPurchaser());
	        if(lAEPurchaser.isFinancierBlocked(pBidBean.getFinancierEntity()))
	        	throw new CommonBusinessException("Financier has been blocked by the buyer");
	        // check cap rate. 
	        if (pCapRateList != null) {
		        BigDecimal lBidHaircut = pBidBean.getHaircut();
		        if (lBidHaircut == null) 
		        	lBidHaircut = BigDecimal.ZERO;
		        BigDecimal lCapRate = null;
		        for (PurchaserSupplierCapRateBean lPurchaserSupplierCapRateBean : pCapRateList) {
		        	if ((lBidHaircut.compareTo(lPurchaserSupplierCapRateBean.getFromHaircut()) >= 0) 
		        		&& (lBidHaircut.compareTo(lPurchaserSupplierCapRateBean.getToHaircut()) <= 0)) {
		        			lCapRate = lPurchaserSupplierCapRateBean.getCapRate();
		        			break;
		        		}
		        }
		        if (lCapRate == null)
		        	throw new CommonBusinessException("Cap Rate not defined");
		        else if (lCapRate.compareTo(pBidBean.getRate()) < 0)
		        	throw new CommonBusinessException("Bid rate exceeds Cap Rate : " + lCapRate);
	        }
	        // compute leg1 and leg 2 amounts
	        pFactoringUnitBean.setAcceptedBid(pBidBean);
	        // check purchaser limit for leg2 amount
	        List<FinancierAuctionSettingBean> lSettings = getFinancierAuctionSettings(pConnection, pBidBean.getFinancierEntity(), pFactoringUnitBean.getPurchaser(), 
	                pFactoringUnitBean.getSupplier(), pBidBean.getFinancierAuId(), false);
	        //
	        validateBidRate(lSettings, pBidBean.getRate(), pFactoringUnitBean.getTenure(),pBidBean.getValidTill());
	        //
            lInstrumentBean = new InstrumentBean();
            lInstrumentBean.setFuId(pFactoringUnitBean.getId());
            lInstrumentBean = instrumentDAO.findBean(pConnection, lInstrumentBean);
            lInstrumentBean.populateNonDatabaseFields();
	        BigDecimal[] lLegInterests = computeLegInterest(pFactoringUnitBean, pSettelmentDate, pBidBean.getHaircut(), pBidBean.getRate(), pBidBean.getCostLeg());
	        BigDecimal lLimitUtilised = BigDecimal.ZERO;
	        //
	        //
	    	if (CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())) {
	    		AppUserBean lAppUserBean = TredsHelper.getInstance().getAppUser(pFactoringUnitBean.getIntroducingAuId());
	    		if(TredsHelper.getInstance().isCashInvoie(lAppUserBean)){
	    			//TODO: INTEREST CALCUATION AT CHILD INSTRUMENT LEVEL
					List<InstrumentBean> lClubbedInstList = instrumentBO.getClubbedBeans(pConnection, lInstrumentBean.getId());
					if(lClubbedInstList!=null && lClubbedInstList.size()>0){
						BigDecimal[] lTotalInst = new BigDecimal[] {BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO};
						for(InstrumentBean lInstBean : lClubbedInstList){
					        BigDecimal[] lInstLegInterests = computeLegInterest(lInstBean, pSettelmentDate, pBidBean.getHaircut(), pBidBean.getRate(), pBidBean.getCostLeg());
					        lInstBean.setPurchaserLeg1Interest(lInstLegInterests[INT_PUR_LEG1]);
					        lInstBean.setSupplierLeg1Interest(lInstLegInterests[INT_SUP_LEG1]);
					        lInstBean.setPurchaserLeg2Interest(lInstLegInterests[INT_PUR_LEG2]);
					        lTotalInst[INT_PUR_LEG1] = lTotalInst[INT_PUR_LEG1].add(lInstLegInterests[INT_PUR_LEG1]);
					        lTotalInst[INT_SUP_LEG1] = lTotalInst[INT_SUP_LEG1].add(lInstLegInterests[INT_SUP_LEG1]);
					        lTotalInst[INT_PUR_LEG2] = lTotalInst[INT_PUR_LEG2].add(lInstLegInterests[INT_PUR_LEG2]);
						}
						lLegInterests[INT_PUR_LEG1]=lTotalInst[INT_PUR_LEG1];
						lLegInterests[INT_SUP_LEG1]=lTotalInst[INT_SUP_LEG1];
						lLegInterests[INT_PUR_LEG2]=lTotalInst[INT_PUR_LEG2];
					}
	    		}
	    	}
	        //
	        //calculating limit utilised
	       	//set the factored amount
	        BigDecimal lFactoredAmount = pFactoringUnitBean.getAmount();
	        BigDecimal lAcceptedHaircut = pFactoringUnitBean.getAcceptedHaircut();
	    	if(lAcceptedHaircut!=null && lAcceptedHaircut.doubleValue() > 0){
	    		lFactoredAmount = lFactoredAmount.multiply(BigDecimal.valueOf(100.0-lAcceptedHaircut.doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
	    	}
			lLimitUtilised = lFactoredAmount.add(lLegInterests[INT_PUR_LEG2]);
	        //setting the second leg oblig amount
	        BidBean lNewBidBean = new BidBean();
	        bidDAO.getBeanMeta().copyBean(pBidBean, lNewBidBean);
	    	lNewBidBean.setLimitUtilised(lLimitUtilised);
	        lNewBidBean.setBidLimitUtilised(lLimitUtilised);
	        //
	        // if bid is ok update utilized values
	        updateBidLimits(pConnection, pFactoringUnitBean, pBidBean, lNewBidBean, pUserBean, lSettings, true, false);
	        //
	        //setting the values from the newly created bean to the existing after validation and updations
	        pBidBean.setLimitUtilised(lNewBidBean.getLimitUtilised());
	        pBidBean.setBidLimitUtilised(lNewBidBean.getBidLimitUtilised());
	        pBidBean.setLimitIds(lNewBidBean.getLimitIds());
	        //
	        Timestamp lTimestamp = new Timestamp(System.currentTimeMillis());
	        //
	        // update bid as accepted
	        pBidBean.setStatus(BidBean.Status.Accepted);
	        pBidBean.setTimestamp(lTimestamp);
	        bidDAO.update(pConnection, pBidBean, BidBean.FIELDGROUP_UPDATEACCEPTSTATUS);
	        bidDAO.insertAudit(pConnection, pBidBean, AuditAction.Update, (pUserBean!=null?pUserBean.getId():new Long(0)));
	
	        // update factoring unit as factored
	     	pFactoringUnitBean.setPurchaserLeg1Interest(lLegInterests[INT_PUR_LEG1]);
	     	pFactoringUnitBean.setSupplierLeg1Interest(lLegInterests[INT_SUP_LEG1]);
	     	pFactoringUnitBean.setPurchaserLeg2Interest(lLegInterests[INT_PUR_LEG2]);
	     	pFactoringUnitBean.setFactoredAmount(lFactoredAmount);
	     	//
	        pFactoringUnitBean.setFinancier(pBidBean.getFinancierEntity());
	        //
	        if(pUserBean!=null){
	            pFactoringUnitBean.setAcceptingEntity(pUserBean.getDomain());
	        }else{
	            if(CostBearer.Buyer.equals(lInstrumentBean.getBidAcceptingEntityType()))
	            	pFactoringUnitBean.setAcceptingEntity(pFactoringUnitBean.getPurchaser());
	            else if(CostBearer.Seller.equals(lInstrumentBean.getBidAcceptingEntityType()))
	            	pFactoringUnitBean.setAcceptingEntity(pFactoringUnitBean.getSupplier());
	        }
	        pFactoringUnitBean.setAcceptDateTime(lTimestamp);
	        pFactoringUnitBean.setAcceptingAuId((pUserBean!=null?pUserBean.getId():new Long(0)));
	        pFactoringUnitBean.setLimitUtilized(lNewBidBean.getLimitUtilised());
	        pFactoringUnitBean.setLimitIds(lNewBidBean.getLimitIds());
	        pFactoringUnitBean.setStatus(FactoringUnitBean.Status.Factored);
	        pFactoringUnitBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
	        pFactoringUnitBean.setLeg1Date(pSettelmentDate);
	        //
	        
	        
	        //
	        //set setllement location
	        pFactoringUnitBean.setPurchaserSettleLoc(lInstrumentBean.getPurSettleClId());
	        pFactoringUnitBean.setSupplierSettleLoc(lInstrumentBean.getSupSettleClId());
	        pFactoringUnitBean.setPurchaserBillLoc(lInstrumentBean.getPurBillClId());
	        pFactoringUnitBean.setSupplierBillLoc(lInstrumentBean.getSupBillClId());
	        for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lSettings) {
	        	if(lFinancierAuctionSettingBean.getLevel().equals(FinancierAuctionSettingBean.Level.Financier_Buyer)){
	        		AppEntityBean lAEFinancer= getAppEntityBean(pFactoringUnitBean.getFinancier());
	        		if(lFinancierAuctionSettingBean.getFinClId()!= null){
	        			pFactoringUnitBean.setFinancierSettleLoc(lFinancierAuctionSettingBean.getFinClId());
	        		}else{
	        			CompanyLocationBean lCompanyLocationBean = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection,lAEFinancer.getCdId());
	        			pFactoringUnitBean.setFinancierSettleLoc(lCompanyLocationBean.getId());
	        		}
	        		if (lAEFinancer.getPreferences()!=null 
	        				&& lAEFinancer.getPreferences().getElb()!=null
	        				&& CommonAppConstants.Yes.Yes.equals(lAEFinancer.getPreferences().getElb())) {
	        			BillingLocationBean lBillingLocationBean = new BillingLocationBean();
	        			lBillingLocationBean.setBillLocId(pFactoringUnitBean.getFinancierSettleLoc());
	        			lBillingLocationBean = billingLocationBO.findBean(pConnection, lBillingLocationBean);
	        			pFactoringUnitBean.setFinancierSettleLoc(lBillingLocationBean.getId());
	        		}else {
	        			pFactoringUnitBean.setFinancierSettleLoc(pFactoringUnitBean.getFinancierSettleLoc());
	        		}
	        	}
	        
	        }
	        //
	        //set the GST Percentage and Surcharge prevaling at time of factoring
	        setCharge(pConnection, pFactoringUnitBean, pBidBean);
	        //set the compute values
	        //
	        factoringUnitDAO.update(pConnection, pFactoringUnitBean, FactoringUnitBean.FIELDGROUP_ACCEPTBID);
	        factoringUnitDAO.insertAudit(pConnection, pFactoringUnitBean, AuditAction.Update, (pUserBean!=null?pUserBean.getId():new Long(0)));
	        BigDecimal lTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, pFactoringUnitBean.getPurchaser());
	        TredsHelper.getInstance().updateTurnover(pConnection, pFactoringUnitBean.getPurchaser(), lTurnover ,(pFactoringUnitBean.getFactoredAmount().multiply(BigDecimal.ONE)), (pUserBean!=null?pUserBean.getId():new Long(0)));
	        BigDecimal lFinacierTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, pFactoringUnitBean.getFinancier());
	        TredsHelper.getInstance().updateTurnover(pConnection, pFactoringUnitBean.getFinancier(), lFinacierTurnover ,(pFactoringUnitBean.getFactoredAmount().multiply(BigDecimal.ONE)), (pUserBean!=null?pUserBean.getId():new Long(0)));
	        //
	        ObligationBean lFinancierObligationBean, lSupplierObligationBean, lPurchaserObligationBeanLeg2;
	        ObligationBean[] lObligations = generateObligations(pConnection, lInstrumentBean, pFactoringUnitBean, pSettelmentDate, lLegInterests, (pUserBean!=null?pUserBean.getId():new Long(0)));
	        //
	        lFinancierObligationBean = lObligations[0];
	        lSupplierObligationBean = lObligations[1];
	        lPurchaserObligationBeanLeg2 = lObligations[2];
	
	        //
	        // leg 3 obligations
	        if(pFactoringUnitBean.getAcceptedHaircut()!=null && pFactoringUnitBean.getAcceptedHaircut().longValue() > 0){
	            BigDecimal lLeg3Amount = pFactoringUnitBean.getAmount().multiply(pFactoringUnitBean.getAcceptedHaircut()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
	        	generateLeg3Obligations(pConnection, pFactoringUnitBean, lInstrumentBean,  pUserBean, lLeg3Amount, true);
	        	if (pFactoringUnitBean.getPurSupLimitUtilized() != null) {
	        	    // release haircut
//	        	    BigDecimal lHaircut = pFactoringUnitBean.getAmount().multiply(pFactoringUnitBean.getAcceptedHaircut()).divide(new BigDecimal(100));
//	        	    purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization( pConnection, pFactoringUnitBean.getPurchaser(), 
//	                        pFactoringUnitBean.getSupplier(), pUserBean, lHaircut, true);
//	        	    pFactoringUnitBean.setPurSupLimitUtilized(pFactoringUnitBean.getPurSupLimitUtilized().subtract(lHaircut));
	        	}
	            updateStatus(pConnection, pFactoringUnitBean, pFactoringUnitBean.getStatus(), pUserBean);
	        }
	        //
	        lInstrumentBeanPostMonetago = instrumentBO.updateStatusUsingFactoringUnit(pConnection, lInstrumentBean, 
	                InstrumentBean.Status.Factored, pUserBean);
	        //this following condition will never arise
	        if(lInstrumentBeanPostMonetago==null)
	        	throw new CommonBusinessException("Instrument not found for Factoring Unit Id "+pFactoringUnitBean.getId().toString());
	        //
	        //now that we have completed accepting this bid, we will release any limits set by other financiers bid for this FU.
	        releaseLimits(pConnection, pFactoringUnitBean, pUserBean);
        	List<BidBean> lUnAcccepteBids = getUnacceptedBids(pConnection, pBidBean.getFuId(), pBidBean.getId());
       		updateUnacceptedBids(pConnection, lUnAcccepteBids);
	        pConnection.commit();
	        // send email one to bid acceptor and one to the bid placer and his admin
	        emailGeneratorBO.sendBidAcceptanceEmails(pConnection, lInstrumentBeanPostMonetago, pFactoringUnitBean, 
	                pBidBean, lSupplierObligationBean, lPurchaserObligationBeanLeg2, lFinancierObligationBean, 
	                pUserBean);   
		
			emailGeneratorBO.sendEmailslUnAcccepteBids(pConnection, lInstrumentBeanPostMonetago, pFactoringUnitBean, 
                lUnAcccepteBids,pUserBean);

		    return true;
    	} catch (Exception lException) {
    		logger.error("Error while accepting bid " + pBidBean.getId() , lException);
    		lException.printStackTrace();
    		pConnection.rollback();
    		//TODO: Revert FactoringUnit Status
            unLockFactoringUnit(pConnection, pFactoringUnitBean);
            //
    		String lInstNoMsg = "";
			//make a list of all the failed childs instrumentids
			//remove the failed ids from the parent instruments
			//using the parent instruments new values update the factoring Unit
			//update the child insturments which failed, if they are factored then update the factoring txn id
    		if(lInstrumentBeanPostMonetago == null &&  lInstrumentBean!=null && 
    				CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
    			
	            List<InstrumentBean> lFactoredInstList = getInstrumentsFactoredByMonetago(pConnection, lInstrumentBean, pUserBean.getId());
	            if(lFactoredInstList!=null && !lFactoredInstList.isEmpty()){
	            	lInstNoMsg =  reStructureInstrumentBean(pConnection, lInstrumentBean, lFactoredInstList, pUserBean);
	            	throw new MonetagoBusinessException(lInstNoMsg+" Grouped instrument " + lInstrumentBean.getId() + " has been restructured. Please refresh.");
	            }else{
	            	throw new CommonBusinessException (lException.getMessage());
	            }
    		}else{
            	throw new CommonBusinessException (lException.getMessage());
    		}
    	}
    }    
    private void setCharge(Connection pConnection, FactoringUnitBean pFactoringUnitBean, BidBean pBidBean) throws Exception{
    	MemberwisePlanBean lMPBean = TredsHelper.getInstance().getPlan(pConnection, pFactoringUnitBean.getPurchaser(), TredsHelper.getInstance().getBusinessDate(),true);
		BigDecimal lChargeValue = null;
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pFactoringUnitBean.getPurchaser());
		boolean lNormalCaluation = false;
		String lChargeBearerEntity = null;
		if ( pFactoringUnitBean.getPurchaser().equals(pFactoringUnitBean.getChargeBearerEntityCode())) {
			lNormalCaluation = true;
			lChargeBearerEntity = pFactoringUnitBean.getChargeBearerEntityCode();
		}else if ( pFactoringUnitBean.getSupplier().equals(pFactoringUnitBean.getChargeBearerEntityCode())){
			lNormalCaluation = true;
			MemberwisePlanBean lMPBean1 = TredsHelper.getInstance().getPlan(pConnection, pFactoringUnitBean.getSupplier(), TredsHelper.getInstance().getBusinessDate(),true);
			lChargeBearerEntity = (lMPBean1==null?pFactoringUnitBean.getPurchaser():pFactoringUnitBean.getSupplier());
			if (lMPBean1!=null) {
				lMPBean = lMPBean1;
			}
		}
		if ((lAppEntityBean.getPreferences()!=null && CommonAppConstants.Yes.Yes.equals(lAppEntityBean.getPreferences().getAcs()))
			|| !lNormalCaluation	){
			//split or percentage split
			if(CostBearingType.Percentage_Split.equals(pFactoringUnitBean.getChargeBearer()) || CostBearingType.Periodical_Split.equals(pFactoringUnitBean.getChargeBearer())) {
				BigDecimal[] lChargers = computeCharge(pConnection,pFactoringUnitBean, pFactoringUnitBean.getLeg1Date());
				setGstSummary(pConnection, pFactoringUnitBean, pFactoringUnitBean.getPurchaser(), pFactoringUnitBean.getBillLocationIdForChargeSplit(CostBearer.Buyer), lChargers[0],ChargeType.Normal,null);
				setGstSummary(pConnection, pFactoringUnitBean, pFactoringUnitBean.getSupplier(), pFactoringUnitBean.getBillLocationIdForChargeSplit(CostBearer.Seller), lChargers[1],ChargeType.Normal,null);
			}			
		}
		if(lNormalCaluation) {
			if(CommonAppConstants.Yes.Yes.equals(lMPBean.getFinancierBearShare())){
	    		BigDecimal[] lAmtArr = TredsHelper.getInstance().getComputeSplitChargeValue(pBidBean,pFactoringUnitBean,pFactoringUnitBean.getLeg1Date());
	 	    	setGstSummary(pConnection, pFactoringUnitBean,pFactoringUnitBean.getChargeBearerEntityCode(), pFactoringUnitBean.getChargeBearerBillLocationId(), lAmtArr[1],ChargeType.Split, null);
	 	    	BigDecimal lFinCharge = lAmtArr[0];
	 	    	setGstSummary(pConnection, pFactoringUnitBean, pFactoringUnitBean.getFinancier(), (pFactoringUnitBean.getFinancierBillLoc()!=null?pFactoringUnitBean.getFinancierBillLoc():pFactoringUnitBean.getFinancierSettleLoc()), lFinCharge,ChargeType.Split, null);
	 	    	lChargeValue = TredsHelper.getInstance().getComputeChargeValue(pBidBean.getNormalChargeType(),pBidBean.getNormalMinAmt(),pBidBean.getNormalPercent(),pBidBean.getNormalMaxAmt(), pFactoringUnitBean,pFactoringUnitBean.getFactoredAmount() );  
	 		    setGstSummary(pConnection, pFactoringUnitBean, pFactoringUnitBean.getFinancier(), (pFactoringUnitBean.getFinancierBillLoc()!=null?pFactoringUnitBean.getFinancierBillLoc():pFactoringUnitBean.getFinancierSettleLoc()), lChargeValue,ChargeType.Normal, null);
	    	}else{
		    	lChargeValue = getNormalPlanCharge(pConnection, pFactoringUnitBean, lChargeBearerEntity, pFactoringUnitBean.getChargeBearerEntityCode(), pFactoringUnitBean.getFactoredAmount(), pFactoringUnitBean.getChargeBearerBillLocationId());
		    	setGstSummary(pConnection, pFactoringUnitBean, pFactoringUnitBean.getChargeBearerEntityCode(), pFactoringUnitBean.getChargeBearerBillLocationId(), lChargeValue,ChargeType.Normal, null);
		    	if (pBidBean.getNormalChargeType()==null) {
		    		lChargeValue = getNormalPlanCharge(pConnection, pFactoringUnitBean, pFactoringUnitBean.getFinancier(), pFactoringUnitBean.getFinancier(), pFactoringUnitBean.getFactoredAmount(), (pFactoringUnitBean.getFinancierBillLoc()!=null?pFactoringUnitBean.getFinancierBillLoc():pFactoringUnitBean.getFinancierSettleLoc()));
		    	}else {
		    		lChargeValue = TredsHelper.getInstance().getComputeChargeValue(pBidBean.getNormalChargeType(),pBidBean.getNormalMinAmt(),pBidBean.getNormalPercent(),pBidBean.getNormalMaxAmt(), pFactoringUnitBean,pFactoringUnitBean.getFactoredAmount() );
		    	}
		    	setGstSummary(pConnection, pFactoringUnitBean, pFactoringUnitBean.getFinancier(), (pFactoringUnitBean.getFinancierBillLoc()!=null?pFactoringUnitBean.getFinancierBillLoc():pFactoringUnitBean.getFinancierSettleLoc()), lChargeValue,ChargeType.Normal, null);
			}
		}
    }

    public Object[] getSplitPlanDetails(Connection pConnection, FactoringUnitBean pFactoringUnitBean,String pEntityPlan, String pFinancier, BigDecimal pFactoredAmount, AppEntityBean pFinEntityBean, Date pSettlementDate) throws Exception{
    	//0=Financier Normal Percent
    	//1=Financier Normal Amount
    	//2=ChargeBearer Split Charge percent
    	//3=Split Min value
    	//4=Financier Split Charge percent
    	Object[] lCharges = new Object[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,BigDecimal.ZERO,null };
    	BigDecimal lChargeValue = BigDecimal.ZERO;
    	//
	   	Long lFinancierSettleLoc = null;
	   	if(pFactoringUnitBean.getFinancierSettleLoc() == null && pFinEntityBean != null){
	   		lFinancierSettleLoc = TredsHelper.getInstance().getSettlementLocation(pConnection, pFinEntityBean.getCdId());
	   	}else{
	   		lFinancierSettleLoc = pFactoringUnitBean.getFinancierSettleLoc();
	   	}
    	MemberwisePlanBean lMPBean = TredsHelper.getInstance().getPlan(pConnection, pFactoringUnitBean.getPurchaser(), TredsHelper.getInstance().getBusinessDate(),true);
	   	if(CommonAppConstants.Yes.Yes.equals(lMPBean.getFinancierBearShare())){
	        Long lPlanId =TredsHelper.getInstance().getCurrentPlan(pConnection, pEntityPlan);
	        if(lPlanId == null){
	        	throw new CommonBusinessException("No active plan found for the buyer " + pFactoringUnitBean.getPurchaser() + ".");
	        }
	        AuctionChargePlanBean lAuctionChargePlanBean = TredsHelper.getInstance().getPlanDetails(pConnection, lPlanId);
	        BigDecimal lTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, pFactoringUnitBean.getPurchaser());
	        logger.info("lTurnover  : "+ (lTurnover!=null?lTurnover:""));
	        AuctionChargeSlabBean lAcsBean =  TredsHelper.getInstance().getChargableDetailsForFinancier(lAuctionChargePlanBean, pFactoringUnitBean, pFactoredAmount, lMPBean.getTotalShare(),  lMPBean.getFinancierShare() , pSettlementDate);
	        lCharges[2] = lAcsBean.getChargePercentValue().subtract(lMPBean.getFinancierShare()).max(BigDecimal.ZERO);
	        lCharges[3] = lAcsBean.getChargeAbsoluteValue();
	        lCharges[4] = lMPBean.getFinancierShare();
	   	}
    	AuctionChargeSlabBean lAcsBean = getNormalPlanFinancierDetails(pConnection, pFactoringUnitBean, pFinancier, pFinancier, pFactoredAmount, lFinancierSettleLoc);
    	lCharges[1] = lAcsBean.getChargeAbsoluteValue();
        lCharges[0] = lAcsBean.getChargePercentValue();
    	lCharges[5] = lAcsBean.getChargeMaxValue();
        lCharges[6] = lAcsBean.getChargeType();
        return lCharges;
    }
    
    
    private BigDecimal getNormalPlanCharge(Connection pConnection, FactoringUnitBean pFactoringUnitBean,String pEntityPlan, String pChargeBearingEntity, BigDecimal pFactoredAmount, Long pSettlementLocation) throws Exception{
    	return TredsHelper.getInstance().getNormalPlanCharge(pConnection, pFactoringUnitBean, pEntityPlan, pChargeBearingEntity, pFactoredAmount,pSettlementLocation,null,null);
    }
    
    private AuctionChargeSlabBean getNormalPlanFinancierDetails(Connection pConnection, FactoringUnitBean pFactoringUnitBean,String pEntityPlan, String pChargeBearingEntity, BigDecimal pFactoredAmount, Long pSettlementLocation) throws Exception{
        BigDecimal lChargeValue = new BigDecimal(0);
        Long lPlanId =TredsHelper.getInstance().getCurrentPlan(pConnection, pEntityPlan);
        if(lPlanId == null){
        	String lName="";
        	if(pFactoringUnitBean.getPurchaser().equals(pEntityPlan)){
        		lName = "buyer";
        	}else {
        		lName = "financier";
        	}
        	throw new CommonBusinessException("No active plan found for the "+ lName + " " + pChargeBearingEntity + ".");
        }
        AuctionChargePlanBean lAuctionChargePlanBean = TredsHelper.getInstance().getPlanDetails(pConnection, lPlanId);
        BigDecimal lTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, pChargeBearingEntity);
        AuctionChargeSlabBean lAcsBean = TredsHelper.getInstance().getChargableDetailsForFinancier(lAuctionChargePlanBean, pFactoringUnitBean, pFactoredAmount, null, null, null);
        return lAcsBean;
    }


    public void setGstSummary(Connection pConnection, FactoringUnitBean pFactoringUnitBean, String pChargeBearingEntity, Long pSettlementLocation,  BigDecimal pChargeValue, ChargeType pChargeType, Long pObId) throws Exception{
        BigDecimal[] lCgstSgstIgstValues = null;
        BigDecimal lTdsValue = BigDecimal.ZERO;
        Timestamp lTimestamp = new Timestamp(System.currentTimeMillis());
        GSTRateBean lGSTRateBean = TredsHelper.getInstance().getGSTRate(pConnection, CommonUtilities.getDate(lTimestamp), pChargeValue);
        AppEntityBean lAEChargeBearer = getAppEntityBean(pChargeBearingEntity);
        CompanyLocationBean lSetllementLocation = null;
        if(pSettlementLocation!=null){
        	lSetllementLocation = new CompanyLocationBean();
        	lSetllementLocation.setId(pSettlementLocation);
          lSetllementLocation = companyLocationDAO.findByPrimaryKey(pConnection, lSetllementLocation);
        }
        if(lSetllementLocation==null)
        	lSetllementLocation = TredsHelper.getInstance().getRegisteredOfficeLocation(pConnection, lAEChargeBearer.getCdId());
        if(lSetllementLocation == null)
        	throw new CommonBusinessException("Charge Bearers Office Address not found");
        lCgstSgstIgstValues = TredsHelper.getInstance().getCgstSgstIgst(lGSTRateBean,  pChargeValue, lSetllementLocation.getState());
        GstSummaryBean lGstSummaryBean = pFactoringUnitBean.getGstSummary(pChargeBearingEntity, pChargeType,pObId);
        if(lGstSummaryBean == null){
        	lGstSummaryBean = pFactoringUnitBean.addGstSummaryBean(pChargeBearingEntity, pChargeType);
        }
        // to calculate TDS value
        PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
        lPSLFilterBean.setPurchaser(pFactoringUnitBean.getPurchaser());
        lPSLFilterBean.setSupplier(pFactoringUnitBean.getSupplier());
        PurchaserSupplierLinkBean lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
        lPurchaserSupplierLinkBean.populateNonDatabaseFields();
        if(PurchaserSupplierLinkBean.Status.Active.equals(lPurchaserSupplierLinkBean.getStatus())){
        	if(lPurchaserSupplierLinkBean.getBuyerTds() != null && 
        			CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getBuyerTds())){
    			if(AppConstants.CostBearingType.Buyer.equals(lPurchaserSupplierLinkBean.getChargeBearer())){
    				if(lPurchaserSupplierLinkBean.getPurchaser().equals(pFactoringUnitBean.getPurchaser())){
        	    		if(lPurchaserSupplierLinkBean.getBuyerTdsPercent().compareTo(BigDecimal.ZERO) > 0){
        	    			lGstSummaryBean.setTds(lPurchaserSupplierLinkBean.getBuyerTdsPercent());
        	    			lTdsValue = pChargeValue.multiply(lPurchaserSupplierLinkBean.getBuyerTdsPercent()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
        	    			lGstSummaryBean.setTdsValue(lTdsValue);
        	    		}
    				}
    			}
        	}else{
        		if(lPurchaserSupplierLinkBean.getSellerTds() != null && 
            			CommonAppConstants.YesNo.Yes.equals(lPurchaserSupplierLinkBean.getSellerTds())){
        			if(AppConstants.CostBearingType.Seller.equals(lPurchaserSupplierLinkBean.getChargeBearer())){
        				if(lPurchaserSupplierLinkBean.getSupplier().equals(pFactoringUnitBean.getSupplier())){
                    		if(lPurchaserSupplierLinkBean.getSellerTdsPercent().compareTo(BigDecimal.ZERO) > 0){
                    			lGstSummaryBean.setTds(lPurchaserSupplierLinkBean.getSellerTdsPercent());
                    			lTdsValue = pChargeValue.multiply(lPurchaserSupplierLinkBean.getSellerTdsPercent()).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
                    			lGstSummaryBean.setTdsValue(lTdsValue);
                    		}
        				}
        			}
        		}
        	}
        }
        lGstSummaryBean.setCharge(pChargeValue);
        lGstSummaryBean.setCgst(lGSTRateBean.getCgst());
        lGstSummaryBean.setSgst(lGSTRateBean.getSgst());
        lGstSummaryBean.setIgst(lGSTRateBean.getIgst());
        lGstSummaryBean.setCgstSurcharge(lGSTRateBean.getCgstSurcharge());
        lGstSummaryBean.setSgstSurcharge(lGSTRateBean.getSgstSurcharge());
        lGstSummaryBean.setIgstSurcharge(lGSTRateBean.getIgstSurcharge());
        lGstSummaryBean.setCgstValue(lCgstSgstIgstValues[0]);
        lGstSummaryBean.setSgstValue(lCgstSgstIgstValues[1]);
        lGstSummaryBean.setIgstValue(lCgstSgstIgstValues[2]);
        lGstSummaryBean.setObId(pObId);
    }
      
    private ObligationBean[] generateObligations(Connection pConnection, InstrumentBean pInstrumentBean, FactoringUnitBean pFactoringUnitBean, Date pSettelmentDate, BigDecimal[] pLegInterests, Long pUserAuId) throws Exception{
        List<ObligationBean> lObligations = new ArrayList<ObligationBean>();
    	BigDecimal lTredsChrgesAndTax = BigDecimal.ZERO, lTredsChrgSupplier = BigDecimal.ZERO, lTredsChrgPurchaser = BigDecimal.ZERO, lTredsChrgFinancier = BigDecimal.ZERO;
    	Date lLeg2Date = pFactoringUnitBean.getMaturityDate();
    	ObligationBean[] lPurchaserObligations = new ObligationBean[2];
    	if(pFactoringUnitBean.getEntityGstSummaryList() !=null){
        	for (GstSummaryBean lBean : pFactoringUnitBean.getEntityGstSummaryList()) {
        		if(lBean.getEntity().equals(pFactoringUnitBean.getFinancier())){
        			lTredsChrgFinancier = lTredsChrgFinancier.add(lBean.getTotalCharge());
        		}else if(lBean.getEntity().equals(pFactoringUnitBean.getChargeBearerEntityCode())){
        			if(CostBearingType.Buyer.equals(pFactoringUnitBean.getChargeBearer())){
        				lTredsChrgPurchaser = lTredsChrgPurchaser.add(lBean.getTotalCharge());
        			}else if(CostBearingType.Seller.equals(pFactoringUnitBean.getChargeBearer())){
        				lTredsChrgSupplier = lTredsChrgSupplier.add(lBean.getTotalCharge());
        	    	}
        		}else if(
    	    			CostBearingType.Percentage_Split.equals(pFactoringUnitBean.getChargeBearer())
    	    			|| CostBearingType.Periodical_Split.equals(pFactoringUnitBean.getChargeBearer()) ){
    	    		if (lBean.getEntity().equals(pFactoringUnitBean.getSupplier())) {
    	    			lTredsChrgSupplier = lTredsChrgSupplier.add(lBean.getTotalCharge());
    	    		}
    	    		if (lBean.getEntity().equals(pFactoringUnitBean.getPurchaser())) {
    	    			lTredsChrgPurchaser = lTredsChrgPurchaser.add(lBean.getTotalCharge());
    	    		}
    	    	}
    		}
    	}
    	lTredsChrgesAndTax = lTredsChrgPurchaser.add(lTredsChrgSupplier).add(lTredsChrgFinancier);
    	if(CommonAppConstants.Yes.Yes.equals(pFactoringUnitBean.getEnableExtension())){
    		lLeg2Date = pFactoringUnitBean.getExtendedDueDate();
    	}
    	//set the factored amount
		BigDecimal lFactoredAmount = pFactoringUnitBean.getAmount();
    	if(pFactoringUnitBean.getAcceptedHaircut()!=null && pFactoringUnitBean.getAcceptedHaircut().doubleValue() > 0){
    		lFactoredAmount = lFactoredAmount.multiply(BigDecimal.valueOf(100.0-pFactoringUnitBean.getAcceptedHaircut().doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
    	}
    	//
        ObligationBean lFinancierObligationBean, lSupplierObligationBean, lPurchaserObligationBeanLeg2;
    	ObligationBean lObligationBean = null;
    	Long lFinancierSettlementClId = null;
    	FinancierAuctionSettingBean lFinancierAuctionSettingBean = TredsHelper.getInstance().getFinancierAuctionSettingBean(pConnection, pFactoringUnitBean.getFinancier(), pFactoringUnitBean.getPurchaser());
		if(lFinancierAuctionSettingBean!=null){
    		lFinancierSettlementClId = TredsHelper.getInstance().getSettlementLocation(pConnection, lFinancierAuctionSettingBean.getFinClId());
		}
    	//LEG1
        // debit financier leg1amount
        lFinancierObligationBean = constructObligationBean(pFactoringUnitBean, pFactoringUnitBean.getFinancier(), TxnType.Debit, 
        		lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]).subtract(pLegInterests[INT_PUR_LEG1]).add(lTredsChrgFinancier),  Type.Leg_1, pSettelmentDate, lFinancierSettlementClId, ObligationBean.Status.Ready,  null);
        lObligations.add(lFinancierObligationBean);
        if( ((pLegInterests[INT_PUR_LEG1]).compareTo(BigDecimal.ZERO) == 1 ) ||
        		(lTredsChrgPurchaser.compareTo(BigDecimal.ZERO) == 1) ){
            lObligationBean = constructObligationBean(pFactoringUnitBean, pFactoringUnitBean.getPurchaser(), TxnType.Debit, 
            		pLegInterests[INT_PUR_LEG1].add(lTredsChrgPurchaser),  Type.Leg_1, pSettelmentDate, pFactoringUnitBean.getPurchaserSettleLoc(), ObligationBean.Status.Ready, null);
            lObligations.add(lObligationBean);
            lPurchaserObligations[0] = lObligationBean;
        }//ELSE IF SUPPLIER BEARS THEN ALREADY ADJUSTED IN THE CREDIT OF SUPPLIER
        // credit supplier leg1amount
        lSupplierObligationBean = constructObligationBean(pFactoringUnitBean, pFactoringUnitBean.getSupplier(), TxnType.Credit, 
        		lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]).subtract(lTredsChrgSupplier),  Type.Leg_1, pSettelmentDate, pFactoringUnitBean.getSupplierSettleLoc(), ObligationBean.Status.Created,  null);
        lObligations.add(lSupplierObligationBean);
        //TREDSCHARGES
        if(lTredsChrgesAndTax.compareTo(BigDecimal.ZERO) == 1){
        	//ADDING THE TREDS SUP AND PUR CHARGES SINCE ONE WILL BE ZERO
        	lObligationBean = constructObligationBean(pFactoringUnitBean, AppConstants.DOMAIN_PLATFORM , TxnType.Credit, 
        			lTredsChrgesAndTax,  Type.Leg_1, pSettelmentDate, null, ObligationBean.Status.Created,  null);
        	lObligations.add(lObligationBean);
        }
        //
        //LEG 2
        lObligationBean = constructObligationBean(pFactoringUnitBean, pFactoringUnitBean.getFinancier(), TxnType.Credit, 
        		lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]),  Type.Leg_2, lLeg2Date, lFinancierSettlementClId, ObligationBean.Status.Created,  null);
        lObligations.add(lObligationBean);
        lPurchaserObligationBeanLeg2 = constructObligationBean(pFactoringUnitBean, pFactoringUnitBean.getPurchaser(), TxnType.Debit, 
        		lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]),  Type.Leg_2, lLeg2Date, pFactoringUnitBean.getPurchaserSettleLoc(), ObligationBean.Status.Ready,  null);        
        lObligations.add(lPurchaserObligationBeanLeg2);
        lPurchaserObligations[1] = lPurchaserObligationBeanLeg2;
        //
        for (ObligationBean lObliBean : lObligations) {
        	if(lObliBean!=null){
        		lObliBean.setId(TredsHelper.getInstance().getObligationId(pConnection));
        		lObliBean.setRecordCreator(pUserAuId);
        		lObliBean.setOriginalDate(lObliBean.getDate());
                obligationDAO.insert(pConnection, lObliBean);
        	}
        }
        //
    	getObligationsSplits(pConnection,pInstrumentBean.getPurSettleClId(), pFactoringUnitBean.getPurchaser(), pFactoringUnitBean.getFinancier(), lFinancierSettlementClId, lFinancierObligationBean,lPurchaserObligations ,lObligations,(CommonAppConstants.Yes.Yes.equals(pInstrumentBean.getGroupFlag())? true : false ), lFinancierAuctionSettingBean);
        //
        return  (new  ObligationBean[] {lFinancierObligationBean, lSupplierObligationBean, lPurchaserObligationBeanLeg2 });
    }
    
    private ObligationBean constructObligationBean(FactoringUnitBean pFactoringUnitBean, String pEntityCode, ObligationBean.TxnType pTxnType,
            BigDecimal pAmount, ObligationBean.Type pType, Date pObligationDate, Long pSettlementCLId, ObligationBean.Status pObligationStatus, String pNarration) throws CommonBusinessException {
        ObligationBean lObligationBean = null;
        if(pAmount!=null&&(pAmount.compareTo(BigDecimal.ZERO)!=0)){
            lObligationBean = new ObligationBean();
            lObligationBean.setTxnEntity(pEntityCode);
            lObligationBean.setTxnType(pTxnType);
            lObligationBean.setAmount(pAmount);
            if (lObligationBean.getAmount().compareTo(BigDecimal.ZERO) < 0) {
    			throw new CommonBusinessException(" Obligations with negative value cannot be generated.");
    		}
            lObligationBean.setOriginalAmount(pAmount);
            lObligationBean.setType(pType);
            lObligationBean.setNarration(pNarration);
            //
            lObligationBean.setFuId(pFactoringUnitBean.getId());
            lObligationBean.setBdId(pFactoringUnitBean.getBdId());
            lObligationBean.setCurrency(pFactoringUnitBean.getCurrency());
            lObligationBean.setSalesCategory(pFactoringUnitBean.getSalesCategory());
            //
            lObligationBean.setDate(pObligationDate); 
            lObligationBean.setStatus(pObligationStatus);
            lObligationBean.setSettlementCLId(pSettlementCLId);
        }else{
        	logger.info("Skipping obligation , For Entity :" + pEntityCode + " , TxnType : " + pTxnType + " , Type :" + pType + " , Narration : "+pNarration);
        }
        return lObligationBean;
    }
    public int generateLeg3Obligations(Connection pConnection, FactoringUnitBean pFactoringUnitBean,InstrumentBean pInstrumentBean,
    		 AppUserBean pUserBean, BigDecimal pLeg3Amount, boolean pOnBidAcceptance) throws Exception{
    	//while 1. bid accepting and 2. generating leg3 manually for expired factoring units
    	//if leg 3 settlement is not required then do not go ahead
    	if(!CommonAppConstants.YesNo.Yes.equals(pFactoringUnitBean.getSettleLeg3Flag())){
    		return 0;
    	}
    	if(pLeg3Amount == null || pLeg3Amount.longValue() <= 0){
    		return 0;
    	}
    	if(pOnBidAcceptance){
    		//while bid accepting the fu and instru mush not be expired
    		//we have to check fu status
    		if ((InstrumentBean.Status.Expired.equals(pInstrumentBean.getStatus()) || 
        			FactoringUnitBean.Status.Expired.equals(pFactoringUnitBean.getStatus()))){
    			throw new CommonBusinessException("Insturment/Factoring Unit has expired.");
    		}
    	}
    	
    	int lObligationCount = 0;
        // leg 3 obligations
    	//if the settlement is made complusory by the buyer then do the settlement
        //Leg3 wont contain any charges
    	
    	//for manual/automatic Leg3 generation check the flag else no need if it comes through bid acceptance
    	ObligationBean lLeg3Obligation = null;
        String[] lLeg3Entities = null;
        ObligationBean.TxnType[] lLeg3TransTypes = null;
        BigDecimal[] lLeg3Amounts = null;
        ObligationBean[] lLeg3Obligations = null;
        
        lLeg3Entities = new String[] {pFactoringUnitBean.getPurchaser(), pFactoringUnitBean.getSupplier()};
        lLeg3TransTypes = new ObligationBean.TxnType[] {ObligationBean.TxnType.Debit, ObligationBean.TxnType.Credit};
        lLeg3Amounts = new BigDecimal[] {pLeg3Amount,pLeg3Amount};
        lLeg3Obligations = new ObligationBean[2]; //TODO:this might be used in sms if not then remove

        List<ObligationBean> lObligations = new ArrayList<ObligationBean>();
        ObligationBean lPurchaserObliBean = null;
        for(int lPtr=0; lPtr < lLeg3Entities.length; lPtr++){
        	lLeg3Obligation = new ObligationBean();
        	lLeg3Obligation.setId(TredsHelper.getInstance().getObligationId(pConnection));
        	lLeg3Obligations[lPtr] = lLeg3Obligation;
            lLeg3Obligation.setFuId(pFactoringUnitBean.getId());
            lLeg3Obligation.setBdId(pFactoringUnitBean.getBdId()!=null?pFactoringUnitBean.getBdId():new Long(0));//TODO: here we cant pass null
            lLeg3Obligation.setTxnEntity(lLeg3Entities[lPtr]);
            lLeg3Obligation.setTxnType(lLeg3TransTypes[lPtr]);
            lLeg3Obligation.setDate(pInstrumentBean.getStatDueDate()); //StatutoryDate
            lLeg3Obligation.setCurrency(pFactoringUnitBean.getCurrency());
            lLeg3Obligation.setAmount(lLeg3Amounts[lPtr]);
            lLeg3Obligation.setOriginalAmount(lLeg3Amounts[lPtr]);
            lLeg3Obligation.setType(ObligationBean.Type.Leg_3);
            lLeg3Obligation.setNarration(null);
            lLeg3Obligation.setStatus((ObligationBean.TxnType.Debit.equals(lLeg3TransTypes[lPtr])?ObligationBean.Status.Ready:ObligationBean.Status.Created));
            lLeg3Obligation.setRecordCreator((pUserBean!=null?pUserBean.getId():new Long(0)));
            lLeg3Obligation.setSalesCategory(pFactoringUnitBean.getSalesCategory());
            obligationDAO.insert(pConnection, lLeg3Obligation);
            lObligations.add(lLeg3Obligation);
            if(lPtr==0){
            	lPurchaserObliBean = lLeg3Obligation;
            }
            lObligationCount++;
        }
        //for splitting of the obligations - this 
        if(lObligationCount > 0 ){
        	TredsHelper.getInstance().getObligationsSplitsForPurchaser(pConnection, pInstrumentBean, pFactoringUnitBean, lPurchaserObliBean, lObligations);
        }
        return lObligationCount;
    }
    
    private boolean valuesChanged(Object[] pValues1, Object[] pValues2) {
        int lCount = pValues1.length;
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            Object lValue1 = pValues1[lPtr];
            Object lValue2 = pValues2[lPtr];
            if(lValue1==null && lValue2==null){
            	//nothing changed, hence continue loop
            }else if (lValue1!=null && lValue2!=null){
            	if(lValue1 instanceof BigDecimal){
            		if(((BigDecimal) lValue1).compareTo(((BigDecimal)lValue2))!=0)
            			return true;
            	}else{
            		if(!lValue1.equals(lValue2)){
            			return true;
            		}
            	}
            }else{
            	return true;
            }
        }
        return false;
    }
    
    private void updateBestBid(Connection pConnection, FactoringUnitBean pFactoringUnitBean, Date pBusinessDate) throws Exception {
		//fetching both type of bids and getting the first bid as the best bid.
		//the sorting is done on basis of rate, time asc and type (reserved gets priority if both rates are same)
        List<BidBean> lBids = getBids(pConnection, pFactoringUnitBean.getId(), null, null, 1, pBusinessDate);
        BidBean lBestBidBean = null;
        if (lBids.size() == 1)
            lBestBidBean = lBids.get(0);
        updateBestBidWithBidBean(pConnection, pFactoringUnitBean, lBestBidBean);
    }
    
    private List<BidBean> getBids(Connection pConnection, Long pFuId, Long pBidId, BidBean.BidType pBidType, int pRecordCount, Date pBusinessDate) throws Exception {
    	Date lBusinessDate = pBusinessDate;
    	if(pBusinessDate == null){
            AuctionCalendarBean lAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
            if (lAuctionCalendarBean == null) {
                throw new CommonBusinessException("Auction calendar not found");
            }
            lBusinessDate = lAuctionCalendarBean.getDate();
    	}
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("SELECT * FROM Bids WHERE BDFuId = ").append(pFuId);
        lSql.append(" AND BDRate IS NOT NULL AND BDStatus = ").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
        lSql.append(" AND (BDValidTill IS NULL OR BDValidTill >= ").append(lDBHelper.formatDate(lBusinessDate)).append(")");
        if (pBidId != null)
            lSql.append(" AND BDId = ").append(pBidId);
        if (pBidType != null)
            lSql.append(" AND BDBidType = ").append(lDBHelper.formatString(pBidType.getCode()));
        lSql.append(" ORDER BY BDRate ASC, BDBidType Desc, BDTimestamp ASC, BDId ASC");
        return bidDAO.findListFromSql(pConnection, lSql.toString(), pRecordCount);
    }
    
    private List<PurchaserSupplierCapRateBean> getCapRateList(Connection pConnection, 
    		FactoringUnitBean pFactoringUnitBean, Date pSettlementDate) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
//        String lCostBearerEntity = pFactoringUnitBean.getCostBearer()==AppConstants.CostBearer.Buyer?
//        		pFactoringUnitBean.getPurchaser():pFactoringUnitBean.getSupplier();
        String lCostBearerOrOwnerEntity = null;
        if(pFactoringUnitBean.isCostSplit()){
        	//if cost is split then the the cap rate of the owner entity are to be applied
        	lCostBearerOrOwnerEntity = pFactoringUnitBean.getOwnerEntity();
        }else{
        	if(pFactoringUnitBean.isPurchaserCompleteCostBearer())
        		lCostBearerOrOwnerEntity = pFactoringUnitBean.getPurchaser();
        	else if(pFactoringUnitBean.isSupplierCompleteCostBearer())
        		lCostBearerOrOwnerEntity = pFactoringUnitBean.getSupplier();
        }
        
        Date lLeg2Date = pFactoringUnitBean.getMaturityDate();
        long lDuration = (lLeg2Date.getTime() - pSettlementDate.getTime())/86400000;
        lSql.append("SELECT * FROM PurchaserSupplierCapRate");
        lSql.append(" WHERE PSLEntityCode = ").append(lDBHelper.formatString(lCostBearerOrOwnerEntity));
        lSql.append(" AND PSLCounterEntityCode = ").append(lDBHelper.formatString(pFactoringUnitBean.getPurchaser()));// always purchaser in the table
        lSql.append(" AND ").append(lDuration).append(" BETWEEN PSLFromUsance AND PSLToUsance");
    	return purchaserSupplierCapRateDAO.findListFromSql(pConnection, lSql.toString(), 0);
    }
    /*private List<BidBean> getBidsForAcceptance(Connection pConnection, FactoringUnitBean pFactoringUnitBean, 
    		Long pBidId, Date pSettlementDate) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        String lCostBearerEntity = pFactoringUnitBean.getCostBearer()==AppConstants.CostBearer.Buyer?
        		pFactoringUnitBean.getPurchaser():pFactoringUnitBean.getSupplier();
        long lDuration = (pFactoringUnitBean.getMaturityDate().getTime() - pSettlementDate.getTime())/86400000;
        lSql.append("SELECT BDFuId, BDFinancierEntity,BDFinancierAuId,BDRate,BDHaircut,BDValidTill,");
        lSql.append("(CASE WHEN ").append(lDuration).append(" BETWEEN PSLFromHaircut AND PSLToHaircut THEN 'Y' ELSE '0' END) BDStatus");
        lSql.append(",BDId,BDTimestamp FROM Bids,PurchaserSupplierCapRate WHERE BDFuId = ").append(pFactoringUnitBean.getId());
        lSql.append(" AND BDRate IS NOT NULL AND BDStatus = ").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
        lSql.append(" AND (BDValidTill IS NULL OR BDValidTill > ").append(lDBHelper.formatDate(new Timestamp(System.currentTimeMillis()))).append(")");
        lSql.append(" AND PSLEntityCode = ").append(lDBHelper.formatString(lCostBearerEntity));
        lSql.append(" AND PSLCounterEntityCode = ").append(lDBHelper.formatString(pFactoringUnitBean.getPurchaser()));// always purchaser in the table
        lSql.append(" AND BDHaircut BETWEEN PSLFromHaircut AND PSLToHaircut");
        lSql.append(" AND ").append(lDuration).append(" BETWEEN PSLFromUsance AND PSLToUsance");
        lSql.append(" ORDER BY BDRate ASC, BDTimestamp ASC, BDId ASC");
        List<BidBean> lList = bidDAO.findListFromSql(pConnection, lSql.toString(), -1);
        return lList;
    }*/
    
    private void updateBestBidWithBidBean(Connection pConnection, FactoringUnitBean pFactoringUnitBean, BidBean pBidBean) throws Exception {
    	//PRASAD
		logger.info("Updating best bid for FU " + pFactoringUnitBean.getId() + " Bid Id : " + ((pBidBean!=null&&pBidBean.getId()!=null)?pBidBean.getId():""));
        if (pBidBean == null || pBidBean.getId()==null) {
            pFactoringUnitBean.setAcceptedBid(null);
            //pFactoringUnitBean.setFinancier(null);
        } else {
            pFactoringUnitBean.setAcceptedBid(pBidBean);
            //pFactoringUnitBean.setFinancier(pBidBean.getFinancierEntity());
        }
        factoringUnitDAO.update(pConnection, pFactoringUnitBean, FactoringUnitBean.FIELDGROUP_UPDATEBESTBID);
    }
    
    private Map<Object, FactoringUnitBidBean> getFinancierBids(Connection pConnection, List<Object> pFuIds, AppUserBean pUserBean) throws Exception {
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, true);
        if (!lAppEntityBean.isFinancier())
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        
        lSql.append("SELECT * FROM FactoringUnits,Bids, Instruments WHERE FURecordVersion > 0 AND INRecordVersion > 0 AND FUId = BDFuId  AND FUId = INFuId ");
        lSql.append(" AND BDFinancierEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        //lSql.append(" AND BDStatus = ").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
        lSql.append(" AND FUId IN (");
        for (Object lId : pFuIds)
            lSql.append(lId).append(",");
        lSql.setLength(lSql.length() - 1);
        lSql.append(")");
        lSql.append(" ORDER BY FUId");
        
        List<FactoringUnitBidBean> lList = factoringUnitBidDAO.findListFromSql(pConnection, lSql.toString(), -1);
        Map<Object, FactoringUnitBidBean> lMap = new HashMap<Object, FactoringUnitBidBean>();
        for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
            lMap.put(lFactoringUnitBidBean.getFactoringUnitBean().getId(), lFactoringUnitBidBean);
        }
        return lMap;
    }

    private Map<String, Object> getFactoringUnitBidJsonFin(Connection pConnection, FactoringUnitBidBean pFactoringUnitBidBean, AppUserBean pUserBean) throws CommonBusinessException {
        FactoringUnitBean lFactoringUnitBean = pFactoringUnitBidBean.getFactoringUnitBean();
        InstrumentBean lInstrumentBean = pFactoringUnitBidBean.getInstrumentBean();
        BidBean lBidBean = pFactoringUnitBidBean.getBidBean();
        boolean lDisplay =  BidBean.Status.Active.equals(lBidBean.getStatus()) ||  BidBean.Status.Accepted.equals(lBidBean.getStatus());
        Map<String, Object> lUnitJson = factoringUnitDAO.getBeanMeta().formatAsMap(lFactoringUnitBean, FactoringUnitBean.FIELDGROUP_FINLIST, null, false, true);
        lUnitJson.put("statusDesc", lFactoringUnitBean.getStatus());
        lUnitJson.put("costBearer", lFactoringUnitBean.getCostBearingType()); 
        //get relationship between buyer and seller
        Date lDateToCheck = new Date(lFactoringUnitBean.getRecordCreateTime().getTime());
        if(lInstrumentBean!=null) {
            lDateToCheck = new Date(lInstrumentBean.getRecordCreateTime().getTime());
        }
        boolean lRelationship = getRelationship(pConnection, lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getSupplier(), lDateToCheck);
        lUnitJson.put("relationship", lRelationship?"Y":"N");
        //
        Map<String, Object> lBidJson = bidDAO.getBeanMeta().formatAsMap(lBidBean, BidBean.FIELDGROUP_FINLIST, null, false, true);
        if(lDisplay){
            lBidJson.put("costLegDesc", (lBidBean!=null?lBidBean.getCostLeg():""));
            lBidJson.put("bidTypeDesc", lBidBean.getBidType());
            lBidJson.put("provBidTypeDesc", lBidBean.getProvBidType());
            if (lBidBean.getProvAction() != null)
                lBidJson.put("provActionDesc", lBidBean.getProvAction().toString());

            if(lBidBean.getStatus()!=null){
            	 if (lFactoringUnitBean != null) {
                	 lBidJson.put("cost", lFactoringUnitBean.getTotalCost());
                 }
            }
        }else{
        	lBidJson.clear();
        }
        Map<String, Object> lMap = new HashMap<String, Object>();
        lMap.put("unit", lUnitJson);
        lMap.put("bid", lBidJson);
        Long lTab = null;
        if (lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Active)
            lTab = TABFIN_ACTIVE;
        else if (lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Factored) {
            if (lFactoringUnitBean.getFinancier().equals(lBidBean.getFinancierEntity()))
                lTab = TABFIN_FACTORED;
            else
                lTab = TABFIN_OTHER;
        }
        lMap.put("tab", lTab);
        Boolean lOwner = Boolean.TRUE;
        if ((pUserBean.getType() != AppUserBean.Type.Admin) && (lBidBean.getFinancierAuId() != null) && (!lBidBean.getFinancierAuId().equals(pUserBean.getId()))){
            if(BidBean.Status.Active.equals(lBidBean.getStatus())){
            	lOwner = Boolean.FALSE;
            }
        }
        
        lMap.put("owner", lOwner);
        //
        lUnitJson.put("tenure", (lFactoringUnitBean.getTenure()));
        MemberwisePlanBean lMPBean = TredsHelper.getInstance().getPlan(pConnection, lFactoringUnitBean.getPurchaser(), TredsHelper.getInstance().getBusinessDate(),true);
        if(lMPBean==null) {
        	throw new CommonBusinessException("Factoring unit of this purchaser cannot be added now. Please contact platform admin.");
        }
        lMap.put("financierBearShare", (CommonAppConstants.Yes.Yes.equals(lMPBean.getFinancierBearShare())?true:false) ) ;
        lMap.put("financierShare",lBidBean.getNormalPercent().add(lBidBean.getSplitPercent()));
       // lMap.put("newfinancierShare",calculateFinacierChargeSharePercent(pConnection,lFactoringUnitBean,lBidBean).toString());
        if(lInstrumentBean!=null){
        	int lInstCount = 1;
        	if(CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
        		String lSql = " SELECT Count(*) ChildCount FROM INSTRUMENTS WHERE INRECORDVERSION > 0 AND INGROUPFLAG IS NULL AND INGROUPINID = "+lInstrumentBean.getId()  ;
        		Statement lStatement;
				try {
					lStatement = pConnection.createStatement();
	        		ResultSet lResultSet  = lStatement.executeQuery(lSql.toString());
	        		if(lResultSet.next()){
		        		lInstCount = lResultSet.getInt("ChildCount");
		                lUnitJson.put("instCount", lInstCount);
		                lUnitJson.put("inId", lInstrumentBean.getId());
	        		}
				} catch (Exception e) {
					logger.info("Error getFactoringUnitBidJsonFin : " + e.getMessage());
	                lUnitJson.put("instCount", 0);
	                lUnitJson.put("inId", 0);
				}
        	}else{
                lUnitJson.put("instCount", 0);
                lUnitJson.put("inId", lInstrumentBean.getId());
        	}
        }
        return lMap;
    }
    
    private boolean getRelationship(Connection pConnection, String pPurchaser, String pSupplier, Date pFromDate)  {
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM PURSUPRELATION_VW   WHERE 1=1 ");
    	lSql.append(" AND RPTPURCHASER = ").append(DBHelper.getInstance().formatString(pPurchaser));
    	lSql.append(" AND RPTSUPPLIER = ").append(DBHelper.getInstance().formatString(pSupplier));
    	lSql.append(" AND RPTSTARTDATE <= ").append(DBHelper.getInstance().formatDate(pFromDate));
    	lSql.append(" AND RPTENDDATE >= ").append(DBHelper.getInstance().formatDate(pFromDate));
    	lSql.append(" AND RPTRELATIONFLAG  = ").append(DBHelper.getInstance().formatString(YesNo.Yes.getCode()));
    	lSql.append(" ORDER BY RPTSTARTDATE ");
    	List<PurSupRelationInstBean> lPSRHBeans;
		try {
			lPSRHBeans = purSupRelationInstDAO.findListFromSql(pConnection, lSql.toString(), 0);
	    	if(lPSRHBeans!=null && !lPSRHBeans.isEmpty()){
	    		return true;
	    	}
		} catch (Exception e) {
			logger.error("Error in getRelationship : "+ e.getMessage());
		}
    	return false;
    }
    
    private AppEntityBean getAppEntityBean(AppUserBean pUserBean, boolean pCheckFinancier) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pUserBean.getDomain()});
        if (!lAppEntityBean.isPlatform() && (pCheckFinancier && !lAppEntityBean.isFinancier()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        return lAppEntityBean;
    }

    private AppEntityBean getAppEntityBean(String pEntityCode) throws Exception {
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        AppEntityBean lAppEntityBean = (AppEntityBean)lMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pEntityCode});
        return lAppEntityBean;
    }

    public String getBidLogJson(Connection pConnection, Long pFactoringUnitId, String pFinancierEntity, IAppUserBean pLoggedInUser) throws Exception {
    	List<Map<String, Object>> lBidMapList = new ArrayList<Map<String, Object>>();
    	List<BidBean> lBidLog =  getBidLog(pConnection, pFactoringUnitId, pFinancierEntity, pLoggedInUser);

        for (BidBean lBidBean : lBidLog) {
        	lBidMapList.add(bidDAO.getBeanMeta().formatAsMap(lBidBean, null, null, true, true));
        }
        
        return new JsonBuilder(lBidMapList).toString();
    }

    public List<BidBean> getBidLog(Connection pConnection, Long pFactoringUnitId, String pFinancierEntity, IAppUserBean pLoggedInUser) throws Exception {
    	String lSql = null;
    	AppUserBean lAppUserBean = (AppUserBean)pLoggedInUser;
        lSql = "SELECT * FROM BIDS_A ";
        lSql += " WHERE BDFUID = " + pFactoringUnitId.toString();
        if(!AppConstants.DOMAIN_PLATFORM.equals(lAppUserBean.getDomain())){
        	lSql += " AND BDFINANCIERENTITY = " + DBHelper.getInstance().formatString(lAppUserBean.getDomain());
        }else{
        	lSql += " AND BDFINANCIERENTITY = " + DBHelper.getInstance().formatString(pFinancierEntity);
        }
		lSql += " ORDER BY ACTIONTIME desc ";

        return bidDAO.findListFromSql(pConnection, (String)lSql, 0);
    }
    
    
    public String getFactoringUnitJson(Connection pConnection, Long pFactoringUnitId, AppUserBean pUserBean) throws Exception {
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        FactoringUnitBean lFactoringUnitBean = null;
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, false);
        List<InstrumentBean> lInstrumentBeans = null;
        //
        lSql.append("SELECT * FROM FactoringUnits WHERE FURecordVersion > 0");
        if (lAppEntityBean.isPurchaser() || lAppEntityBean.isSupplier()) {
        	StringBuilder lWhere1, lWhere2, lWhere3;
        	lWhere1 = new StringBuilder();
        	lWhere2 = new StringBuilder();
        	lWhere3 = new StringBuilder();
        	//
        	lWhere1.append(" FUIntroducingEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	lWhere2.append(" FUCounterEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	lWhere3.append(" FUOwnerEntity = ").append(lDBHelper.formatString(pUserBean.getDomain()));
        	//
            if (pUserBean.getType() != AppUserBean.Type.Admin) {
                if (TredsHelper.getInstance().checkOwnership(pUserBean)) {
	               	lWhere1.append(" AND ( FUIntroducingAUId = ").append(pUserBean.getId()).append(" OR FUIntroducingAUId IS NULL ) ");
	                lWhere2.append(" AND ( FUCounterAUId = ").append(pUserBean.getId()).append(" OR FUCounterAUId IS NULL ) ");
	                lWhere3.append(" AND ( FUOwnerAuId = ").append(pUserBean.getId()).append(" OR  FUOwnerAuId IS NULL ) ");
                }
            }
            
            lSql.append(" AND ( ");
            lSql.append(" ( ").append(lWhere1.toString()).append(" ) ");
            lSql.append(" OR ( ").append(lWhere2.toString()).append(" ) ");
            lSql.append(" OR ( ").append(lWhere3.toString()).append(" ) ");
            lSql.append(" ) ");
        }
		lSql.append(" AND ").append(" FUID = ").append(pFactoringUnitId);
        lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lSql.toString());

        Map<String, Object> lUnitJson = factoringUnitDAO.getBeanMeta().formatAsMap(lFactoringUnitBean, null, null, true, true);
        lUnitJson.put("settleLeg3FlagDesc", lFactoringUnitBean.getSettleLeg3Flag());
        lUnitJson.put("settleLeg3Flag", (lFactoringUnitBean.getSettleLeg3Flag()!=null?lFactoringUnitBean.getSettleLeg3Flag().getCode():"")); //changing the desc to actual code since in the above function 2nd last parameter is true to send desc
        lUnitJson.put("statusDesc", lFactoringUnitBean.getStatus().toString());
        lUnitJson.put("owner", isOwner(lFactoringUnitBean, pUserBean));
        lUnitJson.put("isBuyer", (pUserBean.getDomain().equals(lFactoringUnitBean.getPurchaser())?Boolean.TRUE:Boolean.FALSE));
        lUnitJson.put("costBearingType", lFactoringUnitBean.getCostBearingType());
        //
        BigDecimal[] lBidRangeAndUtils = getBidRangeAndUtilizations(pConnection, lFactoringUnitBean, lAppEntityBean, pUserBean);
        lUnitJson.put("minBidRate",lBidRangeAndUtils[0]);
        lUnitJson.put("maxBidRate",lBidRangeAndUtils[1]);
        lUnitJson.put("balanceLimit", lBidRangeAndUtils[2]);
        lUnitJson.put("balanceBidLimit", lBidRangeAndUtils[3]);
        //
        lSql.replace(0, lSql.length(), "");
        lSql.append("SELECT * FROM Instruments WHERE INRecordVersion > 0 ");
        lSql.append(" AND INFUId = ").append( lFactoringUnitBean.getId());
        lInstrumentBeans = instrumentDAO.findListFromSql(pConnection, lSql.toString(),0);
        
        List<Map<String, Object>> lInstrumentList = new ArrayList<Map<String,Object>>();
        Map<String, Object> lInstJson = null;
        String lLocationName = null;
        for(InstrumentBean lInstrumentBean : lInstrumentBeans){
        	lInstrumentBean.populateNonDatabaseFields();
            lInstJson = instrumentDAO.getBeanMeta().formatAsMap(lInstrumentBean, "dispInstrument", null, true, true);        
            if(lInstJson!=null){
                CompanyLocationBean lCompanyLocationBean = null;
                CompanyLocationBean lSettlementLocationBean = null;
                if(lInstrumentBean.getSupClId().longValue() > 0){
                    lCompanyLocationBean = new CompanyLocationBean();
                    lCompanyLocationBean.setId(lInstrumentBean.getSupClId());
                    lCompanyLocationBean = companyLocationDAO.findByPrimaryKey(pConnection, lCompanyLocationBean);
                    lLocationName = lCompanyLocationBean.getName();
                }else{
                	lLocationName = AppConstants.REG_OFFICE_DESC;
                }
                lInstJson.put("supLocation", lLocationName);
                lInstJson.put("supGstStateDesc", TredsHelper.getInstance().getGSTStateDesc(lInstrumentBean.getSupGstState()));
                lInstJson.put("supGstn", lInstrumentBean.getSupGstn());
                if(lInstrumentBean.getPurClId().longValue() > 0){
                    lCompanyLocationBean = new CompanyLocationBean();
                    lCompanyLocationBean.setId(lInstrumentBean.getPurClId());
                    lCompanyLocationBean = companyLocationDAO.findByPrimaryKey(pConnection, lCompanyLocationBean);
                    lLocationName = lCompanyLocationBean.getName();
                    if(lCompanyLocationBean!=null){
                		lSettlementLocationBean = new CompanyLocationBean();
                		lSettlementLocationBean.setId(lInstrumentBean.getPurSettleClId());
                		lSettlementLocationBean = companyLocationDAO.findByPrimaryKey(pConnection, lSettlementLocationBean);
                    }
                }else{
                	lLocationName = AppConstants.REG_OFFICE_DESC;
                }
                lInstJson.put("purLocation", lLocationName);
                lInstJson.put("purGstStateDesc", TredsHelper.getInstance().getGSTStateDesc(lInstrumentBean.getPurGstState()));
                lInstJson.put("purGstn", lInstrumentBean.getPurGstn());
                if(lSettlementLocationBean!=null){
                	lInstJson.put("settlePurLocation", lSettlementLocationBean.getName());
                	lInstJson.put("settlePurGstStateDesc",TredsHelper.getInstance().getGSTStateDesc(lSettlementLocationBean.getState()));
                	lInstJson.put("settlePurGstn",lSettlementLocationBean.getGstn());
                }
                //
                lInstJson.put("tenure", lInstrumentBean.getTenure());
                lInstJson.put("ewayBillNo",lInstrumentBean.getEwayBillNo());
                lInstJson.put("supplyType", lInstrumentBean.getSupplyType());
                lInstJson.put("docType",lInstrumentBean.getDocType());
                lInstJson.put("docNo", lInstrumentBean.getDocNo());
                lInstJson.put("fromPincode",lInstrumentBean.getFromPincode());
                lInstJson.put("toPincode",lInstrumentBean.getToPincode());
                lInstJson.put("transMode" , lInstrumentBean.getTransMode());
                lInstJson.put("transporterName",lInstrumentBean.getTransporterName());
                lInstJson.put("transporterId",lInstrumentBean.getTransporterId());
                lInstJson.put("transDocNo",lInstrumentBean.getTransDocNo());
                lInstJson.put("transDocDate", lInstrumentBean.getTransDocDate());
                lInstJson.put("vehicleNo", lInstrumentBean.getVehicleNo());
                if(CommonUtilities.hasValue(lInstrumentBean.getSalesCategory())){
                	lInstJson.put("salesCategoryDesc", TredsHelper.getInstance().getSalesCategoryDescription(lInstrumentBean.getSalesCategory()));
                }
            	lInstJson.put("cashDiscountAmount", (lInstrumentBean.getCashDiscountValue()==null?BigDecimal.ZERO:lInstrumentBean.getCashDiscountValue()));
                lInstJson.put("adjAmount",lInstrumentBean.getAdjAmount());
                if(CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
                	lInstJson.put("groupFlag", lInstrumentBean.getGroupFlag());
                }
                lInstrumentList.add(lInstJson);
            }
        }
        lUnitJson.put("insts",lInstrumentList);
        lUnitJson.put("fuid", lFactoringUnitBean.getId().toString()); //update the fuid since it will contain instid
        return new JsonBuilder(lUnitJson).toString();
    }
    
    private BigDecimal[] getBidRangeAndUtilizations(Connection pConnection, FactoringUnitBean pFactoringUnitBean, AppEntityBean pAppEntityBean, AppUserBean pUserBean) throws Exception{
    	BigDecimal[] lRetVal = new BigDecimal[4];
    	for(int lPtr=0; lPtr < lRetVal.length; lPtr++){
    		lRetVal[lPtr] = null;
    	}
        //find the bidding range
        if(pAppEntityBean!=null && pAppEntityBean.isFinancier()){
        	 List<FinancierAuctionSettingBean> lSettings = null;
             lSettings = getFinancierAuctionSettings(pConnection, pAppEntityBean.getCode(), pFactoringUnitBean.getPurchaser(), pFactoringUnitBean.getSupplier(), pUserBean.getId(), true);
             //
             BigDecimal lBaseRate = null;
             List<BigDecimal> lMinRate = new ArrayList<BigDecimal>();
             List<BigDecimal> lMaxRate = new ArrayList<BigDecimal>();
             List<BigDecimal> lBuyerLimitBalance = new ArrayList<BigDecimal>();
             List<BigDecimal> lBuyerBidLimitBalance = new ArrayList<BigDecimal>();
             for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lSettings) {
                 if (FinancierAuctionSettingBean.Level.Financier_Self.equals(lFinancierAuctionSettingBean.getLevel())){
                   	lBaseRate = lFinancierAuctionSettingBean.getBaseBidRate(pFactoringUnitBean.getTenure());
                   	break;
                 }
             }
             for (FinancierAuctionSettingBean lFinancierAuctionSettingBean : lSettings) {
                 if (FinancierAuctionSettingBean.Level.Financier_User.equals(lFinancierAuctionSettingBean.getLevel()) ||
                		 FinancierAuctionSettingBean.Level.Financier_Buyer.equals(lFinancierAuctionSettingBean.getLevel())){
            		 if(lFinancierAuctionSettingBean.getMinSpread() != null || lFinancierAuctionSettingBean.getMaxSpread() != null){
                		 if(lBaseRate!=null){
                        		if(lFinancierAuctionSettingBean.getMinSpread()!=null){
                        			lMinRate.add(lBaseRate.add(lFinancierAuctionSettingBean.getMinSpread()));
                        		}
                        		if(lFinancierAuctionSettingBean.getMaxSpread()!=null){
                        			lMaxRate.add(lBaseRate.add(lFinancierAuctionSettingBean.getMaxSpread()));
                        		}
                        	}
            		 }
            		 if(FinancierAuctionSettingBean.Level.Financier_Buyer.equals(lFinancierAuctionSettingBean.getLevel())){
            			 if(lFinancierAuctionSettingBean.getLimit()!=null)
            				 lBuyerLimitBalance.add(lFinancierAuctionSettingBean.getLimit().subtract((lFinancierAuctionSettingBean.getUtilised()!=null?lFinancierAuctionSettingBean.getUtilised():new BigDecimal(0))));
            			 if(lFinancierAuctionSettingBean.getBidLimit()!=null)
            				 lBuyerBidLimitBalance.add(lFinancierAuctionSettingBean.getBidLimit().subtract((lFinancierAuctionSettingBean.getBidLimitUtilised()!=null?lFinancierAuctionSettingBean.getBidLimitUtilised():new BigDecimal(0))));
            		 }
                 }
        		 if(FinancierAuctionSettingBean.Level.Financier_Self.equals(lFinancierAuctionSettingBean.getLevel())
        				 || FinancierAuctionSettingBean.Level.Financier_User.equals(lFinancierAuctionSettingBean.getLevel()) &&
        				 (lFinancierAuctionSettingBean.getMinBidRate() != null || lFinancierAuctionSettingBean.getMaxBidRate() != null)){
            		if(lFinancierAuctionSettingBean.getMinBidRate()!=null){
            			lMinRate.add(lFinancierAuctionSettingBean.getMinBidRate());
            		}
            		if(lFinancierAuctionSettingBean.getMaxBidRate()!=null){
            			lMaxRate.add(lFinancierAuctionSettingBean.getMaxBidRate());
            		}
        		 }
        		 if(FinancierAuctionSettingBean.Level.Financier_Buyer_Seller.equals(lFinancierAuctionSettingBean.getLevel())){
        			 if(lFinancierAuctionSettingBean.getLimit()!=null)
        				 lBuyerLimitBalance.add(lFinancierAuctionSettingBean.getLimit().subtract((lFinancierAuctionSettingBean.getUtilised()!=null?lFinancierAuctionSettingBean.getUtilised():new BigDecimal(0))));
        			 if(lFinancierAuctionSettingBean.getBidLimit()!=null)
        				 lBuyerBidLimitBalance.add(lFinancierAuctionSettingBean.getBidLimit().subtract((lFinancierAuctionSettingBean.getBidLimitUtilised()!=null?lFinancierAuctionSettingBean.getBidLimitUtilised():new BigDecimal(0))));
        		 }
             }
             if(lMinRate.size()>0){
            	 lRetVal[0] =Collections.max(lMinRate);
             }
             if(lMaxRate.size()>0){
            	 lRetVal[1] =Collections.min(lMaxRate);
             }
             if(lBuyerLimitBalance.size()>0){
            	 lRetVal[2] =Collections.min(lBuyerLimitBalance);
             }
             if(lBuyerBidLimitBalance.size()>0){
            	 lRetVal[3] =Collections.min(lBuyerBidLimitBalance);
             }
        }
    	return lRetVal;
    }
    
    public void autoExpireBids(Connection pConnection, Date pBusinessDate, AppUserBean pUserBean, boolean pCheckValidity) throws Exception{
        logger.info("Auto expiring bids..");
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<FactoringUnitBidBean> lList = null;
        FactoringUnitBean lFactoringUnitBean = null;
        BidBean lBidBean = null;
        List<FinancierAuctionSettingBean> lSettings = null;
        //
        lSql.append("SELECT * FROM FactoringUnits,Bids WHERE FURecordVersion > 0 AND FUId = BDFuId ");
        lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        lSql.append(" AND BDStatus = ").append(lDBHelper.formatString(BidBean.Status.Active.getCode()));
        lSql.append(" ORDER BY FUId");
        //
        lList = factoringUnitBidDAO.findListFromSql(pConnection, lSql.toString(), -1);
        //
        if(lList!=null && lList.size() > 0){
            //
            HashMap<Long, FactoringUnitBean> lFactoringUnits = new HashMap<Long, FactoringUnitBean>();
            for (FactoringUnitBidBean lFactoringUnitBidBean : lList) {
                lFactoringUnitBean = lFactoringUnitBidBean.getFactoringUnitBean();
                lBidBean = lFactoringUnitBidBean.getBidBean();
                lSettings = getFinancierAuctionSettings(pConnection, lBidBean.getFinancierEntity(), lFactoringUnitBean.getPurchaser(), 
                        lFactoringUnitBean.getSupplier(), lBidBean.getFinancierAuId(), false);
                boolean lDeleteBid = false;
                if (pCheckValidity) {
                	//PRASAD CHECK HERE
                	if(lBidBean.getRate()!=null){
                        if ((lBidBean.getValidTill() == null) || (lBidBean.getValidTill().compareTo(pBusinessDate) <= 0)) {
                            logger.info("Deleting expired bid " + lFactoringUnitBidBean.getBidBean().getId());
                            lDeleteBid = true;
                        }
                	}else{
                		//entry or modifcation of provisional bid and check the validity for same
                		if(BidBean.AppStatus.Pending.equals(lBidBean.getAppStatus())){
                            if ((lBidBean.getProvValidTill() == null) || (lBidBean.getProvValidTill().compareTo(pBusinessDate) <= 0)) {
                                logger.info("Deleting expired bid " + lFactoringUnitBidBean.getBidBean().getId());
                                lDeleteBid = true;
                            }
                		}
                	}
                }
                if (!lDeleteBid) {
                    try {
                    	if(lBidBean.getRate()!=null && BidBean.AppStatus.Approved.equals(lBidBean.getAppStatus()) ){
                            validateBidRate(lSettings, lBidBean.getRate(), lFactoringUnitBean.getTenure(),lBidBean.getValidTill());
                    	}
                    } catch (CommonBusinessException pException) {
                        logger.info("Deleting invalid rate bid " + lFactoringUnitBidBean.getBidBean().getId()+". "+pException.getMessage());
                        lDeleteBid = true;
                    }
                }
                if (lDeleteBid) {
                    deleteBid(pConnection, lFactoringUnitBidBean.getBidBean(), lFactoringUnitBidBean.getFactoringUnitBean(), lSettings, pUserBean,BidBean.Status.Expired);
                    if(!lFactoringUnits.containsKey(lFactoringUnitBean.getId())){
                        lFactoringUnits.put(lFactoringUnitBean.getId(), lFactoringUnitBean);
                    }
                }
            }
            //
            for(FactoringUnitBean lFUBean : lFactoringUnits.values()){
                updateBestBid(pConnection, lFUBean,null);
            }
        }
    }
    
    private void deleteBid(Connection pConnection, BidBean pBidBean, FactoringUnitBean pFactoringUnitBean, 
            List<FinancierAuctionSettingBean> pSettings, AppUserBean pUserBean) throws Exception{
    	deleteBid(pConnection, pBidBean, pFactoringUnitBean, pSettings, pUserBean,BidBean.Status.Deleted);
    }
        
    private BidBean deleteBid(Connection pConnection, BidBean pBidBean, FactoringUnitBean pFactoringUnitBean, 
            List<FinancierAuctionSettingBean> pSettings, AppUserBean pUserBean,BidBean.Status pStatusToUpdate) throws Exception{
        BidBean lCopyBidBean = new BidBean();
        //
       	logger.info("Bid received for deletion : " + pBidBean.getId());
        if(!(BidBean.Status.Deleted.equals(pBidBean.getStatus()) ||
        		BidBean.Status.Deleted_By_Owner.equals(pBidBean.getStatus())) 
        		|| BidBean.Status.Expired.equals(pBidBean.getStatus()) )
        {
            bidDAO.getBeanMeta().copyBean(pBidBean, lCopyBidBean);
            if(BidBean.Status.Expired.equals(pStatusToUpdate) && (pBidBean.getRate()!=null || pBidBean.getProvRate()!=null)){
            	pBidBean.setStatus(BidBean.Status.Expired);            	
            }else{
                pBidBean.setStatus(BidBean.Status.Deleted);
            }
            pBidBean.setTimestamp(new Timestamp(System.currentTimeMillis()));
            pBidBean.setLimitUtilised(BigDecimal.ZERO);
            pBidBean.setBidLimitUtilised(BigDecimal.ZERO);
            pBidBean.setLimitIds(null);
            pBidBean.setCheckerAuId(null);            

            updateBidLimits(pConnection, pFactoringUnitBean, lCopyBidBean, pBidBean, pUserBean, pSettings, false, true);

            // audit log financier auid , rate and valid till retained
            Long lUserId = new Long(0);
            if (pBidBean.getFinancierAuId()!=null) lUserId = pBidBean.getFinancierAuId();
            if (pUserBean!=null && pUserBean.getId()!=null) lUserId = pUserBean.getId();
            pBidBean.setLastAuId(lUserId);
            bidDAO.insertAudit(pConnection, pBidBean, GenericDAO.AuditAction.Update,  lUserId);
            pBidBean.clearFinalBid();
            pBidBean.clearProvBid();
            pBidBean.setAppStatus(null);
            pBidBean.setFinancierAuId(null);
            bidDAO.update(pConnection, pBidBean, BeanMeta.FIELDGROUP_UPDATE);
           	logger.info("Bid deleted : " + pBidBean.getId());
        }else{
        	logger.info("Deleted bid received for deletion : " + pBidBean.getId());
        }        
        //
        return pBidBean;
    }
    
    private void logBidError(BidBean pBidBean, String pStatusRemarks, AppUserBean pUserBean){
    	Connection lConnection =null;
    	try{
    		lConnection = DBHelper.getInstance().getConnection();
    		logger.info("logBidError : " +  (pBidBean!=null?pBidBean.getId():"") + " StatusRemarks : " + (pStatusRemarks!=null?pStatusRemarks:""));
    		pBidBean.setStatus(BidBean.Status.Auto_Reject);
    		pBidBean.setStatusRemarks(pStatusRemarks);
    		pBidBean.setTimestamp(new Timestamp(System.currentTimeMillis()));
            bidDAO.insertAudit(lConnection, pBidBean, GenericDAO.AuditAction.Update, pUserBean==null?new Long(0):pUserBean.getId());
    	}catch(Exception lException){
    		logger.info("Error while logging BidError for bid : " + (pBidBean!=null?pBidBean.getId():"") + " StatusRemarks : " + (pStatusRemarks!=null?pStatusRemarks:""));
    	}finally{
    			try {
    				if(lConnection!=null){
    	    			lConnection.close();
    	    			lConnection = null;
    	    		}
				} catch (SQLException e) {
					e.printStackTrace();
				}
    	}
    }
    


    public List<Map<String, Object>> getFinancierObligationJson(Connection pConnection, ObligationBean pObligationBean, AppUserBean pUserBean) throws Exception {
        DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        AppEntityBean lAppEntityBean = getAppEntityBean(pUserBean, false);
        List<Map<String, Object>> lObligations = new ArrayList<Map<String,Object>>();
        //
        lSql.append(" SELECT OBID id " );
        lSql.append(" , OBFUID fuId " );
        lSql.append(" , OBTYPE type " );
        lSql.append(" , OBTXNTYPE txnType " );
        lSql.append(" , TO_CHAR(OBLIGATIONS.OBDATE, 'DDMMYYYY') obliDate " );
        lSql.append(" , FinPur.FASPURCHASERREF buyerRef " );
        lSql.append(" , Fin.FASFINANCIERREF financierRef " );
        lSql.append(" , OBPAYDETAIL1 accountNo " );
        lSql.append(" , OBAMOUNT amount " );
        lSql.append(" , FACTORINGUNITS.FUFACTOREDAMOUNT  factoredAmount " );
        lSql.append(" , CASE FUENABLEEXTENSION ");
        lSql.append(" WHEN 'Y' THEN TO_CHAR(FACTORINGUNITS.FUExtendedDueDate, 'DDMMYYYY') " );
        lSql.append(" ELSE TO_CHAR(FACTORINGUNITS.FUMaturityDate, 'DDMMYYYY') END  dueDate ");
        lSql.append(" , ((CASE FUENABLEEXTENSION WHEN 'Y' THEN FACTORINGUNITS.FUExtendedDueDate ELSE FACTORINGUNITS.FUMaturityDate END ) - FULEG1DATE) tenor "); 
        lSql.append(" , CASE OBTYPE "); 
        lSql.append(" WHEN 'L1' THEN (NVL(FACTORINGUNITS.FUPURCHASERLEG1INTEREST,0) + NVL(FACTORINGUNITS.FUSUPPLIERLEG1INTEREST,0)) ");
        lSql.append(" WHEN 'L2' THEN (NVL(FACTORINGUNITS.FUPURCHASERLEG2INTEREST,0) + NVL(FACTORINGUNITS.FULEG2EXTENSIONINTEREST,0)) ");
        lSql.append(" ELSE 0 ");
        lSql.append(" END  interest " );
        lSql.append(" , NVL(AcceptedBid.BDHAIRCUT,0) haircutPercent " );
        lSql.append(" , CASE OBTYPE WHEN 'L3' THEN  (BDHAIRCUT/100*FUAMOUNT)  ELSE 0 END haircutAmount " );
        lSql.append(" , OBTXNENTITY entityCode " );	
        lSql.append(" , AppPur.AENAME buyerName " );
        lSql.append(" , AppSup.AENAME supplierName " );
        lSql.append(" , AppSup.AEPAN supplierPANCard " );
        lSql.append(" , AppPur.AEPAN buyerPANCard " );
        lSql.append(" , AcceptedBid.BDID bidId " );
        lSql.append(" , FACTORINGUNITS.FUPURCHASER buyerCode " );
        lSql.append(" , FACTORINGUNITS.FUACCEPTEDRATE acceptedBidRate " );
        lSql.append(" , FACTORINGUNITS.FUSUPPLIER supplierCode " );
        lSql.append(" , OBSTATUS status " );
        lSql.append(" , Maker.AULOGINID makerLoginId " );
        lSql.append(" , Checker.AULOGINID checkerLoginId " );
        lSql.append(" , OBLIGATIONS.OBPAYMENTREFNO paymentRefNo " );
        lSql.append(" , OBLIGATIONS.OBRESPERRORCODE respErrorCode " );
        lSql.append(" , OBLIGATIONS.OBRESPREMARKS respRemarks " );
        lSql.append("  FROM OBLIGATIONS " );
        lSql.append("  LEFT OUTER JOIN FACTORINGUNITS ON OBFUID = FUId AND OBTXNENTITY = FUFINANCIER " );
        lSql.append("  LEFT OUTER JOIN BIDS AcceptedBid ON FUBDID = AcceptedBid.BDID AND AcceptedBid.BDSTATUS = 'APT' " );
        lSql.append("  LEFT OUTER JOIN APPUSERS Maker ON Maker.AUID = AcceptedBid.BDFINANCIERAUID " );
        lSql.append("  LEFT OUTER JOIN APPUSERS Checker ON Checker.AUID = AcceptedBid.BDCHECKERAUID " );
        lSql.append("  LEFT OUTER JOIN FINANCIERAUCTIONSETTINGS Fin ON FACTORINGUNITS.FUFINANCIER = Fin.FASFINANCIER AND Fin.FASPURCHASER IS NULL AND Fin.FASAUID IS NULL AND Fin.FASLEVEL = 'YNNN' " );
        lSql.append("  LEFT OUTER JOIN FINANCIERAUCTIONSETTINGS FinPur ON FACTORINGUNITS.FUFINANCIER = FinPur.FASFINANCIER AND FACTORINGUNITS.FUPURCHASER = FinPur.FASPURCHASER AND FinPur.FASSUPPLIER IS NULL AND FinPur.FASAUID IS NULL AND FinPur.FASLEVEL = 'YYNN' " );
        lSql.append("  LEFT OUTER JOIN FINANCIERAUCTIONSETTINGS FinSup ON FACTORINGUNITS.FUFINANCIER = FinSup.FASFINANCIER AND FACTORINGUNITS.FUPURCHASER = FinSup.FASPURCHASER AND FACTORINGUNITS.FUSUPPLIER = FinSup.FASSUPPLIER AND FinSup.FASAUID IS NULL AND FinSup.FASLEVEL = 'YYYN' " );
        lSql.append("  LEFT OUTER JOIN APPENTITIES AppPur ON FACTORINGUNITS.FUPURCHASER = AppPur.AECODE " );
        lSql.append("  LEFT OUTER JOIN APPENTITIES AppSup ON FACTORINGUNITS.FUSUPPLIER = AppSup.AECODE " );
        lSql.append("  WHERE OBRECORDVERSION > 0 AND FURECORDVERSION > 0  AND  AppPur.AERecordVersion > 0 AND  AppSup.AERecordVersion > 0  " );

        //- CHECKER RECORDVERSION SHOULD NOT BE CHECKED SINCE IT CAN COME NULL
        if(lAppEntityBean.isFinancier()){
        	lSql.append( " AND OBTXNENTITY = ").append(lDBHelper.formatString(lAppEntityBean.getCode()));
        }else
        	throw new CommonBusinessException("Entity Not a Financier.");
        
        if(pObligationBean.getId()!=null) lSql.append(" AND  OBID = ").append(pObligationBean.getId());
        if(pObligationBean.getFuId()!=null) lSql.append(" AND  OBFUID = ").append(pObligationBean.getFuId());
        if(pObligationBean.getTxnType()!=null) lSql.append(" AND  OBTXNTYPE = ").append(lDBHelper.formatString(pObligationBean.getTxnType().getCode()));
        if(pObligationBean.getType()!=null) lSql.append(" AND  OBTYPE = ").append(lDBHelper.formatString(pObligationBean.getType().getCode())); //LEG
        if(pObligationBean.getStatus()!=null) lSql.append(" AND  OBSTATUS = ").append(lDBHelper.formatString(pObligationBean.getStatus().getCode()));
        if(pObligationBean.getDate()!=null) lSql.append(" AND  OBDATE >= ").append(lDBHelper.formatDate(pObligationBean.getDate()));
        if(pObligationBean.getFilterToDate()!=null) lSql.append(" AND  OBDATE <= ").append(lDBHelper.formatDate(pObligationBean.getFilterToDate()));
        
		String[] lColumns = {"id", "fuId", "type", "txnType", "obliDate", "buyerRef", "financierRef", "accountNo", "amount", "factoredAmount", "interest", "haircutPercent", "haircutAmount", "entityCode", "buyerName", "supplierName", "supplierPANCard", "buyerPANCard", "buyerCode", "acceptedBidRate", "supplierCode", "status", "makerLoginId", "checkerLoginId", "paymentRefNo", "dueDate", "tenor", "respErrorCode", "respRemarks" };
        Statement lStatement = null;
        ResultSet lResultSet = null;
        try {
            lStatement = pConnection.createStatement();
            lResultSet = lStatement.executeQuery(lSql.toString());
            int lColCount = lColumns.length; // lResultSet.getMetaData().getColumnCount();
            String lTmp = null;
            while (lResultSet.next()) {
            	Map<String, Object> lList = new HashMap<String, Object>();
                for (int lPtr=0;lPtr<lColCount;lPtr++){
                	lTmp = lResultSet.getString(lColumns[lPtr]);
                	lList.put(lColumns[lPtr], lTmp!=null?lTmp:"");
                }
                lObligations.add(lList);
            }
        } finally {
            if (lResultSet != null)
                lResultSet.close();
            if (lStatement != null)
                lStatement.close();
        }
        return lObligations;
    }
    
    private void getObligationsSplits(Connection pConnection, Long pPurSettleCLId, String pPurchaser, String pFinancier, Long pFinSettleCLId, ObligationBean pFinancierObligationBean, ObligationBean[] pPurchaserObligations,List<ObligationBean> pObligations,boolean pIsGroup, FinancierAuctionSettingBean pFinAucSettingBean) throws Exception{
    	FacilitatorEntityMappingBean lPurchaserFEMBean = null ,lFinancierFEMBean = null; 
    	BigDecimal	lMandateAmt = null, lMandateAmtFinancier = BigDecimal.ZERO, lMandateAmtPurchaser = BigDecimal.ZERO;
    	HashMap  lObliSplitSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_OBLIGATIONSPLITTING);
    	BigDecimal lLeg2Amount = BigDecimal.ZERO;
		//
    	lFinancierFEMBean = TredsHelper.getInstance().getNachBeanForFinancier(pConnection, pPurchaser, pFinancier, pFinAucSettingBean);
		lPurchaserFEMBean = TredsHelper.getInstance().getNachBeanForPurchaser(pConnection, pPurSettleCLId,pPurchaser);
		if(lPurchaserFEMBean==null){
			throw new CommonBusinessException("NACH mandate not found.");
		}
		ObligationBean lTmpLegBean  = null;
		for(int lPtr=0; lPtr<pPurchaserObligations.length;lPtr++){
			lTmpLegBean = pPurchaserObligations[lPtr];
			if(lTmpLegBean != null){
				if(lPurchaserFEMBean.getExpiry()!=null && lTmpLegBean.getDate().after(lPurchaserFEMBean.getExpiry())){
					throw new CommonBusinessException("Leg-" + (lPtr+1) + " date " + TredsHelper.getInstance().getFormattedDate(lTmpLegBean.getDate()) +  " falls beyond the NACH mandate expiry date "+ TredsHelper.getInstance().getFormattedDate(lPurchaserFEMBean.getExpiry()) + "."  );
				}
				//Leg2 amount of purchaser - the max amount
				if(Type.Leg_2.equals(lTmpLegBean.getType())){
					lLeg2Amount = lLeg2Amount.max(lTmpLegBean.getAmount());
				}
			}
		}
		if(!CommonAppConstants.YesNo.Yes.equals(lPurchaserFEMBean.getActive())){
			throw new CommonBusinessException("NACH mandate is inactive.");
		}
		//
    	int lObligationSplitCount = 1;
		AppEntityBean lPurchaserEntity = getAppEntityBean(pPurchaser);
		//
    	BigDecimal lMandatePercent =  BigDecimal.valueOf((Double)lObliSplitSetting.get(AppConstants.ATTRIBUTE_MANDATEPERCENT));
		lMandateAmtPurchaser = (lPurchaserFEMBean.getMandateAmount().multiply(lMandatePercent, MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
    	//
		if(CommonAppConstants.YesNo.Yes.equals(lPurchaserEntity.getAllowObliSplitting())){
			//
			//lMandateAmtFinancier = (lFinancierFEMBean.getMandateAmount().multiply(lMandatePercent, MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			lMandateAmtFinancier = lFinancierFEMBean.getMandateAmount();
			lMandateAmt = lMandateAmtFinancier.min(lMandateAmtPurchaser);
			
			if ((lLeg2Amount.compareTo(lMandateAmt))>0){
				//need to split
	    		BigDecimal[] lSplits = lLeg2Amount.divideAndRemainder(lMandateAmt);
	    		if(lSplits[1].compareTo(BigDecimal.ZERO)==1){
	    			lObligationSplitCount = lSplits[0].add(BigDecimal.ONE).intValue();
	    		}else{
	    			lObligationSplitCount = lSplits[0].intValue();
	    		}
			}
		}else{
			//NO SPLITTING THEN  ONLY CHECK THE OBLIGATIONS AMOUNT TO BE BELOW BOTH MANDATE VALUES 
    		if(lLeg2Amount.compareTo(lMandateAmtPurchaser) >= 0){
    			logger.info("Adjusted NACH mandate amount " + lMandateAmtPurchaser.toString() +" is less than instrument amount " + TredsHelper.getInstance().getFormattedAmount(lLeg2Amount, true) +"." );
    			throw new CommonBusinessException("Adjusted NACH mandate amount is less than Leg-2 amount.");
    		}
    		//no need to check financier mandate since it is checked at time of bidding itself
		}
		//splitting 
    	TredsHelper.getInstance().splitObligations(pConnection,pObligations,lObligationSplitCount);
    }
    
    
    private void validateFinancierMandate(Connection pConnection, FactoringUnitBean pFactoringUnitBean, String pFinancierPlacingBid, BigDecimal pHairCut, BigDecimal pRate, CostCollectionLeg pCostLeg, Date pValidityDate, boolean pIsGroupedInstrument) throws Exception{
    	FacilitatorEntityMappingBean lFinancierFEMBean = null; 
    	BigDecimal	lMandateAmtFinancier = BigDecimal.ZERO;
    	BigDecimal lLeg1Amount = null;
    	Date lComputedSettlementDate = pValidityDate ; //Leg 1 date = Bid Expiry + Grace - since we dont know the actual acceptance date and thus the settlementdate ie. Leg1Date
		AppEntityBean lPurchaserEntity = getAppEntityBean(pFactoringUnitBean.getPurchaser());
		//
		if(pValidityDate==null){
			lComputedSettlementDate = TredsHelper.getInstance().getBusinessDate();
		}
		//
    	lFinancierFEMBean = TredsHelper.getInstance().getNachBeanForFinancier(pConnection, pFactoringUnitBean.getPurchaser(), pFinancierPlacingBid, null);
    	if(lFinancierFEMBean==null){
			throw new CommonBusinessException("NACH mandate not found.");
		}
		if(!CommonAppConstants.YesNo.Yes.equals(lFinancierFEMBean.getActive())){
			throw new CommonBusinessException("NACH mandate is inactive.");
		}
		if(lFinancierFEMBean.getExpiry()!=null && lComputedSettlementDate.after(lFinancierFEMBean.getExpiry())){
			throw new CommonBusinessException("Leg-1 date " + TredsHelper.getInstance().getFormattedDate(lComputedSettlementDate) +  " falls beyond the NACH mandate expiry date "+ TredsHelper.getInstance().getFormattedDate(lFinancierFEMBean.getExpiry()) + "."  );
		}

		//mandate amount should be checked only if splitting is disallowed.
		if( !CommonAppConstants.YesNo.Yes.equals(lPurchaserEntity.getAllowObliSplitting())){
	    	//BigDecimal lMandatePercent =  BigDecimal.valueOf((Double)lObliSplitSetting.get(AppConstants.ATTRIBUTE_MANDATEPERCENT));
			//lMandateAmtFinancier = (lFinancierFEMBean.getMandateAmount().multiply(lMandatePercent, MathContext.DECIMAL128).divide(AppConstants.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
			lMandateAmtFinancier = lFinancierFEMBean.getMandateAmount();
			//
			//to calculate the Leg1Amount ie with interest
			 BigDecimal[] lLegIntrests = computeLegInterest(pFactoringUnitBean, lComputedSettlementDate, pHairCut, pRate, pCostLeg);
             //
             BigDecimal[] lLegAmounts = getLegAmounts(pFactoringUnitBean.getPurchaser(), pFactoringUnitBean.getSupplier(), pFinancierPlacingBid, pFactoringUnitBean.getAmount(), pHairCut, pFactoringUnitBean.getSettleLeg3Flag(), lLegIntrests, pFinancierPlacingBid );
             //
             if(lLegAmounts!=null){
            	 lLeg1Amount = lLegAmounts[0];
             }
			//
			//NO SPLITTING THEN  ONLY CHECK THE OBLIGATIONS AMOUNT TO BE BELOW BOTH MANDATE VALUES 
    		if(lLeg1Amount.compareTo(lMandateAmtFinancier) >= 0){
    			logger.info("Adjusted NACH mandate amount " + lMandateAmtFinancier.toString() +" is less than instrument amount " + TredsHelper.getInstance().getFormattedAmount(lLeg1Amount, true) +"." );
    			throw new CommonBusinessException("Adjusted NACH mandate amount is less than Leg-1 amount.");
    		}
		}
    	//
    }
    
    public boolean isGroupedInstrument(Connection pConnection, Long pFuId){
        InstrumentBean lInstrumentBean = new InstrumentBean();
        lInstrumentBean.setFuId(pFuId);
        try {
			lInstrumentBean = instrumentDAO.findBean(pConnection, lInstrumentBean);
	        lInstrumentBean.populateNonDatabaseFields();
	        return CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag());
		} catch (Exception e) {
			e.printStackTrace();
		}
        return false;
    }
    
    private List<InstrumentBean> getInstrumentsFactoredByMonetago(Connection pConnection, InstrumentBean pParentInstrumentBean, Long pAuId) throws CommonBusinessException{
		List<InstrumentBean> lChildBeansToRemove = new ArrayList<InstrumentBean>();
		List<InstrumentBean> lChildBeans = null;
		//
		try{
			if(pParentInstrumentBean.getGroupedInstruments()!=null) {
				lChildBeans = pParentInstrumentBean.getGroupedInstruments();
			}else {
				lChildBeans = instrumentBO.getClubbedBeans(pConnection, pParentInstrumentBean.getId());
			}
		}catch(Exception lEx1){
			throw new CommonBusinessException("Error while fetching grouped instrument.");
		}
		//
		int lChildSize = lChildBeans.size()-1; 
		for(int lPtr=lChildSize; lPtr >= 0; lPtr--){
			//boolean lFactored = false;
			InstrumentBean lChildBean = lChildBeans.get(lPtr);
			try{
				if (lChildBean.getMonetagoFactorTxnId()==null) {
					Map<String,String> lResult = MonetagoTredsHelper.getInstance().getInvoiceByLedgerId(lChildBean.getMonetagoLedgerId());
					//chinmay
					//check response status = 1 = Factored
					if(lResult.containsKey(MonetagoTredsHelper.TREDS_RESPONSE_FACTSTATUS)){
	 					if (Long.parseLong(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_FACTSTATUS))==MonetagoTredsHelper.MONETAGO_STATUS_FACTORED){
	 						//lFactored = true;
	 						if(lResult.containsKey(MonetagoTredsHelper.TREDS_RESPONSE_DUPLICATELEDGERID)){
	 							lChildBean.setStatusRemarks("Already factored in another exchange.");
	 							lChildBean.setStatus(InstrumentBean.Status.Expired);
	 							logger.info(" Exact match already factored. Ledger Id : "+lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_DUPLICATELEDGERID));
								lChildBean.setGroupInId(null);
	 						}else{
	 							//lChildBean.setMonetagoLedgerId(null);
	 							if(lChildBean.getMonetagoFactorTxnId()==null) {
	 								lChildBean.setMonetagoFactorTxnId("DUMMYSELFFACTORED");
	 							}
	 							lChildBean.setStatusRemarks("Self already factored.");
	 							lChildBean.setStatus(InstrumentBean.Status.Drafting);
	 							logger.info(" Self already factored. ");
	 						}
							lChildBeansToRemove.add(lChildBean);
							lChildBeans.remove(lPtr);
	 					}
					}else {
						lChildBean.setStatus(InstrumentBean.Status.Drafting);
					}
				}
			}catch(Exception lMonetagoEx){
				logger.info("Error in reStructureInstrumentBean : " + lMonetagoEx.getMessage());
				throw new CommonBusinessException("Some error at monetago");
			}
		}
		pParentInstrumentBean.setGroupedInstruments(lChildBeans);
		return lChildBeansToRemove;
    }
    
    private String reStructureInstrumentBean(Connection pConnection,  InstrumentBean pParentInstrumentBean,List<InstrumentBean> pChildBeansToRemove, AppUserBean pAppUserBean){
    	String lReturnMsg = "";
    	try{
    		pConnection.setAutoCommit(false);
    		//
    		//fu has to be fetched again since the previous accptance has been rolledbacked.
			FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
			lFactoringUnitBean.setId(pParentInstrumentBean.getFuId());
			lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(pConnection, lFactoringUnitBean);
    		//remcompute instrument
    		if (pChildBeansToRemove.size()>0){
    			//
    			for (InstrumentBean lChildBean : pChildBeansToRemove){
    				if(!InstrumentBean.Status.Drafting.equals(lChildBean.getStatus())) {
        				instrumentDAO.update(pConnection, lChildBean , InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
        				instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pAppUserBean.getId());
    				}else {
    					//self factored, these have to be cancelled, dummyfactorid is set to them in previous logic
    					List<InstrumentBean> lTmpChilds = pParentInstrumentBean.getGroupedInstruments();
    					lTmpChilds.add(lChildBean);
    				}
    			}
    			//batch cancel in monetago
    			HashMap lInstClubSettings = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_INSTCLUBSPLITTING);
    			Integer lMaxInstruments = Integer.valueOf(lInstClubSettings.get(AppConstants.ATTRIBUTE_MAXNOOFFACTINSTRUMENTS).toString());
    			List<Map<InstrumentBean, String>> lMonetagoCancelList = new ArrayList<>();
				Map<InstrumentBean, String> lMonetagoCancelHash = new HashMap<InstrumentBean, String>();
				lMonetagoCancelList.add(lMonetagoCancelHash);
				for (InstrumentBean lChildBean : pParentInstrumentBean.getGroupedInstruments()) {
					if (lMonetagoCancelHash.size()==lMaxInstruments.intValue()) {
						lMonetagoCancelHash = new HashMap<InstrumentBean, String>();
						lMonetagoCancelList.add(lMonetagoCancelHash);
					}
					if(lChildBean.getMonetagoLedgerId()!=null && lChildBean.getMonetagoFactorTxnId()!=null) {
						lMonetagoCancelHash.put(lChildBean, lChildBean.getMonetagoLedgerId());
					}
				}
    			for (Map<InstrumentBean, String> lCancelHash : lMonetagoCancelList) {
    				Map<String, String> lRetMsg =  MonetagoTredsHelper.getInstance().cancelBatch(new ArrayList(lCancelHash.values()) ,CancelResonCode.NotFinanced);
    				if(lRetMsg!=null && lRetMsg.containsKey(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID)) {
    					for (InstrumentBean lMonetagoBean : lCancelHash.keySet()) {
    						lMonetagoBean.setMonetagoLedgerId(null);
    						lMonetagoBean.setMonetagoCancelTxnId(lRetMsg.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
    					}
    				}
    			}
    			for (InstrumentBean lChildBean : pParentInstrumentBean.getGroupedInstruments()){
    				lChildBean.setStatus(InstrumentBean.Status.Drafting);
    				lChildBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
    				lChildBean.setGroupInId(null);
    				instrumentDAO.update(pConnection, lChildBean , InstrumentBean.FIELDGROUP_UPDATEGROUPFIELDSCHILD);
    				instrumentDAO.insertAudit(pConnection, lChildBean, GenericDAO.AuditAction.Update, pAppUserBean.getId());
    			}
   				pParentInstrumentBean.setGroupedInstruments(null);
				lFactoringUnitBean.setAmount(BigDecimal.ZERO);
        		instrumentDAO.delete(pConnection, pParentInstrumentBean);
			    instrumentDAO.insertAudit(pConnection, pParentInstrumentBean, GenericDAO.AuditAction.Delete, pAppUserBean.getId());
        		//all the bids will anyways deleted so updating the fields wrt bids in Factoring Unit.
        		lFactoringUnitBean.setBdId(null);
        		lFactoringUnitBean.setAcceptedBidType(null);
        		lFactoringUnitBean.setAcceptedHaircut(null);
        		lFactoringUnitBean.setAcceptedRate(null);
			    if(!BigDecimal.ZERO.equals(lFactoringUnitBean.getAmount())){
	        		lFactoringUnitBean.setMaturityDate(pParentInstrumentBean.getMaturityDate());
	                lFactoringUnitBean.setStatDueDate(pParentInstrumentBean.getStatDueDate());
	                lFactoringUnitBean.setFactorEndDateTime(pParentInstrumentBean.getFactorMaxEndDateTime());
	                lFactoringUnitBean.setFactorMaxEndDateTime(pParentInstrumentBean.getFactorMaxEndDateTime());
	                lFactoringUnitBean.setExtendedCreditPeriod(pParentInstrumentBean.getExtendedCreditPeriod());
	                lFactoringUnitBean.setExtendedDueDate(pParentInstrumentBean.getExtendedDueDate());
			    	factoringUnitDAO.update(pConnection, lFactoringUnitBean,BeanMeta.FIELDGROUP_UPDATE);
				    factoringUnitDAO.insertAudit(pConnection, lFactoringUnitBean, AuditAction.Update, pAppUserBean.getId());
			    }else{
			    	factoringUnitDAO.delete(pConnection, lFactoringUnitBean);
					factoringUnitDAO.insertAudit(pConnection, lFactoringUnitBean, AuditAction.Delete, pAppUserBean.getId());
			    }
			    lReturnMsg = "For Fuid :"+lFactoringUnitBean.getId()+" Factoring Units, GroupInstruments and bids are deleted";
        		//chinmay
        		//
     			//physical remove -  all the bids for the factoring unit
			    int lCount = removeBidsAndSendEmailToFinanciers(pConnection, lFactoringUnitBean, pAppUserBean);
        		//
    		}
    		pConnection.commit();
    	}catch(Exception lExecption){
    		logger.info("Error in reStructureInstrumentBean : "+ lExecption.getMessage());
    		try {
				pConnection.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}finally{
    		
    	}
    	return lReturnMsg;
    }
    
    private int removeBidsAndSendEmailToFinanciers(Connection pConnection, FactoringUnitBean pFactoringUnitBean, AppUserBean pAppUserBean ) throws Exception{
		//incase any bids left to expire after the end of 
		//second window then the bids will be expired here
		StringBuilder lSql = new StringBuilder();
		int lCount =0;
		lSql.append(" Select * from Bids ");
		lSql.append(" WHERE BDID IS NOT NULL ");
		lSql.append(" AND BDFUID = ").append(pFactoringUnitBean.getId());
		lSql.append(" AND BDSTATUS = ").append(DBHelper.getInstance().formatString(BidBean.Status.Active.getCode()));
		List<BidBean> lBids = bidDAO.findListFromSql(pConnection, lSql.toString(), 0);
		Map<Long,String> lFactoringUnit = null;
		Map<Long,Map<Long,String>> lFinListBidRemoved = new HashMap<Long,Map<Long,String>>();
		List<Map<Long,String>> lFinListInfo = new ArrayList<Map<Long,String>>();
		if(lBids!=null){
			for(BidBean lBean:lBids){
				lFactoringUnit = new HashMap<Long,String>();
				if(lBean.getRate()!=null || lBean.getProvRate()!=null){
		         	lCount++;
		         	lFactoringUnit.put(lBean.getFuId(),lBean.getFinancierEntity());
					lFinListBidRemoved.put(lBean.getId(),lFactoringUnit);
				}else{
					lFactoringUnit.put(lBean.getFuId(),lBean.getFinancierEntity());
					lFinListInfo.add(lFactoringUnit);
				}
			}
		}
		deleteFactoringUnitBids(pConnection, pFactoringUnitBean, pAppUserBean );
		//send mail to financiers that your bid has been removed - lFinListBidRemoved
		//send mail to financiers that there is a change in the FU that you added to watch - lFinListInfo
		emailGeneratorBO.fuClubbedInstChangeEmail(pConnection,pFactoringUnitBean.getAmount(), lFinListBidRemoved, lFinListInfo,pFactoringUnitBean.getPurchaser(),pFactoringUnitBean.getSupplier(),pFactoringUnitBean.getId());
        return lCount;
	}
     
    public boolean hasCheckerForBid(Connection pConnection,Long pAuId){
    	List<MakerCheckerMapBean> lCheckers = null;
		try {
			lCheckers = appUserBO.getCheckers(pConnection, pAuId, MakerCheckerMapBean.CheckerType.Bid);
			if (lCheckers==null || lCheckers.isEmpty()){
	    		return false;
	    	}else{
	    		return true;
	    	}
		} catch (Exception e) {
			logger.info("error while fetching checker list");
		}
    	return false;
    }
    
    public void setBankDetails(Connection pConnection,boolean pPurchaser,String pEntity, Long pClid,FactoringUnitBean pFactoringUnitBean, HashMap<String, String> pBankHash) throws Exception{
		CompanyBankDetailBean lSettleBank = TredsHelper.getInstance().getSettlementBank(pConnection, pEntity, pClid);
		if (lSettleBank==null){
			lSettleBank = TredsHelper.getInstance().getDesignatedBank(pConnection, pEntity);
		}
		if (lSettleBank!=null){
			if (pPurchaser){
				pFactoringUnitBean.setPurAccNo(lSettleBank.getAccNo());
				pFactoringUnitBean.setPurIfsc(lSettleBank.getIfsc());
				pFactoringUnitBean.setPurBankName(pBankHash.get(lSettleBank.getBank()));
				if (CommonAppConstants.Yes.Yes.equals(lSettleBank.getDefaultAccount())){
					pFactoringUnitBean.setPurDesignatedBankFlag(Yes.Yes);
				}
			}else{
				pFactoringUnitBean.setSupAccNo(lSettleBank.getAccNo());
				pFactoringUnitBean.setSupIfsc(lSettleBank.getIfsc());
				pFactoringUnitBean.setSupBankName(pBankHash.get(lSettleBank.getBank()));
				if (CommonAppConstants.Yes.Yes.equals(lSettleBank.getDefaultAccount())){
					pFactoringUnitBean.setSupDesignatedBankFlag(Yes.Yes);
				}
			}
		}
    }

    public boolean changeFU(Connection pConnection, FactoringUnitBean pFactoringUnitBean, AppUserBean pUserBean, Date pSettelmentDate) throws Exception {
	    InstrumentBean lInstrumentBean = null;
            try {
                //check financiers blocked by purchasers
                AppEntityBean lAEPurchaser = getAppEntityBean(pFactoringUnitBean.getPurchaser());
                if(lAEPurchaser.isFinancierBlocked(pFactoringUnitBean.getFinancier()))
                        throw new CommonBusinessException("Financier has been blocked by the buyer");
               
            BidBean lBidBean = new BidBean();
            lBidBean.setId(pFactoringUnitBean.getBdId());
            BidBean pBidBean = bidDAO.findBean(pConnection, lBidBean);
        	//
            lInstrumentBean = new InstrumentBean();
            lInstrumentBean.setFuId(pFactoringUnitBean.getId());
            lInstrumentBean = instrumentDAO.findBean(pConnection, lInstrumentBean);
            lInstrumentBean.populateNonDatabaseFields();
                BigDecimal[] lLegInterests = computeLegInterest(pFactoringUnitBean, pSettelmentDate, pBidBean.getHaircut(), pBidBean.getRate(), pBidBean.getCostLeg());
            //
            //
            //calculating limit utilised
                   //set the factored amount
            BigDecimal lFactoredAmount = pFactoringUnitBean.getAmount();
            BigDecimal lAcceptedHaircut = pFactoringUnitBean.getAcceptedHaircut();
            if(lAcceptedHaircut!=null && lAcceptedHaircut.doubleValue() > 0){
                    lFactoredAmount = lFactoredAmount.multiply(BigDecimal.valueOf(100.0-lAcceptedHaircut.doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
            }
    
            // update factoring unit as factored
             pFactoringUnitBean.setPurchaserLeg1Interest(lLegInterests[INT_PUR_LEG1]);
             pFactoringUnitBean.setSupplierLeg1Interest(lLegInterests[INT_SUP_LEG1]);
             pFactoringUnitBean.setPurchaserLeg2Interest(lLegInterests[INT_PUR_LEG2]);
             pFactoringUnitBean.setFactoredAmount(lFactoredAmount);
            //
            pFactoringUnitBean.setLeg1Date(pSettelmentDate);
            //
            //
            //set the GST Percentage and Surcharge prevaling at time of factoring
            setCharge(pConnection, pFactoringUnitBean,pBidBean);
            //set the compute values
            //
                factoringUnitDAO.update(pConnection, pFactoringUnitBean, FactoringUnitBean.FIELDGROUP_ACCEPTBID);
                factoringUnitDAO.insertAudit(pConnection, pFactoringUnitBean, AuditAction.Update, (pUserBean!=null?pUserBean.getId():new Long(0)));
            //
                BigDecimal lTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, pFactoringUnitBean.getPurchaser());
                TredsHelper.getInstance().updateTurnover(pConnection, pFactoringUnitBean.getPurchaser(), lTurnover ,(pFactoringUnitBean.getFactoredAmount().multiply(BigDecimal.ONE)), (pUserBean!=null?pUserBean.getId():new Long(0)));
                BigDecimal lFinacierTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, pFactoringUnitBean.getFinancier());
                TredsHelper.getInstance().updateTurnover(pConnection, pFactoringUnitBean.getFinancier(), lFinacierTurnover ,(pFactoringUnitBean.getFactoredAmount().multiply(BigDecimal.ONE)), (pUserBean!=null?pUserBean.getId():new Long(0)));
            //
            ObligationBean[] lObligations = generateObligations2(pConnection, lInstrumentBean, pFactoringUnitBean, pSettelmentDate, lLegInterests, (pUserBean!=null?pUserBean.getId():new Long(0)));
            //
            //
            //now that we have completed accepting this bid, we will release any limits set by other financiers bid for this FU.
            pConnection.commit();
            // send email one to bid acceptor and one to the bid placer and his admin
                    //
                return true;
            } catch (Exception lException) {
                    logger.error("Error while accepting bid "  , lException);
                    lException.printStackTrace();
                    pConnection.rollback();
                    String lInstNoMsg = "";
                        //make a list of all the failed childs instrumentids
                        //remove the failed ids from the parent instruments
                        //using the parent instruments new values update the factoring Unit
                        //update the child insturments which failed, if they are factored then update the factoring txn id
            }
            return false;
    }    
    
    
    private ObligationBean[] generateObligations2(Connection pConnection, InstrumentBean pInstrumentBean, FactoringUnitBean pFactoringUnitBean, Date pSettelmentDate, BigDecimal[] pLegInterests, Long pUserAuId) throws Exception{
        List<ObligationBean> lObligations = new ArrayList<ObligationBean>();
            BigDecimal lTredsChrgesAndTax = BigDecimal.ZERO, lTredsChrgSupplier = BigDecimal.ZERO, lTredsChrgPurchaser = BigDecimal.ZERO, lTredsChrgFinancier = BigDecimal.ZERO;
            Date lLeg2Date = pFactoringUnitBean.getMaturityDate();
            ObligationBean[] lPurchaserObligations = new ObligationBean[2];
            if(pFactoringUnitBean.getEntityGstSummaryList() !=null){
                for (GstSummaryBean lBean : pFactoringUnitBean.getEntityGstSummaryList()) {
                        if(lBean.getEntity().equals(pFactoringUnitBean.getFinancier())){
                                lTredsChrgFinancier = lTredsChrgFinancier.add(lBean.getTotalCharge());
                        }else if(lBean.getEntity().equals(pFactoringUnitBean.getChargeBearerEntityCode())){
            if(CostBearingType.Buyer.equals(pFactoringUnitBean.getChargeBearer())){
                                    lTredsChrgPurchaser = lTredsChrgPurchaser.add(lBean.getTotalCharge());
            }else if(CostBearingType.Seller.equals(pFactoringUnitBean.getChargeBearer())){
                                    lTredsChrgSupplier = lTredsChrgSupplier.add(lBean.getTotalCharge());
                            }
                        }
                    }
            }
            lTredsChrgesAndTax = lTredsChrgPurchaser.add(lTredsChrgSupplier).add(lTredsChrgFinancier);
            if(CommonAppConstants.Yes.Yes.equals(pFactoringUnitBean.getEnableExtension())){
                    lLeg2Date = pFactoringUnitBean.getExtendedDueDate();
            }
            //set the factored amount
                BigDecimal lFactoredAmount = pFactoringUnitBean.getAmount();
            if(pFactoringUnitBean.getAcceptedHaircut()!=null && pFactoringUnitBean.getAcceptedHaircut().doubleValue() > 0){
                    lFactoredAmount = lFactoredAmount.multiply(BigDecimal.valueOf(100.0-pFactoringUnitBean.getAcceptedHaircut().doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP); 
            }
            //
        ObligationBean lFinancierObligationBean=null, lSupplierObligationBean=null, lPurchaserObligationBeanLeg2=null;
            ObligationBean lObligationBean = null;
            //LEG1
                List<ObligationDetailBean> lObliDetailList = TredsHelper.getInstance().getObligationDetailBean(pConnection, pFactoringUnitBean.getId(), null, null);
                ObligationSplitsBean lObliSplitBean = null;
                List<ObligationSplitsBean> lObliSplitList = new ArrayList<ObligationSplitsBean>();
                for(ObligationDetailBean lObliDetailBean :  lObliDetailList) {
                        if(Type.Leg_1.equals(lObliDetailBean.getObligationBean().getType())) {
                                if(pFactoringUnitBean.getFinancier().equals(lObliDetailBean.getObligationBean().getTxnEntity())) {
                                        lObligationBean = lObliDetailBean.getObligationBean();
                                        lObligationBean.setAmount(lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]).subtract(pLegInterests[INT_PUR_LEG1]).add(lTredsChrgFinancier));
                                        lObligationBean.setOriginalAmount(lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]).subtract(pLegInterests[INT_PUR_LEG1]).add(lTredsChrgFinancier));
                                        lFinancierObligationBean = lObligationBean;
                                }else if(pFactoringUnitBean.getPurchaser().equals(lObliDetailBean.getObligationBean().getTxnEntity())) {
                                        lObligationBean = lObliDetailBean.getObligationBean();
                                        lObligationBean.setAmount(pLegInterests[INT_PUR_LEG1].add(lTredsChrgPurchaser));
                                        lObligationBean.setOriginalAmount(pLegInterests[INT_PUR_LEG1].add(lTredsChrgPurchaser));
                            lPurchaserObligations[0] = lObligationBean;
                                }else if(pFactoringUnitBean.getSupplier().equals(lObliDetailBean.getObligationBean().getTxnEntity())) {
                                        lObligationBean = lObliDetailBean.getObligationBean();
                                        lObligationBean.setAmount(lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]).subtract(lTredsChrgSupplier));
                                        lObligationBean.setOriginalAmount(lFactoredAmount.subtract(pLegInterests[INT_SUP_LEG1]).subtract(lTredsChrgSupplier));
                                        lSupplierObligationBean = lObligationBean;
                                }else if(AppConstants.DOMAIN_PLATFORM.equals(lObliDetailBean.getObligationBean().getTxnEntity())) {
                                        lObligationBean = lObliDetailBean.getObligationBean();
                                        lObligationBean.setAmount(lTredsChrgesAndTax);
                                        lObligationBean.setOriginalAmount(lTredsChrgesAndTax);
                                }
                        }else if(Type.Leg_2.equals(lObliDetailBean.getObligationBean().getType())) {
                                 if(pFactoringUnitBean.getPurchaser().equals(lObliDetailBean.getObligationBean().getTxnEntity())) {
                                                lObligationBean = lObliDetailBean.getObligationBean();
                                                lObligationBean.setAmount(lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]));
                                                lObligationBean.setOriginalAmount(lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]));
                                                lPurchaserObligations[1] = lObligationBean;
                                                lPurchaserObligationBeanLeg2 = lObligationBean;
                                }else if(pFactoringUnitBean.getFinancier().equals(lObliDetailBean.getObligationBean().getTxnEntity())) {
                                        lObligationBean = lObliDetailBean.getObligationBean();
                                        lObligationBean.setAmount(lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]));
                                        lObligationBean.setOriginalAmount(lFactoredAmount.add(pLegInterests[INT_PUR_LEG2]));
                                }

                        }
                        lObliSplitBean = lObliDetailBean.getObligationSplitsBean();
                        lObliSplitBean.setAmount(lObligationBean.getAmount());
                        if (Type.Leg_1.equals(lObliDetailBean.getObligationBean().getType())) {
                        	lObligationBean.setDate(pSettelmentDate);
                        }
                        lObliSplitList.add(lObliSplitBean);
            lObligations.add(lObligationBean);
                }
        //
        //
        //
        for (ObligationBean lObliBean : lObligations) {
                if(lObliBean!=null){
                        lObliBean.setStatus(ObligationBean.Status.Ready);
                        if (lObliBean.getPfId()!=null) {
                        	lObliBean.setPfId(lObliBean.getPfId()*-1);
                        }
                obligationDAO.update(pConnection, lObliBean);
                }
        }
        GenericDAO<ObligationSplitsBean> obligationSplitDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        for (ObligationSplitsBean lOSBean : lObliSplitList) {
                if(lOSBean!=null){
                        lOSBean.setStatus(ObligationBean.Status.Ready);
                        if (lOSBean.getPfId()!=null) {
                        	lOSBean.setPfId(lOSBean.getPfId()*-1);
                        }
                        obligationSplitDAO.update(pConnection, lOSBean);
                }
        }
        //
        return  (new  ObligationBean[] {lFinancierObligationBean, lSupplierObligationBean, lPurchaserObligationBeanLeg2 });
    }
    public BigDecimal calculateFinacierChargeSharePercent(Connection pConnection,FactoringUnitBean pFactoringUnitBean,BidBean pBidBean) throws Exception{
    	AppEntityBean lFinEntityBean = TredsHelper.getInstance().getAppEntityBean(pBidBean.getFinancierEntity());
    	BigDecimal lFactoredAmount = pFactoringUnitBean.getAmount();
    	Map<String,Object> lRtnMap = new HashMap<String, Object>();
        if(pBidBean.getHaircut() != null && pBidBean.getHaircut().doubleValue()>0){
        	lFactoredAmount = pFactoringUnitBean.getAmount().multiply(BigDecimal.valueOf(100.0-pBidBean.getHaircut().doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
        }
		Object[] lResult = getSplitPlanDetails(pConnection, pFactoringUnitBean, pFactoringUnitBean.getPurchaser(), pBidBean.getFinancierEntity(), lFactoredAmount, lFinEntityBean,null);
		return ((BigDecimal)lResult[0]).add((BigDecimal)lResult[4]);
	}
	public List<Map<String, Object>> getBillWiseFactoringUnit(ExecutionContext pExecutionContext,Long pBillId, String pEntityCode) throws Exception {
		// 
		AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
		List<Map<String, Object>> lList = new ArrayList<Map<String, Object>>();
		Map<String, Object> lMap = null;
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM FACTORINGUNITS, INSTRUMENTS ");
		lSql.append(" WHERE INFUID = FUID ");
		lSql.append(" AND FURECORDVERSION > 0 AND INRECORDVERSION > 0 ");
        if(pBillId != null && lAppEntityBean.isFinancier()){
        	lSql.append(" AND FUFINANCIERBILLID = ").append(pBillId);
        }
        if(pBillId != null && (lAppEntityBean.isSupplier() || lAppEntityBean.isPurchaser())){
        	lSql.append(" AND FUCOSTBEARERBILLID = ").append(pBillId);
        } 
        List<FactoredBean> lFactoredBeanList = factoredBeanDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        if(lFactoredBeanList != null && lFactoredBeanList.size() > 0){
        	for(FactoredBean lFactoredBean : lFactoredBeanList){
        		lMap = new HashMap<String, Object>();
        		FactoringUnitBean lFactoringUnitBean = lFactoredBean.getFactoringUnitBean();
        		InstrumentBean lInstrumentBean = lFactoredBean.getInstrumentBean(); 
        		GstSummaryBean lTotalGstSummaryBean = lFactoringUnitBean.getTotalGstSummary(pEntityCode);
            	lMap = gstSummaryBeanDAO.getBeanMeta().formatAsMap(lTotalGstSummaryBean , GstSummaryBean.FIELDGROUP_GSTFIELDS, null, false);
            	lMap.put("totalValue", lTotalGstSummaryBean.getTotalCharge());
            	lMap.put("fuId", lFactoringUnitBean.getId());
            	lMap.put("invNumber", lInstrumentBean.getInstNumber());
            	lMap.put("fuAmount", lFactoringUnitBean.getAmount().doubleValue());
            	lList.add(lMap);
        	}
        }
		return lList;
	}
	private void lockFactoringUnit(Connection pConnection, FactoringUnitBean pFactoringUnitBean) throws Exception{

    	FactoringUnitBean lFuBean = factoringUnitDAO.findByPrimaryKey(pConnection, pFactoringUnitBean);
    	if(FactoringUnitBean.Status.Bid_Acceptance_In_Progress.equals(lFuBean.getStatus())) {
    		throw new CommonBusinessException("Factoring in progress. Please wait.");
    	}
    	if(!FactoringUnitBean.Status.Active.equals(pFactoringUnitBean.getStatus())) {
			throw new CommonBusinessException("Please check factoring unit status");
		}
    	//only if active
		StringBuilder lSql = new StringBuilder();
		lSql.append(" UPDATE FactoringUnits ");
		lSql.append(" SET FUStatus = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Bid_Acceptance_In_Progress.getCode()));
		lSql.append(" WHERE ");
		lSql.append(" FUID = ").append(pFactoringUnitBean.getId());
		lSql.append(" AND FUStatus = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Active.getCode()));
		lSql.append(" AND FURecordVersion = ").append(pFactoringUnitBean.getRecordVersion());
		
		try(PreparedStatement lStatement = pConnection.prepareStatement(lSql.toString())) {
			pConnection.setAutoCommit(false);
			int lCount = lStatement.executeUpdate();
			pConnection.commit();
			if(lCount==1) {
				return;
			}
    		throw new CommonBusinessException("Factoring in progress. Please wait.");				
		}catch(CommonBusinessException lCBEx) {
			throw lCBEx;
		}catch(Exception lEx) {
			logger.debug("Error in lockFactoringUnit : " + lEx.getMessage());
			try {
				pConnection.rollback();
			}catch(Exception lEx1){
				logger.debug("Error in lockFactoringUnit : rollback :" + lEx.getMessage());
			}
		}
	}
	
	private void unLockFactoringUnit(Connection pConnection, FactoringUnitBean pFactoringUnitBean) throws Exception{
		FactoringUnitBean lTmpFilter = new FactoringUnitBean();
		lTmpFilter.setId(pFactoringUnitBean.getId());
    	FactoringUnitBean lFuBean = factoringUnitDAO.findByPrimaryKey(pConnection, lTmpFilter);
    	if(FactoringUnitBean.Status.Bid_Acceptance_In_Progress.equals(lFuBean.getStatus())) {
        	//only if inprogress
    		StringBuilder lSql = new StringBuilder();
    		lSql.append(" UPDATE FactoringUnits ");
    		lSql.append(" SET FUStatus = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Active.getCode()));
    		lSql.append(" WHERE ");
    		lSql.append(" FUID = ").append(pFactoringUnitBean.getId());
    		lSql.append(" AND FUStatus = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Bid_Acceptance_In_Progress.getCode()));
    		logger.info("unLockFactoringUnit Query : " + lSql.toString());
    		try(PreparedStatement lStatement = pConnection.prepareStatement(lSql.toString())) {
    			pConnection.setAutoCommit(false);
    			lStatement.executeUpdate();
    			pConnection.commit();
    		}catch(Exception lEx) {
    			logger.debug("Error in unLockFactoringUnit : " + lEx.getMessage());
    		}
    	}
	}

	private void checkPlatformStatus(Connection pConnection, String pPurchaser, String pSupplier) throws CommonBusinessException {
		PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
		lPSLFilterBean.setPurchaser(pPurchaser);
		lPSLFilterBean.setSupplier(pSupplier);
		//lPSLFilterBean.setStatus(PurchaserSupplierLinkBean.Status.Active);
		PurchaserSupplierLinkBean lPurchaserSupplierLinkBean=null;
		try {
			lPurchaserSupplierLinkBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
		} catch (Exception e) {
			logger.error("Error in inst.checkPlatformStatus : "+e.getMessage());
		}
		if (lPurchaserSupplierLinkBean != null && PurchaserSupplierLinkBean.PlatformStatus.Suspended.equals(lPurchaserSupplierLinkBean.getPlatformStatus())) {
			throw new CommonBusinessException("Relationship is suspended by platform.");
		}
	}
    
public void acceptBidManually(String pFinancierEntity, Long pFuId , Long pBdId, String pSettlementDate) throws Exception
  {
    Connection lConnection = DBHelper.getInstance().getConnection();
    FactoringUnitBean lFuBean = new FactoringUnitBean();
    BidBean lBidBean = new BidBean();
    try {
      lFuBean.setId(pFuId);
      lFuBean = (FactoringUnitBean)this.factoringUnitDAO.findByPrimaryKey(lConnection, lFuBean);
      lBidBean.setId(pBdId);
      lBidBean.setFuId(pFuId);
      lBidBean.setFinancierEntity(pFinancierEntity);
      lBidBean = (BidBean)this.bidDAO.findByPrimaryKey(lConnection, lBidBean);
      acceptBid(lConnection, lFuBean, lBidBean, null, FormatHelper.getDate(pSettlementDate, "dd-MM-yyyy"), null);
    } catch (Exception lEx) {
      logger.error("Error in acceptBidManually ", lEx);
    }
  }
}
