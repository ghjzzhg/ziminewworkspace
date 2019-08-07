$.bumphNotice = {
    addBumphNotice: function(strTab){
        displayInTab3("AddBumphNoticeTab", strTab, {requestUrl: "AddBumphNoticeAjax",width: "800px", height:600,position:"center"});
    },
    addTransactionType: function(param){
        var strTab = "增加事务类别"
        if(param=="edit"){
            strTab = "修改事务类别";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "AddTransactionType",data:{param:param},width: "600px", position:"center"});
    },
    addTransactionProgress: function(param){
        var strTab = "增加事务进度"
        if(param=="edit"){
            strTab = "修改事务进度";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "AddTransactionProgress",data:{param:param},width: "600px", position:"center"});
    },
    showBumphNotice: function(){
        displayInTab3("AddTransactionTab", "签收记录", {requestUrl: "showBumphNotice",width: "800px",height:600,position:"center"});
    },

    showBumphNoticeInfo: function(){
        displayInTab3("AddTransactionTab", "公文/通知明细", {requestUrl: "showBumphNoticeInfo",width: "800px", position:"center"});
    }

}

$.proposal = {
    proposalFeedbackInfo:function(){
        displayInTab3("AddTransactionTab", "浏览提案及反馈", {requestUrl: "proposalFeedbackInfo",width: "800px",height:600, position:"center"});
    },
    proposalEdit:function(strTab){
        displayInTab3("proposalEditTab", strTab, {requestUrl: "ProposalEdit",width: "800px",height:600, position:"center"});
    }
}

$.meetingNotice = {
    summaryRelease:function(strTab){
        displayInTab3("summaryReleaseTab", strTab, {requestUrl: "summaryRelease",width: "800px",height:600, position:"center"});
    },
    showMeetingInfo:function(){
        displayInTab3("AddTransactionTab", "会议纪要明细", {requestUrl: "showMeetingInfo",width: "800px",height:700, position:"center"});
    },
    addMeetingNotice:function(){
        displayInTab3("addMeetingTab", "发布会议通知", {requestUrl: "MeetingNoticeRelease",width: "800px",height:600, position:"center"});
    }
}

$.memo = {
    addMemo:function(strTab){
        displayInTab3("AddMemo", strTab, {requestUrl: "addMemo",width: "800px",height:500,position:"center"});
    },
    showMemoInfo:function(){
        displayInTab3("AddMemo", "个人日记＆备忘录浏览", {requestUrl: "showMemoInfo",width: "800px",position:"center"});
    }
}
$.liaison = {
    showLiaisonInfo:function(){
        displayInTab3("AddMemo", "审核工作联络单", {requestUrl: "showLiaisonInfo",width: "800px",height:600,position:"center"});
    },
    showLiaison:function(){
        displayInTab3("AddMemo", "工作联络单", {requestUrl: "showLiaison",width: "800px",height:600,position:"center"});
    }
}
$.workPlan = {
    addWorkReport:function(strTab){
        displayInTab3("addWorkReportTab", strTab, {requestUrl: "addWorkReport",width: "800px",height:500,position:"center"});
    },
    feedbackWorkPlan:function(type){
        displayInTab3("FeedbackWorkPlanTab", "反馈工作计划", {
            requestUrl: "feedbackWorkPlan",
            data: {feedbackType: type},
            width: "800px",
            height: "500",
            position: 'center'
        });
    },
     commitReport:function(){
         displayInTab3("commitReportTab", "报告详细信息及反馈", {
             requestUrl: "commitReport",
             width: "800px",
             height: "500",
             position: "center"
         });
     },
    browseReport:function(){
         displayInTab3("commitReportTab", "工作报告浏览", {
             requestUrl: "browseReport",
             width: "800px",
             height: "500",
             position: "center"
         });
     },
    workPlanCreate:function(){
        var options = {
            beforeSubmit: function () {
                return $('#WorkPlanForm').validationEngine('validate');
            },
            dataType: "json",
            success: function (data) {
                closeCurrentTab();
            },
            url: "saveWorkPlanCreate",
            type: 'post'
        };
        $("#WorkPlanForm").ajaxSubmit(options);
    }
}

$.transaction = {
    addTransaction: function(param){
        var strTab = "增加事务"
        if(param=="edit"){
            strTab = "修改事务";
        }
        displayInTab3("AddTransactionTab", strTab, {requestUrl: "TransactionAddAjax",data:{param:param},width: "800px",position:"center"});
    },
    addTransactionType: function(param){
        var strTab = "增加事务类别"
        if(param=="edit"){
            strTab = "修改事务类别";
        }
        displayInTab3("AddTransactionTypeTab", strTab, {requestUrl: "AddTransactionType",data:{param:param},width: "600px", position:"center"});
    },
    addTransactionProgress: function(param){
        var strTab = "增加事务进度"
        if(param=="edit"){
            strTab = "修改事务进度";
        }
        displayInTab3("AddTransactionProgressTab", strTab, {requestUrl: "AddTransactionProgress",data:{param:param},width: "600px", position:"center"});
    },
    transactionInfo:function(){
        displayInTab3("TransactionInfoTab", "事务详细信息及反馈", {requestUrl: "transactionInfo",width: "800px", height:600,position:"center"});
    },
    visitLog:function(strTab){
        closeCurrentTab2();
        displayInTab3("VisitLogTab", "事务："+strTab+"浏览日志", {requestUrl: "visitLogList",width: "800px", position:'top'});
    }
}

