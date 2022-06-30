
package com.xlx.treds.instrument.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.treds.TredsHelper;
import com.xlx.treds.auction.bean.PurchaserSupplierLinkBean;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.instrument.bean.InstrumentCreationKeysBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean;
import com.xlx.treds.master.bean.AuctionChargeSlabBean.ChargeType;
import com.xlx.treds.user.bean.AppUserBean;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;

public class InstrumentCreationKeysBO {

	public static final Logger logger = LoggerFactory.getLogger(InstrumentCreationKeysBO.class);
    
    private GenericDAO<InstrumentCreationKeysBean> instrumentCreationKeysDAO;
    private GenericDAO<PurchaserSupplierLinkBean> purchaserSupplierLinkDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;
    private BeanMeta instrumentCreationKeysBeanMeta;

    public InstrumentCreationKeysBO() {
        super();
        instrumentCreationKeysDAO = new GenericDAO<InstrumentCreationKeysBean>(InstrumentCreationKeysBean.class);
        purchaserSupplierLinkDAO = new GenericDAO<PurchaserSupplierLinkBean>(PurchaserSupplierLinkBean.class);
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
        instrumentCreationKeysBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentCreationKeysBean.class);
    }
    
    public InstrumentCreationKeysBean findBean(ExecutionContext pExecutionContext, 
        InstrumentCreationKeysBean pFilterBean) throws Exception {
        InstrumentCreationKeysBean lInstrumentCreationKeysBean = instrumentCreationKeysDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lInstrumentCreationKeysBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lInstrumentCreationKeysBean;
    }
    
    public List<InstrumentCreationKeysBean> findList(ExecutionContext pExecutionContext, InstrumentCreationKeysBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return instrumentCreationKeysDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, InstrumentCreationKeysBean pInstrumentCreationKeysBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        InstrumentCreationKeysBean lOldInstrumentCreationKeysBean = null;
        if (pNew) {

            pInstrumentCreationKeysBean.setRecordCreator(pUserBean.getId());
            instrumentCreationKeysDAO.insert(lConnection, pInstrumentCreationKeysBean);
        } else {
            lOldInstrumentCreationKeysBean = findBean(pExecutionContext, pInstrumentCreationKeysBean);
            

            pInstrumentCreationKeysBean.setRecordUpdator(pUserBean.getId());
            if (instrumentCreationKeysDAO.update(lConnection, pInstrumentCreationKeysBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, InstrumentCreationKeysBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        InstrumentCreationKeysBean lInstrumentCreationKeysBean = findBean(pExecutionContext, pFilterBean);
        lInstrumentCreationKeysBean.setRecordUpdator(pUserBean.getId());
        instrumentCreationKeysDAO.delete(lConnection, lInstrumentCreationKeysBean);        


        pExecutionContext.commitAndDispose();
    }

    public InstrumentCreationKeysBean saveInstCreationKeys(Connection pConnection, Map<String, Object> pMap, AppUserBean pUserBean) throws Exception {
		InstrumentCreationKeysBean lBean = new InstrumentCreationKeysBean();
		instrumentCreationKeysBeanMeta.validateAndParse(lBean, pMap, null, null);
		if(lBean != null){
			
			List<InstrumentCreationKeysBean> lTmpList = getInstrumentKeys(pConnection, lBean);
			if(lTmpList!=null && lTmpList.size() > 0) {
				throw new CommonBusinessException("Key : "+ lBean.getKey() + " already exists.");
			}
			
			PurchaserSupplierLinkBean lPSLFilterBean = new PurchaserSupplierLinkBean();
			lPSLFilterBean.setPurchaser(lBean.getPurchaserCode());
			lPSLFilterBean.setPurchaserSupplierRef(lBean.getInternalVendorRefNo());
			PurchaserSupplierLinkBean lPSLBean = purchaserSupplierLinkDAO.findBean(pConnection, lPSLFilterBean);
			if(lPSLBean == null || Objects.isNull(lPSLBean)){
				throw new CommonBusinessException(" Please provide valid Purchaser Code and-or Internal Vendor Ref No. for key "+lBean.getKey());
			}
			lBean.setSupplierCode(lPSLBean.getSupplier());
			AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(lBean.getSupplierCode());
			CompanyLocationBean lCompanyLocationFilterBean = new CompanyLocationBean();
			if(lAppEntityBean != null){
				lCompanyLocationFilterBean.setCdId(lAppEntityBean.getCdId());
				lCompanyLocationFilterBean.setGstn(lBean.getSupplierGstn());
				CompanyLocationBean lCompanyLocationBean = companyLocationDAO.findBean(pConnection, lCompanyLocationFilterBean);
				if(Objects.isNull(lCompanyLocationBean)){
					throw new CommonBusinessException(" Invalid supplier GSTN Number for key "+lBean.getKey());
				}
			}
			lBean.setRecordCreator(pUserBean.getId());
			instrumentCreationKeysDAO.insert(pConnection, lBean);
		}
		return lBean;
    }
    
    public List<InstrumentCreationKeysBean> getInstCreationList(Connection pConnection, String pPurEntity, String pSupEntity,Long pInId,Long lSupClid) throws Exception{
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM INSTRUMENTCREATIONKEYS,Companylocations ");
    	lSql.append(" WHERE ICKRECORDVERSION > 0 ");
    	lSql.append(" AND CLRECORDVERSION>0 AND ICKSUPPLIERGSTN=CLGSTN	");
    	lSql.append(" AND CLID = ").append(lSupClid);
    	lSql.append(" AND ICKPURCHASERCODE = ").append(DBHelper.getInstance().formatString(pPurEntity));
    	lSql.append(" AND ICKSUPPLIERCODE = ").append(DBHelper.getInstance().formatString(pSupEntity));
    	lSql.append(" AND ( ICKINID IS NULL OR ICKINID = ").append((pInId==null?DBHelper.getInstance().formatString(""):pInId)).append(" ) ");
    	List<InstrumentCreationKeysBean> lInstCreationKeysBeanList = instrumentCreationKeysDAO.findListFromSql(pConnection, lSql.toString(), -1);
    	return lInstCreationKeysBeanList;
    }

    public List<InstrumentCreationKeysBean> getInstCreationList(Connection pConnection, String pPurEntity) throws Exception{
    	StringBuilder lSql = new StringBuilder();
    	String lFieldsStr = instrumentCreationKeysDAO.getDBColumnNameCsv(null,Arrays.asList("purchaserCode"));
    	lSql.append(" SELECT ").append(lFieldsStr).append(" , inStatus ICKpurchaserCode ");
    	lSql.append(" FROM INSTRUMENTCREATIONKEYS ");
    	lSql.append(" LEFT OUTER JOIN Instruments ON ( ICKINID = INID ) ");
    	lSql.append(" WHERE ICKRECORDVERSION > 0 ");
    	lSql.append(" AND ( ICKRECORDVERSION IS NULL OR ICKRECORDVERSION > 0 )  ");
    	lSql.append(" AND ICKPURCHASERCODE = ").append(DBHelper.getInstance().formatString(pPurEntity));
    	lSql.append(" AND ( INSTATUS IS NULL OR INSTATUS IN ( ");
    	lSql.append( DBHelper.getInstance().formatString(InstrumentBean.Status.Factored.getCode()) );
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_1_Settled.getCode()));
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_1_Failed.getCode()));
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_2_Settled.getCode()));
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_2_Failed.getCode()));
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_3_Generated.getCode()));
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_3_Settled.getCode()));
    	lSql.append(",").append(DBHelper.getInstance().formatString(InstrumentBean.Status.Leg_3_Failed.getCode()));
    	lSql.append(" ) )  ");
    	List<InstrumentCreationKeysBean> lInstCreationKeysBeanList = instrumentCreationKeysDAO.findListFromSql(pConnection, lSql.toString(), -1);
    	if(lInstCreationKeysBeanList!=null && lInstCreationKeysBeanList.size() > 0) {
    		for(InstrumentCreationKeysBean lBean : lInstCreationKeysBeanList) {
    			if(StringUtils.isNotEmpty(lBean.getPurchaserCode())) {
        			lBean.setInstStatus((InstrumentBean.Status) TredsHelper.getInstance().getValue(InstrumentBean.class,"status",lBean.getPurchaserCode()));
    			}else {
    				lBean.setInstStatus(null);
    			}
    			lBean.setPurchaserCode(pPurEntity);
    		}
    	}
    	return lInstCreationKeysBeanList;
    }

    
	public List<InstrumentCreationKeysBean> getInstrumentKeys(Connection pConnection,InstrumentCreationKeysBean pInstrumentCreationKeysBean) throws Exception{
		List<InstrumentCreationKeysBean> lInstrumentCreationKeysBeans = null;
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM INSTRUMENTCREATIONKEYS WHERE ");
		lSql.append(" ICKREFNO|| '^' ||ICKPONUMBER || '^' || ICKSINUMBER = " );
		lSql.append(DBHelper.getInstance().formatString(pInstrumentCreationKeysBean.getKey()));
		lInstrumentCreationKeysBeans = instrumentCreationKeysDAO.findListFromSql(pConnection, lSql.toString(),-1);
		return lInstrumentCreationKeysBeans;
	}

    
}
