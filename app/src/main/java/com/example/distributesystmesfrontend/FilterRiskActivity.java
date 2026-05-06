package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import GamePackage.Game;
import MiscPackage.Request;

public class FilterRiskActivity extends AppCompatActivity {

    private CheckBox checkboxLowRisk, checkboxMediumRisk, checkboxHighRisk;
    private TextView txtResultsInfo;
    private Button btnReset, btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_risk);

        checkboxLowRisk    = findViewById(R.id.checkbox_low_risk);
        checkboxMediumRisk = findViewById(R.id.checkbox_medium_risk);
        checkboxHighRisk   = findViewById(R.id.checkbox_high_risk);
        txtResultsInfo     = findViewById(R.id.txt_results_info);
        btnReset           = findViewById(R.id.btn_reset_filter);
        btnApply           = findViewById(R.id.btn_apply_filter);

        btnReset.setOnClickListener(v -> {
            checkboxLowRisk.setChecked(false);
            checkboxMediumRisk.setChecked(false);
            checkboxHighRisk.setChecked(false);
            txtResultsInfo.setText("Games found: —");
        });

        btnApply.setOnClickListener(v -> applyFilter());
    }

    private List<String> selectedLevels() {
        List<String> levels = new ArrayList<>();
        if (checkboxLowRisk.isChecked())    levels.add("low");
        if (checkboxMediumRisk.isChecked()) levels.add("medium");
        if (checkboxHighRisk.isChecked())   levels.add("high");
        return levels;
    }

    private void applyFilter() {
        List<String> levels = selectedLevels();
        if (levels.isEmpty()) {
            Toast.makeText(this, "Select at least one risk level", Toast.LENGTH_SHORT).show();
            return;
        }

        txtResultsInfo.setText("Searching...");
        btnApply.setEnabled(false);

        new Thread(() -> {
            ArrayList<String> combined = fetchFilteredGameNames(levels);

            runOnUiThread(() -> {
                btnApply.setEnabled(true);
                if (combined == null) {
                    txtResultsInfo.setText("Games found: 0");
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                    return;
                }
                txtResultsInfo.setText("Games found: " + combined.size());

                Intent data = new Intent();
                data.putExtra(MainMenu.EXTRA_FILTERED_GAMES, combined);
                setResult(RESULT_OK, data);
                finish();
            });
        }).start();
    }

    private ArrayList<String> fetchFilteredGameNames(List<String> levels) {
        // LinkedHashSet keeps order and dedupes if a game qualifies under multiple risks
        // (shouldn't happen but defensive)
        LinkedHashSet<String> combined = new LinkedHashSet<>();
        for (String level : levels) {
            try (Socket socket = new Socket(MainMenu.MASTER_HOST, MainMenu.MASTER_PORT)) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                out.writeObject(new Request("filter_games_risklvl", new Object[]{ level }));

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                @SuppressWarnings("unchecked")
                List<Game> games = (List<Game>) in.readObject();

                for (Game g : games) combined.add(g.getGameName());

            } catch (IOException | ClassNotFoundException e) {
                Log.e("ms", "Failed to fetch by risk level " + level, e);
                return null;
            }
        }
        return new ArrayList<>(combined);
    }
}