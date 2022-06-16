package com.daniel.proyectofinal.Model;

public class MessageInbox {

    private String profilePicture;
    private String name;
    private String lastMessage;
    private int contadorMensajeNoLeido;

    public MessageInbox(String profilePicture, String name, String lastMessage, int contadorMensajeNoLeido) {
        this.profilePicture = profilePicture;
        this.name = name;
        this.lastMessage = lastMessage;
        this.contadorMensajeNoLeido = contadorMensajeNoLeido;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public String getName() {
        return name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int getContadorMensajeNoLeido() {
        return contadorMensajeNoLeido;
    }
}
