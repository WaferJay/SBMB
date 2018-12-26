package com.wanfajie.microblog.controller.ajax;

import com.wanfajie.microblog.bean.Comment;
import com.wanfajie.microblog.bean.MicroBlog;
import com.wanfajie.microblog.bean.User;
import com.wanfajie.microblog.bean.form.CommentForm;
import com.wanfajie.microblog.controller.ajax.result.AjaxResult;
import com.wanfajie.microblog.controller.ajax.result.AjaxSingleResult;
import com.wanfajie.microblog.interceptor.login.annotation.LoginRequired;
import com.wanfajie.microblog.service.MicroBlogCommentService;
import com.wanfajie.microblog.service.MicroBlogService;
import com.wanfajie.microblog.service.UserService;
import com.wanfajie.microblog.util.PageUtil;
import com.wanfajie.microblog.util.ValidUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class CommentController {

private static final Sort SORT = Sort.by(Sort.Order.desc("id"));

    @Resource
    private UserService userService;

    @Resource
    private MicroBlogService mbService;

    @Resource
    private MicroBlogCommentService commentService;

    @GetMapping(AjaxURLConfig.Comment.COMMENT_SPECIFIC)
    public AjaxResult getComment(
            @PathVariable("mbId") long microBlogId,
            @PathVariable("id") long commentId) {

        Comment comment = commentService.findById(commentId);

        if (comment == null) {
            return new AjaxResult(404, "没有该条评论");
        }

        if (microBlogId != 0 && comment.getMicroBlogId() != microBlogId) {
            return new AjaxResult(404, "该微博没有该条评论");
        }

        return new AjaxSingleResult<>(0, "成功", comment);
    }

    @PutMapping(AjaxURLConfig.Comment.COMMENT_BASE)
    @LoginRequired
    public AjaxResult postComment(
            @PathVariable("mbId") long microBlogId,
            @RequestBody @Valid CommentForm form,
            BindingResult bindingResult) {

        MicroBlog microBlog = mbService.findById(microBlogId);

        if (microBlog == null) {
            return new AjaxResult(404, "没有指定微博: " + microBlogId);
        }

        if (bindingResult.hasErrors()) {
            List<Map<String, Object>> errors = ValidUtil.convertFieldErrors(bindingResult.getFieldErrors());
            return new AjaxSingleResult<>(1, "表单错误", errors);
        }

        User currentUser = userService.getCurrentUser();
        Comment comment = new Comment(microBlog, currentUser, form.getMessage());
        commentService.save(comment);

        return new AjaxSingleResult<>(0, "成功", comment);
    }

    @GetMapping(AjaxURLConfig.Comment.COMMENT_BASE)
    public AjaxResult getComment(
            @PathVariable("mbId") long microBlogId,
            @RequestParam(value = "page", defaultValue = "1") int pageNum,
            @RequestParam(value = "limit", defaultValue = "10") int size) {

        pageNum -= 1;

        if (!mbService.exists(microBlogId)) {
            return new AjaxResult(404, "微博不存在");
        }

        Pageable pageable = PageRequest.of(pageNum, size, SORT);

        Page<Comment> comments = commentService.findByMicroBlogId(microBlogId, pageable);

        return new AjaxSingleResult<>(0, "成功", PageUtil.page2Map(comments));
    }

    @DeleteMapping(AjaxURLConfig.Comment.COMMENT_SPECIFIC)
    @Transactional
    @LoginRequired
    public AjaxResult deleteComment(
            @PathVariable("mbId") long mbId,
            @PathVariable("id") long id) {
        Comment comment = commentService.findById(id);

        if (comment == null) {
            return new AjaxResult(404, "没有该条评论");
        }

        if (mbId != 0 && comment.getMicroBlogId() != mbId) {
            return new AjaxResult(404, "该微博没有该条评论");
        }

        User currentUser = userService.getCurrentUser();
        if (!comment.getUser().equals(currentUser) ||
                !comment.getMicroBlog().getAuthor().equals(currentUser)) {

            return new AjaxResult(403, "你没有权限删除该评论");
        }

        commentService.delete(comment);

        return new AjaxResult(0, "删除成功");
    }

    // TODO: 赞和取消赞
}
