package com.xlx.treds.entity.rest;

import java.util.Arrays;
import java.util.List;

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
import com.xlx.treds.entity.bean.AlertNotificationSettingBean;
import com.xlx.treds.entity.bo.AlertNotificationSettingBO;

import groovy.json.JsonBuilder;

@Path("/alrtnot")
public class AlertNotificationSettingResource {

    private AlertNotificationSettingBO alertNotificationSettingBO;
    private BeanMeta alertNotificationSettingBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public AlertNotificationSettingResource() {
        super();
        alertNotificationSettingBO = new AlertNotificationSettingBO();
        alertNotificationSettingBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AlertNotificationSettingBean.class);
        defaultListFields = Arrays.asList(new String[]{"notificationType","smsMessageType","emailMessageType"});
        lovFields = Arrays.asList(new String[]{"notificationType"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
        , @QueryParam("new") Boolean pNew , @QueryParam("notificationType") String pNotificationType) throws Exception {
        if (pNew != null)
            pRequest.setAttribute(CommonAppConstants.PARAM_NEW, pNew);
        else if ((pNotificationType != null)) {
            Object[] lKey = new Object[]{pNotificationType};
            String lModify = new JsonBuilder(lKey).toString();
            pRequest.setAttribute(CommonAppConstants.PARAM_MODIFY, lModify);
        }
        pRequest.getRequestDispatcher("/WEB-INF/alrtnot.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{notificationType}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("notificationType") String pNotificationType) throws Exception {
        AlertNotificationSettingBean lFilterBean = new AlertNotificationSettingBean();
        lFilterBean.setNotificationType(pNotificationType);
        AlertNotificationSettingBean lAlertNotificationSettingBean = alertNotificationSettingBO.findBean(pExecutionContext, lFilterBean);
        return alertNotificationSettingBeanMeta.formatAsJson(lAlertNotificationSettingBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        return alertNotificationSettingBO.getNotificationSettingJson(pExecutionContext, lUserBean);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AlertNotificationSettingBean lAlertNotificationSettingBean = new AlertNotificationSettingBean();
        List<ValidationFailBean> lValidationFailBeans = alertNotificationSettingBeanMeta.validateAndParse(lAlertNotificationSettingBean, 
            pMessage, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        alertNotificationSettingBO.save(pExecutionContext, lAlertNotificationSettingBean, lUserBean);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured
    @Path("/{notificationType}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("notificationType") String pNotificationType) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AlertNotificationSettingBean lFilterBean = new AlertNotificationSettingBean();
        lFilterBean.setNotificationType(pNotificationType);
        alertNotificationSettingBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

}