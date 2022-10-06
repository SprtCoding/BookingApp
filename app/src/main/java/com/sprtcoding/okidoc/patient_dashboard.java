package com.sprtcoding.okidoc;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class patient_dashboard extends AppCompatActivity {
    private BottomNavigationView bnv;
    PatientHomeFragment patientHomeFragment = new PatientHomeFragment();
    PatientAppointmentFragment patientAppointmentFragment = new PatientAppointmentFragment();
    PatientAboutFragment patientAboutFragment = new PatientAboutFragment();
    PatientProfileFragment patientProfileFragment = new PatientProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_dashboard);
        initiate();

        getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, patientHomeFragment).commit();

        bnv.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, patientHomeFragment).commit();
                    return true;
                case R.id.appointment:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, patientAppointmentFragment).commit();
                    return true;
                case R.id.about:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, patientAboutFragment).commit();
                    return true;
                case R.id.profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.admin_frame_layout, patientProfileFragment).commit();
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