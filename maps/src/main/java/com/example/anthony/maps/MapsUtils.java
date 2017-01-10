package com.example.anthony.maps;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Anthony on 10/01/2017.
 */
public class MapsUtils {

    private final static String URL_WS_GOOGLE = "http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=fr";

    /**
     * @param geoStart "43.603341,1.435578"
     * @param geoEnd   "43.584166,1.437178"
     * @return
     * @throws Exception
     */
    public static ArrayList<LatLng> getPolylineFromAdrasse(String geoStart, String geoEnd) throws Exception {

        //Construction de l'url à appeler
        final StringBuilder url = new StringBuilder(URL_WS_GOOGLE);
        url.append("&origin=");
        url.append(geoStart);
        url.append("&destination=");
        url.append(geoEnd);

        Log.w("TAG_GEO", url.toString());

        //Appel du web service
        final InputStream stream = new URL(url.toString()).openStream();

        //Traitement des données
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringComments(true);

        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        final Document document = documentBuilder.parse(stream);
        document.getDocumentElement().normalize();

        //On récupère d'abord le status de la requête
        final String status = document.getElementsByTagName("status").item(0).getTextContent();
        if (!"OK".equals(status)) {
            throw new Exception("Erreur lors de la demande de trajet start:##" + geoStart + "## end:##" + geoEnd + "##");
        }

        //On récupère les steps
        final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
        final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
        final int length = nodeListStep.getLength();

        ArrayList<LatLng> lstLatLng = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            final Node nodeStep = nodeListStep.item(i);

            if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                final Element elementStep = (Element) nodeStep;
                //On décode les points du XML
                lstLatLng.addAll(decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent()));
            }
        }

        return lstLatLng;
    }

    public static ArrayList<LatLng> getPolylineFromAdresse(String geoStart, String geoEnd) throws Exception {
        //Construction de l'url à appeler
        final StringBuilder url = new StringBuilder(URL_WS_GOOGLE);
        url.append("&origin=");
        url.append(geoStart);
        url.append("&destination=");
        url.append(geoEnd);

        Log.w("TAG_GEO", url.toString());

        ArrayList<LatLng> lstLatLng = new ArrayList<>();

        return lstLatLng;
    }

    /**
     * Méthode qui décode les points en latitude et longitudes
     */
    private static ArrayList<LatLng> decodePolylines(final String encodedPoints) {
        ArrayList<LatLng> lstLatLng = new ArrayList<>();

        int index = 0;
        int lat = 0, lng = 0;

        while (index < encodedPoints.length()) {
            int b, shift = 0, result = 0;

            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);

            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;

            do {
                b = encodedPoints.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }
            while (b >= 0x20);

            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            lstLatLng.add(new LatLng((double) lat / 1E5, (double) lng / 1E5));
        }

        return lstLatLng;
    }
}
