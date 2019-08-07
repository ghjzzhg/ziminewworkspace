<script type="text/javascript">
    $(function () {
        $("#calendarEventForm").validationEngine("attach", {promptPosition: "bottomLeft"});
    })

    function saveCalendarEvent(){
        var options = {
            beforeSubmit: function () {
                return $("#calendarEventForm").validationEngine("validate");
            },
            dataType: "json",
            success: function (data) {
                showInfo("保存成功");
                closeCurrentTab();
            },
            url: "<#if event.id?has_content>update<#else>create</#if>CalendarEvent",
            type: 'post'
        };
        $("#calendarEventForm").ajaxSubmit(options);
    }
    
    function deleteCalendarEvent() {
        if($("input[name=id]").val()!=null&&$("input[name=id]").val()!="") {

            var confirmIndex = getLayer().confirm("确定删除该日程吗?", {
                btn: ['确定', '取消']
            }, function () {

                $.ajax({
                    url: "deleteCalendarEvent",
                    type: "post",
                    dataType: "json",
                    data: {id: $("input[name=id]").val()},
                    success: function (data) {
                        showInfo("删除成功");
                        closeCurrentTab();
                    }
                })
                getLayer().close(confirmIndex);
            })
        }else {
            closeCurrentTab();
        }
    }
</script>
<div class="portlet box light">
    <div class="portlet-body">
        <form id="calendarEventForm">
            <input type="hidden" name="id" value="${event.id!}">
            <input type="hidden" name="owner" value="${userLogin.partyId}">
            <div class="form-group">
                <label class="control-label">标题<span class="required"> * </span></label>
                <input type="text" name="title" <#if event.completed?has_content && event.completed == "Y"> disabled </#if> class="validate[required,custom[noSpecial]] form-control" value="${event.title!}">
            </div>
            <div class="form-group">
                <label class="control-label">开始时间<span class="required"> * </span></label>
                <input type="text" name="start" <#if event.completed?has_content && event.completed == "Y"> disabled </#if> readOnly id="startTime" class="validate[required] form-control" value="${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(event.start, "yyyy-MM-dd HH:mm:ss")}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss', maxDate:'#F{$dp.$D(\'endTime\')}',isShowClear:false,readOnly:true})">
            </div>
            <div class="form-group">
                <label class="control-label">结束时间<span class="required"> * </span></label>
                <input type="text" id="endTime" <#if event.completed?has_content && event.completed == "Y"> disabled </#if> readOnly class="validate[required] form-control" name="end" value="${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(event.end, "yyyy-MM-dd HH:mm:ss")}" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss', minDate:'#F{$dp.$D(\'startTime\')}',isShowClear:false,readOnly:true})">
            </div>
            <div class="form-group">
                <label class="control-label">内容</label>
                <textarea class="form-control validate[custom[noSpecial]]" rows="3" cols="50"  <#if event.completed?has_content && event.completed == "Y"> disabled </#if> name="content">${event.content!}</textarea>
            </div>
            <div class="form-group">
                <label class="control-label">状态</label>
                <#if event.completed?has_content && event.completed == "Y">
                    <label>已完成</label>
                <#else>
                    <select name="completed">
                        <option value="N">未完成</option>
                        <option value="Y">已完成</option>
                    </select>
                </#if>
            </div>
            <div class="form-group" style="text-align: center">
                <div class="margiv-top-10">
                    <#if event.completed?has_content && event.completed == "Y">
                    <#else>
                        <a href="javascript:saveCalendarEvent();" class="btn green"> 保存 </a>
                    </#if>
                    <#if event.completed?has_content>
                        <a href="javascript:deleteCalendarEvent();" class="btn red"> 删除 </a>
                    </#if>
                </div>
            </div>
        </form>
    </div>
</div>