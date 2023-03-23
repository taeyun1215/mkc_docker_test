package com.mck.domain.post;

import com.mck.domain.comment.Comment;
import com.mck.domain.comment.CommentService;
import com.mck.domain.comment.response.CommentPostDetailViewResponse;
import com.mck.domain.image.response.ImagePostDetailViewResponse;
import com.mck.domain.post.request.PostDto;
import com.mck.domain.post.response.PostMyWriteResponse;
import com.mck.domain.post.response.PostPagingResponse;
import com.mck.domain.post.response.PostDetailViewResponse;
import com.mck.domain.post.response.PostPopularResponse;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
@Slf4j
public class PostController {

    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;

    // Paging 게시글, 10개씩.
    @GetMapping("/all")
    public ResponseEntity<ReturnObject> pagingPost(
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ReturnObject returnObject;

        Page<Post> posts = postService.pagePostList(pageable);
        PostPagingResponse response = PostPagingResponse.from(posts);
        returnObject = ReturnObject.builder().success(true).data(response).build();

        return ResponseEntity.ok().body(returnObject);
    }

    @GetMapping("/allCount")
    public ResponseEntity<ReturnObject> allCountPost() {
        ReturnObject returnObject;

        List<Post> posts = postService.getPostAll();
        returnObject = ReturnObject.builder().success(true).data(posts.size()).build();

        return ResponseEntity.ok().body(returnObject);
    }

    // 게시글 검색
    @PostMapping("search/")
    public ResponseEntity<ReturnObject> searchPost(
            @RequestParam("keyword") String keyword,
            @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ReturnObject returnObject;

        Page<Post> posts = postService.searchPost(keyword, pageable);
        PostPagingResponse response = PostPagingResponse.from(posts);
        returnObject = ReturnObject.builder().success(true).data(response).build();

        return ResponseEntity.ok().body(returnObject);
    }

    // 게시글 상세 정보
    @GetMapping("/read/{post_id}")
    public ResponseEntity<ReturnObject> readPost(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal String username,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse
    ) {
        ReturnObject returnObject;
        ErrorObject errorObject;

        Cookie oldCookie = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("postView")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + postId.toString() + "]")) {
                postService.updateViewPost(postId);
                oldCookie.setValue(oldCookie.getValue() + "_[" + postId + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                httpServletResponse.addCookie(oldCookie);
            }
        } else {
            postService.updateViewPost(postId);
            Cookie newCookie = new Cookie("postView","[" + postId + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            httpServletResponse.addCookie(newCookie);
        }

        User user = userService.getUser(username);
        Post post = postService.viewDetailPost(postId);
        List<Comment> comments = commentService.getComments(postId);

        // images response dto
        List<ImagePostDetailViewResponse> imagePostDetailViewResponse = ImagePostDetailViewResponse.from(post.getImages());

        // comment response dto
        List<CommentPostDetailViewResponse> commentPostDetailViewResponses = CommentPostDetailViewResponse.form(comments);

        // post response dto
        PostDetailViewResponse response = PostDetailViewResponse.from(post, imagePostDetailViewResponse, commentPostDetailViewResponses, user.getUsername());

        returnObject = ReturnObject.builder().success(true).data(response).build();
        return ResponseEntity.ok().body(returnObject);
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
            postService.savePost(postDto, user);
            returnObject = ReturnObject.builder().success(true).data("게시글 등록이 완료되었습니다.").build();

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
    @GetMapping("like/{post_id}")
    public ResponseEntity<ReturnObject> likePost(
            @PathVariable("post_id") Long postId,
            @AuthenticationPrincipal String username
    ) {

        ReturnObject returnObject;
        ErrorObject errorObject;

        User user = userService.getUser(username);
        String description = postService.likePost(postId, user);

        returnObject = ReturnObject.builder().success(true).data(description).build();

        return ResponseEntity.ok().body(returnObject);
    }

    // 인기 게시글
    @GetMapping("popular")
    public ResponseEntity<ReturnObject> popularPost() {

        ReturnObject returnObject;
        ErrorObject errorObject;

        List<Post> posts = postService.popularPost();
        List<PostPopularResponse> postPopularResponse = PostPopularResponse.from(posts);
        returnObject = ReturnObject.builder().success(true).data(postPopularResponse).build();

        return ResponseEntity.ok().body(returnObject);
    }

    // 내가 쓴 게시글
    @GetMapping("/myPost")
    public ResponseEntity<ReturnObject> myPost(
            @AuthenticationPrincipal String username
    ) {

        ReturnObject returnObject;
        ErrorObject errorObject;

        List<Post> posts = postService.myPost(username);

        PostMyWriteResponse postMyWriteResponse = PostMyWriteResponse.from(posts, username);
        returnObject = ReturnObject.builder().success(true).data(postMyWriteResponse).build();

        return ResponseEntity.ok().body(returnObject);
    }

}
