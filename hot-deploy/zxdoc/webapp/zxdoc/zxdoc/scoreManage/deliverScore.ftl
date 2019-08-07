<script src="${request.contextPath}/static/scoreManage.js?t=20171010" type="text/javascript"></script>
<script>
    $(function () {
        $("#editUserScore").validationEngine("attach", {promptPosition: "topRight"});
    })
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 赠送积分</span>
        </div>
    </div>
    <div class="portlet-body">
        <form role="form" action="deliverScore" id="editUserScore" method="post">
            <input type="hidden" value="${parameters.partyIdTo}" name="partyIdTo"/>
            <div class="form-group" style="width: 300px;">
                <label class="control-label">赠送对象:</label>
                <label>${partyName?default('')}</label>
            </div>
            <div class="form-group" style="width: 300px;">
                <label class="control-label">积分<span class="required"> * </span></label>
            <#if maxScore &gt; 0>
                <input type="text" name="score" style="width: 100px" class="form-control validate[required,min[0],max[${maxScore?string}],custom[integer]]">(最高可赠送 ${maxScore?string}分)
            <#else>&nbsp;您当前无可赠送积分
            </#if>
            </div>
<#if maxScore &gt; 0>
            <div class="form-group">
                <div class="margiv-top-10">
                    <a href="javascript:$.scoreManage.deliverScore()" class="btn green">赠送</a>
                </div>
            </div>
</#if>
        </form>
    </div>
</div>