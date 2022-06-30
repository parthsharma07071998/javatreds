package com.xlx.treds.entity.rest;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.entity.bean.MemberwisePlanBean;
import com.xlx.treds.entity.bo.MemberwisePlanBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/memberwiseplan")
public class MemberwisePlanResource {

    private MemberwisePlanBO memberwisePlanBO;
    private BeanMeta memberwisePlanBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public MemberwisePlanResource() {
        super();
        memberwisePlanBO = new MemberwisePlanBO();
        memberwisePlanBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(MemberwisePlanBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","effectiveStartDate","effectiveEndDate","acpId"});
        lovFields = Arrays.asList(new String[]{"code","effectiveStartDate"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode, @QueryParam("effectiveStartDate") Date pEffectiveStartDate) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null) && (pEffectiveStartDate != null)) {
            Object[] lKey = new Object[]{pCode, pEffectiveStartDate};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/memberwiseplan.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="memplan-view")
    @Path("/{code}/{effectiveStartDate}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode, @PathParam("effectiveStartDate") String pEffectiveStartDate) throws Exception {
        MemberwisePlanBean lFilterBean = new MemberwisePlanBean();
        lFilterBean.setCode(pCode);
        lFilterBean.setEffectiveStartDate(CommonUtilities.getDate(pEffectiveStartDate, AppConstants.DATE_FORMAT));
        return new JsonBuilder(memberwisePlanBO.getMapById(pExecutionContext, lFilterBean)).toString(); 
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="memplan-view")
    @Path("/cal/{acpId}/{finShare}")
    public String getCalculations(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("acpId") Long pAcpid, @PathParam("finShare") BigDecimal pEffectiveStartDate) throws Exception {
    	Map<String,Object> lMap = new HashMap<>();
        memberwisePlanBO.getFinacierSplitCalculations(pExecutionContext, lMap, pAcpid, pEffectiveStartDate);
        return new JsonBuilder(lMap).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="memplan-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        MemberwisePlanBean lFilterBean = new MemberwisePlanBean();
        memberwisePlanBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<MemberwisePlanBean> lMemberwisePlanList = memberwisePlanBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (MemberwisePlanBean lMemberwisePlanBean : lMemberwisePlanList) {
            lResults.add(memberwisePlanBeanMeta.formatAsArray(lMemberwisePlanBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured (secKey="memplan-view")
    @Path("/all")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        MemberwisePlanBean lFilterBean = new MemberwisePlanBean();
        memberwisePlanBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<MemberwisePlanBean> lMemberwisePlanList = memberwisePlanBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (MemberwisePlanBean lMemberwisePlanBean : lMemberwisePlanList) {
            lResults.add(memberwisePlanBeanMeta.formatAsArray(lMemberwisePlanBean, null, lFields, true));            
        }
        return new FileDownloadBean("MemberAuctionPlan.csv", 
        		memberwisePlanBeanMeta.formatBeansAsCsv(lMemberwisePlanList, null, lFields, true, true).getBytes(), null).getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<MemberwisePlanBean> lMemberwisePlanList = memberwisePlanBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (MemberwisePlanBean lMemberwisePlanBean : lMemberwisePlanList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lMemberwisePlanBean.getCode() + CommonConstants.KEY_SEPARATOR + lMemberwisePlanBean.getEffectiveStartDate());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lMemberwisePlanBean.getEffectiveEndDate());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="memplan-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="memplan-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        MemberwisePlanBean lMemberwisePlanBean = new MemberwisePlanBean();
        List<ValidationFailBean> lValidationFailBeans = memberwisePlanBeanMeta.validateAndParse(lMemberwisePlanBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        memberwisePlanBO.save(pExecutionContext, lMemberwisePlanBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{code}/{effectiveStartDate}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode, @PathParam("effectiveStartDate") Date pEffectiveStartDate) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        MemberwisePlanBean lFilterBean = new MemberwisePlanBean();
        lFilterBean.setCode(pCode);
        lFilterBean.setEffectiveStartDate(pEffectiveStartDate);
        memberwisePlanBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}