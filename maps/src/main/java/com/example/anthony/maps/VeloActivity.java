package com.example.anthony.maps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anthony.maps.beans.Station;
import com.example.anthony.maps.beans.Trajet;
import com.example.anthony.maps.beans.tracer.DirectionResult;
import com.example.anthony.maps.ws.WsUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class VeloActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private final static int LOCATION_REQ_CODE = 456;

    //Données
    private ArrayList<Station> stations;
    private Trajet trajet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velo);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        stations = new ArrayList<>();

        new ChargerStationAT().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    /* ---------------------------------
    // Maps
    // -------------------------------- */

    /**
     * Clic sur une fenetre d'un marker
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        //Ferme la fenêtre
        marker.hideInfoWindow();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));
            if (location != null) {
                new ChargerTrajetAT(new LatLng(location.getLatitude(), location.getLongitude()), marker.getPosition()).execute();
            }
            else {
                Toast.makeText(this, "Votre position est inconnue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Gestion des clicks sur les marker (non clusters)
        mMap.setOnInfoWindowClickListener(this);

        //Gestion d'affichage des markers
        mMap.setInfoWindowAdapter(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
        }

        refreshMap();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(this).inflate(R.layout.marker_station, null);

        // Set desired height and width
        view.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.marker_window_width), RelativeLayout.LayoutParams
                .WRAP_CONTENT));

        TextView tv_nom = (TextView) view.findViewById(R.id.tv_nom);
        TextView tv_adresse = (TextView) view.findViewById(R.id.tv_adresse);
        TextView tv_velo_dispo = (TextView) view.findViewById(R.id.tv_velo_dispo);
        TextView tv_velo_vide = (TextView) view.findViewById(R.id.tv_velo_vide);
        TextView tv_aller = (TextView) view.findViewById(R.id.tv_aller);

        Station station = (Station) marker.getTag();

        tv_nom.setText(station.getName());
        tv_adresse.setText(station.getAddress());
        tv_velo_dispo.setText(station.getAvailable_bikes() + "");
        tv_velo_vide.setText(station.getAvailable_bike_stands() + "");

        return view;
    }

      /* ---------------------------------
    // Private
    // -------------------------------- */

    private void refreshMap() {
        if (mMap == null) {
            return;
        }

        //On efface tous les Markers
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        if (!stations.isEmpty()) {

            LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();

            for (Station station : stations) {
                final MarkerOptions marker = new MarkerOptions();
                LatLng latLng = station.getPosition();

                marker.position(latLng);
                marker.title(station.getName());
                if (station.getAvailable_bikes() == 0 && station.getAvailable_bike_stands() == 0) {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                else if (station.getAvailable_bikes() == 0) {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                }
                else if (station.getAvailable_bike_stands() == 0) {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                }
                else {
                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }

                mMap.addMarker(marker).setTag(station);

                latLngBounds.include(latLng);
            }

            if (trajet == null) {

                //Animation de la carte
                int padding = 100; // offset from edges of the map in pixels
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds.build(), padding));
            }
            else {
                //on affiche le trajet
                mMap.addMarker(trajet.getDepart());
                mMap.addPolyline(trajet.getPolylineOptions());
                mMap.addMarker(trajet.getArrivee());
                int padding = 100; // offset from edges of the map in pixels
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(trajet.getLatLngBounds(), padding));
            }
        }
    }

    /* ---------------------------------
    // AsyncTask
    // -------------------------------- */
    private class ChargerStationAT extends AsyncTask {

        private Exception exception;
        private ArrayList<Station> stationsServeur;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(VeloActivity.this, "", "Chargement...");
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                stationsServeur = WsUtils.getStationsDuServeur();
            }
            catch (Exception e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            progressDialog.dismiss();

            if (exception != null) {
                Toast.makeText(VeloActivity.this, "Erreur de chargement : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
            else if (stationsServeur == null) {
                Toast.makeText(VeloActivity.this, "Pas de résultat ", Toast.LENGTH_SHORT).show();
            }
            else {
                stations.clear();
                stations.addAll(stationsServeur);

                refreshMap();
            }
        }
    }

    private class ChargerTrajetAT extends AsyncTask {

        private Exception exception;
        private LatLng depart;
        private LatLng arrive;
        private ProgressDialog progressDialog;

        public ChargerTrajetAT(LatLng depart, LatLng arrive) {
            this.depart = depart;
            this.arrive = arrive;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(VeloActivity.this, "", "Chargement...");
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                DirectionResult directionResult = MapsUtils.getPolylineFromAdresseWithLib(depart, arrive);
                if (directionResult != null) {
                    trajet = new Trajet(directionResult);
                }
                else {
                    throw new Exception("Direction result à nulle");
                }
            }
            catch (Exception e) {
                exception = e;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            progressDialog.dismiss();

            if (exception != null) {
                exception.printStackTrace();
            }
            else {
                refreshMap();
            }
        }
    }
}
