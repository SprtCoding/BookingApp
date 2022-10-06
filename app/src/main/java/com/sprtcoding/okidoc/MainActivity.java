package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bnv;
    doc_about_Fragment docAboutFragment = new doc_about_Fragment();
    doc_notif_Fragment docNotifFragment = new doc_notif_Fragment();
    doc_message_Fragment docMessageFragment = new doc_message_Fragment();
    doctor_home_Fragment doctorHomeFragment = new doctor_home_Fragment();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initiate();

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, doctorHomeFragment).commit();

        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, doctorHomeFragment).commit();
                    return true;
                case R.id.notification:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, docNotifFragment).commit();
                    return true;
                case R.id.message:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, docMessageFragment).commit();
                    return true;
                case R.id.about:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, docAboutFragment).commit();
                    return true;
            }
            return false;
        });
    }

    private void initiate() {
        bnv = findViewById(R.id.bottom_nav);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}