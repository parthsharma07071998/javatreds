package com.xlx.treds.instrument.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.DataType;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.ExcelHelper;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.user.bean.AppUserBean;

import groovy.json.JsonBuilder;

public class InstrumentExcelUploader extends BaseFileUploader {
    public static final String FILE_TYPE = "INSTXLS";
    private InstrumentBO instrumentBO;
    private BeanMeta instrumentBeanMeta;
    private List<String> headerFieldList;
    private Map<String, BeanFieldMeta> beanFieldMetaMap;
    private static final String PARAM_ZIPFILENAME = "imgZipFile";
    private HashMap<String, String> newFileNameHash = null;
    private Workbook returnFileWorkbook = null;
    private Sheet returnFileSheet = null;
    
    public InstrumentExcelUploader() {
        super();
        instrumentBO = new InstrumentBO();
        instrumentBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        beanFieldMetaMap = new HashMap<String, BeanFieldMeta>();
    }
    
	@Override
	public int getHeaderCount() {
		return 1;
	}

	@Override
	public boolean buildReturnFile() {
        return true;
	}

	@Override
	public String getReturnFileName(FileUploadBean pFileUploadBean){
		return "RTN_"+pFileUploadBean.getFileName();
	}

	@Override
	public String getFileStore() {
		  return FILE_TYPE;
	}

	@Override
	public String getStoreConnection() {
		// TODO Auto-generated method stub
		return super.getStoreConnection();
	}

	@Override
	public TransactionMode getTransactionMode() {
		// TODO Auto-generated method stub
		return super.getTransactionMode();
	}

	@Override
	public void onStart(FileUploadBean pFileUploadBean) throws Exception {
		// TODO Auto-generated method stub
		super.onStart(pFileUploadBean);
	}

	@Override
	public void onHeader(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
		// TODO Auto-generated method stub
		super.onHeader(pIndex, pRecord, pFileUploadBean);
	}

	@Override
	public void onHeader(int pIndex, String[] pBinaryRecord, FileUploadBean pFileUploadBean) throws Exception {
		headerFieldList = Arrays.asList(pBinaryRecord);
        Map<String, BeanFieldMeta> lFieldMap = instrumentBeanMeta.getFieldMap();
        BeanFieldMeta lBeanFieldMeta = null;
        //initializing the files that are found in header
        for (String lHeader : headerFieldList) {
            if (!lFieldMap.containsKey(lHeader))
                throw new Exception("Invalid field in header : " + lHeader);
            //create the beanFieldMeta map
            lBeanFieldMeta = lFieldMap.get(lHeader);
            beanFieldMetaMap.put(lHeader, lBeanFieldMeta);
        }
        //
        returnFileWorkbook = getWorkbook(pFileUploadBean);
        if(returnFileWorkbook!=null) {
            returnFileSheet = returnFileWorkbook.getSheetAt(0);
        }
        String[] lNewHeaders =  new String[] { "id", "status", "remarks" };
        addResponse(0, lNewHeaders);
    	//
        //For setting the filenames from the csv file to actual file names after storage.
    	Map<String, String> lFormParameters = pFileUploadBean.getFormParameters();
    	String lZipFileName = null;
    	if(lFormParameters!=null && lFormParameters.size() > 0){
    		lZipFileName = lFormParameters.get(PARAM_ZIPFILENAME);
    	}
    	if(CommonUtilities.hasValue(lZipFileName)){
            //byte[] lContent = getZipFileContent(lZipFileName);
            //if(lContent!=null){
            //    Map<String, ByteArrayOutputStream> lNameWiseFiles = CommonUtilities.unZIPAll(lContent);
            //    if(lNameWiseFiles!=null){
            //    	 newFileNameHash = saveZipFileContent(lNameWiseFiles);
            //    }
            //}
    	}
    	//
	}
	
	private void addResponse(int pRowIndex, String[] pResponse) {
		if(returnFileSheet!=null) {
	        int lLastCellIndex = headerFieldList.size()-1;
	        Cell lCell = null;
	        for(int lPtr=1; lPtr <= pResponse.length; lPtr++) {
	            lCell = returnFileSheet.getRow(pRowIndex).createCell(lLastCellIndex+lPtr);
	            lCell.setCellValue(pResponse[lPtr-1]);
	        }
		}
	}

