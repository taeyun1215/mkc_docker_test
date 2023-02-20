package com.mck.domain.post;

import com.mck.domain.post.request.PostDto;
import com.mck.domain.post.response.PostViewResponse;
import com.mck.domain.user.User;
import com.mck.domain.user.UserService;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // Paging 게시글, 10개씩.
    @GetMapping("/all")
    public ResponseEntity<ReturnObject> pagingPost(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Post> posts = postService.pagePostList(pageable);

        ReturnObject object = ReturnObject.builder()
                .data(posts)
                .build();

        return ResponseEntity.ok().body(object);
    }

    // 게시글 검색
    @PostMapping("search/{keyword}")
    public ResponseEntity<ReturnObject> searchPost(
            @PathVariable("keyword") String keyword,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Post> posts = postService.searchPost(keyword, pageable);

        ReturnObject object = ReturnObject.builder()
                .data(posts)
                .build();

        return ResponseEntity.ok().body(object);
    }

    // 게시글 상세 정보 // todo : 쿠키나 세션을 이용하여 조회수 중복 카운터를 방지하기
    @GetMapping("/read/{post_id}")
    public ResponseEntity<ReturnObject> readPost(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal String username
    ) {
        User user = userService.getUser(username);
        Post post = postService.updateViewPost(postId);

        // 자기가 쓴 게시물을 자기가 본다면 수정, 삭제를 할 수 있게 해주는 부분.
        if (post.getUser().getId().equals(user.getId())) {
            ReturnObject object = ReturnObject.builder()
                    .data(post) // todo : writer = true 로 두고 싶음.
                    .build();

            return ResponseEntity.ok().body(object);
        }

        ReturnObject object = ReturnObject.builder()
                .data(post)
                .build();

        return ResponseEntity.ok().body(object);
    }

    // 게시글 추가
    @PostMapping("/new")
    public ResponseEntity<ReturnObject> savePost(
            @Validated @ModelAttribute("postDto") PostDto postDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String username
    ) throws IOException {

        ReturnObject returnObject;
        ErrorObject errorObject;

        if (bindingResult.hasErrors()) {
            errorObject = ErrorObject.builder().code(bindingResult.getFieldError().getCode()).message(bindingResult.getFieldError().getDefaultMessage()).build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        } else {
            User user = userService.getUser(username);
            Post post = postService.savePost(postDto, user);
            PostViewResponse response = PostViewResponse.from(post);
            returnObject = ReturnObject.builder().success(true).data(response).build();

            return ResponseEntity.ok().body(returnObject);
        }
    }

    // 게시글 수정
    @PutMapping("/edit/{post_id}")
    public ResponseEntity<ReturnObject> editPost(
            @PathVariable("post_id") Long postId,
            @Validated @ModelAttribute("postDto") PostDto postDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal String username
    ) throws IOException {

        ReturnObject returnObject;
        ErrorObject errorObject;

        if (bindingResult.hasErrors()) {
            errorObject = ErrorObject.builder().code(bindingResult.getFieldError().getCode()).message(bindingResult.getFieldError().getDefaultMessage()).build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        } else {
            User user = userService.getUser(username);
            postService.editPost(postId, postDto, user);
            returnObject = ReturnObject.builder().success(true).data("수정이 완료되었습니다.").build();

            return ResponseEntity.ok().body(returnObject);
        }
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{post_id}")
    public ResponseEntity<ReturnObject> deletePost(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal String username
    ) throws IOException {

        ReturnObject returnObject;
        ErrorObject errorObject;

        User user = userService.getUser(username);
        postService.deletePost(postId, user);
        returnObject = ReturnObject.builder().success(true).data("삭제가 완료되었습니다.").build();

        return ResponseEntity.ok().body(returnObject);
    }

    // 게시글 좋아요
    @PostMapping("like/{post_id}")
    public ResponseEntity<ReturnObject> likePost(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal String username
    ) {

        User user = userService.getUser(username);
        postService.likePost(postId, user);

        ReturnObject object = ReturnObject.builder()
                .build();

        return ResponseEntity.ok().body(object);
    }

}
