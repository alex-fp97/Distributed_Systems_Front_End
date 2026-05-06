package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RatingBar;
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

public class FilterRatingActivity extends AppCompatActivity {

    private RatingBar filterRatingBar;
    private TextView txtSelectedRating, txtResultsInfo;
    private Button btnReset, btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_rating);

        filterRatingBar   = findViewById(R.id.filter_rating_bar);
        txtSelectedRating = findViewById(R.id.txt_selected_rating);
        txtResultsInfo    = findViewById(R.id.txt_results_info);
        btnReset          = findViewById(R.id.btn_reset_filter);
        btnApply          = findViewById(R.id.btn_apply_filter);

        filterRatingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            txtSelectedRating.setText(rating == 0
                    ? "Selected Rating: All Games"
                    : "Selected Rating: " + (int) rating + "★ and above");
        });

        btnReset.setOnClickListener(v -> {
            filterRatingBar.setRating(0);
            txtSelectedRating.setText("Selected Rating: All Games");
            txtResultsInfo.setText("Games found: —");
        });

        btnApply.setOnClickListener(v -> applyFilter());
    }

    private void applyFilter() {
        double minRating = filterRatingBar.getRating();
        txtResultsInfo.setText("Searching...");
        btnApply.setEnabled(false);

        new Thread(() -> {
            ArrayList<String> names = fetchFilteredGameNames(minRating);

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

    private ArrayList<String> fetchFilteredGameNames(double minRating) {
        try (Socket socket = new Socket(MainMenu.MASTER_HOST, MainMenu.MASTER_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            out.writeObject(new Request("filter_games_rating", new Object[]{ minRating }));

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            @SuppressWarnings("unchecked")
            List<Game> games = (List<Game>) in.readObject();

            ArrayList<String> names = new ArrayList<>();
            for (Game g : games) names.add(g.getGameName());
            return names;

        } catch (IOException | ClassNotFoundException e) {
            Log.e("ms", "Failed to fetch by rating", e);
            return null;
        }
    }
}