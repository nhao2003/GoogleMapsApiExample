package com.example.googlemapsapiexample;

import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.googlemapsapiexample.Fragments.Home;
import com.example.googlemapsapiexample.Fragments.MapsFragment;
import com.example.googlemapsapiexample.Fragments.Routing;
import com.example.googlemapsapiexample.Fragments.Tracking;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity{

    Home home = new Home();
    MapsFragment mapsFragment = new MapsFragment();
    Routing routing = new Routing();
    Tracking tracking = new Tracking();
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                            .replace(R.id.flFragment, routing)
                            .commit();
                    break;
                case R.id.item_fragment_3:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, tracking)
                            .commit();
                    break;
                case R.id.item_fragment_4:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, mapsFragment)
                            .commit();
                    break;
            }
            return true;
        });
    }


}
