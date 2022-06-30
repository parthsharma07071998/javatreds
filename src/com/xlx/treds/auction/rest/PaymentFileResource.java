package com.xlx.treds.auction.rest;

import java.io.IOException;
import java.io.OutputStream;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.AuthenticationHandler;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonValidationException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.Secured;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.IPaymentInterface;
import com.xlx.treds.auction.bean.PaymentFileBean;
import com.xlx.treds.auction.bean.PaymentInterfaceFactory;
import com.xlx.treds.auction.bo.PaymentFileBO;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Singleton
@Path("/payfile")
public class PaymentFileResource {

    private PaymentFileBO paymentFileBO;
    private BeanMeta paymentFileBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public PaymentFileResource() {
        super();
        paymentFileBO = new PaymentFileBO();
        paymentFileBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(PaymentFileBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","date","fileType","facilitator","fileName","recordCount","totalValue","generatedByAuId","generatedTime","status","returnUploadedByAuId","returnUploadedTime"});
        lovFields = Arrays.asList(new String[]{"id","date"});
    }
    
    @GET
    @Produces(MediaType.TEXT_HTML)
    public void page(@Context HttpServletRequest pRequest, @Context HttpServletResponse pResponse) throws Exception {
        pRequest.getRequestDispatcher("/WEB-INF/payfile.jsp").forward(pRequest, pResponse);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="payfile-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        PaymentFileBean lFilterBean = new PaymentFileBean();
        paymentFileBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<PaymentFileBean> lPaymentFileList = paymentFileBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);

        List<Object[]> lResults = new ArrayList<Object[]>();
        for (PaymentFileBean lPaymentFileBean : lPaymentFileList) {
            lResults.add(paymentFileBeanMeta.formatAsArray(lPaymentFileBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="payfile-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PaymentFileBean lPaymentFileBean = new PaymentFileBean();
        List<ValidationFailBean> lValidationFailBeans = paymentFileBeanMeta.validateAndParse(lPaymentFileBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        paymentFileBO.save(pExecutionContext, lPaymentFileBean, lUserBean, pNew);
        

    }

    @GET
    @Secured(secKey="payfile-save")
    @Path("/contents/{id}")
    public Response contents(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PaymentFileBean lFilterBean = new PaymentFileBean();
        lFilterBean.setId(pId);
        PaymentFileBean lPaymentFileBean = paymentFileBO.getFileContents(pExecutionContext, lFilterBean, lUserBean);
        final String lContents = lPaymentFileBean.getContents();
        StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lContents.getBytes());
               output.flush();
            }
        };
        return Response.ok(lStreamingOutput).header("content-disposition", "attachment; filename="+ lPaymentFileBean.getFileName()).build();
    }
    
    @GET
    @Secured(secKey="payfile-save")
    @Path("/processFile/{id}")
    public void processFile(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        @PathParam("id") Long pId) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        PaymentFileBean lFilterBean = new PaymentFileBean();
        lFilterBean.setId(pId);
        PaymentFileBean lPaymentFileBean = paymentFileBO.findBean(pExecutionContext, lFilterBean);
        IPaymentInterface lPaymentImpl = PaymentInterfaceFactory.getPaymentInterface(lPaymentFileBean.getFacilitator());
        lPaymentImpl.processUploadedReturnFile(pExecutionContext.getConnection(), lPaymentFileBean, lUserBean);
    }
    
    @GET
    @Secured(secKey="payfile-save")
    @Path("/settlementFile/{date}")
    public Response settlementFile(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        @PathParam("date") String pDate) throws Exception {
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        IPaymentInterface lPaymentImpl = PaymentInterfaceFactory.getPaymentInterface(AppConstants.FACILITATOR_NPCI);
        byte[] lContent  = lPaymentImpl.createExcellFile(pExecutionContext, CommonUtilities.getDate(pDate, "dd-MM-yyyy"),lUserBean);
    	String lContentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        StreamingOutput lStreamingOutput = new StreamingOutput(){
            @Override
            public void write(OutputStream output)
               throws IOException, WebApplicationException {
               output.write(lContent);
               output.flush();
            }
        };
        return Response.ok(lStreamingOutput, lContentType).header("content-disposition", "attachment; filename = "+"SettlementFile"+pDate).build();
    }
    @GET
    @Path("/paymentFileInfo/{id}")
    public void paymentFileInfo(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            @PathParam("id") Long pId) throws Exception{
    	AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	paymentFileBO.getPaymentFileMessage(pExecutionContext.getConnection(), pId);
    }
    
/*    @GET
    @Path("/paymentFileInfo/{id}")
    public String paymentFileInfo(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
            @PathParam("id") Long pId) throws Exception{
    	AppUserBean lUserBean = (AppUserBean) AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
    	Map<String, Object> lResultMap = paymentFileBO.getPaymentFileMessage(pExecutionContext.getConnection(), pId);
    	return new JsonBuilder(lResultMap).toString();
    }
*/

    
}