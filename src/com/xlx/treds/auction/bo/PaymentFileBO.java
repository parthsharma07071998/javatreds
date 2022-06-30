package com.xlx.treds.auction.bo;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.IPaymentInterface;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationModificationRequestBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PaymentFileBean;
import com.xlx.treds.auction.bean.PaymentInterfaceFactory;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationModificationDetailBean;
import com.xlx.treds.user.bean.AppUserBean;

public class PaymentFileBO {
    
    private GenericDAO<PaymentFileBean> paymentFileDAO;
    private GenericDAO<ObligationBean> obligationBeanDAO;
    private GenericDAO<ObligationSplitsBean> obligationSplitsBeanDAO;
    private GenericDAO<ObligationModificationRequestBean> obligationModificationRequestBeanDAO;
    private BeanMeta obligationBeanMeta;
    private BeanMeta obligationModReqBeanMeta;
    private BeanMeta obligationModDetBeanMeta;

    public PaymentFileBO() {
        super();
        paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
        obligationBeanDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        obligationSplitsBeanDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        obligationModificationRequestBeanDAO = new GenericDAO<ObligationModificationRequestBean>(ObligationModificationRequestBean.class);
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
        obligationModReqBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationModificationRequestBean.class);
        obligationModDetBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationModificationDetailBean.class);
    }
    
    public PaymentFileBean findBean(ExecutionContext pExecutionContext, 
        PaymentFileBean pFilterBean) throws Exception {
        PaymentFileBean lPaymentFileBean = paymentFileDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lPaymentFileBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        return lPaymentFileBean;
    }
    
    public List<PaymentFileBean> findList(ExecutionContext pExecutionContext, PaymentFileBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        return paymentFileDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, PaymentFileBean pPaymentFileBean, AppUserBean pUserBean, 
        boolean pNew) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);

        pExecutionContext.setAutoCommit(false);
        Date lCurrentDate = OtherResourceCache.getInstance().getCurrentDate();
        Date lNextClearingDate = OtherResourceCache.getInstance().getNextClearingDate(lCurrentDate, 1);
       /* if (pPaymentFileBean.getFileType() == TxnType.Debit) {
            // debit file can be generated for today's obligation as well as next clearing days obligations
            if (!pPaymentFileBean.getDate().equals(lCurrentDate) && !pPaymentFileBean.getDate().equals(lNextClearingDate))
                throw new CommonBusinessException("Cannot generate payment file for the given date");
        }*/
        // no check for credit file since it can be generated only after debit return file is uploaded

        Connection lConnection = pExecutionContext.getConnection();
        IPaymentInterface lPaymentImpl = PaymentInterfaceFactory.getPaymentInterface(pPaymentFileBean.getFacilitator());
        lPaymentImpl.generateFile(lConnection, pPaymentFileBean, pUserBean);
        pExecutionContext.commitAndDispose();
    }
    public PaymentFileBean getFileContents(ExecutionContext pExecutionContext, PaymentFileBean pPaymentFileBean, AppUserBean pUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        PaymentFileBean lPaymentFileBean =  paymentFileDAO.findByPrimaryKey(pExecutionContext.getConnection(), pPaymentFileBean);
        IPaymentInterface lPaymentImpl = PaymentInterfaceFactory.getPaymentInterface(lPaymentFileBean.getFacilitator()); 
        return lPaymentImpl.getFileContents(pExecutionContext.getConnection(), lPaymentFileBean, pUserBean);
    }

	public Map<String, Object> getPaymentFileMessage1(Connection pConnection, Long pId) throws Exception{
		Map<String, Object> lReturnMap = new HashMap<String, Object>();
		Map<String,Object> lPaymentStatusMap = new HashMap<String,Object>();
		Map<String,Object> lObligationStatusMap = new HashMap<String,Object>();
		Map<String,Object> lObligationModiReqMap = new HashMap<String,Object>();
		Map<String,Object> lObligationModiDetMap = new HashMap<String,Object>();
		Map<String,Object> lObligationFinalMap = new HashMap<String,Object>();
		ResultSet lResultSet = null;
		PaymentFileBean lPaymentFileFilterBean = new PaymentFileBean();
		lPaymentFileFilterBean.setId(pId);
		PaymentFileBean lPaymentFileBean = paymentFileDAO.findBean(pConnection, lPaymentFileFilterBean);
		lPaymentStatusMap.put("paymentFileStatus", lPaymentFileBean.getStatus().toString());
		StringBuilder lSql = null;
		ObligationBean.Status lStatus = null;
		ObligationModificationRequestBean.Status lObligModiStatus = null;
		ObligationModificationDetailBean lObligModiDetStatus = null;
		
		try(Statement lStatement = pConnection.createStatement();){
			lSql = new StringBuilder();
			lSql.append(" SELECT count(*) AS Count, OBSSTATUS AS Status  FROM OBLIGATIONSPLITS  WHERE ");
			lSql.append(" OBSPFID = " + lPaymentFileBean.getId());
			lSql.append(" group by OBSSTATUS ");
			lResultSet = lStatement.executeQuery(lSql.toString());
			while(lResultSet.next()){
				String lObligStatus  = lResultSet.getString("Status");
				String lObligStatusCount = lResultSet.getString("Count");			
				BeanFieldMeta lField = (BeanFieldMeta) obligationBeanMeta.getFieldMap().get("status");
				Map<Object, IKeyValEnumInterface> lMap1 = lField.getDataSetKeyValueMap();
		        if (lMap1.containsKey(lObligStatus)){
		        	lStatus = (Status) lMap1.get(lObligStatus);
		        	lObligationStatusMap.put(lStatus.toString(), lObligStatusCount);
		        }
			}
			lSql = new StringBuilder();
			lSql.append(" SELECT COUNT(*) Count ,OMRSTATUS ModiStatus FROM OBLIGATIONSMODIREQUESTS ");
			lSql.append(" WHERE OMRFUID IN ");
			lSql.append(" ( ");
			lSql.append(" SELECT OBFUID FROM OBLIGATIONS WHERE OBPFID = " + lPaymentFileBean.getId());
			lSql.append(" ) ");
			lSql.append(" GROUP BY OMRSTATUS ");		
			lResultSet = lStatement.executeQuery(lSql.toString());
			while(lResultSet.next()){
				String obligModiReqStatus = lResultSet.getString("ModiStatus");
				String obligModiReqStatusCount = lResultSet.getString("Count");
				BeanFieldMeta lField = (BeanFieldMeta) obligationModReqBeanMeta.getFieldMap().get("status");
				Map<Object, IKeyValEnumInterface> lMap1 = lField.getDataSetKeyValueMap();
		        if (lMap1.containsKey(obligModiReqStatus)){
		        	lObligModiStatus = (com.xlx.treds.auction.bean.ObligationModificationRequestBean.Status) lMap1.get(obligModiReqStatus);
		        	lObligationModiReqMap.put(lObligModiStatus.toString(), obligModiReqStatusCount);
		        }
			}
			lSql = new StringBuilder();
			lSql.append(" SELECT COUNT(*) Count,OMDREVISEDSTATUS RevStatus FROM OBLIGATIONSMODIDETAILS ");
			lSql.append(" WHERE OMDOMRID IN ");
			lSql.append(" ( ");
			lSql.append(" SELECT OMRID FROM OBLIGATIONSMODIREQUESTS ");
			lSql.append(" WHERE OMRFUID IN ");
			lSql.append(" ( ");
			lSql.append(" SELECT OBFUID FROM OBLIGATIONS ");
			lSql.append(" WHERE OBPFID = " + lPaymentFileBean.getId());
			lSql.append(" ) ");
			lSql.append(" ) ");
			lSql.append(" GROUP BY OMDREVISEDSTATUS ");
			lResultSet = lStatement.executeQuery(lSql.toString());
			while(lResultSet.next()){
				String lObligModiDetailStatus = lResultSet.getString("RevStatus");
				String lObligModiDetailStatusCount = lResultSet.getString("Count");
				BeanFieldMeta lField = (BeanFieldMeta) obligationModDetBeanMeta.getFieldMap().get("status");
				Map<Object, IKeyValEnumInterface> lMap1 = lField.getDataSetKeyValueMap();
		        if (lMap1.containsKey(lObligModiDetailStatus)){
		        	lStatus = (Status) lMap1.get(lObligModiDetailStatus);
		        	lObligationModiDetMap.put(lStatus.toString(), lObligModiDetailStatusCount);
		        }
			}
			lSql = new StringBuilder();
			lSql.append(" SELECT OBSSTATUS ObligationStatus, OMRSTATUS ModificationRequest, OMDREVISEDSTATUS NewStatus ");
			lSql.append(" FROM OBLIGATIONSPLITS ");
			lSql.append(" LEFT OUTER JOIN OBLIGATIONSMODIDETAILS ");
			lSql.append(" ON ");
			lSql.append(" ( OBSOBID=OMDOBID and OBSPARTNUMBER=OMDPARTNUMBER ) ");
			lSql.append(" LEFT OUTER JOIN OBLIGATIONSMODIREQUESTS ON OMRID = OMDOMRID ");
			lSql.append(" WHERE OBSSTATUS != 'SUC' ");
			lSql.append(" AND ");
			lSql.append(" ( OMRSTATUS is null or OMRSTATUS !='APL') ");
			lSql.append(" AND ");
			lSql.append("  OBSPFID = " + lPaymentFileBean.getId());
			lSql.append(" GROUP BY OBSSTATUS,OMRSTATUS,OMDREVISEDSTATUS ");
			lResultSet = lStatement.executeQuery(lSql.toString());
			while(lResultSet.next()){
				String lFinalObligationStatus = lResultSet.getString("ObligationStatus");
				String lFinalModificationRequest = lResultSet.getString("ModificationRequest");
				String lFinalNewStatus = lResultSet.getString("NewStatus");
				BeanFieldMeta lField = (BeanFieldMeta) obligationBeanMeta.getFieldMap().get("status");
				Map<Object, IKeyValEnumInterface> lMap1 = lField.getDataSetKeyValueMap();
		        if (lMap1.containsKey(lFinalObligationStatus)){
		        	lStatus = (Status) lMap1.get(lFinalObligationStatus);
					lObligationFinalMap.put(lStatus.toString(), lFinalNewStatus);
		        }
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(lPaymentStatusMap != null && !lPaymentStatusMap.isEmpty()){
			lReturnMap.put("payment", lPaymentStatusMap);
		}
		if(lObligationStatusMap != null && !lObligationStatusMap.isEmpty()){
			lReturnMap.put("obligationMap", lObligationStatusMap);
		}
		if(lObligationModiReqMap != null && !lObligationModiReqMap.isEmpty()){
			lReturnMap.put("oblimodreq", lObligationModiReqMap);			
		}
		if(lObligationModiDetMap != null && !lObligationModiDetMap.isEmpty()){
			lReturnMap.put("oblimoddetail",lObligationModiDetMap);
		}
		if(lObligationFinalMap != null && !lObligationFinalMap.isEmpty()){
			lReturnMap.put("finalResult", lObligationFinalMap);
		}
		return lReturnMap;
	}
	public void getPaymentFileMessage(Connection pConnection, Long pId) throws Exception{
	Map<String, Object> lReturnMap = new HashMap<String, Object>();
	ResultSet lResultSet = null;
	PaymentFileBean lPaymentFileFilterBean = new PaymentFileBean();
	lPaymentFileFilterBean.setId(pId);
	PaymentFileBean lPaymentFileBean = paymentFileDAO.findBean(pConnection, lPaymentFileFilterBean);
	StringBuilder lSql = null;
	Statement lStatement = pConnection.createStatement();
	lSql = new StringBuilder();
	lSql.append(" SELECT COUNT(1) Count FROM OBLIGATIONSMODIREQUESTS ");
	lSql.append(" WHERE OMRFUID IN ");
	lSql.append(" ( ");
	lSql.append(" SELECT OBFUID FROM OBLIGATIONS WHERE OBPFID = " + lPaymentFileBean.getId());
	lSql.append(" ) and OMRSTATUS IN ('CRT','SNT') ");
	lResultSet = lStatement.executeQuery(lSql.toString());
	while(lResultSet.next()){
		Long obligModiReqStatusCount = lResultSet.getLong("Count");
		if (obligModiReqStatusCount>0) {
			throw new CommonBusinessException("Some modification requests are pending approval. Payfile can be processed once the request is Approved/Rejected.");
		}
	}
	lResultSet.close();
	lStatement.close();
}
    
	public List<String> getObligationStatusAndCount(Map<String,Object> pMap){
		Map<String,Object> lReturnedMap = new HashMap<String,Object>();
		lReturnedMap = pMap;
		List<String> lList = new ArrayList<String>();
		Map<String,Object> lPaymentStatusMap = new HashMap<String,Object>();
		lPaymentStatusMap = (Map<String, Object>) pMap.get("payment");
		lList.add("Payment File Status is "+ lPaymentStatusMap.get("paymentFileStatus"));
		Map<String,Object> lObligationStatusMap = new HashMap<String,Object>();
		lObligationStatusMap = (Map<String, Object>) pMap.get("obligation");
//		lObligationStatusMap.forEach((k,v) -> lList.add("The obligation splits status is "+ k + " and total count is "+ v));
		Map<String,Object> lObligationModiReqMap = new HashMap<String,Object>();
		lObligationModiReqMap = (Map<String, Object>) pMap.get("oblimodreq");
		Map<String,Object> lObligationModiDetMap = new HashMap<String, Object>(); 
		lObligationModiDetMap = (Map<String, Object>) pMap.get("oblimoddetail");
		Map<String,Object> lObligationFinalMap = new HashMap<String, Object>(); 
		lObligationFinalMap = (Map<String, Object>) pMap.get("finalResult");
			lList.add("The payment file status is " + lPaymentStatusMap.get("paymentFileStatus"));
			lList.add("The obligation splits status are as follows "+ lObligationStatusMap.get("lObligStatus"));
			lList.add("The obligation modification Request Status is as follows" + lObligationModiReqMap.get("obligModiReqStatus"));
			lList.add("The obligation modification detail status is as follows" + lObligationModiDetMap.get("lObligModiDetailStatus"));
			lList.add("The obligation Revised status is as follows" + lObligationFinalMap.get("lFinalObligationStatus"));
		return lList;
	}
	
}
