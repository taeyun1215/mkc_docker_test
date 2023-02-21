package com.mck.domain.image.response;

import com.mck.domain.image.Image;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ImageViewResponse {

    private Long id;
    private String originalImageName;
    private String imageName;
    private String imageUrl;

    public static List<ImageViewResponse> from(List<Image> images) {
        List<ImageViewResponse> imageViewResponses = new ArrayList<>();

        images.forEach(image -> {
            ImageViewResponse saveImage = ImageViewResponse.builder()
                    .id(image.getId())
                    .originalImageName(image.getOriginalImageName())
                    .imageName(image.getImageName())
                    .imageUrl(image.getImageUrl())
                    .build();

            imageViewResponses.add(saveImage);
        });

        return imageViewResponses;
    }
}
