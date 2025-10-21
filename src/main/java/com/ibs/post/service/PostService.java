package com.ibs.post.service;

import com.ibs.global.dto.PageResponse;
import com.ibs.global.exception.BusinessException;
import com.ibs.global.exception.ErrorCode;
import com.ibs.post.domain.Category;
import com.ibs.post.domain.Post;
import com.ibs.post.domain.PostStatus;
import com.ibs.post.domain.Tag;
import com.ibs.post.dto.PostCreateRequest;
import com.ibs.post.dto.PostDetailResponse;
import com.ibs.post.dto.PostSummaryResponse;
import com.ibs.post.repository.CategoryRepository;
import com.ibs.post.repository.PostRepository;
import com.ibs.post.repository.TagRepository;
import com.ibs.post.service.mapper.PostMapper;
import com.ibs.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository; // Inject CategoryRepository
    private final PostMapper postMapper;

    public Post createPost(PostCreateRequest request, User author) {
        Set<Tag> tags = processTags(request.tags());
        Category category = findOrCreateCategory(request.category());

        Post post = Post.builder()
                .title(request.title())
                .summary(request.summary())
                .content(request.content())
                .category(category) // Set Category entity
                .coverImageUrl(request.coverImageUrl())
                .author(author)
                .tags(tags)
                .status(request.publish() ? PostStatus.PUBLISHED : PostStatus.DRAFT)
                .publishedAt(request.publish() ? LocalDateTime.now() : null)
                .build();

        post.setSlug(generateUniquePostSlug(request.title()));

        if (request.recommendedPostIds() != null && !request.recommendedPostIds().isEmpty()) {
            Set<Post> recommendedPosts = new HashSet<>(postRepository.findAllById(request.recommendedPostIds()));
            post.setRecommendedPosts(recommendedPosts);
        }
        if (request.relatedPostIds() != null && !request.relatedPostIds().isEmpty()) {
            Set<Post> relatedPosts = new HashSet<>(postRepository.findAllById(request.relatedPostIds()));
            post.setRelatedPosts(relatedPosts);
        }

        return postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Post not found"));
        return postMapper.toDetail(post);
    }

    @Transactional(readOnly = true)
    public PageResponse<PostSummaryResponse> getPosts(String category, String tag, String query, Pageable pageable) {
        Page<Post> postsPage = postRepository.searchPosts(category, tag, query, pageable);
        Page<PostSummaryResponse> summaryPage = postsPage.map(postMapper::toSummary);
        return new PageResponse<>(summaryPage);
    }

    public void deletePost(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND, "Post not found"));
        postRepository.delete(post);
    }

    private Category findOrCreateCategory(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseGet(() -> {
                    String slug = generateUniqueCategorySlug(categoryName);
                    int newOrder = categoryRepository.findTopByOrderByDisplayOrderDesc()
                            .map(cat -> cat.getDisplayOrder() + 1)
                            .orElse(0);

                    Category newCategory = Category.builder()
                            .name(categoryName)
                            .slug(slug)
                            .displayOrder(newOrder)
                            .build();
                    return categoryRepository.save(newCategory);
                });
    }

    private Set<Tag> processTags(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }
        return tagNames.stream()
                .map(name -> tagRepository.findByName(name)
                        .orElseGet(() -> {
                            String slug = generateUniqueTagSlug(name);
                            Tag newTag = Tag.builder().name(name).slug(slug).build();
                            return tagRepository.save(newTag);
                        }))
                .collect(Collectors.toSet());
    }

    private String generateUniquePostSlug(String title) {
        String baseSlug = title.toLowerCase().replaceAll("[^a-z0-9 가-힣-]", "").replaceAll("[\s-]+", "-");
        String slug = baseSlug;
        int counter = 1;
        while (postRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private String generateUniqueTagSlug(String name) {
        String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9 가-힣-]", "").replaceAll("[\s-]+", "-");
        String slug = baseSlug;
        int counter = 1;
        while (tagRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }

    private String generateUniqueCategorySlug(String name) {
        String baseSlug = name.toLowerCase().replaceAll("[^a-z0-9 가-힣-]", "").replaceAll("[\s-]+", "-");
        String slug = baseSlug;
        int counter = 1;
        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }
        return slug;
    }
}
