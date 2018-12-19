package com.example.demo.repository;

import com.example.demo.bean.MicroBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MicroBlogRepository extends JpaRepository<MicroBlog, Long>, JpaSpecificationExecutor<MicroBlog> {

}
