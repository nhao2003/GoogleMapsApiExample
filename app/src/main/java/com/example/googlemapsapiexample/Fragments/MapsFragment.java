package com.example.googlemapsapiexample.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.example.googlemapsapiexample.Models.DirectionResponses;
import com.example.googlemapsapiexample.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.directions.route.AbstractRouting;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;

public class MapsFragment extends Fragment implements RoutingListener{

    GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private LatLng currentPosition;
    private LatLng end = null;

    MarkerOptions markerEnd;
    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 44;
    //polyline object
    private List<Polyline> polylines=null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    boolean isPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_REQUEST_CODE);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            // lay vi tri hien tai
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            if(isPermissionGranted()){
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

            // click mot diem => lay thong tin tai diem do
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {

                    Geocoder geocoder = new Geocoder(getContext());
                    try{
                        // xóa marker cũ
                        map.clear();

                        end=latLng;

                        // lay thong tin tai diem do
                        ArrayList<Address> addresses = (ArrayList<Address>)geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        Toast.makeText(getContext(), addresses.get(0).getAddressLine(0), Toast.LENGTH_LONG).show();
                        markerEnd = new MarkerOptions().position(latLng).title(addresses.get(0).getAddressLine(0));

                        map.addMarker(markerEnd);

                        // tim duong di giua hai diem
                        //start route finding
                        Findroutes(currentPosition,end);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // ham lay toa do hien tai
        if (isPermissionGranted()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        // init lat lng
                        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        // create marker
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
                        map.getUiSettings().setZoomControlsEnabled(true);
                        map.getUiSettings().setMapToolbarEnabled(true);
                        map.setMyLocationEnabled(true);
                        map.setBuildingsEnabled(true);
                    }
                }
            });
        }
    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End)
    {
        if(Start==null || End==null) {
            Toast.makeText(getActivity(),"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key("AIzaSyBg8RtAbXyLYcRpQOe2KPwCuNNvW-Rrq70")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        ShowToast("Error" + e.getMessage());
        Log.d("Error", e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        ShowToast("Finding Route...");
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        // thanh cong => ve duong di
        CameraUpdate center = CameraUpdateFactory.newLatLng(currentPosition);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(Color.BLUE);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);
            }
            else {

            }

        }
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(currentPosition,end);
    }

    public void ShowToast(String value) {
        Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
    }
}
