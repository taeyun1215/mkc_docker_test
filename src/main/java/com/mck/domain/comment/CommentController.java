package com.mck.domain.comment;

import com.mck.domain.user.User;
import com.mck.domain.user.UserRepo;
import com.mck.global.error.ErrorCode;
import com.mck.global.service.UserDetailsImpl;
import com.mck.global.utils.ReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comment")
@Slf4j
public class CommentController {

    private final CommentService commentService;

    private final UserRepo userRepo; // 삭제 예정.

    // 게시글에 모든 댓글 불러오기
    @GetMapping("{post_id}")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("post_id") Long postId) {
        return ResponseEntity.ok().body(commentService.getComments(postId));
    }

    // 댓글 추가
    @PostMapping("/new/{post_id}")
    public ResponseEntity<ReturnObject> saveComment(
            @PathVariable("post_id") Long postId,
            CommentDto commentDto, BindingResult bindingResult
//            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/comment/save").toUriString());
//        User user = userDetails.getUser();

        Optional<User> userOptional = userRepo.findByEmail("taeyun1215@naver.com");  // 삭제 예정.
        User user = userOptional.get(); // 삭제 예정.

        if (bindingResult.hasErrors()) {
            ReturnObject object = ReturnObject.builder()
                    .msg(ErrorCode.MISMATCHED_FORMAT.getMessage())
                    .type(bindingResult.getFieldError().getCode())
                    .build();

            return ResponseEntity.badRequest().body(object);
        } else {
            Comment comment = commentService.saveComment(postId, user, commentDto);

            ReturnObject object = ReturnObject.builder()
                    .msg("ok")
                    .data(comment)
                    .build();

            return ResponseEntity.created(uri).body(object);
        }

    }

    // 대댓글 추가
    @PostMapping("/reNew/{post_id}/{comment_id}")
    public ResponseEntity<ReturnObject> saveReComment(
            @PathVariable("post_id") Long postId,
            @PathVariable("comment_id") Long commentId,
            CommentDto commentDto, BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/reComment/save").toUriString());
//        User user = userDetails.getUser();

        Optional<User> userOptional = userRepo.findByEmail("taeyun1215@naver.com");  // 삭제 예정.
        User user = userOptional.get(); // 삭제 예정.

        if (bindingResult.hasErrors()) {
            ReturnObject object = ReturnObject.builder()
                    .msg(ErrorCode.MISMATCHED_FORMAT.getMessage())
                    .type(bindingResult.getFieldError().getCode())
                    .build();

            return ResponseEntity.badRequest().body(object);
        } else {
            Comment comment = commentService.saveReComment(postId, commentId, user, commentDto);

            ReturnObject object = ReturnObject.builder()
                    .msg("ok")
                    .data(comment)
                    .build();

            return ResponseEntity.created(uri).body(object);
        }

    }


    // 댓글 수정
    @PutMapping("/edit/{comment_id}")
    public ResponseEntity<ReturnObject> editComment(
            @PathVariable("comment_id") Long commentId,
            CommentDto commentDto, BindingResult bindingResult,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // User user = userDetails.getUser();

        Optional<User> userOptional = userRepo.findByEmail("taeyun1215@naver.com");  // 삭제 예정.
        User user = userOptional.get(); // 삭제 예정.

        if (bindingResult.hasErrors()) {
            ReturnObject object = ReturnObject.builder()
                    .msg(ErrorCode.MISMATCHED_FORMAT.getMessage())
                    .type(bindingResult.getFieldError().getCode())
                    .build();

            return ResponseEntity.badRequest().body(object);
        } else {
            commentService.updateComment(commentId, user, commentDto);

            ReturnObject object = ReturnObject.builder()
                    .msg("ok")
                    .build();

            return ResponseEntity.ok().body(object);
        }

    }

    // 댓글 삭제
    @DeleteMapping("/delete/{comment_id}")
    public ResponseEntity<ReturnObject> deleteComment(
            @PathVariable("comment_id") Long commentId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // User user = userDetails.getUser();

        Optional<User> userOptional = userRepo.findByEmail("taeyun1215@naver.com");  // 삭제 예정.
        User user = userOptional.get(); // 삭제 예정.

        commentService.deleteComment(commentId, user);

        ReturnObject object = ReturnObject.builder()
                .msg("ok")
                .build();

        return ResponseEntity.ok().body(object);
    }

}
