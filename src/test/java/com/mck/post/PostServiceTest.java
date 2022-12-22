package com.mck.post;

import ch.qos.logback.core.util.Loader;
import com.mck.domain.post.Post;
import com.mck.domain.post.PostDto;
import com.mck.domain.post.PostRepo;
import com.mck.domain.post.PostService;
import com.mck.domain.user.User;
import com.mck.domain.user.UserService;
import com.mck.domain.user.dto.UserSignUpDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional  // 테스트는 여러번 반복해서 실행해야 하므로 DB에 반영이 안 되게 하기 위해서 사용함.
public class PostServiceTest {

    @Autowired
    private PostService postService;
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("글 작성 테스트")
    void registerPost() {
        // given
        UserSignUpDto user = new UserSignUpDto(
                "devty1215",
                "qwer123!@#",
                "qwer123!@#",
                "taeyun1215@naver.com",
                "gp_dted"
        );

        User userEntity = user.toEntity(passwordEncoder);
        User saveUser = userService.saveUser(userEntity); // 유저 정보 DB에 저장하기

        String fileName = "";
        Resource resource = Loader.getResource()
        List<MockMultipartFile> files

        PostDto postDto = new PostDto(
                "제목입니다.",
                "내용입니다.",
                "이태윤",

        );

        // when
        Post savePost = postService.savePost(postDto, saveUser); // 여기서 유저 정보를 찾기에 위해서 DB에 저장해줘야함.

        // then
        Optional<Post> findPost = postRepo.findByTitle("제목입니다.");
        Assertions.assertEquals(savePost, findPost.get());
    }

    @Test
    @DisplayName("글 수정 테스트")
    void editPost() {
        // given
        UserSignupDto user = new UserSignupDto(
                "devty1215",
                "qwer123!@#",
                "qwer123!@#",
                "taeyun1215@naver.com",
                "gp_dted"
        );

        User saveUser = userService.signup(user);

        PostDto postDto = new PostDto(
                "제목입니다.",
                "내용입니다."
        );

        Post savePost = postService.registerPost(postDto, saveUser);

        PostDto postEditDto = new PostDto(
                "수정된 제목입니다.",
                "수정된 내용입니다."
        );

        // when
        Post EditPost = postService.editPost(savePost.getPostId(), postEditDto, saveUser);

        // then
        Optional<Post> findPost = postRepo.findByPostId(savePost.getPostId());
        Assertions.assertEquals(EditPost.getTitle(), findPost.get().getTitle());
    }

    @Test
    @DisplayName("글 삭제 테스트")
    void deletePost() {
        // given
        UserSignupDto user = new UserSignupDto(
                "devty1215",
                "qwer123!@#",
                "qwer123!@#",
                "taeyun1215@naver.com",
                "gp_dted"
        );

        User saveUser = userService.saveUser(user);

        PostDto postDto = new PostDto(
                "제목입니다.",
                "내용입니다."
        );

        Post savePost = postService.registerPost(postDto, saveUser);

        // when
        postService.deletePost(savePost.getId(), saveUser);

        // then
        Optional<Post> findPost = postRepo.findById(savePost.getId());
        Assertions.assertEquals(true, findPost.isEmpty());
    }

}