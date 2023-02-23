package com.mck.domain.post.response;

import com.mck.domain.comment.Comment;
import com.mck.domain.image.response.ImageViewResponse;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostDetailViewResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int view;
    private boolean writeStatus;
    private List<ImageViewResponse> images;
    private List<Comment> comments;
    private int likes;

    public static PostDetailViewResponse from(Post post, List<ImageViewResponse> imageViewResponse, String seeUsername) {

        boolean writeStatus;

        if (post.getUser().getUsername() == seeUsername) writeStatus = true;
        else writeStatus = false;

        return PostDetailViewResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter())
                .view(post.getView())
                .writeStatus(writeStatus)
                .images(imageViewResponse)
                .comments(post.getComments())
                .build();
    }



}
