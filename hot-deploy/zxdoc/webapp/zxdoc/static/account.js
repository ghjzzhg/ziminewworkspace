$.account = {
    passRegistration: function (userLoginId) {
        if (confirm("确定审核通过吗?")) {
            $.ajax({
                type: 'POST',
                url: "passRegistration",
                async: true,
                dataType: 'json',
                data:{registerUserLoginId:userLoginId},
                success: function (data) {
                    showInfo("审核通过");
                    accountTable.ajax.reload();
                }
            });
        }

    },
    rejectRegistration:function (userLoginId) {
        if(confirm("确定驳回该账号吗？")){
            $.ajax({
                type: 'POST',
                url: "rejectRegistration",
                async: true,
                dataType: 'json',
                data:{rejectUserLoginId:userLoginId},
                success: function (data) {
                    showInfo("驳回成功");
                    accountTable.ajax.reload();
                }
            });
        }
    },
    passQualification: function (userLoginId) {
        if (confirm("确定审核通过实名认证吗?")) {
            $.ajax({
                type: 'POST',
                url: "passQualification",
                async: false,
                dataType: 'json',
                data:{registerUserLoginId:userLoginId},
                success: function (data) {
                    showInfo("审核通过");
                    accountTable.ajax.reload();
                }
            });
        }

    },
    //保存主账号
    createGroupAccount: function () {
/*
        var fileName = $("input[name='regPhoto']").val();
        if(fileName != null && fileName != '') {
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
        }*/

        if("Y" == $("#isPartner").val()){//合伙人账户不能与挂靠的相同
            if($("#registerUserName").val() == $("#groupName").val()){
                showError("合伙人登录账号不允许使用挂靠机构名称");
                return;
            }
        };

        var options = {
            beforeSubmit: function () {
                return $("#accountForm").validationEngine("validate");
            },
            dataType: "html",
            success: function (data) {
                var result = $(data).find("#result").html();
                if (result != "false") {
                    showInfo("保存成功");
                    closeCurrentTab();
                } else {
                    var msg = $(data).find("#msg").html();
                    showError(msg);
                }
            },
            url: "createNewAccount",
            type: 'post'
        };
        $("#accountForm").ajaxSubmit(options);
    },
    //保存子账号
    saveSubInfo: function () {
        var options = {
            beforeSubmit: function () {
                return $("#accountForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                var msg = "保存成功"
                showInfo(msg);
                closeCurrentTab();
            },
            url: "saveSubAccounts",
            type: 'post'
        };
        $("#accountForm").ajaxSubmit(options);
    },
    //修改个人信息
    checkAndSave: function () {
        var type = $("#type").val();
        if(type == "person") {
            var options = {
                beforeSubmit: function () {
                    return $("#accountForm").validationEngine("validate");
                },
                dataType: "json",
                success: function (data) {
                    showInfo("编辑成功");
                    top.location.reload();
                },
                url: "editSubInfos",
                type: 'post'
            };
        }else {
            var options = {
                beforeSubmit: function () {
                    return $("#accountForm").validationEngine("validate");
                },
                dataType: "json",
                success: function (data) {
                    showInfo("编辑成功");
                    top.location.reload();
                },
                url: "updateGroupAccount",
                type: 'post'
            };
        }
        $("#accountForm").ajaxSubmit(options);
    },
    updateGroupAccount: function () {
        $("#username").remove();
        $("#pass").remove();
        $("#pass1").remove();
        $("#typ").remove();
        var options = {
            beforeSubmit: function () {
                return $("#accountForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("编辑成功");
                closeCurrentTab();
            },
            url: "updateGroupAccount",
            type: 'post'
        };
        $("#accountForm").ajaxSubmit(options);
    },
    //编辑子账号
    editSubInfo: function () {
        var options = {
            beforeSubmit: function () {
                return $("#accountForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                var msg = "编辑成功"
                showInfo(msg);
                closeCurrentTab();
            },
            url: "editSubInfos",
            type: 'post'
        };
        $("#accountForm").ajaxSubmit(options);
    },
    uploadUserAvatar: function(){
        var fileName = $("input[name='atta']").val();
        if(fileName){
            if (fileName.indexOf('.jpg') < 0 && fileName.indexOf('.jpeg') < 0 && fileName.indexOf('.gif') < 0 && fileName.indexOf('.png') < 0 && fileName.indexOf('.bmp') < 0) {
                showError("请选择图片上传，格式为：“*.jpg,*.jpeg,*.gif,*.png,*.bmp”");
                return;
            }
            var filesub = fileName.substring(fileName.lastIndexOf('\\') + 1, fileName.length);
            if(filesub.length > 50){
                showError("文件名称长度请小于50个字符");
                return;
            }
            var options = {
                beforeSubmit: function () {
                    return $("#avatarForm").validationEngine("validate");
                },
                dataType: "html",
                success: function (content) {
                    var id = $(content).find("#dataId").html();
                    var msg = $(content).find("#dataMsg").html();
                    if(id){
                        showInfo(msg);
                        closeCurrentTab(id);
                    }else{
                        showError(msg)
                    }
                },
                url: "uploadAvatar",
                type: 'post'
            };
            $("#avatarForm").ajaxSubmit(options);
        }else{
            showError("请选择上传头像")
        }
    },
    updatePassword: function(){
        var options = {
            beforeSubmit: function () {
                return $("#passwordForm").validationEngine("validate");
            },
            dataType: "html",
            success: function (content) {
                //由于返回的content为String类型，需要进行解析
                var el = document.createElement( 'div' );
                el.innerHTML = content;
                var tds = el.getElementsByTagName('div');
                var result = tds[0].innerHTML;
                if(result=="密码错误")
                {
                    showError("您的原密码错误！");
                }
                //目前来说导致修改密码错误的，只有原来的密码验证不通过
                else {
                    showInfo("更新成功");
                    $("input[name=PASSWORD]").val("");
                    $("#newPassword").val("");
                    $("input[name=newPasswordVerify]").val("");
                }
                /*var result = $(content).find("#msg").val();*/
                /*console.log(result);*/
                /*showInfo("更新成功");
                $("input[name=PASSWORD]").val("");
                $("#newPassword").val("");
                $("input[name=newPasswordVerify]").val("");*/
            },
            url: "login",
            type: 'post'
        };
        $("#passwordForm").ajaxSubmit(options);
    },
    deleteQualification:function(data){
        if (confirm("确定审核拒绝实名认证吗?")) {
            $.ajax({
                type:'post',
                url:'deleteQualification',
                dataType:'json',
                data:{userLoginId:data},
                success:function(){
                    showInfo('拒绝认证');
                    accountTable.ajax.reload();
                }
            })
        }

    },
    deletePersonQualify:function(data){
        if (confirm("确定审核拒绝实名认证吗?")) {
            $.ajax({
                type:'post',
                url:'deletePersonQualify',
                dataType:'json',
                data:{personId:data},
                success:function(){
                    showInfo('拒绝认证');
                    accountTable.ajax.reload();
                }
            })
        }

    },
    passPersonQualification:function(personId){
        if(confirm("确定审核通过实名认证吗?")){
            $.ajax({
                type:'post',
                url:'passPersonQualification',
                dataType:'json',
                data:{personId:personId},
                success:function(){
                    showInfo('审核通过');
                    accountTable.ajax.reload();
                }

            })
        }

    }
}