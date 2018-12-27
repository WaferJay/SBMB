(function (config) {
    "use strict";

    var ajaxFn,
        mbAPIConf,
        microblogApp,
        blogTemplate,
        singleImageTemplate,
        simpleImageTemplate,
        uploadImgIds = [],
        renderProcesser = [],
        $blogList,
        $editor,
        $submitBtn,
        $uploadImageBtn;

    function getImageUrl(image) {
        var baseImgUrl;

        if ('config' in window) {
            baseImgUrl = window['config']['API_CONF']['Image'].IMAGE_SPECIFIC_ID;
            return baseImgUrl.format({id: image.id});
        } else {
            // TODO:
        }
    }
    
    function renderImageItems(imageList) {
        var $images = [],
            imageUrl,
            i;

        if (imageList.length === 1) {
            imageUrl = getImageUrl(imageList[0]);
            $images.push(singleImageTemplate.renderDOM({imageUrl: imageUrl})[0]);
        } else {

            for (i=0;i<imageList.length;i++) {

                imageUrl = getImageUrl(imageList[i]);
                $images.push(simpleImageTemplate.renderDOM({imageUrl: imageUrl})[0])
            }
        }

        return $images;
    }

    function renderMicroBlog(microblog) {
        var $blog,
            i;

        console.log(microblog);

        $blog = blogTemplate.renderDOM({
            microBlogId: microblog.id,
            username: microblog.author.name,
            authorId: microblog.author.id,
            content: microblog.content,
            like_count: microblog.likeCount,
            time: new Date(microblog.timestamp).format("{Y}年{mm}月{dd}日 {HH}:{MM}")
        }, "div");

        for (i=0;i<renderProcesser.length;i++) {
            try {
                renderProcesser[i](microblog, $blog);
            } catch (e) {
                console.error("渲染时出错: ", e);
            }
        }

        if ($blogList.children.length === 0) {
            $blogList.appendChild($blog);
        } else {
            $blogList.insertBefore($blog, $blogList.children[0]);
        }
    }
    
    function renderImageList(microBlog, $blog) {
        var imageList,
            $imgArr,
            $each;

        imageList = $blog.querySelector(config.blogImageListSelector);

        if (imageList) {
            $imgArr = renderImageItems(microBlog.mediaFiles);

            while ($imgArr.length !== 0) {
                $each = $imgArr.pop();

                if (imageList.children.length === 0) {
                    imageList.appendChild($each);
                } else {
                    imageList.insertBefore($each, imageList.children[0]);
                }
            }
        } else {
            console.warn("没有找到图片列表DOM");
        }
    }

    function bindLikeButton(microBlog, $blog) {
        var likeBtn = $blog.querySelector(config.likeButtonSelector);

        addEventListener(likeBtn, 'click', function (event) {
            microblogApp.handleClickLikeBtn(event);
        });
    }

    function params(data) {

        var parts = [],
            value,
            key;

        for (key in data) {

            if (data.hasOwnProperty(key)) {
                value = data[key];
                parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
            }
        }

        return parts.join('&');
    }

    if (window['config']) {

        mbAPIConf = window['config']['API_CONF']['MicroBlog'];

        microblogApp = {

            sendMicroBlog: function (content, pids) {
                ajaxFn({
                    url: mbAPIConf.MICROBLOG_BASE,
                    method: 'put',
                    data: {content: content, picIds: pids || []},
                    dataType: 'json',
                    type: 'json',
                    success: function (xhr, data) {

                        if (data.code === 0) {

                            renderMicroBlog(data.data);
                            $editor.value = "";
                            al("发布成功");
                        }
                    },
                    error: function (xhr, data) {

                        console.log(data);
                        if (data.code === 403 || data.status === 403) {
                            al("登录后才能发微博哟～");
                        }
                    }
                });
            },

            uploadImage: function (file) {
                var formData = new FormData(),
                    uploadUri = window['config']['API_CONF']['Image'].IMAGE_UPLOAD;

                formData.append("image-file", file);
                ajaxFn({
                    url: uploadUri,
                    method: 'post',
                    data: formData,
                    type: 'json',
                    dataType: 'raw',
                    success: function (xhr, result) {
                        console.log(result);

                        if (result.code === 0) {
                            uploadImgIds.push(result.data.id);
                            al("上传成功");
                        }
                    }
                });

            },

            removeImageId: function (id) {
                uploadImgIds.remove(id);
            },

            fetchMicroBlog: function (page, limit, id) {

                ajaxFn({
                    url: mbAPIConf.MICROBLOG_BASE + "?" + params({page: page,
                        limit: limit,
                        id: id || 0}),

                    method: 'get',
                    type: 'json',
                    success: function (xhr, data) {
                        var items = data.data.items,
                            i;

                        for (i=0;i<items.length;i++) {

                            renderMicroBlog(items[i]);
                        }
                    }
                });
            },
            
            like: function (microBlogId, cb) {
                ajaxFn({
                    url: mbAPIConf.MICROBLOG_SPECIFIC.format({id: microBlogId}),
                    method: 'post',
                    data: {like: 1},
                    dataType: 'json',
                    type: 'json',
                    success: function (xhr, data) {
                        if (data.code === 0 || data.code === 2) {
                            typeof cb === 'function' && cb(data.code);
                        } else {
                            console.log(data.message, "MicroBlogId: " + microBlogId);
                        }
                    },
                    error: function (xhr, data) {
                        if (data.code === 403 || data.status === 403) {
                            console.log(data.message);
                            al("登录后才能点赞哟~");
                        }
                    }
                });
            },

            unlike: function (microBlogId, cb) {
                ajaxFn({
                    url: mbAPIConf.MICROBLOG_SPECIFIC.format({id: microBlogId}),
                    method: 'post',
                    data: {like: -1},
                    dataType: 'json',
                    type: 'json',
                    success: function (xhr, data) {
                        if (data.code === 0 || data.code === 2) {
                            typeof cb === 'function' && cb(data.code);
                        } else {
                            console.log(data.message, "MicroBlogId: " + microBlogId);
                        }
                    },
                    error: function (xhr, data) {
                        if (data.code === 403 || data.status === 403) {
                            console.log(data.message);
                            al("登录后才能点赞哟~");
                        }
                    }
                });
            },

            renderMicroBlog: renderMicroBlog,

            appendMicroBlogRender: function (render) {
                if (typeof render !== 'function') {
                    return false;
                }

                if (renderProcesser.indexOf(render) > -1) {
                    return false;
                }

                renderProcesser.push(render);
                return true;
            },

            handleSubmitBlog: function () {
                var content = $editor.value;

                if (content) {
                    microblogApp.sendMicroBlog(content, uploadImgIds);
                    uploadImgIds.splice(0, uploadImgIds.length);
                }
            },

            handleClickLikeBtn: function (event) {
                var target = event.currentTarget,
                    value = target.querySelector(".value"),
                    icon,
                    id;

                id = target.dataset.id;
                icon = target.querySelector(".icon");

                if (icon.classList.contains("active")) {
                    microblogApp.unlike(id, function (code) {

                        icon.classList.remove("active");
                        if (code === 0) value.innerText = --target.dataset.likeCount;
                    });
                } else {
                    microblogApp.like(id, function (code) {

                        icon.classList.add("active");
                        if (code === 0) value.innerText = ++target.dataset.likeCount;
                    });
                }
            },

            removeMicroBlog: function (microBlogId, cb) {
                ajaxFn({
                    url: mbAPIConf.MICROBLOG_SPECIFIC.format({id: microBlogId}),
                    method: "delete",
                    type: 'json',
                    success: function (xhr, data) {
                        if (data.code === 0) {
                            al(data.message);
                            typeof cb === 'function' && cb();
                        } else {
                            al("出错了～", data.message);
                        }
                    },
                    error: function (xhr, data) {
                        console.log(data.message);
                        al("哎呦～", "出错了~");
                    }
                });
            },

            queryLikeStatus: function (ids, cb) {

                if (ids.length === 0) {
                    return;
                }

                ajaxFn({
                    url: mbAPIConf.MICROBLOG_LIKE,
                    method: 'post',
                    data: ids,
                    dataType: 'json',
                    type: 'json',
                    success: function (xhr, data) {
                        var key,
                            dom;

                        if (data.code === 0) {
                            typeof cb === 'function' && cb(data.data);

                            for (key in data.data) {
                                dom = document.querySelector("[data-id='"+key+"'] .icon.like");

                                if (data.data[key]) {
                                    dom.classList.add("active");
                                } else {
                                    dom.classList.remove("active");
                                }
                            }
                        }
                    }
                });
            }
        };
    } else {

        // TODO: 本地模拟
        microblogApp = {
            renderMicroBlog: renderMicroBlog
        };
    }

    $editor = document.querySelector(config.textAreaSelector);
    $blogList = document.querySelector(config.blogListSelector);
    blogTemplate = document.querySelector(config.blogTemplateSelector).innerText;
    singleImageTemplate = document.querySelector(config.singleImageItemTemplateSelector).innerText;
    simpleImageTemplate = document.querySelector(config.simpleImageItemTemplateSelector).innerText;

    $submitBtn = document.querySelector(config.submitSelector);
    $submitBtn && addEventListener($submitBtn, 'click', function () {
        microblogApp.handleSubmitBlog();
    });

    $uploadImageBtn = document.querySelector(config.imageUploadSelector);
    $uploadImageBtn && addEventListener($uploadImageBtn, 'click', function () {
        loadFile({
            type: /^image\/(jpeg|jpg|png)$/i,
            accept: ".jpeg,.jpg,.png",
            callback: function (file) {
                microblogApp.uploadImage(file);
            }
        });
    });

    microblogApp.appendMicroBlogRender(renderImageList);
    microblogApp.appendMicroBlogRender(bindLikeButton);

    if (window.define) {

        // XXX: 使require支持设置前缀, 以避免如下情况...
        define(["lib/ajax"], function (ajaxLib) {
            ajaxFn = ajaxLib;
            return microblogApp;
        });
    } else if ('ajax' in window) {

        ajaxFn = ajax;
        window.microblogApp = microblogApp;
    } else {
        console.error("缺少必要的库: AJAX");
    }
})({
    submitSelector: "#submit-blog-btn",
    textAreaSelector: "#blog-editor",
    blogListSelector: ".blog-frags",
    blogTemplateSelector: "#template-blog",
    blogImageListSelector: ".media-image-list",
    imageUploadSelector: "#upload-image-btn",
    singleImageItemTemplateSelector: "#template-single-image-item",
    simpleImageItemTemplateSelector: "#template-simple-image-item",
    likeButtonSelector: ".blog-control-like"
});