package com.xlx.treds.entity.bo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.BidBean.Status;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.master.bean.AuctionChargePlanBean;
import com.xlx.treds.master.bean.AuctionChargePlanBean.Type;
import com.xlx.treds.master.bean.AuctionChargeSlabBean;
import com.xlx.treds.master.bo.AuctionChargePlansBO;

import groovy.json.JsonBuilder;

public class MemberwisePlanBO {
    
    private GenericDAO<MemberwisePlanBean> memberwisePlanDAO;
    private AuctionChargePlansBO auctionChargePlansBO;
    private CompositeGenericDAO<FactoredBean> factoredDAO = null;
  	private GenericDAO<BidBean> bidDAO = null;

    public MemberwisePlanBO() {
        super();
        memberwisePlanDAO = new GenericDAO<MemberwisePlanBean>(MemberwisePlanBean.class);
        auctionChargePlansBO = new AuctionChargePlansBO();
        factoredDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
    }
    
    public MemberwisePlanBean findBean(ExecutionContext pExecutionContext, 
        MemberwisePlanBean pFilterBean) throws Exception {
        MemberwisePlanBean lMemberwisePlanBean = memberwisePlanDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lMemberwisePlanBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        return lMemberwisePlanBean;
    }
    
    public Map<String, Object> getMapById(ExecutionContext pExecutionContext, MemberwisePlanBean pFilterBean) throws Exception {
		MemberwisePlanBean lMemberwisePlanBean = findBean(pExecutionContext, pFilterBean);
		Map<String, Object> lMap = memberwisePlanDAO.getBeanMeta().formatAsMap(lMemberwisePlanBean);
		if (CommonAppConstants.Yes.Yes.equals(lMemberwisePlanBean.getFinancierBearShare())) {
			getFinacierSplitCalculations(pExecutionContext,lMap,lMemberwisePlanBean.getAcpId(),lMemberwisePlanBean.getFinancierShare());
		}
		return lMap;
    }
    
    public void getFinacierSplitCalculations(ExecutionContext pExecutionContext,Map<String, Object> pMap, Long pAcpId,BigDecimal pFinancierShare) throws Exception {
    	AuctionChargePlanBean lAcpBean = new AuctionChargePlanBean();
		lAcpBean.setId(pAcpId);
		lAcpBean = auctionChargePlansBO.findBean(pExecutionContext, lAcpBean);
		List<Map<String, Object>> lSlabList = new ArrayList<Map<String, Object>>();
		pMap.put("slabs",lSlabList );
		Map<String, Object> lSlabMap = null;
		for (AuctionChargeSlabBean lAcsBean : lAcpBean.getAuctionChargeSlabList()) {
			lSlabMap = new HashMap<>();
			lSlabList.add(lSlabMap);
			lSlabMap.put("minAmount", lAcsBean.getMinAmount());
			lSlabMap.put("maxAmount", lAcsBean.getMaxAmount());
			lSlabMap.put("chargePercent", lAcsBean.getChargePercentValue());
			lSlabMap.put("minCharge", lAcsBean.getChargeAbsoluteValue());
			lSlabMap.put("finChargePercent", pFinancierShare);
			lSlabMap.put("otherChargePercent", (lAcsBean.getChargePercentValue().subtract(pFinancierShare)).max(BigDecimal.ZERO));
		}
	}

	public List<MemberwisePlanBean> findList(ExecutionContext pExecutionContext, MemberwisePlanBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	StringBuilder lSql = new StringBuilder();
        String lDBColumnNames =  null;
        
        lSql.append(" SELECT ");
        
        lDBColumnNames =  memberwisePlanDAO.getDBColumnNameCsv(null);
        lSql.append(lDBColumnNames += ",CDCOMPANYNAME \"MPCdName\" ,ACPName \"MPAcpName\" ");
        lSql.append(" , aeTYPE \"MPType\" ");
        lSql.append(" FROM MEMBERWISEPLANS ");
        lSql.append(" LEFT OUTER JOIN COMPANYDETAILS ON  cdcode= mpcode ");
        lSql.append(" LEFT OUTER JOIN AppEntities ON  aecode= mpcode ");
        lSql.append(" LEFT OUTER JOIN AUCTIONCHARGEPLANS ON acpid= mpacpid ");
        lSql.append(" where 1=1 ");
        if(StringUtils.isNotEmpty(pFilterBean.getType())){
        	lSql.append(" AND AETYPE = '" + pFilterBean.getType() + "' ");
        }else {
        	lSql.append(" AND AETYPE = '" + AppEntityBean.EntityType.Purchaser.getCode() + "' ");
        }
        memberwisePlanDAO.appendAsSqlFilter(lSql, pFilterBean, false);

        return memberwisePlanDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
    }
    
