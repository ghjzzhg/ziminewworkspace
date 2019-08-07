<script type="text/javascript">
    $(document).ready(function () {
        var order = 0;
        initCalendar();
    });
    function initCalendar() {
        var eventSourParam = new Object();
        if('${groupValues?default('')}' == ''){
            eventSourParam.url = '/hr/control/getGroupSchedule';
            eventSourParam.data = {
                groupId :'${groupId}',
                workGroupSize:'${workGroupSize}',
                orderValues :'${workOrders}',
                order:'${groupOrder}',
                startDate:'${startDate}',
                endDate:'${endDate}'};
        }else {
            eventSourParam.url = '/hr/control/getAutoWorkScheduleSource';
            eventSourParam.data = {
                autoWorkScheduleId: '${autoWorkScheduleId?default('')}',
                groupValues: '${groupValues}',
                orderValues: '${orderValues}',
                startDate: '${startDate}',
                endDate: '${endDate}'
            };
        }

        eventSourParam.type = 'POST';
        eventSourParam.success = function(data){
        };
        eventSourParam.error = function() {
            alert('there was an error while fetching events!');
        };
        eventSourParam.color = 'yellow';
        eventSourParam.textColor = 'black';
        $('#schduleCalendar').fullCalendar({
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
</script>
<div id="schduleCalendar"></div>
