package com.sprtcoding.okidoc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class register_activity extends AppCompatActivity {
    private TextInputEditText fullNameET, addressET, phoneET, genderET, birthET, emailET,
            passwordET, conPasswordET;
    private MaterialButton loginBtn, signUpBtn;
    private ProgressDialog loadingBar, signupLoading;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initiate();

        //firebase auth
        mAuth = FirebaseAuth.getInstance();
        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");

        signupLoading = new ProgressDialog(register_activity.this);
        signupLoading.setTitle("Creating Account");
        signupLoading.setMessage("Please wait...");
        signupLoading.setCanceledOnTouchOutside(true);

        loadingBar = new ProgressDialog(register_activity.this);
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);

        loginBtn.setOnClickListener(view -> {
            loadingBar.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent i = new Intent(this, login_activity.class);
                startActivity(i);
                finish();
            }, 3000);
        });

        signUpBtn.setOnClickListener(view -> {
            signupLoading.show();
            String name = fullNameET.getText().toString();
            String address = addressET.getText().toString();
            String phone = phoneET.getText().toString();
            String gender = genderET.getText().toString();
            String birth = birthET.getText().toString();
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString();
            String conPassword = conPasswordET.getText().toString();

            if(TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(address)) {
                Toast.makeText(this, "Address is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Phone Number is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(gender)) {
                Toast.makeText(this, "Gender is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(birth)) {
                Toast.makeText(this, "Date of Birth is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Password is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else if(TextUtils.isEmpty(conPassword)) {
                Toast.makeText(this, "Confirm Password is empty.", Toast.LENGTH_SHORT).show();
                signupLoading.dismiss();
            }else {
                if(password.equals(conPassword)) {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    FirebaseUser mUser = mAuth.getCurrentUser();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    assert mUser != null;
                                    hashMap.put("UID", mUser.getUid());
                                    hashMap.put("Fullname", name);
                                    hashMap.put("Address", address);
                                    hashMap.put("Phone", phone);
                                    hashMap.put("Gender", gender);
                                    hashMap.put("DateOfBirth", birth);
                                    hashMap.put("Email", email);
                                    hashMap.put("Status", "Patient");

                                    userRef.child(mUser.getUid()).setValue(hashMap)
                                            .addOnCompleteListener(task1 -> {
                                                signupLoading.dismiss();
                                                if(task1.isSuccessful()) {
                                                    Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(this, patient_dashboard.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            });
                                }
                    });
                }else {
                    Toast.makeText(this, "Password not match! Try Again.", Toast.LENGTH_SHORT).show();
                    signupLoading.dismiss();
                }
            }
        });
    }

    private void initiate() {
        loginBtn = findViewById(R.id.loginBtn);
        signUpBtn = findViewById(R.id.signUpBtn);
        conPasswordET = findViewById(R.id.conPasswordET);
        passwordET = findViewById(R.id.passwordET);
        emailET = findViewById(R.id.emailET);
        birthET = findViewById(R.id.birthET);
        genderET = findViewById(R.id.genderET);
        phoneET = findViewById(R.id.phoneET);
        addressET = findViewById(R.id.addressET);
        fullNameET = findViewById(R.id.fullNameET);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}