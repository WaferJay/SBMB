
function addEventListener(target, event, fn, useCapture) {
    if (target.addEventListener) {
        target.addEventListener(event, fn, useCapture);
    } else if (target.attachEvent) {
        target.attachEvent("on"+event, fn);
    } else {
        target["on"+event] = fn;
    }
}

function removeEventListener(target, event, fn) {
    if (target.removeEventListener) {
        target.removeEventListener(event, fn);
    } else if (target.detachEvent) {
        target.detachEvent("on"+event, fn);
    } else {
        target["on"+event] = null;
    }
}

Function.prototype.method = function (name, fn, force) {
    if (!this.prototype[name] || force) {
        this.prototype[name] = fn;
    }
    return this;
};

String.method("endsWith", function (end) {
    var thisLen = this.length,
        subLen = end.length;

    if (subLen > thisLen) return false;

    while (subLen > 0) {

        if (this.charAt(--thisLen) !== end.charAt(--subLen)) {
            return false;
        }
    }

    return true;
}).method("startsWith", function (end) {
    var thisLen = this.length,
        subLen = end.length,
        i;

    if (subLen > thisLen) return false;

    for (i=0;i<subLen;i++) {

        if (this.charAt(i) !== end.charAt(i)) {
            return false;
        }
    }

    return true;
});

String.method("format", function (obj) {
    return this.replace(/{[\w ]+}/igm, function (matched, idx, str) {
        var key = matched.substring(1, matched.length - 1).trim();

        if (key in obj) {
            return obj[key];
        } else {
            return matched;
        }
    });
});

String.method("renderDOM", function (obj, wrapper) {
    var wrapDOM = document.createElement(wrapper || "div");

    wrapDOM.innerHTML = this.format(obj);

    return wrapper ? wrapDOM : wrapDOM.children;
});

