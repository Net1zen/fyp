package com.daniel.proyectofinal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Model.Center;
import com.daniel.proyectofinal.Model.Post;
import com.daniel.proyectofinal.R;
import com.daniel.proyectofinal.Activity.RedirectProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class PostAdapter extends FirebaseRecyclerAdapter<Post, PostAdapter.ViewHolder> {

    private Context context;
    private DatabaseReference databaseReference;

    // Se llama a este constructor desde FeedFragment
    public PostAdapter(@NonNull FirebaseRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post model) {

        // Comprobar si es un usuario o un centro educativo
        holder.title.setText(model.getPosition());
        holder.description.setText(model.getDescription());
        // Estos datos de abajo los obtenemos directamente de la informacion del centro que esta almacenada en la base de datos
        databaseReference.child("centers").child(model.getPostOwner()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Center center = dataSnapshot.getValue(Center.class);
                // Si la imagen de perfil esta vacía entonces...
                if (center.getImage().equals("")) {
                    Picasso.get().load(R.drawable.servicios_centroseducativos).into(holder.imageProfile);
                } else {
                    // Obtenemos la imagen de perfil de usuario y la ponemos dentro del holder que apunta a la imagen de perfil que se vera en el post
                    holder.imageProfile.setImageResource(R.drawable.foto_perfil);
                }
                holder.username.setText(center.getName());
                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent redirectProfile = new Intent(context, RedirectProfileActivity.class);
                        //*Pasamos el id del propietario de la publicacion *//*
                        redirectProfile.putExtra("userId", center.getUid());
                        redirectProfile.putExtra("userType", "centro");
                        context.startActivity(redirectProfile);
                    }
                });
                holder.imageProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent redirectProfile = new Intent(context, RedirectProfileActivity.class);
                        //*Pasamos el id del propietario de la publicacion *//*
                        redirectProfile.putExtra("userId", center.getUid());
                        redirectProfile.putExtra("userType", "centro");
                        context.startActivity(redirectProfile);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("onBindViewHolder", "loadPost:onCancelled", error.toException());
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ViewHolder(view);
    }

    // Un ViewHolder describe una vista de elemento y los metadatos sobre su lugar dentro del RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageProfile;
        public TextView username;
        public TextView title;
        public TextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.imagePost);
            username = itemView.findViewById(R.id.postUsername);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);
        }
    }
}
