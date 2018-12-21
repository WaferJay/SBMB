package com.wanfajie.microblog.service.impl;

import com.wanfajie.microblog.bean.MediaFile;
import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.MicroBlogMediaFile;
import com.wanfajie.microblog.repository.MediaFileRepository;
import com.wanfajie.microblog.repository.MicroBlogMediaRepository;
import com.wanfajie.microblog.service.MediaFileService;
import com.wanfajie.microblog.service.MicroBlogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Service
public class MediaFileServerImpl implements MediaFileService {

    @PersistenceContext
    private EntityManager manager;

    @Resource
    private MediaFileRepository mediaRepository;

    @Resource
    private MicroBlogService mbService;

    @Resource
    private MicroBlogMediaRepository mBmfRepository;

    @Override
    public boolean exists(long id) {
        return mediaRepository.existsById(id);
    }

    @Override
    public MediaFile findById(long id) {
        return mediaRepository.findById(id)
                .orElse(null);
    }

    @Override
    public List<MediaFile> findByMicroBlog(MicroBlog mb) {
        return mb.getMediaFiles();
    }

    @Override
    public List<MediaFile> findByMicroBlogId(long mbId) {
        MicroBlog mb = mbService.findById(mbId);
        return findByMicroBlog(mb);
    }

    @Override
    public MediaFile save(MediaFile mediaFile) {
        return mediaRepository.save(mediaFile);
    }

    @Override
    public MicroBlogMediaFile reference(MicroBlog mb, MediaFile file) {
        MicroBlogMediaFile entry = new MicroBlogMediaFile(mb.getId(), file.getId());
        entry = mBmfRepository.save(entry);
        return entry;
    }
}
