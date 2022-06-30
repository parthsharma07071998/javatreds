
package com.xlx.treds.instrument.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
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
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
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
import com.xlx.commonn.report.ReportConvertorFactory;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.MemberLocationForInstKeysBean;
import com.xlx.treds.instrument.bo.MemberLocationForInstKeysBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/memberlocforinstkeys")
@Singleton
public class MemberLocationForInstKeysResource {

	public static final Logger logger = LoggerFactory.getLogger(MemberLocationForInstKeysResource.class);

    private MemberLocationForInstKeysBO memberLocationForInstKeysBO;
    private BeanMeta memberLocationForInstKeysBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public MemberLocationForInstKeysResource() {
        super();
        memberLocationForInstKeysBO = new MemberLocationForInstKeysBO();
        memberLocationForInstKeysBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(MemberLocationForInstKeysBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","clId","gstn"});
        lovFields = Arrays.asList(new String[]{"code","clId"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode, @QueryParam("clId") Long pClId) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null) && (pClId != null)) {
            Object[] lKey = new Object[]{pCode, pClId};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/memberlocforinstkeys.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "memberlocforinstkeys-view")
    @Path("/{code}/{clId}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode, @PathParam("clId") Long pClId) throws Exception {
        MemberLocationForInstKeysBean lFilterBean = new MemberLocationForInstKeysBean();
        lFilterBean.setCode(pCode);
        lFilterBean.setClId(pClId);
        MemberLocationForInstKeysBean lMemberLocationForInstKeysBean = memberLocationForInstKeysBO.findBean(pExecutionContext, lFilterBean);
        return memberLocationForInstKeysBeanMeta.formatAsJson(lMemberLocationForInstKeysBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey = "memberlocforinstkeys-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        MemberLocationForInstKeysBean lFilterBean = new MemberLocationForInstKeysBean();
        memberLocationForInstKeysBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<MemberLocationForInstKeysBean> lMemberLocationForInstKeysList = memberLocationForInstKeysBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (MemberLocationForInstKeysBean lMemberLocationForInstKeysBean : lMemberLocationForInstKeysList) {
            lResults.add(memberLocationForInstKeysBeanMeta.formatAsArray(lMemberLocationForInstKeysBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Secured(secKey = "memberlocforinstkeys-view")
    @Path("/download/{format}")
    public Response download(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter, @PathParam("format") ReportConvertorFactory.DownloadFormat pFormat) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        MemberLocationForInstKeysBean lFilterBean = new MemberLocationForInstKeysBean();
        memberLocationForInstKeysBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<MemberLocationForInstKeysBean> lMemberLocationForInstKeysList = memberLocationForInstKeysBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        FileDownloadBean lFileDownloadBean = ReportConvertorFactory.getInstance().convertData(memberLocationForInstKeysBeanMeta.formatBeansAsList(lMemberLocationForInstKeysList, null, 
        		lFields, true), "MemberLocationForInstKeys", memberLocationForInstKeysBeanMeta.getFieldLabelList(null, lFields).toArray(new String[0]), pFormat);
        
        return lFileDownloadBean.getResponseForSendFile();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/lov")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return new JsonBuilder(memberLocationForInstKeysBO.lov(pExecutionContext, lovFields, lUserBean)).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "memberlocforinstkeys-manage")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "memberlocforinstkeys-manage")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        MemberLocationForInstKeysBean lMemberLocationForInstKeysBean = new MemberLocationForInstKeysBean();
        JsonSlurper lJSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJSlurper.parseText(pMessage);
        memberLocationForInstKeysBeanMeta.validateAndParse(lMemberLocationForInstKeysBean, lMap, null);
        lMemberLocationForInstKeysBean.setCode(lUserBean.getDomain());
        lMemberLocationForInstKeysBean.getLocations();
        memberLocationForInstKeysBO.save(pExecutionContext, lMemberLocationForInstKeysBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey = "memberlocforinstkeys-manage")
    @Path("/{code}/{clId}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode, @PathParam("clId") Long pClId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        MemberLocationForInstKeysBean lFilterBean = new MemberLocationForInstKeysBean();
        lFilterBean.setCode(pCode);
        lFilterBean.setClId(pClId);
        memberLocationForInstKeysBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }
    
    
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getlocations")
    public String getLocations(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
    	AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	MemberLocationForInstKeysBean lFilterBean = new MemberLocationForInstKeysBean();
    	lFilterBean.setCode(lUserBean.getDomain());
    	List<MemberLocationForInstKeysBean> lList = memberLocationForInstKeysBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
    	HashMap<String, Object> lMap = new HashMap<>();
    	ArrayList<String> lArrList = new ArrayList<>();
    	lMap.put("value", lArrList);
    	for(MemberLocationForInstKeysBean lBean : lList){
    		lArrList.add(lBean.getKey());
    	}
    	return new JsonBuilder(lMap).toString();
    }
    
    

    
    

}