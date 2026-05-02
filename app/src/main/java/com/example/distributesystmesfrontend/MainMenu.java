package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainMenu extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView navUsername, navBalance, navProfitLoss;

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

        // Get username from login screen
        String username = getIntent().getStringExtra("username");
        navUsername.setText(username);

        // Open drawer on hamburger click
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Game buttons
        findViewById(R.id.slotsButton).setOnClickListener(v -> {
            // TODO: open slots activity
        });

        findViewById(R.id.rouletteButton).setOnClickListener(v -> {
            // TODO: open roulette activity
        });

        findViewById(R.id.blackjackButton).setOnClickListener(v -> {
            // coming soon
        });

        findViewById(R.id.tomeButton).setOnClickListener(v -> {
            // coming soon
        });
    }
}