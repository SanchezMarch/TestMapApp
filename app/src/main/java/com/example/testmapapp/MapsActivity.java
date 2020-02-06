package com.example.testmapapp;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private String TAG = "hey";

    List<LatLng> path = new ArrayList(); //array for our coords

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//
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

        //adding markers of our route and marker
        LatLng start = new LatLng(49.42161, 26.99653);
        mMap.addMarker(new MarkerOptions().position(start).title("Marker in Khmelnitskiy"));

        LatLng finish = new LatLng(50.4546600, 30.5238000);
        mMap.addMarker(new MarkerOptions().position(finish).title("Marker in Kyiv"));

        LatLng zhytomyr = new LatLng(50.2648700, 28.6766900);
        final MarkerOptions someMarker = new MarkerOptions().position(zhytomyr).draggable(true).title("Marker in Zhytomyr");
        mMap.addMarker(someMarker);

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCQVQAvsUQa9CGFN3s1dPrgyXeKeh-Nz3c")
                .build();


        DirectionsApiRequest request = DirectionsApi.getDirections(context, "49.42161, 26.99523", "50.4546600, 30.5238000");
        try {
            DirectionsResult result = request.await();
//code every point of route
            if (result.routes != null && result.routes.length > 0) {
                DirectionsRoute route = result.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }
        //draw polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(7);
            mMap.addPolyline(opts);
//
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // set listener for getting coords of Marker after dragend
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

            }

            LatLng markerCoord = new LatLng(someMarker.getPosition().latitude, someMarker.getPosition().longitude);

        });

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start, 7));

    }

    // What need to do:
    //1. find way to check  markerCoord is in Array path.
    //2. if (markerCoord is in Array){start update to LtLn of someMarker}
}


