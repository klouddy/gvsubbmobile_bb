package edu.gvsu.bbmobile.compare;

import java.util.Comparator;
import java.util.Map;


public class CompareContent implements Comparator<Map> {
	
	private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();
    
    @Override
	public int compare(Map arg0, Map arg1) {
		if(arg0.get("crs_title").equals(arg1.get("crs_title")) && arg0.get("crs_id").equals(arg1.get("crs_id"))){
			Integer iarg0 = Integer.valueOf((String) arg0.get("cnt_pos"));
			Integer iarg1 = Integer.valueOf((String) arg1.get("cnt_pos"));
			return iarg0.compareTo(iarg1);
		}
		else if(arg0.get("crs_title").equals(arg1.get("crs_title")) && !(arg0.get("crs_id").equals(arg1.get("crs_id")))){
			return ((String) arg0.get("crs_id")).compareTo((String) arg1.get("crs_id"));
		}
		else{
			return ((String) arg0.get("crs_title")).compareTo((String) arg1.get("crs_title"));
		}
		
	}
}