package com.mck.domain.post.response;

import com.mck.domain.comment.response.CommentPostPagingResponse;
import com.mck.domain.image.response.ImagePostPagingResponse;
import com.mck.domain.post.Post;
import com.mck.domain.postlike.response.PostLikePostPagingResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PostMyWriteResponse {

    private String username;
    private List<PostAllViewResponse> posts;


    public static PostMyWriteResponse from(List<Post> posts, String username) {
        List<PostAllViewResponse> postList = new ArrayList<>();

        for (Post post : posts) {
            ImagePostPagingResponse imagePostPagingResponse;
            CommentPostPagingResponse commentPostPagingResponse;
            PostLikePostPagingResponse postLikePostPagingResponse;

            if (post.getImages().size() != 0) {
                imagePostPagingResponse = ImagePostPagingResponse.from(post.getImages().get(0), post.getImages().size());
            } else {
                imagePostPagingResponse = ImagePostPagingResponse.from(null, 0);
            }

            if (post.getComments().size() != 0) {
                commentPostPagingResponse = CommentPostPagingResponse.form(post.getComments().size());
            } else {
                commentPostPagingResponse = CommentPostPagingResponse.form(0);
            }

            if (post.getLikes().size() != 0) {
                postLikePostPagingResponse  = PostLikePostPagingResponse.form(post.getLikes().size());
            } else {
                postLikePostPagingResponse  = PostLikePostPagingResponse.form(0);
            }

            PostAllViewResponse postAllViewResponse = PostAllViewResponse.from(post, imagePostPagingResponse, commentPostPagingResponse, postLikePostPagingResponse);
            postList.add(postAllViewResponse);
        }

        return PostMyWriteResponse.builder()
                .username(username)
                .posts(postList)
                .build();
    }

}
