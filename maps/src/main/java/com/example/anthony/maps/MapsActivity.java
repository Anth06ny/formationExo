package com.example.anthony.maps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anthony.maps.beans.Trajet;
import com.example.anthony.maps.beans.tracer.DirectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.InfoWindowAdapter {

    private static final int TAG_START = 1;
    private static final int TAG_STOP = 2;

    private GoogleMap mMap;
    private final static int LOCATION_REQ_CODE = 456;
    private Button bt_it, bt_it2, bt_addPosition, bt_clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bt_it = (Button) findViewById(R.id.bt_it);
        bt_it.setOnClickListener(this);
        bt_it2 = (Button) findViewById(R.id.bt_it2);
        bt_it2.setOnClickListener(this);
        bt_addPosition = (Button) findViewById(R.id.bt_addPosition);
        bt_addPosition.setOnClickListener(this);
        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_clear.setOnClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        afficherLocalisationMap();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Velo");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(this, VeloActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
            // Maps
            // -------------------------------- */
    private void afficherLocalisationMap() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQ_CODE);
        }
    }

    /**
     * Clic sur une fenetre d'un marker
     *
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(MapsActivity.this, "Go go go", Toast.LENGTH_SHORT).show();
        //Ferme la fenêtre
        marker.hideInfoWindow();
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

        // Gestion des clicks sur les marker
        mMap.setOnInfoWindowClickListener(this);

        //Gestion d'affichage des markers
        mMap.setInfoWindowAdapter(this);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(this).inflate(R.layout.marker_cellule, null);

        TextView tv = (TextView) view.findViewById(R.id.tv);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);

        Integer tag = (Integer) marker.getTag();

        if (tag != null && tag == TAG_START) {
            tv.setText("C'est le début");
            iv.setImageResource(R.mipmap.ic_start);
            iv.setColorFilter(Color.CYAN);
        }
        else if (tag != null && tag == TAG_STOP) {
            tv.setText("C'est la fin");
            iv.setImageResource(R.mipmap.ic_end);
            iv.setColorFilter(Color.GREEN);
        }

        return view;
    }

    /* ---------------------------------
    // CallBack
    // -------------------------------- */

    @Override
    public void onClick(View v) {
        if (v == bt_it) {
            new LoadAT().execute();
        }
        else if (v == bt_it2) {
            new LoadAT().execute(true);
        }
        else if (v == bt_addPosition) {
            afficherLocalisationMap();
        }
        else if (v == bt_clear) {
            mMap.clear();
        }
    }

    /* ---------------------------------
    // AsyncTask
    // -------------------------------- */
    private class LoadAT extends AsyncTask<Boolean, Void, DirectionResult> {
        private ProgressDialog dialog;
        private Exception exception;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = ProgressDialog.show(MapsActivity.this, "", "Chargement...");
        }

        @Override
        protected DirectionResult doInBackground(Boolean... params) {
            try {

                LatLng start = new LatLng(43.603341, 1.435578);

                if (params != null && params.length > 0 && params[0]) {
                    if (ActivityCompat.checkSelfPermission(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        LocationManager locationManager = (LocationManager)
                                getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        Location location = locationManager.getLastKnownLocation(locationManager
                                .getBestProvider(criteria, false));
                        if (location != null) {
                            start = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                }

                LatLng end = new LatLng(43.584166, 1.437178);

                return MapsUtils.getPolylineFromAdresseWithLib(start, end);
            }
            catch (final Exception e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(DirectionResult result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (exception != null) {
                Toast.makeText(MapsActivity.this, "Erreur de chargement : " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
            else if (result == null) {
                Toast.makeText(MapsActivity.this, "Pas de résultat ", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    Trajet trajet = new Trajet(result);
                    //On met à jour la carte
                    mMap.clear();
                    mMap.addMarker(trajet.getDepart()).setTag(TAG_START);

                    mMap.addPolyline(trajet.getPolylineOptions());
                    mMap.addMarker(trajet.getArrivee()).setTag(TAG_STOP);

                    int padding = 100; // offset from edges of the map in pixels
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(result.getLatLngBounds(), padding));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MapsActivity.this, "Pas de résultat retourné ", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }
}
