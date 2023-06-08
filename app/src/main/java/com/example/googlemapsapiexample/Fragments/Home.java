package com.example.googlemapsapiexample.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextPaint;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.googlemapsapiexample.Models.DirectionResponses;
import com.example.googlemapsapiexample.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Objects;
import java.util.Random;

public class Home extends Fragment {

    int MapType = GoogleMap.MAP_TYPE_NORMAL;
    private GoogleMap map;
    private ImageView btnLayer;
    final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
            map = googleMap;
            map.getUiSettings().setMyLocationButtonEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            map.getUiSettings().setMapToolbarEnabled(true);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Vui lòng bật và cấp quyền vị trí.", Toast.LENGTH_SHORT).show();
                return;
            }
            map.setMyLocationEnabled(true);
        }
    };

    public Home() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_maps_view);
        if (mapFragment != null) {
            System.out.println("**********************************************A");
            mapFragment.getMapAsync(callback);
        } else {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }
        btnLayer = view.findViewById(R.id.btn_layer);
        btnLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapType++;
                if (MapType >= 5) MapType = 0;
                switch (MapType) {
                    case GoogleMap.MAP_TYPE_NONE:
                        Toast.makeText(requireContext(), "None", Toast.LENGTH_SHORT).show();
                        map.setMapType(GoogleMap.MAP_TYPE_NONE);
                        break;
                    case GoogleMap.MAP_TYPE_NORMAL:
                        Toast.makeText(requireContext(), "Normal", Toast.LENGTH_SHORT).show();
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case GoogleMap.MAP_TYPE_HYBRID:
                        Toast.makeText(requireContext(), "Hybrid", Toast.LENGTH_SHORT).show();
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case GoogleMap.MAP_TYPE_SATELLITE:
                        Toast.makeText(requireContext(), "Satellite", Toast.LENGTH_SHORT).show();
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case GoogleMap.MAP_TYPE_TERRAIN:
                        Toast.makeText(requireContext(), "Terrain", Toast.LENGTH_SHORT).show();
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }
}
