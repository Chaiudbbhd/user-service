package com.lpk.userservice.model;

/**
 * Represents the author of a post.
 * `_id` is typically the author's email (used for identity check).
 * `name` is the display name.
 */
public class Author {

    private String _id;         // Unique identifier, usually the author's email
    private String name;        // Display name
    private String bio;
    private String avatarUrl;

    public Author() {
        // Default constructor
    }

    /**
     * Full constructor to create an Author instance.
     *
     * @param _id        Author's unique identifier (typically email)
     * @param name       Author's display name
     * @param bio        Author's biography
     * @param avatarUrl  URL of the author's profile image
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
