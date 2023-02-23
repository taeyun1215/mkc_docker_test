package com.mck.domain.post.response;

import com.mck.domain.image.response.ImagePostPagingResponse;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PostPagingResponse {

    private List<PostAllViewResponse> posts;

    public static PostPagingResponse from(Page<Post> posts) {

        List<PostAllViewResponse> postList = new ArrayList<>();

        posts.forEach(post -> {
            if (post.getImages().size() != 0) {
                ImagePostPagingResponse imagePostPagingResponse = ImagePostPagingResponse.from(post.getImages().get(0), post.getImages().size());
                PostAllViewResponse postAllViewResponse = PostAllViewResponse.from(post, imagePostPagingResponse);
                postList.add(postAllViewResponse);
            } else {
                ImagePostPagingResponse imagePostPagingResponse = ImagePostPagingResponse.from(null, 0);
                PostAllViewResponse postAllViewResponse = PostAllViewResponse.from(post, imagePostPagingResponse);
                postList.add(postAllViewResponse);
            }
        });

        return PostPagingResponse.builder()
                .posts(postList)
                .build();
    }
}
