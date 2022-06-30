package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants;
import com.xlx.treds.MonetagoTredsHelper;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bo.PurchaserSupplierLimitUtilizationBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class DirectPaymentSettlor implements IPaymentSettlor {
    private static final Logger logger = LoggerFactory.getLogger(DirectPaymentSettlor.class);
	//
	private PaymentFileBean paymentFileBean;
	//
	//Key1=FUID  Key2=ObligationType(L1/L2)   Key3=EntityType(B/S/T)  Key4=UniqueObliKey(ObId when No Split/ "ObId-PartNo" when split
	private HashMap<String, HashMap<ObligationBean.Type, HashMap<EntityType, HashMap<String,IObligation>>>> data = null; //Key1=FUId, Key2=Leg
	//this is the data (transaction type ) which does not belong to the Payment transaction in question ie. Debit/Credit
	private List<IObligation> dataSkipped = null; 
	//
	//Helper variables
	//temp entity list for determining type
	private HashMap<String, DirectPaymentSettlor.EntityType> appEntitiesType = null;	//Key=EntityCode/Domain
    private GenericDAO<ObligationBean> obligationDAO;
    private CompositeGenericDAO<ObligationDetailBean> obligationDetailDAO;
    private CompositeGenericDAO<ObligationModiReqDetailBean> obligationModiReqDetailDAO;
    private Connection connection = null;
    private List<Long> currentUnprocessedList = null; 
    private List<Long> completeObligationsProcessedList = null; 
    private List<String> keyForModificationRequest = null;
    private String[] currentUnprocessedListStr = null; 
    private String[] completeObligationsProcessedListStr = null; 
    private String remarks = null;
	//
	//
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private PurchaserSupplierLimitUtilizationBO purchaserSupplierLimitUtilizationBO;
    private GenericDAO<PaymentFileBean> paymentFileDAO;
    private CompositeGenericDAO<AssignmentNoticeInfo> assignmentNoticeInfoDAO;
    private GenericDAO<AssignmentNoticesBean> assignmentNoticesDAO;
    private GenericDAO<AssignmentNoticeDetailsBean> assignmentNoticeDetailsDAO;
    private GenericDAO<AssignmentNoticeGroupDetailsBean> assignmentNoticeGroupDetailsDAO;
    private GenericDAO<ObligationModificationRequestBean> obligationModificationRequestDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
    //
    
	//FU STATUS UPDATE

	private static int IDX_RESULT_SIZE = 5;
	//
	//FU STATUS UPDATE
	//Leg 1 Success	Seller Credit Leg1 is success and Old Status is Factored
	//Leg 1 Fail	Seller Credit Leg1 is Failed or Cancelled and Old Status is Factored
	//Leg 1 Fail	(Financier Debit)/(Buyer Debit) Leg1 is Failed or Cancelled and Old Status is Factored
	//Leg 2 Success	Buyer Debit Leg2 is success and Old Status is Leg1 Success
	//Leg 2 Fail	Buyer Debit Leg2 is Failed and Old Status is Leg1 Success
	private static String[] resultText = new String[] {"Leg1 Success", "Leg1 Fail", "Leg1 Fail", "Leg2 Success", "Leg2 Fail"};
	private static String[] resultOldFUStatus = new String[] {FactoringUnitBean.Status.Factored.getCode(), FactoringUnitBean.Status.Factored.getCode(), FactoringUnitBean.Status.Factored.getCode(), FactoringUnitBean.Status.Leg_1_Settled.getCode(), FactoringUnitBean.Status.Leg_1_Settled.getCode() };
	private static String[] resultNewFUStatus = new String[] {FactoringUnitBean.Status.Leg_1_Settled.getCode(), FactoringUnitBean.Status.Leg_1_Failed.getCode(), FactoringUnitBean.Status.Leg_1_Failed.getCode(), FactoringUnitBean.Status.Leg_2_Settled.getCode(), FactoringUnitBean.Status.Leg_2_Failed.getCode() };
	private static String[] resultObliType = new String[] {ObligationBean.Type.Leg_1.getCode(), ObligationBean.Type.Leg_1.getCode(), ObligationBean.Type.Leg_1.getCode(), ObligationBean.Type.Leg_2.getCode(), ObligationBean.Type.Leg_2.getCode()};
	private static String[] resultObliTxnType = new String[] {ObligationBean.TxnType.Credit.getCode(), ObligationBean.TxnType.Credit.getCode(), ObligationBean.TxnType.Debit.getCode(), ObligationBean.TxnType.Debit.getCode(), ObligationBean.TxnType.Debit.getCode()};
	private static String[] resultObliStatus = new String[] {ObligationBean.Status.Success.getCode(), ObligationBean.Status.Failed.getCode(), ObligationBean.Status.Failed.getCode(), ObligationBean.Status.Success.getCode(), ObligationBean.Status.Failed.getCode()};
	//
	private static String[] resultOldInstStatus = new String[] {InstrumentBean.Status.Factored.getCode(), InstrumentBean.Status.Factored.getCode(), InstrumentBean.Status.Factored.getCode(), InstrumentBean.Status.Leg_1_Settled.getCode(), InstrumentBean.Status.Leg_1_Settled.getCode() };
	private static String[] resultNewInstStatus = new String[] {InstrumentBean.Status.Leg_1_Settled.getCode(), InstrumentBean.Status.Leg_1_Failed.getCode(), InstrumentBean.Status.Leg_1_Failed.getCode(), InstrumentBean.Status.Leg_2_Settled.getCode(), InstrumentBean.Status.Leg_2_Failed.getCode() };
    //
    public static enum EntityType implements IKeyValEnumInterface<String> {
        Treds("T"), Financier("F"), Buyer("B"), Seller("S");
        private final String code;
        private EntityType(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }
	//
    public Map<Long,Map<String,List<IObligation>>> lObligationMap = new HashMap<Long,Map<String,List<IObligation>>>() ;
    //
	public DirectPaymentSettlor(PaymentFileBean pPaymentFileBean) throws Exception{
		paymentFileBean = pPaymentFileBean;
		//
		data = new HashMap<String,  HashMap<ObligationBean.Type, HashMap<EntityType, HashMap<String, IObligation>>>>();
		dataSkipped = new ArrayList<IObligation>();
		//
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        appEntitiesType = new HashMap<String, DirectPaymentSettlor.EntityType>();
        currentUnprocessedList = new ArrayList<Long>();
        keyForModificationRequest = new ArrayList<String>();
        //
        purchaserSupplierLimitUtilizationBO = new PurchaserSupplierLimitUtilizationBO();
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
        assignmentNoticesDAO = new GenericDAO<AssignmentNoticesBean>(AssignmentNoticesBean.class);
        assignmentNoticeDetailsDAO = new GenericDAO<AssignmentNoticeDetailsBean>(AssignmentNoticeDetailsBean.class);
        assignmentNoticeGroupDetailsDAO = new GenericDAO<AssignmentNoticeGroupDetailsBean>(AssignmentNoticeGroupDetailsBean.class);
        assignmentNoticeInfoDAO = new CompositeGenericDAO<AssignmentNoticeInfo>(AssignmentNoticeInfo.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        obligationDetailDAO = new CompositeGenericDAO<ObligationDetailBean>(ObligationDetailBean.class);
        obligationModiReqDetailDAO = new CompositeGenericDAO<ObligationModiReqDetailBean>(ObligationModiReqDetailBean.class);
        obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        obligationModificationRequestDAO = new GenericDAO<ObligationModificationRequestBean>(ObligationModificationRequestBean.class);
	}
	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#getRemarks()
	 */
	@Override
	public String getRemarks(){
		return remarks;
	}
	
	@Override
	public List<IObligation> getUnProcessedObligations(){
		List<IObligation> lObligations = new ArrayList<IObligation>();
		DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        String lKey = null;
        //		
        //select * from ObligationSplits, Obligations where !Processed and Status != Sent (if Ready then remove pfid)
        lSql.append(" SELECT * FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE  OBSOBID = OBID   ");
        lSql.append(" AND OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0");
        lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
        lSql.append(" AND OBSSTATUS != ").append(lDBHelper.formatString(ObligationBean.Status.Sent.getCode()));
        lSql.append(" AND ( OBSSETTLORPROCESSED IS NULL ");
        lSql.append(" OR OBSSETTLORPROCESSED != ").append(lDBHelper.formatString(Yes.Yes.getCode())).append(" ) ");
        try {
			lObligationDetails =  obligationDetailDAO.findListFromSql(connection, lSql.toString(), -1);
			IObligation lSplitsBean = null;
			for (ObligationDetailBean lBean : lObligationDetails){
				lSplitsBean = lBean.getObligationSplitsBean();
				lSplitsBean.setParentObligation(lBean.getObligationBean());
				//TODO: Ready is not added since we have to think about it
				if(Status.Ready.equals(lSplitsBean.getStatus())){
					
				}else{
					addObligation(lSplitsBean);
					lObligations.add(lSplitsBean);
					lKey = lSplitsBean.getParentObligation().getFuId()+CommonConstants.KEY_SEPARATOR+lSplitsBean.getParentObligation().getType().getCode()+CommonConstants.KEY_SEPARATOR+lSplitsBean.getPartNumber();
					if (!keyForModificationRequest.contains(lKey)){
						keyForModificationRequest.add(lKey);
					}
				}
			}
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
		return lObligations;
	}

	public List<IObligation> getAllObligations(){
		List<IObligation> lObligations = new ArrayList<IObligation>();
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        //		
        lSql.append(" SELECT * FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE ");
        lSql.append(" OBSOBID = OBID "); 
        lSql.append(" AND OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0");
        lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
        try {
			lObligationDetails =  obligationDetailDAO.findListFromSql(connection, lSql.toString(), -1);
			IObligation lSplitsBean = null;
			for (ObligationDetailBean lBean : lObligationDetails){
				lSplitsBean = lBean.getObligationSplitsBean();
				lSplitsBean.setParentObligation(lBean.getObligationBean());
				lObligations.add(lSplitsBean);
			}
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
		return lObligations;
	}

	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#addObligation(com.xlx.treds.auction.bean.IObligation)
	 */
	@Override
	public void addObligation(IObligation pObligation){
		String lFuid =null;
		if(pObligation !=null){
			HashMap<ObligationBean.Type, HashMap<EntityType,HashMap<String,IObligation>>> lLegwiseEntityObligations = null;
			HashMap<EntityType,HashMap<String, IObligation>> lEntityTypewiseObligations = null;
			DirectPaymentSettlor.EntityType lEntityType = null;
			HashMap<String, IObligation> lPartwiseObligations = null;
			lEntityType = getEntityType(pObligation.getTxnEntity());
			if(lEntityType!=null){
				lFuid= pObligation.getFuId().toString();
				if(data.containsKey(lFuid)){
					lLegwiseEntityObligations = data.get(pObligation.getFuId().toString());
				}else{
					lLegwiseEntityObligations = new HashMap<ObligationBean.Type, HashMap<EntityType, HashMap<String,IObligation>>>();
					data.put(lFuid, lLegwiseEntityObligations);
				}
				if(lLegwiseEntityObligations.containsKey(pObligation.getType())){
					lEntityTypewiseObligations = lLegwiseEntityObligations.get(pObligation.getType());
				}else{
					lEntityTypewiseObligations = new HashMap<EntityType, HashMap<String,IObligation>>();
					lLegwiseEntityObligations.put(pObligation.getType(), lEntityTypewiseObligations);
				}
				if(lEntityTypewiseObligations.containsKey(lEntityType)){
					lPartwiseObligations = lEntityTypewiseObligations.get(lEntityType);
				}else{
					lPartwiseObligations = new HashMap<String, IObligation>();
					lEntityTypewiseObligations.put(lEntityType, lPartwiseObligations);
				}
				if(!lPartwiseObligations.containsKey(pObligation.getUniqueObligationKey())){
					lPartwiseObligations.put(pObligation.getUniqueObligationKey(), pObligation);
					if (!currentUnprocessedList.contains(pObligation.getId())){
						currentUnprocessedList.add(pObligation.getId());
					}
				}else{
					dataSkipped.add(pObligation); //duplicate???? - not possible
				}
			}else{
				dataSkipped.add(pObligation); //entity type null - not possible
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#process(java.sql.Connection)
	 */
	@Override
	public void process(Connection pConnection) throws Exception{
		connection = pConnection;
		//
		List<IObligation> lUnProcessedList = getUnProcessedObligations();
		//
		//
		Map<String, ObligationModificationRequestBean> lModifiactionRequestHash = getApprovedObligationModificationRequests();
		//
		currentUnprocessedListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(currentUnprocessedList);
		//
		updateSplitsFromModRequests(lUnProcessedList,lModifiactionRequestHash);
		//
		markUnprocessedSplitsAsProcessed(lUnProcessedList); 
		
		updateParentObligaitons();
		completeObligationsProcessedListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(completeObligationsProcessedList);
		if (completeObligationsProcessedList!=null && completeObligationsProcessedList.size()>0){
			 //
			 markLeg2SplitDebitReadyCancel();
			 markLeg2ParentsReadyCancel();
			 //
			 updateFUUtilPostL1();
			 updateFUUtilPostL2();
			 updatePurSupUtilPostL1();
			 updatePurSupUtilPostL2();
				
			 //
		     updateFactoringUnitStatus();
			 updateInstrumentStatus();
			 updateMonetagoTransStatus();

			 updateTunoverL1();
		    //updateTunoverL2(); //NOT TO DO 
		    
		    
		}
		updatePaymentFileStatus();
	    generateNoticeOfAssignment(pConnection, paymentFileBean.getId(), paymentFileBean.getDate());
	    //
	    sendL1DetailsToClient();
	}
	
	private void updateTunoverL1() throws Exception{
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		Date lFYEndDate = null , lFYStartDate = null;
		Date[] lTmpDates = TredsHelper.getInstance().getFinYearDates(TredsHelper.getInstance().getBusinessDate());
		lFYStartDate = lTmpDates[0];
		lFYEndDate = lTmpDates[1];
		
		///test_TURNOVER();
		
		lSql.append(" MERGE INTO MEMBERTURNOVER ");

		//
		lSql.append(" USING ( ");
		lSql.append(" SELECT FUPurchaser Purchaser ");
		lSql.append(" , SUM((FUAmount * (OBAmount - NVL(OBSettledAmount,0)))/OBAmount) FailAmt ");
		//lSql.append(" , Sum(MTTurnOver) TotalTurnOver ");
		lSql.append(" FROM Obligations, FactoringUnits ");
		lSql.append(" LEFT OUTER JOIN MEMBERTURNOVER ");
		lSql.append(" ON ( ");
		lSql.append(" MTCODE = FUPurchaser  ");
		lSql.append(" AND MTFINYEARSTARTDATE = ").append(lDBHelper.formatDate(lFYStartDate));
		lSql.append(" AND MTFINYEARENDDATE = ").append(lDBHelper.formatDate(lFYEndDate));
		lSql.append(" ) ");
		lSql.append(" WHERE OBFuId = FUId ");
		lSql.append(" AND FURecordVersion > 0 ");
		lSql.append(" AND OBRecordVersion > 0 ");
		lSql.append(" AND OBPFId = ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBID", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" AND OBTXNENTITY = FUFINANCIER ");
		lSql.append(" GROUP BY FUPurchaser ");
		lSql.append(" ) CalAmt ");
		lSql.append(" ON  ");
		lSql.append(" ( ");
		lSql.append(" MTCode = Purchaser  ");
		lSql.append(" AND MTFINYEARSTARTDATE = ").append(lDBHelper.formatDate(lFYStartDate));
		lSql.append(" AND MTFINYEARENDDATE = ").append(lDBHelper.formatDate(lFYEndDate));
		lSql.append(" ) ");
		lSql.append(" WHEN MATCHED THEN ");
		lSql.append(" UPDATE SET MTTurnover = MTTurnover - FailAmt ");
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		try {
			lStatement = connection.createStatement();
		    int lCount = lStatement.executeUpdate(lSql.toString());
		    logger.info("********** Updated Turnover : " + lCount);
		}catch(Exception lEx){  
		    logger.info("********** Updated Turnover : ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
		}
		///test_TURNOVER();
	}

	private void updateFactoringUnitStatus() throws Exception{
		//Leg 1 Success	Seller Credit Leg1 is success and Old Status is Factored
		//Leg 1 Fail	Seller Credit Leg1 is Failed or Cancelled and Old Status is Factored
		//Leg 2 Success	Buyer Debit Leg2 is success and Old Status is Leg1 Success
		//Leg 2 Fail	Buyer Debit Leg2 is Failed and Old Status is Leg1 Success
		// update fu set fustatu=newstatus where furecorv>0 and fuoldstatus=<oldstatus> and fuid in (select obfuid from obligations where obpfid=<pfid> and <abovecondition>
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		int lUpdateCount = 0;
		logger.info("********** Update Factoring Unit Status.");
		for(int lPtr=0; lPtr < IDX_RESULT_SIZE; lPtr++){
			lSql.delete(0, lSql.length());
			lSql.append("UPDATE FACTORINGUNITS ");
			lSql.append(" SET FUSTATUS = ").append(lDBHelper.formatString(resultNewFUStatus[lPtr])); // New Status
			lSql.append(" WHERE FURECORDVERSION > 0 ");
			lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(resultOldFUStatus[lPtr])); // Old Status
			lSql.append(" AND FUID IN ( ");
			lSql.append(" SELECT DISTINCT OBFUID FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
			lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
			lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", completeObligationsProcessedListStr)).append("  ");
			lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(resultObliType[lPtr]));
			lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(resultObliTxnType[lPtr]));
			lSql.append(" AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
			lSql.append(" AND OBSTATUS = ").append(lDBHelper.formatString(resultObliStatus[lPtr]));
			lSql.append("  AND OBISUPFRONTOBLIG IS NULL ");
			lSql.append(" ) ");
			
			logger.info("********** " + resultText[lPtr] + " : " + lSql.toString());
	        Statement lStatement = null;
	        try {
	            lStatement = connection.createStatement();
	            lUpdateCount = lStatement.executeUpdate(lSql.toString());
				logger.info("********** " + resultText[lPtr] + " : " + lUpdateCount);
			}catch(Exception lEx){  
			    logger.info("********** resultText[lPtr] : ERROR : " + lEx.getMessage());
			}finally {
	            if (lStatement != null)
	            	lStatement.close();
	        }
		}
	}

	private void updateInstrumentStatus() throws Exception{
		// update instruments set idstatus=<thestatus> where idstatus!=<thestatus> and infuid in (select fuid from factoringunits where furecordvderion>0 and fustatus=<thestatus> and fuid in (select obfuid from obligations where obpfid = <this>))
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		int lUpdateCount = 0;
		logger.info("********** Update Instrument Status.");
		for(int lPtr=0; lPtr < IDX_RESULT_SIZE; lPtr++){
			lSql.delete(0, lSql.length());
			lSql.append("UPDATE INSTRUMENTS ");
			lSql.append(" SET INSTATUS = ").append(lDBHelper.formatString(resultNewInstStatus[lPtr])); // New Status
			lSql.append(" WHERE INRECORDVERSION > 0 ");
			lSql.append(" AND INSTATUS = ").append(lDBHelper.formatString(resultOldInstStatus[lPtr])); // Old Status
			lSql.append(" AND INFUID IN ( ");
			lSql.append(" SELECT FUID FROM FACTORINGUNITS WHERE FURECORDVERSION > 0 ");
			lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(resultNewFUStatus[lPtr])); // New Status
			lSql.append(" AND FUID IN ( ");
			lSql.append("  SELECT DISTINCT OBFUID FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
			lSql.append("  AND OBPFID = ").append(paymentFileBean.getId());
			lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", completeObligationsProcessedListStr)).append("  ");
			lSql.append("  AND OBTYPE = ").append(lDBHelper.formatString(resultObliType[lPtr]));
			lSql.append("  AND OBTXNTYPE = ").append(lDBHelper.formatString(resultObliTxnType[lPtr]));
			lSql.append("  AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
			lSql.append("  AND OBSTATUS = ").append(lDBHelper.formatString(resultObliStatus[lPtr]));
			lSql.append("  AND OBISUPFRONTOBLIG IS NULL ");
			lSql.append(" ) ");
			lSql.append(" ) ");
			
			logger.info("********** " + resultText[lPtr] + " : " + lSql.toString());
	        Statement lStatement = null;
	        try {
	            lStatement = connection.createStatement();
	            lUpdateCount = lStatement.executeUpdate(lSql.toString());
				logger.info("********** " + resultText[lPtr] + " : " + lUpdateCount);
			}catch(Exception lEx){  
			    logger.info("********** " + resultText[lPtr] + " : ERROR : " + lEx.getMessage());
			}finally {
	            if (lStatement != null)
	            	lStatement.close();
	        }
		}
	}
	
	private void updateMonetagoTransStatus() throws Exception{
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		List<Long> lGroupInIds = new ArrayList<Long>();
		logger.info("********** Update Monetago Status.");
		if(MonetagoTredsHelper.getInstance().performMonetagoCheck()){
			lSql.append("Select * FROM INSTRUMENTS ");
			lSql.append(" WHERE INRECORDVERSION > 0 ");
			lSql.append(" AND INSTATUS = ").append(lDBHelper.formatString(InstrumentBean.Status.Leg_1_Failed.getCode())); // Old Status
			lSql.append(" AND INFUID IN ( ");
			lSql.append(" SELECT FUID FROM FACTORINGUNITS WHERE FURECORDVERSION > 0 ");
			lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_1_Failed.getCode())); // New Status
			lSql.append(" AND FUID IN ( ");
			lSql.append("  SELECT DISTINCT OBFUID FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
			lSql.append("  AND OBPFID = ").append(paymentFileBean.getId());
				lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", currentUnprocessedListStr)).append("  ");
			lSql.append("  AND OBTYPE = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
			lSql.append("  AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
			lSql.append("  AND OBSTATUS = ").append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode()));
			lSql.append("  AND OBISUPFRONTOBLIG IS NULL ");
			lSql.append(" ) ");
			lSql.append(" ) ");
			List<InstrumentBean> lInstrumentList = instrumentDAO.findListFromSql(connection, lSql.toString(), 0);
			boolean lCancellationFailed = false;
	        for(InstrumentBean lInstrumentBean:lInstrumentList){
	        	if(CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
	        		lGroupInIds.add(lInstrumentBean.getId());
	        	}else{
        			if(StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId())){
        				try
        				{
        					cancelMonetago(lInstrumentBean);
        				}catch(Exception lException){
        					lCancellationFailed = true;
        					logger.error("Error while cancelling : " + lException.getMessage());
        				}
        	        }
	        	}
	        }
			//
	        if(lGroupInIds.size() > 0){
				lSql.delete(0, lSql.length());
				lSql.append("Select * FROM INSTRUMENTS WHERE INRecordVersion > 0 ");
				lSql.append(" AND INID IN ( " ).append(TredsHelper.getInstance().getCSVIdsForInQuery(lGroupInIds) ).append(" ) ");
				lInstrumentList = instrumentDAO.findListFromSql(connection, lSql.toString(), 0);
		        for(InstrumentBean lInstrumentBean:lInstrumentList){
        			if(StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId())){
        				try
        				{
        					cancelMonetago(lInstrumentBean);
        				}catch(Exception lException){
        					lCancellationFailed = true;
        					logger.error("Error while cancelling : " + lException.getMessage());
        				}
        	        }
		        }
			}
	        if(lCancellationFailed){
	        	remarks = "Failure in cancellation of some instruments at monetago.";
	        }
		}
	}
	
	private void cancelMonetago(InstrumentBean pInstrumentBean) throws  Exception{
		String lInfoMessage = null;
		Map<String,String> lResult= new HashMap<String, String>();
 		lInfoMessage = "Instrument No :"+pInstrumentBean.getId() +" " + "FactoringUnit No :"+pInstrumentBean.getFuId();
		lResult=MonetagoTredsHelper.getInstance().cancel(pInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.NotFinanced,lInfoMessage,pInstrumentBean.getId());
		if(StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))){
				pInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
				//lInstrumentBean.setMonetagoLedgerId("");
				logger.info("Message Success :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
		}else{
			logger.info("Message Error :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
			throw new CommonBusinessException("Error while Settlement : " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
		}
		instrumentDAO.update(connection, pInstrumentBean,InstrumentBean.FIELDGROUP_UPDATEMONETAGOCANCEL);
	}
	
	//The paymentFileId or the Leg1SettlmentDate
	//If the paymentFileId is received - find the Leg1 Success obligation from the same
	//If the paymentFileId is NOT received - the Leg1SettlementDate is used to find the paymentFileId for that day and 
	//the obligations of all the paymentFileIds for that day are used	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#generateNoticeOfAssignment(java.sql.Connection, java.lang.Long, java.sql.Date)
	 */
	@Override
	public void generateNoticeOfAssignment(Connection pConnection, Long pPaymentFileId, Date pLeg1SettledDate) {
		StringBuilder lSql = null;
		DBHelper lDBHelper = DBHelper.getInstance();
		//if error then do not throw the error - just log.
		try{
			//with the paymentfileId find the FUs from obligations with leg1 success 
			//loop throught the FUs
			//Maintain Hash key=BusDate^Pur^Sup^Fin  value=AssignmentNotices
			//if not in Hash - findAssNotice() AssignmentNotices
			//
			if(pPaymentFileId == null || pPaymentFileId.longValue() <= 0){
				logger.info("Payment file Id required.");
				return;
			}
			logger.info("Payment file Id : " + pPaymentFileId);
			if(pPaymentFileId != null ){
				lSql = new StringBuilder();
	            // AssignmentNoticeDetails
				lSql.append(" SELECT ");
	        	lSql.append(" ASSND.ANDANID ANDANID, ");
	        	lSql.append(" OBFUID ANDFUID, ");
	        	lSql.append(" INPARENT.INID ANDINID, ");
	        	lSql.append(" FUFACTORSTARTDATETIME	ANDFUDATE, ");
	        	lSql.append(" CASE FUENABLEEXTENSION WHEN ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode())).append(" THEN FUExtendedDueDate ELSE FUMaturityDate END  ANDFUDUEDATE, ");
	        	lSql.append(" FUFACTOREDAMOUNT	ANDFUFACTOREDAMOUNT, ");
	        	lSql.append(" INPARENT.ININSTNUMBER	ANDINSTNUMBER, ");
	        	lSql.append(" INPARENT.ININSTDATE	ANDINSTDATE, ");
	        	lSql.append(" INPARENT.INAMOUNT	ANDINSTAMOUNT, ");
	        	lSql.append(" INPARENT.INCURRENCY	ANDCURRENCY, ");
	        	lSql.append(" ASSND.ANDRecordCreateTime ANDRecordCreateTime, ");
	        	// AssignmentNoticeGroupDetails
	        	lSql.append(" ASSNGD.ANGANID ANGANID, ");
	        	lSql.append(" OBFUID ANGFUID, ");
	        	lSql.append(" INPARENT.INID	ANGGROUPINID, ");
	        	lSql.append(" INCHILD.INID	ANGCHILDINID, ");
	        	lSql.append(" INCHILD.INNETAMOUNT	ANGNETAMOUNT, ");
	        	lSql.append(" INCHILD.ININSTNUMBER	ANGINSTNUMBER, ");
	        	lSql.append(" INCHILD.ININSTDATE	ANGINSTDATE, ");
	        	lSql.append(" INCHILD.INAMOUNT	ANGINSTAMOUNT, ");
	        	lSql.append(" INCHILD.INCURRENCY	ANGCURRENCY, ");
	        	lSql.append(" ASSNGD.ANGRecordCreateTime	ANGRECORDCREATETIME, ");
	        	// AssignmentNotices
	        	lSql.append(" ASSN.ANID ANID, ");
	        	lSql.append(" FUPURCHASER  ANPURCHASER," );
	        	lSql.append(" FUSUPPLIER  ANSUPPLIER," );
	        	lSql.append(" FUFINANCIER  ANFINANCIER," );
	        	lSql.append(" ASSN.ANBUSINESSDATE ANBUSINESSDATE," ); //DATE
	        	lSql.append(" OBPAYDETAIL1  ANFINACCNO," ); //1=account no
	        	lSql.append(" OBPAYDETAIL2  ANFINIFSC," ); //2=ifsc
	        	lSql.append(" ASSN.ANFINBRANCHNAME	ANFINBANKNAME," ); //3=financier name XXXXXX 
	        	lSql.append(" BBDBRANCHNAME	ANFINBRANCHNAME," ); //BRANCH NAME
	        	lSql.append(" SUP.CDCOMPANYNAME  ANSUPNAME," );
	        	lSql.append(" PUR.CDCOMPANYNAME  ANPURNAME," );
	        	lSql.append(" FIN.CDCOMPANYNAME  ANFINNAME," );
	        	lSql.append(" SUPL.CLLINE1  ANSUPLINE1," );
	        	lSql.append(" SUPL.CLLINE2  ANSUPLINE2," );
	        	lSql.append(" SUPL.CLLINE3  ANSUPLINE3," );
	        	lSql.append(" SUPL.CLCOUNTRY  ANSUPCOUNTRY," );
	        	lSql.append(" SUPL.CLSTATE  ANSUPSTATE," );
	        	lSql.append(" SUPL.CLDISTRICT  ANSUPDISTRICT," );
	        	lSql.append(" SUPL.CLCITY  ANSUPCITY," );
	        	lSql.append(" SUPL.CLZIPCODE  ANSUPZIPCODE," );
	        	lSql.append(" SUPCBD.CBDBANK ANSUPBANKNAME, ");
	        	lSql.append(" SUPCBD.CBDEMAIL ANSUPBANKEMAIL, ");
	        	lSql.append(" SUPCBD.CBDACCTYPE ANSUPACCTYPE, ");
	        	lSql.append(" PURL.CLLINE1  ANPURLINE1," );
	        	lSql.append(" PURL.CLLINE2  ANPURLINE2," );
	        	lSql.append(" PURL.CLLINE3  ANPURLINE3," );
	        	lSql.append(" PURL.CLCOUNTRY  ANPURCOUNTRY," );
	        	lSql.append(" PURL.CLSTATE  ANPURSTATE," );
	        	lSql.append(" PURL.CLDISTRICT  ANPURDISTRICT," );
	        	lSql.append(" PURL.CLCITY  ANPURCITY," );
	        	lSql.append(" PURL.CLZIPCODE  ANPURZIPCODE," );
	        	lSql.append(" FINL.CLLINE1  ANFINLINE1," );
	        	lSql.append(" FINL.CLLINE2  ANFINLINE2," );
	        	lSql.append(" FINL.CLLINE3  ANFINLINE3," );
	        	lSql.append(" FINL.CLCOUNTRY  ANFINCOUNTRY," );
	        	lSql.append(" FINL.CLSTATE  ANFINSTATE," );
	        	lSql.append(" FINL.CLDISTRICT  ANFINDISTRICT," );
	        	lSql.append(" FINL.CLCITY  ANFINCITY," );
	        	lSql.append(" FINL.CLZIPCODE  ANFINZIPCODE" );
	        	//
	        	lSql.append(" FROM OBLIGATIONS ");
	        	lSql.append(" JOIN APPENTITIES ON OBTXNENTITY = AECODE ");
	        	lSql.append(" LEFT OUTER JOIN COMPANYDETAILS FIN ON ( AECDID = FIN.CDID AND FIN.CDRECORDVERSION > 0 ) ");
	        	lSql.append(" LEFT OUTER JOIN PAYMENTFILES  ON ( OBPFID = PFID ) ");
	        	lSql.append(" LEFT OUTER JOIN FACTORINGUNITS ON ( OBFUID = FUID ) ");
	        	lSql.append(" LEFT OUTER JOIN INSTRUMENTS INPARENT ON ( INPARENT.INFUID = FUID AND INPARENT.INGROUPINID IS NULL ) ");

	        	lSql.append(" FULL OUTER JOIN INSTRUMENTS INCHILD ON ( INCHILD.INGROUPINID IS NOT NULL AND INPARENT.INGROUPFLAG IS NOT NULL AND INPARENT.INGROUPFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode())).append(" AND INPARENT.INID = INCHILD.INGROUPINID ) ");
	        	
	        	lSql.append(" LEFT OUTER JOIN ASSIGNMENTNOTICEDETAILS ASSND ON ( ASSND.ANDFUID = OBFUID  ) ");

	        	lSql.append(" LEFT OUTER JOIN ASSIGNMENTNOTICES ASSN ON ( ASSN.ANID = ASSND.ANDANID ) ");

	        	lSql.append(" FULL OUTER JOIN AssignmentNoticeGroupDetails ASSNGD ON ( ASSNGD.ANGFUID = OBFUID AND ASSNGD.ANGGroupInId = INPARENT.INID AND ASSNGD.ANGChildInId = INCHILD.INID ) ");
	        	
	        	lSql.append(" LEFT OUTER JOIN COMPANYDETAILS SUP ON ( SUP.CDCODE = FUSUPPLIER AND SUP.CDRECORDVERSION > 0 ) ");
	    	    lSql.append(" LEFT OUTER JOIN COMPANYDETAILS PUR ON ( PUR.CDCODE = FUPURCHASER AND PUR.CDRECORDVERSION > 0 ) ");
	    	    lSql.append(" LEFT OUTER JOIN BANKBRANCHDETAIL ON (BBDIFSC = OBPAYDETAIL2) ");

	    	    lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS SUPL ON ( SUPL.CLCDID = SUP.CDID AND SUPL.CLRECORDVERSION > 0 AND SUPL.CLLOCATIONTYPE = ").append(lDBHelper.formatString(CompanyLocationBean.LocationType.RegOffice.getCode())).append(" ) ");  
	    	    lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS PURL ON ( PURL.CLCDID = PUR.CDID AND PURL.CLRECORDVERSION > 0 AND PURL.CLLOCATIONTYPE = ").append(lDBHelper.formatString(CompanyLocationBean.LocationType.RegOffice.getCode())).append(" ) ");  
	    	    lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS FINL ON ( FINL.CLCDID = FIN.CDID AND FINL.CLRECORDVERSION > 0 AND FINL.CLLOCATIONTYPE = ").append(lDBHelper.formatString(CompanyLocationBean.LocationType.RegOffice.getCode())).append(" ) ");  
	    	    lSql.append(" LEFT OUTER JOIN COMPANYBANKDETAILS SUPCBD ON ( SUPCBD.CBDCDID = SUP.CDID and CBDDEFAULTACCOUNT = 'Y' ) ");
	    	    lSql.append(" WHERE OBRECORDVERSION > 0  ");
	        	lSql.append(" AND AERECORDVERSION > 0 ");
	        	lSql.append(" AND FIN.CDRECORDVERSION > 0 ");
	        	lSql.append(" AND FURECORDVERSION > 0 ");
	        	lSql.append(" AND INPARENT.INRECORDVERSION > 0 ");
	        	lSql.append(" AND PFRECORDVERSION > 0 ");
	    		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
	    		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
	    		lSql.append(" AND OBTxnType = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
	    		//clause
	        	lSql.append(" AND OBPFID = ").append(pPaymentFileId);
	        	if (currentUnprocessedListStr !=null && currentUnprocessedListStr.length > 0){
	        		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", currentUnprocessedListStr)).append("  ");
	        	}
	        	lSql.append(" AND FIN.CDFINANCIERFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.Yes.Yes.getCode())); //ONLY FINANCIERS 
	        	lSql.append(" AND ( ANDANID IS NULL OR ANGANID IS NULL ) "); //this skips the existing ones and adds only the new ones
			}
			List<String> lColumns = null;
			AssignmentNoticesBean lAssignmentNoticesFilterBean = new AssignmentNoticesBean();
			lAssignmentNoticesFilterBean.setBusinessDate(pLeg1SettledDate);
			List<AssignmentNoticesBean> lNOABeans = assignmentNoticesDAO.findList(pConnection, lAssignmentNoticesFilterBean, lColumns);
			List<AssignmentNoticeInfo> lNOADetails = (List<AssignmentNoticeInfo>) assignmentNoticeInfoDAO.findListFromSql(pConnection, lSql.toString(), 0);
			HashMap<String, AssignmentNoticesBean> lNOAHash = new HashMap<String, AssignmentNoticesBean>();
			//Hash of NOAs for that Businessdate 
			if(lNOABeans != null && lNOABeans.size() > 0){
				logger.info("List of Existing NOA for Bussiness date : " + pLeg1SettledDate.toString());
				for(AssignmentNoticesBean lDbANBean : lNOABeans){
					lNOAHash.put(lDbANBean.getKey(), lDbANBean);
					logger.info("NOA ID : " + lDbANBean.getId() + " :: Key : " + lDbANBean.getKey());
				}
			}
			if(lNOADetails==null||lNOADetails.size() == 0){
				return;
			}
			//
			HashMap<String, AssignmentNoticeWrapperBean> lANWrapperHash = new HashMap<String, AssignmentNoticeWrapperBean>();
			AssignmentNoticeWrapperBean lANWrapperBean = null;
			//
			AssignmentNoticesBean lANBean = null;
			for(AssignmentNoticeInfo lANInfoBean : lNOADetails){
				lANBean = lANInfoBean.getAssignmentNoticesBean();
				AssignmentNoticeDetailsBean lANDBean = lANInfoBean.getAssignmentNoticeDetailsBean();
				AssignmentNoticeGroupDetailsBean lANGBean = lANInfoBean.getAssignmentNoticeGroupDetailsBean();
	    		//first setting the business date for the new record since the key depends on the same
				if (lANBean.getBusinessDate() == null){
					lANBean.setBusinessDate(pLeg1SettledDate);
				}
				//fetching the already added record and using the same so as to update the details and child records with the key anid
    			if(lNOAHash.containsKey(lANBean.getKey())){
    				lANBean = lNOAHash.get(lANBean.getKey());
    			}
	    		//
	    		if(!lANWrapperHash.containsKey(lANBean.getKey())){
	    			lANWrapperBean = new AssignmentNoticeWrapperBean(lANBean);
	    			lANWrapperHash.put(lANBean.getKey(), lANWrapperBean);
	    		}else{
		    		lANWrapperBean = lANWrapperHash.get(lANBean.getKey());
	    		}
	    		lANWrapperBean.addDetails(lANDBean, lANBean.getKey());
	    		lANWrapperBean.addChildDetails(lANGBean, lANBean.getKey());
			}
			//
			HashMap<String,String> lBankHash = TredsHelper.getInstance().getBankName();
			for(AssignmentNoticeWrapperBean lWrapperBean : lANWrapperHash.values()){
				lANBean = lWrapperBean.getAssignmentNoticesBean();
				if(lANBean.getId()==null){
					if(StringUtils.isBlank(lANBean.getFinBankName())){
						lANBean.setFinBankName(lBankHash.get(lANBean.getFinIfsc().substring(0, 4)));
					}
					lANBean.setPurState(TredsHelper.getInstance().getGSTStateDesc(lANBean.getPurState()));
					lANBean.setSupState(TredsHelper.getInstance().getGSTStateDesc(lANBean.getSupState()));
					lANBean.setFinState(TredsHelper.getInstance().getGSTStateDesc(lANBean.getFinState()));
					lANBean.setPurDistrict(lANBean.getPurDistrict().toUpperCase());
					lANBean.setSupDistrict(lANBean.getSupDistrict().toUpperCase());
					lANBean.setFinDistrict(lANBean.getFinDistrict().toUpperCase());
					//if the Notice Bean is NOT present then insert new one in the Db and add to the Hash
					lANBean.setId(lDBHelper.getUniqueNumber(pConnection, "AssignmentNoticesBean.Id") );
					lANBean.setBusinessDate(pLeg1SettledDate);
					assignmentNoticesDAO.insert(pConnection, lANBean);
					logger.info("NEW NOA ID : " + lANBean.getId() + " :: Key : " + lANBean.getKey());
				}
	    		for(AssignmentNoticeDetailsBean lANDBean : lWrapperBean.getDetails()){
					if(lANDBean.getAnId() == null){
						lANDBean.setAnId(lANBean.getId());
						lANDBean.setRecordCreateTime((new Timestamp(System.currentTimeMillis())));
						assignmentNoticeDetailsDAO.insert(pConnection, lANDBean);
						logger.info("NEW NOA Detail FUId : " + lANDBean.getFuId() + " AnId : " + lANBean.getId() + " :: Key : " + lANBean.getKey());
					}
    				List<AssignmentNoticeGroupDetailsBean> lChildBeans = lWrapperBean.getChildDetails(lANDBean.getInId());
        			if(lChildBeans!=null && lChildBeans.size() > 0){
        				for(AssignmentNoticeGroupDetailsBean lANGBean : lChildBeans){
        					if(lANGBean.getAnId() == null){
        						lANGBean.setAnId(lANBean.getId());
        						lANGBean.setRecordCreateTime((new Timestamp(System.currentTimeMillis())));
        						assignmentNoticeGroupDetailsDAO.insert(pConnection, lANGBean);
        						logger.info("NEW NOA Detail ChildInId : " + lANGBean.getChildInId() + " AnId : " + lANBean.getId() + " :: Key : " + lANBean.getKey());
        					}
        				}
        			}
	    		}
			}			
		}catch(Exception lEx){
			logger.info("Error in generateNoticeOfAssignment : "+lEx.getMessage());
		}
	}

	
	private void markLeg2SplitDebitReadyCancel() throws Exception{
		//UPDATION OF LEG2 OBLIGATIONS - PART AND PARENT
		logger.info("********** PaymentSettlor :: Marking obligations ready - Leg2 Debit Ready Cancel");
        logger.info("********** PaymentSettlor :: Cancelling obligations");
	   	//do the following for parts as well as parent of L2
    	//for successful L1 trans parts, update L2 to ready
    	//for unsuccessful L1 trans parts, update L2 to cancel
    	//the following list will contain FU wise part list to be updated to Ready or Cancell
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		
		lSql.append(" MERGE INTO ObligationSplits ");
		lSql.append(" USING ");
		//
		lSql.append(" ( ");
		lSql.append(" SELECT LOneS.OBSStatus L1Status, LOneS.OBSPartNumber L1PartNo, LOneS.OBFUId L1FUId ");
		lSql.append(" , LTwoS.OBSObId L2ObId, LTwoS.OBSPartNumber L2PartNo, LTwoS.OBSStatus L2PStatus ");
		lSql.append(" FROM ObligationSplits LTwoS ");
		lSql.append(" LEFT OUTER JOIN Obligations LTwoP ");
		lSql.append(" ON (LTwoS.OBSOBId = LTwoP.OBId ) ");
		lSql.append(" JOIN ");
		//
		lSql.append(" ( ");
		lSql.append(" SELECT OBFUId, OBSPartNumber, OBSStatus ");
		lSql.append(" FROM ObligationSplits, Obligations, AppEntities ");
		lSql.append(" WHERE OBRecordversion > 0 AND OBSRECORDVERSION > 0");
		lSql.append(" AND OBSOBID = OBID ");
		lSql.append(" AND OBTXNENTITY = AECode ");
		lSql.append(" AND AEType = ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode())); //FINANCIER
		lSql.append(" AND OBPFId = ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" AND OBTxnType = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
		lSql.append(" GROUP BY OBFUId, OBSPartNumber, OBSStatus ");
		lSql.append(" ) LOneS ");
		//
		lSql.append(" ON ( LOneS.OBFUId = LTwoP.OBFUId AND LOneS.OBSPartNumber = LTwoS.OBSPartNumber ) ");
		lSql.append(" WHERE LTwoP.OBRecordversion > 0 ");
		lSql.append(" AND LTwoS.OBSOBID = LTwoP.OBID ");
		lSql.append(" AND LTwoP.OBPFId IS NULL ");
		lSql.append(" AND LTwoP.OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" ) L2UpdateList ");
		lSql.append(" ON ( L2UpdateList.L2ObId = OBSObId AND L2UpdateList.L1PartNo = OBSPartNumber ) ");
		//
		lSql.append(" WHEN MATCHED THEN ");
		lSql.append(" UPDATE SET OBSStatus = ( CASE L2UpdateList.L1Status ");
		lSql.append(" WHEN ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
		lSql.append(" THEN ").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" WHEN ").append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode()));
		lSql.append(" THEN ").append(lDBHelper.formatString(ObligationBean.Status.Cancelled.getCode()));
		lSql.append(" WHEN ").append(lDBHelper.formatString(ObligationBean.Status.Returned.getCode()));
		lSql.append(" THEN ").append(lDBHelper.formatString(ObligationBean.Status.Cancelled.getCode()));
		lSql.append(" ELSE L2UpdateList.L1Status END ) ");
		
		//test_markLeg2DebitReady();
		
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		try {
			lStatement = connection.createStatement();
		    int lCount = lStatement.executeUpdate(lSql.toString());
		    logger.info("********** PaymentSettlor :: Leg2 Debit Ready and L2 Cancel count " + lCount);
		}catch(Exception lEx){  
		    logger.info("********** PaymentSettlor :: Leg2 Debit Ready and L2 Cancel :  ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
		}
		//test_markLeg2DebitReady();
		
	}
	
	private void markLeg2ParentsReadyCancel() throws Exception{
		//UPDATION OF LEG2 OBLIGATIONS - PART AND PARENT
		logger.info("********** PaymentSettlor :: Marking Parent obligations ready - Leg2 Debit Ready or Cancel");
	   	//do the following for parts as well as parent of L2
    	//for successful L1 trans parts, update L2 to ready
    	//for unsuccessful L1 trans parts, update L2 to cancel
    	//the following list will contain FU wise part list to be updated to Ready or Cancell
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		
		lSql.append(" MERGE INTO OBLIGATIONS ");
		lSql.append(" USING ");
		lSql.append(" ( ");
		lSql.append(" SELECT OBSOBID ");
		lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Cancelled.getCode())).append(" THEN 1 ELSE 0 END) CancelCount ");
		lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Ready.getCode())).append(" THEN 1 WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Created.getCode())).append(" THEN 1 ELSE 0 END) ReadyCount ");
		lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Cancelled.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) CancelL2ObliAmt ");
		lSql.append(" , SUM(CASE OBSSTATUS WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Ready.getCode())).append(" THEN OBSAMOUNT WHEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Created.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) BalanceL2ObliAmt ");
		lSql.append(" , Count(*) TotalCount ");
		lSql.append(" FROM ObligationSplits, Obligations ");
		lSql.append(" RIGHT JOIN  ");
		lSql.append(" ( ");
		lSql.append(" SELECT OBFUId FUId ");
		lSql.append(" FROM ObligationSplits, Obligations, AppEntities  ");
		lSql.append(" WHERE OBRecordversion > 0  ");
		lSql.append(" AND OBSOBID = OBID  ");
		lSql.append(" AND OBTXNENTITY = AECode  ");
		lSql.append(" AND AEType = ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode()));//FINANCIER
		lSql.append(" AND OBPFId = ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND OBType = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" AND OBTxnType = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Debit.getCode() ));
		lSql.append(" GROUP BY OBFUId ");
		lSql.append(" ) LOneFU ON ( OBFUID = LOneFU.FUId ) ");
		lSql.append(" WHERE OBRecordversion > 0  ");
		lSql.append(" AND OBSOBID = OBID  ");
		lSql.append(" AND OBPFId IS NULL ");
		lSql.append(" AND OBType = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" GROUP BY OBSOBID ");
		lSql.append(" ) L2UpdateList ");
		lSql.append(" ON (OBId = L2UpdateList.OBSOBID) ");
		lSql.append(" WHEN MATCHED THEN ");
		lSql.append(" UPDATE SET OBStatus = ( ");
		lSql.append(" CASE  ");
		lSql.append(" WHEN (L2UpdateList.CancelCount>0 AND L2UpdateList.CancelCount=L2UpdateList.TotalCount)  ");
		lSql.append(" THEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Cancelled.getCode()));
		lSql.append(" WHEN (L2UpdateList.ReadyCount>0)  ");
		lSql.append(" THEN ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" END  ");
		lSql.append(" ) , OBAmount = ( ");
		lSql.append(" CASE  ");
		lSql.append(" WHEN (L2UpdateList.CancelCount>0 AND L2UpdateList.CancelCount=L2UpdateList.TotalCount)  ");
		lSql.append(" THEN L2UpdateList.CancelL2ObliAmt  "); //this is full cancel henc we have to retain original amount
		lSql.append(" WHEN (L2UpdateList.ReadyCount>0)  ");
		lSql.append(" THEN L2UpdateList.BalanceL2ObliAmt  "); //only in partial fail of Leg1 or Full Success of L1 we need the sum(successamount)
		lSql.append(" END  ");
		lSql.append(" ) ");

		
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		try {
			lStatement = connection.createStatement();
		    int lCount = lStatement.executeUpdate(lSql.toString());
		    logger.info("********** PaymentSettlor :: Leg2 Parent Debit Ready and L2 Cancel " + lCount);
		}catch(Exception lEx){  
		    logger.info("********** PaymentSettlor :: Leg2 Parent Debit Ready and L2 : ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
		}
		
	}
//	
//	private void test_markLeg2DebitReady() throws Exception{
//		StringBuilder lSql = new StringBuilder();
//		lSql.append(" SELECT OBSOBID, OBSPARTNUMBER, OBSSTATUS FROM OBLIGATIONS, ObligationSplits WHERE OBSOBID = OBID AND OBTYPE = 'L2' AND OBFUID IN ( SELECT OBFUID FROM OBLIGATIONS, ObligationSplits WHERE OBSOBID = OBID AND OBPFID=").append(paymentFileBean.getId()).append(" ) ORDER BY OBSOBID, OBSPARTNUMBER ");
//		if (logger.isDebugEnabled())
//		    logger.debug(lSql.toString());
//		Statement lStatement = null;
//		ResultSet lResultSet = null;
//		try {
//			lStatement = connection.createStatement();
//		    lResultSet = lStatement.executeQuery(lSql.toString());
//		    while (lResultSet.next()){
//		    	System.out.println(lResultSet.getString("OBSOBID")+" : "+ lResultSet.getString("OBSPARTNUMBER") +" : "+lResultSet.getString("OBSSTATUS"));
//		    }
//		    logger.info("TEST" );
//		} finally {
//			if (lStatement != null)
//				lStatement.close();
//		}
//	}
	
	private void updateFUUtilPostL1() throws Exception{
        logger.info("********** PaymentSettlor :: Updating FUs Limit Utilisation post L1.");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();

		lSql.append(" SELECT Main.*  ");
		lSql.append(" , FULIMITUTILIZED, FULIMITIDS  ");
		lSql.append(" FROM  ");
		lSql.append(" ( ");
		lSql.append(" SELECT OBFUId ");
		lSql.append(" , SUM(CASE WHEN obsStatus = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Cancelled.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) OBFailAmt  ");
		lSql.append(" , SUM(OBSAMOUNT) OBAmt  ");
		lSql.append(" FROM ObligationSplits, Obligations ");
		lSql.append(" RIGHT JOIN   ");
		lSql.append(" (  ");
		lSql.append(" SELECT OBFUId FUId  ");
		lSql.append(" FROM ObligationSplits, Obligations TMPOB, AppEntities   ");
		lSql.append(" WHERE TMPOB.OBRecordversion > 0   ");
		lSql.append(" AND OBSOBID = TMPOB.OBID   ");
		lSql.append(" AND OBTXNENTITY = AECode   ");
		lSql.append(" AND AEType = ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode()));//FINANCIER
		lSql.append(" AND TMPOB.OBPFId =  ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("TMPOB.OBId", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND TMPOB.OBType = 'L1'  ");
		lSql.append(" AND TMPOB.OBTxnType = 'D'  ");
		lSql.append(" GROUP BY OBFUId  ");
		lSql.append(" ) LOneFU  ");
		lSql.append(" ON ( LOneFU.FUId = Obligations.OBFUId )  ");
		lSql.append(" JOIN AppEntities  ");
		lSql.append(" ON (OBligations.OBTxnEntity = AECode AND AEType = 'NYN') ");//PURCHASER
		lSql.append(" WHERE OBRecordversion > 0  ");
		lSql.append(" AND OBSOBID = OBID  ");
		lSql.append(" AND OBType = 'L2' ");
		lSql.append(" AND OBTxnType = 'D' ");
		lSql.append(" GROUP BY OBFUId ");
		lSql.append(" ORDER BY OBFUId ");
		lSql.append(" ) Main  ");
		lSql.append(" JOIN FactoringUnits  ");
		lSql.append(" ON (FUId = Main.OBFUId)  ");
		lSql.append(" WHERE FURecordVersion > 0  ");
		//
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		Statement lStatement2 = null;
		ResultSet lResultSet = null;
		try {
			lStatement = connection.createStatement();
			lResultSet =  lStatement.executeQuery(lSql.toString());
			StringBuilder lSql2 = new StringBuilder();
			String lFULimitIds = null;
			BigDecimal lOBFailAmt, lOBAmt , lFULimitUtil, lReleaseAmt;
			
			while (lResultSet.next()){
				//OBFUId, OBId, OBSStatus, PurchUtil, FULIMITUTILIZED, FULIMITIDS
				lFULimitIds = lResultSet.getString("FULIMITIDS");
				lOBFailAmt = lResultSet.getBigDecimal("OBFailAmt");
				lOBAmt = lResultSet.getBigDecimal("OBAmt");
				lFULimitUtil = lResultSet.getBigDecimal("FULIMITUTILIZED");
				
				if(StringUtils.isNotEmpty(lFULimitIds) && lOBFailAmt.compareTo(BigDecimal.ZERO) != 0 ){
					lSql2.delete(0, lSql2.length());
					lSql2.append(" UPDATE FinancierAuctionSettings ");
					lReleaseAmt = lFULimitUtil.multiply(lOBFailAmt).divide(lOBAmt); //????????????????????????????
					lSql2.append(" SET FASUTILISED = FASUTILISED - ").append(lReleaseAmt);
					lSql2.append(" , FASBIDLIMITUTILISED = FASBIDLIMITUTILISED - ").append(lReleaseAmt);
					lSql2.append(" WHERE FASID IN ( ").append(lFULimitIds).append(" ) ");
					lStatement2 = connection.createStatement();
				    int lCount = lStatement2.executeUpdate(lSql2.toString());
				    logger.info("********** PaymentSettlor :: FAS Limit update count " + lCount);
				    lStatement2.close();
				}
			}
		}catch(Exception lEx){  
		    logger.info("********** PaymentSettlor :: Updating FUs Limit Utilisation post L1 : ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
			if (lStatement2 != null && !lStatement2.isClosed())
				lStatement2.close();
		}
	}
	
	
	private void updateFUUtilPostL2() throws Exception{
        logger.info("********** PaymentSettlor :: Updating FUs Limit Utilisation post L2.");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();

		lSql.append(" SELECT Main.* ");
		lSql.append(" , FULIMITUTILIZED, FULIMITIDS ");
		lSql.append(" FROM ");
		lSql.append(" (SELECT OBFUId ");
		lSql.append(" , SUM(CASE WHEN obsStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode())).append(" THEN OBSSETTLEDAMOUNT ELSE 0 END) OBSettledAmt ");
		lSql.append(" , SUM(OBSAMOUNT) OBAmt  ");
		lSql.append(" FROM ObligationSplits, Obligations, AppEntities ");
		lSql.append(" WHERE OBRecordversion > 0 ");
		lSql.append(" AND OBSOBID = OBID ");
		lSql.append(" AND OBTXNENTITY = AECode ");
		lSql.append(" AND AEType = 'NYN' "); //PURCHASER
		lSql.append(" AND OBPFId = ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" AND OBTxnType = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
		lSql.append(" GROUP BY OBFUId ");
		lSql.append(" ORDER BY OBFUId ");
		lSql.append(" ) Main ");
		lSql.append(" JOIN FactoringUnits ");
		lSql.append(" ON (FUId = Main.OBFUId) ");
		lSql.append(" WHERE FURecordVersion > 0 ");
		//
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		Statement lStatement2 = null;
		ResultSet lResultSet = null;
		try {
			lStatement = connection.createStatement();
			lResultSet =  lStatement.executeQuery(lSql.toString());
			StringBuilder lSql2 = new StringBuilder();
			String lFULimitIds = null;
			BigDecimal lOBSettled , lOBAmt, lFULimitUtil, lReleaseAmt;
			
			while (lResultSet.next()){
				//OBFUId, OBId, OBSStatus, PurchUtil, FULIMITUTILIZED, FULIMITIDS
				lFULimitIds = lResultSet.getString("FULIMITIDS");
				lOBSettled = lResultSet.getBigDecimal("OBSettledAmt");
				lOBAmt = lResultSet.getBigDecimal("OBAmt");
				lFULimitUtil = lResultSet.getBigDecimal("FULIMITUTILIZED");
				
				if(StringUtils.isNotEmpty(lFULimitIds)){
					lSql2.delete(0, lSql2.length());
					lSql2.append(" UPDATE FinancierAuctionSettings ");
					lReleaseAmt = lFULimitUtil.multiply(lOBSettled).divide(lOBAmt); //????????????????????????????
					lSql2.append(" SET FASUTILISED = FASUTILISED - ").append(lReleaseAmt);
					lSql2.append(" , FASBIDLIMITUTILISED = FASBIDLIMITUTILISED - ").append(lReleaseAmt);
					lSql2.append(" WHERE FASID IN ( ").append(lFULimitIds).append(" ) ");
					lStatement2 = connection.createStatement();
				    int lCount = lStatement2.executeUpdate(lSql2.toString());
				    lStatement2.close();
				    logger.info("********** PaymentSettlor :: FAS Limit update count " + lCount);
				}
			}
		}catch(Exception lEx){  
		    logger.info("********** PaymentSettlor :: Updating FUs Limit Utilisation post L2 : ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
			if (lStatement2 != null && !lStatement2.isClosed())
				lStatement2.close();
		}
	}	

	
	private void updatePurSupUtilPostL1() throws Exception{
        logger.info("********** PaymentSettlor :: Updating Purchaser Supplier Limit Utilisation post L1.");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();

		lSql.append(" SELECT Main.*  ");
		lSql.append(" , FUPurSupLimitUtilized , FUPurchaser, FUSupplier ");
		lSql.append(" FROM  ");
		lSql.append(" ( ");
		lSql.append(" SELECT OBFUId ");
		lSql.append(" , SUM(CASE WHEN obsStatus = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Cancelled.getCode())).append(" THEN OBSAMOUNT ELSE 0 END) OBFailAmt  ");
		lSql.append(" , SUM(OBSAMOUNT) OBAmt  ");
		lSql.append(" FROM ObligationSplits, Obligations ");
		lSql.append(" RIGHT JOIN   ");
		lSql.append(" (  ");
		lSql.append(" SELECT OBFUId FUId  ");
		lSql.append(" FROM ObligationSplits, Obligations TMPOB, AppEntities   ");
		lSql.append(" WHERE TMPOB.OBRecordversion > 0   ");
		lSql.append(" AND OBSOBID = TMPOB.OBID   ");
		lSql.append(" AND OBTXNENTITY = AECode   ");
		lSql.append(" AND AEType = ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode()));//FINANCIER
		lSql.append(" AND TMPOB.OBPFId =  ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("TMPOB.OBId", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND TMPOB.OBType = 'L1'  ");
		lSql.append(" AND TMPOB.OBTxnType = 'D'  ");
		lSql.append(" GROUP BY OBFUId  ");
		lSql.append(" ) LOneFU  ");
		lSql.append(" ON ( LOneFU.FUId = Obligations.OBFUId )  ");
		lSql.append(" JOIN AppEntities  ");
		lSql.append(" ON (OBligations.OBTxnEntity = AECode AND AEType = 'NYN') ");//PURCHASER
		lSql.append(" WHERE OBRecordversion > 0  ");
		lSql.append(" AND OBSOBID = OBID  ");
		lSql.append(" AND OBType = 'L2' ");
		lSql.append(" AND OBTxnType = 'D' ");
		lSql.append(" GROUP BY OBFUId ");
		lSql.append(" ORDER BY OBFUId ");
		lSql.append(" ) Main  ");
		lSql.append(" JOIN FactoringUnits  ");
		lSql.append(" ON (FUId = Main.OBFUId)  ");
		lSql.append(" WHERE FURecordVersion > 0  ");
		lSql.append("  AND FUPurSupLimitUtilized IS NOT NULL  ");
		//
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		Statement lStatement2 = null;
		ResultSet lResultSet = null;
		try {
			lStatement = connection.createStatement();
			lResultSet =  lStatement.executeQuery(lSql.toString());
			BigDecimal lOBFailAmt, lOBAmt , lPSLimitUtil, lReleaseAmt;
			Long lFuId = null;
			FactoringUnitBean lFUBean = null; 
			Map<String, BigDecimal> lPSwiseReleaseAmt = new HashMap<String, BigDecimal>();
			String lKey = null;
			while (lResultSet.next()){
				//OBFUId, OBId, OBSStatus, PurchUtil, FULIMITUTILIZED, FULIMITIDS
				lOBFailAmt = lResultSet.getBigDecimal("OBFailAmt");
				lFuId = lResultSet.getLong("OBFUId");
				lOBAmt = lResultSet.getBigDecimal("OBAmt");
				lPSLimitUtil = lResultSet.getBigDecimal("FUPurSupLimitUtilized");
				if(lOBFailAmt.compareTo(BigDecimal.ZERO) != 0 ){
					lReleaseAmt = lPSLimitUtil.multiply(lOBFailAmt).divide(lOBAmt); //????????????????????????????
					lKey = lResultSet.getString("FUPurchaser") + CommonConstants.KEY_SEPARATOR + lResultSet.getString("FUSupplier") ;
					if(lPSwiseReleaseAmt.containsKey(lKey)){
						BigDecimal lTmpAmt = lPSwiseReleaseAmt.get(lKey);
						lTmpAmt = lTmpAmt.add(lReleaseAmt);
						lPSwiseReleaseAmt.put(lKey, lTmpAmt);
					}else{
						lPSwiseReleaseAmt.put(lKey, lReleaseAmt);
					}
					logger.info("lKey : "+ lKey +" : "+lReleaseAmt);
	                //
					lFUBean = new FactoringUnitBean();
	                lFUBean.setId(lFuId);
	                lFUBean = factoringUnitDAO.findByPrimaryKey(connection, lFUBean);
	                lFUBean.setPurSupLimitUtilized(lPSLimitUtil.subtract(lReleaseAmt));
	                factoringUnitDAO.update(connection, lFUBean, FactoringUnitBean.FIELDGROUP_UPDATEPURSUPLIMIT);
				}
			}
			String[] lPSCodes = null;
			for(String lPSKey : lPSwiseReleaseAmt.keySet()){
				lPSCodes = CommonUtilities.splitString(lPSKey,CommonConstants.KEY_SEPARATOR);
				lReleaseAmt = lPSwiseReleaseAmt.get(lPSKey);
	            purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(connection, lPSCodes[0], lPSCodes[1], null, lReleaseAmt , true);
			}
		}catch(Exception lEx){  
		    logger.info("********** PaymentSettlor :: Updating Purchaser Supplier Limit Utilisation post L1 : ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
			if (lStatement2 != null && !lStatement2.isClosed())
				lStatement2.close();
		}
	}

	private void updatePurSupUtilPostL2() throws Exception{
        logger.info("********** PaymentSettlor :: Updating Purchaser Supplier Limit Utilisation post L2.");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();

		lSql.append(" SELECT Main.* ");
		lSql.append(" , FUPurSupLimitUtilized , FUId, FUPurchaser, FUSupplier ");
		lSql.append(" FROM ");
		lSql.append(" (SELECT OBFUId ");
		lSql.append(" , SUM(CASE WHEN obsStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode())).append(" THEN OBSSETTLEDAMOUNT ELSE 0 END) OBSettledAmt ");
		lSql.append(" , SUM(OBSAMOUNT) OBAmt  ");
		lSql.append(" FROM ObligationSplits, Obligations, AppEntities ");
		lSql.append(" WHERE OBRecordversion > 0 ");
		lSql.append(" AND OBSOBID = OBID ");
		lSql.append(" AND OBTXNENTITY = AECode ");
		lSql.append(" AND AEType = 'NYN' "); //PURCHASER
		lSql.append(" AND OBPFId = ").append(paymentFileBean.getId());
		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", completeObligationsProcessedListStr)).append("  ");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" AND OBTxnType = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
		lSql.append(" GROUP BY OBFUId ");
		lSql.append(" ORDER BY OBFUId ");
		lSql.append(" ) Main ");
		lSql.append(" JOIN FactoringUnits ");
		lSql.append(" ON (FUId = Main.OBFUId)  ");
		lSql.append(" WHERE FURecordVersion > 0  ");
		lSql.append("  AND FUPurSupLimitUtilized IS NOT NULL  ");
		//
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		Statement lStatement2 = null;
		ResultSet lResultSet = null;
		try {
			lStatement = connection.createStatement();
			lResultSet =  lStatement.executeQuery(lSql.toString());
			BigDecimal lOBSettled , lOBAmt, lPSLimitUtil, lReleaseAmt;
			Long lFuId = null;
			FactoringUnitBean lFUBean = null; 
			Map<String, BigDecimal> lPSwiseReleaseAmt = new HashMap<String, BigDecimal>();
			String lKey = null;
			//
			while (lResultSet.next()){
				//OBFUId, OBId, OBSStatus, PurchUtil, FULIMITUTILIZED, FULIMITIDS
				lOBSettled = lResultSet.getBigDecimal("OBSettledAmt");
				lOBAmt = lResultSet.getBigDecimal("OBAmt");
				lFuId = lResultSet.getLong("FUId");
				lPSLimitUtil = lResultSet.getBigDecimal("FUPurSupLimitUtilized");
				//
				lReleaseAmt = lPSLimitUtil.multiply(lOBSettled).divide(lOBAmt); //????????????????????????????
				if(lReleaseAmt.compareTo(BigDecimal.ZERO) == 1){
					lKey = lResultSet.getString("FUPurchaser") + CommonConstants.KEY_SEPARATOR + lResultSet.getString("FUSupplier") ;
					if(lPSwiseReleaseAmt.containsKey(lKey)){
						BigDecimal lTmpAmt = lPSwiseReleaseAmt.get(lKey);
						lTmpAmt = lTmpAmt.add(lReleaseAmt);
						lPSwiseReleaseAmt.put(lKey, lTmpAmt);
					}else{
						lPSwiseReleaseAmt.put(lKey, lReleaseAmt);
					}
					logger.info("lKey : "+ lKey +" : "+lReleaseAmt);
	                //
					lFUBean = new FactoringUnitBean();
	                lFUBean.setId(lFuId);
	                lFUBean = factoringUnitDAO.findByPrimaryKey(connection, lFUBean);
	                lFUBean.setPurSupLimitUtilized(lPSLimitUtil.subtract(lReleaseAmt));
	                factoringUnitDAO.update(connection, lFUBean, FactoringUnitBean.FIELDGROUP_UPDATEPURSUPLIMIT);
				}
			}
			String[] lPSCodes = null;
			for(String lPSKey : lPSwiseReleaseAmt.keySet()){
				lPSCodes = CommonUtilities.splitString(lPSKey, CommonConstants.KEY_SEPARATOR);
				lReleaseAmt = lPSwiseReleaseAmt.get(lPSKey);
	            purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(connection, lPSCodes[0], lPSCodes[1], null, lReleaseAmt , true);
			}
		}catch(Exception lEx){  
		    logger.info("********** PaymentSettlor :: Updating Purchaser Supplier Limit Utilisation post L2 : ERROR : " + lEx.getMessage());
		}finally {
			if (lStatement != null)
				lStatement.close();
			if (lStatement2 != null && !lStatement2.isClosed())
				lStatement2.close();
		}
	}	


	private DirectPaymentSettlor.EntityType getEntityType(String pEntityCode){
		DirectPaymentSettlor.EntityType lType = null;
		if (appEntitiesType.containsKey(pEntityCode)){
			lType = appEntitiesType.get(pEntityCode);
		}else{
			AppEntityBean lAppEntityBean;
			try {
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pEntityCode);
				if(lAppEntityBean!=null){
					if(lAppEntityBean.isFinancier())
						lType = DirectPaymentSettlor.EntityType.Financier;
					else if(lAppEntityBean.isPurchaser())
						lType = DirectPaymentSettlor.EntityType.Buyer;
					else if(lAppEntityBean.isSupplier())
						lType = DirectPaymentSettlor.EntityType.Seller;
					else if(lAppEntityBean.isPlatform())
						lType = DirectPaymentSettlor.EntityType.Treds;
					appEntitiesType.put(pEntityCode, lType);
				}
			} catch (MemoryDBException e) {
				e.printStackTrace();
			}
		}
		return lType;
	}
	
	private void getObligationMap(Connection pConnection,Long pPfId){
		List<ObligationSplitsBean> lObligations = null;
		DBHelper lDBHelper = DBHelper.getInstance();
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        Map<String, List <ObligationSplitsBean>> lSplitsHash = new HashMap<String, List <ObligationSplitsBean>>();
        //		
        //select * from ObligationSplits, Obligations where !Processed and Status != Sent (if Ready then remove pfid)
        lSql.append(" SELECT * FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE ");
        lSql.append(" OBSOBID = OBID "); 
        lSql.append(" WHERE OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0");
        lSql.append(" AND OBPFID = ").append(pPfId);
        lSql.append(" ORDER BY OBFUID,OBSOBID,OBSPARTNUMBER");
        try {
			lObligationDetails =  obligationDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
			IObligation lSplitsBean = null;
			String lKey = null;
			for (ObligationDetailBean lBean : lObligationDetails){
				lSplitsBean = lBean.getObligationSplitsBean();
				lKey = lSplitsBean.getParentObligation().getFuId()+CommonConstants.KEY_SEPARATOR+lSplitsBean.getParentObligation().getType().getCode()+CommonConstants.KEY_SEPARATOR+lSplitsBean.getPartNumber();
				lSplitsBean.setParentObligation(lBean.getObligationBean());
				if (!lSplitsHash.containsKey(lKey)){
					lObligations = new ArrayList<ObligationSplitsBean>();
					lObligations.add((ObligationSplitsBean) lSplitsBean);
					lSplitsHash.put(lKey, lObligations);
				}else{
					lObligations = lSplitsHash.get(lKey);
					if (!lObligations.contains(lSplitsBean)){
						lObligations.add((ObligationSplitsBean) lSplitsBean);
					}
				}
			}
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
	}
	
	private void updatePaymentFileStatus(){
		//We will check the the total count of obligation splits and
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT  Count(*) TotalSplits ");
		lSql.append(" , Sum(CASE WHEN OBLIGATIONSPLITS.OBSSettlorProcessed = 'Y' THEN 1 ELSE 0 END ) TotalProcessed ");
		lSql.append(" , Sum(CASE WHEN OBLIGATIONSPLITS.OBSSettlorProcessed IS NULL THEN 1 ELSE 0 END) TotalUnProcessed ");
		lSql.append(" FROM OBLIGATIONSPLITS, Obligations ");
		lSql.append(" WHERE OBSObId = OBId ");
		lSql.append(" AND OBSRecordVersion > 0 AND OBRecordVersion > 0 ");
		lSql.append(" AND OBPFId = ").append(paymentFileBean.getId());
		lSql.append(" GROUP BY OBSSettlorProcessed ");
		//
		Long lTotalUnProcessed = null;
		try(Statement lStatement = connection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
		    while (lResultSet.next()){
		    	lTotalUnProcessed = lResultSet.getLong("TotalUnProcessed");
		    }
		}catch(Exception lException){
			
		} 
		if(lTotalUnProcessed!=null && lTotalUnProcessed.equals(new Long(0))){
			paymentFileBean.setStatus(PaymentFileBean.Status.Return_File_Processed);
			Long lTotalProcessedSplits = null;
			BigDecimal lTotalProcessedAmount = null;
			try {
				lSql = new StringBuilder();
				lSql.append(" SELECT Count(*) TotalProcessedSplits ");
				lSql.append(" , Sum(CASE WHEN OBLIGATIONSPLITS.OBSSettlorProcessed = 'Y' THEN OBSAMOUNT ELSE 0 END ) TotalProcessedAmount ");
				lSql.append(" FROM OBLIGATIONSPLITS, Obligations ");
				lSql.append(" WHERE OBSObId = OBId ");
				lSql.append(" AND OBSRecordVersion > 0 AND OBRecordVersion > 0 ");
				lSql.append(" AND OBSPFId = ").append(paymentFileBean.getId());
				lSql.append(" GROUP BY OBSSettlorProcessed ");
				try(Statement lStatement = connection.createStatement();
						ResultSet lResultSet = lStatement.executeQuery(lSql.toString());){
				    while (lResultSet.next()){
				    	lTotalProcessedSplits = lResultSet.getLong("TotalProcessedSplits");
				    	lTotalProcessedAmount = lResultSet.getBigDecimal("TotalProcessedAmount");
				    	paymentFileBean.setRecordCount(lTotalProcessedSplits);
				    	paymentFileBean.setTotalValue(lTotalProcessedAmount);
				    }
				}catch(Exception lException){
					
				} 
				//total count of parent and total of amount of splits
				paymentFileDAO.update(connection, paymentFileBean);
			} catch (SQLException e) {
				logger.info("Error in updatePaymentFileStatus 2 : "+ e.getMessage());
				e.printStackTrace();
			}
		}
		
	}
	private void updateParentObligaitons() throws Exception{
    	//Leg1 - Update Parent SettlementAmount
		List<Map<String, Object>> lList = getObligationsForFinalStatusUpdate();
		List<String> lKeyList = new ArrayList<String>();
		String lKey = null;
		logger.info("********** PaymentSettlor :: updateParentObligaitons CALLED.");
		//
		//
		for ( Map<String, Object> lHash : lList ){
			lKey = lHash.get("OBFUID")+CommonConstants.KEY_SEPARATOR+lHash.get("OBTYPE")+CommonConstants.KEY_SEPARATOR+lHash.get("OBISUPFRONTOBLIG");
			if (!lKeyList.contains(lKey)){
				lKeyList.add(lKey);
			}
		}
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM ( SELECT * FROM Obligations, ObligationSplits where obid=obsobid ) ");
        lSql.append(" WHERE ");
        lSql.append(" OBFUID || '^' || OBTYPE || '^' || OBISUPFRONTOBLIG   in ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(lKeyList)).append(" ) ");
        lSql.append(" AND OBSTATUS not in ( ").append(lDBHelper.formatString(Status.Shifted.getCode())).append(" ," );
        lSql.append(lDBHelper.formatString(Status.Extended.getCode())).append( " ) ");
        lSql.append(" ORDER BY OBID,OBFUID , OBSPARTNUMBER");
        List<ObligationDetailBean> lObligations =  obligationDetailDAO.findListFromSql(connection, lSql.toString(), -1);
    	IObligation lParentObliBean = null;
    	BigDecimal lTmpAmt = null;
    	Map<Long, Integer> lParentSuccessCount = new HashMap<Long, Integer>();
    	Integer lCount = new Integer(0);
    	//count the success and also total the settled amount - both required in parent obligation update
    	ObligationSplitsBean lObliSplitBean = null;
        //Update parent bean only for one child since the bean will be same each time
        HashMap<Long,IObligation> lUpdateParents = new HashMap<Long,IObligation>() ;

    	for (ObligationDetailBean lDetailBean : lObligations){
        	lObliSplitBean = lDetailBean.getObligationSplitsBean();

        	if (!lUpdateParents.containsKey(lObliSplitBean.getId())){
        		lParentObliBean = lDetailBean.getObligationBean();
        		lUpdateParents.put(lParentObliBean.getId(), lParentObliBean);
        		lParentObliBean.setRecordUpdator(lObliSplitBean.getRecordUpdator());
        		lParentObliBean.setSettledDate(lObliSplitBean.getSettledDate());
        		lParentObliBean.setSettledAmount(BigDecimal.ZERO);
        	}else{
        		lParentObliBean = lUpdateParents.get(lObliSplitBean.getId());
        	}
        	lObliSplitBean.setParentObligation(lParentObliBean);
        	
        	if(ObligationBean.Status.Success.equals(lObliSplitBean.getStatus())){
        		lTmpAmt = lParentObliBean.getSettledAmount();
        		if(lTmpAmt==null) lTmpAmt = BigDecimal.ZERO;
        		lTmpAmt = lTmpAmt.add(lObliSplitBean.getSettledAmount());
        		lParentObliBean.setSettledAmount(lTmpAmt);
        		//for Reports to avoid difference in the failed and a settled amount
        		//updating the leg1 amount 
        		if(ObligationBean.Type.Leg_1.equals(lObliSplitBean.getType())){
            		lParentObliBean.setAmount(lTmpAmt);
        		}
            	//System.out.println("After : Id = "+lObligationBean.getId()+" , REC VER : " + lObligationBean.getRecordVersion() + " , AMT " + lParentObliBean.getSettledAmount()  );
        		lCount = lParentSuccessCount.get(lParentObliBean.getId());
        		if(lCount==null) {
        			lCount = new Integer(0);
        		}
        		lCount += new Integer(1);
            	lParentSuccessCount.put(lParentObliBean.getId(), lCount);
        	}
        }
        //repopulate the lParentSuccessCount 
        
		completeObligationsProcessedList = new ArrayList<Long>();
		for(IObligation lUpdatedParentBean :  lUpdateParents.values()){
    		lCount = lParentSuccessCount.get(lUpdatedParentBean.getId());
    		if(lCount==null || lCount.longValue()==0){
    			lUpdatedParentBean.setStatus(Status.Failed);
    		}else{
    			if(ObligationBean.Type.Leg_1.equals(lUpdatedParentBean.getType())){
    				lUpdatedParentBean.setStatus(Status.Success); //for L1 partial success is full success
    			}else if(ObligationBean.Type.Leg_2.equals(lUpdatedParentBean.getType())){
    				lUpdatedParentBean.setStatus(Status.Success); //for L2 partial or full 
    			}else if(ObligationBean.Type.Leg_3.equals(lUpdatedParentBean.getType())){
    				lUpdatedParentBean.setStatus(Status.Success); //for L3 partial or full 
    			}
    		}
    		if (!completeObligationsProcessedList.contains(lUpdatedParentBean.getId())){
    			completeObligationsProcessedList.add(lUpdatedParentBean.getId());
    		}
    		//logger.info("updating parent : "+ lUpdatedParentBean.getId());
    		obligationDAO.update(connection, (ObligationBean) lUpdatedParentBean,ObligationBean.FIELDGROUP_UPDATESETTLEDAMOUNT);
    		//logger.info("updating parent done : "+ lUpdatedParentBean.getId());

		}
		logger.info("********** PaymentSettlor :: updateParentObligaitons ENDED.");
    }
	
	private Map<String, ObligationModificationRequestBean> getApprovedObligationModificationRequests() {
		StringBuilder lSql = new StringBuilder();
		DBHelper lDbHelper = DBHelper.getInstance();
		lSql.append(" SELECT * FROM (SELECT * from OBLIGATIONSMODIREQUESTS , OBLIGATIONSMODIDETAILS ) ");
		lSql.append(" WHERE  (OMRFUID || '^' || OMRTYPE || '^' || OMRPARTNUMBER)  IN  ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(keyForModificationRequest)).append(" ) ");
		lSql.append(" AND OMRSTATUS = ").append(lDbHelper.formatString(ObligationModificationRequestBean.Status.Approved.getCode()));
		List<ObligationModiReqDetailBean> lCompositeList = new ArrayList<ObligationModiReqDetailBean>();
		List<ObligationModificationDetailBean> lDetailList= new ArrayList<ObligationModificationDetailBean>();
		ObligationModificationRequestBean lModificationRequestBean = null;
		Map<String, ObligationModificationRequestBean> lModificationBeansHash = new HashMap<String,ObligationModificationRequestBean>(); 
		String lKey = null;
		try {
			lCompositeList =  obligationModiReqDetailDAO.findListFromSql(connection, lSql.toString(), -1);
			for (ObligationModiReqDetailBean lCompositeBean : lCompositeList){
				ObligationModificationRequestBean lTmpParentBean = lCompositeBean.getObligationModificationRequestBean();
				ObligationModificationDetailBean lTmpChildBean = lCompositeBean.getObligationModificationDetailBean();
				lKey = lTmpParentBean.getFuId()+CommonConstants.KEY_SEPARATOR+lTmpParentBean.getType().getCode()+CommonConstants.KEY_SEPARATOR+lTmpParentBean.getPartNumber();
				if (!lModificationBeansHash.containsKey(lKey)){
					lModificationRequestBean = lTmpParentBean;
					lDetailList = new ArrayList<ObligationModificationDetailBean>();
					lModificationRequestBean.setObliModDetailsList(lDetailList);
					lModificationBeansHash.put(lKey, lTmpParentBean);
				}else{
					lModificationRequestBean = lModificationBeansHash.get(lKey);
				}
				lDetailList = lModificationRequestBean.getObliModDetailsList();
				lDetailList.add(lTmpChildBean);
			}
			return lModificationBeansHash;
        } catch (Exception  e) {
			logger.info(e.getMessage());
		}
		return null;

    }
	
	private void updateSplitsFromModRequests(List<IObligation> pUnProcessedList, Map<String, ObligationModificationRequestBean> pModifiactionRequestHash) {
		String lKey = null;	
		ObligationModificationRequestBean lRequestBean = null;
		for (IObligation lBean : pUnProcessedList){
			lKey = lBean.getParentObligation().getFuId()+CommonConstants.KEY_SEPARATOR+lBean.getParentObligation().getType().getCode()+CommonConstants.KEY_SEPARATOR+lBean.getPartNumber();
			if (pModifiactionRequestHash.containsKey(lKey)){
				lRequestBean = pModifiactionRequestHash.get(lKey);
				for(ObligationModificationDetailBean lDetailBean : lRequestBean.getObliModDetailsList()){
					if(lDetailBean.getObId().equals(lBean.getId()) && lRequestBean.getPartNumber().equals(lBean.getPartNumber())){
						lBean.setAmount(lDetailBean.getRevisedAmount());
						lBean.setStatus(lDetailBean.getRevisedStatus());
						lBean.setDate(lDetailBean.getRevisedDate());
						if (Status.Success.equals(lDetailBean.getRevisedStatus())){
							lBean.setSettledAmount(lDetailBean.getRevisedAmount());
							lBean.setSettledDate(TredsHelper.getInstance().getBusinessDate()); //TODO: Check where is SettledDate is used
						}
						if(lBean instanceof ObligationSplitsBean){
							ObligationSplitsBean lOSBean = (ObligationSplitsBean) lBean;
							//TODO: should we check status
							if(!lOSBean.getPaymentSettlor().equals(lDetailBean.getPaymentSettlor())){
								String lRemarks = "";
								lOSBean.setPaymentSettlor(lDetailBean.getPaymentSettlor());
								if(StringUtils.isNotEmpty(lOSBean.getRespRemarks()))
									lRemarks =  lOSBean.getRespRemarks();
								lRemarks += " Old PFId : "+lOSBean.getPfId().toString();
								lOSBean.setRespRemarks(lRemarks);
								lOSBean.setPfId(null);
							}
							if ( lDetailBean.getPaymentRefNo() != null ){
								lBean.setPaymentRefNo(lDetailBean.getPaymentRefNo());
							}
						}
						try {
							obligationSplitsDAO.update(connection, (ObligationSplitsBean) lBean);
						} catch (Exception e){
							logger.info(e.getMessage());
						}
					}
				}
				try {
					lRequestBean.setStatus(ObligationModificationRequestBean.Status.Applied);
					obligationModificationRequestDAO.update(connection, lRequestBean,ObligationModificationRequestBean.FIELDGROUP_UPDATESTATUS);
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
    }
	
	private void markUnprocessedSplitsAsProcessed(List<IObligation>lUnProcessedList){
		for(IObligation lBean : lUnProcessedList){
			if(lBean instanceof ObligationSplitsBean){
				ObligationSplitsBean lOBSBean = (ObligationSplitsBean) lBean;
				lOBSBean.setSettlorProcessed(Yes.Yes);
				try {
					obligationSplitsDAO.update(connection, lOBSBean,ObligationSplitsBean.FIELDGROUP_MARKPROCESSED);
				} catch (Exception e) {
					logger.info(e.getMessage());
				}
			}
		}
	}
	
	public List<Map<String,Object>>  getObligationsForFinalStatusUpdate(){
        StringBuilder lSql = new StringBuilder(); 
        DBHelper lDBHelper = DBHelper.getInstance();
        Statement lStatement = null;
        ResultSet lResultSet = null;
        String[] lColoumns = new String[]{"OBFUID","OBTYPE","Total_Processed","Total_Count","OBISUPFRONTOBLIG"};
        //
        List<Map<String,Object>> lResultList = new ArrayList<Map<String,Object>>();
        Map<String,Object> lResultHash = null;
        


//        select OBFUID , OBTYPE    ,SUM(CASE WHEN OBSSETTLORPROCESSED = 'Y' THEN 1 ELSE 0 END) Total_Processed ,Count(*) Total_Count  from
//        (select * FROM Obligations oblig, ObligationSplits where obsobid=obid)  
//        where OBFUID || '^' || OBTYPE in
//        ( SELECT distinct OBFUID || '^' || OBTYPE FROM Obligations, ObligationSplits 
//         WHERE  (OBSOBID = OBID)  AND  (  OBId IN ( 1182500000001,1182500000002,1182500000003 )  )    
//        AND OBSSTATUS != 'SNT' AND OBSSETTLORPROCESSED = 'Y' AND OBSTATUS = 'SNT' ) 
//        group by obfuid , obtype 
        
        
        lSql.append(" SELECT OBFUID , OBTYPE  ");
        lSql.append("  ,SUM(CASE WHEN OBSSETTLORPROCESSED = 'Y' THEN 1 ELSE 0 END) Total_Processed ");
        lSql.append(" 	,Count(*) Total_Count ,OBISUPFRONTOBLIG ");
        lSql.append(" FROM ( Select * FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE OBSRECORDVERSION>0 AND OBRECORDVERSION>0 AND OBSOBID=OBID ");
        lSql.append(" AND OBSSTATUS not in ( ").append(lDBHelper.formatString(Status.Shifted.getCode())).append(" ," );
        lSql.append(lDBHelper.formatString(Status.Extended.getCode())).append( " ) ");
        lSql.append(" AND OBSTATUS not in ( ").append(lDBHelper.formatString(Status.Shifted.getCode())).append(" ," );
        lSql.append(lDBHelper.formatString(Status.Extended.getCode())).append( " ) ").append(" ) ");
        lSql.append(" WHERE OBFUID || '^' || OBTYPE in ");
        lSql.append( "  ( SELECT  distinct OBFUID || '^' || OBTYPE FROM Obligations, ObligationSplits ");
        lSql.append(" WHERE ");
        lSql.append(" (OBSOBID = OBID) "); 
//      lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
        lSql.append(" AND OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0");
        lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", currentUnprocessedListStr)).append("  ");
        lSql.append(" AND OBSSTATUS != ").append(lDBHelper.formatString(ObligationBean.Status.Sent.getCode()));
        lSql.append(" AND OBSSETTLORPROCESSED = ").append(lDBHelper.formatString(Yes.Yes.getCode()));
        lSql.append(" AND OBSTATUS = ").append(lDBHelper.formatString(ObligationBean.Status.Sent.getCode()));
        lSql.append(" ) "); 
        lSql.append( " group by obfuid , obtype, OBISUPFRONTOBLIG ");
        logger.info(" getObligationsForFinalStatusUpdate : query : "+ lSql.toString() );
        try {
			lStatement = connection.createStatement();
			logger.info(lSql.toString());
			lResultSet = lStatement.executeQuery(lSql.toString());
			int i = 0;
			while (lResultSet.next()){
				lResultHash =  new HashMap<String,Object>();
				i=0;
				for (i=0;i<lColoumns.length;i++){
					if (i==1){
						lResultHash.put(lColoumns[i],lResultSet.getString(lColoumns[i].toString()));
					}else{
						if (lColoumns[i].equals("OBISUPFRONTOBLIG")) {
							lResultHash.put(lColoumns[i],lResultSet.getString(lColoumns[i].toString())==null?"":lResultSet.getString(lColoumns[i].toString()));
						}else {
							lResultHash.put(lColoumns[i],lResultSet.getLong(lColoumns[i].toString()));
						}
					}
				}
				if(!(lResultHash.get("Total_Processed").equals(0) || lResultHash.get("Total_Count").equals(0) )){
					if(lResultHash.get("Total_Processed").equals(lResultHash.get("Total_Count"))){
						lResultList.add(lResultHash);
					}
				}
				
			}
			lResultSet.close();
			lStatement.close();
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
        return lResultList;
	}

	@Override
	public List<Long> getObligationListReceivedForProcessing() {
		return currentUnprocessedList;
	}
	
	private Map<Long, Object[]> getSupplierObligations() throws Exception{
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        DBHelper lDbHelper = DBHelper.getInstance();
        lSql.append(" SELECT ObligationDetails.* FROM ( ").append(" SELECT Obligations.*, ObligationSplits.*  FROM Obligations, ObligationSplits ").append(" ) ObligationDetails");
        lSql.append(" LEFT OUTER JOIN FACTORINGUNITS ON ( OBFUID = FUID  AND  FUPURCHASER IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(ClientAdapterManager.getInstance().getClientEntityList())).append( " ) ) " );
        lSql.append(" WHERE OBSOBID = OBID and OBTXNENTITY=FUSUPPLIER AND OBPFID = ").append(paymentFileBean.getId());
        lSql.append(" AND ");
        lSql.append(" OBSRECORDVERSION > 0 AND OBRECORDVERSION > 0 ");
        lSql.append(" AND ");
        lSql.append(" OBSTATUS IN ( ").append(lDbHelper.formatString(Status.Success.getCode())).append(" , ");
        lSql.append(lDbHelper.formatString(Status.Failed.getCode())).append(" ) ");
        lSql.append(" AND OBTYPE = ").append(lDbHelper.formatString(Type.Leg_1.getCode()));
        lSql.append(" ORDER BY OBFUID,OBSOBID,OBSPARTNUMBER ");
        lObligationDetails  = obligationDetailDAO.findListFromSql(connection, lSql.toString(), -1);
        Map<Long,Object[]> lObligations = new HashMap<Long,Object[]>();
        ObligationBean lOBBean = null;
        ObligationSplitsBean lOBSplitBean = null;
        List <ObligationSplitsBean> lSplitList = null;
        Object[] lOblig = null;//0=ObligationBean, 1=List<ObligtionSplit>
        for (ObligationDetailBean lOBDetailBean : lObligationDetails){
        	lOBBean = lOBDetailBean.getObligationBean();
        	lOBSplitBean = lOBDetailBean.getObligationSplitsBean();
        	if (!lObligations.containsKey(lOBBean.getId())){
        		lOblig = new Object[2];
        		lOblig[0] = lOBBean;
        		lOblig[1] = new ArrayList<ObligationSplitsBean>();
        		lObligations.put(lOBBean.getId(),lOblig);
        	}else{
        		lOblig = lObligations.get(lOBBean.getId());
        	}
    		lSplitList = (List<ObligationSplitsBean>) lOblig[1];
    		lSplitList.add(lOBSplitBean);
        }
        return lObligations;
	}
	
	private Map<Long, Object[]> getPurchaserObligations() throws Exception{
        StringBuilder lSql = new StringBuilder(); 
        List<ObligationDetailBean> lObligationDetails = null;
        DBHelper lDbHelper = DBHelper.getInstance();
        lSql.append(" SELECT ObligationDetails.* FROM ( ").append(" SELECT Obligations.*, ObligationSplits.*  FROM Obligations, ObligationSplits ").append(" ) ObligationDetails");
        lSql.append(" LEFT OUTER JOIN FACTORINGUNITS ON ( OBFUID = FUID  AND  FUPURCHASER IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(ClientAdapterManager.getInstance().getClientEntityList())).append( " ) ) " );
        lSql.append(" WHERE OBSOBID = OBID and OBTXNENTITY=FUPURCHASER AND OBPFID = ").append(paymentFileBean.getId());
        lSql.append(" AND ");
        lSql.append(" OBSRECORDVERSION > 0 AND OBRECORDVERSION > 0 ");
        lSql.append(" AND ");
        lSql.append(" OBSTATUS IN ( ").append(lDbHelper.formatString(Status.Success.getCode())).append(" , ");
        lSql.append(lDbHelper.formatString(Status.Failed.getCode())).append(" ) ");
        lSql.append(" AND OBTYPE = ").append(lDbHelper.formatString(Type.Leg_2.getCode()));
        lSql.append(" ORDER BY OBFUID,OBSOBID,OBSPARTNUMBER ");
        lObligationDetails  = obligationDetailDAO.findListFromSql(connection, lSql.toString(), -1);
        Map<Long,Object[]> lObligations = new HashMap<Long,Object[]>();
        ObligationBean lOBBean = null;
        ObligationSplitsBean lOBSplitBean = null;
        List <ObligationSplitsBean> lSplitList = null;
        Object[] lOblig = null;//0=ObligationBean, 1=List<ObligtionSplit>
        for (ObligationDetailBean lOBDetailBean : lObligationDetails){
        	lOBBean = lOBDetailBean.getObligationBean();
        	lOBSplitBean = lOBDetailBean.getObligationSplitsBean();
        	if (!lObligations.containsKey(lOBBean.getId())){
        		lOblig = new Object[2];
        		lOblig[0] = lOBBean;
        		lOblig[1] = new ArrayList<ObligationSplitsBean>();
        		lObligations.put(lOBBean.getId(),lOblig);
        	}else{
        		lOblig = lObligations.get(lOBBean.getId());
        	}
    		lSplitList = (List<ObligationSplitsBean>) lOblig[1];
    		lSplitList.add(lOBSplitBean);
        }
        return lObligations;
	}


	
	private void sendL1DetailsToClient(){
		Map<Long, Object[]> lSupplierObligationMap , lFactoredMap = null;
		List<Object[]> lList = new ArrayList<Object[]>();
		try {
			lSupplierObligationMap = getSupplierObligations();
			lFactoredMap = TredsHelper.getInstance().getFactoredBeans(connection,lSupplierObligationMap);
			ObligationBean lOBBean = null;
			Object[] lData = null;
			for (Object[] lTmp : lSupplierObligationMap.values()){
				lData = new Object[4];
				lOBBean = (ObligationBean) lTmp[0];
				Object[] lTmpFactored = lFactoredMap.get(lOBBean.getFuId());
				
				//lTmpFactored  = (0 FuBean , 1 InstBean) 
				lData[0] = lTmpFactored[0];
				lData[1] = lTmpFactored[1];
				
				//lTmp  = (0=ObligationBean, 1=List<ObligtionSplit>)
				lData[2] = lTmp[0];
				lData[3] = lTmp[1];
				lList.add(lData);
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		PostProcessMonitor.getInstance().addL1DetailsForProcessing(lList);
	}
	
	private void sendL2DetailsToClient(){
		Map<Long, Object[]> lObligationMap , lFactoredMap = null;
		List<Object[]> lList = new ArrayList<Object[]>();
		try {
			lObligationMap = getPurchaserObligations();
			lFactoredMap = TredsHelper.getInstance().getFactoredBeans(connection,lObligationMap);
			ObligationBean lOBBean = null;
			Object[] lData = null;
			for (Object[] lTmp : lObligationMap.values()){
				lData = new Object[4];
				lOBBean = (ObligationBean) lTmp[0];
				Object[] lTmpFactored = lFactoredMap.get(lOBBean.getFuId());
				
				//lTmpFactored  = (0 FuBean , 1 InstBean) 
				lData[0] = lTmpFactored[0];
				lData[1] = lTmpFactored[1];
				
				//lTmp  = (0=ObligationBean, 1=List<ObligtionSplit>)
				lData[2] = lTmp[0];
				lData[3] = lTmp[1];
				lList.add(lData);
			}
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		PostProcessMonitor.getInstance().addL2DetailsForProcessing(lList);
	}
	
//	private void test_TURNOVER() throws Exception{
//		Date lFYEndDate = null , lFYStartDate = null;
//		Date[] lTmpDates = TredsHelper.getInstance().getFinYearDates(TredsHelper.getInstance().getBusinessDate());
//		lFYStartDate = lTmpDates[0];
//		lFYEndDate = lTmpDates[1];
//		DBHelper lDBHelper = DBHelper.getInstance();
//
//		StringBuilder lSql = new StringBuilder();
//		lSql.append("  SELECT MTTURNOVER, MTCODE FROM MEMBERTURNOVER WHERE MTCODE IN  ( SELECT DISTINCT FUPURCHASER  FROM OBLIGATIONS, FACTORINGUNITS WHERE OBRECORDVERSION > 0 AND OBFUID = FUID AND OBPFID=").append(paymentFileBean.getId()).append(" ) ");
//		lSql.append(" AND MTFINYEARSTARTDATE = ").append(lDBHelper.formatDate(lFYStartDate));
//		lSql.append(" AND MTFINYEARENDDATE = ").append(lDBHelper.formatDate(lFYEndDate));
//		if (logger.isDebugEnabled())
//		    logger.debug(lSql.toString());
//		Statement lStatement = null;
//		ResultSet lResultSet = null;
//		try {
//			lStatement = connection.createStatement();
//		    lResultSet = lStatement.executeQuery(lSql.toString());
//		    while (lResultSet.next()){
//		    	System.out.println("here ---> "+lResultSet.getString("MTTURNOVER")+" : "+ lResultSet.getString("MTCODE"));
//		    }
//		    logger.info("TEST" );
//		} finally {
//			if (lStatement != null)
//				lStatement.close();
//		}
//	}
	

}
