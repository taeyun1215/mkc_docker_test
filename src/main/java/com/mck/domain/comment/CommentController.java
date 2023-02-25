package com.mck.domain.comment;

import com.mck.domain.comment.request.CommentDto;
import com.mck.domain.user.User;
import com.mck.domain.user.UserService;
import com.mck.global.utils.ErrorObject;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    // 댓글 추가
    @PostMapping("/new/{post_id}")
    public ResponseEntity<ReturnObject> saveComment(
            @PathVariable("post_id") Long postId,
            @Validated CommentDto commentDto, BindingResult bindingResult,
            @AuthenticationPrincipal String username
    ) {
        ReturnObject returnObject;
        ErrorObject errorObject;

        if (bindingResult.hasErrors()) {
            errorObject = ErrorObject.builder().code(bindingResult.getFieldError().getCode()).message(bindingResult.getFieldError().getDefaultMessage()).build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        } else {
            User user = userService.getUser(username);
            commentService.saveComment(postId, user, commentDto);
            returnObject = ReturnObject.builder().success(true).data("댓글 등록이 완료되었습니다.").build();

            return ResponseEntity.ok().body(returnObject);
        }

    }

    // 대댓글 추가
    @PostMapping("/reNew/{post_id}/{comment_id}")
    public ResponseEntity<ReturnObject> saveReComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId,
            @Validated CommentDto commentDto, BindingResult bindingResult,
            @AuthenticationPrincipal String username
    ) {
        ReturnObject returnObject;
        ErrorObject errorObject;

        if (bindingResult.hasErrors()) {
            errorObject = ErrorObject.builder().code(bindingResult.getFieldError().getCode()).message(bindingResult.getFieldError().getDefaultMessage()).build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        } else {
            User user = userService.getUser(username);
            commentService.saveReComment(postId, commentId, user, commentDto);

            returnObject = ReturnObject.builder().success(true).data("대댓글 등록이 완료되었습니다.").build();

            return ResponseEntity.ok().body(returnObject);
        }

    }


    // 댓글 수정
    @PutMapping("/edit/{comment_id}")
    public ResponseEntity<ReturnObject> editComment(
            @PathVariable("comment_id") Long commentId,
            @Validated CommentDto commentDto, BindingResult bindingResult,
            @AuthenticationPrincipal String username
    ) {
        ReturnObject returnObject;
        ErrorObject errorObject;

        if (bindingResult.hasErrors()) {
            errorObject = ErrorObject.builder().code(bindingResult.getFieldError().getCode()).message(bindingResult.getFieldError().getDefaultMessage()).build();
            returnObject = ReturnObject.builder().success(false).error(errorObject).build();

            return ResponseEntity.ok().body(returnObject);
        } else {
            User user = userService.getUser(username);
            commentService.updateComment(commentId, user, commentDto);

            returnObject = ReturnObject.builder().success(true).data("댓글 수정이 완료되었습니다.").build();

            return ResponseEntity.ok().body(returnObject);
        }

    }

    // 댓글 삭제
    @DeleteMapping("/delete/{comment_id}")
    public ResponseEntity<ReturnObject> deleteComment(
            @PathVariable("comment_id") Long commentId,
            @AuthenticationPrincipal String username
    ) {
        ReturnObject returnObject;
        ErrorObject errorObject;

        User user = userService.getUser(username);
        commentService.deleteComment(commentId, user);

        returnObject = ReturnObject.builder().success(true).data("댓글 삭제가 완료되었습니다.").build();

        return ResponseEntity.ok().body(returnObject);
    }

}
