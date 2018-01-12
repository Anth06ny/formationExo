package com.example.anthony.maps;

import android.content.Context;

import com.example.anthony.maps.beans.Station;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Anthony on 18/10/2017.
 */

public class StationIconRenderer extends DefaultClusterRenderer<Station> {

    public StationIconRenderer(Context context, GoogleMap map, ClusterManager<Station> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Station item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);

        markerOptions.position(item.getPosition());
        markerOptions.title(item.getName());
        if (item.getAvailable_bikes() == 0 && item.getAvailable_bike_stands() == 0) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        else if (item.getAvailable_bikes() == 0) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }
        else if (item.getAvailable_bike_stands() == 0) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        }
        else {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
    }

    @Override
    protected void onClusterItemRendered(Station clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
        marker.setTag(clusterItem);
    }
}
