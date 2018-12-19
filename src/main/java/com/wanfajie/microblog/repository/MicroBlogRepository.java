package com.wanfajie.microblog.repository;

import com.wanfajie.microblog.bean.MicroBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MicroBlogRepository extends JpaRepository<MicroBlog, Long>, JpaSpecificationExecutor<MicroBlog> {

}
