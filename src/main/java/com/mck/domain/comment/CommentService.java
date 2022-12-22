package com.mck.domain.comment;

import com.mck.domain.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public interface CommentService {

    // 댓글 순차 정렬
    List<Comment> getComments(Long postId);

    // 댓글 저장
    Comment saveComment(Long postId, User user, CommentDto commentDto);

    // 대댓글 저장
    Comment saveReComment(Long postId, Long commentId, User user, CommentDto commentDto);

    // 댓글 수정
    void updateComment(Long commentId, User user, CommentDto commentDto);

    // 댓글 삭제
    void deleteComment(Long commentId, User user);
}
