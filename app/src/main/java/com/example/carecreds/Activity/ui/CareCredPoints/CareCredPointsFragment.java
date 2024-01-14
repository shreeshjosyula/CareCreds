package com.example.carecreds.Activity.ui.CareCredPoints;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carecreds.Activity.LoginActivity;
import com.example.carecreds.Activity.VolunteerDetailFormActivity;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class CareCredPointsFragment extends Fragment {
    private FirebaseAuth mAuth;
    public CareCredPointsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View careCredPointsView = inflater.inflate(R.layout.fragment_care_cred_points, container, false);
        TextView care_cred_points = careCredPointsView.findViewById(R.id.care_cred_points);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("CareCred")
                .document(SharedPrefsUtil.getString(requireContext(), "email", ""))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        care_cred_points.setText("Current CareCred Points: "+String.valueOf(task.getResult().get("points")));
                    }
                });
        return careCredPointsView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.volunteer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_volunteer) {
            // Handle sign out
            mAuth.signOut();
            SharedPrefsUtil.clearAll(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return true;
        } else if (item.getItemId() == R.id.edit_profile_volunteer){
            Intent intent = new Intent(requireContext(), VolunteerDetailFormActivity.class);
            intent.putExtra("edit", true);
            startActivity(intent);
        }
        // Handle other menu items if needed
        return super.onOptionsItemSelected(item);
    }

}