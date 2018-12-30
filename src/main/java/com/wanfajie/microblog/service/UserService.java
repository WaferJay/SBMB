package com.wanfajie.microblog.service;

import com.wanfajie.microblog.bean.User;

public interface UserService {

    User findById(long id);
    <T> User findBy(String field, T v);
    User findByName(String name);
    User findByEmail(String email);
    User save(User user);
    void delete(long id);
    boolean exists(long id);

    default void delete(User user) {
        delete(user.getId());
    }

    void login(User user, boolean rememberMe);
    void logout();
    User getCurrentUser();
    boolean follow(User follower, User following);
    boolean unFollow(User follower, User following);
}