    public void save(ExecutionContext pExecutionContext, MemberwisePlanBean pMemberwisePlanBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemberwisePlanBean lOldMemberwisePlanBean = null;
        Date lDate = pMemberwisePlanBean.getEffectiveStartDate();
        Date lCurrentDate = TredsHelper.getInstance().getBusinessDate();
        if (lDate.before(lCurrentDate)) {
    		throw new CommonBusinessException("Date should be after the BusinessDate.");
    	}
        //validataion
        MemberwisePlanBean lPreviousEffectivePlan = TredsHelper.getInstance().getPlan(lConnection, pMemberwisePlanBean.getCode(), lDate, true);
        MemberwisePlanBean lNextEffectivePlan = TredsHelper.getInstance().getNextPlan(lConnection, pMemberwisePlanBean.getCode(), lDate);
        if(lNextEffectivePlan!=null && pNew){
        	pMemberwisePlanBean.setEffectiveEndDate(OtherResourceCache.getInstance().getPreviousDate(lNextEffectivePlan.getEffectiveStartDate(),1));
        }
        if(lPreviousEffectivePlan!=null && pNew){
        	lPreviousEffectivePlan.setEffectiveEndDate(OtherResourceCache.getInstance().getPreviousDate(pMemberwisePlanBean.getEffectiveStartDate(),1));
        	lPreviousEffectivePlan.setRecordUpdator(pUserBean.getId());
            if (memberwisePlanDAO.update(lConnection, lPreviousEffectivePlan, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            memberwisePlanDAO.insertAudit(lConnection, lPreviousEffectivePlan, AuditAction.Update, pUserBean.getId());
        }
        
        //
        if (CommonAppConstants.Yes.Yes.equals(pMemberwisePlanBean.getFinancierBearShare())) {
        	AuctionChargePlanBean lACPBean = new AuctionChargePlanBean();
        	lACPBean.setId(pMemberwisePlanBean.getAcpId());
        	lACPBean = auctionChargePlansBO.getPlanDetailsBean(lConnection, lACPBean);
        	if ( !Type.Invoice.equals(lACPBean.getType()) ){
        		throw new CommonBusinessException("Only plan with charge type Invoice allowed for charge splitting.");
        	}
        	for  (AuctionChargeSlabBean lACSlabBean : lACPBean.getAuctionChargeSlabList()) {
        		if (!AuctionChargeSlabBean.ChargeType.Threshold.equals(lACSlabBean.getChargeType())){
            		throw new CommonBusinessException("Slab with Min Amount : "+ lACSlabBean.getMinAmount() + " and Max Amount : "+ lACSlabBean.getMaxAmount() + " should always be threshold type.");
            	}
        	}
        }
        if (pNew) {
            lOldMemberwisePlanBean = memberwisePlanDAO.findByPrimaryKey(lConnection, pMemberwisePlanBean);
            if(lOldMemberwisePlanBean!=null){
            	throw new CommonBusinessException("Plan with same Effective date already exist.");
            }
            pMemberwisePlanBean.setRecordCreator(pUserBean.getId());
            memberwisePlanDAO.insert(lConnection, pMemberwisePlanBean);
            memberwisePlanDAO.insertAudit(lConnection, pMemberwisePlanBean, AuditAction.Insert, pUserBean.getId());
        } else {
            lOldMemberwisePlanBean = findBean(pExecutionContext, pMemberwisePlanBean);
            pMemberwisePlanBean.setRecordCreator(lOldMemberwisePlanBean.getRecordCreator());
            pMemberwisePlanBean.setRecordCreateTime(lOldMemberwisePlanBean.getRecordCreateTime());
            memberwisePlanDAO.getBeanMeta().copyBean( pMemberwisePlanBean, lOldMemberwisePlanBean);
            lOldMemberwisePlanBean.setRecordUpdator(pUserBean.getId());
            if (memberwisePlanDAO.update(lConnection, lOldMemberwisePlanBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            memberwisePlanDAO.insertAudit(lConnection, lOldMemberwisePlanBean, AuditAction.Update, pUserBean.getId());
        }
        MemberwisePlanBean lMemberwisePlanBean = (pNew==true?pMemberwisePlanBean:lOldMemberwisePlanBean);
        if (Yes.Yes.equals(lMemberwisePlanBean.getFinancierBearShare())) {
	         DBHelper lDBHelper = DBHelper.getInstance();
	       	 StringBuilder lSql = new StringBuilder();
	       	 lSql.append(" SELECT * FROM BIDS ");
	       	 lSql.append(" JOIN FACTORINGUNITS ON (BDFUID=FUID) ");
	       	 lSql.append(" WHERE BDSTATUS = ").append(lDBHelper.formatString(Status.Active.getCode()));
	       	 lSql.append(" AND BDRATE IS NOT NULL ");
	       	 lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
	       	 lSql.append(" AND BDCHARGES IS NULL ");
	       	 lSql.append(" AND FUPURCHASER = ").append(lDBHelper.formatString(lMemberwisePlanBean.getCode()));
	       	 List<FactoredBean> lList = factoredDAO.findListFromSql(lConnection, lSql.toString(), -1);
	         FactoringUnitBO lFuBO = new FactoringUnitBO();
	       	 for (FactoredBean lBean : lList){
	    		 FactoringUnitBean lFactoringUnitBean = lBean.getFactoringUnitBean();
	    		 BidBean lBidBean = lBean.getBidBean();
	    		 BigDecimal lFactoredAmount = lFactoringUnitBean.getAmount();
	             if(lBidBean.getHaircut() != null && lBidBean.getHaircut().doubleValue()>0){
	             	lFactoredAmount = lFactoringUnitBean.getAmount().multiply(BigDecimal.valueOf(100.0-lBidBean.getHaircut().doubleValue())).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
	             }
	             AppEntityBean lFinEntityBean = TredsHelper.getInstance().getAppEntityBean(lBidBean.getFinancierEntity());
	             Object[] lCharges = lFuBO.getSplitPlanDetails(lConnection, lFactoringUnitBean, lFactoringUnitBean.getPurchaser(), lBidBean.getFinancierEntity(), lFactoredAmount, lFinEntityBean,null);
                //0=Financier Normal Percent
            	//1=Financier Normal Amount
            	//2=ChargeBearer Split Charge percent
            	//3=Split Min value
            	//4=Financier Split Charge percent
                lBidBean.setNormalPercent((BigDecimal)lCharges[0]);
                lBidBean.setNormalMinAmt((BigDecimal)lCharges[1]);
                lBidBean.setSplitChargeBearerPercent((BigDecimal)lCharges[2]);
                lBidBean.setSplitMinCharge((BigDecimal)lCharges[3]);
                lBidBean.setSplitPercent((BigDecimal)lCharges[4]);
                lBidBean.setNormalMaxAmt((BigDecimal)lCharges[5]);
                lBidBean.setNormalChargeType((AuctionChargeSlabBean.ChargeType)lCharges[6]);
                Map<String,Object> lBidChargesMap = new HashMap<String, Object>();
                lBidChargesMap.put("normalPercent", lBidBean.getNormalPercent());
                lBidChargesMap.put("normalMinAmt", lBidBean.getNormalMinAmt());
                lBidChargesMap.put("normalMaxAmt", lBidBean.getNormalMaxAmt());
                lBidChargesMap.put("normalChargeType", lBidBean.getNormalChargeType());
                lBidChargesMap.put("splitChargeBearerPercent", lBidBean.getSplitChargeBearerPercent());
                lBidChargesMap.put("splitMinCharge", lBidBean.getSplitMinCharge());
                lBidChargesMap.put("splitPercent", lBidBean.getSplitPercent());
                lBidBean.setCharges(new JsonBuilder(lBidChargesMap).toString());
	    	    bidDAO.update(lConnection, lBidBean, BeanMeta.FIELDGROUP_UPDATE);
	    	 }
        }
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, MemberwisePlanBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        MemberwisePlanBean lMemberwisePlanBean = findBean(pExecutionContext, pFilterBean);
        lMemberwisePlanBean.setRecordUpdator(pUserBean.getId());
        memberwisePlanDAO.delete(lConnection, lMemberwisePlanBean);        
        memberwisePlanDAO.insertAudit(lConnection, lMemberwisePlanBean, AuditAction.Delete, pUserBean.getId());

        pExecutionContext.commitAndDispose();
    }
    
}
