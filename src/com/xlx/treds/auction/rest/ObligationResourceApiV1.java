package com.xlx.treds.auction.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.ApiLogger;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.AssignmentNoticeDetailsBean;
import com.xlx.treds.auction.bean.FactoredDetailBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationModificationRequestBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bo.ObligationBO;
import com.xlx.treds.auction.bo.ObligationModificationRequestBO;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/v1/oblig")
public class ObligationResourceApiV1 {
    public static final Logger logger = Logger.getLogger(ObligationResourceApiV1.class);

    private ObligationBO obligationBO;
    private InstrumentBO instrumentBO;
    private BeanMeta obligationBeanMeta;
	private List<String> defaultListFields, lovFields;
    private BeanMeta factoringUnitBeanMeta;
    private GenericDAO<AssignmentNoticeDetailsBean> assignmentNoticeDetailsDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private ObligationModificationRequestBO obligationModificationRequestBO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    //	
    public ObligationResourceApiV1() {
        super();
        obligationBO = new ObligationBO();
        instrumentBO = new InstrumentBO();
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","fuId","txnType","date","amount","type","status","paymentRefNo","respErrorCode","respRemarks"});
        lovFields = Arrays.asList(new String[]{"id","fuId"});
        factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
        obligationModificationRequestBO = new ObligationModificationRequestBO();
        assignmentNoticeDetailsDAO = new GenericDAO<AssignmentNoticeDetailsBean>(AssignmentNoticeDetailsBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        factoringUnitDAO =  new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/oblig.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="oblig-view")
    @Path("/all")
    public Object list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterBean = new ObligationBean();
        Connection lConnection = pExecutionContext.getConnection();
        //
        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        //finding fuid from lMap and will pass it to lFilterBean
        if(lMap.get("inId") != null && StringUtils.isNotEmpty(lMap.get("inId").toString())){
            InstrumentBean lInstFilterBean = new InstrumentBean();
            lInstFilterBean.setId(new Long(lMap.get("inId").toString()));
            lInstFilterBean = instrumentDAO.findBean(lConnection, lInstFilterBean);
            if(lInstFilterBean != null && lInstFilterBean.getFuId() != null){
            	lFilterBean.setFuId(lInstFilterBean.getFuId());
            }else{
            	throw new CommonBusinessException("Obligations not found for Instrument Id : "+(lMap.get("inId")!=null?lMap.get("inId").toString():""));
            }
        }
        Map<String, Object> lTmp = null;
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        List<ObligationBean> lObligationList = obligationBO.findList(lConnection, lFilterBean, lFields, lUserBean);
        List<Object> lResults = new ArrayList<Object>();
        List<Long> lObids = new ArrayList<Long>();
        List<Long> lFuids = new ArrayList<Long>();
        for (ObligationBean lObligationBean : lObligationList) {
        	if (!lObids.contains(lObligationBean.getId())){
        		lObids.add(lObligationBean.getId());
        	}
        	if (!lFuids.contains(lObligationBean.getFuId())){
        		lFuids.add(lObligationBean.getFuId());
        	}
        }
        Map<Long,Object> lSplitMap = TredsHelper.getInstance().getObligationSplitMap(lConnection, lObids);
        Map<Long,Long> lInIdMap = TredsHelper.getInstance().getFactoringInstIdMap(lConnection, lFuids);
        for (ObligationBean lObligationBean : lObligationList) {
            if (lFields == null) {
            	lTmp = obligationBeanMeta.formatAsMap(lObligationBean, null, defaultListFields, false);
            	lTmp.put("inId", lInIdMap.get(lObligationBean.getFuId()));
            	lTmp.put("paymentRefNo", lSplitMap.get(lObligationBean.getId()));
                lResults.add(lTmp); //api
            }else {
                lResults.add(obligationBeanMeta.formatAsArray(lObligationBean, null, lFields, true));  
            }
        }
	        lResponse= new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally {
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }

    @POST
    @Secured(secKey="oblig-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterBean = new ObligationBean();

        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if(lFields==null) lFields = defaultListFields;
        List<ObligationBean> lObligationList = obligationBO.findList(lConnection, lFilterBean, lFields, lUserBean);
    	final StringBuilder lData = new StringBuilder();
    	List<BeanFieldMeta> lFieldList = obligationBeanMeta.getFieldListFromNames(lFields);
    	//for displaying header
         List<ObligationBean> lFilteredList = new ArrayList<ObligationBean>();
         for (ObligationBean lObligationBean : lObligationList) {
             	lFilteredList.add(lObligationBean);
         }
         return new FileDownloadBean("Settlements.csv", 
        		 obligationBeanMeta.formatBeansAsCsv(lFilteredList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
     }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/breakup/{fuid}/{date}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("fuid") Long pFuId, @PathParam("date") String pDate) throws Exception {
        ObligationBean lFilterBean = new ObligationBean();
        lFilterBean.setFuId(pFuId);
        java.util.Date lDate = BeanMetaFactory.getInstance().getDateFormatter().parse(pDate);
        lFilterBean.setDate(new java.sql.Date(lDate.getTime()));
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return new JsonBuilder(obligationBO.findJsonForFU(pExecutionContext, lFilterBean, lUserBean)).toString();
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured
    @Path("/breakuphtml/{fuid}/{date}")
    public void breakupHtml(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("fuid") Long pFuId, @PathParam("date") String pDate) throws Exception {
        ObligationBean lFilterBean = new ObligationBean();
        lFilterBean.setFuId(pFuId);
        java.util.Date lDate = BeanMetaFactory.getInstance().getDateFormatter().parse(pDate);
        lFilterBean.setDate(new java.sql.Date(lDate.getTime()));
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        pRequest.setAttribute("data", obligationBO.findJsonForFU(pExecutionContext, lFilterBean, lUserBean));
        pRequest.getRequestDispatcher("/WEB-INF/obligbreakuphtml.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces("application/x-pdf")
    @Secured
    @Path("/breakuppdf/{fuid}/{date}")
    public Response breakupPDF(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("fuid") Long pFuId, @PathParam("date") String pDate) throws Exception {
        ObligationBean lFilterBean = new ObligationBean();
        lFilterBean.setFuId(pFuId);
        java.util.Date lDate = BeanMetaFactory.getInstance().getDateFormatter().parse(pDate);
        lFilterBean.setDate(new java.sql.Date(lDate.getTime()));
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        String lUrl = TredsHelper.getInstance().getApplicationURL() + "oblig/breakuphtml/" + pFuId + "/" + pDate;
        lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
        renderer.setDocument(lUrl);
        renderer.layout();
        renderer.createPDF(lByteArrayOutputStream);
        lByteArrayOutputStream.close();
        String lFileName = pFuId + new SimpleDateFormat("yyyyMMdd").format(lDate) + ".pdf";
        return sendFileContents(new Object[]{lFileName, lByteArrayOutputStream.toByteArray()});
    }
    
    private Response sendFileContents(Object[] pFileDetails) throws Exception {
        String lFileName = (String) pFileDetails[0];
        final byte[] lData = (byte[]) pFileDetails[1];

        StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData);
               output.flush();
            }
        };
        String lContentType = Files.probeContentType(Paths.get(lFileName));
        return Response.ok(lStreamingOutput, lContentType).header("content-disposition", "attachment; filename = "+lFileName).build();
        
    }
    

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="oblig-view")
    @Path("/obliReport")
    public Object obligationReport(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterBean = new ObligationBean();
        Connection lConnection = pExecutionContext.getConnection();
        
        factoringUnitBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        //TODO: the below list is never used - so the findList can be commented but it is modify the lFilterBean hence not commented currently
        List<ObligationBean> lObligationList = obligationBO.findList(lConnection, lFilterBean, lFields, lUserBean);
        List<Object> lResults = new ArrayList<Object>();
        FactoringUnitBean lFilterFUBean = new FactoringUnitBean();
        List<FactoredDetailBean> lFactoredDetailBeans = obligationBO.getObligationReport(lConnection, lUserBean, lFilterFUBean, lFilterBean,null);
        for (FactoredDetailBean lFactoredDetailBean : lFactoredDetailBeans) {
            lResults.add(factoringUnitBeanMeta.formatAsArray(lFactoredDetailBean.getFactoringUnitBean(), null, lFields, true));            
    	}        
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey="oblig-view")
    @Path("/obliReport")
    public Object obligationReportDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterObliBean = new ObligationBean();
        obligationBeanMeta.validateAndParse(lFilterObliBean, lMap, null, null);

        FactoringUnitBean lFilterFUBean = new FactoringUnitBean();
        factoringUnitBeanMeta.validateAndParse(lFilterFUBean, lMap, null, null);

        lFilterFUBean.setId(lFilterObliBean.getFuId());
        try(Connection lConnection = pExecutionContext.getConnection();){
            //
            final StringBuilder lData  = obligationBO.getObligationReportFileData(lConnection, lUserBean, lFilterFUBean, lFilterObliBean,null);
            //
        	return Response.ok().entity(new StreamingOutput(){
                @Override
                public void write(OutputStream output)
                   throws IOException, WebApplicationException {
                   output.write(lData.toString().getBytes());
                   output.flush();
                }
            }).header("Content-Disposition", "attachment; filename=\"settlementreport.csv\"").header("Content-Type", "application/octet-stream").build();
        	
        }catch(Exception lEx){
			logger.info("Error in obligationReportDownload " + lEx.getMessage());
        	throw new Exception(lEx);
        }
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured
    @Path("/noahtml/{anid}")
    public void noticeOfAssignmentHtml(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("anid") Long pAnId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        pRequest.setAttribute("data", obligationBO.findJsonForNOA(pExecutionContext.getConnection(), pAnId, lUserBean));
        pRequest.getRequestDispatcher("/WEB-INF/noticeofassignmenthtml.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces("application/x-pdf")
    @Secured
    @Path("/noapdf/{anid}")
    public Response noticeOfAssignmentPDF(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("anid") Long pAnId) throws Exception {
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        String lUrl = TredsHelper.getInstance().getApplicationURL() + "oblig/noahtml/" + pAnId ;
        lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
        //the font has been kept in the classpath
        //renderer.getFontResolver().addFont("arial.ttf",true);
        renderer.setDocument(lUrl);
        renderer.layout();
        renderer.createPDF(lByteArrayOutputStream);
        lByteArrayOutputStream.close();
        String lFileName = "noa_"+pAnId+".pdf" ;
        return sendFileContents(new Object[]{lFileName, lByteArrayOutputStream.toByteArray()});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Secured
    @Path("/noahtml/fu/{fuid}")
    public void noticeOfAssignmentFuHtml(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("fuid") Long pFuId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        Connection lConnection = pExecutionContext.getConnection();
        //from fuid find the anid
        AssignmentNoticeDetailsBean lANDBean = new AssignmentNoticeDetailsBean();
        lANDBean.setFuId(pFuId);
        lANDBean = assignmentNoticeDetailsDAO.findBean(lConnection, lANDBean);
        if(lANDBean == null){
        	throw new CommonBusinessException("Assignment Notice not generated for factoring unit : " + pFuId);
        }
        //
        pRequest.setAttribute("data", obligationBO.findJsonForNOA(lConnection, lANDBean.getAnId(), lUserBean));
        pRequest.getRequestDispatcher("/WEB-INF/noticeofassignmenthtml.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces("application/x-pdf")
    @Secured
    @Path("/noapdf/fu/{fuid}")
    public Response noticeOfAssignmentFuPDF(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("fuid") Long pFuId) throws Exception {
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        String lUrl = TredsHelper.getInstance().getApplicationURL() + "oblig/noahtml/fu/" + pFuId ;
        lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
        //the font has been kept in the classpath
        //renderer.getFontResolver().addFont("arial.ttf",true);
        renderer.setDocument(lUrl);
        renderer.layout();
        renderer.createPDF(lByteArrayOutputStream);
        lByteArrayOutputStream.close();
        //from fuid find the anid
        AssignmentNoticeDetailsBean lANDBean = new AssignmentNoticeDetailsBean();
        lANDBean.setFuId(pFuId);
        lANDBean = assignmentNoticeDetailsDAO.findBean(pExecutionContext.getConnection(), lANDBean);
        Long lAnId = pFuId;
        if(lANDBean != null){
        	lAnId = lANDBean.getAnId();
        }
        //
        String lFileName = "noa_"+lAnId+".pdf" ;
        return sendFileContents(new Object[]{lFileName, lByteArrayOutputStream.toByteArray()});
    }
    
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/oblisplit/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId) throws Exception {
        ObligationSplitsBean lFilterBean = new ObligationSplitsBean();
        lFilterBean.setId(pId);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return new JsonBuilder(obligationBO.findJsonForSplitObligations(pExecutionContext, lFilterBean, lUserBean)).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/viewclubbeddetails/{id}")
    public String getdetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
    	InstrumentBean lBean = new InstrumentBean();
    	lBean.setFuId(pId);
    	lBean = instrumentDAO.findBean(lConnection, lBean);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if(CommonAppConstants.Yes.Yes.equals(lBean.getGroupFlag())){
            return new JsonBuilder(instrumentBO.findJsonForClubbedInstruments(lConnection, lBean.getId(), lUserBean)).toString();
    	}else{
            return new JsonBuilder(instrumentBO.findJsonInstrument(lConnection, lBean)).toString();
    	}
    }
    
    @GET
    @Path("/downloadClubbeddetails/{id}")
    public Object InstrumentsDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		 @PathParam("id") Long pId) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
    	InstrumentBean lBean = new InstrumentBean();
    	lBean.setFuId(pId);
    	lBean = instrumentDAO.findBean(lConnection, lBean);
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        final StringBuilder lData  = new StringBuilder();
        Map<String, Object> lMap =  null;
        
    	if(CommonAppConstants.Yes.Yes.equals(lBean.getGroupFlag())){
    		lMap = instrumentBO.findJsonForClubbedInstruments(lConnection, lBean.getId(), lUserBean);
    	}else{
    		lMap = instrumentBO.findJsonInstrument(lConnection, lBean);
    	}
    	String[] lColumnKeys1 = new String[] { "id", "fuId", "instNumber", "amt", "cashdiscountAmt", "adjAmt", "tdsAmt", "netAmt" };
    	String[] lHeaders1 = new String[] { "Instrument Id", "Factoring Unit Id",  "Invoice No.", "Invoice Amount", "Cash Discount Amount", "Adj Amount", "TDS Amount", "Net Amount" };
    	//splitsummary
    	String[] lColumnKeys2 = new String[] { "intL1Amt",  "intL2Amt", "intL2ExtAmt" };
    	String[] lHeaders2 = new String[] { "Interest L1 Amount",  "Interest L2 Amount", "Interest L2 Ext Amount"};

    	//headers
    	for(int lPtr=0; lPtr < lHeaders1.length; lPtr++){
    		if(lPtr > 0)lData.append(",");
    		lData.append(lHeaders1[lPtr]);
    	}
    	if(lMap.containsKey("splitsummary")){
    		Map<String, Object> lTmpMap = (Map<String, Object>) lMap.get("splitsummary");
        	for(int lPtr=0; lPtr < lHeaders2.length; lPtr++){
        		if(lTmpMap.containsKey(lColumnKeys2[lPtr])){
            		lData.append(",");
            		lData.append(lHeaders2[lPtr]);
        		}
        	}
    	}
		lData.append("\r\n");

    	if(lMap.containsKey("splitlist")){
    		List<Map<String, Object>> lTmpList = (List<Map<String, Object>>) lMap.get("splitlist");
    		for(int lPtr=0; lPtr < lTmpList.size(); lPtr++){
    			for(int lPtr1=0; lPtr1 < lColumnKeys1.length; lPtr1++){
            		if(lPtr1 > 0)lData.append(",");
            		if (lColumnKeys1[lPtr1].toString().equals("fuId")){
            			lData.append(TredsHelper.getInstance().getCleanData(lMap.get(lColumnKeys1[lPtr1].toString())));
            		}else{
            			lData.append(TredsHelper.getInstance().getCleanData(lTmpList.get(lPtr).get(lColumnKeys1[lPtr1].toString())));
            		}
            		
    			}
    			if(lMap.containsKey("splitsummary")){
        			for(int lPtr2=0; lPtr2 < lColumnKeys2.length; lPtr2++){
        				if(lTmpList.get(lPtr).containsKey(lColumnKeys2[lPtr2].toString())){
                    		lData.append(",");
                    		lData.append(TredsHelper.getInstance().getCleanData(lTmpList.get(lPtr).get(lColumnKeys2[lPtr2].toString())));
        				}
        			}
    			}
    			lData.append("\r\n");
    		}
    	}
    	if(lMap.containsKey("splitsummary")){
    		Map<String, Object> lTmpMap = (Map<String, Object>) lMap.get("splitsummary");
    		for(int lPtr1=0; lPtr1 < lColumnKeys1.length; lPtr1++){
        		if(lPtr1 > 0)lData.append(",");
        		lData.append(TredsHelper.getInstance().getCleanData(lTmpMap.get(lColumnKeys1[lPtr1].toString())));
			}
			if(lMap.containsKey("splitsummary")){
    			for(int lPtr2=0; lPtr2 < lColumnKeys2.length; lPtr2++){
    				if(lTmpMap.containsKey(lColumnKeys2[lPtr2].toString())){
                		lData.append(",");
                		lData.append(TredsHelper.getInstance().getCleanData(lTmpMap.get(lColumnKeys2[lPtr2].toString())));
    				}
    			}
			}
    	}
		
	
    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=obligations.csv").header("Content-Type", "application/octet-stream").build();
    }

    @POST
    @Secured(secKey="oblig-view")
    @Path("/changeSettlor")
    public void changeSettlor(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        List<String> lKeys = (List<String>) lMap.get("keyList");
        boolean lIsSplits = (boolean) lMap.get("isSplits");
        String lSettlorTypeStr = (String) lMap.get("paymentSettlor");
        String lPaymentSettlor = null;
        if(StringUtils.isNotEmpty(lSettlorTypeStr)){
        	lPaymentSettlor = lSettlorTypeStr;
        }
        obligationBO.changePaymentSettlor(lConnection, lKeys, lIsSplits, lPaymentSettlor);
    }

    @POST
    @Secured
    @Path("/checkModi")
    public boolean checkAccessForModification(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {

        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	ObligationBean.Type lType = null;
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        String pType = (String) lMap.get("type");
    	BeanFieldMeta lField = obligationBeanMeta.getFieldMap().get("type");
        Map<String, IKeyValEnumInterface> lMap1 = lField.getDataSetKeyValueReverseMap();
        if (lMap1.containsKey(lMap.get("type"))){
        	lType = (Type) lMap1.get(lMap.get("type"));
        }
        Long lPartNo = null;;
        Long lFuId = null;
        if(lMap.get("partNo")!=null){
        	lPartNo = new Long(lMap.get("partNo").toString());
        }
        if(lMap.get("fuId")!=null){
        	lFuId = new Long(lMap.get("fuId").toString());
        }
        ObligationModificationRequestBean lObligationModificationRequestBean = obligationModificationRequestBO.createBean(pExecutionContext.getConnection(), lFuId , lType, lPartNo);
        if (AccessControlHelper.getInstance().hasAccess("oblimodreq-save", lUserBean) &&
     			!(AccessControlHelper.getInstance().hasAccess("oblimodreq-approve", lUserBean)	)){
     		if (!(lObligationModificationRequestBean.getStatus()==ObligationModificationRequestBean.Status.Created)){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
     		}
    	}else if(AccessControlHelper.getInstance().hasAccess("oblimodreq-save", lUserBean) &&
     			(AccessControlHelper.getInstance().hasAccess("oblimodreq-approve", lUserBean)	)){
    		if (!((lObligationModificationRequestBean.getStatus()==ObligationModificationRequestBean.Status.Created) || 
    				(lObligationModificationRequestBean.getStatus()==ObligationModificationRequestBean.Status.Sent))){
                throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
     		}
    	}
        if (lObligationModificationRequestBean!=null){
        	return true;
        }
		return false;
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="oblig-view")
    @Path("/vendorpaydetails")
    public Object getVendorDetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	int lRequestId = ApiLogger.logApiRequestResponse(true,pRequest, pFilter, this.getClass().getName(),0);
    	String lResponse = null;
    	try {
	        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
	        JsonSlurper lJsonSlurper = new JsonSlurper();
	        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
	        ObligationBean lFilterBean = new ObligationBean();
	        Connection lConnection = pExecutionContext.getConnection();
	        InstrumentBean lInstrumentBean = new InstrumentBean();
	        //
	        if(lMap!=null) {
	        	for(String lKey : lMap.keySet()) {
	        		if(lMap.get(lKey)!=null && (StringUtils.isEmpty(lMap.get(lKey).toString()))) {
	        			lMap.put(lKey, null);
	        		}
	        	}
	        }
	        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
	        //
	        if(StringUtils.isNotEmpty((String) (lMap.get("inId")))) {
	        	lInstrumentBean.setId(Long.valueOf(lMap.get("inId").toString()));
	        	lInstrumentBean = instrumentDAO.findBean(lConnection, lInstrumentBean);
	        	if (lInstrumentBean == null) {
	        		throw new CommonBusinessException("Instrument Id - "+lInstrumentBean.getId()+" not found.");
	        	}
	        	if (lInstrumentBean.getFuId() == null) {
	        		throw new CommonBusinessException("Instrument Id - "+lInstrumentBean.getId()+" status is "+lInstrumentBean.getStatusDesc()+".");
	        	}
	        	lFilterBean.setFuId(lInstrumentBean.getFuId());
	        }
	        //
	        Map<String, Object> lTmp = null;
	        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
	        List<ObligationBean> lObligationList = obligationBO.findVendorList(lConnection, lFilterBean, lFields, lUserBean);
	        if (lFilterBean.getFuId()!=null && (lObligationList==null || lObligationList.isEmpty())) {
	        	if (lInstrumentBean.getId() != null) {
	        		throw new CommonBusinessException("Instrument Id - "+lInstrumentBean.getId()+" status is "+lInstrumentBean.getStatusDesc()+".");
	        	}
	        	FactoringUnitBean lFUBean = new FactoringUnitBean();
	        	lFUBean.setId(lFilterBean.getFuId());
	        	lFUBean = factoringUnitDAO.findBean(lConnection, lFUBean);
	        	if(lFUBean!=null) {
	        		throw new CommonBusinessException("Factoring Unit Id - "+lFilterBean.getFuId()+" status is "+lFUBean.getStatus().toString()+".");
	        	}
	        	throw new CommonBusinessException("Factoring Unit Id - "+lFilterBean.getFuId()+" no obligations found.");
	        }
	        List<Object> lResults = new ArrayList<Object>();
	        List<Long> lObids = new ArrayList<Long>();
	        for (ObligationBean lObligationBean : lObligationList) {
	        	if (!lObids.contains(lObligationBean.getId())){
	        		lObids.add(lObligationBean.getId());
	        	}
	        }
	        Map<Long,Object> lSplitMap = TredsHelper.getInstance().getObligationSplitMap(lConnection, lObids);
	        for (ObligationBean lObligationBean : lObligationList) {
	            if (lFields == null) {
	            	lTmp = obligationBeanMeta.formatAsMap(lObligationBean, null, defaultListFields, false);
	            	lInstrumentBean = new InstrumentBean();
	            	lInstrumentBean.setFuId(lObligationBean.getFuId());
	            	lInstrumentBean = instrumentDAO.findBean(lConnection, lInstrumentBean);
	            	if (lInstrumentBean!=null) {
	            		lTmp.put("inId", lInstrumentBean.getId());
	            	}
	            	lTmp.put("paymentRefNo", lSplitMap.get(lObligationBean.getId()));
	                lResults.add(lTmp); //api
	            }else {
	                lResults.add(obligationBeanMeta.formatAsArray(lObligationBean, null, lFields, true));  
	            }
	        }
	        lResponse = new JsonBuilder(lResults).toString();
    	}catch(Exception e) {
    		lResponse = e.getMessage();
    		logger.debug(e.getStackTrace());
    		throw e;
    	}finally {
    		ApiLogger.logApiRequestResponse(false,pRequest, lResponse, this.getClass().getName(),lRequestId);
    	}
        return lResponse;
    }
}