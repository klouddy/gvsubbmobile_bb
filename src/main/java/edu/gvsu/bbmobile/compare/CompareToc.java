package edu.gvsu.bbmobile.compare;

import java.util.Comparator;
import java.util.Map;


public class CompareToc implements Comparator<Map> {
    
	@Override
	public int compare(Map arg0, Map arg1) {
		return ((String) arg0.get("pos")).compareTo((String ) arg1.get("pos"));
	}
}