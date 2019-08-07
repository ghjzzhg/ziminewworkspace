<#macro departSelectedTree departList name id>
    <#if departList?has_content>
    <select name="${name}" id="${id}">
        <#list departList as depart>
            <option value="2">组织</option>
        </#list>
    </select>
    </#if>
</#macro>