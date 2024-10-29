package org.sondev.post.mapper;

import org.mapstruct.Mapper;
import org.sondev.post.dto.response.PostResponse;
import org.sondev.post.entity.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
