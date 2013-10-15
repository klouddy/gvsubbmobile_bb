package edu.gvsu.bbmobile.compare;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class CompareGradeableSoonest implements Comparator<Map> {
    
	@Override
	public int compare(Map arg0, Map arg1) {

		// both have due date
		if(arg0.get("cnt_comp_due") != null && arg1.get("cnt_comp_due") != null){
			Date date0 = (Date) arg0.get("due_date");
			Date date1 = (Date) arg1.get("due_date");
			return date1.compareTo(date0);
		}
		// arg0 has due date and arg1 does not
		else if(arg0.get("cnt_comp_due") != null && arg1.get("cnt_comp_due") == null){
			return -1;
		}
		// arg1 good arg0 no due date
		else if(arg0.get("cnt_comp_due") == null && arg1.get("cnt_comp_due") != null){
			return 1;
		}
		// both are null return most recent item
		else{
			Date date0 = (Date) arg0.get("cnt_recent_date");
			Date date1 = (Date) arg1.get("cnt_recent_date");
			return date1.compareTo(date0);
		}
	}
}