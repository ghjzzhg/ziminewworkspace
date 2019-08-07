//给ajax添加全局事件
jQuery(document).ajaxStart(ajaxStart)
    .ajaxComplete(ajaxComplete)
    .ajaxError(ajaxError);
/*$.widget("ui.dialog", $.extend({}, $.ui.dialog.prototype, {
    _title: function(title) {
        if (!this.options.title ) {
            title.html("&#160;");
        } else {
            title.html(this.options.title);
        }
    }
}));*/

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

function showSubMenuAjax(url) {
    $(".ui-dialog-content").dialog("close");
    showLoader();
    jQuery.ajax({
        type: 'GET',
        url: url,
        async: true,
        dataType: 'html'
    }).done(function (content) {
        jQuery("#content-main-section > .page-content > .container").empty().html(content);
    }).always(function () {
        hideLoader();
    });
}

var datetimepicker_tooltip = {
    today: '今天',
    clear: '清除',
    close: '关闭',
    selectTime: '选择时间',
    selectMonth: '选择月份',
    prevMonth: '上个月',
    nextMonth: '下个月',
    selectYear: '选择年份',
    prevYear: '上一年',
    nextYear: '下一年',
    selectDecade: '选择',
    prevDecade: '前十年',
    nextDecade: '后十年',
    prevCentury: '上世纪',
    nextCentury: '下世纪'
}

/**
 *
 * @param options input：必须给定jquery的selector或者直接传入外部jquery对象；timeFormat: 日期、时间格式，默认按日期格式，如果要时间需要传入完整时间格式；yearMonth：year年选择 month 月选择
 * @returns {*} 返回jquery 对象便于级联操作
 */
function initDatePicker(options) {
    var $input;
    if (typeof(options.input) == "string") {
        $input = jQuery(options.input);
    } else if (typeof(options.input) == "object") {
        $input = options.input;
    } else {
        alert("请提供正确的绑定输入控件");
        return;
    }
    var dateOpts = {
        showClear: true,
        tooltips: datetimepicker_tooltip
    }

    var timeFormat;
    if (options.yearMonth) {
        if (options.yearMonth.toLowerCase() == "year") {
            timeFormat = options.timeFormat || "YYYY";
            jQuery.extend(dateOpts, {viewMode: 'years'});
        } else if (options.yearMonth.toLowerCase() == "month") {
            timeFormat = options.timeFormat || "YYYY-MM";
            jQuery.extend(dateOpts, {viewMode: 'months'});
        }
    } else {
        timeFormat = options.timeFormat || "YYYY-MM-DD";
        jQuery.extend(dateOpts, {
            dayViewHeaderFormat: 'YYYY MMMM',
            showTodayButton: true
        });
    }
    jQuery.extend(dateOpts, {format: timeFormat});

    $input.datetimepicker(dateOpts);

    return $input;
}

function getServerError(data) {
    var serverErrorHash = [];
    var serverError = "";
    if (data._ERROR_MESSAGE_LIST_ != undefined) {
        serverErrorHash = data._ERROR_MESSAGE_LIST_;

        jQuery.each(serverErrorHash, function (error, message) {
            if (error != undefined) {
                serverError += message;
            }
        });

        if (serverError == "") {
            serverError = serverErrorHash;
        }
    }
    if (data._ERROR_MESSAGE_ != undefined) {
        serverError += data._ERROR_MESSAGE_;
    }
    return serverError;
}
function getServerInfo(data) {
    var serverInfoHash = [];
    var serverInfo = "";
    if (data._EVENT_MESSAGE_LIST_ != undefined) {
        serverInfoHash = data._EVENT_MESSAGE_LIST_;

        jQuery.each(serverInfoHash, function (info, message) {
            if (info != undefined) {
                serverInfo += message;
            }
        });

        if (serverInfo == "") {
            serverInfo = serverInfoHash;
        }
    }
    if (data._EVENT_MESSAGE_ != undefined) {
        serverInfo += data._EVENT_MESSAGE_;
    }
    return serverInfo;
}

//添加CSRF token
jQuery.ajaxPrefilter(function (options, originalOptions, jqXHR) {
    jqXHR.setRequestHeader("CSRFToken", getCSRFToken());
});

jQuery(function () {
    jQuery.ajaxSetup({cache: false});
// Pnotify notifier
    if (jQuery.pnotify) {
        jQuery.pnotify.defaults.history = false;
        jQuery.pnotify.defaults.delay = 3000;
    }
    if (jQuery.blockUI) {
        jQuery.blockUI.defaults.timeout = 10000;
        jQuery.blockUI.defaults.ignoreIfBlocked = true;
    }
})

function initPage() {
    displayErrorInfo();
    selectHighlight();
    //plugin_defaults();
    //plugins_init();

}


//some labels are not unescaped in the JSON object so we have to do this manualy
function unescapeHtmlText(text) {
    return jQuery('<div />').html(text).text()
}

function getCSRFToken() {
    var csrfTokenValue = "";
    jQuery("input[name=CSRFToken]").each(function () {
        csrfTokenValue = jQuery(this).val();
        return false;
    });
    return csrfTokenValue;
}


function form2json(formId) {
    var o = {};
    var a = jQuery("#" + formId).serializeArray();
    jQuery.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
}

function delRepeat(obj) {
    var newArray = [];
    var provisionalTable = {};
    for (var i = 0, item; (item = obj[i]) != null; i++) {
        if (!provisionalTable[item]) {
            newArray.push(item);
            provisionalTable[item] = true;
        }
    }
    return newArray;
}

function subscribeServerNotice(channel, callback) {
    var socket = jQuery.atmosphere;
    socket.unsubscribe();
    var request = new jQuery.atmosphere.AtmosphereRequest();
    request.transport = 'websocket';
    request.url = appCtx + "/message2client/subscribe";
    request.contentType = "application/json";
    request.headers = {channel: channel};
    request.fallbackTransport = 'streaming';

    request.onMessage = function (response) {
        if ("messageReceived" == response.state && isNotBlank(response.responseBody)) {
            var param = [];
            param.push(response);
            callback.apply(this, param);
        } else if ("error" == response.state) {
            jQuery.atmosphere.log('error', [response.error]);
        }

    };
    request.onOpen = function () {
        jQuery.atmosphere.log('info', ['socket open']);
    };
    request.onError = function () {
        jQuery.atmosphere.log('info', ['socket error']);
        socket.unsubscribe();
    };
    request.onReconnect = function () {
        jQuery.atmosphere.log('info', ['socket reconnect']);
    };
    var subSocket = socket.subscribe(request);
}

function getUploadFileDisplayName(fileName) {
    if (!isBlank(fileName)) {
        var split = fileName.split("{}");
        if (split.length == 2) {
            var fileBaseName = split[0];
            var split2 = split[1].split(".");
            if (split2.length == 2) {
                return fileBaseName + "." + split2[1];
            }
        }
    }
    return fileName;
}

