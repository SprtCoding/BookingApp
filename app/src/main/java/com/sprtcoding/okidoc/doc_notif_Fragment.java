package com.sprtcoding.okidoc;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
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
import com.sprtcoding.okidoc.Adapters.DocNotificationAdapter;
import com.sprtcoding.okidoc.Model.BookingModel;

import java.util.ArrayList;
import java.util.List;

public class doc_notif_Fragment extends Fragment {
    View v;
    private TextView clinicLocation, clinicName;
    private FirebaseDatabase mDb;
    private DatabaseReference userRef, docNotificationRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private LinearLayout no_data;
    LinearLayoutManager linearLayoutManager;
    List<BookingModel> bookingModels;
    DocNotificationAdapter docNotificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_doc_notif_, container, false);
        RecyclerView notif_recycle = v.findViewById(R.id.notif_recycle);

        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        notif_recycle.setHasFixedSize(true);
        notif_recycle.setLayoutManager(linearLayoutManager);

        bookingModels = new ArrayList<>();

        initiate();
        //firebase realtime db instance
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        userRef = mDb.getReference("Users");
        docNotificationRef = mDb.getReference("DocNotification");

        if(mUser != null) {
            userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
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

            docNotificationRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if(snapshot.exists()) {
                            bookingModels.clear();
                            notif_recycle.setVisibility(View.VISIBLE);
                            no_data.setVisibility(View.GONE);
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                BookingModel book = ds.getValue(BookingModel.class);
                                bookingModels.add(book);
                            }
                            docNotificationAdapter = new DocNotificationAdapter(getContext(), bookingModels);
                            notif_recycle.scrollToPosition(bookingModels.size()-1);
                            new Handler().postDelayed(() -> notif_recycle.smoothScrollToPosition(bookingModels.size()-1),350);
                            notif_recycle.setAdapter(docNotificationAdapter);
                        }else {
                            notif_recycle.setVisibility(View.GONE);
                            no_data.setVisibility(View.VISIBLE);
                        }
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