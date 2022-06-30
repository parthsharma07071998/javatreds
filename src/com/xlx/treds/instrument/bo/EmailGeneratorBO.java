

package com.xlx.treds.instrument.bo;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.github.mustachejava.Mustache;
import com.xlx.common.base.CommonConstants;
import com.xlx.common.messaging.CommonMessageSender.MessageBean;
import com.xlx.common.messaging.EmailSender;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.treds.AppConstants;
import com.xlx.treds.AppConstants.CostBearer;
import com.xlx.treds.AppConstants.EmailSenders;
import com.xlx.treds.AppConstants.EntityEmail;
import com.xlx.treds.NotificationInfo;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.AssignmentNoticeDetailsBean;
import com.xlx.treds.auction.bean.AssignmentNoticeInfo;
import com.xlx.treds.auction.bean.AssignmentNoticesBean;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.BidBean.AppStatus;
import com.xlx.treds.auction.bean.BidBean.Status;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.auction.bean.ObliFUInstDetailBean;
import com.xlx.treds.auction.bean.ObliFUInstDetailSplitsBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.TxnType;
import com.xlx.treds.auction.bo.ObligationBO;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentWorkFlowBean;
import com.xlx.treds.master.bean.BankBranchDetailBean;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.user.bean.MakerCheckerMapBean;


public class EmailGeneratorBO {
    private static final Logger logger = LoggerFactory.getLogger(EmailGeneratorBO.class);
            
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private GenericDAO<BidBean> bidDAO;
    private GenericDAO<CompanyBankDetailBean> companyBankDetailDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private CompositeGenericDAO<AssignmentNoticeInfo> assignmentNoticeInfoDAO;
    private GenericDAO<ObligationBean> obligationDAO;
    private static final String NEWLINE = "\r\n";
    private static final int FINOBLIGATIONBEAN = 0;
    private static final int PUROBLIGATIONBEAN = 1;
    private static final int FACTORINGUNITBEAN = 2;
    private static final int INSTRUMENTBEAN = 3;
    private static final int FINAPPENTITYBEAN = 4;
    private static final int PURAPPENTITYBEAN = 5;
    private static final int FINOBLIGATIONSPLITBEAN = 6;
    private static final int PUROBLIGATIONSPLITBEAN = 7;

