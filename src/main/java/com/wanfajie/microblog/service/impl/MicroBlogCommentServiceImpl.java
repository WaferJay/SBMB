package com.wanfajie.microblog.service.impl;

import com.wanfajie.microblog.bean.Comment;
import com.wanfajie.microblog.repository.MicroBlogCommentRepository;
import com.wanfajie.microblog.service.MicroBlogCommentService;
import com.wanfajie.microblog.service.MicroBlogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MicroBlogCommentServiceImpl implements MicroBlogCommentService {

    @Resource
    private MicroBlogCommentRepository commentRepository;

    @Resource
    private MicroBlogService mbService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public Comment findById(long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public Page<Comment> findByMicroBlogId(long id, Pageable pageable) {
        return commentRepository.findAll((root, query, builder) -> builder.equal(root.<Long>get("microBlogId"), id), pageable);
    }
}
