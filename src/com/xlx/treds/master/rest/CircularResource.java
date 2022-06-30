package com.xlx.treds.master.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.YesNo;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.master.bean.CircularBean;
import com.xlx.treds.master.bo.CircularBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/circulars")
public class CircularResource {

    private CircularBO circularBO;
    private BeanMeta circularBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public CircularResource() {
        super();
        circularBO = new CircularBO();
        circularBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CircularBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","circularNo","title","description","date","category","purchaser","supplier","financier","admin","user","department","displayAsNewForDays","fileName","storageFileName","archive","tab","recordVersion"});
        lovFields = Arrays.asList(new String[]{"id","circularNo"});
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
        pRequest.getRequestDispatcher("/WEB-INF/circulars.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        CircularBean lFilterBean = new CircularBean();
        lFilterBean.setId(pId);
        CircularBean lCircularBean = circularBO.findBean(pExecutionContext, lFilterBean);
        return circularBeanMeta.formatAsJson(lCircularBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CircularBean lFilterBean = new CircularBean();
        circularBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<CircularBean> lCircularList = circularBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object> lResults = new ArrayList<Object>();
        for (CircularBean lCircularBean : lCircularList) {
            lResults.add(circularBeanMeta.formatAsMap(lCircularBean, null, defaultListFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured
//    @Path("/all")
//    public String listCsv(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
//        String pFilter) throws Exception {
//        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//        JsonSlurper lJsonSlurper = new JsonSlurper();
//        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
//        CircularBean lFilterBean = new CircularBean();
//        circularBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
//
//        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
//        if (lFields == null) lFields = defaultListFields;
//        List<CircularBean> lCircularList = circularBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
//
//        List<Object[]> lResults = new ArrayList<Object[]>();
//        for (CircularBean lCircularBean : lCircularList) {
//            lResults.add(circularBeanMeta.formatAsArray(lCircularBean, null, lFields, true));            
//        }
//        return new JsonBuilder(lResults).toString();
//    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<CircularBean> lCircularList = circularBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CircularBean lCircularBean : lCircularList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lCircularBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lCircularBean.getCircularNo());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="circulars-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="circulars-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CircularBean lCircularBean = new CircularBean();
        List<ValidationFailBean> lValidationFailBeans = circularBeanMeta.validateAndParse(lCircularBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        circularBO.save(pExecutionContext, lCircularBean, lUserBean, pNew);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured (secKey="circulars-save")
    @Path("/archive")
    public void archive(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	 IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	 JsonSlurper lJsonSlurper = new JsonSlurper();
         Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
         List<String> lIds = (ArrayList<String>) lMap.get("ids");
         YesNo lArchiveFlag = ((Boolean) lMap.get("archiveFlag"))?YesNo.Yes:YesNo.No;
         circularBO.archive(pExecutionContext.getConnection(), lIds, lArchiveFlag,lUserBean);
//         List<CircularBean> lCircularList = circularBO.findList(pExecutionContext, null, lovFields, lUserBean);
//         List<Object> lResults = new ArrayList<Object>();
//         for (CircularBean lCircularBean : lCircularList) {
//             lResults.add(circularBeanMeta.formatAsMap(lCircularBean, null, defaultListFields, true));            
//         }
//         return new JsonBuilder(lResults).toString();
    }
    
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CircularBean lFilterBean = new CircularBean();
        lFilterBean.setId(pId);
        circularBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}