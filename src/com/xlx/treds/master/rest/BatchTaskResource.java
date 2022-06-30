package com.xlx.treds.master.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.messaging.BulkMailSenderFactory;
import com.xlx.common.messaging.IBulkMailSender;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.StatutoryReport;
import com.xlx.treds.auction.bean.DirectPaymentSettlor;
import com.xlx.treds.auction.bean.IPaymentSettlor;
import com.xlx.treds.auction.bean.NPCIPaymentSettlor;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.PaymentFileBean;
import com.xlx.treds.auction.bo.FinancierSettlementFileGenerator;
import com.xlx.treds.bill.bo.BillBO;
import com.xlx.treds.instrument.bo.EmailGeneratorBO;
import com.xlx.treds.master.bo.EndOfDayBO;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.treds.entity.bean.AppEntityPreferenceBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/batchtask")
public class BatchTaskResource {

    private EndOfDayBO eodBO;
    private GenericDAO<PaymentFileBean> paymentFileDAO;
	
    public BatchTaskResource() {
        super();
        eodBO = new EndOfDayBO();
        paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/batchtask.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/eod")
    public String performEOD(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        //List<Map<String, Object>> lList = eodBO.performeEOD(pExecutionContext, lUserBean);
        List<Map<String, Object>> lList =  eodBO.performeEODAutomated(pExecutionContext.getConnection(), lUserBean);
        return new JsonBuilder(lList).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/archive")
    public String performArchive(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<Map<String, Object>> lList = eodBO.performArchive(pExecutionContext, lUserBean);
        return new JsonBuilder(lList).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/genLeg3")
    public String generateLeg3(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        pExecutionContext.setAutoCommit(false);
        List<Map<String, Object>> lList = eodBO.generateLeg3Obligations(pExecutionContext.getConnection(), lUserBean);
    	pExecutionContext.commitAndDispose();
        return new JsonBuilder(lList).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/oblishift")
    public String obligationShift(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lHolidayDate = CommonUtilities.getDate(lData.get("value").toString(),AppConstants.DATE_FORMAT);
        if(lHolidayDate==null)
        	throw new CommonBusinessException("Holiday Date is mandatory.");
        List<Map<String, Object>> lList = null, lList1 = null, lList2 = null;
        Connection lConnection = pExecutionContext.getConnection();
        lList1 = eodBO.performObligationShift(lConnection, lUserBean, lHolidayDate);
        lList2 = eodBO.performDateRecalculation(lConnection, lUserBean, lHolidayDate);
    	lList = new ArrayList<Map<String,Object>>();
        if(lList1!=null && lList1.size() > 0){
        	lList.addAll(lList1);
        }
        if(lList2!=null && lList2.size() > 0){
        	lList.addAll(lList2);
        }
        return new JsonBuilder(lList).toString();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/sendNOAMail")
    public String sendNOAMails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lSettlementDate = CommonUtilities.getDate(lData.get("value").toString(),AppConstants.DATE_FORMAT);
        if(lSettlementDate==null)
        	throw new CommonBusinessException("Settlement Date is mandatory.");
    	//
        List<Map<String, Object>> lList = null;
    	lList = new ArrayList<Map<String,Object>>();
    	//
      	EmailGeneratorBO lEmailGeneratorBO = new EmailGeneratorBO();
    	lList = lEmailGeneratorBO.sendNoticeOfAssignmentEmails(pExecutionContext.getConnection(), lSettlementDate, AuthenticationHandler.getInstance().getLoginKey(pRequest));

        return new JsonBuilder(lList).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/sendBatchMail")
    public String sendBatchMails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lBusinessDate = CommonUtilities.getDate(lData.get("businessDate").toString(),"dd-MMM-yyyy");
        String lTemplate = CommonUtilities.getString(lData.get("template").toString(), true);
        if(lBusinessDate==null)
        	throw new CommonBusinessException("Business Date is mandatory.");
    	//
        List<Map<String, Object>> lList = new ArrayList<Map<String,Object>>();
    	//
    	IBulkMailSender lBulkMailSender = BulkMailSenderFactory.getInstance().getMailSender(lTemplate);
    	if(lBulkMailSender.sendMail(lData)){
			//EndOfDayBO.appendMessage(lMessages, "SendNOAMail", lMailCounter + " mails send for Settlement date " + FormatHelper.getDisplay("dd-MM-yyyy", pSettlementDate) );
    	}else{
    		
    	}
        return new JsonBuilder(lList).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/bulkMailTemplates")
    @Secured
    public String listBulkMailTemplates(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        return lov(pExecutionContext, pRequest);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/generateNOA")
    public String generateNOA(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Long lPFId = null;
        if(lData.get("pfId")!=null)
        	lPFId = new Long(lData.get("pfId").toString());
        if(lPFId==null)
        	throw new CommonBusinessException("Payment File Id is mandatory.");
    	//
        Connection lConnection = pExecutionContext.getConnection();
        PaymentFileBean lFilterBean = new PaymentFileBean();
        lFilterBean.setId(lPFId);
        PaymentFileBean lPaymentFileBean = paymentFileDAO.findByPrimaryKey(lConnection, lFilterBean);
        if (lPaymentFileBean == null)
            throw new CommonBusinessException("Invalid File");
        IPaymentSettlor lPaymentSettlor = null;
        if(AppConstants.FACILITATOR_DIRECT.equals(lPaymentFileBean.getFacilitator())){
        	lPaymentSettlor = new DirectPaymentSettlor(lPaymentFileBean);
        }else{
        	lPaymentSettlor = new NPCIPaymentSettlor(lPaymentFileBean);
        }
        lPaymentSettlor.generateNoticeOfAssignment(lConnection, lPFId, lPaymentFileBean.getDate());

    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	EndOfDayBO.appendMessage(lMessages, "Generate NOA", "Work Done" );
        return new JsonBuilder(lMessages).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/generateFinLegFiles")
    public String generateFinanciersLegFiles(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lBusinessDate = CommonUtilities.getDate(lData.get("businessDate").toString(),"dd-MMM-yyyy");
        String lFinancier = null, lLegCode = null;
        ObligationBean.Type lLeg;
        boolean lUseSettlementDate = false;
        if(lData.get("financier") != null) lFinancier = lData.get("financier").toString();
        if(lData.get("leg") != null) {
        	lLegCode = lData.get("leg").toString();
        }
    	if(ObligationBean.Type.Leg_1.getCode().equals(lLegCode)) 
    		lLeg = ObligationBean.Type.Leg_1;
    	else {
    		lLeg = ObligationBean.Type.Leg_2;
    		lUseSettlementDate=true;
    	}
        if(lBusinessDate==null)
        	throw new CommonBusinessException("Business Date is mandatory.");
    	//
    	FinancierSettlementFileGenerator lFinSettleFileGenerator = new FinancierSettlementFileGenerator();
    	int[] lFinFilesCount = new int[] {0, 0 };
    	String lResult = "Success";
    	try {
			lFinFilesCount = lFinSettleFileGenerator.generateLegFile(pExecutionContext.getConnection(), lBusinessDate, lLeg, lFinancier, lUseSettlementDate);
		} catch (Exception e) {
			e.printStackTrace();
			lResult = "Failed";
		}
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	EndOfDayBO.appendMessage(lMessages, "Generate Leg 2 Files for Financiers.", "Result : " + lResult + ". Total " + lFinFilesCount[1] + " files generated for " + lFinFilesCount[0] + " financiers.");
        return new JsonBuilder(lMessages).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/generateFinMISFiles")
    public String generateFinancierMISFiles(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        String lStrDate = lData.get("startObligationDate").toString();
        java.util.Date lStartObligationDate = StringUtils.isBlank(lStrDate)?null:BeanMetaFactory.getInstance().getDateFormatter().parse(lStrDate);
        lStrDate = lData.get("endObligationDate").toString();
        java.util.Date lEndObligationDate = StringUtils.isBlank(lStrDate)?null:BeanMetaFactory.getInstance().getDateFormatter().parse(lStrDate); 
        if(lStartObligationDate==null && lEndObligationDate == null)
        	throw new CommonBusinessException("One of the Obligation Range Date (Start or End) is mandatory.");
    	//
        String lFinancier = null;
        if(lData.get("financier") != null) lFinancier = lData.get("financier").toString();
        //
        FinancierSettlementFileGenerator lFinSettleFileGenerator = new FinancierSettlementFileGenerator();
    	int[] lFinFilesCount = new int[] {0, 0 };
    	String lResult = "Success";
    	try {
			lFinFilesCount = lFinSettleFileGenerator.generateMISReportFile(pExecutionContext.getConnection(), lStartObligationDate, lEndObligationDate, lFinancier);
		} catch (Exception e) {
			e.printStackTrace();
			lResult = "Failed";
		}
    	List<Map<String, Object>> lMessages = new ArrayList<Map<String,Object>>();
    	EndOfDayBO.appendMessage(lMessages, "Generate MIS Files for Financiers.", "Result : " + lResult + ". Total " + lFinFilesCount[1] + " files generated for " + lFinFilesCount[0] + " financiers.");
        return new JsonBuilder(lMessages).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="bill-save")
    @Path("/generateBills")
    public String generateBills(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lBillingYearMonth = null;
        AppEntityPreferenceBean.BillType lType = AppEntityPreferenceBean.BillType.Monthly;
        if (lData.get("billtype").equals(AppEntityPreferenceBean.BillType.Daily.getCode())) {
        	lBillingYearMonth = CommonUtilities.getDate(lData.get("billYearMonth").toString(),"dd-MM-yyyy");
        	lType = AppEntityPreferenceBean.BillType.Daily;
        	if(lBillingYearMonth==null)
            	throw new CommonBusinessException("Billing Date is mandatory.");
        }else {
        	lBillingYearMonth = CommonUtilities.getDate(lData.get("billYearMonth").toString(),"yyyy-MM");
        	if(lBillingYearMonth==null)
            	throw new CommonBusinessException("Billing Year and Month is mandatory.");
        }
    	//
        List<Map<String, Object>> lList = null;
    	lList = new ArrayList<Map<String,Object>>();
    	//
    	BillBO lBillBO = new BillBO();
    	lList = lBillBO.generateBill(pExecutionContext, lBillingYearMonth,lType ,lUserBean);
        return new JsonBuilder(lList).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/transferFiles")
    public String transferFiles(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
    	//
        List<Map<String, Object>> lList = new ArrayList<Map<String,Object>>();
    	//
       // EndOfDayBO lEndOfDayBO = new EndOfDayBO();
        //lEndOfDayBO.transferFiles(pExecutionContext, lUserBean);
        return new JsonBuilder(lList).toString();
    }

    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/sendObliDue")
    public String sendObliDue(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lSettlementDate = CommonUtilities.getDate(lData.get("value").toString(),AppConstants.DATE_FORMAT);
        if(lSettlementDate==null)
        	throw new CommonBusinessException("Business Date is mandatory.");
    	//
        List<Map<String, Object>> lList = null;
    	lList = new ArrayList<Map<String,Object>>();
    	//
      	EmailGeneratorBO lEmailGeneratorBO = new EmailGeneratorBO();
    	lList = lEmailGeneratorBO.sendObligationsDueDetails(pExecutionContext.getConnection(), null, lSettlementDate);

        return new JsonBuilder(lList).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/sendL1TransDetails")
    public String sendL1TransDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lSettlementDate = CommonUtilities.getDate(lData.get("value").toString(),AppConstants.DATE_FORMAT);
        if(lSettlementDate==null)
        	throw new CommonBusinessException("Business Date is mandatory.");
    	//
        List<Map<String, Object>> lList = null;
    	lList = new ArrayList<Map<String,Object>>();
    	//
      	EmailGeneratorBO lEmailGeneratorBO = new EmailGeneratorBO();
    	lList = lEmailGeneratorBO.sendLeg1StatusTransactionDetails(pExecutionContext.getConnection(), lSettlementDate, null);
        return new JsonBuilder(lList).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="admin-eod")
    @Path("/sendL2Next5DayObliMail")
    public String sendL2Next5DayObliMail(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		,String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        pFilter = pFilter.replace("'", "\"");
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        Date lSettlementDate = CommonUtilities.getDate(lData.get("value").toString(),AppConstants.DATE_FORMAT);
        if(lSettlementDate==null)
        	throw new CommonBusinessException("Business Date is mandatory.");
    	//
        List<Map<String, Object>> lList = null;
    	lList = new ArrayList<Map<String,Object>>();
    	//
      	EmailGeneratorBO lEmailGeneratorBO = new EmailGeneratorBO();
    	lList = lEmailGeneratorBO.sendLeg2ObliNext5DaysDetails(pExecutionContext.getConnection(), lSettlementDate);

        return new JsonBuilder(lList).toString();
    }

    private String lov(ExecutionContext pExecutionContext, HttpServletRequest pRequest) throws Exception {
    	  //IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
          List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
          HashMap<String, String> lTemplates = BulkMailSenderFactory.getInstance().getTemplates();
          for (String lTemplate : lTemplates.keySet()) {
              Map<String, Object> lData = new HashMap<String, Object>();
              lData.put(BeanFieldMeta.JSONKEY_VALUE, lTemplate); // template name
              lData.put(BeanFieldMeta.JSONKEY_TEXT, lTemplates.get(lTemplate)); //template description
              //lData.put(BeanFieldMeta.JSONKEY_DESC, lAppUserBean.getLoginId());
              lResults.add(lData);
          }
          return new JsonBuilder(lResults).toString();    
    }
    
    @POST
    @Path("/rbiReport")
    public Response getFile(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,String pFilter) throws Exception {
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
    	String lMonthAndYear = (String) lData.get("monthAndYear");
        final byte[] lContent = StatutoryReport.getInstance().getExcelData(lMonthAndYear);
        String lContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lContent);
               output.flush();
            }
        };
        return Response.ok(lStreamingOutput, lContentType).header("content-disposition", "attachment; filename = "+"RBIReport_"+lMonthAndYear).build();
    }
    
    @POST
    @Path("/sendpursupnoti")
    public String sendPurSupNotification(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, String pMessage) throws Exception {
    	String lResponse = null;
    	try{
    		AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        Connection lConnection = pExecutionContext.getConnection();
	        JsonSlurper lJsonSlurper = new JsonSlurper();
	        Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
	        Date lNotificationDate = CommonUtilities.getDate(lMap.get("purSupBankNotifyDate").toString(),AppConstants.DATE_FORMAT);
	        EndOfDayBO lEndOfDayBO = new EndOfDayBO();
	        lEndOfDayBO.notifyPSBanker(lConnection,lNotificationDate);
    	}catch(Exception e){
    		lResponse = e.getMessage();
    		throw e;
    	}
    	return lResponse;
    }
        
}