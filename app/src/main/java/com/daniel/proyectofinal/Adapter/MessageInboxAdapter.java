package com.daniel.proyectofinal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Model.MessageInbox;
import com.daniel.proyectofinal.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MessageInboxAdapter extends RecyclerView.Adapter<MessageInboxAdapter.ViewHolder> {

    private ArrayList<MessageInbox> inboxMessages;
    private Context context;

    public MessageInboxAdapter(ArrayList<MessageInbox> inboxMessages, Context context) {
        this.inboxMessages = inboxMessages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_message_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageInbox auxInboxMessages = inboxMessages.get(position);

        if (!auxInboxMessages.getProfilePicture().isEmpty()) {
            Picasso.get().load(auxInboxMessages.getProfilePicture()).into(holder.profilePicture);
        }
        holder.name.setText(auxInboxMessages.getName());
        holder.lastMessage.setText(auxInboxMessages.getLastMessage());

        if (auxInboxMessages.getContadorMensajeNoLeido() == 0) {
            holder.unreadMessages.setVisibility(View.GONE);
        }
        else {
            holder.unreadMessages.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return inboxMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView profilePicture;
        private TextView name;
        private TextView lastMessage;
        private TextView unreadMessages;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profileChatPicture);
            name = itemView.findViewById(R.id.nombreContactoChat);
            lastMessage = itemView.findViewById(R.id.txtUltimoMensaje);
            unreadMessages = itemView.findViewById(R.id.mensajesSinLeer);
        }
    }
}
