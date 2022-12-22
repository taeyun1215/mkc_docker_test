package com.mck.domain.postlike;

import com.mck.domain.post.Post;
import com.mck.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepo extends JpaRepository<PostLike, Long> {

    // 게시글에 좋아요 유무 판단
    Optional<PostLike> findByPostAndUser(Post post, User user);
}
