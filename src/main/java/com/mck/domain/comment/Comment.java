package com.mck.domain.comment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mck.domain.base.BaseEntity;
import com.mck.domain.post.Post;
import com.mck.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "comment")
@Builder
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private long id;

    @Column(
            length = 500,
            nullable = false
    )
    private String content; // 내용

    @Column(
            length = 100,
            nullable = false
    )
    private String writer; // 작성자

    private Long parentId; // 부모 댓글

    @ManyToOne(
            targetEntity = User.class,
            fetch = FetchType.LAZY
    ) // 실제로 요청하는 순간 가져오기 위해 LAZY로 사용함.
    @JoinColumn(name = "username")
    @JsonManagedReference // 순환참조 방지
    private User user;

    @ManyToOne(
            targetEntity = Post.class,
            fetch = FetchType.LAZY
    )
    @JsonManagedReference // 순환참조 방지
    private Post post;

}