function popup(options) {

    var css = "default_modal";
    if (options.popupCss) {
        css = options.popupCss
    }
    var id = options.id;
    jQuery(options.content).filter(options.filter).each(function () {
        if (document.getElementById(id)) {
            jQuery("#" + id).html(jQuery(this).html());
            jQuery("#" + id).modal("show");
        } else {
            var obj = jQuery('<div id="' + id + '" class="modal hide fade ' + css + '">' + jQuery(this).html() + '</div>').modal(
                {
                    backdrop: "static",
                    keyboard: false,
                    show: true
                }
            );
            if (options.show) {
                obj.on("show", options.show);
            }
            if (options.shown) {
                obj.on("shown", options.shown);
            }
            if (options.hidden) {
                obj.on("hidden", options.hidden);
            } else {
                obj.on("hidden", function () {
                    obj.remove();
                });
            }
            if (options.hide) {
                obj.on("hide", options.hide);
            }
        }

    });
}

function closePopup(id) {
    jQuery("#" + id).modal("hide");
}

function chooseAll(obj, id) {
    jQuery("table[id=" + id + "] tbody tr td:first-child input:checkbox").each(function () {
        this.checked = obj.checked;
        jQuery.uniform.update(jQuery(this));
    });
}

function showLoader(msg) {
    getLayer().load();
}

function hideLoader() {
    getLayer().closeAll('loading');
//    jQuery("#load-progress").css("display", "none");
}

function resetPagination(criteriaName) {
    document.getElementById(criteriaName + ".sortCriterion").value = "ID";
    document.getElementById(criteriaName + ".sortDirection").value = "desc";
    document.getElementById(criteriaName + ".pageNumber").value = "1";
}

function showLine(id, content) {
    jQuery("div[id^=close]").each(function () {
        if (jQuery(this).hasClass("col-right_down_display")) {
            hideLine(this.id.substr(5))
        }
    });

    jQuery("#open" + id).css('display', 'none');
    jQuery("#close" + id).removeClass("col-right_down");
    jQuery("#close" + id).addClass("col-right_down_display");
    jQuery("#lineDiv" + id).html(content);
    jQuery("#lineDiv" + id).show();
    jQuery("#lineTr" + id).css('display', '');

}

function hideLine(id) {
    jQuery("#open" + id).css('display', '');
    jQuery("#close" + id).removeClass("col-right_down_display");
    jQuery("#close" + id).addClass("col-right_down");
    jQuery("#lineDiv" + id).html("");
    jQuery("#lineDiv" + id).show();
    jQuery("#lineTr" + id).css('display', 'none');
}


/*function getExpandLine(id, colspan, px2right) {
 return '<tr id="lineTr' + id + '" style="display: none">'
 + '<td colspan="' + colspan + '" class="inner_table">'
 + '<div style="right: ' + px2right + 'px"></div>'
 + '<div id="lineDiv' + id + '"></div>'
 + '</td>'
 + '</tr>';
 }*/

function getExpandLine(id, colspan, px2right) {
    if (!document.getElementById("lineTr" + id)) {
        return '<tr><td id="lineTr' + id + '" colspan="' + colspan + '" style="display: none"' + '" class="inner_table">'
            + '<div class="arrow" style="right: ' + px2right + 'px"></div>'
            + '<div id="lineDiv' + id + '"></div>'
            + '</td></tr>'
    } else {
        return "";
    }
}

function selectHighlight() {
    jQuery(".dataTable tbody tr, .displaytag tbody tr").click(function () {
        var line = jQuery(this);
        var id = this.id;
        if (id.indexOf("lineTr") == -1 && !line.hasClass("ui-selected")) {
            line.addClass("ui-selected");
            line.siblings().removeClass("ui-selected");
            var copyLine = jQuery("tr[tid=" + id + "]");
            copyLine.addClass("ui-selected");
            copyLine.siblings().removeClass("ui-selected");
        }
    });
}

function jsonToString(object) {
    if (object == null) {
        return 'null';
    }
    var type = typeof object;
    if ('object' == type) {
        if (Array == object.constructor) {
            type = 'array';
        } else if (RegExp == object.constructor) {
            type = 'regexp';
        } else {
            type = 'object';
        }
    }
    switch (type) {
        case 'undefined':
        case 'unknown':
        {
            return;
            break;
        }
        case 'function':
        {
            return '"' + object() + '"';
            break;
        }
        case 'boolean':
        case 'regexp':
        {
            return object.toString();
            break;
        }
        case 'number':
        {
            return isFinite(object) ? object.toString() : 'null';
            break;
        }
        case 'string':
        {
            return '"' +
                object.replace(/(\\|\")/g, "\\$1").replace(/\n|\r|\t/g, function () {
                    var a = arguments[0];
                    return (a == '\n') ? '\\n' : (a == '\r') ? '\\r' : (a == '\t') ? '\\t' : ""
                }) +
                '"';
            break;
        }
        case 'object':
        {
            if (object === null)
                return 'null';
            var results = [];
            for (var property in object) {
                var value = jQuery.jsonToString(object[property]);
                if (value !== undefined)
                    results.push(jQuery.jsonToString(property) + ':' + value);
            }
            return '{' + results.join(',') + '}';
            break;

        }
        case 'array':
        {
            var results = [];
            for (var i = 0; i < object.length; i++) {
                var value = jQuery.jsonToString(object[i]);
                if (value !== undefined)
                    results.push(value);
            }
            return '[' + results.join(',') + ']';
            break;

        }
    }
}

function displayJsonResponse(data) {
    var msg = "";

    var serverError = getServerError(data);
    if (serverError) {
        showError(serverError);
    } else {
        var serverInfo = getServerInfo(data);
        if (serverInfo) {
            showInfo(serverInfo);
        } else {
            if (data.data) {
                if (typeof(data.data) == "string") {
                    msg = data.data;
                } else if (typeof(data.data) == "object" && data.data.msg) {
                    msg = data.data.msg;
                }
            } else {
                msg = "操作成功";
            }
            if (data.status || "操作成功" == msg) {
                showInfo(msg)
            } else {
                showError(msg)
            }
        }
    }

}

function showInfo(content, timeout) {
    var layer = getLayer();
    if(layer){
        layer.msg(content, {time: timeout?timeout:2000, icon: 1, closeBtn:0,offset:'15px',shift:6, zIndex:99999999});
    }else{
        new PNotify({
            title: '信息',
            text: content,
            type: 'info',
            opacity: .8
        });
    }

    /*var life = 2000;
     if(timeout){
     life = timeout;
     }
     jQuery.jGrowl(content, {life: timeout, position: "top-right", closer:false, themeState:"", theme: "info-notification"});*/

}

function showInfoSticky(content) {
    if (layer) {
        getLayer().msg(content, {time: 0, icon: 1, closeBtn: 1, offset: '15px', shift: 6});
    } else {
        new PNotify({
            title: '信息',
            text: content,
            type: 'info',
            opacity: .8,
            hide: false
        });
    }
//    jQuery.jGrowl(content, {sticky: true, position: "top-right", closer:false, themeState:"", theme: "info-notification"});
}

