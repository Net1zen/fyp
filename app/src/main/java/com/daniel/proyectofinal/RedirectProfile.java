package com.daniel.proyectofinal;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daniel.proyectofinal.Model.Center;
import com.daniel.proyectofinal.Model.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class RedirectProfile extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private DatabaseReference databaseReference;
    private String uid;
    private String userType;

    private ImageView imageProfile, imgBack;
    private TextView nameData, emailData, phoneData, dynamicDataAgeOrWebsite, addressData;

    private Button btnSitioWeb, btnVerMapa, btnEnviarMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        /*Obtener los datos transferidos mediante el intent (Los datos transferidos por medio de un intent se envian como Bundle)*/
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            // Obtenemos los datos del Bundle, en este caso el userId
            uid = intent.getString("userId");
            userType = intent.getString("userType");
            if (uid.equals("none")) {
                uid = firebaseUser.getUid();
            }
        }

        imageProfile = findViewById(R.id.imagenMisDatos);
        nameData = findViewById(R.id.nombreDato);
        dynamicDataAgeOrWebsite = findViewById(R.id.edadOSitioWebDato);
        emailData = findViewById(R.id.correoDato);
        phoneData = findViewById(R.id.telefonoDato);
        addressData = findViewById(R.id.direccionDato);

        imgBack = findViewById(R.id.close);
        imgBack.setOnClickListener(this);
        btnSitioWeb = findViewById(R.id.btnSitioWeb);
        btnSitioWeb.setOnClickListener(this);
        btnVerMapa = findViewById(R.id.btnVerMapa);
        btnVerMapa.setOnClickListener(this);
        btnEnviarMensaje = findViewById(R.id.btnEnviarMensaje);
        btnEnviarMensaje.setOnClickListener(this);

        // Comprobar si es un usuario o un centro educativo
        checkTypeofUser();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.close:
                finish();
                break;
            case R.id.btnSitioWeb:
                redirectToWebiste();
                break;
            case R.id.btnVerMapa:
                showAdressOnGoogleMaps();
                break;
            case R.id.btnEnviarMensaje:
                sendMessage();
                break;
        }
    }

    private void checkTypeofUser() {
        if (userType.equals("docente")) {
            getTeacherInfo();
        } else {
            getCenterInfo();
            btnSitioWeb.setVisibility(View.VISIBLE);
            btnVerMapa.setVisibility(View.VISIBLE);
        }
    }

    private void sendMessage() {
        Intent chatActivity = new Intent(RedirectProfile.this, ChatActivity.class);
        chatActivity.putExtra("uid", uid);
        chatActivity.putExtra("profileName", nameData.getText().toString());
        startActivity(chatActivity);
    }

    private void getCenterInfo() {
        databaseReference.child("centers").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Center center = dataSnapshot.getValue(Center.class);
                nameData.setText(center.getName());
                emailData.setText(center.getEmail());
                dynamicDataAgeOrWebsite.setText(center.getWebsite());
                phoneData.setText(center.getPhone());
                addressData.setText(center.getAddress());
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
                Toast.makeText(RedirectProfile.this, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTeacherInfo() {
        databaseReference.child("teachers").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Teacher teacher = dataSnapshot.getValue(Teacher.class);
                Log.d("onDataChange", "Entra al método onDataChange de ProfileFragment");
                nameData.setText(teacher.getName() + " " + teacher.getSurname());
                emailData.setText(teacher.getEmail());
                dynamicDataAgeOrWebsite.setText(teacher.getAge() + " Años");
                phoneData.setText(teacher.getPhone());
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

            }
        });
    }

    private void redirectToWebiste() {
        String urlWebsite = dynamicDataAgeOrWebsite.getText().toString();
        System.out.println(urlWebsite);
        Uri uri = Uri.parse(urlWebsite);
        try {
            Intent intentWebsite = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intentWebsite);
        } catch (ActivityNotFoundException exception) {
            Toast.makeText(this, "No se ha podido acceder al sitio", Toast.LENGTH_SHORT).show();
        }
    }

    private void showAdressOnGoogleMaps() {
        String address = addressData.getText().toString();
        String googleMapsAddress = "http://maps.google.com/maps?q=" + address;
        Intent googleMapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleMapsAddress));
        startActivity(googleMapsIntent);
    }
}