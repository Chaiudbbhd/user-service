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

    // ✅ Create post with multipart + status + public visibility
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
    
        // Upload both images to Cloudinary
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
        post.setAuthor(new Author(authorName, authorBio, authorImageUrl));
    
        return postRepository.save(post);
    }
    
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

    // ✅ Legacy JSON post (optional)
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

        if (post.getStatus() == null) {
            post.setStatus("published");
        }

        post.setVisibility("PUBLIC");

        return postRepository.save(post);
    }
    @GetMapping("/public")
    public List<Post> getPublicPosts() {
        return postRepository.findByVisibilityAndStatus("PUBLIC", "published");
    }
    @PatchMapping("/{id}/visibility")
    public Post toggleVisibility(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(email)) {
            throw new RuntimeException("Unauthorized to change visibility");
        }

        String newVisibility = post.getVisibility().equals("PUBLIC") ? "PRIVATE" : "PUBLIC";
        post.setVisibility(newVisibility);
        post.setUpdatedAt(new Date());

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
    public String deletePost(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = tokenProvider.getUsernameFromToken(token);

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(email)) {
            throw new RuntimeException("Unauthorized to delete this post");
        }

        postRepository.deleteById(id);
        return "Post deleted successfully.";
    }
    private String generateSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-+$", "").replaceAll("^-+", "");
    }
}
