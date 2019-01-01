
var Form = (function () {

    "use strict";

    function getFormParameters(inputDomList) {
        var result = {},
            name,
            value,
            each,
            i;

        for (i=0;i<inputDomList.length;i++) {
            each = inputDomList[i];

            name = each.getAttribute("name");
            value = each.value;

            result[name] = value;
        }

        return result;
    }

    function verifyForm(data, formValidator, cb) {
        var validatorResult,
            validArr,
            field,
            fn,
            i;

        for (field in formValidator) {

            validArr = formValidator[field];

            if (typeof validArr === "function") {
                validArr = [validArr];
            }

            if (!Array.isArray(validArr)) {
                console.error(field + "验证器缺失:", validArr);
                continue;
            }

            for (i=0;i<validArr.length;i++) {
                fn = validArr[i];

                if (typeof fn !== "function") {
                    console.warn("invalid validator. Field: "+field);
                    console.warn(fn);
                    continue;
                }

                validatorResult = fn(data.fields[field], data.data[field], data);

                if (validatorResult !== true && typeof validatorResult !== "undefined") {

                    typeof formApp.onFieldError === "function" &&
                    formApp.onFieldError(data.fields[field], data.data[field], validatorResult);

                    return;
                }

            }
        }

        if (typeof cb === "function") {
            cb(data);
        }
    }

    var formApp = {

        validators: {
            required: function (message) {
                return function (dom, v) {
                    if (!v) return message;
                }
            },

            regexp: function (re, message) {
                return function (dom, v) {
                    if (!re.test(v)) return message;
                }
            },

            email: function (message) {
                return formApp.validators.regexp(/^\w+@\w+(\.\w+)?$/i, message);
            },

            length: function (options) {
                var min = options.min || 0,
                    max = options.max || Infinity,
                    message = options.message;

                return function (dom, v) {

                    if (v.length > max || v.length < min) {
                        return message;
                    }
                };
            }
        },

        parseForm: function (form) {
            var inputDoms = form.getElementsByTagName("input"),
                result = {},
                inputs = {},
                each,
                i;

            result.url = form.getAttribute("action");
            result.method = form.getAttribute("method");
            result.data = getFormParameters(inputDoms);

            for (i=0;i<inputDoms.length;i++) {
                each = inputDoms[i];
                inputs[each.getAttribute("name")] = each;
            }

            result.fields = inputs;

            return result;
        },

        onFieldError: function (dom, value, message) {
            alert(message);
        },

        handle: function (formElement, submitCallback) {

            var submitBtnList = [],
                formValidator = {},
                tags,
                i;

            // 查找提交按钮
            if (formElement.querySelector) {
                submitBtnList = formElement.querySelectorAll("input[type='submit'],button[type='submit']");
            } else {
                tags = formElement.getElementsByTagName("button");

                for (i=0;i<tags.length;i++) {
                    if (tags[i].getAttribute("type") === "submit") {
                        submitBtnList.push(tags[i]);
                    }
                }

                tags = formElement.getElementsByTagName("input");

                for (i=0;i<tags.length;i++) {
                    if (tags[i].getAttribute("type") === "submit") {
                        submitBtnList.push(tags[i]);
                    }
                }
            }

            for (i=0;i<submitBtnList.length;i++) {

                addEventListener(submitBtnList[i], "click", function (e) {
                    e.preventDefault();

                    var formData = formApp.parseForm(formElement);

                    verifyForm(formData, formValidator, submitCallback);
                });
            }

            addEventListener(formElement, "submit", function (event) {
                event.preventDefault();
            });

            for (i in formApp.parseForm(formElement).fields) {
                formValidator[i] = null;
            }

            Object.seal(formValidator);

            return formValidator;
        }
    };

    return formApp;
})();

define([], function () {
    return Form;
});