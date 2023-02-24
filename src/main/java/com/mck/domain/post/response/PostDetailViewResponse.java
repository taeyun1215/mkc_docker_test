package com.mck.domain.post.response;

import com.mck.domain.comment.Comment;
import com.mck.domain.comment.response.CommentPostDetailViewResponse;
import com.mck.domain.image.response.ImagePostDetailViewResponse;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Builder
public class PostDetailViewResponse {

    private Long id;
    private String title;
    private String content;
    private String writer;
    private int view;
    private boolean writeStatus;
    private List<ImagePostDetailViewResponse> images;
    private List<CommentPostDetailViewResponse> comments;
    private int likes;

    public static PostDetailViewResponse from(Post post, List<ImagePostDetailViewResponse> imagePostDetailViewResponse, String seeUsername) {

        boolean writeStatus;

        if (Objects.equals(post.getUser().getUsername(), seeUsername)) writeStatus = true;
        else writeStatus = false;

        List<CommentPostDetailViewResponse> commentPostDetailViewResponses = new ArrayList<>();

        if (post.getComments().size() != 0) {
            for (Comment comment : post.getComments()) {
                CommentPostDetailViewResponse commentPostDetailViewResponse = CommentPostDetailViewResponse.form(comment);
                commentPostDetailViewResponses.add(commentPostDetailViewResponse);
            }
        } else {
            commentPostDetailViewResponses.add(null);
        }

        return PostDetailViewResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter())
                .view(post.getView())
                .writeStatus(writeStatus)
                .images(imagePostDetailViewResponse)
                .comments(commentPostDetailViewResponses)
                .build();
    }

}
