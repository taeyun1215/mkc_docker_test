package com.mck.domain.comment.response;

import com.mck.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentPostDetailViewResponse {

    private String content;
    private String writer;

    public static CommentPostDetailViewResponse form(Comment comment) {

        if (comment == null) {
            return null;
        } else {
            return CommentPostDetailViewResponse.builder()
                    .content(comment.getContent())
                    .writer(comment.getWriter())
                    .build();
        }

    }

}
