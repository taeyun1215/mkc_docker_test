package com.mck.domain.image.response;

import com.mck.domain.image.Image;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class ImagePostDetailViewResponse {

    private Long id;
    private String originalImageName;
    private String imageName;
    private String imageUrl;

    public static List<ImagePostDetailViewResponse> from(List<Image> images) {
        List<ImagePostDetailViewResponse> imagePostDetailViewResponses = new ArrayList<>();

        images.forEach(image -> {
            ImagePostDetailViewResponse saveImage = ImagePostDetailViewResponse.builder()
                    .id(image.getId())
                    .originalImageName(image.getOriginalImageName())
                    .imageName(image.getImageName())
                    .imageUrl(image.getImageUrl())
                    .build();

            imagePostDetailViewResponses.add(saveImage);
        });

        if (images.size() == 0) {
            imagePostDetailViewResponses.add(null);
        }

        return imagePostDetailViewResponses;
    }
}