function showError(content) {
    var layer = getLayer();
    if (content && typeof(content) == "string" && content.indexOf('id="error"') != -1) {
        jQuery(content).find("div[id=error]").each(function () {
            content = jQuery(this).html();
        });
    }
    if (content && content.indexOf("会话已过期") > -1) {
        if (parent) {
            parent.location.reload();
        } else {
            window.location.reload();
        }
    }
//    if (content && content.length > 50) {//一般的提示信息不会太长，否则就是其他的系统异常
//        content = "请重新登录后再次尝试，如果问题任然出现，请联系客服人员";
//    }
    if (layer) {
        layer.msg(content, {icon: 2, closeBtn: 0, offset: '15px', shift: 6, zIndex: 99999999});
    } else {
        new PNotify({
            title: '错误',
            text: content,
            opacity: .8,
            type: 'error'/*,
             history: {
             menu: true
             }*/
        });
    }

//    jQuery.jGrowl(content, {life: 10000, sticky: false, position: "top-right", closer:false, themeState:"", theme: "error-notification"});
}

function showErrorSticky(content) {
    if (content && typeof(content) == "string" && content.indexOf('id="error"') != -1) {
        jQuery(content).find("div[id=error]").each(function () {
            content = jQuery(this).html();
        });
    }
//    if (content && content.length > 50) {//一般的提示信息不会太长，否则就是其他的系统异常
//        content = "请重新登录后再次尝试，如果问题任然出现，请联系客服人员";
//    }
    if (layer) {
        layer.msg(content, {time: 0, icon: 2, closeBtn: 1, offset: '15px', shift: 6});
    } else {
        new PNotify({
            title: '错误',
            text: content,
            type: 'error',
            opacity: .8,
            hide: false/*,
             history: {
             menu: true
             }*/
        });
    }
//    jQuery.jGrowl(content, {sticky: true, position: "top-right", closer:false, themeState:"", theme: "error-notification"});
}

function closeCurrentTab(param) {//关闭最后弹出的窗口
    /*var dialogId = jQuery(".ui-dialog:visible:last").find(".ui-dialog-content")[0].id;
     jQuery("#" + dialogId).dialog("destroy").remove();
     jQuery("div.tooltip").remove();//弹出框bootstrap的提示在弹出框关闭时未能及时清理*/

    var openLayer = getLayer();
    var dialog = openLayer.dialogArray.pop();
    if (dialog === undefined)
        return;

    try {
        dialog.callback & dialog.callback(param);
    } catch (e) {
    }
    setTimeout(function(){openLayer.close(dialog.index)},100);//弹出框中发出ajax请求loading在结束时未来得及关闭，如果dialog提前关掉会造成Loading无法关闭。
}
function closeTheTab(index, param) {//根据索引关闭特定窗口
    var openLayer = getLayer(), dialogArray = openLayer.dialogArray || [], dialog;
    for(var i = 0; i < dialogArray.length; i ++){
        if(index == dialogArray[i].index){
            dialog = dialogArray[i];
            break;
        }
    }
    if (dialog === undefined)
        return;

    try {
        dialog.callback & dialog.callback(param);
    } catch (e) {
    }
    setTimeout(function(){openLayer.close(dialog.index)},100);//弹出框中发出ajax请求loading在结束时未来得及关闭，如果dialog提前关掉会造成Loading无法关闭。
}

function closeCurrentTab2() {
    var dialogId = jQuery(".ui-dialog:visible:last").find(".ui-dialog-content")[0].id;
    jQuery("#" + dialogId).dialog("close");
    jQuery("div.tooltip").remove();//弹出框bootstrap的提示在弹出框关闭时未能及时清理
}

function displayInTab(id, title, content, options) {
    alert("请使用 displayInTab3(id, title, options)函数弹出框，使用方法参考oa.js")
}

function displayInTab3(id, title, options) {

    var url = options.requestUrl;
    if (!url) {
        showError("没有指定请求路径 requestUrl");
        return;
    }

//    var top = (document.body.scrollHeight - height)/2;
//    var left = (document.body.scrollWidth - width)/2;


    var dialogAppendTo = "body";
    /*if(contentContainer.length){
     dialogAppendTo = "#column-container";
     }*/
    var width = options.width || $(dialogAppendTo).width() * 0.8;

    var height = options.height || $(dialogAppendTo).height() * 0.8;//默认总高度的90%

    var open = options.open || null;
    var afterOpen = options.afterOpen || null;
    var close = options.close || null;
    var closeOnEscape = options.closeOnEscape || true;
    var autoOpen = options.autoOpen || false;
    var resizable = options.resizable || false;

    if (open) {
        open.call();
    }
    var openLayer = getLayer();
    jQuery.ajax({
        type: "POST",
        url: url,
        data: options.data,
        timeout: AJAX_REQUEST_TIMEOUT,
        cache: false,
        success: function (data) {
            var currentIndex = openLayer.open({
                type: 1,
                title: title,
                content: data, //注意，如果data是object，那么需要字符拼接。
                closeBtn: 1, //不显示关闭按钮
                shade: options.shade == undefined ? 0.3 : options.shade,
                offset: '100px',
                area: [isNaN(width) ? width : (width + 'px'), isNaN(height) ? height : (height + 'px')],
                success: function (layero, index) {
                    if (afterOpen) {
                        afterOpen.call();
                    }
                    $(layero).find("form").validationEngine("detach").validationEngine("attach", {promptPosition: "topLeft"});
                }
            });
            openLayer.dialogArray.push({
                index: currentIndex,
                callback: close
            });
        },
        error: function (xhr, reason, exception) {
            if (exception != 'abort') {
                alert("An error occurred while communicating with the server:\n\n\nreason=" + reason + "\n\nexception=" + exception);
            }
            location.reload(true);
        }
    });
}

function getLayer(){
    var openLayer = layer;
    if(self !== top){
        openLayer = top.layer;
    }
    if(!openLayer.dialogArray){
        openLayer.dialogArray = [];
    }
    return openLayer;
}

//isInstantMessage用于判断是否是一对一聊天
function displayInLayer(title, options,isInstantMessage){
    options.scrollContent = isInstantMessage ? "no" : "yes";
    return displayInLayer2(title, options);
}

