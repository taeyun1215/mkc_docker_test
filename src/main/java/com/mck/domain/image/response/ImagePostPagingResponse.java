package com.mck.domain.image.response;

import com.mck.domain.image.Image;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Null;

@Getter
@Builder
public class ImagePostPagingResponse {

    private String firstImageUrl;
    private long totalImagesCount;

    public static ImagePostPagingResponse from(Image image, long count) {

        if (image == null) {
            return ImagePostPagingResponse.builder()
                    .firstImageUrl(null)
                    .totalImagesCount(0)
                    .build();
        } else {
            return ImagePostPagingResponse.builder()
                    .firstImageUrl(image.getImageUrl())
                    .totalImagesCount(count)
                    .build();
        }

    }
}
