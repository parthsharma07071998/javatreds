package com.xlx.treds.other.rest;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

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

import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.other.bean.CustomFieldBean;
import com.xlx.treds.other.bo.CustomFieldBO;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.commonn.other.bean.FileDownloadBean;

@Path("/customfields")
public class CustomFieldResource {

    private CustomFieldBO customFieldBO;
    private BeanMeta customFieldBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public CustomFieldResource() {
        super();
        customFieldBO = new CustomFieldBO();
        customFieldBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CustomFieldBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","code","settings"});
        lovFields = Arrays.asList(new String[]{"id","code"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null)) {
        	Object[] lKey = new Object[]{pCode};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/customfields.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/{code}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
    	String lCode = pCode;
    	if ("self".equals(lCode)) {
        	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        	lCode = lUserBean.getDomain();
        }
        CustomFieldBean lFilterBean = new CustomFieldBean();
        lFilterBean.setCode(lCode);
        AppEntityBean lAEBean = TredsHelper.getInstance().getAppEntityBean(lFilterBean.getCode());
        if (lAEBean.getPreferences().getEcf()==null || !CommonAppConstants.Yes.Yes.equals(lAEBean.getPreferences().getEcf())) {
        	throw new CommonBusinessException("Access Denied.");
        }
        CustomFieldBean lCustomFieldBean = customFieldBO.findBean(pExecutionContext.getConnection(), lFilterBean);
        return customFieldBeanMeta.formatAsJson(lCustomFieldBean);
    }
    

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
//    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CustomFieldBean lFilterBean = new CustomFieldBean();
        customFieldBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<CustomFieldBean> lCustomFieldList = customFieldBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (CustomFieldBean lCustomFieldBean : lCustomFieldList) {
            lResults.add(customFieldBeanMeta.formatAsArray(lCustomFieldBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
//    @Secured
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CustomFieldBean lCustomFieldBean = new CustomFieldBean();
        List<ValidationFailBean> lValidationFailBeans = customFieldBeanMeta.validateAndParse(lCustomFieldBean, 
            pMessage,"validate" , null);
        customFieldBO.save(pExecutionContext, lCustomFieldBean, lUserBean, pNew);
    }


}