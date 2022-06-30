
package com.xlx.treds.entity.bo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xlx.treds.entity.bean.CompanyBankDetailBean;
import com.xlx.treds.entity.bean.CompanyDetailBean;
import com.xlx.treds.entity.bean.CompanyLocationBean;
import com.xlx.treds.entity.bean.SettleBankLocationMapBean;

import groovy.json.JsonBuilder;

import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;

public class SettleBankLocationMapBO {

	public static final Logger logger = LoggerFactory.getLogger(SettleBankLocationMapBO.class);
    
    private GenericDAO<SettleBankLocationMapBean> settleBankLocationMapDAO;
    private GenericDAO<CompanyDetailBean> companyDetailDAO;
    private GenericDAO<CompanyLocationBean> companyLocationDAO;

    public SettleBankLocationMapBO() {
        super();
        settleBankLocationMapDAO = new GenericDAO<SettleBankLocationMapBean>(SettleBankLocationMapBean.class);
        companyDetailDAO = new GenericDAO<CompanyDetailBean>(CompanyDetailBean.class);	
        companyLocationDAO = new GenericDAO<CompanyLocationBean>(CompanyLocationBean.class);
    }
    
    public SettleBankLocationMapBean findBean(ExecutionContext pExecutionContext, 
        SettleBankLocationMapBean pFilterBean) throws Exception {
        SettleBankLocationMapBean lSettleBankLocationMapBean = settleBankLocationMapDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lSettleBankLocationMapBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lSettleBankLocationMapBean;
    }
    
    public List<SettleBankLocationMapBean> findList(ExecutionContext pExecutionContext, SettleBankLocationMapBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return settleBankLocationMapDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, SettleBankLocationMapBean pSettleBankLocationMapBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        SettleBankLocationMapBean lOldSettleBankLocationMapBean = null;
        if (pNew) {
            SettleBankLocationMapBean lFilterBean = pSettleBankLocationMapBean;
            SettleBankLocationMapBean lNewSettleBankLocBean = settleBankLocationMapDAO.findBean(lConnection, lFilterBean);
            if(lNewSettleBankLocBean != null){
            	throw new CommonBusinessException("Record already present and connot be modified.Please create a new record.");
            }
            if(pSettleBankLocationMapBean.getClId() == null && pSettleBankLocationMapBean.getEnableSetLoc() == null){
            	pSettleBankLocationMapBean.setIsLocationEnable(Boolean.FALSE);
            	pSettleBankLocationMapBean.setIsEnable(Boolean.FALSE);
            }else if(pSettleBankLocationMapBean.getClId() != null){
            	pSettleBankLocationMapBean.setIsLocationEnable(Boolean.FALSE);
            	if(CommonAppConstants.Yes.Yes.equals(pSettleBankLocationMapBean.getEnableSetLoc())){
                	pSettleBankLocationMapBean.setIsEnable(Boolean.TRUE);
            	}else{
            		pSettleBankLocationMapBean.setIsEnable(Boolean.FALSE);
            	}
            }
            settleBankLocationMapDAO.insert(lConnection, pSettleBankLocationMapBean);
        } else {
            lOldSettleBankLocationMapBean = findBean(pExecutionContext, pSettleBankLocationMapBean);
            

            if (settleBankLocationMapDAO.update(lConnection, pSettleBankLocationMapBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        }

        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, SettleBankLocationMapBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        SettleBankLocationMapBean lSettleBankLocationMapBean = findBean(pExecutionContext, pFilterBean);
        settleBankLocationMapDAO.delete(lConnection, lSettleBankLocationMapBean);        


        pExecutionContext.commitAndDispose();
    }

	public List<CompanyDetailBean> getCompanyDetails(Connection pConnection, IAppUserBean lUserBean) throws Exception {
		StringBuilder lSql = new StringBuilder();
		lSql.append(" SELECT * FROM COMPANYDETAILS ");
		lSql.append(" WHERE CDRECORDVERSION > 0 ");
		List<CompanyDetailBean> lCompanyDetailBeanList = companyDetailDAO.findListFromSql(pConnection, lSql.toString(), -1);
		return lCompanyDetailBeanList;
		
	}

	public String getCompanySettleDetail(Connection pConnection, Map<String, Object> pMap,
			IAppUserBean lUserBean) throws Exception {
		boolean lCompanyDetailFlag = false;
		boolean lLocationDetailFlag = false;
		SettleBankLocationMapBean lSettleBankLocationBean = new SettleBankLocationMapBean();
		Map<String,Object> lMap = new HashMap<String,Object>();
		CompanyDetailBean lCompanyDetailFilterBean = new CompanyDetailBean();
		if(pMap.get("cdId") == null){
			throw new CommonBusinessException("Please select company name.");
		}
		lCompanyDetailFilterBean.setId(new Long(pMap.get("cdId").toString()));
		CompanyDetailBean lCompanyDetailBean = companyDetailDAO.findBean(pConnection, lCompanyDetailFilterBean);
		CompanyLocationBean lCompanyLocationFilterBean = new CompanyLocationBean();
		lCompanyLocationFilterBean.setCdId(new Long(pMap.get("cdId").toString()));
		CompanyLocationBean lCompanyLocationBean = companyLocationDAO.findBean(pConnection, lCompanyLocationFilterBean);
		if(lCompanyDetailBean != null){
			if(!CommonAppConstants.Yes.Yes.equals(lCompanyDetailBean.getEnableLocationwiseSettlement())
					&& lCompanyDetailBean.getEnableLocationwiseSettlement() == null){
				lSettleBankLocationBean.setIsLocationEnable(Boolean.FALSE);
				lMap.put("companyDetailFlag", "false");
				lMap.put("enableSetLoc", CommonAppConstants.YesNo.No);
			}else if(CommonAppConstants.Yes.Yes.equals(lCompanyDetailBean.getEnableLocationwiseSettlement())){
				if(CommonAppConstants.Yes.Yes.equals(lCompanyLocationBean.getEnableSettlement())){
					lSettleBankLocationBean.setIsLocationEnable(Boolean.TRUE);
					lSettleBankLocationBean.setIsEnable(Boolean.TRUE);
					lMap.put("companyDetailFlag", "true");
					lMap.put("enableSetLoc", CommonAppConstants.YesNo.Yes);
				}else if(!CommonAppConstants.Yes.Yes.equals(lCompanyLocationBean.getEnableSettlement())){
					lSettleBankLocationBean.setIsLocationEnable(Boolean.TRUE);
					lSettleBankLocationBean.setIsEnable(Boolean.FALSE);
					lMap.put("companyDetailFlag", "true");
					lMap.put("enableSetLoc", CommonAppConstants.YesNo.No);
				}
			}
		}else{
			throw new CommonBusinessException("Company not found.");
		}
		return new JsonBuilder(lMap).toString();
	}
    
}
