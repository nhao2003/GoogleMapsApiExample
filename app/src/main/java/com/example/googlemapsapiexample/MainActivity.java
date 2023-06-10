package com.example.googlemapsapiexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.googlemapsapiexample.Fragments.StreetView;
import com.example.googlemapsapiexample.Fragments.Home;
import com.example.googlemapsapiexample.Fragments.StaticMapView;
import com.example.googlemapsapiexample.Fragments.Tracking;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity{

    StreetView streetView;
    Home home;
    StaticMapView staticMapView;
    Tracking tracking;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        streetView = new StreetView();
        home = new Home();
        staticMapView = new StaticMapView();
        tracking = new Tracking();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_fragment_1:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, home)
                            .commit();
                    break;
                case R.id.item_fragment_2:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, tracking)
                            .commit();
                    break;
                case R.id.item_fragment_3:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, staticMapView)
                            .commit();
                    break;
                case R.id.item_fragment_4:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, streetView)
                            .commit();
                    break;
            }
            return true;
        });
        // init first fragment
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt("some_int", 0);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.flFragment, Home.class, bundle)
                    .commit();
        }
    }


}
