(function () {
    "use strict";

    var httpCodeProcess,
        dataConverter;

    function ajax(options) {
        var url = options.url,
            success = options.success,
            complete = options.complete,
            error = options.error,
            dataType = options.dataType || "form",
            data = options.data || null,
            type = options.type || "raw",
            params = options.params || null,
            method = options.method || "GET",
            headers = options.headers || {},
            headerKey,
            headerValue,
            bodyData,
            contentType,
            xhr;

        if (!url) {
            throw new Error("缺少必要参数" + url);
        }

        dataType = dataType.toLowerCase();
        type = type.toLowerCase();

        if (params) {
            url += "?" + dataConverter.form.encode(params);
        }

        if (dataType in dataConverter) {
            bodyData = dataConverter[dataType].encode(data);
            contentType = dataConverter[dataType].mediaType;
        } else {
            bodyData = dataConverter.form.encode(data);
            contentType = dataConverter.form.mediaType;
        }

        if (window.XMLHttpRequest) {
            xhr = new XMLHttpRequest();
        } else {
            xhr = new ActiveXObject("Microsoft.XMLHTTP");
        }

        xhr.responseType = type;

        xhr.onreadystatechange = function () {
            var processer;

            if (xhr.readyState === 4) {

                if (xhr.status < 100 || xhr.status > 600) {
                    console.warn("未知HTTP Code: ", xhr.status, xhr);
                }

                if (xhr.status >= 100 && xhr.status < 300) {
                    // 成功

                    console.log(xhr.response);
                    typeof success === "function" && success(xhr, xhr.response);
                } else if (xhr.status < 400) {

                    // 一般是重定向, 浏览器会处理重定向?
                    if (xhr.status in httpCodeProcess) {
                        processer = httpCodeProcess[xhr.status];
                    } else {
                        processer = httpCodeProcess.defaultProcess;
                    }

                    processer(xhr, options);
                } else {

                    typeof error === "function" && error(xhr, xhr.response);
                }

                typeof complete === "function" && complete(xhr, xhr.response);
            }
        };

        xhr.open(method, url, true);
        contentType && xhr.setRequestHeader("Content-Type", contentType);
        for (headerKey in headers) {
            headerValue = headers[headerKey];
            xhr.setRequestHeader(headerKey, headerValue);
        }
        xhr.send(bodyData);
    }

    function clone(obj) {
        var newObj = {},
            key;

        for (key in obj) {

            if (obj.hasOwnProperty(key)) newObj[key] = obj[key];
        }

        return newObj
    }

    httpCodeProcess = {
        defaultProcess: function (xhr, options) {
            var url = xhr.getResponseHeader("Location");

            options = clone(options);
            if (url) {
                options.url = url;
                ajax(options);
            } else {

                console.warn("未处理: ", xhr);
            }
        }
    };

    dataConverter = {
        json: {
            encode: function (data) {
                return JSON.stringify(data);
            },
            decode: function (data) {
                return JSON.parse(data);
            },
            mediaType: "application/json"
        },
        form: {
            encode: function (data) {
                var parts = [],
                    value,
                    key;

                for (key in data) {

                    if (data.hasOwnProperty(key)) {
                        value = data[key];
                        parts.push(encodeURIComponent(key) + "=" + encodeURIComponent(value));
                    }
                }

                return parts.join("&");
            },
            decode: function () {
                throw new Error("不支持该格式解码");
            },
            mediaType: "application/x-www-form-urlencoded"
        },
        raw: {
            encode: function (data) {
                return data;
            },
            decode: function (data) {
                return data;
            }
        }
    };

    define([], function () {
        return ajax;
    });
})();

