package com.sprtcoding.okidoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class PatientProfileFragment extends Fragment {
    View v;
    private MaterialButton logoutBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        initiate();
        mAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(getContext());
        loadingBar.setTitle("Signing Out");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);

        logoutBtn.setOnClickListener(view -> {
            loadingBar.show();
            mAuth.signOut();
            Intent i = new Intent(getContext(), login_activity.class);
            startActivity(i);
        });

        return v;
    }

    private void initiate() {
        logoutBtn = v.findViewById(R.id.logoutBtn);
    }
}