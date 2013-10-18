package edu.gvsu.bbmobile.compare;

import java.util.Comparator;
import java.util.Map;


public class CompareContent implements Comparator<Map> {
	
	private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();
    
    @Override
	public int compare(Map arg0, Map arg1) {
		if(arg0.get("crs_title").equals(arg1.get("crs_title")) && arg0.get("crs_id").equals(arg1.get("crs_id"))){
			String strP0 = (String) arg0.get("cnt_pos");
			String strP1 = (String) arg1.get("cnt_pos");
			if(strP0 != null && strP1 != null){
				Integer iarg0 = Integer.valueOf(strP0);
				Integer iarg1 = Integer.valueOf(strP1);
				return iarg0.compareTo(iarg1);
			}
			else if(strP0 == null && strP1 != null){
				return 1;
			}
			else if(strP0 != null && strP1 == null){
				return -1;
			}
			else{
				return 0;
			}
		}
		else if(arg0.get("crs_title").equals(arg1.get("crs_title")) && !(arg0.get("crs_id").equals(arg1.get("crs_id")))){
			return ((String) arg0.get("crs_id")).compareTo((String) arg1.get("crs_id"));
		}
		else{
			return ((String) arg0.get("crs_title")).compareTo((String) arg1.get("crs_title"));
		}
		
	}
}