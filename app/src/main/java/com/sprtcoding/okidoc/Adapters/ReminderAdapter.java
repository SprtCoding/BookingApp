package com.sprtcoding.okidoc.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sprtcoding.okidoc.Model.ReminderModel;
import com.sprtcoding.okidoc.R;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.myviewHolder> {
    private Context mContext;
    List<ReminderModel> reminderModels;

    public ReminderAdapter(Context mContext, List<ReminderModel> reminderModels) {
        this.mContext = mContext;
        this.reminderModels = reminderModels;
    }

    @NonNull
    @Override
    public myviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_layout,parent,false);
        return new myviewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull myviewHolder holder, int position) {
        ReminderModel reminder = reminderModels.get(position);
        holder.reminder.setText(reminder.getSenderName() + " Reminders \n" + reminder.getReminder());
    }

    @Override
    public int getItemCount() {
        return reminderModels.size();
    }

    public static class myviewHolder extends RecyclerView.ViewHolder {
        TextView reminder;

        public myviewHolder(@NonNull View itemView) {
            super(itemView);
            reminder = itemView.findViewById(R.id.reminder);
        }
    }
}
