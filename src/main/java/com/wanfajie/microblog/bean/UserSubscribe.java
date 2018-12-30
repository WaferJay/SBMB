package com.wanfajie.microblog.bean;

import javax.persistence.*;

@Entity
@Table(name = "mb_user_subscribe")
@IdClass(UserSubPrimaryKey.class)
public class UserSubscribe {

    @Id
    @Column(name = "follower_id")
    private long followerId;

    @Id
    @Column(name = "following_id")
    private long followingId;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "follower_id", referencedColumnName = "id", updatable = false, insertable = false)
    private User follower;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "following_id", referencedColumnName = "id", updatable = false, insertable = false)
    private User following;

    private long timestamp;

    public UserSubscribe() {
        timestamp = System.currentTimeMillis();
    }

    public UserSubscribe(User follower, User following) {
        this();
        followerId = follower.getId();
        followingId = following.getId();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getFollowerId() {
        return followerId;
    }

    public long getFollowingId() {
        return followingId;
    }
}
