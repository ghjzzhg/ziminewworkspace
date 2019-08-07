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
            <span class="caption-subject bold uppercase"> 用户积分</span>
        </div>
    </div>
    <div class="portlet-body">
        <#if !userScore?has_content>
            <#assign userScore={}>
        </#if>
        <form role="form" action="saveUserScore" id="editUserScore" method="post">
            <input type="hidden" value="${userScore.userLoginId?default('')}" name="userLoginId"/>
            <div class="form-group" style="width: 300px;">
                <label class="control-label">姓名:</label>
                <label>${userName?default('')}</label>
            </div>
            <div class="form-group" style="width: 300px;">
                <label class="control-label">积分<span class="required"> * </span></label>
                <input type="text" name="scoreNow" class="form-control validate[required,custom[onlyNumberSp]]" value="${userScore.scoreNow?default('')}"/>
            </div>
            <div class="form-group" style="width: 300px;">
                <label class="control-label">积分上限</label>
                <input type="text" name="scoreOn" class="form-control validate[custom[onlyNumberSp]]" value="${userScore.scoreOn?default('')}"/>
            </div>
            <div class="form-group" style="width: 300px;">
                <label class="control-label">积分下限</label>
                <input type="text" name="scoreOff" class="form-control validate[custom[onlyNumberSp]]" value="${userScore.scoreOff?default('')}"/>
            </div>
            <div class="form-group">
                <div class="margiv-top-10">
                    <a href="javascript:$.scoreManage.saveUserScore()" class="btn green">提交</a>
                </div>
            </div>
        </form>
    </div>
</div>