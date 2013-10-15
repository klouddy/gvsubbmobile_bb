package edu.gvsu.bbmobile.compare;

import java.util.Comparator;
import java.util.Date;
import java.util.Map;


public class CompareContentRecent implements Comparator<Map> {
    
	@Override
	public int compare(Map arg0, Map arg1) {
		
		Date date0 = (Date) arg0.get("cnt_recent_date");
		Date date1 = (Date) arg1.get("cnt_recent_date");
		if(date0 != null && date1 != null){
			return date0.compareTo(date1) * -1;
		}
		else if(date0 != null && date1 == null){
			return -1;
		}
		else if(date0 == null && date1 != null){
			return 1;
		}
		else{
			return 0;
		}
	}
}