package edu.gvsu.bbmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.course.CourseToolUtil;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.user.UserDbLoader;

public class BbEmail {
	
	private CourseToolUtil toolUtil;
	private CourseDbLoader cLoader;
	private CourseMembershipDbLoader cmLoader;
	private UserDbLoader uLoader;
	
	private static final String TAG = "BB MOBILE Email Class";
	
	private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();
    
	
	public BbEmail(){
		
		try {
			cLoader = CourseDbLoader.Default.getInstance();
			cmLoader = CourseMembershipDbLoader.Default.getInstance();
			uLoader = UserDbLoader.Default.getInstance();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	public boolean isEmailToolAvailable(String crsId, String strUsername){
		
		try {
			Course crs = cLoader.loadById(Id.generateId(Course.DATA_TYPE, crsId));
			
		} catch (KeyNotFoundException e) {
			// TODO Auto-generated catch block
			log.logError(TAG + "Error isEmailToolAvailable: " + e.getFullMessageTrace());
			e.printStackTrace();
		} catch (PersistenceException e) {
			// TODO Auto-generated catch block
			log.logError(TAG + "Error in isEmailToolAvailable: " + e.getFullMessageTrace());
			e.printStackTrace();
		}
		
		
		return false;
	}
	*/
	
	public List<Map> getAvailableEmailCoursesForUser(String strUsername){
    	List<Map> ret = new ArrayList<Map>();
    	
    	try {
			User currUser = uLoader.loadByUserName(strUsername);
			List<CourseMembership> lcm = cmLoader.loadByUserId(currUser.getId());
			for(CourseMembership cm : lcm ){
				Course crs = cLoader.loadById(cm.getCourseId());
				
				
				if(crs != null && crs.getIsAvailable() && toolUtil.isToolAvailableForCourseUser("course_email", cm)){
					
					Map mapCrs = new HashMap();
					mapCrs.put("crs_id", crs.getId().toExternalString());
					mapCrs.put("crs_batch_uid", crs.getBatchUid());
					mapCrs.put("crs_name", crs.getTitle());
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

}
