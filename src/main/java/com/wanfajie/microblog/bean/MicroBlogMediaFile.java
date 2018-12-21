package com.wanfajie.microblog.bean;

import javax.persistence.*;

@Entity
@Table(name = "mb_microblog_meida_file")
public final class MicroBlogMediaFile {

    @EmbeddedId
    private MicroBlogMediaFilePrimaryKey mbMfPid;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "micro_blog_id", referencedColumnName = "id", updatable = false, insertable = false)
    private MicroBlog microBlog;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "media_file_id", referencedColumnName = "id", updatable = false, insertable = false)
    private MediaFile mediaFile;

    public MicroBlogMediaFile(long mbId, long mfId) {
        this.mbMfPid = new MicroBlogMediaFilePrimaryKey(mbId, mfId);
    }

    public long getMicroBLogId() {
        return mbMfPid.getMicroBlogId();
    }

    public long getMediaFileId() {
        return mbMfPid.getMediaFileId();
    }

    public MicroBlog getMicroBlog() {
        return microBlog;
    }

    public MediaFile getMediaFile() {
        return mediaFile;
    }
}
