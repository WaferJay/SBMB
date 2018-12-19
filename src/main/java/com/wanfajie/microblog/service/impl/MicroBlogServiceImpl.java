package com.wanfajie.microblog.service;

import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.repository.MicroBlogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Service
public class MicroBlogServiceImpl implements MicroBlogService {

    @Resource
    private MicroBlogRepository repository;

    @Override
    public void save(MicroBlog mb) {
        repository.save(mb);
    }

    @Override
    public Page<MicroBlog> findAll(Pageable pageable, long beforeId) {

        long finalBeforeId = beforeId == 0 ? Long.MAX_VALUE : beforeId;

        Page<MicroBlog> page = repository.findAll(new Specification<MicroBlog>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<MicroBlog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.lessThan(root.get("id"), finalBeforeId);
            }
        }, pageable);

        return page;
    }

    @Override
    public MicroBlog findById(long id) {
        return repository.findById(id)
                .orElse(null);
    }

    @Override
    public Page<MicroBlog> findByUserId(long userId, Pageable pageable) {

        Page<MicroBlog> page = repository.findAll(new Specification<MicroBlog>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<MicroBlog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<Long>get("authorId"), userId);
            }
        }, pageable);

        return page;
    }
}
