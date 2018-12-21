package com.wanfajie.microblog.service;

import com.wanfajie.microblog.bean.MediaFile;
import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.MicroBlogMediaFile;

import java.util.List;

public interface MediaFileService {
    boolean exists(long id);
    MediaFile findById(long id);
    List<MediaFile> findByMicroBlog(MicroBlog mb);
    List<MediaFile> findByMicroBlogId(long mbId);
    MediaFile save(MediaFile mediaFile);
    MicroBlogMediaFile reference(MicroBlog mb, MediaFile file);
}
