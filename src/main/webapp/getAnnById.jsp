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

String strRet = "<?xml version='1.0' encoding='ISO-8859-1'?><announcement>";
if(sm.isAuthenticated()){
	Announcements ann = new Announcements();
	
	String annId = request.getParameter("ann_id");
	if(annId != null && !(annId.equals(""))){
	
		Map mAnn = ann.LoadAnnById(annId, sm.getUserName());
		strRet += "<id>" + mAnn.get("id") + "</id>";
		strRet += "<label>" + mAnn.get("label") + "</label>";
		strRet += "<type>" + mAnn.get("type") + "</type>";
		strRet += "<pos>" + mAnn.get("pos") + "</pos>";
		strRet += "<crs_id>" + mAnn.get("crs_id") + "</crs_id>";
		strRet += "<crs_name>" + mAnn.get("crs_name") + "</crs_name>";
		strRet += "<desc>" + "<![CDATA[" + mAnn.get("desc") + "]]>" + "</desc>";
		strRet += "</announcement>";
		out.print(strRet );
	}
	else{
		out.print("");
	}
}
else{
	out.print("not authenticated");
}
%>