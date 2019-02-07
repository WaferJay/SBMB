
function addEventListener(target, event, fn, useCapture) {
    var parts = event.split(","),
        each,
        i,
        j;

    if (!target) return;
    if (!(target instanceof NodeList)) {
        target = [target];
    }

    for (i=0;i<parts.length;i++) {
        each = parts[i].trim();

        for (j=0;j<target.length;j++) {
            if (target[j].addEventListener) {
                target[j].addEventListener(each, fn, useCapture);
            } else if (target[j].attachEvent) {
                target[j].attachEvent("on"+each, fn);
            } else {
                target[j]["on"+each] = fn;
            }
        }
    }
}

function removeEventListener(target, event, fn) {
    var parts = event.split(","),
        each,
        i,
        j;

    if (!target) return;
    if (!(target instanceof NodeList)) {
        target = [target];
    }

    for (i=0;i<parts.length;i++) {
        each = parts[i].trim();

        for (j=0;j<target.length;j++) {
            if (target[j].removeEventListener) {
                target[j].removeEventListener(each, fn);
            } else if (target[j].detachEvent) {
                target[j].detachEvent("on" + each, fn);
            } else {
                target[j]["on" + each] = null;
            }
        }
    }
}

Function.prototype.method = function (name, fn, force) {
    if (!this.prototype[name] || force) {
        this.prototype[name] = fn;
    }
    return this;
};

Date.method("toDict", function () {
    var result = {
        y: this.getFullYear() - 2000,
        Y: this.getFullYear(),
        m: this.getMonth() + 1,
        d: this.getDate(),
        H: this.getHours(),
        M: this.getMinutes(),
        S: this.getSeconds()
    };

    function fill(k, n) {
        result[k+k] = result[k].fillZero(n);
    }

    fill('m', 2);
    fill('d', 2);
    fill('H', 2);
    fill('M', 2);
    fill('S', 2);

    return result;
});

Date.method("format", function (fmt) {
    return fmt.format(this.toDict());
});

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
    return this.replace(/{[\w .]+}/igm, function (matched, idx, str) {
        var key = matched.substring(1, matched.length - 1).trim(),
            outerObject = obj,
            keyParts,
            i;

        if (key in outerObject) {
            return outerObject[key];
        }

        keyParts = key.split(".");

        for (i=0;i<keyParts.length;i++) {
            key = keyParts[i];
            if (outerObject && typeof outerObject === 'object' && key in outerObject) {
                outerObject = outerObject[key];
            } else {
                return matched;
            }
        }
        return outerObject;
    });
});

String.method("renderDOM", function (obj, wrapper) {
    var wrapDOM = document.createElement(wrapper || "div");

    wrapDOM.innerHTML = this.format(obj);

    return wrapper ? wrapDOM : wrapDOM.children;
});

Number.method("isInt", function () {
    return this % 1 === 0;
});

Number.method("fillZero", function (l, n, tail) {
    var v,
        num,
        i;

    if (typeof l !== 'number') {
        throw new Error(l);
    }

    v = this.toString(n || 10);
    num = l - v.length;

    for (i=0;i<num;i++) {

        if (tail) {
            v += '0';
        } else {
            v = '0' + v;
        }
    }

    return v;
});

Array.method('remove', function (item) {
    var idx = this.indexOf(item);

    if (idx !== -1) {
        this.splice(idx, 1);
    }
});

function loadFile(options) {
    var id = "-file-input",
        type = options.type,
        accept = options.accept,
        misCb = options.onMissingType,
        cb = options.callback,
        notSupp = options.notSupported,
        useFileReader = options.useFileReader,
        dom;

    dom = document.querySelector("#"+id);

    if (!dom) {
        dom = document.createElement("input");
        dom.setAttribute("id", id);
        dom.setAttribute("type", "file");
        dom.style.display = "none";

        document.body.appendChild(dom);

        addEventListener(dom, "change", function () {
            var file = dom.files[0],
                matched = false,
                reader;

            if (!type) {
                matched = true;
            } else if (type.test) {
                matched = type.test(file.type);
            } else {
                matched = file.type === type;
            }

            if (!matched) {
                console.warn("文件类型不匹配: " + file.type, type);
                typeof misCb === "function" && misCb(file);
                return;
            }

            if (useFileReader) {

                if (!window.FileReader) {
                    console.warn("浏览器不支持FileReader");
                    typeof notSupp === "function" && notSupp();
                    return;
                }

                reader = new FileReader();

                reader.onload = function (event) {
                    cb(event.target.result);
                };

                reader.readAsDataURL(file);
            } else {
                cb(file);
            }
        });
    }

    dom.setAttribute("accept", accept);

    dom.click();
}

function toggleClick(dom, fn) {
    "use strict";

    var listener = toggleClick._listener = toggleClick._listener || function (event) {

        var target = event.currentTarget,
            status,
            result;

        status = (target.dataset.toggleStatus === "true");
        typeof fn === 'function' && (result = fn(status, target, function () {
            toggleClick._toggleFn(target);
        }));

        typeof result === "boolean" && result && toggleClick._toggleFn(target);
    };

    toggleClick._toggleFn = toggleClick._toggleFn || function (target) {
        var status,
            currentText,
            toggleText,
            toggleClass;

        currentText = target.innerText;
        toggleText = target.dataset.toggleText;
        toggleClass = target.dataset.toggleClass;

        target.classList.toggle(toggleClass);
        target.innerText = toggleText;
        target.dataset.toggleText = currentText;
        status = (target.dataset.toggleStatus === "true");
        target.dataset.toggleStatus = !status;
    };

    addEventListener(dom, "click", listener);

    return listener;
}

var animationsFunction = {
    easeOut: function (t, b, c, d) {
        return (c-b)*((t=t/d-1)*t*t + 1) + b;
    },
    easeIn: function(t,b,c,d){
        return (c-b)*(t/=d)*t + b;
    },
    easeInOut: function(t,b,c,d){
        if ((t/=d/2) < 1) return (c-b)/2*t*t + b;
        return (b-c)/2 * ((--t)*(t-2) - 1) + b;
    }
};

function insertAfter(newElement, targetElement) {
    var parent = targetElement.parentNode,
        last = parent.lastElementChild || parent.lastChild,
        next;

    if (last === targetElement) {
        parent.appendChild(newElement);
    } else {
        next = targetElement.nextElementSibling || targetElement.nextSibling;
        parent.insertBefore(newElement, next);
    }
}