package com.xlx.treds.instrument.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonSlurper;

@Singleton
@Path("/factunitsp")
public class FactoringUnitResourceSP {

	private FactoringUnitBO factoringUnitBO;
    private GenericDAO<InstrumentBean> instrumentDAO;
	private BeanMeta factoringUnitBeanMeta;
	private BeanMeta instrumentBeanMeta;
    private BeanMeta obligationBeanMeta;
    public static final Logger logger = LoggerFactory.getLogger(FactoringUnitResourceSP.class);

	public FactoringUnitResourceSP() {
		super();
		factoringUnitBO = new FactoringUnitBO();
		factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
		instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);		
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
		pRequest.getRequestDispatcher("/WEB-INF/factunitsp.jsp").forward(pRequest, pResponse);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-view")
	@Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		return factoringUnitBO.findListSP(pExecutionContext, lUserBean, false,null,null);
	}

	@POST
	@Secured(secKey = "factunitsp-view")
	@Path("/all")
	public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		return myListDownload(pExecutionContext, pRequest, pFilter, false);
	}
	
	public Object myListDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter, boolean pHistory) throws Exception {
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		Connection lConnection = pExecutionContext.getConnection();
		final StringBuilder lData = new StringBuilder();
		List<String> lFUFields = new ArrayList<String>();
		List<String> lInstFields = new ArrayList<String>();
		//
		lFUFields.add("id");
		lFUFields.add("maturityDate");
		lFUFields.add("amount"); //factored amount
		lFUFields.add("charges");
		lFUFields.add("status");
		//
		lFUFields.add("purName");
		lFUFields.add("supName");
		lInstFields.add("costBearingType");
		//
		lFUFields.add("autoAccept");
		lFUFields.add("acceptedRate");
		lFUFields.add("acceptedHaircut");
		//
		lInstFields.add("amount");
		lInstFields.add("adjAmount");
		lInstFields.add("tdsAmount");
		lInstFields.add("netAmount"); //Factoring Unit Cost
		//
		//
		List<BeanFieldMeta> lFUFieldList = factoringUnitBeanMeta.getFieldListFromNames(lFUFields);
		List<BeanFieldMeta> lInstFieldList = instrumentBeanMeta.getFieldListFromNames(lInstFields);

		// for displaying header
		if (lFUFieldList != null && lInstFieldList != null) {
			for (BeanFieldMeta lBeanFieldMeta : lFUFieldList) {
				if (lData.length() > 0)
					lData.append(",");
				lData.append(lBeanFieldMeta.getLabel());
			}
			for (BeanFieldMeta lBeanFieldMeta : lInstFieldList) {
				if (lData.length() > 0)
					lData.append(",");
				lData.append(lBeanFieldMeta.getLabel());
			}
			lData.append(", Interest");
			lData.append(", Buyer ID, Buyer PAN, Buyer Ref");
			lData.append(", Seller ID, Seller PAN, Seller Ref");
			lData.append("\n");
		}

		FactoringUnitBean lFilterFUBean = null;
		ObligationBean lFilterObligBean = null;
		Long lTab = null;
		if(pHistory) {
			lFilterFUBean = getFilterFUBean(pFilter);
			lFilterObligBean = getFilterObliBean(pFilter);
		} else {
			Map<String, Object> lFilterMap = (Map<String, Object>)new JsonSlurper().parseText(pFilter);
			lTab = Long.valueOf(((Number)lFilterMap.get("tab")).longValue());
		}
		
		
		List<FactoringUnitBean> lFactoringBidBeans = factoringUnitBO.getListSP(pExecutionContext.getConnection(), lUserBean, pHistory,lFilterFUBean, lFilterObligBean);
		InstrumentBean lInstrumentBean = null;
		TredsHelper lTredsHelper = TredsHelper.getInstance();
		for (FactoringUnitBean lFactoringUnitBean : lFactoringBidBeans) {
			if ((lTab != null) && (!lTab.equals(lFactoringUnitBean.getTab())))
				continue;
			lInstrumentBean = new InstrumentBean();
			lInstrumentBean.setFuId(lFactoringUnitBean.getId());
            lInstrumentBean = instrumentDAO.findBean(pExecutionContext.getConnection(), lInstrumentBean);
            lInstrumentBean.populateNonDatabaseFields();
            //
			final Object[] lFURow = factoringUnitBeanMeta.formatAsArray(lFactoringUnitBean, null, lFUFields, true);
			final Object[] lInstRow = instrumentBeanMeta.formatAsArray(lInstrumentBean, null, lInstFields, true);
			if (lFURow != null && lInstRow != null) {
				for (int lPtr = 0; lPtr < lFURow.length; lPtr++) {
					if (lPtr > 0)
						lData.append(",");
					if (lFURow[lPtr] != null && lFURow[lPtr] != "null")
						lData.append(TredsHelper.getInstance().getSanatisedObject(lFURow[lPtr]));
				}
				for (int lPtr = 0; lPtr < lInstRow.length; lPtr++) {
					lData.append(",");
					if (lInstRow[lPtr] != null && lInstRow[lPtr] != "null")
						lData.append(TredsHelper.getInstance().getSanatisedObject(lInstRow[lPtr]));
				}
				lData.append(",");
				lData.append(lFactoringUnitBean.getTotalCost());

				lData.append(",").append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getPurchaser()));
				lData.append(",").append(TredsHelper.getInstance().getSanatisedObject(lTredsHelper.getCompanyPAN(lFactoringUnitBean.getPurchaser())));
				lData.append(",").append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getPurchaserRef()));
				lData.append(",").append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getSupplier()));
				lData.append(",").append(lTredsHelper.getCompanyPAN(lFactoringUnitBean.getSupplier()));
				lData.append(",").append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getSupplierRef()));
				lData.append("\n");
			}
		}

		return Response.ok().entity(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(lData.toString().getBytes());
				output.flush();
			}
		}).header("Content-Disposition", "attachment; filename=\"factoringUnits.csv\"").header("Content-Type", "application/octet-stream").build();

	}
	
	
	@POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/history")
    public String listHistory(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!CommonUtilities.hasValue(pFilter)){
        	throw new CommonBusinessException("Please provide at least one filter");
        }
        FactoringUnitBean lFilterFUBean = getFilterFUBean(pFilter);
        ObligationBean lFilterObliBean = getFilterObliBean(pFilter);
		return factoringUnitBO.findListSP(pExecutionContext, lUserBean, true, lFilterFUBean, lFilterObliBean);
    }

	@POST
	@Secured(secKey = "factunitsp-view")
	@Path("/history")
	public Object listHistoryDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
        if(!CommonUtilities.hasValue(pFilter)){
        	throw new CommonBusinessException("Please provide at least one filter");
        }
		return myListDownload(pExecutionContext, pRequest, pFilter, true);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-suspend")
	@Path("/suspend")
	public String suspend(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		return updateStatus(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Suspended);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-activate")
	@Path("/activate")
	public String activate(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		return updateStatus(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Active);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-withdraw")
	@Path("/withdraw")
	public String withdraw(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		return updateStatus(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Withdrawn);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-acceptbid")
	@Path("/acceptbid")
	public String acceptBid(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pFilter) throws Exception {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		List<Object> lList = (ArrayList<Object>) lJsonSlurper.parseText(pFilter);
		if(lList!=null && lList.size() > 0){
		 	Map<String,Object> lMap =  (Map<String, Object>) lList.get(0);
			Boolean lSpecificBid = (Boolean) lMap.get("specificBidAccept");
			if(lSpecificBid){
				logger.info("/acceptbid specific bid "+((Integer)lMap.get("bdId")).toString()+" received.");
			}
		}
		return updateStatus(pExecutionContext, pRequest, pFilter, FactoringUnitBean.Status.Factored);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-update")
	@Path("/update")
	public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pMessage) throws Exception {
		AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		List<ValidationFailBean> lValidationFailBeans = factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, pMessage, FactoringUnitBean.FIELDGROUP_UPDATEEXTENSION, null);
		if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
			throw new CommonValidationException(lValidationFailBeans);
		factoringUnitBO.updateFactoringUnit(pExecutionContext, lFactoringUnitBean, lAppUserBean);
	}

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Secured(secKey = "factunitsp-update")
	@Path("/updateleg3flag")
	public void updateLeg3Settlement(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
			String pMessage) throws Exception {
		AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		List<ValidationFailBean> lValidationFailBeans = factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, pMessage, FactoringUnitBean.FIELDGROUP_UPDATELEG3FLAG, null);
		if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
			throw new CommonValidationException(lValidationFailBeans);
		factoringUnitBO.updateFactoringUnitLeg3Settlement(pExecutionContext, lFactoringUnitBean, lAppUserBean);
	}

    private String updateStatus(ExecutionContext pExecutionContext, HttpServletRequest pRequest, 
            String pFilter, FactoringUnitBean.Status pStatus) throws Exception {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
		Map<String, String> lDataHash = new HashMap<String,String>();
        if (FactoringUnitBean.Status.Active.equals(pStatus)){
        	lDataHash.put("action", "factoring unit sent to auction");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_FUSENTTOAUCTION, AppConstants.TEMPLATE_PREFIX_FUSENTTOAUCTION, lDataHash);
        }else if (FactoringUnitBean.Status.Factored.equals(pStatus)){
        	lDataHash.put("action", "factoring unit sent to auction");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_BIDACCEPTCB, AppConstants.TEMPLATE_PREFIX_BIDACCEPTCB, lDataHash);
        }else if (FactoringUnitBean.Status.Withdrawn.equals(pStatus)){
        	lDataHash.put("action", "factoring unit withdrawal");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_FUWITHDRAWAL, AppConstants.TEMPLATE_PREFIX_FUWITHDRAWAL, lDataHash);
        }else if (FactoringUnitBean.Status.Suspended.equals(pStatus)){
        	lDataHash.put("action", "factoring unit put on hold");
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_FUWITHDRAWAL, AppConstants.TEMPLATE_PREFIX_FUWITHDRAWAL, lDataHash);
        }
		List<Map<String, Object>> lJsonList = (List<Map<String, Object>>) lJsonSlurper.parseText(pFilter);
		List<FactoringUnitBean> lFilterList = new ArrayList<FactoringUnitBean>();
		for (Map<String, Object> lMap : lJsonList) {
			FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
			factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, lMap, null);
			lFilterList.add(lFactoringUnitBean);
		}
		return factoringUnitBO.updateStatus(pExecutionContext, lFilterList, lUserBean, pStatus);
	}
    
    private FactoringUnitBean getFilterFUBean(String pFilter){
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
		factoringUnitBeanMeta.validateAndParse(lFactoringUnitBean, lMap, null);
		return lFactoringUnitBean;
    }
    private ObligationBean getFilterObliBean(String pFilter){
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String, Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pFilter);
		ObligationBean lObligationBean = new ObligationBean();
		obligationBeanMeta.validateAndParse(lObligationBean, lMap, null);
		return lObligationBean;
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/deedofassignmenthtml/{fuid}")
    public void deedofassignment(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @Context HttpServletResponse pResponse, @PathParam("fuid") Long pFuId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        pRequest.setAttribute("data", TredsHelper.getInstance().getDeedOfAssignment(pFuId));
        pRequest.getRequestDispatcher("/WEB-INF/deedofassignmenthtml.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces("application/x-pdf")
    @Path("/downloaddeedofassignmenthtml/{fuid}")
    public Response downloadClickWrapFiles(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
    		@PathParam("fuid") Long pFuId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        String lUrl = TredsHelper.getInstance().getApplicationURL() + "factunitsp/deedofassignmenthtml/"+pFuId;
		renderer.setDocument(lUrl);
	    renderer.layout();
	    renderer.createPDF(lByteArrayOutputStream);
	    lByteArrayOutputStream.close();
	    final byte[] lPdf = lByteArrayOutputStream.toByteArray();
	    return Response.ok().header("content-disposition", "attachment; filename="+ "deedofassignment.pdf").entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lPdf);
               output.flush();
            }
        }).build();
    }
}