package com.example.demo.bean;

import javax.persistence.*;

@Entity
@Table(name = "mb_media_file")
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "media_type", nullable = false)
    private int type;

    @Column(name = "file_path", nullable = false)
    private String path;

    @Column(name = "upload_ts")
    private long uploadTimestamp;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "microblog_id", nullable = false)
    private long microBlogId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "microblog_id", referencedColumnName = "id", insertable = false, updatable = false)
    private MicroBlog blog;
}
