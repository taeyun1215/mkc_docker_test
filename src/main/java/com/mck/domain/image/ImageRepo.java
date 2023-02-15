package com.mck.domain.image;

import com.mck.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepo extends JpaRepository<Image, Long> {

//    List<Image> findByPost(Post post);
//    List<Image> findByPostOrderByIdAsc(Post post);
//
//    void deleteByPost(Post post);

}
