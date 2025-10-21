package com.ibs.post.repository;

import com.ibs.post.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> searchPosts(String category, String tag, String query, Pageable pageable);
}
