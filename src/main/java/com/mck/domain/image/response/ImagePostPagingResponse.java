package com.mck.domain.image.response;

import com.mck.domain.image.Image;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImagePostPagingResponse {

    private String firstImageUrl;
    private long totalImagesCount;

    public static ImagePostPagingResponse from(Image image, long count) {
        return ImagePostPagingResponse.builder()
                .firstImageUrl(image.getImageUrl())
                .totalImagesCount(count)
                .build();
    }
}
