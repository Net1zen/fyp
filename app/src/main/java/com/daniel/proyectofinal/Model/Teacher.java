package com.daniel.proyectofinal.Model;

public class Teacher {

    private String name;
    private String surname;
    private String email;
    private String password;
    private String age;
    private String phone;
    private String image;
    private String uid;

    public Teacher() {
    }

    public Teacher(String name, String surname, String email, String password, String age, String phone, String image, String uid) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.email = password;
        this.age = age;
        this.phone = phone;
        this.image = image;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}

