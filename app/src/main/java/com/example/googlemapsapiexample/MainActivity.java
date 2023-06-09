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

    Home home;
    MapsFragment mapsFragment;
    Routing routing;
    Tracking tracking;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        home = new Home();
        mapsFragment = new MapsFragment();
        routing = new Routing();
        tracking = new Tracking();
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.item_fragment_1:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, mapsFragment)
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
                            .replace(R.id.flFragment, routing)
                            .commit();
                    break;
                case R.id.item_fragment_4:
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.flFragment, home)
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
                    .add(R.id.flFragment, MapsFragment.class, bundle)
                    .commit();
        }
    }


}
