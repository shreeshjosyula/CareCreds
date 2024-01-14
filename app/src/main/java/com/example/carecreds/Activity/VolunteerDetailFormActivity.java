package com.example.carecreds.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class VolunteerDetailFormActivity extends AppCompatActivity {

    private EditText editTextName, editTextAge, editTextAddress, editTextEmail, editTextPhone, editTextPrimaryEducation, editTextSecondaryEducation, editTextPostSecondaryEducation, editTextWorkExperience, editTextAvailability;
    private RadioGroup radioGroupGender;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_detail_form);

        db = FirebaseFirestore.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        //change status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.windowBg));

        editTextName = findViewById(R.id.editTextName);
        editTextAge = findViewById(R.id.editTextAge);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPrimaryEducation = findViewById(R.id.editTextPrimaryEducation);
        editTextSecondaryEducation = findViewById(R.id.editTextSecondaryEducation);
        editTextPostSecondaryEducation = findViewById(R.id.editTextPostSecondaryEducation);
        editTextWorkExperience = findViewById(R.id.editTextWorkExperience);
        editTextAvailability = findViewById(R.id.editTextAvailability);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);
        String emailStr = SharedPrefsUtil.getString(this, "email", "");
        if (!TextUtils.isEmpty(emailStr)) {
            editTextEmail.setText(emailStr);
            editTextEmail.setEnabled(false);
        }
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getBooleanExtra("edit", false)) {
                db.collection("Volunteer").document(SharedPrefsUtil.getString(this, "email", ""))
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot document = task.getResult();
                                document.getData();
                                editTextName.setText(document.get("fullName").toString());
                                editTextAge.setText(document.get("age").toString());
                                editTextAddress.setText(document.get("address").toString());
                                editTextEmail.setText(document.get("email").toString());
                                editTextPhone.setText(document.get("phone").toString());
                                editTextPrimaryEducation.setText(document.get("primaryEducation").toString());
                                editTextSecondaryEducation.setText(document.get("secondaryEducation").toString());
                                editTextPostSecondaryEducation.setText(document.get("postSecondaryEducation").toString());
                                editTextWorkExperience.setText(document.get("workExperience").toString());
                                editTextAvailability.setText(document.get("availability").toString());
                                String gender = document.get("gender").toString();
                                RadioButton male = findViewById(R.id.radioButtonMale);
                                RadioButton female = findViewById(R.id.radioButtonFemale);
                                RadioButton other = findViewById(R.id.radioButtonOthers);
                                if (gender.equals("Male")){
                                    male.setChecked(true);
                                } else if (gender.equals("Female")) {
                                    female.setChecked(true);
                                } else {
                                    other.setChecked(true);
                                }
                            }
                        });
                buttonSubmit.setText("Update");
            }
        }

        radioGroupGender = findViewById(R.id.radioGroupGender);


        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonSubmit.getText().toString().equals("Update")){
                                    db.collection("Volunteer").document(SharedPrefsUtil.getString(VolunteerDetailFormActivity.this, "email",""))
                        .update("fullName", editTextName.getText().toString().trim(),
                                "age", Integer.parseInt(editTextAge.getText().toString().trim()),
                                "gender", ((RadioButton)findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString().trim(),
                                "address", editTextAddress.getText().toString().trim(),
                                "phone", editTextPhone.getText().toString().trim(),
                                "primaryEducation", editTextPrimaryEducation.getText().toString().trim(),
                                "secondaryEducation", editTextSecondaryEducation.getText().toString().trim(),
                                "postSecondaryEducation", editTextPostSecondaryEducation.getText().toString().trim(),
                                "workExperience", editTextWorkExperience.getText().toString().trim(),
                                "availability", editTextAvailability.getText().toString().trim()
                        )
                        .addOnSuccessListener(aVoid -> {
                            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Form updated successfully!");
                            Intent intent1 = new Intent(VolunteerDetailFormActivity.this, HomeVolunteerActivity.class);
                            startActivity(intent1);
                        });
                } else {
                    submitForm();
                }
            }
        });
    }

    private void submitForm() {
        String name = editTextName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String primaryEducation = editTextPrimaryEducation.getText().toString().trim();
        String secondaryEducation = editTextSecondaryEducation.getText().toString().trim();
        String postSecondaryEducation = editTextPostSecondaryEducation.getText().toString().trim();
        String workExperience = editTextWorkExperience.getText().toString().trim();
        String availability = editTextAvailability.getText().toString().trim();

        RadioButton selectedGenderRadioButton = findViewById(radioGroupGender.getCheckedRadioButtonId());
        String gender = selectedGenderRadioButton != null ? selectedGenderRadioButton.getText().toString() : "";

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age) || TextUtils.isEmpty(address) || TextUtils.isEmpty(email) || TextUtils.isEmpty(availability)) {
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please fill in all required fields!");
            return;
        }

        int userAge = Integer.parseInt(age);
        if (userAge <= 0 || userAge > 120) {
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please enter a valid age!");
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("fullName", name);
        userData.put("age", userAge);
        userData.put("address", address);
        userData.put("email", email);
        userData.put("phone", phone);
        userData.put("gender", gender);
        userData.put("primaryEducation", primaryEducation);
        userData.put("secondaryEducation", secondaryEducation);
        userData.put("postSecondaryEducation", postSecondaryEducation);
        userData.put("workExperience", workExperience);
        userData.put("availability", availability);
        userData.put("rating", 0);

        db.collection("Volunteer")
                .document(SharedPrefsUtil.getString(this, "email", ""))
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("points", 0);

                    db.collection("CareCred")
                            .document(SharedPrefsUtil.getString(this, "email", ""))
                            .set(data);
                    SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Form submitted successfully!");
                    SharedPrefsUtil.saveString(this, "userType", "volunteer");
                    Intent intent = new Intent(VolunteerDetailFormActivity.this, HomeVolunteerActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Error occurred! Contact Us!"));
    }
}