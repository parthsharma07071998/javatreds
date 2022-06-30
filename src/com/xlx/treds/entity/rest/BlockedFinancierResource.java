package com.xlx.treds.entity.rest;

import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bo.AppEntityBO;

@Singleton
@Path("/blockfin")
public class BlockedFinancierResource {

    private AppEntityBO appEntityBO;
    private BeanMeta appEntityBeanMeta;
	
    public BlockedFinancierResource() {
        super();
        appEntityBO = new AppEntityBO();
        appEntityBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(AppEntityBean.class);
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse
         , @QueryParam("code") String pCode) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/blockfin.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="blockfin-view")
    @Path("/{code}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("code") String pCode) throws Exception {
    	IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lFilterBean = new AppEntityBean();
        lFilterBean.setCode(lUserBean.getDomain());
        AppEntityBean lAppEntityBean = appEntityBO.findBean(pExecutionContext, lFilterBean);
        return appEntityBeanMeta.formatAsJson(lAppEntityBean,AppEntityBean.FIELDGROUP_UPDATEBLOCKEDFINANCIERS, null, false);
    }


    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="blockfin-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        AppEntityBean lAppEntityBean = new AppEntityBean();
        List<ValidationFailBean> lValidationFailBeans = appEntityBeanMeta.validateAndParse(lAppEntityBean, 
            pMessage, AppEntityBean.FIELDGROUP_UPDATEBLOCKEDFINANCIERS, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        appEntityBO.updateBlockedFinanciers(pExecutionContext, lAppEntityBean, lUserBean);
    }
    

}