function displayInLayer2(title, options){
    var url = options.requestUrl;
    if (!url) {
        showError("没有指定请求路径 requestUrl");
        return;
    }
    if (url.indexOf('?') > -1) {
        url += "&iframe=true";
    } else {
        url += "?iframe=true";
    }

    if (options.data) {
        for (var key in options.data) {
            url += "&" + key + "=" + options.data[key];
        }
    }

//    var top = (document.body.scrollHeight - height)/2;
//    var left = (document.body.scrollWidth - width)/2;


    var dialogAppendTo = "body";
    var windowWidth = $(dialogAppendTo).width(), ratio = windowWidth <= 1600 ? 0.9 : 0.8;
    var width = options.width || windowWidth * ratio;
    //var height = options.height || "auto";
    var height = options.height || $(dialogAppendTo).height() * 0.8;//默认总高度的90%
    var openLayer = getLayer();
    var layerSettings = {
        type: 2,
        title: title,
        fixed: false,//默认弹出框随父页面滚动条滚动，解决高度问题
        closeBtn: 1, //不显示关闭按钮
        shade: options.shade == undefined ? 0.3 : options.shade,
        resize: options.resize == undefined ? false : options.resize,
        offset: '50px',
        //shift: 2,
        maxmin: false, //默认开启最大化最小化按钮
        content: [url, options.scrollContent || 'yes'] //iframe的url，no代表不显示滚动条
        ,
        success: options.open,
        // end: options.end, //通过options.end传入的函数放到dialogArray中执行，通过options.layer.end传入的交给layer处理
        zIndex: options.zIndex
    }

    if(options.respectHeight){
        $.extend(layerSettings, {area: [isNaN(width) ? width : (width + 'px'), isNaN(height) ? height : (height + 'px')]});
    }else{
        $.extend(layerSettings, {area: isNaN(width) ? width : (width + 'px')});
    }
    $.extend(layerSettings, options.layer);

    /*var oriSuccess = layerSettings.success;
    layerSettings.success = function(layero, index){
        layero.find("iframe").contents().find(".portlet:first").height(layero.find(".layui-layer-content").height()).css("overflow-y", "auto");
        if(oriSuccess){
            oriSuccess(layero, index);
        }
    };
    var oriFull = layerSettings.full;
    layerSettings.full = function(layero){
        setTimeout(function(){//直接执行时获取的新高度依然是原始高度，不知原因
            layero.find("iframe").contents().find(".portlet:first").height(layero.find(".layui-layer-content").height()).css("overflow-y", "auto");
            if(oriFull){
                oriFull(layero);
            }
        }, 100);
    };
    var oriRestore = layerSettings.restore;
    layerSettings.restore = function(layero){
        layero.find("iframe").contents().find(".portlet:first").height(layero.find(".layui-layer-content").height()).css("overflow-y", "auto");
        if(oriRestore){
            oriRestore(layero);
        }
    };*/
    var oriCancel = layerSettings.cancel;
    layerSettings.cancel = function(index, layero){
        // 维护dialogArray中数据
        var start = -1;
        $.each(openLayer.dialogArray, function (i, value) {
            if (value.index == index) {
                start = i;
            }
        });
        // 如果搜索出数据，则进行删除
        if (start != -1){
            openLayer.dialogArray.splice(start, 1);
        }
        if(oriCancel){
            oriCancel(index, layero);
        }
    };
    /*var oriEnd = layerSettings.end;
     if(oriEnd && oriEnd.length){//如果end函数需要参数传入，则通过dialogArray间接实现
     layerSettings.end = undefined;
     }else{
     oriEnd = undefined;
     };*/
    var currentIndex = openLayer.open(layerSettings);

    openLayer.dialogArray.push({
        index: currentIndex,
        callback: options.end//如果end函数需要参数传入，则通过dialogArray间接实现
    });
    return currentIndex;
}


function displayInTab2(id, title, options) {


//    var top = (document.body.scrollHeight - height)/2;
//    var left = (document.body.scrollWidth - width)/2;
    //如果有相同的id需要先删除
    var $dialogDiv = jQuery("#" + id);
    if ($dialogDiv.length > 0) {
        if ($dialogDiv.parent().hasClass('ui-dialog')) {
            $dialogDiv.dialog("open");
            return;
        }
    } else {
        alert(id + "不存在");
        return;
    }

    var maxHeight = jQuery(window).height() - 50;
    $dialogDiv.css("max-height", maxHeight + "px");

    if ($dialogDiv.find(".ui-helper-hidden-accessible").length == 0) {
        $dialogDiv.prepend("<span class='ui-helper-hidden-accessible'><input type='text'/></span>");
    }

    var width = 1000;
    if (options && options.width) {
        width = options.width;
    }

    var position = options && options.position ? options.position : ["center", "center"];
    var appendTo = options && options.appendTo ? options.appendTo : "body";
    var open = options && options.open ? options.open : null;
    var close = options && options.close ? options.close : null;
    var closeOnEscape = options && options.closeOnEscape ? options.closeOnEscape : false;
    var autoOpen = options && options.autoOpen === false ? options.autoOpen : true;

    var rexDialog = $dialogDiv.dialog({
        title: title,
        width: width,
//        height:height,
        resizable: false,
        modal: true,
        dialogClass: "noTitleStuff" + (options.appClass === false ? "" : " app-ui-dialog") + (options.dialogClass ? (" " + options.dialogClass) : ""),
        closeOnEscape: closeOnEscape,
        autoOpen: autoOpen,
        position: position,
        appendTo: appendTo,
        close: function (event, ui) {
            if (close) {
                close.call();
            }
        },
        open: function () {
            if (open) {
                open.call();
            }
            var $body = jQuery("#" + id + " .dialog-body");
            if ($body.length == 1) {
                $body.css({"max-height": (maxHeight - 100) + "px", "overflow": "auto"});
                $body.addClass("scroll");
            }
            plugins_init("#" + id);
        }
    });
    var uiDialog = rexDialog
        .data("ui-dialog");
    if (uiDialog) {
        var dialogOptions = uiDialog.uiDialog.data("uiDraggable").options;

        // Finally, extend the draggable modal box's
        // options and remove any restrictions
        jQuery.extend(dialogOptions, {cancel: '.block-fluid, .not-move', handle: '.head.dark'});
    }
    return rexDialog;
}

function getCurrentDocument() {
    return self !== top ? window.parent.document : document;
}

function getExternalLoginKey() {
    var $parent = $(top.document), externalLoginKey = $parent.find("input[name=externalLoginKey]").val();
    return externalLoginKey;
}

function displayInside(url, fullscreen) {
    if (!url) {
        showError("没有指定请求路径 url");
        return;
    }
    if (url.indexOf("?") == -1) {
        url += "?iframe=true";
    } else {
        url += "&iframe=true";
    }
    var $parent = $(top.document), externalLoginKey = $parent.find("input[name=externalLoginKey]").val();
    //添加externalLoginKey
    if(externalLoginKey){
        url = updateQueryStringParameter(url, "externalLoginKey", externalLoginKey);
    }
    if(self !== top){
        var iframe = $parent.find(".content-frame");
        iframe.attr("src", url);
        if(fullscreen){
            iframe.width($parent.width() - 50);
            iframe.css("margin-left", "-" + ($parent.find("#content-main-section .container").offset().left - 15) + "px");
        }
    } else {
        var contentContainer = $("#content-main-section .container iframe.content-frame");
        if (contentContainer.length) {
            var $target = contentContainer[0];
            $target.src = "about:blank";
            $target.contentWindow.document.write('');
            $target.contentWindow.close();
            contentContainer.remove();
            if( typeof CollectGarbage == "function") {
                CollectGarbage();
            }
            contentContainer = $("#content-main-section .container iframe.content-frame");
        }
        if (!contentContainer.length) {
            contentContainer = $("#content-main-section .container");
            var height = contentContainer.height();
            contentContainer.empty();

            var minHeight = $(window).height() - $(".page-header").height() - $("#footer").height() - 30;

            $('<iframe id="content-frame" class="content-frame" src="' + url + '" frameborder="0"></iframe>')
                /*.load(function() {//避免最外层页面出现滚动条
                    var targetHeight = this.contentWindow.document.body.offsetHeight + 20;
                    this.style.height = (targetHeight < minHeight ? minHeight : targetHeight) + 'px';
                })*/.appendTo(contentContainer).height(minHeight);
        } else {
            contentContainer.attr("src", url);
        }
        if (fullscreen) {
            contentContainer.css("width", $(document).width() - 50);
            contentContainer.css("margin-left", "-" + ($("#content-main-section .container").offset().left - 15) + "px");
        } else {
            contentContainer.css("width", "");//清除全屏iframe
            contentContainer.css("margin-left", "");//清除全屏iframe
        }
    }
}

