package com.mck.domain.post.response;

import com.mck.domain.image.response.ImageViewResponse;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PostPagingResponse {

    private List<PostViewResponse> posts;

    public static PostPagingResponse from(Page<Post> posts) {

        List<PostViewResponse> postList = new ArrayList<>();

        posts.forEach(post -> {
            List<ImageViewResponse> imageViewResponses = ImageViewResponse.from(post.getImages());
            PostViewResponse postViewResponse = PostViewResponse.from(post, imageViewResponses);
            postList.add(postViewResponse);

        });

        return PostPagingResponse.builder()
                .posts(postList)
                .build();
    }
}
