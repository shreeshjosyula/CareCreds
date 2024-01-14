package com.example.carecreds.Activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.example.carecreds.Activity.ui.OpenVolunteerJobs.OpenVolunteerJobsFragment;
import com.example.carecreds.Activity.ui.Services.ServicesFragment;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.carecreds.databinding.ActivityHomeVolunteerBinding;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeVolunteerActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;
    private ActivityHomeVolunteerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeVolunteerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F4DFC8")));
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_open_jobs, R.id.navigation_requests, R.id.navigation_enrolled_jobs, R.id.navigation_care_cred_points)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_volunteer);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Volunteer")
                .document(SharedPrefsUtil.getString(this, "email", ""))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().get("fullName") != null) {
                            String fullName = task.getResult().get("fullName").toString();
                            SharedPrefsUtil.saveString(this, "fullName", fullName);
                            // rating
                            String rating = task.getResult().get("rating").toString();
                            SharedPrefsUtil.saveString(this, "rating", rating);
                        }
                    }
                });

        navView.setOnItemSelectedListener(item -> {
            int destinationId = item.getItemId();
            if (destinationId == R.id.navigation_open_jobs ||
                    destinationId == R.id.navigation_requests ||
                    destinationId == R.id.navigation_enrolled_jobs ||
                    destinationId == R.id.navigation_care_cred_points) {

                navController.popBackStack(destinationId, false);
                navController.navigate(destinationId);

                return true;
            }
            return false;
        });
    }

    @Override
    public void onBackPressed() {

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home_volunteer);
        NavDestination currentDestination = navController.getCurrentDestination();

        if (currentDestination != null && currentDestination.getId() == R.id.navigation_open_jobs) {
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