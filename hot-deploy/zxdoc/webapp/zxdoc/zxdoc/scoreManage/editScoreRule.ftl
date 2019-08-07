<link href="/images/lib/validationEngine/validationEngine.jquery.css" rel="stylesheet" type="text/css"/>
<script type="text/javascript" src="/images/lib/validationEngine/languages/jquery.validationEngine-zh_CN.js?t=20171012"></script>
<script type="text/javascript" src="/images/lib/validationEngine/jquery.validationEngine.js?t=20171010"></script>
<script src="${request.contextPath}/static/scoreManage.js?t=20171010" type="text/javascript"></script>
<script type="text/javascript">
    $(function () {
        $("#scoreRule").validationEngine("attach", {promptPosition: "bottomLeft"});
    })
</script>
<div class="portlet light">
    <div class="portlet-title">
        <div class="caption font-red-sunglo">
            <i class="icon-settings font-red-sunglo"></i>
            <span class="caption-subject bold uppercase"> 新策略</span>
        </div>
    </div>
    <form id="scoreRule">
        <div class="portlet-body">
        <#if !editRule?has_content>
            <#assign editRule = {}>
        </#if>
            <input type="hidden" name="scoreRuleId" value="${editRule.scoreRuleId?default('')}"
            <div class="form-group">
                <label class="control-label">策略名称</label>
                <select class="form-control" name="ruleName">
                <#if rules?has_content>
                    <#list rules as rule>
                        <option value="${rule.enumId?default('')}"
                                <#if editRule?has_content && rule.enumId==editRule.ruleName>selected="selected"</#if>>
                        ${rule.description?default('')}</option>
                    </#list>
                </#if>
                </select>
            </div>
            <div class="form-group">
                <label class="control-label">周期</label>
                <select class="form-control validate[required]" name="rulePeriod">
                    <option value="">-请选择-</option>
                <#if list1?has_content>
                    <#list list1 as list>
                        <option value="${list.enumId?default('')}"
                                <#if editRule?has_content &&  list.enumId==editRule.rulePeriod>selected="selected"</#if>>
                        ${list.description?default('')}</option>
                    </#list>
                </#if>
                </select>
            </div>
            <div class="form-group">
                <label class="control-label">周期内最多奖励次数</label>
                <input type="text" name="maxTimes"
                       class="form-control validate[required,custom[onlyNumberSp],maxSize[8]]"
                       value="${editRule.maxTimes?default('')}"/>
            </div>
            <div class="form-group">
                <label class="control-label">积分</label>
                <input type="text" name="score" class="form-control validate[required,custom[onlyNumberSp],maxSize[8]]"
                       value="${editRule.score?default('')}"/>
            </div>
            <div class="form-group">
                <div class="margiv-top-10">
                    <a href="javascript:$.scoreManage.<#if editRule.scoreRuleId?has_content>save<#else>add</#if>ScoreRule()"
                       class="btn green"> 提交 </a>
                </div>
            </div>
        </div>
    </form>
</div>