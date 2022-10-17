package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtcoding.okidoc.FCM.FCMNotificationSender;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class open_details extends AppCompatActivity {
    private TextView name, reason, date, time, status;
    private ImageView pic;
    private MaterialButton acceptBtn, declineBtn;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference docNotifRef, patientBookingRef, declineRef, userRef;
    private ProgressDialog declineLoadingBar, acceptLoading;
    AlertDialog alertDialog;
    Intent i;
    String patientName, patientReason, DateOfBooking, TimeOfBooking, StatusOfBooking, BookID, UserID, Who, DocName, declineID, userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_details);
        initialize();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        declineLoadingBar = new ProgressDialog(open_details.this);
        declineLoadingBar.setTitle("Decline");
        declineLoadingBar.setMessage("Please wait...");
        declineLoadingBar.setCanceledOnTouchOutside(true);

        acceptLoading = new ProgressDialog(open_details.this);
        acceptLoading.setTitle("Accept");
        acceptLoading.setMessage("Please wait...");
        acceptLoading.setCanceledOnTouchOutside(true);

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
        declineRef = mDb.getReference("DeclineNotification");
        patientBookingRef = mDb.getReference("Booking");
        userRef = mDb.getReference("Users");

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

            userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        DocName = snapshot.child("Fullname").getValue(String.class);
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

        //message
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Send Message");

        @SuppressLint("InflateParams") View inView = getLayoutInflater().inflate(R.layout.message_dialog, null);
        TextInputEditText sendMessageInput;
        MaterialButton submitBtn, cancelDeclineBtn;

        //dialog message
        sendMessageInput = inView.findViewById(R.id.messageET);
        submitBtn = inView.findViewById(R.id.submitBtn);
        cancelDeclineBtn = inView.findViewById(R.id.cancelDeclineBtn);

        cancelDeclineBtn.setOnClickListener(view -> {
            declineLoadingBar.dismiss();
            alertDialog.cancel();
        });

        submitBtn.setOnClickListener(view1 -> {
            declineLoadingBar.show();
            String message = sendMessageInput.getText().toString();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                if(TextUtils.isEmpty(message)) {
                    Toast.makeText(this, "Please enter your reason!", Toast.LENGTH_SHORT).show();
                    declineLoadingBar.dismiss();
                }else {
                    declineID = declineRef.push().getKey();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("SenderName", DocName);
                    hashMap.put("Reason", message);
                    hashMap.put("ID", declineID);
                    assert declineID != null;
                    declineRef.child(UserID).child(mUser.getUid()).child(declineID).updateChildren(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            sendDeclineNotification(UserID, DocName);
                            alertDialog.cancel();
                            //sendNotification(cusID, QuickbiteName);
                            declineLoadingBar.dismiss();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("status", "Not-Approved");
                            patientBookingRef.child(mUser.getUid()).child(UserID).child(BookID).updateChildren(map);
                            docNotifRef.child(mUser.getUid()).child(BookID).removeValue();
                            finish();
                            Toast.makeText(this, "Booking request decline successfully", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }, 3000);
        });

        builder1.setView(inView);
        alertDialog = builder1.create();

        declineBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(open_details.this);
            builder.setTitle("Cancel")
                    .setMessage("Are you sure you want to decline this booking?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        alertDialog.show();
                    })
                    .setNegativeButton("No", (dialogInterface, i) -> {
                        declineLoadingBar.dismiss();
                        dialogInterface.cancel();
                    })
                    .show();
        });

        acceptBtn.setOnClickListener(view -> {
            acceptLoading.show();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                HashMap<String, Object> map = new HashMap<>();
                map.put("status", "Approved");
                patientBookingRef.child(mUser.getUid()).child(UserID).child(BookID).updateChildren(map)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()) {
                                docNotifRef.child(mUser.getUid()).child(BookID).updateChildren(map);
                                sendAcceptNotification(UserID, DocName);
                                acceptLoading.dismiss();
                                finish();
                                Toast.makeText(this, "Successfully accepted.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }, 3000);
        });

    }

    private void sendDeclineNotification(String UserID, String DocName) {
        FirebaseDatabase.getInstance().getReference("UserToken").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userToken = snapshot.child("token").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(open_details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    getApplicationContext(),
                    userToken,
                    "OkiDoc",
                    DocName + " Decline your booking"
            );
        }, 3000);
    }

    private void sendAcceptNotification(String UserID, String DocName) {
        FirebaseDatabase.getInstance().getReference("UserToken").child(UserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    userToken = snapshot.child("token").getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(open_details.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            FCMNotificationSender.sendNotification(
                    getApplicationContext(),
                    userToken,
                    "OkiDoc",
                    DocName + " Accept your booking"
            );
        }, 3000);
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