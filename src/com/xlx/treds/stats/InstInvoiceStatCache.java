package com.xlx.treds.stats;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.poi.util.Units;

import com.xlx.common.utilities.DBHelper;
import com.xlx.commonn.BeanMetaFactory;
import com.xlx.commonn.GenericBean;
import com.xlx.commonn.GenericDAO;
import com.xlx.commonn.bean.BeanMeta;
import com.xlx.treds.instrument.bean.InstrumentBean;
import com.xlx.treds.notialrt.bean.NotiAlrtBean;
import com.xlx.treds.stats.bean.StatsCacheBean;
import com.xlx.treds.stats.bean.StatsCacheBean.Type;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class InstInvoiceStatCache  implements IStatsCacheGenerator {
    private static Logger logger = Logger.getLogger(InstInvoiceStatCache.class);
    
    GenericDAO<InstrumentBean> instrumentDAO = null;
    GenericDAO<StatsCacheBean> statsCacheDAO = null;
    GenericDAO<NotiAlrtBean> notiAlrtDAO = null;
    private GenericDAO genericDAO;
    
    private String statType = null;
    private int instrumentCount = 0;
    private int expiry = 0;
    Map<String, Object> configMap = null;
    
    public static final String INSTRUMENTCOUNT = "instrumentCount";
    public static final String EXPIRY = "expiry";
    public static final String ISPATTERN = "isPattern";
    public static final String PATTERN = "pattern";
    public static final String BEANMETAFILE = "beanMetaFile";
    public static final String BEANCLASS = "beanClass";
    public static final String UNITS = "units";
    public static final String TENS = "tens";
    public static final String HUNDREDS = "hundreds";
    public static final String THOUSANDS = "thousands";
    public static final String INSTNUMBER = "instNumber";
    public static final String MAXAMTOCCURRENCE = "maxAmtOccurrence";
    public static final String PONUMBER = "poNumber";
    public static final String ISPOPATTERN = "isPoPattern";
    public static final String POPATTERN = "poPattern";
    //
    public static final int ARR_UNITS_POS = 0;
    public static final int ARR_TENS_POS = 1;
    public static final int ARR_HUNDREDS_POS = 2;
    public static final int ARR_THOUSANDS_POS = 3;
    
    public InstInvoiceStatCache(String pStatType, Object pConfigMap) throws Exception{
		super();
		statType = pStatType;
		configMap = (Map<String, Object>) pConfigMap;
		setConfigs();
		getGenericDAO();
		instrumentDAO = new GenericDAO<InstrumentBean>(InstrumentBean.class);
		statsCacheDAO = new GenericDAO<StatsCacheBean>(StatsCacheBean.class);
		notiAlrtDAO = new GenericDAO<NotiAlrtBean>(NotiAlrtBean.class);
	}

	private void getGenericDAO() throws Exception {
		Class lClass = Class.forName((String)configMap.get(BEANCLASS));
		BeanMeta lBeanMeta =BeanMetaFactory.getInstance().getBeanMeta(null,(String)configMap.get(BEANMETAFILE));
    	genericDAO = new GenericDAO<>(lClass,  null,lBeanMeta );         
	}

	public void setConfigs() {
		if (!configMap.isEmpty()) {
			if (configMap.containsKey(INSTRUMENTCOUNT)) {
				instrumentCount = ((Integer)configMap.get(INSTRUMENTCOUNT)).intValue();
			}
			if (configMap.containsKey(EXPIRY)) {
				expiry = ((Integer)configMap.get(EXPIRY)).intValue();
			}
		}
	}
	
	@Override
	public void generateAlert(Object pObject) {
		try{
			InstrumentBean lInstrumentBean =  (InstrumentBean) pObject;
			String lValue = (String) getValue(Type.Instrument_Invoice, lInstrumentBean.getSupplier());
			validateInvNumPattern(lInstrumentBean.getSupplier(),lValue, lInstrumentBean.getInstNumber());
			validateAmountPattern(lInstrumentBean.getSupplier(),lValue, lInstrumentBean.getAmount());
			validatePoNumPattern(lInstrumentBean.getSupplier(),lValue, lInstrumentBean.getPoNumber());
		}catch (Exception lEx) {
			logger.debug("Error in generateAlert InstInvoiceStatCache");
			logger.debug(lEx.getStackTrace());
		}
	}
 
	@Override
	public void generate(String pKey) {
 		StatsCacheBean lStatsCacheBean = new StatsCacheBean();
		lStatsCacheBean.setKey(pKey);
		lStatsCacheBean.setType(Type.Instrument_Invoice);
		DBHelper lDBHelper = DBHelper.getInstance();
		
		Map<String,Object> lDefinedSettings = new HashMap<>();
		try (Connection lConnection = lDBHelper.getConnection()){
			StatsCacheBean lOldStatsCacheBean = statsCacheDAO.findBean(lConnection, lStatsCacheBean);
			StringBuilder lSql = new StringBuilder();
			lSql.append(" SELECT INID,ININSTNUMBER,INPONUMBER,ININSTDATE ");
			lSql.append(" , trunc(INAMOUNT) INAMOUNT ");
			lSql.append(" , MOD (trunc(INAMOUNT), 10)/1 INUNITS ");
			lSql.append(" , MOD (trunc(INAMOUNT), 100)/10 INTENS ");
			lSql.append(" , MOD (trunc(INAMOUNT), 1000)/100 INHUNDREDS ");
			lSql.append(" , MOD (trunc(INAMOUNT), 10000)/1000 INTHOUSANDS ");
			lSql.append(" FROM INSTRUMENTS WHERE ");
			lSql.append(" INSUPPLIER = ").append(lDBHelper.formatString(pKey));
			lSql.append(" AND INGROUPFLAG IS NULL ");
			lSql.append(" AND INRECORDVERSION > 0 ");
			lSql.append(" ORDER BY INRECORDCREATETIME DESC ");
			lSql.append(" FETCH FIRST ").append(instrumentCount).append(" ROW ONLY ");
			List<GenericBean> lList = genericDAO.findListFromSql(lConnection, lSql.toString(), -1);
			Map<String, Integer> lInstNumberMap = new HashMap<String, Integer>();
			Map<String, Integer> lPoNumberMap = new HashMap<String, Integer>();
			String lPattern = null;
			int[] lCounter = new int[] {0,0,0,0};
			for (GenericBean lGenericBean : lList) {
				lPattern = getPattern((String)lGenericBean.getProperty(INSTNUMBER));
				if (lInstNumberMap.containsKey(lPattern)) {
					int lCount = lInstNumberMap.get(lPattern).intValue();
					lInstNumberMap.put(lPattern,lCount+1);
				}else {
					lInstNumberMap.put(lPattern,1);
				}
				lPattern = getPattern((String)lGenericBean.getProperty(PONUMBER));
				if (lPoNumberMap.containsKey(lPattern)) {
					int lCount = lPoNumberMap.get(lPattern).intValue();
					lPoNumberMap.put(lPattern,lCount+1);
				}else {
					lPoNumberMap.put(lPattern,1);
				}
				if (((BigDecimal)lGenericBean.getProperty(UNITS)).compareTo(BigDecimal.ZERO) == 0) {
					lCounter[ARR_UNITS_POS] = lCounter[ARR_UNITS_POS] + 1;
				}
				if (isRounded((BigDecimal)lGenericBean.getProperty(TENS))) {
					lCounter[ARR_TENS_POS] = lCounter[ARR_TENS_POS] + 1;
				}
				if (isRounded((BigDecimal)lGenericBean.getProperty(HUNDREDS))) {
					lCounter[ARR_HUNDREDS_POS] = lCounter[ARR_HUNDREDS_POS] + 1;
				}
				if (isRounded((BigDecimal)lGenericBean.getProperty(THOUSANDS))) {
					lCounter[ARR_THOUSANDS_POS] = lCounter[ARR_THOUSANDS_POS] + 1;
				}
			}
			lPattern = null;
			int lCount = 0;
			for (String lKeyPattern : lInstNumberMap.keySet()) {
				if(lCount == 0) {
					lCount = lInstNumberMap.get(lKeyPattern).intValue();
					lPattern = lKeyPattern;
				}else if (lCount < lInstNumberMap.get(lKeyPattern).intValue()) {
					lCount = lInstNumberMap.get(lKeyPattern).intValue();
					lPattern = lKeyPattern;
				}
			}
			lDefinedSettings.put(ISPATTERN,isPattern(lCount));
			lDefinedSettings.put(PATTERN,lPattern);
			lPattern = null;
			lCount = 0;
			for (String lKeyPattern : lPoNumberMap.keySet()) {
				if(lCount == 0) {
					lCount = lPoNumberMap.get(lKeyPattern).intValue();
					lPattern = lKeyPattern;
				}else if (lCount < lPoNumberMap.get(lKeyPattern).intValue()) {
					lCount = lPoNumberMap.get(lKeyPattern).intValue();
					lPattern = lKeyPattern;
				}
			}
			lDefinedSettings.put(ISPOPATTERN,isPattern(lCount));
			lDefinedSettings.put(POPATTERN,lPattern);
			//
			lDefinedSettings.put(UNITS,lCounter[ARR_UNITS_POS]);
			lDefinedSettings.put(TENS,lCounter[ARR_TENS_POS]);
			lDefinedSettings.put(HUNDREDS,lCounter[ARR_HUNDREDS_POS]);
			lDefinedSettings.put(THOUSANDS,lCounter[ARR_THOUSANDS_POS]);
			lStatsCacheBean.setValue(new JsonBuilder(lDefinedSettings).toString());
			lStatsCacheBean.setExpiry(new Timestamp(System.currentTimeMillis()+expiry));
			if (lOldStatsCacheBean==null) {
				statsCacheDAO.insert(lConnection, lStatsCacheBean);
			}else {
				lStatsCacheBean.setId(lOldStatsCacheBean.getId());
				statsCacheDAO.update(lConnection, lStatsCacheBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isPattern(int lCount) {
		if (lCount>1) {
			return true;
		}
		return false;
	}


	@Override
	public Object getValue(Type pType, String pKey) {
		DBHelper lDBHelper = DBHelper.getInstance();
		StringBuilder lSql = new StringBuilder();
		try(Connection lConnection = DBHelper.getInstance().getConnection();){
			lSql.append(" SELECT * FROM STATSCACHE WHERE");
			lSql.append(" SCTYPE = ").append(lDBHelper.formatString(pType.getCode()));
			lSql.append(" AND SCKEY = ").append(lDBHelper.formatString(pKey));
			StatsCacheBean lStatsCacheBean = statsCacheDAO.findBean(lConnection, lSql.toString());
			if (lStatsCacheBean==null || new Timestamp(System.currentTimeMillis()).compareTo(lStatsCacheBean.getExpiry()) > 1) {
				generate(pKey);
				return getValue(pType, pKey);
			}
			return lStatsCacheBean.getValue();
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	public static String getPattern(String pInvNo) {
		String lPattern = "(";
		boolean lWasNo = false;
        for (int i = 0; i < pInvNo.length(); i++) {
            if (Character.isDigit(pInvNo.charAt(i))) {
            	if (!lWasNo) {
            		String lChar = String.valueOf(lPattern.charAt(lPattern.length()-1));
                	lPattern += "[0-9]*";
                	lWasNo =true;
            	}
            }else if (Character.isLetter(pInvNo.charAt(i))) {
            	lPattern += pInvNo.charAt(i);
            	lWasNo =false;
            }else {
            	lPattern += "["+pInvNo.charAt(i)+"]"+"{1}";
            	lWasNo =false;
            }
        } 
        lPattern += ")";
        System.out.println(lPattern);
		return lPattern;
	}
	
	public void validateInvNumPattern(String pKey,String pData , String pInvoiceNumber) {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pData);
		if ((boolean) lMap.get(ISPATTERN)) {
			 Pattern lRegexPattern = Pattern.compile((String)lMap.get(PATTERN));
			 if (!lRegexPattern.matcher(pInvoiceNumber).matches()) {
				 try (Connection lConnection = DBHelper.getInstance().getConnection();){
					 NotiAlrtBean lAlrtBean = new NotiAlrtBean();
					 lAlrtBean.setType(NotiAlrtBean.Type.Instrument_Invoice);
					 lAlrtBean.setKey(pKey);
					 lAlrtBean.setAlertDesc("Invoice No : " +pInvoiceNumber +" does not match the previous pattern.");
					 lAlrtBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
					 notiAlrtDAO.insert(lConnection, lAlrtBean);
				 }catch (Exception e) {
				 }
			 }
		}
	}
	
	public void validateAmountPattern(String pKey,String pData , BigDecimal pInvAmount) {
		 JsonSlurper lJsonSlurper = new JsonSlurper();
		 String lPatternPlace = null;
		 Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pData);
		 int lMaxOccurances = Integer.parseInt(configMap.get(MAXAMTOCCURRENCE).toString());
		 int lUnits = Integer.parseInt(lMap.get(UNITS).toString());
		 int lTens = Integer.parseInt(lMap.get(TENS).toString());
		 int lHundreds = Integer.parseInt(lMap.get(HUNDREDS).toString());
		 int lThousands = Integer.parseInt(lMap.get(THOUSANDS).toString());
		 if (lMaxOccurances<=lUnits) {
			 lPatternPlace = UNITS;
		 }
		 if (lUnits<=lTens && lMaxOccurances<=lTens) {
			 lPatternPlace = TENS;
		 }
		 if (lTens<=lHundreds && lMaxOccurances<=lHundreds) {
			 lPatternPlace = HUNDREDS;
		 }
		 if (lHundreds<=lThousands && lMaxOccurances<=lThousands) {
			 lPatternPlace = THOUSANDS;
		 }
		 boolean lGenerateAlrt ;
		 if (lPatternPlace!=null) {
			 int lAmt = pInvAmount.intValue();
			 if (THOUSANDS.equals(lPatternPlace) && lAmt>=1000) {
				 lGenerateAlrt = !isRounded(new BigDecimal(lAmt/1000)); 
			 }else if (HUNDREDS.equals(lPatternPlace) && lAmt>=100) {
				 lGenerateAlrt = !isRounded(new BigDecimal(lAmt/1000));
			 }else if (TENS.equals(lPatternPlace) && lAmt>=10) {
				 lGenerateAlrt = !isRounded(new BigDecimal(lAmt/1000));
			 }else if (UNITS.equals(lPatternPlace) && lAmt>=1) {
				 lGenerateAlrt = !isRounded(new BigDecimal(lAmt/1000));
			 }
			 try (Connection lConnection = DBHelper.getInstance().getConnection();){
				 NotiAlrtBean lAlrtBean = new NotiAlrtBean();
				 lAlrtBean.setType(NotiAlrtBean.Type.Instrument_Amount);
				 lAlrtBean.setKey(pKey);
				 lAlrtBean.setAlertDesc("Invoice Amt : " +pInvAmount +" does not match the previous pattern.");
				 lAlrtBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
				 notiAlrtDAO.insert(lConnection, lAlrtBean);
			 }catch (Exception e) {
			 }
		 }
	}
	
	public void validatePoNumPattern(String pKey,String pData , String pPoNumber) {
		JsonSlurper lJsonSlurper = new JsonSlurper();
		Map<String,Object> lMap = (Map<String, Object>) lJsonSlurper.parseText(pData);
		if ((boolean) lMap.get(ISPOPATTERN)) {
			 Pattern lRegexPattern = Pattern.compile((String)lMap.get(POPATTERN));
			 if (!lRegexPattern.matcher(pPoNumber).matches()) {
				 try (Connection lConnection = DBHelper.getInstance().getConnection();){
					 NotiAlrtBean lAlrtBean = new NotiAlrtBean();
					 lAlrtBean.setType(NotiAlrtBean.Type.Instrument_PO_Number);
					 lAlrtBean.setKey(pKey);
					 lAlrtBean.setAlertDesc("PO No : " +pPoNumber +" does not match the previous pattern.");
					 lAlrtBean.setRecordCreateTime(new Timestamp(System.currentTimeMillis()));
					 notiAlrtDAO.insert(lConnection, lAlrtBean);
				 }catch (Exception e) {
				 }
			 }
		}
	}
	
	public boolean isRounded(BigDecimal pNum) {
		try {
			pNum.intValueExact();
			return true;
		}catch (Exception e) {
			return false;
		}
	}
	
    public static void main(String args[]) 
    { 
         String lPattern = getPattern("IE/0/7/1");
        Pattern pt = Pattern.compile(lPattern);
        if (pt.matcher("IE/071").matches()) System.out.println("1 : true");
        if (pt.matcher("IE/0/7/1").matches()) System.out.println("2 : true");
        if (pt.matcher("IIE/000/00700/00001").matches()) System.out.println("3 : true");
        if (pt.matcher("IE/000/00700/00001/").matches()) System.out.println("4 : true");
        if (pt.matcher("IE/000/00700/00001").matches()) System.out.println("5 : true");
        if (pt.matcher("/000/00700/00001").matches()) System.out.println("6 : true");
    }

}
