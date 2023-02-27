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

    /*
    SELECT p.id, COUNT(p.id)
    FROM post p
    inner join post_like pl
    on p.id  = pl.post_id
    WHERE 1 = 1
    AND p.create_time >= "2023-02-19 15:50:53.330"
    GROUP BY p.id
    ORDER BY COUNT(p.id) DESC
    */

    // 시간 대 검색
    private BooleanExpression betweenTime(LocalDateTime localDateTime) {
        return post.createTime.goe(localDateTime.minusDays(7));
    }
}
