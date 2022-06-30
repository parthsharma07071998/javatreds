package com.xlx.treds.auction.bo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.commonn.bo.BaseFileUploader;
import com.xlx.treds.AppConstants;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationModificationDetailBean;
import com.xlx.treds.auction.bean.ObligationModificationRequestBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.auction.bean.PaymentFileBean;
import com.xlx.treds.auction.bean.PaymentSettlor;

public class ObligationModUploader extends BaseFileUploader {
	private ObligationModificationRequestBO obligationModificationRequestBO;
    private static final Logger logger = LoggerFactory.getLogger(ObligationModUploader.class);
    public static final String FILE_TYPE = "MODREQ";
    public static final int OBLI_MOD_REQ_EXCELL_FUID = 0;
    public static final int OBLI_MOD_REQ_EXCELL_TYPE = 1;
    public static final int OBLI_MOD_REQ_EXCELL_PARTNO = 2;
    public static final int OBLI_MOD_REQ_EXCELL_OBID = 3;
    public static final int OBLI_MOD_REQ_EXCELL_TXNTYPE = 4;
    public static final int OBLI_MOD_REQ_EXCELL_ORIGDATE = 5;
    public static final int OBLI_MOD_REQ_EXCELL_ORIGSTATUS = 6;
    public static final int OBLI_MOD_REQ_EXCELL_ORIGAMOUNT = 7;
    public static final int OBLI_MOD_REQ_EXCELL_ORIGSETTLOR = 8;
    public static final int OBLI_MOD_REQ_EXCELL_REVDATE = 9;
    public static final int OBLI_MOD_REQ_EXCELL_REVSTATUS = 10;
    public static final int OBLI_MOD_REQ_EXCELL_REVAMOUNT = 11;
    public static final int OBLI_MOD_REQ_EXCELL_REVSETTLOR = 12;
    public static final int OBLI_MOD_REQ_EXCELL_RATE = 13;
    public static final int OBLI_MOD_REQ_EXCELL_DAYDIFF = 14;
    public static final int OBLI_MOD_REQ_EXCELL_INTEREST = 15;
    public static final int OBLI_MOD_REQ_EXCELL_CALCULATEDREVISEDAMOUNT = 16;
    public static final int OBLI_MOD_REQ_EXCELL_PAYREFNO = 17;
    public static final int OBLI_MOD_REQ_EXCELL_REMARKS = 18;
    public static final int OBLI_MOD_REQ_EXCELL_RTN_REMARKS = 19;
    //status for sheet 2
    public static final int OBLI_MOD_REQ_EXCELL_STATUS = 3;
    
    public static final int OBLI_MOD_REQ_EXCELL_ORIGAMOUNT_ADDRESS = 0;
    public static final int OBLI_MOD_REQ_EXCELL_DATEDIFF_ADDRESS = 1;
    public static final int OBLI_MOD_REQ_EXCELL_INTEREST_ADDRESS = 2;
    public static final int OBLI_MOD_REQ_EXCELL_ORIGDATE_ADDRESS = 3;
    public static final int OBLI_MOD_REQ_EXCELL_REVDATE_ADDRESS = 4;
    public static final int OBLI_MOD_REQ_EXCELL_RATE_ADDRESS = 5;
    
    
    public ObligationModUploader() {
        super();
        obligationModificationRequestBO = new ObligationModificationRequestBO();
    }
    
    @Override
    public String getFileStore() {
        return FILE_TYPE;
    }
    
    @Override
    public boolean isBinaryFile() {
        return true;
    }
    
    @Override
    public boolean buildReturnFile() {
        return true;
    }
    
