package com.example.n0584052.whatsfortea;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
//import com.google.android.gms.maps.*;
//import com.google.android.gms.maps.model.*;
import static android.R.attr.radius;
import static android.R.attr.type;

//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.ui.PlacePicker;



public class ShopsNearbyFragment extends Fragment {
//    protected GeoDataClient mGeoDataClient;
//    protected PlaceDetectionClient mPlaceDetectionClient;
//    protected GoogleApiClient mGoogleApiClient;
    //private OnFragmentInteractionListener mListener;
//    GoogleMap googleMap;
//    MapView mMapView;
//    View mView;
//
    public ShopsNearbyFragment() {

    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_shops_nearby, container, false);
//
//        mMapView = (MapView) rootView.findViewById(R.id.map);
//        mMapView.onCreate(savedInstanceState);
//
//        mMapView.onResume(); // needed to get the map to display immediately
//
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        mMapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(GoogleMap mMap) {
//                googleMap = mMap;
//
//                // For showing a move to my location button
//
//                googleMap.setMyLocationEnabled(true);
//
//                // For dropping a marker at a point on the Map
//                LatLng sydney = new LatLng(-34, 151);
//                googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//                // For zooming automatically to the location of the marker
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//        });
//
//        return rootView;
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        mMapView.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mMapView.onPause();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mMapView.onDestroy();
//    }
//
//    @Override
//    public void onLowMemory() {
//        super.onLowMemory();
//        mMapView.onLowMemory();
//    }
}
