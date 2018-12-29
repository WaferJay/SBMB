(function (config) {
    "use strict";

    var ajax,
        mbAPIConf,
        microblogApp,
        blogTemplate,
        singleImageTemplate,
        simpleImageTemplate,
        uploadImgIds = [],
        renderProcesser = [],
        dragHandler,
        dragImageTemplate,
        uploadedImageList,
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
    
    function renderImageItems(imageList, template) {
        var $images = [],
            imageUrl,
            i;

        for (i=0;i<imageList.length;i++) {

            imageUrl = getImageUrl(imageList[i]);
            $images.push(template.renderDOM({imageUrl: imageUrl, id: imageList[i].id})[0])
        }

        return $images;
    }

    function renderMicroBlog(microblog) {
        var $blog,
            i;

        microblog.time = new Date(microblog.timestamp).format("{Y}年{mm}月{dd}日 {HH}:{MM}");
        console.log(microblog);
        $blog = blogTemplate.renderDOM(microblog, "div");

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
            $imgArr = renderImageItems(microBlog.mediaFiles,
                microBlog.mediaFiles.length === 1 ?
                    singleImageTemplate : simpleImageTemplate);

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

            sendMicroBlog: function (content) {
                ajax({
                    url: mbAPIConf.MICROBLOG_BASE,
                    method: 'put',
                    data: {content: content, picIds: uploadImgIds},
                    dataType: 'json',
                    type: 'json',
                    success: function (xhr, data) {

                        if (data.code === 0) {

                            renderMicroBlog(data.data);
                            $editor.value = "";
                            microblogApp.clearUploadedImages();
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
                ajax({
                    url: uploadUri,
                    method: 'post',
                    data: formData,
                    type: 'json',
                    dataType: 'raw',
                    success: function (xhr, result) {
                        var $image;
                        console.log(result);

                        if (result.code === 0) {
                            $image = renderImageItems([result.data], dragImageTemplate)[0];
                            uploadedImageList.appendChild($image);
                            dragHandler.handleNew($image);
                            uploadImgIds.push(result.data.id);
                            al("上传成功");
                        }
                    }
                });

            },

            removeImageId: function (id) {
                var $img = uploadedImageList.querySelector("[data-img-id='"+ id +"']");

                if ($img) {
                    $img.remove();
                    uploadImgIds.remove(id);
                }
            },

            clearUploadedImages: function () {
                uploadImgIds.splice(0, uploadImgIds.length);

                while (uploadedImageList.children.length) {
                    uploadedImageList.children[0].remove();
                }
            },

            fetchMicroBlog: function (page, limit, id) {

                ajax({
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
                ajax({
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
                ajax({
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
                    microblogApp.sendMicroBlog(content);
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
                ajax({
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

                ajax({
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
                                dom = document.querySelector("[data-id='"+key+"'][data-like-count] .icon.like");

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
    dragImageTemplate = document.querySelector(config.uploadedImageItemTemplateSelector);
    dragImageTemplate = dragImageTemplate ? dragImageTemplate.innerText : null;
    uploadedImageList = document.querySelector(config.imageDragSelector);

    $submitBtn = document.querySelector(config.submitSelector);
    $submitBtn && addEventListener($submitBtn, 'click', function () {
        microblogApp.handleSubmitBlog();
        $submitBtn.classList.add("disable");
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

    $editor && $submitBtn &&
    addEventListener($editor, "input,propertychange,change", function () {

        $editor.value ?
            $submitBtn.classList.remove("disable") :
            $submitBtn.classList.add("disable");
    });

    microblogApp.appendMicroBlogRender(renderImageList);
    microblogApp.appendMicroBlogRender(bindLikeButton);

    define(["lib/ajax", 'lib/drag'], function (ajaxLib, dragLib) {
        ajax = ajaxLib;

        if ($submitBtn) {

            dragHandler = dragLib(config.imageDragSelector, "imgId", function (order) {
                uploadImgIds = order;
            });
        }
        return microblogApp;
    });
})({
    submitSelector: "#submit-blog-btn",
    textAreaSelector: "#blog-editor",
    blogListSelector: ".blog-frags",
    blogTemplateSelector: "#template-blog",
    blogImageListSelector: ".media-image-list",
    imageUploadSelector: "#upload-image-btn",
    singleImageItemTemplateSelector: "#template-single-image-item",
    simpleImageItemTemplateSelector: "#template-simple-image-item",
    uploadedImageItemTemplateSelector: "#template-uploaded-image-item",
    likeButtonSelector: ".blog-control-like",
    imageDragSelector: "#upload-images-drag > .media-image-list"
});