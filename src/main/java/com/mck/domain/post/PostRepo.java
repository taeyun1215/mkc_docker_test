package com.mck.domain.post;

import com.mck.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepo extends JpaRepository<Post, Long> {

    Optional<Post> findById(Long post_id);
    Optional<Post> findByIdAndUser(Long post_id, User user);
    Optional<Post> findByTitle(String title);

//     keyword가 포함 된 모든 Post를 반환.
//    @Query(
//            value = "SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%",
//            countQuery = "SELECT COUNT(p.id) FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%"
//    )
//    Page<Post> findAllSearch(@Param("keyword") String keyword, Pageable pageable);

    // 게시글 수정.
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE post p SET p.title = :title, p.content = :content WHERE p.id = :id", nativeQuery = true)
    void editPost(@Param("title") String title, @Param("content") String content, @Param("id") Long id);

    // 조회수 증가.
    @Modifying(clearAutomatically = true)
    @Query(value = "UPDATE post p SET p.view = p.view + 1 WHERE p.id = :id", nativeQuery = true)
    int updateView(@Param("id") Long id);

}
