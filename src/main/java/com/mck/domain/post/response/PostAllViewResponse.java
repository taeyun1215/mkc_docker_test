package com.mck.domain.post.response;

import com.mck.domain.comment.Comment;
import com.mck.domain.image.response.ImagePostPagingResponse;
import com.mck.domain.image.response.ImageViewResponse;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostAllViewResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int view;
    private boolean writeStatus;
    private ImagePostPagingResponse image;
    private List<Comment> comments;
    private int likes;

    public static PostAllViewResponse from(Post post, ImagePostPagingResponse imagePostPagingResponse) {

        return PostAllViewResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .view(post.getView())
                .image(imagePostPagingResponse)
                .comments(post.getComments())
                .build();
    }

}
