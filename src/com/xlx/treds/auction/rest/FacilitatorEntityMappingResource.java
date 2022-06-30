package com.xlx.treds.auction.rest;

import java.util.ArrayList;
import java.util.Arrays;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.auction.bean.FacilitatorEntityMappingBean;
import com.xlx.treds.auction.bo.FacilitatorEntityMappingBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/facentmap")
public class FacilitatorEntityMappingResource {

    private FacilitatorEntityMappingBO facilitatorEntityMappingBO;
    private BeanMeta facilitatorEntityMappingBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public FacilitatorEntityMappingResource() {
        super();
        facilitatorEntityMappingBO = new FacilitatorEntityMappingBO();
        facilitatorEntityMappingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(FacilitatorEntityMappingBean.class);
        defaultListFields = Arrays.asList(new String[]{"facilitator","entityCode","mappingCode","active","cbdId","mandateAmount","haircut"});
        lovFields = Arrays.asList(new String[]{"facilitator","entityCode","cbdId"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("facilitator") String pFacilitator, @QueryParam("entityCode") String pEntityCode, @QueryParam("cbdId") Long pCBDId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pFacilitator != null) && (pEntityCode != null)) {
            Object[] lKey = new Object[]{pFacilitator, pEntityCode, pCBDId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/facentmap.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="facentmap-view")
    @Path("/{facilitator}/{entityCode}/{cbdId}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("facilitator") String pFacilitator, @PathParam("entityCode") String pEntityCode, @PathParam("cbdId") Long pCBDId) throws Exception {
        FacilitatorEntityMappingBean lFilterBean = new FacilitatorEntityMappingBean();
        lFilterBean.setFacilitator(pFacilitator);
        lFilterBean.setEntityCode(pEntityCode);
        lFilterBean.setCbdId(pCBDId);
        FacilitatorEntityMappingBean lFacilitatorEntityMappingBean = facilitatorEntityMappingBO.findBean(pExecutionContext, lFilterBean);
        return facilitatorEntityMappingBeanMeta.formatAsJson(lFacilitatorEntityMappingBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="facentmap-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        FacilitatorEntityMappingBean lFilterBean = new FacilitatorEntityMappingBean();
        facilitatorEntityMappingBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<FacilitatorEntityMappingBean> lFacilitatorEntityMappingList = facilitatorEntityMappingBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (FacilitatorEntityMappingBean lFacilitatorEntityMappingBean : lFacilitatorEntityMappingList) {
            lResults.add(facilitatorEntityMappingBeanMeta.formatAsArray(lFacilitatorEntityMappingBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="facentmap-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="facentmap-save")
    public String update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        String lMsg = save(pExecutionContext, pRequest, pMessage, false);
        if(StringUtils.isNotBlank(lMsg))
        	return lMsg;
        return null;
    }
    
    private String save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        FacilitatorEntityMappingBean lFacilitatorEntityMappingBean = new FacilitatorEntityMappingBean();
        List<ValidationFailBean> lValidationFailBeans = facilitatorEntityMappingBeanMeta.validateAndParse(lFacilitatorEntityMappingBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        return facilitatorEntityMappingBO.save(pExecutionContext, lFacilitatorEntityMappingBean, lUserBean, pNew);
    }

}