package com.wanfajie.microblog.bean;

import javax.persistence.*;

@Entity
@Table(name = "mb_micro_blog_like_record")
public class MicroBlogLike {

    @EmbeddedId
    private MicroBlogLikePrimaryKey mbLikeId;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "micro_blog_id", referencedColumnName = "id", updatable = false, insertable = false)
    private MicroBlog microBlog;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false, insertable = false)
    private User user;

    @Column(name = "timestamp")
    private long timestamp;

    public MicroBlogLike() {
        timestamp = System.currentTimeMillis();
    }

    public MicroBlogLike(long microBlogId, long userId) {
        this();
        mbLikeId = new MicroBlogLikePrimaryKey(microBlogId, userId);
    }

    public MicroBlogLike(MicroBlog microBlog, User user) {
        this(microBlog.getId(), user.getId());
    }

    public long getMicroBlogId() {
        return mbLikeId.getMicroBlogId();
    }

    public long getUserId() {
        return mbLikeId.getUserId();
    }

    public User getUser() {
        return user;
    }

    public MicroBlog getMicroBlog() {
        return microBlog;
    }

    public MicroBlogLikePrimaryKey getPrimaryKey() {
        return mbLikeId;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
