package com.xlx.treds.auction.rest;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.AccessControlHelper;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bean.ObligationModificationRequestBean;
import com.xlx.treds.auction.bo.ObligationModificationRequestBO;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/oblimodreq")
public class ObligationModificationRequestResource {

    private ObligationModificationRequestBO obligationModificationRequestBO;
    private BeanMeta obligationModificationRequestBeanMeta;
    private BeanMeta obligationBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public ObligationModificationRequestResource() {
        super();
        obligationModificationRequestBO = new ObligationModificationRequestBO();
        obligationModificationRequestBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationModificationRequestBean.class);
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","fuId","partNumber","type","date","status","createDate","createrAuId","approveRejectDate","approveRejectAuId"});
        lovFields = Arrays.asList(new String[]{"id","fuId"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId , @QueryParam("fuId") Long pfuId 
		, @QueryParam("type") String pType, @QueryParam("partNo") Long pPartNo ) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }else if ((pfuId!=null && StringUtils.isNotEmpty(pType) && pPartNo!=null) ){
        	Object[] lKey = new Object[]{pfuId,pType,pPartNo};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/oblimodreq.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        ObligationModificationRequestBean lFilterBean = new ObligationModificationRequestBean();
        lFilterBean.setId(pId);
        ObligationModificationRequestBean lObligationModificationRequestBean = obligationModificationRequestBO.findBean(pExecutionContext, lFilterBean);
        BigDecimal lAcceptedRate = TredsHelper.getInstance().getAcceptedRate(pExecutionContext.getConnection(),lObligationModificationRequestBean.getFuId());
        HashMap<String, Object> lMap = (HashMap<String, Object>) obligationModificationRequestBeanMeta.formatAsMap(lObligationModificationRequestBean, null, null, false);
        if (lAcceptedRate!=null) {
        	lMap.put("acceptedRate", lAcceptedRate);
        }
        return new JsonBuilder(lMap).toString();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{fuId}/{type}/{partNo}")
    public String getData(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("fuId") Long pFuId, @PathParam("partNo") Long pPartNo, @PathParam("type") String pType) throws Exception {

        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	ObligationBean.Type lType = null;
    	BeanFieldMeta lField = obligationBeanMeta.getFieldMap().get("type");
        Map<String, IKeyValEnumInterface> lMap = lField.getDataSetKeyValueReverseMap();
        if (lMap.containsKey(pType)){
        	lType = (Type) lMap.get(pType);
        }
        ObligationModificationRequestBean lObligationModificationRequestBean = obligationModificationRequestBO.createBean(pExecutionContext.getConnection(), pFuId, lType, pPartNo);
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
        BigDecimal lAcceptedRate = TredsHelper.getInstance().getAcceptedRate(pExecutionContext.getConnection(),lObligationModificationRequestBean.getFuId());
        HashMap<String, Object> lFinalMap = (HashMap<String, Object>) obligationModificationRequestBeanMeta.formatAsMap(lObligationModificationRequestBean, null, null, false);
        if (lAcceptedRate!=null) {
        	lFinalMap.put("acceptedRate", lAcceptedRate);
        }
        return new JsonBuilder(lFinalMap).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationModificationRequestBean lFilterBean = new ObligationModificationRequestBean();
        obligationModificationRequestBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<ObligationModificationRequestBean> lObligationModificationRequestList = obligationModificationRequestBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (ObligationModificationRequestBean lObligationModificationRequestBean : lObligationModificationRequestList) {
            lResults.add(obligationModificationRequestBeanMeta.formatAsArray(lObligationModificationRequestBean, null, lFields, false));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured
    @Path("/all")
    public String listCsv(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationModificationRequestBean lFilterBean = new ObligationModificationRequestBean();
        obligationModificationRequestBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<ObligationModificationRequestBean> lObligationModificationRequestList = obligationModificationRequestBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (ObligationModificationRequestBean lObligationModificationRequestBean : lObligationModificationRequestList) {
            lResults.add(obligationModificationRequestBeanMeta.formatAsArray(lObligationModificationRequestBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<ObligationModificationRequestBean> lObligationModificationRequestList = obligationModificationRequestBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (ObligationModificationRequestBean lObligationModificationRequestBean : lObligationModificationRequestList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lObligationModificationRequestBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lObligationModificationRequestBean.getFuId());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-save", lUserBean)){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-save", lUserBean)){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationModificationRequestBean lObligationModificationRequestBean = new ObligationModificationRequestBean();
        List<ValidationFailBean> lValidationFailBeans = obligationModificationRequestBeanMeta.validateAndParse(lObligationModificationRequestBean, 
           pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
		   //TODO: 
//        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
//            throw new CommonValidationException(lValidationFailBeans);
        obligationModificationRequestBO.save(pExecutionContext, lObligationModificationRequestBean, lUserBean, pNew);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured 
    @Path("/revisedStatus")
    public String get2FAType(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
    	Map<String,ArrayList<Map<String,String>>> lReturnMap = new HashMap<String,ArrayList<Map<String,String>>>();
    	ArrayList<Map<String,String>> lStatuses= new ArrayList<Map<String,String>>();
    	Map<String,String> lStatusObj = new HashMap<String,String>();
    	Map<Status,List<Status>> lStatusesMap = TredsHelper.getInstance().getValidModificationStatuses();
    	for (Status lOldStatus : lStatusesMap.keySet() ){
    			lStatuses= new ArrayList<Map<String,String>>();
    			List<Status> lNewStatusList = lStatusesMap.get(lOldStatus);
    			for(Status lNewStatus : lNewStatusList){
    				lStatusObj = new HashMap<String,String>();
    				lStatusObj.put(BeanFieldMeta.JSONKEY_TEXT, lNewStatus.name());
    				lStatusObj.put(BeanFieldMeta.JSONKEY_VALUE, lNewStatus.getCode());
    				lStatuses.add(lStatusObj);
    			}
    			lReturnMap.put(lOldStatus.getCode(), lStatuses);
    	}
    	return new JsonBuilder(lReturnMap).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
	@Path("checker")
    public void setCheckerStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pFilter) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	ObligationModificationRequestBean.Status lStatus = null;
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
    	if (ObligationModificationRequestBean.Status.Approved.getCode().equals((String)lMap.get("status"))){
    		lStatus = ObligationModificationRequestBean.Status.Approved;
    	}else if (ObligationModificationRequestBean.Status.Rejected.getCode().equals((String)lMap.get("status"))){
    		lStatus = ObligationModificationRequestBean.Status.Rejected;
    	}
    	if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-approve", lUserBean)){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
    	String lRemarks = (String) lMap.get("remarks");
    	String lId = null;
    	if (lMap.containsKey("id")) {
    		lId = ( lMap.get("id").toString());
    		obligationModificationRequestBO.updateStatus(pExecutionContext.getConnection(), lStatus, Long.valueOf(lId) , (AppUserBean) lUserBean,lRemarks);
    	}else {
    		if(StringUtils.isNotEmpty(lMap.get("ids").toString())){
            	String[] lTmpList1 = CommonUtilities.splitString(lMap.get("ids").toString(), ",");
            	for(int lPtr=0; lPtr < lTmpList1.length; lPtr++){
            		obligationModificationRequestBO.updateStatus(pExecutionContext.getConnection(), lStatus, Long.valueOf(lTmpList1[lPtr]) , (AppUserBean) lUserBean,lRemarks);
            	}
            }
    	}
    	
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
	@Path("status")
    public void markAsSent(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
         ,String pFilter) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	ObligationModificationRequestBean.Status lStatus = null;
    	JsonSlurper lJsonSlurper = new JsonSlurper();
    	Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
    	if (ObligationModificationRequestBean.Status.Sent.getCode().equals((String)lMap.get("status"))){
    		lStatus = ObligationModificationRequestBean.Status.Sent;
    	}
    	if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-save", lUserBean)){
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
    	}
    	String lId = ( lMap.get("id").toString());
    	obligationModificationRequestBO.updateStatus(pExecutionContext.getConnection(), lStatus, Long.valueOf(lId) , (AppUserBean) lUserBean,null);
    }
    
    @GET
    @Path("generateObliModiRequests/{pfId}")
    public Response getModiRequest(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("pfId") Long pPfId) throws Exception {
       	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
       	ObligationModificationRequestBean.Status lStatus = null;
       	JsonSlurper lJsonSlurper = new JsonSlurper();
       	if (!AccessControlHelper.getInstance().hasAccess("oblimodreq-save", lUserBean)){
               throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
       	}
       	byte[] lContent = obligationModificationRequestBO.getObligationDetails(pExecutionContext.getConnection(), pPfId);
       	String lContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lContent);
               output.flush();
            }
        };
        return Response.ok(lStreamingOutput, lContentType).header("content-disposition", "attachment; filename = "+"ObligationsForPfId"+pPfId).build();
    }
}