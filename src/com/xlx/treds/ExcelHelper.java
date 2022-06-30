package com.xlx.treds;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.xlx.common.utilities.CommonUtilities;

public class ExcelHelper
{
	public static Logger logger = Logger.getLogger(ExcelHelper.class);
	
	public static Workbook getWorkbook(String pFile) throws Exception
	{
		return WorkbookFactory.create(new File(pFile));
	}
	
	public static Workbook getWorkbookNIO(String pFile) throws Exception
	{
		NPOIFSFileSystem lNpoifsFileSystem = new NPOIFSFileSystem(new File(pFile));
		return WorkbookFactory.create(lNpoifsFileSystem);
	}
	
	public static Workbook getWorkbook(byte[] pData) throws Exception
	{
		//POIFSFileSystem was being used earlier and it supported only older Excel file (*.xls) and not newer one (*.xlsx)
		ByteArrayInputStream lByteArrayInputStream = new ByteArrayInputStream(pData);
//		POIFSFileSystem lNpoifsFileSystem = new POIFSFileSystem(lByteArrayInputStream);
//		return WorkbookFactory.create(lNpoifsFileSystem);
		return WorkbookFactory.create(lByteArrayInputStream);
	}

	public static ArrayList<String[]> getExcelSheetData(Workbook pWorkbook, int pSheetIndex) throws Exception
    {
		return getExcelSheetData(pWorkbook, pSheetIndex, false);
    }
	public static ArrayList<String[]> getExcelSheetData(Workbook pWorkbook, int pSheetIndex, boolean pAddHeaderToData) throws Exception
    {
        ArrayList<String[]> lData = new ArrayList<String[]>();
        DecimalFormatSymbols lFormatSymbols = new DecimalFormatSymbols();
        Sheet lSheet = pWorkbook.getSheetAt(pSheetIndex);
        logger.info("Sheet name : " + pWorkbook.getSheetName(pSheetIndex));
        logger.info("Sheet rows : " + lSheet.getLastRowNum());
        Iterator<Row> lRows = lSheet.rowIterator();
        int lColCount = 0;
        while (lRows.hasNext())
        {
            // data rows
            String[] lRowData = null;
            //
        	Row lRow = (Row)lRows.next();
            Iterator<Cell> lCells = lRow.cellIterator();
            if (lRow.getRowNum() == 0) // header row. count columns
            {
                while (lCells.hasNext())
                {
                    lCells.next();
                    lColCount++;
                }
                logger.debug("Column count : " + lColCount);
                if(pAddHeaderToData){
                    try
                    {
                    	lRowData = getRowData(lRow, lColCount);
                    	if(lRowData != null)
                        	lData.add(lRowData);
                    }
                    catch(Exception pException)
                    {
                    	pException.printStackTrace();
                    	throw new Exception("Error while reading header : " + (lRow.getRowNum()+1));
                    }
                }
                continue;
            }
            
            try
            {
            	lRowData = getRowData(lRow, lColCount);
            	if(lRowData != null)
                	lData.add(lRowData);
            }
            catch(Exception pException)
            {
            	pException.printStackTrace();
            	throw new Exception("Error while reading row : " + (lRow.getRowNum()+1));
            }
        }
        logger.info("No. of records : " + lData.size());
        return lData;
    }
	
	public static boolean isEmpty(String[] pData)
	{
		if(pData==null || pData.length == 0)return true;
		for(String lData : pData)
		{
			if(CommonUtilities.hasValue(lData))
			{
				return false;
			}
		}
		return true;
	}
	
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#################0.##############", new DecimalFormatSymbols());

    public static String[] getRowData(Row pRow,int pColCount) throws Exception
    {
    	String[] lRowData = new String[pColCount];
    	Iterator<Cell> lCells = pRow.cellIterator();
    	// data rows
        while (lCells.hasNext())
        {
        	Cell lCell = (Cell)lCells.next();
            int lColIndex = lCell.getColumnIndex();
            if (lColIndex < pColCount)
            {
                try
                {
                	lRowData[lColIndex] = getCellData(lCell);
                }
                catch (Exception lException)
                {
                	//logger.info(lCell.getStringCellValue());
             		lException.printStackTrace();
                	throw new Exception("Error while reading cell : " + (lColIndex + 1)  + " msg : " + lException.getMessage() );
                }
            }
        }
        if(isEmpty(lRowData))return null;
    	return lRowData;
    }
    
    public static String getCellData(Cell pCell)
	{
		 switch (pCell.getCellType())
         {
         case Cell.CELL_TYPE_NUMERIC :
         case Cell.CELL_TYPE_FORMULA:
             return DECIMAL_FORMAT.format(pCell.getNumericCellValue());
         case Cell.CELL_TYPE_STRING : 
        	 return pCell.getRichStringCellValue().getString();
         case Cell.CELL_TYPE_BLANK :
        	 return null;
         case Cell.CELL_TYPE_BOOLEAN :
        	 return null;
         case Cell.CELL_TYPE_ERROR :
        	 return null;
         default :
             logger.info("Type not supported.");
             return null;
         }
	}
}
