package com.daniel.proyectofinal.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Adapter.CenterAdapter;
import com.daniel.proyectofinal.Adapter.TeacherAdapter;
import com.daniel.proyectofinal.Model.Center;
import com.daniel.proyectofinal.Model.Teacher;
import com.daniel.proyectofinal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerViewTeachers;
    private RecyclerView recyclerViewCenters;
    private ArrayList<Teacher> teachers;
    private ArrayList<Center> centers;
    private TeacherAdapter teacherAdapter;
    private CenterAdapter centerAdapter;
    private DatabaseReference databaseReferenceTeachers;
    private DatabaseReference databaseReferenceCenters;
    private FirebaseUser firebaseUser;

    private EditText search_bar;

    /**/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerViewTeachers = view.findViewById(R.id.recycler_view_teachers);
        recyclerViewTeachers.setHasFixedSize(true);
        recyclerViewTeachers.setLayoutManager(new LinearLayoutManager(getContext()));

        /*Centers*/
        recyclerViewCenters = view.findViewById(R.id.recycler_view_centers);
        recyclerViewCenters.setHasFixedSize(true);
        recyclerViewCenters.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReferenceTeachers = FirebaseDatabase.getInstance().getReference().child("teachers");
        databaseReferenceCenters = FirebaseDatabase.getInstance().getReference().child("centers");

        teachers = new ArrayList<>();
        centers = new ArrayList<>();
        teacherAdapter = new TeacherAdapter(getContext(), teachers, true);
        centerAdapter = new CenterAdapter(getContext(), centers);
        recyclerViewTeachers.setAdapter(teacherAdapter);
        recyclerViewCenters.setAdapter(centerAdapter);

        search_bar = view.findViewById(R.id.searchBar);

        // Escuchador para cada vez que se vaya a buscar un usuario en la barra de busqueda
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString());
                searchCenter(charSequence.toString());
                readUsers();
                readCenters();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    private void readCenters() {
        databaseReferenceCenters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())) {
                    centers.clear(); // Primero limpiamos la lista
                    // Recorremos los datos
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Center center = snapshot.getValue(Center.class);
                        centers.add(center);
                    }
                    centerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("readCenters", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // Añadir values a la lista de usuarios
    private void readUsers() {
        databaseReferenceTeachers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (TextUtils.isEmpty(search_bar.getText().toString())) {
                    teachers.clear(); // Primero limpiamos la lista
                    // Recorremos los datos
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Teacher teacher = snapshot.getValue(Teacher.class);
                        teachers.add(teacher);
                    }
                    teacherAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("readUsers", "loadPost:onCancelled", error.toException());
            }
        });
    }

    // Este método es llamado cada vez que vayamos a buscar en la barra de busqueda, y nos muestra los usuarios segun los caracteres que hayamos puesto
    private void searchUser(String keySearch) {
        Query query = databaseReferenceTeachers.orderByChild("name").startAt(keySearch).endAt(keySearch + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teachers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Teacher teacher = snapshot.getValue(Teacher.class);
                    teachers.add(teacher);
                }
                teacherAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("searchUser", "loadPost:onCancelled", error.toException());
            }
        });
    }

    private void searchCenter(String keySearch) {
        Query query = databaseReferenceCenters.orderByChild("name").startAt(keySearch).endAt(keySearch + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                centers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Center center = snapshot.getValue(Center.class);
                    centers.add(center);
                }
                centerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("searchCenter", "loadPost:onCancelled", error.toException());
            }
        });
    }
}