	@Override
	public boolean onData(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
		return false;
	}
	
	private Object getFormattedValue(String pFieldName,  String pValue){
		//        STRING, INTEGER, DECIMAL, DATE, TIME, DATETIME, BOOLEAN, OBJECT
    	BeanFieldMeta lBeanFieldMeta = beanFieldMetaMap.get(pFieldName);
    	if(lBeanFieldMeta!=null){
    		if(StringUtils.isNotEmpty(pValue)){
    			if(DataType.DATE.equals(lBeanFieldMeta.getDataType())){
    				return  DateUtil.getJavaDate(Double.parseDouble(pValue));
    			}else if(DataType.DATETIME.equals(lBeanFieldMeta.getDataType())){
    				return  DateUtil.getJavaDate(Double.parseDouble(pValue));
    			}else if(DataType.TIME.equals(lBeanFieldMeta.getDataType())){
    				return  DateUtil.getJavaDate(Double.parseDouble(pValue));
    			}else if(DataType.INTEGER.equals(lBeanFieldMeta.getDataType())){
    				return new Long(pValue);
    			}else if(DataType.DECIMAL.equals(lBeanFieldMeta.getDataType())){
    				return new BigDecimal(pValue);
    			}
    		}else{
    			if(!DataType.STRING.equals(lBeanFieldMeta.getDataType())){
    				return null;
    			}
    		}
    	}
		return pValue;
	}

