package com.mck.domain.post.response;

import com.mck.domain.comment.response.CommentPostDetailViewResponse;
import com.mck.domain.image.response.ImagePostDetailViewResponse;
import com.mck.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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
    private LocalDateTime createTime;

    public static PostDetailViewResponse from(
            Post post,
            List<ImagePostDetailViewResponse> imagePostDetailViewResponse,
            List<CommentPostDetailViewResponse> commentPostDetailViewResponses,
            String seeUsername
    ) {
        boolean writeStatus;

        if (Objects.equals(post.getUser().getUsername(), seeUsername)) writeStatus = true;
        else writeStatus = false;

        return PostDetailViewResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .writer(post.getWriter())
                .view(post.getView())
                .likes(post.getLikes().size())
                .writeStatus(writeStatus)
                .images(imagePostDetailViewResponse)
                .comments(commentPostDetailViewResponses)
                .createTime(post.getCreateTime())
                .build();
    }

}
