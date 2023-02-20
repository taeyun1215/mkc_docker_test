package com.mck.domain.post.response;

import com.mck.domain.comment.Comment;
import com.mck.domain.image.Image;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostViewResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int view;
    private List<Image> images;
    private List<Comment> comments;
    private int likes;

    public static PostViewResponse from(Post post) {
        return PostViewResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter())
                .view(post.getView())
                .images(post.getImages())
                .comments(post.getComments())
                .build();
    }

}
