<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareCourses,
				java.util.List,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Courses crs = new Courses();
	Integer limit = null;
	if(request.getParameter("limit") != null){
		limit = Integer.valueOf(request.getParameter("limit"));
	}
	List<Map> mCrs = crs.getAvailableCoursesForUser(sm.getUserName());
	if(mCrs != null){
		Collections.sort(mCrs, new CompareCourses());
	}
	JSONArray jaAnns = new JSONArray(mCrs);
	
	
	out.print(jaAnns.toString());
}
else{
	out.print("not authenticated");
}
%>