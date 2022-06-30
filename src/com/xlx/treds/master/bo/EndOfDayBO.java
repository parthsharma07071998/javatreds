package com.xlx.treds.master.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.formula.ptg.MemErrPtg;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.AppEntityStatus;
import com.xlx.treds.AppConstants.CompanyApprovalStatus;
import com.xlx.treds.AppConstants.EmailSenders;
import com.xlx.treds.AppConstants.EntityEmail;
import com.xlx.treds.AppInitializer;
import com.xlx.treds.ClickWrapHelper;
import com.xlx.treds.MonetagoTredsHelper;
import com.xlx.treds.NotificationInfo;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.ClientAdapterManager;
import com.xlx.treds.adapter.IClientAdapter;
import com.xlx.treds.adapter.PostProcessMonitor;
import com.xlx.treds.adapter.ProcessInformationBean;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.auction.bean.FactoredPaymentBean;
import com.xlx.treds.auction.bean.FinancierAuctionSettingBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bo.FinancierSettlementFileGenerator;
import com.xlx.treds.auction.bo.ObligationBO;
import com.xlx.treds.auction.bo.PurchaserSupplierLimitUtilizationBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ApprovalStatus;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ChargeType;
import com.xlx.treds.entity.bean.RegistrationChargeBean.RequestType;
import com.xlx.treds.entity.bo.RegistrationChargeBO;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentBean.Status;
import com.xlx.treds.instrument.bo.EmailGeneratorBO;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.master.bean.AuctionCalendarBean;
import com.xlx.treds.master.bean.ConfirmationWindowBean;
import com.xlx.treds.master.bean.HolidayMasterBean;
import com.xlx.treds.master.bean.SystemParameterBean;
import com.xlx.treds.monitor.BulkReportGenerator;
import com.xlx.treds.other.bean.BuyerCreditRatingBean;
import com.xlx.treds.other.bean.CashInvoicePaymentAdviceGenerator;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.IAgreementAcceptanceBean;

public class EndOfDayBO {
    private static Logger logger = Logger.getLogger(EndOfDayBO.class);
    
    private static final int AUCTION_DAYS_CREATED_IN_EOD = 6;
    
    private GenericDAO<AuctionCalendarBean> auctionCalendarDAO;
    private GenericDAO<SystemParameterBean> systemParameterDAO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private CompositeGenericDAO<FactoredBean> factoredBeanDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private GenericDAO<ObligationBean> obligationDAO=null;
    private GenericDAO<ObligationSplitsBean> obligationSplitDAO=null;
    private PurchaserSupplierLimitUtilizationBO purchaserSupplierLimitUtilizationBO;
    private FactoringUnitBO factoringUnitBO;
    private GenericDAO<BidBean> bidDAO;
    private GenericDAO<AppUserBean> appUserDAO;
    private GenericDAO<BuyerCreditRatingBean> buyerCreditRatingDAO;
    private GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingBeanDAO=null;
    private CompositeGenericDAO<FactoredPaymentBean> factoredPaymentBeanDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailDAO;
    private GenericDAO<AppEntityBean> appEntitiesDAO;
    private GenericDAO<RegistrationChargeBean> registrationChargeDAO;
    private RegistrationChargeBO registrationChargeBO;
    
    public EndOfDayBO() {
        super();
        auctionCalendarDAO = new GenericDAO<AuctionCalendarBean>(AuctionCalendarBean.class);
        systemParameterDAO = new GenericDAO<SystemParameterBean>(SystemParameterBean.class);
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        obligationSplitDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
        purchaserSupplierLimitUtilizationBO = new PurchaserSupplierLimitUtilizationBO();
        factoringUnitBO = new FactoringUnitBO();
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
        factoredBeanDAO = new CompositeGenericDAO<FactoredBean>(FactoredBean.class);
        appUserDAO = new GenericDAO<AppUserBean>(AppUserBean.class);
        buyerCreditRatingDAO = new GenericDAO<BuyerCreditRatingBean>(BuyerCreditRatingBean.class);
        financierAuctionSettingBeanDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
        factoredPaymentBeanDAO = new CompositeGenericDAO<FactoredPaymentBean>(FactoredPaymentBean.class);
        companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
        appEntitiesDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
        registrationChargeBO = new RegistrationChargeBO();
        registrationChargeDAO = new GenericDAO<RegistrationChargeBean>(RegistrationChargeBean.class);
    }
    
    public List<Map<String, Object>> performeEOD(Connection pConnection, AppUserBean pAppUserBean,Map<String,Object> pAttachmentMap) throws Exception {
    	List<String> lMonetagoErrorMessageList = new ArrayList<String>();
    	Map<String,Object> lObligationShiftMessages = new HashMap<String,Object>();
    	Map<String,Object>  lDateRecalculationMessages = new HashMap<String,Object>();
    	//
    	if(pAttachmentMap==null){
    		pAttachmentMap = new HashMap<String, Object>();
    	}
    	//
    	pAttachmentMap.put("MonetagoErrorMessageList", lMonetagoErrorMessageList);
    	pAttachmentMap.put("ObligationShiftMessages", lObligationShiftMessages);
    	pAttachmentMap.put("DateRecalculationMessages", lDateRecalculationMessages);
    	
    	int lProcessCount = 1;
        checkAccess(pConnection, pAppUserBean);
        pConnection.setAutoCommit(false);
        List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();	
        SystemParameterBean lSystemParameterBean = systemParameterDAO.findBean(pConnection, (SystemParameterBean)null);
        Date lPreEODDate = lSystemParameterBean.getDate();
        logger.info("-------------------------------------E.O.D Started------------------------------");
        logger.info("Current Date : " + lSystemParameterBean.getDate());
        Date lNextDate = OtherResourceCache.getInstance().getNextTradingDate(lSystemParameterBean.getDate(), 1);
        lSystemParameterBean.setDate(lNextDate);
        systemParameterDAO.update(pConnection, lSystemParameterBean, BeanMeta.FIELDGROUP_UPDATE);
        appendMessage(lMessages, "Current Business Date", BeanMetaFactory.getInstance().getDateFormatter().format(lNextDate));
        logger.info("E.O.D "+lProcessCount +" Current Business Date" + lSystemParameterBean.getDate());
        // generate calendar for next 7 days
        generateAuctionCalendar(pConnection, lNextDate, pAppUserBean);
        logger.info("E.O.D "+ (lProcessCount++) +"  generated calendar for next 7 days. " );
        // expire factoring units
        int lCount = expireFactoringUnits(pConnection, lPreEODDate, lNextDate, pAppUserBean);
        appendMessage(lMessages, "Expired Factoring Units", lCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +"  ----("+ lCount +")----Factoring Units Expired " );
        // expire instruments
        lCount = expireInstruments(pConnection, lNextDate, lMonetagoErrorMessageList);
        appendMessage(lMessages, "Expired Instruments", lCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +  "  ----("+ lCount +")----Instruments Expired " );
        // expire CHILD instruments
        lCount = expireChildInstruments(pConnection, lNextDate, lMonetagoErrorMessageList);
        appendMessage(lMessages, "Expired Grouped Child Instruments", lCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +"  ----("+ lCount +")----Grouped Child Instruments Expired " );
        //
        lCount = notifyPSBanker(pConnection, lPreEODDate);
        appendMessage(lMessages, "Purchaser Supplier Notification", lCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +  "  ----("+ lCount +")----Purchaser Supplier Notification " );
      	EmailGeneratorBO lEmailGeneratorBO = new EmailGeneratorBO();
      	List<Map<String, Object>> lList =  null;
      	try{
          	lList = lEmailGeneratorBO.sendLeg2ObliNext5DaysDetails(pConnection, lNextDate);
          	for(Map<String,Object> lTmp : lList)
          		lMessages.add(lTmp);
          	logger.info("E.O.D "+ (lProcessCount++) + "  Leg2 Next 5days obligation mails Sent.");
      	}catch(Exception lEx){
      		logger.info("Error while sending Leg2 Next 5days obligation mails " + lEx.getMessage());
      	}
//      	try{
//      		//incase any bids left to expire after the end of 
//    		//second window then the bids will be expired here
//          	lCount = expireBid(lConnection,lNextDate);
//      	}catch(Exception lEx){
//      		logger.info("" + lEx.getMessage());
//      	}
//      	appendMessage(lMessages, "Expired Bids", lCount + " records");
//      	logger.info("E.O.D "+ (lProcessCount++) +"  ----("+ lCount +")----Bids Expired.");
//      	//expire bids
//        factoringUnitBO.updateBestBidAfterExpireBids(lConnection, lNextDate);
//        appendMessage(lMessages, "Best Bid Updated after expiry", "");
//        logger.info("E.O.D "+ (lProcessCount++) +"  Bids updated after expiry.");
      	
      	//Generate leg3 files
      	lList =  new ArrayList<Map<String,Object>>() ;
      	String lMsg = null;
      	try{
      		Date lNextClearingDate = OtherResourceCache.getInstance().getNextClearingDate(lPreEODDate, 1);
          	lList = generateLeg3Obligations(pConnection, pAppUserBean,lNextDate);
          	for(Map<String,Object> lTmp : lList){
          		for(Object lTmpMsg: lTmp.values()){
          			if(lMsg==null){
          				lMsg = (String) lTmpMsg; 
          			}else{
          				lMsg += (String)lTmpMsg; 
          			}
          		}
          	}
          	logger.info("E.O.D "+ (lProcessCount++) + "  Leg3 Obligation generation.");
      	}catch(Exception lEx){
      		lMsg = "Error while Leg3 Obligation generation";
      		logger.info("Error while Leg3 Obligation generation " + lEx.getMessage());
      	}
      	appendMessage(lMessages,"Leg3 Obligation generation.",lMsg);

      	//generate misReport
      	int[] lFinFilesCount = new int[] {0, 0 };
      	String lResult = "Success";
      	try {
      		FinancierSettlementFileGenerator lFinSettleFileGenerator = new FinancierSettlementFileGenerator();
      		lFinFilesCount = lFinSettleFileGenerator.generateMISReportFile(pConnection, lPreEODDate, lPreEODDate, null);
			logger.info("E.O.D "+ (lProcessCount++) + "  MIS report file generation.");
      	}catch(Exception lEx){
      		lResult = "Failed";
      		logger.info("Error while MIS report file generation " + lEx.getMessage());
      	}
      	appendMessage(lMessages,"Generate MIS Files for Financiers.", "Result : " + lResult + ". Total " + lFinFilesCount[1] + " files generated for " + lFinFilesCount[0] + " financiers.");
      	//
      	int[] lCounts = new int[] { 0,  0 };
      	lResult = "Success";
      	try {
      		lCounts = generateAnnualRegistrationCharges(pConnection,lPreEODDate, pAppUserBean,lNextDate);
			logger.info("E.O.D "+ (lProcessCount++) + "  Annual registration charge generation.");
      	}catch(Exception lEx) {
      		lResult = "Failed";
      		logger.info("Error while generating Annual charge " + lEx.getMessage());
      	}
      	appendMessage(lMessages,"Generate annual charge.", "Result : " + lResult + ". Total " + lCounts[0] + " charge generated out of " + lCounts[1] + " .");
      	//
      	lResult = "Success";
      	try {
      		lCounts = new int[] { 0,  0 };
      		lCounts = registrationChargeBO.generateBills(pConnection,pAppUserBean);
			logger.info("E.O.D "+ (lProcessCount++) + "  Registration/Annual charge bill generation.");
      	}catch(Exception lEx) {
      		lResult = "Failed";
      		logger.info("Error while generating bills of Registration/Annual charge " + lEx.getMessage());
      	}
      	appendMessage(lMessages,"Generate bills of reg/annual charge.", "Result : " + lResult + ". Total " + lCounts[0] + " charge generated out of " + lCounts[1] + " .");
      	//
        // Registration User : If the registration user has not submitted his form after 30 days of creation then deactivate his account login
    	Long lDaysToAdd = TredsHelper.getInstance().getExpiryDays();
		if (lDaysToAdd!=null){
    		lCount = deactivateDormantRegistrations(pConnection, lDaysToAdd, lPreEODDate , pAppUserBean);
        appendMessage(lMessages, "Deactivated RegUsers", lCount+"");
        logger.info("E.O.D "+ (lProcessCount++) +"  ----("+ lCount +")----Dormant registrations deactivated. " );
		}
      
		int lUpdatedLimitCount = updateLimits(pConnection);
        appendMessage(lMessages, "Limits "+(lUpdatedLimitCount<0?"Update Failed":"Updated"), lUpdatedLimitCount+"");
    	//
        //AnnualRegistrationCharges
        generateAnnualRegistrationCharges(pConnection,lPreEODDate , null, lNextDate);
        extendAnnualRegistrationCharges(pConnection, lPreEODDate, null, lNextDate);
		//
        
      	pConnection.commit();
        logger.info("-------------------------------------E.O.D Ended------------------------------");
        MemoryDBManager.getInstance().reloadTableDistributed(AuctionCalendarBean.ENTITY_NAME);
        
        Long lDaysNearing =  RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_FINANCIERLIMITEXPIRYNEARINGDAYS);
        int lMailedFinaciersCount = notifyExpiryOfLimits(pConnection, lDaysNearing, lPreEODDate);
        appendMessage(lMessages, "Total Finaciers mailed for Limit nearing Expiry", lCount+"");
        logger.info("E.O.D "+ (lProcessCount++) +"  ----("+ lMailedFinaciersCount +")----Financier notified of expiring limit levels. " );
        
        
//        POST EOD MODIFICATIONS
//        OBLIGATION SHIFTS
//        LOGIC - Get list of next 6 days
//        Get list of auction calendars and go through Confirmation windows, and get list of all dates having obligation settlement dates
        
