package com.example.googlemapsapiexample.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.googlemapsapiexample.R;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import org.jetbrains.annotations.NotNull;

public class StreetView extends Fragment implements
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
        if(Home.end == null){
            Home.end = UIT_TIME_SQUARE;
        }
        mStreetViewPanorama.setPosition(Home.end); // where the street view will be shown.
        /** you can control the inputs into street view */
        mStreetViewPanorama.setUserNavigationEnabled(true);
        mStreetViewPanorama.setPanningGesturesEnabled(true);
        mStreetViewPanorama.setZoomGesturesEnabled(true);
    }
}
