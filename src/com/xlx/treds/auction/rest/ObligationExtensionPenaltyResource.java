package com.xlx.treds.auction.rest;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.xlx.treds.auction.bean.ObligationExtensionPenaltyBean;
import com.xlx.treds.auction.bo.ObligationExtensionPenaltyBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/obligpenalty")
public class ObligationExtensionPenaltyResource {

    private ObligationExtensionPenaltyBO obligationExtensionPenaltyBO;
    private BeanMeta obligationExtensionPenaltyBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public ObligationExtensionPenaltyResource() {
        super();
        obligationExtensionPenaltyBO = new ObligationExtensionPenaltyBO();
        obligationExtensionPenaltyBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(ObligationExtensionPenaltyBean.class);
        defaultListFields = Arrays.asList(new String[]{"financier","purchaser","allowExtension","maxExtension"});
        lovFields = Arrays.asList(new String[]{"financier","purchaser"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("financier") String pFinancier, @QueryParam("purchaser") String pPurchaser) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pFinancier != null) && (pPurchaser != null)) {
            Object[] lKey = new Object[]{pFinancier, pPurchaser};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/obligpenalty.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligpenalty-view")
    @Path("/{financier}/{purchaser}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("financier") String pFinancier, @PathParam("purchaser") String pPurchaser) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionPenaltyBean lFilterBean = new ObligationExtensionPenaltyBean();
        lFilterBean.setFinancier(pFinancier);
        lFilterBean.setPurchaser(pPurchaser);
        ObligationExtensionPenaltyBean lObligationExtensionPenaltyBean = obligationExtensionPenaltyBO.findBean(pExecutionContext, lFilterBean, lUserBean, true);
        return obligationExtensionPenaltyBeanMeta.formatAsJson(lObligationExtensionPenaltyBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligpenalty-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ObligationExtensionPenaltyBean lFilterBean = new ObligationExtensionPenaltyBean();
        obligationExtensionPenaltyBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<ObligationExtensionPenaltyBean> lObligationExtensionPenaltyList = obligationExtensionPenaltyBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (ObligationExtensionPenaltyBean lObligationExtensionPenaltyBean : lObligationExtensionPenaltyList) {
            lResults.add(obligationExtensionPenaltyBeanMeta.formatAsArray(lObligationExtensionPenaltyBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligpenalty-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="obligpenalty-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        ObligationExtensionPenaltyBean lObligationExtensionPenaltyBean = new ObligationExtensionPenaltyBean();
        List<ValidationFailBean> lValidationFailBeans = obligationExtensionPenaltyBeanMeta.validateAndParse(lObligationExtensionPenaltyBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        obligationExtensionPenaltyBO.save(pExecutionContext, lObligationExtensionPenaltyBean, lUserBean, pNew);
    }

}