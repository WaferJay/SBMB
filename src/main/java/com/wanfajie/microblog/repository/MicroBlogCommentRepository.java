package com.wanfajie.microblog.repository;

import com.wanfajie.microblog.bean.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MicroBlogCommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
}
