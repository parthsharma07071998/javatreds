package com.xlx.treds.auction.rest;

import java.sql.Date;
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
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.ObligationExtensionBean;
import com.xlx.treds.auction.bean.ObligationExtensionBean.Status;
import com.xlx.treds.auction.bo.ObligationExtensionBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/obligext")
public class ObligationExtensionResource {

    private ObligationExtensionBO obligationExtensionBO;
    private BeanMeta obligationExtensionBeanMeta;
	private List<String> defaultListFields, lovFields;
	private GenericDAO<ObligationExtensionBean> obligationExtensionDAO;
	
    public ObligationExtensionResource() {
        super();
        obligationExtensionBO = new ObligationExtensionBO();
        obligationExtensionBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationExtensionBean.class);
        defaultListFields = Arrays.asList(new String[]{"obId","creditObId","purchaser","financier","oldDate","Currency","oldAmount","newDate","penalty","penaltyRate","newAmount","status","remarks","upfrontCharge"});
        lovFields = Arrays.asList(new String[]{"obId","creditObId"});
        obligationExtensionDAO = new GenericDAO<ObligationExtensionBean>(ObligationExtensionBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("obId") Long pObId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pObId != null)) {
            Object[] lKey = new Object[]{pObId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/obligext.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-view")
    @Path("/{obId}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("obId") Long pObId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionBean lFilterBean = new ObligationExtensionBean();
        lFilterBean.setObId(pObId);
        ObligationExtensionBean lObligationExtensionBean = obligationExtensionBO.findBean(pExecutionContext, lFilterBean, lUserBean);
        return obligationExtensionBeanMeta.formatAsJson(lObligationExtensionBean);
    }

    @GET
    @Secured(secKey="obligext-view")
    @Path("/check/{obId}")
    public void canSeekExtention(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("obId") Long pObId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionBean lFilterBean = new ObligationExtensionBean();
        lFilterBean.setObId(pObId);
        //while fetching error is thrown thus if no errors thrown then the check is true
        ObligationExtensionBean lObligationExtensionBean = obligationExtensionBO.findBean(pExecutionContext, lFilterBean, lUserBean);
        if(lObligationExtensionBean==null){
        	throw new CommonBusinessException("No data retrived for extension.");
        }
    }
    
    @POST
    @Secured(secKey="obligext-view")
    @Path("/check")
    public String canSeekExtention1(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    	,String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        List<Long> lIdList = (List<Long>) lMap.get("obIds");
        boolean lUpFront =  (boolean) lMap.get("upfrontCharge");
        ObligationExtensionBean lFilterBean = new ObligationExtensionBean();
        Map<String, Object> lRtnMap = new HashMap<>();
        lRtnMap.put("success",new ArrayList<Object>());
        lRtnMap.put("error",new ArrayList<Object>());
        ObligationExtensionBean lObligationExtensionBean = null;
        for (Long lId : lIdList) {
        	lFilterBean.setNewDate(new Date(BeanMetaFactory.getInstance().getDateFormatter().parse((String)lMap.get("newDate")).getTime()));
            lFilterBean.setObId(lId);
            if (lUpFront) {
            	lFilterBean.setUpfrontCharge(Yes.Yes);
            }
            Map<String,String> lErrMap = new HashMap<>();
        	// while fetching error is thrown thus if no errors thrown then the check is true
            try{
            	lObligationExtensionBean = obligationExtensionBO.findBean(pExecutionContext, lFilterBean, lUserBean);
            	if(lObligationExtensionBean==null){
                	throw new CommonBusinessException("No data retrived for extension.");
                }else {
                	List<Object> lObj = (List<Object>) lRtnMap.get("success");
                	lObj.add(obligationExtensionBeanMeta.formatAsMap(lObligationExtensionBean));
                }
            }catch (Exception e) {
            	List<Object> lObj =  (List<Object>) lRtnMap.get("error");
            	lErrMap.put("id", lId.toString());
            	lErrMap.put("message", e.getMessage());
            	lObj.add(lErrMap);
            }
        }
        return new JsonBuilder(lRtnMap).toString();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationExtensionBean lFilterBean = new ObligationExtensionBean();
        obligationExtensionBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<ObligationExtensionBean> lObligationExtensionList = obligationExtensionBO.findList(pExecutionContext, lFilterBean, null, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (ObligationExtensionBean lObligationExtensionBean : lObligationExtensionList) {
            lResults.add(obligationExtensionBeanMeta.formatAsArray(lObligationExtensionBean, null, lFields, false));            
        }
        return new JsonBuilder(lResults).toString();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionBean lObligationExtensionBean = new ObligationExtensionBean();
        List<ValidationFailBean> lValidationFailBeans = obligationExtensionBeanMeta.validateAndParse(lObligationExtensionBean, 
            pMessage, null, null);
//        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
//            throw new CommonValidationException(lValidationFailBeans);
        obligationExtensionBO.save(pExecutionContext, lObligationExtensionBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-save")
    @Path("/{obId}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("obId") Long pObId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionBean lFilterBean = new ObligationExtensionBean();
        lFilterBean.setObId(pObId);
        obligationExtensionBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey={"obligext-wf", "obligext-save"})
    @Path("/status")
    public void updateStatus(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        List<Long> lIdList = (List<Long>) lMap.get("obIds");
        for (Long lId : lIdList) {
        	ObligationExtensionBean lObligationExtensionBean = new ObligationExtensionBean();
            List<ValidationFailBean> lValidationFailBeans = obligationExtensionBeanMeta.validateAndParse(lObligationExtensionBean, 
                pMessage, ObligationExtensionBean.FIELDGROUP_UPDATESTATUS, null);
            lObligationExtensionBean.setObId(lId);
            obligationExtensionBO.updateStatus(pExecutionContext, lObligationExtensionBean, lUserBean);
        }
    }
    
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-save")
    @Path("/sendToFin")
    public void sendToFinancier(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionBean lObligationExtensionBean = new ObligationExtensionBean();
        List<ValidationFailBean> lValidationFailBeans = obligationExtensionBeanMeta.validateAndParse(lObligationExtensionBean, 
            pMessage, ObligationExtensionBean.FIELDGROUP_UPDATESTATUS, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        lObligationExtensionBean.setStatus(Status.ForApproval);
        obligationExtensionDAO.update(pExecutionContext.getConnection(), lObligationExtensionBean,ObligationExtensionBean.FIELDGROUP_UPDATESTATUS);
        pExecutionContext.getConnection().commit();
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/check/{obId}/{date}/{upFront}")
    public String check(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("obId") Long pObId, @PathParam("date") String pNewDate, @PathParam("upFront") String pUpFront) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionBean lFilterBean = new ObligationExtensionBean();
        lFilterBean.setObId(pObId);
        lFilterBean.setNewDate(new Date(BeanMetaFactory.getInstance().getDateFormatter().parse(pNewDate).getTime()));
        if (CommonAppConstants.Yes.Yes.getCode().equals(pUpFront)) {
        	lFilterBean.setUpfrontCharge(Yes.Yes);
        }
        ObligationExtensionBean lObligationExtensionBean = obligationExtensionBO.findBean(pExecutionContext, lFilterBean, lUserBean);
        return obligationExtensionBeanMeta.formatAsJson(lObligationExtensionBean);
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-save")
    @Path("/save")
    public String save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        boolean lSubmit = (boolean) lMap.get("submit");
        List<Map<String, Object>> lBeanList =  (List<Map<String, Object>>) lMap.get("list");
        ObligationExtensionBean lObligationExtensionBean = null;
        for (Map<String, Object> lTmpMap : lBeanList) {
        	lObligationExtensionBean = new ObligationExtensionBean();
        	List<ValidationFailBean> lValidationFailBeans = obligationExtensionBeanMeta.validateAndParse(lObligationExtensionBean, 
        			lTmpMap ,  BeanMeta.FIELDGROUP_UPDATE , null);
        	if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
                throw new CommonValidationException(lValidationFailBeans);
        	if (lSubmit==true) {
        		lObligationExtensionBean.setStatus(Status.ForApproval);
        	}
        	obligationExtensionBO.save(pExecutionContext, lObligationExtensionBean, lUserBean, false);
        }
        return new JsonBuilder(null).toString();
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligext-wf")
    @Path("/placebid")
    public void placeBid(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
        List<Long> lIdList = (List<Long>) lMap.get("obIds");
        boolean lSubmit = (boolean) lMap.get("submit");
        for (Long lId : lIdList) {
        	ObligationExtensionBean lObligationExtensionBean = new ObligationExtensionBean();
            obligationExtensionBeanMeta.validateAndParse(lObligationExtensionBean, lMap, null);
            lObligationExtensionBean.setObId(lId);
            obligationExtensionBO.placeBid(pExecutionContext, lObligationExtensionBean, lUserBean,lSubmit);
        }
        
    }
    
}