(function (config) {
    "use strict";

    var domList = [],
        scrollApp;

    function scrollTop() {
        return document.documentElement.scrollTop ||
            window.pageYOffset ||
            document.body.scrollTop;
    }

    function scrollLeft() {
        return document.documentElement.scrollLeft ||
            window.pageXOffset ||
            document.body.scrollLeft
    }

    function clientHeight() {
        return window.innerHeight ||
            document.documentElement.clientHeight ||
            document.body.clientHeight;
    }

    function getEnterElements() {
        var top = scrollTop(),
            height = clientHeight(),
            pos = top + height,
            enterList = [],
            dom,
            domTop,
            i;

        for (i=0;i<domList.length;i++) {
            dom = domList[i];
            domTop = dom.element.offsetTop;

            if (pos > domTop)
                enterList.push(dom);
        }

        return {
            enters: enterList,
            top: top,
            height: height,
            pos: pos
        };
    }

    function handleAnchor(event) {
        var target = event.currentTarget,
            anchorName,
            id,
            duration = 0,
            startScroll,
            $anchor;

        event.preventDefault();
        anchorName = target.dataset.href || target.getAttribute("href");

        if (anchorName) {

            if (anchorName.charAt(0) === '#') {
                $anchor = document.querySelector("[name='" + anchorName.substring(1) + "']");
            }

            if (!$anchor) {
                $anchor = document.querySelector(anchorName);
            }

            if (!$anchor) {
                console.warn("没有找到锚点:", anchorName);
                return;
            }

            startScroll = scrollTop();
            id = setInterval(function () {
                var curr;
                duration += config.tick;
                curr = config.animation(duration, startScroll, $anchor.offsetTop, config.duration);

                window.scrollTo(scrollLeft(), curr);
                if (duration >= config.duration) {
                    clearInterval(id);
                }
            }, config.tick);
        }
    }

    addEventListener(document, "scroll", function () {
        var result = getEnterElements(),
            enters = result.enters,
            element,
            callback,
            isCallOnce,
            i;

        for (i=0;i<enters.length;i++) {
            element = enters[i].element;
            callback = enters[i].callback;
            isCallOnce = enters[i].callOnce;

            if (isCallOnce) {

                !enters[i].called && typeof callback === 'function' &&
                callback(element, result.top, result.height);
            } else {
                typeof callback === 'function' &&
                callback(element, result.top, result.height);
            }

            enters[i].called = true;
        }

        for (i=0;i<domList.length;i++) {
            if (result.enters.indexOf(domList[i]) === -1) {
                domList[i].called = false;
            }
        }
    });

    scrollApp = {
        handleScroll: function (dom, cb, once) {
            domList.push({
                element: dom,
                callback: cb,
                callOnce: once || false
            });
        },

        handleAnchor: function (dom) {
            dom && addEventListener(dom, "click", handleAnchor);
        },

        getEnterElements: getEnterElements,

        top: scrollTop
    };

    window.ScrollApp = scrollApp;

    define([], function () {
        return scrollApp;
    });
})({
    tick: 20,
    duration: 500,
    animation: animationsFunction.easeIn
});
