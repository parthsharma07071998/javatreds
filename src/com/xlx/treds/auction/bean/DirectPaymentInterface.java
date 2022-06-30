package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.common.base.CommonConstants;
import com.xlx.common.memdb.MemoryDBManager;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.user.bean.AppUserBean;



public class DirectPaymentInterface implements IPaymentInterface {
	private CompositeGenericDAO<ObligationDetailBean> obligationDetailDAO;
	private GenericDAO<PaymentFileBean> paymentFileDAO;
	private GenericDAO<ObligationBean> obligationDAO;
	private GenericDAO<ObligationSplitsBean> obligationSplitsDAO;
	private IPaymentSettlor paymentSettlor = null;
	
	public DirectPaymentInterface() {
		super();
		paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
		obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
		obligationSplitsDAO = new GenericDAO<ObligationSplitsBean>(ObligationSplitsBean.class);
		obligationDetailDAO = new CompositeGenericDAO<ObligationDetailBean>(ObligationDetailBean.class);
	}

	@Override
	public void generateFile(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean)
			throws Exception {
		generateFiles(pConnection, pPaymentFileBean, pAppUserBean);
		if(pPaymentFileBean!=null && pPaymentFileBean.getId()!=null){
			//emailGeneratorBO.sendObligationsDueDetails(pConnection, pPaymentFileBean.getId(), pPaymentFileBean.getDate());
		}
	}

	@Override
	public PaymentFileBean getFileContents(Connection pConnection, PaymentFileBean pPaymentFileBean,
			AppUserBean pAppUserBean) throws Exception {
		// TODO Auto-generated method stub
		return pPaymentFileBean;
	}

