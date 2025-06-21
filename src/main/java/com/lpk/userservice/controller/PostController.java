package com.lpk.userservice.controller;

import com.lpk.userservice.model.Post;
import com.lpk.userservice.repository.PostRepository;
import com.lpk.userservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping
public Post createPost(@RequestHeader("Authorization") String authHeader, @RequestBody Post post) {
    String token = authHeader.replace("Bearer ", "");
    String email = tokenProvider.getUsernameFromToken(token);

    post.setUserId(email);
    post.setCreatedAt(new Date());
    post.setUpdatedAt(new Date());

    post.setSlug(generateSlug(post.getTitle()));

    // Set default values if missing
    if (post.getStatus() == null) {
        post.setStatus("draft"); // or "published"
    }
    if (post.getVisibility() == null) {
        post.setVisibility("PRIVATE"); // or "PUBLIC"
    }

    return postRepository.save(post);
}


    @GetMapping("/public")
    public List<Post> getPublicPosts() {
        return postRepository.findByVisibility("PUBLIC");
    }

    // ✅ Toggle visibility (PUBLIC <-> PRIVATE)
    @PatchMapping("/{id}/visibility")
    public Post toggleVisibility(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUserId().equals(email)) {
                throw new RuntimeException("Unauthorized to change visibility of this post");
            }

            String newVisibility = post.getVisibility().equals("PUBLIC") ? "PRIVATE" : "PUBLIC";
            post.setVisibility(newVisibility);
            post.setUpdatedAt(new Date());
            return postRepository.save(post);
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-+$", "").replaceAll("^-+", "");
    }

    @GetMapping("/slug/{slug}")
public Post getPostBySlug(@PathVariable String slug) {
    return postRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Post not found"));
}


    // ✅ Delete post
    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (!post.getUserId().equals(email)) {
                throw new RuntimeException("Unauthorized to delete this post");
            }

            postRepository.deleteById(id);
            return "Post deleted successfully.";
        } else {
            throw new RuntimeException("Post not found");
        }
    }
}
