
package com.xlx.treds.adapter.rest;

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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.adapter.IOCLClientAdapter;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bo.AdapterRequestResponseBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/adapterrequestresponse")
@Singleton
public class AdapterRequestResponseResource {

	public static final Logger logger = LoggerFactory.getLogger(AdapterRequestResponseResource.class);

    private AdapterRequestResponseBO adapterRequestResponseBO;
    private BeanMeta adapterRequestResponseBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public AdapterRequestResponseResource() {
        super();
        adapterRequestResponseBO = new AdapterRequestResponseBO();
        adapterRequestResponseBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AdapterRequestResponseBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","entityCode","processId","key","type","apiRequestType","apiRequestUrl","apiRequestData","uid","timestamp","requestStatus","responseAckStatus","apiResponseUrl","apiResponseData","apiResponseStatus","apiResponseDataReturned","provResponseAckStatus","provResponseData"});
        lovFields = Arrays.asList(new String[]{"id","entityCode"});
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
        pRequest.getRequestDispatcher("/WEB-INF/adapterrequestresponse.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "adapterrequestresponse-resend")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        AdapterRequestResponseBean lFilterBean = new AdapterRequestResponseBean();
        lFilterBean.setId(pId);
        AdapterRequestResponseBean lAdapterRequestResponseBean = adapterRequestResponseBO.findBean(pExecutionContext, lFilterBean);
        return adapterRequestResponseBeanMeta.formatAsJson(lAdapterRequestResponseBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "arr-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        AdapterRequestResponseBean lFilterBean = new AdapterRequestResponseBean();
        adapterRequestResponseBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<AdapterRequestResponseBean> lAdapterRequestResponseList = adapterRequestResponseBO.findListFromSql(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (AdapterRequestResponseBean lAdapterRequestResponseBean : lAdapterRequestResponseList) {
            lResults.add(adapterRequestResponseBeanMeta.formatAsArray(lAdapterRequestResponseBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }
    
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "arr-resend")
    @Path("/resendresponse")
    public void resendResponseToClient(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AdapterRequestResponseBean lAdapterRequestResponseBean = new AdapterRequestResponseBean();
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        adapterRequestResponseBeanMeta.validateAndParse(lAdapterRequestResponseBean, lMap, null);
        adapterRequestResponseBO.resendResponse(pExecutionContext.getConnection(), lAdapterRequestResponseBean);
    }

}