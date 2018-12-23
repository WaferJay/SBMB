

var Alert = (function () {
    "use strict";

    var defaultConfig = {
        text: "",
        showOkButton: true,
        okButtonText: "OK",
        cancelButtonText: "Cancel",
        closeOnOk: true,
        closeOnCancel: true,
        closeOnComplete: true
    };

    var loaded = false,
        mask,
        alBox,
        alOkBtn,
        alCancelBtn,
        alTitle,
        alContent,
        handleClickFn,
        currentTimerId,
        oldLoadFn;

    function init(e) {
        var root = document.createElement("div");

        root.setAttribute("class", "alert-mask");
        root.setAttribute("id", "-alert-mask");
        root.style.display = "none";

        root.innerHTML += '\n' +
            '<div class="alert-box" id="-alert-box">\n' +
            '    <h2 id="-alert-title">你好</h2>\n' +
            '    <p id="-alert-content">世界</p>\n' +
            '\n' +
            '    <div class="alert-btn-group">\n' +
            '        <button class="alert-btn alert-btn-ok" id="-alert-ok-btn" style="display: none;">OK</button>\n' +
            '        <button class="alert-btn alert-btn-cancel" id="-alert-cancel-btn" style="display: none;">Cancel</button>\n' +
            '    </div>\n' +
            '</div>\n';

        document.body.appendChild(root);

        findAlertElements();

        loaded = true;
    }

    oldLoadFn = window.onload;
    window.onload = function (e) {
        try {
            typeof oldLoadFn === "function" && oldLoadFn(e);
        } catch (e) {
            console.error(e);
        }

        init(e);
    };

    function findAlertElements() {
        mask = document.getElementById("-alert-mask");
        alBox = document.getElementById("-alert-box");
        alOkBtn = document.getElementById("-alert-ok-btn");
        alCancelBtn = document.getElementById("-alert-cancel-btn");
        alTitle = document.getElementById("-alert-title");
        alContent = document.getElementById("-alert-content");
    }

    function mergeDefaultConfig(config) {
        var key;

        if (!config || typeof config !== "object") {
            config = {}
        }

        for (key in defaultConfig) {

            if (!(key in config) || config[key] === null || typeof config[key] === "undefined") {
                config[key] = defaultConfig[key];
            }
        }

        return config;
    }

    function hideElement(target) {
        target.style.display = "none";
    }

    function showElement(target) {
        target.style.display = "";
    }

    var alertApp = {

        close: function () {
            alBox.style.animation = "hideAlert 0.2s";

            setTimeout(function () {
                hideElement(mask);
                alBox.style.opacity = "0";
            }, 200);
        },

        show: function (options) {
            if (!loaded) {
                throw new Error("等待加载");
            }

            options = mergeDefaultConfig(options);

            if (typeof options.title !== "string") {
                throw new Error("缺少title参数");
            } else {
                alTitle.innerText = options.title;
            }

            if (typeof options.text === "string") {
                alContent.innerText = options.text;
            }

            if (options.showOkButton === true) {
                showElement(alOkBtn);
            } else {
                hideElement(alOkBtn);
            }

            if (options.showCancelButton === true) {
                showElement(alCancelBtn);
            } else {
                hideElement(alCancelBtn);
            }

            if (typeof options.okButtonText === "string") {
                alOkBtn.innerText = options.okButtonText;
            }

            if (typeof options.cancelButtonText === "string") {
                alCancelBtn.innerText = options.cancelButtonText;
            }

            removeEventListener(alOkBtn, "click", handleClickFn);
            removeEventListener(alCancelBtn, "click", handleClickFn);
            handleClickFn = null;

            handleClickFn = function (e) {

                if (e.target === alOkBtn) {

                    options.closeOnOk === true && alertApp.close();
                    typeof options.callback === "function" && options.callback(true);
                } else if (e.target === alCancelBtn) {

                    options.closeOnCancel === true && alertApp.close();
                    typeof options.callback === "function" && options.callback(false);
                } else {
                    throw new Error("不可达");
                }
            };

            addEventListener(alOkBtn, "click", handleClickFn);
            addEventListener(alCancelBtn, "click", handleClickFn);

            showElement(mask);
            alBox.style.opacity = "1";
            alBox.style.animation = "showAlert 0.2s";

            clearTimeout(currentTimerId);

            if (typeof options.timer === "number") {
                currentTimerId = setTimeout(function () {
                    options.closeOnComplete === true && alertApp.close();
                    typeof options.callback === "function" && options.callback();
                }, options.timer);
            }
        }
    };

    return alertApp;
})();


function al(title, text) {
    Alert.show({
        title: title,
        text: text,
        showOkButton: true
    });
}