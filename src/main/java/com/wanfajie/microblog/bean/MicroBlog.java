package com.wanfajie.microblog.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.springframework.context.annotation.Lazy;

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

    @JsonIgnore
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "author_id", updatable = false, insertable = false)
    private User author;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    // TODO: 赞和转载

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "mb_microblog_media_file",
            joinColumns = @JoinColumn(name = "micro_blog_id"),
            inverseJoinColumns = @JoinColumn(name = "media_file_id"))
    private List<MediaFile> mediaFiles;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "microblog_id")
    private List<Comment> comments;

    public MicroBlog() {
        timestamp = System.currentTimeMillis();
    }

    public MicroBlog(long authorId, User author, String content, List<MediaFile> mediaFiles) {
        this();
        this.authorId = authorId;
        this.author = author;
        this.content = content;
        this.mediaFiles = mediaFiles;
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
        this.authorId = author.getId();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<MediaFile> getMediaFiles() {
        return mediaFiles;
    }

    public void setMediaFiles(List<MediaFile> files) {
        mediaFiles = files;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        MicroBlog microBlog = (MicroBlog) object;

        return id == microBlog.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "MicroBlog{" +
                "id=" + id +
                ", authorId=" + authorId +
                ", author=" + author +
                ", content='" + content + '\'' +
                '}';
    }
}
