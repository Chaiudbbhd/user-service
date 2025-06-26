package com.lpk.userservice.model;

/**
 * Represents the author of a post.
 * Both `_id` and `name` fields now store the email for author identity check.
 */
public class Author {

    private String _id;         // Typically the author's email
    private String name;        // Also set as email for frontend comparison
    private String bio;
    private String avatarUrl;

    public Author() {
    }

    /**
     * Full constructor.
     *
     * @param _id        The author's ID (email)
     * @param name       The author's name (email, for matching frontend token sub)
     * @param bio        The author's bio
     * @param avatarUrl  URL to the author's profile image
     */
    public Author(String _id, String name, String bio, String avatarUrl) {
        this._id = _id;
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
    }

    // Getters and Setters
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
