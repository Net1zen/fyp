package com.daniel.proyectofinal.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daniel.proyectofinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView close;
    private TextView post;
    private EditText editTextPosition, editTextDescription;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        close = findViewById(R.id.close);
        post = findViewById(R.id.post);
        editTextPosition = findViewById(R.id.cargo);
        editTextDescription = findViewById(R.id.description);

        close.setOnClickListener(this);
        post.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                startActivity(new Intent(PostActivity.this, HomeActivity.class));
                finish();
                break;
            case R.id.post:
                uploadPost();
                break;
        }
    }

    // Método para subir la publicacion
    private void uploadPost() {
        String position = editTextPosition.getText().toString();
        String description = editTextDescription.getText().toString();

        if (position.isEmpty()) {
            editTextPosition.setError("No puede dejar este campo vacío");
            editTextPosition.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            editTextDescription.setError("No puede dejar este campo vacío");
            editTextDescription.requestFocus();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Subiendo publicación");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Guardar el post en la base de datos de Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        String postId = databaseReference.push().getKey(); // Obtener el id del post

        // Almacenar los campos utilizando un HashMap
        HashMap<String, Object> postMap = new HashMap<>();
        postMap.put("postid", postId);
        postMap.put("position", position);
        postMap.put("description", description);
        postMap.put("postOwner", firebaseUser.getUid()); // Obtener el id del propietario de la publicación

        databaseReference.child(postId).setValue(postMap);

        // Enviar los datos a Firebase
        databaseReference.child(postId).setValue(postMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss(); // Finalizar progressDialog
                Toast.makeText(PostActivity.this, "Se ha subido la publicación correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PostActivity.this, HomeActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss(); // Finalizar progressDialog
                Toast.makeText(PostActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
    }
}