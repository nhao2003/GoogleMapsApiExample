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
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Objects;
import java.util.Random;

public class Home extends Fragment implements
        OnStreetViewPanoramaReadyCallback {  // PanoramaReadyCallback to use StreetViewPanorama.
    private static final LatLng UIT_TIME_SQUARE = new LatLng(10.87007575745032, 106.80306452005529); // sample input.
    private StreetViewPanorama mStreetViewPanorama; // the object that handle panorama preview.

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportStreetViewPanoramaFragment panoramaFragment =
                (SupportStreetViewPanoramaFragment) getChildFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        panoramaFragment.getStreetViewPanoramaAsync(this); // call this to use the fragment.
    }
    /**
     * after panorama get ready, you can declare the position.
     */
    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        mStreetViewPanorama.setPosition(UIT_TIME_SQUARE); // where the street view will be shown.
        /** you can control the inputs into street view */
        mStreetViewPanorama.setUserNavigationEnabled(true);
        mStreetViewPanorama.setPanningGesturesEnabled(true);
        mStreetViewPanorama.setZoomGesturesEnabled(true);
    }
}
