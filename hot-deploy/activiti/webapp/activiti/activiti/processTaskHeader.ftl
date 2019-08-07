<#include "component://oa/webapp/humanres/humanres/hrMacroLibrary.ftl"/>
<#include "component://activiti/webapp/activiti/activiti/processTaskToolbar.ftl"/>

<div id="process-header-form" class="basic-form validationEngineContainer" style="padding: 5px 5px">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3"><i class="glyphicon glyphicon-forward"></i><span>自定义流程流转逻辑</span></li>
        </ul>
        <br class="clear">
    </div>
    <table class="basic-table">
        <tr>
            <td style="width: 150px">
            <#if !nextWays?has_content || (isBranchParameter?has_content && isBranchParameter == '0')>
            <div style="padding: 5px 0">
                <label onclick="$.workflow.toggleExecuteWays()">
                    <input class="validate[groupRequired[executeWays]]" type="radio" name="executeWay" value="0" checked>
                    <span title="按流程设计逻辑执行">按流程定义流转</span>
                </label>
            </div>
            </#if>
            <#if nextWays?has_content && nextWays?size &gt; 1>
            <div style="padding: 5px 0">
                <label onclick="$.workflow.toggleExecuteWays()">
                    <input class="validate[groupRequired[executeWays]]" type="radio" name="executeWay" value="1" <#if isBranchParameter?has_content && isBranchParameter == '1'> checked </#if>>
                    <span title="选择当前相邻的任务执行">选择合适路径</span>
                </label>
            </div>
            </#if>

            <#if allWays?has_content>
            <div style="padding: 5px 0">
                <label onclick="$.workflow.toggleExecuteWays()">
                    <input class="validate[groupRequired[executeWays]]" type="radio" name="executeWay" value="2">
                    <span title="选择流程图中任意一个节点跳转">自由流转至</span>
                </label>
            </div>
            </#if>
            </td>
            <td>
                <div id="nextWays" style="<#if isBranchParameter?has_content && isBranchParameter == '0'>display: none</#if>;padding: 5px 5px">
                <#if nextWays?has_content && nextWays?size &gt; 1>
                    <#list nextWays as nextWay>
                        <div style="padding: 5px 0">
                            <label>
                                <input type="radio" onclick="$.workflow.updatePassBtnText()" name="nextWay" value="${nextWay.activityId}" <#if nextWay_index == 0 && isBranchParameter?has_content && isBranchParameter == '1'>  checked </#if>><span>${nextWay.name}</span>
                            </label>
                        </div>
                    </#list>
                </#if>
                </div>
                <div id="anyWays" style="display: none;padding: 5px 5px">
                <#if allWays?has_content>
                    <#list allWays as nextWay>
                        <div style="padding: 5px 0">
                            <label>
                                <input type="radio" onclick="$.workflow.updatePassBtnText()" name="nextWay" value="${nextWay.activityId}"><span>${nextWay.name}</span>
                            </label>
                        </div>
                    </#list>
                </#if>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label>
                    <span title="选择下一步执行人">设置执行人</span>
                </label>
            </td>
            <td>
                <div id="nextApprovers" style="padding: 5px 5px">
                <@dataScope id="wfCandidates" appCtx="/workflow/control" name="wfCandidates" dataId="" dataAttr="" entityName="" readonly=false dept=true level=true position=true user=true/>
                </div>
            </td>
        </tr>
        <tr>
            <td>
                <label>
                    <span title="设置任务执行期限">设置期限</span>
                </label>
            </td>
            <td>
                <input type="text" id="wfDueDate" name="wfDueDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'})">
            </td>
        </tr>
        <tr>
            <td>
                <label>
                    <span title="设置任务通知形式">通知方式</span>
                </label>
            </td>
            <td>
                <label>
                <input type="checkbox" name="wfNoticeType" value="systemNotice" checked>&nbsp;系统通知
                </label>
                <label>
                <input type="checkbox" name="wfNoticeType" value="emailNotice">&nbsp;邮件通知
                </label>
                <label>
                <input type="checkbox" name="wfNoticeType" value="smsNotice">&nbsp;短信通知
                </label>
            </td>
        </tr>
    </table>
</div>