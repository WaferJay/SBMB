package com.example.demo.service;

import com.example.demo.bean.User;

public interface UserService {

    User findById(long id);
    <T> User findBy(String field, T v);
    User findByName(String name);
    User findByEmail(String email);
    User save(User user);
    void delete(long id);

    default void delete(User user) {
        delete(user.getId());
    }

    void login(User user, boolean rememberMe);
    void logout();
    User getCurrentUser();
}
