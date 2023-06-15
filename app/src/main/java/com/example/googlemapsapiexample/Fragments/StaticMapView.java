package com.example.googlemapsapiexample.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.example.googlemapsapiexample.MapPicker;
import com.example.googlemapsapiexample.R;
import com.google.android.gms.maps.model.LatLng;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class StaticMapView extends Fragment {
    private ImageView imageView;
    private TextView addressTextView, locationTextView;
    private Button pickLocationButton;
    private Spinner mapTypeSpinner, zoomSpinner;
    private static final String ROADMAP = "Roadmap";
    private static final String SATELLITE = "Satellite";
    private static final String TERRAIN = "Terrain";
    private static final String HYBRID = "Hybrid";
    private static final String[] ZOOM_LEVELS = {"1: World", "2", "3", "4", "5: Landmass/continent", "6", "7", "8", "9", "10: City", "11", "12", "13", "14", "15: Streets", "16", "17", "18", "19", "20: Buildings", "21"};
    private static final String[] MAP_TYPES = {ROADMAP, SATELLITE, TERRAIN, HYBRID};

    ActivityResultLauncher<Intent> mapPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    LatLng selectedLatLng = result.getData().getParcelableExtra("selectedLatLng");
                    int zoom = zoomSpinner.getSelectedItemPosition() + 1;
                    String mapType = mapTypeSpinner.getSelectedItem().toString().toLowerCase();
                    int height = imageView.getHeight();
                    int width = imageView.getWidth();

                    Geocoder geocoder = new Geocoder(requireContext());
                    ArrayList<Address> addresses = null;

                    try {
                        addresses = (ArrayList<Address>) geocoder.getFromLocation(selectedLatLng.latitude, selectedLatLng.longitude, 1);
                        addressTextView.setText(addresses.get(0).getAddressLine(0));
                        locationTextView.setText("Tọa độ: " + selectedLatLng.latitude + ", " + selectedLatLng.longitude);

                        String staticMapUrl = getMapStaticUrl(selectedLatLng, zoom, height, width, mapType);
                        System.out.println(staticMapUrl);
                        Glide.with(this)
                                .load(staticMapUrl)
                                .placeholder(R.drawable.ic_launcher_background)
                                .fitCenter()
                                .into(imageView);
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Lỗi: Không xác định được vị trí", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }



                }
            });

    private String getMapStaticUrl(LatLng latLng, int zoom, int height, int width, String mapType) {
        Uri.Builder builder = Uri.parse("https://maps.googleapis.com/maps/api/staticmap?").buildUpon();
        builder.appendQueryParameter("center", latLng.latitude + "," + latLng.longitude)
                .appendQueryParameter("zoom", String.valueOf(zoom))
                .appendQueryParameter("size", width + "x" + height)
                .appendQueryParameter("maptype", mapType)
                .appendQueryParameter("markers", "color:" + "red" + "|" + latLng.latitude + "," + latLng.longitude)
                .appendQueryParameter("key", getString(R.string.api_key));
        return builder.build().toString();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView = view.findViewById(R.id.imageView);
        addressTextView = view.findViewById(R.id.tv_address);
        locationTextView = view.findViewById(R.id.tv_location);
        pickLocationButton = view.findViewById(R.id.btn_pick);
        mapTypeSpinner = view.findViewById(R.id.mapTypesSpin);
        ArrayAdapter<String> mapTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, MAP_TYPES);
        mapTypeSpinner.setAdapter(mapTypeAdapter);
        zoomSpinner = view.findViewById(R.id.zoomSpin);
        ArrayAdapter<String> zoomAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, ZOOM_LEVELS);
        zoomSpinner.setAdapter(zoomAdapter);
        zoomSpinner.setSelection(14);
        pickLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MapPicker.class);
            mapPickerLauncher.launch(intent);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routing, container, false);
    }
}
