package com.daniel.proyectofinal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Model.Center;
import com.daniel.proyectofinal.R;
import com.daniel.proyectofinal.RedirectProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CenterAdapter extends RecyclerView.Adapter<CenterAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Center> centers;

    private DatabaseReference databaseReference;

    public CenterAdapter(Context context, ArrayList<Center> centers){
        this.context = context;
        this.centers = centers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new CenterAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        showCenter(holder, position);
    }

    private void showCenter(ViewHolder holder, int position) {
        Center center = centers.get(position);
        holder.name.setText(center.getName());
        try {
            // Si existe la imagen
            Picasso.get().load(center.getImage()).placeholder(R.drawable.foto_perfil).into(holder.imageProfile);
        } catch (Exception exception) {
            // Si no existe la imagen
            Picasso.get().load(R.drawable.servicios_centroseducativos).into(holder.imageProfile);
        }
        // Redirigir al perfil del usuario seleccionado
        // Click on the name
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RedirectProfile.class);
                // Enviar datos al intent
                intent.putExtra("userId", center.getUid()); // Los datos que se envian mediante putExtra son enviados como bundle
                intent.putExtra("userType", "centro");
                context.startActivity(intent);
            }
        });
        // Click on the image profile
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RedirectProfile.class);
                // Enviar datos al intent
                intent.putExtra("userId", center.getUid());
                intent.putExtra("userType", "centro");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return centers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageProfile;
        public TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            name = itemView.findViewById(R.id.fullname);
        }
    }
}
