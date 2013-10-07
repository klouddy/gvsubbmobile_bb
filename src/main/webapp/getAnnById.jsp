<%@ page language="java" contentType="text/html; charset=US-ASCII"
    pageEncoding="US-ASCII"%>
<%@page import="edu.gvsu.bbmobile.*,
				edu.gvsu.bbmobile.compare.CompareAnn,
				java.util.List,
				java.util.Map,
				java.util.ArrayList,
				java.util.Collections,
				org.json.*" %>
<%

SessionManager sm = new SessionManager(request);

blackboard.platform.log.LogService logService = blackboard.platform.log.LogServiceFactory.getInstance();
blackboard.platform.log.Log log = logService.getDefaultLog();


if(sm.isAuthenticated()){
	Announcements ann = new Announcements();
	
	String annId = request.getParameter("ann_id");
	if(annId != null && !(annId.equals(""))){
	
		Map mAnn = ann.LoadAnnById(annId, sm.getUserName());
		List<Map> lstAnn = new ArrayList<Map>();
		lstAnn.add(mAnn);
		
		JSONArray ja = new JSONArray(lstAnn);
		
		
		out.print(ja.toString());
	}
	else{
		out.print("{}");
	}
}
else{
	out.print("not authenticated");
}
%>