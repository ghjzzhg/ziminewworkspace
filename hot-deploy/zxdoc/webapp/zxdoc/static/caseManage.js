$.caseManage = {
    createCaseTemplate: function () {
        var fileName = $("input[name='uploadedFile']").val();
        if(fileName != null && fileName != '') {
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
        }
        var options = {
            beforeSubmit: function () {
                return $("#caseTemplateForm").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                var id = $(data).find("#caseId").html();
                location.href = "EditCaseTemplateBaseTime?iframe=true&templateId=" + id;
            },
            url: "createCaseTemplate",
            type: 'post'
        };
        $("#caseTemplateForm").ajaxSubmit(options);
    },
    updateCaseTemplate: function () {
        // var newFormData = $("#caseTemplateForm").serialize();//比较表单数据，有变化时才发出更新请求
        var fileName = $("input[name='uploadedFile']").val();
        if(fileName != null && fileName != '') {
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
        }
        //改动1：由原来的文件改动才会保存改为一直提交
        //改动2：现在可以改动参与方，如果参与方被改动，则修改参与方
        var options = {
            beforeSubmit: function () {
                return $("#caseTemplateForm").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                var id = $(data).find("#caseId").html();
                location.href = "EditCaseTemplateBaseTime?iframe=true&templateId=" + $("#templateId").val();
            },
            url: "updateCaseTemplate",
            type: 'post'
        };
        $("#caseTemplateForm").ajaxSubmit(options);
    },
    editCaseTemplate: function (id,flag) {
        displayInLayer('修改模板', {
            requestUrl: 'EditCaseTemplate?templateId=' + id, height: '80%', data:{privateFlag:flag}, layer:{end: function(){
                if(flag == "true"){
                    var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                    parent.layer.close(index);
                }else{
                    templateTable.ajax.reload();
                }
            }}});
    },
    deleteCaseTemplate: function (id,flag) {
        if (confirm("确定删除该项吗?")) {
            $.ajax({
                type: 'POST',
                url: "deleteCaseTemplate",
                async: false,
                dataType: 'json',
                data: {id: id},
                success: function (data) {
                    if(flag == "true"){
                        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
                        parent.layer.close(index);
                    }else {
                        showInfo("删除成功");
                        templateTable.ajax.reload();
                    }
                }
            });
        }
    },
    loseEfficacy: function (id,flag) {
        var msg = '确定使该项失效吗?';
        if(flag){
            msg = '确定使该项生效吗?'
        }
        if (confirm(msg)) {
            $.ajax({
                type: 'POST',
                url: "loseEfficacy",
                async: false,
                dataType: 'json',
                data: {id: id, flag: flag},
                success: function (data) {
                    showInfo("操作成功");
                    templateTable.ajax.reload();
                }
            });
        }
    },
    saveCaseTemplateNodes: function (timeFlag) {
        // var newFormData = $("#templateNodesForm").serialize();//比较表单数据，有变化时才发出更新请求
        /*var fileFlag = false;
        $("input[name^='atta_']").each(function () {
            var fileName = $(this).val();
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                fileFlag = true;
                return;
            }
        })
        if(fileFlag){
            showError("文件名称长度请小于50个字符");
            return;
        }*/
        if($("#templateNodesForm").validationEngine("validate")){
            $("select[name^='dueBase_']").each(function () {
                var value = $(this).val();
                var name = $(this).attr("name");
                var index = name.substring(name.lastIndexOf("dueBase_") + "dueBase_".length, name.length)
                if(value != null && value.indexOf("BT") >= 0){
                    var s = $("#addAnSub_" + index).val();
                    if(s == 2){
                        $("#dueDay_" + index).val("-" + $("#dueDay_" + index).val());
                    }
                }
            })
        }else{
            return;
        }
        // if (newFormData != formData) {
            var options = {
                dataType: "html",
                success: function (data) {
                    if(timeFlag){
                        location.href = "EditCaseTemplateNodesTime?iframe=true&templateId=" + $("#templateId").val();
                    }else{
                        location.href = "EditCaseFolders?iframe=true&templateId=" + $("#templateId").val();
                    }

                },
                url: "saveCaseTemplateNodes",
                type: 'post'
            };
            $("#templateNodesForm").ajaxSubmit(options);
        // } else {
        //     location.href = "EditCaseFolders?iframe=true&templateId=" + $("#templateId").val();
        // }
    },
    saveCaseTemplateNodeGroups: function () {
        if(!$("#templateNodesForm").validationEngine("validate")){
            return;//避免未录入任何数据直接下一步
        }
        var newFormData = $("#templateNodesForm").serialize();//比较表单数据，有变化时才发出更新请求
        if (newFormData != formData) {
            var options = {
                dataType: "json",
                success: function (data) {
                    location.href = "EditCaseTemplateNodes?iframe=true&templateId=" + $("#templateId").val();
                },
                url: "saveCaseTemplateNodeGroups",
                type: 'post'
            };
            $("#templateNodesForm").ajaxSubmit(options);
        } else {
            location.href = "EditCaseTemplateNodes?iframe=true&templateId=" + $("#templateId").val();
        }
    },
    saveCaseTemplateBaseTime: function () {
        if(!$("#templateNodesForm").validationEngine("validate")){
            return;
        }
        var timeNames = [], timeNameCheck = true;
        $("input[name^=name]").each(function(){
            var nameValue = $(this).val();
            if(nameValue){
                if(timeNames.indexOf(nameValue) > -1){
                    showError("基准时间名称不允许重复");
                    timeNameCheck = false;
                    return false;
                }else{
                    timeNames.push(nameValue);
                }
            }
        });
        if(!timeNameCheck){
            return;
        }
        var newFormData = $("#templateNodesForm").serialize();//比较表单数据，有变化时才发出更新请求
        if (newFormData != formData) {
            var options = {
                dataType: "json",
                success: function (data) {
                    location.href = "EditCaseTemplateNodeGroups?iframe=true&templateId=" + $("#templateId").val();
                },
                url: "saveCaseTemplateBaseTime",
                type: 'post'
            };
            $("#templateNodesForm").ajaxSubmit(options);
        } else {
            location.href = "EditCaseTemplateNodeGroups?iframe=true&templateId=" + $("#templateId").val();
        }
    },
    caseBack: function (type) {
        var caseid = $("#caseId").val()
        var caseName = $("#caseName").val()
        //任务
        if(type == 0){
            location.href = "EditCaseTemplateNodesTime?iframe=true&isFromCase=true&caseName=" + caseName + "&caseId=" + caseid;
        }else if(type == 1){
            location.href = "EditCaseTemplateNodes?iframe=true&isFromCase=true&caseName=" + caseName + "&caseId=" + caseid;
        }else if(type == 2){ //流程
            location.href = "EditCaseTemplateNodeGroups?iframe=true&isFromCase=false&caseId=" + caseid;
        }else if(type == 3){ //基本信息
            location.href = "UpdateCaseBaseInfo?iframe=true&isFromCase=false&caseId=" + caseid;
        }else if(type == 4){ //临时CASE基本信息
            location.href = "UpdateBlankCaseBaseInfo?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
        }else if(type == 5){ //步骤
            location.href = "EditCaseTemplateNodeGroups?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
        }else if(type == 6){ //任务
            location.href = "EditCaseTemplateNodes?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
        }else if(type == 7){ //期限
            location.href = "EditCaseTemplateNodesTime?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
        }
    },
    caseTemplateBack: function (type) {
        var templateId = $("#templateId").val()
        //时间
        if(type == 0){
            location.href = "EditCaseTemplateNodesTime?iframe=true&isFromCase=false&templateId=" + templateId;
        }else if(type == 1){ //任务
            location.href = "EditCaseTemplateNodes?iframe=true&isFromCase=false&templateId=" + templateId;
        }else if(type == 2){ //流程
            location.href = "EditCaseTemplateNodeGroups?iframe=true&isFromCase=false&templateId=" + templateId;
        }else if(type == 3){ //基准时间
            location.href = "EditCaseTemplateBaseTime?iframe=true&isFromCase=false&templateId=" + templateId;
        }else if(type == 4){ //基本信息
            location.href = "EditCaseTemplate?iframe=true&isFromCase=false&templateId=" + templateId;
        }
    },
    saveCaseProgressGroup: function () {
        if(!$("#templateNodesForm").validationEngine("validate")){
            return;//避免未录入任何数据直接下一步
        }
        var newFormData = $("#templateNodesForm").serialize();//比较表单数据，有变化时才发出更新请求
        if (newFormData != formData) {
            var options = {
                dataType: "json",
                type: "post",
                url: "saveCaseProgressGroup",
                success: function (data) {
                    location.href = "EditCaseTemplateNodes?iframe=true&isFromCase=true&caseName=" + data.caseName + "&caseId=" + $("#caseId").val();
                }
            };
            $("#templateNodesForm").ajaxSubmit(options);
        }else{
            location.href = "EditCaseTemplateNodes?iframe=true&isFromCase=true&caseName=" + $("#caseName").val() + "&caseId=" + $("#caseId").val();
        }
    },
    saveBlankCaseProgressGroup: function () {
        if(!$("#templateNodesForm").validationEngine("validate")){
            return;//避免未录入任何数据直接下一步
        }
        var newFormData = $("#templateNodesForm").serialize();//比较表单数据，有变化时才发出更新请求
        if (newFormData != formData) {
            var options = {
                dataType: "json",
                type: "post",
                url: "saveBlankCaseProgressGroup",
                success: function (data) {
                    location.href = "EditCaseTemplateNodes?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
                }
            };
            $("#templateNodesForm").ajaxSubmit(options);
        }else{
            location.href = "EditCaseTemplateNodes?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
        }
    },
    saveCaseFolders: function () {
        var options = {
            beforeSubmit: function () {
                return $("#templateFoldersForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功");
                closeCurrentTab();
            },
            url: "saveCaseFolders",
            type: 'post'
        };
        $("#templateFoldersForm").ajaxSubmit(options);
    },
    saveBlankCaseFolders: function () {
        var options = {
            beforeSubmit: function () {
                return $("#templateFoldersForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功");
                closeCurrentTab();
            },
            url: "saveBlankCaseFolders",
            type: 'post'
        };
        $("#templateFoldersForm").ajaxSubmit(options);
    },
    saveCaseProgress: function (caseId, configNodeTime) {
        var fileFlag = false;
        $("input[name^='atta_']").each(function () {
            var fileName = $(this).val();
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                fileFlag = true;
                return;
            }
        })
        if(fileFlag){
            showError("文件名称长度请小于50个字符");
            return;
        }
        var options = {
            beforeSubmit: function () {
                return $("#templateNodesForm").validationEngine("validate");
            },
            dataType: "html",
            success: function (content) {
                var msg = $(content).find("#msg").html();
                if(configNodeTime) {
                    location.href = "EditCaseNodesTime?iframe=true&isFromCase=true&caseName=" + $("#caseName").val() + "&caseId=" + $("#caseId").val();
                }else if(msg=="ok") {
                    location.href = "EditCaseFolders?iframe=true&caseId=" + caseId;
                }else {
                    closeCurrentTab();
                }
            },
            url: "SaveCaseProgress?caseId=" + caseId,
            type: "post"
        };
        $("#templateNodesForm").ajaxSubmit(options);
    },
    saveBlankCaseProgress: function (configNodeTime) {
        var fileFlag = false;
        $("input[name^='atta_']").each(function () {
            var fileName = $(this).val();
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                fileFlag = true;
                return;
            }
        })
        if(fileFlag){
            showError("文件名称长度请小于50个字符");
            return;
        }
        var options = {
            beforeSubmit: function () {
                return $("#templateNodesForm").validationEngine("validate");
            },
            dataType: "html",
            success: function (content) {
                var msg = $(content).find("#msg").html();
                if(configNodeTime) {
                    location.href = "EditCaseNodesTime?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
                }else if(msg=="ok") {
                    location.href = "EditBlankCaseFolders?iframe=true&blankCaseSessionKey=" + $("#blankCaseSessionKey").val();
                }else {
                    closeCurrentTab();
                }

            },
            url: "SaveBlankCaseProgress",
            type: "post"
        };
        $("#templateNodesForm").ajaxSubmit(options);
    },
    saveCaseInfo: function () {
        var options = {
            beforeSubmit: function () {
                return $("#caseTemplateForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {

            },
            url: "SaveCase",
            type: "post"
        };
        $("#caseTemplateForm").ajaxSubmit(options);
    },
    completeCaseStep: function (progressId, isSkipped) {
        var msg = "是否确认完成？"
        if(isSkipped == "true"){
            msg = "是否确认忽略？";
        }
        if(confirm(msg)) {
            var description = "";
            if (!isSkipped) {
                description = $("#description").val();
            }
            $.ajax({
                url: "SaveCaseStep",
                type: "POST",
                data: {"progressId": progressId, "isSkipped": isSkipped, "description": description},
                dataType: "json",
                success: function (content) {
                    if (isSkipped) {
                        getProgress();
                    } else {
                        closeCurrentTab();
                    }
                }
            });
        }
    },
getProgress: function (partyId, caseId) {
        $.ajax({
            url: "CaseProgress",
            type: "POST",
            data: {"caseId": caseId},
            dataType: "json",
            success: function (content) {
                var caseProgresses = content.caseProgresses;
                var stepHtml = "";
                //遍历大步骤
                if(caseProgresses){
                    $(caseProgresses).each(function () {
                        var childStepHtml = "";
                        var templateFileLinks = "";
                        var isPreComplete = this.isPreDone;
                        var childStepList = this.childProgressList;
                        var stepTitle = this.name;
                        var isStepDone = this.isStepDone;
                        //遍历子步骤
                        if(childStepList){
                            var childStepAmount = childStepList.length;
                            $(childStepList).each(function () {
                                var isComplete = false;
                                var isSkipped = false;
                                //遍历文件
                                var templateFileLinks = "";
                                var completedFileLinks = "";
                                $(this.progressTemplateFileList).each(function () {
                                    templateFileLinks += Mustache.render($("#fileDownloadLink").html(), {
                                            fileName: this.dataResourceName,
                                            //TODO 后台传当前网站的URL
                                            fileURL:"javascript:viewPdfInLayer('"+ this.dataResourceId +"')"
                                        }) + '<a style="text-decoration:none" href="/content/control/downloadUploadFile?dataResourceId=' + this.dataResourceId + '&externalLoginKey=' + getExternalLoginKey() + '" title="下载"><i class="fa fa-download"></i> </a>,'
                                });
                                $(this.progressCompletedFileList).each(function () {
                                    completedFileLinks += Mustache.render($("#fileDownloadLink").html(), {
                                            fileName: this.dataResourceName,
                                            //TODO 后台传当前网站的URL
                                            fileURL:"javascript:viewPdfInLayer('" + this.dataResourceId + "')"
                                        }) + '<a style="text-decoration:none" href="/content/control/downloadUploadFile?dataResourceId=' + this.dataResourceId + '&externalLoginKey=' + getExternalLoginKey() + '" title="下载"><i class="fa fa-download"></i> </a>,'
                                });
                                templateFileLinks = templateFileLinks.substring(0, templateFileLinks.length - 1);
                                completedFileLinks = completedFileLinks.substring(0, completedFileLinks.length - 1);
                                var filesTable = Mustache.render($("#filesTable").html(), {
                                    templateFileLinks: templateFileLinks,
                                    completedFileLinks: completedFileLinks,
                                    templateFileClass: templateFileLinks ? '' : 'hide',
                                    completeFileClass: completedFileLinks ? '' : 'hide'
                                });
                                if (templateFileLinks == "" && completedFileLinks == "") {
                                    filesTable = "";
                                }
                                if (this.skipped) {
                                    isSkipped = true;
                                }
                                if (this.completeTime || isSkipped) {
                                    isComplete = true;
                                }
                                var isDoneClass = "";
                                var progressTitleClass = "";
                                if (isSkipped) {
                                    progressTitleClass = "timeline-body-alerttitle font-red-intense skipped";
                                    isDoneClass = "timeline-item skipped done";
                                } else if (this.completeTime) {
                                    progressTitleClass = "timeline-body-title font-blue-madison";
                                    isDoneClass = "timeline-item done";
                                } else {
                                    progressTitleClass = "timeline-body-alerttitle font-red-intense";
                                    isDoneClass = "timeline-item";
                                }
                                childStepHtml += Mustache.render($("#caseProgressChildStepAtom").html(), {
                                    isDoneClass: isDoneClass,
                                    displayNotice: !isComplete ? "inline" : "none",
                                    displayBtn: !isComplete && this.personId == partyId && isPreComplete ? "inline" : "none",
                                    partyId:this.personId,
                                    progressTitleClass: progressTitleClass,
                                    progressTitle: this.title,
                                    progressId: this.id,
                                    groupName: this.fullName,
                                    progressTime: isComplete ? (!this.skipped ? "完成时间: " + this.completeTime : "已忽略该步骤") : "期限: " + (this.dueTime ? this.dueTime.split(" ")[0] : ""),
                                    completeDesc: this.completeDesc,
                                    filesTable: filesTable,
                                    childStepDoneClass: isComplete ? "mt-list-item done" : "mt-list-item"
                                    //TODO 头像
                                })
                            });
                        }
                        stepHtml += Mustache.render($("#caseProgressAtom").html(), {
                            stepDoneClass: isPreComplete && !isStepDone ? "list-toggle active uppercase" : (isStepDone ? "list-toggle done uppercase" : "list-toggle uppercase"),
                            stepTitle: stepTitle,
                            divId: "completed-simple" + this.seq,
                            divIdChooser: "#completed-simple" + this.seq,
                            childStepAmount: childStepAmount,
                            caseProgressChildStepAtom: childStepHtml,
                            isDoing: isPreComplete && !isStepDone ? "in" : ""
                        })
                    });
                }
                $("#caseProgress").html(stepHtml);
                $('#caseProgress [data-hover="dropdown"]').dropdownHover();
                parent.adjustContentFrame();
            }
        })
    },
    uploadCompletedCaseFile: function (filePathList, fileSharePartyId, progressId) {
        var shareFolders = encodeURIComponent(filePathList);
        displayInLayer("发布文档", {
            requestUrl: "/ckfinder/control/OpenFileinputSelection?allowLocalUpload=false&fileSharePartyId=" + fileSharePartyId + "&filePathList=" + shareFolders + "&externalLoginKey=" + getExternalLoginKey(),
            width: '950px',
            height: '600px',
            layer: {
                btn: ['上传'],
                success: function () {

                },
                end: function () {
                    getProgress();
                },
                yes: function (index, layero) {
                    var selectionIframe = layero.parent().find("#layui-layer-iframe" + index).contents(), fileIds = [], fileNames = [];
                    selectionIframe.find("input[type=checkbox]:checked").each(function () {
                        var file = $(this), fileName = file.attr("fileName");
                        var fileValue = file.val();
                        if(fileValue != null && fileValue != "" && fileValue != "on"){
                            fileIds.push(file.val());
                            fileNames.push(fileName);
                        }
                    });
                    if (fileIds.length) {
                        $.ajax({
                            type: 'POST',
                            url: "addCaseCompletedFiles",
                            async: false,
                            dataType: 'json',
                            data: {progressId: progressId, dataResourceIds: fileIds.join(",")},
                            success: function (data) {
                                showInfo(data.msg);
                                closeCurrentTab();//如果设定了yes回调，需进行手工关闭
                            }
                        });
                    } else {
                        parent.layer.msg("请勾选要上传的文件")
                    }
                    selectionIframe.find(".fileinput.fileinput-exists").each(function () {
                        var file = $(this).find("input[type=file]"), fileName = $(this).find(".fileinput-filename").html();
                        //TODO:临时上传的文件保存后再发消息
                    });
                    //TODO:将文件放到文件列表中去
                }
            }
        });
    },
    createCaseRemark: function () {
        var options = {
            beforeSubmit: function () {
                return $("#caseRemarkForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                $("input[name=remark]").val("");
                getCaseRemark();
            },
            url: "createCaseRemark",
            type: 'post'
        };
        $("#caseRemarkForm").ajaxSubmit(options);
    },updateCaseBaseTime: function (caseId){
        displayInLayer("更新基准日", {
            requestUrl: "UpdateCaseBaseTime?caseId=" + caseId,
            width: '500px',
            height: '500px'});
    },updateCaseBasicInfo: function (caseId){
        displayInLayer("更新CASE", {
            requestUrl: "UpdateCaseBaseInfo?caseId=" + caseId,
                height: '80%', width: '80%', layer: {
                end: function () {
                    caseTable.ajax.reload()
                }
            }
        });
    },
    updateCaseFiledStatus:function (caseId){
        if(confirm("是否确认归档？")){
            $.ajax({
                type: 'POST',
                url: "updateCaseFiledStatus",
                async: false,
                dataType: 'json',
                data: {caseId: caseId},
                success: function (data) {
                    showInfo(data.msg);
                    caseTable.ajax.reload();
                }
            });
        }
    },
    saveCaseBaseTime: function () {
        var options = {
            beforeSubmit: function () {
                return $("#caseBaseTimeForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功");
                closeCurrentTab();
            },
            url: "saveCaseBaseTime",
            type: 'post'
        };
        $("#caseBaseTimeForm").ajaxSubmit(options);
    }

}