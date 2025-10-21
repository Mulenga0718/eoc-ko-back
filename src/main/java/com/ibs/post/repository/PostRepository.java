package com.ibs.post.repository;

import com.ibs.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID>, PostRepositoryCustom {
    boolean existsBySlug(String slug);
    Optional<Post> findBySlug(String slug);
}
