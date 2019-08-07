$.ticket = {
    editChannel: function () {
        displayInTab3("channelTab", "受理渠道管理", {
            requestUrl: "EditEnums",
            data:{enumTypeId: "TICKET_INBOUND_CHANNEL"},
            width: "600px"
        });
    },
    editInboundType: function () {
        displayInTab3("channelTab", "受理方式", {
            requestUrl: "EditEnums",
            data:{enumTypeId: "TICKET_INBOUND_TYPE"},
            width: "600px"
        });
    },
    editContentType: function () {
        displayInTab3("channelTab", "来电类型", {
            requestUrl: "EditEnums",
            data:{enumTypeId: "TICKET_CONTENT_TYPE"},
            width: "600px"
        });
    },
    findTickets: function(url){
        var options = {
            type: 'GET',
            url: url ? url : "ListTicketsOnly",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#ListTickets").html(content);
            }
        };
        $("#FindTickets").ajaxSubmit(options);
    },
    viewTicket: function (ticketId) {
        displayInTab3("editTicketTab", "工单信息", {/*与editTicket的Tab id保持一致，使得解锁编辑时被自动替换*/
            requestUrl: "ViewTicket",
            data:{ticketId: ticketId ? ticketId : ""},
            width: "800px"
        });
    },
    editTicket: function (ticketId, forceEdit) {
        displayInTab3("editTicketTab", "工单信息", {
            requestUrl: "EditTicket",
            data:{ticketId: ticketId ? ticketId : "", forceEdit: forceEdit},
            width: "800px"
        });
    },
    saveTicket: function(){
        KindEditor.sync('textarea[name="content"]');
        var options = {
            beforeSubmit: function () {
                return $("#ticketForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
                $.dispatch.findTickets();
            },
            url: "saveTicket",
            type: 'post'
        };
        $("#ticketForm").ajaxSubmit(options);
    },
    removeTicket: function(ticketId){
        if (confirm("确定删除该项吗?")) {
            $.ajax({
                type: 'POST',
                url: "removeTicket",
                async: false,
                dataType: 'json',
                data:{ticketId:ticketId},
                success: function (data) {
                    showInfo("删除成功");
                    $.dispatch.findTickets();
                }
            });
        }
    },
    editTaskResponse: function (taskId, responseId) {
        displayInTab3("editTaskResponseTab", "任务反馈信息", {
            requestUrl: "EditTaskResponse",
            data:{taskId: taskId, responseId: responseId},
            width: "80%"
        });
    },
    createTaskResponse: function(){
        KindEditor.sync('textarea[name="content"]');
        var options = {
            beforeSubmit: function () {
                return $("#taskResponseForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo("反馈成功");
            },
            url: "createTaskReponse",
            type: 'post'
        };
        $("#taskResponseForm").ajaxSubmit(options);
    },
    updateTaskResponse: function(){
        KindEditor.sync('textarea[name="content"]');
        var options = {
            beforeSubmit: function () {
                return $("#taskResponseForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo("更新成功");
            },
            url: "updateTaskResponse",
            type: 'post'
        };
        $("#taskResponseForm").ajaxSubmit(options);
    },
    removeTaskResponse: function(responseId){
        if (confirm("确定删除该项吗?")) {
            $.ajax({
                type: 'POST',
                url: "removeTaskResponse",
                async: false,
                dataType: 'json',
                data:{responseId:responseId},
                success: function (data) {
                    showInfo("删除成功");
                }
            });
        }
    },
    viewTask: function (taskId) {
        displayInTab3("viewTaskTab", "任务单信息", {
            requestUrl: "ViewTask",
            data:{taskId: taskId ? taskId : ""},
            width: "800px"
        });
    },
    findAllTasks: function(){
        var options = {
            type: 'GET',
            url: "AllTaskOnly",
            async: true,
            dataType: 'html',
            success: function (content) {
                $("#ListTasks").html(content);
            }
        };
        $("#FindTasks").ajaxSubmit(options);
    },
    applyDelay: function (taskId) {
        displayInTab3("applyDelayTab", "申请延时", {
            requestUrl: "ApplyDelay",
            data:{taskId: taskId ? taskId : ""},
            width: "70%"
        });
    },
    saveApplyDelay: function(){
        KindEditor.sync('textarea[name="reason"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
            },
            url: "saveApplyDelay",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    },
    applyFollow: function (taskId) {
        displayInTab3("applyFollowTab", "申请跟踪", {
            requestUrl: "ApplyFollow",
            data:{taskId: taskId ? taskId : ""},
            width: "70%"
        });
    },
    saveApplyFollow: function(){
        KindEditor.sync('textarea[name="reason"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
            },
            url: "saveApplyFollow",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    },
    applyNoReview: function (taskId) {
        displayInTab3("applyNoReviewTab", "申请不回访", {
            requestUrl: "ApplyNoReview",
            data:{taskId: taskId ? taskId : ""},
            width: "70%"
        });
    },
    saveApplyNoReview: function(){
        KindEditor.sync('textarea[name="reason"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
            },
            url: "saveApplyNoReview",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    },
    editTicketSupervise: function (ticketId, superviseId) {
        displayInTab3("editTicketSuperviseTab", "督办工单", {
            requestUrl: "EditTicketSupervise",
            data:{ticketId: ticketId, superviseId: superviseId},
            width: "80%"
        });
    },
    createTicketSupervise: function(){
        KindEditor.sync('textarea[name="reason"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo("成功督办");
            },
            url: "createTicketSupervise",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    },
    updateTicketSupervise: function(){
        KindEditor.sync('textarea[name="reason"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo("更新成功");
            },
            url: "updateTicketSupervise",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    },
    listTicketSuperviseResponse: function ( superviseId) {
        displayInTab3("listTicketSuperviseResTab", "督办回复信息", {
            requestUrl: "ListTicketSuperviseResponse",
            data:{superviseId: superviseId},
            width: "80%"
        });
    },
    editTicketSuperviseResponse: function ( superviseId) {
        displayInTab3("editTicketSuperviseResTab", "回复督办", {
            requestUrl: "EditTicketSuperviseResponse",
            data:{superviseId: superviseId},
            width: "80%"
        });
    },
    createTicketSuperviseResponse: function(){
        KindEditor.sync('textarea[name="content"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo("成功回复");
            },
            url: "createTicketSuperviseResponse",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    },
    updateTicketSuperviseResponse: function(){
        KindEditor.sync('textarea[name="content"]');
        var options = {
            beforeSubmit: function () {
                return $("#applyForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
                showInfo("更新成功");
            },
            url: "updateTicketSuperviseResponse",
            type: 'post'
        };
        $("#applyForm").ajaxSubmit(options);
    }
}