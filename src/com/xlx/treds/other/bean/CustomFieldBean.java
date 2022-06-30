package com.xlx.treds.other.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.xlx.commonn.CommonAppConstants;
import com.xlx.commonn.CommonAppConstants.Yes;
import com.xlx.commonn.IKeyValEnumInterface;
import com.xlx.treds.AppConstants.FieldType;
import com.xlx.treds.TredsHelper;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;

public class CustomFieldBean {
	
    public enum Fields implements IKeyValEnumInterface<String>{
        ONE("1","ONE"),TWO("2","TWO"),THREE("3","THREE"),FOUR("4","FOUR");
        
        private final String code;
        private final String desc;
        private Fields(String pCode, String pDesc) {
            code = pCode;
            desc = pDesc;
        }
        public String getCode() {
            return code;
        }
        public String toString() {
        	return desc;
        }
    }

    private Long id;
    private String code;
    private Fields fields;
    private String field1label;
    private String field1Name;
    private FieldType field1Type;
    private Yes field1Mandatory;
    private String field2label;
    private String field2Name;
    private FieldType field2Type;
    private Yes field2Mandatory;
    private String field3label;
    private String field3Name;
    private FieldType field3Type;
    private Yes field3Mandatory;
    private String field4label;
    private String field4Name;
    private FieldType field4Type;
    private Yes field4Mandatory;

    public Long getId() {
        return id;
    }

    public void setId(Long pId) {
        id = pId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String pCode) {
        code = pCode;
    }

    public String getField1label() {
        return field1label;
    }

    public void setField1label(String pfield1label) {
        field1label = pfield1label;
    }

    public String getField1Name() {
        return field1Name;
    }

    public void setField1Name(String pField1Name) {
        field1Name = pField1Name;
    }

    public FieldType getField1Type() {
        return field1Type;
    }

    public void setField1Type(FieldType pField1Type) {
        field1Type = pField1Type;
    }

    public Yes getField1Mandatory() {
        return field1Mandatory;
    }

    public void setField1Mandatory(Yes pField1Mandatory) {
        field1Mandatory = pField1Mandatory;
    }

    public String getField2label() {
        return field2label;
    }

    public void setField2label(String pfield2label) {
        field2label = pfield2label;
    }

    public String getField2Name() {
        return field2Name;
    }

    public void setField2Name(String pField2Name) {
        field2Name = pField2Name;
    }

    public FieldType getField2Type() {
        return field2Type;
    }

    public void setField2Type(FieldType pField2Type) {
        field2Type = pField2Type;
    }

    public Yes getField2Mandatory() {
        return field2Mandatory;
    }

    public void setField2Mandatory(Yes pField2Mandatory) {
        field2Mandatory = pField2Mandatory;
    }

    public String getField3label() {
        return field3label;
    }

    public void setField3label(String pfield3label) {
        field3label = pfield3label;
    }

    public String getField3Name() {
        return field3Name;
    }

    public void setField3Name(String pField3Name) {
        field3Name = pField3Name;
    }

    public FieldType getField3Type() {
        return field3Type;
    }

    public void setField3Type(FieldType pField3Type) {
        field3Type = pField3Type;
    }

    public Yes getField3Mandatory() {
        return field3Mandatory;
    }

    public void setField3Mandatory(Yes pField3Mandatory) {
        field3Mandatory = pField3Mandatory;
    }

    public String getField4label() {
        return field4label;
    }

    public void setField4label(String pfield4label) {
        field4label = pfield4label;
    }

    public String getField4Name() {
        return field4Name;
    }

    public void setField4Name(String pField4Name) {
        field4Name = pField4Name;
    }

    public FieldType getField4Type() {
        return field4Type;
    }

    public void setField4Type(FieldType pField4Type) {
        field4Type = pField4Type;
    }

    public Yes getField4Mandatory() {
        return field4Mandatory;
    }

    public void setField4Mandatory(Yes pField4Mandatory) {
        field4Mandatory = pField4Mandatory;
    }
    
