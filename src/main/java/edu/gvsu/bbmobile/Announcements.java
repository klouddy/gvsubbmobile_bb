package edu.gvsu.bbmobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.gvsu.bbmobile.compare.CompareAnnRecent;
import blackboard.data.announcement.Announcement;
import blackboard.data.course.Course;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.announcement.AnnouncementDbLoader;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.user.UserDbLoader;

public class Announcements {
	
	private AnnouncementDbLoader annLoader;
	private UserDbLoader uLoader;
	private CourseDbLoader cLoader;
	
	private static final String TAG = "BB MOBILE ANNOUNCMENTS";
	
	private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();

	
	public List<Map> LoadAnnByCrsId(String strCrsId, String strUserName){
        try{annLoader = AnnouncementDbLoader.Default.getInstance();}catch(Exception ex){ex.printStackTrace();}
        try{uLoader = UserDbLoader.Default.getInstance();}catch(Exception ex){ex.printStackTrace();}
        
        User currUser = new User();
        try{currUser = uLoader.loadByUserName(strUserName);}catch(Exception ex){ex.printStackTrace();}
        List lstAnn = null;
        try{
            lstAnn = annLoader.loadAvailableByCourseIdAndUserId(Id.generateId(Course.DATA_TYPE, strCrsId), currUser.getId(), -1, true);
        }catch (Exception ex){ex.printStackTrace();}

        List<Map> theReturn = new ArrayList<Map>();
        int count = 0;
        for(Iterator itr = lstAnn.iterator(); itr.hasNext();){
            Announcement annCurr = (Announcement) itr.next();
            Map currAnn = new HashMap();
            currAnn.put("label", annCurr.getTitle());
            currAnn.put("desc", annCurr.getBody().getText());
            currAnn.put("id", annCurr.getId().toExternalString());
            currAnn.put("type", "501");
            theReturn.add(currAnn);
            count++;
        }

        return theReturn;
    }
	
