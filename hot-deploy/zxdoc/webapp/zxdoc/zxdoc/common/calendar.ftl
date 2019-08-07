<script type="text/javascript">
    function initCalendar() {
        var perCalendar = $('#portlet-calendar').fullCalendar({
            buttonText: {
                prev: '<<上月',
                next: '下月>>',
                today: '今天'
            },
            weekMode: 'variable',
            contentHeight: 'auto',
            aspectRatio: 1,
            eventLimit: true,
            monthNames: ['01月', '02月', '03月', '04月', '05月', '06月', '07月',
                '08月', '09月', '10月', '11月', '12月'],
            dayNamesShort: ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
            titleFormat: {
                month: 'YYYY 年 MMMM'
            },
            theme: false,
            editable: true,
            allDaySlot: false,
            currentTimezone: 'Asia/Shanghai',
            events: {
                url: 'UserCalendarJson',
                type: 'POST',
                dataType: 'json',
                dataFilter: function(data, type){
                    return JSON.stringify($.parseJSON(data).data);
                },
                error: function() {
                    alert('获取日程信息错误');
                }
            },
            dayClick: function(date, jsEvent, view){
                displayInLayer("日程", {requestUrl: 'CalendarEvent?date=' + date.format(),width:400, height: 500, layer:{
                    end: function(){
                        if(isIE()) {
                            $('#portlet-calendar').fullCalendar('destroy');
                            initCalendar();
                        }else {
                            perCalendar.fullCalendar( 'refetchEvents' );
                        }
                    }
                }});
            },
            eventClick: function(calEvent, jsEvent, view) {
                displayInLayer("日程", {requestUrl: 'CalendarEvent?id=' + calEvent.id,width:400, height: 500, layer:{
                    end: function(){
                        if(isIE()) {
                            $('#portlet-calendar').fullCalendar('destroy');
                            initCalendar();
                        }else {
                            perCalendar.fullCalendar( 'refetchEvents' );
                        }
                    }
                }});
            }
        });
    }
    $(function(){
        initCalendar();
    })

    //判断是否是ie浏览器，如果是ie浏览器，直接销毁，重新初始化，解决ie11下不能刷新的bug
    function isIE() { //ie?
        if (!!window.ActiveXObject || "ActiveXObject" in window)
            return true;
        else
            return false;
    }
</script>
<div class="portlet light">
    <div class="portlet-body">
        <div id="portlet-calendar"></div>
    </div>
</div>