    public String getSettings() {
    	Map<String,Object> lMap = new HashMap<String, Object>();
    	if (fields !=null) {
    		lMap.put("fields", fields.getCode());
    	}
		if(field1label!=null) {
			lMap.put("field1label", field1label);
		}
		if(field1Name!=null) {
			lMap.put("field1Name", field1Name);
		}
		if(field1Type!=null) {
			lMap.put("field1Type", field1Type.getCode());
		}
		if(field1Mandatory!=null) {
			lMap.put("field1Mandatory", field1Mandatory.getCode());
		}
		if(field2label!=null) {
			lMap.put("field2label", field2label);
		}
		if(field2Name!=null) {
			lMap.put("field2Name", field2Name);
		}
		if(field2Type!=null) {
			lMap.put("field2Type", field2Type.getCode());
		}
		if(field2Mandatory!=null) {
			lMap.put("field2Mandatory", field2Mandatory.getCode());
		}
		if(field3label!=null) {
			lMap.put("field3label", field3label);
		}
		if(field3Name!=null) {
			lMap.put("field3Name", field3Name);
		}
		if(field3Type!=null) {
			lMap.put("field3Type", field3Type.getCode());
		}
		if(field3Mandatory!=null) {
			lMap.put("field3Mandatory", field3Mandatory.getCode());
		}
		if(field4label!=null) {
			lMap.put("field4label", field4label);
		}
		if(field4Name!=null) {
			lMap.put("field4Name", field4Name);
		}
		if(field4Type!=null) {
			lMap.put("field4Type", field4Type.getCode());
		}
		if(field4Mandatory!=null) {
			lMap.put("field4Mandatory", field4Mandatory.getCode());
		}
		if(lMap.size()>0) {
	        return  new JsonBuilder(lMap).toString();
		}
		return null;
    }

    public void setSettings(String pSettings) {
    	field1label= null;
    	field2label = null;
    	field3label = null;
    	field4label = null;
    	field1Name= null;
    	field2Name = null;
    	field3Name = null;
    	field4Name = null;
    	field1Type= null;
    	field2Type = null;
    	field3Type = null;
    	field4Type = null;
    	field1Mandatory= null;
    	field2Mandatory = null;
    	field3Mandatory = null;
    	field4Mandatory = null;
    	fields = null;
    	if(StringUtils.isNotEmpty(pSettings)) {
    		Map<String,Object> lMap = (Map<String,Object>) new JsonSlurper().parseText(pSettings);
    		if(lMap.containsKey("fields") && lMap.get("fields")!=null) {
    			fields = (Fields) TredsHelper.getValue(CustomFieldBean.class,"fields",(String) lMap.get("fields"));
    		}
    		if(lMap.containsKey("field1label")) {
    			field1label = lMap.get("field1label").toString();
    		}
    		if(lMap.containsKey("field2label")) {
    			field2label = lMap.get("field2label").toString();
    		}
    		if(lMap.containsKey("field3label")) {
    			field3label = lMap.get("field3label").toString();
    		}
    		if(lMap.containsKey("field4label")) {
    			field4label = lMap.get("field4label").toString();
    		}
    		if(lMap.containsKey("field1Name")) {
    			field1Name = lMap.get("field1Name").toString();
    		}
    		if(lMap.containsKey("field2Name")) {
    			field2Name = lMap.get("field2Name").toString();
    		}
    		if(lMap.containsKey("field3Name")) {
    			field3Name = lMap.get("field3Name").toString();
    		}
    		if(lMap.containsKey("field4Name")) {
    			field4Name = lMap.get("field4Name").toString();
    		}
    		if(lMap.containsKey("field1Type") && lMap.get("field1Type")!=null) {
    			field1Type = (FieldType) TredsHelper.getValue(CustomFieldBean.class,"field1Type",(String) lMap.get("field1Type"));
    		}
    		if(lMap.containsKey("field2Type") && lMap.get("field2Type")!=null) {
    			field2Type = (FieldType) TredsHelper.getValue(CustomFieldBean.class,"field2Type",(String) lMap.get("field2Type"));
    		}
    		if(lMap.containsKey("field3Type") && lMap.get("field3Type")!=null ) {
    			field3Type = (FieldType) TredsHelper.getValue(CustomFieldBean.class,"field3Type",(String) lMap.get("field3Type"));
    		}
    		if(lMap.containsKey("field4Type") && lMap.get("field4Type")!=null) {
    			field4Type = (FieldType) TredsHelper.getValue(CustomFieldBean.class,"field4Type",(String) lMap.get("field4Type"));
    		}
    		if(lMap.containsKey("field1Mandatory") && lMap.get("field1Mandatory")!=null) {
    			field1Mandatory = (Yes) TredsHelper.getValue(CustomFieldBean.class,"field1Mandatory",(String) lMap.get("field1Mandatory"));
    		}
    		if(lMap.containsKey("field2Mandatory")  && lMap.get("field2Mandatory")!=null) {
    			field2Mandatory = (Yes) TredsHelper.getValue(CustomFieldBean.class,"field2Mandatory",(String) lMap.get("field2Mandatory"));
    		}
    		if(lMap.containsKey("field3Mandatory") && lMap.get("field3Mandatory")!=null) {
    			field3Mandatory = (Yes) TredsHelper.getValue(CustomFieldBean.class,"field3Mandatory",(String) lMap.get("field3Mandatory"));
    		}
    		if(lMap.containsKey("field4Mandatory")  && lMap.get("field4Mandatory")!=null) {
    			field4Mandatory = (Yes) TredsHelper.getValue(CustomFieldBean.class,"field4Mandatory",(String) lMap.get("field4Mandatory"));
    		}
    	}
    }
    
