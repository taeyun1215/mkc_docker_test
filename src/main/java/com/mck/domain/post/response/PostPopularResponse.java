package com.mck.domain.post.response;

import com.mck.domain.comment.response.CommentPostPagingResponse;
import com.mck.domain.post.Post;
import com.mck.domain.postlike.response.PostLikePostPagingResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PostPopularResponse {

    private Long id;
    private String title;
    private int commentCount;
    private int likeCount;

    public static PostPopularResponse from(
            Post post,
            int commentCount,
            int likeCount
    ) {
        return PostPopularResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .commentCount(commentCount)
                .likeCount(likeCount)
                .build();
    }

    public static List<PostPopularResponse> from(List<Post> posts) {
        List<PostPopularResponse> postPopularResponses = new ArrayList<>();

        for (Post post : posts) {
            PostPopularResponse postPopularResponse;
            CommentPostPagingResponse commentPostPagingResponse;
            PostLikePostPagingResponse postLikePostPagingResponse;

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

            postPopularResponse = PostPopularResponse.from(post, commentPostPagingResponse.getCommentCount(), postLikePostPagingResponse.getPostLikeCount());
            postPopularResponses.add(postPopularResponse);
        }

        return postPopularResponses;
    }

}
