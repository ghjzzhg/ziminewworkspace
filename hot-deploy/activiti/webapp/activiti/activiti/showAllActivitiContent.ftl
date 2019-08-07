<script type="text/javascript" charset="utf-8" src="/workflow/static/activiti.js"></script>
<script type="text/javascript">
    $(function () {
        $.ajax({
            type: 'POST',
            url: "ProcessTaskForm",
            async: true,
            dataType: 'html',
            data:{submitFormId: '${submitFormId?default("")}',taskId: '${parameters.taskId?default("")}', viewType: '${parameters.nextViewType?default("")}', view: '${parameters.formId?default("")}'},
            success: function (content) {
                $("#showContentForm").html(content);
            }
        });
    })
//    /**
//     * @param passInfo 通过
//     * @param rejectInfo 驳回
//     * @param backToStartInfo 驳回至发起人
//     * @param terminateInfo 中止
//     */
//    function setAClick(passInfo,rejectInfo,backToStartInfo,terminateInfo){
//        var pass = $("#activitiPassA")[0];
//        var reject = $("#activitiRejectA")[0];
//        var backToStart = $("#activitiBackToStartA")[0];
//        var terminate = $("#activitiTerminateA")[0];
//        setClickInfo(pass,passInfo);
//        setClickInfo(reject,rejectInfo);
//        setClickInfo(backToStart,backToStartInfo);
//        setClickInfo(terminate,terminateInfo);
//    }
//
//    function setClickInfo(v,setfunction){
//        var clickfunction = $(v).attr("onclick")
//        $(v).attr("onclick",clickfunction+setfunction+"()")
//    }
</script>
<div id="showContentForm">

</div>
<div id="graphTrac" style="display: none">
    <iframe id="processEditor" name="processEditor" frameborder="no" style="overflow-y:auto; border: 0;width:100%; min-height:100%;">
    </iframe>
</div>