package com.daniel.proyectofinal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.daniel.proyectofinal.R;

public class ChoiceRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView btnDocente, btnCentro, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_register);

        btnDocente = findViewById(R.id.imgDocente);
        btnCentro = findViewById(R.id.imgCentro);
        btnBack = findViewById(R.id.imgVolverAtras);

        btnDocente.setOnClickListener(this);
        btnCentro.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgDocente:
                startActivity(new Intent(getApplicationContext(), RegisterTeacherActivity.class));
                finish(); // Eliminar aplicación de la pila
                break;
            case R.id.imgCentro:
                startActivity(new Intent(getApplicationContext(), RegisterCenterActivity.class));
                finish();
                break;
            case R.id.imgVolverAtras:
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
    }
}