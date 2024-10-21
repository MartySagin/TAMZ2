package com.example.cv_3;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private ArrayList<String> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Získání seznamu historyList z MainActivity
        historyList = getIntent().getStringArrayListExtra("HISTORY_LIST");

        // Inicializace RecyclerView a nastavení adapteru
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new HistoryAdapter(this, historyList);
        recyclerView.setAdapter(historyAdapter);

        loadHistory(); // Načtení historie z SharedPreferences

    }

    private void loadHistory() {
        SharedPreferences sharedPreferences = getSharedPreferences("AppSettings", MODE_PRIVATE);
        Set<String> savedHistory = sharedPreferences.getStringSet("historyList", new HashSet<>());

        historyList.clear();
        historyList.addAll(savedHistory); // Přidání všech položek do ArrayListu

        historyAdapter.notifyDataSetChanged(); // Aktualizace RecyclerView
    }


}