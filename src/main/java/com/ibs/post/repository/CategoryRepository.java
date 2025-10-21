package com.ibs.post.repository;

import com.ibs.post.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    boolean existsBySlug(String slug);
    List<Category> findAllByOrderByDisplayOrderAsc();
    Optional<Category> findTopByOrderByDisplayOrderDesc();
}
