package com.lpk.userservice.model;

public class Author {
    private String _id;       // ✅ Add this field
    private String name;
    private String bio;
    private String avatarUrl;

    public Author() {
    }

    // ✅ Updated constructor
    public Author(String _id, String name, String bio, String avatarUrl) {
        this._id = _id;
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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