function updateQueryStringParameter(uri, key, value) {
    var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
    var separator = uri.indexOf('?') !== -1 ? "&" : "?";
    if (uri.match(re)) {
        return uri.replace(re, '$1' + key + "=" + value + '$2');
    }
    else {
        return uri + separator + key + "=" + value;
    }
}

function ajaxStart() {
    showLoader();
}

function ajaxComplete() {
    hideLoader();
}

function ajaxError(e, jqxhr, settings, exception) {
    hideLoader();
    if("abort" == exception){//客户端中断请求错误忽略
        return;
    }
    if(jqxhr.responseXML != null && jqxhr.responseXML !=""){
        if(jqxhr.responseXML.title != null && jqxhr.responseXML.title !="") {
            if (jqxhr.responseXML.title == "413 Request Entity Too Large") {
                showError("资源文件太大，请重新选择！");
                return;
            }
        }
    }
    var response;
    try {
        response = jQuery.parseJSON(jqxhr.responseText);
    } catch (e) {

    }
    if (!response) {
        try {
            var responseText = jqxhr.responseText.substring(2);
            response = jQuery.parseJSON(responseText);
        } catch (e) {

        }
    }
    if (!response) {
        // showError("服务器无响应，请联系管理员.");
    } else {

        var serverError = getServerError(response);
        if (serverError) {
            showError(serverError);
        } else {
            var serverInfo = getServerInfo(response);
            if (serverInfo) {
                showInfo(serverInfo);
            }
        }

        /*var message = response.data;
         if(response["type"] && response["type"] == 'validate'){
         var messages = message.split(/:(.+)?/);
         var label = jQuery("label[for=" + messages[0] + "]");
         if(label.length > 0){
         message = label.html() + ":" + messages[1];
         }
         }
         showError(message);*/
    }
}

function displayErrorInfo() {
    var errorMsg = jQuery("div[id=error]");
    if (errorMsg.length == 1) {
        showError(errorMsg.html());
        errorMsg.remove();
    }
}

function eraseDate(obj) {
    jQuery(obj).siblings(".datepicker").each(function () {
        jQuery(this).val("");
    });
}

function pickupDate(obj) {
    jQuery(obj).siblings(".datepicker").each(function () {
        jQuery(this).trigger("focus");
    });
}

