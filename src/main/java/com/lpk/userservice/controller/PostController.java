package com.lpk.userservice.controller;

import com.lpk.userservice.model.Author;
import com.lpk.userservice.model.Post;
import com.lpk.userservice.repository.PostRepository;
import com.lpk.userservice.security.JwtTokenProvider;
import com.lpk.userservice.service.UploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/public")
    public List<Post> getAllPublicPosts() {
        return postRepository.findByVisibility("PUBLIC");
    }

    // ✅ Post creation with image upload
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Post createPost(
            @RequestParam("title") String title,
            @RequestParam("excerpt") String excerpt,
            @RequestParam("content") String content,
            @RequestParam("tags") String tags,
            @RequestParam("authorName") String authorName,
            @RequestParam("authorBio") String authorBio,
            @RequestParam("coverImage") MultipartFile coverImage,
            @RequestParam("authorImage") MultipartFile authorImage,
            @RequestParam(value = "status", defaultValue = "published") String status,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        String coverImageUrl = uploadService.save(coverImage);
        String authorImageUrl = uploadService.save(authorImage);

        Post post = new Post();
        post.setTitle(title);
        post.setExcerpt(excerpt);
        post.setContent(content);
        post.setTags(List.of(tags.split(",")));
        post.setUserId(email);
        post.setImage(coverImageUrl);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setStatus(status);
        post.setVisibility("PUBLIC");
        post.setSlug(generateSlug(title));
        post.setAuthor(new Author(email, authorName, authorBio, authorImageUrl)); // ✅ include email

        return postRepository.save(post);
    }

    // ✅ Post creation via JSON
    @PostMapping
    public Post createPostJson(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Post post
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        post.setUserId(email);
        post.setCreatedAt(new Date());
        post.setUpdatedAt(new Date());
        post.setSlug(generateSlug(post.getTitle()));
        post.setStatus(post.getStatus() != null ? post.getStatus() : "published");
        post.setVisibility("PUBLIC");

        if (post.getAuthor() == null) {
            post.setAuthor(new Author(email, "", "", null));
        } else {
            post.setAuthor(new Author(
                    email,
                    post.getAuthor().getName(),
                    post.getAuthor().getBio(),
                    post.getAuthor().getAvatarUrl()
            ));
        }

        return postRepository.save(post);
    }

    // ✅ Post update with multipart data
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
            post.setAuthor(new Author(email, authorName, authorBio, authorImgUrl)); // ✅ include email
        } else {
            String existingAvatar = post.getAuthor() != null ? post.getAuthor().getAvatarUrl() : null;
            post.setAuthor(new Author(email, authorName, authorBio, existingAvatar)); // ✅ include email
        }

        return postRepository.save(post);
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        Optional<Post> post = postRepository.findBySlug(slug);
        if (post.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");
        }
        return ResponseEntity.ok(post.get());
    }

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

    @GetMapping("/me/{status}")
    public List<Post> getMyPostsByStatus(
            @PathVariable String status,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);
        return postRepository.findByUserIdAndStatus(email, status.toLowerCase());
    }

    @DeleteMapping("/{id}")
public ResponseEntity<?> deletePost(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
    try {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to delete this post");
        }

        postRepository.deleteById(id);
        return ResponseEntity.ok("Post deleted successfully.");

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}


    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-+$", "").replaceAll("^-+", "");
    }
}
