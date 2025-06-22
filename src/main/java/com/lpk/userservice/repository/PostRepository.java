package com.lpk.userservice.repository;

import com.lpk.userservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserId(String userId);
    List<Post> findByVisibility(String visibility);
    Optional<Post> findBySlug(String slug);
    List<Post> findByVisibilityAndStatus(String visibility, String status);
    List<Post> findByUserIdAndStatus(String userId, String status);


}
