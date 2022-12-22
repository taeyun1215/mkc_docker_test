package com.mck.domain.image;

import com.mck.domain.post.Post;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // 테스트 코드 작성용
public class ImageDto {
    private Long id;
    private String originalImageName;
    private String imageName;
    private String imageUrl;

    private Post post;

    public Image toEntity(Post post) {
        return Image.builder()
                .id(id)
                .originalImageName(originalImageName)
                .imageName(imageName)
                .imageUrl(imageUrl)
                .post(post)
                .build();
    }

}