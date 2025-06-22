package com.lpk.userservice.controller;

import com.lpk.userservice.model.Author;
import com.lpk.userservice.model.Post;
import com.lpk.userservice.repository.PostRepository;
import com.lpk.userservice.security.JwtTokenProvider;
import com.lpk.userservice.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Autowired
    private UploadService uploadService;

    // ✅ Create post with multipart + author
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("authorName") String authorName,
            @RequestParam("authorBio") String authorBio,
            @RequestParam("authorImage") MultipartFile authorImage,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        String imageUrl = uploadService.save(authorImage);

        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setUserId(email);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setStatus("draft");
        post.setVisibility("PRIVATE");
        post.setSlug(generateSlug(title));
        post.setAuthor(new Author(authorName, authorBio, imageUrl));

        return postRepository.save(post);
    }

    // ✅ Update post with multipart form
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post updatePost(
            @PathVariable String id,
            @RequestParam("title") String title,
            @RequestParam("excerpt") String excerpt,
            @RequestParam("content") String content,
            @RequestParam("tags") String tags,
            @RequestParam("authorName") String authorName,
            @RequestParam("authorBio") String authorBio,
            @RequestParam(value = "authorImage", required = false) MultipartFile authorImage,
            @RequestParam(value = "image", required = false) String coverImageUrl,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        Post post = postRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(email)) {
            throw new RuntimeException("Unauthorized to update this post");
        }

        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);
        post.setTags(List.of(tags.split(",")));
        post.setUpdatedAt(new Date());

        if (coverImageUrl != null && !coverImageUrl.isEmpty()) {
            post.setImage(coverImageUrl);
        }

        if (authorImage != null && !authorImage.isEmpty()) {
            String authorImgUrl = uploadService.save(authorImage);
            post.setAuthor(new Author(authorName, authorBio, authorImgUrl));
        } else if (post.getAuthor() != null) {
            post.getAuthor().setName(authorName);
            post.getAuthor().setBio(authorBio);
        } else {
            post.setAuthor(new Author(authorName, authorBio, null));
        }

        return postRepository.save(post);
    }

    // ✅ Legacy JSON post
    @PostMapping
    public Post createPost(@RequestHeader("Authorization") String authHeader, @RequestBody Post post) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        post.setUserId(email);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setSlug(generateSlug(post.getTitle()));

        if (post.getStatus() == null) {
            post.setStatus("draft");
        }
        if (post.getVisibility() == null) {
            post.setVisibility("PRIVATE");
        }

        return postRepository.save(post);
    }

    @GetMapping("/public")
    public List<Post> getPublicPosts() {
        return postRepository.findByVisibility("PUBLIC");
    }

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

    @GetMapping("/slug/{slug}")
public Post getPostBySlug(@PathVariable String slug) {
    return postRepository.findBySlug(slug)
            .orElseThrow(() -> new RuntimeException("Post not found"));
}

// ✅ Added new method to fetch post by ID for editing
@GetMapping("/{id}")
public Post getPostById(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    String email = tokenProvider.getUsernameFromToken(token);

    Post post = postRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Post not found"));

    if (!post.getUserId().equals(email)) {
        throw new RuntimeException("Unauthorized to access this post");
    }

    return post;
}


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

    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-+$", "").replaceAll("^-+", "");
    }
}
