package com.sprtcoding.okidoc;

import android.content.Intent;
import android.graphics.text.LineBreaker;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.okidoc.Adapters.DocNotificationAdapter;
import com.sprtcoding.okidoc.Adapters.ReminderAdapter;
import com.sprtcoding.okidoc.Model.ReminderModel;

import java.util.ArrayList;
import java.util.List;

public class PatientHomeFragment extends Fragment {
    View v;
    private RecyclerView reminder_recycle;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef, reminderRef;
    private MaterialButton bookBtn, messageBtn;
    private TextView clinicLocation, clinicName, doctorName, clinicNames, drName, textView;
    LinearLayoutManager linearLayoutManager;
    String doctorUID = "vkWUXCFaWJdRZBBBzGwgAb8vtTn1";
    String clinicName1, address;
    AlertDialog alertDialog;
    List<ReminderModel> reminderModels;
    ReminderAdapter reminderAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_patient_home, container, false);
        initiate();

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        reminder_recycle.setHasFixedSize(true);
        reminder_recycle.setLayoutManager(linearLayoutManager);

        reminderModels = new ArrayList<>();

        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        reminderRef = mDb.getReference("Reminder");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setJustificationMode(LineBreaker.JUSTIFICATION_MODE_INTER_WORD);
        }

        userRef.child(doctorUID).addValueEventListener(new ValueEventListener() {
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

        reminderRef.child(doctorUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    reminderModels.clear();
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        ReminderModel reminder = ds.getValue(ReminderModel.class);
                        reminderModels.add(reminder);
                    }
                    reminderAdapter = new ReminderAdapter(getContext(), reminderModels);
                    reminder_recycle.scrollToPosition(reminderModels.size()-1);
                    new Handler().postDelayed(() -> reminder_recycle.smoothScrollToPosition(reminderModels.size()-1),350);
                    reminder_recycle.setAdapter(reminderAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //message
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("You Are?");

        View inView = getLayoutInflater().inflate(R.layout.alert_dialog, null);
        MaterialButton newBtn, oldBtn;

        newBtn = inView.findViewById(R.id.newBtn);
        oldBtn = inView.findViewById(R.id.oldBtn);

        newBtn.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), book_activity.class);
            startActivity(i);
        });

        builder.setView(inView);
        alertDialog = builder.create();

        bookBtn.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), book_activity.class);
            i.putExtra("clinicName", clinicName1);
            i.putExtra("clinicLocation", address);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        });

        return v;
    }

    private void initiate() {
        reminder_recycle = v.findViewById(R.id.reminder_recycle);
        clinicLocation = v.findViewById(R.id.clinicLocation);
        clinicName = v.findViewById(R.id.clinicName);
        clinicNames = v.findViewById(R.id.clinicNames);
        doctorName = v.findViewById(R.id.doctorName);
        drName = v.findViewById(R.id.drName);
        textView = v.findViewById(R.id.textView);
        bookBtn = v.findViewById(R.id.bookBtn);
        messageBtn = v.findViewById(R.id.messageBtn);
    }
}