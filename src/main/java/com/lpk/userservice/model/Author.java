package com.lpk.userservice.model;

public class Author {
    private String email;       // ✅ Add this field!
    private String name;
    private String bio;
    private String avatarUrl;

    public Author() {
    }

    public Author(String email, String name, String bio, String avatarUrl) {
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    // ✅ Getters & Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
