package edu.gvsu.bbmobile.compare;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Map;


public class CompareGradesRecent implements Comparator<Map> {
    
	@Override
	public int compare(Map arg0, Map arg1) {
		Calendar cal0 = (Calendar) arg0.get("post_cal");
		Calendar cal1 = (Calendar) arg1.get("post_cal");
		if(cal0 != null && cal1 != null){
			return cal1.compareTo(cal0);
		}
		else if(cal0 == null && cal1 != null){
			return -1;
		}
		else if(cal0 != null && cal1 == null){
			return 1;
		}
		else{
			return 0;
		}
	}
}