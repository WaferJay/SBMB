package com.example.demo.controller.ajax;

import com.example.demo.bean.MicroBlog;
import com.example.demo.bean.User;
import com.example.demo.bean.form.MicroBlogForm;
import com.example.demo.controller.ajax.result.AjaxResult;
import com.example.demo.controller.ajax.result.AjaxSingleResult;
import com.example.demo.controller.validator.MicroBlogFormValidator;
import com.example.demo.iinterceptor.login.annotation.LoginRequired;
import com.example.demo.service.MicroBlogService;
import com.example.demo.service.UserService;
import com.example.demo.util.PageUtil;
import com.example.demo.util.ValidUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
public class MicroBlogController {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "id");

    @Resource
    private MicroBlogService mbService;

    @Resource
    private UserService userService;

    @GetMapping(AjaxURLConfig.MicroBlog.FETCH_ONE_MICROBLOG)
    public AjaxResult getMicroBlog(@PathVariable("id") long blogId) {
        MicroBlog blog = mbService.findById(blogId);

        if (blog == null) {
            return new AjaxResult(404, "没有这条博客");
        }

        return new AjaxSingleResult<>(0, "成功", blog);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.FETCH_ALL_MICROBLOG)
    public AjaxResult getAllMicroBlog(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "id", defaultValue = "0") long id) {

        page -= 1;
        Pageable pageable = PageRequest.of(page, limit, DEFAULT_SORT);
        Page<MicroBlog> resultPage = mbService.findAll(pageable, id);

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(resultPage));
    }

    @PutMapping(AjaxURLConfig.MicroBlog.POST_MICROBLOG)
    @ResponseStatus(HttpStatus.CREATED)
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

        String content = form.getContent();

        MicroBlog blog = new MicroBlog();
        blog.setContent(content);
        blog.setAuthorId(currentUser.getId());

        mbService.save(blog);

        return new AjaxSingleResult<>(0, "成功", blog);
    }

    @GetMapping(AjaxURLConfig.MicroBlog.FETCH_USER_MICROBLOG)
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

    @GetMapping(AjaxURLConfig.MicroBlog.FETCH_SUB_MICROBLOG)
    public AjaxResult getSubMicroBlog() {
        // TODO:
        return null;
    }
}
