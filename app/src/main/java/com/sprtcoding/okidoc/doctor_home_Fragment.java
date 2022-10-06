package com.sprtcoding.okidoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class doctor_home_Fragment extends Fragment {
    View v;
    private MaterialButton logoutBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef;
    private MaterialButton bookBtn, messageBtn;
    private TextView clinicLocation, clinicName, doctorName, clinicNames, drName, textView;
    String clinicName1, address;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_doctor_home_, container, false);

        initiate();
        mAuth = FirebaseAuth.getInstance();

        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        if(mAuth.getCurrentUser() != null) {
            userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        address = snapshot.child("Address").getValue(String.class);
                        String docName = snapshot.child("Fullname").getValue(String.class);
                        if(snapshot.hasChild("ClinicName")) {
                            clinicName1 = snapshot.child("ClinicName").getValue(String.class);
                            clinicName.setText(clinicName1);
                            clinicNames.setText(clinicName1);
                        }
                        clinicLocation.setText(address);
                        doctorName.setText(docName);
                        drName.setText(docName);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

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
        clinicLocation = v.findViewById(R.id.clinicLocation);
        clinicName = v.findViewById(R.id.clinicName);
        clinicNames = v.findViewById(R.id.clinicNames);
        doctorName = v.findViewById(R.id.doctorName);
        drName = v.findViewById(R.id.drName);
        textView = v.findViewById(R.id.textView);
    }
}