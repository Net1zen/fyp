package com.daniel.proyectofinal.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.daniel.proyectofinal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterTeacherActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private EditText editTextName, editTextAge, editTextEmail, editTextPassword, editTextPhone, editTextSurname;
    private Button btnRegister;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        firebaseAuth = FirebaseAuth.getInstance();

        // INSTANCIAR VISTAS
        editTextName = findViewById(R.id.nombre);
        editTextSurname = findViewById(R.id.apellido);
        editTextAge = findViewById(R.id.edad);
        editTextPhone = findViewById(R.id.telefono);
        editTextEmail = findViewById(R.id.correo);
        editTextPassword = findViewById(R.id.password);

        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(this);
        btnBack = findViewById(R.id.close);
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                validateFields();
                break;
            case R.id.close:
                startActivity(new Intent(getApplicationContext(), ChoiceRegisterActivity.class));
                finish();
                break;
        }
    }

    private void validateFields() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String name = editTextName.getText().toString();
        String surname = editTextSurname.getText().toString();
        String age = editTextAge.getText().toString();
        String phone = editTextPhone.getText().toString();

        // Validamos los datos introducidos
        if (name.isEmpty()) {
            editTextName.setError("No puede dejar este campo vacío");
            editTextName.requestFocus();
            return;
        }
        if (surname.isEmpty()) {
            editTextSurname.setError("No puede dejar este campo vacío");
            editTextSurname.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            editTextAge.setError("No puede dejar este campo vacío");
            editTextAge.requestFocus();
            return;
        }
        if (phone.isEmpty()) {
            editTextPhone.setError("No puede dejar este campo vacío");
            editTextPhone.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError("Escriba un correo electrónico");
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Email no válido");
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError("Escriba una contraseña");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            editTextPassword.setError("Longitud minima de 6 caracteres");
            editTextPassword.requestFocus();
            return;
        } else {
            registerUser(email, password);
        }
    }

    /* Método para registrar el usuario*/
    private void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*Si el registro es exitoso*/
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            assert user != null;    // Afirmamos que el usuario no es nulo
                            // Datos que vamos a registrar
                            String uid = user.getUid(); // Obtenemos el uid del usuario
                            String email = editTextEmail.getText().toString();
                            String password = editTextPassword.getText().toString();
                            String name = editTextName.getText().toString();
                            String surname = editTextSurname.getText().toString();
                            String age = editTextAge.getText().toString();
                            String phone = editTextPhone.getText().toString();

                            /*Creamos un HashMap para enviar los datos a Firebase*/
                            HashMap<Object, String> userData = new HashMap<>();
                            userData.put("uid", uid);
                            userData.put("email", email);
                            userData.put("password", password);
                            userData.put("name", name);
                            userData.put("surname", surname);
                            userData.put("age", age);
                            userData.put("phone", phone);
                            userData.put("isEducationalCenter", "false");
                            // La imagen de momento estará vacía
                            userData.put("image", "");

                            // Inicializamos la instancia de la base de datos de Firebase
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            // Creamos la base de datos
                            DatabaseReference reference = database.getReference("teachers");
                            // El nombre de la base de datos "no relacional es users"
                            reference.child(uid).setValue(userData);
                            Toast.makeText(RegisterTeacherActivity.this, "Se ha registrado exitosamente", Toast.LENGTH_SHORT).show();
                            // Una vez registrado, se nos enviará al MainActivity
                            startActivity(new Intent(RegisterTeacherActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterTeacherActivity.this, "Algo ha salido mal", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(RegisterTeacherActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}