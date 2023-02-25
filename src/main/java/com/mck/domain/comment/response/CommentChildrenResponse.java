package com.mck.domain.comment.response;

import com.mck.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentChildrenResponse {

    private long id;
    private String content;
    private String writer;
    private LocalDateTime createTime;

    public static CommentChildrenResponse from(Comment comment) {
        return CommentChildrenResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .writer(comment.getWriter())
                .createTime(comment.getCreateTime())
                .build();
    }

}
