package org.sondev.post.service;

import java.time.Instant;

import org.sondev.post.dto.request.PostRequest;
import org.sondev.post.dto.response.PageResponse;
import org.sondev.post.dto.response.PostResponse;
import org.sondev.post.entity.Post;
import org.sondev.post.mapper.PostMapper;
import org.sondev.post.repository.PostRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final DateTimeFormatter dateTimeFormatter;

    public PostService(PostRepository postRepository, PostMapper postMapper, DateTimeFormatter dateTimeFormatter) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public PostResponse createPost(PostRequest postRequest) {
        // get userId from token ==> dieu chinh token voi sub : id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        var now = Instant.now();
        Post post = Post.builder()
                .content(postRequest.getContent())
                .userId(authentication.getName())
                .createdDate(now)
                .modifiedDate(now)
                .build();
        post = postRepository.save(post);
        return postMapper.toPostResponse(post);
    }

    public PageResponse<PostResponse> getMyPosts(int page, int size) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        if (page < 1) page = 1;
        if (size < 5) size = 5;

        // phan trang
        Sort sort = Sort.by("createdDate").descending();

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        var pageData = postRepository.findAllByUserId(userId, pageable);

        var postList = pageData.getContent().stream()
                .map(post -> {
                    var postResponse = postMapper.toPostResponse(post);
                    postResponse.setCreated(dateTimeFormatter.format(post.getCreatedDate()));
                    return postResponse;
                })
                .toList();
        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }
}
