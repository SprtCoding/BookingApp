package com.sprtcoding.okidoc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PatientProfileFragment extends Fragment {
    View v;
    private MaterialButton logoutBtn, editBtn;
    private FirebaseDatabase mDB;
    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private TextView name, address, gender, birth, phone, email, status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_patient_profile, container, false);

        initiate();
        mAuth = FirebaseAuth.getInstance();

        mDB = FirebaseDatabase.getInstance();
        userRef = mDB.getReference("Users");

        if(mAuth.getCurrentUser() != null) {
            userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        String fname = snapshot.child("Fullname").getValue(String.class);
                        String addrss = snapshot.child("Address").getValue(String.class);
                        String gndr = snapshot.child("Gender").getValue(String.class);
                        String bday = snapshot.child("DateOfBirth").getValue(String.class);
                        String phn = snapshot.child("Phone").getValue(String.class);
                        String mail = snapshot.child("Email").getValue(String.class);
                        String stat = snapshot.child("Status").getValue(String.class);

                        name.setText(fname);
                        address.setText(addrss);
                        gender.setText(gndr);
                        birth.setText(bday);
                        phone.setText(phn);
                        email.setText(mail);
                        status.setText(stat);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

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
        editBtn = v.findViewById(R.id.editBtn);
        name = v.findViewById(R.id.name);
        address = v.findViewById(R.id.address);
        gender = v.findViewById(R.id.gender);
        birth = v.findViewById(R.id.birth);
        phone = v.findViewById(R.id.phone);
        email = v.findViewById(R.id.email);
        status = v.findViewById(R.id.status);
    }
}