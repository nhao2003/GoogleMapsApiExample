package com.example.googlemapsapiexample.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.directions.route.Route;
import com.directions.route.RouteException;
import com.example.googlemapsapiexample.R;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.directions.route.AbstractRouting;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.material.snackbar.Snackbar;

public class Home extends Fragment implements RoutingListener {
    GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private SupportMapFragment mapFragment;
    private LatLng currentPosition;
    public static LatLng end = null;

    MarkerOptions markerEnd;
    //to get location permissions.
    private final static int LOCATION_REQUEST_CODE = 44;
    //polyline object
    private List<Polyline> polylines = null;
    // btn change background
    private FloatingActionButton btnNormal;
    private FloatingActionButton btnStatellite;
    private FloatingActionButton btnHybrid;
    private FloatingActionButton btnTerrain;
    private FloatingActionButton btnNone;
    // btn find route
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabRoute;
    // search
    private SearchView mSearchView;


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
        // change type map
        onHandleChangeTypeMap();

        // btn find route
        fabRoute = view.findViewById(R.id.fab_route);
        fabRoute.setOnClickListener(v -> {
            // tim duong di giua hai diem
            //start route finding
            if (end != null) {
                Findroutes(currentPosition, end);
            }
        });

        // search
        onHandleSearch();
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
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setPadding(0, 120, 0, 0);
            // lay vi tri hien tai
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
            if (isPermissionGranted()) {
                getCurrentLocation();
            } else {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
            }

            // click mot diem => lay thong tin tai diem do
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(@NonNull LatLng latLng) {

                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        // xóa marker cũ
                        map.clear();
                        end = latLng;
                        // lay thong tin tai diem do
                        ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        ShowSnackbar(addresses.get(0).getAddressLine(0)); // show info address
                        markerEnd = new MarkerOptions().position(latLng).title(addresses.get(0).getAddressLine(0));

                        map.addMarker(markerEnd);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        Toast.makeText(getContext(), "Không xác định được vị trí", Toast.LENGTH_SHORT).show();
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
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 15));
                        map.getUiSettings().setZoomControlsEnabled(true);
                        map.getUiSettings().setMapToolbarEnabled(true);
                        map.getUiSettings().setMyLocationButtonEnabled(true);
                        map.setMyLocationEnabled(true);
                        map.setBuildingsEnabled(true);
                    }
                }
            });
        }
    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
            Toast.makeText(getActivity(), "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getString(R.string.api_key))  //also define your api key here.
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
        if (polylines != null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < route.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(Color.BLUE);
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);
            } else {

            }

        }
    }

    @Override
    public void onRoutingCancelled() {
        Findroutes(currentPosition, end);
    }

    private void onHandleChangeTypeMap() {
        // change type map
        btnNormal = getActivity().findViewById(R.id.fbtn_normal);
        btnNormal.setOnClickListener(v -> {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        });
        btnStatellite = getActivity().findViewById(R.id.fbtn_satellite);
        btnStatellite.setOnClickListener(v -> {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        });
        btnHybrid = getActivity().findViewById(R.id.fbtn_hybrid);
        btnHybrid.setOnClickListener(v -> {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        });
        btnTerrain = getActivity().findViewById(R.id.fbtn_terrain);
        btnTerrain.setOnClickListener(v -> {
            map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        });
        btnNone = getActivity().findViewById(R.id.fbtn_none);
        btnNone.setOnClickListener(v -> {
            map.setMapType(GoogleMap.MAP_TYPE_NONE);
        });
    }


    private void onHandleSearch() {
        mSearchView = getActivity().findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                String location = mSearchView.getQuery().toString();
                List<Address> addresses = null;

                if (location != null) {
                    Geocoder geocoder = new Geocoder(getContext());

                    try {
                        addresses = geocoder.getFromLocationName(location, 1);

                        if (addresses != null) {
                            Address address = addresses.get(0);
                            end = new LatLng(address.getLatitude(), address.getLongitude());
                            // add this marker to map
                            map.clear();
                            markerEnd = new MarkerOptions().position(end).title(location);

                            map.addMarker(markerEnd);
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(end, 19));
                        }
                    } catch (Exception e) {
                        ShowToast("Không tìm ra địa điểm: " + e.getMessage());
                    }
                } else {
                    ShowToast("Vui lòng nhập gì đó");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    public void ShowToast(String value) {
        Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
    }

    public void ShowSnackbar(String value) {
        RelativeLayout layout = getView().findViewById(R.id.base_layout);
        Snackbar snackbar = Snackbar.make(layout, value, Snackbar.LENGTH_LONG)
                .setBackgroundTint(ContextCompat.getColor(getContext(), R.color.white))
                .setTextColor(ContextCompat.getColor(getContext(), R.color.black));

        snackbar.show();
    }
}