	public Map LoadAnnById(String strId, String strUsername){
		
		Map theReturn = new HashMap();
		try{
			annLoader = AnnouncementDbLoader.Default.getInstance();
			cLoader = CourseDbLoader.Default.getInstance();
			uLoader = UserDbLoader.Default.getInstance();
			User usr = uLoader.loadByUserName(strUsername);
			List<Announcement> availableAnn = annLoader.loadAvailableByUserId(usr.getId());
			
			Id annId = Id.generateId(Announcement.DATA_TYPE, strId);
			for(Iterator i = availableAnn.iterator(); i.hasNext();){
				Announcement ann = (Announcement) i.next();
				log.logError(TAG + " announcement: " + ann.getId().toExternalString());
				if ( ann.getId().toExternalString().equals(strId)){
					Map mAnn = new HashMap();
					mAnn.put("id", ann.getId().toExternalString());
					mAnn.put("label", ann.getTitle());
					mAnn.put("desc", ann.getBody().getFormattedText());
					mAnn.put("type", BbObjectType.ANNOUNCEMENT);
					mAnn.put("pos", String.valueOf(ann.getPosition()));
					mAnn.put("crs_id", ann.getCourseId().toExternalString());
					Course crs = new Course();
					try{
		            	crs = cLoader.loadById(ann.getCourseId());
		            	mAnn.put("crs_name", crs.getTitle());
		            }catch (Exception ex){
		            	//TODO: handle exception
		            	mAnn.put("crs_name", "");
		            	ex.printStackTrace();
		            	log.logError(TAG + " error loading course for announcement ", ex);
		            }
					theReturn = mAnn;
				}
				
			}
			return theReturn;
			
		}catch (Exception ex){
			//TODO: handle exception
			log.logError(TAG + " error in setting up announcement: ", ex);
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public List<Map> loadRecentAnnByUserName(String strUsername, Integer max){
		List<Map> theRet = new ArrayList<Map>();
		try{
			annLoader = AnnouncementDbLoader.Default.getInstance();
			uLoader = UserDbLoader.Default.getInstance();
			cLoader = CourseDbLoader.Default.getInstance();
			User usr = uLoader.loadByUserName(strUsername);
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm a");
			
			List<Announcement> anns = annLoader.loadAvailableByUserId(usr.getId());
			Integer totalCount = anns.size();
			Integer c = 1;
			for(Announcement a : anns){
				if(c > max){ break; }
				Calendar recent = getRecentDate(a);
				Course crsCurr = new Course();
	            Map currAnn = new HashMap();
	            currAnn.put("label", a.getTitle());
	            currAnn.put("id", a.getId().toExternalString());
	            //get Course Name
	            String strCrsName = "";
	            try{
	            	crsCurr = cLoader.loadById(a.getCourseId());
	            }catch (Exception ex){
	            	//TODO: handle exception
	            	ex.printStackTrace();
	            	log.logError(TAG + " error loading course for announcement ", ex);
	            }
	            currAnn.put("crs_name", crsCurr.getTitle());
	            currAnn.put("crs_id", a.getCourseId().toExternalString());
	            currAnn.put("pos", (String.valueOf(a.getPosition())));
	            currAnn.put("type", BbObjectType.ANNOUNCEMENT);
	            currAnn.put("post_date", sdf.format(recent.getTime()));
	            currAnn.put("post_cal", recent);
	            theRet.add(currAnn);
	            c++;
			}
			
			Map countMap = new HashMap();
			countMap.put("total", totalCount);
			countMap.put("type", BbObjectType.LIST_COUNT);
			theRet.add(countMap);
			
		}catch(Exception e){
			//TODO: log error
			e.printStackTrace();
		}
		
		Collections.sort(theRet, new CompareAnnRecent());
		
		if(max != null){
			if(max > theRet.size()){
				max = theRet.size();
			}
			
			List<Map> tmpList = new ArrayList<Map>();
			for(int i = 0; i < max; i++){
				theRet.get(i).remove("post_cal");
				tmpList.add(theRet.get(i));
			}
			theRet = tmpList;
		}
		
		return theRet;
	}
	
	/**
	 * This will load recent announcements up to limit for user and corse only
	 * @param strUsername
	 * @param max
	 * @param crsId
	 * @return
	 */
	public List<Map> loadRecentAnnByUserName(String strUsername, Integer max, String crsId){
		List<Map> theRet = new ArrayList<Map>();
		try{
			annLoader = AnnouncementDbLoader.Default.getInstance();
			uLoader = UserDbLoader.Default.getInstance();
			cLoader = CourseDbLoader.Default.getInstance();
			User usr = uLoader.loadByUserName(strUsername);
			
			int totalCount = 0;
			
			SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy hh:mm a");
			
			List<Announcement> anns = annLoader.loadAvailableByUserId(usr.getId());
			totalCount = anns.size();
			Integer c = 1;
			for(Announcement a : anns){
				Calendar recent = getRecentDate(a);
				Course crsCurr = new Course();
	            Map currAnn = new HashMap();
	            currAnn.put("label", a.getTitle());
	            currAnn.put("id", a.getId().toExternalString());
	            //get Course Name
	            String strCrsName = "";
	            try{
	            	crsCurr = cLoader.loadById(a.getCourseId());
	            }catch (Exception ex){
	            	//TODO: handle exception
	            	ex.printStackTrace();
	            	log.logError(TAG + " error loading course for announcement ", ex);
	            }
	            currAnn.put("crs_name", crsCurr.getTitle());
	            currAnn.put("crs_id", a.getCourseId().toExternalString());
	            currAnn.put("pos", (String.valueOf(a.getPosition())));
	            currAnn.put("type", BbObjectType.ANNOUNCEMENT);
	            currAnn.put("post_date", sdf.format(recent.getTime()));
	            currAnn.put("post_cal", recent);
	            if(a.getCourseId().toExternalString().equals(crsId)){
	            	theRet.add(currAnn);
		            c++;
	            }
	            
			}
			
			Map countMap = new HashMap();
			countMap.put("total", totalCount);
			countMap.put("type", BbObjectType.LIST_COUNT);
			theRet.add(countMap);
			
			
		}catch(Exception e){
			//TODO: log error
			e.printStackTrace();
		}
		
		Collections.sort(theRet, new CompareAnnRecent());
		
		if(max != null){
			if(max > theRet.size()){
				max = theRet.size();
			}
			
			List<Map> tmpList = new ArrayList<Map>();
			for(int i = 0; i < max; i++){
				theRet.get(i).remove("post_cal");
				tmpList.add(theRet.get(i));
			}
			theRet = tmpList;
		}
		
		return theRet;
	}
	
	/**
	 * For Announcement item get what should be considered the most recent date.
	 * @param ann
	 * @return calendar object
	 */
	
	private Calendar getRecentDate(Announcement ann){
		if(ann.getIsPermanent()){
			return ann.getModifiedDate();
		}
		else{
			if(ann.getModifiedDate().after(ann.getRestrictionStartDate()) 
					&& ann.getModifiedDate().before(ann.getRestrictionEndDate())){
				return ann.getModifiedDate();
			}
			else{
				return ann.getRestrictionStartDate();
			}
		}
	}
	
	
	
	public List<Map> loadAnnByUserName(String strUserName, Integer intMax){
    	try{annLoader = AnnouncementDbLoader.Default.getInstance();}catch(Exception ex){ex.printStackTrace();}
    	try{	cLoader = CourseDbLoader.Default.getInstance();}
    	catch(Exception ex){ex.printStackTrace();}
        try{uLoader = UserDbLoader.Default.getInstance();}catch(Exception ex){ex.printStackTrace();}
        
        if(intMax == null){
	    	intMax = 20;
	    }
        
        User currUser = new User();
        try{currUser = uLoader.loadByUserName(strUserName);}catch(Exception ex){ex.printStackTrace();}
        List lstAnn = null;
        try{
        	lstAnn = annLoader.loadAvailableByUserId(currUser.getId());
        }catch (Exception ex){ex.printStackTrace();}

        List<Map> theReturn = new ArrayList<Map>();
        int count = 0;
        for(Iterator itr = lstAnn.iterator(); itr.hasNext();){
            Announcement annCurr = (Announcement) itr.next();
            Course crsCurr = new Course();
            Map currAnn = new HashMap();
            currAnn.put("label", org.json.simple.JSONObject.escape(annCurr.getTitle()));
            currAnn.put("desc", org.json.simple.JSONObject.escape(annCurr.getBody().getText()));
            currAnn.put("id", annCurr.getId().toExternalString());
            //get Course Name
            String strCrsName = "";
            try{
            	crsCurr = cLoader.loadById(annCurr.getCourseId());
            }catch (Exception ex){
            	//TODO: handle exception
            	ex.printStackTrace();
            	log.logError(TAG + " error loading course for announcement ", ex);
            }
            currAnn.put("crs_name", crsCurr.getTitle());
            currAnn.put("crs_id", annCurr.getCourseId().toExternalString());
            currAnn.put("pos", (String.valueOf(annCurr.getPosition())));
            currAnn.put("type", "501");
            theReturn.add(currAnn);
            
            count++;
            if(count > intMax){ break; }
        }

        return theReturn;
    }


}
