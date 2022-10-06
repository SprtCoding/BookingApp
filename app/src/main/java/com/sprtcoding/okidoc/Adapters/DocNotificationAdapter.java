package com.sprtcoding.okidoc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.sprtcoding.okidoc.Model.BookingModel;
import com.sprtcoding.okidoc.R;

import java.util.List;

public class DocNotificationAdapter extends RecyclerView.Adapter<DocNotificationAdapter.myViewHolder> {
    private Context mContext;
    List<BookingModel> bookingModels;

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

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        BookingModel appointment = bookingModels.get(position);
        holder.name.setText(appointment.getName());

    }

    @Override
    public int getItemCount() {
        return bookingModels.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        MaterialButton openBtn;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            openBtn = itemView.findViewById(R.id.openBtn);
        }
    }
}