function checkBrowser() {
    var Sys = {};
    var ua = navigator.userAgent.toLowerCase();
    var s;
    (s = ua.match(/msie ([\d.]+)/)) ? Sys.ie = s[1] :
        (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.firefox = s[1] :
            (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.chrome = s[1] :
                (s = ua.match(/opera.([\d.]+)/)) ? Sys.opera = s[1] :
                    (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.safari = s[1] : 0;
    return Sys;
}

function isAcrobatPluginInstall() {
    var Sys = checkBrowser();
    if (Sys.safari) {
        return true;
    }
//如果是firefox浏览器
    if (navigator.plugins && navigator.plugins.length) {
        for (var x = 0; x < navigator.plugins.length; x++) {

            if (navigator.plugins[x].name == 'Adobe Acrobat')
                return true;
        }
    }
//下面代码都是处理IE浏览器的情况
    else if (window.ActiveXObject) {
        for (x = 2; x < 10; x++) {
            try {
                var oAcro = eval("new ActiveXObject('PDF.PdfCtrl." + x + "');");
                if (oAcro) {
                    return true;
                }
            }
            catch (e) {
            }
        }
        try {
            var oAcro4 = new ActiveXObject('PDF.PdfCtrl.1');
            if (oAcro4)
                return true;
        }
        catch (e) {
        }
        try {
            var oAcro7 = new ActiveXObject('AcroPDF.PDF.1');
            if (oAcro7)
                return true;
        }
        catch (e) {
        }
    }
}

function previewReport(options) {
    jQuery.ajax({
        type: 'POST',
        url: appCtx + "/report/ajax/generate/" + options.type,
        data: options.data,
        async: true,
        dataType: 'json',
        success: function (data) {
            popupView(data.fileName, options);
        }
    })
}

function previewFile(fileUrl, readOnly) {
    jQuery.ajax({
        type: 'POST',
        url: appCtx + "/util/ajax/upload/file/preview",
        async: true,
        dataType: 'json',
        data: {"fileUrl": fileUrl},
        success: function (data) {
            jQuery('#viewerPlaceHolder').FlexPaperViewer({
                config: {
                    ViewerPath: appCtx + '/FlexPaperViewer.swf',
                    SwfFile: appCtx + encodeURIComponent(encodeURIComponent(data.data)),
                    ReadOnly: readOnly,
                    Scale: 0.6,
                    ZoomTransition: 'easeOut',
                    ZoomTime: 0.5,
                    ZoomInterval: 0.2,
                    FitPageOnLoad: true,
                    FitWidthOnLoad: false,
                    FullScreenAsMaxWindow: false,
                    ProgressiveLoading: false,
                    MinZoomSize: 0.2,
                    MaxZoomSize: 5,
                    SearchMatchAll: false,
                    InitViewMode: 'Portrait',
                    RenderingOrder: 'flash',
                    StartAtPage: '',
                    jsDirectory: 'static/js/lib/flexpaper',
                    localeDirectory: 'static/js/lib/flexpaper/zh_CN',
                    PrintEnabled: false,
                    PrintToolsVisible: false,
                    ViewModeToolsVisible: true,
                    ZoomToolsVisible: true,
                    NavToolsVisible: true,
                    CursorToolsVisible: true,
                    SearchToolsVisible: true,
                    WMode: 'opaque',
                    localeChain: 'zh_CN'
                }
            });
            jQuery("#viewerPlaceHolderContainer").dialog({
                width: "800",
                height: "600",
                resizable: true,
                modal: true,
                dialogClass: "noTitleStuff"
            });
        }
    })
}

function closeFilePreview() {
    jQuery("#viewerPlaceHolderContainer").dialog("close");
}

function showWorkflow(pid) {
    var content = '<table border="0" cellspacing="0" cellpadding="0" style="width:100%">' +
        '<tr>' +
        '<td class="page_header">' +
        '流程状态图' +
        '</td>' +
        '<td class="page_header">' +
        '<div style="float:right">' +
        '<span onclick="closeCurrentTab(this)" class="button"><span>关闭</span></span>' +
        '</div>' +
        '</td>' +
        '</tr>' +
        '<tr>' +
        '<td colspan="2"><img src="' + appCtx + '/workflow/process/trace/auto/' + pid + '" /></td>' +
        '</tr>' +
        '</table>';
    displayInTab("workflowDiagram", "流程状态图", content, {width: "800px"});
}

// 引入js和css文件
function include(id, path, file) {
    if (document.getElementById(id) == null) {
        var files = typeof file == "string" ? [file] : file;
        for (var i = 0; i < files.length; i++) {
            var name = files[i].replace(/^\s|\s$/g, "");
            var att = name.split('.');
            var ext = att[att.length - 1].toLowerCase();
            var isCSS = ext == "css";
            var tag = isCSS ? "link" : "script";
            var attr = isCSS ? " type='text/css' rel='stylesheet' " : " type='text/javascript' ";
            var link = (isCSS ? "href" : "src") + "='" + path + name + "'";
            document.write("<" + tag + (i == 0 ? " id=" + id : "") + attr + link + "></" + tag + ">");
        }
    }
}

// 打开一个窗体
function windowOpen(url, name, width, height) {
    var top = parseInt((window.screen.height - height) / 2, 10), left = parseInt((window.screen.width - width) / 2, 10),
        options = "location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes," +
            "resizable=yes,scrollbars=yes," + "width=" + width + ",height=" + height + ",top=" + top + ",left=" + left;
    window.open(url, name, options);
}

// 显示加载框
function loading(mess) {
    jQuery.jBox.tip.mess = null;
    jQuery.jBox.tip(mess, 'loading', {opacity: 0});
}


function addDatePicker(selector) {

    jQuery(selector).datepicker({
        //        todayBtn: true,
        //        clearBtn: true,
        todayHighlight: true,
        language: 'zh-CN',
        autoclose: true
    });

    /* jQuery(selector).datepicker({
     showButtonPanel:true,
     yearRange:"1900:2050",
     changeYear:true,
     changeMonth:true,
     dateFormat:'yy-mm-dd',
     autoSize:true,
     showAnim:'slideDown',
     beforeShow:function (input) {
     jQuery('#ui-datepicker-div').maxZIndex();
     registerDateClearBtn(input);
     }
     });
     jQuery(selector).each(function(){
     if (jQuery(this).siblings().length == 0) {
     jQuery(this).after('&nbsp;<span title="选择日期" class="link" style="vertical-align: middle;" onClick="pickupDate(this)"><img height="16" src="' + appCtx + '/static/images/ico_cal.gif" border="0"/></span>');
     }
     })*/

}

function isJsonString(str) {
    try {
        JSON.parse(str);
    } catch (e) {
        return false;
    }
    return true;
}

function deleteEnum(enumId, enumTypeId) {
    $.ajax({
        type: 'POST',
        url: "deleteEnum",
        async: false,
        dataType: 'json',
        data: {enumId: enumId},
        success: function (data) {
            ajaxUpdateArea("EditEnumsWrapper", "EditEnums", "enumTypeId=" + enumTypeId)
        }
    });
}

/**
 * 汉字词的拼音首字母
 * @param hanzi 汉字
 * @param updateFields 转换后内容需要回填的区域id（不带#号），可以是input类型，也可以是div\span等
 */
function getPinYin(hanzi, updateFields) {
    jQuery.ajax({
        type: "GET",
        url: "getPinYin",
        data: {hanziContent: hanzi},
        timeout: AJAX_REQUEST_TIMEOUT,
        cache: true,
        async: true,
        dataType: 'json',
        success: function (data) {
            if (updateFields) {
                if (typeof(updateFields) == "string") {
                    updateFields = [updateFields];
                }
                for (var field in updateFields) {
                    jQuery("#" + updateFields[field] + ":not(:input)").html(data.pinyin);
                    jQuery("#" + updateFields[field] + ":input").val(data.pinyin);
                }
            }
        },
        error: function (xhr, reason, exception) {

        }
    });
}

//判断是case聊天还是即时聊天
function openWhichRoom(title, chatRoomJID,obj) {
    obj.parent().remove();
    $(".msg-number").text($("#messageList").find("li").length).pulsate("destroy");
    $.ajax({
        url:"whichRoom",
        type:"post",
        data:{"jid":chatRoomJID},
        dataType:"json",
        success:function (data) {
            if(data.result == "即时聊天")
            {
                openInstantMessages(title, chatRoomJID);
            }else {
                checkUserIsMember(title,chatRoomJID);
            }

        }
    })
}

//case协作需要判断当前用户是否还在case成员中，防止被删除用户依旧可以进入
function checkUserIsMember(title,jid) {
    $.ajax({
        type:"post",
        url:"checkUserIsMember",
        data:{jid:jid},
        success:function (data) {
            if(data.isMember=="Y")
            {
                openCaseChat(title, jid);
            }else {
                showError("您已经从该case中移除，请联系该case的项目经理！");
            }
        }
    })
}

function openChatRoom(options){
    //更新最近聊天记录
    top.updateRecentChatHistory();

    var url = options.url, title = options.title, chatRoomJID = options.chatRoomJID, hideMem = options.hideMem, width= options.width, height = options.height;
    var chatRoomIndexMap = getLayer().chatRoomIndexMap;
    if(chatRoomIndexMap && chatRoomIndexMap[chatRoomJID]){//如果已经存在相同的room聊天窗口，则忽略此次调用。
        var roomIndex = chatRoomIndexMap[chatRoomJID];
        var layero = $("#layui-layer" + roomIndex);
        if(layero.length){
            getLayer().restore(roomIndex);
            layero.click();
        }
        return;
    }
    //更新聊天时间
    $.ajax({
        type:"post",
        async:true,
        url:"updateChatTime",
        data:{jid:chatRoomJID},
        dataType:"json",
        success:function (data) {
        }
    })
    var roomLayerIndex = displayInLayer(title, {
        requestUrl: url, data: {chatRoomJID: chatRoomJID,hideMem: hideMem,clientDate: new Date().getTime()}, width: width, height: height, shade: 0, respectHeight: true, layer: {
            btn:['关闭'],
            btn1: function(index, layero){
                layero.find("iframe")[0].contentWindow.leaveAndClose();
            },
            id: chatRoomJID.substring(0, chatRoomJID.indexOf('@')),//避免重复打开同一个聊天室
            maxmin: true,
            /*min: function (layero) {//最小化回调函数
             var selector = layero.selector;
             var thisIframeIndex = selector.replace("#layui-layer", "");
             if(!getIframeInfos().iframePosition[thisIframeIndex] && getIframeInfos().iframePosition[thisIframeIndex] != 0){
             getIframeInfos().iframePosition[thisIframeIndex] = getIframeInfos().minChatIframeCount;
             getIframeInfos().minChatIframeCount ++;
             }
             getIframeInfos().iframeIndexes += "min#" + thisIframeIndex + "#";
             $(selector, parent.document).css({"top": "0px", "left": getIframeInfos().iframePosition[thisIframeIndex] * 180 + "px"});
             },*/
            closeBtn: 0,
            /*cancel: function (index) {//被关闭时的回调函数
             if(getIframeInfos().minChatIframeCount > 0){
             getIframeInfos().minChatIframeCount --;
             }
             if(getIframeInfos().iframePosition){
             for(item in getIframeInfos().iframePosition){
             if(getIframeInfos().iframePosition[item] > getIframeInfos().iframePosition[index]){
             getIframeInfos().iframePosition[item] --;
             $("#layui-layer" + item).css({"left": getIframeInfos().iframePosition[item] * 180 + "px"});
             }
             }
             }
             delete getIframeInfos().iframePosition[index];
             },*/
            restore: function (layero) {//还原窗口的回调函数
                /*var selector = layero.selector;
                 var thisIframeIndex = selector.replace("#layui-layer", "");
                 $("#layui-layer" + thisIframeIndex + "> div[class = 'layui-layer-title']").css({"background": ""});
                 getIframeInfos().iframeIndexes = getIframeInfos().iframeIndexes.replace("min#" + thisIframeIndex + "#", "") + "normal" + thisIframeIndex;
                 layer.style(thisIframeIndex, {"background": ""});*/
                layero.find("div.layui-layer-title").css("background", "inherit");
                layero.css("width", "90%");
                setTimeout(function(){
                    var editorWin = layero.find("iframe")[0].contentWindow;
                    editorWin.changeEditorHeight();
                    if(editorWin.activateNtko){
                        editorWin.activateNtko();
                    }
                }, 1000);

            },full: function(layero){
                    layero.css("width", "100%");
                    setTimeout(function(){
                        var editorWin = layero.find("iframe")[0].contentWindow;
                        editorWin.changeEditorHeight();
                        if(editorWin.activateNtko){
                            editorWin.activateNtko();
                        }
                    }, 1000);
            },
            // zIndex: getLayer().zIndex, //重点1
            success:function(layero, roomLayerIndex){
                // getLayer().setTop(layero); //重点2

                //将聊天id与打开的弹出框索引记录下来，从而可以判定同一个聊天室是否已经存在打开的聊天室
                var chatRoomIndexMap = getLayer().chatRoomIndexMap;
                if(chatRoomIndexMap == undefined){
                    chatRoomIndexMap = {};
                    getLayer().chatRoomIndexMap = chatRoomIndexMap
                }
                chatRoomIndexMap[chatRoomJID] = roomLayerIndex;
                var attachedChatResizeEvent = getLayer().attachedChatResizeEvent;
                if(!attachedChatResizeEvent){
                    getLayer().attachedChatResizeEvent = true;
                    $(top.window).on('resize', function(){
                        console.log("resize");
                        var winHeight = $(top.window).height(), fitHeight = winHeight * 0.8;
                        var chats = getLayer().chatRoomIndexMap;
                        for(var roomId in chats){
                            var chatIndex = chats[roomId];
                            if(chatIndex){
                                var layerObj = $("#layui-layer" + chatIndex, top.document);
                                if(layerObj.data("minimized")){
                                    continue;
                                }
                                var isFull = layerObj.find(".layui-layer-max").hasClass("layui-layer-maxmin");
                                layerObj.height(isFull ? winHeight : fitHeight);
                                var iframeWin = layerObj.find("iframe");
                                iframeWin.height(layerObj.height() - 100);
                                iframeWin[0].contentWindow.changeEditorHeight();
                            }
                        }
                    });
                }
            },end: function(index){
                var chatRoomIndexMap = getLayer().chatRoomIndexMap;
                if(chatRoomIndexMap){
                    for(var roomId in chatRoomIndexMap){
                        if(chatRoomIndexMap[roomId] == index){
                            chatRoomIndexMap[roomId] = undefined;
                        }
                    }
                }
            }
        }
    },//增加辨识，取消滚动条
    true);
}
function openInstantMessages(title, chatRoomJID) {
    openChatRoom({url: 'openInstantMessage', title: title, chatRoomJID: chatRoomJID,hideMem:"noHide", width: '60%', height: '70%'});
}

function openCaseChat(title, chatRoomJID) {
    //查看该用户是否为项目经理，不是项目经理无法邀请用户
    $.ajax({
        type:"post",
        url:"checkIsManage",
        data:{chatRoomJID:chatRoomJID},
        dataType:"json",
        async:false,
        success:function (data) {
            openChatRoom({url: 'ChatHome', title: title, chatRoomJID: chatRoomJID,hideMem: data.data, width: '90%', height: '80%'});
        }
    })

}
function getIframeInfos(){
    var layer = getLayer();
    if(!layer.iframePosition){
        layer.iframePosition = {};
    }
    if(!layer.iframeIndexes){
        layer.iframeIndexes = "";
    }
    if(!layer.minChatIframeCount){
        layer.minChatIframeCount = 0;
    }

    return {iframePosition: layer.iframePosition, iframeIndexes: layer.iframeIndexes, minChatIframeCount: layer.minChatIframeCount};
}


function addFileinput(obj){
    var addBtn = $(obj), wrapper = addBtn.parent(), lastInput = wrapper.find("div.fileinput:last"), suffix = wrapper.data("suffix");
    var lastInputName = lastInput.find("input[type=file]").attr("name");
    var index = lastInputName.match(/-\d+-/g);
    if(index){
        var first = lastInputName.indexOf("-");
        var newInputName = lastInputName.substring(0, first);
        index = index[0];
        index = index.match(/\d+/g);
        index = parseInt(index) + 1;
        var newInput = lastInput.parent().clone();
        newInput.find("input[type=file]").attr("name", newInputName + "-" + index + "-" + suffix);
        newInput.find(".fileinput-filename").html("");
        newInput.find("div.fileinput-exists").removeClass("fileinput-exists").addClass("fileinput-new");
        wrapper.append(newInput);
    }
}

function deleteFileinput(obj){
    var deleteBtn = $(obj), wrapper = deleteBtn.parent(), otherFile = wrapper.siblings("div.fileinput-wrapper");
    if(otherFile.length){
        wrapper.remove();
    }
}

function deleteOldFile(obj){
    var deleteBtn = $(obj), fileRow = deleteBtn.parent(), wrapper = deleteBtn.closest(".fileinput-wrapper");
    fileRow.remove();
    if(wrapper.find(".file-row").length == 0){
        wrapper.find(".empty-selection").removeClass("hide");//显示占位div
    }
}

function openFileinputSelection(obj, allowLocalUpload){
    var fileWrapper = $(obj).closest(".fileinput-wrapper"), inputName = fileWrapper.data("name"), oldInputName = fileWrapper.data("oldName"), suffix=fileWrapper.data("suffix");
    displayInLayer("选择文件", {requestUrl: "/ckfinder/control/OpenFileinputSelection?allowLocalUpload=" + allowLocalUpload + "&inputName=" + inputName + "&externalLoginKey=" + $("input[name=externalLoginKey]").val(),  width: '950px',
        height: '600px',layer:{
        btn: ['添加'],
        yes: function(index, layero){
            var selectionIframe = layero.parent().find("#layui-layer-iframe" + index).contents(), hasInput = false;
            selectionIframe.find("input[type=checkbox]:checked").each(function(){
                var file = $(this),fileName = file.attr("fileName");
                fileWrapper.append('<div class="file-row"><input type="hidden" name="' + oldInputName + '" value="' + file.val() + '"/>'
                                    +'<i class="fa fa-remove font-red" style="cursor: pointer;margin-right:10px" onclick="deleteOldFile(this)"></i><a href="#nowhere" onclick="">' + fileName + '</a></div>');
            });
            fileWrapper.find("input[type=hidden]").each(function(index){
                $(this).attr("name", oldInputName + "-" + index + (suffix ? "-" + suffix : ""));
                hasInput = true;
            });
            selectionIframe.find(".fileinput.fileinput-exists").each(function(){
                var file = $(this).find("input[type=file]"), fileName = $(this).find(".fileinput-filename").html();
                fileWrapper.append('<div class="file-row">'
                                                    +'<i class="fa fa-remove font-red" style="cursor: pointer;margin-right:10px" onclick="deleteOldFile(this)"></i><a href="#nowhere" onclick="">' + fileName + '</a></div>');
                fileWrapper.find(".file-row").last().append(file);
            });
            fileWrapper.find("input[type=file]").each(function(index, obj){
                $(this).attr("name", inputName + "-" + index + (suffix ? "-" + suffix : ""));
                hasInput = true;
            });

            if(hasInput){
                fileWrapper.find(".empty-selection").addClass("hide");
            }
            closeCurrentTab();//如果设定了yes回调，需进行手工关闭
        }
    }});
}

function initDatatables(selector, options){
    $.fn.dataTable.ext.errMode = 'none';
    var element = $(selector);
    var settings = {

        // Internationalisation. For more info refer to http://datatables.net/manual/i18n
        "language": {
            "aria": {
                "sortAscending": ": activate to sort column ascending",
                "sortDescending": ": activate to sort column descending"
            },
            "emptyTable": "无记录",
            "info": "当前 _START_ - _END_ 总共 _TOTAL_ 条",
            "infoEmpty": "无记录",
            "infoFiltered": "",
            "zeroRecords": "无记录"
        },
        buttons: [
        ],

        // Or you can use remote translation file
        //"language": {
        //   url: '//cdn.datatables.net/plug-ins/3cfcc339e89/i18n/Portuguese.json'
        //},

        // setup buttons extension: http://datatables.net/extensions/buttons/

        // scroller extension: http://datatables.net/extensions/scroller/
        // scrollY:        300,
        deferRender:    true,
        scroller:       false,
        paging: true,
        stateSave:      false,
        "searching": false,
        "lengthChange": false,
        // set the initial value
        "pageLength": 10,
        "dom": "<'row' <'col-md-12'B>><'row'<'col-md-6 col-sm-12'l><'col-md-6 col-sm-12'f>r><'table-scrollable't><'row'<'col-md-5'i><'col-md-7'p>>"
    };
    $.extend(settings, options)
    return element.DataTable(settings);
}
function viewPdfInLayer(fileId){
    displayInLayer("文件预览", {
        requestUrl: "/content/control/FileHandler?dataResourceId=" + fileId + "&externalLoginKey=" + getExternalLoginKey(),
        height:'90%',respectHeight:true,layer: {maxmin: false, zIndex: 99999999,
            cancel: function(index, layero){
                var prepareLeave = layero.find("iframe")[0].contentWindow.prepareLeave;
                if(prepareLeave){
                    prepareLeave();
                }
            }
        }
    })
}

/**
 * xml 转换为json
 * @param xml
 * @returns {{}}
 */
function xmlToJson(xml) {
    // Create the return object
    var obj = {};
    if (xml.nodeType == 1) { // element
        // do attributes
        if (xml.attributes.length > 0) {
            obj["attributes"] = {};
            for (var j = 0; j < xml.attributes.length; j++) {
                var attribute = xml.attributes.item(j);
                obj["attributes"][attribute.nodeName] = attribute.nodeValue;
            }
        }
    } else if (xml.nodeType == 3) { // text
        obj = xml.nodeValue;
    }
    if (xml.hasChildNodes()) {
        for(var i = 0; i < xml.childNodes.length; i++) {
            var item = xml.childNodes.item(i);
            var nodeName = item.nodeName;
            if (typeof(obj[nodeName]) == "undefined") {
                obj[nodeName] = xmlToJson(item);
            } else {
                if (typeof(obj[nodeName].length) == "undefined") {
                    var old = obj[nodeName];
                    obj[nodeName] = [];
                    obj[nodeName].push(old);
                }
                obj[nodeName].push(xmlToJson(item));
            }
        }
    }
    return obj;
};

function searchScore(id){
    displayInLayer('文件下载', {requestUrl: 'searchScoreByUserId',data:{libraryId:id}, height: '200px', width: '400px'})
}

/**
 * detect IE
 * returns version of IE or false, if browser is not Internet Explorer
 */
function detectIE() {
    var ua = window.navigator.userAgent;

    // Test values; Uncomment to check result …

    // IE 10
    // ua = 'Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)';

    // IE 11
    // ua = 'Mozilla/5.0 (Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko';

    // Edge 12 (Spartan)
    // ua = 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36 Edge/12.0';

    // Edge 13
    // ua = 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586';

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
        // Edge (IE 12+) => return version number
        return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return false;
}

function removeFileUpload(fileId, callback) {
    layer.msg('确认删除该附件吗？', {
        time: 0 //不自动关闭
        ,btn: ['确认', '取消']
        ,yes: function(index){
            layer.close(index);
            jQuery.ajax({
                type: 'POST',
                url: "/content/control/removeUpload",
                data: {fileId: fileId, externalLoginKey: getExternalLoginKey()},
                async: true,
                dataType: 'json',
                success: function (data) {
                    if(data.data.status){
                        showInfo(data.data.message);
                    }else{
                        showError(data.data.message);
                    }
                    if(callback){
                        callback.call(this, data.data);
                    }
                }
            })
        }
    });
}

function afterFileUploadRemoved(data){
    var fileItem = $("div[fileId=" + data.fileId + "]");
    var uploadifyFileId = fileItem.attr("id"), instanceId = fileItem.attr("instanceId");
    $('#' + instanceId).uploadify('cancel', uploadifyFileId, true);
    var fileIdField = $("#fileIdField-" + instanceId.substring(instanceId.indexOf("-") + 1));
    var uploadFileIdArray = fileIdField.data("uploadFileIdArray");
    uploadFileIdArray.splice( $.inArray(data.fileId, uploadFileIdArray), 1 );
    fileIdField.data("uploadFileIdArray", uploadFileIdArray);
    fileIdField.val(uploadFileIdArray.join(","));
    fileItem.remove();
}