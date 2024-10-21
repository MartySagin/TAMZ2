package com.example.cv_3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private ArrayList<String> historyList;
    private Context context;

    public HistoryAdapter(Context context, ArrayList<String> historyList) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String historyEntry = historyList.get(position);
        holder.historyTextView.setText(historyEntry);

        // Set click listener for delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeHistoryItem(holder.getAdapterPosition());
            }
        });
    }

    private void removeHistoryItem(int position) {
        historyList.remove(position); // Odstraň položku ze seznamu
        notifyItemRemoved(position);  // Aktualizace UI

        SharedPreferences sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Set<String> updatedSet = new HashSet<>(historyList);

        editor.putStringSet("historyList", updatedSet);
        editor.apply();
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView historyTextView;
        Button deleteButton;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            historyTextView = itemView.findViewById(R.id.historyTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
