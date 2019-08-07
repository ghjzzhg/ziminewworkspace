<script type="text/javascript">
    function openMore(){
        displayInLayer2('全部最新信息', {requestUrl: '/zxdoc/control/campaignList',});
    }
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
<div style="display: none;padding: 5px" id="runForContent">
    <input type="hidden" id="index"/>
    <textarea style="margin: 0px; width: 100%; height: 126px;" id="content"></textarea>
    <center>
        <div>
            <a href="javascript:void(0);" class="btn green" onclick="runFor()"> 完成 </a>
        </div>
    </center>
</div>
<div class="portlet light tasks-widget ">
    <div class="portlet-body" style="height: 270;overflow-y: auto">
        <div class="task-content">
            <div class="scroller" style="height: 200px" data-always-visible="1" data-rail-visible1="1">
                <!-- START TASK LIST -->
                <ul class="task-list">
                    <#list relatedTickets as relatedTicket>
                            <li>
                                <div class="task-title">
                                    <span class="task-title-sp"><a href="javascript:;" onclick="displayInLayer('详细信息', {requestUrl: 'ViewTicket?ticketId=${relatedTicket.ticketId}', height: 600})"> ${relatedTicket.startTime?string("yyyy-MM-dd")}&nbsp;${relatedTicket.title} </a></span>
                                    <span class="label label-sm label-danger">${relatedTicket.typeName!}</span>
                                <#if relatedTicket.result == "AVAILABLE">
                                    <span class="task-bell"><i class="font-yellow fa"></i>
                                </span>
                                </div>
                                    <input type="hidden" id="ticketId_${relatedTicket?index}" value="${relatedTicket.ticketId}"/>
                                    <input type="hidden" id="roleTypeId_${relatedTicket?index}" value="${relatedTicket.roleTypeId}"/>
                                    <div class="task-config">
                                        <div class="task-config-btn btn-group"><#--
                                            <a class="btn btn-sm default" href="javascript:;" data-toggle="dropdown"
                                               data-hover="dropdown" data-close-others="true">
                                            </a>-->
                                            <button type="button" class="btn btn-info btn-sm" onclick="fillReason(${relatedTicket?index})">竞选</button>
                                            <#--<a href="javascript:;" onclick="fillReason(${relatedTicket?index})">
                                                <i class="fa fa-check"></i> 竞选 </a>-->
                                        </div>
                                    </div>
                                <#elseif relatedTicket.result == "ONGOING">
                                    <span class="task-bell"><i class="font-yellow fa fa-clock-o"></i>
                                </span>
                                </div>
                                    <div class="task-config">已竞选</div>
                                <#elseif relatedTicket.result == "SUCCESS">
                                    <span class="task-bell"><i class="font-green fa fa-check"></i>
                                </span>
                                </div>
                                    <div class="task-config">竞选成功</div>
                                </#if>
                            </li>
                    </#list>
                </ul>
                <!-- END START TASK LIST -->
            </div>
        </div>
    </div>
</div>