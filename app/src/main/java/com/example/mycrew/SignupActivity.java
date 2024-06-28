package com.example.mycrew;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Patterns;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    EditText inputName, inputEmail, inputUsername, inputPassword;
    Button signup, toLogin;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputName = findViewById(R.id.input_name_signup);
        inputEmail = findViewById(R.id.input_mail_signup);
        inputUsername = findViewById(R.id.input_username_signup);
        inputPassword = findViewById(R.id.input_password_signup);
        signup = findViewById(R.id.button_signup);
        toLogin = findViewById(R.id.button_to_login);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                String name = inputName.getText().toString();
                String email = inputEmail.getText().toString();
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupActivity.this, "Por favor introduzca un email válido.", Toast.LENGTH_SHORT).show();
                    return;
                }

                checkIfUserExists(name, email, username, new UserExistenceCallback() {
                    @Override
                    public void onUserExistenceChecked(boolean userExists) {
                        if (userExists) {
                            Toast.makeText(SignupActivity.this, "El email o nombre de usuario ya están registrados.", Toast.LENGTH_SHORT).show();
                        } else {
                            HelperClass helperClass = new HelperClass(name, email, username, password);
                            reference.child(username).setValue(helperClass);

                            Toast.makeText(SignupActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("password", password);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void checkIfUserExists(String name, String email, String username, UserExistenceCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    HelperClass user = userSnapshot.getValue(HelperClass.class);
                    if (user != null && (user.getEmail().equals(email) || user.getUsername().equals(username))) {
                        userExists = true;
                        break;
                    }
                }
                callback.onUserExistenceChecked(userExists);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SignupActivity.this, "Error de verificación de usuario", Toast.LENGTH_SHORT).show();
                callback.onUserExistenceChecked(true);
            }
        });
    }

    interface UserExistenceCallback {
        void onUserExistenceChecked(boolean userExists);
    }

}