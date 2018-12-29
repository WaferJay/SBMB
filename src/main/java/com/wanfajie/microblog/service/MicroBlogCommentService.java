package com.wanfajie.microblog.service;

import com.wanfajie.microblog.bean.Comment;
import com.wanfajie.microblog.bean.MicroBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MicroBlogCommentService {
    Comment save(Comment comment);
    Comment findById(long id);
    void delete(long id);
    void delete(Comment comment);
    default Page<Comment> findByMicroBlog(MicroBlog mb, Pageable pageable) {
        return findByMicroBlogId(mb.getId(), pageable);
    }
    default Page<Comment> findByMicroBlog(MicroBlog mb, Pageable pageable, long beforeId) {
        return findByMicroBlogId(mb.getId(), pageable, beforeId);
    }
    default Page<Comment> findByMicroBlogId(long id, Pageable pageable) {
        return findByMicroBlogId(id, pageable, 0);
    }
    Page<Comment> findByMicroBlogId(long id, Pageable pageable, long beforeId);
}
