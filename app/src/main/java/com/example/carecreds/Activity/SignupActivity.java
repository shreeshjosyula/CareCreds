package com.example.carecreds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Patterns;
import android.widget.Toast;

import com.example.carecreds.R;
import com.example.carecreds.Utils.SharedPrefsUtil;
import com.example.carecreds.Utils.SnackBarUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText nameEditText, emailSignUpEditText, passwordSignUpEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        emailSignUpEditText = findViewById(R.id.emailSignUpEditText);
        passwordSignUpEditText = findViewById(R.id.passwordSignUpEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        TextView loginText = findViewById(R.id.LoginPageNav);
        Button signUpButton = findViewById(R.id.signUpButton);

        getSupportActionBar().hide();
        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailSignUpEditText.getText().toString().trim().isEmpty()) {
                    emailSignUpEditText.setError("Email is required");
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailSignUpEditText.getText().toString().trim()).matches()) {
                    emailSignUpEditText.setError("Invalid email format");
                    return;
                }
                if (passwordSignUpEditText.getText().toString().trim().isEmpty()) {
                    passwordSignUpEditText.setError("Password is required");
                    return;
                }
                if (confirmPasswordEditText.getText().toString().trim().isEmpty()) {
                    confirmPasswordEditText.setError("Confirmation of password is required");
                    return;
                }
                mAuth.createUserWithEmailAndPassword(emailSignUpEditText.getText().toString().trim(), passwordSignUpEditText.getText().toString().trim())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    SharedPrefsUtil.saveString(SignupActivity.this, "email", emailSignUpEditText.getText().toString().trim());
                                    SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "User created successfully");
                                    Intent intent = new Intent(SignupActivity.this, UserTypeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    SnackBarUtils.showCustomSnackbar(findViewById(android.R.id.content), "User creation failed!");
                                }
                            }
                        });

            }
        });
    }
}
