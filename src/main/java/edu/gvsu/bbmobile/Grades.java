/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.gvsu.bbmobile;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import edu.gvsu.bbmobile.compare.CompareGrades;
import blackboard.data.gradebook.Lineitem;
import blackboard.persist.DataType;
import blackboard.persist.Id;
import blackboard.persist.gradebook.LineitemDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.data.course.Course;
import blackboard.data.gradebook.Score;
import blackboard.data.user.User;
import blackboard.persist.course.CourseCourseDbLoader;
import blackboard.persist.course.CourseDbLoader;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.data.course.CourseMembership;
import blackboard.persist.gradebook.ScoreDbLoader;

/**
 *
 * @author romeroj
 */
public class Grades {

    private LineitemDbLoader liLoader;
    private CourseMembershipDbLoader cmLoader;
    private UserDbLoader uLoader;
    private CourseDbLoader cLoader;
    private ScoreDbLoader scoreLoader;
    private CourseCourseDbLoader asLoader;

    private blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
    private blackboard.platform.log.Log log = logService.getDefaultLog();


    public Grades(){
    	

    }
    
    /**
     * Load all colums from a course grade center with scores (if there) for the user.
     * 
     * @param strCourseId
     * @param strUserName
     * @param intMax -- the max number of rows returned for display purposes.
     * @return
     */
    public Map[] LoadAllColsForCourseWithScores(String strCourseId, String strUserName, Integer intMax){
    	try { liLoader = LineitemDbLoader.Default.getInstance();
	    	cmLoader = CourseMembershipDbLoader.Default.getInstance();
	    	uLoader = UserDbLoader.Default.getInstance();
	    	scoreLoader = ScoreDbLoader.Default.getInstance();
	    	cLoader = CourseDbLoader.Default.getInstance();
	    } catch (Exception ex){ ex.printStackTrace(); }
	    
	    
	    
	    ArrayList<Lineitem> lis = new ArrayList();
	    Course crs = new Course();
	    User usr = new User();
	    // get all line items for passed crs id
	    try{
	    	lis = liLoader.loadByCourseId(Id.generateId(Course.DATA_TYPE, strCourseId));
	    	crs = cLoader.loadById(Id.generateId(Course.DATA_TYPE, strCourseId));
	    	usr = uLoader.loadByUserName(strUserName);
	    }catch(Exception ex){
	    	log.logError("BBMobile error loading lineitems: " + ex.getStackTrace().toString());
	    }

	    //Setup intMax -- this is the max rows to display for the course.
	    // if it is -1 then there is no max.
	    int arrSize = 0; 
	    if(intMax == -1){
	    	intMax = 100000;
	    	arrSize = lis.size();
	    }
	    else{
	    	if(lis.size() > intMax){
	    		arrSize = intMax + 1;
	    	}
	    	else{
	    		arrSize = lis.size();
	    	}
	    }
	    
	    // add all line items to the return map
	    Map[] theReturn = new HashMap[arrSize];
	    int c = 0;
	    for(Iterator lisItr = lis.iterator(); lisItr.hasNext();){
	    	Lineitem currLi = (Lineitem) lisItr.next();
	    	
	    	
	    	theReturn[c] = new HashMap();
	    	if(c < intMax){
		    	theReturn[c].put("id", currLi.getId().toExternalString());
		    	theReturn[c].put("label", currLi.getName());
		    	theReturn[c].put("crs_id", strCourseId);
		    	theReturn[c].put("crs_name", crs.getTitle());
		    	theReturn[c].put("points_poss", String.valueOf(currLi.getPointsPossible()));
		    	theReturn[c].put("pos", String.valueOf(currLi.getColumnOrder()));
		    	theReturn[c].put("type", "601");
		    	theReturn[c].put("col_type", currLi.getType());
		    	Score currScore = new Score();
		    	try{
		    		
		    		CourseMembership currCm = cmLoader.loadByCourseAndUserId(crs.getId(), usr.getId());
		    		
		    		currScore = scoreLoader.loadByCourseMembershipIdAndLineitemId(currCm.getId(), currLi.getId());
		    		if(currScore != null){
				    	theReturn[c].put("score_id", currScore.getId().toExternalString());
				    	theReturn[c].put("grade", currScore.getGrade());
			    	}
		    		else{
		    			theReturn[c].put("score_id", "NA");
		    			theReturn[c].put("grade", "-");
		    		}
		    		
		    	}catch(Exception ex){
		    		String strLog = "BBMobile erro in loading score: " + ex.getLocalizedMessage()+ " \n";
		    		StackTraceElement[] st = ex.getStackTrace();
		    		for( int i = 0; i < st.length; i++){
		    			strLog += "\t\t" + st[i].getClassName() + ":" + st[i].getFileName() + " Line: " + st[i].getLineNumber() + "\n";
		    			if (i > 10){ 
		    				strLog += "\n\t\t...";
		    				break; 
		    			}
		    		}
		    		log.logError(strLog);
		    	}
	    	}
	    	else{
	    		theReturn[c].put("id", crs.getId().toExternalString());
	    		theReturn[c].put("label", "See More");
	    		theReturn[c].put("type", "more_link");
	    		theReturn[c].put("crs_name", crs.getTitle());
	    		theReturn[c].put("pos", String.valueOf(c + 1));
	    		return theReturn;
	    	
	    	}
	    	c++;
	    }
	    
	    return theReturn;
    }
    
