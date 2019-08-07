<script type="text/javascript">
    var calendar;
    $(document).ready(function () {
        calendar = initCalendar();
    });
    function initCalendar() {
        $('#testCalendar').fullCalendar({
            buttonText: {
                prev: '<<上月',
                next: '下月>>',
                today: '今天'
            },
            height: 600,
            titleFormat: {
                month: 'YYYY 年 MMMM'
            },
            dayNames: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
            currentTimezone: 'Asia/Shanghai',
            editable: true,
            eventLimit: true,
            eventSources: [
                {
                    url: 'getMySelfSchedule?type=view&partyId=${parameters.partyId}',
                    color: 'yellow',
                    textColor: 'black'
                }
            ],
            eventClick: function (calEvent, jsEvent, view) {
                var m = $.fullCalendar.moment(calEvent.start);
                var startStr = m.format();
                $.workLog.showWorkLog(calEvent.id,startStr,"view");
            }
        });
    }
</script>
<style type="text/css">
    .ui-dialog{
        z-index: 999;
    }
</style>
<div align="center">
    <div id="testCalendar" style="width: 70%;"></div>
</div>

<div align="center">
    <span style="color: red;"
          class="tooltip">说明：点击以上相应日期即可新增最近3天工作日志；为提高工作效率，你可提前计划好以后任何一天的工作，当这一天来临时，系统将会在本页发出醒目提醒！</span>
</div>