package com.example.distributesystmesfrontend;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.random.RandomGenerator;

public class Roulette extends AppCompatActivity {

    private double sessionStartBalance;
    private PlayerPackage.Player pl;
    private boolean exiting = false;

    private static final Set<Integer> RED_NUMBERS = new HashSet<>(Arrays.asList(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
    ));

    private static final int[] WHEEL_ORDER = {
            0,32,15,19,4,21,2,25,17,34,6,27,13,36,11,30,8,23,10,
            5,24,16,33,1,20,14,31,9,22,18,29,7,28,12,35,3,26
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        buildNumberGrid();

        pl = (PlayerPackage.Player) getIntent().getSerializableExtra("player");
        sessionStartBalance = pl.getBalance();

        findViewById(R.id.increaseBetButton).setOnClickListener(v->increaseBet());
        findViewById(R.id.decreaseBetButton).setOnClickListener(v->decreaseBet());

        Button bet = findViewById(R.id.betButton);
        TextView betTextView = findViewById(R.id.betAmountText);

        bet.setOnClickListener(v-> {
            new Thread(()->{
                try{
                Socket socket = new Socket("192.168.1.74", 5000);
                String betText = betTextView.getText().toString();
                MiscPackage.Request rq = new MiscPackage.Request("place_bet", new Object[]{pl, Double.parseDouble(betText), "Roulette"});

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                out.writeObject(rq);

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                double result = in.readDouble();

                Log.v("ms", "Received: " + result);

                if (result == -1){
                    runOnUiThread(() -> {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    });
                }else{

                    runOnUiThread(() -> {
                        pl.addBalance(-Double.parseDouble(betText));
                        pl.addBalance(result);
                        Toast.makeText(Roulette.this, "Won: " + result, Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }}).start();
            spinWheel();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (exiting) return;
                exiting = true;

                Toast.makeText(Roulette.this, "Saving stats...", Toast.LENGTH_SHORT).show();

                new Thread(() -> {
                    double lifetimeProfitLoss = fetchLifetimeProfitLoss();

                    runOnUiThread(() -> {
                        Intent data = new Intent();
                        data.putExtra("sessionProfitLoss", pl.getBalance() - sessionStartBalance);
                        data.putExtra("lifetimeProfitLoss", lifetimeProfitLoss);
                        data.putExtra("newBalance", pl.getBalance());
                        data.putExtra("player", pl);
                        setResult(RESULT_OK, data);
                        finish();
                    });
                }).start();
            }
        });
    }


    private void buildNumberGrid() {
        GridLayout grid = findViewById(R.id.numberGrid);
        grid.setRowCount(3);
        grid.setColumnCount(12);

        float density = getResources().getDisplayMetrics().density;
        int cellSize = (int) (40 * density);

        for (int col = 0; col < 12; col++) {
            for (int row = 0; row < 3; row++) {
                final int number = (col * 3) + (3 - row);

                TextView cell = new TextView(this);
                cell.setText(String.valueOf(number));
                cell.setTextSize(12f);
                cell.setTextColor(Color.WHITE);
                cell.setGravity(Gravity.CENTER);
                cell.setClickable(true);
                cell.setFocusable(true);

                int bgColor = RED_NUMBERS.contains(number)
                        ? Color.parseColor("#c62828")
                        : Color.parseColor("#212121");
                cell.setBackgroundColor(bgColor);

                cell.setOnClickListener(v -> onNumberClicked(number));
                findViewById(R.id.btn0).setOnClickListener(v->onNumberClicked(0));

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellSize;
                params.height = cellSize;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.setMargins(1, 1, 1, 1);

                grid.addView(cell, params);
            }
        }
    }

    private void onNumberClicked(int number) {
        Toast.makeText(this, "Clicked " + number, Toast.LENGTH_SHORT).show();
    }

    private void spinWheel() {
        ImageView wheel = findViewById(R.id.rouletteWheel);

        Random rand = new Random();
        int randomPos = Math.abs(rand.nextInt()) % WHEEL_ORDER.length;

        int pocketIndex = -1;
        for (int i = 0; i < WHEEL_ORDER.length; i++) {
            if (WHEEL_ORDER[i] == randomPos) {
                pocketIndex = i;
                break;
            }
        }

        float degreesPerPocket = 360f / 37f;
        float targetAngle = 360f - pocketIndex * degreesPerPocket;
        float totalRotation = 360f * 5 + targetAngle;

        ObjectAnimator anim = ObjectAnimator.ofFloat(wheel, "rotation", 0f, totalRotation);
        anim.setDuration(4000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();

    }

    private void increaseBet(){
        TextView currBetText = findViewById(R.id.betAmountText);
        if (currBetText.getText().toString().equals("0.1")){
            currBetText.setText("0.2");
        }
        else if (currBetText.getText().toString().equals("0.2")){
            currBetText.setText("0.5");
        }
        else if (Double.parseDouble(currBetText.getText().toString()) == 10.0){
            Toast.makeText(this, "Maximum bet reached", Toast.LENGTH_SHORT).show();
        }
        else{
            double newBet = Double.parseDouble(currBetText.getText().toString()) + 0.5;
            String buttonNewText = newBet+"";
            currBetText.setText(buttonNewText);
        }
    }

    private void decreaseBet(){
        TextView currBetText = findViewById(R.id.betAmountText);
        if (currBetText.getText().toString().equals("0.1")){
            Toast.makeText(this, "Minimum bet reached", Toast.LENGTH_SHORT).show();
        }
        else if (currBetText.getText().toString().equals("0.2")){
            currBetText.setText("0.1");
        }
        else if (currBetText.getText().toString().equals("0.5")){
            currBetText.setText("0.2");
        }
        else{
            double newBet = Double.parseDouble(currBetText.getText().toString()) - 0.5;
            String buttonNewText = newBet+"";
            currBetText.setText(buttonNewText);
        }
    }

    private double fetchLifetimeProfitLoss() {
        try (Socket socket = new Socket("192.168.1.74", 5000)) {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            out.writeObject(new MiscPackage.Request(
                    "get_player_profitLoss",
                    new Object[]{ pl.getPlayerName() }
            ));
            out.flush();

            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            double total = in.readDouble();
            Log.v("ms", "Lifetime P/L received: " + total);
            return total;
        } catch (IOException e) {
            Log.e("ms", "Failed to fetch lifetime P/L", e);
            return 0;  // fall back to 0 if the network fails — don't block exit
        }
    }
}