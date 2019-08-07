<#include "component://widget/templates/chooser.ftl"/>
<script type="text/javascript">
    $(document).ready(function () {
        initCalendar('${partyId?default('')}');
    });
    function initCalendar(partyId) {
        var perCalendar = $('#staffCheckingInCalendar').fullCalendar({
            buttonText: {
                prev: '<<上月',
                next: '下月>>',
                today: '今天'
            },
            weekMode: 'variable',
            contentHeight: 550,
            monthNames: ['01月', '02月', '03月', '04月', '05月', '06月', '07月',
                '08月', '09月', '10月', '11月', '12月'],
            dayNamesShort: ['周日', '周一', '周二', '周三', '周四', '周五', '周六'],
            titleFormat: {
                month: 'YYYY 年 MMMM'
            },
            theme: true,
            editable: false,
            allDaySlot: false,
            //eventLimit: true,
            currentTimezone: 'Asia/Shanghai',
            events: '',
            eventSources: [
                // your event source
                {
                    url: '/hr/control/getStaffCheckingStatus',
                    type: 'POST',
                    data: {
                        staffId: partyId
                    },
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
               /* var contentHtml = '<p>'+ event.title +'</p><p>aaa</p>';
                element.html(contentHtml);*/
            },
            eventAfterAllRender:function(view) {

            }
        });
    }

    function searche(){
        $('#staffCheckingInCalendar').fullCalendar('destroy');
        var partyId = $($("input[name='staff']")[0]).val();
        initCalendar(partyId);
    }
</script>
<style>
    .ui-dialog {
        position: fixed;
        z-index: 999;
    }
</style>

<form method="post" id="StaffCheckingInCalendarForm" class="basic-form" name="StaffCheckingInCalendarForm">
    <table cellspacing="0" class="basic-table">
        <tbody>
        <tr>
            <td class="label">
                <label for="StaffCheckingInCalendarForm_staff" id="StaffCheckingInCalendarForm_staff_title">所属员工</label></td>
            <td>
                <@chooser chooserType="LookupEmployee" name="staff" importability=true chooserValue="${partyId?default('')}" />
                <#--<@htmlTemplate.lookupField value="${partyId}" formName="StaffCheckingInCalendarForm" name="staff" id="partyId" fieldFormName="LookupStaff" position="center"/>-->
            </td>
            <td>
                <a class="smallSubmit" onclick="searche()">搜索</a>
            </td>
        </tr>
        </tbody>
    </table>
    <div id="staffCheckingInCalendar"></div>
</form>