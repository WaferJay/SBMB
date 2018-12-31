package com.wanfajie.microblog.service.impl;

import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.MicroBlogLike;
import com.wanfajie.microblog.bean.MicroBlogLikePrimaryKey;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.repository.MicroBlogRepository;
import com.wanfajie.microblog.service.MicroBlogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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

        if (beforeId == 0) {
            return mbRepository.findAll(pageable);
        } else {
            return mbRepository.findAll((root, query, builder) -> builder.lessThanOrEqualTo(root.get("id"), beforeId), pageable);
        }
    }

    @Override
    public MicroBlog findById(long id) {
        return mbRepository.findById(id)
                .orElse(null);
    }

    @Override
    public Page<MicroBlog> findByUserId(long userId, Pageable pageable) {
        return mbRepository.findAll((root, query, builder) -> builder.equal(root.get("authorId"), userId), pageable);
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

    @Override
    public boolean isUserLiked(User user, MicroBlog microBlog) {
        MicroBlogLike likeRecord = getLikeRecord(user, microBlog);

        return likeRecord != null;
    }

    private MicroBlogLike getLikeRecord(User user, MicroBlog microBlog) {
        Object key = new MicroBlogLikePrimaryKey(microBlog, user);

        return entityManager.find(MicroBlogLike.class, key);
    }

    @Override
    @Transactional
    public boolean like(User user, MicroBlog microBlog) {

        if (isUserLiked(user, microBlog)) {
            return false;
        }

        MicroBlogLike record = new MicroBlogLike(microBlog, user);

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
        entityManager.merge(microBlog);

        return true;
    }

    @Override
    public Page<MicroBlog> findSubscribeMicroBlog(User user, long beforeId, Pageable pageable) {
        return beforeId == 0 ? mbRepository.findSubscribeMicroBlog(user.getId(), pageable)
                : mbRepository.findSubscribeMicroBlog(user.getId(), beforeId, pageable);
    }
}
