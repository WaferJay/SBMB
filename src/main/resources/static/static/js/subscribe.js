(function () {
    "use strict";

    var ajax,
        subApp,
        urlConfig = window.config.API_CONF.Subscribe;

    function handleClick(event) {
        var target = event.currentTarget,
            currentText,
            toggleText,
            toggleClass,
            userId;

        userId = target.dataset.userId;
        currentText = target.innerText;
        toggleText = target.dataset.toggleText;
        toggleClass = target.dataset.toggleClass;

        subApp.sub(userId, function () {
            target.innerText = toggleText;
            target.dataset.toggleText = currentText;
            target.classList.toggle(toggleClass);
        });
    }

    subApp = {
        sub: function (userId, cb, eb) {
            ajax({
                url: urlConfig.SUBSCRIBE.format({uid: userId}),
                method: "post",
                dataType: "json",
                type: "json",
                success: function (xhr, data) {
                    data.code === 0 ? al("关注成功") : al(data.message);
                    typeof cb === 'function' && cb(data);
                },
                error: function (xhr, data) {
                    if (data.code === 403 || data.status === 403) {
                        al("登录才能关注别人哟~");
                    } else {
                        al("出错了~", data.message);
                    }
                    typeof eb === 'function' && eb(data);
                }
            });
        },
        unsub: function (userId, cb, eb) {
            ajax({
                url: urlConfig.UNSUBSCRIBE.format({uid: userId}),
                method: "post",
                dataType: "json",
                type: 'json',
                success: function (xhr, data) {
                    al("取消关注成功");
                    typeof cb === 'function' && cb(data);
                },
                error: function (xhr, data) {
                    al("出错了~", data.message);
                    typeof eb === 'function' && eb(data);
                }
            });
        },
        handle: function (dom) {
            addEventListener(dom, "click", handleClick);
        }
    };

    define(["lib/ajax"], function (ajaxLib) {
        ajax = ajaxLib;
        return subApp;
    });
})();