	@Override
	public boolean onData(int pIndex, String[] pBinaryRecord, FileUploadBean pFileUploadBean) throws Exception {
		String[] lValues = pBinaryRecord;
		Map<String, Object> lMap = new HashMap<String, Object>(); 
        int lCount = headerFieldList.size()<lValues.length?headerFieldList.size():lValues.length;
        //this is required so that the excel formatted string is converted into proper datatype readable from hash by our BeanFramework
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            lMap.put(headerFieldList.get(lPtr), getFormattedValue(headerFieldList.get(lPtr), lValues[lPtr]));
        }      
        //
        InstrumentBean lInstrumentBean = new InstrumentBean();
        Long lId = null;
        boolean lSuccess = false;
        String lMessage = null;
        List<String> lFieldList = new ArrayList<String>();
        List<String> lRemoveList = new ArrayList<String>();
        try {
        	//removing all the items that are to be fetched from Buyser/Seller Link
			String[] lTempList = new String[] { "autoConvert", "costBearingType", "buyerPercent","splittingPoint", "preSplittingCostBearer", "postSplittingCostBearer","chargeBearer","settleLeg3Flag","bidAcceptingEntityType","haircutPercent","cashDiscountPercent","cashDiscountValue" };
			for(int lPtr=0; lPtr< lTempList.length; lPtr++){
				lRemoveList.add(lTempList[lPtr]);
			}
        	for(int lPtr=0; lPtr < headerFieldList.size(); lPtr++){
        		if(!lRemoveList.contains(headerFieldList.get(lPtr))){
        			lFieldList.add(headerFieldList.get(lPtr));
        		}
        	}
        	//
        	//conversion of comma seperated keys to json
        	String lTmpFieldKey = "instrumentCreationKeys";
        	if(lFieldList.contains(lTmpFieldKey)){
            	for(int lPtr=0; lPtr < headerFieldList.size(); lPtr++){
            		if(lTmpFieldKey.equals(headerFieldList.get(lPtr))){
            			if(StringUtils.isNotEmpty(lValues[lPtr])){
                    		List<String> lTmpKeys = CommonUtilities.getDelimitedString(lValues[lPtr], ",");
                    		if(lTmpKeys!=null){
                    			lMap.put(lTmpFieldKey, new JsonBuilder(lTmpKeys).toString());
                    		}
            			}
            			break;
            		}
            	}
        	}
        	//
            List<ValidationFailBean> lValidationFailBeans = instrumentBeanMeta.validateAndParse(lInstrumentBean, lMap, null, lFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
            	//manually set the supporting file names (non-Database) since they are not set in the validateAndPares function
            	lInstrumentBean.setSup1((String)lMap.get("sup1"));
            	lInstrumentBean.setSup2((String)lMap.get("sup2"));
            	lInstrumentBean.setSup3((String)lMap.get("sup3"));
            	lInstrumentBean.setSup4((String)lMap.get("sup4"));
            	lInstrumentBean.setSup5((String)lMap.get("sup5"));
            	//
            	if(newFileNameHash!=null){
            		HashMap<String, String> lNewFileNameHash = newFileNameHash;
                    if(CommonUtilities.hasValue(lInstrumentBean.getInstImage()))
                      	lInstrumentBean.setInstImage(lNewFileNameHash.get(lInstrumentBean.getInstImage().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getCreditNoteImage()))
                      	lInstrumentBean.setCreditNoteImage(lNewFileNameHash.get(lInstrumentBean.getCreditNoteImage().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup1()))
                     	lInstrumentBean.setSup1(lNewFileNameHash.get(lInstrumentBean.getSup1().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup2()))
                     	lInstrumentBean.setSup2(lNewFileNameHash.get(lInstrumentBean.getSup2().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup3()))
                     	lInstrumentBean.setSup3(lNewFileNameHash.get(lInstrumentBean.getSup3().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup4()))
                     	lInstrumentBean.setSup4(lNewFileNameHash.get(lInstrumentBean.getSup4().toLowerCase()));
                     if(CommonUtilities.hasValue(lInstrumentBean.getSup5()))
                     	lInstrumentBean.setSup5(lNewFileNameHash.get(lInstrumentBean.getSup5().toLowerCase()));
            	}else{
                    lInstrumentBean.setInstImage(" ");
            	}
            	//
                lInstrumentBean.setFileId(pFileUploadBean.getId());
                instrumentBO.save(pFileUploadBean.getExecutionContext(), lInstrumentBean, (AppUserBean)pFileUploadBean.getAppUserBean(), lInstrumentBean.getId()==null, true, false, "", false,null,null);
                lId = lInstrumentBean.getId();
                lSuccess = true;
            }
        } catch (Exception lException) {
            lMessage = "Internal Error : " + lException.getMessage();
        }
        String[] lNewData =  new String[] { lId!=null?lId.toString():"", (lSuccess?FileUploaderFactory.RECORDSTATUS_SUCCESS:FileUploaderFactory.RECORDSTATUS_FAIL), lMessage };
        addResponse(pIndex, lNewData);
        return lSuccess;
	}

	@Override
	public void onEndOfFile(FileUploadBean pFileUploadBean) throws Exception {
		ByteArrayOutputStream lOutStream = new ByteArrayOutputStream();
		try {
			returnFileWorkbook.write(lOutStream);
			pFileUploadBean.setReturnFileBinaryContent(lOutStream.toByteArray());
	    	pFileUploadBean.setResponseBuffer(null);
		} finally {
			lOutStream.close();
		}
	}

	@Override
	public void onComplete(FileUploadBean pFileUploadBean) {
		// TODO Auto-generated method stub
		super.onComplete(pFileUploadBean);
	}

	@Override
	public boolean isBinaryFile() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List<String[]> parseBinaryFile(FileUploadBean pFileUploadBean) throws Exception {
		byte[] lFileContent = pFileUploadBean.getFileBinaryContent();
		Workbook lWorkbook = ExcelHelper.getWorkbook(lFileContent);
		ArrayList<String[]> lFileData = ExcelHelper.getExcelSheetData(lWorkbook, 0, true);
		return lFileData;
	}

	@Override
	public String getFileSubPath(FileUploadBean pFileUploadBean) throws Exception {
		// TODO Auto-generated method stub
		return super.getFileSubPath(pFileUploadBean);
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return super.isSecure();
	}
	
	
	private Workbook getWorkbook(FileUploadBean pFileUploadBean) {
		try {
	        pFileUploadBean.setReturnFileBinaryContent(pFileUploadBean.getFileBinaryContent());
			if(pFileUploadBean!=null && pFileUploadBean.getReturnFileBinaryContent()!=null) {
				return ExcelHelper.getWorkbook(pFileUploadBean.getReturnFileBinaryContent());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
   
}
