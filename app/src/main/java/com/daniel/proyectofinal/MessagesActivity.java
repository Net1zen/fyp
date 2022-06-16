package com.daniel.proyectofinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.daniel.proyectofinal.Adapter.MessageInboxAdapter;
import com.daniel.proyectofinal.Model.MessageInbox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesActivity extends AppCompatActivity {

    private String name;
    private ArrayList<MessageInbox> inboxMessages;
    private CircleImageView profilePicture;
    private RecyclerView recyclerViewInboxMessages;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // getReferenceFromUrl
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        profilePicture = findViewById(R.id.profilePictureMessages);
        recyclerViewInboxMessages = findViewById(R.id.recycler_view_inbox_messages);
        recyclerViewInboxMessages.setHasFixedSize(true);
        recyclerViewInboxMessages.setLayoutManager(new LinearLayoutManager(this));
        inboxMessages = new ArrayList<>();

        displayChatMessages();

    }

    private void displayChatMessages() {
        // OBTENER DATOS
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePictureUrl = snapshot.child("users").child(firebaseUser.getUid()).child("image").getValue(String.class);
                // Obtener la foto de perfil del usuario y mostrarla
                Picasso.get().load(profilePictureUrl).into(profilePicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MessagesActivity.this, "Ha ocurrido un error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inboxMessages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String getUserId = dataSnapshot.getKey();

                    if (!getUserId.equals(firebaseUser.getUid())) {
                        String getUserName = dataSnapshot.child("name").getValue(String.class);
                        String getProfilePicture = dataSnapshot.child("image").getValue(String.class);
                        MessageInbox messageInbox = new MessageInbox(getProfilePicture, getUserName, "", 0);
                        inboxMessages.add(messageInbox);
                    }
                }
                recyclerViewInboxMessages.setAdapter(new MessageInboxAdapter(inboxMessages,MessagesActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}