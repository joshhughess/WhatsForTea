package com.example.n0584052.whatsfortea;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Bundle;
//import com.google.android.gms.maps.*;
//import com.google.android.gms.maps.model.*;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executor;

import static android.R.attr.radius;
import static android.R.attr.type;

//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.ui.PlacePicker;


public class ShopsNearbyFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("users/"+mAuth.getCurrentUser().getUid());
    private String preferredShop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shops_nearby, container, false);

        mDatabase.child("preferences").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if(dataSnapshot.getKey().equals("supermarket")){
                    Log.d("preferredshop", dataSnapshot.getValue().toString());
                    preferredShop = dataSnapshot.getValue().toString();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    String locationURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.getLatitude()+","+location.getLongitude()+"&radius=5000&type=shop&keyword="+preferredShop+"&key=AIzaSyAGqDyutTlkZu7nU2zP9gBVUILJ8Sum-4k";
                                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                    StrictMode.setThreadPolicy(policy);
                                    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                                    HttpPost httppost = new HttpPost(locationURL);
// Depends on your web service
                                    httppost.setHeader("Content-type", "application/json");

                                    InputStream inputStream = null;
                                    String result = null;
                                    try {
                                        HttpResponse response = httpclient.execute(httppost);
                                        HttpEntity entity = response.getEntity();

                                        inputStream = entity.getContent();
                                        // json is UTF-8 by default
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                                        StringBuilder sb = new StringBuilder();

                                        String line = null;
                                        while ((line = reader.readLine()) != null)
                                        {
                                            sb.append(line + "\n");
                                        }
                                        result = sb.toString();
                                        JSONObject jObject = new JSONObject(result);
                                        JSONArray jArray = jObject.getJSONArray("results");
                                        for (int i=0; i < jArray.length(); i++)
                                        {
                                            try {
                                                JSONObject oneObject = jArray.getJSONObject(i);
                                                // Pulling items from the array
                                                JSONObject geometry = oneObject.getJSONObject("geometry");
                                                String openCloseStatus = "Closed now";
                                                JSONObject opening_hours = oneObject.getJSONObject("opening_hours");
                                                if (opening_hours.has("open_now")){
                                                    openCloseStatus = "Open now";
                                                }
                                                String nameLocation = oneObject.getString("name");
                                                String ratingLocation = oneObject.getString("rating");
                                                JSONObject locations = geometry.getJSONObject("location");
                                                String oneObjectsItem = oneObject.getString("geometry");
                                                String oneObjectsItem2 = oneObject.getString("id");
                                                LatLng thisLatLong = new LatLng(Double.valueOf(locations.getString("lat")),Double.valueOf(locations.getString("lng")));
                                                googleMap.addMarker(new MarkerOptions().position(thisLatLong).title(nameLocation).snippet(ratingLocation+" ★ "+openCloseStatus));
                                                Log.d("added marker", "onSuccess");
                                            } catch (JSONException e) {
                                                // Oops
                                            }
                                        }
                                    } catch (Exception e) {
                                        // Oops
                                        Log.d("uh-oh !",e.toString());
                                    }
                                    finally {
                                        try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                                    }
                                    LatLng myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(12).build();
                                    if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                        // TODO: Consider calling
                                        //    ActivityCompat#requestPermissions
                                        // here to request the missing permissions, and then overriding
                                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                        //                                          int[] grantResults)
                                        // to handle the case where the user grants the permission. See the documentation
                                        // for ActivityCompat#requestPermissions for more details.
                                        return;
                                    }
                                    googleMap.setMyLocationEnabled(true);

                                    // For zooming automatically to the location of the marker
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                }
                            }
                        });


                return;
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    }
