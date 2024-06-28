package com.example.mycrew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DetailActivity extends AppCompatActivity {

    ImageView readConsolePicture;
    TextView readConsoleNickname, readConsoleBrand, readConsoleName, readConsoleModel, readConsoleStatus, readConsoleDescription;
    Button edit, delete, close;
    String key = "", imageURL = "", owner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readConsolePicture = findViewById(R.id.read_console_picture);
        readConsoleNickname = findViewById(R.id.read_console_nickname);
        readConsoleBrand = findViewById(R.id.read_console_brand);
        readConsoleName = findViewById(R.id.read_console_name);
        readConsoleModel = findViewById(R.id.read_console_model);
        readConsoleStatus = findViewById(R.id.read_console_status);
        readConsoleDescription = findViewById(R.id.read_console_description);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(this).load(bundle.getString("Image")).into(readConsolePicture);
            readConsoleNickname.setText(bundle.getString("Nickname"));
            readConsoleBrand.setText(bundle.getString("Brand"));
            readConsoleName.setText(bundle.getString("Name"));
            readConsoleModel.setText(bundle.getString("Model"));
            readConsoleStatus.setText(bundle.getString("Status"));
            readConsoleDescription.setText(bundle.getString("Description"));
            imageURL = bundle.getString("Image");
            owner = bundle.getString("Owner");
            key = bundle.getString("Key");
        }

        close = findViewById(R.id.button_close_read);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        delete = findViewById(R.id.button_delete_console);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        edit = findViewById(R.id.button_edit_console);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, UpdateActivity.class);
                intent.putExtra("Image", imageURL);
                intent.putExtra("Nickname", readConsoleNickname.getText().toString());
                intent.putExtra("Name", readConsoleName.getText().toString());
                intent.putExtra("Brand", readConsoleBrand.getText().toString());
                intent.putExtra("Model", readConsoleModel.getText().toString());
                intent.putExtra("Status", readConsoleStatus.getText().toString());
                intent.putExtra("Description", readConsoleDescription.getText().toString());
                intent.putExtra("Owner", owner);
                intent.putExtra("Key", key);
                startActivity(intent);
                finish();
            }
        });

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Eliminar Consola");
        builder.setMessage("¿Está seguro de que desea eliminar esta consola? Esta acción no se puede deshacer.");
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteConsole();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    private void deleteConsole() {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("consoles");
        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageReference = storage.getReferenceFromUrl(imageURL);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(key).removeValue();
                Toast.makeText(DetailActivity.this, "Consola eliminada", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });
    }

}