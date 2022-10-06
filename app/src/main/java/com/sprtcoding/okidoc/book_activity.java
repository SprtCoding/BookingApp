package com.sprtcoding.okidoc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
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

import java.util.Calendar;
import java.util.HashMap;

public class book_activity extends AppCompatActivity {
    private AutoCompleteTextView autoCompleteTextView, reasonCTV;
    private TextView clinicName, clinicLocation, filename;
    private MaterialButton dateBtn, timeBtn, bookBtn;
    Intent i;
    String clinicNames, clinicLocations , docUID = "vkWUXCFaWJdRZBBBzGwgAb8vtTn1", patientName;
    int t1Hour, t1Minute, SELECT_PHOTO = 1;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference bookRef, docNotificationRef, userRef;
    private CardView uploadPhoto;
    private LinearLayout uploadBtn;
    Uri MyUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        initiate();
        i = getIntent();
        clinicNames = i.getStringExtra("clinicName");
        clinicLocations = i.getStringExtra("clinicLocation");

        loadingBar = new ProgressDialog(book_activity.this);
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDb = FirebaseDatabase.getInstance();
        bookRef = mDb.getReference("Booking");
        docNotificationRef = mDb.getReference("DocNotification");
        userRef = mDb.getReference("Users");

        clinicLocation.setText(clinicLocations);
        clinicName.setText(clinicNames);

        if(mUser != null) {
            userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        patientName = snapshot.child("Fullname").getValue(String.class);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(book_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        String[] who = new String[] {
                "Old Patient",
                "New Patient"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_item,
                who
        );

        autoCompleteTextView.setAdapter(adapter);

        if(autoCompleteTextView != null) {
            autoCompleteTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    getValue(editable);
                }
            });
        }

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateBtn.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    book_activity.this,
                    (datePicker, year1, month1, day1) -> {
                        month1 = month1 + 1;
                        String date = day1 + "/" + month1 + "/" + year1;
                        dateBtn.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        timeBtn.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    book_activity.this,
                    (TimePickerDialog.OnTimeSetListener) (timePicker, hourOfDay, minute) -> {
                        t1Hour = hourOfDay;
                        t1Minute = minute;

                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.set(0,0,0,t1Hour,t1Minute);
                        timeBtn.setText(DateFormat.format("hh:mm aa", calendar1));
                    }, 12, 0 , false);
            timePickerDialog.updateTime(t1Hour, t1Minute);
            timePickerDialog.show();
        });

        bookBtn.setOnClickListener(view -> {
            loadingBar.show();
            String whoTV = autoCompleteTextView.getText().toString();
            String reasonTV = reasonCTV.getText().toString();
            String dateTV = dateBtn.getText().toString();
            String timeTV = timeBtn.getText().toString();

            if(whoTV.equals("")) {
                Toast.makeText(this, "select who are you", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else if(reasonTV.equals("")) {
                Toast.makeText(this, "select reason of booking", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else if(dateTV.equals("Select Date")) {
                Toast.makeText(this, "select date of booking", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else if(timeTV.equals("Select Time")) {
                Toast.makeText(this, "select time of booking", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else {
                String bookID = bookRef.push().getKey();
                HashMap<String, Object> map = new HashMap<>();
                map.put("who", whoTV);
                map.put("Name", patientName);
                map.put("reason", reasonTV);
                map.put("dateOfBooking", dateTV);
                map.put("timeOfBooking", timeTV);
                map.put("status", "No-Status");
                map.put("userID", mUser.getUid());
                map.put("bookID", bookID);
                assert bookID != null;
                bookRef.child(docUID).child(mUser.getUid()).child(bookID).setValue(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        docNotificationRef.child(docUID).child(bookID).setValue(map);
                        loadingBar.dismiss();
                        Toast.makeText(this, "Booked successfully.", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(book_activity.this, patient_dashboard.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        });

        uploadBtn.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i,SELECT_PHOTO);
        });

        if(reasonCTV != null) {
            reasonCTV.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    getReasonValue(editable);
                }
            });
        }
    }

    private void getReasonValue(Editable editable) {
        if(editable.toString().equals("Diagnosis")) {
            uploadPhoto.setVisibility(View.VISIBLE);
        }else {
            uploadPhoto.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null) {
            MyUri = data.getData();
            filename.setText(patientName + "." + getFileExtension(MyUri));
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void getValue(Editable editable) {
        if(editable.toString().equals("Old Patient")) {
            String[] reason = new String[] {
                    "Follow Up Check-Up",
                    "Diagnosis"
            };

            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                    this,
                    R.layout.dropdown_item,
                    reason
            );

            reasonCTV.setAdapter(adapter1);

        }else if(editable.toString().equals("New Patient")) {
            String[] reason = new String[] {
                    "Check-Up"
            };

            ArrayAdapter<String> adapter1 = new ArrayAdapter<>(
                    this,
                    R.layout.dropdown_item,
                    reason
            );

            reasonCTV.setAdapter(adapter1);
        }
    }

    private void initiate() {
        filename = findViewById(R.id.filename);
        uploadPhoto = findViewById(R.id.uploadPhoto);
        uploadBtn = findViewById(R.id.uploadBtn);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        reasonCTV = findViewById(R.id.reasonCTV);
        clinicName = findViewById(R.id.clinicName);
        clinicLocation = findViewById(R.id.clinicLocation);
        dateBtn = findViewById(R.id.dateBtn);
        timeBtn = findViewById(R.id.timeBtn);
        bookBtn = findViewById(R.id.bookBtn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}