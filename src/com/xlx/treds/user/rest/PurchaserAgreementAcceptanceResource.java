package com.xlx.treds.user.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.RestrictedLoginRequired;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.ClickWrapHelper;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/puraggacc")
public class PurchaserAgreementAcceptanceResource {
	private static final Logger logger = LoggerFactory.getLogger(PurchaserAgreementAcceptanceResource.class);
	
	private List<String> defaultListFields, lovFields;
	
    public PurchaserAgreementAcceptanceResource() {
        super();
        defaultListFields = Arrays.asList(new String[]{"id","purchaser","revisionDate"});
        lovFields = Arrays.asList(new String[]{"id","purchaser"});
        
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("id") Long pId , @QueryParam("domain") String pDomain) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pId != null)) {
            Object[] lKey = new Object[]{pId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        Map<String, Object> lMap = null;
        if(StringUtils.isNotEmpty(pDomain)) {
        	lMap = ClickWrapHelper.getInstance().getAgreementAttributes(null,pDomain,null,true);
        }
        pRequest.setAttribute("data", lMap);
        pRequest.getRequestDispatcher("/WEB-INF/puraggacc.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/accept")
    @RestrictedLoginRequired(resourceGroup=AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT)
    public void accept(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	ClickWrapHelper.getInstance().acceptClickWrapAgreement(pExecutionContext, (AppUserBean) lUserBean);
    }
    
    @POST
    @Produces("application/x-pdf")
    @Path("/downloadclickwrap")
    @RestrictedLoginRequired(resourceGroup=AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT)
    public Response downloadClickWrapFiles(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            String pFilter) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        ByteArrayOutputStream lByteArrayOutputStream = new ByteArrayOutputStream();
        ITextRenderer renderer = new ITextRenderer();
        String lUrl = TredsHelper.getInstance().getApplicationURL() + "puraggacc/clickwraphtml";
        lUrl += "?loginKey=" + AuthenticationHandler.getInstance().getLoginKey(pRequest);
        if (lMap.get("instId")!=null || lMap.get("domain")!=null){
        	lUrl += "&domain="+lMap.get("domain");
        }else{
        	lUrl += "&domain="+lUserBean.getDomain();
        }
        if (lMap.containsKey("getImage")){
        	lUrl += "&getImage="+lMap.get("getImage");
        }
        if (lMap.get("instId")!=null){
        	lUrl += "&inid="+lMap.get("instId");
        	lUrl += "&skipLog="+false;
        }else{
            if (lMap.containsKey("skipLog")){
            	lUrl += "&skipLog="+lMap.get("skipLog");
            }
        }
        renderer.setDocument(lUrl);
	    renderer.layout();
	    renderer.createPDF(lByteArrayOutputStream);
	    lByteArrayOutputStream.close();
	    final byte[] lPdf = lByteArrayOutputStream.toByteArray();
	    return Response.ok().header("content-disposition", "attachment; filename="+ "agreement.pdf").entity(new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lPdf);
               output.flush();
            }
        }).build();
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Path("/clickwraphtml")
    @RestrictedLoginRequired(resourceGroup=AppConstants.RESOURCEGROUP_CLICKWRAPAGREEMENT)
    public void getClickwrapHtml1(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
		@Context HttpServletResponse pResponse,@QueryParam ("domain") String pDomain,
		@QueryParam ("inid") Long pInId,@QueryParam ("skipLog") boolean pSkipLog,@QueryParam ("getImage") boolean pGetImage) throws Exception {
        AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean= null;
        if(lUserBean!=null){
        	lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        }
        if ( ClickWrapHelper.getInstance().isAgreementEnabled(lUserBean.getDomain())){
        	Map<String, Object> lMap = ClickWrapHelper.getInstance().getAgreementAttributes(pExecutionContext.getConnection(), pDomain, pInId,pSkipLog);
        	if(pGetImage){
            	lMap.put("imagePath", TredsHelper.getInstance().getApplicationURL());
            }
            pRequest.setAttribute("data", lMap);
            pRequest.getRequestDispatcher("/WEB-INF/"+(String)lMap.get("path")).forward(pRequest, pResponse);
        }else{
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        
        
    }

}