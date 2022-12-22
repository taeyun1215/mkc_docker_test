package com.mck.domain.image;

import com.mck.domain.post.Post;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public interface ImageService {
    // DB에 이미지 저장.
    List<Image> saveImages(Post post, List<MultipartFile> imageFiles) throws IOException;

    // 이미지 순번대로 반환해주는 JPA
    List<Image> findByPostOrderByImageIdAsc(Post post);

    // 이미지 업데이트
    void updateImage(List<MultipartFile> imageFile, Post post) throws IOException;

    // 이미지 삭제
    void deleteImage(Post post) throws IOException;
}
