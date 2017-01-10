package com.example.anthony.maps;

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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private final static int LOCATION_REQ_CODE = 456;
    private Button bt_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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

        afficherLocalisationMap();
    }

    /* ---------------------------------
    // Maps
    // -------------------------------- */
    private void afficherLocalisationMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
        }
    }

    /* ---------------------------------
    // CallBack
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

        // Add a marker in Sydney and move the camera
        //        LatLng sydney = new LatLng(-34, 151);
        //        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onClick(View v) {
        new LoadAT().execute();
    }

    /* ---------------------------------
    // AsyncTask
    // -------------------------------- */
    private class LoadAT extends AsyncTask<Void, Void, ArrayList<LatLng>> {
        private ProgressDialog dialog;
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MapsActivity.this, "", "Chargement...");
        }

        @Override
        protected ArrayList<LatLng> doInBackground(Void... params) {
            try {

                LatLng start = new LatLng(43.603341, 1.435578);
                LatLng end = new LatLng(43.584166, 1.437178);
                return MapsUtils.getPolylineFromAdresse(start, end);
            }
            catch (final Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<LatLng> result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (exception != null) {
                Toast.makeText(MapsActivity.this, "Erreur de chargement : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
            else if (result.isEmpty()) {
                Toast.makeText(MapsActivity.this, "Pas de résultat ", Toast.LENGTH_SHORT).show();
            }
            else {
                //On déclare le polyline, c'est-à-dire le trait (ici bleu) que l'on ajoute sur la carte pour tracer l'itinéraire
                final PolylineOptions polylines = new PolylineOptions();
                polylines.color(Color.BLUE);

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                //On construit le polyline
                for (final LatLng latLng : result) {
                    polylines.add(latLng);
                    builder.include(latLng);
                }

                //On déclare un marker vert que l'on placera sur le départ
                final MarkerOptions markerA = new MarkerOptions();
                markerA.position(result.get(0));
                markerA.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                //On déclare un marker rouge que l'on mettra sur l'arrivée
                final MarkerOptions markerB = new MarkerOptions();
                markerB.position(result.get(result.size() - 1));
                markerB.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                //On met à jour la carte
                LatLngBounds bounds = builder.build();
                int padding = 0; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                mMap.moveCamera(cu);

                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(result.get(0), 15));

                mMap.addMarker(markerA);
                mMap.addPolyline(polylines);
                mMap.addMarker(markerB);
            }
        }
    }
}
