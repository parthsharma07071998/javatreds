package com.xlx.treds.auction.bean;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

public interface IPaymentSettlor {

	String getRemarks();

	void addObligation(IObligation pObligation);

	void process(Connection pConnection) throws Exception;

	//The paymentFileId or the Leg1SettlmentDate
	//If the paymentFileId is received - find the Leg1 Success obligation from the same
	//If the paymentFileId is NOT received - the Leg1SettlementDate is used to find the paymentFileId for that day and 
	//the obligations of all the paymentFileIds for that day are used	
	void generateNoticeOfAssignment(Connection pConnection, Long pPaymentFileId, Date pLeg1SettledDate);

	List<IObligation> getUnProcessedObligations();
	
	List<Long> getObligationListReceivedForProcessing();
}