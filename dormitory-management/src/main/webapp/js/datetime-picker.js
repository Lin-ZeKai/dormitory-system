/**
 * 日期时间选择器（Flatpickr + Element UI 风格）
 */
var DateTimePicker = (function () {
    var SERVER_FORMAT = 'Y-m-d\\TH:i';
    var DISPLAY_FORMAT = 'Y年m月d日 H:i';

    function init(input, options) {
        if (!input || typeof flatpickr === 'undefined') {
            return null;
        }
        var config = {
            enableTime: true,
            dateFormat: SERVER_FORMAT,
            altInput: true,
            altFormat: DISPLAY_FORMAT,
            locale: (flatpickr.l10ns && flatpickr.l10ns.zh) ? flatpickr.l10ns.zh : 'default',
            time_24hr: true,
            minuteIncrement: 1,
            allowInput: false,
            disableMobile: true,
            wrap: false
        };
        if (options) {
            Object.keys(options).forEach(function (key) {
                config[key] = options[key];
            });
        }
        return flatpickr(input, config);
    }

    function bindLeaveForm(form) {
        if (!form) {
            return;
        }
        var startInput = form.querySelector('#startTime');
        var endInput = form.querySelector('#endTime');
        var endPicker = init(endInput);
        var startPicker = init(startInput, {
            onChange: function (selectedDates) {
                if (endPicker && selectedDates.length) {
                    endPicker.set('minDate', selectedDates[0]);
                }
            }
        });
        return { start: startPicker, end: endPicker };
    }

    return {
        init: init,
        bindLeaveForm: bindLeaveForm
    };
})();
