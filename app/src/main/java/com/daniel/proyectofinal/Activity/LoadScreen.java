package com.daniel.proyectofinal.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.daniel.proyectofinal.R;

public class LoadScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);
        // Esto se representa en segundos
        final int duracion = 2500;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Esto se ejecutará pasado los segundos que hemos establecido
                Intent intent = new Intent(LoadScreen.this, HomeActivity.class);
                startActivity(intent);
                finish();
                // En la actividad HomeActivity hace la condición del onStart
            }
        }, duracion);
    }
}