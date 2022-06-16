package com.daniel.proyectofinal.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Activity.MainActivity;
import com.daniel.proyectofinal.Adapter.PostAdapter;
import com.daniel.proyectofinal.Activity.MessagesActivity;
import com.daniel.proyectofinal.Model.Post;
import com.daniel.proyectofinal.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;
    private ImageView btnChats;
    private FirebaseUser firebaseUser;
    private String uid;

    private List<String> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // Controlar de que hay un usuario con sesión activa
        if (firebaseUser == null) {
            startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            getActivity().finish();
        }
        uid = firebaseUser.getUid();
        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true); // Motrar el ultimo post de primero
        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        // options se le pasara al constructor de PostAdapter
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("posts"), Post.class)
                        .build();
        postAdapter = new PostAdapter(options, getContext());
        recyclerViewPosts.setAdapter(postAdapter);

        btnChats = (ImageView) view.findViewById(R.id.btnChats);
        btnChats.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        postAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        postAdapter.stopListening();
    }

    // Método que lee todos los posts para mostrarlos en el FeedFragment
    private void readPosts() {
        ProgressDialog progressDialog = new ProgressDialog(getActivity().getApplicationContext());
        progressDialog.setMessage("Cargando publicaciones");
        progressDialog.setCancelable(false);
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    for (String id : userList) {
                        if (post.getPostOwner().equals(id)) {
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("readPosts", "loadPost:onCancelled", error.toException());
            }
        });
        progressDialog.dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChats:
                showChats();
                break;
        }
    }

    private void showChats() {
        startActivity(new Intent(getActivity().getApplicationContext(), MessagesActivity.class));
    }
}