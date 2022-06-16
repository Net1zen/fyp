package com.daniel.proyectofinal.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.daniel.proyectofinal.Model.Teacher;
import com.daniel.proyectofinal.R;
import com.daniel.proyectofinal.Fragments.FeedFragment;
import com.daniel.proyectofinal.Fragments.ProfileFragment;
import com.daniel.proyectofinal.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Por defecto se mostrara el FeedFragment
        showSelectedFragment(new FeedFragment());

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        /**/
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        showSelectedFragment(new FeedFragment());
                        break;
                    case R.id.nav_search:
                        showSelectedFragment(new SearchFragment());
                        break;
                    case R.id.nav_create:
                        // Solo los centros pueden hacer posts
                        checkTypeOfUser();
                        break;
                    case R.id.nav_profile:
                        showSelectedFragment(new ProfileFragment());
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        // Llamar al método de verificacion de sesión para que se ejecute cuando se inicie la actividad
        super.onStart();
        verificacionInicioSesion();
    }

    private void checkTypeOfUser(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().equals(Teacher.class)){
                    Log.d("docente", "El usuario actual es un docente");
                    Toast.makeText(HomeActivity.this, "Solo los centros educativos pueden crear publicaciones", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("centro", "El usuario actual es un centro educativo");
                    Intent postActivity = new Intent(HomeActivity.this, PostActivity.class);
                    startActivity(postActivity);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("checkKindOfUser", "loadPost:onCancelled", error.toException());
            }
        });
    }


    // Método para verificar si el usuario ya ha iniciado sesión previamente
    private void verificacionInicioSesion(){
        // Si el usuario ha iniciado sesión nos dirige directamente a esta actividad
        if (firebaseUser == null){
        // En caso de que no haya iniciado sesión nos dirige a la pantalla de Login que en este caso es la MainActivity
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish(); // Quitarla de la  activity de la pila
        }
    }

    /* Metodo que permite elegir el fragment */
    private void showSelectedFragment(Fragment fragment){
        // Dentro de la vista cointainerView muestre el fragment que el usuario elija
        getSupportFragmentManager().beginTransaction().replace(R.id.containerView, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}