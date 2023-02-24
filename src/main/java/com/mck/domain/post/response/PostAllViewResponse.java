package com.mck.domain.post.response;

import com.mck.domain.comment.response.CommentPostPagingResponse;
import com.mck.domain.image.response.ImagePostPagingResponse;
import com.mck.domain.post.Post;
import com.mck.domain.postlike.response.PostLikePostPagingResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostAllViewResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int view;
    private ImagePostPagingResponse image;
    private int comments;
    private int likes;
    private LocalDateTime createTime;

    public static PostAllViewResponse from(
            Post post,
            ImagePostPagingResponse imagePostPagingResponse,
            CommentPostPagingResponse commentPostPagingResponse,
            PostLikePostPagingResponse postLikePostPagingResponse
    ) {
        return PostAllViewResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter())
                .view(post.getView())
                .image(imagePostPagingResponse)
                .comments(commentPostPagingResponse.getCommentCount())
                .likes(postLikePostPagingResponse.getPostLikeCount())
                .createTime(post.getCreateTime())
                .build();
    }

}
