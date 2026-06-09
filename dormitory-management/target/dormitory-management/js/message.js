/**
 * Element UI 风格顶部 Message 提示（纯 JS，无需 Vue）
 */
var Message = (function () {
    var TOP = 20;
    var GAP = 16;
    var DURATION = 3000;
    var items = [];

    function container() {
        var el = document.getElementById('message-container');
        if (!el) {
            el = document.createElement('div');
            el.id = 'message-container';
            el.className = 'message-container';
            document.body.appendChild(el);
        }
        return el;
    }

    function layout() {
        var top = TOP;
        items.forEach(function (item) {
            item.el.style.top = top + 'px';
            top += item.el.offsetHeight + GAP;
        });
    }

    function remove(item) {
        item.el.classList.remove('is-visible');
        item.el.classList.add('is-leaving');
        window.setTimeout(function () {
            if (item.el.parentNode) {
                item.el.parentNode.removeChild(item.el);
            }
            items = items.filter(function (x) { return x !== item; });
            layout();
        }, 280);
    }

    function show(text, type) {
        if (!text) {
            return null;
        }
        var el = document.createElement('div');
        el.className = 'message message-' + (type || 'info');
        el.setAttribute('role', 'alert');
        el.innerHTML =
            '<span class="message-icon" aria-hidden="true"></span>' +
            '<span class="message-content"></span>';
        el.querySelector('.message-content').textContent = text;
        container().appendChild(el);

        var item = { el: el };
        items.push(item);
        window.requestAnimationFrame(function () {
            el.classList.add('is-visible');
            layout();
        });

        var timer = window.setTimeout(function () {
            remove(item);
        }, DURATION);
        el.addEventListener('click', function () {
            window.clearTimeout(timer);
            remove(item);
        });
        return item;
    }

    return {
        success: function (text) { return show(text, 'success'); },
        warning: function (text) { return show(text, 'warning'); },
        error: function (text) { return show(text, 'error'); },
        info: function (text) { return show(text, 'info'); }
    };
})();
