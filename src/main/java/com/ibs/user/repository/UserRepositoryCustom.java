package com.ibs.user.repository;

import com.ibs.user.domain.User;

import java.util.List;

public interface UserRepositoryCustom {
    List<User> findUsersByUsernameContaining(String usernameKeyword);
}
