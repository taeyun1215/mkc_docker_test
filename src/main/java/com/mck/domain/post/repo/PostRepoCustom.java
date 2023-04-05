package com.mck.domain.post.repo;

import com.mck.domain.post.Post;

import java.util.List;

public interface PostRepoCustom {

    // 인기 게시글
    List<Post> popularPost();

    // 내가 쓴 게시글
    List<Post> myPost(String username);

    // 닉네임 수정시 게시글 닉네임 수정
    Long editPostNickname(String existNickname, String editNickname);

}
