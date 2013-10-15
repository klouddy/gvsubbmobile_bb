package edu.gvsu.bbmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.course.CourseMembership.Role;
import blackboard.data.navigation.CourseToc;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseCourseDbLoader;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.gradebook.LineitemDbLoader;
import blackboard.persist.gradebook.ScoreDbLoader;
import blackboard.persist.navigation.CourseTocDbLoader;
import blackboard.persist.user.UserDbLoader;

public class Courses {
	
	private CourseMembershipDbLoader cmLoader;
    private UserDbLoader uLoader;
    private CourseDbLoader cLoader;
    private CourseTocDbLoader tocLoader;
    
    private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();
    private String TAG = "BB COURSES";

    public Courses(){
    	try {
			cmLoader = CourseMembershipDbLoader.Default.getInstance();
			uLoader = UserDbLoader.Default.getInstance();
	    	cLoader = CourseDbLoader.Default.getInstance();
	    	tocLoader = CourseTocDbLoader.Default.getInstance();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public List<Map> getAvailableCoursesForUser(String strUsername){
    	List<Map> ret = new ArrayList<Map>();
    	
    	try {
			User currUser = uLoader.loadByUserName(strUsername);
			List<CourseMembership> lcm = cmLoader.loadByUserId(currUser.getId());
			for(CourseMembership cm : lcm ){
				Course crs = cLoader.loadById(cm.getCourseId());
				if(crs != null && crs.getIsAvailable()){
					
					Map mapCrs = new HashMap();
					mapCrs.put("crs_id", crs.getId().toExternalString());
					mapCrs.put("crs_batch_uid", crs.getBatchUid());
					mapCrs.put("crs_name", crs.getTitle());
					mapCrs.put("type", BbObjectType.COURSE);
					ret.add(mapCrs);
				}
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			log.logError(TAG + "Error with getAvailableCorusesForUser: " + e.getFullMessageTrace() );
			e.printStackTrace();
		}
    	
    	
    	
    	return ret;
    }
    
    public List<Course> getAvailableCoursesForUserList(String strUsername){
    	List<Course> ret = new ArrayList<Course>();
    	
    	try {
			User currUser = uLoader.loadByUserName(strUsername);
			List<CourseMembership> lcm = cmLoader.loadByUserId(currUser.getId());
			for(CourseMembership cm : lcm ){
				Course crs = cLoader.loadById(cm.getCourseId());
				if(crs != null && crs.getIsAvailable()){
					
					ret.add(crs);
				}
			}
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			log.logError(TAG + "Error with getAvailableCorusesForUser: " + e.getFullMessageTrace() );
			e.printStackTrace();
		}
    	
    	
    	
    	return ret;
    }
    
    public List<Map> getAvailableTocForCrs(String strCrsId, String strUsername){
    	
    	
    	List<CourseToc> tocs = new ArrayList<CourseToc>();
		try {
			User usr = uLoader.loadByUserName(strUsername);
			CourseMembership cm = cmLoader.loadByCourseAndUserId(Id.generateId(Course.DATA_TYPE, strCrsId), usr.getId());
			tocs = tocLoader.loadByCourseId(Id.generateId(Course.DATA_TYPE, strCrsId));
			if(cm.getRole().equals(Role.STUDENT)){
				for(CourseToc toc : tocs){
					if(!(toc.getIsEnabled()) || !(toc.getContentAvailable()) || toc.getContentEmpty()){
						tocs.remove(toc);
					}
				}
			}
			
			for(CourseToc toc : tocs){
				Map currToc = new HashMap();
				currToc.put("id", toc.getId().toExternalString());
				currToc.put("label", toc.getLabel());
				currToc.put("type", BbObjectType.TABLE_OF_CONTENTS);
				
			}
			
		} catch (KeyNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    
    
}
