package com.daniel.proyectofinal.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.daniel.proyectofinal.Activity.EditProfileActivity;
import com.daniel.proyectofinal.Activity.HomeActivity;
import com.daniel.proyectofinal.Activity.MainActivity;
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


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final int PICK_PDF_FILE = 2;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String uid;
    private Uri curriculumUri;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private ImageView imageProfile;
    private TextView name, email, phone, dynamicDataAgeOrWebsite, address;

    private Button logout, actualizarDatos, subirCurriculum, changePassword;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        /*FIREBASE*/
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference().child("CVs");
        // Comprobar si hay una sesión activa
        if (firebaseUser==null){
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            getActivity().finish();
        }
        uid = firebaseUser.getUid();

        /*EDIT TEXTS*/
        imageProfile = view.findViewById(R.id.imagenMisDatos);
        name = view.findViewById(R.id.nombreDato);
        email = view.findViewById(R.id.correoDato);
        phone = view.findViewById(R.id.telefonoDato);
        dynamicDataAgeOrWebsite = view.findViewById(R.id.edadOSitioWebDato);
        address = view.findViewById(R.id.direccionDato);

        /*BOTONES*/
        logout = (Button) view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(this);
        actualizarDatos = (Button) view.findViewById(R.id.btnActualizarDatos);
        actualizarDatos.setOnClickListener(this);
        subirCurriculum = (Button) view.findViewById(R.id.subirCurriculum);
        subirCurriculum.setOnClickListener(this);
        changePassword = (Button) view.findViewById(R.id.btnActualizarPassword);
        changePassword.setOnClickListener(this);

        // Comprobar si es un usuario o un centro educativo
        checkTypeOfUser();
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                cerrarSesion();
                break;
            case R.id.btnActualizarDatos:
                actualizarDatos();
                break;
            case R.id.subirCurriculum:
                chooseCurriculum();
                break;
        }
    }

    private void chooseCurriculum() {
        Intent explorerIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        explorerIntent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(explorerIntent, "Selecciona un documento"), PICK_PDF_FILE);
    }

    private void checkTypeOfUser(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("teachers").getValue().equals(firebaseUser.getUid())){
                    Log.d("docente", "El usuario actual es un docente");
                    getTeacherInfo();
                } else {
                    Log.d("centro", "El usuario actual es un centro educativo");
                    getCenterInfo();
                    subirCurriculum.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("checkTypeOfUser", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void getCenterInfo() {
        databaseReference.child("centers").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Center center = dataSnapshot.getValue(Center.class);
                name.setText(center.getName());
                dynamicDataAgeOrWebsite.setText(center.getWebsite());
                email.setText(center.getEmail());
                phone.setText(center.getPhone());
                address.setText(center.getAddress());
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
                Log.w("getCenterInfo", "loadPost:onCancelled", error.toException());
            }
        });
    }

    /*Método que obtiene los datos del usuario y los muestra*/
    private void getTeacherInfo() {
        databaseReference.child("teachers").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                name.setText(teacher.getName() + " " + teacher.getSurname());
                dynamicDataAgeOrWebsite.setText(teacher.getAge() + " Años");
                email.setText(teacher.getEmail());
                phone.setText(teacher.getPhone());
                address.setVisibility(View.GONE);
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
                Log.w("getTeacherInfo", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void actualizarDatos() {
        startActivity(new Intent(getActivity(), EditProfileActivity.class));
    }

    private void cerrarSesion() {
        firebaseAuth.signOut(); // Cierra sesión del usuario activo actualmente
        // Luego de cerrar sesión nos dirige al MainActivity
        Toast.makeText(getActivity(), "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK){
            curriculumUri = data.getData();
            uploadCurriculum();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No se ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }

    /*Metodo que carga el documento pdf y nos devuelve el url del pdf para setear este url en el campo curriculum del docente*/
    private void uploadCurriculum() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Cargando documento...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (curriculumUri != null) {
            // Path de la imagen, en el nombre contendra la la fecha
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".pdf");
            uploadTask = fileReference.putFile(curriculumUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String curriculumUrl = uri.toString();
                                    databaseReference.child("teachers").child(firebaseUser.getUid()).child("curriculum").setValue(curriculumUrl);
                                    progressDialog.dismiss();
                                }
                            });
                        }
                    }
                }});
        }
    }
}