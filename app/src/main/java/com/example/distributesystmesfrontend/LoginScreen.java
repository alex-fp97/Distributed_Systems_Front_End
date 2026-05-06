package com.example.distributesystmesfrontend;

import static com.example.distributesystmesfrontend.MainMenu.MASTER_HOST;
import static com.example.distributesystmesfrontend.MainMenu.MASTER_PORT;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import GamePackage.Game;

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
                PlayerPackage.Player pl = new PlayerPackage.Player(usernameText.getText().toString(), 100.0);
                Intent i = new Intent(this, MainMenu.class);
                i.putExtra("player", pl);
                startActivity(i);
            }
            else{
                Toast.makeText(this, "Type a username", Toast.LENGTH_SHORT).show();
            }
        });
    }
}