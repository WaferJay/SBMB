package com.wanfajie.microblog.repository;

import com.wanfajie.microblog.bean.MicroBlogMediaFile;
import com.wanfajie.microblog.bean.MicroBlogMediaFilePrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MicroBlogMediaRepository extends JpaRepository<MicroBlogMediaFile, MicroBlogMediaFilePrimaryKey> {
}
