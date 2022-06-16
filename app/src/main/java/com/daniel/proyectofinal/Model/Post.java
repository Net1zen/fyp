package com.daniel.proyectofinal.Model;

public class Post {

    private String postid;
    private String postOwner;
    private String position;
    private String description;

    public Post(){}

    public Post(String postid, String postOwner, String position, String description) {
        this.postid = postid;
        this.postOwner = postOwner;
        this.position = position;
        this.description = description;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostOwner() {
        return postOwner;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
