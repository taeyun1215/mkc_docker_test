package com.mck.domain.comment;

import com.mck.domain.post.Post;
import com.mck.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {

    // 댓글 정렬
    List<Comment> findByPostOrderByIdAsc(Post post);

    // 댓글에 대한 권한 조회
    Optional<Comment> findByIdAndUser(Long id, User user);

    // 댓글 수정
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE comment c SET c.content = :content WHERE c.id = :id", nativeQuery = true)
    void editComment(@Param("content") String content, @Param("id") Long id);

}
