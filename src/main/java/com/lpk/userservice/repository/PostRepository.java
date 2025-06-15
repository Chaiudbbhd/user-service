package com.lpk.userservice.repository;

import com.lpk.userservice.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {
    List<Post> findByUserId(String userId);
    List<Post> findByVisibility(String visibility);
}
