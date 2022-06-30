package com.xlx.treds.auction.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.xlx.treds.AppConstants.EntityType;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class FactoredDetailBean {

	private FactoredBean factoredBean;
	private List<ObligationDetailInfoBean> obligationDetailsInfo;
    //
	// fields to display
/*	public static final List<String> fields_FactoringUnit = getColumns(0);
	public static final List<String> fields_Bid = getColumns(1);
	public static final List<String> fields_Instrument = getColumns(2);
	public static final List<String> fields_Obligation = getColumns(3);
	public static final List<String> fields_AppEntity = getColumns(4);
	public static final List<String> fields_CompanyDetail = getColumns(5);
	public static final List<String> fields_FEM = getColumns(6);
	public static final List<String> fields_CompanyBankDetail = getColumns(7);*/

	public FactoredDetailBean(FactoredBean pFactoredBean) {
		factoredBean = pFactoredBean;
		obligationDetailsInfo = new ArrayList<ObligationDetailInfoBean>();
		//
	}

/*	private static List<String> getColumns(int pIndex) {
		List<String> lList = new ArrayList<String>();
		if (pIndex == 0) { // FU
			lList.add("fuid");
			lList.add("amount");
			lList.add("costBearer");
			lList.add("chargeBearer");
			lList.add("acceptedRate");
			lList.add("acceptedHaircut");
			lList.add("status");
		}
		else if (pIndex == 1) { // BID
			lList.add("costLeg");
		}
		else if (pIndex == 2) { // INST
			lList.add("instNumber");
			lList.add("amount");
			lList.add("adjAmount");
			lList.add("tdsAmount");
		}
		else if (pIndex == 3) { // OBLI
			lList.add("id");
			lList.add("txnEntity");
			lList.add("date");
		}
		else if (pIndex == 4) { // AE
			lList.add("name");
		}
		else if (pIndex == 5) { // CD
			lList.add("companyname");
			lList.add("pan");
		}
		else if (pIndex == 5) { // FEM
			lList.add("facilitator");
		}
		else if (pIndex == 7) { // CBD
			lList.add("bank");
			lList.add("ifsc");
			lList.add("accNo");
		}
		return lList;
	}*/

	public FactoringUnitBean getFactoringUnitBean() {
		return factoredBean.getFactoringUnitBean();
	}

	public InstrumentBean getInstrumentBean() {
		return factoredBean.getInstrumentBean();
	}

	public BidBean getBidBean() {
		return factoredBean.getBidBean();
	}

	public void addObligationDetailInfo(ObligationDetailInfoBean pObligationDetailBean) {
		obligationDetailsInfo.add(pObligationDetailBean);
	}

	public ObligationDetailInfoBean getObligationDetailInfo(ObligationBean.Type pLeg, ObligationBean.TxnType pTransType, EntityType pEntityType) throws Exception {
		ObligationDetailInfoBean lRetBean = null;
		HashSet<Long> lObligationIds = new HashSet<Long>(); //obligation ids to skip - failed and cancelled transactions
		Long lObliId = null;
		for (ObligationDetailInfoBean lBean : obligationDetailsInfo) {
			lObliId =  lBean.getObligationBean().getOldObligationId();
			if(lObliId !=null && !lObligationIds.contains(lObliId) ){
				lObligationIds.add(lObliId);
			}
		}		
		for (ObligationDetailInfoBean lBean : obligationDetailsInfo) {
			if (pLeg.equals(lBean.getObligationBean().getType())
					&& pTransType.equals(lBean.getObligationBean().getTxnType())  &&
					!lObligationIds.contains(lBean.getObligationBean().getId()) ) {
				AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lBean.getObligationBean().getTxnEntity());
				if(lAppEntityBean!=null){
					if(EntityType.Platform.equals(pEntityType) && lAppEntityBean.isPlatform()){
						lRetBean = lBean;
					}else if(EntityType.Financier.equals(pEntityType) && lAppEntityBean.isFinancier()){
						lRetBean = lBean;
					}else if(EntityType.Purchaser.equals(pEntityType) && lAppEntityBean.isPurchaser()){
						lRetBean = lBean;
					}else if(EntityType.Supplier.equals(pEntityType) && lAppEntityBean.isSupplier()){
						lRetBean = lBean;
					}
					if(lRetBean!=null) break;
				}
			}
		}
		return lRetBean;
	}

}
