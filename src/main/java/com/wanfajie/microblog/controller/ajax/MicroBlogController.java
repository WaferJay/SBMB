package com.wanfajie.microblog.controller.ajax;

import com.wanfajie.microblog.bean.MediaFile;
import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.form.MicroBlogForm;
import com.wanfajie.microblog.bean.form.UpdateMicroBlogForm;
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
import javax.validation.Valid;
import java.util.*;

@RestController
public class MicroBlogController {

    public static final Sort MICROBLOG_DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "id");

    @Resource
    private MicroBlogService mbService;

    @Resource
    private UserService userService;

    @Resource
    private MediaFileService mediaService;

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_SPECIFIC)
    public AjaxResult getMicroBlog(@PathVariable("id") long blogId) {
        MicroBlog blog = mbService.findById(blogId);

        if (blog == null) {
            return new AjaxResult(404, "没有这条博客");
        }

        return new AjaxSingleResult<>(0, "成功", blog);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_BASE)
    public AjaxSingleResult<Map<String, Object>> getAllMicroBlog(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "id", defaultValue = "0") long id) {

        page -= 1;
        Pageable pageable = PageRequest.of(page, limit, MICROBLOG_DEFAULT_SORT);
        Page<MicroBlog> resultPage = mbService.findAll(pageable, id);

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(resultPage));
    }

    @DeleteMapping(AjaxURLConfig.MicroBlog.MICROBLOG_SPECIFIC)
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

    @PutMapping(AjaxURLConfig.MicroBlog.MICROBLOG_BASE)
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
        blog.setAuthor(currentUser);
        blog.setMediaFiles(imageList);

        blog = mbService.save(blog);

        return new AjaxSingleResult<>(0, "成功", blog);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_USER_FETCH)
    public AjaxResult getUserMicroBlog(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @PathVariable("userId") long userId) {
        pageNum -= 1;
        User user = userService.findById(userId);

        Pageable pageable = PageRequest.of(pageNum, limit, MICROBLOG_DEFAULT_SORT);

        if (user == null)
            return new AjaxResult(1, "用户不存在");

        Page<MicroBlog> page = mbService.findByUserId(userId, pageable);

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(page));
    }

    @PostMapping(AjaxURLConfig.MicroBlog.MICROBLOG_SPECIFIC)
    @LoginRequired
    public AjaxResult operaMicroBlog(@RequestBody @Valid UpdateMicroBlogForm form,
                                     @PathVariable("id") int microBlogId,
                                     BindingResult bindingResult) {

        MicroBlog microBlog = mbService.findById(microBlogId);
        if (microBlog == null) {
            return new AjaxResult(404, "没有该条微博: " + microBlogId);
        }

        if (bindingResult.hasErrors()) {
            List<Map<String, Object>> fieldErrors = ValidUtil.convertFieldErrors(bindingResult.getFieldErrors());
            return new AjaxSingleResult<>(1, "表单错误", fieldErrors);
        }

        boolean res;
        AjaxResult result = new AjaxResult();
        User currentUser = userService.getCurrentUser();
        switch (form.getLike()) {
            case 1:
                res = mbService.like(currentUser, microBlog);
                if (res) {
                    result.setCode(0);
                    result.setMessage("点赞成功");
                } else {
                    result.setCode(2);
                    result.setMessage("点赞失败");
                }
                break;
            case -1:
                res = mbService.unLike(currentUser, microBlog);
                if (res) {
                    result.setCode(0);
                    result.setMessage("取消成功");
                } else {
                    result.setCode(2);
                    result.setMessage("取消失败");
                }
                break;
            default:
                result.setCode(400);
                result.setMessage("什么也没做...");
        }

        return result;
    }

    @RequestMapping(value = AjaxURLConfig.MicroBlog.MICROBLOG_LIKE, method = {RequestMethod.POST, RequestMethod.GET})
    @LoginRequired
    public AjaxResult queryLikeStatus(@RequestBody Set<Long> microBlogIds) {
        if (microBlogIds == null) {
            return new AjaxResult(400, "缺少参数");
        }

        User currentUser = userService.getCurrentUser();

        Map<Long, Boolean> result = new HashMap<>();
        for (Long id : microBlogIds) {
            if (id != null) {
                MicroBlog mb = mbService.findById(id);

                if (mb != null) {
                    boolean flag = mbService.isUserLiked(currentUser, mb);
                    result.put(id, flag);
                }
            }
        }

        return new AjaxSingleResult<>(0, "成功", result);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.MICROBLOG_SUB_FETCH)
    @LoginRequired
    public AjaxSingleResult<Map<String, Object>> getSubMicroBlog(
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "mid", defaultValue = "0") long beforeMicroblogId) {
        pageNum--;

        User currentUser = userService.getCurrentUser();
        Page<MicroBlog> page = mbService.findSubscribeMicroBlog(currentUser, beforeMicroblogId,
                PageRequest.of(pageNum, limit, MICROBLOG_DEFAULT_SORT));

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(page));
    }
}
