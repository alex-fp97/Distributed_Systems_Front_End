package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MainMenu extends AppCompatActivity {

    public static final String MASTER_HOST = "192.168.1.74";
    public static final int MASTER_PORT = 5000;
    public static final String EXTRA_FILTERED_GAMES = "filteredGameNames";

    DrawerLayout drawerLayout;
    private PlayerPackage.Player pl;
    TextView navUsername, navBalance, navProfitLoss;
    Button betFilter, riskFilter, ratingFilter, addFunds, resetFilters, rateGame;
    private RecyclerView gamesList;
    private GameAdapter gameAdapter;

    private final ActivityResultLauncher<Intent> rouletteLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            double sessionPL = data.getDoubleExtra("sessionProfitLoss", 0);
                            double lifetimePL = data.getDoubleExtra("lifetimeProfitLoss", 0);
                            double newBalance = data.getDoubleExtra("newBalance", pl.getBalance());
                            pl.updateBalance(newBalance);
                            refreshStats(sessionPL, lifetimePL);
                        }
                    });

    private final ActivityResultLauncher<Intent> filterRatingLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleFilterResult);

    private final ActivityResultLauncher<Intent> filterRiskLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleFilterResult);

    private final ActivityResultLauncher<Intent> filterBetLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    this::handleFilterResult);

    private final ActivityResultLauncher<Intent> addFundsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Intent data = result.getData();
                            double newBalance = data.getDoubleExtra("newBalance", pl.getBalance());
                            pl.updateBalance(newBalance);
                            refreshStats(0, 0);
                        }
                    });

    private final ActivityResultLauncher<Intent> rateGameLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        // Nothing to do — rating was saved on the server side
                    });

    @SuppressWarnings("unchecked")
    private void handleFilterResult(androidx.activity.result.ActivityResult result) {
        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
            ArrayList<String> names = (ArrayList<String>) result.getData()
                    .getSerializableExtra("filteredGameNames");
            if (names != null) gameAdapter.setGames(names);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        drawerLayout = findViewById(R.id.main);

        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        navUsername = headerView.findViewById(R.id.navUsername);
        navBalance = headerView.findViewById(R.id.navBalance);
        navProfitLoss = headerView.findViewById(R.id.navProfitLoss);
        betFilter = headerView.findViewById(R.id.betFilterBtn);
        riskFilter = headerView.findViewById(R.id.riskFilterBtn);
        ratingFilter = headerView.findViewById(R.id.ratingFilterBtn);
        addFunds = headerView.findViewById(R.id.addFundsBtn);
        rateGame = headerView.findViewById(R.id.rateGameBtn);
        resetFilters = headerView.findViewById(R.id.resetFiltersBtn);

        pl = (PlayerPackage.Player) getIntent().getSerializableExtra("player");
        navUsername.setText(pl.getPlayerName());

        refreshStats(0, 0);

        // Setup the games RecyclerView
        gamesList = findViewById(R.id.gamesList);
        gamesList.setLayoutManager(new GridLayoutManager(this, 2));
        gameAdapter = new GameAdapter(this::onGameClicked);
        gamesList.setAdapter(gameAdapter);

        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        betFilter.setOnClickListener(v -> {
            Intent i = new Intent(this, FilterBetActivity.class);
            filterBetLauncher.launch(i);
        });

        riskFilter.setOnClickListener(v -> {
            Intent i = new Intent(this, FilterRiskActivity.class);
            filterRiskLauncher.launch(i);
        });

        ratingFilter.setOnClickListener(v -> {
            Intent i = new Intent(this, FilterRatingActivity.class);
            filterRatingLauncher.launch(i);
        });

        addFunds.setOnClickListener(v -> {
            Intent i = new Intent(this, AddFundsActivity.class);
            i.putExtra("player", pl);
            addFundsLauncher.launch(i);
        });

        rateGame.setOnClickListener(v -> {
            if (gameAdapter.getItemCount() == 0) {
                Toast.makeText(this, "No games available", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(this, RateGameActivity.class);
            i.putExtra(RateGameActivity.EXTRA_AVAILABLE_GAMES,
                    new ArrayList<>(gameAdapter.getCurrentGames()));
            rateGameLauncher.launch(i);
        });

        resetFilters.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            fetchAllGames();
        });

        // Fetch the full list once at startup
        fetchAllGames();
    }

    private void onGameClicked(String gameName) {
        switch (gameName) {
            case "Roulette":
                Intent i = new Intent(this, Roulette.class);
                i.putExtra("player", pl);
                rouletteLauncher.launch(i);
                break;
            case "Slots":
                Toast.makeText(this, "Slots coming soon", Toast.LENGTH_SHORT).show();
                break;
            case "Black Jack":
                Toast.makeText(this, "Blackjack coming soon", Toast.LENGTH_SHORT).show();
                break;
            case "Tome of Sadness":
                Toast.makeText(this, "Tome of Sadness coming soon", Toast.LENGTH_SHORT).show();
                break;
            case "Sweet Bonanza":
                Toast.makeText(this, "Sweet Bonanza coming soon", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, gameName + " not implemented", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshStats(double sessionProfitLoss, double lifetimeProfitLoss) {
        navBalance.setText(String.format("Balance: %.2f", pl.getBalance()));
        navProfitLoss.setText(String.format("Total profit/loss: %+.2f", lifetimeProfitLoss));
    }

    private void fetchAllGames() {
        new Thread(() -> {
            List<String> gameNames = new ArrayList<>();
            try (Socket socket = new Socket(MASTER_HOST, MASTER_PORT)) {
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                out.writeObject(new MiscPackage.Request(
                        "filter_games_bettingLimits",
                        new Object[]{ 0.0 }));

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                @SuppressWarnings("unchecked")
                List<GamePackage.Game> games = (List<GamePackage.Game>) in.readObject();

                for (GamePackage.Game g : games) gameNames.add(g.getGameName());
                Log.d("ms", "Available games: " + gameNames);

            } catch (IOException | ClassNotFoundException e) {
                Log.e("ms", "Failed to fetch games", e);
            }

            runOnUiThread(() -> gameAdapter.setGames(gameNames));
        }).start();
    }
}