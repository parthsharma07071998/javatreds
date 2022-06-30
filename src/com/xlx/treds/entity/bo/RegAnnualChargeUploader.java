package com.xlx.treds.entity.bo;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
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

import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanFieldMeta.DataType;
import com.xlx.commonn.bean.BeanFieldMeta.ValidationFailBean;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.commonn.bo.FileUploaderFactory;
import com.xlx.treds.ExcelHelper;
import com.xlx.treds.entity.bean.RegistrationChargeBean;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ApprovalStatus;
import com.xlx.treds.entity.bean.RegistrationChargeBean.ChargeType;
import com.xlx.treds.entity.bean.RegistrationChargeBean.RequestType;
import com.xlx.treds.user.bean.AppUserBean;

public class RegAnnualChargeUploader extends BaseFileUploader {
    public static final String FILE_TYPE = "ANNFXLS";
    private RegistrationChargeBO registrationChargeBO;
    private GenericDAO<RegistrationChargeBean> registrationChargeDAO;
    private BeanMeta registrationChargeBeanMeta;
    private List<String> headerFieldList;
    private Map<String, BeanFieldMeta> beanFieldMetaMap;
    private Workbook returnFileWorkbook = null;
    private Sheet returnFileSheet = null;
    
    public RegAnnualChargeUploader() {
        super();
        registrationChargeBO = new RegistrationChargeBO();
        registrationChargeDAO = new GenericDAO<RegistrationChargeBean>(RegistrationChargeBean.class);
        registrationChargeBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(RegistrationChargeBean.class);
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
		return super.getStoreConnection();
	}

	@Override
	public TransactionMode getTransactionMode() {
		return super.getTransactionMode();
	}

	@Override
	public void onStart(FileUploadBean pFileUploadBean) throws Exception {
		super.onStart(pFileUploadBean);
	}

	@Override
	public void onHeader(int pIndex, String pRecord, FileUploadBean pFileUploadBean) throws Exception {
		super.onHeader(pIndex, pRecord, pFileUploadBean);
	}

