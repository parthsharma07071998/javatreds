package com.xlx.treds.auction.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObliFUInstDetailBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationModificationDetailBean;
import com.xlx.treds.auction.bean.ObligationModificationRequestBean;
import com.xlx.treds.auction.bean.ObligationModificationRequestBean.Status;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.EmailGeneratorBO;
import com.xlx.treds.user.bean.AppUserBean;

public class ObligationModificationRequestBO {

    private static final Logger logger = LoggerFactory.getLogger(ObligationModificationRequestBO.class);
    
	private GenericDAO<ObligationModificationRequestBean> obligationModificationRequestDAO;
	private GenericDAO<ObligationModificationDetailBean> obligationModificationDetailDAO;
	private CompositeGenericDAO<ObligationDetailBean> obligationDetailDAO;
	private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
	private GenericDAO<ObligationBean> obligationDAO;
	private GenericDAO<FactoringUnitBean> factoringUnitDAO;
	private GenericDAO<InstrumentBean> instrumentDAO;
	private EmailGeneratorBO emailGeneratorBO;

	
    public ObligationModificationRequestBO() {
        super();
        obligationModificationRequestDAO = new GenericDAO<ObligationModificationRequestBean>(ObligationModificationRequestBean.class);
        obligationModificationDetailDAO = new GenericDAO<ObligationModificationDetailBean>(ObligationModificationDetailBean.class);
        obligationDetailDAO = new CompositeGenericDAO<ObligationDetailBean>(ObligationDetailBean.class);
        obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		emailGeneratorBO = new EmailGeneratorBO();
    }
    
    public ObligationModificationRequestBean findBean(ExecutionContext pExecutionContext, 
        ObligationModificationRequestBean pFilterBean) throws Exception {
        ObligationModificationRequestBean lObligationModificationRequestBean = obligationModificationRequestDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lObligationModificationRequestBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        ObligationModificationDetailBean lFilterBean = new ObligationModificationDetailBean();
        lFilterBean.setOmrId(lObligationModificationRequestBean.getId());
        List<ObligationModificationDetailBean> lList = obligationModificationDetailDAO.findList(pExecutionContext.getConnection(), lFilterBean, (String)null);
        lObligationModificationRequestBean.setObliModDetailsList(lList);
        return lObligationModificationRequestBean;
    }
    