    @Override
    public void onEndOfFile(FileUploadBean pFileUploadBean) throws Exception {
    	byte[] lContent = pFileUploadBean.getFileBinaryContent();
    	Long lFuId= null;
    	ByteArrayInputStream lByteInputStream = new ByteArrayInputStream(lContent);
    	Workbook lWorkBook = WorkbookFactory.create(lByteInputStream);
    	String lKey =null;
    	Sheet lSheet = lWorkBook.getSheet("Obligation Details");
    	HashMap<Long,ObligationModificationRequestBean> lRequestHash =  new HashMap<Long,ObligationModificationRequestBean>();
    	HashMap<String,Long> lFURowPostition =  new HashMap<String,Long>();
    	for (Row lRow: lSheet) {
    		if (lRow.getRowNum()==0){
    			continue;
    		}
    		if (lRow.getCell(OBLI_MOD_REQ_EXCELL_FUID)==null) {
    			break;
    		}
    		lFuId = Long.valueOf((long) lRow.getCell(OBLI_MOD_REQ_EXCELL_FUID).getNumericCellValue());
            if(!lRequestHash.containsKey(lFuId)){
            	ObligationModificationRequestBean lBean =new ObligationModificationRequestBean();
            	lRequestHash.put(lFuId, lBean );
            	lBean.setFuId(lFuId);
            	lBean.setObliModDetailsList(new ArrayList<ObligationModificationDetailBean>());
        		lBean.setType(getType(lRow.getCell(OBLI_MOD_REQ_EXCELL_TYPE).getStringCellValue()));
            	lBean.setPartNumber(Long.valueOf((long) lRow.getCell(OBLI_MOD_REQ_EXCELL_PARTNO).getNumericCellValue()));
            	lBean.setStatus(ObligationModificationRequestBean.Status.Sent);
            	lBean.setDate(CommonUtilities.getToday());
            }
            ObligationModificationRequestBean lOMRBean = lRequestHash.get(lFuId);
            ObligationModificationDetailBean lOMDBean = new ObligationModificationDetailBean();
            lOMDBean.setObId(Long.valueOf((long) lRow.getCell(OBLI_MOD_REQ_EXCELL_OBID).getNumericCellValue()));
        	lOMDBean.setTxnType(getTxnType(lRow.getCell(OBLI_MOD_REQ_EXCELL_TXNTYPE).getStringCellValue()));
        	lOMDBean.setPartNumber(Long.valueOf((long) lRow.getCell(OBLI_MOD_REQ_EXCELL_PARTNO).getNumericCellValue()));
            lOMDBean.setOrigDate(new Date(lRow.getCell(OBLI_MOD_REQ_EXCELL_ORIGDATE).getDateCellValue().getTime()));
            lOMDBean.setOrigStatus(getStatus(lRow.getCell(OBLI_MOD_REQ_EXCELL_ORIGSTATUS).getStringCellValue()));
            lOMDBean.setOrigPaymentSettlor(lRow.getCell(OBLI_MOD_REQ_EXCELL_ORIGSETTLOR).getStringCellValue());
            lOMDBean.setOrigAmount(BigDecimal.valueOf(lRow.getCell(OBLI_MOD_REQ_EXCELL_ORIGAMOUNT).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
            lOMDBean.setRevisedDate(new Date(lRow.getCell(OBLI_MOD_REQ_EXCELL_REVDATE).getDateCellValue().getTime()));
            lOMDBean.setRevisedStatus(getStatus(lRow.getCell(OBLI_MOD_REQ_EXCELL_REVSTATUS).getStringCellValue()));
            lOMDBean.setRevisedAmount(BigDecimal.valueOf(lRow.getCell(OBLI_MOD_REQ_EXCELL_REVAMOUNT).getNumericCellValue()).setScale(2, RoundingMode.HALF_UP));
            if (AppConstants.FACILITATOR_NPCI.equals(lRow.getCell(OBLI_MOD_REQ_EXCELL_REVSETTLOR).getStringCellValue()) 
            		|| AppConstants.FACILITATOR_DIRECT.equals(lRow.getCell(OBLI_MOD_REQ_EXCELL_REVSETTLOR).getStringCellValue())) {
            	lOMDBean.setPaymentSettlor(lRow.getCell(OBLI_MOD_REQ_EXCELL_REVSETTLOR).getStringCellValue());
            }else {
            	throw new CommonBusinessException("Please select one of the following settlor i.e 1:"+AppConstants.FACILITATOR_NPCI +" or 2:"+AppConstants.FACILITATOR_DIRECT);
            }
            if (lRow.getCell(OBLI_MOD_REQ_EXCELL_PAYREFNO).getCellType() == Cell.CELL_TYPE_NUMERIC ) {
            	lRow.getCell(OBLI_MOD_REQ_EXCELL_PAYREFNO).setCellType(Cell.CELL_TYPE_STRING);
            }
            lOMDBean.setPaymentRefNo(lRow.getCell(OBLI_MOD_REQ_EXCELL_PAYREFNO)==null?null:lRow.getCell(OBLI_MOD_REQ_EXCELL_PAYREFNO).getStringCellValue());
            if (lRow.getCell(OBLI_MOD_REQ_EXCELL_REMARKS).getCellType() == Cell.CELL_TYPE_NUMERIC ) {
            	lRow.getCell(OBLI_MOD_REQ_EXCELL_REMARKS).setCellType(Cell.CELL_TYPE_STRING);
            }
            lOMDBean.setRemarks(lRow.getCell(OBLI_MOD_REQ_EXCELL_REMARKS)==null?null:lRow.getCell(OBLI_MOD_REQ_EXCELL_REMARKS).getStringCellValue());
            lOMRBean.getObliModDetailsList().add(lOMDBean);
            lKey = lOMRBean.getFuId()+CommonConstants.KEY_SEPARATOR+lOMRBean.getType()+CommonConstants.KEY_SEPARATOR+lOMRBean.getPartNumber();
            if(!lFURowPostition.containsKey(lKey)){
            	lFURowPostition.put(lKey, new Long(lRow.getRowNum()));
            }
    	}
    	ExecutionContext lExecutionContext =  pFileUploadBean.getExecutionContext();
    	lExecutionContext.setAutoCommit(false);
    	boolean lRollback = false;
    	Cell lCell = lSheet.getRow(0).createCell(OBLI_MOD_REQ_EXCELL_RTN_REMARKS);
    	lCell.setCellValue("RTN REMARKS");
    	for (ObligationModificationRequestBean lOMRBean : lRequestHash.values()){
    		lKey = lOMRBean.getFuId()+CommonConstants.KEY_SEPARATOR+lOMRBean.getType()+CommonConstants.KEY_SEPARATOR+lOMRBean.getPartNumber();
    		lCell = lSheet.getRow(lFURowPostition.get(lKey).intValue()).createCell(OBLI_MOD_REQ_EXCELL_RTN_REMARKS);
    		ObligationModificationRequestBean lFilterBean = new ObligationModificationRequestBean();
    		try{
    			if(lOMRBean.hasDetailsChanged()){
    				obligationModificationRequestBO.saveWithoutTransaction(lExecutionContext, lOMRBean, pFileUploadBean.getAppUserBean(), true);
    			}else{
            		lCell.setCellValue("Info : No Changes done so modification request not created.");
    			}
    		}catch(Exception lEx){
    			logger.info("ObligationModUploader :  onEndOfFile : "+lEx.getMessage());
    			lCell.setCellValue("Error : "+lEx.getMessage());
    			lRollback=true;
    		}
    		
    	}
    	if(lRollback){
    		lExecutionContext.rollback();
    	}else{
    		lExecutionContext.commitAndDispose();
    	}
    	ByteArrayOutputStream lByteArrayOpStream = new ByteArrayOutputStream();
		lWorkBook.write(lByteArrayOpStream);
		lByteArrayOpStream.close();
		lWorkBook.close();
    	pFileUploadBean.setReturnFileBinaryContent(lByteArrayOpStream.toByteArray());
    	
   }
    
   public ObligationBean.Status getStatus(String pStatus){
	   if(ObligationBean.Status.Failed.toString().equals(pStatus)){
   			return ObligationBean.Status.Failed;
       }else if(ObligationBean.Status.Created.toString().equals(pStatus)){
      		return ObligationBean.Status.Created;
       }else if(ObligationBean.Status.Ready.toString().equals(pStatus)){
      		return ObligationBean.Status.Ready;
       }else if(ObligationBean.Status.Returned.toString().equals(pStatus)){
     		return ObligationBean.Status.Returned;
       }else if(ObligationBean.Status.Prov_Success.toString().equals(pStatus)){
    		return ObligationBean.Status.Prov_Success;
       }else if(ObligationBean.Status.Sent.toString().equals(pStatus)){
    		return ObligationBean.Status.Sent;
       }else if(ObligationBean.Status.Success.toString().equals(pStatus)){
    		return ObligationBean.Status.Success;
       }else if(ObligationBean.Status.Cancelled.toString().equals(pStatus)){
    		return ObligationBean.Status.Cancelled;
       }
	return null;
   }
   
   public ObligationBean.TxnType getTxnType(String pTxnType){
	   if(ObligationBean.TxnType.Debit.toString().equals(pTxnType)){
	   		return ObligationBean.TxnType.Debit;
	   }else if(ObligationBean.TxnType.Credit.toString().equals(pTxnType)){
	   		return ObligationBean.TxnType.Credit;
	   }
	return null;
   }
   
   public ObligationBean.Type getType(String pType){
	   if(ObligationBean.Type.Leg_1.toString().equals(pType)){
	   		return ObligationBean.Type.Leg_1;
	   }else if(ObligationBean.Type.Leg_2.toString().equals(pType)){
	   		return ObligationBean.Type.Leg_2;
	   }else{
		    return ObligationBean.Type.Leg_3;
	   }
   }
   
}