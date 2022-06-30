package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.RefMasterHelper;
import com.xlx.common.registry.RegistryHelper;
import com.xlx.common.registry.bean.RefCodeValuesBean;
import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.common.utilities.FormatHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.user.bean.AppUserBean;

public class ICICIPaymentImpl implements IPaymentInterface {
    private static final Logger logger = LoggerFactory.getLogger(ICICIPaymentImpl.class);
    public static final String COLUMN_SEPERATOR = "|";
    public static final String RECORD_SEPERATOR = "\r\n";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_FORMAT2 = "ddMMyyyy";
    
    public static final String PAYINSTATUS_SUCCESS = "SUCCESS";
    public static final String PAYINSTATUS_FAIL = "FAILED";
    public static final String PAYOUTSTATUS_SUCCESS = "P";
    
    public static final String REFCODE_ICICINACHDESC = "ICICINACHDESC";
    public static final String REGISTRY_ICICITREDSACCOUNT = "server.settings.icicitredsaccount";
    public static final String ATTRIBUTE_NEFTPOOLACCOUNTNO = "neftpoolaccountno";
    
    public static final String TREDSCODE_LEG1FAILED = "TR001";
    public static final String TREDSCODE_LEG1FAILED_DESC = "Transaction cancelled since corresponding Leg1 failed.";
    
    private GenericDAO<ObligationBean> obligationDAO;
    private GenericDAO<PaymentFileBean> paymentFileDAO;
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    //
    private IPaymentSettlor paymentSettlor = null;
    //
    //Consumer Code	TPSL Transaction ID	Customer Name	Amount	Date	Status	Reason Code	Reason description	Clinet Code
    private static final int IDX_DR_RTN_MAPPINGCODE = 0; //ConsumerCode = MappingCode
    private static final int IDX_DR_RTN_TPSLTRANSID = 1; //their unique trans id
    private static final int IDX_DR_RTN_CUSTOMERNAME = 2;
    private static final int IDX_DR_RTN_AMOUNT = 3;
    private static final int IDX_DR_RTN_DATE = 4;
    private static final int IDX_DR_RTN_STATUS = 5;
    private static final int IDX_DR_RTN_REASONCODE = 6;
    private static final int IDX_DR_RTN_REASONDESC = 7; //we will not use this : DK
    private static final int IDX_DR_RTN_OBLIGATIONID = 8; //ClientCode = Obligation Id
    private static final int IDX_DR_RTN_SIZE = 9;
    
    public ICICIPaymentImpl() {
        super();
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
    }

    @Override
    public void generateFile(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
    	DBHelper lDbHelper = DBHelper.getInstance();
    	String lSql=null;
    	
    	lSql = getQuery(pPaymentFileBean);
    	
        List<ObligationBean> lList = obligationDAO.findListFromSql(pConnection, lSql.toString(), 0);
        List<IObligation> lObligationList  = new ArrayList<IObligation>();
        for (ObligationBean lBean : lList){
        	lObligationList.add(lBean);
        }
        if ((lObligationList == null) || (lObligationList.size() == 0))
            throw new CommonBusinessException("No obligations");

        pPaymentFileBean.setId(lDbHelper.getUniqueNumber(pConnection, "PaymentFile.Id"));
        
        // cache entity mappings
        int lPtr = 1;
        Map<String, FacilitatorEntityMappingBean> lFacilitatorMap = null;
        Map<Long, CompanyBankDetailBean> lDesignatedBankMap = null;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        //
        if (AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator())) {
            lFacilitatorMap = TredsHelper.getInstance().getFacilitatorEntityMap(pConnection, AppConstants.FACILITATOR_ICICI);
        }
        lDesignatedBankMap = TredsHelper.getInstance().getEntityBankDetailsMap(pConnection);
        //
        BigDecimal lTotalValue = BigDecimal.ZERO;
        for (IObligation lObligationBean : lObligationList) {
        	
            lObligationBean.setFileSeqNo(Long.valueOf(lPtr++));
            lObligationBean.setRecordUpdator(pAppUserBean.getId());
            //
            updateObligation(pConnection, lObligationBean, pPaymentFileBean, lFacilitatorMap, lDesignatedBankMap, lMemoryTable, null);
            //
            lTotalValue = lTotalValue.add(lObligationBean.getAmount());
        }
        
