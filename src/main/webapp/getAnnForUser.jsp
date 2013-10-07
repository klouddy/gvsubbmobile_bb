<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareAnn,
				java.util.List,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Announcements ann = new Announcements();
	Integer limit = null;
	if(request.getParameter("limit") != null){
		limit = Integer.valueOf(request.getParameter("limit"));
	}
	List<Map> mAnn = ann.loadAnnByUserName(sm.getUserName(), limit);
	CompareAnn bob = new CompareAnn();
	if(mAnn != null){
		Collections.sort(mAnn, new CompareAnn());
	}
	JSONArray jaAnns = new JSONArray(mAnn);
	
	
	out.print(jaAnns.toString());
}
else{
	out.print("not authenticated");
}
%>