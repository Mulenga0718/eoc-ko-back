package com.ibs.post.api;

import com.ibs.global.dto.PageResponse;
import com.ibs.global.service.CloudflareService;
import com.ibs.post.domain.Post;
import com.ibs.post.dto.PostCreateRequest;
import com.ibs.post.dto.PostDetailResponse;
import com.ibs.post.dto.PostSummaryResponse;
import com.ibs.post.service.PostService;
import com.ibs.post.service.mapper.PostMapper;
import com.ibs.user.domain.User;
import com.ibs.user.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostMapper postMapper;
    private final CloudflareService cloudflareService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDetailResponse> createPostWithImage(
            @RequestPart("request") @Valid PostCreateRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        PostCreateRequest processedRequest = request;
        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = cloudflareService.uploadImage(image);
                // Create a new record instance with the updated coverImageUrl
                processedRequest = new PostCreateRequest(
                        request.title(),
                        request.summary(),
                        request.content(),
                        request.category(),
                        imageUrl, // new value
                        request.tags(),
                        request.recommendedPostIds(),
                        request.relatedPostIds(),
                        request.publish());
            } catch (IOException ex) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload image to Cloudflare", ex);
            }
        }
        return createPostResponse(processedRequest);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable String slug) {
        return ResponseEntity.ok(postService.getPostBySlug(slug));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostSummaryResponse>> getPosts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag,
            @RequestParam(name = "q", required = false) String query,
            @PageableDefault(sort = "publishedAt", direction = Sort.Direction.DESC, size = 12) Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPosts(category, tag, query, pageable));
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<Void> deletePost(@PathVariable String slug) {
        postService.deletePost(slug);
        return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
    }

    private ResponseEntity<PostDetailResponse> createPostResponse(PostCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User author = customUserDetails.getUser();

        Post createdPost = postService.createPost(request, author);
        PostDetailResponse response = postMapper.toDetail(createdPost);

        return ResponseEntity
                .created(URI.create("/posts/" + response.slug()))
                .body(response);
    }
}
