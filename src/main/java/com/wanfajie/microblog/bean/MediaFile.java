package com.wanfajie.microblog.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "mb_media_file")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "file_path", nullable = false)
    private String path;

    @Column(name = "file_suffix", nullable = false)
    private String suffix;

    @Column(name = "upload_ts")
    private long uploadTimestamp;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    private MediaFile() {
        uploadTimestamp = System.currentTimeMillis();
    }

    public MediaFile(long userId, String path, String suffix) {
        this();
        this.userId = userId;
        this.path = path;
        this.suffix = suffix;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public long getUploadTimestamp() {
        return uploadTimestamp;
    }

    public void setUploadTimestamp(long uploadTimestamp) {
        this.uploadTimestamp = uploadTimestamp;
    }

    public User getUser() {
        return user;
    }
}
