package edu.gvsu.bbmobile;

import java.util.ArrayList;
import java.util.List;

import edu.gvsu.bbmobile.BbObj;

public class BbObjectType {
	
	private List<BbObj> objs = new ArrayList<BbObj>();
	

	public static BbObj CNT_LEARNING_MODULE = new BbObj(101, "resource/x-bb-lesson");
	public static BbObj CNT_ITEM = new BbObj(102, "resource/x-bb-document");
	public static BbObj CNT_ASSIGNMENT = new BbObj(103, "resource/x-bb-assignment");
	
	private void addToList(){
		this.objs = new ArrayList<BbObj>();
		this.objs.add(CNT_LEARNING_MODULE);
		this.objs.add(CNT_ITEM);
		this.objs.add(CNT_ASSIGNMENT);
	}
	
	public int getIdForHanlde(String strHandle){
		
		addToList();
		for(BbObj o : this.objs){
			if(o.bbHandle.equals(strHandle)){
				return o.id;
			}
		}
		return -1;
	}
	
	public String getResourceForId(int intId){
		addToList();
		for(BbObj o : this.objs){
			if(o.id == intId){
				return o.bbHandle;
			}
		}
		return null;
	}
	
	
	
}
