package com.xlx.treds.auction.bean;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.xlx.common.memdb.MemoryTable;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.bean.FileUploadBean;
import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.user.bean.AppUserBean;

public interface IPaymentInterface {
	
	void generateFile(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception;

	PaymentFileBean getFileContents(Connection pConnection, PaymentFileBean pPaymentFileBean, AppUserBean pAppUserBean) throws Exception;
	
	//Helper functions
	void tempCreateFile(List<IObligation> pListObligation, PaymentFileBean pPaymentFileBean) ;

	String getQuery(PaymentFileBean pPaymentFileBean);

	String getFileName(PaymentFileBean pPaymentFileBean) throws CommonBusinessException;
	
	List<IObligation> getObligationListForFile(Connection pConnection, PaymentFileBean pPaymentFileBean, Date pDate, boolean pOnlySentObligations) throws Exception;
	
	String getFileContentAsString(PaymentFileBean pPaymentFileBean, List<IObligation> pObligationList);

	void updateObligation(Connection pConnection, IObligation pObligationBean, PaymentFileBean pPaymentFileBean, Map<String, FacilitatorEntityMappingBean> pFacilitatorMap, Map<Long, CompanyBankDetailBean> pDesignatedBankMap, MemoryTable pMemoryTable, CompanyBankDetailBean pCompanyBankDetailBean) throws Exception;
	
	void uploadReturnFile(FileUploadBean pFileUploadBean) throws Exception;
	
	public void processUploadedReturnFile(Connection pConnection, PaymentFileBean pPaymentFileBean ,AppUserBean pUserBean)throws Exception;

	public byte[] createExcellFile(ExecutionContext pExecutionContext, Date pDate,AppUserBean pUserBean) throws Exception;
}
