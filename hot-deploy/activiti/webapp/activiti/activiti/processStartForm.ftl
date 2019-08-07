<#include "component://activiti/webapp/activiti/activiti/activitiLibrary.ftl"/>

<div class="dynamic-form-dialog">
    <form class='dynamic-form' method='post'>
        <table class='basic-table hover-bar' border='0' cellspacing='0' cellpadding='0'>
            <tbody id='dynamic-form-tbody'>
                <tr>
                    <td>提醒类型</td>
                    <td><input type='checkbox' id='email' name='fp_email'/>发送邮件</td>
                </tr>
                <#list formProperties as prop>
                    <#if prop.required>
                        <#local className="required"/>
                        <#local warnContent="<span class='mandatory'>*</span>"/>
                    <#else>
                        <#local className=""/>
                        <#local warnContent=""/>
                    </#if>
                    <tr>createFieldHtml(data, this, className, warnContent)</tr>
                </#list>
            </tbody>
        </table>
    </form>
</div>