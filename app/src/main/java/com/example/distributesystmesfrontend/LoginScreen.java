package com.example.distributesystmesfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginScreen extends AppCompatActivity {

    EditText usernameText;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameText = findViewById(R.id.usernameContainer);
        button = findViewById(R.id.loginButton);

        button.setOnClickListener(v->{
            if (!usernameText.getText().toString().isEmpty()) {
                Intent i = new Intent(this, MainMenu.class);
                i.putExtra("username", usernameText.getText().toString());
                startActivity(i);
            }
            else{
                Toast.makeText(this, "Type a username", Toast.LENGTH_SHORT).show();
            }
        });
    }
}