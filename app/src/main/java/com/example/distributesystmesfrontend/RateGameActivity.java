package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import MiscPackage.Request;

public class RateGameActivity extends AppCompatActivity {

    public static final String EXTRA_AVAILABLE_GAMES = "availableGames";

    private Spinner spinnerGames;
    private RatingBar ratingBar;
    private TextView txtRatingDisplay;
    private Button btnSubmit, btnCancel;

    private List<String> gameNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_game);

        spinnerGames     = findViewById(R.id.spinner_games);
        ratingBar        = findViewById(R.id.rating_bar);
        txtRatingDisplay = findViewById(R.id.txt_rating_display);
        btnSubmit        = findViewById(R.id.btn_submit_rating);
        btnCancel        = findViewById(R.id.btn_cancel);

        setupGamesSpinner();

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) ->
                txtRatingDisplay.setText("Rating: " + (int) rating + "/5"));

        btnSubmit.setOnClickListener(v -> submitRating());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void setupGamesSpinner() {
        ArrayList<String> passed = (ArrayList<String>) getIntent().getSerializableExtra(EXTRA_AVAILABLE_GAMES);
        if (passed == null || passed.isEmpty()) {
            Toast.makeText(this, "No games available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        gameNames = new ArrayList<>();
        gameNames.add("Select a game...");
        gameNames.addAll(passed);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, gameNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGames.setAdapter(adapter);
    }

    private void submitRating() {
        String selectedGame = spinnerGames.getSelectedItem().toString();
        float rating = ratingBar.getRating();

        if (selectedGame.equals("Select a game...")) {
            Toast.makeText(this, "Please select a game", Toast.LENGTH_SHORT).show();
            return;
        }
        if (rating == 0) {
            Toast.makeText(this, "Please provide a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);

        new Thread(() -> {
            boolean success = sendRating(selectedGame, (double) rating);

            runOnUiThread(() -> {
                btnSubmit.setEnabled(true);
                if (success) {
                    Toast.makeText(this,
                            "Rated " + selectedGame + ": " + (int) rating + "/5",
                            Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this,
                            "Failed to submit rating", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private boolean sendRating(String gameName, double rating) {
        try (Socket socket = new Socket(MainMenu.MASTER_HOST, MainMenu.MASTER_PORT)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            out.writeObject(new Request("rate_game", new Object[]{ gameName, rating }));

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            return in.readBoolean();

        } catch (IOException e) {
            Log.e("ms", "Failed to submit rating", e);
            return false;
        }
    }
}