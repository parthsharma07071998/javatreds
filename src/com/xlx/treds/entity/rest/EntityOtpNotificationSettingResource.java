package com.xlx.treds.entity.rest;

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
import com.xlx.treds.AppConstants;
import com.xlx.treds.entity.bean.EntityOtpNotificationSettingBean;
import com.xlx.treds.entity.bo.EntityOtpNotificationSettingBO;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/entotpnot")
public class EntityOtpNotificationSettingResource {

    private EntityOtpNotificationSettingBO entityOtpNotificationSettingBO;
    private BeanMeta entityOtpNotificationSettingBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public EntityOtpNotificationSettingResource() {
        super();
        entityOtpNotificationSettingBO = new EntityOtpNotificationSettingBO();
        entityOtpNotificationSettingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(EntityOtpNotificationSettingBean.class);
        defaultListFields = Arrays.asList(new String[]{"code","notificationType","enabled"});
        lovFields = Arrays.asList(new String[]{"code","notificationType"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("code") String pCode, @QueryParam("notificationType") String pNotificationType ,@QueryParam("name") String pName) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pCode != null) && (pNotificationType != null)) {
            Object[] lKey = new Object[]{pCode, pNotificationType};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.setAttribute("code", pCode);
        pRequest.setAttribute("name", pName);
        pRequest.getRequestDispatcher("/WEB-INF/entotpnot.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
    	String lEntityCode = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
	    	JsonSlurper lJsonSlurper = new JsonSlurper();
    		Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
			if( lMap!=null && lMap.size() >0 ){
	    		lEntityCode = (String)lMap.get("code");
			}
    	}
        return entityOtpNotificationSettingBO.getNotificationSettingJson(pExecutionContext, lUserBean ,lEntityCode);
    }

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
    	String lEntityCode = null;
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	if (AppConstants.DOMAIN_PLATFORM.equals(lUserBean.getDomain())){
	    	JsonSlurper lJsonSlurper = new JsonSlurper();
    		Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pMessage);
			if( lMap!=null && lMap.size() >0 ){
	    		lEntityCode = (String)lMap.get("code");
			}
    	}else{
    		lEntityCode = lUserBean.getDomain();
    	}
        EntityOtpNotificationSettingBean lEntityOtpNotificationSettingBean = new EntityOtpNotificationSettingBean();
        List<ValidationFailBean> lValidationFailBeans = entityOtpNotificationSettingBeanMeta.validateAndParse(lEntityOtpNotificationSettingBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        //
        lEntityOtpNotificationSettingBean.setCode(lEntityCode);
        
        EntityOtpNotificationSettingBean lOldENSBean = null;
        EntityOtpNotificationSettingBean lFilterBean = new EntityOtpNotificationSettingBean();
        lFilterBean.setCode(lEntityCode);
        lFilterBean.setNotificationType(lEntityOtpNotificationSettingBean.getNotificationType());
        lOldENSBean = entityOtpNotificationSettingBO.findBean(pExecutionContext.getConnection(), lFilterBean);
        if(lOldENSBean!=null){
        	lOldENSBean.setSmsMessageType(lEntityOtpNotificationSettingBean.getSmsMessageType());
        	lOldENSBean.setMobile(lEntityOtpNotificationSettingBean.getMobile());
        	lOldENSBean.setEmailMessageType(lEntityOtpNotificationSettingBean.getEmailMessageType());
        	lOldENSBean.setEmail(lEntityOtpNotificationSettingBean.getEmail());
        	lEntityOtpNotificationSettingBean = lOldENSBean;
        	pNew = false;
        }
        else{
        	pNew = true;
        }
        entityOtpNotificationSettingBO.save(pExecutionContext, lEntityOtpNotificationSettingBean, lUserBean, pNew ,lEntityCode);
    }

}