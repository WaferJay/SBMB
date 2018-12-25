(function (config) {
    "use strict";

    var ajaxFn,
        mbAPIConf,
        microblogApp,
        blogTemplate,
        singleImageTemplate,
        simpleImageTemplate,
        uploadImgIds = [],
        $blogList,
        $editor;

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
            $imgArr,
            imageList,
            $each;

        console.log(microblog);

        // TODO: 渲染微博
        $blog = blogTemplate.renderDOM({
            username: microblog.author.name,
            content: microblog.content,
            time: new Date(microblog.timestamp).format("{Y}年{m}月{d}日 {H}:{M}")
        }, "div");

        imageList = $blog.querySelector(config.blogImageListSelector);

        if (imageList) {
            $imgArr = renderImageItems(microblog.mediaFiles);

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

        if ($blogList.children.length === 0) {
            $blogList.appendChild($blog);
        } else {
            $blogList.insertBefore($blog, $blogList.children[0]);
        }
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
            
            handleSubmitBlog: function () {
                var content = $editor.value;

                if (content) {
                    // TODO: 传递图片ID
                    microblogApp.sendMicroBlog(content, uploadImgIds);
                    uploadImgIds.splice(0, uploadImgIds.length);
                }
            }
        };
    } else {

        // TODO: 本地模拟
        microblogApp = {};
    }

    $editor = document.querySelector(config.textAreaSelector);
    $blogList = document.querySelector(config.blogListSelector);
    blogTemplate = document.querySelector(config.blogTemplateSelector).innerText;
    singleImageTemplate = document.querySelector(config.singleImageItemTemplateSelector).innerText;
    simpleImageTemplate = document.querySelector(config.simpleImageItemTemplateSelector).innerText;

    addEventListener(document.querySelector(config.submitSelector), 'click', function () {
        microblogApp.handleSubmitBlog();
    });

    addEventListener(document.querySelector(config.imageUploadSelector), 'click', function () {
        loadFile({
            type: /^image\/(jpeg|jpg|png)$/i,
            accept: ".jpeg,.jpg,.png",
            callback: function (file) {
                microblogApp.uploadImage(file);
            }
        });
    });

    if (window.define) {

        define(["static/js/ajax"], function (ajaxLib) {
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
    simpleImageItemTemplateSelector: "#template-simple-image-item"
});