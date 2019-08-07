<div>
    <table class="basic-table hover-bar">
        <thead>
            <tr class="header-row-2">
                <td>
                    申请编号
                </td>
                <td>
                    流程名称/类型
                </td>
                <td>
                    申请人/时间/部门
                </td>
                <td>
                    流程监控
                </td>
                <td>
                    当前状态
                </td>
            </tr>
        </thead>
        <tbody>
        <#list workflows as workflow>
            <tr class="<#if workflow_index%2 = 1>alternate-row</#if>">
                <td>
                    <a href="javascript:$.workflow.approve('')" class="linktext">${workflow.key}</a>
                </td>
                <td>
                    <a href="javascript:$.workflow.approve('')" class="linktext">
                    ${workflow.name}<br/>
                    ${workflow.type}
                    </a>
                </td>
                <td>
                    ${workflow.applyer}<br/>
                    ${workflow.startTime}<br/>
                    ${workflow.department}
                </td>
                <td>
                    <div style="background-color: lightskyblue;text-align: center">
                    <#if workflow.transitions?has_content>
                    <#list workflow.transitions as trans>
                        <#if trans_index != 0>
                            →
                        </#if>
                    <div style="display: inline-block;vertical-align: middle">
                        <#list trans as tran>
                            <#if tran_index != 0><br/></#if>
                            ${tran}
                        </#list>
                    </div>
                    </#list>
                    </#if>
                    </div>
                    <div style="text-align: center">
                        <#if workflow.transNow?has_content>
                            <#list workflow.transNow as trans>
                                <#if trans_index != 0>
                                    →
                                </#if>
                                <div style="display: inline-block;vertical-align: middle">
                                    <#list trans as tran>
                                        <#if tran_index != 0><br/></#if>
                                    ${tran}
                                    </#list>
                                </div>
                            </#list>
                        </#if>
                    </div>
                </td>
                <td>
                    ${workflow.status}
                </td>
            </tr>
        </#list>
        </tbody>
    </table>
</div>