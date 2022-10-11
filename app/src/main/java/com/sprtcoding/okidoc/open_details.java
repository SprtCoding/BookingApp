package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class open_details extends AppCompatActivity {
    private TextView name, reason, date, time, status;
    private ImageView pic;
    private MaterialButton acceptBtn, declineBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference docNotifRef, patientBookingRef;
    Intent i;
    String patientName, patientReason, DateOfBooking, TimeOfBooking, StatusOfBooking, BookID, UserID, Who;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_details);
        initialize();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // get string extra
        i = getIntent();
        patientName = i.getStringExtra("name");
        patientReason = i.getStringExtra("reason");
        DateOfBooking = i.getStringExtra("date");
        TimeOfBooking = i.getStringExtra("time");
        StatusOfBooking = i.getStringExtra("status");
        UserID = i.getStringExtra("userID");
        Who = i.getStringExtra("who");
        BookID = i.getStringExtra("bookID");

        mDb = FirebaseDatabase.getInstance();
        docNotifRef = mDb.getReference("DocNotification");
        patientBookingRef = mDb.getReference("Booking");

        name.setText(patientName);
        if(mUser != null) {
            docNotifRef.child(mUser.getUid()).child(BookID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if(snapshot.hasChild("DiagnosisImage")) {
                            String image = snapshot.child("DiagnosisImage").getValue(String.class);
                            pic.setVisibility(View.VISIBLE);
                            Picasso.get().load(image).fit().centerInside().into(pic);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(open_details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        reason.setText(patientReason);
        date.setText(DateOfBooking);
        time.setText(TimeOfBooking);
        status.setText(StatusOfBooking);

    }

    private void initialize() {
        name = findViewById(R.id.name);
        reason = findViewById(R.id.reason);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        status = findViewById(R.id.status);
        pic = findViewById(R.id.pic);
        acceptBtn = findViewById(R.id.acceptBtn);
        declineBtn = findViewById(R.id.declineBtn);
    }
}