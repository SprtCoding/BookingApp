package com.sprtcoding.okidoc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.okidoc.Adapters.AppointmentAdapter;
import com.sprtcoding.okidoc.Model.BookingModel;

import java.util.ArrayList;
import java.util.List;

public class PatientAppointmentFragment extends Fragment {
    View v;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef, appointmentRef;
    private TextView clinicLocation, clinicName;
    private LinearLayout no_data;
    LinearLayoutManager linearLayoutManager;
    String doctorUID = "vkWUXCFaWJdRZBBBzGwgAb8vtTn1";
    List<BookingModel> bookingModels;
    AppointmentAdapter appointmentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_patient_appointment, container, false);
        RecyclerView book_recycle = v.findViewById(R.id.book_recycle);

        linearLayoutManager = new LinearLayoutManager(getContext());
        book_recycle.setHasFixedSize(true);
        book_recycle.setLayoutManager(linearLayoutManager);

        bookingModels = new ArrayList<>();

        initiate();
        //firebase realtime db instance
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        appointmentRef = mDb.getReference("Booking");

        userRef.child(doctorUID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String address = snapshot.child("Address").getValue(String.class);
                    if(snapshot.hasChild("ClinicName")) {
                        String clinicName1 = snapshot.child("ClinicName").getValue(String.class);
                        clinicName.setText(clinicName1);
                    }
                    clinicLocation.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        if(mUser != null) {
            appointmentRef.child(doctorUID).child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        bookingModels.clear();
                        book_recycle.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.GONE);
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            BookingModel appointment = ds.getValue(BookingModel.class);
                            bookingModels.add(appointment);
                        }
                        appointmentAdapter = new AppointmentAdapter(getContext(), bookingModels);
                        book_recycle.setAdapter(appointmentAdapter);
                    }else {
                        book_recycle.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        return v;
    }

    private void initiate() {
        no_data = v.findViewById(R.id.no_data);
        clinicLocation = v.findViewById(R.id.clinicLocation);
        clinicName = v.findViewById(R.id.clinicName);
    }
}