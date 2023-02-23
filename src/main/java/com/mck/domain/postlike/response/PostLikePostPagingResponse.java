package com.mck.domain.postlike.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostLikePostPagingResponse {

    private int postLikeCount;

    public static PostLikePostPagingResponse form(int postLikeCount) {
        return PostLikePostPagingResponse.builder()
                .postLikeCount(postLikeCount)
                .build();
    }

}
