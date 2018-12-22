package com.wanfajie.microblog.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "mb_microblog_comment")
public class Comment {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "microblog_id", updatable = false)
    private long microBlogId;

    @Column(name = "user_id", updatable = false)
    private long userId;

    private String message;

    @Column(name = "like_count", nullable = false)
    private int likeCount = 0;

    private long timestamp;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinColumn(name = "microblog_id", referencedColumnName = "id", insertable = false, updatable = false)
    @JsonIgnore
    private MicroBlog microBlog;

    public Comment() {
        this.timestamp = System.currentTimeMillis();
    }

    public Comment(MicroBlog mb, User user, String message) {
        this(mb.getId(), user.getId(), message);
    }

    public Comment(long microBlogId, long userId, String message) {
        this();
        this.microBlogId = microBlogId;
        this.userId = userId;
        this.message = message;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }

    public MicroBlog getMicroBlog() {
        return microBlog;
    }

    public void setMicroBlog(MicroBlog microBlog) {
        this.microBlog = microBlog;
    }

    public int incrementLikeCount() {
        return ++likeCount;
    }

    public int decrementLikeCount() {
        return --likeCount;
    }

    public int getLikeCount() {
        return likeCount;
    }
}