       	Date[] lSettlementDates = new Date[AUCTION_DAYS_CREATED_IN_EOD];
    	//
    	lSettlementDates[0] = OtherResourceCache.getInstance().getNextDate(lPreEODDate, 1);
    	for(int lPtr=1; lPtr < AUCTION_DAYS_CREATED_IN_EOD; lPtr++){
    		lSettlementDates[lPtr] = OtherResourceCache.getInstance().getNextDate(lSettlementDates[lPtr-1], 1);
    	}
        String lDisplayDates = "";
        boolean lObliShiftComplete;
        List<Map<String,Object>> lObligationShiftErrorMessages = null;
        List<Map<String,Object>> lDateRecalculationErrorMessages = null; 
        for(int lPtr=0; lPtr < AUCTION_DAYS_CREATED_IN_EOD; lPtr++){
        	lObliShiftComplete = false;
    		try{
    			lObligationShiftMessages.put(lSettlementDates[lPtr].toString(),performObligationShift(pConnection, pAppUserBean, lSettlementDates[lPtr]));
    			lObliShiftComplete = true;
    			lDateRecalculationMessages.put(lSettlementDates[lPtr].toString(),performDateRecalculation(pConnection, pAppUserBean, lSettlementDates[lPtr]));
            	lDisplayDates += (((lPtr>0)?", " : "") + lSettlementDates[lPtr]); 
    		}catch(Exception lEx){
    			logger.info("Error in EOD : " + lEx.getMessage());
    			if (!lObliShiftComplete){
    				logger.info("Error while Performing Obligation Shift for Date :"+lSettlementDates[lPtr]);
    				logger.info(lEx.getMessage());
    				lObligationShiftErrorMessages = new ArrayList<Map<String,Object>>();
    				appendMessage(lObligationShiftErrorMessages, "Error", lEx.getMessage());
    				lObligationShiftMessages.put(lSettlementDates[lPtr].toString(),lObligationShiftErrorMessages);
    			}else{
    				logger.info("Error while Performing Date Recalculation for Date :"+lSettlementDates[lPtr]);
    				logger.info(lEx.getMessage());
    				lObligationShiftErrorMessages = new ArrayList<Map<String,Object>>();
    				appendMessage(lDateRecalculationErrorMessages, "Error  : ",lEx.getMessage());
    				lDateRecalculationMessages.put(lSettlementDates[lPtr].toString(), lObligationShiftErrorMessages);
    			}
    		}
    	}
        appendMessage(lMessages, "Obligation shifted", "for date's :"+lDisplayDates);
        logger.info("E.O.D "+ (lProcessCount++) +  "Performed obligation shift for Dates :  ----("+ lDisplayDates +")----" );
        appendMessage(lMessages, "Inst/FactUnit 'Expiry Shifted' ", "for date's :"+lDisplayDates);
        logger.info("E.O.D "+ (lProcessCount++) +  "Performed date recalculation for Dates :  ----("+ lDisplayDates +")----" );
        //
        try {
        	BulkReportGenerator lBulkReportGenerator = new BulkReportGenerator("BulkReportConfig.json"); 
    		FileDownloadBean lFileDownloadBean = lBulkReportGenerator.generateBulkReport(pConnection);
    		lBulkReportGenerator.sendEmail(lFileDownloadBean);
        }catch (Exception lEx){
        	
        }
        
        //Modified change in ratig
        int lBuyerModificationRatingCount = notifyBuyerModificationInRating(pConnection);
        appendMessage(lMessages, "Modified Buyer Rating", lBuyerModificationRatingCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +  "  ----("+ lBuyerModificationRatingCount +")----Modified Buyer Rating" );
        
        //Expiry in rating notification
        int lBuyerExpiryRatingCount = notifyBuyerExpiryRating(pConnection);
        appendMessage(lMessages, "Notify Buyer Expiry Rating", lBuyerExpiryRatingCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +  "  ----("+ lBuyerExpiryRatingCount +")----Notify Buyer Expiry Rating" );
		//
        
        //Payment Advice
        int lPaymentAdviceCount =  0;
        CashInvoicePaymentAdviceGenerator lCIPAGenerator = new CashInvoicePaymentAdviceGenerator();
        //TODO:HARDCODING - TO REMOVE 
        Date lSettlementDate = lPreEODDate;
        if(lSettlementDate!=null){
            lPaymentAdviceCount = lCIPAGenerator.cashInvoicePayAdvice(pConnection,lSettlementDate);
        }
        appendMessage(lMessages, "Payment Advice ", lPaymentAdviceCount + " records");
        logger.info("E.O.D "+ (lProcessCount++) +  "  ----("+ lPaymentAdviceCount +")----Payment Advice" );
        
        
        
