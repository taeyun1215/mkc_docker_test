package com.mck.domain.comment;

import com.mck.domain.comment.request.CommentDto;
import com.mck.domain.post.Post;
import com.mck.domain.post.repo.PostRepo;
import com.mck.domain.user.User;
import com.mck.domain.user.UserRepo;
import com.mck.global.error.BusinessException;
import com.mck.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepo commentRepo;
    private final PostRepo postRepo;
    private final UserRepo userRepo;

    @Override
    @Transactional
    public List<Comment> getComments(Long postId) {
        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        List<Comment> comments = commentRepo.findByPostOrderByIdAsc(findPost);
        List<Comment> headComments = new ArrayList<>(); // 부모 댓글
        List<Comment> arrangeComments = new ArrayList<>(); // 정렬된 댓글

        for (Comment comment : comments) {
            if (comment.getParentId() == null) {
                headComments.add(comment);
            }
        }

        for (Comment headComment : headComments) {
            arrangeComments.add(headComment);

            for (Comment comment : comments) {
                if (comment.getParentId() != null) {
                    if (headComment.getId() == comment.getParentId()) {
                        arrangeComments.add(comment);
                    }
                }
            }
        }

        return arrangeComments;
    }


    @Override
    @Transactional
    public void saveComment(Long postId, User user, CommentDto commentDto) {

        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        Comment comment = commentDto.toEntity(findUser, findPost, null);
        Comment saveComment = commentRepo.save(comment);

        log.info("새로운 댓글 정보를 DB에 저장했습니다 : ", saveComment.getId());

    }

    @Override
    @Transactional
    public void saveReComment(Long postId, Long commentId, User user, CommentDto reCommentDto) {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        Post findPost = postRepo.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_EXIST_POST));

        Comment reComment = reCommentDto.toEntity(findUser, findPost, commentId);
        Comment saveComment = commentRepo.save(reComment);
        log.info("새로운 대댓글 정보를 DB에 저장했습니다 : ", saveComment.getId());

    }

    @Override
    @Transactional
    public void updateComment(Long commentId, User user, CommentDto commentDto) {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        validateUpdateComment(commentId, findUser);

        commentRepo.editComment(commentDto.getContent(), commentId);
        log.info("댓글을 수정하였습니다 : ", commentDto.getContent());
    }

    @Transactional
    public void validateUpdateComment(Long commentId, User user) {
        commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_EXIST_COMMENT.getMessage()));

        commentRepo.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_EDIT_PERMISSION_COMMENT.getMessage()));
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, User user) {
        User findUser = userRepo.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.NOT_EXISTING_ACCOUNT.getMessage()));

        validateDeleteComment(commentId, findUser);
        Optional<Comment> deleteComment = commentRepo.findById(commentId);

        commentRepo.delete(deleteComment.get());
        log.info("댓글을 삭제하였습니다 : ", commentId);
    }

    @Transactional
    public void validateDeleteComment(Long commentId, User user) {
        commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_EXIST_COMMENT.getMessage()));

        commentRepo.findByIdAndUser(commentId, user)
                .orElseThrow(() -> new RuntimeException(ErrorCode.NOT_DELETE_PERMISSION_COMMENT.getMessage()));
    }

}
