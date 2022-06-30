package com.xlx.treds.instrument.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.BidBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.FactoringUnitBidBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.instrument.bo.InstrumentBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/factunitfin")
public class FactoringUnitResourceFin {

    private FactoringUnitBO factoringUnitBO;
    private InstrumentBO instrumentBO;
    private BeanMeta factoringUnitBeanMeta;
    private BeanMeta obligationBeanMeta;
    private BeanMeta bidBeanMeta;
    private List<String> defaultListFields;
    
    public FactoringUnitResourceFin() {
        super();
        instrumentBO = new InstrumentBO();
        factoringUnitBO = new FactoringUnitBO();
        factoringUnitBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FactoringUnitBean.class);
        bidBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(BidBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","maturityDate","purchaser","supplier","amount","tenure","acceptedHaircut","capRate","status","filterSellerCategory","filterMsmeStatus"});
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/factunitfin.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FactoringUnitBean lFilterBean = new FactoringUnitBean();
        factoringUnitBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        boolean lShowAll = lMap.containsKey("showall");
        List<FactoringUnitBean> lFactoringUnitList = factoringUnitBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean, lShowAll);

        List<Object> lResults = new ArrayList<Object>();
        for (FactoringUnitBean lFactoringUnitBean : lFactoringUnitList) {
            if (lFields == null)
                lResults.add(factoringUnitBeanMeta.formatAsMap(lFactoringUnitBean, null, defaultListFields, false));
            else
                lResults.add(factoringUnitBeanMeta.formatAsArray(lFactoringUnitBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey="factunitfin-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FactoringUnitBean lFilterBean = new FactoringUnitBean();
        factoringUnitBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        boolean lShowAll = lMap.containsKey("showall");
        List<FactoringUnitBean> lFactoringUnitList = factoringUnitBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean, lShowAll);
        final StringBuilder lData = new StringBuilder();
    	List<BeanFieldMeta> lFieldList = factoringUnitBeanMeta.getFieldListFromNames(lFields);
    	//for displaying header
    	if(lFieldList!=null){
    		for(BeanFieldMeta lBeanFieldMeta : lFieldList){
    			if(lData.length() > 0) lData.append(",");
    			lData.append(lBeanFieldMeta.getLabel());
    		}
			lData.append(", Buyer ID, Buyer PAN");
			lData.append(", Seller ID, Seller PAN");
            lData.append("\n");
    	}
    	//display data rowwise
		TredsHelper lTredsHelper = TredsHelper.getInstance();
    	for (FactoringUnitBean lFactoringUnitBean : lFactoringUnitList) {
            //if (lFields == null)
                //lResults.add(factoringUnitBeanMeta.formatAsMap(lFactoringUnitBean, null, defaultListFields, false));
            //else
        	 final Object[] lRow = factoringUnitBeanMeta.formatAsArray(lFactoringUnitBean, null, lFields, true);

            if(lRow != null)
            {
            	for(int lPtr=0; lPtr < lRow.length; lPtr++){
            		if(lPtr>1) lData.append(",");
            		if(lRow[lPtr]!=null&&lRow[lPtr]!="null")
            			lData.append(TredsHelper.getInstance().getSanatisedObject(lRow[lPtr]));
            	}
				lData.append(",").append(lFactoringUnitBean.getPurchaser());
				lData.append(",").append(lTredsHelper.getCompanyPAN(lFactoringUnitBean.getPurchaser()));
				lData.append(",").append(lFactoringUnitBean.getSupplier());
				lData.append(",").append(lTredsHelper.getCompanyPAN(lFactoringUnitBean.getSupplier()));
                lData.append("\n");
            }
        }
        
    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"factoringUnits.csv\"").header("Content-Type", "application/octet-stream").build();
    }
    

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/finReport")
    public Object financierReport(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        if(!CommonUtilities.hasValue(pFilter)) pFilter = "{}";
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterBean = new ObligationBean();
        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<Map<String, Object>> lJsonData = factoringUnitBO.getFinancierObligationJson(pExecutionContext.getConnection(), lFilterBean , lUserBean);
        
        return new JsonBuilder(lJsonData).toString();
    }
    
