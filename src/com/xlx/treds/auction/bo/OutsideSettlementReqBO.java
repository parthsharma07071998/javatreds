package com.xlx.treds.auction.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.swing.DropMode;

import com.xlx.common.user.bean.IUserBean;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationExtensionBean;
import com.xlx.treds.auction.bean.ObligationModificationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.OutsideSettlementDetBean;
import com.xlx.treds.auction.bean.OutsideSettlementReqBean;
import com.xlx.treds.auction.bean.OutsideSettlementReqBean.Status;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.auction.bean.OutsideSettlementSplitsBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class OutsideSettlementReqBO {
    
    private GenericDAO<OutsideSettlementReqBean> outsideSettlementReqDAO;
    private GenericDAO<OutsideSettlementSplitsBean> outsideSettlementSplitsDAO;
    private GenericDAO<OutsideSettlementDetBean> outsideSettlementDetDAO;
    private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
    private GenericDAO<ObligationBean> obligationDAO;
    private CompositeGenericDAO<ObligationDetailBean> obligationDetailDAO;
    
    private static Long PURCHASER_PENDING = Long.valueOf(0);
    private static Long PURCHASER_SENTFORAPPROVAL = Long.valueOf(1);
    private static Long PURCHASER_RETURNED  = Long.valueOf(2);
    private static Long PURCHASER_APPROVED  = Long.valueOf(3);
    private static Long PURCHASER_REJECTED = Long.valueOf(4);
    
    private static Long FINANCIER_PENDING = Long.valueOf(0);
    private static Long FINANCIER_RETURNED = Long.valueOf(1);
    private static Long FINANCIER_APPROVED = Long.valueOf(2);
    private static Long FINANCIER_REJECTED = Long.valueOf(3);

    public OutsideSettlementReqBO() {
        super();
        outsideSettlementReqDAO = new GenericDAO<OutsideSettlementReqBean>(OutsideSettlementReqBean.class);
        outsideSettlementSplitsDAO = new GenericDAO<OutsideSettlementSplitsBean>(OutsideSettlementSplitsBean.class);
        outsideSettlementDetDAO = new GenericDAO<OutsideSettlementDetBean>(OutsideSettlementDetBean.class);
        obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        obligationDetailDAO = new CompositeGenericDAO<ObligationDetailBean>(ObligationDetailBean.class);
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
    }
    
    public void setTabs(OutsideSettlementReqBean pBean, Boolean pIsPurchaser) {
		if (Boolean.TRUE.equals(pIsPurchaser)) {
			switch (pBean.getStatus()) {
			case Created:
				pBean.setTab(PURCHASER_PENDING);
				return;
			case Returned:
				pBean.setTab(PURCHASER_RETURNED);
				return;
			case Sent:
				pBean.setTab(PURCHASER_SENTFORAPPROVAL);
				return;
			case Approved:
				pBean.setTab(PURCHASER_APPROVED);
				return;
			case Rejected:
				pBean.setTab(PURCHASER_REJECTED);
				return;
			}
		}else {
			switch (pBean.getStatus()) {
			case Sent:
				pBean.setTab(FINANCIER_PENDING);
				return;
			case Returned:
				pBean.setTab(FINANCIER_RETURNED);
				return;
			case Approved:
				pBean.setTab(FINANCIER_APPROVED);
				return;
			case Rejected:
				pBean.setTab(FINANCIER_REJECTED);
				return;
			}
		}
		
	}
    
    public OutsideSettlementReqBean findBean(ExecutionContext pExecutionContext, 
        OutsideSettlementReqBean pFilterBean) throws Exception {
        OutsideSettlementReqBean lOutsideSettlementReqBean = outsideSettlementReqDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lOutsideSettlementReqBean == null) {
        	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }else {
        	OutsideSettlementDetBean lOutSettleDetBean = new OutsideSettlementDetBean();
        	lOutSettleDetBean.setOsrId(lOutsideSettlementReqBean.getId());
        	lOutsideSettlementReqBean.setOutSettleDetailList(outsideSettlementDetDAO.findList(pExecutionContext.getConnection(), lOutSettleDetBean));
        }
        return lOutsideSettlementReqBean;
    }
    
    public List<OutsideSettlementReqBean> findList(ExecutionContext pExecutionContext, OutsideSettlementReqBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean, boolean pTredsFin) throws Exception {
    	AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        List<OutsideSettlementReqBean> lList = outsideSettlementReqDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
        List<OutsideSettlementReqBean> lRtnList = new ArrayList<OutsideSettlementReqBean>();
        for (OutsideSettlementReqBean lBean : lList) {
        	boolean lPurchaser = false;
        	if (lAppEntityBean.isPurchaser() || (lAppEntityBean.isPlatform() && !pTredsFin)) {
        		lPurchaser = true;
        	}
        	setTabs(lBean, lPurchaser);
        	lRtnList.add(lBean);
        }
        return lRtnList;
    }
    
    public void save(ExecutionContext pExecutionContext, OutsideSettlementReqBean pOutsideSettlementReqBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        OutsideSettlementReqBean lOldOutsideSettlementReqBean = null;
        Map<Long, OutsideSettlementDetBean> lOldMap = new HashMap<Long, OutsideSettlementDetBean>();
        if (pNew) {
            outsideSettlementReqDAO.insert(lConnection, pOutsideSettlementReqBean);
        } else {
            lOldOutsideSettlementReqBean = findBean(pExecutionContext, pOutsideSettlementReqBean);
            pOutsideSettlementReqBean.setRecordUpdator(pUserBean.getId());
            if (outsideSettlementReqDAO.update(lConnection, pOutsideSettlementReqBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        if (lOldOutsideSettlementReqBean.getOutSettleDetailList() != null) {
            for (OutsideSettlementDetBean lDetailBean : lOldOutsideSettlementReqBean.getOutSettleDetailList() ) {
                lOldMap.put(lDetailBean.getId(), lDetailBean);
            }
       }
        if (pOutsideSettlementReqBean.getOutSettleDetailList() != null) {
	        for (OutsideSettlementDetBean lDetailBean : pOutsideSettlementReqBean.getOutSettleDetailList()) {
	        	if (lDetailBean.getId() == null) {
	        			lDetailBean.setOsrId(pOutsideSettlementReqBean.getId());
	        			lDetailBean.setRecordCreator(pUserBean.getId());
	        			outsideSettlementDetDAO.insert(lConnection, lDetailBean);
	                	outsideSettlementDetDAO.insertAudit(lConnection, lDetailBean, AuditAction.Insert,pUserBean.getId());
	            }else {
	            	OutsideSettlementDetBean lOldOutsideSettleDetBean = lOldMap.remove(lDetailBean.getId());
	                if (lOldOutsideSettleDetBean == null) {
	                	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
	                }
	                	outsideSettlementDetDAO.getBeanMeta().copyBean( lDetailBean, lOldOutsideSettleDetBean, null, null);
	                	lOldOutsideSettleDetBean.setRecordUpdator(pUserBean.getId());
	                	outsideSettlementDetDAO.update(lConnection, lOldOutsideSettleDetBean, BeanMeta.FIELDGROUP_UPDATE);
	                	outsideSettlementDetDAO.insertAudit(lConnection, lOldOutsideSettleDetBean, AuditAction.Update,pUserBean.getId());
	            }
	        }
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, OutsideSettlementReqBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        OutsideSettlementReqBean lOutsideSettlementReqBean = findBean(pExecutionContext, pFilterBean);
        lOutsideSettlementReqBean.setRecordUpdator(pUserBean.getId());
        outsideSettlementReqDAO.delete(lConnection, lOutsideSettlementReqBean);        


        pExecutionContext.commitAndDispose();
    }

	public void createRequest(ExecutionContext pExecutionContext, HttpServletRequest pRequest,OutsideSettlementReqBean pOutsideSettlementReqBean,IAppUserBean pUserBean) throws Exception {
		if (pOutsideSettlementReqBean.getSplitList()!=null) {
			pExecutionContext.setAutoCommit(false);
			Connection lConnection = pExecutionContext.getConnection();
			pOutsideSettlementReqBean.setStatus(Status.Created);
			pOutsideSettlementReqBean.setCreateDate(new Date(System.currentTimeMillis()));
			pOutsideSettlementReqBean.setCreaterAuId(pUserBean.getId());
			outsideSettlementReqDAO.insert(lConnection, pOutsideSettlementReqBean);
			OutsideSettlementSplitsBean lSplitList = new OutsideSettlementSplitsBean();
			lSplitList.setId(pOutsideSettlementReqBean.getId());
			lSplitList.setList(pOutsideSettlementReqBean.getSplitList());
			outsideSettlementSplitsDAO.insert(lConnection, lSplitList);
			StringBuilder lSql = new StringBuilder();
			lSql.append(" UPDATE OBLIGATIONSPLITS SET OBSOSRID = ").append(pOutsideSettlementReqBean.getId());
			lSql.append(" WHERE  (OBSOBID || '^' || OBSPARTNUMBER)  IN ( ").append(pOutsideSettlementReqBean.getSplitList()).append(" ) ");
			Statement lStatement = null;
			try {
				lStatement = lConnection.createStatement();
			    int lCount = lStatement.executeUpdate(lSql.toString());
			}catch(Exception lEx){  
				//error here
			}finally {
				if (lStatement != null)
					lStatement.close();
			}
			pExecutionContext.commitAndDispose();
		}
	}

	public String findBeanJson(ExecutionContext pExecutionContext, OutsideSettlementReqBean pFilterBean) throws Exception {
		OutsideSettlementReqBean lBean = findBean(pExecutionContext, pFilterBean);
		HashMap<String, Object> lMap = (HashMap<String, Object>) outsideSettlementReqDAO.getBeanMeta().formatAsMap(lBean);
		DBHelper lDbHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT OBLIGATIONSPLITS.* ");
		lSql.append(" ,OBTXNENTITY \"OBSTransactionEntity\" ");
		lSql.append(" ,OBTYPE \"OBSLegType\" ");
		lSql.append(" ,OBFuId \"OBSFactUntId\" ");
		lSql.append(" ,FUFINANCIER \"OBSFinancierEntity\" ");
		lSql.append(" FROM OBLIGATIONSPLITS ");
		lSql.append(" JOIN OBLIGATIONS ON (OBSOBID=OBID) ");
		lSql.append(" JOIN FACTORINGUNITS ON (OBFUID=FUID) ");
		lSql.append(" WHERE OBSRECORDVERSION > 0 ");
		lSql.append(" AND OBSRECORDVERSION > 0 ");
		lSql.append(" AND OBTXNENTITY = ").append(lDbHelper.formatString(lBean.getBuyerCode()));
		lSql.append(" AND OBSOSRID = ").append(lBean.getId());
		List<ObligationSplitsBean> lSplits = obligationSplitsDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(),-1);
		HashMap<Long,Object> lFuAmtMap = new HashMap<>();
		for (ObligationSplitsBean lSplitBean : lSplits) {
			BigDecimal lTmpAmt = null;
			if (!lFuAmtMap.containsKey(lSplitBean.getFactUntId())){
				lFuAmtMap.put(lSplitBean.getFactUntId(),BigDecimal.ZERO);
			}
			lTmpAmt = (BigDecimal) lFuAmtMap.get(lSplitBean.getFactUntId());
			lTmpAmt = lTmpAmt.add(lSplitBean.getAmount());
			lFuAmtMap.put(lSplitBean.getFactUntId(),lTmpAmt);
		}
		lMap.put("fuDetails",lFuAmtMap);
		return new JsonBuilder(lMap).toString();  
	}

	public void updateStatus(ExecutionContext pExecutionContext, OutsideSettlementReqBean pOutsideSettlementReqBean,
			IAppUserBean pUserBean) throws Exception {
		pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        OutsideSettlementReqBean lOutsideSettlementReqBean = findBean(pExecutionContext, pOutsideSettlementReqBean);
        if (lOutsideSettlementReqBean == null) {
        	throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pUserBean.getDomain());
        DBHelper lDbHelper = DBHelper.getInstance();
 		StringBuilder lSql = new StringBuilder();
 		lSql.append(" SELECT OBLIGATIONSPLITS.* ");
 		lSql.append(" ,OBTXNENTITY \"OBSTransactionEntity\" ");
 		lSql.append(" ,OBTYPE \"OBSLegType\" ");
 		lSql.append(" ,OBFuId \"OBSFactUntId\" ");
 		lSql.append(" ,FUFINANCIER \"OBSFinancierEntity\" ");
 		lSql.append(" ,OBLIGATIONS.* ");
 		lSql.append(" FROM OBLIGATIONSPLITS ");
 		lSql.append(" JOIN OBLIGATIONS ON (OBSOBID=OBID) ");
 		lSql.append(" JOIN FACTORINGUNITS ON (OBFUID=FUID) ");
 		lSql.append(" WHERE OBSRECORDVERSION > 0 ");
 		lSql.append(" AND OBRECORDVERSION > 0 ");
 		lSql.append(" AND OBTXNENTITY = ").append(lDbHelper.formatString(lOutsideSettlementReqBean.getBuyerCode()));
 		lSql.append(" AND OBSOSRID = ").append(lOutsideSettlementReqBean.getId());
 		List<ObligationDetailBean> lOBDetailList = obligationDetailDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(),-1);
 		ObligationSplitsBean lPurSplitBean = null; 
 		BigDecimal lTmpAmt = BigDecimal.ZERO;
 		for (ObligationDetailBean lBean : lOBDetailList) {
 			lPurSplitBean = lBean.getObligationSplitsBean();
 			lTmpAmt = lTmpAmt.add(lPurSplitBean.getAmount());
 		}
 		BigDecimal lDetTmpAmt = BigDecimal.ZERO;
 		for (OutsideSettlementDetBean lDetBean : lOutsideSettlementReqBean.getOutSettleDetailList()) {
 			lDetTmpAmt = lDetTmpAmt.add(lDetBean.getAmount());
 		}
 		if (lTmpAmt.compareTo(lDetTmpAmt) != 0) {
 			throw new CommonBusinessException("Amount Mismatch.");
 		}
        if (lAppEntityBean.isFinancier()) {
        	if(!(Status.Sent.equals(lOutsideSettlementReqBean.getStatus()) 
        			&& (Status.Approved.equals(pOutsideSettlementReqBean.getStatus()) 
        					|| Status.Rejected.equals(pOutsideSettlementReqBean.getStatus()) 
        					|| Status.Returned.equals(pOutsideSettlementReqBean.getStatus()) ))
        			){
        		throw new CommonBusinessException("Invalid Status.");
        	}
        }else if (lAppEntityBean.isPurchaser() || lAppEntityBean.isPlatform()) {
        	if(!( (Status.Created.equals(lOutsideSettlementReqBean.getStatus()) 
        			|| Status.Returned.equals(lOutsideSettlementReqBean.getStatus()))
        					&& Status.Sent.equals(pOutsideSettlementReqBean.getStatus()))){
        		throw new CommonBusinessException("Invalid Status.");
        	}
        }
        lOutsideSettlementReqBean.setStatus(pOutsideSettlementReqBean.getStatus());
        outsideSettlementReqDAO.update(lConnection, lOutsideSettlementReqBean);
        if (Status.Approved.equals(pOutsideSettlementReqBean.getStatus())) {
        	lSql = new StringBuilder();
        	lSql.append(" SELECT finsplit.*,finoblig.* ");
     		lSql.append(" FROM OBLIGATIONSPLITS split");
     		lSql.append(" JOIN OBLIGATIONS oblig ON (OBSOBID=OBID) ");
     		lSql.append(" JOIN FACTORINGUNITS ON (OBFUID=FUID) ");
     		lSql.append(" left outer JOIN OBLIGATIONS finoblig ON ( FUID=finoblig.obfuid and oblig.OBDATE=finoblig.obdate and oblig.OBTYPE=finoblig.OBTYPE ) ");
     		lSql.append(" left outer JOIN OBLIGATIONSPLITS finsplit on (finsplit.OBSOBID=finoblig.OBID) ");
     		lSql.append(" WHERE oblig.OBRECORDVERSION > 0 ");
     		lSql.append(" AND split.OBSRECORDVERSION > 0 ");
     		lSql.append(" AND oblig.OBTXNENTITY = ").append(lDbHelper.formatString(lOutsideSettlementReqBean.getBuyerCode()));
     		lSql.append(" AND split.OBSOSRID = ").append(lOutsideSettlementReqBean.getId());
     		lSql.append(" AND finoblig.OBRECORDVERSION > 0 ");
     		lSql.append(" AND finsplit.OBSRECORDVERSION > 0 ");
     		lSql.append(" AND finoblig.OBTXNENTITY != ").append(lDbHelper.formatString(lOutsideSettlementReqBean.getBuyerCode()));
     		List<ObligationDetailBean> lOBFinDetailList = obligationDetailDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(),-1);
     		ObligationSplitsBean lSplitBean = null; 
     		ObligationBean lObligBean = null;
     		List<Long> lObIds = new ArrayList<Long>();
     		List<Long> lFuIds = new ArrayList<Long>();
     		for (ObligationDetailBean lBean : lOBDetailList) {
     			lSplitBean = lBean.getObligationSplitsBean();
     			lObligBean = lBean.getObligationBean();
     			if (!lFuIds.contains(lObligBean.getId())) {
     				lFuIds.add(lObligBean.getFuId());
     			}
     			if (!lObIds.contains(lObligBean.getId())) {
     				lObligBean.setSettledAmount(lObligBean.getAmount());
     				lObligBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
     				lObligBean.setStatus(ObligationBean.Status.Success);
     				lObIds.add(lObligBean.getId());
     				obligationDAO.update(lConnection, lObligBean, ObligationBean.FIELDGROUP_UPDATESETTLEDAMOUNT);
     			}
     			lSplitBean.setSettledAmount(lSplitBean.getAmount());
     			lSplitBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
     			lSplitBean.setStatus(ObligationBean.Status.Success);
     			obligationSplitsDAO.update(lConnection, lSplitBean,"return");
     		}
     		for (ObligationDetailBean lBean : lOBFinDetailList) {
     			lSplitBean = lBean.getObligationSplitsBean();
     			lObligBean = lBean.getObligationBean();
     			if (!lFuIds.contains(lObligBean.getId())) {
     				lFuIds.add(lObligBean.getFuId());
     			}
     			if (!lObIds.contains(lObligBean.getId())) {
     				lObligBean.setSettledAmount(lObligBean.getAmount());
     				lObligBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
     				lObligBean.setStatus(ObligationBean.Status.Success);
     				lObIds.add(lObligBean.getId());
     				obligationDAO.update(lConnection, lObligBean, ObligationBean.FIELDGROUP_UPDATESETTLEDAMOUNT);
     			}
     			lSplitBean.setSettledAmount(lSplitBean.getAmount());
     			lSplitBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
     			lSplitBean.setStatus(ObligationBean.Status.Success);
     			obligationSplitsDAO.update(lConnection, lSplitBean,"return");
     		}
     		
     		lSql = new StringBuilder();
			lSql.append(" UPDATE FACTORINGUNITS SET FUSTATUS ").append(lDbHelper.formatString(FactoringUnitBean.Status.Leg_2_Settled.getCode()));
			String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lFuIds);
			lSql.append(" WHERE ").append(TredsHelper.getInstance().getInQuery("FUID",lListStr));
			lSql.append(" AND FURECORDVERSION > 0 ");
			lSql = new StringBuilder();
			lSql.append(" UPDATE INSTRUMENTS SET INSTATUS ").append(lDbHelper.formatString(InstrumentBean.Status.Leg_2_Settled.getCode()));
			lSql.append(" WHERE ").append(TredsHelper.getInstance().getInQuery("INFUID",lListStr));
			lSql.append(" AND INRECORDVERSION > 0 ");
        }else if (Status.Rejected.equals(pOutsideSettlementReqBean.getStatus())) {
        	lSql = new StringBuilder();
			lSql.append(" UPDATE OBLIGATIONSPLITS SET OBSOSRID = null ");
			lSql.append(" WHERE ");
			lSql.append(" OBSOSRID = ").append(lOutsideSettlementReqBean.getId());
			Statement lStatement = null;
			try {
				lStatement = lConnection.createStatement();
			    int lCount = lStatement.executeUpdate(lSql.toString());
			}catch(Exception lEx){  
				//error here
			}finally {
				if (lStatement != null)
					lStatement.close();
			}
        }
        pExecutionContext.commitAndDispose();
	}
    
}
