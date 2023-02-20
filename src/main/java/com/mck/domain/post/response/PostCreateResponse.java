package com.mck.domain.post.response;

import com.mck.domain.comment.Comment;
import com.mck.domain.image.Image;
import com.mck.domain.post.Post;
import com.mck.domain.postlike.PostLike;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostCreateResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int view;
    private List<Image> images;
    private List<Comment> comments;
    private int likes;

    public static PostCreateResponse from(Post post) {
        return PostCreateResponse.builder()
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
