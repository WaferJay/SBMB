package com.wanfajie.microblog.bean;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserSubPrimaryKey implements Serializable {
    @Column(name = "follower_id")
    private long followerId;

    @Column(name = "following_id")
    private long followingId;

    public UserSubPrimaryKey(User follower, User following) {
        this(follower.getId(), following.getId());
    }

    public UserSubPrimaryKey(long followerId, long followingId) {
        this.followerId = followerId;
        this.followingId = followingId;
    }
}