        return lMessages;
    }
    
    public List<Map<String, Object>> performArchive(ExecutionContext pExecutionContext, AppUserBean pAppUserBean) throws Exception {
        checkAccessAfterEodBeforeStart(pAppUserBean);
        pExecutionContext.setAutoCommit(false);
        List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
        Connection lConnection = pExecutionContext.getConnection();
        SystemParameterBean lSystemParameterBean = systemParameterDAO.findBean(lConnection, (SystemParameterBean)null);
        logger.info("Current Date : " + lSystemParameterBean.getDate());
        // archive factoringunits, factoringunits_a, bids, bids_a, instruments, instruments_a
        // mark instruments and factoringunits to be archived
        int lCount = markArchive(lConnection, lSystemParameterBean.getDate());
        appendMessage(lMessages, "Instruments Marked for Archiving", lCount + " records");
        Connection lSourceConnection = pExecutionContext.getConnection();
        Connection lArchConnection = DBHelper.getInstance().getConnection("Arch");
        lArchConnection.setAutoCommit(false);
        
        lCount = archiveData(lSourceConnection, lArchConnection, "Instruments", " INId IN (SELECT INId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Instrument", lCount + " records");
        lCount = archiveData(lSourceConnection, lArchConnection, "Instruments_A", " INId IN (SELECT INId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Instruments Audit", lCount + " records");
        lCount = archiveData(lSourceConnection, lArchConnection, "InstrumentWorkFlow", " IWFInId IN (SELECT INId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Instrument Work Flow", lCount + " records");

        lCount = archiveData(lSourceConnection, lArchConnection, "FactoringUnits", " FUId IN (SELECT INFuId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Factoring Units", lCount + " records");
        lCount = archiveData(lSourceConnection, lArchConnection, "FactoringUnits_A", " FUId IN (SELECT INFuId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Factoring Units Audit", lCount + " records");
        lCount = archiveData(lSourceConnection, lArchConnection, "Bids", " BDFuId IN (SELECT INFuId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Bids", lCount + " records");
        lCount = archiveData(lSourceConnection, lArchConnection, "Bids_A", " BDFuId IN (SELECT INFuId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Bids Audit", lCount + " records");
        lCount = archiveData(lSourceConnection, lArchConnection, "Obligations", " OBFuId IN (SELECT INFuId FROM InstrumentArchiveTemp)");
        appendMessage(lMessages, "Archived Obligations", lCount + " records");

        lArchConnection.commit();
        pExecutionContext.commitAndDispose();
        return lMessages;
    }
    
    private int markArchive(Connection pConnection, Date pDate) throws Exception {
        Long lLong = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_ARCHIVEDAYCOUNT);
        int lArchiveDayCount = lLong==null?7:lLong.intValue();
        DBHelper lDBHelper = DBHelper.getInstance();
        executeUpdate(pConnection, "DELETE FROM InstrumentArchiveTemp");
        StringBuilder lSql = new StringBuilder();
        lSql.append("INSERT INTO InstrumentArchiveTemp SELECT INId,INFuId FROM Instruments WHERE INRecordVersion <= 0 OR (INStatus IN (");
        lSql.append(lDBHelper.formatString(InstrumentBean.Status.Expired.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Leg_1_Failed.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Leg_2_Settled.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Leg_2_Failed.getCode()));
        lSql.append(") AND INStatusUpdateTime + ").append(lArchiveDayCount).append(" < ").append(lDBHelper.formatDate(pDate)).append(")");
        lSql.append(" UNION SELECT NULL,FUId FROM FactoringUnits WHERE FURecordVersion <= 0 OR FUStatus = ");
        lSql.append(lDBHelper.formatString(FactoringUnitBean.Status.Withdrawn.getCode()));
        //FOR CHILD INSTRUMENTS
        //ALSO MARK THE CHILD INSTRUMENTS WHOSE PARENT HAS BEEN ARCHIVED.
        lSql.append(" UNION SELECT ChildInst.INID INId,ParentInst.INFuID INFuId FROM Instruments ParentInst ");
        lSql.append(" RIGHT OUTER JOIN Instruments ChildInst ");
        lSql.append(" ON ( ParentInst.INGROUPFLAG = 'Y' AND ParentInst.INID = ChildInst.INGROUPINID ) ");
        lSql.append(" WHERE ParentInst.INRecordVersion <= 0 OR (ParentInst.INStatus IN (");
        lSql.append(lDBHelper.formatString(InstrumentBean.Status.Expired.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Leg_1_Failed.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Leg_2_Settled.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Leg_2_Failed.getCode()));
        lSql.append(") AND ParentInst.INStatusUpdateTime + ").append(lArchiveDayCount).append(" < ").append(lDBHelper.formatDate(pDate)).append(")");
        //
        return executeUpdate(pConnection, lSql.toString());
    }
    
    private int archiveData(Connection pSrcConnection, Connection pArchConnection, String pTable, String pFilter) throws Exception {
        Statement lSourceStatement = null;
        ResultSet lSourceResultSet = null;
        PreparedStatement lDestinationStatement = null;
        try {
            lSourceStatement = pSrcConnection.createStatement();
            String lSelectSql = "SELECT * FROM " + pTable + " WHERE " + pFilter;
            lSourceResultSet = lSourceStatement.executeQuery(lSelectSql);
            StringBuffer lInsertSql = new StringBuffer();
            lInsertSql.append("INSERT INTO ").append(pTable).append("_Arch VALUES (");
            int lColCount = lSourceResultSet.getMetaData().getColumnCount();
            for (int lColPtr=0;lColPtr<lColCount;lColPtr++) {
                if (lColPtr > 0) lInsertSql.append(","); 
                lInsertSql.append("?");
            }
            lInsertSql.append(")");
            logger.debug(lSelectSql);
            logger.debug(lInsertSql.toString());
            
            lDestinationStatement = pArchConnection.prepareStatement(lInsertSql.toString());
            int lCount = 0;
            while (lSourceResultSet.next())
            {
                for (int lColPtr=1;lColPtr<=lColCount;lColPtr++)
                    lDestinationStatement.setObject(lColPtr, lSourceResultSet.getObject(lColPtr));
                lDestinationStatement.executeUpdate();
                lCount++;
            }
            // delete
            int lDeleteCount = executeUpdate(pSrcConnection, "DELETE FROM " + pTable + " WHERE " + pFilter);
            if (lCount != lDeleteCount)
                throw new Exception("Mismatch in delete and archive counts for table " + pTable + ". Archived : " + lCount + ", Deleted : " + lDeleteCount);
            return lCount;
        } finally {
            if (lSourceResultSet != null)
                lSourceResultSet.close();
            if (lSourceStatement != null)
                lSourceStatement.close();
            if (lDestinationStatement != null)
                lDestinationStatement.close();
        }
    }
    private int expireFactoringUnits(Connection pConnection, Date pCurrentTradingDate, Date pNextTradingDate, AppUserBean pAppUserBean) throws Exception {
    	int lRetVal = 0;
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("UPDATE FactoringUnits SET FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Expired.getCode()));
		lSql.append(" , FUSTATUSUPDATETIME = sysdate ");
        lSql.append(" WHERE FURecordVersion > 0 AND FUStatus IN (").append(lDBHelper.formatString(FactoringUnitBean.Status.Ready_For_Auction.getCode()));
        lSql.append(",").append(lDBHelper.formatString(FactoringUnitBean.Status.Active.getCode()));
        //withdrawn factoring unit should be kept as is - should not be changed to expired.
        lSql.append(",").append(lDBHelper.formatString(FactoringUnitBean.Status.Suspended.getCode())).append(")");
        lSql.append(" AND FUFactorEndDateTime < ").append(lDBHelper.formatDate(pNextTradingDate));
        lRetVal = executeUpdate(pConnection, lSql.toString());
        //logger.debug("expireFactoringUnits : " + lSql.toString());
        if(lRetVal>0){
        	FactoringUnitBean lFactoringUnitBean = null;
        	InstrumentBean lInstrumentBean = null;
        	FactoringUnitBO lFactoringUnitBO = new FactoringUnitBO();
        	List<FactoredBean> lFactoredBean = getExpiredFactoringUnits(pConnection, pCurrentTradingDate, pNextTradingDate);
        	//
        	if(lFactoredBean!=null && lFactoredBean.size() > 0){
        		for(int lPtr=0; lPtr < lFactoredBean.size(); lPtr++){
        			lFactoringUnitBean = lFactoredBean.get(lPtr).getFactoringUnitBean();
        			lInstrumentBean = lFactoredBean.get(lPtr).getInstrumentBean();
                	lFactoringUnitBO.releaseLimits(pConnection, lFactoringUnitBean, pAppUserBean);
        		}
        	}
        	//TODO: REMOVAL OF SUPPLIERS CLICK WRAP AGREEMENTS IF FACTORING UNIT EXPIRES
        }
        return lRetVal;
    }
    
    public int expireInstruments(Connection pConnection, Date pCurDate, List<String> pMonetagoErrorMessageList) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("Select * from instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NULL ");//excluding child instruments from getting expired.
        lSql.append(" AND INStatus IN (").append(lDBHelper.formatString(InstrumentBean.Status.Drafting.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Submitted.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Approved.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Returned.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Checker_Rejected.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Approved.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Returned.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Rejected.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Checker_Pending.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Counter_Checker_Return.getCode()));
        lSql.append(",").append(lDBHelper.formatString(InstrumentBean.Status.Converted_To_Factoring_Unit.getCode())).append(")");
        //withdrawn instrument is actually logically removed, hence - should not be changed to expired.
        lSql.append(" AND (INFactorMaxEndDateTime < ").append(lDBHelper.formatDate(pCurDate));
        lSql.append(" OR INFUId IN (SELECT FUId FROM FactoringUnits WHERE FURecordVersion > 0 AND FUStatus = ");
        lSql.append(lDBHelper.formatString(FactoringUnitBean.Status.Expired.getCode())).append("))");
        
        int lExpireCount = 0;
	   	 List<InstrumentBean> lInstrumentBeans = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
	     for (InstrumentBean lInstrumentBean : lInstrumentBeans ){
	         if(MonetagoTredsHelper.getInstance().performMonetagoCheck()){
	 	     	try{
	 	     		if(!InstrumentBean.Status.Expired.equals(lInstrumentBean.getStatus()) 
		 	     			&& StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) ){
			     		Map<String,String> lResult= new HashMap<String, String>();
			     		String lInfoMessage = "Instrument No :"+lInstrumentBean.getId()+"  Factoring Unit No :"+lInstrumentBean.getFuId();
			 			lResult=MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.NotFinanced, lInfoMessage,lInstrumentBean.getId());
			 			if(StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))){
			 				lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
			 				lInstrumentBean.setMonetagoLedgerId("");
			 				logger.info("Message:  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
			 			}else{
        	    			logger.info("Message Error :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
        	    			if(pMonetagoErrorMessageList!=null){
            	    			pMonetagoErrorMessageList.add("Instrument No :"+lInstrumentBean.getId()+"  "+lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
        	    			}
        	    			throw new CommonBusinessException("Error while Expiring: " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
        	    		}
	 	     		}
	 	     	}catch(Exception lException){
					logger.error(lException.getMessage());
    				logger.error(lException.getStackTrace());
				}
	        }//monetago check
	         //the following check is done since some instrument will expire along with the factoring unit, where we have handled the same.
	         //the reamaining instruments which are not converted to factoring units will be expired here.
	        if(!InstrumentBean.Status.Expired.equals(lInstrumentBean.getStatus())){
	 	     	lInstrumentBean.setStatus(Status.Expired);
	 	     	lInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		     	instrumentDAO.update(pConnection, lInstrumentBean,InstrumentBean.FIELDGROUP_UPDATEEXPIREINSTRUMENT);
		     	lExpireCount++;
	        	List<IAgreementAcceptanceBean> lList = ClickWrapHelper.getInstance().getAcceptedAggrementDetails(pConnection, lInstrumentBean.getPurchaser(), lInstrumentBean.getId());
	        	ClickWrapHelper.getInstance().removeClickWrapAgreements(pConnection, lList, new Long(1));
    			//
            	if(InstrumentBean.Status.Expired.equals(lInstrumentBean.getStatus()) && 
            		!CommonAppConstants.Yes.Yes.equals(lInstrumentBean.getGroupFlag())){
    				IClientAdapter lClientAdapter = ClientAdapterManager.getInstance().getClientAdapter(lInstrumentBean.getAdapterEntity());
    				if (lClientAdapter!=null){
    					try (Connection lAdapterConnection = DBHelper.getInstance().getConnection();){
    						ProcessInformationBean lProcessInformationBean = new ProcessInformationBean(ProcessInformationBean.PROCESSID_FACTORINGUNIT_STATUS, lAdapterConnection);
        					lProcessInformationBean.setTredsDataForProcessing(new Object[] {null, lInstrumentBean});
        					lProcessInformationBean.setKey(lInstrumentBean.getId().toString());
    						lClientAdapter.sendResponseToClient(lProcessInformationBean);
						//TODO: check whether post process is required
        					PostProcessMonitor.getInstance().addPostProcess(lClientAdapter, lProcessInformationBean, null);
    					}catch(Exception lEx) {
    						logger.error(lEx.getMessage());
    						logger.error(lEx.getStackTrace());
    					}
    				}
    			}
            	//
        	}
     	}
        return lExpireCount;
    }
    
    private int expireChildInstruments(Connection pConnection, Date pCurDate, List<String> pMonetagoErrorMessageList) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("Select * from instruments WHERE INRecordVersion > 0 AND INGROUPINID IS NOT NULL ");//excluding child instruments from getting expired.
        lSql.append(" AND INID IN ( ");
        lSql.append(" Select INID from instruments WHERE INRecordVersion > 0 AND INGROUPFLAG = 'Y' ");//excluding child instruments from getting expired.
        lSql.append(" AND INStatus = ").append(lDBHelper.formatString(InstrumentBean.Status.Expired.getCode()));
        lSql.append(" AND (INFactorMaxEndDateTime < ").append(lDBHelper.formatDate(pCurDate));
        lSql.append(" OR INFUId IN (SELECT FUId FROM FactoringUnits WHERE FURecordVersion > 0 AND FUStatus = ");
        lSql.append(lDBHelper.formatString(FactoringUnitBean.Status.Expired.getCode())).append("))");
        lSql.append(" ) ");
        
        int lExpireCount = 0;
	   	 List<InstrumentBean> lInstrumentBeans = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
	     for (InstrumentBean lInstrumentBean : lInstrumentBeans ){
	         if(MonetagoTredsHelper.getInstance().performMonetagoCheck()){
	 	     	try{
	 	     		if(StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId()) ){
			     		Map<String,String> lResult= new HashMap<String, String>();
			     		String lInfoMessage = "Instrument No :"+lInstrumentBean.getId()+"  Factoring Unit No :"+lInstrumentBean.getFuId();
			 			lResult=MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.NotFinanced, lInfoMessage,lInstrumentBean.getId());
			 			if(StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))){
			 				lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
			 				lInstrumentBean.setMonetagoLedgerId("");
			 				logger.info("Message:  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
			 			}else{
        	    			logger.info("Message Error :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
        	    			if(pMonetagoErrorMessageList!=null){
            	    			pMonetagoErrorMessageList.add(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
        	    			}
        	    			throw new CommonBusinessException("Error while Expiring: " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
        	    		}
	 	     		}
	 	     	}catch(Exception lException){
	 	     		//add to the list
					logger.error(lException.toString());
				}
	        }//monetago check
	         //the following check is done since some instrument will expire along with the factoring unit, where we have handled the same.
	         //the reamaining instruments which are not converted to factoring units will be expired here.
	        if(!InstrumentBean.Status.Expired.equals(lInstrumentBean.getStatus())){
	 	     	lInstrumentBean.setStatus(Status.Expired);
	 	     	lInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
		     	instrumentDAO.update(pConnection, lInstrumentBean,InstrumentBean.FIELDGROUP_UPDATEEXPIREINSTRUMENT);
		     	lExpireCount++;
        	}
     	}
        return lExpireCount;
    }
    
    private void generateAuctionCalendar(Connection pConnection, Date pCurDate, AppUserBean pAppUserBean) throws Exception {
        Date lEpochDate = new Date(0);
        AuctionCalendarBean lFilterBean = new AuctionCalendarBean();
        lFilterBean.setDate(lEpochDate);
        lFilterBean.setActive(CommonAppConstants.YesNo.Yes);
        List<AuctionCalendarBean> lTemplateBeanList = auctionCalendarDAO.findList(pConnection, lFilterBean, (String)null);
        Date lNextDate = pCurDate;
        for (int lPtr=0;lPtr<7;lPtr++) {
            if (lPtr > 0)
                lNextDate = OtherResourceCache.getInstance().getNextTradingDate(lNextDate, 1);
            for (AuctionCalendarBean lTemplateBean : lTemplateBeanList) {
                lFilterBean.setDate(lNextDate);
                lFilterBean.setType(lTemplateBean.getType());
                AuctionCalendarBean lDBBean = auctionCalendarDAO.findBean(pConnection, lFilterBean);
                if (lDBBean == null) {
                    lDBBean = new AuctionCalendarBean();
                    lDBBean.setType(lTemplateBean.getType());
                    lDBBean.setDate(lNextDate);
                    lDBBean.setActive(CommonAppConstants.YesNo.Yes);
                    long lOffset = lNextDate.getTime() - lTemplateBean.getDate().getTime();
                    lDBBean.setBidStartTime(new Timestamp(lTemplateBean.getBidStartTime().getTime() + lOffset ));
                    lDBBean.setBidEndTime(new Timestamp(lTemplateBean.getBidEndTime().getTime() + lOffset));
                    lDBBean.setRecordCreator(pAppUserBean.getId());
                    List<ConfirmationWindowBean> lConfWinList = new ArrayList<ConfirmationWindowBean>();
                    lDBBean.setConfWinList(lConfWinList);
                    if (lTemplateBean.getConfWinList() != null) {
                    	Date lPrevSettlementDate = null,lPrevTemplateSettlementDate = null;
                    	int lClearingHolidayOffset = 0;
                        for (ConfirmationWindowBean lTemplateConfirmationWindowBean : lTemplateBean.getConfWinList()) {
                            if (lTemplateConfirmationWindowBean.getActive() == CommonAppConstants.YesNo.Yes) {
                				if(CommonAppConstants.YesNo.Yes.equals(lTemplateConfirmationWindowBean.getSkipClearingHoliday())){
                                	if(isTradingDayAndClearingHoliday(lNextDate)){
                                		lClearingHolidayOffset=1;
                                	}
                            	}
                                ConfirmationWindowBean lConfirmationWindowBean = new ConfirmationWindowBean();
                                if(lPrevSettlementDate==null)
                                {
                                	lPrevSettlementDate = new Date(lTemplateConfirmationWindowBean.getSettlementDate().getTime() + lOffset);
                                	lPrevSettlementDate = OtherResourceCache.getInstance().getNextClearingDate(lPrevSettlementDate, lClearingHolidayOffset);
                                    lConfirmationWindowBean.setSettlementDate(lPrevSettlementDate);
                                }
                                else
                                {
                                	if(lClearingHolidayOffset!=1){
                                    	lPrevSettlementDate = new Date(lPrevSettlementDate.getTime() + (lTemplateConfirmationWindowBean.getSettlementDate().getTime()-lPrevTemplateSettlementDate.getTime()));
                                    	lPrevSettlementDate = OtherResourceCache.getInstance().getNextClearingDate(lPrevSettlementDate, 0);
                                	}
                                    lConfirmationWindowBean.setSettlementDate(lPrevSettlementDate);
                                }
                                lPrevTemplateSettlementDate = lTemplateConfirmationWindowBean.getSettlementDate();
                                //
                                lConfirmationWindowBean.setConfStartTime(new Timestamp(lTemplateConfirmationWindowBean.getConfStartTime().getTime() + lOffset));
                                lConfirmationWindowBean.setConfEndTime(new Timestamp(lTemplateConfirmationWindowBean.getConfEndTime().getTime() + lOffset));
                                lConfirmationWindowBean.setActive(CommonAppConstants.YesNo.Yes);
                                lConfWinList.add(lConfirmationWindowBean);
                            }
                        }
                    }
                    auctionCalendarDAO.insert(pConnection, lDBBean);
                }// end if dbbean==null
            }// end for lTemplateBean
        }// end for lPtr
        
    }

    private boolean isTradingDayAndClearingHoliday(Date pDate){
		Date[] lFilter = new Date[] { pDate };
		MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(HolidayMasterBean.ENTITY_NAME);
		HolidayMasterBean lHolidayMasterBean=null;
		try {
			lHolidayMasterBean = (HolidayMasterBean) lMemoryTable.selectSingleRow(HolidayMasterBean.f_Date, lFilter);
			if (lHolidayMasterBean != null
					&& !HolidayMasterBean.Type.Trading.equals(lHolidayMasterBean.getType())
					&& HolidayMasterBean.Type.Clearing.equals(lHolidayMasterBean.getType())) {
				return true;
			}
		} catch (MemoryDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
    }
    
    private void checkAccess(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
        // query current auction calendar from database. To mitigate auction calendar memory table replication issues
        GenericDAO<AuctionCalendarBean> lAuctionCalendarDAO = new GenericDAO<AuctionCalendarBean>(AuctionCalendarBean.class);
        String lSql = "SELECT * FROM AuctionCalendar WHERE ACRecordVersion > 0 AND ACType = " + DBHelper.getInstance().formatString(OtherResourceCache.AUCTIONTYPE_NORMAL) 
                + " AND ACDate = (SELECT SPDate FROM SystemParameters WHERE SPRecordVersion > 0) ";

        AuctionCalendarBean lCurrentAuctionCalendarBean = lAuctionCalendarDAO.findBean(pConnection, lSql);
        if(lCurrentAuctionCalendarBean!=null)
        {
            if (lCurrentAuctionCalendarBean.getStatus() != AuctionCalendarBean.Status.Closed)
                throw new CommonBusinessException("Action cannot be performed now");
            if (lCurrentAuctionCalendarBean.getConfWinList() != null) {
                for (ConfirmationWindowBean lConfirmationWindowBean : lCurrentAuctionCalendarBean.getConfWinList()) {
                    if (lConfirmationWindowBean.getStatus() != ConfirmationWindowBean.Status.Closed)
                        throw new CommonBusinessException("EOD cannot be perfomed now. Confirmation windows not yet closed");
                }
            }
        }
    }
    
    private void checkAccessAfterEodBeforeStart(AppUserBean pAppUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pAppUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        
        AuctionCalendarBean lCurrentAuctionCalendarBean = OtherResourceCache.getInstance().getAuctionCalendarBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        if(lCurrentAuctionCalendarBean!=null)
        {
            if (lCurrentAuctionCalendarBean.getStatus() != AuctionCalendarBean.Status.Pending)
                throw new CommonBusinessException("Action cannot be performed now");
            if (lCurrentAuctionCalendarBean.getConfWinList() != null) {
                for (ConfirmationWindowBean lConfirmationWindowBean : lCurrentAuctionCalendarBean.getConfWinList()) {
                    if (lConfirmationWindowBean.getStatus() != ConfirmationWindowBean.Status.Pending)
                        throw new CommonBusinessException("Action cannot be performed now");
                }
            }
        }
    }
    
    public static void appendMessage(List<Map<String, Object>> pMessages, String pAction, String pRemarks) {
        Map<String, Object> lMap = new HashMap<String, Object>();
        lMap.put("act", pAction);
        lMap.put("rem", pRemarks);
        if(pMessages!=null)
        	pMessages.add(lMap);
    }
    
    private int executeUpdate(Connection pConnection, String pSql) throws Exception {
        logger.debug(pSql.toString());
        Statement lStatement = null;
        try {
            lStatement = pConnection.createStatement();
            return lStatement.executeUpdate(pSql.toString());
        } finally {
            if (lStatement != null)
                lStatement.close();
        }
    }
    
    public List<Map<String, Object>> generateLeg3Obligations(Connection pConnection, AppUserBean pAppUserBean) throws Exception {
    	//to generate Leg3 obligations for expired factoring units
    	checkAccessAfterEodBeforeStart(pAppUserBean);
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	//
    	SystemParameterBean lSystemParameterBean = systemParameterDAO.findBean(pConnection, (SystemParameterBean)null);
        ConfirmationWindowBean lConfirmationWindowBean = OtherResourceCache.getInstance().getCurrentNextConfirmationWindowBean(OtherResourceCache.AUCTIONTYPE_NORMAL);
        if(lConfirmationWindowBean!=null && 
        		ConfirmationWindowBean.Status.Pending.equals(lConfirmationWindowBean.getStatus())){
    		lMessages = generateLeg3Obligations(pConnection, pAppUserBean, lSystemParameterBean.getDate());
    	}else{
	        appendMessage(lMessages, "Leg3 Obligation generation", " No Expired Factoring Units found." );
    	}
        return lMessages;
    }

    public List<Map<String, Object>> generateLeg3Obligations(Connection pConnection, AppUserBean pAppUserBean, Date pBusinessDate) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	//to generate Leg3 obligations for expired factoring units
    	FactoringUnitBean lFactoringUnitBean = null;
    	InstrumentBean lInstrumentBean = null;
    	List<FactoredBean> lFactoredBeans = null;
        Date lPrevDate = OtherResourceCache.getInstance().getPreviousTradingDate(pBusinessDate, 1);
        Date lNextDate = pBusinessDate;
        int lObligationCount = 0;
    	lFactoredBeans = getExpiredFactoringUnits(pConnection, lPrevDate, lNextDate);
    	if(lFactoredBeans!=null && lFactoredBeans.size() > 0){
    		int lUtilReleaseCount=0;
	        appendMessage(lMessages, "Leg3 Obligation generation", " "+lFactoredBeans.size() + " Expired Factoring Units found." );
    		for(int lPtr=0; lPtr < lFactoredBeans.size(); lPtr++){
    			lFactoringUnitBean = lFactoredBeans.get(lPtr).getFactoringUnitBean();
    	        lInstrumentBean = lFactoredBeans.get(lPtr).getInstrumentBean();
    	        //
    	        lInstrumentBean.populateNonDatabaseFields();
	        	if(CommonAppConstants.YesNo.Yes.equals(lFactoringUnitBean.getSettleLeg3Flag())) {
        	        lObligationCount = factoringUnitBO.generateLeg3Obligations(pConnection, lFactoringUnitBean, lInstrumentBean, pAppUserBean, lFactoringUnitBean.getAmount(), false);
	        	}else{
	        		lObligationCount = 0;
	        	}
    	        if(lObligationCount>0){
        	        //update the factoring unit - mark it as leg 3 generated
    	        	lFactoringUnitBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
        	        lFactoringUnitBean.setStatus(FactoringUnitBean.Status.Leg_3_Generated);
        	        factoringUnitDAO.update(pConnection, lFactoringUnitBean, BeanMeta.FIELDGROUP_UPDATE);
        	        factoringUnitDAO.insertAudit(pConnection, lFactoringUnitBean, AuditAction.Update, pAppUserBean.getId());
        	        //
        	        lInstrumentBean.setStatus(InstrumentBean.Status.Leg_3_Generated);
        	        lInstrumentBean.setStatusUpdateTime(CommonUtilities.getCurrentDateTime());
        	        instrumentDAO.update(pConnection, lInstrumentBean,InstrumentBean.FIELDGROUP_UPDATESTATUS);
        	        instrumentDAO.insertAudit(pConnection, lInstrumentBean, AuditAction.Insert, pAppUserBean.getId());
        	        appendMessage(lMessages, "Leg3 Obligation generation", lObligationCount + " obligations generated for Factoring Unit : " + lFactoringUnitBean.getId());
    	        }
    	        else {
    	        	if(!CommonAppConstants.YesNo.Yes.equals(lFactoringUnitBean.getSettleLeg3Flag())) {
        	        	//leg3 obligations are not generated for the Expired FU, then release
        	        	BigDecimal lLimit = lFactoringUnitBean.getLimitUtilized();
        	        	if(lLimit==null) lLimit = new BigDecimal(0);
    	        		lFactoringUnitBean.setLimitUtilized(new BigDecimal(0));
                        purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(pConnection, lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getSupplier(), pAppUserBean, lLimit, true);
                        factoringUnitBO.updateStatus(pConnection, lFactoringUnitBean, lFactoringUnitBean.getStatus(), pAppUserBean);
                        lUtilReleaseCount++;
    	        	}
    	        }
    		}
	        appendMessage(lMessages, "Leg3 Obligation generation", " Utilisation released for " + lUtilReleaseCount + " expired instruments." );
    	}else{
	        appendMessage(lMessages, "Leg3 Obligation generation", " No Expired Factoring Units found." );
    	}    	
    	//
        return lMessages;
    }

    private List<FactoredBean> getExpiredFactoringUnits(Connection pConnection, Date pLastTradingDate, Date pNextTradingDate) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("SELECT * FROM FactoringUnits, Instruments ");
        lSql.append(" WHERE FURecordVersion > 0 AND INRecordVersion > 0 ");
        lSql.append(" AND FUID = INFUID ");
        lSql.append(" AND FUStatus = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Expired.getCode()));
        lSql.append(" AND FUFactorEndDateTime BETWEEN ").append(lDBHelper.formatDate(pLastTradingDate)).append(" AND ").append(lDBHelper.formatDate(pNextTradingDate));
        //logger.debug("getExpiredFactoringUnits : " + lSql.toString());
        //
        return factoredBeanDAO.findListFromSql(pConnection, lSql.toString(),-1);
    }

    public List<Map<String, Object>>  performObligationShift(Connection pConnection, AppUserBean pAppUserBean, Date pObligationDate) throws Exception {
        checkAccessAfterEodBeforeStart(pAppUserBean);
        pConnection.setAutoCommit(false);
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	ObligationBO lObligationBO = null;
    	ObligationBean lObligationFilterBean = null;
    	List<ObligationBean> lObligations = null;
    	ObligationBean lObligationBean = null;
    	SystemParameterBean lSystemParameterBean = systemParameterDAO.findBean(pConnection, (SystemParameterBean)null);
    	//
    	//validate both obligation dates
    	if(pObligationDate.before(lSystemParameterBean.getDate()))
    		throw new CommonBusinessException("Selected obligation date should be after the current business date.");
    	//check whether selected obligation date is holiday.
    	OtherResourceCache lOtherResourceCache = OtherResourceCache.getInstance();
    	Date lNewObliDate = lOtherResourceCache.getWorkingDate(pObligationDate, false, false, 0);
    	
    	if(pObligationDate.equals(lNewObliDate))
    		throw new CommonBusinessException("Selected date is not a Holiday.");
    	//
    	
		HolidayMasterBean lHolidayMasterBean = (HolidayMasterBean) MemoryDBManager.getInstance().getTable(HolidayMasterBean.ENTITY_NAME).selectSingleRow(HolidayMasterBean.f_Date, new Date[] { pObligationDate });
    	if (lHolidayMasterBean!= null && YesNo.Yes.equals(lHolidayMasterBean.getDisableShifting())) {
    		throw new CommonBusinessException("Obligations cannot be shifted as shifting disabled in Holiday Master");
    	}
		//find the obligation which are created and ready for that holiday date
    	lObligationBO = new ObligationBO();
    	lObligationFilterBean = new ObligationBean();
    	lObligationFilterBean.setDate(pObligationDate);
    	lObligationFilterBean.setFilterToDate(pObligationDate);
    	lObligations = lObligationBO.findList(pConnection, lObligationFilterBean, null, pAppUserBean);
    	//
    	if(lObligations != null && lObligations.size() > 0){
    		for(int lPtr=lObligations.size()-1; lPtr >= 0 ; lPtr--) {
    			lObligationBean = lObligations.get(lPtr);
    			if(!ObligationBean.Status.Created.equals(lObligationBean.getStatus()) &&
    					!ObligationBean.Status.Ready.equals(lObligationBean.getStatus())){
    				lObligations.remove(lPtr);
    			}
    		}
    	}
    	if(lObligations == null || lObligations.size() == 0){
        	appendMessage(lMessages, "No obligation found for the holiday date.", "-");
    	}
    	else{
	    	//
	    	Date lNextClearingDate = null;
	        Long lDayDiffrence = RegistryHelper.getInstance().getLong(AppConstants.REGISTRY_OBLIGATIONHOLIDAYPREPONEDAYS);
	        
	        if(lDayDiffrence!=null && lDayDiffrence.longValue()>0){
	        	if(pObligationDate.compareTo(lSystemParameterBean.getDate()) <= lDayDiffrence.longValue()){//prepone
	            	lNextClearingDate = OtherResourceCache.getInstance().getPreviousClearingDate(pObligationDate, 0);//        		
	        	}
	        	else {//postpone
	            	lNextClearingDate = OtherResourceCache.getInstance().getNextClearingDate(pObligationDate, 1);//
	        	}
	        }
	        else{
	        	lNextClearingDate = OtherResourceCache.getInstance().getNextClearingDate(pObligationDate, 1);//
	        }
	        //this condition may not arise
	    	if(!lSystemParameterBean.getDate().before(lNextClearingDate))
	    		throw new CommonBusinessException("New obligation date should be after the current business date.");
	    	//
			
	    	HashSet<Long> lFUIds = new HashSet<Long>();
	    	Map<Long,Long> lObIdMap = new HashMap<Long,Long>();  //Key=OldObId, Value=NewObId
	    	//update the existing obligation
	    	for(int lPtr=0; lPtr < lObligations.size(); lPtr++){
	    		lObligationBean= new ObligationBean();
	    		obligationDAO.getBeanMeta().copyBean(lObligations.get(lPtr), lObligationBean); //new obligation
	    		//new obligation changes
	    		lObligationBean.setOldObligationId(lObligationBean.getId());
	    		lObligationBean.setId(TredsHelper.getInstance().getObligationId(pConnection));
	    		lObligationBean.setDate(lNextClearingDate);
	    		lObligationBean.setRecordCreateTime(null);
	    		lObligationBean.setRecordCreator(pAppUserBean.getId());
	    		lObligationBean.setRecordUpdateTime(null);
	    		lObligationBean.setRecordUpdator(null);
	    		lObligationBean.setRecordVersion(new Long(1));
	    		obligationDAO.insert(pConnection, lObligationBean);
	    		lObIdMap.put(lObligationBean.getOldObligationId(),lObligationBean.getId());
	    		//old obligation changes
	    		lObligationBean = lObligations.get(lPtr); //old obligation
	    		lObligationBean.setStatus(ObligationBean.Status.Shifted);
	    		lObligationBean.setRecordUpdateTime(CommonUtilities.getCurrentDate());
	    		lObligationBean.setRecordUpdator(pAppUserBean.getId());
	    		lObligationBean.setRespRemarks("Shifted due to holiday.");
	    		obligationDAO.update(pConnection, lObligationBean);
	    		if(!lFUIds.contains(lObligationBean.getFuId())){
	    			lFUIds.add(lObligationBean.getFuId());
	            	appendMessage(lMessages, lObligationBean.getFuId().toString() + "'s obligations.", "Updated");
	    		}
	    	}
	    	shiftSplits(pConnection,lObIdMap,pAppUserBean);
	    	
    	}
    	pConnection.commit();
        return lMessages;
    }
    
    public List<Map<String, Object>>  performDateRecalculation(Connection pConnection, AppUserBean pAppUserBean, Date pHolidayDate) throws Exception {
        checkAccessAfterEodBeforeStart(pAppUserBean);
        pConnection.setAutoCommit(false);
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	SystemParameterBean lSystemParameterBean = systemParameterDAO.findBean(pConnection, (SystemParameterBean)null);
    	//
    	//validate both obligation dates
    	if(pHolidayDate.before(lSystemParameterBean.getDate()))
    		throw new CommonBusinessException("Selected holiday date should be after the current business date.");
    	//check whether selected date is holiday.
    	OtherResourceCache lOtherResourceCache = OtherResourceCache.getInstance();
    	Date lTempDate = lOtherResourceCache.getWorkingDate(pHolidayDate, false, false, 0);
    	if(pHolidayDate.equals(lTempDate))
    		throw new CommonBusinessException("Selected date is not a Holiday.");
    	//
    	StringBuilder lSql = new StringBuilder();
    	List<InstrumentBean> lInstruments = null;
    	DBHelper lDbHelper = DBHelper.getInstance();
    	InstrumentBean lInstrumentBean;
    	
    	lSql.append("SELECT * FROM INSTRUMENTS WHERE INRECORDVERSION > 0  AND INGROUPINID IS NULL "); //excluding child instruments
    	lSql.append(" AND INSTATUS NOT IN ( ");
    	lSql.append(lDbHelper.formatString(InstrumentBean.Status.Withdrawn.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Factored.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Expired.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Leg_1_Failed.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Leg_2_Failed.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Leg_1_Settled.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Leg_2_Settled.getCode()));
    	lSql.append(",").append(lDbHelper.formatString(InstrumentBean.Status.Leg_3_Generated.getCode()));
    	lSql.append(" ) ");
    	lSql.append(" AND  ( ");
    	//InstDueDate can be on an holiday
    	lSql.append(" INSTATDUEDATE = ").append(lDbHelper.formatDate(pHolidayDate));
    	lSql.append(" OR INMATURITYDATE = ").append(lDbHelper.formatDate(pHolidayDate));
    	lSql.append(" ) ");
    	lInstruments = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
    	//
    	if (lInstruments!=null && lInstruments.size() > 0){
    		Date lNewClearingDate = lOtherResourceCache.getPreviousClearingDate(pHolidayDate, 0);
	        //this condition may not arise
        	if(!lSystemParameterBean.getDate().before(lNewClearingDate))
        		throw new CommonBusinessException("New clearing date should be after the current business date.");
    		FactoringUnitBean lFactoringUnitBean = null;
        	//
        	appendMessage(lMessages, "Instruments with Statutory Due date or Maturity date on Holiday ", lInstruments.size() + " records");
    		String lTempMsg;
    		for(int lPtr=0; lPtr < lInstruments.size(); lPtr++){
    			lInstrumentBean = lInstruments.get(lPtr);
    			lTempMsg = lInstrumentBean.getId().toString();
    			if(lInstrumentBean.getStatDueDate().equals(pHolidayDate)){
    				lInstrumentBean.setStatDueDate(lNewClearingDate);
    				lTempMsg += " - Stat Due date";
    			}
    			if(lInstrumentBean.getMaturityDate().equals(pHolidayDate)){
    				lInstrumentBean.setMaturityDate(lNewClearingDate);
    				lTempMsg += " - Maturity date";
    			}
    			//update instrument
    			instrumentDAO.update(pConnection, lInstrumentBean,BeanMeta.FIELDGROUP_UPDATE);
    	        instrumentDAO.insertAudit(pConnection, lInstrumentBean, AuditAction.Update, pAppUserBean.getId());
    	        if(lInstrumentBean.getFuId() != null &&
    	        		!lInstrumentBean.getMaturityDate().equals(pHolidayDate)){
        	        lFactoringUnitBean = new FactoringUnitBean();
        	        lFactoringUnitBean.setId(lInstrumentBean.getFuId());
        	        lFactoringUnitBean = factoringUnitDAO.findByPrimaryKey(pConnection, lFactoringUnitBean);
        	        //update factoring unit
        	        lFactoringUnitBean.setMaturityDate(lNewClearingDate);
        	        factoringUnitDAO.update(pConnection, lFactoringUnitBean, BeanMeta.FIELDGROUP_UPDATE);
        	        factoringUnitDAO.insertAudit(pConnection, lFactoringUnitBean, AuditAction.Update, pAppUserBean.getId());
    	        }
            	appendMessage(lMessages, lTempMsg, "Updated");
    		}
    	}
    	else{
        	appendMessage(lMessages, "No Statutory/Maturity dates falling on holiday.", "-");
    	}
    	pConnection.commit();
        return lMessages;
    }
	public int expireBid(Connection pConnection, Date pDate) throws Exception{
		//incase any bids left to expire after the end of 
		//second window then the bids will be expired here
		StringBuilder lSql = new StringBuilder();
		int lCount =0;
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append(" Select * from Bids");
		lSql.append(" WHERE BDID IS NOT NULL ");
		lSql.append(" AND BDSTATUS = ").append(DBHelper.getInstance().formatString(BidBean.Status.Active.getCode()));
		lSql.append(" AND( ").append(" ( ");
		lSql.append(" BDAPPSTATUS = ").append(DBHelper.getInstance().formatString(BidBean.AppStatus.Approved.getCode()));
		lSql.append(" AND BDVALIDTILL IS NOT NULL  ");
		lSql.append(" AND BDVALIDTILL <  ").append(lDBHelper.formatDate(pDate));
		lSql.append(" ) " );
		lSql.append(" OR ").append(" ( ");
		lSql.append(" BDAPPSTATUS = ").append(DBHelper.getInstance().formatString(BidBean.AppStatus.Pending.getCode()));
		lSql.append(" AND BDVALIDTILL IS NULL "); 
		lSql.append(" AND BDPROVVALIDTILL IS NOT NULL "); 
		lSql.append(" AND BDPROVVALIDTILL < ");
		lSql.append(lDBHelper.formatDate(pDate)).append(" ) " );
		lSql.append(" OR ").append(" ( ");
		lSql.append(" BDAPPSTATUS = ").append(DBHelper.getInstance().formatString(BidBean.AppStatus.Pending.getCode()));
		lSql.append(" AND BDVALIDTILL IS NOT NULL  ");
		lSql.append(" AND BDPROVVALIDTILL IS NOT NULL  ");
		lSql.append(" AND BDVALIDTILL < ");
		lSql.append(lDBHelper.formatDate(pDate)).append(" ) " );
		lSql.append(" OR ").append(" ( ");
		lSql.append(" BDAPPSTATUS IS NULL ");
		lSql.append(" AND BDVALIDTILL IS NULL  ");
		lSql.append(" AND BDPROVVALIDTILL IS NULL  ");
		lSql.append(" AND BDRATE IS NULL ");
		lSql.append(" AND BDPROVRATE IS NULL ").append(" ) " );
		lSql.append(" ) " );
		List<BidBean> lBids = bidDAO.findListFromSql(pConnection, lSql.toString(), 0);
		for(BidBean lBean:lBids){
			if (lBean.getRate() == null && lBean.getProvRate() == null){
				lBean.setStatus(com.xlx.treds.auction.bean.BidBean.Status.Deleted);
			}else{
				lBean.setStatus(com.xlx.treds.auction.bean.BidBean.Status.Expired);
			}
			lBean.setTimestamp(new Timestamp(System.currentTimeMillis()));
         	bidDAO.insertAudit(pConnection, lBean,AuditAction.Update,new Long(1));
         	lBean.clearFinalBid();
         	lBean.clearProvBid();
         	lBean.setAppStatus(null);
         	lBean.setFinancierAuId(null);
         	bidDAO.update(pConnection, lBean,BidBean.FIELDGROUP_UPDATESTATUS);
         	lCount++;
		}
        return lCount;
	}
	
	public List<Map<String, Object>> performeEODAutomated(Connection pConnection, AppUserBean pAppUserBean){
		List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
		Map<String,Object>lDataValues = new HashMap<String, Object>();
		Map<String,Object> lAttachmentMap = new HashMap<String, Object>();
    	try {
			lDataValues.put(EmailSender.TO, TredsHelper.getInstance().getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		try {
			Date lBusinessDate = TredsHelper.getInstance().getBusinessDate();
			appendMessage(lMessages, "EOD date",(lBusinessDate!=null?BeanMetaFactory.getInstance().getDateFormatter().format(lBusinessDate):""));
			List<Map<String, Object>> lList = performeEOD(pConnection, pAppUserBean, lAttachmentMap);
			lMessages.addAll(lList);
			lDataValues.put("message", lMessages);
			lDataValues.put("status", "success");
			lDataValues.put("action", "EOD performed successfully");
		} catch (Exception exp) {
			appendMessage(lMessages, "Error while Performing EOD", exp.getMessage());
			lDataValues.put("message", lMessages);
			lDataValues.put("status", "falied");
			lDataValues.put("action", "Error while performing EOD");
			logger.info(exp.getMessage());
		}
		List<Map<String, Object>> lTmpList =  (List<Map<String, Object>>) lDataValues.get("message");
		int lCount = 1;
		for (Map<String, Object> lTmpMap : lTmpList){
			lTmpMap.put("count", lCount);
			lCount++;
		}
		lDataValues.put("message", lTmpList);
		List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
		List<String> lMonetagoFailedMessageList = (List<String>) lAttachmentMap.get("MonetagoFailedMessageList");
		if(lMonetagoFailedMessageList!=null && !lMonetagoFailedMessageList.isEmpty()){
			MimeBodyPart lMimeBodyPart = null;
			lMimeBodyPart = getMonetagoMessageFileAsAttachment(lMonetagoFailedMessageList);
			lListAttach.add(lMimeBodyPart);
			lDataValues.put(EmailSender.ATTACHMENTS,lListAttach);
		}
		Map<String, Object> lObligationShiftMessages = (Map<String, Object>) lAttachmentMap.get("ObligationShiftMessages");
		if(lObligationShiftMessages!=null && !lObligationShiftMessages.isEmpty()){
			MimeBodyPart lMimeBodyPart = null;
			lMimeBodyPart = getMessageFileAsAttachment(lObligationShiftMessages,"ObligationShiftMessages");
			lListAttach.add(lMimeBodyPart);
			lDataValues.put(EmailSender.ATTACHMENTS,lListAttach);
		}
		Map<String, Object> lDateRecalculationMessages = (Map<String, Object>) lAttachmentMap.get("DateRecalculationMessages");
		if(lDateRecalculationMessages!=null && !lDateRecalculationMessages.isEmpty()){
			MimeBodyPart lMimeBodyPart = null;
			lMimeBodyPart = getMessageFileAsAttachment(lDateRecalculationMessages,"DateRecalculationMessages");
			lListAttach.add(lMimeBodyPart);
			lDataValues.put(EmailSender.ATTACHMENTS,lListAttach);
		}
		
		EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_EOD_SUCCESS, lDataValues);
		return (List<Map<String, Object>>) lDataValues.get("message");
	}
	
	private MimeBodyPart getMonetagoMessageFileAsAttachment(List<String> pMonetagoFailedMessageList) {
		ByteArrayOutputStream lByteArrayOutputStream = null;
		try{
    		lByteArrayOutputStream = new ByteArrayOutputStream();
			StringBuilder lTemp = new StringBuilder();
			for(String lMessage : pMonetagoFailedMessageList){
				lTemp.append(lMessage).append("\r\n");
			}
			lByteArrayOutputStream.write(lTemp.toString().getBytes());
		    lByteArrayOutputStream.close();
		    //
		    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArrayOutputStream.toByteArray(), "application/csv")));
			lMimeBodyPart.setFileName("MonetagoMessages.txt");
			return lMimeBodyPart;
		} 
		catch (Exception ex) 
		{
			logger.info("Error in getMonetagoMessageFileAsAttachment : " + ex.getMessage());
		}
		return null;
	}

	private int deactivateDormantRegistrations(Connection pConnection, Long pDaysToAdd, Date pPreEODDate, AppUserBean pAppUserBean){
		int lCount = 0;
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		List<AppUserBean> lRegUserList = null;
		//
		lSql.append(" SELECT AUId FROM  AppUsers ");
		lSql.append(" LEFT OUTER JOIN COMPANYDETAILS_P ON ( AUID = CDID ) ");
		lSql.append(" WHERE AURecordVersion > 0 AND CDRECORDVERSION > 0 ");
		lSql.append(" AND CDAPPROVALSTATUS IN ( ").append(lDBHelper.formatString(CompanyApprovalStatus.Draft.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(CompanyApprovalStatus.Returned.getCode()));
		lSql.append(" , ").append(lDBHelper.formatString(CompanyApprovalStatus.Rejected.getCode())).append(" ) "); 
		lSql.append(" AND AUDomain = ").append(lDBHelper.formatString(AppConstants.DOMAIN_REGUSER));
		lSql.append(" AND AUStatus = ").append(lDBHelper.formatString(IAppUserBean.Status.Active.getCode()));
		lSql.append(" AND ( AURecordCreateTime + ").append(pDaysToAdd.toString()).append(" ) <= ").append(lDBHelper.formatDate(pPreEODDate));	
		//
		logger.info(lSql.toString());
		try(Statement lStatement =  pConnection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
			List<Long> lAuIds = new ArrayList<Long>();
			while (lResultSet.next()){
				lAuIds.add(lResultSet.getLong("AUID"));
			}
			if(lAuIds.size() > 0){
				lRegUserList = new ArrayList<AppUserBean>();
			    MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(IAppUserBean.ENTITY_NAME);
			    AppUserBean lAppUserBean = null;
				for (Long lAuId : lAuIds){
				    lAppUserBean = (AppUserBean)lMemoryTable.selectSingleRow(IAppUserBean.f_Id, new Long[]{lAuId});
				    lRegUserList.add(lAppUserBean);
				}
			}
	        MemoryDBConnection lMemoryDBConnection = MemoryDBManager.getInstance().getConnection();
			//deactivate and save
	        AppUserBean lOldAppUserBean = null;
			for(AppUserBean lAppUserBean : lRegUserList){
				if(lAppUserBean!=null){
					lOldAppUserBean = new AppUserBean();
					appUserDAO.getBeanMeta().copyBean(lAppUserBean, lOldAppUserBean);
					lAppUserBean.setStatus(IAppUserBean.Status.Disabled);
					if (appUserDAO.update(pConnection, lAppUserBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
		                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
		            lMemoryDBConnection.deleteRow(IAppUserBean.ENTITY_NAME, IAppUserBean.f_Id, lAppUserBean);
		            lMemoryDBConnection.addRow(IAppUserBean.ENTITY_NAME, lOldAppUserBean);
		            appUserDAO.insertAudit(pConnection, lOldAppUserBean, GenericDAO.AuditAction.Update, pAppUserBean.getId());
				}
			}
			//send mail of deactivation 
			for(AppUserBean lAppUserBean : lRegUserList){
				if(lAppUserBean!=null){
					Map<String,Object>lDataValues = new HashMap<String, Object>();
					lDataValues.put(EmailSender.TO, lAppUserBean.getEmail());
		        	lDataValues.put("loginId", lAppUserBean.getLoginId());
		        	lDataValues.put("days", pDaysToAdd);
					EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_REGISTRATIONDEACTIVATE, lDataValues);
					lCount++;
				}
			}
		} catch (Exception e) {
			logger.info("Error : " + e.getMessage());
		}
		//
		return lCount;
	}
	
	private MimeBodyPart getMessageFileAsAttachment(Map<String,Object> pMap,String pFileName) {
		ByteArrayOutputStream lByteArrayOutputStream = null;
		try{
    		lByteArrayOutputStream = new ByteArrayOutputStream();
			StringBuilder lTemp = new StringBuilder();
			for(String lKey : pMap.keySet()){
				List<Map<String, Object>> lList = (List<Map<String, Object>>) pMap.get(lKey);
				for(Map<String, Object> lMap : lList){
					lTemp.append(lKey).append(" : ").append(lMap.get("act").toString()).append(lMap.get("rem").toString()).append("\r\n");
				}
			}
			lByteArrayOutputStream.write(lTemp.toString().getBytes());
		    lByteArrayOutputStream.close();
		    //
		    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArrayOutputStream.toByteArray(), "application/csv")));
			lMimeBodyPart.setFileName(pFileName+".txt");
			return lMimeBodyPart;
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			logger.info("Error in getMessageFileAsAttachment : " + ex.getMessage());
		}
		return null;
	}
	
	private void shiftSplits(Connection pConnection, Map<Long,Long> lMap, AppUserBean pAppUserBean) throws Exception{
		List<Long> lObIds = new ArrayList(lMap.keySet());
		List<ObligationSplitsBean> lSplitsList = null;
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM ObligationSplits");
		lSql.append(" WHERE  OBSOBID IN ( ").append(TredsHelper.getInstance().getCSVIdsForInQuery(lObIds)).append(" ) ");
		try {
			lSplitsList = obligationSplitDAO.findListFromSql(pConnection, lSql.toString(), 0);
		} catch (Exception e) {
			logger.info("error while fetching splits for shift ."+e.getMessage());
		}
		ObligationSplitsBean lNewSplitBean = null;
		for (ObligationSplitsBean lBean : lSplitsList){
			lNewSplitBean= new ObligationSplitsBean();
    		obligationSplitDAO.getBeanMeta().copyBean(lBean, lNewSplitBean); //new obligation
    		// insert new Bean
    		lNewSplitBean.setObid(lMap.get(lBean.getId()));
    		obligationSplitDAO.insert(pConnection, lNewSplitBean);
    		//update old
    		lBean.setStatus(ObligationBean.Status.Shifted);
    		lBean.setRecordUpdateTime(CommonUtilities.getCurrentDate());
    		lBean.setRecordUpdator(pAppUserBean.getId());
    		lBean.setRespRemarks("Shifted due to holiday.");
    		obligationSplitDAO.update(pConnection, lBean,ObligationSplitsBean.FIELDGROUP_UPDATESTATUS);
		}
	}
	private int notifyExpiryOfLimits(Connection pConnection, Long pDaysToAdd, Date pPreEODDate){
		int lCount = 0;
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		List<FinancierAuctionSettingBean> lFinAucSettings = null;
		Map<String, List<FinancierAuctionSettingBean>> lFinwiseExpiredSettings = new HashMap<String, List<FinancierAuctionSettingBean>>();
		lSql.append(" SELECT * FROM FinancierAuctionSettings ");
		lSql.append(" WHERE FASFinancier IS NOT NULL ");
		lSql.append(" AND FASExpiryDate IS NOT NULL ");
		lSql.append(" AND FASExpiryDate >  ").append(lDBHelper.formatDate(pPreEODDate));	
		lSql.append(" AND ( FASExpiryDate + ").append(pDaysToAdd.toString()).append(" ) >= ").append(lDBHelper.formatDate(pPreEODDate));	
		lSql.append(" ORDER BY FASFinancier ");
		logger.info(lSql.toString());
		try{
			lFinAucSettings = financierAuctionSettingBeanDAO.findListFromSql(pConnection, lSql.toString(), 0);
			if(lFinAucSettings != null){
				List<FinancierAuctionSettingBean> lTmpList = null;
				for(FinancierAuctionSettingBean lBean : lFinAucSettings){
					if(!lFinwiseExpiredSettings.containsKey(lBean.getFinancier())){
						lTmpList = new ArrayList<FinancierAuctionSettingBean>();
						lFinwiseExpiredSettings.put(lBean.getFinancier(), lTmpList);
					}else{
						lTmpList = lFinwiseExpiredSettings.get(lBean.getFinancier());
					}
					lTmpList.add(lBean);
				}
			}
			List<FinancierAuctionSettingBean> lExpiredList = null;
	    	Date lFinalDate = OtherResourceCache.getInstance().addDaysToDate(pPreEODDate, pDaysToAdd.intValue());
			for(String lFinancier : lFinwiseExpiredSettings.keySet()){
				lExpiredList  = lFinwiseExpiredSettings.get(lFinancier);
				Map<String,Object>lDataValues = new HashMap<String, Object>();
				List<Map<String,Object>> lExpiryList = new ArrayList<Map<String,Object>>();
				Map<String,Object>lLevelDetails = new HashMap<String, Object>();
		    	lDataValues.put(EmailSender.TO, TredsHelper.getInstance().getAdminUserEmail(lFinancier));
		    	AppEntityBean lPurchaser = null, lSupplier = null;
		    	AppUserBean lUser = null;
		    	List<Long> lCreatorList = new ArrayList<Long>();
		    	for(FinancierAuctionSettingBean lBean : lExpiredList){
		    		if (lFinalDate.compareTo(lBean.getExpiryDate()) <= 0) {
		    			continue;
		    		}
		    		lLevelDetails = new HashMap<String, Object>();
		    		lPurchaser = StringUtils.isNotEmpty(lBean.getPurchaser())?TredsHelper.getInstance().getAppEntityBean(lBean.getPurchaser()):null;
		    		lSupplier = StringUtils.isNotEmpty(lBean.getSupplier())?TredsHelper.getInstance().getAppEntityBean(lBean.getSupplier()):null;
		    		lUser = lBean.getAuId()!=null?TredsHelper.getInstance().getAppUser(lBean.getAuId()):null;
		    		lLevelDetails.put("level", lBean.getLevel().toString());
		    		lLevelDetails.put("purchaser", (lPurchaser!=null)?lPurchaser.getName():"" );
		    		lLevelDetails.put("supplier", (lSupplier!=null)?lSupplier.getName():"" );
		    		lLevelDetails.put("login", (lUser!=null)?lUser.getLoginId():"");
		    		lLevelDetails.put("userName", (lUser!=null)?lUser.getName():"");
		    		lLevelDetails.put("expiryDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lBean.getExpiryDate()));
		    		lExpiryList.add(lLevelDetails);
		    		if(!lCreatorList.contains(lBean.getRecordCreator())){
		    			lCreatorList.add(lBean.getRecordCreator());
		    		}
		    	}
		    	for(Long lUserId : lCreatorList){
			    	lDataValues.put(EmailSender.CC, TredsHelper.getInstance().getUserEmail(lUserId));
		    	}
		    	if (!lExpiryList.isEmpty()) {
		    		lDataValues.put("expiryList", lExpiryList);
					EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FINANCIERLIMITNEAREXPIRY, lDataValues);
					lCount++;
		    	}
	        	
			}
		} catch (Exception e) {
			logger.info("Error : " + e.getMessage());
		}
		return lCount;
	}
	
	private int updateLimits(Connection pConnection) {
	 	int lRetCount = 0;
		String lSql = "select fulimitids, sum(FULIMITUTILIZED) FULIMITUTILIZED , sum(FULIMITUTILIZED) FUPURSUPLIMITUTILIZED from factoringunits ";
		lSql += " where FURECORDVERSION > 0 AND FUSTATUS IN ('FACT','L1SET','L2FAIL') group by fulimitids ";
		lSql += " UNION ALL ";
		lSql += " SELECT 	BDLIMITIDS AS fulimitids,SUM(BDLIMITUTILISED) FULIMITUTILIZED,SUM(BDBIDLIMITUTILISED) FUPURSUPLIMITUTILIZED ";
		lSql += " FROM BIDS, FACTORINGUNITS WHERE BDFUID = fuid AND FUSTATUS  = 'ACT' AND BDSTATUS = 'ACT' AND BDRATE IS NOT NULL AND BDBIDTYPE = 'RES' GROUP BY BDLIMITIDS ";

		try {
			GenericDAO<FactoringUnitBean> factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
			GenericDAO<FinancierAuctionSettingBean> financierAuctionSettingDAO = new GenericDAO<FinancierAuctionSettingBean>(FinancierAuctionSettingBean.class);
			List<FactoringUnitBean> lFUBeans = factoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
			Map<Long, BigDecimal> lFASLimits = new HashMap<Long, BigDecimal>();//key=LimitId, value=CumulativeLimitAmount 
			Map<Long, BigDecimal> lFASBidLimits = new HashMap<Long, BigDecimal>();//key=LimitId, value=CumulativeBidLimitAmount 
			String lUpdateSql = "";

			if(lFUBeans != null){
				String[] lLimitIds = null;
				Long lLimitId = null;
				BigDecimal lTotalLimit = BigDecimal.ZERO, lTotalBidLimit = BigDecimal.ZERO;

				for(FactoringUnitBean lFUBean : lFUBeans  ){
					if(CommonUtilities.hasValue(lFUBean.getLimitIds())){
						lLimitIds = lFUBean.getLimitIds().split(",");
						if(lLimitIds!=null){
							for(int lPtr=0; lPtr < lLimitIds.length; lPtr++){
								if(CommonUtilities.hasValue(lLimitIds[lPtr])){
									lLimitId = new Long(lLimitIds[lPtr]);
									//
									if(!lFASLimits.containsKey(lLimitId)){
										lTotalLimit = BigDecimal.ZERO;	
									}else {
										lTotalLimit = lFASLimits.get(lLimitId);
									}
									lTotalLimit = lTotalLimit.add(lFUBean.getLimitUtilized());
									lFASLimits.put(lLimitId, lTotalLimit);
									//
									if(!lFASBidLimits.containsKey(lLimitId)){
										lTotalBidLimit = BigDecimal.ZERO;	
									}else {
										lTotalBidLimit = lFASBidLimits.get(lLimitId);
									}
									lTotalBidLimit = lTotalBidLimit.add(lFUBean.getLimitUtilized());
									lFASBidLimits.put(lLimitId, lTotalBidLimit);
									//
								}
							}
						}
					}
				}
			}

			lSql = "SELECT * FROM FINANCIERAUCTIONSETTINGS ORDER BY FASFINANCIER, FASPURCHASER, FASSUPPLIER, FASLEVEL DESC";
		 	List<FinancierAuctionSettingBean> lFASList = financierAuctionSettingDAO.findListFromSql(pConnection, lSql, -1);
		 	Long lLimitId =null;
		 	BigDecimal lComputedLimit = BigDecimal.ZERO, lComputedBidLimit = BigDecimal.ZERO;
		 
			for(FinancierAuctionSettingBean lFASBean : lFASList){
				lLimitId = lFASBean.getId();
				//
				lComputedLimit = BigDecimal.ZERO;
				if(lFASLimits.containsKey(lLimitId)) 
					lComputedLimit = lFASLimits.get(lLimitId);
				//
				lComputedBidLimit = BigDecimal.ZERO;
				if(lFASBidLimits.containsKey(lLimitId)) 
					lComputedBidLimit = lFASBidLimits.get(lLimitId);
				BigDecimal lUtilised = lFASBean.getUtilised()==null?BigDecimal.ZERO:lFASBean.getUtilised();
				BigDecimal lBidLimitUtilised = lFASBean.getBidLimitUtilised()==null?BigDecimal.ZERO:lFASBean.getBidLimitUtilised();
				//
				lUpdateSql = "";
				if(lComputedLimit.subtract(lUtilised).setScale(2)!= BigDecimal.ZERO.setScale(2) ||
						lComputedBidLimit.subtract(lBidLimitUtilised).setScale(2)!= BigDecimal.ZERO.setScale(2)){
					lUpdateSql = "UPDATE FINANCIERAUCTIONSETTINGS SET FASUTILISED = " + lComputedLimit + ", FASBIDLIMITUTILISED = " + lComputedBidLimit + "  WHERE FASID = "+ lLimitId;
					logger.info("Limit Update : " + lUpdateSql);
			        executeUpdate(pConnection, lUpdateSql);
			        lRetCount++;
				}
			}
		}catch(Exception e) {
			logger.info("Error : " + e.getMessage());
			lRetCount = -1;
		}
		return lRetCount;
	}
	
	
	
	public int notifyBuyerExpiryRating(Connection pConnection){
		int lCount = 0;
		Long lExpiryDays = new Long(365); //fail safe
		Long lExpiryMailRangeDays = new Long(15); //fail safe
		StringBuilder lSql = new StringBuilder();
		Map<String,Object> lDataValues = new HashMap<String, Object>();
		HashMap<String, Object> lBuyerCreditSetting = new HashMap<String,Object>();
    	lBuyerCreditSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_BUYERCREDITRATING);
    	if(lBuyerCreditSetting != null){
    		lExpiryDays =  (Long) lBuyerCreditSetting.get(AppConstants.ATTRIBUTE_BUYERCREDITRATING_EXPIRYDAYS);
    		lExpiryMailRangeDays = (Long)lBuyerCreditSetting.get(AppConstants.ATTRIBUTE_BUYERCREDITRATING_EXPIRYMAILRANGEDAYS);
    	}
		lSql.append(" SELECT * FROM BuyerCreditRatings ");
		lSql.append(" WHERE  BCRRecordVersion > 0 ");
		lSql.append(" AND BCRRATINGDATE + ").append(lExpiryDays).append(" - ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate()))).append(" > 0 ");
		lSql.append(" AND BCRRATINGDATE + ").append(lExpiryDays).append(" - ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
		lSql.append(" <= ").append(lExpiryMailRangeDays);
		logger.info(lSql.toString());
		try{
			List<BuyerCreditRatingBean> lBeanList = buyerCreditRatingDAO.findListFromSql(pConnection, lSql.toString(), -1);
			if(lBeanList.size() > 0){				
				List<Map<String,Object>> lRatings = new ArrayList<Map<String,Object>>();
				for(BuyerCreditRatingBean lBean : lBeanList){
					Map<String,Object> lRating = new HashMap<String, Object>();
					lRating.put("buyer", lBean.getBuyerCode());
					lRating.put("ratingAgency", lBean.getRatingAgency());
					lRating.put("ratingDate", lBean.getRatingDate());
					lRating.put("rating", lBean.getRating());
					lRating.put("remarks", lBean.getRemarks());
					lRatings.add(lRating);
				}
				lDataValues.put("ratings", lRatings);
				List<String> lFinancierCodeList = getAllFinanciers(pConnection);
		        List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
		        Long lTmpParam = null;
		        Map<String,List<String>> lEmailMap = null; 
		        if(lFinancierCodeList.size() > 0){
					for (String lFinancierCode : lFinancierCodeList){
				        lNotificationInfos.add(new NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_BUYERRATINGEXPIRY_1, EntityEmail.RoleBased, lFinancierCode, EmailSenders.BCC, lTmpParam));
					}
			        lEmailMap = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
			        if (lEmailMap!=null) {
			        	TredsHelper.getInstance().setEmailsToData(lEmailMap, lDataValues);
			        }
			        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BUYEREXPIRYRATINGNOTIFICATION, lDataValues);
			        lCount++;
		        }
			}
		} catch (Exception e) {
			logger.info("Error : " + e.getMessage());
		}
		return lCount;
	}
	
	public int notifyBuyerModificationInRating(Connection pConnection){
		int lCount = 0;
		String lTimeChange = null;
		List<String> lBuyerCodes = new ArrayList<String>();
		Map<String, String> lBuyerCodeMap = new HashMap<String , String>();
		HashMap<String, Object> lBuyerCreditSetting = new HashMap<String,Object>();
    	lBuyerCreditSetting = RegistryHelper.getInstance().getStructure(AppConstants.REGISTRY_BUYERCREDITRATING);
    	if(lBuyerCreditSetting != null){
    		lTimeChange = (String) lBuyerCreditSetting.get(AppConstants.ATTRIBUTE_BUYERCREDITRATING_CHANGEINRATINGTIME);
    	}
		StringBuilder lSql = new StringBuilder();
		Map<String,Object> lDataValues = new HashMap<String, Object>();
		lSql.append(" SELECT BCRBUYERCODE FROM BuyerCreditRatings ");
		lSql.append(" WHERE  BCRRecordVersion > 0 ");
		lSql.append("  AND ( BCRRecordCreateTime > ( ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
		lSql.append(" - ").append(lTimeChange).append(" ) ");
		lSql.append(" OR ( BCRRECORDUPDATETIME IS NOT NULL AND BCRRECORDUPDATETIME >  ( ").append(DBHelper.getInstance().formatDate(CommonUtilities.getDate(CommonUtilities.getCurrentDate())));
		lSql.append(" - ").append(lTimeChange).append("  ) ) )");
		logger.info(lSql.toString());
		try{
			try(Connection lConnection = DBHelper.getInstance().getConnection();
					Statement lStatement =  lConnection.createStatement();
					ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
				String lTmpCode=null;
				while (lResultSet.next()){
					lTmpCode = lResultSet.getString("BCRBuyerCode");
					if(!lBuyerCodes.contains(lTmpCode)){
						lBuyerCodes.add(lTmpCode);
					}
				}
			}catch (Exception ex) {
				ex.printStackTrace();
			}
			if(lBuyerCodes.size() > 0){
				lDataValues.put("buyers", lBuyerCodes);
				List<String> lFinancierCodeList = getAllFinanciers(pConnection);
		        List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
		        Long lTmpParam = null;
		        if(lFinancierCodeList.size() > 0){
					for (String lFinancierCode : lFinancierCodeList){
				        lNotificationInfos.add(new NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_BUYERRATINGCHANGE_1, EntityEmail.RoleBased, lFinancierCode, EmailSenders.BCC, lTmpParam));
					}
			        Map<String,List<String>> lEmailMap = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
			        if (lEmailMap!=null) {
			        	TredsHelper.getInstance().setEmailsToData(lEmailMap, lDataValues);
			        }
			        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BUYERCHANGEINRATINGNOTIFICATION, lDataValues);
			        lCount++;
		        }
			}
		} catch (Exception e) {
			logger.info("Error : " + e.getMessage());
		}
		return lCount;
	}
	
	public List<String> getAllFinanciers(Connection pConnection) throws Exception {
		List<String> lFinancierCode = new ArrayList<String>();
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT CDCODE AS FINANCIERCODE FROM COMPANYDETAILS ");
		lSql.append(" WHERE CDFINANCIERFLAG = ").append(DBHelper.getInstance().formatString(CommonAppConstants.YesNo.Yes.getCode()));
		lSql.append(" AND CDAPPROVALSTATUS = ").append(DBHelper.getInstance().formatString(AppConstants.CompanyApprovalStatus.Approved.getCode()));
		try(Connection lConnection = DBHelper.getInstance().getConnection();
				Statement lStatement =  lConnection.createStatement();
				ResultSet lResultSet = lStatement.executeQuery(lSql.toString()); ){
			while (lResultSet.next()){
				lFinancierCode.add(lResultSet.getString("FinancierCode"));
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return lFinancierCode;
	}

	
	public int notifyPSBanker(Connection pConnection, Date pCurrentDate) throws Exception{
		int lCount = 0; 
		Map<String,Object> lDatavalues = null;
		
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM FACTORINGUNITS LEFT OUTER JOIN OBLIGATIONS ");
		lSql.append(" ON (FUID = OBFUID) ");
		lSql.append(" WHERE FURECORDVERSION > 0 AND OBRECORDVERSION > 0 ");
		lSql.append(" AND TO_DATE(TO_CHAR(FUACCEPTDATETIME , 'DD-MM-YYYY'), 'DD-MM-YYYY') = ").append(DBHelper.getInstance().formatDate(pCurrentDate));
		lSql.append(" AND FUSTATUS = ").append(DBHelper.getInstance().formatString(FactoringUnitBean.Status.Factored.getCode()));
		List<FactoredPaymentBean> lFactoredPaymentBeans = factoredPaymentBeanDAO.findListFromSql(pConnection, lSql.toString(), -1);
		List<Map<String,Object>> lDataList = null; //List of all BuyerSeller combination
		Map<String,List<Map<String,Object>>> lPurchasersList = new HashMap<String, List<Map<String,Object>>>();
		Map<String,List<Map<String,Object>>> lSuppliersList = new HashMap<String, List<Map<String,Object>>>();
		if(lFactoredPaymentBeans != null && !lFactoredPaymentBeans.isEmpty()){
			///
			lDataList = new ArrayList<Map<String,Object>>();
			Map<String,Object> lData = null; //data of one BuyerSeller combination.
			List<Map<String,Object>> lObliList = null; //List of all obligations of a particular BuyerSeller combination
			Map<String,Object> lTmpDataHash = new HashMap<String, Object>(); //key=Buyer^Seller value=lData
			Map<String,Object> lObliHash = null; //Data of one obligation
			String lKey = null;
			int lSrNo = 0;
			//
			//
			for(FactoredPaymentBean lFactoredPaymentBean : lFactoredPaymentBeans){
				FactoringUnitBean lFactoringUnitBean = lFactoredPaymentBean.getFactoringUnitBean();
				ObligationBean lObligationBean = lFactoredPaymentBean.getObligationBean();
				//ObligationSplitsBean lObligationSplitsBean = lFactoredPaymentBean.getObligationSplitsBean();
				//
				lKey = lFactoringUnitBean.getPurchaser()+CommonConstants.KEY_SEPARATOR+lFactoringUnitBean.getSupplier();
				//
				if( !lTmpDataHash.containsKey(lKey) ){
					lData = new HashMap<String, Object>();
					lData.put("code", lKey);
					lData.put("purchaser", TredsHelper.getInstance().getAppEntityBean(lFactoringUnitBean.getPurchaser()).getName());
					lData.put("supplier", TredsHelper.getInstance().getAppEntityBean(lFactoringUnitBean.getSupplier()).getName());
					lObliList = new ArrayList<Map<String,Object>>();
					lData.put("obligations", lObliList);
					lTmpDataHash.put(lKey, lData);
					lSrNo = 1;
				}else{
					lData = (Map<String,Object>) lTmpDataHash.get(lKey);
					lObliList = (List<Map<String,Object>>) lData.get("obligations");
					lSrNo = (Integer)lObliList.get(lObliList.size()-1).get("srNo");
					lSrNo++;
				}
				lObliHash = new HashMap<String,Object>();
				lObliHash.put("srNo", lSrNo);
				lObliHash.put("fuId",lObligationBean.getFuId());
				lObliHash.put("obId", lObligationBean.getId());
				lObliHash.put("leg2AmtBuyer",lObligationBean.getAmount());
				lObliHash.put("ObligDateL2Buyer", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lObligationBean.getDate()));
				//
				lObliList.add(lObliHash);
				//
			}
			String[] lPS = null;
			List<String> lEntityList = new ArrayList<>();
			if(lTmpDataHash.keySet().size() > 0) {
			for(String lTmpKey : lTmpDataHash.keySet()){
				lData = (Map<String,Object>) lTmpDataHash.get(lTmpKey);
				lDataList.add(lData);
				//
				lPS = CommonUtilities.splitString(lTmpKey, CommonConstants.KEY_SEPARATOR);
				List<Map<String,Object>> lPurchDataList = null;
				if(!lPurchasersList.containsKey(lPS[0])){
					lPurchDataList = new ArrayList<Map<String,Object>>();
					lPurchasersList.put(lPS[0], lPurchDataList);
				}else{
					lPurchDataList = (List<Map<String,Object>>)lPurchasersList.get(lPS[0]);
				}
				lPurchDataList.add(lData);
				//
				List<Map<String,Object>> lSupDataList = null;
				if(!lSuppliersList.containsKey(lPS[1])){
					lSupDataList = new ArrayList<Map<String,Object>>();
					lSuppliersList.put(lPS[1], lSupDataList);
				}else{
					lSupDataList = (List<Map<String,Object>>)lSuppliersList.get(lPS[1]);
				}
				lSupDataList.add(lData);
			}
				if(lPurchasersList.size()>0) {
			lEntityList.addAll(lPurchasersList.keySet());
				}
				if(lSuppliersList.keySet().size() > 0) {
			lEntityList.addAll(lSuppliersList.keySet());
				}
			}
			if (!lEntityList.isEmpty()) {
				lSql = new StringBuilder();
				lSql.append(" SELECT CDCODE CBDLINE1 , CBDEMAIL FROM COMPANYDETAILS,COMPANYBANKDETAILS ");
				lSql.append(" WHERE CDID=CBDID ");
				lSql.append(" AND  CDCODE IN ( ").append(TredsHelper.getInstance().getCSVStringForInQuery(lEntityList, true)).append(" ) ");
				List<CompanyBankDetailBean> lCompanyBankDetailBeanList = companyBankDetailDAO.findListFromSql(pConnection, lSql.toString(), -100);
				Map<String,List<String>> lBankEmailHash = new HashMap<>();
				List<String> lBankList = null;
				for (CompanyBankDetailBean lCBDBean : lCompanyBankDetailBeanList) {
					if (!lBankEmailHash.containsKey(lCBDBean.getLine1())) {
						lBankEmailHash.put(lCBDBean.getLine1(), new ArrayList<String>());
					}
					lBankList = lBankEmailHash.get(lCBDBean.getLine1());
					if (lCBDBean.getEmail()!=null) {
						lBankList.add(lCBDBean.getEmail());
					}
				}
				for(String lPurchaser : lPurchasersList.keySet()){
					lDataList = lPurchasersList.get(lPurchaser);
					lDatavalues = new HashMap<String, Object>();
					lDatavalues.put("data", lDataList);
					if (lBankEmailHash.containsKey(lPurchaser)) {
						lBankList = lBankEmailHash.get(lPurchaser);
						for (String lEmail: lBankList){
							lDatavalues.put(EmailSender.TO, TredsHelper.getInstance().getAdminUserEmail(lPurchaser));
							lDatavalues.put(EmailSender.CC, lEmail);
					        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_PURCHASERSUPPLIERBANKER, lDatavalues);
						}
					}
				}
				
				for(String lSupplier : lSuppliersList.keySet()){
					lDataList = lSuppliersList.get(lSupplier);
					lDatavalues = new HashMap<String, Object>();
					lDatavalues.put("data", lDataList);
					if (lBankEmailHash.containsKey(lSupplier)) {
						lBankList = lBankEmailHash.get(lSupplier);
						for (String lEmail: lBankList){
							lDatavalues.put(EmailSender.TO, TredsHelper.getInstance().getAdminUserEmail(lSupplier));
							lDatavalues.put(EmailSender.CC, lEmail);
					        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_PURCHASERSUPPLIERBANKER, lDatavalues);
						}
					}
				}
			}else {
				logger.info("notifyPSBanker : PS Entity list empty for "+(pCurrentDate!=null?pCurrentDate.toString():" NO DATE."));
			}
		}else {
			logger.info("notifyPSBanker : No obligation for "+(pCurrentDate!=null?pCurrentDate.toString():" NO DATE."));
		}
        return lCount;
	}
	
	private int[] generateAnnualRegistrationCharges(Connection pConnection, Date pDate, IAppUserBean pUserBean, Date pEodDate) {
		//Get all appentities where registrationExpiryDate < businessdate 
		// and status=active 
		// and not exists (record in registrationcharges for entity where effectivedate > registrationExpiryDate)
		int[] lRetVal = new int[] { 0, 0};
		StringBuilder lSql = new StringBuilder();
		//
		lSql.append(" SELECT aecode, aecdid, aename, aetype, aestatus, AEREGEXPIRYDATE, AEEXTENDEDREGEXPIRYDATE ");
		lSql.append(" , AEREGEXPIRYDATE ");
		lSql.append(" FROM AppEntities ");
		lSql.append(" WHERE AERECORDVERSION > 0 ");
		lSql.append(" AND AETYPE IN ");
		lSql.append(" ( ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Supplier.getCode()));
		lSql.append(" , ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Purchaser.getCode()));
		lSql.append(" , ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode()));
		lSql.append(" ) ");
		lSql.append(" AND AESTATUS = ").append(DBHelper.getInstance().formatString(AppEntityStatus.Active.getCode()));
		lSql.append(" AND AEREGEXPIRYDATE IS NOT NULL ");
		lSql.append(" AND AEREGEXPIRYDATE BETWEEN ");
		lSql.append(DBHelper.getInstance().formatDate(OtherResourceCache.getInstance().addDaysToDate(pDate, 7)));
		lSql.append(" AND ");
		lSql.append(DBHelper.getInstance().formatDate(OtherResourceCache.getInstance().addDaysToDate(pEodDate, 7)));
		//
		List<AppEntityBean> lAppEntitesForAnnualCharge = null;
		RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
		int lTotal = 0, lErrorCount = 0;
		List<String> lEntityErrors = new ArrayList<String>();
		try {
			lAppEntitesForAnnualCharge = appEntitiesDAO.findListFromSql(pConnection, lSql.toString(), 0);
			if(lAppEntitesForAnnualCharge!=null && lAppEntitesForAnnualCharge.size() > 0) {
				lTotal = lAppEntitesForAnnualCharge.size();
				for(AppEntityBean lAppEntityBean : lAppEntitesForAnnualCharge) {
					try {
						lRegistrationChargeBO.createAnnualFeesCharge(pConnection, lAppEntityBean.getCode(), pUserBean);
						Map<String, Object> lDataValues = new HashMap<>();
		            	lDataValues.put("renewDate", FormatHelper.getDisplay(AppConstants.DATE_FORMAT,lAppEntityBean.getRegExpiryDate()));
		            	String lNotificationType = null;
		            	String lTemplate = null;
		            	// email
		                List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
		                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_AnualChargeExpiry_1;
		                lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lAppEntityBean.getCode(), EmailSenders.TO));
		                lTemplate = AppConstants.TEMPLATE__ANUALCHARGEEXPIRY;
		            	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
		                if (!lEmailIds.isEmpty()) {
		                	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
		                    EmailSender.getInstance().addMessage(lTemplate,lDataValues);
		                }
					}catch(Exception lEx) {
						logger.error("Error while creating annual charge for : "+lAppEntityBean.getCode(),lEx);
						lEntityErrors.add(lAppEntityBean.getCode());
					}
				}
				lErrorCount = lEntityErrors.size();
				lRetVal[0] = lTotal;
				lRetVal[1] = lTotal - lErrorCount;
			}
		} catch (Exception e) {
			logger.error("Error in generateAnnualRegistrationCharges : "+e.getMessage());
		}
		return lRetVal;
	}
	
	private int[] extendAnnualRegistrationCharges(Connection pConnection, Date pDate, IAppUserBean pUserBean, Date pEodDate) {
		//Get all appentities where registrationExpiryDate < businessdate 
		// and status=active 
		// and not exists (record in registrationcharges for entity where effectivedate > registrationExpiryDate)
		int[] lRetVal = new int[] { 0, 0};
		StringBuilder lSql = new StringBuilder();
		//
		lSql.append(" SELECT * FROM AppEntities ");
		lSql.append(" WHERE AERECORDVERSION > 0 ");
		lSql.append(" AND AETYPE IN ");
		lSql.append(" ( ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Supplier.getCode()));
		lSql.append(" , ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Purchaser.getCode()));
		lSql.append(" , ").append(DBHelper.getInstance().formatString(AppEntityBean.EntityType.Financier.getCode()));
		lSql.append(" ) ");
		lSql.append(" AND AESTATUS = ").append(DBHelper.getInstance().formatString(AppEntityStatus.Active.getCode()));
		lSql.append(" AND AEREGEXPIRYDATE IS NOT NULL ");
		lSql.append(" AND AEEXTENDEDREGEXPIRYDATE IS NULL ");
		lSql.append(" AND AEREGEXPIRYDATE < ");
		lSql.append(DBHelper.getInstance().formatDate(pEodDate));
		//
		List<AppEntityBean> lAppEntitesForAnnualCharge = null;
		RegistrationChargeBO lRegistrationChargeBO = new RegistrationChargeBO();
		int lTotal = 0, lErrorCount = 0;
		List<String> lEntityErrors = new ArrayList<String>();
		try {
			lAppEntitesForAnnualCharge = appEntitiesDAO.findListFromSql(pConnection, lSql.toString(), 0);
			HashMap<String,AppEntityBean> lEntHash = new HashMap<String,AppEntityBean>();
			for (AppEntityBean lAppEntityBean : lAppEntitesForAnnualCharge) {
				lEntHash.put(lAppEntityBean.getCode(),lAppEntityBean);
			}
			if(lAppEntitesForAnnualCharge!=null && lAppEntitesForAnnualCharge.size() > 0) {
				lTotal = lAppEntitesForAnnualCharge.size();
				lSql = new StringBuilder();
				lSql.append(" SELECT * FROM REGISTRATIONCHARGES ");
				lSql.append(" WHERE RCCHARGETYPE = ").append(DBHelper.getInstance().formatString(RegistrationChargeBean.ChargeType.Annual.getCode()));
				lSql.append(" AND RCAPPROVALSTATUS != ").append(DBHelper.getInstance().formatString(RegistrationChargeBean.ApprovalStatus.Approved.getCode()));
				lSql.append(" AND RCENTITYCODE IN ( ").append(TredsHelper.getInstance().getCSVEnumsForInQuery(new ArrayList<String>(lEntHash.keySet()))).append(" ) ");
				List<RegistrationChargeBean> lRegChargeBeans = registrationChargeDAO.findListFromSql(pConnection, lSql.toString(), -1);
				AppEntityBean lAeBean = null;
				for(RegistrationChargeBean lBean : lRegChargeBeans) {
					try {
						lBean.setExtendedDate(OtherResourceCache.getInstance().addDaysToDate(lBean.getEffectiveDate(),30));
						lBean.setApprovalStatus(ApprovalStatus.Approved);
						lBean.setRequestType(RequestType.Extenstion);
						lBean.setRecordUpdator(new Long(0));
						registrationChargeDAO.update(pConnection, lBean);
						lAeBean = TredsHelper.getInstance().getAppEntityBean(lBean.getEntityCode());
						lAeBean.setExtendedRegExpiryDate(lBean.getExtendedDate());
						lAeBean.setRecordUpdator(new Long(0));
						appEntitiesDAO.update(pConnection, lAeBean);
//						MemoryDBConnection lMemoryDBConnection = MemoryDBManager.getInstance().getConnection();
//						lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAeBean);
//						lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAeBean);
					}catch(Exception lEx) {
						logger.error("Error while creating annual charge for : "+lAeBean.getCode(),lEx);
						lEntityErrors.add(lAeBean.getCode());
					}
				}
				lErrorCount = lEntityErrors.size();
				lRetVal[0] = lTotal;
				lRetVal[1] = lTotal - lErrorCount;
			}
		} catch (Exception e) {
			logger.error("Error in generateAnnualRegistrationCharges : "+e.getMessage());
		}
		return lRetVal;
	}
	
	public static void main(String[] args) throws Exception {
		BeanMetaFactory.createInstance(null);
		AppInitializer lAppInitializer = new AppInitializer(null);
		lAppInitializer.loadTable(null, true);
		//EndOfDayBO lEndOfDayBO = new EndOfDayBO();
		//Date lDate = FormatHelper.getDate("28-03-2019", AppConstants.DATE_FORMAT);
		//lEndOfDayBO.notifyPSBanker(DBHelper.getInstance().getConnection(), lDate);
		RegistrationChargeBO lBO = new RegistrationChargeBO();
		lBO.createRegistrationCharge(null, "CA0000071", null);
	}

}