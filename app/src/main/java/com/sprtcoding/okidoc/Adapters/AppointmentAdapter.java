package com.sprtcoding.okidoc.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
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

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.myViewHolder> {
    private Context mContext;
    List<BookingModel> bookingModels;

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
        BookingModel appointment = bookingModels.get(position);
        holder.date.setText(appointment.getDateOfBooking());
        holder.time.setText(appointment.getTimeOfBooking());
        holder.reason.setText(appointment.getReason());
        holder.status.setText(appointment.getStatus());

        if(appointment.getStatus().equals("Approved")) {
            holder.myBtn.setText("View Details");
            holder.myBtn.setTextColor(Color.rgb(6, 190, 222));
            holder.myBtn.setIconTintResource(R.color.color3);
            holder.myBtn.setOnClickListener(view -> {
                Toast.makeText(mContext, "View Details", Toast.LENGTH_SHORT).show();
            });
        }else if(appointment.getStatus().equals("Not-Approved")) {
            holder.myBtn.setText("Book Again");
            holder.myBtn.setTextColor(Color.rgb(6, 190, 222));
            holder.myBtn.setIconTintResource(R.color.color3);
            holder.myBtn.setOnClickListener(view -> {
                Toast.makeText(mContext, "Book Again", Toast.LENGTH_SHORT).show();
            });
        }else {
            holder.myBtn.setOnClickListener(view -> {
                Toast.makeText(mContext, "cancel", Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookingModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        MaterialButton myBtn;
        TextView reason, date, time, status;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            myBtn = itemView.findViewById(R.id.myBtn);
            reason = itemView.findViewById(R.id.reason);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            status = itemView.findViewById(R.id.status);
        }
    }
}
