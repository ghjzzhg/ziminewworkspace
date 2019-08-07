<#--<script type="text/javascript" src="/images/lib/bootstrap/js/bootstrap.min.js"></script>-->
<script type="text/javascript">
    function fillReason(index) {
        $("#index").val(index);
        layer.open({
            type: 1,
            skin: 'layui-layer-rim', //加上边框
            area: ['420px', '240px'], //宽高
            content: $("#runForContent")
        })
    }
    function runFor() {
        var index = $("#index").val();
        var ticketId = $("#ticketId_" + index).val();
        var roleTypeId = $("#roleTypeId_" + index).val();
        $.ajax({
            url: "RunForTicket",
            type: "post",
            data: {reason: $("#content").val(), ticketId: ticketId, roleTypeId: roleTypeId},
            dataType: "json",
            success: function(content) {
                showInfo(content.data);
                closeCurrentTab();
                displayInside('showPortalPage?portalPageId=ZxdocProvider&parentPortalPageId=ZxdocProvider');
            }
        })
    }
</script>
<div style="display: none;" id="runForContent">
    <input type="hidden" id="index"/>
    <textarea style="margin: 0px; width: 408px; height: 126px;" id="content"></textarea>
    <center>
        <div>
            <a href="javascript:void(0);" class="btn green" onclick="runFor()"> 完成 </a>
        </div>
    </center>
</div>
<#list relatedTickets as relatedTicket>
<div class="timeline-body-content">
${relatedTicket.title}
    <#if relatedTicket.result == "AVAILABLE">
        <input type="hidden" id="ticketId_${relatedTicket?index}" value="${relatedTicket.ticketId}"/>
        <input type="hidden" id="roleTypeId_${relatedTicket?index}" value="${relatedTicket.roleTypeId}"/>
        <div class="btn-group">
            <button class="btn btn-circle green btn-outline btn-sm dropdown-toggle" type="button" data-toggle="dropdown" data-hover="dropdown" data-close-others="true">
                操作
                <i class="fa fa-angle-down"></i>
            </button>
            <ul class="dropdown-menu" role="menu">
                <li>
                    <a href="javascript:;" onclick="fillReason(${relatedTicket?index})">竞选</a>
                </li>
            </ul>
        </div>
    <#elseif relatedTicket.result == "ONGOING">&nbsp;已竞选
    <#elseif relatedTicket.result == "SUCCESS">&nbsp;竞选成功
    </#if>
</div>
</#list>

