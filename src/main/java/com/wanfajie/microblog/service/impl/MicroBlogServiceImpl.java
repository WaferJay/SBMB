package com.wanfajie.microblog.service.impl;

import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.MicroBlogLike;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.repository.MicroBlogRepository;
import com.wanfajie.microblog.service.MicroBlogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@Service
public class MicroBlogServiceImpl implements MicroBlogService {

    @Resource
    private MicroBlogRepository mbRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public MicroBlog save(MicroBlog mb) {
        return mbRepository.save(mb);
    }

    @Override
    public Page<MicroBlog> findAll(Pageable pageable, long beforeId) {

        long finalBeforeId = beforeId == 0 ? Long.MAX_VALUE : beforeId;

        Page<MicroBlog> page = mbRepository.findAll(new Specification<MicroBlog>() {
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
        return mbRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Page<MicroBlog> findByUserId(long userId, Pageable pageable) {

        Page<MicroBlog> page = mbRepository.findAll(new Specification<MicroBlog>() {
            @Nullable
            @Override
            public Predicate toPredicate(Root<MicroBlog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.equal(root.<Long>get("authorId"), userId);
            }
        }, pageable);

        return page;
    }

    @Override
    public void delete(long id) {
        mbRepository.deleteById(id);
    }

    @Override
    public void delete(MicroBlog mb) {
        mbRepository.delete(mb);
    }

    @Override
    public boolean exists(long id) {
        return mbRepository.existsById(id);
    }

    private MicroBlogLike getLikeRecord(User user, MicroBlog microBlog) {
        return entityManager.find(MicroBlogLike.class, new MicroBlogLike(user.getId(), microBlog.getId()).getPrimaryKey());
    }

    @Override
    @Transactional
    public boolean like(User user, MicroBlog microBlog) {

        if (getLikeRecord(user, microBlog) != null) {
            return false;
        }

        MicroBlogLike record = new MicroBlogLike(user.getId(), microBlog.getId());

        entityManager.persist(record);
        microBlog.setLikeCount(microBlog.getLikeCount() + 1);
        entityManager.merge(microBlog);

        return true;
    }

    @Override
    @Transactional
    public boolean unLike(User user, MicroBlog microBlog) {

        MicroBlogLike likeRecord = getLikeRecord(user, microBlog);
        if (likeRecord == null) {
            return false;
        }

        entityManager.remove(likeRecord);
        long count = microBlog.getLikeCount() - 1;
        microBlog.setLikeCount(count >= 0 ? count : 0);

        return true;
    }
}
