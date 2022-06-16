package com.daniel.proyectofinal.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.daniel.proyectofinal.Activity.EditProfileActivity;
import com.daniel.proyectofinal.Activity.MainActivity;
import com.daniel.proyectofinal.Model.Teacher;
import com.daniel.proyectofinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileCenterFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uid;

    private ImageView imageProfile;
    private TextView name, email, website, phone, address;

    private Button logout, actualizarDatos;

    public ProfileCenterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_center, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = firebaseUser.getUid();

        firebaseDatabase = firebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        imageProfile = view.findViewById(R.id.imagenMisDatos);
        name = view.findViewById(R.id.nombreDato);
        email = view.findViewById(R.id.correoDato);
        website = view.findViewById(R.id.sitiowebDato);
        phone = view.findViewById(R.id.telefonoDato);
        address = view.findViewById(R.id.direccionDato);

        logout = (Button) view.findViewById(R.id.btnLogout);
        logout.setOnClickListener(this);
        actualizarDatos = (Button) view.findViewById(R.id.btnActualizarDatos);
        actualizarDatos.setOnClickListener(this);

        userInfo();
        return view;
    }

    /*Método que obtiene los datos del usuario y los muestra*/
    private void userInfo() {
        Log.d("userInfo", "Entra al método user Info de ProfileFragment");
        databaseReference.child("users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                Log.d("onDataChange", "Entra al método onDataChange de ProfileFragment");
                name.setText(teacher.getName());
                email.setText(teacher.getEmail());
                website.setText(teacher.getAge() + " Años");
                phone.setText(teacher.getPhone());

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogout:
                cerrarSesion();
                break;
            case R.id.btnActualizarDatos:
                actualizarDatos();
                break;
        }

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
}