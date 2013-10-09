<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareContent,
				java.util.List,
				java.util.Map,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
	Contents cnts = new Contents();
	List<Map> lstCnts = cnts.getContents(sm.getUserName());
	if(lstCnts != null && lstCnts.size() > 0){
		Collections.sort(lstCnts, new CompareContent());
		JSONArray jsonCnts = new JSONArray(lstCnts);
		out.print(jsonCnts.toString());
	}
	else{
		out.print("[]");
	}
	
}
else{
	out.print("not authenticated");
}
%>