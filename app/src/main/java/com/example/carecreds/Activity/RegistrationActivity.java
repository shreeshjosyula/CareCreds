package com.example.carecreds.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etFullName, etEmail, etPhoneNumber, etDateOfBirth, etAddress,
            etEmergencyContact, etMedicalHistory, etDietaryRestrictions, etLanguagePreferences,
            etInterestsAndHobbies, etMedicalInsurance, etTransportationNeeds, etPreferredTimeForAssistance;
    private RadioGroup radioGroupGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        etAddress = findViewById(R.id.etAddress);
        etEmergencyContact = findViewById(R.id.etEmergencyContact);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
        etDietaryRestrictions = findViewById(R.id.etDietaryRestrictions);
        etLanguagePreferences = findViewById(R.id.etLanguagePreferences);
        etInterestsAndHobbies = findViewById(R.id.etInterestsAndHobbies);
        etMedicalInsurance = findViewById(R.id.etMedicalInsurance);
        etTransportationNeeds = findViewById(R.id.etTransportationNeeds);
        etPreferredTimeForAssistance = findViewById(R.id.etPreferredTimeForAssistance);

        radioGroupGender = findViewById(R.id.radioGroupGender);
        String emailStr = SharedPrefsUtil.getString(this, "email", "");
        if (!TextUtils.isEmpty(emailStr)) {
            etEmail.setText(emailStr);
            etEmail.setEnabled(false);
        }

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(view -> registerUser());
    }

    private void registerUser() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String emergencyContact = etEmergencyContact.getText().toString().trim();
        String medicalHistory = etMedicalHistory.getText().toString().trim();
        String dietaryRestrictions = etDietaryRestrictions.getText().toString().trim();
        String languagePreferences = etLanguagePreferences.getText().toString().trim();
        String interestsAndHobbies = etInterestsAndHobbies.getText().toString().trim();
        String medicalInsurance = etMedicalInsurance.getText().toString().trim();
        String transportationNeeds = etTransportationNeeds.getText().toString().trim();
        String preferredTimeForAssistance = etPreferredTimeForAssistance.getText().toString().trim();

        int selectedGenderId = radioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedGender = findViewById(selectedGenderId);
        String gender = selectedGender.getText().toString();

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty()) {
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please fill in all required fields");
            return;
        }

        if (!isValidDateFormat(dateOfBirth)) {
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Enter valid data: dd/MM/yyyy");
            return;
        }
        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", fullName);
        userData.put("email", email);
        userData.put("phoneNumber", phoneNumber);
        userData.put("gender", gender);
        userData.put("dateOfBirth", dateOfBirth);
        userData.put("address", address);
        userData.put("emergencyContact", emergencyContact);
        userData.put("medicalHistory", medicalHistory);
        userData.put("dietaryRestrictions", dietaryRestrictions);
        userData.put("languagePreferences", languagePreferences);
        userData.put("interestsAndHobbies", interestsAndHobbies);
        userData.put("medicalInsurance", medicalInsurance);
        userData.put("transportationNeeds", transportationNeeds);
        userData.put("preferredTimeForAssistance", preferredTimeForAssistance);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Client")
                .document(email)
                .set(userData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPrefsUtil.saveString(this, "email", email);
                        SharedPrefsUtil.saveString(this, "userType", "client");
                        SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Registration successful!");
                        Intent intent = new Intent(RegistrationActivity.this, HomeClientActivity.class);
                        startActivity(intent);
                    } else {
                        SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Failed to register. Please try again.");
                    }
                });
    }

    private boolean isValidDateFormat(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}

