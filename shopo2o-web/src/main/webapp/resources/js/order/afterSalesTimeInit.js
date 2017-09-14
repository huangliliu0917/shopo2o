//初始化时间插件
$("#orderCreateTimePick").daterangepicker(
    {
        showDropdowns: true,
        showWeekNumbers: false, //是否显示第几周
        timePicker: true, //是否显示小时和分钟
        timePickerIncrement: 60, //时间的增量，单位为分钟
        timePicker12Hour: false, //是否使用12小时制来显示时间
        opens: 'right', //日期选择框的弹出位置
        buttonClasses: ['btn btn-default'],
        applyClass: 'btn-small btn-primary blue',
        cancelClass: 'btn-small',
        autoUpdateInput: false,
        format: 'YYYY-MM-DD HH:mm:ss', //控件中from和to 显示的日期格式
        separator: ' to ',
        locale: {
            applyLabel: '确定',
            cancelLabel: '取消',
            fromLabel: '起始时间',
            toLabel: '结束时间',
            customRangeLabel: '自定义',
            daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
            monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                '七月', '八月', '九月', '十月', '十一月', '十二月'],
            firstDay: 1
        }
    }, function (start, end) {//格式化日期显示框
        $('#orderCreateTimePick').val(start.format('YYYY-MM-DD HH:mm:ss') + ' - ' + end.format('YYYY-MM-DD HH:mm:ss'));
        $('#createBeginTime').val(start.format('YYYY-MM-DD HH:mm:ss'));
        $("#createEndTime").val(end.format('YYYY-MM-DD HH:mm:ss'));
    }
);
$("#orderPayTimePick").daterangepicker(
    {
        showDropdowns: true,
        showWeekNumbers: false, //是否显示第几周
        timePicker: true, //是否显示小时和分钟
        timePickerIncrement: 60, //时间的增量，单位为分钟
        timePicker12Hour: false, //是否使用12小时制来显示时间
        opens: 'right', //日期选择框的弹出位置
        buttonClasses: ['btn btn-default'],
        applyClass: 'btn-small btn-primary blue',
        cancelClass: 'btn-small',
        autoUpdateInput: false,
        format: 'YYYY-MM-DD HH:mm:ss', //控件中from和to 显示的日期格式
        separator: ' to ',
        locale: {
            applyLabel: '确定',
            cancelLabel: '取消',
            fromLabel: '起始时间',
            toLabel: '结束时间',
            customRangeLabel: '自定义',
            daysOfWeek: ['日', '一', '二', '三', '四', '五', '六'],
            monthNames: ['一月', '二月', '三月', '四月', '五月', '六月',
                '七月', '八月', '九月', '十月', '十一月', '十二月'],
            firstDay: 1
        }
    }, function (start, end) {//格式化日期显示框
        $('#orderPayTimePick').val(start.format('YYYY-MM-DD HH:mm:ss') + ' - ' + end.format('YYYY-MM-DD HH:mm:ss'));
        $('#payBeginTime').val(start.format('YYYY-MM-DD HH:mm:ss'));
        $("#payEndTime").val(end.format('YYYY-MM-DD HH:mm:ss'));
    }
);
$("#orderCreateTimePick").on('cancel.daterangepicker', function () {
    $("#orderCreateTimePick").val("");
    $("#createBeginTime").val("");
    $("#createEndTime").val("");
});
$("#orderPayTimePick").on('cancel.daterangepicker', function () {
    $("#orderPayTimePick").val("");
    $("#payBeginTime").val("");
    $("#payEndTime").val("");
});