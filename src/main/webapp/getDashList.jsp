<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.*,
				java.util.List,
				java.util.ArrayList,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Announcements ann = new Announcements();
	Grades getGrades = new Grades();
	List<Map> theRet = new ArrayList<Map>();
	List<Map> grades = new ArrayList<Map>();
	
	//limits
	Integer limit = null;
	if(request.getParameter("limit") != null){
		limit = Integer.valueOf(request.getParameter("limit"));
	}
	else{
		limit = 5;
	}
	String strCrsId = request.getParameter("crs_id");
	if(strCrsId != null){
		theRet = ann.loadRecentAnnByUserName(sm.getUserName(), limit, strCrsId);
		grades = getGrades.LoadRecentGradesByUsername(sm.getUserName(), limit, strCrsId);
	}
	else{
		theRet = ann.loadRecentAnnByUserName(sm.getUserName(), limit);
		grades = getGrades.LoadRecentGradesByUsername(sm.getUserName(), limit);
	}
	if(theRet != null){
		Collections.sort(theRet, new CompareAnnRecent());
		// take away calendar item.
		for(Map a: theRet){
			a.remove("post_cal");
		}
	}
	if(grades != null){
		Collections.sort(grades, new CompareGradesRecent());
		for(Map a: grades){
			a.remove("post_cal");
		}
		theRet.addAll(grades);
	}
	
	//Course specific
	//TODO: add this element
	
	
	

	
	
	JSONArray jaAnns = new JSONArray(theRet);
	
	
	out.print(jaAnns.toString());
}
else{
	out.print("not authenticated");
}
%>