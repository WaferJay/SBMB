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

    MicroBlogLikePrimaryKey(long microBlogId, long userId) {
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
}
