package com.example.conectamovilevaluacion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class Perfil extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private ImageView profileImage,volver;
    private EditText nombreEditText;
    private EditText descripcionEditText;
    private Button uploadButton;
    private Button modificarButton;
    private Button guardarButton;
    private boolean cambiosRealizados = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid());

        profileImage = findViewById(R.id.profile_image_view);
        volver = findViewById(R.id.imgVolver2);
        nombreEditText = findViewById(R.id.nombre_edit_text);
        descripcionEditText = findViewById(R.id.descripcion_edit_text);
        uploadButton = findViewById(R.id.upload_button);
        modificarButton = findViewById(R.id.btnModificar);
        guardarButton = findViewById(R.id.btnGuardar);

        uploadButton.setOnClickListener(view -> seleccionarImagen());
        modificarButton.setOnClickListener(view -> modificarDatos());
        guardarButton.setOnClickListener(view -> guardarDatos());

        obtenerDatosPerfil();
        obtenerImagenPerfil();
        volver();
    }

    private void obtenerDatosPerfil() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nombre = dataSnapshot.child("nombre").getValue(String.class);
                    String descripcion = dataSnapshot.child("descripcion").getValue(String.class);

                    nombreEditText.setText(nombre);
                    descripcionEditText.setText(descripcion);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Manejar errores de base de datos
            }
        });
    }
    private void volver(){
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Perfil.this, Menu.class);
                startActivity(intent);
            }
        });
    }

    private void obtenerImagenPerfil() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String savedImageUrl = sharedPreferences.getString("imageURL", "");

        if (!savedImageUrl.isEmpty()) {
            Picasso.with(this).load(savedImageUrl).into(profileImage);
        }
    }

    private void seleccionarImagen() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar imagen"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imagenUri = data.getData();
            subirImagenFirebase(imagenUri);
        }
    }

    private void subirImagenFirebase(Uri filePath) {
        if (filePath != null) {
            StorageReference ref = mStorageRef.child("images/" + mAuth.getCurrentUser().getUid());

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("imageURL", imageUrl);
                            editor.apply();

                            Picasso.with(this).load(imageUrl).into(profileImage);
                        });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Perfil.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void modificarDatos() {
        String nuevoNombre = nombreEditText.getText().toString().trim();
        String nuevaDescripcion = descripcionEditText.getText().toString().trim();

        nombreEditText.setText(nuevoNombre);
        descripcionEditText.setText(nuevaDescripcion);

        Toast.makeText(this, "Datos modificados", Toast.LENGTH_SHORT).show();
        cambiosRealizados = true;
    }

    private void guardarDatos() {
        if (cambiosRealizados) {
            String nuevoNombre = nombreEditText.getText().toString().trim();
            String nuevaDescripcion = descripcionEditText.getText().toString().trim();

            databaseReference.child("nombre").setValue(nuevoNombre);
            databaseReference.child("descripcion").setValue(nuevaDescripcion);

            cambiosRealizados = false;
            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se han realizado cambios", Toast.LENGTH_SHORT).show();
        }
    }
}
