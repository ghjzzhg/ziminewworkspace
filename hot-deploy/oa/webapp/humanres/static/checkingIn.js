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

function addShow(){
    displayInTab3("OrganizationTab", "新增考勤信息", {requestUrl: "AddCheckingIn", data:{}, width: "800px"});
   /* $.ajax({
        type: 'GET',
        url: "AddCheckingIn",
        async: true,
        dataType: 'html',
        success: function (content) {
            displayInTab("OrganizationTab", "新增考勤信息", content, {});
        }
    });*/
}

function addHolidayInfo(param){
    var strTab = "新增假期信息";
    if(2 == param){
        strTab = "修改假期信息";
    }
    displayInTab3("OrganizationTab", strTab, {requestUrl: "AddHolidayInfo", data:{param:param}, width: "800px"});

   /* $.ajax({
        type: 'POST',
        url: "AddHolidayInfo",
        async: true,
        dataType: 'html',
        data:{param: param},
        success: function (content) {
            displayInTab("OrganizationTab", strTab, content, {});
        }
    });*/
}


function lookHolidayInfo(){
    displayInTab3("OrganizationTab", "查看假期信息", {requestUrl: "LookHolidayInfo", data:{}, width: "800px"});
    /*$.ajax({
        type: 'GET',
        url: "LookHolidayInfo",
        async: true,
        dataType: 'html',
        success: function (content) {
            displayInTab("OrganizationTab", "查看假期信息", content, {});
        }
    });*/
}

function updateWorkNum(param){
    var strTab = "增加班次信息";
    if(2 == param){
        strTab = "修改班次信息";
    }
    displayInTab3("OrganizationTab", strTab, {requestUrl: "EditWorkNum", data:{}, width: "800px"});
    /*$.ajax({
        type: 'GET',
        url: "EditWorkNum",
        async: true,
        dataType: 'html',
        success: function (content) {
            displayInTab3("OrganizationTab", strTab, content);
        }
    });*/
}

function updateWorkRegime(param){
    var strTab = "增加班制信息";
    if(2 == param){
        strTab = "修改班制信息";
    }
    displayInTab3("OrganizationTab", strTab, {requestUrl: "EditListOfWorkByWeek", data:{}, width: "800px"});
   /* $.ajax({
        type: 'GET',
        url: "EditWorkRegime",
        async: true,
        dataType: 'html',
        success: function (content) {
            oa.js.displayInTab("OrganizationTab", strTab, content, {});
        }
    });*/


}

function showWorkRegime(){
    displayInTab3("OrganizationTab", "查看班制信息", {requestUrl: "WorkRegimeShow", data:{}, width: "800px"});
}

function editWorkSchedule(){
    displayInTab3("EditWorkScheduleTab", "新增排班信息", {requestUrl: "EditWorkSchedule", data:{}, width: "800px"});
}

function autoScheduling(){
    displayInTab3("OrganizationTab", "新增自动排班", {requestUrl: "AutoScheduling", data:{}, width: "800px"});
}

function planScheduling(){
    displayInTab3("OrganizationTab", "日程安排", {requestUrl: "LookCheckingStatistics", data:{}, width: "800px"});
}

function showWorkerCheckingIn(num,status) {
    $.ajax({
        type: 'POST',
        url: "WorkerCheckingIn",
        async: true,
        data:{num: num,status:status},
        dataType: 'html',
        success: function (content) {
            $("#WorkerChecking").html(content);
        }
    });
}

function showPerson(){
    displayInTab3("OrganizationTab", "选择员工", {requestUrl: "ShowPerson", data:{}, width: "800px"});
}
function showCalendar(){
    closeCurrentTab();
    displayInTab3("OrganizationTab", "新增个人排班", {requestUrl: "showCalendar", data:{type:"personSchedule"}, width: "800px"});
}