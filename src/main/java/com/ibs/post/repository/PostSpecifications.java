package com.ibs.post.repository;

import com.ibs.post.domain.Post;
import com.ibs.post.domain.PostStatus;
import com.ibs.post.domain.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

public final class PostSpecifications {

    private PostSpecifications() {
    }

    public static Specification<Post> hasStatus(PostStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Post> hasCategory(String category) {
        if (!StringUtils.hasText(category)) {
            return null;
        }
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase());
    }

    public static Specification<Post> hasTag(String tagNameOrSlug) {
        if (!StringUtils.hasText(tagNameOrSlug)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            query.distinct(true);
            Join<Post, Tag> tags = root.join("tags", JoinType.LEFT);
            String value = tagNameOrSlug.toLowerCase();
            return criteriaBuilder.or(
                    criteriaBuilder.equal(criteriaBuilder.lower(tags.get("slug")), value),
                    criteriaBuilder.equal(criteriaBuilder.lower(tags.get("name")), value)
            );
        };
    }

    public static Specification<Post> containsText(String queryText) {
        if (!StringUtils.hasText(queryText)) {
            return null;
        }
        return (root, query, criteriaBuilder) -> {
            String like = "%" + queryText.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("summary")), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), like)
            );
        };
    }
}
