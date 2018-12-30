package com.wanfajie.microblog.service;

import com.wanfajie.microblog.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface UserService {

    Sort SORT_DEFAULT = Sort.by("timestamp");

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
    Page<User> findFollowing(User user, Pageable pageable);
    Page<User> findFollower(User user, Pageable pageable);
    default Page<User> findFollowing(User user, int page, int limit) {
        return findFollowing(user, PageRequest.of(page, limit, SORT_DEFAULT));
    }
    default Page<User> findFollower(User user, int page, int limit) {
        return findFollower(user, PageRequest.of(page, limit, SORT_DEFAULT));
    }
}
