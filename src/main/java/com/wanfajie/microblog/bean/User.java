package com.wanfajie.microblog.bean;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "mb_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @JsonIgnore
    @Column(name = "pwd", nullable = false)
    @Length(min = 40, max = 40)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "register_ts", nullable = false)
    private long registerTimestamp;

    @JsonIgnore
    @OneToMany(mappedBy = "authorId", cascade = CascadeType.REMOVE)
    private List<MicroBlog> blogs;

    @JsonIgnore
    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL)
    private List<MediaFile> files;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(
            name = "mb_user_subscribe",
            joinColumns = @JoinColumn(name = "following_id"),
            inverseJoinColumns = @JoinColumn(name = "follower_id"))
    private List<User> followers;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(
            name = "mb_user_subscribe",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id"))
    private List<User> followings;

    public User() {
        registerTimestamp = System.currentTimeMillis();
    }

    public User(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        User user = (User) object;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public long getRegisterTimestamp() {
        return registerTimestamp;
    }

    public void setRegisterTimestamp(long registerTimestamp) {
        this.registerTimestamp = registerTimestamp;
    }

    public List<MediaFile> getFiles() {
        return files;
    }

    public List<MicroBlog> getBlogs() {
        return blogs;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    @JsonGetter(value = "blog_count")
    public int getBlogCount() {
        return blogs == null ? 0 : blogs.size();
    }

    public List<User> getFollowers() {
        return followers;
    }

    public List<User> getFollowings() {
        return followings;
    }

    @JsonGetter(value = "follower_count")
    public int getFollowerCount() {
        return  followers == null ? 0 : followers.size();
    }

    @JsonGetter(value = "following_count")
    public int getFollowingCount() {
        return followings == null ? 0 : followings.size();
    }
}
