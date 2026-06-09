(function () {
    function bindPasswordToggle(input, toggle) {
        toggle.addEventListener('click', function () {
            var show = input.type === 'password';
            input.type = show ? 'text' : 'password';
            toggle.classList.toggle('is-visible', show);
            toggle.setAttribute('aria-label', show ? '隐藏密码' : '显示密码');
            toggle.setAttribute('title', show ? '隐藏密码' : '显示密码');
        });
    }

    function initAllPasswordToggles(root) {
        var scope = root || document;
        scope.querySelectorAll('.password-field').forEach(function (wrap) {
            var input = wrap.querySelector('input');
            var toggle = wrap.querySelector('.password-toggle');
            if (input && toggle && !toggle.dataset.bound) {
                toggle.dataset.bound = '1';
                bindPasswordToggle(input, toggle);
            }
        });
    }

    window.initAllPasswordToggles = initAllPasswordToggles;

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', function () {
            initAllPasswordToggles();
        });
    } else {
        initAllPasswordToggles();
    }
})();
