package com.wanfajie.microblog.bean;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MicroBlogLikePrimaryKey implements Serializable {

    @Column(name = "micro_blog_id")
    private long microBlogId;

    @Column(name = "user_id")
    private long userId;

    public MicroBlogLikePrimaryKey(MicroBlog microBlog, User user) {
        this(microBlog.getId(), user.getId());
    }

    public MicroBlogLikePrimaryKey(long microBlogId, long userId) {
        this.microBlogId = microBlogId;
        this.userId = userId;
    }

    public long getMicroBlogId() {
        return microBlogId;
    }

    public void setMicroBlogId(long microBlogId) {
        this.microBlogId = microBlogId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        MicroBlogLikePrimaryKey that = (MicroBlogLikePrimaryKey) object;

        return microBlogId == that.microBlogId && userId == that.userId;
    }

    @Override
    public int hashCode() {
        int result = (int) (microBlogId ^ (microBlogId >>> 32));
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}
