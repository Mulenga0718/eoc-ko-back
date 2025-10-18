package com.ibs.user.repository;

import com.ibs.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.ibs.user.domain.QUser.user; // QUser 클래스 임포트

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<User> findUsersByUsernameContaining(String usernameKeyword) {
        return queryFactory
                .selectFrom(user)
                .where(user.username.containsIgnoreCase(usernameKeyword))
                .fetch();
    }
}
