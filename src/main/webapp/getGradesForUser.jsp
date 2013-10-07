<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareGrades,
				java.util.List,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Grades grades = new Grades();
	
	List<Map> mGrades = grades.LoadGradesByUN(sm.getUserName());
	CompareGrades bob = new CompareGrades();
	if(mGrades != null){
		Collections.sort(mGrades, new CompareGrades());
	}
	JSONArray jaGrades = new JSONArray(mGrades);
	
	out.print(jaGrades.toString());
}
else{
	out.print("not authenticated");
}
%>