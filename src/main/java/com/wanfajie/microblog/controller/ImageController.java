package com.wanfajie.microblog.controller;

import com.wanfajie.microblog.bean.MediaFile;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.controller.ajax.result.AjaxExceptionResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.service.MediaFileService;
import com.wanfajie.microblog.service.UserService;
import com.wanfajie.microblog.util.FileUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ImageController {

    @Resource
    private MediaFileService mediaService;

    @Value("${spring.image.upload-path}")
    private String imageBasePathStr;

    @Resource
    private UserService userService;

    private File imageBasePathFile;

    private final Object IMAGE_BASE_PATH_LOCK = new Object();

    private File getImageBasePathFile() {

        if (imageBasePathFile == null) {

            synchronized (IMAGE_BASE_PATH_LOCK) {

                if (imageBasePathFile == null)
                    imageBasePathFile = new File(imageBasePathStr);
            }
        }

        return imageBasePathFile;
    }

    private static final Set<String> SUPPORTED_IMAGE_SUFFIX;
    static {
        Set<String> set = new HashSet<>();
        Collections.addAll(set, "jpeg", "jpg", "png", "gif");
        SUPPORTED_IMAGE_SUFFIX = Collections.unmodifiableSet(set);
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
        if (!SUPPORTED_IMAGE_SUFFIX.contains(suffix)) {
            return new AjaxResult(1002, "不支持文件后缀: " + suffix);
        }

        String imagePath = FileUtil.uuidPath();


        Path path = getImageBasePathFile()
                .toPath()
                .resolve(imagePath)
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

    @GetMapping("/upload/images/{uuid}.{suffix}")
    public ResponseEntity getImage(
            HttpServletResponse response,
            @PathVariable("uuid") String uuid,
            @PathVariable("suffix") String suffix) {

        if (!SUPPORTED_IMAGE_SUFFIX.contains(suffix)) {
            return ResponseEntity.notFound()
                    .build();
        }

        String pathStr = FileUtil.splitStringToPath(uuid);

        File filePath = getImageBasePathFile()
                .toPath()
                .resolve(pathStr)
                .toAbsolutePath()
                .toFile();

        if (!filePath.isFile() || !filePath.canRead()) {
            return ResponseEntity.notFound()
                    .build();
        }

        response.setStatus(200);
        response.setHeader("Content-Type", "image/" + suffix);
        try (InputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
            OutputStream os = response.getOutputStream();
            byte[] buf = new byte[1024];
            int count;

            while ((count = is.read(buf)) > 0) {
                os.write(buf, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @GetMapping("/upload/images/{id}")
    public ResponseEntity getImage(HttpServletResponse response, @PathVariable("id") long id) {
        MediaFile fileInfo = mediaService.findById(id);

        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }

        File filePath = getImageBasePathFile()
                .toPath()
                .resolve(fileInfo.getPath())
                .toAbsolutePath()
                .toFile();

        if (!filePath.isFile() || !filePath.canRead()) {
            return ResponseEntity.notFound()
                    .build();
        }

        response.setStatus(200);
        response.setHeader("Content-Type", "image/" + fileInfo.getSuffix());
        try (InputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
            OutputStream os = response.getOutputStream();
            byte[] buf = new byte[1024];
            int count;

            while ((count = is.read(buf)) > 0) {
                os.write(buf, 0, count);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}