        pPaymentFileBean.setFileName(getFileName(pPaymentFileBean));
        pPaymentFileBean.setRecordCount(Long.valueOf(lObligationList.size()));
        pPaymentFileBean.setTotalValue(lTotalValue);
        pPaymentFileBean.setGeneratedByAuId(pAppUserBean.getId());
        pPaymentFileBean.setGeneratedTime(new Timestamp(System.currentTimeMillis()));
        pPaymentFileBean.setStatus(PaymentFileBean.Status.Generated);
        paymentFileDAO.insert(pConnection, pPaymentFileBean);
        //
        tempCreateFile(lObligationList, pPaymentFileBean);
    }
    @Override
    public void tempCreateFile(List<IObligation> pListObligation, PaymentFileBean pPaymentFileBean){
		if (ObligationBean.TxnType.Debit.equals(pPaymentFileBean.getFileType()) &&
				AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator())) {
        	tempCreateDebitNACHFile(pListObligation, pPaymentFileBean.getId());
        }else if (ObligationBean.TxnType.Credit.equals(pPaymentFileBean.getFileType()) &&
        		AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator())) {
            	tempCreateCreditNACHFile(pListObligation, pPaymentFileBean.getId());
        }else if (ObligationBean.TxnType.Credit.equals(pPaymentFileBean.getFileType()) &&
        		AppConstants.FACILITATOR_ICICINEFT.equals(pPaymentFileBean.getFacilitator())) {
        	tempCreateCreditNEFTFile(pListObligation, pPaymentFileBean.getId());
        }
	}
    
    @Override
    public String getQuery(PaymentFileBean pPaymentFileBean){
    	 StringBuilder lSql = new StringBuilder();
         DBHelper lDbHelper = DBHelper.getInstance();
         //
    	 lSql.append("SELECT * FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
         lSql.append(" AND OBDATE = ").append(lDbHelper.formatDate(pPaymentFileBean.getDate()));
         lSql.append(" AND OBTXNTYPE = ").append(lDbHelper.formatString(pPaymentFileBean.getFileType().getCode()));
         if (AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator())){
         	//NACH
 	       	 if (ObligationBean.TxnType.Debit.equals(pPaymentFileBean.getFileType())){
 	             lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
 	       	 }else{
 	       		 //not (Treds Credit & Financer Credit of obligtype=Shift)
 	             lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
 	             
 	             lSql.append(" AND ( ");
 	             
 	             //NOT TREDS credit ie. Supplier Credit (in Leg1)
 	             lSql.append(" ( "); //Seller
                  lSql.append(" OBTYPE = ").append(lDbHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
                  lSql.append(" AND OBTXNENTITY != ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));
                  lSql.append(" )  ");
 	             
                  //NOT Financier credit ie. Supplier Credit (in Leg1)
                  lSql.append(" OR ( ");
                  lSql.append(" OBTYPE = ").append(lDbHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
                  lSql.append(" AND OBTXNENTITY != ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));
                  lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
                  lSql.append(" AND OBOLDOBLIGATIONID IS NULL  ");
                  lSql.append(" ) ");
                  
                  //Financier credit ie. Financier Credit (in Leg2)
                  lSql.append(" OR ( ");
                  lSql.append(" OBTYPE = ").append(lDbHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
                  lSql.append(" AND OBTXNENTITY != ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));
                  lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
                  lSql.append(" AND OBOLDOBLIGATIONID IS NULL  ");
                  lSql.append(" ) ");

                  lSql.append(" ) ");

                  lSql.append(" AND OBOLDOBLIGATIONID IS NULL ");
 	             lSql.append(" AND OBTXNENTITY != ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));

 	       	 }
         }
         else{
         	//NEFT - always credit file 
         	 if (ObligationBean.TxnType.Credit.equals(pPaymentFileBean.getFileType())){
               		 //Treds Credit(in Leg1) and Financier Credit (in Leg2) which are shifted
                      lSql.append(" AND ( OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Failed.getCode()));
                      lSql.append(" AND OBOLDOBLIGATIONID IS NOT NULL  "); //to avoid fetching obligation which are already failed, only shifted obligation will be retried again and again
                      lSql.append(" OR ( ");
                      lSql.append(" OBTYPE = ").append(lDbHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
                      lSql.append(" AND OBTXNENTITY = ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));
                      lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
                      lSql.append(" )  ");
                      lSql.append(" OR ( ");
                      lSql.append(" OBTYPE = ").append(lDbHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
                      lSql.append(" AND OBTXNENTITY != ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));
                      lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
                      lSql.append(" AND OBOLDOBLIGATIONID IS NOT NULL  "); 
                      lSql.append(" )  ");
                      lSql.append(" OR ( ");
                      lSql.append(" OBTYPE = ").append(lDbHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
                      lSql.append(" AND OBTXNENTITY != ").append(lDbHelper.formatString(AppConstants.DOMAIN_PLATFORM));
                      lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
                      lSql.append(" ) ) ");
             	 }
         }
         lSql.append(" ORDER BY OBRECORDCREATETIME, OBID ");
         return lSql.toString();
    }

   
    @Override
    public String getFileName(PaymentFileBean pPaymentFileBean) throws CommonBusinessException{
    	String lFileName = null;
        Timestamp lCurrentTime = new Timestamp(System.currentTimeMillis());
        if (ObligationBean.TxnType.Debit.equals(pPaymentFileBean.getFileType())) {
            if (AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator())) {
                lFileName = "TREDS_PAYIN_" + new SimpleDateFormat("ddMMyyyyHHmmss").format(lCurrentTime) + ".txt";
            } else {
                throw new CommonBusinessException("Debit file cannot be generated for this facilitator code");
            }
        } else {
            if (AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator())) 
                lFileName = "ACH-CR-ICIC-ICICH2H" + "103355" + "-" + new SimpleDateFormat("ddMMyyyy").format(lCurrentTime) + "-" + 
                        StringUtils.leftPad(pPaymentFileBean.getId().toString(), 6, '0') + "-INP.txt";
            else
                lFileName = "TREDS_TRUPLD_" + new SimpleDateFormat("ddMMyyyyHHmmss").format(lCurrentTime) + ".txt";
        }
        return lFileName;
    }
    
    
    @Override
    public void updateObligation(Connection pConnection, IObligation pObligationBean, PaymentFileBean pPaymentFileBean, Map<String, FacilitatorEntityMappingBean> pFacilitatorMap, Map<Long, CompanyBankDetailBean> pDesignatedBankMap, MemoryTable pMemoryTable, CompanyBankDetailBean pCompanyBankDetailBean) throws Exception{
        AppEntityBean lAppEntityBean = null;
        Long lCdId = null;

	   	 if(AppConstants.DOMAIN_PLATFORM.equals(pObligationBean.getTxnEntity())){
			 lCdId = new Long(0);
		 }else{
	         lAppEntityBean = (AppEntityBean)pMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{pObligationBean.getTxnEntity()});
	         lCdId = lAppEntityBean.getCdId();
		 }
    	
    	 if (ObligationBean.TxnType.Debit.equals(pPaymentFileBean.getFileType())) {
    		 //do nothing
         } else {
             CompanyBankDetailBean lDesignatedBank = null;
        	 //find the company details bean and check whether the settlementwise 
        	 if(pObligationBean.getSettlementCLId()!=null && pCompanyBankDetailBean != null ){ 
        		 lDesignatedBank = pCompanyBankDetailBean;
        	 }else{
                 lDesignatedBank = pDesignatedBankMap.get(lCdId);
                 if(lDesignatedBank == null || lDesignatedBank.getId() == null)
                     throw new CommonBusinessException("Designated Bank for " + pObligationBean.getTxnEntity() + " not found");
        	 }
             pObligationBean.setPayDetail1(lDesignatedBank.getAccNo()); 
             pObligationBean.setPayDetail2(lDesignatedBank.getIfsc());
             if(lAppEntityBean!=null){
                 pObligationBean.setPayDetail3(lAppEntityBean.getName()); 
             }else{
                 pObligationBean.setPayDetail3(lDesignatedBank.getFirstName()); 
             }
    	   	 if(lAppEntityBean!=null){
    	   		 if(lAppEntityBean.isPurchaser() || lAppEntityBean.isFinancier()){
					 Date lExpiry = null;
    	   			 String lKey = pObligationBean.getTxnEntity() + CommonConstants.KEY_SEPARATOR + lDesignatedBank.getId();
    	   			 FacilitatorEntityMappingBean lFEMBean = pFacilitatorMap.get(lKey);
    		         String lMappingCode = (lFEMBean != null)?lFEMBean.getMappingCode():"";
    		         if (StringUtils.isBlank(lMappingCode))
    		             throw new CommonBusinessException("Mapping code for " + pObligationBean.getTxnEntity() + " not found.");
    		         pObligationBean.setPayDetail4(lMappingCode);
    		         lExpiry = pFacilitatorMap.get(lKey).getExpiry();
    		         if(lExpiry!=null && lExpiry.before(pObligationBean.getDate())){
    		        	 throw new CommonBusinessException("Mapping code for " + pObligationBean.getTxnEntity() + " is  expired.");
    		         }
    	   		 }
    	   	 }
         }
         pObligationBean.setStatus(ObligationBean.Status.Sent);
         pObligationBean.setPfId(pPaymentFileBean.getId());

         pObligationBean.setRecordUpdateTime(new Timestamp(System.currentTimeMillis()));
         obligationDAO.update(pConnection, (ObligationBean)pObligationBean, ObligationBean.FIELDGROUP_GENERATE);
    }
    
    private void tempCreateDebitNACHFile(List<IObligation> pListObligation, Long pPFId){
    	if(pListObligation!=null){
    		StringBuilder lFileContent = new StringBuilder();
    		String[] lTemp = null;
            for(IObligation lBean : pListObligation){
            	lTemp = new String[IDX_DR_RTN_SIZE];
            	for(int lPtr=0; lPtr<lTemp.length; lPtr++)
            		lTemp[lPtr] ="";
            	lTemp[IDX_DR_RTN_MAPPINGCODE] = lBean.getPayDetail1();
            	lTemp[IDX_DR_RTN_STATUS] = PAYINSTATUS_SUCCESS; //PAYINSTATUS_FAIL
            	lTemp[IDX_DR_RTN_OBLIGATIONID] = lBean.getId().toString(); //obli id
            	lTemp[IDX_DR_RTN_REASONCODE] = ""; //reasoncode
            	lTemp[IDX_DR_RTN_AMOUNT] = lBean.getAmount().toString();
            	lTemp[IDX_DR_RTN_DATE] = FormatHelper.getDisplay(DATE_FORMAT2, lBean.getDate()).toString();
            	lFileContent.append(CommonUtilities.joinString(lTemp, CommonConstants.COLUMN_SEPARATOR, true));
            	lFileContent.append("\n");
            }
            logger.debug("*********************************");
            logger.debug("DEBIT NACH RETURN FILE");
            logger.debug("*********************************");
            logger.debug(lFileContent.toString());
            logger.debug("*********************************");
    	}
    	
    }
    
    private void tempCreateCreditNACHFile(List<IObligation> pListObligation, Long pPFId){
    	if(pListObligation!=null){
            BigDecimal lTotal = new BigDecimal(0);
    		StringBuilder lFileContent = new StringBuilder();
            int[] lHeaderFieldWidths = new int[]{2,7,40,14,9,9,15,3,13,13,8,10,10,3,18,18,11,35,9,2,57};
            int[] lDataFieldWidths = new int[]{2,9,2,3,15,40,9,7,20,13,13,10,10,1,2,11,35,11,18,30,3,15,20,7};
            String lTemp = null;
            StringBuilder lHeader = new StringBuilder();
            String lAppendChar = " ";
            for(IObligation lBean : pListObligation){
                for(int lPtr=0; lPtr < lDataFieldWidths.length; lPtr++){
                	lTemp = "";
                	lAppendChar = " ";
                	if(lPtr==0) lTemp="11";
                	if(lPtr==10) {
                		lTemp=lBean.getAmount().multiply(new BigDecimal(100)).longValue()+""; 
                		lAppendChar = "0";
                	}
                	else if(lPtr==13) {
                		lTemp="1"; //SUCCESS 
                	}
                	else if(lPtr==15) {
                		lTemp=lBean.getPayDetail2(); 
                	}
                	else if(lPtr==16) {
                		lTemp=lBean.getPayDetail1(); 
                	}
                	else if(lPtr==19){
                		lTemp=lBean.getId().toString(); 
                	}
                	lTemp = CommonUtilities.appendChars(lDataFieldWidths[lPtr],lTemp,lAppendChar,false);
                	lFileContent.append(lTemp);
                }
            	lTotal = lTotal.add(lBean.getAmount());
                lFileContent.append("\n");
            }
            for(int lPtr=0; lPtr < lHeaderFieldWidths.length; lPtr++){
            	lTemp = "";
        		lAppendChar = " ";
            	if(lPtr==0) lTemp="11";
            	if(lPtr==9) {
            		lTemp=lTotal.multiply(new BigDecimal(100)).longValue()+""; //Total Amount
            		lAppendChar = "0";
            	}
            	else if(lPtr==15) lTemp=pPFId.toString(); // PFId  
            	else if(lPtr==18) {
            		lTemp= pListObligation.size()+""; // RecordCount
            		lAppendChar = "0";
            	}
            	lTemp = CommonUtilities.appendChars(lHeaderFieldWidths[lPtr],lTemp,lAppendChar,false);
            	lHeader.append(lTemp);
            }
            logger.debug("*********************************");
            logger.debug("CREDIT NACH RETURN FILE");
            logger.debug(lHeader.toString() + "\n" + lFileContent.toString());
            logger.debug("*********************************");
    	}
    }
    
    private void tempCreateCreditNEFTFile(List<IObligation> pListObligation, Long pPFId){
    	if(pListObligation!=null){
    		StringBuilder lFileContent = new StringBuilder();
    		String[] lTemp = null;
            for(IObligation lBean : pListObligation){
            	lTemp = new String[16];
            	for(int lPtr=0; lPtr<lTemp.length; lPtr++)
            		lTemp[lPtr] ="";
            	lTemp[1] = lBean.getId().toString();
            	lTemp[5] = lBean.getPayDetail1(); //account
            	lTemp[6] = lBean.getPayDetail2(); //ifsc
            	lTemp[3] = lBean.getAmount().toString();
            	lTemp[7] = FormatHelper.getDisplay(DATE_FORMAT, lBean.getDate()).toString();
            	lTemp[15] = PAYOUTSTATUS_SUCCESS; // PAYOUTSTATUS_SUCCESS / PAYINSTATUS_FAIL
            	lFileContent.append(CommonUtilities.joinString(lTemp, CommonConstants.COLUMN_SEPARATOR, true));
            	lFileContent.append("\n");
            }
            logger.debug("*********************************");
            logger.debug("CREDIT NACH NEFT FILE");
            logger.debug(lFileContent.toString());
            logger.debug("*********************************");
    	}
    	
    }

   
    @Override
    public PaymentFileBean getFileContents(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
        PaymentFileBean lPaymentFileBean = paymentFileDAO.findByPrimaryKey(pConnection, pPaymentFileBean);
        if (lPaymentFileBean == null)
            throw new CommonBusinessException("Invalid File");
        List<IObligation> lObligationList = getObligationListForFile(pConnection, lPaymentFileBean,null,false);
        if ((lObligationList == null) || (lObligationList.size() == 0))
            throw new CommonBusinessException("No obligations");
        pPaymentFileBean.setContents(getFileContentAsString(lPaymentFileBean, lObligationList));
        return lPaymentFileBean;
    }

	@Override
    public String getFileContentAsString(PaymentFileBean pPaymentFileBean, List<IObligation> lObligationList) {
		String lFileContent = null;
        if (ObligationBean.TxnType.Debit.equals(pPaymentFileBean.getFileType()))
        	lFileContent = getContentsNACHDebitFile(pPaymentFileBean, lObligationList);
        else {
            if (AppConstants.FACILITATOR_ICICI.equals(pPaymentFileBean.getFacilitator()))
            	lFileContent = getContentsNACHCreditFile(pPaymentFileBean, lObligationList);
            else
            	lFileContent = getContentsNEFTCreditFile(pPaymentFileBean, lObligationList);
        }
        return lFileContent;
	}
	
    private String getContentsNACHDebitFile(PaymentFileBean pPaymentFileBean, List<IObligation> lObligationList) {
        StringBuilder lContents = new StringBuilder();
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT2);
        for (IObligation lObligationBean : lObligationList) {
            lContents.append(lObligationBean.getPayDetail1()).append(COLUMN_SEPERATOR);
            lContents.append(lObligationBean.getAmount()).append(COLUMN_SEPERATOR);
            lContents.append(lSimpleDateFormat.format(lObligationBean.getDate())).append(COLUMN_SEPERATOR);
            lContents.append(lObligationBean.getId()).append(RECORD_SEPERATOR);
        }
        return lContents.toString();
    }
    
    private String getContentsNACHCreditFile(PaymentFileBean pPaymentFileBean, List<IObligation> lObligationList) {
        StringBuilder lContents = new StringBuilder();
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        char lZero = '0';
        CompanyBankDetailBean lTredsBankAccount = TredsHelper.getInstance().getTredsAccount();
        String lUserName = lTredsBankAccount.getFirstName();
        String lSponsorBankIfsc = lTredsBankAccount.getIfsc();
        String lUserNumber = lTredsBankAccount.getLastName();
        String lTredsAccountNo = lTredsBankAccount.getAccNo();
        long lTotalAmount = 0;
        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);
        for (IObligation lObligationBean : lObligationList) {
            long lAmount = lObligationBean.getAmount().multiply(AppConstants.HUNDRED).longValue();
            lTotalAmount += lAmount;

            lContents.append(RECORD_SEPERATOR);
            lContents.append("23");// ACH Transaction Code
            lContents.append(fixedLengthString(null, 9));// Control
            lContents.append("10");// Destination Account Type
            lContents.append(fixedLengthString(null, 3));//Ledger Folio Number
            lContents.append(fixedLengthString(null, 15));//Control
            lContents.append(fixedLengthString(lObligationBean.getPayDetail3(), 40));//Beneficiary Account Holder's Name
            lContents.append(fixedLengthString(null, 9));//Control
            lContents.append(fixedLengthString(null, 7));//Control
            lContents.append(fixedLengthString(lUserName, 20));//User Name
            lContents.append(fixedLengthString(null, 13));//Control
            lContents.append(StringUtils.leftPad(String.valueOf(lAmount), 13, lZero));//Amount
            lContents.append(fixedLengthString(null, 10));//Reserved (ACH Item Seq No.)
            lContents.append(fixedLengthString(null, 10));//Reserved (Checksum)
            lContents.append(fixedLengthString(null, 1));//Reserved (Flag for success / return)
            lContents.append(fixedLengthString(null, 2));//Reserved (Reason Code)
            lContents.append(fixedLengthString(lObligationBean.getPayDetail2(), 11));//Destination Bank IFSC / MICR / IIN
            lContents.append(fixedLengthString(lObligationBean.getPayDetail1(), 35));//Beneficiary's Bank Account number
            lContents.append(fixedLengthString(lSponsorBankIfsc, 11));//Sponsor Bank IFSC / MICR / IIN
            lContents.append(fixedLengthString(lUserNumber, 18));//User Number
            lContents.append(fixedLengthString(lObligationBean.getId().toString(), 27));//Transaction Reference
            lContents.append(fixedLengthString(null, 3));//Entity Type - financier(FIN)/seller(SEL)/buyer(BUY)/TReDS charges(TRE)
            lContents.append("TRE");//Product Type
            lContents.append(fixedLengthString(null, 15));//Beneficiary Aadhaar Number
            lContents.append(fixedLengthString(null, 20));//UMRN
            lContents.append(fixedLengthString(null, 7));//Filler
        }
        lContents.append("\n"); //New Line at the end of file
        StringBuilder lHeader = new StringBuilder();
        lHeader.append("12");//ACH transaction code
        lHeader.append(fixedLengthString(null, 7));//Control
        lHeader.append(fixedLengthString(lUserName, 40));//User Name
        lHeader.append(fixedLengthString(null, 14));//Control
        lHeader.append(fixedLengthString(null, 9));//ACH File Number
        lHeader.append(fixedLengthString(null, 9));//Control
        lHeader.append(fixedLengthString(null, 15));//Control
        lHeader.append(fixedLengthString(null, 3));//Ledger Folio Number
        lHeader.append("9999999999999");//User Defined limit for individual items
        lHeader.append(StringUtils.leftPad(String.valueOf(lTotalAmount), 13, lZero));//Total Amount in paise (Balancing Amount)
        lHeader.append(lSimpleDateFormat.format(lObligationList.get(0).getDate()));//Settlement Date (DDMMYYYY)
        lHeader.append(fixedLengthString(null, 10));//Reserved (kept blank by user)
        lHeader.append(fixedLengthString(null, 10));//Reserved (kept blank by user)
        lHeader.append(fixedLengthString(null, 3));//Filler
        lHeader.append(fixedLengthString(lUserNumber, 18));//User Number
        lHeader.append(fixedLengthString(pPaymentFileBean.getId().toString(), 18));//User Reference
        lHeader.append(fixedLengthString(lSponsorBankIfsc, 11));//Sponsor Bank IFSC / MICR / IIN
        lHeader.append(fixedLengthString(lTredsAccountNo, 35));//User's Bank Account Number
        lHeader.append(StringUtils.leftPad(String.valueOf(lObligationList.size()), 9, lZero));//Total Items
        lHeader.append(fixedLengthString(null, 2));//Settlement Cycle (Kept blank by User)
        lHeader.append(fixedLengthString(null, 57));//Filler
        lHeader.append(lContents);
        return lHeader.toString();
    }
    
    private String getContentsNEFTCreditFile(PaymentFileBean pPaymentFileBean, List<IObligation> lObligationList) {
        StringBuilder lContents = new StringBuilder();
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        HashMap<String,Object> lSettings = RegistryHelper.getInstance().getStructure(REGISTRY_ICICITREDSACCOUNT);
        String lTredsPoolAccount = (String)lSettings.get(ATTRIBUTE_NEFTPOOLACCOUNTNO);
        TredsHelper lTredsHelper = TredsHelper.getInstance();
        for (IObligation lObligationBean : lObligationList) {
            //1. Payment Product
            lContents.append("I").append(COLUMN_SEPERATOR);
            //2. Payment Document No. (Unique)
            lContents.append(lObligationBean.getId()).append(COLUMN_SEPERATOR);
            //3. Debit Account Number
            lContents.append(lTredsPoolAccount).append(COLUMN_SEPERATOR);
            //4. Amount
            lContents.append(lTredsHelper.getFormattedAmount(lObligationBean.getAmount(),false)).append(COLUMN_SEPERATOR);
            //5. Beneficiary Name
            lContents.append(lObligationBean.getPayDetail3()).append(COLUMN_SEPERATOR);
            //6. Credit Account Number
            lContents.append(lObligationBean.getPayDetail1()).append(COLUMN_SEPERATOR);
            //7. Credit Account IFSC Code
            lContents.append(lObligationBean.getPayDetail2()).append(COLUMN_SEPERATOR);
            //8. Transaction Date
            lContents.append(lSimpleDateFormat.format(lObligationBean.getDate())).append(COLUMN_SEPERATOR);
            //9. Reference
            lContents.append(lObligationBean.getId()).append(COLUMN_SEPERATOR);
            //10. Beneficiary code 
            lContents.append("").append(COLUMN_SEPERATOR);
            //11. Mobile No.
            lContents.append("").append(COLUMN_SEPERATOR);
            //12. Email ID
            lContents.append("").append(COLUMN_SEPERATOR);
            //13. Payment Details (Narration)
            String lNarration = ((ObligationBean)lObligationBean).getNarration();
            if(!CommonUtilities.hasValue(lNarration)) lNarration = "";
            lContents.append(lNarration).append(RECORD_SEPERATOR);
            //DATA RECEIVED FROM THE RETURN FILE (14, 15, 16)
            //14. RTGS No. / Rejection reason / Additional Details 
            //15. CMS Payment Reference No. for Refund (Unique) 
            //16. Status of Transaction. 
        }
        return lContents.toString();
    }
    
    @Override
    public void uploadReturnFile(FileUploadBean pFileUploadBean) throws Exception {
        pFileUploadBean.getExecutionContext().setAutoCommit(false);
        String lSettlorRemarks = null;
        try {
            String lPFId = pFileUploadBean.getFormParameters().get("pfId");
            PaymentFileBean lFilterBean = new PaymentFileBean();
            lFilterBean.setId(Long.valueOf(lPFId));
            Connection lConnection = pFileUploadBean.getExecutionContext().getConnection();
            PaymentFileBean lPaymentFileBean = paymentFileDAO.findByPrimaryKey(lConnection, lFilterBean);
            if (lPaymentFileBean == null)
                throw new CommonBusinessException("Invalid File");
            if (lPaymentFileBean.getStatus() != PaymentFileBean.Status.Generated)
                throw new CommonBusinessException("Return file already uploaded.");
            
            List<IObligation> lObligationList = getObligationListForFile(lConnection, lPaymentFileBean,null,true);
            Map<Long, ObligationBean> lMap = new HashMap<Long, ObligationBean>();
            for (IObligation lObligationBean : lObligationList)
                lMap.put(lObligationBean.getId(), (ObligationBean) lObligationBean);
            
            paymentSettlor = new PaymentSettlor(lPaymentFileBean);
            if (ObligationBean.TxnType.Debit.equals(lPaymentFileBean.getFileType()))
                uploadNACHDebitReturnFile(lConnection, lPaymentFileBean, pFileUploadBean, lMap);
            else {
                if (AppConstants.FACILITATOR_ICICI.equals(lPaymentFileBean.getFacilitator()))
                    uploadNACHCreditReturnFile(lConnection, lPaymentFileBean, pFileUploadBean, lMap);
                else
                    uploadNEFTCreditReturnFile(lConnection, lPaymentFileBean, pFileUploadBean, lMap);
            }
            paymentSettlor.process(lConnection);
        	lSettlorRemarks = paymentSettlor.getRemarks();
            if(CommonUtilities.hasValue(pFileUploadBean.getRemarks())){
            	pFileUploadBean.setRemarks(pFileUploadBean.getRemarks()+". "+lSettlorRemarks);
            }else{
            	pFileUploadBean.setRemarks(lSettlorRemarks);
            }

            if (lMap.isEmpty()) {
            	logger.debug("updating paymentFile ");
                lPaymentFileBean.setStatus(PaymentFileBean.Status.Return_File_Uploaded);
                lPaymentFileBean.setReturnUploadedByAuId(pFileUploadBean.getAuId());
                lPaymentFileBean.setReturnUploadedTime(new Timestamp(System.currentTimeMillis()));
                paymentFileDAO.update(lConnection, lPaymentFileBean, PaymentFileBean.FIELDGROUP_RETURN);
            }
            pFileUploadBean.getExecutionContext().commit();
        } catch (Exception lException) {
        	logger.debug("Error in uploadReturnFile : " + lException.getMessage());
        	pFileUploadBean.setSuccessCount(new Long(0));
            pFileUploadBean.getExecutionContext().rollback();
            lSettlorRemarks = paymentSettlor.getRemarks();
           if(CommonUtilities.hasValue(lSettlorRemarks)){
        	   throw new CommonBusinessException(lException.getMessage()+" " + lSettlorRemarks);
           }
            throw lException;
        }
    }
    

    private void uploadNACHDebitReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean, 
            FileUploadBean pFileUploadBean, Map<Long, ObligationBean> pObligationMap) throws Exception {
        List<String> lRecords = (List<String>)pFileUploadBean.getContext();
        int lCount = lRecords.size();
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT2);
        FactoringUnitBean lFactoringUnitFilterBean = new FactoringUnitBean();
        HashSet<String> lFULegs = new HashSet<String>();
        FactoringUnitBean lFactoringUnitBean = new FactoringUnitBean();
        AppUserBean lAppUserBean = new AppUserBean();
        String lKey = null;
        lAppUserBean.setId(pFileUploadBean.getAuId());
        logger.debug("Debit Return File Record Count : " + lCount);
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            String[] lRecord = CommonUtilities.splitString(lRecords.get(lPtr), COLUMN_SEPERATOR);
            if(lRecord==null || lRecord.length != IDX_DR_RTN_SIZE)
                throw new CommonBusinessException("Record length mismatch in record " + (lPtr));
            
            String lMappingCode = lRecord[IDX_DR_RTN_MAPPINGCODE];
            BigDecimal lAmount = NumberUtils.createBigDecimal(lRecord[IDX_DR_RTN_AMOUNT]);
            Date lDate = lSimpleDateFormat.parse(lRecord[IDX_DR_RTN_DATE]);
            String lStatus = lRecord[IDX_DR_RTN_STATUS];
            String lReasonCode = lRecord[IDX_DR_RTN_REASONCODE];
            Long lObligationId = Long.valueOf(lRecord[IDX_DR_RTN_OBLIGATIONID]);
            
            ObligationBean lObligationBean = pObligationMap.remove(lObligationId);
            if (lObligationBean == null)
                throw new CommonBusinessException("Obligation record not found for id " + lObligationId + " for record " + (lPtr));
            if (!lObligationBean.getPayDetail1().equals(lMappingCode))
                throw new CommonBusinessException("NACH code mismatch in record " + (lPtr));
            if (lObligationBean.getAmount()==null || 
            		lAmount==null || 
            		(lObligationBean.getAmount().compareTo(lAmount) != 0 ))
                throw new CommonBusinessException("Amount mismatch in record " + (lPtr));
            if (!lObligationBean.getDate().equals(lDate))
                throw new CommonBusinessException("Date mismatch in record " + (lPtr));
            if (!(PAYINSTATUS_SUCCESS.equalsIgnoreCase(lStatus) || PAYINSTATUS_FAIL.equalsIgnoreCase(lStatus) ) )
                throw new CommonBusinessException("Incorrect Status in record " + (lPtr));
            logger.debug("lStatus : " + lStatus);
            if (PAYINSTATUS_SUCCESS.equalsIgnoreCase(lStatus.trim())) {
                logger.debug("lStatus in success ");
                lObligationBean.setStatus(ObligationBean.Status.Success);
                lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
                lObligationBean.setSettledAmount(lAmount);
                //
                if(ObligationBean.Type.Leg_2.equals(lObligationBean.getType()) || 
                        ObligationBean.Type.Leg_3.equals(lObligationBean.getType())){
                    lKey=lObligationBean.getFuId().toString() + CommonConstants.KEY_SEPARATOR + lObligationBean.getType().getCode();
                    if(!lFULegs.contains(lKey)){
                        lFULegs.add(lKey);
                        lFactoringUnitFilterBean.setId(lObligationBean.getFuId());
                        lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFactoringUnitFilterBean);
                    }
                }
            } else if (PAYINSTATUS_FAIL.equalsIgnoreCase(lStatus.trim())) {
                logger.debug("lStatus in failed ");
                lObligationBean.setStatus(ObligationBean.Status.Failed);
                lObligationBean.setSettledAmount(BigDecimal.ZERO);
                //in case of fail
                if(ObligationBean.Type.Leg_1.equals(lObligationBean.getType())){
                    lFactoringUnitFilterBean.setId(lObligationBean.getFuId());
                    lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFactoringUnitFilterBean);
                }
            }
            lObligationBean.setRespErrorCode(lReasonCode);
            RefCodeValuesBean lRefCodeValuesBean = RefMasterHelper.getInstance().getRefCodeValuesBean(lReasonCode, REFCODE_ICICINACHDESC);
            if (lRefCodeValuesBean != null)
                lObligationBean.setRespRemarks(lRefCodeValuesBean.getDesc());
            else{
            	//if the code desc is not present then displaying the code colon desc.
            	if(CommonUtilities.hasValue(lReasonCode)){
                    lObligationBean.setRespRemarks(lReasonCode + " : " + (lRecord[IDX_DR_RTN_REASONDESC]!=null?lRecord[IDX_DR_RTN_REASONDESC]:""));
            	}
            }
            	
            lObligationBean.setRecordUpdator(pFileUploadBean.getAuId());
            obligationDAO.update(pConnection, lObligationBean, ObligationBean.FIELDGROUP_RETURN);
            // TODO : if leg2 and successful and txnentity is purchaser then get affected limit ids from the factoring unit and reduce utilization
            if ((lObligationBean.getStatus() == ObligationBean.Status.Success) && (lObligationBean.getType() == ObligationBean.Type.Leg_2)) {
                lFactoringUnitFilterBean.setId(lObligationBean.getFuId());
                lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFactoringUnitFilterBean);
                if (lFactoringUnitBean == null)
                    throw new CommonBusinessException("Factoring unit " + lFactoringUnitFilterBean.getId() + " not found");
            }
            paymentSettlor.addObligation(lObligationBean);
        }
    }

    private void uploadNACHCreditReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean, 
        FileUploadBean pFileUploadBean, Map<Long, ObligationBean> pObligationMap) throws Exception {
        List<String> lRecords = (List<String>)pFileUploadBean.getContext();
        int lCount = lRecords.size();
        int[] lHeaderFieldWidths = new int[]{2,7,40,14,9,9,15,3,13,13,8,10,10,3,18,18,11,35,9,2,57};
        int[] lDataFieldWidths = new int[]{2,9,2,3,15,40,9,7,20,13,13,10,10,1,2,11,35,11,18,30,3,15,20,7};
        String[] lHeader = CommonUtilities.splitString(lRecords.get(0), lHeaderFieldWidths);
        // check header
        long lTotalAmount = Long.parseLong(lHeader[9]);
        Long lPfId = Long.valueOf(lHeader[15].trim());
        int lRecordCount = Integer.parseInt(lHeader[18]);
        int lActualRecordCount = 0;
        long lActualTotalAmount = 0;
        if (!pPaymentFileBean.getId().equals(lPfId)) 
            throw new CommonBusinessException("Mismatch in payment file id. Selected : " 
                + pPaymentFileBean.getId() + ", Header Record : " + lPfId);
        if(lHeader==null || lHeader.length != lHeaderFieldWidths.length)
            throw new CommonBusinessException("Record length mismatch in header record.");
        for (int lPtr=1;lPtr<lCount;lPtr++) {
            String[] lRecord = CommonUtilities.splitString(lRecords.get(lPtr), lDataFieldWidths);
            if(lRecord==null || lRecord.length != lDataFieldWidths.length)
                throw new CommonBusinessException("Record length mismatch in record " + (lPtr));
            Long lObId = Long.valueOf(lRecord[19].trim());
            ObligationBean lObligationBean = pObligationMap.remove(lObId);
            if (lObligationBean == null)
                throw new CommonBusinessException("Obligation record not found for id " + lObId + " for record " + (lPtr+1));
            String lAccount = lRecord[16].trim();
            String lIFSC = lRecord[15].trim();
            long lAmount = Long.parseLong(lRecord[10]);
            BigDecimal lDecimalAmount = new BigDecimal(lAmount).divide(AppConstants.HUNDRED, MathContext.DECIMAL128).setScale(2, RoundingMode.HALF_UP);
            String lTxnRefNo = lRecord[11];
            int lStatus = Integer.parseInt(lRecord[13]);
            String lReason = lRecord[14];
            
            if (!lObligationBean.getPayDetail1().equals(lAccount))
                throw new CommonBusinessException("Account No mismatch in record " + (lPtr+1));
            if (!lObligationBean.getPayDetail2().equals(lIFSC))
                throw new CommonBusinessException("IFSC mismatch in record " + (lPtr+1));
            if (lObligationBean.getAmount()==null || 
            		lDecimalAmount==null || 
            		(lObligationBean.getAmount().compareTo(lDecimalAmount) != 0 ))
                throw new CommonBusinessException("Amount mismatch in record " + (lPtr+1));
            if (lStatus == 1) {
                lObligationBean.setStatus(ObligationBean.Status.Success);
                lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
                lObligationBean.setSettledAmount(lDecimalAmount);
            } else {
                lObligationBean.setStatus(ObligationBean.Status.Failed);
                lObligationBean.setSettledAmount(BigDecimal.ZERO);
            }
            lObligationBean.setPaymentRefNo(lTxnRefNo);
            lObligationBean.setRespRemarks(lReason);
            lObligationBean.setRecordUpdator(pFileUploadBean.getAuId());
            obligationDAO.update(pConnection, lObligationBean, ObligationBean.FIELDGROUP_RETURN);
            paymentSettlor.addObligation(lObligationBean);
            lActualRecordCount++;
            lActualTotalAmount += lAmount;
        }
        if (lActualRecordCount != lRecordCount)
            throw new CommonBusinessException("Record count mismatch. Actual : " + lActualRecordCount + " Header : " + lRecordCount);
        if (lActualTotalAmount != lTotalAmount)
            throw new CommonBusinessException("Total amount mismatch. Actual : " + lActualTotalAmount + " Header : " + lTotalAmount);
    }
    
    private void uploadNEFTCreditReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean, 
            FileUploadBean pFileUploadBean, Map<Long, ObligationBean> pObligationMap) throws Exception {
        List<String> lRecords = (List<String>)pFileUploadBean.getContext();
        int lCount = lRecords.size();
        SimpleDateFormat lSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
        int lFailedCount = 0;
        for (int lPtr=0;lPtr<lCount;lPtr++) {
            String[] lRecord = CommonUtilities.splitString(lRecords.get(lPtr), COLUMN_SEPERATOR);
            if(lRecord==null || lRecord.length != 16)
                throw new CommonBusinessException("Record length mismatch in record " + (lPtr+1));
            Long lObId = Long.valueOf(lRecord[1]);
            ObligationBean lObligationBean = pObligationMap.remove(lObId);
            if (lObligationBean == null)
                throw new CommonBusinessException("Obligation record not found for id " + lObId + " for record " + (lPtr));
            String lAccount = lRecord[5];
            String lIFSC = lRecord[6];
            BigDecimal lAmount = NumberUtils.createBigDecimal(lRecord[3]);
            Date lDate = lSimpleDateFormat.parse(lRecord[7]);
            String lReason = lRecord[13];
            String lTxnRefNo = lRecord[14];
            String lStatus = lRecord[15];
            
            if (!lObligationBean.getPayDetail1().equals(lAccount))
                throw new CommonBusinessException("Account No mismatch in record " + (lPtr));
            if (!lObligationBean.getPayDetail2().equals(lIFSC))
                throw new CommonBusinessException("IFSC mismatch in record " + (lPtr));
            if (lObligationBean.getAmount()==null || 
            		lAmount==null || 
            		(lObligationBean.getAmount().compareTo(lAmount) != 0 ))
                throw new CommonBusinessException("Amount mismatch in record " + (lPtr));
            if (!lObligationBean.getDate().equals(lDate))
                throw new CommonBusinessException("Date mismatch in record " + (lPtr));
            if (PAYOUTSTATUS_SUCCESS.equals(lStatus)) {
                lObligationBean.setStatus(ObligationBean.Status.Success);
                lObligationBean.setSettledDate(TredsHelper.getInstance().getBusinessDate());
                lObligationBean.setSettledAmount(lAmount);
            } else {
                lObligationBean.setStatus(ObligationBean.Status.Failed);
                lObligationBean.setSettledAmount(BigDecimal.ZERO);
                lFailedCount++;
            }
            lObligationBean.setPaymentRefNo(lTxnRefNo);
            lObligationBean.setRespRemarks(lReason);
            lObligationBean.setRecordUpdator(pFileUploadBean.getAuId());
            obligationDAO.update(pConnection, lObligationBean, ObligationBean.FIELDGROUP_RETURN);
            paymentSettlor.addObligation(lObligationBean);
        }
        // update factoring units status
        // update instrument status
    }
    
    private String fixedLengthString(String pString, int pLength) {
        if (pString == null)
            pString = "";
        if (pString.length() > pLength)
            pString = pString.substring(0, pLength);
        else
            pString = StringUtils.rightPad(pString, pLength, ' ');
        return pString;
    }
    
    @Override
    public List<IObligation> getObligationListForFile(Connection pConnection, PaymentFileBean pPaymentFileBean, java.sql.Date pDate, boolean pOnlySentObligations) throws Exception {
        StringBuilder lSql = new StringBuilder();
        DBHelper lDbHelper = DBHelper.getInstance();
        //
        lSql.append("SELECT * FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
        lSql.append(" AND OBDATE = ").append(lDbHelper.formatDate(pPaymentFileBean.getDate()));
    	lSql.append(" AND OBTXNTYPE = ").append(lDbHelper.formatString(pPaymentFileBean.getFileType().getCode()));
        if(pOnlySentObligations){
            lSql.append(" AND OBSTATUS = ").append(lDbHelper.formatString(ObligationBean.Status.Sent.getCode()));
        }
        lSql.append(" AND OBPFID = ").append(pPaymentFileBean.getId());
        lSql.append(" ORDER BY OBFileSeqNo ");
        
        List<ObligationBean> lList = obligationDAO.findListFromSql(pConnection, lSql.toString(), 0);
        List<IObligation> lObligationList = new ArrayList<IObligation>();
        if ((lList == null) || (lList.size() == 0))
            throw new CommonBusinessException("No obligations");
        for (int lPtr=0; lPtr<lList.size(); lPtr++){
        	IObligation IBean = lList.get(lPtr);
        	lObligationList.add(IBean);
        }
        return lObligationList;
    }

	@Override
	public void processUploadedReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean,
			AppUserBean pUserBean) {

	}

	@Override
	public byte[] createExcellFile(ExecutionContext pExecutionContext, java.sql.Date pDate,AppUserBean pAppUserBean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
    
}
