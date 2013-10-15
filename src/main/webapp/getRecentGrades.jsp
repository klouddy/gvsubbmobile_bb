<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareGradesRecent,
				java.util.List,
				java.util.ArrayList,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Grades grades = new Grades();
	List<Map> mGrades = new ArrayList<Map>();
	Integer limit = null;
	if(request.getParameter("limit") != null){
		limit = Integer.valueOf(request.getParameter("limit"));
	}
	else{
		limit = 5;
	}
	String crsId = request.getParameter("crs_id");
	if(crsId == null){
		mGrades = grades.LoadRecentGradesByUsername(sm.getUserName(), limit);	
	}
	else{
		mGrades = grades.LoadRecentGradesByUsername(sm.getUserName(), limit, crsId);
	}
	
	if(mGrades != null){
		Collections.sort(mGrades, new CompareGradesRecent());
		// take away calendar item.
		for(Map a: mGrades){
			a.remove("post_cal");
		}
	}
	JSONArray jaAnns = new JSONArray(mGrades);
	
	
	out.print(jaAnns.toString());
}
else{
	out.print("not authenticated");
}
%>