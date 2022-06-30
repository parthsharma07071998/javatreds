package com.xlx.treds.other.bo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.xlx.common.memdb.MemoryDBConnection;
import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.CommonBusinessException;
import com.xlx.commonn.ExecutionContext;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.GenericDAO.AuditAction;
import com.xlx.commonn.bean.BeanFieldMeta;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.commonn.user.bean.IAppUserBean;
import com.xlx.treds.TredsHelper;
import com.xlx.treds.entity.bean.AppEntityBean;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.other.bean.CustomFieldBean;

public class CustomFieldBO {
    
    private GenericDAO<CustomFieldBean> customFieldDAO;
    private GenericDAO<AppEntityBean> appEntityDAO;

    public CustomFieldBO() {
        super();
        customFieldDAO = new GenericDAO<CustomFieldBean>(CustomFieldBean.class);
        appEntityDAO = new GenericDAO<AppEntityBean>(AppEntityBean.class);
    }
    
    public CustomFieldBean findBean(Connection pConnection, 
        CustomFieldBean pFilterBean) throws Exception {
    	StringBuilder lSql = new StringBuilder();
    	lSql.append(" SELECT * FROM CUSTOMFIELDS WHERE CFCODE = ");
    	lSql.append(DBHelper.getInstance().formatString(pFilterBean.getCode()));
    	if (pFilterBean.getId()==null) {
    		lSql.append(" and cfid in  ( ");
        	lSql.append(" SELECT max(cfid) FROM CUSTOMFIELDS WHERE CFCODE = ");
        	lSql.append(DBHelper.getInstance().formatString(pFilterBean.getCode()));
        	lSql.append(" ) ");
    	}else {
    		lSql.append(" and cfid = ").append(pFilterBean.getId());
    	}
        CustomFieldBean lCustomFieldBean = customFieldDAO.findBean(pConnection, lSql.toString());
        if (lCustomFieldBean == null) {
        	lCustomFieldBean = new CustomFieldBean();
        	lCustomFieldBean.setCode(pFilterBean.getCode());
        	lCustomFieldBean.setId(new Long(0));
        }
        return lCustomFieldBean;
    }
    
    public List<CustomFieldBean> findList(ExecutionContext pExecutionContext, CustomFieldBean pFilterBean, 
        List<String> pColumnList, IAppUserBean pUserBean) throws Exception {
        return customFieldDAO.findList(pExecutionContext.getConnection(), pFilterBean, pColumnList);
    }
    
    public void save(ExecutionContext pExecutionContext, CustomFieldBean pCustomFieldBean, IAppUserBean pUserBean, 
        boolean pNew) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();
        CustomFieldBean lOldCustomFieldBean = null;
        if (!pCustomFieldBean.getId().equals(new Long(0))) {
        	lOldCustomFieldBean = findBean(pExecutionContext.getConnection(), pCustomFieldBean);
        	Map<String,Object> lDiffData = TredsHelper.getInstance().getFieldListDiff(customFieldDAO,lOldCustomFieldBean,pCustomFieldBean);
        	if (lDiffData==null || lDiffData.isEmpty()) {
        		return;
        	}
        }
        pCustomFieldBean.setId(null);
        BeanMeta lInstBeanMeta = BeanMetaFactory.getInstance().getBeanMeta(InstrumentBean.class);
        List<String> lInstFields = new ArrayList<>();
        for(BeanFieldMeta lFieldMeta : lInstBeanMeta.getFieldList()) {
        	lInstFields.add(lFieldMeta.getName());
        }
        List<Map<String, Object>> lList = new ArrayList();
        lList = (List<Map<String, Object>>) pCustomFieldBean.getConfig().get("inputParams");
        for (Map<String, Object> lTmpMap : lList) {
        	if(lInstFields.contains(lTmpMap.get("name"))){
        		throw new CommonBusinessException("Field with name" + lTmpMap.get("name") +" already exist. Please change the name and try again.") ;
        	}
        }
        customFieldDAO.insert(lConnection, pCustomFieldBean, BeanMeta.FIELDGROUP_INSERT);
        AppEntityBean lAppEntityBean = TredsHelper.getInstance().getAppEntityBean(pCustomFieldBean.getCode());
        lAppEntityBean.setCfId(pCustomFieldBean.getId());
        if (appEntityDAO.update(lConnection, lAppEntityBean, AppEntityBean.FIELDGROUP_UPDATECUSTOMFIELDS) == 0)
            throw new CommonBusinessException(CommonBusinessException.RECORD_NOT_FOUND);
        appEntityDAO.insertAudit(lConnection, lAppEntityBean, AuditAction.Update, pUserBean.getId());
        MemoryDBConnection lMemoryDBConnection = pExecutionContext.getMemoryDBConnection();
        lMemoryDBConnection.deleteRow(AppEntityBean.ENTITY_NAME, AppEntityBean.f_Code, lAppEntityBean);
        lMemoryDBConnection.addRow(AppEntityBean.ENTITY_NAME, lAppEntityBean);
        pExecutionContext.commitAndDispose();
    }
    
    public void delete(ExecutionContext pExecutionContext, CustomFieldBean pFilterBean, 
        IAppUserBean pUserBean) throws Exception {
        pExecutionContext.setAutoCommit(false);
        Connection lConnection = pExecutionContext.getConnection();

        CustomFieldBean lCustomFieldBean = findBean(pExecutionContext.getConnection(), pFilterBean);
        customFieldDAO.delete(lConnection, lCustomFieldBean);        


        pExecutionContext.commitAndDispose();
    }
    
}
