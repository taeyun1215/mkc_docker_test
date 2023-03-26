package com.mck.domain.alert;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

// 사용자 알람
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 수신자 (comment는 작성자, reply는 comment 작성자)
    private String username;
    // comment : 사용자 게시글에 comment
    // reply : 사용자 댓글에 reply
    private String type;
    // alert 대상 url
    private String url;
    // 메세지
    private String message;
    // 알람 확인 여부 (0: 미확인, 1: 확인)
    private Integer confirm;
    // 알람 생성일자
    private LocalDateTime createdAt;
}
