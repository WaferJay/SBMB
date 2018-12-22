package com.wanfajie.microblog.controller.ajax;

import com.wanfajie.microblog.bean.MediaFile;
import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.form.MicroBlogForm;
import com.wanfajie.microblog.controller.ajax.result.AjaxResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.controller.validator.MicroBlogFormValidator;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.service.MediaFileService;
import com.wanfajie.microblog.service.MicroBlogService;
import com.wanfajie.microblog.service.UserService;
import com.wanfajie.microblog.util.PageUtil;
import com.wanfajie.microblog.util.ValidUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class MicroBlogController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "id");

    @Resource
    private MicroBlogService mbService;

    @Resource
    private UserService userService;

    @Resource
    private MediaFileService mediaService;

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG)
    public AjaxResult getMicroBlog(@PathVariable("id") long blogId) {
        MicroBlog blog = mbService.findById(blogId);

        if (blog == null) {
            return new AjaxResult(404, "没有这条博客");
        }

        return new AjaxSingleResult<>(0, "成功", blog);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_FETCH)
    public AjaxResult getAllMicroBlog(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "id", defaultValue = "0") long id) {

        page -= 1;
        Pageable pageable = PageRequest.of(page, limit, DEFAULT_SORT);
        Page<MicroBlog> resultPage = mbService.findAll(pageable, id);

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(resultPage));
    }

    @DeleteMapping(AjaxURLConfig.MicroBlog.MICROBLOG)
    @Transactional
    @LoginRequired
    public AjaxResult deleteMicroBlog(@PathVariable long id) {
        MicroBlog blog = mbService.findById(id);

        if (blog != null) {
            User currentUser = userService.getCurrentUser();

            if (blog.getAuthor().equals(currentUser)) {
                mbService.delete(blog);
                return new AjaxSingleResult<>(0, "删除成功", blog);
            } else {
                return new AjaxResult(403, "没有权限删除其他人的微博");
            }
        } else {
            return new AjaxResult(404, "该微博不存在");
        }
    }

    @PutMapping(AjaxURLConfig.MicroBlog.MICROBLOG_CREATE)
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    @LoginRequired
    public AjaxResult postMicroBlog(@RequestBody MicroBlogForm form, BindingResult bindingResult) {
        User currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            throw new IllegalStateException();
        }

        MicroBlogFormValidator.getInstance()
                .validate(form, bindingResult);

        if (bindingResult.hasErrors()) {
            List<Map<String, Object>> errors = ValidUtil.convertFieldErrors(bindingResult.getFieldErrors());

            return new AjaxSingleResult<>(1, "表单错误", errors);
        }

        List<MediaFile> imageList = new ArrayList<>();

        if (form.getPicIds() != null) {

            for (long id : form.getPicIds()) {
                MediaFile file = mediaService.findById(id);

                if (file == null) {
                    return new AjaxResult(2, "没有该图片资源: " + id);
                }

                imageList.add(file);
            }
        }

        String content = form.getContent();

        MicroBlog blog = new MicroBlog();
        blog.setContent(content);
        blog.setAuthorId(currentUser.getId());
        blog.setMediaFiles(imageList);

        mbService.save(blog);

        return new AjaxSingleResult<>(0, "成功", blog);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_USER_FETCH)
    public AjaxResult getUserMicroBlog(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @PathVariable("userId") long userId) {
        pageNum -= 1;
        User user = userService.findById(userId);

        Pageable pageable = PageRequest.of(pageNum, limit, DEFAULT_SORT);

        if (user == null)
            return new AjaxResult(1, "用户不存在");

        Page<MicroBlog> page = mbService.findByUserId(userId, pageable);

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(page));
    }

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_SUB_FETCH)
    public AjaxResult getSubMicroBlog() {
        // TODO:
        return null;
    }
}
