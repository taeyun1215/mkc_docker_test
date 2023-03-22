package com.mck.infra.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mck.domain.image.Image;
import com.mck.domain.image.ImageRepo;
import com.mck.domain.post.Post;
import com.mck.domain.post.request.PostDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final ImageRepo imageRepo;

    private static final String IMAGE_URL_PREFIX = "https://yeh-bucket.s3.ap-northeast-2.amazonaws.com/";

    @Transactional
    public List<Image> uploadFile(Post post, List<MultipartFile> multipartFile) {
        List<Image> images = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        multipartFile.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            // 이미지 DB 저장.
            String originalFilename = file.getOriginalFilename();
            String imageUrl = IMAGE_URL_PREFIX + fileName;

            Image image = Image.builder()
                    .imageName(fileName)
                    .imageUrl(imageUrl)
                    .originalImageName(originalFilename)
                    .post(post)
                    .build();

            Image saveImage = imageRepo.save(image);
            images.add(saveImage);

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch(IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }

        });

        return images;
    }

    @Transactional
    public void updateFile(Post post, List<Image> images, PostDto postDto) {
        images.forEach(image -> {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, image.getImageName()));
            imageRepo.delete(image);
        });

        if (postDto.getImageFiles() != null) {
            uploadFile(post, postDto.getImageFiles());
        }
    }

    @Transactional
    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }
}