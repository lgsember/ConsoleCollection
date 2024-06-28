package com.example.mycrew;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateActivity extends AppCompatActivity {

    ImageView updateConsolePicture;
    EditText updateConsoleNickname, updateConsoleBrand, updateConsoleName, updateConsoleModel, updateConsoleStatus, updateConsoleDescription;
    Button update, cancelUpdate;
    String imageURL, nickname, brand, name, model, status, description;
    String key, oldImageURL, owner;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateConsolePicture = findViewById(R.id.edit_console_picture);
        updateConsoleNickname = findViewById(R.id.edit_console_nickname);
        updateConsoleBrand = findViewById(R.id.edit_console_brand);
        updateConsoleName = findViewById(R.id.edit_console_name);
        updateConsoleModel = findViewById(R.id.edit_console_model);
        updateConsoleStatus = findViewById(R.id.edit_console_status);
        updateConsoleDescription = findViewById(R.id.edit_console_description);

        cancelUpdate = findViewById(R.id.button_cancel_update);
        cancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>(){
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                uri = data.getData();
                                updateConsolePicture.setImageURI(uri);
                            }
                        } else {
                            Toast.makeText(UpdateActivity.this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateConsolePicture);
            updateConsoleNickname.setText(bundle.getString("Nickname"));
            updateConsoleBrand.setText(bundle.getString("Brand"));
            updateConsoleName.setText(bundle.getString("Name"));
            updateConsoleModel.setText(bundle.getString("Model"));
            updateConsoleStatus.setText(bundle.getString("Status"));
            updateConsoleDescription.setText(bundle.getString("Description"));
            oldImageURL = bundle.getString("Image");
            key = bundle.getString("Key");
            owner = bundle.getString("Owner");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("consoles").child(key);

        updateConsolePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        update = findViewById(R.id.button_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri == null) {
                    updateConsoleWithoutImageChange();
                } else {
                    saveData();
                }
            }
        });
    }

    public void saveData(){
        storageReference = FirebaseStorage.getInstance().getReference().child("Android Images").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                updateConsole();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void updateConsole(){
        nickname = updateConsoleNickname.getText().toString().trim().isEmpty() ? nickname : updateConsoleNickname.getText().toString().trim();
        brand = updateConsoleBrand.getText().toString().trim().isEmpty() ? brand : updateConsoleBrand.getText().toString().trim();
        name = updateConsoleName.getText().toString().trim().isEmpty() ? name : updateConsoleName.getText().toString().trim();
        model = updateConsoleModel.getText().toString().trim().isEmpty() ? model : updateConsoleModel.getText().toString().trim();
        status = updateConsoleStatus.getText().toString().trim().isEmpty() ? status : updateConsoleStatus.getText().toString().trim();
        description = updateConsoleDescription.getText().toString().trim().isEmpty() ? description : updateConsoleDescription.getText().toString().trim();

        ItemClass itemClass = new ItemClass(name, brand, model, nickname, description, status, imageURL, owner);

        databaseReference.setValue(itemClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageURL);
                        reference.delete();
                    }
                    Toast.makeText(UpdateActivity.this, "Consola actualizada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateActivity.this, "No se ha podido actualizar la consola", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateConsoleWithoutImageChange() {
        nickname = updateConsoleNickname.getText().toString().trim().isEmpty() ? nickname : updateConsoleNickname.getText().toString().trim();
        brand = updateConsoleBrand.getText().toString().trim().isEmpty() ? brand : updateConsoleBrand.getText().toString().trim();
        name = updateConsoleName.getText().toString().trim().isEmpty() ? name : updateConsoleName.getText().toString().trim();
        model = updateConsoleModel.getText().toString().trim().isEmpty() ? model : updateConsoleModel.getText().toString().trim();
        status = updateConsoleStatus.getText().toString().trim().isEmpty() ? status : updateConsoleStatus.getText().toString().trim();
        description = updateConsoleDescription.getText().toString().trim().isEmpty() ? description : updateConsoleDescription.getText().toString().trim();

        ItemClass itemClass = new ItemClass(name, brand, model, nickname, description, status, oldImageURL, owner);

        databaseReference.setValue(itemClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(UpdateActivity.this, "Consola actualizada", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(UpdateActivity.this, "No se ha podido actualizar la consola", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}