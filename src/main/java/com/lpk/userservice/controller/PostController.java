package com.lpk.userservice.controller;

import com.lpk.userservice.model.Post;
import com.lpk.userservice.repository.PostRepository;
import com.lpk.userservice.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

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
        return postRepository.save(post);
    }

    @GetMapping("/public")
    public List<Post> getPublicPosts() {
        return postRepository.findByVisibility("PUBLIC");
    }
}
