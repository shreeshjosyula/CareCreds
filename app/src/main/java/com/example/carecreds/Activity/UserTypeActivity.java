package com.example.carecreds.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        getSupportActionBar().hide();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Button volunteerFormButton = findViewById(R.id.volunteerButton);
        Button clientFormButton = findViewById(R.id.clientButton);
        String email = SharedPrefsUtil.getString(getApplicationContext(), "email", "");
        volunteerFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("accountType", "volunteer");
                db.collection("userType").document(email).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(UserTypeActivity.this, VolunteerDetailFormActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Error creating account");
                        });
            }
        });

        clientFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("accountType", "client");
                db.collection("userType").document(email).set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(UserTypeActivity.this, RegistrationActivity.class);
                            startActivity(intent);
                        })
                        .addOnFailureListener(e -> {
                            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Error creating account");
                        });
            }
        });

    }
}