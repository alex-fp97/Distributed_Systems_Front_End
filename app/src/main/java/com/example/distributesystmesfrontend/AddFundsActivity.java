package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddFundsActivity extends AppCompatActivity {

    private EditText amountInput;
    private TextView currentBalanceText;
    private PlayerPackage.Player pl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_funds);

        amountInput        = findViewById(R.id.amountInput);
        currentBalanceText = findViewById(R.id.currentBalanceText);

        pl = (PlayerPackage.Player) getIntent().getSerializableExtra("player");
        if (pl == null) {
            Toast.makeText(this, "No player loaded", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateBalanceDisplay();

        // Quick amount buttons
        findViewById(R.id.btn10).setOnClickListener(v -> amountInput.setText("10.00"));
        findViewById(R.id.btn25).setOnClickListener(v -> amountInput.setText("25.00"));
        findViewById(R.id.btn50).setOnClickListener(v -> amountInput.setText("50.00"));
        findViewById(R.id.btn100).setOnClickListener(v -> amountInput.setText("100.00"));

        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());

        findViewById(R.id.addFundsButton).setOnClickListener(v -> addFunds());
    }

    private void addFunds() {
        String amountStr = amountInput.getText().toString().trim();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Enter a positive amount", Toast.LENGTH_SHORT).show();
            return;
        }

        pl.addBalance(amount);

        Toast.makeText(this,
                String.format("Added $%.2f → New balance: $%.2f", amount, pl.getBalance()),
                Toast.LENGTH_SHORT).show();

        // Return updated player to MainMenu
        Intent data = new Intent();
        data.putExtra("player", pl);
        data.putExtra("newBalance", pl.getBalance());
        setResult(RESULT_OK, data);
        finish();
    }

    private void updateBalanceDisplay() {
        currentBalanceText.setText(String.format("Current Balance: $%.2f", pl.getBalance()));
    }
}