package com.mck.domain.comment.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentPostPagingResponse {

    private int commentCount;

    public static CommentPostPagingResponse form(int commentCount) {
        return CommentPostPagingResponse.builder()
                .commentCount(commentCount)
                .build();

    }
}
