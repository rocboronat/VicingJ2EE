<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<%@page import="cat.pipo.bicing.cities.spain.barcelona.BarcelonaGlobals" %>
<%@page import="cat.pipo.bicing.cities.spain.girona.GironaGlobals" %>
<%@page import="cat.pipo.bicing.cities.canada.montreal.MontrealGlobals" %>
<%@page import="cat.pipo.bicing.bean.Station" %>
<%@page import="java.util.List" %>
<%@page import="java.util.Iterator" %>
<%@page import="java.text.SimpleDateFormat" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Cp1252"/>
<title></title>
</head>
<body>

<% SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); %>
<li>Barcelona last update: <%=sdf.format(BarcelonaGlobals.LAST_UPDATE)%> (<%=BarcelonaGlobals.LAST_UPDATE.getTime()%>)</li>
<li>Girona last update: <%=sdf.format(GironaGlobals.LAST_UPDATE)%> (<%=GironaGlobals.LAST_UPDATE.getTime()%>)</li>
<li>Montreal last update: <%=sdf.format(MontrealGlobals.LAST_UPDATE)%> (<%=MontrealGlobals.LAST_UPDATE.getTime()%>)</li>

<%	synchronized (GironaGlobals.STATIONS){ %>
		<h1>Girona Girocleta</h1>
		<table>
<%	List barcelona = GironaGlobals.STATIONS;
	for (Iterator iter = barcelona.iterator(); iter.hasNext();) {
		Station s = (Station) iter.next(); %>
		<tr>
			<td><%=s.getId()%></td>
			<td><%=s.getDireccio()%></td>
			<td><%=s.getForatsPlens()%></td>
			<td><%=s.getForatsBuits()%></td>
			<td><%=s.getCoords()%></td>
			<td><a href="http://maps.google.com/maps?f=q&source=s_q&hl=es&geocode=&q=<%=s.getY()%>,<%=s.getX()%>&sll=0.0,0.0&sspn=0.011861,0.027874&ie=UTF8&t=h&z=16" target="_blank">show</a></td>
		</tr>
<%	}} %>
		</table>

<%	synchronized (BarcelonaGlobals.STATIONS){ %>
		<h1>Barcelona Bicing</h1>
		<table>
<%	List barcelona = BarcelonaGlobals.STATIONS;
	for (Iterator iter = barcelona.iterator(); iter.hasNext();) {
		Station s = (Station) iter.next(); %>
		<tr>
			<td><%=s.getId()%></td>
			<td><%=s.getDireccio()%></td>
			<td><%=s.getForatsPlens()%></td>
			<td><%=s.getForatsBuits()%></td>
			<td><%=s.getCoords()%></td>
			<td><a href="http://maps.google.com/maps?f=q&source=s_q&hl=es&geocode=&q=<%=s.getY()%>,<%=s.getX()%>&sll=0.0,0.0&sspn=0.011861,0.027874&ie=UTF8&t=h&z=16" target="_blank">show</a></td>
		</tr>
<%	}} %>
		</table>

<%	synchronized (MontrealGlobals.STATIONS){ %>
		<h1>Montreal Bixi</h1>
		<table>
<%	List barcelona = MontrealGlobals.STATIONS;
	for (Iterator iter = barcelona.iterator(); iter.hasNext();) {
		Station s = (Station) iter.next(); %>
		<tr>
			<td><%=s.getId()%></td>
			<td><%=s.getDireccio()%></td>
			<td><%=s.getForatsPlens()%></td>
			<td><%=s.getForatsBuits()%></td>
			<td><%=s.getCoords()%></td>
			<td><a href="http://maps.google.com/maps?f=q&source=s_q&hl=es&geocode=&q=<%=s.getY()%>,<%=s.getX()%>&sll=0.0,0.0&sspn=0.011861,0.027874&ie=UTF8&t=h&z=16" target="_blank">show</a></td>
		</tr>
<%	}} %>
		</table>

</body>
</html>
