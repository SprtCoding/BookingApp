package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashScreen extends AppCompatActivity {
    private FirebaseUser mUSer;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef, tokenRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mAuth = FirebaseAuth.getInstance();
        mUSer = mAuth.getCurrentUser();
        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        tokenRef = mDb.getReference("UserToken");
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        if(mUSer != null) {
            userRef.child(mUSer.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        String stat = snapshot.child("Status").getValue(String.class);
                        assert stat != null;
                        if(stat.equals("Doctor")) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(task1 -> {
                                            if (!task1.isSuccessful()) {
                                                Toast.makeText(SplashScreen.this, "Fetching FCM registration token failed" + task1.getException(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // Get new FCM registration token
                                            String token = task1.getResult();
                                            tokenRef.child(mUSer.getUid()).child("token").setValue(token);
                                        });
                                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }, 3000);
                        }else if(stat.equals("Patient")) {
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                FirebaseMessaging.getInstance().getToken()
                                        .addOnCompleteListener(task1 -> {
                                            if (!task1.isSuccessful()) {
                                                Toast.makeText(SplashScreen.this, "Fetching FCM registration token failed" + task1.getException(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // Get new FCM registration token
                                            String token = task1.getResult();
                                            tokenRef.child(mUSer.getUid()).child("token").setValue(token);
                                        });
                                Intent i = new Intent(SplashScreen.this, patient_dashboard.class);
                                startActivity(i);
                                finish();
                            }, 3000);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SplashScreen.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            Intent i = new Intent(this, login_activity.class);
            startActivity(i);
            finish();
        }
    }

}