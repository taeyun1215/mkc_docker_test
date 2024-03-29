package com.mck.domain.post.request;

import com.mck.domain.post.Post;
import com.mck.domain.user.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
public class PostEditDto {

    @NotBlank(message = "제목은 필수 입력 값입니다.")
    private String title;

    @NotBlank(message = "내용은 필수 입력 값입니다.")
    private String content;

    private String writer;

    private List<MultipartFile> imageFiles; // 새로 추가 된 이미지 파일

    private List<String> imagesId; // 기존 이미지 파일

    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .content(content)
                .writer(user.getNickname())
                .user(user)
                .build();
    }
}
