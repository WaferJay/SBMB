package com.example.demo.bean;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mb_microblog")
public class MicroBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "author_id", nullable = false)
    private long authorId;

    @ManyToOne(optional = false, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "author_id", updatable = false, insertable = false)
    private User author;

    @Column(name = "content", nullable = false)
    private String content;

    @OneToMany(mappedBy = "microBlogId")
    private List<MediaFile> mediaFiles;
}
