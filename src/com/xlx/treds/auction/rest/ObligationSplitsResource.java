package com.xlx.treds.auction.rest;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bo.ObligationSplitsBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/obsplit")
public class ObligationSplitsResource {

    private ObligationSplitsBO obligationSplitsBO;
    private BeanMeta obligationSplitsBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public ObligationSplitsResource() {
        super();
        obligationSplitsBO = new ObligationSplitsBO();
        obligationSplitsBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationSplitsBean.class);
        defaultListFields = Arrays.asList(new String[]{"obid","partNumber","amount","status","pfId","fileSeqNo","settledDate","settledAmount","paymentRefNo","respErrorCode","respRemarks"});
        lovFields = Arrays.asList(new String[]{"obid","partNumber"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("obid") Long pObid, @QueryParam("partNumber") Long pPartNumber) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pObid != null) && (pPartNumber != null)) {
            Object[] lKey = new Object[]{pObid, pPartNumber};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/obsplit.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{obid}/{partNumber}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("obid") Long pObid, @PathParam("partNumber") Long pPartNumber) throws Exception {
        ObligationSplitsBean lFilterBean = new ObligationSplitsBean();
        lFilterBean.setId(pObid);
        lFilterBean.setPartNumber(pPartNumber);
        ObligationSplitsBean lObligationSplitsBean = obligationSplitsBO.findBean(pExecutionContext, lFilterBean);
        return obligationSplitsBeanMeta.formatAsJson(lObligationSplitsBean);
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
        ObligationSplitsBean lFilterBean = new ObligationSplitsBean();
        obligationSplitsBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<ObligationSplitsBean> lObligationSplitsList = null;
        if (lMap.containsKey("forModification")) {
        	lObligationSplitsList = obligationSplitsBO.findListForModification(pExecutionContext, lFilterBean, lFields, lUserBean);
        }else {
        	lObligationSplitsList = obligationSplitsBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        }
         

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (ObligationSplitsBean lObligationSplitsBean : lObligationSplitsList) {
            lResults.add(obligationSplitsBeanMeta.formatAsArray(lObligationSplitsBean, null, lFields, true));            
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
//        ObligationSplitsBean lFilterBean = new ObligationSplitsBean();
//        obligationSplitsBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
//
//        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
//        if (lFields == null) lFields = defaultListFields;
//        List<ObligationSplitsBean> lObligationSplitsList = obligationSplitsBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
//
//        List<Object[]> lResults = new ArrayList<Object[]>();
//        for (ObligationSplitsBean lObligationSplitsBean : lObligationSplitsList) {
//            lResults.add(obligationSplitsBeanMeta.formatAsArray(lObligationSplitsBean, null, lFields, true));            
//        }
//        return new JsonBuilder(lResults).toString();
//    }
//
//    @GET
//    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
//    @Path("/all")
//    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
//        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
//        List<ObligationSplitsBean> lObligationSplitsList = obligationSplitsBO.findList(pExecutionContext, null, lovFields, lUserBean);
//        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
//        for (ObligationSplitsBean lObligationSplitsBean : lObligationSplitsList) {
//            Map<String, Object> lData = new HashMap<String, Object>();
//            lData.put(BeanFieldMeta.JSONKEY_VALUE, lObligationSplitsBean.getObid() + "," + lObligationSplitsBean.getPartNumber());
//            lData.put(BeanFieldMeta.JSONKEY_TEXT, lObligationSplitsBean.getAmount());
//            lResults.add(lData);
//        }
//        return new JsonBuilder(lResults).toString();
//    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationSplitsBean lObligationSplitsBean = new ObligationSplitsBean();
        List<ValidationFailBean> lValidationFailBeans = obligationSplitsBeanMeta.validateAndParse(lObligationSplitsBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        obligationSplitsBO.save(pExecutionContext, lObligationSplitsBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{obid}/{partNumber}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("obid") Long pObid, @PathParam("partNumber") Long pPartNumber) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationSplitsBean lFilterBean = new ObligationSplitsBean();
        lFilterBean.setId(pObid);
        lFilterBean.setPartNumber(pPartNumber);
        obligationSplitsBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}