    public List<ObligationModificationRequestBean> findList(ExecutionContext pExecutionContext, ObligationModificationRequestBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    	List<ObligationModificationRequestBean> lList =  obligationModificationRequestDAO.findList(pExecutionContext.getConnection(), pFilterBean, new ArrayList<String>());
    	MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
    	AppUserBean lAppUserBean = null;
    	for(ObligationModificationRequestBean lBean : lList){
    		if(lBean.getCreaterAuId() != null){
    			lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lBean.getCreaterAuId()});
    			lBean.setCreaterName(lAppUserBean.getName());
    		}
    		if(lBean.getApproveRejectAuId() != null){
    			lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lBean.getApproveRejectAuId()});
    			lBean.setApproveRejectName(lAppUserBean.getName());
    		}
    	}
    	List<ObligationModificationRequestBean> lModificationRequests = new ArrayList<ObligationModificationRequestBean>();
    	for (ObligationModificationRequestBean lBean : lList){
    		if ( Status.Created.equals(lBean.getStatus())){
    			if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-save", pUserBean) && 
        				AccessControlHelper.getInstance().hasAccess("oblimodreq-approve", pUserBean)){
                    continue;
            	}
    		}
    		lModificationRequests.add(lBean);
    	}
    	return lModificationRequests;
    }
    
    public void save(ExecutionContext pExecutionContext, ObligationModificationRequestBean pObligationModificationRequestBean, IAppUserBean pUserBean, 
            boolean pNew) throws Exception {
            pExecutionContext.setAutoCommit(false);
            saveWithoutTransaction(pExecutionContext,pObligationModificationRequestBean,pUserBean,pNew);
            pExecutionContext.commitAndDispose();
    }

    public void saveWithoutTransaction(ExecutionContext pExecutionContext, ObligationModificationRequestBean pObligationModificationRequestBean, IAppUserBean pUserBean, 
            boolean pNew) throws Exception {
        Connection lConnection = pExecutionContext.getConnection();
    	ObligationModificationRequestBean lOldObligationModificationRequestBean = null;
        Map<Long, ObligationModificationDetailBean> lOldMap = new HashMap<Long, ObligationModificationDetailBean>();
        lOldObligationModificationRequestBean = getModificationRequestBean(lConnection, pObligationModificationRequestBean.getFuId(), pObligationModificationRequestBean.getType(), pObligationModificationRequestBean.getPartNumber());
        if (pObligationModificationRequestBean.getId()==null && 
        		lOldObligationModificationRequestBean != null && lOldObligationModificationRequestBean.getId()!=null){
        	if ( !(ObligationModificationRequestBean.Status.Rejected.equals(lOldObligationModificationRequestBean.getStatus()) &&
        			ObligationModificationRequestBean.Status.Applied.equals(lOldObligationModificationRequestBean.getStatus()))){
        		throw new CommonBusinessException("Request already present.");
        	}
        }
        if (pObligationModificationRequestBean.getId()== null){
        	pObligationModificationRequestBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, "ObligationModificationRequestBean.id"));
        	pObligationModificationRequestBean.setCreaterAuId(pUserBean.getId());
        	pObligationModificationRequestBean.setCreateDate(CommonUtilities.getToday());
        	if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-save", pUserBean)){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        	}
        	obligationModificationRequestDAO.insert(lConnection, pObligationModificationRequestBean );
        	obligationModificationRequestDAO.insertAudit(lConnection, pObligationModificationRequestBean, AuditAction.Insert, pUserBean.getId());
        }else{
        	lOldObligationModificationRequestBean = findBean(pExecutionContext, pObligationModificationRequestBean);
         	List<String> lColumns = new ArrayList<String>();
         	lColumns.add("status");
         	obligationModificationRequestDAO.getBeanMeta().copyBean( pObligationModificationRequestBean, lOldObligationModificationRequestBean, null, lColumns);
         	boolean lCanModify = AccessControlHelper.getInstance().hasAccess("oblimodreq-save", pUserBean);
         	boolean lCanApprove = AccessControlHelper.getInstance().hasAccess("oblimodreq-approve", pUserBean);
         	if(Status.Created.equals(pObligationModificationRequestBean.getStatus())){
         		if(!lCanModify){
         			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
         		}
         	}else if(Status.Sent.equals(pObligationModificationRequestBean.getStatus())){
         		if(!(lCanApprove && lCanModify)){
         			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
         		}
         	}
         	lOldObligationModificationRequestBean.setRecordUpdator(pUserBean.getId());
         	if (obligationModificationRequestDAO.update(lConnection, lOldObligationModificationRequestBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                 throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
         	obligationModificationRequestDAO.insertAudit(lConnection, lOldObligationModificationRequestBean, AuditAction.Update, pUserBean.getId());
            if (lOldObligationModificationRequestBean.getObliModDetailsList() != null) {
                 for (ObligationModificationDetailBean lObligationModificationDetailBean : lOldObligationModificationRequestBean.getObliModDetailsList() ) {
                     lOldMap.put(lObligationModificationDetailBean.getId(), lObligationModificationDetailBean);
                 }
            }
        }
        if (pObligationModificationRequestBean.getObliModDetailsList() != null) {
        	BigDecimal lDebitAmt = BigDecimal.ZERO.setScale(2);
        	BigDecimal lCreditAmt = BigDecimal.ZERO.setScale(2);
        	Date lRevisedDate = null;
        	ObligationBean.Status lRevisedStatus = null; 
        	String lPaymentSettlor = null;
        	
        	for (ObligationModificationDetailBean lObligationModificationDetailBean : pObligationModificationRequestBean.getObliModDetailsList()) {
        		//revised amount
        		if(TxnType.Debit.equals(lObligationModificationDetailBean.getTxnType())){
            		lDebitAmt = lDebitAmt.add(lObligationModificationDetailBean.getRevisedAmount());
            	}else if (TxnType.Credit.equals(lObligationModificationDetailBean.getTxnType())){
            		lCreditAmt = lCreditAmt.add(lObligationModificationDetailBean.getRevisedAmount());
            	}
            	//revised date
        		if(lRevisedDate==null){
        			lRevisedDate = lObligationModificationDetailBean.getRevisedDate();
            	}
        		if (lRevisedDate==null || !lRevisedDate.equals(lObligationModificationDetailBean.getRevisedDate())){
        			throw new CommonBusinessException("Credit And Debit obligation date mismatch.");
        		}
            	//revised status
        		if(lRevisedStatus==null){
        			lRevisedStatus = lObligationModificationDetailBean.getRevisedStatus();
            	}
        		if (lRevisedStatus==null || !lRevisedStatus.equals(lObligationModificationDetailBean.getRevisedStatus())){
        			throw new CommonBusinessException("Credit And Debit obligation status mismatch.");
        		}
        		if ((ObligationBean.Status.Success.equals(lObligationModificationDetailBean.getRevisedStatus()) 
        				|| ObligationBean.Status.Failed.equals(lObligationModificationDetailBean.getRevisedStatus()))
        				&& AppConstants.FACILITATOR_DIRECT.equals(lObligationModificationDetailBean.getPaymentSettlor())){
        			throw new CommonBusinessException("For Direct Settlements status should be marked as Ready/Created ");
        		}
            	//payment settlor
        		if(lPaymentSettlor==null){
        			lPaymentSettlor = lObligationModificationDetailBean.getPaymentSettlor();
            	}
        		if (lPaymentSettlor==null || !lPaymentSettlor.equals(lObligationModificationDetailBean.getPaymentSettlor())){
        			throw new CommonBusinessException("Credit And Debit obligation settlor mismatch.");
        		}
            }

        	logger.info("Credit amt :"+lCreditAmt+" and Debit amt:"+lDebitAmt);
            if(lDebitAmt.compareTo(lCreditAmt)!=0){
            	throw new CommonBusinessException("Credit And Debit obligation amount mismatch.");
            }
        	
            for (ObligationModificationDetailBean lObligationModificationDetailBean : pObligationModificationRequestBean.getObliModDetailsList()) {
            	if (lObligationModificationDetailBean.getId() == null) {
                	if (lObligationModificationDetailBean.getRevisedStatus()==null) throw new CommonBusinessException("Please select a valid revised status.");
                	if(CommonAppConstants.YesNo.Yes.equals(pObligationModificationRequestBean.getIsPreModification()) || TredsHelper.getInstance().allowObligationModificationRequest(lConnection, pObligationModificationRequestBean.getFuId(), pObligationModificationRequestBean.getPartNumber(), pObligationModificationRequestBean.getType(), lObligationModificationDetailBean.getOrigStatus(), lObligationModificationDetailBean.getRevisedStatus())){
            			lObligationModificationDetailBean.setId(DBHelper.getInstance().getUniqueNumber(lConnection, "ObligationModificationDetailBean.id"));
                    	lObligationModificationDetailBean.setOmrId(pObligationModificationRequestBean.getId());
                    	lObligationModificationDetailBean.setRecordCreator(pUserBean.getId());
                    	obligationModificationDetailDAO.insert(lConnection, lObligationModificationDetailBean);
                    	obligationModificationDetailDAO.insertAudit(lConnection, lObligationModificationDetailBean, AuditAction.Insert,pUserBean.getId());
					}
                }else {
                	ObligationModificationDetailBean lOldObligationModificationDetailBean = lOldMap.remove(lObligationModificationDetailBean.getId());
                    if (lOldObligationModificationDetailBean == null)
                        throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
                    if (!obligationModificationDetailDAO.getBeanMeta().equalBean(lObligationModificationDetailBean, lOldObligationModificationDetailBean, BeanMeta.FIELDGROUP_UPDATE, null)) {
                     	List<String> lColumns = new ArrayList<String>();
                     	lColumns.add("revisedAmount");
                     	lColumns.add("revisedDate");
                     	lColumns.add("revisedStatus");
                     	lColumns.add("paymentSettlor");
                     	lColumns.add("remarks");
                     	lColumns.add("paymentRefNo");
                    	obligationModificationDetailDAO.getBeanMeta().copyBean( lObligationModificationDetailBean, lOldObligationModificationDetailBean, null, lColumns);
                    	lOldObligationModificationDetailBean.setRecordUpdator(pUserBean.getId());
                    	obligationModificationDetailDAO.update(lConnection, lOldObligationModificationDetailBean, BeanMeta.FIELDGROUP_UPDATE);
                    	obligationModificationDetailDAO.insertAudit(lConnection, lOldObligationModificationDetailBean, AuditAction.Update,pUserBean.getId());
                    }
                }
            
            }
        }
    }
    
	public ObligationModificationRequestBean createBean(Connection pConnection, Long pFuId, Type pType,Long pPartNumber) throws Exception {
		DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        ObligationModificationRequestBean lObligationModificationRequestBean = getModificationRequestBean(pConnection, pFuId, pType, pPartNumber);
        if(lObligationModificationRequestBean==null){
        	List<ObligationDetailBean> lObligationDetails = null;
            //
            lSql.append("SELECT * FROM Obligations ");
            if (!new Long(0).equals(pPartNumber)){
            	lSql.append(" , ObligationSplits ");
            }
            lSql.append(" WHERE ");
            lSql.append(" OBFUID = ").append(lDBHelper.formatString(pFuId.toString())); 
            lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(pType.getCode()));
            lSql.append(" AND OBSTATUS NOT IN ( ");
            lSql.append(lDBHelper.formatString(ObligationBean.Status.Shifted.getCode())).append(" , ");
            lSql.append(lDBHelper.formatString(ObligationBean.Status.Extended.getCode())).append(" ) "); 
            if (!new Long(0).equals(pPartNumber)){
				lSql.append(" AND OBSSTATUS NOT IN ( ");
	            lSql.append(lDBHelper.formatString(ObligationBean.Status.Shifted.getCode())).append(" , ");
	            lSql.append(lDBHelper.formatString(ObligationBean.Status.Extended.getCode())).append(" ) ");
	            lSql.append(" AND OBSSETTLORPROCESSED IS NULL ");
            	lSql.append(" AND OBSPARTNUMBER = ").append(lDBHelper.formatString(pPartNumber.toString()));
            	lSql.append(" AND OBSOBID = OBID "); 
            }
            
            lObligationDetails =  obligationDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
            ObligationBean lObligationBean = null;
            ObligationSplitsBean lObligationSplitsBean = null;
            ObligationModificationDetailBean lObligationModificationDetailBean = null;
            lObligationModificationRequestBean = new ObligationModificationRequestBean();
            List<ObligationModificationDetailBean> lObligationModificationDetails = new ArrayList<ObligationModificationDetailBean>();
            lObligationModificationRequestBean.setFuId(pFuId);
            lObligationModificationRequestBean.setType(pType);
            lObligationModificationRequestBean.setPartNumber(pPartNumber);
            lObligationModificationRequestBean.setStatus(ObligationModificationRequestBean.Status.Created);
            lObligationModificationRequestBean.setDate(CommonUtilities.getToday());
            List <Long> lObIds = new ArrayList<Long>();
            for(ObligationDetailBean lObligationDetailBean : lObligationDetails){
            	lObligationBean = lObligationDetailBean.getObligationBean();
            	lObligationModificationDetailBean = new ObligationModificationDetailBean();
            	lObligationModificationDetailBean.setTxnType(lObligationBean.getTxnType());
            	lObligationModificationDetailBean.setRevisedDate(lObligationBean.getDate());
            	lObligationModificationDetailBean.setOrigDate(lObligationBean.getDate());
            	if(!new Long(0).equals(pPartNumber)){
	            	lObligationSplitsBean = lObligationDetailBean.getObligationSplitsBean();
	            	lObligationSplitsBean.setParentObligation(lObligationBean);
	            	lObligationModificationDetailBean.setObId(lObligationSplitsBean.getObid());
	            	lObligationModificationDetailBean.setOrigAmount(lObligationSplitsBean.getAmount());
	            	lObligationModificationDetailBean.setOrigStatus(lObligationSplitsBean.getStatus());
	            	lObligationModificationDetailBean.setPartNumber(lObligationSplitsBean.getPartNumber());
	            	lObligationModificationDetailBean.setRevisedAmount(lObligationSplitsBean.getAmount());
	            	lObligationModificationDetailBean.setPaymentSettlor(lObligationSplitsBean.getPaymentSettlor());
            	}else{
            		lObligationModificationDetailBean.setObId(lObligationBean.getId());
            		lObligationModificationDetailBean.setOrigAmount(lObligationBean.getAmount());
	            	lObligationModificationDetailBean.setOrigStatus(lObligationBean.getStatus());
	            	lObligationModificationDetailBean.setPartNumber(pPartNumber);
	            	lObligationModificationDetailBean.setRevisedAmount(lObligationBean.getAmount());
	            	lObligationModificationDetailBean.setRevisedStatus(lObligationBean.getStatus());
	            	lObIds.add(lObligationBean.getId());
            	}
            	lObligationModificationDetails.add(lObligationModificationDetailBean);
            }
            lObligationModificationRequestBean.setObliModDetailsList(lObligationModificationDetails);
            if( (new Long(0)).equals(pPartNumber)){
            	String lSettlor = getSettlor(pConnection, lObIds) ;
	            for(ObligationModificationDetailBean lBean : lObligationModificationRequestBean.getObliModDetailsList()){
	            	lBean.setPaymentSettlor(lSettlor);
	            } 
            }
        }
        if( !(new Long(0)).equals(pPartNumber)){
        	boolean lHasProvSuccess = false;
        	for(ObligationModificationDetailBean lBean : lObligationModificationRequestBean.getObliModDetailsList()){
        		if(ObligationBean.Status.Prov_Success.equals(lBean.getOrigStatus())){
        			lHasProvSuccess = true;
        		}
            }
        	if(!lHasProvSuccess){
                for(ObligationModificationDetailBean lBean : lObligationModificationRequestBean.getObliModDetailsList()){
                	if (!TredsHelper.getInstance().getValidOldStatusForModification().contains(lBean.getOrigStatus().getCode())){
                		throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
                	}
                }
        	}
        }
		return lObligationModificationRequestBean;
	}
	
	public ObligationModificationRequestBean getModificationRequestBean(Connection pConnection,  Long pFuId, Type pType,Long pPartNumber) throws Exception {
		DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        ObligationModificationRequestBean lObligationModificationRequestBean = new ObligationModificationRequestBean();
		lSql.append("SELECT * FROM ObligationsModiRequests ");
        lSql.append(" WHERE ");
        lSql.append(" OMRFUID = ").append(lDBHelper.formatString(pFuId.toString())); 
        lSql.append(" AND OMRTYPE = ").append(lDBHelper.formatString(pType.getCode()));
        lSql.append(" AND OMRPARTNUMBER = ").append(lDBHelper.formatString(pPartNumber.toString()));
        lSql.append(" AND OMRSTATUS NOT IN ( ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Rejected.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Applied.getCode()));
        lSql.append(" ) ");
        lObligationModificationRequestBean = obligationModificationRequestDAO.findBean(pConnection, lSql.toString());
	    if (lObligationModificationRequestBean != null){
	        	ObligationModificationDetailBean lFilterBean = new ObligationModificationDetailBean();
		        lFilterBean.setOmrId(lObligationModificationRequestBean.getId());
		        List<ObligationModificationDetailBean> lList = obligationModificationDetailDAO.findList(pConnection, lFilterBean, (String)null);
		        lObligationModificationRequestBean.setObliModDetailsList(lList);
	    }
	    return lObligationModificationRequestBean;
    }
	
	public void updateStatus(Connection pConnection,ObligationModificationRequestBean.Status pStatus, Long pId, AppUserBean pUserBean, String pRemarks ) throws Exception{
		ObligationModificationRequestBean lFilterBean = new ObligationModificationRequestBean();
		lFilterBean.setId(pId);
		String lFieldGroup = ObligationModificationRequestBean.FIELDGROUP_UPDATESTATUS;
		ObligationModificationRequestBean lModificationRequestBean = obligationModificationRequestDAO.findByPrimaryKey(pConnection, lFilterBean);
		ObligationModificationDetailBean lDetailBean = new ObligationModificationDetailBean();
		lDetailBean.setOmrId(lModificationRequestBean.getId());
		List<ObligationModificationDetailBean> lOMDList = obligationModificationDetailDAO.findList(pConnection, lDetailBean, (String)null);
		lModificationRequestBean.setObliModDetailsList(lOMDList);
		if (!Status.Created.equals(lModificationRequestBean.getStatus()) && Status.Sent.equals(pStatus)){
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED+" Invalid Status.");
		}
		if (!Status.Sent.equals(lModificationRequestBean.getStatus()) && 
				( Status.Approved.equals(pStatus) || Status.Rejected.equals(pStatus) )){
			throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED+" Invalid Status.");
		}
		lModificationRequestBean.setStatus(pStatus);
		if (pStatus.equals(Status.Rejected)||pStatus.equals(Status.Approved)){
			lModificationRequestBean.setApproveRejectAuId(pUserBean.getId());
	     	lModificationRequestBean.setApproveRejectDate(CommonUtilities.getToday());
	     	lFieldGroup = ObligationModificationRequestBean.FIELDGROUP_APPROVEREJECT;
		}
		if (StringUtils.isNotEmpty(pRemarks)){
			lModificationRequestBean.setRemarks(pRemarks);
		}
		//Keeping the obligationModificationDetailBean - obligationId wise hash
		//Key=ObliModiDetailBean.ObligationId , Value=ObliModiDetailBean
		Map<Long, ObligationModificationDetailBean> lOBDetailMap = new HashMap<Long, ObligationModificationDetailBean>(); 
		String lNewSettlor = null;
		String lOldSettlor = null;
		for(ObligationModificationDetailBean lBean : lModificationRequestBean.getObliModDetailsList() ){
			if (StringUtils.isEmpty(lOldSettlor)){
				lNewSettlor = lBean.getPaymentSettlor();
			}
			if (!lOBDetailMap.containsKey(lBean.getObId())){
				lOBDetailMap.put(lBean.getObId(),lBean);
			}
		}
		if (Status.Approved.equals(lModificationRequestBean.getStatus()) 
				&& CommonAppConstants.YesNo.Yes.equals(lModificationRequestBean.getIsPreModification())){
			logger.info("Oblig Modi : update Status : Approval - Premodification.");
			List<ObligationDetailBean> lDetailList = TredsHelper.getInstance().getObligationDetailBean(pConnection, lModificationRequestBean.getFuId(), lModificationRequestBean.getType(), null);
			List<Long> lUpdatedParentObIds = new ArrayList<Long>();
			ObligationBean lObliBean = null;
			ObligationSplitsBean lObliSplitsBean = null; 
			ObligationModificationDetailBean lModiDetailBean = null; 
			for (ObligationDetailBean lBean : lDetailList){
				lObliBean = lBean.getObligationBean();
				lObliSplitsBean = lBean.getObligationSplitsBean();
				lObliSplitsBean.setParentObligation(lObliBean);
				lModiDetailBean = lOBDetailMap.get(lObliBean.getId());
				if (!lUpdatedParentObIds.contains(lObliBean.getId())){
					if (!lObliBean.getDate().equals(lModiDetailBean.getRevisedDate())){
						obligationDAO.insertAudit(pConnection, lObliBean, AuditAction.Update, pUserBean.getId());
						lObliBean.setRecordUpdator(pUserBean.getId());
						lObliBean.setDate(lModiDetailBean.getRevisedDate());
						lObliBean.setRespRemarks(lModiDetailBean.getRemarks());
						obligationDAO.update(pConnection, lObliBean,ObligationBean.FIELDGROUP_UPDATEPREGENERATIONMODIDICATION);
						lUpdatedParentObIds.add(lObliBean.getId());
					}
				}
				if (!lObliSplitsBean.getPaymentSettlor().equals(lModiDetailBean.getPaymentSettlor())){
					obligationSplitsDAO.insertAudit(pConnection, lObliSplitsBean, AuditAction.Update, pUserBean.getId());
					lObliSplitsBean.setRecordUpdator(pUserBean.getId());
					lObliSplitsBean.setPaymentSettlor(lModiDetailBean.getPaymentSettlor());
					obligationSplitsDAO.update(pConnection, lObliSplitsBean,ObligationSplitsBean.FIELDGROUP_UPDATEPREGENERATIONMODIDICATION);
				}
			}
		}
		if (Status.Approved.equals(lModificationRequestBean.getStatus()) && AppConstants.FACILITATOR_DIRECT.equals(lNewSettlor)){
			logger.info("Oblig Modi : update Status : Approval - Direct Settlor.");
			List<ObligationDetailBean> lList = TredsHelper.getInstance().getObligationDetailBean(pConnection, lModificationRequestBean.getFuId(), lModificationRequestBean.getType(), lModificationRequestBean.getPartNumber());
			AppEntityBean lAppEntity = null;
			String lEntityCode = null;
    		String lEmailNotifyType = null;
    		List<ObliFUInstDetailBean> lTmpList = null;
    		ObliFUInstDetailBean lFuObInstBean = null;
    		FactoringUnitBean lFuBean = null;
    		InstrumentBean lInstBean = null;
    		//Key = FuId , Value = Cr. obligation non treds
    		Map<Long,ObligationBean> lCreditObliMap = new HashMap<Long, ObligationBean>();
    		
			for (ObligationDetailBean lDetailsBean : lList){
				ObligationBean lObligationBean = lDetailsBean.getObligationBean();
				if(!AppConstants.DOMAIN_PLATFORM.equals(lObligationBean.getTxnEntity()) && 
						TxnType.Credit.equals(lObligationBean.getTxnType()) ){
					lCreditObliMap.put(lObligationBean.getFuId(), lObligationBean);
					ObligationSplitsBean lOBSBean = lDetailsBean.getObligationSplitsBean();
					lOBSBean.setParentObligation(lObligationBean);
					if (CommonAppConstants.YesNo.Yes.equals(lModificationRequestBean.getIsPreModification())){
						CompanyBankDetailBean lCBDBean = TredsHelper.getInstance().getSettlementBank(pConnection, lObligationBean.getTxnEntity(), lObligationBean.getSettlementCLId());
						lObligationBean.setPayDetail1(lCBDBean.getAccNo());
						lObligationBean.setPayDetail2(lCBDBean.getIfsc());
						lEntityCode =  lObligationBean.getTxnEntity();
						lAppEntity = TredsHelper.getInstance().getAppEntityBean(lEntityCode);
						lObligationBean.setPayDetail3(lAppEntity.getName());
					}else{
						lObligationBean.setPayDetail1(lOBSBean.getPayDetail1());
						lObligationBean.setPayDetail2(lOBSBean.getPayDetail2());
						lObligationBean.setPayDetail3(lOBSBean.getPayDetail3());
					}
				}
			}
    		//
			for (ObligationDetailBean lDetailsBean : lList){
				ObligationSplitsBean lOBSBean = lDetailsBean.getObligationSplitsBean();
				ObligationBean lObligationBean = lDetailsBean.getObligationBean();
				lOBSBean.setParentObligation(lObligationBean);
				ObligationBean lCrObligationBean = null;
				if (!lOBDetailMap.containsKey(lOBSBean.getId())){
					continue;
				}
				if (StringUtils.isEmpty(lOldSettlor)){
					lOldSettlor = lOBSBean.getPaymentSettlor();
				}
				if ((!lOldSettlor.equals(lNewSettlor)) && 
						AppConstants.FACILITATOR_DIRECT.equals(lNewSettlor) &&
						TxnType.Debit.equals(lObligationBean.getTxnType())){
					Map<String, Object> lMailData = new HashMap<String, Object>();
					lEntityCode =  lObligationBean.getTxnEntity();
					lMailData.put("entityCode", lEntityCode);
					lMailData.put("settlementDate",lOBDetailMap.get(lOBSBean.getId()).getRevisedDate());
					lMailData.put("obligationAmt",lOBDetailMap.get(lOBSBean.getId()).getRevisedAmount());
					lAppEntity = TredsHelper.getInstance().getAppEntityBean(lEntityCode);
					lTmpList = new ArrayList<ObliFUInstDetailBean>();
					//
					lObligationBean.setDate(lOBDetailMap.get(lObligationBean.getId()).getRevisedDate());
					lObligationBean.setAmount(lOBDetailMap.get(lObligationBean.getId()).getOrigAmount());
					
					lCrObligationBean = lCreditObliMap.get(lObligationBean.getFuId());
					lObligationBean.setPayDetail1(lCrObligationBean.getPayDetail1());
					lObligationBean.setPayDetail2(lCrObligationBean.getPayDetail2());
					lObligationBean.setPayDetail3(lCrObligationBean.getPayDetail3());
					//
					lFuObInstBean = new ObliFUInstDetailBean();
					lFuObInstBean.setObligationBean(lObligationBean);
					//
					lFuBean = new FactoringUnitBean();
					lFuBean.setId(lObligationBean.getFuId());
					lFuObInstBean.setFactoringUnitBean(factoringUnitDAO.findBean(pConnection, lFuBean));
					//
					lInstBean = new InstrumentBean();
					lInstBean.setFuId(lObligationBean.getFuId());
					lFuObInstBean.setInstrumentBean(instrumentDAO.findBean(pConnection, lInstBean));
					lFuObInstBean.getInstrumentBean().populateNonDatabaseFields();
					//
					lTmpList.add(lFuObInstBean);
					//
					lEmailNotifyType = null;
					if(lAppEntity!=null){
						if(lAppEntity.isFinancier()){
							lEmailNotifyType =  AppConstants.EMAIL_NOTIFY_TYPE_CURRENTFINOBLIGATIONDUE_1;
						}
						else if (lAppEntity.isPurchaser()){
							lEmailNotifyType =  AppConstants.EMAIL_NOTIFY_TYPE_CURRENTPUROBLIGATIONDUE_1;
						}else{
							logger.info("1Entity Type different : " + lEntityCode + " : " + lAppEntity.getType() );
						}
					}else{
						logger.info("1Entity not found : " + lEntityCode );
					}

					List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lEntityCode, lEmailNotifyType);
					
					lMailData.put(EmailSender.TO, lEmailIds);
					lMailData.put(EmailSender.CC, TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
					//the attachment
					List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
					//change the function parameter to ANId
					lListAttach.add(emailGeneratorBO.getObligationsDue(lEntityCode, lOBDetailMap.get(lOBSBean.getId()).getRevisedDate(), lTmpList,true));
					lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
					//
					if(lAppEntity!=null){
						if(lAppEntity.isFinancier()){
							EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLIDUEL1FINANCIER, (HashMap)lMailData);
						}
						else if (lAppEntity.isPurchaser()){
							EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLIDUEL1PURCHASER, (HashMap)lMailData);
						}else{
							logger.info("Entity Type different : " + lAppEntity.getCode() + " : " + lAppEntity.getType() );
						}
					}else{
						logger.info("Entity not found : " + lEntityCode );
					}
				}
			}
		}
		if(Status.Approved.equals(lModificationRequestBean.getStatus()) && CommonAppConstants.YesNo.Yes.equals(lModificationRequestBean.getIsPreModification())){
			lModificationRequestBean.setStatus(Status.Applied);
		}
     	if (obligationModificationRequestDAO.update(pConnection, lModificationRequestBean, lFieldGroup) == 0)
             throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		if(Status.Approved.equals(lModificationRequestBean.getStatus()) 
				&& !CommonAppConstants.YesNo.Yes.equals(lModificationRequestBean.getIsPreModification())){
			//update the child using approved modification request
			//there after using the updated child - update the parent as below
			//change parent amount to sum(success)+sum(rdy/crt)
			List<ObligationDetailBean> lList = TredsHelper.getInstance().getObligationDetailBean(pConnection, lModificationRequestBean.getFuId(), lModificationRequestBean.getType(), lModificationRequestBean.getPartNumber());
			HashMap<Long,ObligationSplitsBean> lSplitsHash = new HashMap<Long,ObligationSplitsBean>();
			HashMap<Long,ObligationBean> lParentHash = new HashMap<Long,ObligationBean>();
			for(ObligationDetailBean lBean:lList){
				lSplitsHash.put(lBean.getObligationSplitsBean().getObid(), lBean.getObligationSplitsBean());
				if (!lParentHash.containsKey(lBean.getObligationBean().getId())) {
					lParentHash.put(lBean.getObligationBean().getId(), lBean.getObligationBean());
				}
			}
			boolean lMarkAppllied = false;
			List<Long> lParentIds = new ArrayList<>();
			List<Long> lL2FUIds = new ArrayList<>();
			for(ObligationModificationDetailBean lModDetailBean : lModificationRequestBean.getObliModDetailsList()){
				if(lModificationRequestBean.getId().equals(lModDetailBean.getOmrId()) &&
						lModificationRequestBean.getPartNumber().equals(lModDetailBean.getPartNumber())){
					ObligationSplitsBean lSplitsBean = lSplitsHash.get(lModDetailBean.getObId());
					if (ObligationBean.Status.Ready.equals(lModDetailBean.getRevisedStatus())
							|| ObligationBean.Status.Created.equals(lModDetailBean.getRevisedStatus())){
						lSplitsBean.setAmount(lModDetailBean.getRevisedAmount());
						lSplitsBean.setStatus(lModDetailBean.getRevisedStatus());
						lSplitsBean.setDate(lModDetailBean.getRevisedDate());
						logger.info("new Status Ready");
						lSplitsBean.setPfId(null);
						//keep the paydetails same 
						lSplitsBean.setPaymentRefNo(null);
						lSplitsBean.setFileSeqNo(null);
						if(lSplitsBean instanceof ObligationSplitsBean){
							ObligationSplitsBean lOSBean = (ObligationSplitsBean) lSplitsBean;
							//TODO: should we check status
							if(!lOSBean.getPaymentSettlor().equals(lModDetailBean.getPaymentSettlor())){
								String lRemarks = "";
								lOSBean.setPaymentSettlor(lModDetailBean.getPaymentSettlor());
								if(StringUtils.isNotEmpty(lOSBean.getRespRemarks())){
									lRemarks =  lOSBean.getRespRemarks();
								}
								lRemarks += ((lOSBean.getPfId()!=null)?(" Old PFId : "+lOSBean.getPfId().toString()):" PFId ");
								lOSBean.setRespRemarks(lRemarks);
								lOSBean.setPfId(null);
							}
						}
						try {
							obligationSplitsDAO.update(pConnection, (ObligationSplitsBean) lSplitsBean);
						} catch (Exception e){
							logger.info("Error while updating splits : " +e.getMessage());
						}
					}
					ObligationBean lParentBean = lParentHash.get(lModDetailBean.getObId());
					if (!lParentIds.contains(lParentBean.getId())){
						if (ObligationBean.Status.Ready.equals(lModDetailBean.getRevisedStatus())
								|| ObligationBean.Status.Created.equals(lModDetailBean.getRevisedStatus())){
							lParentBean.setDate(lModDetailBean.getRevisedDate());
							logger.info("new Status Ready");
							lParentBean.setPfId(null);
							lParentBean.setStatus(ObligationBean.Status.Ready);
							//keep the paydetails same 
							try {
								obligationDAO.update(pConnection, (ObligationBean) lParentBean);
								lParentIds.add(lParentBean.getId());
								if(ObligationBean.Type.Leg_2.equals(lParentBean.getType())) {
									lL2FUIds.add(lParentBean.getFuId());
								}
							} catch (Exception e){
								logger.info("Error while updating parent : " +e.getMessage());
						}
					}
					if (!lMarkAppllied){
						lMarkAppllied = true;
					} 
				}
			}
			if(lL2FUIds.size() > 0) {
				StringBuilder lSql = new StringBuilder();
				lSql.append(" UPDATE FactoringUnits SET FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Leg_1_Settled.getCode()));
				lSql.append(" , FUSTATUSUPDATETIME = sysdate ");
				lSql.append(" WHERE 1=1 ");
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lL2FUIds);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("FUID", lListStr)).append("  ");
				lSql.append(" AND FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Leg_2_Failed.getCode()));
				if (logger.isDebugEnabled())
				    logger.debug(lSql.toString());
				Statement lStatement = null;
				try {
					lStatement = pConnection.createStatement();
				    int lCount = lStatement.executeUpdate(lSql.toString());
				}catch(Exception lEx){  
				    logger.info("error in updating status of factoringunits from L2FAIL to L1SET "+lEx.getMessage());
				}finally {
					if (lStatement != null)
						lStatement.close();
				}
			}
			if(lL2FUIds.size() > 0) {
				StringBuilder lSql = new StringBuilder();
				lSql.append(" UPDATE Instruments SET INSTATUS = ").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_1_Settled.getCode()));
				lSql.append(" WHERE 1=1 ");
				String[] lListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(lL2FUIds);
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("INFUID", lListStr)).append("  ");
				lSql.append(" AND INSTATUS = ").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_2_Failed.getCode()));
				if (logger.isDebugEnabled())
				    logger.debug(lSql.toString());
				Statement lStatement = null;
				try {
					lStatement = pConnection.createStatement();
				    int lCount = lStatement.executeUpdate(lSql.toString());
				}catch(Exception lEx){  
				    logger.info("error in updating status of instruments from L2FAIL to L1SET "+lEx.getMessage());
				}finally {
					if (lStatement != null)
						lStatement.close();
				}
			}
		}
		if (lMarkAppllied){
			lModificationRequestBean.setStatus(Status.Applied);
			obligationModificationRequestDAO.update(pConnection, lModificationRequestBean, ObligationModificationRequestBean.FIELDGROUP_UPDATESTATUS);
			//
			Long lFuid = lModificationRequestBean.getFuId();
			Type lLeg = lModificationRequestBean.getType();
			StringBuilder lSql = new StringBuilder();
			lSql.append(" MERGE INTO OBLIGATIONS ");
			lSql.append(" USING ( ");
			lSql.append(" SELECT OBSOBID ");
			lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Success.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) SuccessAmt ");
			lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Ready.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) ReadyAmt ");
			lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Created.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) CreatedAmt ");
			lSql.append(" FROM Obligationsplits,Obligations ");
			lSql.append(" WHERE OBSRecordVersion > 0 ");
			lSql.append(" AND OBRecordVersion > 0 ");
			lSql.append(" AND OBSOBID=OBID ");
			lSql.append(" AND OBFUID = ").append(lFuid);
			lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(lLeg.getCode()));
			lSql.append(" GROUP BY OBSOBID ");
			lSql.append(" ) CalAmt ");
			lSql.append(" ON  ");
			lSql.append(" ( ");
			lSql.append(" OBID = OBSOBID  ");
			lSql.append(" ) ");
			lSql.append(" WHEN MATCHED THEN ");
			lSql.append(" UPDATE SET OBAmount = SuccessAmt + ReadyAmt + CreatedAmt ");
			if (logger.isDebugEnabled())
			    logger.debug(lSql.toString());
			Statement lStatement = null;
			try {
				lStatement = pConnection.createStatement();
			    int lCount = lStatement.executeUpdate(lSql.toString());
			}catch(Exception lEx){  
			    logger.info("error in updating obligation amount"+lEx.getMessage());
			}finally {
				if (lStatement != null)
					lStatement.close();
			}
			lModificationRequestBean.setStatus(Status.Applied);
	     	if (obligationModificationRequestDAO.update(pConnection, lModificationRequestBean, lFieldGroup) == 0)
	             throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
			}
		}
	}
	    
	private String getSettlor(Connection pConnection, List<Long> lObIds){
		String lSettlor = null;
		StringBuilder lSql = new StringBuilder();
		lSql.append(" Select distinct OBSPAYMENTSETTLOR from Obligationsplits where OBSOBID in ( ");
		lSql.append(TredsHelper.getInstance().getCSVIdsForInQuery(lObIds)).append(" ) ");
		try (Statement lStatement = pConnection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
			while (lResultSet.next()){
				if(StringUtils.isEmpty(lSettlor)){
					lSettlor = lResultSet.getString("OBSPAYMENTSETTLOR");
				}else if (!(lSettlor.equals(lResultSet.getString("OBSPAYMENTSETTLOR")))) {
					throw new CommonBusinessException("Multiple settlors found for splits.");
				}
			}
		}catch(Exception e){
			logger.info(e.getMessage());
		}
		return lSettlor;
	}
	
	public List<ObligationModificationRequestBean> getModificationRequestList(Connection pConnection,  Long pPfId) throws Exception {
		DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationModificationRequestBean> lObligationModificationRequestList = new ArrayList<>();
		lSql.append(" SELECT * FROM OBLIGATIONSMODIREQUESTS,OBLIGATIONSMODIDETAILS");
        lSql.append(" WHERE ");
        lSql.append(" OMRFUID || '^' || OMRTYPE || '^' || OMRPARTNUMBER  IN ( ");
        lSql.append(" SELECT DISTINCT OBFUID || '^' || OBTYPE || '^' || OBSPARTNUMBER FROM OBLIGATIONS,OBLIGATIONSPLITS ");
        lSql.append(" WHERE ");
        lSql.append(" OBSOBID = OBID ");
        lSql.append(" AND OBPFID = ").append(pPfId); 
        lSql.append(" AND OBSPFID = ").append(pPfId);
        lSql.append(" AND OBSSETTLORPROCESSED IS NULL ");
        lSql.append(" AND OBSTATUS = ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Sent.getCode())); 
		lSql.append(" AND OBSSTATUS IN ( ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Prov_Success.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Returned.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Cancelled.getCode())).append(" ) "); 
        lSql.append(" ) ");
        lSql.append(" AND OMRID = OMDOMRID ");
        lSql.append(" AND OMRSTATUS NOT IN ( ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Rejected.getCode()));
        lSql.append(" , ").append(lDBHelper.formatString(ObligationModificationRequestBean.Status.Applied.getCode()));
        lSql.append(" ) ");
        lObligationModificationRequestList = obligationModificationRequestDAO.findListFromSql(pConnection, lSql.toString(),-1);
	    for(ObligationModificationRequestBean lOMRBean :lObligationModificationRequestList){
	    	if (lOMRBean != null){
	        	ObligationModificationDetailBean lFilterBean = new ObligationModificationDetailBean();
		        lFilterBean.setOmrId(lOMRBean.getId());
		        List<ObligationModificationDetailBean> lList = obligationModificationDetailDAO.findList(pConnection, lFilterBean, (String)null);
		        lOMRBean.setObliModDetailsList(lList);
	    }
	    	
	    }
	    return lObligationModificationRequestList;
    }
	
	public byte[] getObligationDetails(Connection pConnection, Long pPfId) throws Exception {
		//Input : PFId
		//Output : 1 WorkBook with 2 Sheets - 1st Sheet : New Modification Request and 2nd Sheet : Already Created requests
		//Step 1 : Find already created Modification Request 
		//Step 2 : Find all UnProcessed Failed/Returned/Cancelled/ProvSuccess 
		//Step 3 : Loop through data fetched in Step2 and add to 1st sheet only those not present in data of Step1 and add the skipped data to Sheet2
		//Step 4 : Created 2 Sheets - 1st Sheet : New Modification Request and 2nd Sheet : Already Created requests
		DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
    	List<ObligationDetailBean> lObligationDetails = null;
    	ObligationModificationRequestBean lRequestBean = null;
    	Set<String> lHash = new HashSet<>();
    	Object[] lRowData = null;
    	Object[] lAlreadyCrtModReqRowData = null;
        //find already created modification requests for the same pfid
    	List<ObligationModificationRequestBean> lRequestList = getModificationRequestList(pConnection, pPfId);
    	Map<String,ObligationModificationRequestBean> lModReqHash = new HashMap<>();
    	String lKeyStr = null;
    	if (lRequestList.size()>0 && !lRequestList.isEmpty()){
    		for(ObligationModificationRequestBean lOMRBean : lRequestList ){
    			//key = fuid^type^partno
    			lKeyStr = lOMRBean.getFuId()+CommonConstants.KEY_SEPARATOR+lOMRBean.getType()+CommonConstants.KEY_SEPARATOR+lOMRBean.getPartNumber();
    			if(!lModReqHash.containsKey(lKeyStr)){
    				lModReqHash.put(lKeyStr,lOMRBean);
    			}
    		}
    	}
        lSql.append("SELECT * FROM Obligations ");
        lSql.append(" , ObligationSplits ");
        lSql.append(" WHERE ");
        lSql.append(" OBSOBID = OBID ");
        lSql.append(" AND OBPFID = ").append(pPfId); 
        lSql.append(" AND OBSPFID = ").append(pPfId);
        lSql.append(" AND OBSSETTLORPROCESSED IS NULL ");
        lSql.append(" AND OBSTATUS = ").append(lDBHelper.formatString(ObligationBean.Status.Sent.getCode())); //parent staus sent 
		lSql.append(" AND OBSSTATUS IN ( "); //chid status - Failed+
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Prov_Success.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Returned.getCode())).append(" , ");
        lSql.append(lDBHelper.formatString(ObligationBean.Status.Cancelled.getCode())).append(" ) "); 
        lObligationDetails =  obligationDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
        ObligationBean lObligationBean = null;
        ObligationSplitsBean lObligationSplitsBean = null;
        Object[] lHeaders = new Object[]{"Factoring Unit Id","Leg","PartNo","Obligation Id","Txn Type","Original Date","Original Status","Original Amount","Original Settlor","Revised Date","Revised Status","Revised Amount","Revised Settlor","Rate","Date diff","Interest","Calculated Amount","Payment Ref No","Remarks"};
        Object[] lModReqHeaders = new Object[]{"FuId","Leg","PartNo","Status"};
        int lCount = 1;
        int lReqCount = 1;
        Map<Long,Object[]> lDataHash = new HashMap<>();
        Map<Long,Object[]> lAlreadyCreatedModHash = new HashMap<>();
        lDataHash.put(new Long(0), lHeaders);
        lAlreadyCreatedModHash.put(new Long(0),lModReqHeaders);
        Map<Long,BigDecimal> lFuWiseRateMap = new HashMap<>();
        for(ObligationDetailBean lObligationDetailBean : lObligationDetails){
        	lObligationBean = lObligationDetailBean.getObligationBean();
        	lObligationSplitsBean = lObligationDetailBean.getObligationSplitsBean();
        	// skip all obligations whose requests are already created.
        	if (!lFuWiseRateMap.containsKey(lObligationBean.getFuId())){
        		lFuWiseRateMap.put(lObligationBean.getFuId(), TredsHelper.getInstance().getAcceptedRate(pConnection, lObligationBean.getFuId()));
        	}
        	lKeyStr = lObligationBean.getFuId()+CommonConstants.KEY_SEPARATOR+lObligationBean.getType()+CommonConstants.KEY_SEPARATOR+lObligationSplitsBean.getPartNumber();
        	if (lModReqHash.containsKey(lKeyStr)){
        		lRequestBean = lModReqHash.get(lKeyStr);
        		if (!lHash.contains(lKeyStr)){
        			lHash.add(lKeyStr);
        			lAlreadyCrtModReqRowData = new Object[4];
        			lAlreadyCrtModReqRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_FUID] = lRequestBean.getFuId();
        			lAlreadyCrtModReqRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_TYPE] = lRequestBean.getType().toString();
        			lAlreadyCrtModReqRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_PARTNO] = lRequestBean.getPartNumber();
        			lAlreadyCrtModReqRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_STATUS] = lRequestBean.getStatus().toString();
        			lAlreadyCreatedModHash.put(new Long(lReqCount), lAlreadyCrtModReqRowData);
        			lReqCount++;
        		}
        		continue;
        	}
        	lRowData = new Object[17];
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_FUID] = lObligationBean.getFuId();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_TYPE] = lObligationBean.getType().toString();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_PARTNO] = lObligationSplitsBean.getPartNumber();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_OBID] = lObligationSplitsBean.getObid();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_TXNTYPE] = lObligationBean.getTxnType().toString();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGDATE] = lObligationBean.getDate();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGSTATUS] = lObligationSplitsBean.getStatus().toString();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGAMOUNT] = lObligationSplitsBean.getAmount();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGSETTLOR] = lObligationSplitsBean.getPaymentSettlor();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVDATE] = lObligationBean.getDate();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVSTATUS] = lObligationSplitsBean.getStatus().toString();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVAMOUNT] = lObligationSplitsBean.getAmount();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVSETTLOR] = lObligationSplitsBean.getPaymentSettlor();
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_RATE] = lFuWiseRateMap.get(lObligationBean.getFuId());
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_DAYDIFF] = "";
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_INTEREST] = "";
        	lRowData[ObligationModUploader.OBLI_MOD_REQ_EXCELL_CALCULATEDREVISEDAMOUNT] = "";
        	lDataHash.put(new Long(lCount),lRowData );
        	lCount++;
        }
        Object[] lWorkBookData = new Object[]{lDataHash,lAlreadyCreatedModHash};
        // Blank workbook 
        XSSFWorkbook lWorkbook = new XSSFWorkbook(); 
        // Create a blank sheet 
        XSSFSheet lSheet1 = lWorkbook.createSheet("Obligation Details");
        XSSFSheet lSheet2 = lWorkbook.createSheet("alreadyCreated Request");
        XSSFSheet[] lSheetArr = new XSSFSheet[]{lSheet1,lSheet2};
        DataFormat lDataFormat = lWorkbook.createDataFormat();
        CellStyle lDateCellStyle = lWorkbook.createCellStyle();
        lDateCellStyle.setDataFormat(lDataFormat.getFormat("dd-mm-yyyy"));
        CellStyle lNumCellStyle = lWorkbook.createCellStyle();
        lNumCellStyle.setDataFormat(lDataFormat.getFormat("##0"));
        for (int i=0; i<lWorkBookData.length; i++ ){
   	        String[] lCells = new String[] {};
        	 for (Long lKey : ((Map<Long, Object[]>) lWorkBookData[i]).keySet()) { 
                 Row row = lSheetArr[i].createRow(lKey.intValue());
                 lSheetArr[i].autoSizeColumn(1000000000);
                 Object[] lObjArr = (Object[]) ((Map<Long, Object[]>) lWorkBookData[i]).get(lKey);
                 int lCellnum = 0; 
                 lCells = new String[6];
                 for (Object lData : lObjArr) { 
                     Cell lCell = row.createCell(lCellnum++);
                     if (i == 0) {
                    	 if(row.getRowNum()>0) {
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGAMOUNT+1)) {
                    		 lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGAMOUNT_ADDRESS] = lCell.getAddress().formatAsString();
                    	 }
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGDATE+1)) {
                    		 lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGDATE_ADDRESS] = lCell.getAddress().formatAsString();
                    	 }
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVDATE+1)) {
                    		 lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVDATE_ADDRESS] = lCell.getAddress().formatAsString();
                    	 }
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_RATE+1)) {
                    		 lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_RATE_ADDRESS] = lCell.getAddress().formatAsString();
                    	 }
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_DAYDIFF+1)) {
                    		 lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_DATEDIFF_ADDRESS] = lCell.getAddress().formatAsString();
                    	 }
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_INTEREST+1)) {
                    		 lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_INTEREST_ADDRESS] = lCell.getAddress().formatAsString();
                    	 }
                    	 if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_DAYDIFF+1)) {
                    		 lCell.setCellFormula("DATEDIF("+lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGDATE_ADDRESS]+","+lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_REVDATE_ADDRESS]+",\"d\")");
                    		 continue;
                    	 }else  if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_INTEREST+1)) {
                    		 //IPMT(B2,1,1,-B1)
                    		 lCell.setCellFormula("IPMT("+lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_RATE_ADDRESS]+"/365"+",1,1,-"+lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_ORIGAMOUNT_ADDRESS]+")");
                    		 continue;
                    	 }else  if (lCellnum == (ObligationModUploader.OBLI_MOD_REQ_EXCELL_CALCULATEDREVISEDAMOUNT+1)) {
                    		 lCell.setCellFormula("PRODUCT("+lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_DATEDIFF_ADDRESS]+","+lCells[ObligationModUploader.OBLI_MOD_REQ_EXCELL_INTEREST_ADDRESS]+")");
                    		 continue;
                    	 }
                    	 }
                     }
                     if (lData instanceof String) {
                         lCell.setCellValue((String)lData);
         	        } else if (lData instanceof BigDecimal) {
         	            lCell.setCellValue(((BigDecimal)lData).doubleValue());
         	            lCell.setCellStyle(lNumCellStyle);
                     } else if(lData instanceof Integer) {
                     	lCell.setCellValue(((Integer) lData).intValue());
                     	lCell.setCellStyle(lNumCellStyle);
                     } else if(lData instanceof Double) {
                     	lCell.setCellValue(((Double) lData).doubleValue());
                     	lCell.setCellStyle(lNumCellStyle);
                     } else if(lData instanceof Float) {
                     	lCell.setCellValue(((Float) lData).doubleValue());
                     	lCell.setCellStyle(lNumCellStyle);
                     } else if(lData instanceof Long) {
                     	lCell.setCellValue(((Long) lData).longValue());
                     	lCell.setCellStyle(lNumCellStyle);
                     } else if (lData instanceof Date) {
                     	lCell.setCellValue((Date)lData);
                     	lCell.setCellStyle(lDateCellStyle);
                     }
                 } 
             } 
        }
        ByteArrayOutputStream lByetOutputStream = new ByteArrayOutputStream();
    	try {
    		lWorkbook.write(lByetOutputStream);
    		lByetOutputStream.close();
    		return lByetOutputStream.toByteArray();
    	}catch(Exception e){
    		
    	}
		return null;
	}
	
}
