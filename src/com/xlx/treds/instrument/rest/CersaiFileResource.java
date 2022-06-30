package com.xlx.treds.instrument.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
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

import org.apache.commons.lang.StringUtils;

import com.xlx.common.utilities.CommonUtilities;
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
import com.xlx.commonn.bo.FileUploadHelper;
import com.xlx.commonn.other.bean.FileDownloadBean;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.CersaiFileBean;
import com.xlx.treds.instrument.bo.CersaiFileBO;
import com.xlx.treds.instrument.bo.CersaiFileGenerator;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

@Path("/cersaifile")
public class CersaiFileResource {

    private static String FIELD_SEPERATOR = "|";
    private static String LINE_SEPERATOR = "\r\n";
    private static String COMMA = ",";
    private static String DOUBLEQUOTE = "\"";

    private CersaiFileBO cersaiFileBO;
    private BeanMeta cersaiFileBeanMeta;
	private List<String> defaultListFields, lovFields;
	
    public CersaiFileResource() {
        super();
        cersaiFileBO = new CersaiFileBO();
        cersaiFileBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(CersaiFileBean.class);
        defaultListFields = Arrays.asList(new String[]{"id","date","financier","fileName","recordCount","generatedByAuId","generatedTime"});
        lovFields = Arrays.asList(new String[]{"id","date"});
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
        pRequest.getRequestDispatcher("/WEB-INF/cersaifile.jsp").forward(pRequest, pResponse);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-view")
    @Path("/{id}")
    public String get(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        CersaiFileBean lFilterBean = new CersaiFileBean();
        lFilterBean.setId(pId);
        CersaiFileBean lCersaiFileBean = cersaiFileBO.findBean(pExecutionContext, lFilterBean);
        return cersaiFileBeanMeta.formatAsJson(lCersaiFileBean);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-view")
    @Path("/all")
    public String list(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pFilter) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CersaiFileBean lFilterBean = new CersaiFileBean();
        cersaiFileBeanMeta.validateAndParse(lFilterBean, lMap, null, null);

        List<String> lFields = (List<String>)lMap.get(BeanMeta.PARAM_COLUMNNAMES);
        if (lFields == null) lFields = defaultListFields;
        List<CersaiFileBean> lCersaiFileList = cersaiFileBO.findList(pExecutionContext, lFilterBean, lFields, lUserBean);
        List<Object[]> lResults = new ArrayList<Object[]>();
        for (CersaiFileBean lCersaiFileBean : lCersaiFileList) {
            lResults.add(cersaiFileBeanMeta.formatAsArray(lCersaiFileBean, null, lFields, true));            
        }
        return new JsonBuilder(lResults).toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-view")
    @Path("/all")
    public String lov(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        List<CersaiFileBean> lCersaiFileList = cersaiFileBO.findList(pExecutionContext, null, lovFields, lUserBean);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        for (CersaiFileBean lCersaiFileBean : lCersaiFileList) {
            Map<String, Object> lData = new HashMap<String, Object>();
            lData.put(BeanFieldMeta.JSONKEY_VALUE, lCersaiFileBean.getId());
            lData.put(BeanFieldMeta.JSONKEY_TEXT, lCersaiFileBean.getDate());
            lResults.add(lData);
        }
        return new JsonBuilder(lResults).toString();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-save")
    public void insert(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, true);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-save")
    public void update(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage) throws Exception {
        save(pExecutionContext, pRequest, pMessage, false);
    }
    
    private void save(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest, 
        String pMessage, boolean pNew) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CersaiFileBean lCersaiFileBean = new CersaiFileBean();
        List<ValidationFailBean> lValidationFailBeans = cersaiFileBeanMeta.validateAndParse(lCersaiFileBean, 
            pMessage, pNew ? BeanMeta.FIELDGROUP_INSERT : BeanMeta.FIELDGROUP_UPDATE, null);
        if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0))
            throw new CommonValidationException(lValidationFailBeans);
        cersaiFileBO.save(pExecutionContext, lCersaiFileBean, lUserBean, pNew);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-save")
    @Path("/{id}")
    public void delete(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
        , @PathParam("id") Long pId) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CersaiFileBean lFilterBean = new CersaiFileBean();
        lFilterBean.setId(pId);
        cersaiFileBO.delete(pExecutionContext, lFilterBean, lUserBean);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-save")
    @Path("/checkGeneration/{factorDate}")
    public String checkGeneration(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("factorDate") String pFactorDate) throws Exception {
        IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        CersaiFileBean lFilterBean = new CersaiFileBean();
        Date lFactorDate = new Date(new SimpleDateFormat("dd-MMM-yyyy").parse(pFactorDate).getTime());  
        AppEntityBean lEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
        if(lEntityBean==null){
        	throw new CommonBusinessException("Loggedin entity not found.");
        }
        if(!lEntityBean.isFinancier()){
        	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        }
        lFilterBean.setFileName(lUserBean.getDomain());
        lFilterBean.setDate(lFactorDate);
        List<CersaiFileBean> lCersaiFileList = cersaiFileBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
        String lMessage = "";
        if(lCersaiFileList!=null && !lCersaiFileList.isEmpty()){
        	int lFileCount = lCersaiFileList.size();
        	lMessage = lFileCount + " cersai file" + (lFileCount>0?"s":"") + " exist for the factor date " + pFactorDate.toString();
        }
        Map<Integer,Integer> lPendingCount =  cersaiFileBO.getCersaiFileInstrumentCounts(pExecutionContext.getConnection(), lUserBean.getDomain(), lFactorDate);
        List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
        Map<String, Object> lInfoMsg = new HashMap<String, Object>();
    	String lPrevDataMsg = "", lNewDataMsg = "";
    	int lNewCount = 0;
        if(lPendingCount!=null && lPendingCount.size() > 0){
        	for(int lFileId : lPendingCount.keySet()){
        		if(lFileId > 0){
            		if(StringUtils.isNotEmpty(lPrevDataMsg)) {
            			lPrevDataMsg += ", ";
            		}
            		lPrevDataMsg += " File " + lFileId + " has " +  lPendingCount.get(lFileId) + " records";
        		}
        		if(lFileId == 0){
        			lNewCount = lPendingCount.get(lFileId);
        		}
        	}
        	if(lNewCount==0){
        		lNewDataMsg = "No data found for new file genration.";
        	}else{
        		lNewDataMsg = lNewCount +  " records found for new file generation.";
        	}
        }else{
    		lNewDataMsg = "No data found for new file genration.";
        }        
    	lInfoMsg.put("message", lPrevDataMsg + "  " + lNewDataMsg);
    	lInfoMsg.put("newCount",lNewCount);
    	lResults.add(lInfoMsg);
        return new JsonBuilder(lResults).toString();
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(secKey="cersaifiles-save")
    @Path("/generate/{factorDate}")
    public String generate(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest
    		, @PathParam("factorDate") String pFactorDate) throws Exception {
    	 IAppUserBean lUserBean = AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
         CersaiFileBean lFilterBean = new CersaiFileBean();
         Date lFactorDate = new Date(new SimpleDateFormat("dd-MMM-yyyy").parse(pFactorDate).getTime());  
         AppEntityBean lEntityBean = TredsHelper.getInstance().getAppEntityBean(lUserBean.getDomain());
         if(lEntityBean==null){
         	throw new CommonBusinessException("Loggedin entity not found.");
         }
         if(!lEntityBean.isFinancier()){
         	throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
         }
         lFilterBean.setFinancier(lUserBean.getDomain());
         lFilterBean.setDate(lFactorDate);
         List<CersaiFileBean> lCersaiFileList = cersaiFileBO.findList(pExecutionContext, lFilterBean, null, lUserBean);
         String lMessage = "";
         List<Map<String, Object>> lResults = new ArrayList<Map<String, Object>>();
         Map<String, Object> lMessageHash = new HashMap<>();
         if(lCersaiFileList!=null && !lCersaiFileList.isEmpty()){
         	int lFileCount = lCersaiFileList.size();
         	lMessage = lFileCount + " cersai file" + (lFileCount>0?"s":"") + " exist for the factor date " + pFactorDate.toString();
         	//TODO: add message to result
         }
         pExecutionContext.setAutoCommit(false);
         List<String> lCersaiFileNames =  cersaiFileBO.updateInstruments(pExecutionContext.getConnection(), (AppUserBean) lUserBean, lFactorDate);
         if(lCersaiFileNames!=null ) { //&& lCFIds.size() > 0){
        	 if (!lMessage.isEmpty()){
        		 lMessage += ",  ";
        	 }
        	 lMessage += lCersaiFileNames.size() + " file/s generated with file name/s " ;
        	 for(int lPtr=0; lPtr < lCersaiFileNames.size(); lPtr++){
        		 if(lPtr!=0){
        			 lMessage += ", ";
        		 }
        		 lMessage += lCersaiFileNames.get(lPtr);
        		 if(lPtr+1 == lCersaiFileNames.size()){
        			 lMessage += ".";
        		 }
        	 }
          	//TODO: add message to result
        	 lMessageHash.put("message", lMessage);
        	 lResults.add(lMessageHash);
         }
         pExecutionContext.commitAndDispose();
         return new JsonBuilder(lResults).toString();
    }
    
    @POST
    @Secured(secKey="cersaifiles-save")
    @Path("/download")
    public Object listDownload(@Context ExecutionContext pExecutionContext, @Context HttpServletRequest pRequest,
    		String pFilter) throws Exception {
    	String lContentType = "application/octet-stream";
        AppUserBean lUserBean = (AppUserBean)AuthenticationHandler.getInstance().getLoggedInUserBean(pRequest);
        JsonSlurper lJsonSlurper = new JsonSlurper();
        Map<String, Object> lMap = (Map<String, Object>)lJsonSlurper.parseText(pFilter);
        CersaiFileBean lFilterBean = new CersaiFileBean();
        cersaiFileBeanMeta.validateAndParse(lFilterBean, lMap, null, null);
        boolean lConvertToCSV = false;
        if(lMap.containsKey("csv")){
        	lConvertToCSV = (boolean) lMap.get("csv");
        }
        CersaiFileBean lCfBean =  cersaiFileBO.findBean(pExecutionContext, lFilterBean);
        byte[] lFileData = FileUploadHelper.readFile(lCfBean.getStorageFileName(), null, "CERSAIFILES");
        byte[] lResultData = lFileData;
        if(lConvertToCSV){
            lResultData = getDataForExcel(lFileData, FIELD_SEPERATOR);
        }
        String lFileName = lCfBean.getFileName();
        if(lConvertToCSV) {
        	lFileName+=".csv";
        	lContentType = "text/csv";
        }else{
        	lFileName+=".txt";
        }
        return new FileDownloadBean(lFileName, lResultData, lContentType).getResponseForSendFile();
    }
    
    private byte[] getDataForExcel(byte[] pFileData, String pFieldSeperator){
    	byte[] lResultData = null;
    	//TODO: TO ADD HEADER LOGIC HERE
        if(pFileData != null){
        	try(ByteArrayInputStream lInputStream = new ByteArrayInputStream(pFileData);
        		BufferedReader bfReader = new BufferedReader(new InputStreamReader(lInputStream));){
        		String lTemp = null;
        		String[] lTempArr = null;
        		StringBuilder lResult = new StringBuilder();
        		int lPtr = 0;
        		int lLine = 0;
        		String lCurrHeaderCode =null, lPrevHeaderCode = null;
                while((lTemp = bfReader.readLine()) != null){
                	lTempArr = CommonUtilities.splitString(lTemp,pFieldSeperator);
                	if(lLine > 0){
                    	lResult.append(LINE_SEPERATOR);
                	}
                	//
                	if(lTempArr.length > 0) {
                		lPtr=0;
                    	lCurrHeaderCode = lTempArr[0];
                    	if(!lCurrHeaderCode.equalsIgnoreCase(lPrevHeaderCode)) {
                    		//add header text
                    		String[] lTmpHeader = CersaiFileGenerator.getMappedHeaders(lCurrHeaderCode);
                    		if(lTmpHeader!=null) {
                            	for(String lTemp2 : lTmpHeader ){
                            		if(lPtr > 0){
                            			lResult.append(COMMA);
                            		}
                            		lResult.append(DOUBLEQUOTE).append(lPtr==0?"HEADER_":"").append(lTemp2).append(DOUBLEQUOTE);
                            		lPtr++;
                            	}
                            	lResult.append(LINE_SEPERATOR);
                    		}
                    	}
                	}
                	//
                	lPtr = 0;
                	for(String lTemp2 : lTempArr ){
                		if(lPtr > 0){
                			lResult.append(COMMA);
                		}
                		lResult.append(DOUBLEQUOTE).append(lTemp2).append(DOUBLEQUOTE);
                		lPtr++;
                	}
                	//
                	lLine++;
                	lPrevHeaderCode = lCurrHeaderCode;
                }
                lResultData = lResult.toString().getBytes();
        	}catch(Exception lEx){
                lEx.printStackTrace();
        	}
        }
        return lResultData;
    }
} 