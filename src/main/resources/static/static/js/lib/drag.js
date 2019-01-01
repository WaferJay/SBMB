(function () {

    "use strict";

    function indexOf(nodeList, node) {
        var i;

        for (i=0;i<nodeList.length;i++) {
            if (nodeList[i] === node) {
                return i;
            }
        }
        return -1;
    }

    function bindDragHandler(item, dragStart, dragOver, drop) {

        item.setAttribute("draggable", "true");

        if (!item.getAttribute("drag-bind")) {

            item.setAttribute("drag-bind", "1");
            addEventListener(item, 'dragstart', dragStart);
            addEventListener(item, 'dragover', dragOver);
            addEventListener(item, 'drop', drop);
        }
    }

    function drag(parentSelector, itemTagKey, cb) {
        var $parent = document.querySelector(parentSelector),
            $items = $parent.children,
            itemCount = $items.length,
            tagKey = 'orderTag',
            $current,
            i;

        if (typeof itemTagKey === 'string') {
            tagKey = itemTagKey;
        } else {

            for (i=0;i<itemCount;i++) {
                $items[i].dataset[tagKey] = i;
            }
        }

        function handleDragStart(event) {
            $current = event.currentTarget;
        }

        function handleDrop(event) {
            $current = null;
            typeof cb === 'function' && cb(getTags());
        }

        function handleDragOver(event) {

            var target = event.currentTarget,
                nextIndex,
                currIndex,
                overIndex;

            event.preventDefault();

            if (target === $current) {
                return;
            }

            currIndex = indexOf($parent.children, $current);
            overIndex = indexOf($parent.children, target);
            if (currIndex === -1 || overIndex === -1) {
                console.error($current, target);
                throw new Error(currIndex + " -> " + overIndex);
            }

            if (currIndex >= overIndex) {
                // 前移插在当前元素前
                $parent.insertBefore($current, target);
            } else {
                // 后移插在当前元素后
                nextIndex = overIndex + 1;
                if (nextIndex >= $parent.children.length) {
                    $parent.appendChild($current);
                } else {
                    $parent.insertBefore($current, $parent.children[nextIndex]);
                }
            }
        }

        for (i = 0; i < itemCount; i++) {

            bindDragHandler($items[i], handleDragStart, handleDragOver, handleDrop);
        }

        function getTags() {

            var i,
                tags = [];

            for (i=0;i<$items.length;i++) {
                tags.push($parent.children[i].dataset[tagKey]);
            }
            return tags;
        }

        return {
            getTags: getTags,
            handleNew: function (item) {
                bindDragHandler(item, handleDragStart, handleDragOver, handleDrop);
            }
        };
    }

    define([], function () {
        return drag;
    });

})();