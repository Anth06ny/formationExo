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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anthony.maps.beans.Station;
import com.example.anthony.maps.beans.Trajet;
import com.example.anthony.maps.beans.metro.StationMetroBean;
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
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class VeloWithClusterActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemClickListener<Station>, View.OnClickListener {

    private static final int SHOW_VELO_ID = 1;
    private static final int SHOW_METRO_ID = 2;

    private GoogleMap mMap;
    private ClusterManager<Station> mClusterManager;
    private Button bt_refresh;
    //Données
    private ArrayList<Station> stations;
    private ArrayList<StationMetroBean> stationsMetro;
    private Trajet trajet;
    private boolean showMetro, showVelo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_velo);

        bt_refresh = (Button) findViewById(R.id.bt_refresh);

        bt_refresh.setOnClickListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        stations = new ArrayList<>();
        stationsMetro = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        new ChargerStationAT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new ChargerStationMetroAT().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            refreshMap();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, SHOW_METRO_ID, 0, "Metro");
        menu.add(0, SHOW_VELO_ID, 0, "Velo");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == SHOW_METRO_ID) {
            showMetro = !showMetro;
            refreshMap();
        }
        else if (item.getItemId() == SHOW_VELO_ID) {
            showVelo = !showVelo;
            refreshMap();
        }
        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Maps
    // -------------------------------- */

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

        //On redrige la creation de l'info window sur les markers (non cluster)
        mMap.setInfoWindowAdapter(this);

        mClusterManager = new ClusterManager<Station>(this, mMap);
        //On ajoute la gestion des markerOption pour les clusters
        mClusterManager.setRenderer(new StationIconRenderer(this, mMap, mClusterManager));
        //On redirige la création de l'InfoWindow pour les clusters
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(this);

        // Clic sur le groupement de cluster
        //mMap.setOnInfoWindowClickListener(mClusterManager.getMarkerManager());

        //On redirige les evenements de la map sur le clusterManager
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        //Gestion des clics
        mMap.setOnInfoWindowClickListener(this);

        refreshMap();
    }

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

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {

        if (marker.getTag() == null) {
            return null; //c'est un groupe
        }

        View view = LayoutInflater.from(this).inflate(R.layout.marker_station, null);

        // Set desired height and width
        view.setLayoutParams(new RelativeLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.marker_window_width), RelativeLayout.LayoutParams
                .WRAP_CONTENT));

        TextView tv_nom = (TextView) view.findViewById(R.id.tv_nom);
        TextView tv_adresse = (TextView) view.findViewById(R.id.tv_adresse);
        TextView tv_velo_dispo = (TextView) view.findViewById(R.id.tv_velo_dispo);
        TextView tv_velo_vide = (TextView) view.findViewById(R.id.tv_velo_vide);

        Station station = (Station) marker.getTag();

        tv_nom.setText(station.getName());
        tv_adresse.setText(station.getAddress());
        tv_velo_dispo.setText(station.getAvailable_bikes() + "");
        tv_velo_vide.setText(station.getAvailable_bike_stands() + "");

        return view;
    }

    @Override
    public boolean onClusterItemClick(Station station) {

        return false;
    }

      /* ---------------------------------
    // Private
    // -------------------------------- */

    private void refreshMap() {
        if (mMap == null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        //On efface tous les Markers
        mMap.clear();
        mClusterManager.clearItems();
        if (!stations.isEmpty() && showVelo) {
            LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
            for (Station station : stations) {
                mClusterManager.addItem(station);
                latLngBounds.include(station.getPosition());
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

        //Les stations de metro
        if (!stationsMetro.isEmpty() && showMetro) {
            LatLngBounds.Builder latLngBounds = new LatLngBounds.Builder();
            for (StationMetroBean stationMetroBean : stationsMetro) {

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(stationMetroBean.getPosition());
                markerOptions.title(stationMetroBean.getName());
                if (stationMetroBean.getLigne() == 1) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }
                else if (stationMetroBean.getLigne() == 2) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                }
                else if (stationMetroBean.getLigne() == 3) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                }
                else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
                mMap.addMarker(markerOptions);
                latLngBounds.include(stationMetroBean.getPosition());
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

        mClusterManager.cluster();
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
            progressDialog = ProgressDialog.show(VeloWithClusterActivity.this, "", "Chargement...");
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
                Toast.makeText(VeloWithClusterActivity.this, "Erreur de chargement : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
            else if (stationsServeur == null) {
                Toast.makeText(VeloWithClusterActivity.this, "Pas de résultat ", Toast.LENGTH_SHORT).show();
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
            progressDialog = ProgressDialog.show(VeloWithClusterActivity.this, "", "Chargement...");
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

    private class ChargerStationMetroAT extends AsyncTask {

        private Exception exception;
        private ArrayList<StationMetroBean> stationsMetroServeur;
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(VeloWithClusterActivity.this, "", "Chargement...");
        }

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                stationsMetroServeur = WsUtils.getStationsMetro();
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
                Toast.makeText(VeloWithClusterActivity.this, "Erreur de chargement : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
            else if (stationsMetroServeur == null) {
                Toast.makeText(VeloWithClusterActivity.this, "Pas de résultat ", Toast.LENGTH_SHORT).show();
            }
            else {
                stationsMetro.clear();
                stationsMetro.addAll(stationsMetroServeur);

                refreshMap();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == bt_refresh) {
            new ChargerStationAT().execute();
        }
    }
}
