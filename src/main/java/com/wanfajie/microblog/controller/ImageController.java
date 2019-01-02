package com.wanfajie.microblog.controller;

import com.wanfajie.microblog.bean.MediaFile;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.config.bean.ImageUploadProperties;
import com.wanfajie.microblog.controller.ajax.result.AjaxExceptionResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.service.MediaFileService;
import com.wanfajie.microblog.service.UserService;
import com.wanfajie.microblog.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

@Controller
public class ImageController {

    @Resource
    private MediaFileService mediaService;

    @Resource
    private UserService userService;

    private File imageBasePathFile;
    private Set<String> allowSuffixSet;

    @Autowired
    private void parseConfig(ImageUploadProperties properties) {
        imageBasePathFile = properties.getFullPathFile();
        allowSuffixSet = properties.getAllowSuffix();
    }

    @PostMapping("/image/upload")
    @ResponseBody
    @LoginRequired
    public AjaxResult uploadImage(@RequestParam("image-file") MultipartFile imageFile) {

        String fileName = imageFile.getOriginalFilename();
        String[] parts = FileUtil.splitFileName(fileName);
        if (parts == null) {
            return new AjaxResult(1001, "无文件后缀");
        }

        String suffix = parts[1];
        if (!allowSuffixSet.contains(suffix)) {
            return new AjaxResult(1002, "不支持文件后缀: " + suffix);
        }

        String imagePath = FileUtil.uuidPath();

        Path path = imageBasePathFile.toPath()
                .resolve(imagePath + "." + suffix)
                .toAbsolutePath();

        File imagePathFile = path.toFile();

        FileUtil.mkdirs(imagePathFile);

        try {
            imageFile.transferTo(imagePathFile);
        } catch (IOException e) {
            return new AjaxExceptionResult(2, e);
        }

        User user = userService.getCurrentUser();
        MediaFile mediaFile = new MediaFile(user.getId(), imagePath, suffix);
        mediaService.save(mediaFile);

        return new AjaxSingleResult<>(0, "成功", mediaFile);
    }

    @GetMapping("/upload/images/{id}")
    public Object redirectImage(@PathVariable("id") long id) {
        MediaFile fileInfo = mediaService.findById(id);

        if (fileInfo == null) {
            return ResponseEntity.notFound()
                    .build();
        }

        String relPathStr = fileInfo.getPath() + "." + fileInfo.getSuffix();
        File filePath = imageBasePathFile.toPath()
                .resolve(relPathStr)
                .toAbsolutePath()
                .toFile();

        if (!filePath.isFile() || !filePath.canRead()) {
            return ResponseEntity.notFound()
                    .build();
        }

        return new ModelAndView("redirect:/upload/images/" + relPathStr);
    }
}
