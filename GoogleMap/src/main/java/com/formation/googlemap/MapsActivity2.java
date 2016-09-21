package com.formation.googlemap;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private final static int LOCATION_REQ_CODE = 456;
    private Button bt_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bt_add = (Button) findViewById(R.id.bt_add);
        bt_add.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        setUpMap();
    }

    /* ---------------------------------
    //  MAP
    // -------------------------------- */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        //        LatLng sydney = new LatLng(-34, 151);
        //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        setUpMap();
    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
        }
    }

    /* ---------------------------------
   //  Clic
   // -------------------------------- */

    @Override
    public void onClick(View v) {
        new LoadAT().execute();
    }

    private class LoadAT extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog dialog;
        private final ArrayList<LatLng> lstLatLng = new ArrayList<LatLng>();

        private final static String URL_WS_GOOGLE = "http://maps.googleapis.com/maps/api/directions/xml?sensor=false&language=fr";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MapsActivity2.this, "", "Chargement...");
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //Construction de l'url à appeler
                final StringBuilder url = new StringBuilder(URL_WS_GOOGLE);
                url.append("&origin=");
                url.append("40.714224,-73.961452");
                url.append("&destination=");
                url.append("40.714524,-73.961552");

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
                    return false;
                }

                //On récupère les steps
                final Element elementLeg = (Element) document.getElementsByTagName("leg").item(0);
                final NodeList nodeListStep = elementLeg.getElementsByTagName("step");
                final int length = nodeListStep.getLength();

                for (int i = 0; i < length; i++) {
                    final Node nodeStep = nodeListStep.item(i);

                    if (nodeStep.getNodeType() == Node.ELEMENT_NODE) {
                        final Element elementStep = (Element) nodeStep;

                        //On décode les points du XML
                        decodePolylines(elementStep.getElementsByTagName("points").item(0).getTextContent());
                    }
                }

                return true;
            }
            catch (final Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        /**
         * Méthode qui décode les points en latitude et longitudes
         */
        private void decodePolylines(final String encodedPoints) {
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
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (!result) {
                Toast.makeText(MapsActivity2.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
            }
            else {
                //On déclare le polyline, c'est-à-dire le trait (ici bleu) que l'on ajoute sur la carte pour tracer l'itinéraire
                final PolylineOptions polylines = new PolylineOptions();
                polylines.color(Color.BLUE);

                //On construit le polyline
                for (final LatLng latLng : lstLatLng) {
                    polylines.add(latLng);
                }

                //On déclare un marker vert que l'on placera sur le départ
                final MarkerOptions markerA = new MarkerOptions();
                markerA.position(lstLatLng.get(0));
                markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                //On déclare un marker rouge que l'on mettra sur l'arrivée
                final MarkerOptions markerB = new MarkerOptions();
                markerB.position(lstLatLng.get(lstLatLng.size() - 1));
                markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                //On met à jour la carte
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lstLatLng.get(0), 10));
                mMap.addMarker(markerA);
                mMap.addPolyline(polylines);
                mMap.addMarker(markerB);
            }
        }
    }
}
