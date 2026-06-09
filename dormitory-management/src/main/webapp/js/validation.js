/**
 * 学号（14位）与手机号（11位）前端校验
 * 错误提示使用顶部 Message（Element UI 风格）
 * options.live === false 时仅在表单提交时校验
 */
var DormValidation = (function () {
    var STUDENT_ID = /^\d{14}$/;
    var PHONE = /^1[3-9]\d{9}$/;

    function trim(v) {
        return (v || '').trim();
    }

    function isStudentId(v) {
        return STUDENT_ID.test(trim(v));
    }

    function isPhone(v) {
        return PHONE.test(trim(v));
    }

    function isUsername(v) {
        var t = trim(v);
        return isStudentId(t) || isPhone(t);
    }

    function getUsernameErrorMessage(v) {
        var t = trim(v);
        if (!t) {
            return '请输入14位学号或11位手机号';
        }
        if (isUsername(t)) {
            return null;
        }
        var len = t.length;
        if (len === 11) {
            if (t.charAt(0) !== '1') {
                return '手机号须以 1 开头';
            }
            if (!/^[3-9]$/.test(t.charAt(1))) {
                return '手机号第二位须为 3-9（如 138、159）';
            }
            return '手机号格式不正确';
        }
        if (len < 11) {
            return '位数不足，请输入 14 位学号或 11 位手机号';
        }
        if (len > 11 && len < 14) {
            return '学号须为 14 位数字，当前已输入 ' + len + ' 位';
        }
        return '请输入 14 位学号或 11 位手机号';
    }

    function getPhoneErrorMessage(v) {
        var t = trim(v);
        if (!t) {
            return '请输入 11 位手机号';
        }
        if (isPhone(t)) {
            return null;
        }
        if (t.length < 11) {
            return '手机号须为 11 位数字，当前已输入 ' + t.length + ' 位';
        }
        if (t.charAt(0) !== '1') {
            return '手机号须以 1 开头';
        }
        if (!/^[3-9]$/.test(t.charAt(1))) {
            return '手机号第二位须为 3-9（如 138、159）';
        }
        return '手机号格式不正确';
    }

    function notifyError(message) {
        if (window.Message && typeof window.Message.error === 'function') {
            window.Message.error(message);
        }
    }

    function markInvalid(input) {
        if (input) {
            input.classList.add('is-invalid');
        }
    }

    function showFieldError(input, message, withToast) {
        if (!input || !message) {
            return;
        }
        if (withToast !== false) {
            notifyError(message);
        }
        markInvalid(input);
    }

    function clearFieldError(input) {
        if (!input) {
            return;
        }
        input.classList.remove('is-invalid');
    }

    function shouldShowUsernameLive(input) {
        return trim(input.value).length >= 11;
    }

    function validateUsernameField(input, forceShow) {
        if (!input) {
            return true;
        }
        var value = trim(input.value);
        if (!value) {
            if (forceShow) {
                showFieldError(input, getUsernameErrorMessage(''));
                return false;
            }
            clearFieldError(input);
            return false;
        }
        var msg = getUsernameErrorMessage(value);
        if (msg) {
            if (forceShow) {
                showFieldError(input, msg);
            } else if (shouldShowUsernameLive(input)) {
                markInvalid(input);
            }
            return false;
        }
        clearFieldError(input);
        return true;
    }

    function validatePhoneField(input, forceShow) {
        if (!input) {
            return true;
        }
        var value = trim(input.value);
        if (!value) {
            if (forceShow) {
                showFieldError(input, getPhoneErrorMessage(''));
                return false;
            }
            clearFieldError(input);
            return false;
        }
        var msg = getPhoneErrorMessage(value);
        if (msg) {
            if (forceShow) {
                showFieldError(input, msg);
            } else if (value.length >= 11) {
                markInvalid(input);
            }
            return false;
        }
        clearFieldError(input);
        return true;
    }

    function bindLiveValidation(input, validateFn) {
        if (!input) {
            return;
        }
        input.addEventListener('input', function () {
            validateFn(input, false);
        });
        input.addEventListener('blur', function () {
            validateFn(input, true);
        });
    }

    function bindSubmitOnlyClear(input, isValidFn) {
        if (!input) {
            return;
        }
        input.addEventListener('input', function () {
            if (input.classList.contains('is-invalid') && isValidFn(trim(input.value))) {
                clearFieldError(input);
            }
        });
    }

    function digitsOnly(input) {
        if (!input) {
            return;
        }
        input.addEventListener('input', function () {
            var cleaned = input.value.replace(/\D/g, '');
            if (input.value !== cleaned) {
                input.value = cleaned;
            }
        });
    }

    function setupUsernameInput(input, options) {
        options = options || {};
        if (!input) {
            return;
        }
        input.setAttribute('maxlength', '14');
        input.setAttribute('inputmode', 'numeric');
        if (!input.getAttribute('placeholder')) {
            input.setAttribute('placeholder', '14位学号或11位手机号');
        }
        digitsOnly(input);
        if (options.live === false) {
            bindSubmitOnlyClear(input, function (v) {
                return !getUsernameErrorMessage(v);
            });
        } else {
            bindLiveValidation(input, validateUsernameField);
        }
    }

    function setupPhoneInput(input, options) {
        options = options || {};
        if (!input) {
            return;
        }
        input.setAttribute('maxlength', '11');
        input.setAttribute('inputmode', 'numeric');
        if (!input.getAttribute('placeholder')) {
            input.setAttribute('placeholder', '11位手机号');
        }
        digitsOnly(input);
        if (options.live === false) {
            bindSubmitOnlyClear(input, function (v) {
                return !getPhoneErrorMessage(v);
            });
        } else {
            bindLiveValidation(input, validatePhoneField);
        }
    }

    function bindUsernameForm(form, selector, options) {
        options = options || {};
        if (!form) {
            return;
        }
        form.setAttribute('novalidate', 'novalidate');
        var input = form.querySelector(selector || 'input[name="username"]');
        setupUsernameInput(input, options);
        form.addEventListener('submit', function (e) {
            if (!validateUsernameField(input, true)) {
                e.preventDefault();
                input.focus();
            }
        });
    }

    function bindPhoneForm(form, selector, options) {
        options = options || {};
        if (!form) {
            return;
        }
        form.setAttribute('novalidate', 'novalidate');
        var input = form.querySelector(selector || 'input[name="phone"]');
        setupPhoneInput(input, options);
        form.addEventListener('submit', function (e) {
            if (!validatePhoneField(input, true)) {
                e.preventDefault();
                input.focus();
            }
        });
    }

    return {
        isStudentId: isStudentId,
        isPhone: isPhone,
        isUsername: isUsername,
        getUsernameErrorMessage: getUsernameErrorMessage,
        getPhoneErrorMessage: getPhoneErrorMessage,
        showFieldError: showFieldError,
        clearFieldError: clearFieldError,
        validateUsernameField: validateUsernameField,
        validatePhoneField: validatePhoneField,
        setupUsernameInput: setupUsernameInput,
        setupPhoneInput: setupPhoneInput,
        bindUsernameForm: bindUsernameForm,
        bindPhoneForm: bindPhoneForm
    };
})();