    /**
     * Get data for each score that a student has a grade for in the grade center
     * @param strUserName
     * @return
     */
    public List<Map> LoadGradesByUN(String strUserName){
    	try { liLoader = LineitemDbLoader.Default.getInstance();
	    	cmLoader = CourseMembershipDbLoader.Default.getInstance();
	    	uLoader = UserDbLoader.Default.getInstance();
	    	scoreLoader = ScoreDbLoader.Default.getInstance();
	    	cLoader = CourseDbLoader.Default.getInstance();
	    } catch (Exception ex){ ex.printStackTrace(); }
	    
	    ArrayList<Score> scores = new ArrayList(); // master score list for all scores for user
	    
	    try{
	    	// get list of course Memberships for user passed
	    	User currUser = uLoader.loadByUserName(strUserName);
		    List cms = cmLoader.loadByUserId(currUser.getId());
		    
		    //loop through list of course memberships
		    // for each mem load all scores 
		    // add score info to return
		    for (Iterator cmItr = cms.iterator(); cmItr.hasNext();){
		    	CourseMembership currCm = (CourseMembership) cmItr.next();
		    	
		    	List currScores = scoreLoader.loadByCourseMembershipId(currCm.getId());
		    	scores.addAll(currScores);
		    }
	    }catch (Exception ex){
	    	log.logError("BBMobile Error: " + ex.getStackTrace().toString());
	    }
	    
	    
	    // loop through all scores and add info to return map[]
	    List<Map> mapReturn = new ArrayList<Map>();
	    Map[] theReturn = new HashMap[scores.size()];
	    int c = 0;
	    for (Iterator<Score> sItr = scores.iterator(); sItr.hasNext();){
	    	Score currScore = (Score) sItr.next();
	    	Lineitem currLi = new Lineitem();
	    	
	    	Course currCourse = new Course();
	    	try {
	    		currLi = liLoader.loadById(currScore.getLineitemId());
	    		currCourse = cLoader.loadById(currLi.getCourseId());
	    	}catch (Exception ex){
	    		log.logError("BBMobile Error loading lineitem from score: " + ex.getLocalizedMessage());
	    	}
	    	Map curr = new HashMap();
	    	curr.put("label", currLi.getName());
	    	curr.put("grade", currScore.getGrade());
	    	curr.put("crs_id", currCourse.getId().toExternalString());
	    	curr.put("crs_name", currCourse.getTitle());
	    	curr.put("points_poss", String.valueOf(currLi.getPointsPossible()));
	    	curr.put("pos", String.valueOf(currLi.getColumnOrder()));
	    	curr.put("type", "601");
	    	curr.put("id", currScore.getId().toExternalString());
	    	curr.put("col_type", currLi.getType());
	    	
	    	mapReturn.add(curr);
	    	c++;
	    }
	    
	    return mapReturn;
	    
    }

    /**
     * 
     * @param strUsername
     * @param strCrsId
     * @return
     */
    public Map[] LoadGradesByUserNameAndCourseId(String strUsername, String strCrsId){
        try { liLoader = LineitemDbLoader.Default.getInstance();
        	cmLoader = CourseMembershipDbLoader.Default.getInstance();
        	uLoader = UserDbLoader.Default.getInstance();
        } catch (Exception ex){ ex.printStackTrace(); }

        List liCurr = new ArrayList();
        try {
            liCurr = liLoader.loadByCourseId(Id.generateId(Course.DATA_TYPE, strCrsId));
            for (Iterator itrLi = liCurr.iterator(); itrLi.hasNext();){
                Lineitem li = (Lineitem) itrLi.next();
                if(li.getIsAvailable() == false){
                    liCurr.remove(li);
                }
                

            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        Map[] the_return = new HashMap[liCurr.size()];

        for(int i = 0; i < liCurr.size(); i++){
            Lineitem li = (Lineitem) liCurr.get(i);

            the_return[i] = new HashMap();
            the_return[i].put("label", li.getName());
            //for each line item we need to get the sore for that student
            List scores = li.getScores();
            for(Iterator itr = scores.iterator(); itr.hasNext();){
            	Score currScore = (Score) itr.next();
            	CourseMembership currCm = new CourseMembership();
            	User currUser = new User();
            	try{
            		currCm = cmLoader.loadById(currScore.getCourseMembershipId());
            		currUser = uLoader.loadByUserName(strUsername);
            		
            	}catch(Exception ex){ex.printStackTrace();}
            	if(currCm.getUserId().toExternalString().equals(currUser.getId().toExternalString())){
            		the_return[i].put("grade", currScore.getGrade());
            		the_return[i].put("crs_id", currCm.getCourseId().toExternalString());
            	}
            }
        }
        
        return the_return;
    }
    
}