$.osManager={
    showCreateForm:function(){
        displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{}, width: "800px"});
    },
    deleteOsManager:function(id){
        $.ajax({
            type: 'POST',
            url: "deleteOsManager",
            async: true,
            data:{osManagementId: id},
            dataType: 'html',
            success: function (content) {
                $("#ListSubOrgs").html(content);
            }
        });
    },
    createOsManager:function(id){
        var options = {
            dataType:"json",
            success:function (data) {
                closeCurrentTab();
                showInfo(data.data.msg);
            },
            url:"saveOsManager",
            type:'post'
        };
        $("#EditOsManager").ajaxSubmit(options);
    },
    showUpdateForm:function(id){
        displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{osManagementId: id}, width: "800px"});
    },
    showInventoryInfoForm:function(id){
        displayInTab3("SubOrganizationTab", "仓库信息", {requestUrl: "EditInventoryInfo", data:{inventoryInfoId: id}, width: "800px"});
    }

}




function searchOsManager() {
    var options = {
        dataType:"html",
        success:function (content) {
            $("#ListSubOrgs").html(content);
        },
        url:"ListOsManager",
        type:'post'
    };
    $("#SearchOsManager").ajaxSubmit(options);
}

function showCreateForm(){
    displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{}, width: "800px"});
}

function showUpdateForm(id){
    displayInTab3("SubOrganizationTab", "办公用品", {requestUrl: "EditOsManager", data:{osManagementId: id}, width: "800px"});
}

function createOsManager() {
    var options = {
        dataType:"json",
        success:function (data) {
            closeCurrentTab();
            showInfo(data.data.msg);
        },
        url:"saveOsManager",
        type:'post'
    };
    $("#EditOsManager").ajaxSubmit(options);
}

function deleteOsManager(id) {
    $.ajax({
        type: 'POST',
        url: "deleteOsManager",
        async: true,
        data:{osManagementId: id},
        dataType: 'html',
        success: function (content) {
            $("#ListSubOrgs").html(content);
            /*
             //showInfo(data.data.msg);
             alert(data.data.msg);*/
        }
    });
}


function showInventoryInfoForm(id){
    displayInTab3("SubOrganizationTab", "仓库信息", {requestUrl: "EditInventoryInfo", data:{inventoryInfoId: id}, width: "800px"});
}

function deleteInventoryInfo(id){
    alert("aa");
    $.ajax({
        type: 'POST',
        url: "deleteInventoryInfo",
        async: true,
        data:{inventoryInfoId: id},
        dataType: 'html',
        success: function (content) {
            $("#content-main-section").html($(content));
        }
    });
}


function showCustomerInfoForm(id){
    displayInTab3("SubOrganizationTab", "维护客户信息", {requestUrl: "EditCustomerInfo", data:{customerInfoId: id}, width: "800px"});

}

function showProductTypeForm(id){
    var strTab = "修改货品类别";
    if(id == ""){
        strTab = "增加货品类别";
    }
    displayInTab3("SubOrganizationTab", strTab, {requestUrl: "EditProductType", data:{productCode: id}, width: "800px"});

}

function showInputOrOutInventoryForm(id,url){
    var strTab = "入库"
    if("EditOutInventory"==url){
        strTab = "出库"
    }
    displayInTab3("SubOrganizationTab", strTab, {requestUrl: url, data:{productCode: id}, width: "800px"});

}

function searchOutInventory(){
    var options = {
        dataType:"html",
        success:function (content) {
            $("#ListSubOrgs").html($(content));
        },
        url:"searchOutInventory",
        type:'post'
    };
    $("#EditOutInventoryMany").ajaxSubmit(options);
}


function showReceiveInfo(id){
    displayInTab3("SubOrganizationTab", "领用单明细信息", {requestUrl: "receiveInfo", data:{receiveNum: id}, width: "800px"});

}


function showFixedAssetsInfoForm(id){
    displayInTab3("SubOrganizationTab", "固定资产", {requestUrl: "FixedAssetsInfoForm", data:{productCode: id}, width: "800px"});

}

function borrowRegister(){
    displayInTab3("SubOrganizationTab", "借用预登记", {requestUrl: "borrowRegister", data:{}, width: "800px"});

}

function borrowRegisterConfirm(){
    displayInTab3("SubOrganizationTab", "借用预登记", {requestUrl: "borrowRegisterConfirm", data:{}, width: "1000px"});

}

function borrowInfo(){
    displayInTab3("SubOrganizationTab", "资产借用单详情", {requestUrl: "borrowInfo", data:{}, width: "800px"});
}

function addOutInventoryMany(){
    $.ajax({
        type: 'POST',
        url: "AddOutInventoryMany",
        async: true,
        dataType: 'html',
        success: function (content) {
            $("#ListSubOrgs").html($(content));
        }
    });
}


function addReceiveForm(){
    displayInTab3("SubOrganizationTab", "增加新的领用单", {requestUrl: "OutInventoryMany", data:{}, width: "800px"});
}

function sendOut(){
    displayInTab3("SubOrganizationTab", "领用单明细信息", {requestUrl: "sendOut", data:{}, width: "800px"});
}



function addFixedAssets(){
    displayInTab3("SubOrganizationTab", "固定资产增加", {requestUrl: "AddFixedAssets", data:{}, width: "800px"});
}


