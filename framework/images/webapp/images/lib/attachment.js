(function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD. Register as anonymous module.
        define(['jquery'], factory);
    } else if (typeof exports === 'object') {
        // Node / CommonJS
        factory(require('jquery'));
    } else {
        // Browser globals.
        factory(jQuery);
    }
})(function ($) {

    'use strict';
    function getUniqId(){
        return '_' + Math.random().toString(36).substr(2, 9);
    }

    function indexOf(value, array) {
        var i = 0, l = array.length;
        for (; i < l; i = i + 1) {
            if (equal(value, array[i])) return i;
        }
        return -1;
    }

    /**
     * Compares equality of a and b
     * @param a
     * @param b
     */
    function equal(a, b) {
        if (a === b) return true;
        if (a === undefined || b === undefined) return false;
        if (a === null || b === null) return false;
        // Check whether 'a' or 'b' is a string (primitive or object).
        // The concatenation of an empty string (+'') converts its argument to a string's primitive.
        if (a.constructor === String) return a + '' === b + ''; // a+'' - in case 'a' is a String object
        if (b.constructor === String) return b + '' === a + ''; // b+'' - in case 'b' is a String object
        return false;
    }

    var Attachment, cache={};

    /**
     * Creates a new class
     *
     * @param superClass
     * @param methods
     */
    function clazz(SuperClass, methods) {
        var constructor = function () {
        };
        constructor.prototype = new SuperClass;
        constructor.prototype.constructor = constructor;
        constructor.prototype.parent = SuperClass.prototype;
        constructor.prototype = $.extend(constructor.prototype, methods);
        return constructor;
    }

    Attachment = clazz(Object, {
        init: function (opts) {
            var $element = opts.element, self = this;
            this.element = opts.element;
            if (!$element.is("input[type=text]")) {
                console.log("仅支持input[text]标签")
                return;
            }
            // if ($element.data("attachment")) {
            //     return;//已实现初始化
            // }
            var url = opts.url || $element.data("oriUrl") || 'FineUploader';

            if (url) {
                if (url.indexOf("?") == -1) {
                    url += "?"
                }
                $element.data("oriUrl", url);
            }
            $element.data("attachment", self);
            $element.data("oriSize", {width: $element.width(), height: $element.height()});
            $element.css({visibility: 'hidden', height: 0, width:0, padding: 0});
            var eleOpt = {
                showLink: true,
                readonly: false,
                showTip: false,
                simple: true,
                download: true,
                showThumbnail: false,
                removeImmediately:true
            };
            this.eleOpt = eleOpt;
            var oriId = ($element.attr("id") || $element.data("id") || opts.id || getUniqId());
            $.extend(eleOpt, opts, {
                eleId: "attachment_" + oriId,
                name: $element.attr("name") || $element.data("name") || opts.name || "",
                url: url,
                showLink: $element.data("show-link") || opts.showLink,
                icon: $element.data("icon") || opts.icon || "",
                wrapperClass: $element.data("wrapper-class") || opts.wrapperClass || "btn btn-success",
                btnStyle: $element.data("btn-style") || opts.btnStyle || "",
                btnClass: $element.data("btn-class") || opts.btnClass || "btn green",
                btnTitle: $element.data("btn-title") || opts.btnTitle || "上传",
                validateClass: $element.data("validate-class") || opts.validateClass || "",
                extension: $element.data("extension") || opts.extension || "",
                readonly: $element.data("readonly") || opts.readonly,
                showTip: $element.data("show-tip") || opts.showTip,
                simple: $element.data("simple") || opts.simple,
                download: $element.data("download") || opts.download,
                showThumbnail: $element.data("show-thumbnail") || opts.showThumbnail,
                thumbnailWidth: $element.data("thumbnail-width") || opts.thumbnailWidth || 100,
                thumbnailHeight: $element.data("thumbnail-height") || opts.thumbnailHeight || 100,
                dropZones: $element.data("drop-zones") || opts.dropZones || "",
                sizeLimit: $element.data("size-limit") || opts.sizeLimit || 10485760,
                compressImgWidth: $element.data("compress-img-width") || opts.compressImgWidth || 10485760,
                numberLimit: $element.data("number-limit") || opts.numberLimit || 0,
                removeImmediately: $element.data("remove-immediately") || opts.removeImmediately,
                value: $element.val()|| opts.value || "",
                defaultValue: $element.data("value") || "",
                onComplete: $element.data("onComplete") || opts.onComplete || ""
            });
            if(eleOpt.showTip && !eleOpt.readonly){
                $element.before('<span class="fa fa-question tipl" style="float: right" title="可拖拽文件至上传图标' + (eleOpt.extension ? '；支持格式('+ eleOpt.extension + ')' : '') + '"></span>');
            }
            $element.before('<span id="' + eleOpt.eleId +'" style="' + eleOpt.btnStyle + '" class="uploader-button ' + eleOpt.wrapperClass + (eleOpt.simple ? ' simple' : '') + '" title="' + (eleOpt.simple ? '上传' : '') +'">' + (eleOpt.btnTitle ?  eleOpt.btnTitle : '上传') + '</span>')
            $element.addClass(eleOpt.validateClass);
            $element.attr("id", eleOpt.eleId + "_value");
            $element.data("oriId", oriId);

            var uploadSetting = {
                id: oriId,
                endpoint: eleOpt.url,
                readonly: eleOpt.readonly,
                download: eleOpt.download
                ,editPermission :''
                ,deletePermission :''
                ,downloadPermission :'',
                mini: eleOpt.simple && !eleOpt.btnClass,
                compressImgWidth: eleOpt.compressImgWidth,
                sizeLimit: eleOpt.sizeLimit,
                uploadButton: eleOpt.simple ? '' : eleOpt.btnTitle,
                dropZones: eleOpt.dropZones,
                numberLimit: eleOpt.numberLimit,
                showLink: eleOpt.showLink,
                removeImmediately: eleOpt.removeImmediately,
                showThumbnail: eleOpt.showThumbnail,
                thumbnailWidth: eleOpt.thumbnailWidth,
                thumbnailHeight: eleOpt.thumbnailHeight,
                btnClass: eleOpt.btnClass,
                icon: eleOpt.icon,
                onComplete: eleOpt.onComplete
            };
            $.extend(uploadSetting, {fineUploader: eleOpt.fineUploader || {}});
            if(eleOpt.extension){
                $.extend(uploadSetting, {allowedExtensions: eleOpt.extension.replace(/\s+/g, '').split(",")})
            }
            if(eleOpt.defaultValue){
                uploadSetting["attachments"] = eleOpt.defaultValue;
            }
            init_upload(uploadSetting);
        },
        val: function(){
            return this.element.val();
        },
        destroy: function(){
            this.element.removeData("attachment");
            this.element.siblings("span.uploader-button").remove();
            var oriSize = this.element.data("oriSize");
            this.element.css({visibility: 'hidden', width: oriSize.width, height: oriSize.height});
            this.element.attr("id", this.element.data("oriId"));
        },
        instantce: function(){
            return this.element.data("fineUploaderInstance");
        }
    });
    var uploadLoadingIndex = {}, backUploading;
    function init_upload(options){
        var eleId = options.id, allowedExtensions = options.allowedExtensions || [];
        if(!options.readonly){
            var setting = {
                mini:options.mini,
                debug:false,
                button: $("#attachment_" + eleId)[0],
                request: {
                    endpoint:options.endpoint,
                    forceMultipart:true,
                    customHeaders:{'Accept' : 'text/plain, application/json, text/javascript, */*; q=0.01'},
                    params:{'CSRFToken' : getCSRFToken(), 'compressImgWidth' : options.compressImgWidth, 'editPermission' : options.editPermission, 'deletePermission' : options.deletePermission, 'downloadPermission' : options.downloadPermission}
                },
                validation: {
                    stopOnFirstInvalidFile: false,
                    allowedExtensions: allowedExtensions,
                    sizeLimit: options.sizeLimit
                },
                text: {
                    uploadButton: options.uploadButton,
                    cancelButton: '取消',
                    failUpload:'上传失败',
                    dragZone: ''
                },

                failedUploadTextDisplay:{
                    mode:'none'
                },
                callbacks: {
                    onComplete: function(id, fileName, responseJSON, request) {
                        $("#attachment_" + eleId + " ul.qq-upload-list").remove();
                        if(options.fineUploader.onTruncateComplete && "function" == typeof(options.fineUploader.onTruncateComplete)){
                            options.fineUploader.onTruncateComplete(responseJSON);//上传后立即由调用者处理结果
                        }else{
                            var valueField = $("#attachment_" + eleId + "_value"), oldValue = valueField.val();
                            valueField.val( (oldValue ? oldValue + "," : "") + responseJSON.fileId);
                            showInfo("上传成功");
                            if(options.showLink){
                                createUploadItem(options, responseJSON.fileId, fileName);
                            }
                            if(options.onComplete){
                                var callType = typeof(options.onComplete);
                                if(callType == "function"){
                                    options.onComplete(responseJSON);
                                }else if(callType == "string" && window[options.onComplete]){
                                    window[options.onComplete].call(window, responseJSON);
                                }
                            }
                        }
                    },
                    onValidate: function(file, button){
                        if(options.fineUploader.onValidate){
                            return options.fineUploader.onValidate(file, button);
                        }
                        return true;
                    },
                    onValidateBatch:function(files, button){
                        if(options.fineUploader.onValidateBatch){
                            options.fineUploader.onValidateBatch(file, button);
                        }else{
                            if(files.length){
                                var failedNames = [], invalidNames = [], sizeLimit = options.sizeLimit;
                                for(var i in files){
                                    var fileSize = files[i].size, fileName = files[i].name, fileNameInfo = fileName.split("."), ext = fileNameInfo[fileNameInfo.length - 1];
                                    if(allowedExtensions.length && allowedExtensions.indexOf(ext) < 0){
                                        invalidNames.push(fileName);
                                    }else if(fileSize > sizeLimit){
                                        failedNames.push(fileName);
                                    }
                                }
                                var errorMsg = "";
                                if(invalidNames.length){
                                    errorMsg += "【" + invalidNames.join("<br/>") + "】<br/><span style='color:red;'>文件格式不支持</span>";
                                }
                                if(failedNames.length){
                                    errorMsg += "【" + failedNames.join("<br/>") + "】<br/><span style='color:red;'>文件大小超限未上传</span>";
                                }
                                if(errorMsg){
                                    showErrorSticky(errorMsg);
                                }

                                if(files.length > (failedNames.length + invalidNames.length)){
                                    backUploading = getLayer().load(1, {
                                        shade: [0.1,'#fff']
                                    });
                                }
                            }
                        }
                    },
                    onAllComplete: function(succeeded, failed){
                        if(backUploading){
                            getLayer().close(backUploading);
                        }
                        if(options.fineUploader.onAllComplete){
                            options.fineUploader.onAllComplete(succeeded, failed);
                        }else{
                            if(succeeded.length) {
                                showInfo("上传成功");
                            }
                        }
                    }
                },
                messages: {
                    typeError: "不支持的文件格式",
                    sizeError: "{file} 文件超过上传限制 {sizeLimit}",
                    onLeave: "文件正在上传, 此时离开将取消上传."
                }
            };

            $.extend(setting, options.fineUploader);

            if(options.dropZones){
                setting.dragAndDrop = {
                    extraDropzones:$(options.dropZones).get()
                }
            }

            if(options.numberLimit){
                setting.callbacks.onSubmit = function(id, fileName){
                    var oldValue = $("#attachment_" + options.id + "_value").val();
                    if(!oldValue){
                        return true;
                    }else{
                        oldValue = oldValue.split(",");
                        if(oldValue.length == options.numberLimit){//达到上限
                            showError("最多允许上传" + options.numberLimit + "个附件.");
                            return false;
                        }
                    }
                }
            }
            var fineUploaderInstance = new qq.FineUploaderBasic(setting);
            // fineUploaderInstance.setEndpoint(options.endpoint);
            $("#attachment_" + eleId).data("fineUploaderInstance", fineUploaderInstance);
            if(options.btnClass){
                $("#attachment_" + eleId + " .btn").addClass(options.btnClass);
            }else if(options.icon){
                $("#attachment_" + eleId + " .btn.btn-mini").attr("class", options.icon).css({"background-color": "transparent", "border" : 0});
                var $uploadIcon = $("#attachment_" + eleId + " ." + options.icon + " .icon-upload");
                $uploadIcon.attr("style", $uploadIcon.attr("style") + ";background-image:none!important");
            }
        }

        if(options.attachments){
            var attachments = options.attachments;
            for(var i in attachments){
                var attachment = attachments[i];
                if(options.showLink){
                    createUploadItem(options, attachment.dataResourceId, attachment.dataResourceName);
                }
            }

        }
    }

    function createUploadItem(options, fileId, fileName){
        var attStr = '', downloadStr = "";
        var fileType = fileName.substr(fileName.lastIndexOf('.')+1,fileName.length);
        var removeStr = '<span class="tipl fa fa-remove ' + fileId + '" onclick="remove_upload(\'' + options.id + '\',\'' + fileId + '\',\'' + fileName + '\')" title="删除"></span>';
        if(options.showThumbnail){
            attStr = '<div class="attachment-thumbnail ' + fileId + '">';
            if("png,jpg,jpeg,gif,bmp".indexOf(fileType.toLowerCase()) > -1){
                attStr += '<a class="attachment-image ' + fileId + '" title="' + fileName + '" href="#here" ><img class="thumbnail_image" src="/oa/control/imageView?fileName=' + fileId + '" style="width: ' + options.thumbnailWidth + 'px;height: ' + options.thumbnailHeight + 'px"></a>';
            }else {
                attStr += '<a class="attachment-image ' + fileId + '"  title="' + fileName + '" href="#here"><img class="thumbnail_image" src="/css/themes/virgo/img/files/' + fileType.toUpperCase() + '.png" style="width: ' + options.thumbnailWidth + 'px;height: ' + options.thumbnailHeight + 'px"><div style="text-align:center">' + (fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName) + '.' + fileType + '</div></a>';
            }
            attStr += removeStr + "</div>";
        }else{
            if("png,jpg,jpeg,gif,bmp".indexOf(fileType.toLowerCase()) > -1) {
                attStr = '<a class="attachment-image ' + fileId + '" title="' + fileName + '" style="color: #55a3e5;margin-left:5px" href="#here">' + (fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName) + '</a>';
            }else{
                attStr = '<a class="tipl ' + fileId + '" title="' + fileName + '" style="color: #55a3e5;margin-left:5px" href="#here">' + (fileName.length > 10 ? fileName.substring(0, 10) + '...' : fileName) + '</a>';
            }
            attStr += removeStr;
        }
        $("#attachment_" + options.id).after("<div class='attach-wrapper'>" + attStr + "</div>");
    }

    function get_upload(eleId, id){
        return $("#attachment_" + eleId + "_value").val();
    }

    window.remove_upload = function(eleId, id,name) {
        var confirmIndex = layer.confirm("确定删除吗?", {
            btn: ['确定', '取消']
        }, function () {
            $("." + id).parent().remove();
            var oldValue = $("#attachment_" + eleId + "_value").val();
            if (!oldValue) {
                oldValue = "";
            }
            oldValue = oldValue.split(",");
            var newValue = "";
            for (var i in oldValue) {
                var value = oldValue[i];
                if (value && value != id) {
                    newValue += value + ",";
                }
            }
            if (newValue.length > 0) {
                newValue = newValue.substring(0, newValue.length - 1);
            }
            $("#attachment_" + eleId + "_value").val(newValue);
            layer.close(confirmIndex);
        });
    }

    $.fn.attachment = function () {
        var args = Array.prototype.slice.call(arguments, 0)
            , allowedMethods = ["val", "destroy", "instance"];
        var opts = {};

        // initialize every element
        this.each(function () {
            if (args.length === 0 || typeof(args[0]) === "object") {
                opts = args.length === 0 ? {} : $.extend({}, args[0]);
                opts.element = $(this);
                new Attachment().init(opts);
            } else if (typeof(args[0]) === "string") {

                if (indexOf(args[0], allowedMethods) < 0) {
                    throw "Unknown method: " + args[0];
                }
                var attachment = $(this).data("attachment");
                if (attachment) {
                    attachment[args[0]].apply(attachment, args.slice(1));
                }
            }
        });

        return this;
    };

});