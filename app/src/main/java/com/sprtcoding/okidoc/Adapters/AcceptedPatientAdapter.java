package com.sprtcoding.okidoc.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

public class AcceptedPatientAdapter extends RecyclerView.Adapter<AcceptedPatientAdapter.myViewHolder>{
    private Context mContext;
    List<BookingModel> bookingModels;
    private ProgressDialog loadingBar;

    public AcceptedPatientAdapter(Context mContext, List<BookingModel> bookingModels) {
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

        if(appointment.getStatus().equals("Approved")) {
            holder.name.setText(appointment.getName());
            holder.date.setText(appointment.getDate());
            holder.time.setText(appointment.getTime());
            holder.phone.setText(appointment.getPatientNumber());
            holder.status.setText(appointment.getStatus());
            holder.openBtn.setText("View Details");
            holder.openBtn.setTextColor(Color.rgb(6, 190, 222));
            holder.openBtn.setIconTintResource(R.color.color3);
            holder.openBtn.setOnClickListener(view -> Toast.makeText(mContext, "View Details", Toast.LENGTH_SHORT).show());
        }

    }

    @Override
    public int getItemCount() {
        return bookingModels.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView name, status, date, time, phone;
        MaterialButton openBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            phone = itemView.findViewById(R.id.phone);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            openBtn = itemView.findViewById(R.id.openBtn);
        }
    }
}
