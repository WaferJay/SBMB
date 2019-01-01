package com.wanfajie.microblog.repository;

import com.wanfajie.microblog.bean.MicroBlog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MicroBlogRepository extends JpaRepository<MicroBlog, Long>, JpaSpecificationExecutor<MicroBlog> {

    @Query(value = "select mb.* from mb_microblog as mb " +
            "left join mb_user_subscribe as sub on mb.author_id = sub.following_id " +
            "where sub.follower_id = :userId or mb.author_id = :userId",
            countQuery = "select count(1) from mb_user_subscribe as sub " +
                    "left join mb_microblog as mb on mb.author_id = sub.following_id " +
                    "where sub.follower_id = :userId or mb.author_id = :userId",
            nativeQuery = true)
    Page<MicroBlog> findSubscribeMicroBlog(@Param("userId") long userId, Pageable pageable);

    @Query(value = "select mb.* from mb_microblog as mb " +
            "left join mb_user_subscribe as sub " +
            "on mb.author_id = sub.following_id and mb.id <= :beforeId " +
            "where sub.follower_id = :userId or mb.author_id = :userId",
            countQuery = "select count(1) from mb_user_subscribe as sub " +
                    "left join mb_microblog as mb on mb.author_id = sub.following_id and mb.id <= :beforeId " +
                    "where sub.follower_id = :userId or mb.author_id = :userId",
            nativeQuery = true)
    Page<MicroBlog> findSubscribeMicroBlog(@Param("userId") long userId,
                                           @Param("beforeId") long beforeId,
                                           Pageable pageable);
}
