package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import GamePackage.Game;
import MiscPackage.Request;

public class FilterBetActivity extends AppCompatActivity {

    private EditText editMinBet;
    private TextView txtResultsInfo;
    private Button btnReset, btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_bet);

        editMinBet     = findViewById(R.id.edit_min_bet);
        txtResultsInfo = findViewById(R.id.txt_results_info);
        btnReset       = findViewById(R.id.btn_reset_filter);
        btnApply       = findViewById(R.id.btn_apply_filter);

        btnReset.setOnClickListener(v -> {
            editMinBet.setText("");
            txtResultsInfo.setText("Games found: —");
        });

        btnApply.setOnClickListener(v -> applyFilter());
    }

    private void applyFilter() {
        String minText = editMinBet.getText().toString().trim();
        if (minText.isEmpty()) {
            Toast.makeText(this, "Enter a minimum bet", Toast.LENGTH_SHORT).show();
            return;
        }

        double minBet;
        try {
            minBet = Double.parseDouble(minText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        txtResultsInfo.setText("Searching...");
        btnApply.setEnabled(false);

        new Thread(() -> {
            ArrayList<String> names = fetchFilteredGameNames(minBet);

            runOnUiThread(() -> {
                btnApply.setEnabled(true);
                if (names == null) {
                    txtResultsInfo.setText("Games found: 0");
                    Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                    return;
                }
                txtResultsInfo.setText("Games found: " + names.size());

                Intent data = new Intent();
                data.putExtra(MainMenu.EXTRA_FILTERED_GAMES, names);
                setResult(RESULT_OK, data);
                finish();
            });
        }).start();
    }

    private ArrayList<String> fetchFilteredGameNames(double minBet) {
        try (Socket socket = new Socket(MainMenu.MASTER_HOST, MainMenu.MASTER_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            out.writeObject(new Request("filter_games_bettingLimits", new Object[]{ minBet }));

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            @SuppressWarnings("unchecked")
            List<Game> games = (List<Game>) in.readObject();

            ArrayList<String> names = new ArrayList<>();
            for (Game g : games) names.add(g.getGameName());
            return names;

        } catch (IOException | ClassNotFoundException e) {
            Log.e("ms", "Failed to fetch by bet limit", e);
            return null;
        }
    }
}