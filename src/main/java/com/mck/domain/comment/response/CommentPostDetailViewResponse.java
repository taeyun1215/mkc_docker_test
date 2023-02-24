package com.mck.domain.comment.response;

import com.mck.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentPostDetailViewResponse {

    private String content;
    private String writer;
    private LocalDateTime createTime;

    public static CommentPostDetailViewResponse form(Comment comment) {

        if (comment == null) {
            return null;
        } else {
            return CommentPostDetailViewResponse.builder()
                    .content(comment.getContent())
                    .writer(comment.getWriter())
                    .createTime(comment.getCreateTime())
                    .build();
        }

    }

}
