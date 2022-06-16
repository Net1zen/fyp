package com.daniel.proyectofinal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Model.Teacher;
import com.daniel.proyectofinal.R;
import com.daniel.proyectofinal.RedirectProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<Teacher> teachers;
    private final boolean isFragment; //Para diferenciar si se utilizara en un fragmento o en una activity

    private DatabaseReference databaseReference;

    /*Este constructor es llamado desde SearchFragment*/
    public TeacherAdapter(Context context, ArrayList<Teacher> teachers, boolean isFragment) {
        this.context = context;
        this.teachers = teachers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new TeacherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        databaseReference = FirebaseDatabase.getInstance().getReference();
        showUser(holder, position);
    }

    private void showUser(ViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        holder.fullname.setText(teacher.getName() + " " + teacher.getSurname());
        try {
            // Si existe la imagen
            Picasso.get().load(teacher.getImage()).placeholder(R.drawable.foto_perfil).into(holder.imageProfile);
        } catch (Exception exception) {
            // Si no existe la imagen
            Picasso.get().load(R.drawable.foto_perfil).into(holder.imageProfile);
        }
        // Redirigir al perfil del usuario seleccionado
        // Click on the name
        holder.fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RedirectProfile.class);
                // Enviar datos al intent
                intent.putExtra("userId", teacher.getUid()); // Los datos que se envian mediante putExtra son enviados como bundle
                intent.putExtra("userType", "docente");
                context.startActivity(intent);
            }
        });
        // Click on the image profile
        holder.imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, RedirectProfile.class);
                // Enviar datos al intent
                intent.putExtra("userId", teacher.getUid());
                intent.putExtra("userType", "docente");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return teachers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView imageProfile;
        public TextView fullname;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image_profile);
            fullname = itemView.findViewById(R.id.fullname);
            btnFollow = itemView.findViewById(R.id.btn_follow);
        }
    }
}
