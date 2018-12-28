
(function (config) {
    "use strict";

    var ajaxFn,
        template,
        commentAPIConf,
        commentApp;

    if (window.config) {
        commentAPIConf = window['config']['API_CONF']['Comment'];
    }

    function showError() {
        al("哎呦~", "出错了...");
    }

    function renderBlogComment(comment) {
        var dom,
            microBlogId = comment.microBlogId,
            $mb,
            $list;

        comment.time = new Date().format("{Y}年{mm}月{dd}日 {HH}:{SS}");
        dom = template.renderDOM(comment, "div");

        $mb = document.querySelector("[data-microblog-id='"+ microBlogId + "']");
        $list = $mb && $mb.querySelector(config.commentListSelector);

        if ($list.children.length) {
            $list.insertBefore(dom, $list.children[0]);
        } else {
            $list.appendChild(dom);
        }
    }

    commentApp = {
        sendComment: function (microBlogId, comment, cb) {
            ajaxFn({
                url: commentAPIConf.COMMENT_BASE.format({mid: microBlogId}),
                method: 'put',
                dataType: 'json',
                type: 'json',
                data: {message: comment},
                success: function (xhr, data) {
                    if (data.code === 0) {
                        renderBlogComment(data.data);
                        al("评论成功");
                        typeof cb === 'function' && cb(data.data);
                    } else {
                        showError();
                    }
                },
                error: function (xhr, data) {
                    showError();
                }
            });
        },
        deleteComment: function (commentId, microBlogId) {
            ajaxFn({
                url: commentAPIConf.COMMENT_BASE.format({mid: microBlogId}),
                method: 'put',
                dataType: 'json',
                type: 'json',
                data: {message: comment},
                success: function (xhr, data) {
                    if (data.code === 0) {
                        al("删除成功");
                    } else {
                        showError();
                    }
                },
                error: function (xhr, data) {
                    showError();
                }
            });
        }
    };

    template = document.querySelector(config.templateSelector).innerText;

    if (window.define) {
        define(['lib/ajax'], function (ajax) {
            ajaxFn = ajax;

            return commentApp;
        });
    } else {
        window.Comment = commentApp;
    }
})({
    templateSelector: "#template-comment-item",
    commentListSelector: ".comment-list"
});