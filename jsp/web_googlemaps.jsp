<%@page pageEncoding="Cp1252" contentType="text/html; charset=Cp1252" %>
<%@page import="cat.pipo.bicing.cities.spain.barcelona.BarcelonaGlobals" %>
<%@page import="cat.pipo.bicing.cities.spain.girona.GironaGlobals" %>
<%@page import="cat.pipo.bicing.Globals" %>
<%@page import="java.text.SimpleDateFormat" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Global Bicing</title>

<script type="text/javascript" src="/barcelonabicing/js/egeoxml.js"></script>
<script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAO8mgk7sxRUS4GjAKxdW5nxRZBm-8pt5EfPoIZTBBpdHD-Lv_hxQWEJvYQGs8y7-68oooMDgRn9WNkg" type="text/javascript"></script>


<script type="text/javascript">

	var mapa;

	function inicialitzarOpcionsMapa(){
		mapa.enableScrollWheelZoom();
		mapa.enableContinuousZoom();
		mapa.enableInfoWindow();

		mapa.addControl(new GMapTypeControl());
        mapa.addControl(new GLargeMapControl());
        mapa.addControl(new GOverviewMapControl());

		exml = new EGeoXml("exml", mapa);
		exml.parseString('<%=Globals.KML%>');
	}

	function cargar() {
		if (GBrowserIsCompatible()) {
			mapa = new GMap2(document.getElementById("mapa"));
			mapa.setCenter(new GLatLng(41.39805, 2.180087));

			inicialitzarOpcionsMapa();
		}
	}

	function recargar() {
		var lastCenter = mapa.getCenter();
		var lastZoom = mapa.getZoom();

//		alert(lastCenter);
//		alert(lastZoom);

		var lastCenter = mapa.getCenter();
		mapa = new GMap2(document.getElementById("mapa"));
		mapa.setCenter(lastCenter);
		mapa.setZoom(lastZoom);

		inicialitzarOpcionsMapa();
	}

</script>
</head>

<body onload="cargar()">

<div id="mapa" style="width: 100%; min-height:700px; height: 100%; border: 1px solid gray;">
</div>
<div>
<% SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); %>
<li>Barcelona last update: <%=sdf.format(BarcelonaGlobals.LAST_UPDATE)%> (<%=BarcelonaGlobals.LAST_UPDATE.getTime()%>)</li>
<li>Girona last update: <%=sdf.format(GironaGlobals.LAST_UPDATE)%> (<%=GironaGlobals.LAST_UPDATE.getTime()%>)</li>
<!-- <input type="button" title="I'm so horny!!" value="Pushme!!" onclick="recargar();"/> -->
</div>
</body>
</html>