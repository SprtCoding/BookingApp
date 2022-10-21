package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.okidoc.Adapters.AcceptedPatientAdapter;
import com.sprtcoding.okidoc.Adapters.DocNotificationAdapter;
import com.sprtcoding.okidoc.Model.BookingModel;

import java.util.ArrayList;
import java.util.List;

public class accepted_patient_list extends AppCompatActivity {
    private RecyclerView accepted_recycle;
    private LinearLayout no_data;
    private FirebaseDatabase mDb;
    private DatabaseReference docNotificationRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    LinearLayoutManager linearLayoutManager;
    List<BookingModel> bookingModels;
    AcceptedPatientAdapter acceptedPatientAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accepted_patient_list);
        initialize();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        accepted_recycle.setHasFixedSize(true);
        accepted_recycle.setLayoutManager(linearLayoutManager);

        bookingModels = new ArrayList<>();

        //firebase realtime db instance
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        docNotificationRef = mDb.getReference("DocNotification");

        if(mUser != null) {
            docNotificationRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        accepted_recycle.setVisibility(View.VISIBLE);
                        no_data.setVisibility(View.GONE);
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            BookingModel book = ds.getValue(BookingModel.class);
                            if(book.getStatus().equals("Approved")) {
                                bookingModels.add(book);
                                acceptedPatientAdapter = new AcceptedPatientAdapter(accepted_patient_list.this, bookingModels);
                                accepted_recycle.scrollToPosition(bookingModels.size()-1);
                                new Handler().postDelayed(() -> accepted_recycle.smoothScrollToPosition(bookingModels.size()-1),350);
                                accepted_recycle.setAdapter(acceptedPatientAdapter);
                            }
                        }
                    }else {
                        accepted_recycle.setVisibility(View.GONE);
                        no_data.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(accepted_patient_list.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initialize() {
        accepted_recycle = findViewById(R.id.accepted_recycle);
        no_data = findViewById(R.id.no_data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}