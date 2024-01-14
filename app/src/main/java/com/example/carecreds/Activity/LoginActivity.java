package com.example.carecreds.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        removeStatusBar();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        rememberMeCheckBox = findViewById(R.id.rememberMeCheckBox);
        rememberMeCheckBox.setVisibility(View.GONE);
    }

    private void setClickListeners() {
        findViewById(R.id.SignUpPageNav).setOnClickListener(v -> openSignUpPage());
        findViewById(R.id.loginButton).setOnClickListener(v -> loginUser());
        findViewById(R.id.forgotPasswordTextView).setOnClickListener(v -> showPasswordResetToast());
    }

    private void openSignUpPage() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (validateInputs(email, password)) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            checkUserTypeAndProceed(email);
                        } else {
                            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Error occurred! Contact Us!");
                        }
                    });
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Email is required");
            return false;
        }

        if (!isValidEmail(email)) {
            emailEditText.setError("Invalid email format");
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please enter a valid email address!");
            return false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Password is required") ;
            return false;
        }

        return true;
    }

    private void checkUserTypeAndProceed(String email) {
            db.collection("userType").document(email).get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    if (task1.getResult().get("accountType") == null) {
                        SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please complete your profile!");
                        SharedPrefsUtil.saveString(LoginActivity.this, "email", email);
                        Intent intent = new Intent(LoginActivity.this, UserTypeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    if (task1.getResult().get("accountType").equals("client")) {
                        checkClientDetails("Client", email);
                    } else {
                        checkClientDetails("Volunteer", email);
                    }
                }
            });
    }

    private void checkClientDetails(String CollectionName, String email) {
        db.collection(CollectionName).document(email).get().addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                SharedPrefsUtil.saveString(LoginActivity.this, "email", email);
                if (task2.getResult().get("fullName") == null) {
                    SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Please complete your profile!");
                    if (CollectionName.equals("Client")) {
                        Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(LoginActivity.this, VolunteerDetailFormActivity.class);
                        startActivity(intent);
                        finish();
                    } ;
                } else {
                    handleSuccessfulLogin(CollectionName, email);
                }
            } else {
                SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Wrong email or password!");
            }
        });
    }

    private void handleSuccessfulLogin(String CollectionName, String email) {
        if (rememberMeCheckBox.isChecked()) {
            SharedPrefsUtil.saveBoolean(LoginActivity.this, "rememberMe", true);
        }

        if (CollectionName.equals("Client")) {
            SharedPrefsUtil.saveString(LoginActivity.this, "userType", "client");
            Intent intent = new Intent(LoginActivity.this, HomeClientActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPrefsUtil.saveString(LoginActivity.this, "userType", "volunteer");
            Intent intent = new Intent(LoginActivity.this, HomeVolunteerActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void showPasswordResetToast() {
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Email is required!");
            return;
        }
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Password reset email sent to " + email);
                        } else {
                            SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "Failed to send reset email. Check your email or try again later.");
                        }
                    }
                });
    }

    private boolean isValidEmail(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void removeStatusBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}

