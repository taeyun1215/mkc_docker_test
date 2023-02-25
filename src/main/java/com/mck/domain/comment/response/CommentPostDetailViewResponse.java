package com.mck.domain.comment.response;

import com.mck.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Builder
public class CommentPostDetailViewResponse {

    private Long id;
    private String content;
    private String writer;
    private LocalDateTime createTime;
    private List<CommentChildrenResponse> children;

    public static List<CommentPostDetailViewResponse> form(List<Comment> comments) {
        List<CommentPostDetailViewResponse> commentPostDetailViewResponses = new ArrayList<>();

        if (comments.size() != 0) {
            for (Comment comment : comments) {
                if (comment.getParentId() == null) {
                    CommentPostDetailViewResponse commentPostDetailViewResponse = CommentPostDetailViewResponse.builder()
                            .id(comment.getId())
                            .content(comment.getContent())
                            .writer(comment.getWriter())
                            .createTime(comment.getCreateTime())
                            .build();

                    commentPostDetailViewResponses.add(commentPostDetailViewResponse);
                }
            }

            for (CommentPostDetailViewResponse commentPostDetailViewResponse : commentPostDetailViewResponses) {
                List<CommentChildrenResponse> children = new ArrayList<>();
                for (Comment comment : comments) {
                    if (comment.getParentId() != null) {
                        if (Objects.equals(commentPostDetailViewResponse.getId(), comment.getParentId())) {
                            CommentChildrenResponse commentChildrenResponse = CommentChildrenResponse.from(comment);
                            children.add(commentChildrenResponse);
                        }
                    }
                }
                commentPostDetailViewResponse.setChildren(children);
            }
        } else return null;

        return commentPostDetailViewResponses;
    }

}
