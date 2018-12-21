package com.wanfajie.microblog.repository;

import com.wanfajie.microblog.bean.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MediaFileRepository extends JpaRepository<MediaFile, Long>, JpaSpecificationExecutor<MediaFile> {
}
