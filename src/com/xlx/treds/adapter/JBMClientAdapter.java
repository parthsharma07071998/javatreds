package com.xlx.treds.adapter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.xlx.common.utilities.CommonUtilities;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.CompositeGenericDAO;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.http.bean.ApiResponseBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean;
import com.xlx.treds.adapter.bean.AdapterRequestResponseBean.ApiResponseStatus;
import com.xlx.treds.auction.bean.ObligationBean;
import com.xlx.treds.auction.bean.ObligationDetailBean;
import com.xlx.treds.auction.bean.ObligationSplitsBean;
import com.xlx.treds.instrument.bean.BHELPEMInstrumentBean;
import com.xlx.treds.instrument.bean.BHELPEMInstrumentBean.Type;
import com.xlx.treds.instrument.bean.FactoringUnitBean;
import com.xlx.treds.instrument.bean.InstrumentBean;

public class JBMClientAdapter implements IClientAdapter {
    private static Logger logger = Logger.getLogger(JBMClientAdapter.class);
	private static final String LOG_HEADER = "JBMClientAdapter :: ";
	
	private static final String DATE_FORMAT = AppConstants.DATE_FORMAT;
	private String entityCode = null;
    private ClientSettingsBean clientSettingsBean;
    private GenericDAO<BHELPEMInstrumentBean> bhelPemInstrumentDAO;
    private GenericDAO<ObligationBean> obligationDAO;
 
    //
    
	public JBMClientAdapter(String pEntityCode,ClientSettingsBean lClientSettingsBean){
		super();
		entityCode = pEntityCode;
		clientSettingsBean = lClientSettingsBean;
		bhelPemInstrumentDAO = new GenericDAO<BHELPEMInstrumentBean>(BHELPEMInstrumentBean.class);
		obligationDAO = new GenericDAO<ObligationBean>(ObligationBean.class);
	}
	
	@Override
	public String convertClientDataToTredsData(ProcessInformationBean pProcessInformationBean) throws Exception {
		return null;
	}


	@Override
	public String convertTredsDataToClientData(ProcessInformationBean pProcessInformationBean) throws Exception {
		return null;
	}

	@Override
	public boolean connectClient() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClientConnected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean sendResponseToClient(ProcessInformationBean pProcessInformationBean) throws Exception {
		if (ProcessInformationBean.PROCESSID_INST.equals(pProcessInformationBean.getProcessId())) {
			InstrumentBean lInstrumentBean = (InstrumentBean) pProcessInformationBean.getTredsDataForProcessing();
			BHELPEMInstrumentBean lBhelInstrumentBean = new BHELPEMInstrumentBean();
			lBhelInstrumentBean.setInId(lInstrumentBean.getId());
			lBhelInstrumentBean.setCreateTime(CommonUtilities.getCurrentDateTime());
			lBhelInstrumentBean.setType(Type.Instrument);
			lBhelInstrumentBean.setEntityCode(pProcessInformationBean.getEntityCode());
			bhelPemInstrumentDAO.insert(pProcessInformationBean.getConnection(), lBhelInstrumentBean);
		} 
		return false;
	}

	@Override
	public String getURL(Long pProcessId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long logOutgoing(ProcessInformationBean pProcessInformationBean, String pOutUrl, ApiResponseBean pApiResponseBean, AdapterRequestResponseBean pAdapterRequestResponseBean, boolean pNew) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long logInComing(ProcessInformationBean pProcessInformationBean, String pInApiUrl, ApiResponseStatus pApiResponseStatus, boolean pNew, boolean pValidateUniqueRequest) throws CommonBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addPostActionToQueue(ProcessInformationBean pOldProcessInformationBean) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void performActionPostIncoming(ProcessInformationBean pOldProcessInformationBean) {
		if (ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pOldProcessInformationBean.getProcessId()) 
				|| ProcessInformationBean.PROCESSID_LEG2SETTLED.equals(pOldProcessInformationBean.getProcessId())) {
			Object[] lLeg1Details = (Object[]) pOldProcessInformationBean.getTredsDataForProcessing();
			FactoringUnitBean lFactBean = (FactoringUnitBean) lLeg1Details[0];
			InstrumentBean lInstBean = (InstrumentBean) lLeg1Details[1];
			ObligationBean lOBBean = (ObligationBean) lLeg1Details[2];
			List<ObligationSplitsBean> lSplitsBeans = (List<ObligationSplitsBean>) lLeg1Details[3];
			BHELPEMInstrumentBean lBhelInstrumentBean = new BHELPEMInstrumentBean();
			lBhelInstrumentBean.setInId(lOBBean.getId());
			lBhelInstrumentBean.setCreateTime(CommonUtilities.getCurrentDateTime());
			if (ObligationBean.Type.Leg_1.equals(lOBBean.getType())) {
				if (lFactBean.getPurchaser().equals(lOBBean.getTxnEntity())) {
					lBhelInstrumentBean.setType(Type.Leg1Interest);
				}else if (lFactBean.getSupplier().equals(lOBBean.getTxnEntity())) {
					lBhelInstrumentBean.setType(Type.Leg1);
				}
			}else if (ObligationBean.Type.Leg_2.equals(lOBBean.getType())) {
				lBhelInstrumentBean.setType(Type.Leg2);
			}
			lBhelInstrumentBean.setEntityCode(pOldProcessInformationBean.getEntityCode());
			try {
				bhelPemInstrumentDAO.insert(pOldProcessInformationBean.getConnection(), lBhelInstrumentBean);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ProcessInformationBean.PROCESSID_LEG1SETTLED.equals(pOldProcessInformationBean.getProcessId())){
			Object[] lLeg1Details = (Object[]) pOldProcessInformationBean.getTredsDataForProcessing();
			FactoringUnitBean lFactBean = (FactoringUnitBean) lLeg1Details[0];
			InstrumentBean lInstBean = (InstrumentBean) lLeg1Details[1];
			StringBuilder lSql = new StringBuilder();
			lSql.append(" Select DISTINCT OBID from OBLIGATIONS,Obligationsplits WHERE OBRECORDVERSION>0  ");
			lSql.append(" and OBSRECORDVERSION>0 and OBSOBID=OBID ");
			lSql.append(" AND OBFUID = ").append(lFactBean.getId());
			lSql.append(" AND OBTXNENTITY = ").append(DBHelper.getInstance().formatString(lFactBean.getPurchaser()));
			lSql.append(" AND OBTYPE = ").append(DBHelper.getInstance().formatString(ObligationBean.Type.Leg_2.getCode()));
			List<ObligationBean> lObligList = null;
			try {
				lObligList = obligationDAO.findListFromSql(pOldProcessInformationBean.getConnection(), lSql.toString(), -1);
				if (lObligList!=null) {
					for (ObligationBean lBean :lObligList ) {
						BHELPEMInstrumentBean lBhelInstrumentBean = new BHELPEMInstrumentBean();
						lBhelInstrumentBean.setInId(lBean.getId());
						lBhelInstrumentBean.setCreateTime(CommonUtilities.getCurrentDateTime());
						lBhelInstrumentBean.setType(Type.Leg2Future);
						lBhelInstrumentBean.setEntityCode(pOldProcessInformationBean.getEntityCode());
						try {
							bhelPemInstrumentDAO.insert(pOldProcessInformationBean.getConnection(), lBhelInstrumentBean);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}catch (Exception e) {
				
			}
		}
		
	}

	@Override
	public boolean reSendResponseToClient(ProcessInformationBean pProcessInformationBean, AdapterRequestResponseBean pAdapterRequestResponseBean) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	

	
}