	@Override
	public void tempCreateFile(List<IObligation> pListObligation, PaymentFileBean pPaymentFileBean) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getQuery(PaymentFileBean pPaymentFileBean) {
		 StringBuilder lSql = new StringBuilder();
         DBHelper lDbHelper = DBHelper.getInstance();
         //
    	 // leftouter join CompanyLocations, CompanyBankDetails
         lSql.append("SELECT * FROM OBLIGATIONS ");
         lSql.append(" left outer join ObligationSplits on OBID=OBSOBID ");
         lSql.append(" WHERE OBRECORDVERSION > 0 AND OBSRECORDVERSION > 0");
         lSql.append(" AND OBDATE = ").append(lDbHelper.formatDate(pPaymentFileBean.getDate()));
         lSql.append(" AND OBSTATUS IN ( ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
         lSql.append(" , ").append(lDbHelper.formatString(ObligationBean.Status.Created.getCode())).append(" ) ");
         lSql.append(" AND OBSSTATUS IN ( ").append(lDbHelper.formatString(ObligationBean.Status.Ready.getCode()));
         lSql.append(" , ").append(lDbHelper.formatString(ObligationBean.Status.Created.getCode())).append(" ) ");
         lSql.append(" AND OBSPAYMENTSETTLOR = ").append(lDbHelper.formatString(pPaymentFileBean.getFacilitator()));
         lSql.append(" ORDER BY OBFUID, OBSOBID, OBTYPE, OBTXNTYPE DESC, OBSPARTNUMBER, OBDATE  "); //Debit transc first then Credit transactions

         return lSql.toString();
	}

	 private void generateFiles(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception {
	    	//seperate function since we will be generating multiple payment files.
	    	//pPaymentFileBean is just a dummy file containing date, facilitator and txnType
	    	//we should generate multiple file from the same
	        String  lSql = null;
	        DBHelper lDbHelper = DBHelper.getInstance();
	        //
	    	lSql = getQuery(pPaymentFileBean);
	    	BigDecimal lTotalValue = BigDecimal.ZERO;
	        
	        List<ObligationDetailBean> lObligationList = obligationDetailDAO.findListFromSql(pConnection, lSql, 0);
	        if ((lObligationList == null) || (lObligationList.size() == 0))
	            throw new CommonBusinessException("No obligations");

	        // cache entity mappings
	        MemoryTable lMemoryTable = MemoryDBManager.getInstance().getTable(AppEntityBean.ENTITY_NAME);

	        IObligation lObligationBean = null;
	        IObligation lParentObligationBean = null;
	        //

	        HashSet<Long> lUpdatedParentObliList = new HashSet<Long>();
	        //
	        //default setting before starting loop
	        pPaymentFileBean.setId(lDbHelper.getUniqueNumber(pConnection, "PaymentFile.Id"));
	        //
	        if(lObligationList!= null && lObligationList.size() > 0){
	        	Map<String, FacilitatorEntityMappingBean>  lFacilitatorMap = TredsHelper.getInstance().getFacilitatorEntityMap(pConnection, AppConstants.FACILITATOR_NPCI);	   		
	        	Map<Long, CompanyBankDetailBean> lDesignatedBankMap = TredsHelper.getInstance().getEntityBankDetailsMap(pConnection);	
	        	CompanyBankDetailBean lCoBankDetailBean = null;
		        for(ObligationDetailBean lODBean : lObligationList){
		    		lObligationBean = lODBean.getObligationSplitsBean(); //if split then split object , if not split then parent object ie. the actual obligaion will be returned
		    		lParentObligationBean = lODBean.getObligationBean();
		        	lObligationBean.setParentObligation(lParentObligationBean); 
		        	lCoBankDetailBean = TredsHelper.getInstance().getSettlementBank(pConnection, lParentObligationBean.getTxnEntity(), lParentObligationBean.getSettlementCLId());
		            lObligationBean.setRecordUpdator(pAppUserBean.getId());
		            //
		            updateObligation(pConnection, lObligationBean, pPaymentFileBean, lFacilitatorMap, lDesignatedBankMap, lMemoryTable, lCoBankDetailBean);
		            if(!lObligationBean.isParentObligation()){
		            	if(!lUpdatedParentObliList.contains(lParentObligationBean.getId())){
		                	lUpdatedParentObliList.add(lParentObligationBean.getId());
		                    updateObligation(pConnection, lParentObligationBean, pPaymentFileBean, lFacilitatorMap, lDesignatedBankMap, lMemoryTable, lCoBankDetailBean);
		            	}
		            }
		            lTotalValue = lTotalValue.add(lObligationBean.getAmount());
		        }
	        }
            pPaymentFileBean.setFileName(getFileName(pPaymentFileBean));
            pPaymentFileBean.setRecordCount(Long.valueOf(lObligationList.size()));
            pPaymentFileBean.setTotalValue(lTotalValue);
            pPaymentFileBean.setGeneratedByAuId(pAppUserBean.getId());
            pPaymentFileBean.setGeneratedTime(new Timestamp(System.currentTimeMillis()));
            pPaymentFileBean.setStatus(PaymentFileBean.Status.Return_File_Uploaded);
            paymentFileDAO.insert(pConnection, pPaymentFileBean);
  
	    }
		
	
	@Override
	public String getFileName(PaymentFileBean pPaymentFileBean) throws CommonBusinessException {
		String lFileName = null;
    	//TODO: : TRA-Bank Short code-Uploading user name-DDMMYYYY-Sequence number-INP.csv
        lFileName = "DUMMYFILE";
        return lFileName;
	}

	@Override
	public List<IObligation> getObligationListForFile(Connection pConnection, PaymentFileBean pPaymentFileBean, Date pDate, boolean pOnlySentObligations) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileContentAsString(PaymentFileBean pPaymentFileBean, List<IObligation> pObligationList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateObligation(Connection pConnection, IObligation pObligationBean, PaymentFileBean pPaymentFileBean,
			Map<String, FacilitatorEntityMappingBean> pFacilitatorMap,
			Map<Long, CompanyBankDetailBean> pDesignatedBankMap, MemoryTable pMemoryTable,
			CompanyBankDetailBean pCompanyBankDetailBean) throws Exception {
        IObligation lObligationBean = pObligationBean;
        AppEntityBean lAppEntityBean = null;
        Long lCdId = null;
	   	if(!lObligationBean.isParentObligation()){
	   		lObligationBean.setStatus(ObligationBean.Status.Success);
		   	lObligationBean.setSettledAmount(lObligationBean.getAmount());
		   	lObligationBean.setSettledDate(new Date(System.currentTimeMillis()));
	   	}
	   	if (pDesignatedBankMap==null){
	   		pDesignatedBankMap = TredsHelper.getInstance().getEntityBankDetailsMap(pConnection);	
	   	}
	   	if (pFacilitatorMap==null){
	   		pFacilitatorMap = TredsHelper.getInstance().getFacilitatorEntityMap(pConnection, AppConstants.FACILITATOR_NPCI);	   		
	   	}
	    lObligationBean.setPfId(pPaymentFileBean.getId());
	    lObligationBean.setRecordUpdateTime(new Timestamp(System.currentTimeMillis()));
	    if(lObligationBean.isParentObligation()){
	    	lObligationBean.setStatus(ObligationBean.Status.Sent);
	    	if(AppConstants.DOMAIN_PLATFORM.equals(lObligationBean.getTxnEntity())){
				 lCdId = new Long(0);
			 }else{
		         lAppEntityBean = (AppEntityBean)pMemoryTable.selectSingleRow(AppEntityBean.f_Code, new String[]{lObligationBean.getTxnEntity()});
		         lCdId = lAppEntityBean.getCdId();
			 }
	         CompanyBankDetailBean lDesignatedBank = null;
	    	 if(lObligationBean.getSettlementCLId()!=null && pCompanyBankDetailBean != null ){ 
	    		 lDesignatedBank = pCompanyBankDetailBean;
	    	 }else{
	             lDesignatedBank = pDesignatedBankMap.get(lCdId);
	    	 }
	         if(lDesignatedBank == null || lDesignatedBank.getId() == null)
	             throw new CommonBusinessException("Designated Bank for " + lObligationBean.getTxnEntity() + " not found");
	         lObligationBean.setPayDetail1(lDesignatedBank.getAccNo()); 
	         lObligationBean.setPayDetail2(lDesignatedBank.getIfsc());
	         if(lAppEntityBean!=null){
		    lObligationBean.setPayDetail3(lAppEntityBean.getName());
	         }else{
	             lObligationBean.setPayDetail3(lDesignatedBank.getFirstName()); 
	         }
		   	 if(lAppEntityBean!=null){
		   		 if(lAppEntityBean.isPurchaser() || lAppEntityBean.isFinancier()){
					 Date lExpiry = null;
		   			 String lKey = lObligationBean.getTxnEntity() + CommonConstants.KEY_SEPARATOR + lDesignatedBank.getId();
		   			 FacilitatorEntityMappingBean lFEMBean = pFacilitatorMap.get(lKey);
			         String lMappingCode = (lFEMBean != null)?lFEMBean.getMappingCode():"";
			         if (StringUtils.isBlank(lMappingCode))
			             throw new CommonBusinessException("Mapping code for " + lObligationBean.getTxnEntity() + " not found.");
			         lObligationBean.setPayDetail4(lMappingCode);
			         lExpiry = pFacilitatorMap.get(lKey).getExpiry();
			         if(lExpiry!=null && lExpiry.before(lObligationBean.getDate())){
			        	 throw new CommonBusinessException("Mapping code for " + lObligationBean.getTxnEntity() + " is  expired.");
			         }
		   		 }
		   	 }
       	 obligationDAO.update(pConnection, (ObligationBean)lObligationBean, ObligationBean.FIELDGROUP_GENERATE);
        }else{
       	 obligationSplitsDAO.update(pConnection, (ObligationSplitsBean)lObligationBean, ObligationBean.FIELDGROUP_GENERATE_DIRECT);
        }

	}

	@Override
	public void uploadReturnFile(FileUploadBean pFileUploadBean) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void processUploadedReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean,
			AppUserBean pUserBean) throws Exception {
        if (PaymentFileBean.Status.Generated.equals(pPaymentFileBean.getStatus())){
        	throw new CommonBusinessException("Please select a valid file for processing.");
        }else if (PaymentFileBean.Status.Return_File_Processed.equals(pPaymentFileBean.getStatus())){
        	throw new CommonBusinessException("File already processed.");
        }
		paymentSettlor = new DirectPaymentSettlor(pPaymentFileBean);
		paymentSettlor.process(pConnection);
    	//if(CommonUtilities.hasValue(paymentSettlor.getRemarks()))
    		//pFileUploadBean.setRemarks(paymentSettlor.getRemarks()); ASK SIR
		List <Long> lUnprocessedObIds = paymentSettlor.getObligationListReceivedForProcessing();
		if(lUnprocessedObIds!=null){
			//DIRECT - WE WILL NOT SEND LEG 1 STATUS TRANSACTION MAIL - 11-02-2019
			//emailGeneratorBO.sendLeg1StatusTransactionDetails(pConnection, pPaymentFileBean.getDate(), lUnprocessedObIds);
		}
    	//pPaymentFileBean.setStatus(PaymentFileBean.Status.Return_File_Processed);
    	//add audit for who processed?????  ASK SIR
        //pPaymentFileBean.setReturnUploadedByAuId(pFileUploadBean.getAuId()); 
        pPaymentFileBean.setReturnUploadedTime(new Timestamp(System.currentTimeMillis()));
        paymentFileDAO.update(pConnection, pPaymentFileBean, PaymentFileBean.FIELDGROUP_RETURN);
	}

	@Override
	public byte[] createExcellFile(ExecutionContext pExecutionContext, Date pDate ,AppUserBean pAppUserBean) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
