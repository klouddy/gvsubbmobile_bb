package edu.gvsu.bbmobile;

import blackboard.data.announcement.Announcement;
import blackboard.data.content.Content;
import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.user.User;
import blackboard.persist.Id;
import blackboard.persist.content.ContentDbLoader;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.platform.session.BbSession;
import blackboard.platform.session.BbSessionManagerService;
import blackboard.platform.session.BbSessionManagerServiceFactory;

import javax.servlet.http.HttpServletRequest;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: romeroj
 * Date: 9/15/13
 * Time: 11:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionManager {
    private BbSession session = null;
    private int sessionId = 0;
    private HttpServletRequest req;
    private CourseMembershipDbLoader cmLoader;

    public SessionManager(HttpServletRequest request){
        BbSessionManagerService sessionMan = BbSessionManagerServiceFactory.getInstance();
        session = sessionMan.getSession(request);
        sessionId = session.getBbSessionId();

        req = request;
    }

    public boolean isAuthenticated(){
        if(session != null){
            return session.isAuthenticated();
        }
        return false;
    }

    public int getSessionId(){
        return sessionId;
    }

    public BbSession getSession(){
        return session;
    }

    public boolean hasAccessToCourse(String strCrsId){
        boolean boolReturn = false;
        try { cmLoader = CourseMembershipDbLoader.Default.getInstance();}catch(Exception ex){ ex.printStackTrace(); }
        Id idUserId = session.getUserId();

        List cmlCurr = null;
        try{ cmlCurr = cmLoader.loadByCourseId(Id.generateId(Course.DATA_TYPE, strCrsId));}catch(Exception ex){ex.printStackTrace();}
        for(Iterator itr = cmlCurr.iterator(); itr.hasNext();){
            CourseMembership cmCurr = (CourseMembership) itr.next();
            if(cmCurr.getUserId().equals(idUserId)){
                return true;
            }
        }

        return boolReturn;
    }

    public boolean hasAccessToContentItem(String strCntId){
        try{
            ContentDbLoader cntLoader = ContentDbLoader.Default.getInstance();
            Id idUserId = session.getUserId();

            Content cntItem = cntLoader.loadById(Id.generateId(Content.DATA_TYPE, strCntId));

            if(this.hasAccessToCourse(cntItem.getCourseId().toExternalString())){
                CourseMembership cmCurr = CourseMembershipDbLoader.Default.getInstance().loadByCourseAndUserId(cntItem.getCourseId(), idUserId);
                if(cmCurr.getRole().equals(CourseMembership.Role.INSTRUCTOR) || cmCurr.getRole().equals(CourseMembership.Role.TEACHING_ASSISTANT)){
                    return true;
                }
                else if (cmCurr.getRole().equals(CourseMembership.Role.STUDENT) && cntItem.getIsAvailable()){
                    return true;
                }

            }
            return false;

        }catch (Exception ex){
            return false;
        }


    }
    
    
    public String getUserName(){
        return session.getUserName();
    }

}
