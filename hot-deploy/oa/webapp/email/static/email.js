$.email = {
    writeEmail: function(){
        $.ajax({
            type: 'GET',
            url: 'ComposeMail',
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#mailContent").html(content);
            }
        });
    },
    viewEmail: function(id,type){
        $.ajax({
            type: 'GET',
            url: 'ViewMail',
            async: true,
            dataType: 'html',
            data:{communicationEventId:id,type:type},
            success: function (content) {
                $("#mailContent").html(content);
            }
        });
    },
    returnView : function(type){
        if(type=="inbox"){
            $.email.inbox();
        } else if(type=="Trash"){
            $.email.Trash();
        } else if(type=="Draft"){
            $.email.Draft();
        }
    },
    inbox: function(){
        $.ajax({
            type: 'GET',
            url: 'Inbox',
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#mailContent").html(content);
            }
        });
    },
    Trash: function(){
        $.ajax({
            type: 'GET',
            url: 'Trash',
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#mailContent").html(content);
            }
        });
    },
    Draft: function(){
        $.ajax({
            type: 'GET',
            url: 'Draft',
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#mailContent").html(content);
            }
        });
    },
    sendEmail : function(type){
        $('textarea[name="content"]').val(mailContent.html());
        var options = {
            beforeSubmit: function () {
                return ;
            },
            data:{type:type},
            dataType: "json",
            success: function (data) {
                showInfo(data.data.msg);
                $.email.writeEmail();
            },
            url: "sendEmail",
            type: 'post'
        };
        $("#sendEmail").ajaxSubmit(options);
    },
    downloadFile : function(id){
        var form = $("<form>");   //定义一个form表单
        form.attr('style', 'display:none');   //在form表单中添加查询参数
        form.attr('target', '');
        form.attr('method', 'post');
        form.attr('action', "stream");
        var input1 = $('<input>');
        input1.attr('type', 'hidden');
        input1.attr('name', 'contentId');
        input1.attr('value', id);
        $('body').append(form);  //将表单放置在web中
        form.append(input1);   //将查询参数控件提交到表单上
        form.submit();
    },
    chooseContacts: function(targetId){
        displayInTab3('ChooseContactsTab', '选择人员', {requestUrl: 'LookupEmailContacts', data:{targetId : targetId}, width: 500});
    },
    addGroup: function(){
        displayInTab3('AddGroupTab', '添加分组', {requestUrl: 'CreateEmailGroup', width: 500});
    },
    saveGroup: function(addresses){
        showInfo("add: " + addresses);
    },
    deleteEmail : function(formName){
        var cform = document[formName];
        var len = cform.elements.length;
        var arr = "";
        var j=0;
        for (var i = 1; i < len; i++) {
            var element = cform.elements[i];
            if (element.name.substring(0, 10) == "_rowSubmit" && element.checked == true) {
                arr += element.value + ",";
            }
        }
        showInfo(arr);
        $.ajax({
            type: 'POST',
            url: "deleteEmail",
            async: true,
            data:{communicationEventIdList:arr},
            dataType: 'json',
            success: function (data) {
                showInfo(data.data.msg);
                $.email.inbox();
            }
        });
    },
    removeEmail : function(formName){
        var cform = document[formName];
        var len = cform.elements.length;
        var arr = "";
        var j=0;
        for (var i = 1; i < len; i++) {
            var element = cform.elements[i];
            if (element.name.substring(0, 10) == "_rowSubmit" && element.checked == true) {
                arr += element.value + ",";
            }
        }
        $.ajax({
            type: 'POST',
            url: "removeEmail",
            async: true,
            data:{communicationEventIdList:arr},
            dataType: 'json',
            success: function (data) {
                showInfo(data.data.msg);
                $.email.inbox();
            }
        });
    }
}