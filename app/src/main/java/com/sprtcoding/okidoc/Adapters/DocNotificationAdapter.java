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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sprtcoding.okidoc.Model.BookingModel;
import com.sprtcoding.okidoc.R;
import com.sprtcoding.okidoc.book_activity;
import com.sprtcoding.okidoc.open_details;

import java.util.List;

public class DocNotificationAdapter extends RecyclerView.Adapter<DocNotificationAdapter.myViewHolder> {
    private Context mContext;
    List<BookingModel> bookingModels;
    private ProgressDialog loadingBar;

    public DocNotificationAdapter(Context mContext, List<BookingModel> bookingModels) {
        this.mContext = mContext;
        this.bookingModels = bookingModels;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doc_notif_layout,parent,false);
        return new myViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        loadingBar = new ProgressDialog(mContext);
        loadingBar.setTitle("Loading");
        loadingBar.setMessage("Please wait...");
        loadingBar.setCanceledOnTouchOutside(true);

        BookingModel appointment = bookingModels.get(position);
        holder.name.setText(appointment.getName());

        if(appointment.getStatus().equals("Approved")) {
            holder.status.setText(appointment.getStatus());
            holder.openBtn.setText("View Details");
            holder.openBtn.setTextColor(Color.rgb(6, 190, 222));
            holder.openBtn.setIconTintResource(R.color.color3);
            holder.openBtn.setOnClickListener(view -> Toast.makeText(mContext, "View Details", Toast.LENGTH_SHORT).show());
        }else {
            holder.openBtn.setOnClickListener(view -> {
                loadingBar.show();
                android.os.Handler handler = new Handler();
                handler.postDelayed(() -> {
                    loadingBar.dismiss();
                    Intent i = new Intent(mContext, open_details.class);
                    i.putExtra("name", appointment.getName());
                    i.putExtra("reason", appointment.getReason());
                    i.putExtra("status", appointment.getStatus());
                    i.putExtra("date", appointment.getDateOfBooking());
                    i.putExtra("time", appointment.getTimeOfBooking());
                    i.putExtra("bookID", appointment.getBookID());
                    i.putExtra("userID", appointment.getUserID());
                    i.putExtra("who", appointment.getWho());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }, 3000);
            });
        }

    }

    @Override
    public int getItemCount() {
        return bookingModels.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        MaterialButton openBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            openBtn = itemView.findViewById(R.id.openBtn);
        }
    }
}
