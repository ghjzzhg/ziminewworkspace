<script type="text/javascript">
    var eventsArray1 = new Array();
    var eventsArray2 = new Array();
    var eventsArray3 = new Array();
    for(var i=0;i<30;i++){
        if(i<10){
            eventsArray1.push(
                    {
                        id:i,
                        title:"班次：早班",
                        start:"2015-06-0"+i,
                    },
                    {
                        id:i,
                        title:"班次：中班",
                        start:"2015-06-0"+i,
                    },
                    {
                        id:i,
                        title:"班次：晚班",
                        start:"2015-06-0"+i,
                    }
            );
        }else{
            eventsArray1.push(
                    {
                        id:i,
                        title:"班次：早班",
                        start:"2015-06-"+i,
                    },
                    {
                        id:i,
                        title:"班次：中班",
                        start:"2015-06-"+i,
                    },
                    {
                        id:i,
                        title:"班次：晚班",
                        start:"2015-06-"+i,
                    }
            );
        }

    }
    for(var i=0;i<30;i++){
        if(i<10){
            eventsArray2.push(
                    {
                        id:i,
                        start:"2015-06-0"+i,
                    }
            );
        }else{
            eventsArray2.push(
                    {
                        id:i,
                        start:"2015-06-"+i,
                    }
            );
        }

    }
    for(var i=0;i<30;i++){
        if(i<10){
            eventsArray3.push(
                    {
                        id:i,
                        title:"未及时签到",
                        start:"2015-06-0"+i,
                    },
                    {
                        id:i,
                        title:"未及时签退",
                        start:"2015-06-0"+i,
                    }
            );
        }else{
            eventsArray3.push(
                    {
                        id:i,
                        title:"待签到",
                        start:"2015-06-"+i,
                    },
                    {
                        id:i,
                        title:"待签退",
                        start:"2015-06-"+i,
                    }
            );
        }

    }

    var nowEventArray;
    <#if type?has_content>
        <#if type=="AutoScheduling">
        nowEventArray = eventsArray1;
        <#elseif type=="personSchedule">
        nowEventArray = eventsArray2;
        </#if>
    <#else >
    nowEventArray = eventsArray3;
    </#if>

    $(document).ready(function () {
        initCalendar();
        $(".fc-widget-header").width('');

    });




    function initCalendar() {
        $('#testCalendar').fullCalendar({
            buttonText:{
                prev:'<<上月',
                next:'下月>>',
                today:'今天'
            },
            weekMode :'variable',
            contentHeight: 550,
            monthNames:['01月', '02月', '03月', '04月', '05月', '06月', '07月',
                '08月', '09月', '10月', '11月', '12月'],
            dayNamesShort:['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'],
            titleFormat: {
                month: 'YYYY 年 MMMM'
            },
            theme:false,
            editable:false,
            allDaySlot:false,
            currentTimezone:'Asia/Shanghai',
            events:nowEventArray,
            <#if type?has_content>
                <#if type=="personSchedule">
                    eventRender: function(event, element) {
                        var selector = '<select id="select'+event.id+'" style="height: 20px,width:100%" disabled="disabled">';
                        selector += '<option value="1">'+"早班"+'</option>'+
                                '<option value="2">'+"中班"+'</option>'+
                                '<option value="3">'+"晚班"+'</option>';

                        selector +='</select>';

                        selector += '<div align="center"><span id="pencil'+event.id+'" class="icon-pencil" title="修改"  style="cursor: pointer;" onclick="changeWorkOrder('+event.id+')" order=""></span></div>';
                        selector += '<div align="center"><span id="remove'+event.id+'" class="icon-remove" title="取消" style="cursor: pointer;display: none" onclick="cancelChangeWorkOrder('+event.id+')" order=""></span></div>';
                        element.html(selector);
                    },
                </#if>
            </#if>

        });
    }

    function changeWorkOrder(id){
        $("#select"+id).removeAttr("disabled");
        $("#remove"+id).show();
        $("#pencil"+id).hide();
    }

    function cancelChangeWorkOrder(id){
        $("#select"+id).attr("disabled","disabled");
        $("#remove"+id).hide();
        $("#pencil"+id).show();
    }

</script>