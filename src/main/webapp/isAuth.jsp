<%@page import="edu.gvsu.bbmobile.SessionManager"%>

<%

SessionManager sm = new SessionManager(request);

if(sm.isAuthenticated()){
    out.print("true");
}
else { out.print ("false"); }



%>
