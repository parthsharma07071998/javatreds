package com.xlx.treds.stats;

import com.xlx.treds.stats.bean.StatsCacheBean.Type;

public interface IStatsCacheGenerator  {

	public void generate(String pKey);	
	public Object getValue(Type pType, String pKey);
	public void generateAlert(Object pObject);

}
