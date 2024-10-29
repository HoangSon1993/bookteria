package org.sondev.post.service;

import java.time.Instant;
import java.util.List;

import org.sondev.post.dto.request.PostRequest;
import org.sondev.post.dto.response.PostResponse;
import org.sondev.post.entity.Post;
import org.sondev.post.mapper.PostMapper;
import org.sondev.post.repository.PostRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostService(PostRepository postRepository, PostMapper postMapper) {
        this.postRepository = postRepository;
        this.postMapper = postMapper;
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

    public List<PostResponse> getMyPosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return postRepository.findAllByUserId(userId).stream()
                .map((p) -> postMapper.toPostResponse(p))
                .toList();
    }
}
