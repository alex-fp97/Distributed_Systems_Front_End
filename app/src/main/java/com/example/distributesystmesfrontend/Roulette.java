package com.example.distributesystmesfrontend;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Roulette extends AppCompatActivity {

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

        String username = getIntent().getStringExtra("username");
        double balance = getIntent().getDoubleExtra("balance", 0.0);

        findViewById(R.id.increaseBetButton).setOnClickListener(v->increaseBet());
        findViewById(R.id.decreaseBetButton).setOnClickListener(v->decreaseBet());

        Button bet = findViewById(R.id.betButton);
        bet.setOnClickListener(v->{
            spinWheel(23);
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

    private void spinWheel(int landingNumber) {
        ImageView wheel = findViewById(R.id.rouletteWheel);

        int pocketIndex = -1;
        for (int i = 0; i < WHEEL_ORDER.length; i++) {
            if (WHEEL_ORDER[i] == landingNumber) {
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
        TextView currBetText= findViewById(R.id.betAmountText);
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
}