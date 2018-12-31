package com.wanfajie.microblog.service;

import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface MicroBlogService {

    MicroBlog save(MicroBlog mb);
    MicroBlog findById(long id);
    Page<MicroBlog> findAll(Pageable pageable, long beforeId);
    default Page<MicroBlog> findByUser(User user, Pageable pageable) {
        return findByUserId(user.getId(), pageable);
    }
    Page<MicroBlog> findByUserId(long userId, Pageable pageable);
    void delete(long id);
    void delete(MicroBlog mb);
    boolean exists(long id);
    boolean isUserLiked(User user, MicroBlog microBlog);
    boolean like(User user, MicroBlog microBlog);
    boolean unLike(User user, MicroBlog microBlog);
    default Page<MicroBlog> findSubscribeMicroBlog(User user, Pageable pageable) {
        return findSubscribeMicroBlog(user, 0, pageable);
    }
    Page<MicroBlog> findSubscribeMicroBlog(User user, long beforeMId, Pageable pageable);
}
