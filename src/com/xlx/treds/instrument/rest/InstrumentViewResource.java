package com.xlx.treds.instrument.rest;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.FactoredBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.GstSummaryBean;
import com.xlx.treds.instrument.bean.InstFactUnitBidFilterBean;
import com.xlx.treds.instrument.bean.InstReportBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/")
public class InstrumentViewResource {

    private InstrumentBO instrumentBO;
    private FactoringUnitBO factoringUnitBO;
    private BeanMeta bidBeanMeta;
    private GenericDAO<BidBean> bidDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    private GenericDAO<FactoringUnitBean> factoringunitDAO;
    private BeanMeta instFactUnitBidFilterBeanMeta;
    private BeanMeta instReportBeanMeta;
	private List<String> defaultListFields, lovFields;
	private BeanMeta instrumentBeanMeta;
	private BeanMeta gstSummaryBeanMeta;
	
	private static final int COMPRESSION_LEVEL = 1;
	
    public InstrumentViewResource() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta =BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        bidBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BidBean.class);
        instFactUnitBidFilterBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstFactUnitBidFilterBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","type","poDate","poNumber","purchaser","purLocation","supplier","supLocation","supRefNum","uniqueNo","instFor","description","instDate","goodsAcceptDate","purAcceptDate","instDueDate","maturityDate","currency","amount","adjAmount","taxAmount","tdsAmount","netAmount","creditNoteAmount","instImage","creditNoteImage","supporting1","supporting2","factoringPer","factoringAmount","factorStartDateTime","factorEndDateTime","autoAccept","status","statusRemarks","fuId","makerEntity","makerAuId","makerCreateDateTime","makerModifyDateTime","checkerAuId","checkerActionDateTime","counterAuId","counterActionDateTime"});
        lovFields = Arrays.asList(new String[]{"id","id"});
        factoringUnitBO = new FactoringUnitBO();
        factoringunitDAO=new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
        instReportBeanMeta=BeanMetaFactory.getInstance().getBeanMeta(InstReportBean.class);
        gstSummaryBeanMeta=BeanMetaFactory.getInstance().getBeanMeta(GstSummaryBean.class);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/instview")
    public void pageInstrumentView(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        ) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/instview.jsp").forward(pRequest, pResponse);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instview-view")
    @Path("/instview")
    public String instrumentView(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @QueryParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return instrumentBO.getInstrumentJson(pExecutionContext, pId, lUserBean);
    }

    @GET	
    @Produces(MediaType.TEXT_HTML)
    @Path("/bidlogview")
    public void pageBidLogView(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        ) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/bidlogview.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instview-bidlog")
    @Path("/bidlogview")
    public String bidLogView(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @QueryParam("fuid") Long pFactoringUnitId ,@QueryParam("financierEntity") String pFinancierEntity ) throws Exception {
    		IAppUserBean lIAppUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    		Connection lConnection = pExecutionContext.getConnection();
    		return factoringUnitBO.getBidLogJson(lConnection , pFactoringUnitId, pFinancierEntity,  lIAppUserBean);
    }
    
    @GET
    @Path("/bidlogviewdnl")
    public Object pageBidLogViewDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @QueryParam("fuid") Long pFactoringUnitId , @QueryParam("financierEntity") String pFinancierEntity ) throws Exception {
		AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<BidBean> lBidBeanList = factoringUnitBO.getBidLog(pExecutionContext.getConnection(), pFactoringUnitId, pFinancierEntity, lUserBean);
        // check if provisional images exist
        boolean lProv = false;
        for (BidBean lBidBean : lBidBeanList) {
            if (lBidBean.getProvAction() != null) {
                lProv = true;
                break;
            }
        }
        final StringBuilder lData = new StringBuilder();
		List<String> lBBFields = new ArrayList<String>();
		//
		lBBFields.add("financierLoginId");
		lBBFields.add("rate");
		lBBFields.add("haircut");
		lBBFields.add("validTill");
		lBBFields.add("bidType");
		if (lProv) {
            lBBFields.add("provAction");
	        lBBFields.add("provRate");
	        lBBFields.add("provHaircut");
	        lBBFields.add("provValidTill");
            lBBFields.add("provBidType");
            lBBFields.add("appStatus");
		}
		lBBFields.add("costLeg");
		//
		lBBFields.add("status");
		lBBFields.add("timestamp");
        lBBFields.add("statusRemarks");
		//
		List<BeanFieldMeta> lBBFieldList = bidBeanMeta.getFieldListFromNames(lBBFields);

		// for displaying header
		if (lBBFieldList != null) {
			for (BeanFieldMeta lBeanFieldMeta : lBBFieldList) {
				if (lData.length() > 0)
					lData.append(",");
				lData.append(lBeanFieldMeta.getLabel());
			}
			lData.append("\n");
		}


		for (BidBean lBidBean : lBidBeanList) {
			final Object[] lBBRow = bidBeanMeta.formatAsArray(lBidBean, null, lBBFields, true);
			if (lBBRow != null) {
				for (int lPtr = 0; lPtr < lBBRow.length; lPtr++) {
					if (lPtr > 0)
						lData.append(",");
					if (lBBRow[lPtr] != null && lBBRow[lPtr] != "null")
						lData.append(TredsHelper.getInstance().getSanatisedObject(lBBRow[lPtr]));
				}
				lData.append("\n");
			}
		}

		return Response.ok().entity(new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(lData.toString().getBytes());
				output.flush();
			}
		}).header("Content-Disposition", "attachment; filename=\""+(pFactoringUnitId!=null?pFactoringUnitId.toString():"")+"_BidLog.csv\"").header("Content-Type", "application/octet-stream").build();
	}    
    

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/fuview")
    public void pageFactoringUnitView(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        ) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/fuview.jsp").forward(pRequest, pResponse);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="instview-fu")
    @Path("/fuview")
    public String factoringUnitView(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @QueryParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return factoringUnitBO.getFactoringUnitJson(pExecutionContext.getConnection(), pId, (AppUserBean)lUserBean);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/instreport")
    public void pageInstReport(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
            pRequest.getRequestDispatcher("/WEB-INF/instreport.jsp").forward(pRequest, pResponse);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="report-instreport")
    @Path("/instreport/all")
    public String listInstReport(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pFilter) throws Exception {
    	ArrayList<Object> lRowList = null;
    	Object[] lData = getInstReport(pExecutionContext, pRequest, pFilter);
    	if(lData != null && lData.length > 1){
    		lRowList = (ArrayList<Object>) lData[1];
    	}
    	return new JsonBuilder(lRowList).toString();
	}
    
    private Object[] getInstReport(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, String pFilter) throws Exception {
    	AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FactoringUnitBean lFuBean = new FactoringUnitBean();
        FactoredBean pFactoredBean=new FactoredBean();
        InstFactUnitBidFilterBean lFilterBean = new InstFactUnitBidFilterBean();
        lFilterBean.setFactid((String) lMap.get("fact_Id"));
        lFilterBean.setFactstatus((List<FactoringUnitBean.Status>) lMap.get("fact_Status"));
        lFilterBean.setBidID((String) lMap.get("bid_Id"));
        lFilterBean.setFinancierEntity((List<String>) lMap.get("bid_FinancierEntity"));
        lFilterBean.setInstid((String) lMap.get("inst_Id"));
        lFilterBean.setSalesCategory((List<String>) lMap.get("inst_SalesCategory"));
        lFilterBean.setInststatus((List<InstrumentBean.Status>) lMap.get("inst_Status"));
        lFilterBean.setPurchaser((List<String>) lMap.get("fact_Purchaser"));
        lFilterBean.setSupplier((List<String>) lMap.get("fact_Supplier"));
        lFilterBean.setStatus((List<BidBean.Status>) lMap.get("bid_Status"));
        if (!Objects.isNull((String)lMap.get("inst_IsAggregatorCreated"))) {
        	lFilterBean.setInstIsAggregatorCreated(Yes.Yes);
        }
        lFilterBean.setInstAggregatorEntity((List<String>)lMap.get("inst_AggregatorEntity"));
        instReportBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        List<FactoredBean> lFactoredBeanList = instrumentBO.findList(pExecutionContext, lUserBean,lFilterBean);
		List<Object[]> lResults = new ArrayList<Object[]>();
		List<String[]> lFieldNames = new ArrayList<String[]>();
		String lFieldSplit = null;
		for(int lPtr=0; lPtr< lFields.size(); lPtr++){
			lFieldSplit=lFields.get(lPtr);
			String lRem = null;
			if(lFieldSplit.contains("_")) {
				lRem = lFieldSplit.split("_")[1];
			}else {
				lRem = "";
			}
			if (!StringUtils.isBlank(lRem)) {
				lRem=Character.toLowerCase(lRem.charAt(0)) + lRem.substring(1);
			}
			if(lFieldSplit.contains("_")) {
				lFieldSplit=lFieldSplit.split("_")[0];
			}
			lFieldNames.add(new String[]{lFieldSplit,lRem});
		}
        Map<String, Object> lInstJson = null;
        Map<String, Object> lFactJson = null;
        Map<String, Object> lBidJson = null;
        Map<String, Object> lGstJson = null;
        BeanMeta lBidBeanMeta = bidDAO.getBeanMeta();
        BeanMeta lInstBeanMeta = instrumentDAO.getBeanMeta();
        BeanMeta lfactoringBeanMeta = factoringunitDAO.getBeanMeta();
        List<ArrayList<Object>> lRowList = new ArrayList<ArrayList<Object>>();
        ArrayList<Object> lRowData = null;
        ArrayList<String> lHeaderData = new ArrayList<String>();
        //
        Map<String, BeanFieldMeta> lInstMap = lInstBeanMeta.getFieldMap();
        Map<String, BeanFieldMeta> lBidMap =lBidBeanMeta.getFieldMap();
        Map<String, BeanFieldMeta> lFactMap =lfactoringBeanMeta.getFieldMap();
        BeanFieldMeta lBeanFieldMeta = null;
        for(int lPtr=0; lPtr< lFieldNames.size(); lPtr++){
			lFieldSplit = lFieldNames.get(lPtr)[0].toString();
			if(lFieldSplit.matches("inst")){
				lBeanFieldMeta =  lInstMap.get(lFieldNames.get(lPtr)[1].toString());
			}else if(lFieldSplit.matches("fact")){
				lBeanFieldMeta =  lFactMap.get(lFieldNames.get(lPtr)[1].toString());
			}else if(lFieldSplit.matches("bid")){
				lBeanFieldMeta =  lBidMap.get(lFieldNames.get(lPtr)[1].toString());
			}else{
				lBeanFieldMeta = null;
			}
			if(lBeanFieldMeta!=null){
				lHeaderData.add(lBeanFieldMeta.getLabel());
			}else{
				lHeaderData.add("");
			}
			
		}
        //
        List <GstSummaryBean> lEntityGSTList = new ArrayList<GstSummaryBean>();
        for (FactoredBean lFactorBean : lFactoredBeanList) {
        	lGstJson =null;	
	    	if(lFactorBean!=null){
	    		lFactorBean.getInstrumentBean().populateNonDatabaseFields();
	    		lFuBean = lFactorBean.getFactoringUnitBean();
				//to remove all bids 
	    		if(!(FactoringUnitBean.Status.Factored.equals(lFactorBean.getFactoringUnitBean().getStatus())
	    			||FactoringUnitBean.Status.Leg_1_Failed.equals(lFactorBean.getFactoringUnitBean().getStatus()) 
	    			||FactoringUnitBean.Status.Leg_1_Settled.equals(lFactorBean.getFactoringUnitBean().getStatus()) 
	    			||FactoringUnitBean.Status.Leg_2_Failed.equals(lFactorBean.getFactoringUnitBean().getStatus()) 
	    			||FactoringUnitBean.Status.Leg_2_Settled.equals(lFactorBean.getFactoringUnitBean().getStatus())
	    			||FactoringUnitBean.Status.Leg_3_Generated.equals(lFactorBean.getFactoringUnitBean().getStatus())
	    			||FactoringUnitBean.Status.Expired.equals(lFactorBean.getFactoringUnitBean().getStatus()))){
	    			lFactorBean.getFactoringUnitBean().setBdId(null);
	    		}
	            lInstJson = lInstBeanMeta.formatAsMap(lFactorBean.getInstrumentBean(), null, null, true, true);        
	            lFactJson = lfactoringBeanMeta.formatAsMap(lFactorBean.getFactoringUnitBean(), null, null, true, true);        
	            lBidJson = lBidBeanMeta.formatAsMap(lFactorBean.getBidBean(), null, null, true, true);
	            lEntityGSTList = lFuBean.getEntityGstSummaryList();
	            if ( lEntityGSTList!=null && !lEntityGSTList.isEmpty()) {
	            	for(GstSummaryBean lGstSummaryBean : lEntityGSTList) {
	    				if (lFuBean.getChargeBearerEntityCode().equals(lGstSummaryBean.getEntity())) {
	    					lGstJson = gstSummaryBeanMeta.formatAsMap(lGstSummaryBean, null, null, true, true);
	    				}
	    			}
	            }
	            lRowData = new ArrayList<Object>(); 
	    		for(int lPtr=0; lPtr< lFieldNames.size(); lPtr++){
	    			lFieldSplit = lFieldNames.get(lPtr)[0].toString();
	    			if(lFieldSplit.matches("inst")){
	    				lRowData.add(lInstJson.get(lFieldNames.get(lPtr)[1].toString()));
	    			}else if(lFieldSplit.matches("fact")){
	    				lRowData.add(lFactJson.get(lFieldNames.get(lPtr)[1].toString()));
	    			}else if(lFieldSplit.matches("bid")){
	    				lRowData.add(lBidJson.get(lFieldNames.get(lPtr)[1].toString()));
	    			}else if(lFieldSplit.matches("gst")) {
	    				if (lGstJson!=null) {
	    					lRowData.add(lGstJson.get(lFieldNames.get(lPtr)[1].toString()));
	    				}else {
	    					GstSummaryBean lGstSummaryBean = new GstSummaryBean();
	    					lRowData.add(gstSummaryBeanMeta.formatAsMap(lGstSummaryBean, null, null, true, true).get(lFieldNames.get(lPtr)[1].toString()));
	    				}
	    			}
	    		}lRowList.add(lRowData);
	    	}
	    }    
        return new Object[] { lHeaderData, lRowList};
    }
    
    @POST
    @Secured(secKey="report-instreport")
    @Path("/instreport/all")
    public Response listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	ArrayList<Object> lRowList = null;
    	ArrayList<String> lHeaderList = null;
    	Object[] lRptData = getInstReport(pExecutionContext, pRequest, pFilter);
    	final StringBuilder lHeader = new StringBuilder();
    	final StringBuilder lData = new StringBuilder();
    	
    	if(lRptData != null && lRptData.length > 1){
    		lHeaderList = (ArrayList<String>) lRptData[0];
    		lRowList = (ArrayList<Object>) lRptData[1];
    	}
    	if(lHeaderList !=null && lHeaderList.size() > 0){
			for(String lList : lHeaderList){
				if(lHeader.length() > 0) lHeader.append(",");
				lHeader.append(lList);
			}
			lHeader.append("\n");
		}
    	lData.append(lHeader);
    	if(lRowList !=null && lRowList.size() > 0){
			for(Object lRow : lRowList){
	    		int lPtr = 0;
				for(Object lField : ((ArrayList<Object>)lRow)){
					if(lPtr>0) lData.append(",");
					if(lField != null)
						if (TredsHelper.getInstance().getSanatisedObject(lField).toString().contains(",")){
							lData.append("\"").append(TredsHelper.getInstance().getSanatisedObject(lField)).append("\"");
						}else{
							lData.append(TredsHelper.getInstance().getSanatisedObject(lField));
						}
					lPtr++;
				}
				lData.append("\n");
			}
		}
    

		//
    	  return Response.ok().entity(new StreamingOutput(){
              @Override
              public void write(OutputStream output)
                 throws IOException, WebApplicationException {
					ZipOutputStream zipout = new ZipOutputStream(output);
					zipout.setComment("Created by jsp File Browser v. ");
					zipout.setLevel(COMPRESSION_LEVEL);
					zipout.putNextEntry(new ZipEntry("InstrumentReport.csv"));
					BufferedInputStream lInputStream = new BufferedInputStream(new ByteArrayInputStream(lData.toString().getBytes()));
					byte buffer[] = new byte[0xffff];
					try{
						int b;
						while ((b = lInputStream.read(buffer)) != -1){
							zipout.write(buffer, 0, b);
						}
					}catch(Exception e){
						
					}
					lInputStream.close();
					//
					zipout.closeEntry();
					zipout.finish();
					output.flush();
                 //
                 //
              }
          }).header("Content-Disposition", "attachment; filename=InstrumentReport.zip").header("Content-Type", "application/zip").build();
      }
    
    
}