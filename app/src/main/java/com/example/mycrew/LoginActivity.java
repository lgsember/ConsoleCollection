package com.example.mycrew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText inputUsername, inputPassword;
    Button login, toSignup;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputUsername = findViewById(R.id.input_username_login);
        inputPassword = findViewById(R.id.input_password_login);
        login = findViewById(R.id.button_login);
        toSignup = findViewById(R.id.button_to_signup);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        if (username != null && password != null) {
            inputUsername.setText(username);
            inputPassword.setText(password);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateUsername() || !validatePassword()){
                    return;
                } else {
                    checkUser();
                }
            }
        });

        toSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

    }

    public Boolean validateUsername(){
        String username = inputUsername.getText().toString();
        if(username.isEmpty()){
            inputUsername.setError("Campo requerido");
            return false;
        } else {
            inputUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword(){
        String password = inputPassword.getText().toString();
        if(password.isEmpty()){
            inputPassword.setError("Campo requerido");
            return false;
        } else {
            inputPassword.setError(null);
            return true;
        }
    }

    public void checkUser(){
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(username);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordDB = userSnapshot.child("password").getValue(String.class);

                        if (passwordDB != null && passwordDB.equals(password)) {
                            inputPassword.setError(null);

                            String nameDB = userSnapshot.child("name").getValue(String.class);
                            String emailDB = userSnapshot.child("email").getValue(String.class);
                            String usernameDB = userSnapshot.child("username").getValue(String.class);

                            Log.d("MainActivity", "nameDB: " + nameDB);
                            Log.d("MainActivity", "emailDB: " + emailDB);
                            Log.d("MainActivity", "usernameDB: " + usernameDB);

                            Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("name", nameDB);
                            intent.putExtra("email", emailDB);
                            intent.putExtra("username", usernameDB);
                            intent.putExtra("password", passwordDB);
                            startActivity(intent);
                        } else {
                            inputPassword.setError("Contraseña incorrecta");
                        }
                    }
                } else {
                    inputUsername.setError("Usuario no registrado");
                    inputUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MainActivity", "Database error: " + error.getMessage());
            }
        });

    }

}