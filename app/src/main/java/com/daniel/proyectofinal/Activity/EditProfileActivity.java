package com.daniel.proyectofinal.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.daniel.proyectofinal.Model.Center;
import com.daniel.proyectofinal.Model.Teacher;
import com.daniel.proyectofinal.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_FILE = 1;
    private ImageView btnClose;
    private CircleImageView imageProfile;
    private TextView save, changePhoto;
    private EditText editTextName, editTextSurnameOrAddress, editTextAgeOrWebsite, editTextPhone, editTextEmail;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        btnClose = findViewById(R.id.closeEdit);
        imageProfile = findViewById(R.id.imageProfile);
        save = findViewById(R.id.save);
        changePhoto = findViewById(R.id.changePhoto);

        editTextName = findViewById(R.id.nombre);
        editTextSurnameOrAddress = findViewById(R.id.apellidoODireccion);
        editTextAgeOrWebsite = findViewById(R.id.edadOSitioWeb);
        editTextPhone = findViewById(R.id.telefono);
        editTextEmail = findViewById(R.id.correo);

        changePhoto.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        save.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        // Obtenemos el usuario de la sesión activa
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Obtenermos la referencia del storage
        storageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");

        // Comprobar si es un docente o un centro educativo
        checkTypeOfUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.closeEdit:
                finish();
                break;
            case R.id.save:
                updateProfile();
                finish();
                break;
            case R.id.changePhoto:
                // Elegir una imagen de la galeria
                selectPictureFromGallery();
                break;
        }
    }

    private void checkTypeOfUser(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("teachers").getValue().equals(firebaseUser.getUid())){
                    Log.d("docente", "El usuario actual es un docente");
                    getTeacherData();
                } else {
                    Log.d("centro", "El usuario actual es un centro educativo");
                    getCenterData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("checkKindOfUser", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void getCenterData() {
        databaseReference.child("centers").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Center center = dataSnapshot.getValue(Center.class);
                editTextName.setText(center.getName());
                editTextSurnameOrAddress.setText(center.getAddress());
                editTextAgeOrWebsite.setText(center.getWebsite());
                editTextPhone.setText(center.getPhone());
                editTextEmail.setText(center.getEmail());
                try {
                    // Si existe la imagen
                    Picasso.get().load(center.getImage()).placeholder(R.drawable.foto_perfil).into(imageProfile);
                } catch (Exception exception) {
                    // Si no existe la imagen
                    Picasso.get().load(R.drawable.servicios_centroseducativos).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("getCenterData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void getTeacherData() {
        databaseReference.child("teachers").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                editTextName.setText(teacher.getName());
                editTextSurnameOrAddress.setText(teacher.getSurname());
                editTextAgeOrWebsite.setText(teacher.getAge());
                editTextPhone.setText(teacher.getPhone());
                editTextEmail.setText(teacher.getEmail());
                try {
                    // Si existe la imagen
                    Picasso.get().load(teacher.getImage()).placeholder(R.drawable.foto_perfil).into(imageProfile);
                } catch (Exception exception) {
                    // Si no existe la imagen
                    Picasso.get().load(R.drawable.foto_perfil).into(imageProfile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("getTeacherData", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void updateProfile() {
        HashMap<String, Object> editProfileMap = new HashMap<>();
        editProfileMap.put("name", editTextName.getText().toString());
        editProfileMap.put("surname", editTextSurnameOrAddress.getText().toString());
        editProfileMap.put("age", editTextAgeOrWebsite.getText().toString());
        editProfileMap.put("phone", editTextPhone.getText().toString());
        // Actualizar los datos en la base de datos
        databaseReference.child("teachers").child(firebaseUser.getUid()).updateChildren(editProfileMap);

    }

    // Se llama cuando el usuario ha elegido una imagen de la galeria
    // Obtener la foto seleccionada de la galeria, guardar en un Uri y despues se hace un push al storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_FILE && resultCode == RESULT_OK) {
            imageUri = data.getData();
            uploadImage();
        } else {
            Toast.makeText(this, "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    /*Metodo que carga la imagen y nos devuelve el url de la imagen para setear este url en la imagen del usuario*/
    private void uploadImage() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando imagen...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (imageUri != null) {
            // Path de la imagen, en el nombre contendra la la fecha
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpeg");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();
                                    databaseReference.child("teachers").child(firebaseUser.getUid()).child("image").setValue(imageUrl);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }});
        }
    }

    /*Método para abrir la galeria y seleccionar una imagen*/
    private void selectPictureFromGallery() {
        // Obtener el contenido de la galeria
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*"); // Formato de imagen
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(galleryIntent,"Selecciona una foto"), PICK_IMAGE_FILE);
    }

}