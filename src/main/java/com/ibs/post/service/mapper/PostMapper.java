package com.ibs.post.service.mapper;

import com.ibs.post.domain.Post;
import com.ibs.post.domain.Tag;
import com.ibs.post.dto.PostAuthorResponse;
import com.ibs.post.dto.PostDetailResponse;
import com.ibs.post.dto.PostSimpleResponse;
import com.ibs.post.dto.PostSummaryResponse;
import com.ibs.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(source = "category.name", target = "category")
    @Mapping(target = "tags", expression = "java(mapTagNames(post.getTags()))")
    PostSummaryResponse toSummary(Post post);

    @Mapping(source = "category.name", target = "category")
    @Mapping(target = "author", expression = "java(toAuthor(post.getAuthor()))")
    @Mapping(target = "tags", expression = "java(mapTagNames(post.getTags()))")
    @Mapping(target = "recommendedPosts", expression = "java(toSimplePosts(post.getRecommendedPosts()))")
    @Mapping(target = "relatedPosts", expression = "java(toSimplePosts(post.getRelatedPosts()))")
    PostDetailResponse toDetail(Post post);

    default PostAuthorResponse toAuthor(User user) {
        if (user == null) {
            return null;
        }
        return new PostAuthorResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getJobTitle(),
                user.getProfileImage()
        );
    }

    default List<PostSimpleResponse> toSimplePosts(Set<Post> posts) {
        if (posts == null) {
            return List.of();
        }
        return posts.stream()
                .filter(Objects::nonNull)
                .map(post -> new PostSimpleResponse(
                        post.getId(),
                        post.getSlug(),
                        post.getTitle(),
                        post.getSummary(),
                        post.getCategory() != null ? post.getCategory().getName() : null,
                        post.getCoverImageUrl(),
                        map(post.getPublishedAt())
                ))
                .collect(Collectors.toList());
    }

    default List<String> mapTagNames(Set<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return List.of();
        }
        return tags.stream()
                .filter(Objects::nonNull)
                .map(Tag::getName)
                .toList();
    }

    // Method to map LocalDateTime to Instant
    default Instant map(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}
