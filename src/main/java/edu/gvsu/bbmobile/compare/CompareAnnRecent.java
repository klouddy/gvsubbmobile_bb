package edu.gvsu.bbmobile.compare;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;


public class CompareAnnRecent implements Comparator<Map> {
	
	private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();

    
	@Override
	public int compare(Map arg0, Map arg1) {
		
		Calendar cal0 = (Calendar) arg0.get("post_cal");
		Calendar cal1 = (Calendar) arg1.get("post_cal");
		if(cal0 != null && cal1 != null){
			return cal1.compareTo(cal0);
		}
		else if(cal0 == null && cal1 != null){
			//log.logError("Map 0 null post_cal: " + arg0 + "\n\n");
			return -1;
		}
		else if(cal0 != null && cal1 == null){
			//log.logError("Map 1 null post_cal: " + arg1 + "\n\n");
			return 1;
		}
		else{
			//log.logError("Map 0 & Map 1 null post_cal: " + arg0 + "\n\n" + arg1 + "\n\n");
			return 0;
		}
		
	}
}