    public Map<String, Object> getConfig() {
    	Map<String,Object> lFinalMap = new HashMap<String,Object>();
    	ArrayList<Map<String,Object>> lList = new ArrayList<Map<String,Object>>();
    	Map<String,Object> lMap = null;
    	String[] lArr = {"field1", "field2", "field3", "field4"};
		for (String lField:lArr) {
    		lMap = new HashMap<String,Object>();
    		if (lField.equals("field1") && field1Name!=null && field1Type!=null) {
    			lMap.put("name",field1Name);
    			lMap.put("label",field1label);
    			lMap.put("dataType",field1Type.getCode());
    			if(FieldType.String.equals(field1Type)) {
    				lMap.put("maxLength", 500);
    			}else if (FieldType.Decimal.equals(field1Type)){
    				lMap.put("integerLength",10);
    				lMap.put("decimalLength",2);
    			}
    			if (field1Mandatory!=null && CommonAppConstants.Yes.Yes.equals(field1Mandatory)) {
    				lMap.put("notNull",true);
    			}
    			lList.add(lMap);
    		}else if (lField.equals("field2") && field2Name!=null && field2Type!=null ) {
    			lMap.put("name",field2Name);
    			lMap.put("label",field2label);
    			lMap.put("dataType",field2Type.getCode());
    			if(FieldType.String.equals(field2Type)) {
    				lMap.put("maxLength", 500);
    			}else if (FieldType.Decimal.equals(field2Type)){
    				lMap.put("integerLength",10);
    				lMap.put("decimalLength",2);
    			}
    			if (field2Mandatory!=null && CommonAppConstants.Yes.Yes.equals(field3Mandatory)) {
    				lMap.put("notNull",true);
    			}
    			lList.add(lMap);
    		}else if (lField.equals("field3") && field3Name!=null && field3Type!=null) {
    			lMap.put("name",field3Name);
    			lMap.put("label",field3label);
    			lMap.put("dataType",field3Type.getCode());
    			if(FieldType.String.equals(field3Type)) {
    				lMap.put("maxLength", 500);
    			}else if (FieldType.Decimal.equals(field3Type)){
    				lMap.put("integerLength",10);
    				lMap.put("decimalLength",2);
    			}
    			if (field3Mandatory!=null && CommonAppConstants.Yes.Yes.equals(field3Mandatory)) {
    				lMap.put("notNull",true);
    			}
    			lList.add(lMap);
    		}else if (lField.equals("field4")  && field4Name!=null && field4Type!=null) {
    			lMap.put("name",field4Name);
    			lMap.put("label",field4label);
    			lMap.put("dataType",field4Type.getCode());
    			if(FieldType.String.equals(field4Type)) {
    				lMap.put("maxLength", 500);
    			}else if (FieldType.Decimal.equals(field4Type)){
    				lMap.put("integerLength",10);
    				lMap.put("decimalLength",2);
    			}else if (FieldType.DATETIME.equals(field4Type)) {
    				lMap.put("format" , "dd-MM-yyyy HH:mm:ss:SS");
    			}
    			else if (FieldType.DATE.equals(field4Type)) {
    				lMap.put("format" , "dd-MM-yyyy");
    			}
    			if (field4Mandatory!=null && CommonAppConstants.Yes.Yes.equals(field4Mandatory)) {
    				lMap.put("notNull",true);
    			}
    			lList.add(lMap);
    		}
    	}
		if (!lList.isEmpty()) {
			lFinalMap.put("inputParams", lList);
			lFinalMap.put("cfId", id);
			return lFinalMap;
		}
		return null;
    }
    

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields pFields) {
        fields = pFields;
    }

}