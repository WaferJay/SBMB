package com.example.demo.service;

import com.example.demo.bean.MicroBlog;
import com.example.demo.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MicroBlogService {

    void save(MicroBlog mb);
    MicroBlog findById(long id);
    Page<MicroBlog> findAll(Pageable pageable, long beforeId);
    default Page<MicroBlog> findByUser(User user, Pageable pageable) {
        return findByUserId(user.getId(), pageable);
    }
    Page<MicroBlog> findByUserId(long userId, Pageable pageable);
}
