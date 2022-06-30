package com.xlx.treds.auction.bean;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.common.memdb.MemoryDBException;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants;
import com.xlx.treds.MonetagoTredsHelper;
import com.xlx.treds.OtherResourceCache;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.ObligationBean.Status;
import com.xlx.treds.auction.bo.PurchaserSupplierLimitUtilizationBO;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bo.FactoringUnitBO;
import com.xlx.treds.user.bean.AppUserBean;

public class PaymentSettlor implements IPaymentSettlor {
    private static final Logger logger = LoggerFactory.getLogger(PaymentSettlor.class);
	//
	private PaymentFileBean paymentFileBean;
	private ObligationBean.TxnType txnType;
	private String facilitator;
	//
	private HashMap<String, HashMap<ObligationBean.Type, HashMap<EntityType,IObligation>>> data = null; //Key1=FUId, Key2=Leg
	//this is the data (transaction type ) which does not belong to the Payment transaction in question ie. Debit/Credit
	private List<IObligation> dataSkipped = null; 
	//
	//Helper variables
	//temp entity list for determining type
	private HashMap<String, PaymentSettlor.EntityType> appEntitiesType = null;	//Key=EntityCode/Domain
	private TredsHelper tredsHelper = null;
    private GenericDAO<ObligationBean> obligationDAO;
    private Connection connection = null;
    private List<Long> newObligationList = null;
    private String remarks = null;
	//
    private static final int IDX_BULK_CANCEL_LEG1_CR = 0; 
    private static final int IDX_BULK_CANCEL_LEG2_DR_CR = 1; 
    private static final int IDX_BULK_READY_SELLER_CR = 2; 
    private static final int IDX_BULK_READY_LEG1_TREDS_CR = 3; 
    private static final int IDX_BULK_READY_LEG2_DR = 4; 
    private static final int IDX_BULK_READY_LEG2_FIN_CR = 5;
    private static final int IDX_BULK_SIZE = 6;
	//
    private GenericDAO<FactoringUnitBean> factoringUnitDAO;
    private GenericDAO<BidBean> bidDAO;
    private PurchaserSupplierLimitUtilizationBO purchaserSupplierLimitUtilizationBO;
    private FactoringUnitBO factoringUnitBO;
    private GenericDAO<PaymentFileBean> paymentFileDAO;
    private CompositeGenericDAO<AssignmentNoticeInfo> assignmentNoticeInfoDAO;
    private GenericDAO<AssignmentNoticesBean> assignmentNoticesDAO;
    private GenericDAO<AssignmentNoticeDetailsBean> assignmentNoticeDetailsDAO;
    private GenericDAO<AssignmentNoticeGroupDetailsBean> assignmentNoticeGroupDetailsDAO;
    private GenericDAO<InstrumentBean> instrumentDAO;
    //
	//FU STATUS UPDATE
	private static int IDX_RESULT_LEG1_SUCCESS = 0;
	private static int IDX_RESULT_LEG1_CREDIT_FAIL = 1;
	private static int IDX_RESULT_LEG1_DEBIT_FAIL = 2;
	private static int IDX_RESULT_LEG2_SUCCESS = 3;
	private static int IDX_RESULT_LEG2_FAIL = 4;
	private static int IDX_RESULT_SIZE = 5;
	//
	//FU STATUS UPDATE
	//Leg 1 Success	Seller Credit Leg1 is success and Old Status is Factored
	//Leg 1 Fail	Seller Credit Leg1 is Failed or Cancelled and Old Status is Factored
	//Leg 1 Fail	(Financier Debit)/(Buyer Debit) Leg1 is Failed or Cancelled and Old Status is Factored
	//Leg 2 Success	Buyer Debit Leg2 is success and Old Status is Leg1 Success
	//Leg 2 Fail	Buyer Debit Leg2 is Failed and Old Status is Leg1 Success
	private static String[] resultText = new String[] {"Leg1 Success", "Leg1 Fail", "Leg1 Fail", "Leg2 Success", "Leg2 Fail"};
	private static String[] resultOldFUStatus = new String[] {FactoringUnitBean.Status.Factored.getCode(), FactoringUnitBean.Status.Factored.getCode(), FactoringUnitBean.Status.Factored.getCode(), FactoringUnitBean.Status.Leg_1_Settled.getCode(), FactoringUnitBean.Status.Leg_1_Settled.getCode() };
	private static String[] resultNewFUStatus = new String[] {FactoringUnitBean.Status.Leg_1_Settled.getCode(), FactoringUnitBean.Status.Leg_1_Failed.getCode(), FactoringUnitBean.Status.Leg_1_Failed.getCode(), FactoringUnitBean.Status.Leg_2_Settled.getCode(), FactoringUnitBean.Status.Leg_2_Failed.getCode() };
	private static String[] resultObliType = new String[] {ObligationBean.Type.Leg_1.getCode(), ObligationBean.Type.Leg_1.getCode(), ObligationBean.Type.Leg_1.getCode(), ObligationBean.Type.Leg_2.getCode(), ObligationBean.Type.Leg_2.getCode()};
	private static String[] resultObliTxnType = new String[] {ObligationBean.TxnType.Credit.getCode(), ObligationBean.TxnType.Credit.getCode(), ObligationBean.TxnType.Debit.getCode(), ObligationBean.TxnType.Debit.getCode(), ObligationBean.TxnType.Debit.getCode()};
	private static String[] resultObliStatus = new String[] {ObligationBean.Status.Success.getCode(), ObligationBean.Status.Failed.getCode(), ObligationBean.Status.Failed.getCode(), ObligationBean.Status.Success.getCode(), ObligationBean.Status.Failed.getCode()};
	//
	private static String[] resultOldInstStatus = new String[] {InstrumentBean.Status.Factored.getCode(), InstrumentBean.Status.Factored.getCode(), InstrumentBean.Status.Factored.getCode(), InstrumentBean.Status.Leg_1_Settled.getCode(), InstrumentBean.Status.Leg_1_Settled.getCode() };
	private static String[] resultNewInstStatus = new String[] {InstrumentBean.Status.Leg_1_Settled.getCode(), InstrumentBean.Status.Leg_1_Failed.getCode(), InstrumentBean.Status.Leg_1_Failed.getCode(), InstrumentBean.Status.Leg_2_Settled.getCode(), InstrumentBean.Status.Leg_2_Failed.getCode() };
    //
    public static enum EntityType implements IKeyValEnumInterface<String> {
        Treds("T"), Financier("F"), Buyer("B"), Seller("S");
        private final String code;
        private EntityType(String pCode) {
            code = pCode;
        }
        public String getCode() {
            return code;
        }
    }
	//
	public PaymentSettlor(PaymentFileBean pPaymentFileBean) throws Exception{
		paymentFileBean = pPaymentFileBean;
		txnType = pPaymentFileBean.getFileType();
		facilitator = pPaymentFileBean.getFacilitator();
		//
		if(AppConstants.FACILITATOR_NPCI.equals(facilitator) ||
				txnType!=null){
			data = new HashMap<String,  HashMap<ObligationBean.Type, HashMap<EntityType,IObligation>>>();
			dataSkipped = new ArrayList<IObligation>();
		}else{
			throw new Exception("Invalid Transaction Type.");
		}
		//
		tredsHelper = TredsHelper.getInstance();
        obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
        appEntitiesType = new HashMap<String, PaymentSettlor.EntityType>();
        newObligationList = new ArrayList<Long>();
        //
        purchaserSupplierLimitUtilizationBO = new PurchaserSupplierLimitUtilizationBO();
        factoringUnitBO = new FactoringUnitBO();
        factoringUnitDAO = new GenericDAO<FactoringUnitBean>(FactoringUnitBean.class);
        bidDAO = new GenericDAO<BidBean>(BidBean.class);
        paymentFileDAO = new GenericDAO<PaymentFileBean>(PaymentFileBean.class);
        assignmentNoticesDAO = new GenericDAO<AssignmentNoticesBean>(AssignmentNoticesBean.class);
        assignmentNoticeDetailsDAO = new GenericDAO<AssignmentNoticeDetailsBean>(AssignmentNoticeDetailsBean.class);
        assignmentNoticeGroupDetailsDAO = new GenericDAO<AssignmentNoticeGroupDetailsBean>(AssignmentNoticeGroupDetailsBean.class);
        assignmentNoticeInfoDAO = new CompositeGenericDAO<AssignmentNoticeInfo>(AssignmentNoticeInfo.class);
        instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
	}
	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#getRemarks()
	 */
	@Override
	public String getRemarks(){
		return remarks;
	}

	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#addObligation(com.xlx.treds.auction.bean.IObligation)
	 */
	@Override
	public void addObligation(IObligation pObligation){
		if(pObligation !=null){
			HashMap<ObligationBean.Type, HashMap<EntityType,IObligation>> lLegwiseEntityObligations = null;
			HashMap<EntityType,IObligation> lEntityTypewiseObligations = null;
			PaymentSettlor.EntityType lEntityType = null;
			if(AppConstants.FACILITATOR_NPCI.equals(facilitator) ||
					txnType.equals(pObligation.getTxnType())){
				lEntityType = getEntityType(pObligation.getTxnEntity());
				if(lEntityType!=null){
					if(data.containsKey(pObligation.getFuId().toString())){
						lLegwiseEntityObligations = data.get(pObligation.getFuId().toString());
					}else{
						lLegwiseEntityObligations = new HashMap<ObligationBean.Type, HashMap<EntityType,IObligation>>();
						data.put(pObligation.getFuId().toString(), lLegwiseEntityObligations);
					}
					if(lLegwiseEntityObligations.containsKey(pObligation.getType())){
						lEntityTypewiseObligations = lLegwiseEntityObligations.get(pObligation.getType());
					}else{
						lEntityTypewiseObligations = new HashMap<EntityType,IObligation>();
						lLegwiseEntityObligations.put(pObligation.getType(), lEntityTypewiseObligations);
					}
					if(!lEntityTypewiseObligations.containsKey(lEntityType)){
						lEntityTypewiseObligations.put(lEntityType,pObligation);
					}else{
						dataSkipped.add(pObligation); //duplicate???? - not possible
					}
				}else{
					dataSkipped.add(pObligation); //entity type null - not possible
				}
			}else{
				dataSkipped.add(pObligation); //NOT of the Transaction Type (D/C) 
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#process(java.sql.Connection)
	 */
	@Override
	public void process(Connection pConnection) throws Exception{
		connection = pConnection;
		newObligationList = new ArrayList<Long>();
		//
		HashMap<ObligationBean.Type, HashMap<EntityType,IObligation>> lLegwiseEntityObligations = null;
		HashMap<EntityType,IObligation> lEntityTypewiseObligations = null;
		//Leg 1 Obligations
		IObligation lObliFin = null, lObliBuy = null; //Leg1-Debit and Leg2 Buy-D , Fin-C
		IObligation lObliSell = null, lObliTreds = null; //Leg1, Credit
		//
		Boolean[] lBulkUpdates = new Boolean[IDX_BULK_SIZE];
		for(int lPtr=0; lPtr < lBulkUpdates.length; lPtr++){
			lBulkUpdates[lPtr] = Boolean.FALSE;
		}
		//
		List<Long> lFUReleaseLimit = new ArrayList<Long>();
		List<Long> lFULeg1Failed = new ArrayList<Long>();
		//
		//loop through data and check whether there are any mismatches which needs to be handled
		for(String pFuIds : data.keySet()){
			lLegwiseEntityObligations = data.get(pFuIds);
			if(lLegwiseEntityObligations!=null){
				for(ObligationBean.Type lObliType : lLegwiseEntityObligations.keySet()){
					lEntityTypewiseObligations = lLegwiseEntityObligations.get(lObliType);
					if(lEntityTypewiseObligations!=null){
						if(ObligationBean.Type.Leg_1.equals(lObliType)){
							if(ObligationBean.TxnType.Debit.equals(txnType) || AppConstants.FACILITATOR_NPCI.equals(facilitator)){
								if(AppConstants.FACILITATOR_ICICI.equals(facilitator) || AppConstants.FACILITATOR_NPCI.equals(facilitator)){
									lObliFin = lEntityTypewiseObligations.get(EntityType.Financier);
									lObliBuy = lEntityTypewiseObligations.get(EntityType.Buyer);
									//
									//if the obligation is has not come in the file then find the same 
									//find the obligation
									if(lObliBuy==null){
										lObliBuy = getSiblingObligation(lObliFin);
									}
									if(lObliFin==null){
										lObliFin = getSiblingObligation(lObliBuy);
									}
									//
									//Financier debit fails
									//Buy debit fails
									//Financier debit success
									//Buy debit success
									if (lObliFin!=null && ObligationBean.Status.Failed.equals(lObliFin.getStatus())){
										if(lObliBuy!=null && ObligationBean.Status.Success.equals(lObliBuy.getStatus())){
											//if buyer debit exists and is success then reverse buyer debit using NEFT
											reverseObligation(lObliBuy);
										}
										//BULK UPDATE 
										//cancel all leg 1 credits
										// cancel all leg2 debits and credits
										lBulkUpdates[IDX_BULK_CANCEL_LEG1_CR] = Boolean.TRUE;
										lBulkUpdates[IDX_BULK_CANCEL_LEG2_DR_CR] = Boolean.TRUE;
										if(!lFUReleaseLimit.contains(lObliFin.getFuId())) {
											lFUReleaseLimit.add(lObliFin.getFuId());
										}
										if(!lFULeg1Failed.contains(lObliFin.getFuId())) {
											lFULeg1Failed.add(lObliFin.getFuId());
										}
									}
									if (lObliBuy!=null && ObligationBean.Status.Failed.equals(lObliBuy.getStatus())){
										if(lObliFin!=null && ObligationBean.Status.Success.equals(lObliFin.getStatus())){
											//if financier debit was success then reverse it using NEFT
											reverseObligation(lObliFin);
										}
										//BULK UPDATE 
										//cancel all leg 1 credits
										// cancel all leg2 debits and credits
										lBulkUpdates[IDX_BULK_CANCEL_LEG1_CR] = Boolean.TRUE;
										lBulkUpdates[IDX_BULK_CANCEL_LEG2_DR_CR] = Boolean.TRUE;
										if(!lFUReleaseLimit.contains(lObliFin.getFuId())) {
											lFUReleaseLimit.add(lObliFin.getFuId());
										}
										if(!lFULeg1Failed.contains(lObliFin.getFuId())) {
											lFULeg1Failed.add(lObliFin.getFuId());
										}
									}
									if (lObliFin!=null && ObligationBean.Status.Success.equals(lObliFin.getStatus())){
										//if buyer debit exists and is failed then reverse financier debit using NEFT
										if(lObliBuy!=null && ObligationBean.Status.Failed.equals(lObliBuy.getStatus())){
											//if buyer debit exists and is failed then reverse financier debit using NEFT
											reverseObligation(lObliFin);
										}else if (lObliBuy==null){
											//BULK UPDATE 
											//else set leg 1 seller credit to ready
										    lBulkUpdates[IDX_BULK_READY_SELLER_CR] = Boolean.TRUE;
										}
									}
									if (lObliBuy!=null && ObligationBean.Status.Success.equals(lObliBuy.getStatus())){
										if(lObliFin!=null && ObligationBean.Status.Failed.equals(lObliFin.getStatus())){
											//if financier debit was falied then reverse buyer debit  using NEFT
											reverseObligation(lObliBuy);
										}else if(lObliFin!=null && ObligationBean.Status.Success.equals(lObliFin.getStatus())){
											//BULK UPDATE 
											//else set leg 1 seller credit to ready
											lBulkUpdates[IDX_BULK_READY_SELLER_CR] = Boolean.TRUE;
										}
									}
								}// end if facilitator
							}// end if txntype = debit
							if (ObligationBean.TxnType.Credit.equals(txnType) || AppConstants.FACILITATOR_NPCI.equals(facilitator)) {
								lObliSell = lEntityTypewiseObligations.get(EntityType.Seller);
								lObliTreds = lEntityTypewiseObligations.get(EntityType.Treds);
								//
								//if the obligation is has not come in the file then find the same 
								//find the obligation
								if(lObliSell==null){
									lObliSell = getSiblingObligation(lObliTreds);
								}
								if(lObliTreds==null){
									lObliTreds = getSiblingObligation(lObliSell);
								}
								//
								if(AppConstants.FACILITATOR_ICICI.equals(facilitator) || AppConstants.FACILITATOR_NPCI.equals(facilitator)){
									//Sell Credit fails
									//Sell Credit success
									if (lObliSell!=null && ObligationBean.Status.Failed.equals(lObliSell.getStatus())){
									    lObliFin = lEntityTypewiseObligations.get(EntityType.Financier);
									    lObliBuy = lEntityTypewiseObligations.get(EntityType.Buyer);
									    if (lObliFin == null)
									        lObliFin = getObligation(lObliSell.getFuId(), ObligationBean.TxnType.Debit, EntityType.Financier, ObligationBean.Type.Leg_1);
									    if (lObliBuy == null)
									        lObliBuy = getObligation(lObliSell.getFuId(), ObligationBean.TxnType.Debit, EntityType.Buyer, ObligationBean.Type.Leg_1);
										//reverse financier debit using NEFT
										//if buyer debit exists then reverse buyer debit using NEFT
										reverseObligation(lObliFin);
										reverseObligation(lObliBuy);
										//BULK UPDATE 
										//cancel all leg1 credits if any other than this
										//cancel all leg2 debits and credits
										lBulkUpdates[IDX_BULK_CANCEL_LEG1_CR] = Boolean.TRUE;
										lBulkUpdates[IDX_BULK_CANCEL_LEG2_DR_CR] = Boolean.TRUE;
										if(!lFUReleaseLimit.contains(lObliSell.getFuId())) {
											lFUReleaseLimit.add(lObliSell.getFuId());
										}
										if(!lFULeg1Failed.contains(lObliFin.getFuId())) {
											lFULeg1Failed.add(lObliFin.getFuId());
										}
									}else if (lObliSell!=null && ObligationBean.Status.Success.equals(lObliSell.getStatus())){
										//BULK UPDATE 
										//set leg1 treds credit to ready
										//set leg2 debits to ready
										lBulkUpdates[IDX_BULK_READY_LEG1_TREDS_CR] = Boolean.TRUE;
										lBulkUpdates[IDX_BULK_READY_LEG2_DR] = Boolean.TRUE;
									}
								}
								if(AppConstants.FACILITATOR_ICICINEFT.equals(facilitator)){
									if (lObliTreds!=null && ObligationBean.Status.Failed.equals(lObliTreds.getStatus())){
										//generate new obligation for T+1 to be sent using NEFT
										recreateObligation(lObliTreds);
										//BULK UPDATE 
										//set leg1 treds credit to ready
										//set leg2 debits to ready
										lBulkUpdates[IDX_BULK_READY_LEG1_TREDS_CR] = Boolean.TRUE;
										lBulkUpdates[IDX_BULK_READY_LEG2_DR] = Boolean.TRUE;
									}else if (lObliTreds!=null && ObligationBean.Status.Success.equals(lObliTreds.getStatus())){
										//DO NOTHING
									}
								} // end if facilitator
							} // end if txntype = credit
						}else if(ObligationBean.Type.Leg_2.equals(lObliType)){
							if(ObligationBean.TxnType.Debit.equals(txnType) || AppConstants.FACILITATOR_NPCI.equals(facilitator)){
								lObliBuy = lEntityTypewiseObligations.get(EntityType.Buyer);
								//
								if(AppConstants.FACILITATOR_ICICI.equals(facilitator) || AppConstants.FACILITATOR_NPCI.equals(facilitator)){
									//Buyer Debit Fails
									//Buyer Debit Success
									if (lObliBuy!=null && ObligationBean.Status.Failed.equals(lObliBuy.getStatus())){
										//DO NOTHING
									}else if (lObliBuy!=null && ObligationBean.Status.Success.equals(lObliBuy.getStatus())){
										//BULK UPDATE 
										//set leg2 fin credit as ready
										lBulkUpdates[IDX_BULK_READY_LEG2_FIN_CR] = Boolean.TRUE;
										if(!lFUReleaseLimit.contains(lObliBuy.getFuId())) {
											lFUReleaseLimit.add(lObliBuy.getFuId());
										}
									}
								}
							}
							if(ObligationBean.TxnType.Credit.equals(txnType)){
								lObliFin = lEntityTypewiseObligations.get(EntityType.Financier);
								//
								if(AppConstants.FACILITATOR_ICICI.equals(facilitator) ||
										AppConstants.FACILITATOR_ICICINEFT.equals(facilitator)){
									//Financier Credit fails
									//Financier Credit success
									if (lObliFin!=null && ObligationBean.Status.Failed.equals(lObliFin.getStatus())){
										//generate new obligation for T+1 to be sent using NEFT
										recreateObligation(lObliFin);
									}else if (lObliFin!=null && ObligationBean.Status.Success.equals(lObliFin.getStatus())){
										//DO NOTHING
									}
								}
							}
						}else if(ObligationBean.Type.Leg_3.equals(lObliType)){
						}						
					}
				}
			}
		}
        if(lBulkUpdates[IDX_BULK_CANCEL_LEG1_CR] || lBulkUpdates[IDX_BULK_CANCEL_LEG2_DR_CR]){
            cancelSibling();
        }
		if (!AppConstants.FACILITATOR_NPCI.equals(facilitator)) {
    	    if(lBulkUpdates[IDX_BULK_READY_SELLER_CR]){
    	    	markSellerCreditReady();
    	    }
    	    if(lBulkUpdates[IDX_BULK_READY_LEG1_TREDS_CR]){
    	    	markTredsCreditReady();
    	    }
		}
	    if(lBulkUpdates[IDX_BULK_READY_LEG2_DR]){
	    	markLeg2DebitReady();
	    }
        if (!AppConstants.FACILITATOR_NPCI.equals(facilitator)) {
    	    if(lBulkUpdates[IDX_BULK_READY_LEG2_FIN_CR]){
    	    	markLeg2CreditReady();
            }
	    }
	    //release limits 
        FactoringUnitBean lFactoringUnitBean=null;
        Map<Long,FactoringUnitBean> lFUBeanHash=new HashMap<Long,FactoringUnitBean>();
        BidBean lBidBean = new BidBean();
        AppUserBean lAppUserBean = new AppUserBean();
        lAppUserBean.setId(paymentFileBean.getGeneratedByAuId());
        //
        logger.info("********* Releasing Limits for FUId Count : "+ lFUReleaseLimit.size() );
	    for(Long lFUId : lFUReleaseLimit){
            //
	        logger.info("********* Releasing Limits for FUId : "+ lFUId.toString());
	    	lFactoringUnitBean = new FactoringUnitBean();
            lFactoringUnitBean.setId(lFUId);
            lFactoringUnitBean = factoringUnitDAO.findBean(pConnection, lFactoringUnitBean);
            logger.info(("********* FU LimitIds " + (lFactoringUnitBean.getLimitIds()!=null?lFactoringUnitBean.getLimitIds().toString():"")));
            logger.info(("********* FU LimitUtilised " + (lFactoringUnitBean.getLimitUtilized()!=null?lFactoringUnitBean.getLimitUtilized().toString():"")));
            //
            lBidBean = new BidBean();
            lBidBean.setId(lFactoringUnitBean.getBdId());
            lBidBean = bidDAO.findBean(pConnection, lBidBean);
            logger.info(("********* Bid LimitIds " + (lBidBean.getLimitIds()!=null?lBidBean.getLimitIds().toString():"")));
            logger.info(("********* Bid LimitUtilised " + (lBidBean.getLimitUtilised()!=null?lBidBean.getLimitUtilised().toString():"")));
            logger.info(("********* Bid BidLimitUtilised " + (lBidBean.getBidLimitUtilised()!=null?lBidBean.getBidLimitUtilised().toString():"")));
            //
	        logger.info("********* Releasing Bid Limits.");
            factoringUnitBO.releaseLimit(pConnection, lFactoringUnitBean, lBidBean, lAppUserBean);
            //this has to be done so that it won't get reduced twice.
			lBidBean.setLimitUtilised(BigDecimal.ZERO);
            lBidBean.setBidLimitUtilised(BigDecimal.ZERO);
            lBidBean.setLimitIds(null);
            bidDAO.update(pConnection, lBidBean);
            //
			if (lFactoringUnitBean.getPurSupLimitUtilized() != null) {
		        logger.info("********* Releasing Purchaser Supplier Limits.");
	            logger.info(("********* PurchSupp LimitUtilised " + (lBidBean.getBidLimitUtilised()!=null?lBidBean.getBidLimitUtilised().toString():"")));
                purchaserSupplierLimitUtilizationBO.updatePSLimitUtilization(pConnection, lFactoringUnitBean.getPurchaser(), 
                        lFactoringUnitBean.getSupplier(), lAppUserBean, lFactoringUnitBean.getPurSupLimitUtilized(), true);
                lFactoringUnitBean.setPurSupLimitUtilized(null);
                factoringUnitDAO.update(pConnection, lFactoringUnitBean,FactoringUnitBean.FIELDGROUP_UPDATESTATUS);
            }
			lFUBeanHash.put(lFactoringUnitBean.getId(), lFactoringUnitBean);
	    }
		//check fu status and then proceed to calculate tunover
	    updateTurnOver(pConnection, lFUBeanHash, lFULeg1Failed);
	    //
	    updateFactoringUnitStatus(pConnection);
	    updateInstrumentStatus(pConnection);
	    updateMonetagoTransStatus(pConnection);
	    generateNoticeOfAssignment(pConnection, paymentFileBean.getId(), TredsHelper.getInstance().getBusinessDate());
	    //
	}
	
	private void updateTurnOver(Connection pConnection, Map<Long,FactoringUnitBean> pFUBeanHash, List<Long> pFULeg1Failed ){
	    BigDecimal lTurnover = null;
	    Map<String, BigDecimal> lEntitywiseTotalFUAmt = new HashMap<String, BigDecimal>();
	    BigDecimal lTotalFactAmt = BigDecimal.ZERO;
        FactoringUnitBean lFactoringUnitBean=null;
        String[] lPurFinEntity = null;
	    for(Long lFUId : pFULeg1Failed){
	    	lFactoringUnitBean = pFUBeanHash.get(lFUId);
	    	lPurFinEntity = new String[] { lFactoringUnitBean.getPurchaser(), lFactoringUnitBean.getFinancier() };
	    	for(int lPtr=0; lPtr < lPurFinEntity.length; lPtr++){
		    	lTotalFactAmt = BigDecimal.ZERO;
		    	if(lEntitywiseTotalFUAmt.containsKey(lPurFinEntity[lPtr])){
		    		lTotalFactAmt = lEntitywiseTotalFUAmt.get(lPurFinEntity[lPtr]);
		    	}
		    	lTotalFactAmt = lTotalFactAmt.add(lFactoringUnitBean.getFactoredAmount());
		    	lEntitywiseTotalFUAmt.put(lPurFinEntity[lPtr], lTotalFactAmt);
	    	}
	    }
	    for(String lEntityCode : lEntitywiseTotalFUAmt.keySet()){
			lTurnover = TredsHelper.getInstance().getTurnoverAmount(pConnection, lEntityCode);
	    	lTotalFactAmt = lEntitywiseTotalFUAmt.get(lEntityCode);
			TredsHelper.getInstance().updateTurnover(pConnection, lEntityCode,  lTurnover ,(lTotalFactAmt.multiply(BigDecimal.ONE.negate())),new Long(1));
	    }
	}
	
	private void updateFactoringUnitStatus(Connection pConnection) throws Exception{
		//Leg 1 Success	Seller Credit Leg1 is success and Old Status is Factored
		//Leg 1 Fail	Seller Credit Leg1 is Failed or Cancelled and Old Status is Factored
		//Leg 2 Success	Buyer Debit Leg2 is success and Old Status is Leg1 Success
		//Leg 2 Fail	Buyer Debit Leg2 is Failed and Old Status is Leg1 Success
		// update fu set fustatu=newstatus where furecorv>0 and fuoldstatus=<oldstatus> and fuid in (select obfuid from obligations where obpfid=<pfid> and <abovecondition>
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		int lUpdateCount = 0;
		logger.info("********** Update Factoring Unit Status.");
		for(int lPtr=0; lPtr < IDX_RESULT_SIZE; lPtr++){
			lSql.delete(0, lSql.length());
			lSql.append("UPDATE FACTORINGUNITS ");
			lSql.append(" SET FUSTATUS = ").append(lDBHelper.formatString(resultNewFUStatus[lPtr])); // New Status
			lSql.append(" , FUSTATUSUPDATETIME = sysdate ");
			lSql.append(" WHERE FURECORDVERSION > 0 ");
			lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(resultOldFUStatus[lPtr])); // Old Status
			lSql.append(" AND FUID IN ( ");
			lSql.append(" SELECT OBFUID FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
			lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
			lSql.append(" AND OBTYPE = ").append(lDBHelper.formatString(resultObliType[lPtr]));
			lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(resultObliTxnType[lPtr]));
			lSql.append(" AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
			lSql.append(" AND OBSTATUS = ").append(lDBHelper.formatString(resultObliStatus[lPtr]));
			lSql.append(" ) ");
			
			logger.info("********** " + resultText[lPtr] + " : " + lSql.toString());
	        Statement lStatement = null;
	        try {
	            lStatement = pConnection.createStatement();
	            lUpdateCount = lStatement.executeUpdate(lSql.toString());
				logger.info("********** " + resultText[lPtr] + " : " + lUpdateCount);
	        } finally {
	            if (lStatement != null)
	            	lStatement.close();
	        }
		}
	}

	private void updateInstrumentStatus(Connection pConnection) throws Exception{
		// update instruments set idstatus=<thestatus> where idstatus!=<thestatus> and infuid in (select fuid from factoringunits where furecordvderion>0 and fustatus=<thestatus> and fuid in (select obfuid from obligations where obpfid = <this>))
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		int lUpdateCount = 0;
		logger.info("********** Update Instrument Status.");
		for(int lPtr=0; lPtr < IDX_RESULT_SIZE; lPtr++){
			lSql.delete(0, lSql.length());
			lSql.append("UPDATE INSTRUMENTS ");
			lSql.append(" SET INSTATUS = ").append(lDBHelper.formatString(resultNewInstStatus[lPtr])); // New Status
			lSql.append(" , INSTATUSUPDATETIME = sysdate ");
			lSql.append(" WHERE INRECORDVERSION > 0 ");
			lSql.append(" AND INSTATUS = ").append(lDBHelper.formatString(resultOldInstStatus[lPtr])); // Old Status
			lSql.append(" AND INFUID IN ( ");
			lSql.append(" SELECT FUID FROM FACTORINGUNITS WHERE FURECORDVERSION > 0 ");
			lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(resultNewFUStatus[lPtr])); // New Status
			lSql.append(" AND FUID IN ( ");
			lSql.append("  SELECT OBFUID FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
			lSql.append("  AND OBPFID = ").append(paymentFileBean.getId());
			lSql.append("  AND OBTYPE = ").append(lDBHelper.formatString(resultObliType[lPtr]));
			lSql.append("  AND OBTXNTYPE = ").append(lDBHelper.formatString(resultObliTxnType[lPtr]));
			lSql.append("  AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
			lSql.append("  AND OBSTATUS = ").append(lDBHelper.formatString(resultObliStatus[lPtr]));
			lSql.append(" ) ");
			lSql.append(" ) ");
			
			logger.info("********** " + resultText[lPtr] + " : " + lSql.toString());
	        Statement lStatement = null;
	        try {
	            lStatement = pConnection.createStatement();
	            lUpdateCount = lStatement.executeUpdate(lSql.toString());
				logger.info("********** " + resultText[lPtr] + " : " + lUpdateCount);
	        } finally {
	            if (lStatement != null)
	            	lStatement.close();
	        }
		}
	}
	
	private void updateMonetagoTransStatus(Connection pConnection) throws Exception{
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		int lUpdateCount = 0;
		logger.info("********** Update Monetago Status.");
		if(MonetagoTredsHelper.getInstance().performMonetagoCheck()){
			for(int lPtr=0; lPtr < IDX_RESULT_SIZE; lPtr++){
				lSql.delete(0, lSql.length());
				lSql.append("Select * FROM INSTRUMENTS ");
				lSql.append(" WHERE INRECORDVERSION > 0 ");
				lSql.append(" AND INSTATUS = ").append(lDBHelper.formatString(InstrumentBean.Status.Leg_1_Failed.getCode())); // Old Status
				lSql.append(" AND INFUID IN ( ");
				lSql.append(" SELECT FUID FROM FACTORINGUNITS WHERE FURECORDVERSION > 0 ");
				lSql.append(" AND FUSTATUS = ").append(lDBHelper.formatString(FactoringUnitBean.Status.Leg_1_Failed.getCode())); // New Status
				lSql.append(" AND FUID IN ( ");
				lSql.append("  SELECT DISTINCT OBFUID FROM OBLIGATIONS WHERE OBRECORDVERSION > 0 ");
				lSql.append("  AND OBPFID = ").append(paymentFileBean.getId());
				lSql.append("  AND OBTYPE = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
				lSql.append("  AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
				lSql.append("  AND OBSTATUS = ").append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode()));
				lSql.append(" ) ");
				lSql.append(" ) ");
				List<InstrumentBean> lInstrumentList = instrumentDAO.findListFromSql(pConnection, lSql.toString(), 0);
				String lInfoMessage = null;
				boolean lCancellationFailed = false;
		        for(InstrumentBean lInstrumentBean:lInstrumentList){
        			if(StringUtils.isNotBlank(lInstrumentBean.getMonetagoLedgerId())){
        				try
        				{
            				Map<String,String> lResult= new HashMap<String, String>();
            	     		lInfoMessage = "Instrument No :"+lInstrumentBean.getId() +" " + "FactoringUnit No :"+lInstrumentBean.getFuId();
            	    		lResult=MonetagoTredsHelper.getInstance().cancel(lInstrumentBean.getMonetagoLedgerId(), MonetagoTredsHelper.CancelResonCode.NotFinanced,lInfoMessage,lInstrumentBean.getId());
            	    		if(StringUtils.isNotBlank(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID))){
            	   				lInstrumentBean.setMonetagoCancelTxnId(lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_TRANSID));
            	   				lInstrumentBean.setMonetagoLedgerId("");
            	   				logger.info("Message Success :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
            	    		}else{
            	    			logger.info("Message Error :  " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
            	    			lCancellationFailed = true;
            	    			throw new CommonBusinessException("Error while Settlement : " +lResult.get(MonetagoTredsHelper.TREDS_RESPONSE_MESSAGE));
            	    		}
            	    		instrumentDAO.update(pConnection, lInstrumentBean,InstrumentBean.FIELDGROUP_UPDATEMONETAGOCANCEL);
        				}catch(Exception lException){
        					logger.error(lException.toString());
        				}
        	        }
		        }
		        if(lCancellationFailed){
		        	remarks = "Failure in cancellation of some instruments at monetago.";
		        }
			}
		}
	}
	
	//The paymentFileId or the Leg1SettlmentDate
	//If the paymentFileId is received - find the Leg1 Success obligation from the same
	//If the paymentFileId is NOT received - the Leg1SettlementDate is used to find the paymentFileId for that day and 
	//the obligations of all the paymentFileIds for that day are used	
	/* (non-Javadoc)
	 * @see com.xlx.treds.auction.bean.IPaymentSettlor#generateNoticeOfAssignment(java.sql.Connection, java.lang.Long, java.sql.Date)
	 */
	@Override
	public void generateNoticeOfAssignment(Connection pConnection, Long pPaymentFileId, Date pLeg1SettledDate) {
		StringBuilder lSql = null;
		DBHelper lDBHelper = DBHelper.getInstance();
		//if error then do not throw the error - just log.
		try{
			//with the paymentfileId find the FUs from obligations with leg1 success 
			//loop throught the FUs
			//Maintain Hash key=BusDate^Pur^Sup^Fin  value=AssignmentNotices
			//if not in Hash - findAssNotice() AssignmentNotices
			//
			if(pPaymentFileId == null || pPaymentFileId.longValue() <= 0){
				logger.info("Payment file Id required.");
				return;
			}
			logger.info("Payment file Id : " + pPaymentFileId);
			if(pPaymentFileId != null ){
				lSql = new StringBuilder();
	            // AssignmentNoticeDetails
				lSql.append(" SELECT ");
	        	lSql.append(" ASSND.ANDANID ANDANID, ");
	        	lSql.append(" OBFUID ANDFUID, ");
	        	lSql.append(" FUFACTORSTARTDATETIME	ANDFUDATE, ");
	        	lSql.append(" CASE FUENABLEEXTENSION WHEN 'Y' THEN FUExtendedDueDate ELSE FUMaturityDate END  ANDFUDUEDATE, ");
	        	lSql.append(" FUFACTOREDAMOUNT	ANDFUFACTOREDAMOUNT, ");
	        	lSql.append(" ININSTNUMBER	ANDINSTNUMBER, ");
	        	lSql.append(" ININSTDATE	ANDINSTDATE, ");
	        	lSql.append(" INAMOUNT	ANDINSTAMOUNT, ");
	        	lSql.append(" INCURRENCY	ANDCURRENCY, ");
	        	lSql.append(" ASSND.ANDRecordCreateTime ANDRecordCreateTime, ");
	        	// AssignmentNotices
	        	lSql.append(" ASSN.ANID ANID, ");
	        	lSql.append(" FUPURCHASER  ANPURCHASER," );
	        	lSql.append(" FUSUPPLIER  ANSUPPLIER," );
	        	lSql.append(" FUFINANCIER  ANFINANCIER," );
	        	lSql.append(" ASSN.ANBUSINESSDATE ANBUSINESSDATE," ); //DATE
	        	lSql.append(" OBPAYDETAIL1  ANFINACCNO," ); //1=account no
	        	lSql.append(" OBPAYDETAIL2  ANFINIFSC," ); //2=ifsc
	        	lSql.append(" ASSN.ANFINBRANCHNAME	ANFINBANKNAME," ); //3=financier name XXXXXX 
	        	lSql.append(" BBDBRANCHNAME	ANFINBRANCHNAME," ); //BRANCH NAME
	        	lSql.append(" SUP.CDCOMPANYNAME  ANSUPNAME," );
	        	lSql.append(" PUR.CDCOMPANYNAME  ANPURNAME," );
	        	lSql.append(" FIN.CDCOMPANYNAME  ANFINNAME," );
	        	lSql.append(" SUPL.CLLINE1  ANSUPLINE1," );
	        	lSql.append(" SUPL.CLLINE2  ANSUPLINE2," );
	        	lSql.append(" SUPL.CLLINE3  ANSUPLINE3," );
	        	lSql.append(" SUPL.CLCOUNTRY  ANSUPCOUNTRY," );
	        	lSql.append(" SUPL.CLSTATE  ANSUPSTATE," );
	        	lSql.append(" SUPL.CLDISTRICT  ANSUPDISTRICT," );
	        	lSql.append(" SUPL.CLCITY  ANSUPCITY," );
	        	lSql.append(" SUPL.CLZIPCODE  ANSUPZIPCODE," );
	        	lSql.append(" PURL.CLLINE1  ANPURLINE1," );
	        	lSql.append(" PURL.CLLINE2  ANPURLINE2," );
	        	lSql.append(" PURL.CLLINE3  ANPURLINE3," );
	        	lSql.append(" PURL.CLCOUNTRY  ANPURCOUNTRY," );
	        	lSql.append(" PURL.CLSTATE  ANPURSTATE," );
	        	lSql.append(" PURL.CLDISTRICT  ANPURDISTRICT," );
	        	lSql.append(" PURL.CLCITY  ANPURCITY," );
	        	lSql.append(" PURL.CLZIPCODE  ANPURZIPCODE," );
	        	lSql.append(" FINL.CLLINE1  ANFINLINE1," );
	        	lSql.append(" FINL.CLLINE2  ANFINLINE2," );
	        	lSql.append(" FINL.CLLINE3  ANFINLINE3," );
	        	lSql.append(" FINL.CLCOUNTRY  ANFINCOUNTRY," );
	        	lSql.append(" FINL.CLSTATE  ANFINSTATE," );
	        	lSql.append(" FINL.CLDISTRICT  ANFINDISTRICT," );
	        	lSql.append(" FINL.CLCITY  ANFINCITY," );
	        	lSql.append(" FINL.CLZIPCODE  ANFINZIPCODE" );
	        	//
	        	lSql.append(" FROM OBLIGATIONS ");
	        	lSql.append(" JOIN APPENTITIES ON OBTXNENTITY = AECODE ");
	        	lSql.append(" LEFT OUTER JOIN COMPANYDETAILS FIN ON ( AECDID = FIN.CDID AND FIN.CDRECORDVERSION > 0 ) ");
	        	lSql.append(" LEFT OUTER JOIN PAYMENTFILES  ON ( OBPFID = PFID ) ");
	        	lSql.append(" LEFT OUTER JOIN FACTORINGUNITS ON ( OBFUID = FUID ) ");
	        	lSql.append(" LEFT OUTER JOIN INSTRUMENTS ON ( INFUID = FUID ) ");
	        	lSql.append(" LEFT OUTER JOIN ASSIGNMENTNOTICEDETAILS ASSND ON ( ASSND.ANDFUID = OBFUID ) ");
	        	lSql.append(" LEFT OUTER JOIN ASSIGNMENTNOTICES ASSN ON ( ASSN.ANID = ASSND.ANDANID ) ");
	        	lSql.append(" LEFT OUTER JOIN COMPANYDETAILS SUP ON ( SUP.CDCODE = FUSUPPLIER AND SUP.CDRECORDVERSION > 0 ) ");
	    	    lSql.append(" LEFT OUTER JOIN COMPANYDETAILS PUR ON ( PUR.CDCODE = FUPURCHASER AND PUR.CDRECORDVERSION > 0 ) ");
	    	    lSql.append(" LEFT OUTER JOIN BANKBRANCHDETAIL ON (BBDIFSC = OBPAYDETAIL2) ");

	    	    lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS SUPL ON ( SUPL.CLCDID = SUP.CDID AND SUPL.CLRECORDVERSION > 0 AND SUPL.CLLOCATIONTYPE = ").append(lDBHelper.formatString(CompanyLocationBean.LocationType.RegOffice.getCode())).append(" ) ");  
	    	    lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS PURL ON ( PURL.CLCDID = PUR.CDID AND PURL.CLRECORDVERSION > 0 AND PURL.CLLOCATIONTYPE = ").append(lDBHelper.formatString(CompanyLocationBean.LocationType.RegOffice.getCode())).append(" ) ");  
	    	    lSql.append(" LEFT OUTER JOIN COMPANYLOCATIONS FINL ON ( FINL.CLCDID = FIN.CDID AND FINL.CLRECORDVERSION > 0 AND FINL.CLLOCATIONTYPE = ").append(lDBHelper.formatString(CompanyLocationBean.LocationType.RegOffice.getCode())).append(" ) ");  
	    	    
	    	    lSql.append(" WHERE OBRECORDVERSION > 0  ");
	        	lSql.append(" AND AERECORDVERSION > 0 ");
	        	lSql.append(" AND FIN.CDRECORDVERSION > 0 ");
	        	lSql.append(" AND FURECORDVERSION > 0 ");
	        	lSql.append(" AND INRECORDVERSION > 0 ");
	        	lSql.append(" AND PFRECORDVERSION > 0 ");
	    		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
	    		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
	    		lSql.append(" AND OBTxnType = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
	    		//clause
	        	lSql.append(" AND OBPFID = ").append(pPaymentFileId);
	        	lSql.append(" AND FIN.CDFINANCIERFLAG = 'Y' "); //ONLY FINANCIERS 
	        	lSql.append(" AND ANDANID IS NULL "); 
			}
			List<String> lColumns = null;
			AssignmentNoticesBean lAssignmentNoticesFilterBean = new AssignmentNoticesBean();
			lAssignmentNoticesFilterBean.setBusinessDate(pLeg1SettledDate);
			List<AssignmentNoticesBean> lNOABeans = assignmentNoticesDAO.findList(pConnection, lAssignmentNoticesFilterBean, lColumns);
			List<AssignmentNoticeInfo> lNOADetails = (List<AssignmentNoticeInfo>) assignmentNoticeInfoDAO.findListFromSql(pConnection, lSql.toString(), 0);
			HashMap<String, AssignmentNoticesBean> lNOAHash = new HashMap<String, AssignmentNoticesBean>();
			//Hash of NOAs for that Businessdate 
			if(lNOABeans != null && lNOABeans.size() > 0){
				logger.info("List of Existing NOA for Bussiness date : " + pLeg1SettledDate.toString());
				for(AssignmentNoticesBean lDbANBean : lNOABeans){
					lNOAHash.put(lDbANBean.getKey(), lDbANBean);
					logger.info("NOA ID : " + lDbANBean.getId() + " :: Key : " + lDbANBean.getKey());
				}
			}
			if(lNOADetails==null||lNOADetails.size() == 0){
				return;
			}
			//
			HashMap<String, AssignmentNoticeWrapperBean> lANWrapperHash = new HashMap<String, AssignmentNoticeWrapperBean>();
			AssignmentNoticeWrapperBean lANWrapperBean = null;
			//
			AssignmentNoticesBean lANBean = null;
			for(AssignmentNoticeInfo lANInfoBean : lNOADetails){
				lANBean = lANInfoBean.getAssignmentNoticesBean();
				AssignmentNoticeDetailsBean lANDBean = lANInfoBean.getAssignmentNoticeDetailsBean();
	    		AssignmentNoticeGroupDetailsBean lANGBean = lANInfoBean.getAssignmentNoticeGroupDetailsBean();
	    		//first setting the business date for the new record since the key depends on the same
				if (lANBean.getBusinessDate() == null){
					lANBean.setBusinessDate(pLeg1SettledDate);
				}
				//fetching the already added record and using the same so as to update the details and child records with the key anid
    			if(lNOAHash.containsKey(lANBean.getKey())){
    				lANBean = lNOAHash.get(lANBean.getKey());
    			}
	    		//
	    		if(!lANWrapperHash.containsKey(lANBean.getKey())){
	    			lANWrapperBean = new AssignmentNoticeWrapperBean(lANBean);
	    			lANWrapperHash.put(lANBean.getKey(), lANWrapperBean);
	    		}else{
		    		lANWrapperBean = lANWrapperHash.get(lANBean.getKey());
	    		}
	    		lANWrapperBean.addDetails(lANDBean, lANBean.getKey());
	    		lANWrapperBean.addChildDetails(lANGBean, lANBean.getKey());
			}
			//
			HashMap<String,String> lBankHash = TredsHelper.getInstance().getBankName();
			for(AssignmentNoticeWrapperBean lWrapperBean : lANWrapperHash.values()){
				lANBean = lWrapperBean.getAssignmentNoticesBean();
				if(lANBean.getId()==null){
					if(StringUtils.isBlank(lANBean.getFinBankName())){
						lANBean.setFinBankName(lBankHash.get(lANBean.getFinIfsc().substring(0, 4)));
					}
					//if the Notice Bean is NOT present then insert new one in the Db and add to the Hash
					lANBean.setId(lDBHelper.getUniqueNumber(pConnection, "AssignmentNoticesBean.Id") );
					lANBean.setBusinessDate(pLeg1SettledDate);
					assignmentNoticesDAO.insert(pConnection, lANBean);
					logger.info("NEW NOA ID : " + lANBean.getId() + " :: Key : " + lANBean.getKey());
				}
	    		for(AssignmentNoticeDetailsBean lANDBean : lWrapperBean.getDetails()){
					if(lANDBean.getAnId() == null){
						lANDBean.setAnId(lANBean.getId());
						lANDBean.setRecordCreateTime((new Timestamp(System.currentTimeMillis())));
						assignmentNoticeDetailsDAO.insert(pConnection, lANDBean);
						logger.info("NEW NOA Detail FUId : " + lANDBean.getFuId() + " AnId : " + lANBean.getId() + " :: Key : " + lANBean.getKey());
					}
    				List<AssignmentNoticeGroupDetailsBean> lChildBeans = lWrapperBean.getChildDetails(lANDBean.getInId());
        			if(lChildBeans!=null && lChildBeans.size() > 0){
        				for(AssignmentNoticeGroupDetailsBean lANGBean : lChildBeans){
        					if(lANGBean.getAnId() == null){
        						lANGBean.setAnId(lANBean.getId());
        						lANGBean.setRecordCreateTime((new Timestamp(System.currentTimeMillis())));
        						assignmentNoticeGroupDetailsDAO.insert(pConnection, lANGBean);
        						logger.info("NEW NOA Detail ChildInId : " + lANGBean.getChildInId() + " AnId : " + lANBean.getId() + " :: Key : " + lANBean.getKey());
        					}
        				}
        			}
	    		}
			}			
		}catch(Exception lEx){
			logger.info("Error in generateNoticeOfAssignment : "+lEx.getMessage());
		}
	}

	private ObligationBean getSiblingObligation(IObligation pObligationBean){
		ObligationBean lObligationBean = null;
		StringBuilder lSql = new StringBuilder();
		if(pObligationBean!=null){
			List<ObligationBean> lTemp = null;
			lSql.append("SELECT * FROM Obligations WHERE OBRecordVersion > 0 ");
			lSql.append(" AND OBFUId = ").append(pObligationBean.getFuId()); //for that FU all Leg obligations
			lSql.append(" AND OBPFId = ").append(pObligationBean.getPfId()); //only those obligation which were in the sent file
			lSql.append(" AND OBId != ").append(pObligationBean.getId()); // skip the requesting obligation 
			try {
				 lTemp = obligationDAO.findListFromSql(connection, lSql.toString(), 0);
			} catch (Exception e) {
				 e.printStackTrace();
			}
			if(lTemp!=null && lTemp.size() > 0){
				lObligationBean = lTemp.get(0);
				if(lTemp.size() > 1){
					//log that multiple 
				}
			}
		}
		return lObligationBean;
	}

	private ObligationBean getObligation(Long pFactoringUnitId, ObligationBean.TxnType pTxnType, EntityType pEntityType, ObligationBean.Type pLeg){
		ObligationBean lObligationBean = null;
		StringBuilder lSql = new StringBuilder();
		if(pFactoringUnitId!=null){
			List<ObligationBean> lTemp = null;
			lSql.append("SELECT * FROM Obligations WHERE OBRecordVersion > 0 ");
			lSql.append(" AND OBFUId = ").append(pFactoringUnitId); //for that FU all Leg obligations
			lSql.append(" AND OBTxnType = ").append(DBHelper.getInstance().formatString(pTxnType.getCode())); //only those obligation which were in the sent file
			lSql.append(" AND OBType = ").append(DBHelper.getInstance().formatString(pLeg.getCode())); //only those obligation which were in the this leg			
			try {
				 lTemp = obligationDAO.findListFromSql(connection, lSql.toString(), 0);
			} catch (Exception e) {
				 e.printStackTrace();
			}
			if(lTemp!=null && lTemp.size() > 0){
				EntityType lEntityType = null;
				for(ObligationBean lBean : lTemp){
					lEntityType = getEntityType(lBean.getTxnEntity());
					if(lEntityType.equals(pEntityType)){
						lObligationBean = lBean;
						break;
					}
				}
			}
		}
		return lObligationBean;
	}

	private void reverseObligation(IObligation pObligation){
		if(pObligation!=null){
		    if (pObligation.getStatus() != ObligationBean.Status.Success)
		        return;
			ObligationBean lNewObligation = null;
			Date lNextClearingDate = null;
			ObligationBean.TxnType lNewTxnType = null;
			//
    		try {
    	        logger.info("********** PaymentSettlor :: reverse obligation : "+ pObligation.toString());
    			lNewObligation = new ObligationBean();
	    		obligationDAO.getBeanMeta().copyBean(pObligation, lNewObligation); //new obligation
	    		//setting the paymentFile date so that we can use the same function for the reversal of any previous obligations (eg. Reversing in NACH Credit file)
	        	lNextClearingDate = TredsHelper.getInstance().getNEFTReversalDate(); 
	        	if(ObligationBean.TxnType.Debit.equals(pObligation.getTxnType())) {
	        		lNewTxnType = ObligationBean.TxnType.Credit;
	        	}else if(ObligationBean.TxnType.Credit.equals(pObligation.getTxnType())){
	        		lNewTxnType = ObligationBean.TxnType.Debit;
	        	}
	    		//new obligation changes
	        	lNewObligation.setId(TredsHelper.getInstance().getObligationId(connection));
	        	lNewObligation.setTxnType(lNewTxnType);
	        	lNewObligation.setStatus(ObligationBean.Status.Ready);
	    		lNewObligation.setDate(lNextClearingDate);
	    		lNewObligation.setRecordCreateTime((new Timestamp(System.currentTimeMillis())));
	    		lNewObligation.setRecordCreator(paymentFileBean.getGeneratedByAuId());
	    		lNewObligation.setRecordUpdateTime(null);
	    		lNewObligation.setRecordUpdator(null);
	    		lNewObligation.setRecordVersion(new Long(1));
	    		//
	        	lNewObligation.setNarration(null);
	        	lNewObligation.setPfId(null);
	        	lNewObligation.setFileSeqNo(null);
	        	lNewObligation.setPayDetail1(null);
	        	lNewObligation.setPayDetail2(null);
	        	lNewObligation.setPayDetail3(null);
	        	lNewObligation.setSettledAmount(null);
	        	lNewObligation.setSettledDate(null);
	        	lNewObligation.setPaymentRefNo(null);
	        	lNewObligation.setRespErrorCode(null);
	        	lNewObligation.setRespRemarks(null);
	        	lNewObligation.setOldObligationId(pObligation.getId());
	    		//
	    		obligationDAO.insert(connection, lNewObligation);
	    		newObligationList.add(lNewObligation.getId());
	    		pObligation.setStatus(Status.Shifted); //THIS IS DONE SO THAT THE SAME WONT BE SHIFTED AGAIN
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
		
	private void recreateObligation(IObligation pObligation){
		if(pObligation!=null){
			ObligationBean lNewObligation = null;
			Date lNextClearingDate = null;
			//
    		try {
    	        logger.info("********** PaymentSettlor :: recreate obligation : "+ pObligation.toString());
    	        lNewObligation = new ObligationBean();
	    		obligationDAO.getBeanMeta().copyBean(pObligation, lNewObligation); //new obligation
	    		//setting the paymentFile date so that we can use the same function for the reversal of any previous obligations (eg. Reversing in NACH Credit file)
	        	lNextClearingDate = OtherResourceCache.getInstance().getNextClearingDate(paymentFileBean.getDate(), 1);
	    		//new obligation changes
	        	lNewObligation.setId(TredsHelper.getInstance().getObligationId(connection));
	        	lNewObligation.setStatus(ObligationBean.Status.Ready);
	        	//
	    		lNewObligation.setDate(lNextClearingDate);
	    		lNewObligation.setRecordCreateTime((new Timestamp(System.currentTimeMillis())));
	    		lNewObligation.setRecordCreator(paymentFileBean.getGeneratedByAuId());
	    		lNewObligation.setRecordUpdateTime(null);
	    		lNewObligation.setRecordUpdator(null);
	    		lNewObligation.setRecordVersion(new Long(1));
	    		//
	        	lNewObligation.setNarration(null);
	        	lNewObligation.setPfId(null);
	        	lNewObligation.setFileSeqNo(null);
	        	lNewObligation.setPayDetail1(null);
	        	lNewObligation.setPayDetail2(null);
	        	lNewObligation.setPayDetail3(null);
	        	lNewObligation.setPaymentRefNo(null);
	        	lNewObligation.setSettledAmount(null);
	        	lNewObligation.setSettledDate(null);
	        	lNewObligation.setRespErrorCode(null);
	        	lNewObligation.setRespRemarks(null);
	        	lNewObligation.setOldObligationId(pObligation.getId());
	    		//
	    		obligationDAO.insert(connection, lNewObligation);
	    		newObligationList.add(lNewObligation.getId());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void cancelSibling() throws Exception{
        logger.info("********** PaymentSettlor :: Cancelling obligations");
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("UPDATE Obligations SET OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Cancelled.getCode()));
        //the below two lines existed in the previous query.
        //lSql.append(", OBSettledAmount = 0, OBRespErrorCode = ").append(lDBHelper.formatString(TREDSCODE_LEG1FAILED));
        //lSql.append(", OBRespRemarks = ").append(lDBHelper.formatString(TREDSCODE_LEG1FAILED_DESC));
        
        lSql.append(" WHERE OBRecordVersion > 0 ");

        lSql.append(" AND OBFuId IN (SELECT OBFuId FROM Obligations WHERE OBRecordVersion > 0");
        lSql.append(" AND OBPfId = ").append(paymentFileBean.getId());
        lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Failed.getCode())).append(")");

        lSql.append(" AND OBStatus IN (").append(lDBHelper.formatString(ObligationBean.Status.Created.getCode()));
        lSql.append(",").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode())).append(")");

        if(newObligationList!=null && newObligationList.size() > 0){
    		lSql.append(" AND OBId NOT IN ( " );
        	for(int lPtr=0; lPtr < newObligationList.size(); lPtr++){
        		if(lPtr > 0) lSql.append(",");
        		lSql.append(newObligationList.get(lPtr));
        	}
    		lSql.append(" ) " );
        }
        
        if (logger.isDebugEnabled())
            logger.debug(lSql.toString());
        Statement lStatement = null;
        try {
            lStatement = connection.createStatement();
            int lCount = lStatement.executeUpdate(lSql.toString());
            logger.info("Cancelled obligation count " + lCount);
        } finally {
            if (lStatement != null)
            	lStatement.close();
        }
	}
	
	private void markSellerCreditReady() throws Exception{
        logger.info("********** PaymentSettlor :: Marking obligations ready - Seller Credit Ready");
        StringBuilder lSql = new StringBuilder();
        DBHelper lDBHelper = DBHelper.getInstance();
        lSql.append("UPDATE Obligations A SET OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode()));
        lSql.append(" WHERE OBRecordVersion > 0 ");
        lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
        lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Created.getCode()));
        lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Credit.getCode()));
        lSql.append(" AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
        lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));

        lSql.append(" AND NOT EXISTS (SELECT OBId FROM Obligations B WHERE OBRecordVersion > 0");
        lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
        lSql.append(" AND OBStatus != ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
        //lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));
        lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
        lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
        lSql.append(" AND B.OBFuId = A.OBFuId AND B.OBType = A.OBType)");
        if (logger.isDebugEnabled())
            logger.debug(lSql.toString());
        Statement lStatement = null;
        try {
			lStatement = connection.createStatement();
            int lCount = lStatement.executeUpdate(lSql.toString());
            logger.info("********** PaymentSettlor :: Seller Credit Ready count " + lCount);
        } finally {
        	if (lStatement != null)
        		lStatement.close();
        }
	}
	private void markTredsCreditReady() throws Exception{
		logger.info("********** PaymentSettlor :: Marking obligations ready - TREDs Credit Ready");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("UPDATE Obligations A SET OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" WHERE OBRecordVersion > 0 ");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Created.getCode()));
		lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Credit.getCode()));
		lSql.append(" AND OBTXNENTITY = ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
		lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));
		
		lSql.append(" AND EXISTS (SELECT OBId FROM Obligations B WHERE OBRecordVersion > 0");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
		//lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));
		lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Credit.getCode()));
		lSql.append(" AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
		lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
		lSql.append(" AND B.OBFuId = A.OBFuId AND B.OBType = A.OBType)");
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		try {
			lStatement = connection.createStatement();
		    int lCount = lStatement.executeUpdate(lSql.toString());
		    logger.info("********** PaymentSettlor :: TREDs Credit Ready count " + lCount);
		} finally {
			if (lStatement != null)
				lStatement.close();
		}
	}
	private void markLeg2DebitReady() throws Exception{
		logger.info("********** PaymentSettlor :: Marking obligations ready - Leg2 Debit Ready");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("UPDATE Obligations A SET OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" WHERE OBRecordVersion > 0 ");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Created.getCode()));
		lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
		
		lSql.append(" AND EXISTS (SELECT OBId FROM Obligations B WHERE OBRecordVersion > 0");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_1.getCode()));
		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
		//lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));
		lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Credit.getCode()));
		lSql.append(" AND OBTXNENTITY != ").append(lDBHelper.formatString(AppConstants.DOMAIN_PLATFORM));
		lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
		lSql.append(" AND B.OBFuId = A.OBFuId)");
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		try {
			lStatement = connection.createStatement();
		    int lCount = lStatement.executeUpdate(lSql.toString());
		    logger.info("********** PaymentSettlor :: Leg2 Debit Ready count " + lCount);
		} finally {
			if (lStatement != null)
				lStatement.close();
		}
	}
	private void markLeg2CreditReady()throws Exception{
		logger.info("********** PaymentSettlor :: Marking obligations ready - Leg2 Credit Ready");
		StringBuilder lSql = new StringBuilder();
		DBHelper lDBHelper = DBHelper.getInstance();
		lSql.append("UPDATE Obligations A SET OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Ready.getCode()));
		lSql.append(" WHERE OBRecordVersion > 0 ");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Created.getCode()));
		lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Credit.getCode()));
		lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));
		
		lSql.append(" AND EXISTS (SELECT OBId FROM Obligations B WHERE OBRecordVersion > 0");
		lSql.append(" AND OBType = ").append(lDBHelper.formatString(ObligationBean.Type.Leg_2.getCode()));
		lSql.append(" AND OBStatus = ").append(lDBHelper.formatString(ObligationBean.Status.Success.getCode()));
		//lSql.append(" AND OBDate = ").append(lDBHelper.formatDate(paymentFileBean.getDate()));
		lSql.append(" AND OBTXNTYPE = ").append(lDBHelper.formatString(ObligationBean.TxnType.Debit.getCode()));
		lSql.append(" AND OBPFID = ").append(paymentFileBean.getId());
		lSql.append(" AND B.OBFuId = A.OBFuId AND B.OBType = A.OBType)");
		if (logger.isDebugEnabled())
		    logger.debug(lSql.toString());
		Statement lStatement = null;
		try {
			lStatement = connection.createStatement();
		    int lCount = lStatement.executeUpdate(lSql.toString());
		    logger.info("********** PaymentSettlor :: Leg2 Debit Ready count " + lCount);
		} finally {
			if (lStatement != null)
				lStatement.close();
		}
	}
	
	private PaymentSettlor.EntityType getEntityType(String pEntityCode){
		PaymentSettlor.EntityType lType = null;
		if (appEntitiesType.containsKey(pEntityCode)){
			lType = appEntitiesType.get(pEntityCode);
		}else{
			AppEntityBean lAppEntityBean;
			try {
				lAppEntityBean = tredsHelper.getAppEntityBean(pEntityCode);
				if(lAppEntityBean!=null){
					if(lAppEntityBean.isFinancier())
						lType = PaymentSettlor.EntityType.Financier;
					else if(lAppEntityBean.isPurchaser())
						lType = PaymentSettlor.EntityType.Buyer;
					else if(lAppEntityBean.isSupplier())
						lType = PaymentSettlor.EntityType.Seller;
					else if(lAppEntityBean.isPlatform())
						lType = PaymentSettlor.EntityType.Treds;
					appEntitiesType.put(pEntityCode, lType);
				}
			} catch (MemoryDBException e) {
				e.printStackTrace();
			}
		}
		return lType;
	}

	@Override
	public List<IObligation> getUnProcessedObligations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getObligationListReceivedForProcessing() {
		// TODO Auto-generated method stub
		return null;
	}

}
