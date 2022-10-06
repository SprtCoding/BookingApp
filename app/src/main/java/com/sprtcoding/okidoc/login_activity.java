package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login_activity extends AppCompatActivity {
    private TextInputEditText emailET, passwordET;
    private MaterialButton signupBtn, loginBtn;
    private ProgressDialog loadingBar, loginLoading;
    private FirebaseAuth mAuth;
    private FirebaseUser mUSer;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initiate();
        loading();
        loginLoadingBar();
        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        //firebase auth
        mAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(view -> {
            loadingBar.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent i = new Intent(this, register_activity.class);
                startActivity(i);
                finish();
            }, 3000);
        });

        loginBtn.setOnClickListener(view -> {
            loginLoading.show();
            String email = emailET.getText().toString().trim();
            String pass = passwordET.getText().toString().trim();
            if(TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Email is empty.", Toast.LENGTH_SHORT).show();
                loginLoading.dismiss();
            }else if(TextUtils.isEmpty(pass)) {
                Toast.makeText(this, "Password is empty.", Toast.LENGTH_SHORT).show();
                loginLoading.dismiss();
            }else {
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                mUSer = mAuth.getCurrentUser();
                                assert mUSer != null;
                                userRef.child(mUSer.getUid()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.exists()) {
                                            loginLoading.dismiss();
                                            String stat = snapshot.child("Status").getValue(String.class);
                                            assert stat != null;
                                            if(stat.equals("Doctor")) {
                                                Intent i = new Intent(login_activity.this, MainActivity.class);
                                                startActivity(i);
                                                finish();
                                            }else if(stat.equals("Patient")) {
                                                Intent i = new Intent(login_activity.this, patient_dashboard.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(login_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
            }
        });
    }

    private void loginLoadingBar() {
        loginLoading = new ProgressDialog(login_activity.this);
        loginLoading.setTitle("Logging In");
        loginLoading.setMessage("Please wait...");
        loginLoading.setCanceledOnTouchOutside(true);
    }

    private void loading() {
        loadingBar = new ProgressDialog(login_activity.this);
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);
    }

    private void initiate() {
        signupBtn = findViewById(R.id.signupBtn);
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn = findViewById(R.id.loginBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}