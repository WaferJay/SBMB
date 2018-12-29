package com.wanfajie.microblog.service.impl;

import com.wanfajie.microblog.bean.Comment;
import com.wanfajie.microblog.repository.MicroBlogCommentRepository;
import com.wanfajie.microblog.service.MicroBlogCommentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;

@Service
public class MicroBlogCommentServiceImpl implements MicroBlogCommentService {

    @Resource
    private MicroBlogCommentRepository commentRepository;

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
    public Page<Comment> findByMicroBlogId(long id, Pageable pageable, long beforeId) {
        return commentRepository.findAll((root, query, builder) -> {
            Predicate whereMbComment = builder.equal(root.get("microBlogId"), id);

            return beforeId == 0 ? whereMbComment :
                    builder.and(
                        whereMbComment,
                        builder.lessThan(root.get("id"), beforeId)
                    );
        }, pageable);
    }
}
