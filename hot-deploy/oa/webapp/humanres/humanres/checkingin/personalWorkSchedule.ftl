<script type="text/javascript">
    $(document).ready(function () {
        var order = 0;
        $.ajax({
            type: 'POST',
            url: "findListOfWorkAll",
            async: true,
            data:{},
            dataType: 'json',
            success: function (data) {
                var listOfWorkArray = new Array();
                var returnValue = data.returnValue;
                for(var i=0;i<returnValue.length;i++){
                    var listOfWork = new Object();
                    listOfWork.listOfWorkId = returnValue[i].listOfWorkId;
                    listOfWork.name = returnValue[i].listOfWorkName;
                    listOfWorkArray.push(listOfWork);
                }
                initCalendar(order,listOfWorkArray);
            }
        });
    });
    function initCalendar(order,listOfWorkArray) {
        var perCalendar = $('#personalWorkScheduleCalendar').fullCalendar({
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
            events: {

            },
            eventSources: [
                // your event source
                {
                    url: '/hr/control/getPersonalWorkSchdule',
                    type: 'POST',
                 <#if date?has_content>
                     data: {
                         staffId: '${staffId?default('')}',
                         date:'${date?default('')}'
                     }
                 <#else >
                     data: {
                         staffId: '${staffId?default('')}'
                     }
                 </#if>,
                    success:function(data){

                    },
                    error: function() {
                        alert('there was an error while fetching events!');
                    },
                    color: 'yellow',   // a non-ajax option
                    textColor: 'black' // a non-ajax option

                }
            ],
            eventRender: function(event, element) {
                var contentHtml = '<div align="center">' +
                        '<span>' +
                        '<select style="height: inherit;width: 80%" id="personalSchedule_selected_' + event.start +'" disabled="true">' +
                        '<option value="">--请选择--</option>';
                var index = "0";
                for(var i = 0;i < listOfWorkArray.length;i++){
                    if(event.id == listOfWorkArray[i].listOfWorkId){
                        index = "1";
                    }
                }
                for(var i = 0;i < listOfWorkArray.length;i++){
                    if(event.id == listOfWorkArray[i].listOfWorkId){
                        contentHtml = contentHtml + '<option value="' + event.start + '-' + listOfWorkArray[i].listOfWorkId + '-' + index + '" selected>'+ listOfWorkArray[i].name +'</option>';
                    }else{
                        contentHtml = contentHtml + '<option value="' + event.start + '-' + listOfWorkArray[i].listOfWorkId +  '-' + index +'">'+ listOfWorkArray[i].name +'</option>';
                    }
                }
                contentHtml = contentHtml + '</select>'+
                        '</span>' +
                        '<span style="width:50%">' +
                        '<a id="personalSchedule_update_' + event.start +'" class="icon-edit" title="修改" href="#" onclick="updatePersonalSchedule('+ event.start + ',$(this))"></a>' +
                        '<a style="display:none" id="personalSchedule_delete_' + event.start +'" class="icon-trash" title="取消" href="#" onclick="deletePersonalSchedule('+ event.start + ',$(this))"></a>' +
                        '</span>'+
                        '<div>';
                element.html(contentHtml);
            },
            eventAfterAllRender:function(view) {

            }
        });
    }
    function updatePersonalSchedule(str,obj){
        $("#personalSchedule_selected_"+str).removeAttr("disabled");
        $(obj).css("display","none");
        $("#personalSchedule_delete_"+str).css("display"," ");
    }
    function deletePersonalSchedule(str,obj){
        $("#personalSchedule_selected_"+str).attr("disabled","true");
        $(obj).css("display","none");
        $("#personalSchedule_update_"+str).css("display"," ");
    }

    function savePersonalWorkSchedule() {
        var valueStr = "";
        $("#personalWorkScheduleCalendar").find("select:not([disabled])").each(function(){
            var personalValue = this.value;
            if(personalValue){
                valueStr += ","+personalValue;
            }
        });
        var options = {
            beforeSubmit:function () {
            },
            dataType:"json",
            success:function (data) {
                $.checkingIn.searchScheduleOfWork();
                showInfo(data.returnValue);
                closeCurrentTab();
            },
            url:"savePersonalWorkSchedule",
            data:{valueStr:valueStr},
            type:'post'
        };
        $("#personalWorkScheduleForm").ajaxSubmit(options);
    }
</script>

<form method="post" id="personalWorkScheduleForm" class="basic-form" name="personalWorkScheduleForm">
    <input type="hidden"  name="staff" value="${staffId?default('')}"/>
    <input type="hidden"  name="personalWorkScheduleId" value="${dataValue?default('')}"/>
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="staffSearchForm_e" id="staffSearchForm_e_title">所属员工</label></td>
            <td>
            ${person.fullName}
            </td>
        </tr>
        </tbody>
    </table>
    <div id="personalWorkScheduleCalendar"></div>
    <div align="center">
        <span>
            <a class="smallSubmit" href="#" onclick="savePersonalWorkSchedule()">保存</a>
        </span>
    </div>
</form>