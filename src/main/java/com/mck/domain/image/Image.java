package com.mck.domain.image;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mck.domain.post.Post;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "image")
@Getter
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(
            length = 200,
            nullable = false
    )
    private String originalImageName; // 원본 이미지 파일

    @Column(
            length = 500,
            nullable = false
    )
    private String imageName; // 실제로 서버에 저장할 이미지 파일명

    @Column(
            length = 500,
            nullable = false
    )
    private String imageUrl; // 이미지 조회 경로

    @ManyToOne(
            targetEntity = Post.class,
            fetch = FetchType.LAZY
    ) // 실제로 요청하는 순간 가져오기 위해 LAZY로 사용함.
    @JsonManagedReference // 순환참조 방지
    public Post post;

}
