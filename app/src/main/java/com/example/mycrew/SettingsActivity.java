package com.example.mycrew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import com.google.firebase.database.ValueEventListener;

public class SettingsActivity extends AppCompatActivity {

    EditText editName, editEmail, editPassword;
    Button buttonEditUser, buttonDeleteUser, buttonCancelEditUser;
    FirebaseDatabase database;
    DatabaseReference reference;
    String username, name, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editName = findViewById(R.id.edit_name_user);
        editEmail = findViewById(R.id.edit_mail_user);
        editPassword = findViewById(R.id.edit_password_user);
        buttonEditUser = findViewById(R.id.button_edit_user);

        buttonCancelEditUser = findViewById(R.id.button_cancel_edit_user);
        buttonCancelEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        editName.setText(name);
        editEmail.setText(email);
        editPassword.setText(password);

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users");

        buttonEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString();
                String newEmail = editEmail.getText().toString();
                String newPassword = editPassword.getText().toString();

                checkIfEmailExists(newName, newEmail, newPassword);
            }
        });

        buttonDeleteUser = findViewById(R.id.button_delete_user);
        buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void checkIfEmailExists(String newName, String newEmail, String newPassword) {
        if (!Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            Toast.makeText(SettingsActivity.this, "Por favor introduzca un email válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.orderByChild("email").equalTo(newEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean emailExists = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String existingUsername = dataSnapshot.getKey();
                    if (!existingUsername.equals(username)) {
                        emailExists = true;
                        break;
                    }
                }

                if (emailExists) {
                    Toast.makeText(SettingsActivity.this, "El email ya está siendo utilizado por otro usuario.", Toast.LENGTH_SHORT).show();
                } else {
                    HelperClass updatedUser = new HelperClass(newName, newEmail, username, newPassword);

                    reference.child(username).setValue(updatedUser);

                    Toast.makeText(SettingsActivity.this, "Datos del usuario actualizados exitosamente.", Toast.LENGTH_SHORT).show();

                    name = newName;
                    email = newEmail;
                    password = newPassword;

                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsActivity.this, "Error al comprobar email duplicado.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Usuario");
        builder.setMessage("¿Está seguro de que desea eliminar su cuenta? Esta acción no se puede deshacer.");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteUser();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void deleteUser() {
        DatabaseReference consolesReference = FirebaseDatabase.getInstance().getReference("consoles");
        consolesReference.orderByChild("owner").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    dataSnapshot.getRef().removeValue();
                }

                reference.child(username).removeValue();

                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}