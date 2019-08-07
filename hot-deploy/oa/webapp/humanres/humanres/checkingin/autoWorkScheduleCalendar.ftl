<#assign autoWorkScheduleId = autoWorkScheduleId?default('')>
<#assign workGroups = workGroups?default('')>
<#assign workOrders = workOrders?default('')>
<#assign startDate = startDate?default('')>
<#assign endDate = endDate?default('')>
<script type="text/javascript">
    $(document).ready(function () {
        var order = 0;
        initCalendar('#autoWorkSchduleCalendar');
    });
    function initCalendar(id) {

        var eventSourParam = new Object();

        eventSourParam.url = '/hr/control/getAutoWorkScheduleSource';
        eventSourParam.data = {
            autoWorkScheduleId: '${autoWorkScheduleId}',
            groupValues: '${workGroups}',
            orderValues: '${workOrders}',
            startDate: '${startDate}',
            endDate: '${endDate}'
        };
        eventSourParam.type = 'POST';
        eventSourParam.success = function (data) {
        };
        eventSourParam.error = function () {
            alert('there was an error while fetching events!');
        };
        eventSourParam.color = 'yellow';
        eventSourParam.textColor = 'black';
        $(id).fullCalendar({
            buttonText: {
                prev: '<<上月',
                next: '下月>>',
                today: '今天'
            },
            weekMode: 'variable',
            contentHeight: 550,
            monthNames: ['01月', '02月', '03月', '04月', '05月', '06月', '07月',
                '08月', '09月', '10月', '11月', '12月'],
            dayNamesShort: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
            titleFormat: {
                month: 'YYYY 年 MMMM'
            },
            theme: false,
            editable: false,
            allDaySlot: false,
            currentTimezone: 'Asia/Shanghai',
            eventSources: [eventSourParam],
            eventRender: function(event, element) {
                var contentHtml = '<span title="班组：班次">'+ event.workGroup +'：'+ event.title +'</span>';
                element.html(contentHtml);
            }
        });
    }
    function save(){
        var options = {
            dataType:"json",
            success:function (data) {
                showInfo(data.returnValue);
                closeCurrentTab();
                $.checkingIn.searchScheduleOfWork();
            },
            async: true,
            url:"saveAutoSchedule",
            type:'post'
        };
        $("#autoWorkSchduleForm").ajaxSubmit(options);
    }
    function getGroupSchedule(id){
        var groupId = id;
        var startDate = $('#autoWorkSchedule_startDate').val();
        var endDate = $('#autoWorkSchedule_endDate').val();
        var workOrders = $('input[name="workOrders"]').val();
        var workGroups = $('input[name="workGroups"]').val();
        var workGroupSize = workGroups.split(',').length - 1;
        var data = new Object();
        if(groupId == ''){
            var workGroups = $('input[name="workGroups"]').val();
            var autoWorkScheduleId = $('input[name="autoWorkScheduleId"]').val();
            data.autoWorkScheduleId = autoWorkScheduleId;
            data.groupValues = workGroups;
            data.orderValues = workOrders;
            data.startDate = startDate;
            data.endDate = endDate;
        }else{
            var groupOrder = ($("#group_"+groupId).attr("name").split("_"))[0];
            data.groupId = groupId;
            data.groupOrder = groupOrder;
            data.workGroupSize = workGroupSize;
            data.workOrders = workOrders;
            data.startDate = startDate;
            data.endDate = endDate;
        }
        $.ajax({
            type: 'POST',
            url: "DepartmentScheduleCalendar",
            async: true,
            data:data,
            dataType: 'html',
            success: function (data) {
                $("#autoWorkSchduleCalendar").html(data);
            }
        });
    }
</script>
<form method="post" id="autoWorkSchduleForm" class="basic-form" name="autoWorkSchduleForm">
    <input type="hidden" name="autoWorkScheduleId" value="${autoWorkScheduleId}">
    <input type="hidden" name="workGroups" value="${workGroups}">
    <input type="hidden" name="workOrders" value="${workOrders}">
    <input type="hidden" name="startDate" id="autoWorkSchedule_startDate" value="${startDate}">
    <input type="hidden" name="endDate" id="autoWorkSchedule_endDate" value="${endDate}">

    <#if groupListSize != 1>
        <div class="screenlet-title-bar">
            <ul>
                <li class="h3 ms-hover">所属班组</li>
            </ul>
        </div>
        <table>
            <div>
                <select style="width:100%;" onchange="getGroupSchedule(this.value)">
                    <option value="">--请选择--</option>
                    <#if groupList?has_content>
                        <#list groupList as list>
                            <option value="${list.partyId}" id="group_${list.partyId}" name="${list.order}_${list.partyId}">${list.groupName}</option>
                        </#list>
                    </#if>
                </select>
            <#--<#else>-->
            <#--<label>${groupList[0].groupName}</label>-->
            </div>
        </table>
    </#if>
    <div id="autoWorkSchduleCalendar"></div>
    <div id="autoWorkSchduleCalendarTo"></div>
    <div>
        <span>
            <a href="#" class="buttontext" onclick="save()">${uiLabelMap.CommonSave}</a>
        </span>
    </div>
</form>