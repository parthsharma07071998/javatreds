package com.xlx.treds.auction.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationBean.Type;
import com.xlx.treds.auction.bo.ModificationObligationBO;
import com.xlx.treds.auction.bo.ObligationBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/modifyOblig")
public class ModificationObligationResource {

    private ObligationBO obligationBO;
    private ModificationObligationBO modifiactionObligationBO;
    private BeanMeta obligationBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public ModificationObligationResource() {
        super();
        obligationBO = new ObligationBO();
        modifiactionObligationBO = new ModificationObligationBO();
        obligationBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","fuId","partNumber","type","date","status","createDate","createrAuId","approveRejectDate","approveRejectAuId"});
        lovFields = Arrays.asList(new String[]{"id","fuId"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId , @QueryParam("fuId") Long pfuId 
		, @QueryParam("type") String pType) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }else if ((pfuId!=null && StringUtils.isNotEmpty(pType))){
            pRequest.setAttribute("fuId", pfuId);
            pRequest.setAttribute("type", pType);
        }
        pRequest.getRequestDispatcher("/WEB-INF/modifyOblig.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public Object list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pFilter) throws Exception {
    	ObligationBean.Type lType = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationBean lFilterBean = new ObligationBean();
        obligationBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        BeanFieldMeta lField = obligationBeanMeta.getFieldMap().get("type");
        Map<String, IKeyValEnumInterface> lMap1 = lField.getDataSetKeyValueReverseMap();
        if (lMap1.containsKey(lMap.get("type"))){
        	lType = (Type) lMap1.get(lMap.get("type"));
        }
        Long pFuId = (Long) lMap.get("fuId");
        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<ObligationBean> lObligationList = obligationBO.getObligationsForModification(pExecutionContext.getConnection(), pFuId, lType);
		
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (ObligationBean lObligationBean : lObligationList) {
        	lResults.add(obligationBeanMeta.formatAsArray(lObligationBean, null, lFields, false));        		
        }
        return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/changeDate")
    public Object listForDate(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pFilter) throws Exception {
    	ObligationBean.Type lType = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
		String lId = lMap.get("id").toString();
		Object pDate = (Object)lMap.get("date");
		List<Map<String, Object>> lTableData = new ArrayList<Map<String,Object>>();
		lTableData = (List<Map<String, Object>>) lMap.get("table");
		List<Map<String, Object>> lFinalData = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> lData : lTableData){
				lData.put("date", pDate);
				lFinalData.add(lData);
		}
		System.out.println(lFinalData.size());
        return new JsonBuilder(lFinalData).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/changeSettlor")
    public Object listForSettlor(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pFilter) throws Exception {
    	ObligationBean.Type lType = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
		String lId = lMap.get("id").toString();
		Object pSettlor = (Object)lMap.get("settlor");
		List<Map<String, Object>> lTableData = new ArrayList<Map<String,Object>>();
		lTableData = (List<Map<String, Object>>) lMap.get("table");
		List<Map<String, Object>> lFinalData = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> lData : lTableData){
				lData.put("paymentSettlor", pSettlor);
				lFinalData.add(lData);
		}
		System.out.println(lFinalData.size());
        return new JsonBuilder(lFinalData).toString();
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/addRemarks")
    public Object listForRemarks(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pFilter) throws Exception {
    	ObligationBean.Type lType = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
		String lId = lMap.get("id").toString();
		Object pRemarks = (Object)lMap.get("respRemarks");
		List<Map<String, Object>> lTableData = new ArrayList<Map<String,Object>>();
		lTableData = (List<Map<String, Object>>) lMap.get("table");
		List<Map<String, Object>> lFinalData = new ArrayList<Map<String,Object>>();
		for (Map<String, Object> lData : lTableData){
			if (lData.get("id").equals(Long.valueOf(lId))){
				lData.put("respRemarks", pRemarks);
			}
			lFinalData.add(lData);	
		}
		System.out.println(lFinalData.size());
        return new JsonBuilder(lFinalData).toString();
    }
    @POST
    @Secured
    @Path("/save")
    public void Save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , String pFilter) throws Exception {
    	ObligationBean.Type lType = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        modifiactionObligationBO.saveModifiedData( pExecutionContext,lMap,(AppUserBean) lUserBean);
    }

    }