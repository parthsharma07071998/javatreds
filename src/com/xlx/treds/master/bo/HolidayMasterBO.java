package com.xlx.treds.master.bo;

import java.sql.Connection;
import java.util.List;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.AppConstants;
import com.xlx.treds.master.bean.HolidayMasterBean;

public class HolidayMasterBO {
    
    private GenericDAO<HolidayMasterBean> holidayMasterDAO;
    private BeanMeta holidayMasterBeanMeta;

    public HolidayMasterBO() {
        super();
        holidayMasterDAO = new GenericDAO<HolidayMasterBean>(HolidayMasterBean.class);
        holidayMasterBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(HolidayMasterBean.class);
    }
    
    public HolidayMasterBean findBean(ExecutionContext pExecutionContext, 
        HolidayMasterBean pFilterBean) throws Exception {
        HolidayMasterBean lHolidayMasterBean = holidayMasterDAO.findByPrimaryKey(pExecutionContext.getConnection(), pFilterBean);
        if (lHolidayMasterBean == null) 
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
  
        return lHolidayMasterBean;
    }
    
    public List<HolidayMasterBean> findList(ExecutionContext pExecutionContext, HolidayMasterBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return holidayMasterDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public List<HolidayMasterBean> findListFromSql(ExecutionContext pExecutionContext, HolidayMasterBean pFilterBean, 
            List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
    		StringBuilder lSql = new StringBuilder();
    		lSql.append(" SELECT * FROM HolidayMaster ");
    		lSql.append(" where HMRecordVersion > 0");
    		if(pFilterBean.getFromDate() != null){
    			lSql.append(" and HMDate >= ").append(DBHelper.getInstance().formatDate(pFilterBean.getFromDate()));
    		}
    		if(pFilterBean.getToDate() != null){
    			lSql.append(" and HMDate <= ").append(DBHelper.getInstance().formatDate(pFilterBean.getToDate()));
    		}
            return holidayMasterDAO.findListFromSql(pExecutionContext.getConnection(), lSql.toString(), -1);
        }
    
    public void save(ExecutionContext pExecutionContext, HolidayMasterBean pHolidayMasterBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        HolidayMasterBean lOldHolidayMasterBean = null;
        if (pNew) {
            pHolidayMasterBean.setRecordCreator(pUserBean.getId());
            holidayMasterDAO.insert(lConnection, pHolidayMasterBean);
            holidayMasterDAO.insertAudit(lConnection, pHolidayMasterBean, GenericDAO.AuditAction.Insert, pUserBean.getId());
        } else {
            lOldHolidayMasterBean = findBean(pExecutionContext, pHolidayMasterBean);
            pHolidayMasterBean.setRecordUpdator(pUserBean.getId());
            if (holidayMasterDAO.update(lConnection, pHolidayMasterBean, BeanMeta.FIELDGROUP_UPDATE) == 0)
                throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
            pHolidayMasterBean.setRecordCreator(lOldHolidayMasterBean.getRecordCreator());
            pHolidayMasterBean.setRecordCreateTime(lOldHolidayMasterBean.getRecordCreateTime());
            lMemoryDBConnection.deleteRow(HolidayMasterBean.ENTITY_NAME, HolidayMasterBean.f_Id, lOldHolidayMasterBean);
            holidayMasterDAO.insertAudit(lConnection, pHolidayMasterBean, GenericDAO.AuditAction.Update, pUserBean.getId());
        }
        lMemoryDBConnection.addRow(HolidayMasterBean.ENTITY_NAME, pHolidayMasterBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, HolidayMasterBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        if (!AppConstants.DOMAIN_PLATFORM.equals(pUserBean.getDomain()))
            throw new CommonBusinessException(CommonBusinessException.ACCESS_DENIED);
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        HolidayMasterBean lHolidayMasterBean = findBean(pExecutionContext, pFilterBean);
        lHolidayMasterBean.setRecordUpdator(pUserBean.getId());
        holidayMasterDAO.delete(lConnection, lHolidayMasterBean);        
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(HolidayMasterBean.ENTITY_NAME, HolidayMasterBean.f_Id, lHolidayMasterBean);
        pExecutionContext.commitAndDispose();
    }
    
}
