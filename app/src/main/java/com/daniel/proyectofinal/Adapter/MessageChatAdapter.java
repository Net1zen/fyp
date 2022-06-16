package com.daniel.proyectofinal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.proyectofinal.Model.ChatMessage;
import com.daniel.proyectofinal.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageChatAdapter extends RecyclerView.Adapter<MessageChatAdapter.ViewHolder>{

    private List<ChatMessage> chatMessages = new ArrayList<>();
    private Context context;
    private String senderName;

    public MessageChatAdapter(Context context, String senderName){
        this.context = context;
        this.senderName = senderName;
    }

    public void addMessage(ChatMessage chatMessage){
        chatMessages.add(chatMessage);
        /*Enviar una notifiación cuando se inserta un nuevo elemento*/
        notifyItemInserted(chatMessages.size()); // El ultimo elemento de nuestra lista
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        holder.name.setText(senderName);
        holder.txtMessage.setText(chatMessage.getMessage());
        holder.hour.setText(chatMessage.getTime());
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    // Un ViewHolder describe una vista de elemento y los metadatos sobre su lugar dentro del RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView profilePicture;
        public TextView name;
        public TextView txtMessage;
        public TextView hour;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profilePicture = itemView.findViewById(R.id.imagePost);
            name = itemView.findViewById(R.id.nombreEmisor);
            txtMessage = itemView.findViewById(R.id.txtMensaje);
            hour = itemView.findViewById(R.id.horaMensaje);
        }
    }
}
