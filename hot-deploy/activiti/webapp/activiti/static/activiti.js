$.workflow = {
    manageCategory: function () {
        displayInTab3("manageCategoryTab", "流程分类",{requestUrl: "EditEnums", data:{enumTypeId: "WORKFLOW_CATEGORY"}, width: "600px"});
    },
    createModelDialog: function () {
        displayInTab3("createModelTab", "创建流程模型",{requestUrl: "/workflow/control/CreateModel", width: "400px"});
    },
    deployModel: function (id) {
        $.ajax({
            type: 'GET',
            url: "deployModel",
            async: false,
            data:{modelId: id},
            dataType: 'json',
            success: function (content) {
                displayJsonResponse(content);
            }
        });
    },
    defineModel: function (obj, id) {
        var $obj = $(obj), $tr = $obj.closest("tr"), $tb = $obj.closest("table");
        $tb.find("tr.selected-row").removeClass("selected-row");
        $tr.addClass("selected-row");
        $("#modelEditor").attr("src", "DefineModel?modelId=" + id);
    },

    deleteModel: function (id) {
        if(confirm("确认删除该模型吗?")){
            $.ajax({
                type: 'GET',
                url: "deleteModel",
                async: false,
                data:{modelId: id},
                dataType: 'json',
                success: function (content) {
                    displayJsonResponse(content);
                    ajaxUpdateAreas('ModelList,ModelListOnly,sortField=sn')
                }
            });
        }

    },

    saveModel: function () {
        var options = {
            beforeSubmit: function () {
                return $("#ModelForm").validationEngine("validate");
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                displayJsonResponse(data);
                ajaxUpdateAreas('ModelList,ModelListOnly,sortField=sn')
            },

            url: "saveModel", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#ModelForm").ajaxSubmit(options);
    },
    toggleProcessSuspend: function(defId) {
        $.ajax({
            type:'POST',
            url:"/workflow/control/toggleProcessSuspend",
            async:true,
            data:{defId: defId},
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
                ajaxUpdateAreas('ProcessList,ProcessListOnly,sortField=sn')
            }
        });
        return true;
    },
    convert2Model: function(defId) {
        $.ajax({
            type:'POST',
            url:"convert2Model",
            async:true,
            data:{defId: defId},
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
            }
        });
        return true;
    },
    deleteProcess: function(defId) {
        $.ajax({
            type:'POST',
            url:"deleteProcess",
            async:true,
            data:{defId: defId},
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
                ajaxUpdateAreas('ProcessList,ProcessListOnly,sortField=sn')
            }
        });
        return true;
    },
    uploadProcess: function () {
        var options = {
            beforeSubmit: function () {
                return $("#UploadProcessForm").validationEngine("validate");
            }, // pre-submit callback
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                displayJsonResponse(data);
                ajaxUpdateAreas('ProcessList,ProcessListOnly,sortField=sn')
            },

            url: "uploadProcess", // override for form's 'action' attribute
            type: 'post'        // 'get' or 'post', override for form's 'method' attribute
            //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
            //clearForm: true        // clear all form fields after successful submit
            //resetForm: true        // reset the form after successful submit

            // $.ajax options can be used here too, for example:
            //timeout:   3000
        };
        $("#UploadProcessForm").ajaxSubmit(options);
    },
    uploadProcessForm: function () {
        displayInTab3("uploadProcessFormTab", "部署流程",{requestUrl: "UploadProcess"});
    },
    createActFormDialog: function () {
        displayInTab3("editActFormTab", "新建表单",{requestUrl: "CreateActForm"});
    },
    editActForm: function (id) {
        displayInTab3("editActFormTab", "修改表单",{requestUrl: "CreateActForm", data:{formId: id}});
    },
    deleteActForm: function(formId) {
        $.ajax({
            type:'POST',
            url:"deleteActForm",
            async:true,
            data:{formId: formId},
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
                ajaxUpdateAreas('ActFormList,ActFormListOnly,sortField=sn')
            }
        });
        return true;
    },
    manageActFormType: function () {
        displayInTab3("manageActFormTypeTab", "表单类型",{requestUrl: "EditEnums", data:{enumTypeId: "ACT_TASK_FORM_TYPE"}, width: "600px"});
    },
    showProcessHistory: function (processInstanceId,taskId) {
        displayInTab3("processHistoryTab", "流程审批历史",{requestUrl: "/workflow/control/ProcessHistory", data:{processInstanceId: processInstanceId,taskId:taskId}, width: "800px"});
    },
    startProcess: function (options) {
        $.ajax({
            type:'POST',
            url:"startProcess",
            async:true,
            data:options,
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
            }
        });
        return true;
    },
    extractFieldNameAndValue: function(jObj){
        var name = jObj.attr("name");
        if(!name){
            return false;
        }
        if(jObj[0].type == "radio" || jObj[0].type == "checkbox"){
            if(jObj.is(":checked")){
                return [name, jObj.val()];
            }
        }else{
            return [name, jObj.val()];
        }
        return false;
    },
    prepareFlowDataForm: function(taskId, formId,submitForm){
        var targetFormId = taskId + "Form";
        if(window.KindEditor){
            window.KindEditor.sync(".kindeditor");
        }
        var validation = $("#formInfo").validationEngine("validate");//自定义页面表单验证
        var headerValidation = $("#process-header-form").validationEngine("validate");//流程表单头部验证
        var submitValidation = "";
        if($("#"+submitForm).length) {
            submitValidation = $("#" + submitForm).validationEngine("validate");//指定提交某个外部表单的验证
        }
        if(validation && headerValidation && (submitValidation  || submitForm == "")){
            var taskForm = $("#" + targetFormId);
            if(taskForm.length){
                taskForm.empty();
            }else{
                $("body").append("<form id='" + targetFormId + "' name='" + targetFormId + "'></form>");
                taskForm = $("#" + targetFormId);
            }
            var allFields = "<input type='hidden' name='taskId' value='" + taskId + "'>";
            allFields += "<input type='hidden' name='formId' value='" + formId + "'>";
            var viewFormIdValue = $("#viewFormId").val();
            allFields += "<input type='hidden' name='viewFormId' value='" + (viewFormIdValue ? viewFormIdValue : "") + "'>";
            var nextWay = $("input[name=nextWay]:checked").val();
            allFields += "<input type='hidden' name='nextWay' value='" + (nextWay ? nextWay : "") + "'>";
            var externalLoginKey = getExternalLoginKey();
            allFields += "<input type='hidden' name='externalLoginKey' value='" + (externalLoginKey ? externalLoginKey : "") + "'>";

            $("#processTaskFormWrapper :input[name^=wf]").each(function(){
                var $this = $(this), field = $.workflow.extractFieldNameAndValue($this);
                if(field){
                    allFields += "<input type='hidden' name='" + field[0] + "' value='" + field[1] + "'>";
                }
            })

            $("#processTaskFormWrapper :input[name^=fp_]").each(function(){
                var $this = $(this), field = $.workflow.extractFieldNameAndValue($this);
                if(field){
                    allFields += "<input type='hidden' name='" + field[0] + "' value='" + field[1] + "'>";
                }
            })

            //ofbiz页面表单验证
            var form = formId ? $("#" + formId) : $(".taskFormContainer form");
            if(form.length){
                form.find(":input").each(function(){
                    var $this = $(this), name = $this.attr("name");
                    if(!name || name.indexOf("fp_") || name.indexOf("wf")){//忽略特定开头的字段，在上面已经统一处理了。
                        return true;
                    }
                    var field = $.workflow.extractFieldNameAndValue($this);
                    if(field){
                        allFields += "<input type='hidden' name='" + field[0] + "' value='" + field[1] + "'>";
                    }
                });
            }
            taskForm.append(allFields);
            return targetFormId;
        }else {
            return false;
        }
    },
    pass: function (taskId, formId,submitForm) {
        if(!confirm("确定执行吗？")){
            return;
        }
        var dataFormId = $.workflow.prepareFlowDataForm(taskId, formId,submitForm);//将所有数据放入临时创建的form表单中，采用form表单提交的方式，可以实现多行数提交至服务器端，以数组形式存储变量。
        if(dataFormId){
            var options = {
                beforeSubmit: function () {
                    return true;
                }, // pre-submit callback
                dataType: "json",
                success: function (data) {
                    displayJsonResponse(data);
                    closeCurrentTab();
                    if(top.window.activitiClickPassFunction){
                        activitiClickPassFunction();
                    }else{
                        showSubMenuAjax('MyTaskList')
                    }
                },
                url: "/workflow/control/passTask", // override for form's 'action' attribute
                type: 'post'        // 'get' or 'post', override for form's 'method' attribute
                //dataType:  null        // 'xml', 'script', or 'json' (expected server response type)
                //clearForm: true        // clear all form fields after successful submit
                //resetForm: true        // reset the form after successful submit

                // $.ajax options can be used here too, for example:
                //timeout:   3000
            };
            $("#" + dataFormId).ajaxSubmit(options);
        }
    },
    reject: function (taskId, formId, submitForm) {
        var rejectReason = $("#approveComment").val();
        if(!rejectReason){
            showError("请在流转备注处输入驳回原因");
            return;
        }
        if(!confirm("确定驳回吗？")){
            return;
        }
        var formData = {};//驳回时仅需要驳回原因
        formData["fp_approveComment"] = rejectReason;
        formData["taskId"] = taskId;
        formData["formId"] = formId;
        var externalLoginKey = getExternalLoginKey();
        formData["externalLoginKey"] = externalLoginKey;
        $.ajax({
            type:'POST',
            url:"/workflow/control/rejectTask",
            async:true,
            data:formData,
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
                closeCurrentTab();
                if(top.window.activitiClickRejectFunction){
                    activitiClickRejectFunction();
                }else{
                    showSubMenuAjax('MyTaskList')
                }
            }
        });
    },
    backToStart: function (taskId, formId,submitForm) {
        var rejectReason = $("#approveComment").val();
        if(!rejectReason){
            showError("请在流转备注处输入驳回原因");
            return;
        }
        if (!confirm("确定驳回至发起者吗？")) {
            return;
        }
        var formData = {};//驳回时仅需要驳回原因
        formData["fp_approveComment"] = rejectReason;
        formData["taskId"] = taskId;
        formData["formId"] = formId;
        var externalLoginKey = getExternalLoginKey();
        formData["externalLoginKey"] = externalLoginKey;
        $.ajax({
            type:'POST',
            url:"/workflow/control/backToStart",
            async:true,
            data:formData,
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
                closeCurrentTab();
                if(top.window.activitiClickBackToStartFunction){
                    activitiClickBackToStartFunction();
                }else{
                    showSubMenuAjax('MyTaskList')
                }
            }
        });
    },
    terminate: function (processInstanceId, formId,submitForm) {
        var rejectReason = $("#approveComment").val();
        if(!rejectReason){
            showError("请在流转备注处输入终止原因");
            return;
        }
        if (!confirm("确定终止该流程吗？")) {
            return;
        }
        var formData = {};//终止时仅需要终止原因
        formData["fp_approveComment"] = rejectReason;
        formData["processInstanceId"] = processInstanceId;
        formData["formId"] = formId;
        var externalLoginKey = getExternalLoginKey();
        formData["externalLoginKey"] = externalLoginKey;
        $.ajax({
            type:'POST',
            url:"/workflow/control/terminateTask",
            async:true,
            data:formData,
            dataType:'json',
            success:function (data) {
                displayJsonResponse(data);
                closeCurrentTab();
                if(top.window.activitiClickTerminateFunction){
                    activitiClickTerminateFunction();
                }else{
                    showSubMenuAjax('MyTaskList')
                }
            }
        });
    },
    allot: function (processInstanceId) {
        displayInTab3("allotTbl", "选择转办人", {requestUrl: "/workflow/control/allot", data:{processInstanceId: processInstanceId},width:300,height:150});
    },
    setAllot: function () {
        var validation = $("#setAllotForm").validationEngine("validate");
        if(validation) {
            if (!confirm("确定转办该流程吗？")) {
                return;
            }
            var options = {
                beforeSubmit: function () {
                    return true;
                },
                dataType: "json",
                success: function (data) {
                    showInfo(data.msg);
                    closeCurrentTab();
                    if(top.window.activitiClicksetAllotFunction){
                        activitiClicksetAllotFunction();
                    }else{
                        showSubMenuAjax('MyTaskList')
                    }
                },
                url: "setAllot",
                type: 'post'
            };
            $("#setAllotForm").ajaxSubmit(options);
            return true;
        }
    },
    toggleExecuteWays: function () {
        var way = $("input[name=executeWay]:checked").val(), executeBtn = $(".activitiPassA"), title="执行";

        if(way == "0"){
            $("#anyWays, #nextWays, #nextApprovers").hide();
            $("input[name=nextWay]:checked").prop('checked', false);
            title = executeBtn.attr("oriTitle");
        }else if(way == "1"){
            $("#anyWays").hide();
            $("#nextWays").show();
            $("#nextApprovers").show();
            title = $("input[name=nextWay]:checked").siblings("span").text();
        }else if(way == "2"){
            $("#anyWays").show();
            $("#nextWays").hide();
            $("#nextApprovers").show();
            title = "流转至" + $("input[name=nextWay]:checked").siblings("span").text();
        }
        executeBtn.text(title);
        executeBtn.attr(title);
    },
    updatePassBtnText: function(){
        var way = $("input[name=executeWay]:checked").val(), title = $("input[name=nextWay]:checked").siblings("span").text(), executeBtn = $(".activitiPassA");
        if(way == "2"){
            title = "流转至" + title;
        }
        executeBtn.text(title);
        executeBtn.attr(title);
    },
    processTask: function (taskId, viewType, view) {
        var externalLoginKey = getExternalLoginKey();
        displayInTab3("processTaskTab", "任务处理",{requestUrl: "/workflow/control/ProcessTaskForm", data:{taskId: taskId, viewType: viewType, view: view, externalLoginKey: externalLoginKey}, width: "90%"});
    },
    graphTrace: function(defId, instanceId){
        var frame = document.getElementById("processEditor");
        if(!frame){
            $("body").append('<div id="graphTrac" style="display: none"><iframe id="processEditor" name="processEditor" frameborder="no" style="overflow-y:auto; border: 0;width:100%; min-height:800px;"></iframe></div>');
            frame = document.getElementById("processEditor");
        }
        /*var frameDoc = frame.contentDocument || frame.contentWindow.document;
        frameDoc.removeChild(frameDoc.documentElement);*/
        var height = $("body").height() * 0.8;
        $("#graphTrac").dialog({
            autoOpen:true,
            width:'900px',
            height:height,
            open:function(){
                $("#processEditor").attr("src", "/workflow/diagram-viewer/index.html?processDefinitionId=" + defId + "&processInstanceId=" + instanceId);
            }
        });
        //displayInTab3("graphTraceTab", "流程图",{requestUrl: "/workflow/diagram-viewer/index.html?processDefinitionId=" + defId + "&processInstanceId=" + instanceId, width: "600px"});
    },
    readFormFields: function(processDefinitionId) {
    var dialog = this;

    // 设置表单提交id
//    $form.attr('action', appCtx + '/process/form/dynamic/start-process/' + processDefinitionId);

    // 读取启动时的表单
    jQuery.getJSON('/workflow/control/getStartForm', {procDefId: processDefinitionId}, function(data) {
        var trs =  '<div class="head sub-head">'+
            '<div class="icon"><span class="icos-article"></span></div>'+
            '<h2>详细信息</h2>'+
            '</div>';
        trs +='<div class="block">'+
            '<table cellpadding="0" cellspacing="0" width="100%">';
        trs += "<tr><td width='20%'>提醒类型</td><td><input type='checkbox' id='email' name='fp_email'/>发送邮件</td></tr>";
        jQuery.each(data.form.formProperties, function() {
            var className = this.required === true ? "required" : "";
            var warnContent = this.required === true ? '<span class="mandatory">*</span>' : '';
            trs += "<tr>" + this.createFieldHtml(data, this, className, warnContent);
            /*if(this.required === true) {
             trs += "<span class='mandatory'>*</span>";
             }*/
            trs += "</td></tr></table></div>";
        });
        var $dynamic = $('#runProcessTab').find('.widget');
        $dynamic.append(trs);
        $dynamic.find('input[type=text]').addClass('fmDataSingleLine');
        // 初始化日期组件
        $form.find('.dateISO').datepicker();
    });
}
}
