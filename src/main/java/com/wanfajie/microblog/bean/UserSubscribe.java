package com.wanfajie.microblog.bean;

import javax.persistence.*;

@Entity
@Table(name = "mb_user_subscribe")
public class UserSubscribe {

    @EmbeddedId
    private UserSubPrimaryKey primaryKey;

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
        primaryKey = new UserSubPrimaryKey(follower, following);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public UserSubPrimaryKey getPrimaryKey() {
        return primaryKey;
    }
}
