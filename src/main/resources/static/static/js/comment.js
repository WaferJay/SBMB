
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

    function getCommentList(microBlogId) {
        var $mb = document.querySelector("[data-microblog-id='"+ microBlogId + "']");
        return $mb && $mb.querySelector(config.commentListSelector);
    }

    function renderBlogComment(comment) {
        var dom,
            microBlogId = comment.microBlogId,
            $list;

        comment.time = new Date().format("{Y}年{mm}月{dd}日 {HH}:{SS}");
        dom = template.renderDOM(comment)[0];

        $list = getCommentList(microBlogId);

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
        },
        fetchComments: function (microBlogId, page, lastId, cb) {
            ajaxFn({
                url: commentAPIConf.COMMENT_BASE.format({mid: microBlogId}),
                method: 'get',
                type: 'json',
                params: {page: page, id: lastId || 0},
                success: function (xhr, data) {
                    var items;

                    if (data.code === 0) {
                        items = data.data.items;
                        while (items.length)
                            renderBlogComment(items.pop());

                        typeof cb === 'function' && cb(data.data);
                    }
                },
                error: function () {
                    al("哎呀～", "出错了～");
                }
            });
        },
        
        clearCommentList: function (microBlogId) {
            var $list = getCommentList(microBlogId),
                $items,
                i;

            $items = $list.querySelectorAll(".comment-item");

            for (i=0;i<$items.length;i++) {
                $items[i].remove();
            }
        },

        queryCommentList: getCommentList
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