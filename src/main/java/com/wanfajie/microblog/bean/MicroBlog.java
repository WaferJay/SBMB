package com.wanfajie.microblog.bean;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.springframework.context.annotation.Lazy;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "mb_microblog")
public class MicroBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "author_id", nullable = false)
    private long authorId;

    @ManyToOne(cascade = CascadeType.DETACH, fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", updatable = false, insertable = false)
    private User author;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    @Column(name = "like_count", nullable = false)
    private long likeCount;

    // TODO: 转载

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    @JoinTable(name = "mb_microblog_media_file",
            joinColumns = @JoinColumn(name = "micro_blog_id"),
            inverseJoinColumns = @JoinColumn(name = "media_file_id"))
    private List<MediaFile> mediaFiles;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "microblog_id")
    private List<Comment> comments;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "mb_micro_blog_like_record",
            joinColumns = @JoinColumn(name = "micro_blog_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likeUsers;

    public MicroBlog() {
        timestamp = System.currentTimeMillis();
        likeCount = 0;
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

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }

    public Set<User> getLikeUsers() {
        return likeUsers;
    }

    @JsonGetter
    public int getCommentCount() {
        return comments.size();
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