    public EmailGeneratorBO() {
        super();
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
        companyBankDetailDAO = new GenericDAO<CompanyBankDetailBean>(CompanyBankDetailBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        assignmentNoticeInfoDAO = new CompositeGenericDAO<AssignmentNoticeInfo>(AssignmentNoticeInfo.class);
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
    }

    public void sendInstrumentSubmittedMail(Connection pConnection, InstrumentBean pInstrumentBean, 
            InstrumentWorkFlowBean pWorkFlowBean, List<MakerCheckerMapBean> pCheckers, AppUserBean pUserBean) throws Exception {
        String lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTSUBMIT_1;
        // send email to checkers
        Map<String, Object> lDataValues = instrumentDAO.getBeanMeta().formatAsMap(pInstrumentBean);
        lDataValues.put("statusDateTime", BeanMetaFactory.getInstance().getDateTimeFormatter().format(pWorkFlowBean.getStatusUpdateTime()));
        if (StringUtils.isNotBlank(pWorkFlowBean.getStatusRemarks()))
            lDataValues.put("statusRemarks", pWorkFlowBean.getStatusRemarks());
        //for sms
    	lDataValues.put("netAmount", TredsHelper.getInstance().getFormattedAmount(lDataValues.get("netAmount"), true));
        lDataValues.put("currency", pInstrumentBean.getCurrency());
        //
    	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
        List<String> lCheckerMobileNos = new ArrayList<String>();
        for (MakerCheckerMapBean lMakerCheckerMapBean : pCheckers) {
        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_INSTSUBMIT_1,EntityEmail.UserEmail, lMakerCheckerMapBean.getCheckerId(), EmailSenders.TO));
            String lMobileNo =  TredsHelper.getInstance().getUserMobileNo(lMakerCheckerMapBean.getCheckerId());
            if (StringUtils.isNotBlank(lMobileNo))
            	lCheckerMobileNos.add(lMobileNo);
        }
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_INSTSUBMIT_2,EntityEmail.UserEmail, pUserBean.getId(), EmailSenders.CC));
    	if(CommonAppConstants.YesNo.Yes.equals(pUserBean.getPurchaserAggregatorFlag())){
    		//TODO: Q. Mail access - notification access??
    		//TODO: Q. If the aggregator initiates then mail should go to admin of purchaser
        	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, pInstrumentBean.getPurchaser(), EmailSenders.CC));
        	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, pInstrumentBean.getPurchaser(), EmailSenders.CC));
    	}else{
        	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, pUserBean.getDomain(), EmailSenders.CC));
    	}

    	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
        if (!lEmailIds.isEmpty()) {
        	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTSUBMIT, lDataValues);
            
        }
    }
    
    public void sendInstrumentCounterInBoxMail(Connection pConnection, InstrumentBean pInstrumentBean, 
            InstrumentWorkFlowBean pWorkFlowBean) throws Exception {
        OtherResourceCache lOtherResourceCache = OtherResourceCache.getInstance();
        String lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCHKRAPPCNTR_1;
        // email
        Map<String, Object> lDataValues = instrumentDAO.getBeanMeta().formatAsMap(pInstrumentBean);
        lDataValues.put("statusDateTime", BeanMetaFactory.getInstance().getDateTimeFormatter().format(pWorkFlowBean.getStatusUpdateTime()));
        if (StringUtils.isNotBlank(pWorkFlowBean.getStatusRemarks()))
            lDataValues.put("statusRemarks", pWorkFlowBean.getStatusRemarks());
        lDataValues.put("netAmount", TredsHelper.getInstance().getFormattedAmount(lDataValues.get("netAmount"), true));
        lDataValues.put("currency", pInstrumentBean.getCurrency());
        Long lLocationId = null;
        if(pInstrumentBean.getCounterEntity().equals(pInstrumentBean.getPurchaser())){
        	lLocationId = pInstrumentBean.getPurClId();
        }else if(pInstrumentBean.getCounterEntity().equals(pInstrumentBean.getSupplier())){
        	lLocationId = pInstrumentBean.getSupClId();
        }
        List<String> lEmailIds = lOtherResourceCache.getEmailIdsFromNotificationSettings(pConnection, pInstrumentBean.getCounterEntity(), lNotificationType);
        if (lEmailIds != null) {
            lDataValues.put(EmailSender.TO, lEmailIds);
        }
        List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
        lNotificationInfos.add(new NotificationInfo(lNotificationType, EntityEmail.RoleBased, pInstrumentBean.getCounterEntity(), EmailSenders.TO, lLocationId));
        Map<String,List<String>> lEmailMap = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
        if (lEmailMap!=null) {
        	TredsHelper.getInstance().setEmailsToData(lEmailMap, lDataValues);
        }
        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTCOUNTERINBOX, lDataValues);
    }
    
    public void sendInstrumentCheckerActionMailToMaker(Connection pConnection, InstrumentBean pInstrumentBean, 
            InstrumentWorkFlowBean pWorkFlowBean, AppUserBean pUserBean) throws Exception {
        String lNotificationType = null;
        // email
        Map<String, Object> lDataValues = instrumentDAO.getBeanMeta().formatAsMap(pInstrumentBean);
        lDataValues.put("statusDateTime", BeanMetaFactory.getInstance().getDateTimeFormatter().format(pWorkFlowBean.getStatusUpdateTime()));
        if (StringUtils.isNotBlank(pWorkFlowBean.getStatusRemarks()))
            lDataValues.put("statusRemarks", pWorkFlowBean.getStatusRemarks());
        String lVerb = null, lNoun = null;
        if (pInstrumentBean.getStatus() == InstrumentBean.Status.Checker_Approved
        		||InstrumentBean.Status.Counter_Approved.equals(pInstrumentBean.getStatus())) {
            lVerb = "approved";
            lNoun = "Approval";
        } else if (pInstrumentBean.getStatus() == InstrumentBean.Status.Checker_Returned) {
            lVerb = "returned";
            lNoun = "Return";
        } else if (pInstrumentBean.getStatus() == InstrumentBean.Status.Checker_Rejected) {
            lVerb = "rejected";
            lNoun = "Reject";
        }
        lDataValues.put("verb", lVerb);
        lDataValues.put("noun", lNoun);
        lDataValues.put("netAmount", TredsHelper.getInstance().getFormattedAmount(lDataValues.get("netAmount"), true));
        lDataValues.put("currency", pInstrumentBean.getCurrency());
        //
        if(InstrumentBean.Status.Checker_Rejected.equals(pInstrumentBean.getStatus())){
            lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCHKRREJ_1;
        }else if(InstrumentBean.Status.Checker_Returned.equals(pInstrumentBean.getStatus())){
            lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCHKRRET_1;
        }else if(InstrumentBean.Status.Checker_Approved.equals(pInstrumentBean.getStatus()) 
        		||InstrumentBean.Status.Counter_Approved.equals(pInstrumentBean.getStatus())  ){
            lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCHKRAPP_1;
        }

    	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
    	if(!InstrumentBean.Status.Checker_Returned.equals(pInstrumentBean.getStatus())){
        	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, pInstrumentBean.getMakerAuId(), EmailSenders.TO));
    	}
    	if(pInstrumentBean.getMakerAuId()!=null){
        	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.UserEmail, pInstrumentBean.getMakerAuId(), EmailSenders.TO));
    	}
    	if(pUserBean!=null && pUserBean.getId()!=null){
        	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.UserEmail, pUserBean.getId(), EmailSenders.CC));    	
    	}
    	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, pInstrumentBean.getMakerEntity(), EmailSenders.CC));
    	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
        if (!lEmailIds.isEmpty()) {
        	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTCHECKERACTION, lDataValues);
        }
       //
    }
    
    public String sendInstrumentCounterActionMail(Connection pConnection, InstrumentBean lInstrumentBean, InstrumentWorkFlowBean pWorkFlowBean, AppUserBean pUserBean){
        // email
        String lReturnMsg = "";
        Map<String, Object> lDataValues = instrumentDAO.getBeanMeta().formatAsMap(lInstrumentBean);
        if(pWorkFlowBean!=null){
            lDataValues.put("statusDateTime", BeanMetaFactory.getInstance().getDateTimeFormatter().format(pWorkFlowBean.getStatusUpdateTime()));
            if (StringUtils.isNotBlank(pWorkFlowBean.getStatusRemarks()))
                lDataValues.put("statusRemarks", pWorkFlowBean.getStatusRemarks());
        }else{
            lDataValues.put("statusDateTime",BeanMetaFactory.getInstance().getDateTimeFormatter().format(CommonUtilities.getCurrentDate()));
        }
        String lVerb = null, lNoun = null, lFUMsg = "";
        String lNotificationType = null, lCounterNotificationType = null;
        
        try{
            if ((lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Approved) 
                    || (lInstrumentBean.getStatus() == InstrumentBean.Status.Converted_To_Factoring_Unit)) {
                lVerb = "accepted";
                lNoun = "Acceptance";
                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCNTRACC_1;
                lCounterNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_CNTRINSTACC_1;
                lFUMsg = "It is now converted to Factoring Unit and pending for further action.";
            } else if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Returned) {
                lVerb = "returned";
                lNoun = "Return";
                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCNTRRET_1;
                lFUMsg = "It is now sent back and pending for further action.";
            } else if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Rejected) {
                lVerb = "rejected";
                lNoun = "Reject";
                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCNTRREJ_1;
                lCounterNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_CNTRINSTREJ_1;
            } else if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Pending) {
                lVerb = "submitted for your approval";
                lNoun = "Submit";
                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCNTRMKRSUB_1;
                lFUMsg = "It is now sent to checker for further action.";
            } else if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Return) {
            	lVerb = "returned";
                lNoun = "Return";
                lNotificationType = AppConstants.EMAIL_NOTIFY_TYPE_INSTCNTRCHKRET_1;
                lFUMsg = "It is now sent back and pending for further action.";
            }
            lDataValues.put("verb", lVerb);
            lDataValues.put("noun", lNoun);
            lDataValues.put("netAmount", TredsHelper.getInstance().getFormattedAmount(lDataValues.get("netAmount"), true));
            lDataValues.put("currency", lInstrumentBean.getCurrency());
            //
            if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Pending || lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Return) {
            	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
            	if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Pending){
                	lDataValues.put("user", lInstrumentBean.getCounterCheckerLoginId());
                }else if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Pending){
                	lDataValues.put("user", lInstrumentBean.getCounterLoginId());
                }
            	if(lInstrumentBean.getCounterAuId()!=null){
            		if(CommonAppConstants.YesNo.Yes.equals(pUserBean.getPurchaserAggregatorFlag())){
    	            	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lInstrumentBean.getPurchaser(), EmailSenders.TO));
    	            	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lInstrumentBean.getPurchaser(), EmailSenders.CC));
            		}
            		if (lInstrumentBean.getStatus() == InstrumentBean.Status.Counter_Checker_Return){
            			lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.UserEmail, lInstrumentBean.getMakerAuId(), EmailSenders.TO));
            		}
    	            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lInstrumentBean.getCounterAuId(), EmailSenders.TO));
    	            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lInstrumentBean.getCounterAuId(), EmailSenders.CC));        	
            	}
            	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
                if (!lEmailIds.isEmpty()) {
                	Map<String, Object> lDataValuesMaker = new HashMap<String,Object>(lDataValues);
                	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesMaker);
                	EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTCOUNTERMAKCHKACTION, lDataValuesMaker);
                }
            }else{
            	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
            	if(lInstrumentBean.getMakerAuId()!=null){
            		if(CommonAppConstants.YesNo.Yes.equals(pUserBean.getPurchaserAggregatorFlag())){
    	            	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lInstrumentBean.getPurchaser(), EmailSenders.TO));
    	            	lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lInstrumentBean.getPurchaser(), EmailSenders.CC));
            		}
    	            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.AdminEmail, lInstrumentBean.getMakerAuId(), EmailSenders.TO));
    	            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.UserEmail, lInstrumentBean.getMakerAuId(), EmailSenders.TO));
    	            lNotificationInfos.add(new  NotificationInfo( lNotificationType,EntityEmail.Explicit, lInstrumentBean.getMakerAuId(), EmailSenders.CC));        	
            	}
            	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
                if (!lEmailIds.isEmpty()) {
                	Map<String, Object> lDataValuesCounter = new HashMap<String,Object>(lDataValues);
                	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesCounter);
                    EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTCOUNTERACTION, lDataValuesCounter);
                }
                // email to action taking entity admin
                if ( !StringUtils.isEmpty(lCounterNotificationType)){
                    //
                    lNotificationInfos.clear();
                    if(pUserBean!=null && pUserBean.getId()!= null){
                    	if(CommonAppConstants.YesNo.Yes.equals(pUserBean.getPurchaserAggregatorFlag())){
                        	lNotificationInfos.add(new  NotificationInfo( lCounterNotificationType,EntityEmail.AdminEmail, lInstrumentBean.getPurchaser(), EmailSenders.TO));
                        	lNotificationInfos.add(new  NotificationInfo( lCounterNotificationType,EntityEmail.Explicit, lInstrumentBean.getPurchaser(), EmailSenders.CC));
                    	}
                    	lNotificationInfos.add(new  NotificationInfo( lCounterNotificationType,EntityEmail.AdminEmail, pUserBean.getId(), EmailSenders.TO));
                    	lNotificationInfos.add(new  NotificationInfo( lCounterNotificationType,EntityEmail.UserEmail, pUserBean.getId(), EmailSenders.TO));
                    	lNotificationInfos.add(new  NotificationInfo( lCounterNotificationType,EntityEmail.Explicit, pUserBean.getId(), EmailSenders.CC));        	
                    }
                	lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
                    if (!lEmailIds.isEmpty()) {
                    	HashMap<String, Object> lDataValuesSelfAdmin = new HashMap<String, Object>(lDataValues);
                    	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesSelfAdmin);
                        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTCOUNTERACTIONSELF, lDataValuesSelfAdmin);
                    }
                }
            }
        }catch(Exception e){
            logger.info("Error in sendMail : " + e.getMessage());
        }
        	lReturnMsg = "Your Instrument "+ lNoun + " action is completed. " + lFUMsg;
        return lReturnMsg;
    }
    
    public void sendInstrumentReadyForAuctionMail(Connection pConnection, FactoringUnitBean pFactoringUnitBean, InstrumentBean pInstrumentBean){
        if (FactoringUnitBean.Status.Ready_For_Auction.equals(pFactoringUnitBean.getStatus())){
            try{
                //Send Mails to - Cost Bearer Entity, Owner Entity and Admin
                Map<String, Object> lDataValues = new HashMap<String, Object>();
                Map<String,List<String>> lMailIds = new  HashMap<String,List<String>>();

                HashSet<String> lCodes = new HashSet<String>();
                if (pFactoringUnitBean.isPurchaserCostBearer()){
                    lCodes.add(pFactoringUnitBean.getPurchaser());
                }
                if (pFactoringUnitBean.isSupplierCostBearer()){
                    lCodes.add(pFactoringUnitBean.getSupplier());
                }
            	lCodes.add(pFactoringUnitBean.getOwnerEntity());
            	//
            	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
                for (String lCode : lCodes){
                	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUAUCREADY_1,EntityEmail.AdminEmail, lCode, EmailSenders.TO));
                	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUAUCREADY_1,EntityEmail.Explicit, lCode, EmailSenders.CC));
                }
                //feting the mailids without treds admin - if any found then we will refetch the same down along with treds admin
            	lMailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos);
                if (lMailIds.isEmpty())
                    return;
                //add the platform and then refetch the mail ids. 
            	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUAUCREADY_1,EntityEmail.AdminEmail, AppConstants.DOMAIN_PLATFORM, EmailSenders.CC));
            	lMailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos);
                
            	TredsHelper.getInstance().setEmailsToData(lMailIds, lDataValues);

                //{{fuId}}, {{fuAmount}} ,  {{invoiceNo}},  {{invoiceDate}}, {{supName}}     {{purName}} , {{counterAcceptDate}}
                lDataValues.put("fuId", pFactoringUnitBean.getId());
                lDataValues.put("fuAmount", TredsHelper.getInstance().getFormattedAmount(pFactoringUnitBean.getAmount(), true));
                lDataValues.put("invoiceNo", pInstrumentBean.getInstNumber());
                lDataValues.put("invoiceDate", pInstrumentBean.getInstDate());
                lDataValues.put("instCount", pInstrumentBean.getInstCount());
                lDataValues.put("supName", pInstrumentBean.getSupName());
                lDataValues.put("purName", pInstrumentBean.getPurName());
                lDataValues.put("counterAcceptDate", BeanMetaFactory.getInstance().getDateTimeFormatter().format(new Timestamp(System.currentTimeMillis())));
                lDataValues.put("sysDate", TredsHelper.getInstance().getBusinessDate().toString());         
                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_INSTRUMENTREADYFORAUCTION, lDataValues);  
            }catch(Exception ex){
                logger.info("Error while sending mail : Instrument Ready for Auction : " +  ex.getMessage(), ex);
            }
        }
    }

    public void sendFUCreatedMailToDesignatedBank(Connection pConnection, FactoringUnitBean pFactoringUnitBean, InstrumentBean pInstrumentBean){
        Map<String, Object> lDataValues = new HashMap<String, Object>();
        CompanyBankDetailBean lCBDBean = new CompanyBankDetailBean();
        //
        try{
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pInstrumentBean.getPurchaser());
            lCBDBean.setCdId(lAppEntityBean.getCdId());
            lCBDBean.setDefaultAccount(Yes.Yes);
            lCBDBean = companyBankDetailDAO.findBean(pConnection, lCBDBean);
            if(lCBDBean!=null && lCBDBean.getId()!=null && CommonUtilities.hasValue(lCBDBean.getEmail())){
                lDataValues.put(EmailSender.TO, lCBDBean.getEmail());
                //{{fuId}},  {{fuAmount}},  {{invoiceNo}},   {{invoiceDate}},             {{supName}},     {{purName}},     {{fuCreatedDateTime}}
                lDataValues.put("fuId", pFactoringUnitBean.getId());
                lDataValues.put("fuAmount", TredsHelper.getInstance().getFormattedAmount(pFactoringUnitBean.getAmount(), true));
                lDataValues.put("invoiceNo", pInstrumentBean.getInstNumber());
                lDataValues.put("instCount", pInstrumentBean.getInstCount());
                lDataValues.put("invoiceDate", pInstrumentBean.getInstDate());
                lDataValues.put("supName", pInstrumentBean.getSupName());
                lDataValues.put("purName", pInstrumentBean.getPurName());
                lDataValues.put("fuCreatedDateTime", new Timestamp(System.currentTimeMillis()));
                lDataValues.put("sysDate", TredsHelper.getInstance().getBusinessDate().toString());         
                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUCREATEDINFORMDESIGNATEDBANK, lDataValues);
            }else{
                logger.info("Email not sent to Designated Bank of Buyer ("+ pFactoringUnitBean.getPurName() +") for FU " + pFactoringUnitBean.getId());
            }
        }catch(Exception ex){
            logger.info("Error in Send Mail : FUCreatedInformDesignatedBank. " + ex.getMessage());
        }
    }

    //The return value is hash of template and data :: key=TemplateName value=Map<String,Object>DataValues
    public Map<String, Object> createBidManageEmail(Connection pConnection, FactoringUnitBean pFactoringUnitBean, 
            BidBean pOldBidBean, BidBean pNewBidBean, AppUserBean pAppUserBean) throws Exception {
    	Map<String, Object> lTemplatewiseData = new HashMap<String, Object>();
        Map<String, Object> lDataValues = bidDAO.getBeanMeta().formatAsMap(pNewBidBean);
        lDataValues.putAll(factoringUnitDAO.getBeanMeta().formatAsMap(pFactoringUnitBean));
        String lNotificationTypeOwner = null, lNotificationTypeNonOwner = null;
        String lAction = null;
        boolean lCancel = false;
        if ((pNewBidBean.getStatus() == BidBean.Status.Deleted) || (pNewBidBean.getStatus() == BidBean.Status.Deleted_By_Owner)) {
        	if(pOldBidBean!=null && pOldBidBean.getAppStatus()!=null){
        		if(pOldBidBean.getAppStatus().equals(AppStatus.Approved)){
        			lNotificationTypeNonOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDCANCELNONOWNER_1;
        			lNotificationTypeOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDCANCELOWNER_1;
        		}
                lAction = "Cancelled";
                lCancel = true;
        	}else{
    			lNotificationTypeNonOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDCANCELNONOWNER_1;
    			lNotificationTypeOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDCANCELOWNER_1;
                lAction = "Cancelled";
                lCancel = true;
        	}
        } else {
            if (((pOldBidBean == null) || (pOldBidBean.getFinancierAuId() == null)) ||
                	(pOldBidBean!=null && pOldBidBean.getProvAction() != null && BidBean.ProvAction.Entry.equals(pOldBidBean.getProvAction()))) {
                lNotificationTypeOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDENTRYOWNER_1;
                lNotificationTypeNonOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDENTRYNONOWNER_1;
                lAction = "Placed";
            } else {
                lNotificationTypeOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDMODIFYOWNER_1;
                lNotificationTypeNonOwner = AppConstants.EMAIL_NOTIFY_TYPE_BIDMODIFYNONOWNER_1;
                lAction = "Modified";
            }
        }
        lDataValues.put("amount", TredsHelper.getInstance().getFormattedAmount(pFactoringUnitBean.getAmount(), true));
        lDataValues.put("bdTimestamp", BeanMetaFactory.getInstance().getDateTimeFormatter().format(pNewBidBean.getTimestamp()));
        if (pNewBidBean.getValidTill() != null)
            lDataValues.put("validTill", BeanMetaFactory.getInstance().getDateFormatter().format(pNewBidBean.getValidTill()));
        lDataValues.put("action", lAction);
        if (pNewBidBean.getBidType() != null)
            lDataValues.put("bidType", pNewBidBean.getBidType().toString());
        // copy old bid values in case of cancellation
        if (lCancel && (pOldBidBean != null)) {
            lDataValues.put("rate", pOldBidBean.getRate());
        	lDataValues.put("validTill", (pOldBidBean.getValidTill()!=null?BeanMetaFactory.getInstance().getDateFormatter().format(pOldBidBean.getValidTill()):""));
            lDataValues.put("haircut", pOldBidBean.getHaircut());
            if (pOldBidBean.getBidType() != null)
                lDataValues.put("bidType", pOldBidBean.getBidType().toString());
        }
        String lNonOwnerEntity = pFactoringUnitBean.getOwnerEntity().equals(pFactoringUnitBean.getPurchaser())?pFactoringUnitBean.getSupplier():pFactoringUnitBean.getPurchaser();
        Long lNonOwnerAuId = pFactoringUnitBean.getIntroducingEntity().equals(lNonOwnerEntity)?pFactoringUnitBean.getIntroducingAuId():pFactoringUnitBean.getCounterAuId();

        // email to owner
        if (StringUtils.isNotEmpty(lNotificationTypeOwner)){
        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
        	lNotificationInfos.add(new  NotificationInfo( lNotificationTypeOwner,EntityEmail.AdminEmail, pFactoringUnitBean.getOwnerEntity(), EmailSenders.TO));
        	if( pFactoringUnitBean.getOwnerAuId()!=null){
            	lNotificationInfos.add(new  NotificationInfo( lNotificationTypeOwner,EntityEmail.UserEmail, pFactoringUnitBean.getOwnerAuId(), EmailSenders.TO));
        	}
        	lNotificationInfos.add(new  NotificationInfo( lNotificationTypeOwner,EntityEmail.Explicit, pFactoringUnitBean.getOwnerEntity(), EmailSenders.CC));
            Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
            if (!lEmailIds.isEmpty()) {
                TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
                lTemplatewiseData.put(AppConstants.TEMPLATE_BIDMANAGEOWNER, new HashMap<String,Object>(lDataValues));
            }
        }
        // email to non owner
        if (StringUtils.isNotEmpty(lNotificationTypeNonOwner)) {
        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
        	if(!StringUtils.isEmpty(lNonOwnerEntity)){
            	lNotificationInfos.add(new  NotificationInfo(lNotificationTypeNonOwner, EntityEmail.AdminEmail, lNonOwnerEntity, EmailSenders.TO));
            	lNotificationInfos.add(new  NotificationInfo(lNotificationTypeNonOwner, EntityEmail.Explicit, lNonOwnerEntity, EmailSenders.CC));
        	}
        	if(lNonOwnerAuId != null){
            	lNotificationInfos.add(new  NotificationInfo(lNotificationTypeNonOwner, EntityEmail.UserEmail, lNonOwnerAuId, EmailSenders.TO));
        	}
        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
            if (!lEmailIds.isEmpty()) {
                TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
                lTemplatewiseData.put(AppConstants.TEMPLATE_BIDMANAGENONOWNER, new HashMap<String,Object>(lDataValues));
            }
        }
        return lTemplatewiseData;
    }
    
    public void sendBidAcceptanceEmails(Connection pConnection, InstrumentBean lInstrumentBean, FactoringUnitBean pFactoringUnitBean,
            BidBean pBidBean, ObligationBean lSupplierObligationBean, ObligationBean lPurchaserObligationBeanLeg2, 
            ObligationBean lFinancierObligationBean, AppUserBean pUserBean) throws Exception {
        Map<String, Object> lDataValues = factoringUnitDAO.getBeanMeta().formatAsMap(pFactoringUnitBean);
        lDataValues.put("inId", lInstrumentBean.getId());
        lDataValues.put("instNumber", lInstrumentBean.getInstNumber());
        lDataValues.put("instCount", lInstrumentBean.getInstCount());
        lDataValues.put("bdTimestamp", BeanMetaFactory.getInstance().getDateTimeFormatter().format(pBidBean.getTimestamp()));
        if(lSupplierObligationBean!=null){
            lDataValues.put("leg1ObDate", BeanMetaFactory.getInstance().getDateFormatter().format(lSupplierObligationBean.getDate()));
            lDataValues.put("leg1ObAmount", TredsHelper.getInstance().getFormattedAmount(lSupplierObligationBean.getAmount(),true));
        }else {
            lDataValues.put("leg1ObDate", BeanMetaFactory.getInstance().getDateFormatter().format(lFinancierObligationBean.getDate()));
            lDataValues.put("leg1ObAmount", TredsHelper.getInstance().getFormattedAmount(lFinancierObligationBean.getAmount(),true));           
        }
        if (lPurchaserObligationBeanLeg2 != null) {
            lDataValues.put("leg2ObDate", BeanMetaFactory.getInstance().getDateFormatter().format(lPurchaserObligationBeanLeg2.getDate()));
            lDataValues.put("leg2ObAmount", TredsHelper.getInstance().getFormattedAmount(lPurchaserObligationBeanLeg2.getAmount(),true));           
        }

        lDataValues.put("amount", TredsHelper.getInstance().getFormattedAmount(lDataValues.get("amount"), true));
        lDataValues.put("currency", lInstrumentBean.getCurrency());
        List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
        if(false) {
        //  Please temporarily halt the email sent to buyer’s bank on bid acceptance. (Mail Srini Sir 28-06-2019 )
        try{
            AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lInstrumentBean.getPurchaser());
            CompanyBankDetailBean lCBDBean = new CompanyBankDetailBean();
            //TODO: use settlement bank if asked to change
            lCBDBean.setCdId(lAppEntityBean.getCdId());
            lCBDBean.setDefaultAccount(Yes.Yes);
            lCBDBean = companyBankDetailDAO.findBean(pConnection, lCBDBean);
            if(lCBDBean!=null && lCBDBean.getId()!=null && CommonUtilities.hasValue(lCBDBean.getEmail())){
            	List<String> lEmails = Arrays.asList(lCBDBean.getEmail());
            	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.UserDefined, pFactoringUnitBean.getPurchaser(), EmailSenders.CC , lEmails));
            }else{
                logger.info("Email not sent to Designated Bank of Buyer ("+ pFactoringUnitBean.getPurName() +") for FU " + pFactoringUnitBean.getId());
            }
        }catch(Exception ex){
            logger.info("Error in Send Mail : to pur. " + ex.getMessage());
        }
        }
        Map<String,List<String>> lMailIds = new  HashMap<String,List<String>>();
        Map<String,List<String>> lTmpEmailIds = null;
    	//purchaser
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.AdminEmail, pFactoringUnitBean.getPurchaser(), EmailSenders.TO));
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.Explicit, pFactoringUnitBean.getPurchaser(), EmailSenders.CC));
        if(pUserBean!= null && pFactoringUnitBean.getPurchaser().equals(pUserBean.getDomain())){
        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.UserEmail, pUserBean.getId(), EmailSenders.TO));
        }
        lMailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
        if (!lMailIds.isEmpty()) {
        	HashMap<String,Object> lPurDataValues = new HashMap<String,Object>(lDataValues); 
        	TredsHelper.getInstance().setEmailsToData(lMailIds, lPurDataValues);
        	
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BIDACCEPTSELF, lPurDataValues);
        }
        ObligationBO lObligationBO = new ObligationBO();
        ObligationBean lFilterBean = new ObligationBean();
    	lFilterBean.setFuId(pFactoringUnitBean.getId());
    	AppUserBean lPurAppUserBean = TredsHelper.getInstance().getAdminUser(pFactoringUnitBean.getPurchaser());
    	Map<String,Object> lMap = lObligationBO.getPayAdviseBuyer(pConnection, lFilterBean, pUserBean);
        if (!lMailIds.isEmpty()) {
        	HashMap<String,Object> lPurDataValues = new HashMap<String,Object>(lMap); 
            List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
        	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        	ITextRenderer renderer = new ITextRenderer();
        	String lUrl = TredsHelper.getInstance().getApplicationURL() + "oblig/paymentadvisehtml/" + pFactoringUnitBean.getId() ;
        	lUrl += "?domain="+pFactoringUnitBean.getPurchaser();
        	renderer.setDocument(lUrl);
            renderer.layout();
            renderer.createPDF(lByteArrayOutputStream);
            lByteArrayOutputStream.close();
        	byte[] lPdf = lByteArrayOutputStream.toByteArray();
//        	try (FileOutputStream fos = new FileOutputStream("D:\\tmp\\Final.pdf")) {
//	 		   fos.write(lPdf);
//	 		   fos.close();
// 			}
			MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		    String lFileType = "application/pdf";
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lPdf, lFileType)));
			lMimeBodyPart.setFileName("paymentAdvice_"+pFactoringUnitBean.getId()+".pdf");
			if(lMimeBodyPart!=null){
        		lAttachList.add(lMimeBodyPart);
        		lPurDataValues.put(EmailSender.ATTACHMENTS, lAttachList);
        	}
        	TredsHelper.getInstance().setEmailsToData(lMailIds, lPurDataValues);
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_PAYMENTADVMAIL, lPurDataValues);
        }
        //supplier
        lNotificationInfos.clear();
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.AdminEmail, pFactoringUnitBean.getSupplier(), EmailSenders.TO));
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.Explicit, pFactoringUnitBean.getSupplier(), EmailSenders.CC));
        if(pUserBean!= null && pFactoringUnitBean.getSupplier().equals(pUserBean.getDomain())){
        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTCB_1,EntityEmail.UserEmail, pUserBean.getId(), EmailSenders.TO));
        }
        lMailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
        if (!lMailIds.isEmpty()) {
        	List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
        	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        	ITextRenderer renderer = new ITextRenderer();
        	String lUrl = TredsHelper.getInstance().getApplicationURL() + "factunitsp/deedofassignmenthtml/" + pFactoringUnitBean.getId() ;
        	renderer.setDocument(lUrl);
            renderer.layout();
            renderer.createPDF(lByteArrayOutputStream);
            lByteArrayOutputStream.close();
        	byte[] lPdf = lByteArrayOutputStream.toByteArray();
//        	try (FileOutputStream fos = new FileOutputStream("D:\\tmp\\Final.pdf")) {
//	 		   fos.write(lPdf);
//	 		   fos.close();
// 			}
			MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		    String lFileType = "application/pdf";
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lPdf, lFileType)));
			lMimeBodyPart.setFileName("deedofassignment.pdf");
			if(lMimeBodyPart!=null){
        		lAttachList.add(lMimeBodyPart);
            	lDataValues.put(EmailSender.ATTACHMENTS, lAttachList);
        	}
        	HashMap<String,Object> lSupDataValues = new HashMap<String,Object>(lDataValues); 
        	TredsHelper.getInstance().setEmailsToData(lMailIds, lSupDataValues);
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BIDACCEPTSELF, lSupDataValues);
        }
        lObligationBO = new ObligationBO();
        lFilterBean = new ObligationBean();
    	lFilterBean.setFuId(pFactoringUnitBean.getId());
    	AppUserBean lSupAppUserBean = TredsHelper.getInstance().getAdminUser(pFactoringUnitBean.getSupplier());
    	lMap = lObligationBO.getPayAdviseBuyer(pConnection, lFilterBean, pUserBean);
        if (!lMailIds.isEmpty()) {
        	HashMap<String,Object> lSupDataValues = new HashMap<String,Object>(lMap); 
            List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
        	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        	ITextRenderer renderer = new ITextRenderer();
        	String lUrl = TredsHelper.getInstance().getApplicationURL() + "oblig/paymentadvisehtml/" + pFactoringUnitBean.getId() ;
        	lUrl += "?domain="+pFactoringUnitBean.getSupplier();
        	renderer.setDocument(lUrl);
            renderer.layout();
            renderer.createPDF(lByteArrayOutputStream);
            lByteArrayOutputStream.close();
        	byte[] lPdf = lByteArrayOutputStream.toByteArray();
//        	try (FileOutputStream fos = new FileOutputStream("D:\\tmp\\Final.pdf")) {
//	 		   fos.write(lPdf);
//	 		   fos.close();
// 			}
			MimeBodyPart lMimeBodyPart = new MimeBodyPart();
		    String lFileType = "application/pdf";
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lPdf, lFileType)));
			lMimeBodyPart.setFileName("paymentAdvice_"+pFactoringUnitBean.getId()+".pdf");
			if(lMimeBodyPart!=null){
        		lAttachList.add(lMimeBodyPart);
        		lSupDataValues.put(EmailSender.ATTACHMENTS, lAttachList);
        	}
        	TredsHelper.getInstance().setEmailsToData(lMailIds, lSupDataValues);
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_PAYMENTADVMAIL, lSupDataValues);
        }
        
        // email to financier
        lNotificationInfos.clear();
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1,EntityEmail.AdminEmail, pBidBean.getFinancierEntity(), EmailSenders.TO));
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1,EntityEmail.UserEmail, pBidBean.getFinancierAuId(), EmailSenders.TO));
    	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1,EntityEmail.Explicit, pBidBean.getFinancierEntity(), EmailSenders.CC));
        lTmpEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
        if(!lTmpEmailIds.isEmpty()) {
            HashMap<String, Object> lFinDataValues = new HashMap<String, Object>(lDataValues);
            //remove the to and cc from the previous data
        	//add the new to and cc to the data
        	TredsHelper.getInstance().setEmailsToData(lTmpEmailIds, lFinDataValues);
        	//add more information only for financier
            lFinDataValues.put("leg1ObDate", BeanMetaFactory.getInstance().getDateFormatter().format(lFinancierObligationBean.getDate()));
            lFinDataValues.put("leg1ObAmount", TredsHelper.getInstance().getFormattedAmount(lFinancierObligationBean.getAmount(), true));
            //
            EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BIDACCEPTFINANCIER,  lFinDataValues);
        }
    }
    
    public void sendFactoringUnitSuspendWithdrawEmails(Connection pConnection, List<FactoringUnitBean> pFactoringUnitList, 
            Map<Long, List<BidBean>> pDeletedBidMap, AppUserBean pUserBean) throws Exception {
        for (FactoringUnitBean lFactoringUnitBean : pFactoringUnitList) {
            String lAction = null;
            if (lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Suspended)
                lAction = "Put on Hold";
            else if (lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Withdrawn)
                lAction = "Withdrawn";
            else
                continue;
            Map<String, Object> lDataValues = factoringUnitDAO.getBeanMeta().formatAsMap(lFactoringUnitBean);
            lDataValues.put("action", lAction);
            lDataValues.put("amount", TredsHelper.getInstance().getFormattedAmount(lFactoringUnitBean.getAmount(), true));
            lDataValues.put("actionDateTime", BeanMetaFactory.getInstance().getDateTimeFormatter().format(new Timestamp(System.currentTimeMillis())));

            Map<String,List<String>> lEmailIds = null;
        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWOWNER_1,EntityEmail.AdminEmail, pUserBean.getDomain(), EmailSenders.TO));
        	if(lFactoringUnitBean.getOwnerAuId()!=null){
            	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWOWNER_1,EntityEmail.UserEmail, lFactoringUnitBean.getOwnerAuId(), EmailSenders.TO));
            	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWOWNER_1,EntityEmail.Explicit, lFactoringUnitBean.getOwnerAuId(), EmailSenders.CC));
        	}
            lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos);
            // email to owner entity admin
            if (!lEmailIds.isEmpty()) {
            	Map<String, Object> lDataValuesOwnerAdmin = new HashMap<String,Object>(lDataValues); 
            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesOwnerAdmin);
                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUSUSPWITHDRAWOWNER, lDataValuesOwnerAdmin);
            }
            // email to non owner entity and its user involved
            String lNonOwnerEntity = pUserBean.getDomain().equals(lFactoringUnitBean.getPurchaser())?lFactoringUnitBean.getSupplier():lFactoringUnitBean.getPurchaser();
            Long lNonOwnerAUId = lNonOwnerEntity.equals(lFactoringUnitBean.getIntroducingEntity())?lFactoringUnitBean.getIntroducingAuId():lFactoringUnitBean.getCounterAuId();

            lNotificationInfos.clear();
        	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWNONOWNER_1,EntityEmail.AdminEmail, lNonOwnerEntity, EmailSenders.TO));
        	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWNONOWNER_1,EntityEmail.UserEmail, lNonOwnerAUId, EmailSenders.TO));
        	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWNONOWNER_1,EntityEmail.Explicit, lNonOwnerEntity, EmailSenders.CC));
            lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos);
            if (!lEmailIds.isEmpty()) {
            	Map<String, Object> lDataValuesNonOwner = new HashMap<String, Object>(lDataValues);
            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesNonOwner);
                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUSUSPWITHDRAWNONOWNER, lDataValuesNonOwner);
            }
            // email to financiers who have placed bids
            List<BidBean> lDeletedBids = pDeletedBidMap.get(lFactoringUnitBean.getId());
            if (lDeletedBids != null) {
                for (BidBean lBidBean : lDeletedBids) {
                	lNotificationInfos.clear();
                	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWFINANCIER_1,EntityEmail.AdminEmail, lBidBean.getFinancierEntity(), EmailSenders.TO));
                	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWFINANCIER_1,EntityEmail.UserEmail, lBidBean.getFinancierAuId(), EmailSenders.TO));
                	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUSUSPWITHDRAWFINANCIER_1,EntityEmail.Explicit, lBidBean.getFinancierEntity(), EmailSenders.CC));
                    lEmailIds = TredsHelper.getInstance().getEmails(pConnection,lNotificationInfos);
                    if (!lEmailIds.isEmpty()) {
                    	Map<String, Object> lDataValuesFin = new HashMap<String, Object>(lDataValues);
                    	//
                    	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesFin);
                        EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUSUSPWITHDRAWFINANCIER, lDataValuesFin);
                    }
                }
            }
        }
    }

    //this will be used by the admin process to generate mails for todays obligations and send the same
    public List<Map<String, Object>> sendNoticeOfAssignmentEmails(Connection pConnection, Date pSettlementDate, String pLoginKey) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
		//FROM BUSINESS DATE PICK UP ALL THE ASSIGNMENTNOTICES (with Date)
		//group by the Details table and find 2 things - total count of fu's and total amount
		//hash the above on basis of id
		//LOOP THROUGH THESE ASSIGMENT NOTICES 
		lSql.append(" SELECT ANDANID ");
		lSql.append(" , COUNT(ANDFUID) ANDFUID ");
		lSql.append(" , SUM(ANDFUFACTOREDAMOUNT) ANDFUFACTOREDAMOUNT ");
		lSql.append(" , ANPURCHASER, ANSUPPLIER, ANFINANCIER, ANBUSINESSDATE, ANSUPNAME  ");
		lSql.append(" FROM ASSIGNMENTNOTICEDETAILS, ASSIGNMENTNOTICES ");
		lSql.append(" WHERE ANDANID = ANID  AND ANBUSINESSDATE =  ").append(DBHelper.getInstance().formatDate(pSettlementDate));
		lSql.append(" GROUP BY ANDANID, ANPURCHASER, ANSUPPLIER, ANFINANCIER, ANBUSINESSDATE, ANSUPNAME ");
		//
		List<AssignmentNoticeInfo> lANInfoBeans = assignmentNoticeInfoDAO.findListFromSql(pConnection, lSql.toString(), 0);
		AssignmentNoticesBean lANBean = null;
		AssignmentNoticeDetailsBean lANDBean = null;
		//
		if(lANInfoBeans!=null){
			String[] lEntities = new String[] { "", "", "" }; 
			//String[] lMailDataKeys = new String[] { "sName", "fuCount", "totalFactAmt" }; 
			int lMailCounter = 0;
			for(AssignmentNoticeInfo lANInfoBean : lANInfoBeans ){
				if(lANInfoBean!= null){
					lANBean = lANInfoBean.getAssignmentNoticesBean();
					lANDBean = lANInfoBean.getAssignmentNoticeDetailsBean();
					if(lANBean!=null){
						lEntities[0] = lANBean.getPurchaser();
						lEntities[1] = lANBean.getSupplier();
						lEntities[2] = lANBean.getFinancier();
						//
			        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
						for(int lPtr=0; lPtr < lEntities.length; lPtr++){
				        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_NOTICEOFASSIGNMENT_1,EntityEmail.NOAEmail, lEntities[lPtr], ((lPtr==0)?EmailSenders.TO:EmailSenders.CC)));
				        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_NOTICEOFASSIGNMENT_1,EntityEmail.Explicit, lEntities[lPtr], EmailSenders.CC));
						}
			        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
			            if (!lEmailIds.isEmpty()) {
			            	Map<String, Object> lMailData = new HashMap<String, Object>();
			            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lMailData);
							//the data
							lMailData.put("sName", lANBean.getSupName());
							lMailData.put("fuCount",lANDBean.getFuId());
							lMailData.put("totalFactAmt",TredsHelper.getInstance().getFormattedAmount(lANDBean.getFuFactoredAmount(),true));//this is total factored amt
							//the attachment
							List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
							//change the function parameter to ANId
							lListAttach.add(getNOAPdf(lANDBean.getAnId(), pLoginKey));
							lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
							//
			                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_NOTICEOFASSIGNMENT, lMailData);
			                lMailCounter++;
			            }
					}
				}
			}
			EndOfDayBO.appendMessage(lMessages, "SendNOAMail", lMailCounter + " mails send for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pSettlementDate) );
		}else{
			EndOfDayBO.appendMessage(lMessages, "SendNOAMail", "No Leg1 settlements found for date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, pSettlementDate) );
		}
		return lMessages;
    }
    private MimeBodyPart getNOAPdf(Long pAssignmentNoticeId, String pLoginKey){
    	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
    	try 
    	{
    	    ITextRenderer renderer = new ITextRenderer();
    	    //
			String lUrl = TredsHelper.getInstance().getApplicationURL();
			lUrl += "oblig/noahtml/" + pAssignmentNoticeId;
			lUrl += "?loginKey=" + pLoginKey;						
    	    renderer.setDocument(lUrl);
    	    renderer.layout();
    	    renderer.createPDF(lByteArrayOutputStream);
    	    lByteArrayOutputStream.close();
    	    //
    	    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArrayOutputStream.toByteArray(), "application/pdf")));
			lMimeBodyPart.setFileName("noa_"+ pAssignmentNoticeId+".pdf");
			return lMimeBodyPart;
    	} 
    	catch (Exception ex) 
    	{
    		ex.printStackTrace();
    	}
    	return null;
    }

    public List<Map<String, Object>> sendObligationsDueDetails(Connection pConnection, Long pPaymentFileId, Date pSettlementDate) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
    	CompositeGenericDAO<ObliFUInstDetailBean> lObligationFactoringUnitDAO = null;
    	Date lSettlementDate = pSettlementDate;
    	//
    	if((pPaymentFileId==null || pPaymentFileId.longValue() <= 0) &&
    			pSettlementDate == null){
    		throw new CommonBusinessException("Payment File Id or Settlement Date required for sending Obligations due.");
    	}
    	//
    	lSql.append(" SELECT * ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, Instruments ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 ");
    	lSql.append(" AND FURECORDVERSION > 0 ");
    	lSql.append(" AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID ");
    	lSql.append(" AND INFUID = FUID ");
    	lSql.append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Sent.getCode()));
    	lSql.append(" AND OBTXNTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Debit.getCode()));
		//Obligation of any type L1/L2 should be fetched
    	//
    	if(pPaymentFileId!=null && pPaymentFileId.longValue() > 0){
        	lSql.append(" AND OBPFID = ").append(pPaymentFileId);
    	}
    	if(pSettlementDate!=null){
        	lSql.append(" AND OBDATE = ").append(DBHelper.getInstance().formatDate(pSettlementDate));
    	}
    	lSql.append(" ORDER BY OBTXNENTITY, FUID ");
    	//
        lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailBean>(ObliFUInstDetailBean.class);
    	List<ObliFUInstDetailBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
        TredsHelper lTredsHelper = TredsHelper.getInstance();
		int lMailCounter = 0;
		//
    	if(lObligations!=null && lObligations.size() > 0){
    		ObligationBean lObliBean = null;
    		Map<String, BigDecimal> lEntitywiseTotalObli = new HashMap<String, BigDecimal>();
    		Map<String, List<ObliFUInstDetailBean>> lEntitywiseObliList = new HashMap<String, List<ObliFUInstDetailBean>>();
    		List<ObliFUInstDetailBean> lTmpList = null;
    		BigDecimal lTmpTotal = null;
    		//
    		//cumulating entitywise in hash
    		for(ObliFUInstDetailBean lObliFUInstBean : lObligations){
    			lObliBean = lObliFUInstBean.getObligationBean();
    			if(!lEntitywiseTotalObli.containsKey(lObliBean.getTxnEntity())){
    				lTmpTotal = BigDecimal.ZERO;
    				lEntitywiseTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			}
    			lTmpTotal = lEntitywiseTotalObli.get(lObliBean.getTxnEntity());
    			lTmpTotal = lTmpTotal.add(lObliBean.getAmount());
    			lEntitywiseTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			//
    			if(!lEntitywiseObliList.containsKey(lObliBean.getTxnEntity())){
    				lTmpList = new ArrayList<ObliFUInstDetailBean>();
        			lEntitywiseObliList.put(lObliBean.getTxnEntity(), lTmpList);
    			}
    			lTmpList = lEntitywiseObliList.get(lObliBean.getTxnEntity());
    			lTmpList.add(lObliFUInstBean);
    			//
    			if(lSettlementDate==null){
    				lSettlementDate = lObliBean.getDate();
    			}
    		}    		
    		//looping through hash and sending mails and creating attachment file entitywise
    		AppEntityBean lAppEntity = null;
    		String lEmailNotifyType = null;
    		for(String lEntityCode : lEntitywiseObliList.keySet()){
    			lTmpList = lEntitywiseObliList.get(lEntityCode);
    			lTmpTotal = lEntitywiseTotalObli.get(lEntityCode);
				//
    			Map<String, Object> lMailData = new HashMap<String, Object>();
				//the to and cc
				lAppEntity = TredsHelper.getInstance().getAppEntityBean(lEntityCode);
				lEmailNotifyType = null;
				if(lAppEntity!=null){
					if(lAppEntity.isFinancier()){
						lEmailNotifyType =  AppConstants.EMAIL_NOTIFY_TYPE_CURRENTFINOBLIGATIONDUE_1;
					}
					else if (lAppEntity.isPurchaser()){
						lEmailNotifyType =  AppConstants.EMAIL_NOTIFY_TYPE_CURRENTPUROBLIGATIONDUE_1;
					}else{
						logger.info("1Entity Type different : " + lAppEntity.getCode() + " : " + lAppEntity.getType() );
					}
				}else{
					logger.info("1Entity not found : " + lEntityCode );
				}

				List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lEntityCode, lEmailNotifyType);
				
				lMailData.put(EmailSender.TO, lEmailIds);
				lMailData.put(EmailSender.CC, lTredsHelper.getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
				//
				//the data
				lMailData.put("entityCode", lEntityCode);
				lMailData.put("settlementDate",lSettlementDate);
				lMailData.put("obligationAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal,true));
				//the attachment
				List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
				//change the function parameter to ANId
				lListAttach.add(getObligationsDue(lEntityCode, lSettlementDate, lTmpList,false));
				lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
				//
				if(lAppEntity!=null){
					if(lAppEntity.isFinancier()){
						EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLIDUEL1FINANCIER, lMailData);
		                lMailCounter++;
					}
					else if (lAppEntity.isPurchaser()){
						EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLIDUEL1PURCHASER, lMailData);
		                lMailCounter++;
					}else{
						logger.info("Entity Type different : " + lAppEntity.getCode() + " : " + lAppEntity.getType() );
					}
				}else{
					logger.info("Entity not found : " + lEntityCode );
				}
    		}
        	if(lMailCounter == 0){
        		logger.info("Obligations Due : No Mails sent.");
        	}else{
    			EndOfDayBO.appendMessage(lMessages, "Obligations Due", lMailCounter + " mails send for Obligations due for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
        	}
    	}else{
    		//no obligations found for the same
    		if(lSettlementDate!=null)
    			EndOfDayBO.appendMessage(lMessages, "Obligations Due", lMailCounter + " No Obligations due for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
    		else
    			EndOfDayBO.appendMessage(lMessages, "Obligations Due", lMailCounter + " No Obligations due for payment file id " + (pPaymentFileId!=null?pPaymentFileId:"") );
    	}
    	//
		return lMessages;
    }
    
    public MimeBodyPart getObligationsDue(String pEntityName, Date pSettlementDate,List<ObliFUInstDetailBean> pData,Boolean pAddPayDetails){
    	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
    	StringBuilder lData = new StringBuilder();
    	boolean lIsEntityFinancier = false, lIsEntityPurchaser = false;
    	ObliFUInstDetailBean lBean = null;
    	ObligationBean lObliBean = null;
    	FactoringUnitBean lFUBean = null;
    	InstrumentBean lInstBean = null;
    	String lHeader = null;
    	try 
    	{
    		if(pData!=null){
    			lBean = pData.get(0);
    			lObliBean = lBean.getObligationBean();
    			lFUBean = lBean.getFactoringUnitBean();
    			lInstBean = lBean.getInstrumentBean();
    			//
    			lIsEntityFinancier = lObliBean.getTxnEntity().equals(lFUBean.getFinancier());
    			lIsEntityPurchaser = lObliBean.getTxnEntity().equals(lFUBean.getPurchaser());
    			//
    			if(lIsEntityFinancier){
        			lHeader = "Factoring Unit id,Invoice date,Invoice no,Invoice Amount,Deductions,TDS,Factoring Unit value,Bid acceptance date time,Financier Name,Debit bank account,Debit Amount,Debit Date,Buyer Name,Seller Name,Amount on due date,Due date,Tenor,Discounting Rate,Interest Amount";
    			}else if (lIsEntityPurchaser){
    				lHeader = "Factoring Unit id,Invoice date,Invoice no,Invoice Amount,Deductions,TDS,Factoring Unit value,Financier Name,Buyer Name,Seller Name, Debit amount,Debit date,Leg";    				
    			}
    			if (pAddPayDetails){
    				lHeader += ",Account No,IFSC Code,Beneficiary Name";
    			}
    			lData.append(lHeader);
    			//
    			for(ObliFUInstDetailBean lDataBean : pData){
        			lObliBean = lDataBean.getObligationBean();
        			lFUBean = lDataBean.getFactoringUnitBean();
        			lInstBean = lDataBean.getInstrumentBean();
        			//
        			if (lData.length() > 0) lData.append(NEWLINE);
        			 //Factoring Unit id
        			lData.append(lObliBean.getFuId()).append(CommonConstants.COMMA);
        			//Invoice date
        			lData.append(lInstBean.getInstDate()).append(CommonConstants.COMMA);
        			//Invoice no
        			lData.append(lInstBean.getInstNumber()).append(CommonConstants.COMMA);
        			//Invoice Amount
        			lData.append(lInstBean.getAmount()).append(CommonConstants.COMMA);
        			//Deductions
        			lData.append(lInstBean.getAdjAmount()).append(CommonConstants.COMMA);
        			//TDS
        			lData.append(lInstBean.getTdsAmount()).append(CommonConstants.COMMA);
        			//Factoring Unit value
        			lData.append(lFUBean.getFactoredAmount()).append(CommonConstants.COMMA);
        			//Bid acceptance date time
        			if(lIsEntityFinancier) lData.append(lFUBean.getAcceptDateTime()).append(CommonConstants.COMMA);
        			//Financier Name
        			lData.append(lFUBean.getFinName()).append(CommonConstants.COMMA);
					//Financier Account Number
        			if(lIsEntityFinancier) lData.append("'").append(lObliBean.getPayDetail1()).append(CommonConstants.COMMA);
        			//Debit Amount
        			if(lIsEntityFinancier) lData.append(lObliBean.getAmount()).append(CommonConstants.COMMA);
        			//Debit Date
        			if(lIsEntityFinancier) lData.append(lObliBean.getDate()).append(CommonConstants.COMMA);
        			//Buyer Name
        			lData.append(lFUBean.getPurName()).append(CommonConstants.COMMA);
        			//Seller Name
        			lData.append(lFUBean.getSupName()).append(CommonConstants.COMMA);
        			//amount on due date - leg 2 amt
        			if(lIsEntityFinancier) 
        				lData.append(lFUBean.getFinLeg2Amount()).append(CommonConstants.COMMA);
        			else if(lIsEntityPurchaser) 
        				lData.append(lObliBean.getAmount()).append(CommonConstants.COMMA);
        			//Due date 
        			if(lIsEntityFinancier && ObligationBean.Type.Leg_1.equals(lObliBean.getType())){
            			lData.append(lFUBean.getLeg2MaturityExtendedDate()).append(CommonConstants.COMMA);
        			}else{
            			lData.append(lObliBean.getDate()).append(CommonConstants.COMMA);
        			}
        			//Tenor
        			if(lIsEntityFinancier) lData.append(TredsHelper.getInstance().getTenure(lFUBean.getLeg2MaturityExtendedDate(), lFUBean.getLeg1Date())).append(CommonConstants.COMMA);
        			//Discounting Rate
        			if(lIsEntityFinancier) lData.append(lFUBean.getAcceptedRate()).append(CommonConstants.COMMA);
        			//Interest Amount        			
        			if(lIsEntityFinancier) lData.append(lFUBean.getTotalCost()).append(CommonConstants.COMMA);
        			//Leg
        			if(lIsEntityPurchaser) lData.append(lObliBean.getType()).append(CommonConstants.COMMA);
        			
        			if(pAddPayDetails) lData.append(lObliBean.getPayDetail1()).append(CommonConstants.COMMA);
        			if(pAddPayDetails) lData.append(lObliBean.getPayDetail2()).append(CommonConstants.COMMA);
        			if(pAddPayDetails) lData.append(lObliBean.getPayDetail3()).append(CommonConstants.COMMA);
    			}
    		}
    		lByteArrayOutputStream.write(lData.toString().getBytes());
    	    lByteArrayOutputStream.close();
    	    //
    	    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArrayOutputStream.toByteArray(), "application/csv")));
			lMimeBodyPart.setFileName("Obli_"+pEntityName +"_"+FormatHelper.getDisplay("yyyyMMdd", pSettlementDate) +".csv");
			return lMimeBodyPart;
    	} 
    	catch (Exception ex) 
    	{
    		ex.printStackTrace();
    	}
    	return null;
    }
    

    public List<Map<String, Object>> sendLeg1StatusTransactionDetails(Connection pConnection, Date pSettlementDate, List<Long> pCurrentUnprocessedList) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
    	CompositeGenericDAO<ObliFUInstDetailBean> lObligationFactoringUnitDAO = null;
    	Date lSettlementDate = pSettlementDate;
    	//
    	if(pSettlementDate == null){
    		throw new CommonBusinessException("Settlement Date required for sending Leg 1 obligation Status.");
    	}
    	//
    	lSql.append(" SELECT * ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, Instruments ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 ");
    	lSql.append(" AND FURECORDVERSION > 0 ");
    	lSql.append(" AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID ");
    	lSql.append(" AND INFUID = FUID ");
    	lSql.append(" AND OBTXNTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Debit.getCode()));
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
    	lSql.append(" AND OBSTATUS != ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Sent.getCode()));
    	//
    	lSql.append(" AND OBDATE = ").append(DBHelper.getInstance().formatDate(pSettlementDate));
    	//
    	if(pCurrentUnprocessedList!=null && pCurrentUnprocessedList.size() > 0){
    		String[] lCurrentUnprocessedListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(pCurrentUnprocessedList);
    		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", lCurrentUnprocessedListStr)).append("  ");
    	}
    	//
    	lSql.append(" ORDER BY OBTXNENTITY, FUID ");
    	//
        lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailBean>(ObliFUInstDetailBean.class);
    	List<ObliFUInstDetailBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
		Map<String, Object> lMailData = null;
        TredsHelper lTredsHelper = TredsHelper.getInstance();
		int lMailCounter = 0;
		//
    	if(lObligations!=null && lObligations.size() > 0){
    		ObligationBean lObliBean = null;
    		Map<String, BigDecimal[]> lEntitywiseTotalObli = new HashMap<String, BigDecimal[]>();
    		Map<String, List<ObliFUInstDetailBean>> lEntitywiseObliList = new HashMap<String, List<ObliFUInstDetailBean>>();
    		List<ObliFUInstDetailBean> lTmpList = null;
    		BigDecimal[] lTmpTotal = null; //0=Total   1=Success
    		//
    		//cumulating entitywise in hash
    		for(ObliFUInstDetailBean lObliFUInstBean : lObligations){
    			lObliBean = lObliFUInstBean.getObligationBean();
    			if(!lEntitywiseTotalObli.containsKey(lObliBean.getTxnEntity())){
    				lTmpTotal = new BigDecimal[2];
    				lTmpTotal[0] = BigDecimal.ZERO;
    				lTmpTotal[1] = BigDecimal.ZERO;
    				lEntitywiseTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			}
    			lTmpTotal = lEntitywiseTotalObli.get(lObliBean.getTxnEntity());
    			lTmpTotal[0] = lTmpTotal[0].add(lObliBean.getAmount());
    			if(ObligationBean.Status.Success.equals(lObliBean.getStatus()))
        			lTmpTotal[1] = lTmpTotal[1].add(lObliBean.getAmount());
    			lEntitywiseTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			//
    			if(!lEntitywiseObliList.containsKey(lObliBean.getTxnEntity())){
    				lTmpList = new ArrayList<ObliFUInstDetailBean>();
        			lEntitywiseObliList.put(lObliBean.getTxnEntity(), lTmpList);
    			}
    			lTmpList = lEntitywiseObliList.get(lObliBean.getTxnEntity());
    			lTmpList.add(lObliFUInstBean);
    			//
    			if(lSettlementDate==null){
    				lSettlementDate = lObliBean.getDate();
    			}
    		}    		
    		//looping through hash and sending mails and creating attachment file entitywise
    		for(String lEntityCode : lEntitywiseObliList.keySet()){
    			lTmpList = lEntitywiseObliList.get(lEntityCode);
    			lTmpTotal = lEntitywiseTotalObli.get(lEntityCode);
				//
				lMailData = new HashMap<String, Object>();
				//the to and cc
		    	List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lEntityCode, AppConstants.EMAIL_NOTIFY_TYPE_OBLIGATIONSTATUS_1);
				lMailData.put(EmailSender.TO, lEmailIds);
				lMailData.put(EmailSender.CC, lTredsHelper.getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
				//
				//the data
				lMailData.put("entityCode", lEntityCode);
				lMailData.put("settlementDate",lSettlementDate);
				lMailData.put("totalObligationAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[0],true));
				lMailData.put("settledAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[1],true));
				lMailData.put("failedCancelledAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[0].subtract(lTmpTotal[1]),true));
				//the attachment
				List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
				//change the function parameter to ANId
				lListAttach.add(getLeg1TransactionDetails(lEntityCode, lSettlementDate, lTmpList));
				lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
				//
				EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLILEG1STATUS, lMailData);
                lMailCounter++;
    		}
        	if(lMailCounter == 0){
        		logger.info("Leg1 Trans Status : No Mails sent.");
        	}else{
    			EndOfDayBO.appendMessage(lMessages, "Leg1 Trans Status", lMailCounter + " mails send indicating Leg1 transaction status for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
        	}
    	}else{
    		//no obligations found for the same
			EndOfDayBO.appendMessage(lMessages, "Leg1 Trans Status", lMailCounter + " No Obligations found for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
    	}
    	//
		return lMessages;
    }
    
    
    public List<Map<String, Object>> sendLeg1StatusTransactionDetailsPurchaser(Connection pConnection, Date pSettlementDate, List<Long> pCurrentUnprocessedList) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
    	CompositeGenericDAO<ObliFUInstDetailBean> lObligationFactoringUnitDAO = null;
    	Date lSettlementDate = pSettlementDate;
    	//
    	if(pSettlementDate == null){
    		throw new CommonBusinessException("Settlement Date required for sending Leg 1  (Purchaser) obligation Status.");
    	}
    	//
    	lSql.append(" SELECT * ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, Instruments ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 ");
    	lSql.append(" AND FURECORDVERSION > 0 ");
    	lSql.append(" AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID ");
    	lSql.append(" AND INFUID = FUID ");
    	lSql.append(" AND OBTXNTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Credit.getCode()));
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_1.getCode()));
    	lSql.append(" AND FUSUPPLIER = OBTXNENTITY ");
    	lSql.append(" AND OBSTATUS != ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Sent.getCode()));
    	//
    	lSql.append(" AND OBDATE = ").append(DBHelper.getInstance().formatDate(pSettlementDate));
    	//
    	if(pCurrentUnprocessedList!=null && pCurrentUnprocessedList.size() > 0){
    		String[] lCurrentUnprocessedListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(pCurrentUnprocessedList);
    		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", lCurrentUnprocessedListStr)).append("  ");
    	}
    	//
    	lSql.append(" ORDER BY OBTXNENTITY, FUID ");
    	//
        lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailBean>(ObliFUInstDetailBean.class);
    	List<ObliFUInstDetailBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
		Map<String, Object> lMailData = null;
        TredsHelper lTredsHelper = TredsHelper.getInstance();
		int lMailCounter = 0;
		//
    	if(lObligations!=null && lObligations.size() > 0){
    		ObligationBean lObliBean = null;
    		FactoringUnitBean lFuBean = null;
    		//Key=Purchaser , Value = Hash(Key=Supplier, Value=new Long{ 0=Total, 1=SuccessTotal }
    		Map<String,Map<String, BigDecimal[]>> lPurchwiseSuppTotalObli = new HashMap<String,Map<String, BigDecimal[]>>();
    		Map<String, BigDecimal[]> lSuppTotalObli = new HashMap<String, BigDecimal[]>();
    		//Key=Purchaser , Value=List of Obligation of mixed suppliers
    		Map<String, List<ObliFUInstDetailBean>> lPurchwiseObliList = new HashMap<String, List<ObliFUInstDetailBean>>();
    		List<ObliFUInstDetailBean> lTmpList = null;
    		BigDecimal[] lTmpTotal = null; //0=Total   1=Success
    		//
    		//cumulating entitywise in hash
    		for(ObliFUInstDetailBean lObliFUInstBean : lObligations){
    			lObliBean = lObliFUInstBean.getObligationBean();
    			lFuBean = lObliFUInstBean.getFactoringUnitBean();
    			if(!lPurchwiseSuppTotalObli.containsKey(lFuBean.getPurchaser())){
    				lSuppTotalObli = new HashMap<String, BigDecimal[]>();
    				lPurchwiseSuppTotalObli.put(lFuBean.getPurchaser(), lSuppTotalObli);
    			}
    			if(!lSuppTotalObli.containsKey(lObliBean.getTxnEntity())){
    				lTmpTotal = new BigDecimal[2];
    				lTmpTotal[0] = BigDecimal.ZERO;
    				lTmpTotal[1] = BigDecimal.ZERO;
    				lSuppTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			}
    			lTmpTotal = lSuppTotalObli.get(lObliBean.getTxnEntity());
    			lTmpTotal[0] = lTmpTotal[0].add(lObliBean.getAmount());
    			if(ObligationBean.Status.Success.equals(lObliBean.getStatus()))
        			lTmpTotal[1] = lTmpTotal[1].add(lObliBean.getAmount());
    			lSuppTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			//
    			if(!lPurchwiseObliList.containsKey(lFuBean.getPurchaser())){
    				lTmpList = new ArrayList<ObliFUInstDetailBean>();
        			lPurchwiseObliList.put(lFuBean.getPurchaser(), lTmpList);
    			}
    			lTmpList = lPurchwiseObliList.get(lFuBean.getPurchaser());
    			lTmpList.add(lObliFUInstBean);
    			//
    			if(lSettlementDate==null){
    				lSettlementDate = lObliBean.getDate();
    			}
    		}    		
    		//looping through hash and sending mails and creating attachment file entitywise
    		List<Map<String,Object>> lSuppTotalDataList = new ArrayList<Map<String,Object>>();
    		Map<String,Object> lSuppData = null;
    		for(String lPurchaserEntityCode : lPurchwiseObliList.keySet()){
    			//get the purchaser-wise obligations list
    			lTmpList = lPurchwiseObliList.get(lPurchaserEntityCode);
    			//get the purchaser-wise ( hash of supplier-wise totals )
    			lSuppTotalObli = lPurchwiseSuppTotalObli.get(lPurchaserEntityCode);
    			
				lMailData = new HashMap<String, Object>();
				//the to and cc
		    	//List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lEntityCode, AppConstants.EMAIL_NOTIFY_TYPE_OBLIGATIONSTATUS_1);
				//lMailData.put(EmailSender.TO, lEmailIds);
				lMailData.put(EmailSender.TO, lTredsHelper.getAdminUserEmail(lPurchaserEntityCode));
				lMailData.put(EmailSender.CC, lTredsHelper.getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
				//
				//supplierwise totals
				for(String lSuppKey : lSuppTotalObli.keySet()){
					//the data
					lTmpTotal = lSuppTotalObli.get(lSuppKey);
					lSuppData = new HashMap<String, Object>(); 
					lSuppData.put("entityCode", lSuppKey);
					lSuppData.put("settlementDate",lSettlementDate);
					lSuppData.put("totalObligationAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[0],true));
					lSuppData.put("settledAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[1],true));
					lSuppData.put("failedCancelledAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[0].subtract(lTmpTotal[1]),true));
					lSuppTotalDataList.add(lSuppData);
				}
				lMailData.put("suppList", lSuppTotalDataList);
				//the attachment
				List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
				//change the function parameter to ANId
				lListAttach.add(getLeg1TransactionDetails(lPurchaserEntityCode, lSettlementDate, lTmpList));
				lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
				//
				EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLILEG1STATUSPURCHSER, lMailData);
                lMailCounter++;
    		}
        	if(lMailCounter == 0){
        		logger.info("Leg1 Trans Status (Purchaser) : No Mails sent.");
        	}else{
    			EndOfDayBO.appendMessage(lMessages, "Leg1 Trans Status (Purchaser) ", lMailCounter + " mails send indicating Leg1 transaction status for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
        	}
    	}else{
    		//no obligations found for the same
			EndOfDayBO.appendMessage(lMessages, "Leg1 Trans Status (Purchaser) ", lMailCounter + " No Obligations found for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
    	}
    	//
		return lMessages;
    }
    
 	//TODO: _PM what to do of this function?????
    public List<Map<String, Object>> sendLeg2StatusTransactionDetailsPurchaser(Connection pConnection, Date pSettlementDate, List<Long> pCurrentUnprocessedList) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
    	CompositeGenericDAO<ObliFUInstDetailBean> lObligationFactoringUnitDAO = null;
    	Date lSettlementDate = pSettlementDate;
    	//
    	if(pSettlementDate == null){
    		throw new CommonBusinessException("Settlement Date required for sending Leg 2  (Purchaser) obligation Status.");
    	}
    	//
    	lSql.append(" SELECT * ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, Instruments ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 ");
    	lSql.append(" AND FURECORDVERSION > 0 ");
    	lSql.append(" AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID ");
    	lSql.append(" AND INFUID = FUID ");
    	lSql.append(" AND OBTXNTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Debit.getCode()));
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_2.getCode()));
    	lSql.append(" AND FUSUPPLIER = OBTXNENTITY ");
    	lSql.append(" AND OBSTATUS != ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Sent.getCode()));
    	//
    	lSql.append(" AND OBDATE = ").append(DBHelper.getInstance().formatDate(pSettlementDate));
    	//
    	if(pCurrentUnprocessedList!=null && pCurrentUnprocessedList.size() > 0){
    		String[] lCurrentUnprocessedListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(pCurrentUnprocessedList);
    		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", lCurrentUnprocessedListStr)).append("  ");
    	}
    	//
    	lSql.append(" ORDER BY OBTXNENTITY, FUID ");
    	//
        lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailBean>(ObliFUInstDetailBean.class);
    	List<ObliFUInstDetailBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
		Map<String, Object> lMailData = null;
        TredsHelper lTredsHelper = TredsHelper.getInstance();
		int lMailCounter = 0;
		//
    	if(lObligations!=null && lObligations.size() > 0){
    		ObligationBean lObliBean = null;
    		FactoringUnitBean lFuBean = null;
    		//Key=Purchaser , Value = Hash(Key=Supplier, Value=new Long{ 0=Total, 1=SuccessTotal }
    		Map<String,Map<String, BigDecimal[]>> lPurchwiseSuppTotalObli = new HashMap<String,Map<String, BigDecimal[]>>();
    		Map<String, BigDecimal[]> lSuppTotalObli = new HashMap<String, BigDecimal[]>();
    		//Key=Purchaser , Value=List of Obligation of mixed suppliers
    		Map<String, List<ObliFUInstDetailBean>> lPurchwiseObliList = new HashMap<String, List<ObliFUInstDetailBean>>();
    		List<ObliFUInstDetailBean> lTmpList = null;
    		BigDecimal[] lTmpTotal = null; //0=Total   1=Success
    		//
    		//cumulating entitywise in hash
    		for(ObliFUInstDetailBean lObliFUInstBean : lObligations){
    			lObliBean = lObliFUInstBean.getObligationBean();
    			lFuBean = lObliFUInstBean.getFactoringUnitBean();
    			if(!lPurchwiseSuppTotalObli.containsKey(lFuBean.getPurchaser())){
    				lSuppTotalObli = new HashMap<String, BigDecimal[]>();
    				lPurchwiseSuppTotalObli.put(lFuBean.getPurchaser(), lSuppTotalObli);
    			}
    			if(!lSuppTotalObli.containsKey(lObliBean.getTxnEntity())){
    				lTmpTotal = new BigDecimal[2];
    				lTmpTotal[0] = BigDecimal.ZERO;
    				lTmpTotal[1] = BigDecimal.ZERO;
    				lSuppTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			}
    			lTmpTotal = lSuppTotalObli.get(lObliBean.getTxnEntity());
    			lTmpTotal[0] = lTmpTotal[0].add(lObliBean.getAmount());
    			if(ObligationBean.Status.Success.equals(lObliBean.getStatus()))
        			lTmpTotal[1] = lTmpTotal[1].add(lObliBean.getAmount());
    			lSuppTotalObli.put(lObliBean.getTxnEntity(), lTmpTotal);
    			//
    			if(!lPurchwiseObliList.containsKey(lFuBean.getPurchaser())){
    				lTmpList = new ArrayList<ObliFUInstDetailBean>();
        			lPurchwiseObliList.put(lFuBean.getPurchaser(), lTmpList);
    			}
    			lTmpList = lPurchwiseObliList.get(lFuBean.getPurchaser());
    			lTmpList.add(lObliFUInstBean);
    			//
    			if(lSettlementDate==null){
    				lSettlementDate = lObliBean.getDate();
    			}
    		}    		
    		//looping through hash and sending mails and creating attachment file entitywise
    		List<Map<String,Object>> lSuppTotalDataList = new ArrayList<Map<String,Object>>();
    		Map<String,Object> lSuppData = null;
    		for(String lPurchaserEntityCode : lPurchwiseObliList.keySet()){
    			//get the purchaser-wise obligations list
    			lTmpList = lPurchwiseObliList.get(lPurchaserEntityCode);
    			//get the purchaser-wise ( hash of supplier-wise totals )
    			lSuppTotalObli = lPurchwiseSuppTotalObli.get(lPurchaserEntityCode);
    			
				lMailData = new HashMap<String, Object>();
				//the to and cc
		    	//List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lEntityCode, AppConstants.EMAIL_NOTIFY_TYPE_OBLIGATIONSTATUS_1);
				//lMailData.put(EmailSender.TO, lEmailIds);
				lMailData.put(EmailSender.TO, lTredsHelper.getAdminUserEmail(lPurchaserEntityCode,true));
				lMailData.put(EmailSender.CC, lTredsHelper.getAdminUserEmail(AppConstants.DOMAIN_PLATFORM,true));
				//
				//supplierwise totals
				for(String lSuppKey : lSuppTotalObli.keySet()){
					//the data
					lTmpTotal = lSuppTotalObli.get(lSuppKey);
					lSuppData = new HashMap<String, Object>(); 
					lSuppData.put("entityCode", lSuppKey);
					lSuppData.put("settlementDate",lSettlementDate);
					lSuppData.put("totalObligationAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[0],true));
					lSuppData.put("settledAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[1],true));
					lSuppData.put("failedCancelledAmt",TredsHelper.getInstance().getFormattedAmount(lTmpTotal[0].subtract(lTmpTotal[1]),true));
					lSuppTotalDataList.add(lSuppData);
				}
				lMailData.put("suppList", lSuppTotalDataList);
				//the attachment
				List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
				//change the function parameter to ANId
				lListAttach.add(getLeg1TransactionDetails(lPurchaserEntityCode, lSettlementDate, lTmpList));
				lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
				//
				EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLILEG1STATUSPURCHSER, lMailData);
                lMailCounter++;
    		}
        	if(lMailCounter == 0){
        		logger.info("Leg1 Trans Status (Purchaser) : No Mails sent.");
        	}else{
    			EndOfDayBO.appendMessage(lMessages, "Leg1 Trans Status (Purchaser) ", lMailCounter + " mails send indicating Leg1 transaction status for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
        	}
    	}else{
    		//no obligations found for the same
			EndOfDayBO.appendMessage(lMessages, "Leg1 Trans Status (Purchaser) ", lMailCounter + " No Obligations found for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
    	}
    	//
		return lMessages;
    }
    

    
    public List<Map<String, Object>> sendLeg2StatusTransactionDetails(Connection pConnection, Date pSettlementDate, List<Long> pCurrentUnprocessedList) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
    	CompositeGenericDAO<ObliFUInstDetailSplitsBean> lObligationFactoringUnitDAO = null;
    	Date lSettlementDate = pSettlementDate;
    	Map<Long,ObligationSplitsBean> lObligationSplitList = null; 
    	//
    	if(pSettlementDate == null){
    		throw new CommonBusinessException("Settlement Date required for sending Leg 2 obligation Status.");
    	}
    	//
    	lSql.append(" SELECT * ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, Instruments, OBLIGATIONSPLITS   ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 ");
    	lSql.append(" AND FURECORDVERSION > 0 ");
    	lSql.append(" AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBSRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID ");
    	lSql.append(" AND INFUID = FUID ");
    	lSql.append(" AND OBSOBID = OBID ");
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_2.getCode()));
    	lSql.append(" AND OBSSTATUS IN ( ");
    		lSql.append(DBHelper.getInstance().formatString(ObligationBean.Status.Failed.getCode()));
    		lSql.append(" , ");
    		lSql.append(DBHelper.getInstance().formatString(ObligationBean.Status.Cancelled.getCode()));
    	lSql.append(" ) ");
    	//
    	lSql.append(" AND OBDATE = ").append(DBHelper.getInstance().formatDate(pSettlementDate));
    	//
    	if(pCurrentUnprocessedList!=null && pCurrentUnprocessedList.size() > 0){
    		String[] lCurrentUnprocessedListStr = TredsHelper.getInstance().getCSVIdsListForInQuery(pCurrentUnprocessedList);
    		lSql.append(" AND ").append(TredsHelper.getInstance().getInQuery("OBId", lCurrentUnprocessedListStr)).append("  ");
    	}
    	//
    	lSql.append(" ORDER BY OBTXNENTITY, FUID, OBSPARTNUMBER");
    	//
        lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailSplitsBean>(ObliFUInstDetailSplitsBean.class);
    	List<ObliFUInstDetailSplitsBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);

        TredsHelper lTredsHelper = TredsHelper.getInstance();
        Map<String, FacilitatorEntityMappingBean> lFacilitatorMap = null;
		int lMailCounter = 0;
		//
    	if(lObligations!=null && lObligations.size() > 0){
    		ObligationBean lObliBean = null;
    		Map<Long, Object> lFuwiseDetails = new HashMap<Long,Object>();
    		Object[] lData = null; 
    		//0=FinObligation,
    		//1=PurObligation,
    		//2=FactUnit,
    		//3=Instrument,
    		//4=lFinAppEntity,
    		//5=lPurAppentity.
    		//
    		lData = new Object[10];
    		boolean lFirstTimeFlag = false;
    		for(ObliFUInstDetailSplitsBean lObliFUInstBean : lObligations){
    			lObliBean = lObliFUInstBean.getObligationBean();
    			lFirstTimeFlag = false;
    			if(!lFuwiseDetails.containsKey(lObliBean.getFuId())){
    				lFuwiseDetails.put(lObliBean.getFuId(), new Object[10]);
    				lFirstTimeFlag = true;
    			}
    			lData = (Object[]) lFuwiseDetails.get(lObliBean.getFuId());
    			AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(lObliBean.getTxnEntity());
    			if(lFirstTimeFlag){
    				lData[FACTORINGUNITBEAN] = lObliFUInstBean.getFactoringUnitBean();
    				lData[INSTRUMENTBEAN] = lObliFUInstBean.getInstrumentBean();
    			}
    			if (lAEBean.isFinancier() ){
    				if ( lData[FINOBLIGATIONBEAN] == null ){
        				lData[FINOBLIGATIONBEAN] = lObliBean;
        				lData[FINAPPENTITYBEAN] = lAEBean;
        				lData[FINOBLIGATIONSPLITBEAN] = new HashMap<Long,ObligationSplitsBean>();
    				}
    				lObligationSplitList =  (Map<Long, ObligationSplitsBean>) lData[FINOBLIGATIONSPLITBEAN]; 
    				lObligationSplitList.put(lObliFUInstBean.getObligationSplitBean().getPartNumber(), lObliFUInstBean.getObligationSplitBean());
				}else if (lAEBean.isPurchaser()){
					if( lData[PUROBLIGATIONBEAN] == null ){
						lData[PUROBLIGATIONBEAN]=lObliBean;
						lData[PURAPPENTITYBEAN] = lAEBean;
						lData[PUROBLIGATIONSPLITBEAN] = new HashMap<Long,ObligationSplitsBean>();
    				}
    				lObligationSplitList = (Map<Long,ObligationSplitsBean>) lData[PUROBLIGATIONSPLITBEAN]; 
    				lObligationSplitList.put(lObliFUInstBean.getObligationSplitBean().getPartNumber(), lObliFUInstBean.getObligationSplitBean());
				}else{
    				continue;
    			}
    		}    		
    		// send memo as attachment to financier
    		Object[] lTransactionDetails = null;
    		AppEntityBean lAppEntity = null;
    		ObligationBean lObligationBean =  null;
    		Map<String,CompanyBankDetailBean> lCompanyBankMap = new HashMap<String,CompanyBankDetailBean>();
    		Map<String,BankBranchDetailBean> lBankBranchMap = new HashMap<String,BankBranchDetailBean>();
    		lFacilitatorMap = TredsHelper.getInstance().getFacilitatorEntityMap(pConnection, AppConstants.FACILITATOR_NPCI);
    		//loop through the FUIds and send the corresponding mails
    		for(Long lFuid: lFuwiseDetails.keySet()){
    			lTransactionDetails = (Object[]) lFuwiseDetails.get(lFuid);
    			lAppEntity = (AppEntityBean) lTransactionDetails[FINAPPENTITYBEAN];
    			for (int i=0 ; i<2 ;i++){
    				// 0 = FINOBLIGATIONBEAN , 1 = PUROBLIGATIONBEAN
    				lObligationBean = (ObligationBean) lTransactionDetails[i];
    				Map<String, Object> lMailData = new HashMap<String, Object>();
    				//the to and cc
    		    	List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lObligationBean.getTxnEntity(), AppConstants.EMAIL_NOTIFY_TYPE_OBLIGATIONL2STATUS_1);
    				lMailData.put(EmailSender.TO, lEmailIds);
    				lMailData.put(EmailSender.CC, lTredsHelper.getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
    				//
    				//the data
    				lMailData.put("entityCode", lObligationBean.getTxnEntity());
    				lMailData.put("settlementDate",lSettlementDate);
    				lMailData.put("obligationAmt",TredsHelper.getInstance().getFormattedAmount(lObligationBean.getAmount(),true));
    				lMailData.put("settledAmt",TredsHelper.getInstance().getFormattedAmount(lObligationBean.getSettledAmount(),true));
    				lMailData.put("failedCancelledAmt",TredsHelper.getInstance().getFormattedAmount(lObliBean.getAmount().subtract(lObligationBean.getSettledAmount()),true));
    				// add attachment only if financier.
    				if (lAppEntity.isFinancier() && lAppEntity.getCode().equals(lObligationBean.getTxnEntity())){
    					// add attachment here 
    					//addMemoAndMandateAsAttachment(pConnection,lTransactionDetails, lMailData, lCompanyBankMap, lBankBranchMap, lFacilitatorMap);
    				}
    				EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLILEG2STATUS, lMailData);
    				lMailCounter++;
    			}
    		}
        	if(lMailCounter == 0){
        		logger.info("Leg2 Trans Status : No Mails sent.");
        	}else{
    			EndOfDayBO.appendMessage(lMessages, "Leg2 Trans Status", lMailCounter + " mails send indicating Leg1 transaction status for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
        	}
    	}else{
			EndOfDayBO.appendMessage(lMessages, "Leg2 Trans Status", lMailCounter + " No Obligations found for Settlement date " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
    	}
    	//
		return lMessages;
    }
    
    private MimeBodyPart getLeg1TransactionDetails(String pEntityName, Date pSettlementDate,List<ObliFUInstDetailBean> pData){
    	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
    	StringBuilder lData = new StringBuilder();
    	ObliFUInstDetailBean lBean = null;
    	ObligationBean lObliBean = null;
    	FactoringUnitBean lFUBean = null;
    	InstrumentBean lInstBean = null;
    	String lHeader = null;
    	try 
    	{
    		if(pData!=null){
    			lBean = pData.get(0);
    			lObliBean = lBean.getObligationBean();
    			lFUBean = lBean.getFactoringUnitBean();
    			lInstBean = lBean.getInstrumentBean();
    			//
    			lHeader = "Factoring Unit id, Invoice date, Invoice no, Invoice Amount, Deductions, TDS, Factoring Unit value, Bid acceptance date time, Financier Name, Buyer Name, Seller Name, Amount, Due date, Status";

    			lData.append(lHeader);
    			//
    			for(ObliFUInstDetailBean lDataBean : pData){
        			lObliBean = lDataBean.getObligationBean();
        			lFUBean = lDataBean.getFactoringUnitBean();
        			lInstBean = lDataBean.getInstrumentBean();
        			//
        			if (lData.length() > 0) lData.append(NEWLINE);
        			 //Factoring Unit id
        			lData.append(lObliBean.getFuId()).append(CommonConstants.COMMA);
        			//Invoice date
        			lData.append(lInstBean.getInstDate()).append(CommonConstants.COMMA);
        			//Invoice no
        			lData.append(lInstBean.getInstNumber()).append(CommonConstants.COMMA);
        			//Invoice Amount
        			lData.append(lInstBean.getAmount()).append(CommonConstants.COMMA);
        			//Deductions
        			lData.append(lInstBean.getAdjAmount()).append(CommonConstants.COMMA);
        			//TDS
        			lData.append(lInstBean.getTdsAmount()).append(CommonConstants.COMMA);
        			//Factoring Unit value
        			lData.append(lFUBean.getFactoredAmount()).append(CommonConstants.COMMA);
        			//Bid acceptance date time
        			lData.append(lFUBean.getAcceptDateTime()).append(CommonConstants.COMMA);
        			//Financier Name
        			lData.append(lFUBean.getFinName()).append(CommonConstants.COMMA);
        			//
        			//Buyer Name
        			lData.append(lFUBean.getPurName()).append(CommonConstants.COMMA);
        			//Seller Name
        			lData.append(lFUBean.getSupName()).append(CommonConstants.COMMA);
       			 	//Credit amount ???
        			lData.append(lObliBean.getAmount()).append(CommonConstants.COMMA);
       			 	//Due date ??
        			lData.append(lObliBean.getSettledDate()).append(CommonConstants.COMMA);
       			 	//Status
        			lData.append(lObliBean.getStatus().toString());
        			//
    			}
    		}
    		lByteArrayOutputStream.write(lData.toString().getBytes());
    	    lByteArrayOutputStream.close();
    	    //
    	    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArrayOutputStream.toByteArray(), "application/csv")));
			lMimeBodyPart.setFileName("Obligation_status_" + pEntityName +"_"+FormatHelper.getDisplay("yyyyMMdd", pSettlementDate) +".csv");
			return lMimeBodyPart;
    	} 
    	catch (Exception ex) 
    	{
    		ex.printStackTrace();
    	}
    	return null;
    }
    

    public List<Map<String, Object>> sendLeg2ObliNext5DaysDetails(Connection pConnection, Date pSettlementDate) throws Exception {
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	StringBuilder lSql = new StringBuilder();
    	CompositeGenericDAO<ObliFUInstDetailBean> lObligationFactoringUnitDAO = null;
    	Date lSettlementDate = pSettlementDate;
    	Date[] lSettlementDates = new Date[5];
    	//
    	if(pSettlementDate == null){
    		throw new CommonBusinessException("Settlement Date required for sending Leg 2 obligation for next 5 days.");
    	}
    	//
    	lSettlementDates[0] = pSettlementDate;
    	for(int lPtr=1; lPtr < 5; lPtr++){
    		lSettlementDates[lPtr] = OtherResourceCache.getInstance().getNextClearingDate(lSettlementDates[lPtr-1], 1);
    	}
    	//
    	lSql.append(" SELECT * ");
    	lSql.append(" FROM OBLIGATIONS, FACTORINGUNITS, Instruments ");
    	lSql.append(" WHERE OBRECORDVERSION > 0 ");
    	lSql.append(" AND FURECORDVERSION > 0 ");
    	lSql.append(" AND INRECORDVERSION > 0 ");
    	lSql.append(" AND OBFUID = FUID ");
    	lSql.append(" AND INFUID = FUID ");
    	lSql.append(" AND OBTXNENTITY = INPURCHASER ");//ONLY PURCHASER
    	lSql.append(" AND OBTXNTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.TxnType.Debit.getCode()));
    	lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_2.getCode()));
    	lSql.append(" AND OBSTATUS = ").append(DBHelper.getInstance().formatString(ObligationBean.Status.Ready.getCode()));
    	//
    	lSql.append(" AND OBDATE IN ( ");
    	for(int lPtr=0; lPtr<5; lPtr++){
    		if(lPtr>0 && lSettlementDates[lPtr]!=null)
    			lSql.append(",");
    		if(lSettlementDates[lPtr]!=null)
    			lSql.append(DBHelper.getInstance().formatDate(lSettlementDates[lPtr]));
    	}
    	lSql.append(" ) ");    	
    	lSql.append(" ORDER BY OBDATE ASC, OBTXNENTITY, FUID ");
    	//
        lObligationFactoringUnitDAO = new CompositeGenericDAO<ObliFUInstDetailBean>(ObliFUInstDetailBean.class);
    	List<ObliFUInstDetailBean> lObligations = lObligationFactoringUnitDAO.findListFromSql(pConnection, lSql.toString(), -1);
		Map<String, Object> lMailData = null;
        TredsHelper lTredsHelper = TredsHelper.getInstance();
		int lMailCounter = 0;
		//
    	if(lObligations!=null && lObligations.size() > 0){
    		ObligationBean lObliBean = null;
    		Map<String, Map<String, BigDecimal>> lEntitywiseTotalObli = new HashMap<String, Map<String,BigDecimal>>();
    		Map<String, List<ObliFUInstDetailBean>> lEntitywiseObliList = new HashMap<String, List<ObliFUInstDetailBean>>();
    		List<ObliFUInstDetailBean> lTmpList = null;
    		Map<String, BigDecimal> lDatewiseTotals = null; //key=SettlmentDate  Value=Total Dues for SettlementDate
    		BigDecimal lTmpTotal = null;
    		//
    		//cumulating entitywise in hash
    		String lObliDateStr = null;
    		for(ObliFUInstDetailBean lObliFUInstBean : lObligations){
    			lObliBean = lObliFUInstBean.getObligationBean();
    			lObliDateStr = FormatHelper.getDisplay(AppConstants.DATE_FORMAT ,lObliBean.getDate());
    			if(!lEntitywiseTotalObli.containsKey(lObliBean.getTxnEntity())){
    				lDatewiseTotals = new HashMap<String, BigDecimal>();
    				lEntitywiseTotalObli.put(lObliBean.getTxnEntity(), lDatewiseTotals);
    			}
    			lDatewiseTotals = lEntitywiseTotalObli.get(lObliBean.getTxnEntity());
    			if(!lDatewiseTotals.containsKey(lObliDateStr)){
    				lTmpTotal = BigDecimal.ZERO;
    				lDatewiseTotals.put(lObliDateStr, lTmpTotal );
    			}
    			lTmpTotal = lDatewiseTotals.get(lObliDateStr);
    			lTmpTotal = lTmpTotal.add(lObliBean.getAmount());
    			lDatewiseTotals.put(lObliDateStr, lTmpTotal);
    			lEntitywiseTotalObli.put(lObliBean.getTxnEntity(), lDatewiseTotals);
    			//
    			if(!lEntitywiseObliList.containsKey(lObliBean.getTxnEntity())){
    				lTmpList = new ArrayList<ObliFUInstDetailBean>();
        			lEntitywiseObliList.put(lObliBean.getTxnEntity(), lTmpList);
    			}
    			lTmpList = lEntitywiseObliList.get(lObliBean.getTxnEntity());
    			lTmpList.add(lObliFUInstBean);
    			//
    		}    		
    		//looping through hash and sending mails and creating attachment file entitywise
    		AppEntityBean lAppEntityBean = null;
    		for(String lEntityCode : lEntitywiseObliList.keySet()){
    			lTmpList = lEntitywiseObliList.get(lEntityCode);
    			lDatewiseTotals = lEntitywiseTotalObli.get(lEntityCode);
				//
				lMailData = new HashMap<String, Object>();
				//the to and cc
		    	List<String> lEmailIds = OtherResourceCache.getInstance().getEmailIdsFromNotificationSettings(pConnection, lEntityCode, AppConstants.EMAIL_NOTIFY_TYPE_FUTUREDATEOBLIGATIONDUE_1);
		    	List<String> lCCEmailIds = new ArrayList<String>();
				lMailData.put(EmailSender.TO, lEmailIds);
				lCCEmailIds.add(lTredsHelper.getAdminUserEmail(AppConstants.DOMAIN_PLATFORM));
				//
				//send to Purchasers Default Bank.
				lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lEntityCode);
				if(lAppEntityBean.isPurchaser()){
			        CompanyBankDetailBean lCBDBean = new CompanyBankDetailBean();
		            lCBDBean.setCdId(lAppEntityBean.getCdId());
		            lCBDBean.setDefaultAccount(Yes.Yes);
		            lCBDBean = companyBankDetailDAO.findBean(pConnection, lCBDBean);
		            if(lCBDBean!=null && lCBDBean.getId()!=null && CommonUtilities.hasValue(lCBDBean.getEmail())){
		            	lCCEmailIds.add(lCBDBean.getEmail());
		            }else{
		            	logger.info("Designated Banks email not found for "+ lEntityCode +".");
		            }
				}
				if(!lCCEmailIds.isEmpty()){
					lMailData.put(EmailSender.CC, lCCEmailIds);
				}
				//
				//the data
				lMailData.put("entityCode", lEntityCode);
				
				lDatewiseTotals = lEntitywiseTotalObli.get(lEntityCode);
				
				if(lDatewiseTotals!=null){
					int lPtr=1;
					for(Date lDate : lSettlementDates){
						lTmpTotal = lDatewiseTotals.get(FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lDate));
						lMailData.put("debitDate"+lPtr,(lDate!=null?lDate:""));
						lMailData.put("amountDue"+lPtr,TredsHelper.getInstance().getFormattedAmount((lTmpTotal!=null?lTmpTotal:BigDecimal.ZERO),true));
						lPtr++;
					}
				}
				
				//the attachment
				List<MimeBodyPart> lListAttach = new ArrayList<MimeBodyPart>();
				//change the function parameter to ANId
				lListAttach.add(getLeg2ObliNext5DaysDetails(lEntityCode, lSettlementDate, lTmpList));
				lMailData.put(EmailSender.ATTACHMENTS,lListAttach);
				//
				EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_OBLILEG2DUENEXT5DAYS, lMailData);
                lMailCounter++;
    		}
        	if(lMailCounter == 0){
        		logger.info("Leg2 trans for Next 5 days - status : No Mails sent.");
        	}else{
    			EndOfDayBO.appendMessage(lMessages, "Leg2 trans for Next 5 days - status", lMailCounter + " mails send indicating Leg2 transaction status for Settlement date starting " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
        	}
    	}else{
    		//no obligations found for the same
			EndOfDayBO.appendMessage(lMessages, "Leg2 trans for next 5 days - status", lMailCounter + " No Obligations found for Settlement date starting " + FormatHelper.getDisplay(AppConstants.DATE_FORMAT, lSettlementDate) );
    	}
    	//
		return lMessages;
    }
    
    private MimeBodyPart getLeg2ObliNext5DaysDetails(String pEntityName, Date pSettlementDate,List<ObliFUInstDetailBean> pData){
    	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
    	StringBuilder lData = new StringBuilder();
    	ObliFUInstDetailBean lBean = null;
    	ObligationBean lObliBean = null;
    	FactoringUnitBean lFUBean = null;
    	InstrumentBean lInstBean = null;
    	String lHeader = null;
    	Date lPrevDate = null;
    	BigDecimal lTotalAmt = BigDecimal.ZERO;
    	try 
    	{
    		if(pData!=null){
    			lBean = pData.get(0);
    			lObliBean = lBean.getObligationBean();
    			lFUBean = lBean.getFactoringUnitBean();
    			lInstBean = lBean.getInstrumentBean();
    			//
    			lHeader = "Factoring Unit id, Invoice no, Invoice Amount, Deductions, TDS, Factoring Unit value, Financier Name, Buyer Name, Seller Name,  Debit amount , Debit date, Interest Amount, Charges";

    			lData.append(lHeader);
    			//
    			if(pData.size() > 0){
        			for(ObliFUInstDetailBean lDataBean : pData){
            			lObliBean = lDataBean.getObligationBean();
            			lFUBean = lDataBean.getFactoringUnitBean();
            			lInstBean = lDataBean.getInstrumentBean();
            			//
            			if(!lObliBean.getDate().equals(lPrevDate)){
            				//if the previous date is present that means its not the first line hence show the totals
                			if(lPrevDate!=null){
                				//put the totals
                				lData.append(NEWLINE);
                				for(int lPtr=0; lPtr < (13-1); lPtr++){
                					if(lPtr==8){
                						lData.append("Amount due on ").append(TredsHelper.getInstance().getFormattedDate(lPrevDate));
                					}
                					else if(lPtr==9){
                						lData.append(lTotalAmt);
                					}
            						lData.append(CommonConstants.COMMA);
                				}
                			}
                			//reset total for the next
            				lTotalAmt = BigDecimal.ZERO;
            			}
            			//
            			if (lData.length() > 0) lData.append(NEWLINE);
            			 //Factoring Unit id
            			lData.append(lObliBean.getFuId()).append(CommonConstants.COMMA);
            			//Invoice no
            			lData.append(lInstBean.getInstNumber()).append(CommonConstants.COMMA);
            			//Invoice Amount
            			lData.append(lInstBean.getAmount()).append(CommonConstants.COMMA);
            			//Deductions
            			lData.append("").append(CommonConstants.COMMA);
            			//TDS
            			lData.append(lInstBean.getTdsAmount()).append(CommonConstants.COMMA);
            			//Factoring Unit value
            			lData.append(lFUBean.getFactoredAmount()).append(CommonConstants.COMMA);
            			//Financier Name
            			lData.append(lFUBean.getFinName()).append(CommonConstants.COMMA);
            			//Buyer Name
            			lData.append(lFUBean.getPurName()).append(CommonConstants.COMMA);
            			//Seller Name
            			lData.append(lFUBean.getSupName()).append(CommonConstants.COMMA);
            			//Credit amount 
            			lData.append(lObliBean.getAmount()).append(CommonConstants.COMMA);
            			lTotalAmt = lTotalAmt.add(lObliBean.getAmount());
           			 	//Due date 
            			lData.append(lObliBean.getDate()).append(CommonConstants.COMMA);
            			//Interest Amount
        				lData.append(lFUBean.getTotalPurchaserInterest()).append(CommonConstants.COMMA);
            			//Charges
        				if(CostBearer.Buyer.equals(lFUBean.getChargeBearer())){
        					lData.append(lFUBean.getTransactionCharge(lFUBean.getPurchaser()));
        				}
            			lData.append(CommonConstants.COMMA);
            			//            			
            			//set the previous date - used for writing the totals
            			lPrevDate= lObliBean.getDate();
        			}
        			//
    				//put the totals
    				lData.append(NEWLINE);
    				for(int lPtr=0; lPtr < (13-1); lPtr++){
    					if(lPtr==8){
    						lData.append("Amount due on ").append(TredsHelper.getInstance().getFormattedDate(lPrevDate));
    					}
    					else if(lPtr==9){
    						lData.append(lTotalAmt);
    					}
						lData.append(CommonConstants.COMMA);
    				}
    			}
    		}
    		lByteArrayOutputStream.write(lData.toString().getBytes());
    	    lByteArrayOutputStream.close();
    	    //
    	    MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lByteArrayOutputStream.toByteArray(), "application/csv")));
			lMimeBodyPart.setFileName("Obli5d_" + pEntityName +"_"+FormatHelper.getDisplay("yyyyMMdd", pSettlementDate) +".csv");
			return lMimeBodyPart;
    	} 
    	catch (Exception ex) 
    	{
    		ex.printStackTrace();
    	}
    	return null;
    }

	public void sendEmailslUnAcccepteBids(Connection pConnection, InstrumentBean lInstrumentBean,
			FactoringUnitBean pFactoringUnitBean,List<BidBean> pBids, AppUserBean pUserBean) {
		Map<String, Object> lDataValues = factoringUnitDAO.getBeanMeta().formatAsMap(pFactoringUnitBean);
        lDataValues.put("inId", lInstrumentBean.getId());
        lDataValues.put("instNumber", lInstrumentBean.getInstNumber());
        lDataValues.put("amount", TredsHelper.getInstance().getFormattedAmount(lDataValues.get("amount"), true));
        lDataValues.put("instCount", lInstrumentBean.getInstCount());
        // email to financier
        for(BidBean lBidBean : pBids){
	        if (Status.NotAccepted.equals(lBidBean.getStatus())){
				try {
		        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
		        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1,EntityEmail.AdminEmail, lBidBean.getFinancierAuId(), EmailSenders.CC));
		        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1,EntityEmail.UserEmail, lBidBean.getFinancierAuId(), EmailSenders.TO));
		        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BIDACCEPTFIN_1,EntityEmail.Explicit, lBidBean.getFinancierAuId(), EmailSenders.CC));
		        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
		            if (!lEmailIds.isEmpty()) {
		            	Map<String, Object> lDataValuesMail = new HashMap<String,Object>(lDataValues);
		            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValuesMail);
		            	logger.info(lDataValuesMail.toString());
		                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_UNACCEPTBIDSFINANCIER, lDataValuesMail);
		            }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        }
        }
	}
    
	public void sendEmailBestBidChanged(Connection pConnection,BidBean pBidBean) {
		Map<String, Object> lDataValues = new HashMap<String, Object>();
        lDataValues.put("id", pBidBean.getFuId());
        lDataValues.put("bidRate", pBidBean.getRate());
        
        // email to financier
			try {
	        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
	        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.AdminEmail, pBidBean.getFinancierAuId(), EmailSenders.CC));
	        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.UserEmail, pBidBean.getFinancierAuId(), EmailSenders.TO));
	        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.Explicit, pBidBean.getFinancierAuId(), EmailSenders.CC));
	        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
	            if (!lEmailIds.isEmpty()) {
	            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
	                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_BESTBIDCHANGEDFINANCIER, lDataValues);
	            }
			} catch (Exception e1) {
				e1.printStackTrace();
			}
        }

	public void sendEmailFuActivationInformToMappedFinancier(Connection pConnection,List<String> pFinEntities,FactoringUnitBean pFactoringUnitBean,InstrumentBean pInstrumentBean) {
		Map<String, Object> lDataValues = new HashMap<String, Object>();
        // email to financier
			try {
		        lDataValues.put("fuId", pFactoringUnitBean.getId());
		        lDataValues.put("fuAmount", TredsHelper.getInstance().getFormattedAmount(pFactoringUnitBean.getAmount(), true));
		        lDataValues.put("invoiceNo", pInstrumentBean.getInstNumber());
		        lDataValues.put("instCount", pInstrumentBean.getInstCount());
		        lDataValues.put("invoiceDate", pInstrumentBean.getInstDate());
		        lDataValues.put("supName", pInstrumentBean.getSupName());
		        lDataValues.put("purName", pInstrumentBean.getPurName());
		        lDataValues.put("fuCreatedDateTime", new Timestamp(System.currentTimeMillis()));
		        lDataValues.put("sysDate", TredsHelper.getInstance().getBusinessDate().toString()); 
		        
	        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
	        	for (String lFinEntityCode :pFinEntities){
	        		lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUACTIVEMAPPEDFINANCIER_1, EntityEmail.AdminEmail, lFinEntityCode, EmailSenders.BCC));
		        	lNotificationInfos.add(new  NotificationInfo(AppConstants.EMAIL_NOTIFY_TYPE_FUACTIVEMAPPEDFINANCIER_1, EntityEmail.Explicit, lFinEntityCode, EmailSenders.BCC));
	        	}
	        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection,  lNotificationInfos); 
	            if (!lEmailIds.isEmpty()) {
	            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
	                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUACTIVATIONINFORMMAPPEDFINANCIER, lDataValues);
	            }
			} catch (Exception e1) {
				logger.info("Error while sendEmailFuActivationInformToMappedFinancier : "+ e1.getMessage());
				e1.printStackTrace();
			}
        }

	 public void fuClubbedInstChangeEmail(Connection pConnection, BigDecimal pFUAmount, Map<Long,Map<Long,String>> pFinListBidRemoved ,List<Map<Long,String>> pFinListInfo,
			 String pPurchaserEntity, String pSupplierEntity, Long pFuId ) throws Exception {
	    	Map<String, Object> lDataValues = null;
	        for(Long lKeyBdId : pFinListBidRemoved.keySet()){
	        	for (Long lKeyFuId : pFinListBidRemoved.get(lKeyBdId).keySet()){
	        	lDataValues = new HashMap<String, Object>();
	        	lDataValues.put("bdId", lKeyBdId);
	        		lDataValues.put("fuId", lKeyFuId);
	        		if(BigDecimal.ZERO.equals(pFUAmount)){
	        			lDataValues.put("fuAmt", pFUAmount);
	        		}
	        		String lEntity = pFinListBidRemoved.get(lKeyBdId).get(lKeyFuId);
	        		try {
	    	        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
	    	        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.AdminEmail, lEntity, EmailSenders.TO));
		            	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.Explicit, lEntity, EmailSenders.CC));
	    	        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
	    	            if (!lEmailIds.isEmpty()) {
	    	            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
	    	                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUCLUBBEDINSTCHANGED, lDataValues);
	    	            }
	    			} catch (Exception e1) {
	    				e1.printStackTrace();
	    			}
	        	}
	        }
	        for(Map<Long,String> lData : pFinListInfo){
	        	for (Long lKeyFuId : lData.keySet()){
		        	lDataValues = new HashMap<String, Object>();
	        		lDataValues.put("fuId", lKeyFuId);
	        		if(BigDecimal.ZERO.equals(pFUAmount)){
	        			lDataValues.put("fuAmt", pFUAmount);
	        		}
	        		String lEntity = lData.get(lKeyFuId);
	        		try {
	    	        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
	    	        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.AdminEmail, lEntity, EmailSenders.TO));
		            	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.Explicit, lEntity, EmailSenders.CC));
	    	        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
	    	            if (!lEmailIds.isEmpty()) {
	    	            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
	    	                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUCLUBBEDINSTCHANGED, lDataValues);
	    	            }
	    			} catch (Exception e1) {
	    				e1.printStackTrace();
	    			}
	        	}
	        }
	        String[] lEntities = new String[] {pPurchaserEntity,pSupplierEntity};
	        for(String lEntity : lEntities){
	        	if(StringUtils.isEmpty(lEntity)){
	        		continue;
	        	}
	        	lDataValues = new HashMap<String, Object>();
	    		lDataValues.put("fuId", pFuId);
	    		if(BigDecimal.ZERO.equals(pFUAmount)){
        			lDataValues.put("fuAmt", pFUAmount);
        		}
	    		try {
		        	List<NotificationInfo> lNotificationInfos = new ArrayList<NotificationInfo>();
		        	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.AdminEmail, lEntity, EmailSenders.TO));
	            	lNotificationInfos.add(new  NotificationInfo( AppConstants.EMAIL_NOTIFY_TYPE_BESTBIDCHANGEDFIN_1,EntityEmail.Explicit, lEntity, EmailSenders.CC));
		        	Map<String,List<String>> lEmailIds = TredsHelper.getInstance().getEmails(pConnection, lNotificationInfos); 
		            if (!lEmailIds.isEmpty()) {
		            	TredsHelper.getInstance().setEmailsToData(lEmailIds, lDataValues);
		                EmailSender.getInstance().addMessage(AppConstants.TEMPLATE_FUCLUBBEDINSTCHANGED, lDataValues);
		            }
				} catch (Exception e1) {
					e1.printStackTrace();
				}
	        }
	    }
	 
	 public void addMemoAndMandateAsAttachment(Connection pConnection, Object[] pTransactionDetails, Map<String, Object> pMailData 
			 		, Map<String, CompanyBankDetailBean> pCompanyBankMap, Map<String,BankBranchDetailBean> pBankBranchMap
			 			, Map<String, FacilitatorEntityMappingBean> pFacilitatorMap) throws Exception{
			ObligationBean lFinObligationBean = (ObligationBean) pTransactionDetails[FINOBLIGATIONBEAN];
			ObligationBean lPurObligationBean = (ObligationBean) pTransactionDetails[PUROBLIGATIONBEAN];
			ObligationSplitsBean lFinSplit = null;
			ObligationSplitsBean lPurSplit = null;
			Map <String,Object> lData = null;
			Map<String,Object> lMemo = new HashMap<String,Object>();
			Map<String,Object> lMandate = new HashMap<String,Object>();
			List<Object> lMemoData = new ArrayList<Object>();
			CompanyBankDetailBean lFinCompanyBankDetailBean = null;
			CompanyBankDetailBean lPurCompanyBankDetailBean = null;
			BankBranchDetailBean lFinBankBranchDetailBean = null;
			BankBranchDetailBean lPurBankBranchDetailBean = null;
			FacilitatorEntityMappingBean lFinNACHBean = null;
			FacilitatorEntityMappingBean lPurNACHBean = null;
			Map<Long,ObligationSplitsBean> lFinObligationSplitMap = (Map<Long,ObligationSplitsBean>) pTransactionDetails[FINOBLIGATIONSPLITBEAN];
			Map<Long,ObligationSplitsBean> lPurObligationSplitMap = (Map<Long,ObligationSplitsBean>) pTransactionDetails[PUROBLIGATIONSPLITBEAN];
			AppEntityBean lFinAppEntityBean = (AppEntityBean) pTransactionDetails[FINAPPENTITYBEAN];
			AppEntityBean lPurAppEntityBean = (AppEntityBean) pTransactionDetails[PURAPPENTITYBEAN];
			InstrumentBean lInstBean = (InstrumentBean) pTransactionDetails[INSTRUMENTBEAN];
			FactoringUnitBean lFactUntBean = (FactoringUnitBean) pTransactionDetails[FACTORINGUNITBEAN];
			String lFinBankKey = lFinAppEntityBean.getCode()+CommonConstants.KEY_SEPARATOR+lFinObligationBean.getSettlementCLId();
			String lPurBankKey = lPurAppEntityBean.getCode()+CommonConstants.KEY_SEPARATOR+lPurObligationBean.getSettlementCLId();
			//
			HashMap<String,String> lBankHash = TredsHelper.getInstance().getBankName();
			//
			if (!pCompanyBankMap.containsKey(lFinBankKey)){
				pCompanyBankMap.put(lFinBankKey, TredsHelper.getInstance().getSettlementBank(pConnection, lFinObligationBean.getTxnEntity(), lFinObligationBean.getSettlementCLId()));
			}
			lFinCompanyBankDetailBean = pCompanyBankMap.get(lFinBankKey);
			//
			if (!pCompanyBankMap.containsKey(lPurBankKey)){
				pCompanyBankMap.put(lPurBankKey, TredsHelper.getInstance().getSettlementBank(pConnection, lPurObligationBean.getTxnEntity(), lPurObligationBean.getSettlementCLId()));
			}
			lPurCompanyBankDetailBean = pCompanyBankMap.get(lPurBankKey);
			//
			if (!pBankBranchMap.containsKey(lFinObligationBean.getPayDetail2())){
				pBankBranchMap.put(lFinObligationBean.getPayDetail2(), TredsHelper.getInstance().getBankBranchBean(pConnection, lFinObligationBean.getPayDetail2()));
			}
			lFinBankBranchDetailBean = pBankBranchMap.get(lFinObligationBean.getPayDetail2());
			//
			if (!pBankBranchMap.containsKey(lPurObligationBean.getPayDetail2())){
				pBankBranchMap.put(lPurObligationBean.getPayDetail2(), TredsHelper.getInstance().getBankBranchBean(pConnection, lPurObligationBean.getPayDetail2()));
			}
			lPurBankBranchDetailBean = pBankBranchMap.get(lPurObligationBean.getPayDetail2());
			//
			lFinNACHBean = pFacilitatorMap.get(lFinBankKey);
			lPurNACHBean = pFacilitatorMap.get(lPurBankKey);
			for (Long lKey : lPurObligationSplitMap.keySet()){
				lPurSplit = lPurObligationSplitMap.get(lKey);
				lFinSplit = lFinObligationSplitMap.get(lKey);
				lData = new HashMap<String,Object>();
				lData.put("uniqueRefreneceNumber", "uniqueRefreneceNumber");
				lData.put("purEntityName", lPurAppEntityBean.getName());
				lData.put("payDetil1",lPurObligationBean.getPayDetail1());
				lData.put("payDetil2",lPurObligationBean.getPayDetail2());
				lData.put("accountType", lPurCompanyBankDetailBean.getAccType().toString());
				lData.put("finEntityName",lFinAppEntityBean.getName());
				lData.put("obdate",lPurSplit.getSettledDate());
				lData.put("amount", lPurSplit.getAmount());
				lData.put("fuid",lFactUntBean.getId());
				lData.put("obdate",lPurSplit.getSettledDate());
				lData.put("reasonCode", lPurSplit.getRespRemarks());
				lMemoData.add(lData);
			}
			lMemo.put("splits" ,lMemoData);
			lMemo.put("appPath" , TredsHelper.getInstance().getApplicationURL());
			lMemo.put("fileName" , "memo.pdf");
			lMemo.put("template",AppConstants.TEMPLATE_MEMO);
			lMandate.put("purEntityName", lPurAppEntityBean.getName());
			lMandate.put("payDetail3", lPurObligationBean.getPayDetail3());
			lMandate.put("bankName", lBankHash.get(lPurCompanyBankDetailBean.getIfsc().substring(0, 4)));
			lMandate.put("branchName", lPurBankBranchDetailBean.getBranchname());
			lMandate.put("acountNo", lPurObligationBean.getPayDetail1());
			lMandate.put("ifsc", lPurBankBranchDetailBean.getIfsc());
			lMandate.put("accountType", lPurCompanyBankDetailBean.getAccType().toString());
			lMandate.put("startDate", "");
			lMandate.put("endDate", "");
			lMandate.put("amount", lFactUntBean.getAmount());
			lMandate.put("frequency", "");
			lMandate.put("status", "");
			lMandate.put("appPath" , TredsHelper.getInstance().getApplicationURL());
			lMandate.put("fileName" , "mandate.pdf");
			lMandate.put("template",AppConstants.TEMPLATE_MANDATE);
			Object[] lPdfData = new Object[2];
			lPdfData[0] = lMemo;
			lPdfData[1] = lMandate;
			List<MimeBodyPart> lAttachList = new ArrayList<MimeBodyPart>();
			for (int i=0 ; i < 2 ; i++ ){
				Map<String,Object> lMailData = (Map<String, Object>) lPdfData[i];
				MessageBean lMessageBean = new MessageBean((String)lMailData.get("template"), lMailData);
				EmailSender lEmailSender = EmailSender.getInstance();
				Mustache lMustache = lEmailSender.getMustache(lMessageBean.getTemplate());
				if (lMustache == null)
					throw new Exception("Template not found : " + lMessageBean.getTemplate());
			    StringWriter lStringWriter = new StringWriter();
			    lMustache.execute(lStringWriter, new Object[]{lMessageBean.getDataValues(), null});
			 	ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
			 	ITextRenderer renderer = new ITextRenderer();
			 	renderer.setDocumentFromString(lStringWriter.toString());
			    renderer.layout();
			    try {
					renderer.createPDF(lByteArrayOutputStream);
					lByteArrayOutputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			 	byte[] lPdf = lByteArrayOutputStream.toByteArray();
			 	MimeBodyPart lMimeBodyPart = new MimeBodyPart();
			    String lFileType = "application/pdf";
				lMimeBodyPart.setDataHandler(new  DataHandler(new ByteArrayDataSource(lPdf, lFileType)));
				lMimeBodyPart.setFileName((String)lMailData.get("fileName"));
				if(lMimeBodyPart!=null){
	        		lAttachList.add(lMimeBodyPart);
	        	}
			}
    		pMailData.put(EmailSender.ATTACHMENTS, lAttachList);
		}

}
