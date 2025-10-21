package com.ibs.post.repository;

import com.ibs.post.domain.Post;
import com.ibs.post.domain.QPost;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchPosts(String category, String tag, String query, Pageable pageable) {
        QPost post = QPost.post;

        List<Post> content = queryFactory
                .selectFrom(post)
                .where(
                        categoryEq(category),
                        tagEq(tag),
                        queryContains(query)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.publishedAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(
                        categoryEq(category),
                        tagEq(tag),
                        queryContains(query)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression categoryEq(String category) {
        return StringUtils.hasText(category) ? QPost.post.category.name.equalsIgnoreCase(category) : null;
    }

    private BooleanExpression tagEq(String tag) {
        return StringUtils.hasText(tag) ? QPost.post.tags.any().name.equalsIgnoreCase(tag) : null;
    }

    private BooleanExpression queryContains(String query) {
        if (!StringUtils.hasText(query)) {
            return null;
        }
        return QPost.post.title.containsIgnoreCase(query)
                .or(QPost.post.content.containsIgnoreCase(query));
    }
}
