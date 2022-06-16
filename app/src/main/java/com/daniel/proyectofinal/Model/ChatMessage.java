package com.daniel.proyectofinal.Model;

public class ChatMessage {

    private String profilePicture;
    private String name;
    private String message;
    private String time;
    private Teacher teacher;
    private Center center;

    public ChatMessage(String profilePicture, String name, String message, String time) {
        this.profilePicture = profilePicture;
        this.name = name;
        this.message = message;
        this.time = time;
    }

    public ChatMessage() {
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
