package com.mck.domain.post.repo;

import com.mck.domain.post.Post;
import com.mck.domain.post.QPost;
import com.mck.domain.postlike.QPostLike;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PostRepoImpl implements PostRepoCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;
    private final QPostLike postLike = QPostLike.postLike;

    @Override
    public List<Post> popularPost() {
        return jpaQueryFactory
                .selectFrom(post)
                .leftJoin(post.likes, postLike)
                .where(
                        betweenTime(LocalDateTime.now())
                )
                .groupBy(post.id)
                .orderBy(post.id.count().desc(), postLike.id.desc().nullsLast(), post.id.asc())
                .limit(10)
                .fetch();
    }

    @Override
    public List<Post> myPost(String username) {
        return jpaQueryFactory
                .selectFrom(post)
                .where(
                        sameUsername(username)
                )
                .orderBy(post.id.desc())
                .fetch();
    }

    // 시간 대 검색
    private BooleanExpression betweenTime(LocalDateTime localDateTime) {
        return post.createTime.goe(localDateTime.minusDays(14));
    }

    // 시간 대 검색
    private BooleanExpression sameUsername(String username) {
        return post.user.username.eq(username);
    }

}
