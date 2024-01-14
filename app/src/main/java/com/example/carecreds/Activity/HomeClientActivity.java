package com.example.carecreds.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import com.example.carecreds.Activity.ui.Services.ServicesFragment;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carecreds.databinding.ActivityHomeClientBinding;

public class HomeClientActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    private ActivityHomeClientBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F4DFC8")));

        // Disable the Up button
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        binding = ActivityHomeClientBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_services, R.id.navigation_all_volunteers, R.id.navigation_enrolled_volunteers, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_client);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_client);
        NavDestination currentDestination = navController.getCurrentDestination();

        if (currentDestination != null && currentDestination.getId() == R.id.navigation_services) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please click BACK again to exit");

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            super.onBackPressed();
        }
    }






}