    @POST
    @Secured(secKey="factunitfin-view")
    @Path("/finReport")
    public Object financierReportDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pFilter) throws Exception {
        final StringBuilder lData  = new StringBuilder();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterBean = new ObligationBean();
        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

		String[] lColumns = {"ObliId", "FuId", "Financier", "BidId", "MakerAuId", "MakerLoginId", "CheckerAuId", "CheckerLoginId", "Purchaser", "Supplier", "DueDate", "PurchaserRef", "SupplierRef" };
        List<Map<String, Object>> lJsonData = factoringUnitBO.getFinancierObligationJson(pExecutionContext.getConnection(), lFilterBean , lUserBean);
        Map<String, Object> lObliData = null;
        for(int lPtr=0; lPtr < lColumns.length; lPtr++){
        	if(lPtr > 0) lData.append(",");
        	lData.append(lColumns[lPtr]);
        }
    	lData.append("\n");
        for(int lPtr=0; lPtr < lJsonData.size(); lPtr++){
        	lObliData = lJsonData.get(lPtr); 
        	for(int lPtr2=0; lPtr2 < lColumns.length; lPtr2++){
            	if(lPtr2 > 0) lData.append(",");
        		lData.append(TredsHelper.getInstance().getSanatisedObject(lObliData.get(lColumns[lPtr2])));
        	}
        	lData.append("\n");
        }
    	
    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"finObliReport.csv\"").header("Content-Type", "application/octet-stream").build();
    }
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/history")
    public String listHistory(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!CommonUtilities.hasValue(pFilter)){
        	throw new CommonBusinessException("Please provide at least one filter");
        }
		FactoringUnitBean lFilterFUBean = getFilterFUBean(pFilter);
		ObligationBean lFilterObliBean = getFilterObliBean(pFilter);
        return factoringUnitBO.findHistoryFin(pExecutionContext, lUserBean, lFilterFUBean, lFilterObliBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/approval")
    public String listApproval(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return factoringUnitBO.findForApproval(pExecutionContext, lUserBean);
    }
    
    @POST
    @Secured(secKey="factunitfin-view")
    @Path("/history")
    public Object listHistoryDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        if(!CommonUtilities.hasValue(pFilter)){
        	throw new CommonBusinessException("Please provide at least one filter");
        }
		FactoringUnitBean lFilterFUBean = getFilterFUBean(pFilter);
		ObligationBean lFilterObliBean = getFilterObliBean(pFilter);
        List<FactoringUnitBidBean> lFactoringUnitBidBeans = factoringUnitBO.getFactoringUnitHistory(pExecutionContext.getConnection(), lUserBean, lFilterFUBean, lFilterObliBean);
    	return myDownload(pExecutionContext, pRequest, pFilter, lFactoringUnitBidBeans);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/watch")
    public String watchList(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return factoringUnitBO.findWatchListFin(pExecutionContext, lUserBean);
    }

    @POST
    @Secured(secKey="factunitfin-view")
    @Path("/watch")
    public Object watchListDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<FactoringUnitBidBean> lFactoringUnitBidBeans = factoringUnitBO.getFactoringUnitInWatchWindow(pExecutionContext.getConnection(), lUserBean, true);
    	return myDownload(pExecutionContext, pRequest, pFilter, lFactoringUnitBidBeans);
    }

    public Object myDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pFilter, List<FactoringUnitBidBean> pFactoringUnitBidBeans) throws Exception {
        FactoringUnitBean lFactoringUnitBean = null;
        BidBean lBidBean = null;
        final StringBuilder lData = new StringBuilder();
        String lNewLine = "\r\n";
        String lComma = ",";
        String lHeader = "Id,Maturity Date,Currency,Amount,Best Rate,Buyer Name,Buyer Ref,Seller Name,Cost Bearering Type,My Rate,Haircut,Valid Till,Bid Type,Cost Leg,My Bid Status,FU Status,Accepted Rate,Interest,Userame";
        lData.append(lHeader);
        SimpleDateFormat lDateFormatter = BeanMetaFactory.getInstance().getDateFormatter();
        for(FactoringUnitBidBean lFUBidBean : pFactoringUnitBidBeans){
            lFactoringUnitBean = lFUBidBean.getFactoringUnitBean();
            lBidBean = lFUBidBean.getBidBean();
            lData.append(lNewLine);
            lData.append(lFactoringUnitBean.getId()).append(lComma);// Id
            lData.append(lDateFormatter.format(lFactoringUnitBean.getMaturityDate())).append(lComma);// Maturity Date
            lData.append(lFactoringUnitBean.getCurrency()).append(lComma);// Currency
            lData.append(lFactoringUnitBean.getAmount()).append(lComma);// Amount
            if (lFactoringUnitBean.getAcceptedRate() != null 
            		&& lFactoringUnitBean.isFactored() ){
                lData.append(lFactoringUnitBean.getAcceptedRate());
            }
            lData.append(lComma);// Best Rate
            lData.append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getPurName())).append(lComma);// Buyer Name
            lData.append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getPurchaserRef())).append(lComma);// Buyer Ref
            lData.append(TredsHelper.getInstance().getSanatisedObject(lFactoringUnitBean.getSupName())).append(lComma);// Seller Name
            lData.append(lFactoringUnitBean.getCostBearingType()).append(lComma);//TODO: SENDING COSTBEARINGTYPE INSETAD OF Cost Bearer

            if (lBidBean.getRate() != null) lData.append(lBidBean.getRate());
            lData.append(lComma);// My Rate
            if (lBidBean.getHaircut() != null) lData.append(lBidBean.getHaircut());
            lData.append(lComma);// Haircut
            if (lBidBean.getValidTill() != null) lData.append(lDateFormatter.format(lBidBean.getValidTill()));
            lData.append(lComma);// Valid Till
            if (lBidBean.getBidType() != null) lData.append(lBidBean.getBidType());
            lData.append(lComma);// Bid Type
            if (lBidBean.getCostLeg() != null) lData.append(lBidBean.getCostLeg());
            lData.append(lComma);// Cost Leg
            if (lBidBean.getStatus() != null) lData.append(lBidBean.getStatus());
            lData.append(lComma);// My Bid Status

            lData.append(lFactoringUnitBean.getStatus()).append(lComma);// FU Status
            if ((lFactoringUnitBean.getAcceptedRate() != null) && (lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Factored) 
                    && (lBidBean.getStatus() == BidBean.Status.Accepted)) 
                lData.append(lFactoringUnitBean.getAcceptedRate());
            lData.append(lComma);// Accepted Rate
            // Interest
            if ((lFactoringUnitBean.getStatus() == FactoringUnitBean.Status.Factored) && (lBidBean.getStatus() == BidBean.Status.Accepted))
                lData.append(lFactoringUnitBean.getTotalCost());
            lData.append(lComma);
            //Financier Name
            if ((lBidBean.getFinancierAuId()!=null && lBidBean.getLastAuIdUserName()!=null)){
            	lData.append(lBidBean.getLastAuIdUserName());
            }
        }        
        /*List<String> lFUFields = new ArrayList<String>();
        List<String> lBidFields = new ArrayList<String>();
    	HashMap<String, String> lFUCustomNames = new HashMap<String, String>();
    	lFUCustomNames.put("acceptedRate", "Accepted/Best Rate");
        lFUCustomNames.put("amount", "Amount");
        lFUCustomNames.put("status", "FU Status");
        HashMap<String, String> lBidCustomNames = new HashMap<String, String>();
        lBidCustomNames.put("rate", "My Rate");
        lBidCustomNames.put("status", "My Bid Status");
		//"finList": ["id","maturityDate","currency","amount","purchaser","supplier","financier","purName","supName","finName","bdId","acceptedRate","acceptedHaircut","status"]
        lFUFields.add("id");
        lFUFields.add("maturityDate");
        lFUFields.add("currency");
        lFUFields.add("amount");
        lFUFields.add("acceptedRate");
        lFUFields.add("status");
        //
        lFUFields.add("purName");
        lFUFields.add("supName");
        lFUFields.add("costBearer");
        //
        lBidFields.add("rate");
        lBidFields.add("haircut");
        lBidFields.add("validTill");
        lBidFields.add("bidType");
        lBidFields.add("costLeg");
        lBidFields.add("status");
        //
        List<BeanFieldMeta> lFUFieldList = factoringUnitBeanMeta.getFieldListFromNames(lFUFields);
    	List<BeanFieldMeta> lBidFieldList = bidBeanMeta.getFieldListFromNames(lBidFields);
        
    	//for displaying header
    	if(lFUFieldList!=null && lBidFieldList!=null){
    		for(BeanFieldMeta lBeanFieldMeta : lFUFieldList){
    			if(lData.length() > 0) lData.append(",");
        		if(lFUCustomNames.containsKey(lBeanFieldMeta.getName())){
        			lData.append(lFUCustomNames.get(lBeanFieldMeta.getName()));
        		}else{
        			lData.append(lBeanFieldMeta.getLabel());
        		}
    		}
    		for(BeanFieldMeta lBeanFieldMeta : lBidFieldList){
    			if(lData.length() > 0) lData.append(",");
        		if(lBidCustomNames.containsKey(lBeanFieldMeta.getName())){
        			lData.append(lBidCustomNames.get(lBeanFieldMeta.getName()));
        		}else{
        			lData.append(lBeanFieldMeta.getLabel());
        		}
    		}
            lData.append("\n");
    	}

        for(FactoringUnitBidBean lFUBidBean : pFactoringUnitBidBeans){
        	lFactoringUnitBean = lFUBidBean.getFactoringUnitBean();
        	lBidBean = lFUBidBean.getBidBean();
        	
        	final Object[] lFURow = factoringUnitBeanMeta.formatAsArray(lFactoringUnitBean, null, lFUFields, true);
        	final Object[] lBidRow = bidBeanMeta.formatAsArray(lBidBean, null, lBidFields, true);
            if(lFURow != null)
            {
            	for(int lPtr=0; lPtr < lFURow.length; lPtr++){
            		if(lPtr>0) lData.append(",");
            		if(lFURow[lPtr]!=null&&lFURow[lPtr]!="null")
            			lData.append(lFURow[lPtr]);
            	}
                if(lBidRow != null && lBidRow.length > 0)
                {
                	for(int lPtr=0; lPtr < lBidRow.length; lPtr++){
                		lData.append(",");
                		if(lBidRow[lPtr]!=null&&lBidRow[lPtr]!="null")
                			lData.append(lBidRow[lPtr]);
                	}
                }else{
                	for(int lPtr=0; lPtr < lBidFields.size(); lPtr++){
                		lData.append(",");
                	}
                }
                lData.append("\n");
            }
        }*/

    	return Response.ok().entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lData.toString().getBytes());
               output.flush();
            }
        }).header("Content-Disposition", "attachment; filename=\"factoringUnitBid.csv\"").header("Content-Type", "application/octet-stream").build();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/addwatch")
    public String watchAdd(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Object> lFuIds = (List<Object>)lJsonSlurper.parseText(pFilter);
        return factoringUnitBO.addToWatch(pExecutionContext, lFuIds, lUserBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-view")
    @Path("/removewatch")
    public String watchRemove(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        List<Object> lFuIds = (List<Object>)lJsonSlurper.parseText(pFilter);
        return factoringUnitBO.removeFromWatch(pExecutionContext, lFuIds, lUserBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-bid")
    @Path("/updatebid")
    public String updateBid(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
		Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        BidBean lBidBean = new BidBean();
        bidBeanMeta.validateAndParse(lBidBean, lData, null);
        List<Object> lFuIds = (List<Object>)lData.get("ids");
        lBidBean.setAppStatus(null);
        Map <String,String> lDataHash = new HashMap<String, String>();
        if (factoringUnitBO.hasCheckerForBid(lConnection, lUserBean.getId())){
        	
			lDataHash.put("action", "Bid submission");
        }else{
            lDataHash.put("action", "Bid modification");
        }
        if (lDataHash!=null)
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, lConnection, AppConstants.OTP_NOTIFY_TYPE_BIDENTRY, AppConstants.OTP_NOTIFY_TYPE_BIDENTRY, lDataHash);
    	return factoringUnitBO.updateBid(pExecutionContext, lFuIds, lBidBean, lUserBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-bid-check")
    @Path("/updatebidstatus")
    public String updateBidStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lData = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        BidBean lBidBean = new BidBean();
        bidBeanMeta.validateAndParse(lBidBean, lData, null);
        List<Object> lFuIds = (List<Object>)lData.get("ids");
        if (lBidBean.getAppStatus() == null)
            throw new CommonBusinessException("Approval Status missing");
        Map<String, String> lDataHash = new HashMap<String,String>();
        if (BidBean.AppStatus.Approved.equals(lBidBean.getAppStatus())){
        	lDataHash.put("action", "Bid approval/rejection");
        }else if (BidBean.AppStatus.Rejected.equals(lBidBean.getAppStatus())){
        	lDataHash.put("action", "Bid approval/rejection");
        }
        if (!lDataHash.isEmpty()){
        	TredsHelper.getInstance().verifyOrSendOTP(pRequest, lUserBean, pExecutionContext.getConnection(), AppConstants.OTP_NOTIFY_TYPE_BIDCHKAPPREJ, AppConstants.TEMPLATE_PREFIX_BIDCHKAPPREJ, lDataHash);        	
        }
        return factoringUnitBO.updateBid(pExecutionContext, lFuIds, lBidBean, lUserBean);
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="factunitfin-depth")
    @Path("/depth/{id}")
    public String depth(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return factoringUnitBO.depth(pExecutionContext, pId, lUserBean);
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
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/viewclubbeddetails/{id}")
    public String getdetails(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
            , @PathParam("id") Long pId) throws Exception {
    	Connection lConnection = pExecutionContext.getConnection();
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return new JsonBuilder(instrumentBO.findJsonForInstruments(lConnection, pId, lUserBean)).toString();
    }
    
    
//    @POST
//    @Secured(secKey="factunitfin-view")
//    @Path("/cersai")
//    public Object cersaiReportDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
//        String pFilter) throws Exception {
//        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//        JsonSlurper lJsonSlurper = new JsonSlurper();
//        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
//        String lFactorDateStr = (String)lMap.get("factorDate");
//        Date lFactorDate = CommonUtilities.getDate(lFactorDateStr, "DD-MM-YYYY");
//        CersaiFileGenerator lCersaiFileGenerator = new CersaiFileGenerator();
//        
//    	return  lCersaiFileGenerator.cersaiDownload(pExecutionContext.getConnection(), lUserBean.getDomain(), lFactorDate, lUserBean);
//    }

    
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Path("/calculatesplitcharge")
//    public String getFinPlatformCharge(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
//    		String pMessage) throws Exception{
//    	AppUserBean lAppUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//    	JsonSlurper lJsonSlurper = new JsonSlurper();
//    	Map<String,Object> lRtnMap = new HashMap<String, Object>();
//    	Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pMessage);
//    	FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean(); 
//    	lMap.get(lMap.get("fuId"));
//    	lFactoringUnitBean = factoringUnitDAO.findBean(pExecutionContext.getConnection(), lFactoringUnitBean);
//    	BidBean lBidBean = new BidBean(); 
//    	lMap.get(lMap.get("bidId"));
//    	lBidBean = bidDAO.findBean(pConnection, lBidBean);
//    	return factoringUnitBO.calculateFinacierChargeSharePercent(pExecutionContext.getConnection(),pMessage);
//    }
    
    
}