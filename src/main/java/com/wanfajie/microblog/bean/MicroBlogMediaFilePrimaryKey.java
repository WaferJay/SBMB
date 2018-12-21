package com.wanfajie.microblog.bean;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public final class MicroBlogMediaFilePrimaryKey implements Serializable {

    @Column(name = "micro_blog_id")
    private long microBlogId;

    @Column(name = "media_file_id")
    private long mediaFileId;

    MicroBlogMediaFilePrimaryKey(long microBlogId, long mediaFileId) {
        this.microBlogId = microBlogId;
        this.mediaFileId = mediaFileId;
    }

    public long getMicroBlogId() {
        return microBlogId;
    }

    public void setMicroBlogId(long microBlogId) {
        this.microBlogId = microBlogId;
    }

    public long getMediaFileId() {
        return mediaFileId;
    }

    public void setMediaFileId(long mediaFileId) {
        this.mediaFileId = mediaFileId;
    }
}
