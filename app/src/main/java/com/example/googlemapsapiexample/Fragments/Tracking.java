package com.example.googlemapsapiexample.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.example.googlemapsapiexample.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Tracking extends Fragment implements OnMapReadyCallback {

    Timer timer;
    GoogleMap googleMap;
    Button button;
    TextView textView;
    private List<LatLng> latLngList;
    private FusedLocationProviderClient fusedLocationClient;
    private boolean isTracking = false;

    int startTime = 0;

    boolean isPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return false;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        button = view.findViewById(R.id.btn_start);
        textView = view.findViewById(R.id.tv_time);
        button.setOnClickListener(v -> toggleTracking());
        return view;
    }

    private void toggleTracking() {
        if(isPermissionGranted())
            if (!isTracking) {
                startTracking();
            } else {
                stopTracking();
            }
        else
            Toast.makeText(this.getContext(), "Vui lòng cấp quyền cho ứng dụng", Toast.LENGTH_SHORT).show();
    }

    private void startTracking() {
        googleMap.clear();
        latLngList = new ArrayList<>();
        button.setText("Stop");
        startTime = 0;
        textView.setText("0");
        isTracking = true;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                requireActivity().runOnUiThread(() -> {
                    startTime++;
                    fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            latLngList.add(latLng);
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
                            PolylineOptions polylineOptions = new PolylineOptions().color(Color.RED).width(25).addAll(latLngList);
                            googleMap.addPolyline(polylineOptions);
                        }
                    });
                    int minutes = startTime / 60; // Số phút
                    int seconds = startTime % 60; // Số giây
                    String formattedTime = String.format("%02d:%02d", minutes, seconds);
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    String formattedDistance = decimalFormat.format(calculateDistance(latLngList)) + "m";
                    textView.setText(formattedTime + "\n" + formattedDistance);
                });

            }
        }, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTracking();
    }

    private void stopTracking() {
        if (timer != null) {
            timer.cancel();
        }
        button.setText("Start");
        isTracking = false;
    }

    private double calculateDistance(List<LatLng> latLngs) {
        double totalDistance = 0;
        if (latLngs.size() >= 2) {
            for (int i = 0; i < latLngs.size() - 1; i++) {
                LatLng startPoint = latLngs.get(i);
                LatLng endPoint = latLngs.get(i + 1);
                float[] results = new float[1];
                Location.distanceBetween(startPoint.latitude, startPoint.longitude, endPoint.latitude, endPoint.longitude, results);
                totalDistance += results[0];
            }
        }
        return totalDistance;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        if (isPermissionGranted()) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 19));
                    }
                }
            });
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull @NotNull GoogleMap gMap) {
        googleMap = gMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.setBuildingsEnabled(true);
        getCurrentLocation();
    }

}
