package com.mck.domain.useremail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Column;

@Getter @Setter
@RedisHash(timeToLive = 600)
@AllArgsConstructor
@Builder
public class UserEmail {
    @Id
    private String id;

    @Indexed
    @Column(unique = true)
    private String email;

    private String code;
}
