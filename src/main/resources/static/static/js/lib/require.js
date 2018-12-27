(function () {

    "use strict";

    var moduleCache = {},
        defaultConfig = {};

    var require = function (deps, callback) {
        var params = [],
            depCount = 0,
            i,
            len,
            modName;

        //获取当前正在执行的js代码段，这个在onLoad事件之前执行
        modName = document.currentScript && document.currentScript.id || 'REQUIRE_MAIN';

        if (deps && deps.length) {
            for (i = 0, len = deps.length; i < len; i++) {
                (function (i) {
                    depCount++;
                    //这块回调很关键
                    loadMod(deps[i], function (param) {
                        params[i] = param;
                        depCount--;
                        if (depCount === 0) {
                            saveModule(modName, params, callback);
                        }
                    });
                })(i);
            }
        } else {
            setTimeout(function () {
                saveModule(modName, null, callback);
            }, 0);
        }
    };

    function getPathUrl(modName) {
        var url = modName,
            baseUrl = defaultConfig['baseUrl'];

        // XXX: 构造的URI可能不规范
        if (!url.endsWith('.js')) url = url + '.js';
        if (baseUrl) url = baseUrl + url;
        return url;
    }

    //模块加载
    function loadMod(modName, callback) {
        var url = getPathUrl(modName),
            fs,
            mod,
            _script;

        //如果该模块已经被加载
        if (moduleCache[modName]) {
            mod = moduleCache[modName];
            if (mod.status === 'loaded') {
                setTimeout(function () {
                    callback(mod.export);
                }, 0);
            } else {
                //如果未到加载状态直接往onLoad插入值，在依赖项加载好后会解除依赖
                mod.onload.push(callback);
            }
        } else {

            /*
            这里重点说一下Module对象
            status代表模块状态
            onLoad事实上对应requireJS的事件回调，该模块被引用多少次变化执行多少次回调，通知被依赖项解除依赖
            */
            moduleCache[modName] = {
                modName: modName,
                status: 'loading',
                export: null,
                onload: [callback]
            };

            _script = document.createElement('script');
            _script.id = modName;
            _script.type = 'text/javascript';
            _script.charset = 'utf-8';
            _script.async = true;
            _script.src = url;

            fs = document.getElementsByTagName('script')[0];
            fs.parentNode.insertBefore(_script, fs);

        }
    }

    function saveModule(modName, params, callback) {
        var mod, fn;

        if (moduleCache.hasOwnProperty(modName)) {
            mod = moduleCache[modName];
            mod.status = 'loaded';
            //输出项
            mod.export = callback ? callback.apply(window, params) : null;

            //解除父类依赖，这里事实上使用事件监听较好
            while (fn = mod.onload.shift()) {
                fn(mod.export);
            }
        } else {
            callback && callback.apply(window, params);
        }
    }

    require.config = function (config, overwrite) {
        var key,
            value;

        for (key in config) {

            if (config.hasOwnProperty(key)) {

                if (key in defaultConfig && !overwrite) continue;
                value = config[key];
                defaultConfig[key] = value;
            }
        }
    };

    window.require = require;
    window.define = require;

})();