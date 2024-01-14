package com.example.carecreds.Activity.ui.EditProfile;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.carecreds.Activity.LoginActivity;
import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EditClientProfileFragment extends Fragment {

    private EditText etFullName, etEmail, etPhoneNumber, etDateOfBirth, etAddress;
    private RadioGroup radioGroupGender;
    private EditText etEmergencyContact, etMedicalHistory, etDietaryRestrictions;
    private EditText etLanguagePreferences, etInterestsAndHobbies, etMedicalInsurance;
    private EditText etTransportationNeeds, etPreferredTimeForAssistance;
    private Button btnUpdate;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public EditClientProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.activity_registration, container, false);
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(requireContext(), R.style.inputStyle);
        LayoutInflater themedInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = themedInflater.inflate(R.layout.activity_registration, container, false);

        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etPhoneNumber = view.findViewById(R.id.etPhoneNumber);
        etDateOfBirth = view.findViewById(R.id.etDateOfBirth);
        etAddress = view.findViewById(R.id.etAddress);
        radioGroupGender = view.findViewById(R.id.radioGroupGender);
        etEmergencyContact = view.findViewById(R.id.etEmergencyContact);
        etMedicalHistory = view.findViewById(R.id.etMedicalHistory);
        etDietaryRestrictions = view.findViewById(R.id.etDietaryRestrictions);
        etLanguagePreferences = view.findViewById(R.id.etLanguagePreferences);
        etInterestsAndHobbies = view.findViewById(R.id.etInterestsAndHobbies);
        etMedicalInsurance = view.findViewById(R.id.etMedicalInsurance);
        etTransportationNeeds = view.findViewById(R.id.etTransportationNeeds);
        etPreferredTimeForAssistance = view.findViewById(R.id.etPreferredTimeForAssistance);
        btnUpdate = view.findViewById(R.id.btnRegister);
        btnUpdate.setText("Update");
        etEmail.setEnabled(false);

        // Fetch user data from Firestore and populate the fields
        fetchUserData();

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserData(view);
            }
        });

        return view;
    }

    private void fetchUserData() {
        String userId = SharedPrefsUtil.getString(requireContext(), "email", "");
        db.collection("Client").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Retrieve user data and populate fields
                                etFullName.setText(document.getString("fullName"));
                                etEmail.setText(document.getString("email"));
                                etPhoneNumber.setText(document.getString("phoneNumber"));
                                etDateOfBirth.setText(document.getString("dateOfBirth"));
                                etAddress.setText(document.getString("address"));
                                etEmergencyContact.setText(document.getString("emergencyContact"));
                                etMedicalHistory.setText(document.getString("medicalHistory"));
                                etDietaryRestrictions.setText(document.getString("dietaryRestrictions"));
                                etLanguagePreferences.setText(document.getString("languagePreferences"));
                                etInterestsAndHobbies.setText(document.getString("interestsAndHobbies"));
                                etMedicalInsurance.setText(document.getString("medicalInsurance"));
                                etTransportationNeeds.setText(document.getString("transportationNeeds"));
                                etPreferredTimeForAssistance.setText(document.getString("preferredTimeForAssistance"));

                                // Gender RadioButton selection based on retrieved data
                                String gender = document.getString("gender");
                                if (gender != null) {
                                    if (gender.equals("Male")) {
                                        radioGroupGender.check(R.id.radioButtonMale);
                                    } else if (gender.equals("Female")) {
                                        radioGroupGender.check(R.id.radioButtonFemale);
                                    } else if (gender.equals("Others")) {
                                        radioGroupGender.check(R.id.radioButtonOthers);
                                    }
                                }
                            } else {
                                SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "No such document");
                            }
                        } else {
                            SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Failed to fetch data");
                        }
                    }
                });

        etDateOfBirth.setEnabled(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit_profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out) {
            // Handle sign out
            mAuth.signOut();
            SharedPrefsUtil.clearAll(requireContext());
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return true;
        }
        // Handle other menu items if needed
        return super.onOptionsItemSelected(item);
    }

    private void updateUserData(View view) {
        String userId = SharedPrefsUtil.getString(requireContext(), "email", "");

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


        // Get selected gender from RadioGroup
        String gender = "";
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = radioGroupGender.findViewById(selectedId);
            gender = radioButton.getText().toString();
        }

        if (fullName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty() || dateOfBirth.isEmpty() || address.isEmpty()) {
            SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Please fill in all required fields");
            return;
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Please enter a valid email address");
            return;
        }

        db.collection("Client").document(userId)
                .update(
                        "fullName", fullName,
                        "email", email,
                        "phoneNumber", phoneNumber,
                        "dateOfBirth", dateOfBirth,
                        "address", address,
                        "gender", gender,
                        "emergencyContact", emergencyContact,
                        "medicalHistory", medicalHistory,
                        "dietaryRestrictions", dietaryRestrictions,
                        "languagePreferences", languagePreferences,
                        "interestsAndHobbies", interestsAndHobbies,
                        "medicalInsurance", medicalInsurance,
                        "transportationNeeds", transportationNeeds,
                        "preferredTimeForAssistance", preferredTimeForAssistance
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Data updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        SnackBarUtils.showCustomSnackbar(requireActivity().findViewById(android.R.id.content), "Failed to update data");
                    }
                });
    }
}
