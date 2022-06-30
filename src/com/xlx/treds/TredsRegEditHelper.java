package com.xlx.treds;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.xlx.common.base.CommonCollection;
import com.xlx.common.memdb.MemoryTable;
import com.xlx.common.registry.bean.RegistryEntryBean;
import com.xlx.common.registry.bean.RegistryEntryValueBean;
import com.xlx.common.registry.bean.RegistryNodeBean;
import com.xlx.common.registry.dao.RegistryEntryDAO;
import com.xlx.common.registry.dao.RegistryEntryValueDAO;
import com.xlx.common.registry.dao.RegistryNodeDAO;
import com.xlx.common.registry.gui.RegistryExportHelper;
import com.xlx.common.utilities.DBHelper;

public class TredsRegEditHelper 
{
    public static Logger logger = Logger.getLogger(TredsRegEditHelper.class);

    private MemoryTable nodesTable, entriesTable, entryValuesTable;

    private RegistryExportHelper registryExportHelper = null;

    public TredsRegEditHelper()
    {
        initialize();
    }

    public void initialize()
    {
    	Connection lConnection =null;
        try
        {
        	registryExportHelper = new RegistryExportHelper();
        	nodesTable = new MemoryTable(RegistryNodeBean.ENTITY_NAME, RegistryNodeBean.class);
            nodesTable.addIndex(RegistryNodeBean.f_Id, true, new int[] { RegistryNodeBean.idx_Id });
            nodesTable.addIndex(RegistryNodeBean.f_ParentId, false, new int[] { RegistryNodeBean.idx_ParentId });
            //Added By Vishal
            nodesTable.addIndex(RegistryNodeBean.f_Name, false, new int[] { RegistryNodeBean.idx_Name });
            RegistryNodeDAO lRegistryNodeDAO = new RegistryNodeDAO();
            lConnection = DBHelper.getInstance().getConnection();
            CommonCollection lCollection = lRegistryNodeDAO.findCollection(lConnection,
                    "SELECT * FROM registrynodes WHERE RNRECORDVERSION > 0 ORDER BY 1");
            nodesTable.addRows(lCollection);

            entriesTable = new MemoryTable(RegistryEntryBean.ENTITY_NAME, RegistryEntryBean.class);
            entriesTable.addIndex(RegistryEntryBean.f_Id, true, new int[] { RegistryEntryBean.idx_Id });
            entriesTable.addIndex(RegistryEntryBean.f_RnId, false, new int[] { RegistryEntryBean.idx_RnId });
            //Added By Vishal
            entriesTable.addIndex(RegistryEntryBean.f_Name, false, new int[] { RegistryEntryBean.idx_Name });
            RegistryEntryDAO lRegistryEntryDAO = new RegistryEntryDAO();
            lCollection = lRegistryEntryDAO.findCollection(lConnection,
                    "SELECT * FROM registryentries WHERE RERECORDVERSION > 0 ORDER BY 1");
            entriesTable.addRows(lCollection);

            entryValuesTable = new MemoryTable(RegistryEntryValueBean.ENTITY_NAME, RegistryEntryValueBean.class);
            entryValuesTable.addIndex(RegistryEntryValueBean.f_Id, true, new int[] { RegistryEntryValueBean.idx_Id });
            entryValuesTable.addIndex(RegistryEntryValueBean.f_ReId, false,
                    new int[] { RegistryEntryValueBean.idx_ReId });
            RegistryEntryValueDAO lRegistryEntryValueDAO = new RegistryEntryValueDAO();
            lCollection = lRegistryEntryValueDAO.findCollection(lConnection,
                    "SELECT * FROM registryentryvalues WHERE REVRECORDVERSION > 0 ORDER BY 1");
            entryValuesTable.addRows(lCollection);

            registryExportHelper.setEntriesTable(entriesTable);
    		registryExportHelper.setNodesTable(nodesTable);
    		registryExportHelper.setEntryValuesTable(entryValuesTable);

            lConnection.close();
        }
        catch (Exception lException)
        {
            logger.error("Error while initializing", lException);
        }finally{
            if (lConnection != null) {
                try {
                    lConnection.close();
                } catch (Exception lException) {
					logger.error("TredsRegEditHelper.initialize : Error while closing database connection", lException);
                }
            }
        }
    }

    public MemoryTable getEntriesTable()
    {
        return entriesTable;
    }

    public MemoryTable getEntryValuesTable()
    {
        return entryValuesTable;
    }

    public MemoryTable getNodesTable()
    {
        return nodesTable;
    }

    public boolean importRegistry(String pFileName) throws SQLException
    {
    	boolean lRetVal = false;
    	try(Connection lConnection = DBHelper.getInstance().getConnection();)
		{
    		 lConnection.setAutoCommit(false);
    		 try{
        		 registryExportHelper.importRegistry(pFileName, lConnection);
        		 lConnection.commit();
        		 lRetVal = true;
    		 }catch(Exception lEx){
				logger.info("Error in importRegistry 1 : " + lEx.getMessage());
				if (lConnection != null)
					lConnection.rollback();
    		 }
		}
		catch (Exception pException)
		{
			logger.info("Error in importRegistry 2 : " + pException.getMessage());
        }
    	return lRetVal;
    }

    public static void main(String args[]) throws Exception
    {
        try
        {
        	
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        TredsRegEditHelper lTredsRegEditHelper = new TredsRegEditHelper();
    }

}
