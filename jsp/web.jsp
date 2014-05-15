<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<%@page import="cat.pipo.bicing.cities.spain.barcelona.BarcelonaGlobals" %>
<%@page import="cat.pipo.bicing.cities.spain.girona.GironaGlobals" %>
<%@page import="cat.pipo.bicing.cities.canada.montreal.MontrealGlobals" %>
<%@page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Global Bicing</title>

<div>
<% SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); %>
<li>Barcelona last update: <%=sdf.format(BarcelonaGlobals.LAST_UPDATE)%> (<%=BarcelonaGlobals.LAST_UPDATE.getTime()%>)</li>
<li>Girona last update: <%=sdf.format(GironaGlobals.LAST_UPDATE)%> (<%=GironaGlobals.LAST_UPDATE.getTime()%>)</li>
<li>Montreal last update: <%=sdf.format(MontrealGlobals.LAST_UPDATE)%> (<%=MontrealGlobals.LAST_UPDATE.getTime()%>)</li>
<!-- <input type="button" title="I'm so horny!!" value="Pushme!!" onclick="recargar();"/> -->
</div>

</body>
</html>