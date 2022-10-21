package com.sprtcoding.okidoc.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sprtcoding.okidoc.Model.BookingModel;
import com.sprtcoding.okidoc.R;
import com.sprtcoding.okidoc.book_activity;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.myViewHolder> {
    private Context mContext;
    List<BookingModel> bookingModels;
    String docUID = "vkWUXCFaWJdRZBBBzGwgAb8vtTn1";
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase mDb;
    private DatabaseReference bookRef, docNotificationRef;
    private ProgressDialog loadingBar;

    public AppointmentAdapter(Context mContext, List<BookingModel> bookingModels) {
        this.mContext = mContext;
        this.bookingModels = bookingModels;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_layout,parent,false);
        return new myViewHolder(view);
    }

    @SuppressLint({"SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        loadingBar = new ProgressDialog(mContext);
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDb = FirebaseDatabase.getInstance();
        bookRef = mDb.getReference("Booking");
        docNotificationRef = mDb.getReference("DocNotification");

        BookingModel appointment = bookingModels.get(position);
        holder.date.setText(appointment.getDateOfBooking());
        holder.time.setText(appointment.getTimeOfBooking());
        holder.reason.setText(appointment.getReason());
        holder.status.setText(appointment.getStatus());
        holder.dateNow.setText(appointment.getDate());
        holder.timeNow.setText(appointment.getTime());

        if(appointment.getStatus().equals("Approved")) {
            holder.declineLayout.setVisibility(View.GONE);
            holder.myBtn.setText("View Details");
            holder.myBtn.setTextColor(Color.rgb(6, 190, 222));
            holder.myBtn.setIconTintResource(R.color.color3);
            holder.myBtn.setOnClickListener(view -> Toast.makeText(mContext, "View Details", Toast.LENGTH_SHORT).show());
        }else if(appointment.getStatus().equals("Not-Approved")) {
            holder.declineLayout.setVisibility(View.VISIBLE);
            holder.declineMessage.setText(appointment.getDeclineMessage());
            holder.myBtn.setText("Book Again");
            holder.myBtn.setTextColor(Color.rgb(6, 190, 222));
            holder.myBtn.setIconTintResource(R.color.color3);
            holder.myBtn.setOnClickListener(view -> {
                loadingBar.show();
                android.os.Handler handler = new Handler();
                handler.postDelayed(() -> {
                    loadingBar.dismiss();
                    Intent i = new Intent(mContext, book_activity.class);
                    mContext.startActivity(i);
                }, 3000);
            });
        }else {
            holder.myBtn.setOnClickListener(view -> {
                loadingBar.show();
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Cancel")
                        .setMessage("Are you sure you want to cancel your booking?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            android.os.Handler handler = new Handler();
                            handler.postDelayed(() -> bookRef.child(docUID).child(mUser.getUid()).child(appointment.getBookID()).removeValue()
                                    .addOnCompleteListener(task -> {
                                        if(task.isSuccessful()) {
                                            loadingBar.dismiss();
                                            docNotificationRef.child(docUID).child(appointment.getBookID()).removeValue();
                                            Toast.makeText(mContext, "Booking Cancel Successfully.", Toast.LENGTH_SHORT).show();
                                        }
                                    }), 3000);
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            loadingBar.dismiss();
                            dialogInterface.cancel();
                        })
                        .show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookingModels.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        MaterialButton myBtn;
        TextView reason, date, time, status, dateNow, timeNow, declineMessage;
        LinearLayout declineLayout;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            declineMessage = itemView.findViewById(R.id.declineMessage);
            declineLayout = itemView.findViewById(R.id.declineLayout);
            myBtn = itemView.findViewById(R.id.myBtn);
            reason = itemView.findViewById(R.id.reason);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
            dateNow = itemView.findViewById(R.id.dateNow);
            timeNow = itemView.findViewById(R.id.timeNow);
        }
    }
}
