package com.daniel.proyectofinal.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Adapter.MessageChatAdapter;
import com.daniel.proyectofinal.Model.ChatMessage;
import com.daniel.proyectofinal.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private CircleImageView profilePicture;
    private TextView chatProfileName;
    private RecyclerView recyclerViewMessages;
    private EditText txtMessage;
    private Button btnSend;
    private MessageChatAdapter messageAdapter;
    private String uid;
    private String txtChatProfileName;
    private String urlProfilePicture;
    private String txtSenderName;

    /*Firebase*/
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        profilePicture = findViewById(R.id.fotoPerfil);
        chatProfileName = findViewById(R.id.nombrePerfilChat);
        recyclerViewMessages = findViewById(R.id.recycler_view_messages);
        txtMessage = findViewById(R.id.txtMensaje);
        btnSend = findViewById(R.id.btnEnviar);

        /*Firebase*/
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("chat");

        /*Obtener los datos transferidos mediante el intent (Los datos transferidos por medio de un intent se envian como Bundle)*/
        Bundle intent = getIntent().getExtras();
        if (intent != null) {
            // Obtenemos los datos del Bundle, en este caso el userId
            uid = intent.getString("uid");
            txtChatProfileName = intent.getString("profileName");
            txtSenderName = intent.getString("senderName");
        }
        messageAdapter = new MessageChatAdapter(this, txtSenderName);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMessages.setLayoutManager(linearLayoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
        chatProfileName.setText(txtChatProfileName);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //messageAdapter.addMessage(new ChatMessage("",name.getText().toString(), txtMessage.getText().toString(), "00:00"));
                /*Enviar el mensaje a la base de datos*/
                ChatMessage chatMessage = new ChatMessage("", chatProfileName.getText().toString(), txtMessage.getText().toString(), "00:00");
                databaseReference.push().setValue(chatMessage);
            }
        });
        /*Hacer que la pantalla se desplace segun van apareciendo nuevos mensajes*/
        messageAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            // Este método se llama cuando insertamos un nuevo objeto, en este caso un nuevo mensaje
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });

        databaseReference.addChildEventListener(new ChildEventListener() {
            /*Cuando agreguemos un nuevo dato a la base de datos lo añadira al ArrayList de mensajes*/
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                messageAdapter.addMessage(chatMessage);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /*Método para pasar al ultimo objeto del recyclerview*/
    private void setScrollbar() {
        recyclerViewMessages.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
}