<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareAnnRecent,
				java.util.List,
				java.util.ArrayList,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Announcements ann = new Announcements();
	List<Map> mAnn = new ArrayList<Map>();
	
	Integer limit = null;
	if(request.getParameter("limit") != null){
		limit = Integer.valueOf(request.getParameter("limit"));
	}
	else{
		limit = 5;
	}
	String crsId = request.getParameter("crs_id");
	if(crsId != null){
		mAnn = ann.loadRecentAnnByUserName(sm.getUserName(), limit, crsId);
	}
	else{
		mAnn = ann.loadRecentAnnByUserName(sm.getUserName(), limit);		
	}
	 
	
	JSONArray jaAnns = new JSONArray(mAnn);
	
	
	out.print(jaAnns.toString());
}
else{
	out.print("not authenticated");
}
%>