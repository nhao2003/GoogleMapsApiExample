package com.example.googlemapsapiexample.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import org.jetbrains.annotations.NotNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Routing extends Fragment {

    private GoogleMap map;
    private LatLng fkip;
    private LatLng monas;
    final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull @NotNull GoogleMap googleMap) {
            map = googleMap;

            MarkerOptions markerFkip = new MarkerOptions()
                    .position(fkip)
                    .title("FKIP");
            MarkerOptions markerMonas = new MarkerOptions()
                    .position(monas)
                    .title("Monas");

            map.addMarker(markerFkip);
            map.addMarker(markerMonas);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(monas, 11.6f));

            String fromFKIP = fkip.latitude + "," +     fkip.longitude;
            String toMonas = monas.latitude + "," + monas.longitude;

            ApiServices apiServices = RetrofitClient.apiServices(requireContext());
            apiServices.getDirection(fromFKIP, toMonas, "AIzaSyBg8RtAbXyLYcRpQOe2KPwCuNNvW-Rrq70")
                    .enqueue(new Callback<DirectionResponses>() {//
                        @Override
                        public void onResponse(@NonNull Call<DirectionResponses> call, @NonNull Response<DirectionResponses> response) {
                            drawPolyline(response);
                            System.out.println("======================");
                            System.out.println("bisa dong oke" + response.message());
                            System.out.println(response.body());
                        }

                        @Override
                        public void onFailure(@NonNull Call<DirectionResponses> call, @NonNull Throwable t) {
                            System.out.println("********************");
                            System.out.println(t.getMessage());
                            System.out.println(t.getLocalizedMessage());
                            t.printStackTrace();
                        }
                    });
        }
    };


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fkip = new LatLng(-6.3037978, 106.8693713);
        monas = new LatLng(-6.1890511, 106.8251573);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_view);
        if (mapFragment != null) {
            System.out.println("**********************************************A");
            mapFragment.getMapAsync(callback);
        } else {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_routing, container, false);
    }


    private void drawPolyline(@NonNull Response<DirectionResponses> response) {
        if (response.body() != null && !response.body().getRoutes().isEmpty()) {
            String shape = response.body().getRoutes().get(0).getOverviewPolyline().getPoints();
            PolylineOptions polyline = new PolylineOptions()
                    .addAll(PolyUtil.decode(shape))
                    .width(8f)
                    .color(Color.RED);
            map.addPolyline(polyline);
        }
    }

    private interface ApiServices {
        @GET("maps/api/directions/json")
        Call<DirectionResponses> getDirection(@Query("origin") String origin,
                                              @Query("destination") String destination,
                                              @Query("key") String apiKey);
    }

    private static class RetrofitClient {
        static ApiServices apiServices(Context context) {
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .build();

            return retrofit.create(ApiServices.class);
        }
    }
}
