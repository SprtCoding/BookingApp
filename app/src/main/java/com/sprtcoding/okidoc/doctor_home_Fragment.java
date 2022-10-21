package com.sprtcoding.okidoc;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.okidoc.FCM.FCMNotificationSender;

import java.util.HashMap;

public class doctor_home_Fragment extends Fragment {
    View v;
    private MaterialButton logoutBtn, setReminderBtn, viewApprovedBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar, setLoadingBar, loading;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef, reminderRef;
    private TextView clinicLocation, clinicName, doctorName, clinicNames, drName, textView;
    String clinicName1, address, reminderID, docName;
    AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_doctor_home_, container, false);

        initiate();
        mAuth = FirebaseAuth.getInstance();

        setLoadingBar = new ProgressDialog(getContext());
        setLoadingBar.setTitle("Set Reminder");
        setLoadingBar.setMessage("Please wait...");
        setLoadingBar.setCanceledOnTouchOutside(true);

        loading = new ProgressDialog(getContext());
        loading.setMessage("Please wait...");
        loading.setCanceledOnTouchOutside(true);

        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        reminderRef = mDb.getReference("Reminder");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        if(mAuth.getCurrentUser() != null) {
            userRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        address = snapshot.child("Address").getValue(String.class);
                        docName = snapshot.child("Fullname").getValue(String.class);
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

        //message
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
        builder1.setTitle("Set Reminder");

        @SuppressLint("InflateParams") View inView = getLayoutInflater().inflate(R.layout.set_reminder_dialog, null);
        TextInputEditText setReminderInput;
        MaterialButton setBtn, cancelBtn;

        //dialog message
        setReminderInput = inView.findViewById(R.id.messageET);
        setBtn = inView.findViewById(R.id.setBtn);
        cancelBtn = inView.findViewById(R.id.cancelBtn);

        cancelBtn.setOnClickListener(view -> {
            setLoadingBar.dismiss();
            alertDialog.cancel();
        });

        setBtn.setOnClickListener(view1 -> {
            setLoadingBar.show();
            String reminder = setReminderInput.getText().toString();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(TextUtils.isEmpty(reminder)) {
                    Toast.makeText(getContext(), "Please enter your reminder!", Toast.LENGTH_SHORT).show();
                    setLoadingBar.dismiss();
                }else {
                    reminderID = reminderRef.push().getKey();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("SenderName", docName);
                    hashMap.put("Reminder", reminder);
                    hashMap.put("ID", reminderID);
                    reminderRef.child(mAuth.getCurrentUser().getUid()).child(docName).updateChildren(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            sendReminderNotification(UserID, DocName);
                            alertDialog.cancel();
                            setLoadingBar.dismiss();
                            Toast.makeText(getContext(), "Reminder set successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 3000);
        });

        builder1.setView(inView);
        alertDialog = builder1.create();

        setReminderBtn.setOnClickListener(view -> {
            alertDialog.show();
        });

        viewApprovedBtn.setOnClickListener(view -> {
            loading.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                Intent i = new Intent(getContext(), accepted_patient_list.class);
                startActivity(i);
                loading.dismiss();
            }, 3000);
        });

        return v;
    }

    private void initiate() {
        setReminderBtn = v.findViewById(R.id.setReminderBtn);
        viewApprovedBtn = v.findViewById(R.id.viewApprovedBtn);
        logoutBtn = v.findViewById(R.id.logoutBtn);
        clinicLocation = v.findViewById(R.id.clinicLocation);
        clinicName = v.findViewById(R.id.clinicName);
        clinicNames = v.findViewById(R.id.clinicNames);
        doctorName = v.findViewById(R.id.doctorName);
        drName = v.findViewById(R.id.drName);
        textView = v.findViewById(R.id.textView);
    }
}