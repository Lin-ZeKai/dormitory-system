/**
 * 学号（14位）与手机号（11位）前端校验
 * 错误提示使用顶部 Message（Element UI 风格）
 * options.live === false 时仅在表单提交时校验
 */
var DormValidation = (function () {
    var STUDENT_ID = /^\d{14}$/;
    var PHONE = /^1[3-9]\d{9}$/;
    var REAL_NAME = /^[\u4e00-\u9fa5a-zA-Z·]{2,20}$/;

    function trim(v) {
        return (v || '').trim();
    }

    function isStudentId(v) {
        return STUDENT_ID.test(trim(v));
    }

    function isPhone(v) {
        return PHONE.test(trim(v));
    }

    function isRealName(v) {
        return REAL_NAME.test(trim(v));
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

    function getStudentIdErrorMessage(v) {
        var t = trim(v);
        if (!t) {
            return '请输入14位学号';
        }
        if (isStudentId(t)) {
            return null;
        }
        if (t.length < 14) {
            return '学号须为 14 位数字，当前已输入 ' + t.length + ' 位';
        }
        return '学号须为 14 位数字';
    }

    function getRealNameErrorMessage(v) {
        var t = trim(v);
        if (!t) {
            return '请输入真实姓名';
        }
        if (isRealName(t)) {
            return null;
        }
        if (t.length < 2) {
            return '姓名至少 2 个字符';
        }
        if (t.length > 20) {
            return '姓名不能超过 20 个字符';
        }
        return '姓名只能包含中文、英文字母或间隔号「·」，不能包含数字';
    }

    function validateStudentIdField(input, forceShow) {
        if (!input) {
            return true;
        }
        var msg = getStudentIdErrorMessage(input.value);
        if (msg) {
            if (forceShow) {
                showFieldError(input, msg);
            }
            return false;
        }
        clearFieldError(input);
        return true;
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

    function validateRealNameField(input, forceShow) {
        if (!input) {
            return true;
        }
        var msg = getRealNameErrorMessage(input.value);
        if (msg) {
            if (forceShow) {
                showFieldError(input, msg);
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

    function realNameOnly(input) {
        if (!input) {
            return;
        }
        input.addEventListener('input', function () {
            var cleaned = input.value.replace(/[^\u4e00-\u9fa5a-zA-Z·]/g, '');
            if (input.value !== cleaned) {
                input.value = cleaned;
            }
        });
    }

    function setupStudentIdInput(input, options) {
        options = options || {};
        if (!input) {
            return;
        }
        input.setAttribute('maxlength', '14');
        input.setAttribute('inputmode', 'numeric');
        if (!input.getAttribute('placeholder')) {
            input.setAttribute('placeholder', '14位学号');
        }
        digitsOnly(input);
        if (options.live === false) {
            bindSubmitOnlyClear(input, function (v) {
                return !getStudentIdErrorMessage(v);
            });
        }
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

    function setupRealNameInput(input, options) {
        options = options || {};
        if (!input) {
            return;
        }
        input.setAttribute('maxlength', '20');
        if (!input.getAttribute('placeholder')) {
            input.setAttribute('placeholder', '如：张三');
        }
        realNameOnly(input);
        if (options.live === false) {
            bindSubmitOnlyClear(input, function (v) {
                return !getRealNameErrorMessage(v);
            });
        } else {
            bindLiveValidation(input, validateRealNameField);
        }
    }

    function bindStudentAccountForm(form, options) {
        options = options || {};
        if (!form) {
            return;
        }
        form.setAttribute('novalidate', 'novalidate');
        form.setAttribute('autocomplete', 'off');
        var studentSelector = options.studentField
            ? ('input[name="' + options.studentField + '"]')
            : 'input[name="username"]';
        var studentInput = form.querySelector(studentSelector);
        var phoneInput = form.querySelector('input[name="phone"]');
        var realNameInput = form.querySelector('input[name="realName"]');
        setupStudentIdInput(studentInput, options);
        setupPhoneInput(phoneInput, options);
        setupRealNameInput(realNameInput, options);
        form.addEventListener('submit', function (e) {
            var studentOk = validateStudentIdField(studentInput, true);
            if (!studentOk) {
                e.preventDefault();
                studentInput.focus();
                return;
            }
            if (!validatePhoneField(phoneInput, true)) {
                e.preventDefault();
                phoneInput.focus();
                return;
            }
            if (!validateRealNameField(realNameInput, true)) {
                e.preventDefault();
                realNameInput.focus();
                return;
            }
            if (trim(studentInput.value) === trim(phoneInput.value)) {
                e.preventDefault();
                Message.error('学号与手机号不能相同');
                studentInput.focus();
            }
        });
    }

    function bindRegisterForm(form, options) {
        bindStudentAccountForm(form, options);
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
        isRealName: isRealName,
        isUsername: isUsername,
        getStudentIdErrorMessage: getStudentIdErrorMessage,
        getUsernameErrorMessage: getUsernameErrorMessage,
        getPhoneErrorMessage: getPhoneErrorMessage,
        getRealNameErrorMessage: getRealNameErrorMessage,
        showFieldError: showFieldError,
        clearFieldError: clearFieldError,
        validateStudentIdField: validateStudentIdField,
        validateUsernameField: validateUsernameField,
        validatePhoneField: validatePhoneField,
        validateRealNameField: validateRealNameField,
        setupStudentIdInput: setupStudentIdInput,
        setupUsernameInput: setupUsernameInput,
        setupPhoneInput: setupPhoneInput,
        setupRealNameInput: setupRealNameInput,
        bindRegisterForm: bindRegisterForm,
        bindStudentAccountForm: bindStudentAccountForm,
        bindUsernameForm: bindUsernameForm,
        bindPhoneForm: bindPhoneForm
    };
})();