	@Override
	public void onHeader(int pIndex, String[] pBinaryRecord, FileUploadBean pFileUploadBean) throws Exception {
		//HEADERS : entityCode,paymentDate,paymentAmount,paymentRefrence,billedEntityCode
		headerFieldList = Arrays.asList(pBinaryRecord);
        Map<String, BeanFieldMeta> lFieldMap = registrationChargeBeanMeta.getFieldMap();
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
		//HEADERS : entityCode,paymentDate,paymentAmount,paymentRefrence,billedEntityCode
		String[] lValues = pBinaryRecord;
		Map<String, Object> lMap = new HashMap<String, Object>(); 
        int lCount = headerFieldList.size()<lValues.length?headerFieldList.size():lValues.length;
        //this is required so that the excel formatted string is converted into proper datatype readable from hash by our BeanFramework
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            lMap.put(headerFieldList.get(lPtr), getFormattedValue(headerFieldList.get(lPtr), lValues[lPtr]));
        }      
        //
        RegistrationChargeBean lRegistrationChargeBean = new RegistrationChargeBean();
        Long lId = null;
        boolean lSuccess = false;
        String lMessage = null;
        //
        try(Connection lConnection = DBHelper.getInstance().getConnection()) {
            List<ValidationFailBean> lValidationFailBeans = registrationChargeBeanMeta.validateAndParse(lRegistrationChargeBean, lMap, null, headerFieldList, null);
            if ((lValidationFailBeans != null) && (lValidationFailBeans.size() > 0)) {
                ValidationFailBean lValidationFailBean = lValidationFailBeans.get(0);
                lMessage = lValidationFailBean.getName() + ":" + lValidationFailBean.getMessage();
                if (lValidationFailBeans.size() > 1)
                    lMessage += ". " + (lValidationFailBeans.size() - 1) + " more errors";
            } else {
            	if(StringUtils.isEmpty(lRegistrationChargeBean.getEntityCode())) {
            		throw new CommonBusinessException("Entity Code mandatory.");
            	}
            	if(lRegistrationChargeBean.getRequestType() == null) {
            		throw new CommonBusinessException("RequestType is mandatory.");
            	}
            	if(lRegistrationChargeBean.getChargeType() == null) {
            		lRegistrationChargeBean.setChargeType(ChargeType.Annual);
            	}
            	if(ChargeType.Annual.equals(lRegistrationChargeBean.getChargeType()) && 
            			( lRegistrationChargeBean.getAnnualFeeYear() == null || lRegistrationChargeBean.getAnnualFeeYear().equals(new Long(0))) )  {
            		throw new CommonBusinessException("For Annual charge, fee year is mandatory.");
            	}
            	//clearing unwanted data
            	if(RequestType.Extenstion.equals(lRegistrationChargeBean.getRequestType())) {
    				//if waiver then payemnt details are null
            		lRegistrationChargeBean.setPaymentAmount(null);
            		lRegistrationChargeBean.setPaymentDate(null);
            		lRegistrationChargeBean.setPaymentRefrence(null);
    			}else if(RequestType.Payment.equals(lRegistrationChargeBean.getRequestType())) {
    				if (lRegistrationChargeBean.getPaymentDate()== null) {
    					throw new CommonBusinessException("Payment Date cannot be null");
    				}
    				if (lRegistrationChargeBean.getPaymentAmount()== null) {
    					throw new CommonBusinessException("Payment Amount cannot be null");
    				}
    				if (lRegistrationChargeBean.getPaymentRefrence()== null) {
    					throw new CommonBusinessException("Payment Ref no cannot be null");
    				}
    				lRegistrationChargeBean.setExtendedDate(null);
    				lRegistrationChargeBean.setExtensionCount(null);    				
    			}else if(RequestType.Waiver.equals(lRegistrationChargeBean.getRequestType())) {
    				//if waiver then payemnt details are null
    				lRegistrationChargeBean.setPaymentAmount(null);
    				lRegistrationChargeBean.setPaymentDate(null);
    				lRegistrationChargeBean.setPaymentRefrence(null);
    				//
    				lRegistrationChargeBean.setExtendedDate(null);
    				lRegistrationChargeBean.setExtensionCount(null);
    				//
    				lRegistrationChargeBean.setBilledEntityCode(null);
    				lRegistrationChargeBean.setBilledEntityClId(null);
    			}
            	AppUserBean lAppUserBean = (AppUserBean) pFileUploadBean.getAppUserBean();
            	//first create the charge
            	if(lRegistrationChargeBean.getAnnualFeeYear()==null) {
            		throw new CommonBusinessException("Annual fee year mandatory.");
            	}
            	Date lEffectiveDate = registrationChargeBO.getAnnualFeeDate(lRegistrationChargeBean.getEntityCode(),lRegistrationChargeBean.getAnnualFeeYear().intValue());
            	//first create the charge
            	RegistrationChargeBean lRCBean = registrationChargeBO.createCharge( lConnection, lRegistrationChargeBean.getEntityCode(), lAppUserBean,ChargeType.Annual, lEffectiveDate );
            	if(lRCBean != null) {
            		//then update the charge
            		registrationChargeBeanMeta.copyBean(lRegistrationChargeBean, lRCBean, null, headerFieldList);
            		//
            		//setting the bill to zero so that bill is not generated for the same.
            		lRCBean.setBillId(new Long(0));
            		lRCBean.setMakerAuId(lAppUserBean.getId());
            		lRCBean.setMakerTimestamp(CommonUtilities.getCurrentDateTime());
            		lRCBean.setCheckerAuId(lAppUserBean.getId());
            		lRCBean.setCheckerTimestamp(CommonUtilities.getCurrentDateTime());
            		lRCBean.setApprovalStatus(ApprovalStatus.Approved);
            		registrationChargeDAO.update(lConnection, lRCBean);
            		//
            		registrationChargeBO.updateEntityExpiryDate(lConnection, lRCBean, MemoryDBManager.getInstance().getConnection(), lAppUserBean,false);
            	}
                lId = lRegistrationChargeBean.getId();
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
		super.onComplete(pFileUploadBean);
	}

	@Override
	public boolean isBinaryFile() {
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
		return super.getFileSubPath(pFileUploadBean);
	}

	@Override
	public boolean isSecure() {
		return false;
		//return super.isSecure();
	}
	
	
	private Workbook getWorkbook(FileUploadBean pFileUploadBean) {
		try {
	        pFileUploadBean.setReturnFileBinaryContent(pFileUploadBean.getFileBinaryContent());
			if(pFileUploadBean!=null && pFileUploadBean.getReturnFileBinaryContent()!=null) {
				return ExcelHelper.getWorkbook(pFileUploadBean.getReturnFileBinaryContent());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
   
}
