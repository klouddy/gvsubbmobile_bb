package edu.gvsu.bbmobile.compare;

import java.util.Comparator;
import java.util.Map;


public class CompareAnn implements Comparator<Map> {
    
	@Override
	public int compare(Map arg0, Map arg1) {
		if(arg0.get("crs_name").equals(arg1.get("crs_name")) && arg0.get("crs_id").equals(arg1.get("crs_id"))){
			Integer iarg0 = Integer.valueOf((String) arg0.get("pos"));
			Integer iarg1 = Integer.valueOf((String) arg1.get("pos"));
			return iarg0.compareTo(iarg1);
		}
		else if(arg0.get("crs_name").equals(arg1.get("crs_name")) && !(arg0.get("crs_id").equals(arg1.get("crs_id")))){
			return ((String) arg0.get("crs_id")).compareTo((String) arg1.get("crs_id"));
		}
		else{
			return ((String) arg0.get("crs_name")).compareTo((String) arg1.get("crs_name"));
		}
	}
}