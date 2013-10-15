<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				java.util.List,
				java.util.ArrayList,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Contents cnts = new Contents();
	List<Map> mContents = new ArrayList<Map>();
	Integer limit = null;
	if(request.getParameter("limit") != null){
		limit = Integer.valueOf(request.getParameter("limit"));
	}
	else{
		limit = 5;
	}
	String crsId = request.getParameter("crs_id");
	if(crsId == null){
		mContents = cnts.getRecentGradeableContents(sm.getUserName(), limit);
	}
	else{
		mContents = cnts.getRecentGradeableContents(sm.getUserName(), limit, crsId);
	}
	
	
	JSONArray jaAnns = new JSONArray(mContents);
	out.print(jaAnns.toString());
}
else{
	out.print("not authenticated");
}
%>